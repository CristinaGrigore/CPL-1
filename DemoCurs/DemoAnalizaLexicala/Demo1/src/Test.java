import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.io.IOException;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IOException {
        var input  = CharStreams.fromFileName("program.txt");

        var lexer = new CPLangLexer(input);
        var tokenStream = new CommonTokenStream(lexer);

        tokenStream.fill();
        List<Token> tokens = tokenStream.getTokens();
        for (var token : tokens) {
            // Decomentati aceasta secventa in locul celei de mai jos,
            // pentru o afisare mai curata
            /*
            var text = token.getText();
            var type = CPLangLExer.VOCABULARY.getSymbolicName(token.getType());
            */

            System.out.println(((CommonToken) token).toString(lexer));
        }
    }
}
