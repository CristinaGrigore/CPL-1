import java.io.IOException;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;


public class Test {

    public static void main(String[] args) throws IOException {
        var input = CharStreams.fromFileName("program4.txt");

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
        var tree = parser.expr();
        System.out.println(tree.toStringTree(parser));

        // Visitor-ul de mai jos parcurge arborele de derivare și construiește
        // un arbore de sintaxă abstractă (AST).
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
                return new IfNode((Expression)visit(ctx.cond),
                        (Expression)visit(ctx.thenBranch),
                        (Expression)visit(ctx.elseBranch),
                        ctx.start);
            }

            @Override
            public ASTNode visitPlus(CPLangParser.PlusContext ctx) {
                return new PlusNode(
                        ctx.op,
                        (Expression)visit(ctx.left),
                        (Expression)visit(ctx.right)
                );
            }

            @Override
            public ASTNode visitDef(CPLangParser.DefContext ctx) {
                var id = new IdNode(ctx.name);
                var type = new TypeNode(ctx.type);

                return new VarDefNode(ctx.start, id, type);
            }

            @Override
            public ASTNode visitBlock(CPLangParser.BlockContext ctx) {
                var stmts = new LinkedList<ASTNode>();

                for (var child : ctx.children) {
                    var stmt = visit(child);
                    if (stmt != null)
                        stmts.add(stmt);
                }

                return new BlockNode(ctx.start, stmts);
            }
        };

        // ast este AST-ul proaspăt construit pe baza arborelui de derivare.
        var ast = astConstructionVisitor.visit(tree);

        System.out.println("===== Introducere StringTemplate =====");
        var st1 = new ST("Cool <name; separator=\", \">");
        st1.add("name", "compiler");
        st1.add("name", "interpreter");
        System.out.println(st1.render());

        var st2 = new ST("internal");
        st1.add("name", st2);  // ANTLR permite generare recursiva de cod
        System.out.println(st1.render());

        System.out.println("\n===== Testare StringTemplate =====");
        var group = new STGroupFile("cgen.stg");
        var literal = group.getInstanceOf("literal");  // intoarce mereu alta instanta; nu e singleton
        literal.add("value", 5);
        System.out.println(literal.render());

        System.out.println("\n===== Generare de cod: e1 + e2 =====");
        var plus = group.getInstanceOf("plus");  // intoarce mereu alta instanta; nu e singleton
        plus.add("e1", literal);  // param2 formal e Object; ANTLR apeleaza toString() pe param2
        plus.add("e2", literal);
        System.out.println(plus.render());

        // ABORDAREA 1.
        // onePassVisitor permite rezolvarea simbolurilor într-o singură
        // trecere. Astfel, referirile anticipate NU pot fi rezolvate.
        Scope globalScope = new DefaultScope(null);
        globalScope.add(TypeSymbol.INT);
        globalScope.add(TypeSymbol.BOOL);

        var onePassVisitor = new ASTVisitor<TypeSymbol>() {
            // Reținem domeniul de vizibilitate curent
            Scope currentScope = globalScope;

            @Override
            public TypeSymbol visit(IdNode id) {
                // La referirea unei variabile, o căutăm în domeniul
                // de vizibilitate curent.
                var symbol = (IdSymbol)currentScope.lookup(id.getToken().getText());

                // Semnalăm eroare dacă nu există.
                if (symbol == null) {
                    error(id.getToken(), id.getToken().getText() + " undefined");
                    return null;
                }

                // Atașăm simbolul nodului din arbore.
                id.setSymbol(symbol);

                return symbol.getType();
            }

            @Override
            public TypeSymbol visit(IntNode intt) {
                return TypeSymbol.INT;
            }

            @Override
            public TypeSymbol visit(IfNode iff) {
                iff.cond.accept(this);
                iff.thenBranch.accept(this);
                iff.elseBranch.accept(this);
                return null;
            }

            @Override
            public TypeSymbol visit(PlusNode plus) {
                // Pt simplitate consideram ca operatorii sunt Int (IRL pot fi Float etc)
                var leftType = plus.left.accept(this);
                var rightType = plus.right.accept(this);

                if (leftType == null || rightType == null) {
                    return null;
                }

                // Tipurile de simbol au instanta unica => putem folosi !=, nu !.equals()
                if (leftType != TypeSymbol.INT || rightType != TypeSymbol.INT) {
                    error(plus.getToken(), "operand + has a different type than Int");
                    return null;
                }

                return TypeSymbol.INT;
            }

            @Override
            public TypeSymbol visit(TypeNode type) {
                return null;
            }

            @Override
            public TypeSymbol visit(VarDefNode definition) {
                var id = definition.id;
                var type = definition.Type;

                // La definirea unei variabile, creăm un nou simbol.
                var symbol = new IdSymbol(id.getToken().getText());

                // Adăugăm simbolul în domeniul de vizibilitate curent,
                // semnalând eroare dacă exista deja acolo.
                if (! currentScope.add(symbol)) {
                    error(id.getToken(), id.getToken().getText() + " redefined");
                    return null;
                }

                id.setSymbol(symbol);

                var typeSymb = (TypeSymbol)globalScope.lookup(type.getToken().getText());
                if (typeSymb == null) {
                    error(
                            type.getToken(),
                            id.getToken().getText() + " has undefined type " + type.getToken().getText()
                    );
                    return null;
                }

                symbol.setType(typeSymb);

                return null;
            }

            @Override
            public TypeSymbol visit(BlockNode block) {
                // Fiecare bloc constituie un nou domeniu de vizibilitate.
                currentScope = new DefaultScope(currentScope);

                for (var stmt : block.statements)
                    stmt.accept(this);

                // La ieșirea din bloc, revenim la domeniul anterior.
                currentScope = currentScope.getParent();

                return null;
            }

        };

        // ABORDAREA 2.
        // În vederea gestiunii referirilor anticipate, utilizăm două treceri,
        // una de definire a simbolurilor, și cealaltă, de rezolvare.
        var definitionPassVisitor = new ASTVisitor<Void>() {
            Scope currentScope = new DefaultScope(null);

            @Override
            public Void visit(IdNode id) {
                // La referirea unei variabile, nu o mai căutăm imediat
                // în domeniul de vizibilitate curent, ci doar memorăm
                // domeniul în nodul de AST, în vederea căutării în trecerea
                // următoare.
                id.scope = currentScope;

                return null;
            }

            @Override
            public Void visit(IntNode intt) {
                return null;
            }

            @Override
            public Void visit(IfNode iff) {
                iff.cond.accept(this);
                iff.thenBranch.accept(this);
                iff.elseBranch.accept(this);
                return null;
            }

            @Override
            public Void visit(PlusNode plus) {
                plus.left.accept(this);
                plus.right.accept(this);
                return null;
            }

            @Override
            public Void visit(TypeNode type) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Void visit(VarDefNode definition) {
                var id = definition.id;

                // La definirea unei variabile, creăm un nou simbol.
                var symbol = new IdSymbol(id.getToken().getText());

                // Adăugăm simbolul în domeniul de vizibilitate curent,
                // semnalând eroare dacă exista deja acolo.
                if (! currentScope.add(symbol)) {
                    error(id.getToken(),
                            id.getToken().getText() + " redefined");
                    return null;
                }

                // Atașăm simbolul nodului din arbore.
                id.setSymbol(symbol);

                return null;
            }

            @Override
            public Void visit(BlockNode block) {
                currentScope = new DefaultScope(currentScope);

                for (var stmt : block.statements)
                    stmt.accept(this);

                currentScope = currentScope.getParent();

                return null;
            }

        };

        // A doua trecere, pentru rezolvarea simbolurilor în raport cu domeniile
        // de vizibilitate memorate în prima trecere. Observați că, în această
        // trecere, nu mai este necesară gestiunea domeniului curent,
        // ca în prima trecere.
        var resolutionPassVisitor = new ASTVisitor<Void>() {

            @Override
            public Void visit(IdNode id) {
                // La referirea unei variabile, o căutăm în domeniul
                // de vizibilitate memorat în trecerea anterioară.
                var symbol = id.getScope().lookup(id.getToken().getText());

                // Semnalăm eroare dacă nu există.
                if (symbol == null) {
                    error(id.getToken(),
                            id.getToken().getText() + " undefined");
                    return null;
                }

                // Atașăm simbolul nodului din arbore.
                id.setSymbol((IdSymbol)symbol);

                return null;
            }

            @Override
            public Void visit(IntNode intt) {
                return null;
            }

            @Override
            public Void visit(IfNode iff) {
                iff.cond.accept(this);
                iff.thenBranch.accept(this);
                iff.elseBranch.accept(this);
                return null;
            }

            @Override
            public Void visit(PlusNode plus) {
                plus.left.accept(this);
                plus.right.accept(this);
                return null;
            }

            @Override
            public Void visit(TypeNode type) {
                return null;
            }

            @Override
            public Void visit(VarDefNode definition) {
                // Acum nu mai avem nimic de făcut cu definițiile de variabile.
                return null;
            }

            @Override
            public Void visit(BlockNode block) {
                for (var stmt : block.statements)
                    stmt.accept(this);

                return null;
            }

        };

        var codeGenVisitor = new ASTVisitor<ST>() {
            @Override
            public ST visit(IntNode intNode) {
                return group
                        .getInstanceOf("literal")
                        .add("value", intNode.getToken().getText());
            }

            @Override
            public ST visit(IdNode idNode) {
                return null;
            }

            @Override
            public ST visit(IfNode ifNode) {
                return null;
            }

            @Override
            public ST visit(BlockNode blockNode) {
                var seq = group.getInstanceOf("sequence");
                blockNode.statements.forEach(stmt -> seq.add("e", stmt.accept(this)));

                return seq;
            }

            @Override
            public ST visit(TypeNode typeNode) {
                return null;
            }

            @Override
            public ST visit(VarDefNode varDefNode) {
                return null;
            }

            @Override
            public ST visit(PlusNode plusNode) {
                return group.getInstanceOf("plus")
                        .add("e1", plusNode.left.accept(this))
                        .add("e2", plusNode.right.accept(this));
            }
        };

        // Pentru testare, utilizați fie onePassVisitor,
        // fie definitionPassVisitor și resolutionPassVisitor concomitent.
//        ast.accept(onePassVisitor);

        ast.accept(definitionPassVisitor);
        ast.accept(resolutionPassVisitor);

        System.out.println("\n===== Generare de cod: bloc de expresii =====");
        System.out.println(ast.accept(codeGenVisitor).render());
    }

    public static void error(Token token, String message) {
        System.err.println("line " + token.getLine()
                + ":" + (token.getCharPositionInLine() + 1)
                + ", " + message);
    }
}