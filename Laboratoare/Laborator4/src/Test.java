import java.io.IOException;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.*;


public class Test {

    public static void main(String[] args) throws IOException {
        var input = CharStreams.fromFileName("manual.txt");

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
        System.out.println(tree.toStringTree(parser));

        // Visitor-ul de mai jos parcurge arborele de derivare și construiește
        // un arbore de sintaxă abstractă (AST).
        var astConstructionVisitor = new CPLangParserBaseVisitor<ASTNode>() {
            @Override
            public ASTNode visitId(CPLangParser.IdContext ctx) {
                return new Id(ctx.ID().getSymbol());
            }

            @Override
            public ASTNode visitInt(CPLangParser.IntContext ctx) {
                return new Int(ctx.INT().getSymbol());
            }

            @Override
            public ASTNode visitIf(CPLangParser.IfContext ctx) {
                return new If((Expression)visit(ctx.cond),
                              (Expression)visit(ctx.thenBranch),
                              (Expression)visit(ctx.elseBranch),
                              ctx.start);
            }

            // TODO3: Completati cu alte metode pentru a trece din arborele
            // generat automat in reprezentarea AST-ului dorit
            @Override
            public ASTNode visitProg(CPLangParser.ProgContext ctx) {
                return new Program(ctx.instructions
                        .stream()
                        .map(instr -> (Instruction)visitInstruction(instr))
                        .collect(Collectors.toList()));
            }

            @Override
            public ASTNode visitVarDef(CPLangParser.VarDefContext ctx) {
                var initValue = ctx.init == null ? null : (Expression)visit(ctx.init);

                return new VarDef(ctx.type, ctx.name, initValue);
            }

            @Override
            public ASTNode visitFuncDef(CPLangParser.FuncDefContext ctx) {
                var formalsList = ctx.formals
                        .stream()
                        .map(formal -> (Formal)visitFormal(formal))
                        .collect(Collectors.toList());

                return new FuncDef(ctx.type, ctx.name, formalsList, (Expression)visit(ctx.body));
            }

            @Override
            public ASTNode visitFormal(CPLangParser.FormalContext ctx) {
                return new Formal(ctx.type, ctx.name);
            }

            @Override
            public ASTNode visitCall(CPLangParser.CallContext ctx) {
                var argsList = ctx.args
                        .stream()
                        .map(arg -> (Expression)visit(arg))
                        .collect(Collectors.toList());

                return new Call(ctx.start, ctx.name, argsList);
            }

            @Override
            public ASTNode visitParen(CPLangParser.ParenContext ctx) {
                return visit(ctx.e);
            }

            @Override
            public ASTNode visitPlusMinus(CPLangParser.PlusMinusContext ctx) {
                if (ctx.op.getText().equals("+")) {
                    return new Plus(
                            ctx.op,
                            (Expression)visit(ctx.left),
                            (Expression)visit(ctx.right)
                    );
                } else {
                    return new Minus(
                            ctx.op,
                            (Expression)visit(ctx.left),
                            (Expression)visit(ctx.right)
                    );
                }
            }

            @Override
            public ASTNode visitBool(CPLangParser.BoolContext ctx) {
                return new Bool(ctx.BOOL().getSymbol());
            }

            @Override
            public ASTNode visitMultDiv(CPLangParser.MultDivContext ctx) {
                if (ctx.op.getText().equals("*")) {
                    return new Mult(
                            ctx.op,
                            (Expression)visit(ctx.left),
                            (Expression)visit(ctx.right)
                    );
                } else {
                    return new Div(
                            ctx.op,
                            (Expression)visit(ctx.left),
                            (Expression)visit(ctx.right)
                    );
                }
            }

            @Override
            public ASTNode visitUnaryMinus(CPLangParser.UnaryMinusContext ctx) {
                return new UnaryMinus(ctx.start, (Expression)visit(ctx.e));
            }

            @Override
            public ASTNode visitRelational(CPLangParser.RelationalContext ctx) {
                return new Relational(
                        ctx.op,
                        (Expression)visit(ctx.left),
                        (Expression)visit(ctx.right)
                );
            }

            @Override
            public ASTNode visitFloat(CPLangParser.FloatContext ctx) {
                return new FloatNode(ctx.FLOAT().getSymbol());
            }

            @Override
            public ASTNode visitAssign(CPLangParser.AssignContext ctx) {
                return new Assign(ctx.ASSIGN().getSymbol(), ctx.name, (Expression)visit(ctx.e));
            }
        };

        // ast este AST-ul proaspăt construit pe baza arborelui de derivare.
        var ast = astConstructionVisitor.visit(tree);

        // Acest visitor parcurge AST-ul generat mai sus.
        // ATENȚIE! Avem de-a face cu două categorii de visitori:
        // (1) Cei pentru arborele de derivare, care extind
        //     CPLangParserBaseVisitor<T> și
        // (2) Cei pentru AST, care extind ASTVisitor<T>.
        // Aveți grijă să nu îi confundați!
        var printVisitor = new ASTVisitor<Void>() {
            int indent = 0;

            @Override
            public Void visit(Id id) {
                printIndent("ID " + id.token.getText());
                return null;
            }

            @Override
            public Void visit(Int intt) {
                printIndent("INT " + intt.token.getText());
                return null;
            }

            @Override
            public Void visit(If iff) {
                printIndent("IF");

                indent++;
                iff.cond.accept(this);
                iff.thenBranch.accept(this);
                iff.elseBranch.accept(this);
                indent--;

                return null;
            }

            @Override
            public Void visit(Call call) {
                printIndent("CALL");

                ++indent;
                printIndent(call.name.getText());
                call.args.forEach(arg -> arg.accept(this));
                --indent;

                return null;
            }

            @Override
            public Void visit(UnaryMinus unaryMinus) {
                printIndent(unaryMinus.token.getText());

                ++indent;
                unaryMinus.expr.accept(this);
                --indent;

                return null;
            }

            @Override
            public Void visit(Mult mult) {
                printIndent(mult.token.getText());

                ++indent;
                mult.leftExpr.accept(this);
                mult.rightExpr.accept(this);
                --indent;

                return null;
            }

            @Override
            public Void visit(Div div) {
                printIndent(div.token.getText());

                ++indent;
                div.leftExpr.accept(this);
                div.rightExpr.accept(this);
                --indent;

                return null;
            }

            @Override
            public Void visit(Plus plus) {
                printIndent(plus.token.getText());

                ++indent;
                plus.leftExpr.accept(this);
                plus.rightExpr.accept(this);
                --indent;

                return null;
            }

            @Override
            public Void visit(Minus minus) {
                printIndent(minus.token.getText());

                ++indent;
                minus.leftExpr.accept(this);
                minus.rightExpr.accept(this);
                --indent;

                return null;
            }

            @Override
            public Void visit(Relational relational) {
                printIndent(relational.token.getText());

                ++indent;
                relational.leftExpr.accept(this);
                relational.rightExpr.accept(this);
                --indent;

                return null;
            }

            @Override
            public Void visit(Assign assign) {
                printIndent(assign.token.getText());

                ++indent;
                printIndent(assign.name.getText());
                assign.expr.accept(this);
                --indent;

                return null;
            }

            @Override
            public Void visit(FloatNode floatt) {
                printIndent(floatt.num.toString());
                return null;
            }

            @Override
            public Void visit(Bool booll) {
                printIndent(booll.val.toString());
                return null;
            }

            @Override
            public Void visit(Formal formal) {
                printIndent("FORMAL");

                ++indent;
                printIndent(formal.type.getText());
                printIndent(formal.name.getText());
                --indent;

                return null;
            }

            @Override
            public Void visit(VarDef varDef) {
                printIndent("VAR DEF");

                ++indent;
                printIndent(varDef.type.getText());
                printIndent(varDef.name.getText());

                if (varDef.value != null) {
                    ++indent;
                    varDef.value.accept(this);
                    --indent;
                }
                --indent;

                return null;
            }

            @Override
            public Void visit(FuncDef funcDef) {
                printIndent("FUNC DEF");

                ++indent;
                printIndent(funcDef.type.getText());
                printIndent(funcDef.name.getText());

                ++indent;
                funcDef.formals.forEach(formal -> formal.accept(this));

                ++indent;
                funcDef.body.accept(this);
                indent -= 3;

                return null;
            }

            @Override
            public Void visit(Program program) {
                printIndent("PROGRAM");

                ++indent;
                program.instructions.forEach(instr -> instr.accept(this));
                --indent;

                return null;
            }

            // TODO4: Afisati fiecare nod astfel incat nivelul pe care acesta
            // se afla in AST sa fie reprezentat de numarul de tab-uri.
            // Folositi functia de mai jos 'printIndent' si incrementati /
            // decrementati corespunzator numarul de tab-uri

            void printIndent(String str) {
                for (int i = 0; i < indent; i++)
                    System.out.print("\t");
                System.out.println(str);
            }
        };

        // TODO5: Creati un program CPLang care sa cuprinda cat mai multe
        // constructii definite in laboratorul de astazi. Scrieti codul CPLang
        // intr-un fisier txt si modificati fisierul de input de la inceputul
        // metodei 'main'

        System.out.println("The AST is");
        ast.accept(printVisitor);
    }


}
