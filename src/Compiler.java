import error.LexicalError;
import error.SyntaxError;
import lexer.Lexer;
import model.unit.IUnit;
import parser.SyntaxParser;

import java.io.*;

public class Compiler {

    public static void main(String[] args) throws IOException {
        FileReader fr = new FileReader(args[0]);
        BufferedReader br = new BufferedReader(fr, 256);
        PushbackReader pr = new PushbackReader(br);
        Lexer lexer = new Lexer(pr);
        SyntaxParser parser = new SyntaxParser(lexer);
        FileWriter writer = new FileWriter(args[1]);
        try {
            IUnit compUnit = parser.parse();
            if (lexer.readToken() != null)
                System.exit(1);
            writer.write(compUnit.dump());
            writer.flush();
        }
        catch (LexicalError | SyntaxError error) {
            System.exit(1);
        }
        finally {
            writer.close();
            pr.close();
            br.close();
            fr.close();
        }
    }

}
