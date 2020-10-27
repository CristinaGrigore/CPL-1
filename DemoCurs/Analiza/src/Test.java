import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IOException {
        var input  = CharStreams.fromFileName("program.txt");

        var lexer = new CPLangLexer(input);
        var tokenStream = new CommonTokenStream(lexer);

        System.out.println("============ [TOKENS] ============");
        tokenStream.fill();
        List<Token> tokens = tokenStream.getTokens();
        for (var token : tokens) {
            // Decomentati aceasta secventa in locul celei de mai jos,
            // pentru o afisare mai curata
            // var text = token.getText();
            // var type = CPLangLexer.VOCABULARY.getSymbolicName(token.getType());

            System.out.println(((CommonToken) token).toString(lexer));
        }

        System.out.println("\n============ [PARSE TREES] ============");
        var parser = new CPLangParser(tokenStream);
        var tree = parser.expr();
        System.out.println(tree.toStringTree(parser));

        System.out.println("\n============ [CU LISTENER] ============");
        var intListener = new CPLangParserBaseListener() {
            @Override
            public void exitInt(CPLangParser.IntContext ctx) {
                System.out.println(ctx.INT().toString());
            }
        };

        var walker = new ParseTreeWalker();
        // Parcurge `tree` cu un DFS de la stanga la drepta si aplica metodele din `intListener` pe firecare nod
        // Adica aplica `enterTip()` cand ajunge intr-un nod si `exitTip()` cand se intoarce in parintele nodului
        // Dezavantaje:
        // - walkerul trimite listenerul pe intregul arbore, desi e posibil sa folosim un nr mic de noduri de interes
        // - metodele nu intorc nimic; pt comunicare trebuie folositeprin variabilele interne ale clasei BaseListener
        walker.walk(intListener, tree);

        // E nevoie de suprascrierea lui `visitIf()` ca `visitID()` sa nu intoarca null, deoarece primul token intalnit
        // e `if` si el nu stie sa parcurga arborele in continuare de acolo
        System.out.println("\n============ [CU VISITOR] ============");
        var varVisitor = new CPLangParserBaseVisitor<List<String>>() {
            @Override
            public List<String> visitId(CPLangParser.IdContext ctx) {
                return List.of(ctx.ID().toString());
            }

            // Nu e nevoie sa fie suprascrisa. Suprascrierea e doar pt simplitate.
            @Override
            public List<String> visitInt(CPLangParser.IntContext ctx) {
                return Collections.emptyList();
            }

            @Override
            public List<String> visitIf(CPLangParser.IfContext ctx) {
                // Daca am setat `cond`, `thenBranch` si `elseBranch` in CPLangParser.g4 avem acces la:
                // TODO: Ce sunt numerele afisate? Pare ca sunt liste de id-uri ale ramurilor pe care s-a mers.
                System.out.println("if " + ctx.cond + " then " + ctx.thenBranch + " else " + ctx.elseBranch + "");
                // Astea permit identificarea partilor dintr-o expresie

                // Cand un simbol apare de mai multe ori, `ctx.expr()` intoarce o lista de noduri
                var list = new LinkedList<String>();

                for (var e : ctx.expr()) {
                    list.addAll(visit(e));
                }

                return list;
            }
        };

        System.out.println(varVisitor.visit(tree));

        System.out.println("\n============ [ABSTRACT SYNTAX TREE] ============");
        var astConstructionVisitor = new CPLangParserBaseVisitor<ASTNode>() {
            @Override
            public ASTNode visitId(CPLangParser.IdContext ctx) {
                return new IdNode(ctx.ID().getSymbol());
            }

            @Override
            public ASTNode visitInt(CPLangParser.IntContext ctx) {
                return new IntNode(ctx.INT().getSymbol());
            }

            @Override
            public ASTNode visitIf(CPLangParser.IfContext ctx) {
                return new IfNode(
                    (Expression)visit(ctx.cond),
                    (Expression)visit(ctx.thenBranch),
                    (Expression)visit(ctx.elseBranch),
                    ctx.start
                );
            }
        };

        var ast = astConstructionVisitor.visit(tree);
        var astPrintVisitor = new ASTVisitor<Void>() {
            int depth = 0;

            @Override
            public Void visit(IntNode intNode) {
                printTabs();
                System.out.println("INT " + intNode.token.getText());
                return null;
            }

            @Override
            public Void visit(IdNode idNode) {
                printTabs();
                System.out.println("ID " + idNode.token.getText());
                return null;
            }

            @Override
            public Void visit(IfNode ifNode) {
                printTabs();
                System.out.println("IF " + ifNode.token.getText());
                ++depth;
                ifNode.cond.accept(this);
                ifNode.thenBranch.accept(this);
                ifNode.elseBranch.accept(this);
                --depth;
                return null;
            }

            private void printTabs() {
                System.out.print("\t".repeat(depth));
            }
        };

        ast.accept(astPrintVisitor);
    }
}
