import analyzer.SemanticAnalyzer;
import analyzer.SymTable;
import error.CompileError;
import lexer.Lexer;
import model.symbol.Function;
import model.token.Ident;
import model.unit.CompUnit;
import parser.SyntaxParser;
import reader.CompileReader;

import java.io.FileWriter;
import java.io.IOException;

public class Compiler {

    public static void main(String[] args) throws IOException {
        CompileReader reader = new CompileReader(args[0]);
        Lexer.setReader(reader);
        SyntaxParser parser = SyntaxParser.getParser();
        FileWriter writer = null;
        try {
            SymTable table = SymTable.getInstance();
            table.put(new Function(new Ident("getint"), 0, false));
            table.put(new Function(new Ident("putint"), 1, true));
            table.put(new Function(new Ident("getch"), 0, false));
            table.put(new Function(new Ident("putch"), 1, true));
            CompUnit compUnit = parser.parse();
            if (Lexer.getLexer().getToken() != null)
                System.exit(1);
            writer = new FileWriter(args[1]);
            writer.write("""
                            declare i32 @getint()
                            declare void @putint(i32)
                            declare i32 @getch()
                            declare void @putch(i32)
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
