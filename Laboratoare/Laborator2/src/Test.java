import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.antlr.v4.runtime.*;


public class Test {
	public static void main(String[] args) throws IOException {
		var input = CharStreams.fromFileName("manual.txt");
		FileWriter fileWriter = new FileWriter("output.txt");

		var lexer = new CPLangLexer(input);
		var tokenStream = new CommonTokenStream(lexer);

		tokenStream.fill();
		List<Token> tokens = tokenStream.getTokens();
		for (var token : tokens) {
			// Decomentați această secvență în locul celei de mai jos,
			// pentru o afișare mai curată
			/*
			var text = token.getText();
			var type = CPLangLexer.VOCABULARY.getSymbolicName(token.getType());
			System.out.println(text + " : " + type);
			*/

			fileWriter.write(((CommonToken)token).toString(lexer) + "\n");
		}
		fileWriter.close();
	}
}
