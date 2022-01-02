import analyzer.SemanticAnalyzer;
import analyzer.SymTable;
import error.CompileError;
import lexer.Lexer;
import model.symbol.Function;
import model.token.Ident;
import model.unit.Argument;
import model.unit.CompUnit;
import parser.SyntaxParser;
import reader.CompileReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Compiler {

    public static void main(String[] args) throws IOException {
        if (args.length == 3 && args[2].equals("--print")) {
            FileReader reader = new FileReader(args[0]);
            int c = reader.read();
            while (c != -1) {
                System.out.print((char) c);
                c = reader.read();
            }
            return;
        }
        CompileReader reader = new CompileReader(args[0]);
        Lexer.setReader(reader);
        SyntaxParser parser = SyntaxParser.getParser();
        FileWriter writer = null;
        try {
            SymTable table = SymTable.getInstance();
            table.put(new Function(
                    false,
                    new Ident("getint"),
                    Collections.emptyList()));
            table.put(new Function(
                    true,
                    new Ident("putint"),
                    List.of(new Argument(false))));
            table.put(new Function(
                    false,
                    new Ident("getch"),
                    Collections.emptyList()));
            table.put(new Function(
                    true,
                    new Ident("putch"),
                    List.of(new Argument(false))));
            table.put(new Function(
                    false,
                    new Ident("getarray"),
                    List.of(new Argument(true))));
            table.put(new Function(
                    true,
                    new Ident("putarray"),
                    List.of(
                            new Argument(false),
                            new Argument(true))));
            CompUnit compUnit = parser.parse();
            if (Lexer.getLexer().getToken() != null)
                System.exit(1);
            writer = new FileWriter(args[1]);
            writer.write("""
                            declare i32 @getint()
                            declare void @putint(i32)
                            declare i32 @getch()
                            declare void @putch(i32)
                            declare i32 @getarray(i32*)
                            declare void @putarray(i32, i32*)
                            declare void @memset(i32*, i32, i32)
                            
                            """);
            writer.write(SemanticAnalyzer.getAnalyzer().dump(compUnit));
            writer.flush();
        }
        catch (CompileError error) {
            System.err.println("----- Compile Error -----");
            System.err.println(error.getMessage());
            System.exit(1);
        }
        finally {
            reader.close();
            if (writer != null)
                writer.close();
        }
    }

}
