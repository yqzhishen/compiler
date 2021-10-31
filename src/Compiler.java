import analyzer.SemanticAnalyzer;
import error.CompileError;
import lexer.Lexer;
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
            CompUnit compUnit = parser.parse();
            if (Lexer.getLexer().getToken() != null)
                System.exit(1);
            writer = new FileWriter(args[1]);
/*
            writer.write(SemanticAnalyzer.getAnalyzer().dump(compUnit));
            writer.write(compUnit.dump());
            writer.flush();
*/
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
