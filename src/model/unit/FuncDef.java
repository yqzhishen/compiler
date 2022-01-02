package model.unit;

import analyzer.SymTable;
import analyzer.Tagger;
import error.CompileError;
import error.SemanticError;
import model.ir.*;
import model.ir.Return;
import model.symbol.Array;
import model.symbol.Function;
import model.symbol.Variable;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class FuncDef extends AbstractUnit implements IMetaUnit {

    public static boolean inVoidFunction;

    private boolean isVoid;

    private Ident ident;

    private final List<Argument> arguments = new ArrayList<>();

    private Block funcBlock;

    @Override
    public FuncDef build() throws IOException, CompileError {
        isVoid = this.require(TokenType.Int, TokenType.Void).getType().equals(TokenType.Void);
        ident = (Ident) this.require(TokenType.Ident);
        boolean isMain = ident.getName().equals("main");
        if (isMain && isVoid) {
            throw new SemanticError(ident.getPos(), "return type of 'main' function must be 'int'");
        }
        this.require(TokenType.LPar);
        if (!TokenType.RPar.equals(lexer.nextType())) {
            this.arguments.add(new Argument().build());
            while (!TokenType.RPar.equals(lexer.nextType())) {
                this.require(TokenType.Comma);
                arguments.add(new Argument().build());
            }
        }
        this.require(TokenType.RPar);
        if (isMain && !arguments.isEmpty()) {
            throw new SemanticError(ident.getPos(), "'main' function must have no arguments");
        }
        this.funcBlock = new Block(true).build();
        return this;
    }

    @Override
    public String generateCode() throws CompileError {
        FuncDef.inVoidFunction = this.isVoid;
        if (ident.getName().equals("main")) {
            CompUnit.hasMain = true;
        }
        SymTable table = SymTable.getInstance();
        table.put(new Function(isVoid, ident, arguments));
        StringBuilder builder = new StringBuilder("define dso_local ");
        builder.append(isVoid ? "void" : "i32").append(" @").append(ident.getName());
        StringJoiner argJoiner = new StringJoiner(", ", "(", ")");
        for (Argument argument : arguments) {
            Operand operand = Operand.local(Tagger.newTag());
            argument.setAddress(operand);
            if (argument.isPointer()) {
                argJoiner.add(Array.dumpShape(argument.getShape()) + "* " + operand);
            }
            else {
                argJoiner.add("i32 " + operand);
            }
        }
        builder.append(argJoiner).append(" {");
        StringJoiner blockJoiner = new StringJoiner("\n    ", "\n    ", "\n");
        table.pushLayer();
        List<Instruction> instructions = new ArrayList<>();
        Tagger.newTag();
        for (Argument argument : arguments) {
            if (!argument.isPointer()) {
                Operand address = Operand.local(Tagger.newTag());
                Allocate allocate = new Allocate("i32", address);
                instructions.add(allocate);
                Store store = new Store("i32", argument.getAddress(), "i32*", address);
                instructions.add(store);
                argument.setAddress(address);
                table.put(new Variable(argument.getIdent(), address));
            }
            else {
                List<IExpr> shape = argument.getShape();
                for (int i = 0; i < argument.getShape().size(); ++i) {
                    int size = shape.get(i).calculate();
                    if (size < 0)
                        throw new SemanticError(argument.getIdent().getPos(), "array size must not be negative");
                    shape.set(i, new Number(size));
                }
                List<IExpr> fShape = new ArrayList<>();
                fShape.add(new Number(-1));
                fShape.addAll(argument.getShape());
                Array array = Array.varArray(argument.getIdent(), fShape, null);
                array.setAddress(argument.getAddress());
                table.put(array);
            }
        }
        instructions.addAll(funcBlock.generateIr());
        if (instructions.isEmpty() || !instructions.get(instructions.size() - 1).getType().equals(InstructionType.Ret)) {
            if (isVoid) {
                instructions.add(new Return());
            }
            else {
                instructions.add(new Return("i32", Operand.number(0)));
            }
        }
        instructions.forEach(instruction -> blockJoiner.add(instruction.toString()));
        builder.append(blockJoiner).append("}\n");
        table.popLayer();
        Tagger.reset();
        return builder.toString();
    }

    @Override
    public boolean isFunction() {
        return true;
    }

}
