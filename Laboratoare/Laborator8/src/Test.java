import java.io.IOException;

import org.antlr.v4.runtime.*;


public class Test {

    public static void main(String[] args) throws IOException {
        var input = CharStreams.fromFileName("codegen.txt");

        var lexer = new CPLangLexer(input);
        var tokenStream = new CommonTokenStream(lexer);

        /*
        tokenStream.fill();
        List<Token> tokens = tokenStream.getTokens();
        for (var token : tokens) {
            var text = token.getText();
            var type = CPLangLexer.VOCABULARY.getSymbolicName(token.getType());

            System.out.println(text + " : " + type);
        }
        */

        var parser = new CPLangParser(tokenStream);
        var tree = parser.prog();
        System.out.println("\n" + tree.toStringTree(parser));

        // Construcția AST-ului din arborele de derivare
        var astConstructionVisitor = new ASTConstructionVisitor();
        var ast = astConstructionVisitor.visit(tree);

        // Analiză semantică, trecerea 1: definire simboluri și domenii de vizibilitate
        var definitionPassVisitor = new DefinitionPassVisitor();
        ast.accept(definitionPassVisitor);
        
        // Analiză semantică, trecerea 2: verificare simboluri
        var resolutionPassVisitor = new ResolutionPassVisitor();
        ast.accept(resolutionPassVisitor);
        
        // Generarea de cod
        var codeGenVisitor = new CodeGenVisitor();
        var t = ast.accept(codeGenVisitor);
        System.out.println(t.render());
    }


}
