import java.util.*;

public class ASTConstructionVisitor extends CPLangParserBaseVisitor<ASTNode> {
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

    @Override
    public ASTNode visitFloat(CPLangParser.FloatContext ctx) {
        return new FloatNum(ctx.FLOAT().getSymbol());
    }

    @Override
    public ASTNode visitBool(CPLangParser.BoolContext ctx) {
        return new Bool(ctx.BOOL().getSymbol());
    }

    @Override
    public ASTNode visitAssign(CPLangParser.AssignContext ctx) {
        return new Assign(new Id(ctx.name),
                          (Expression)visit(ctx.e),
                          ctx.ASSIGN().getSymbol());
    }

    @Override
    public ASTNode visitRelational(CPLangParser.RelationalContext ctx) {
        return new Relational((Expression)visit(ctx.left),
                              (Expression)visit(ctx.right),
                              ctx.op);
    }

    @Override
    public ASTNode visitPlusMinus(CPLangParser.PlusMinusContext ctx) {
        if (ctx.op.getText().equals("+")) {
            return new Plus((Expression)visit(ctx.left),
                                 (Expression)visit(ctx.right),
                                 ctx.op);
        }
        else if (ctx.op.getText().equals("-")) {
            return new Minus((Expression)visit(ctx.left),
                                 (Expression)visit(ctx.right),
                                 ctx.op);
        } else {
            return null;
        }
    }

    @Override
    public ASTNode visitMultDiv(CPLangParser.MultDivContext ctx) {
        if (ctx.op.getText().equals("*")) {
            return new Mult((Expression)visit(ctx.left),
                            (Expression)visit(ctx.right),
                            ctx.op);
        }
        else if (ctx.op.getText().equals("/")) {
            return new Div((Expression)visit(ctx.left),
                             (Expression)visit(ctx.right),
                             ctx.op);
        } else {
            return null;
        }
    }

    @Override
    public ASTNode visitUnaryMinus(CPLangParser.UnaryMinusContext ctx) {
        return new UnaryMinus((Expression)visit(ctx.e),
                               ctx.MINUS().getSymbol());
    }

    @Override
    public ASTNode visitParen(CPLangParser.ParenContext ctx) {
        return visit(ctx.e);
    }

    @Override
    public ASTNode visitCall(CPLangParser.CallContext ctx) {
        LinkedList<Expression> args = new LinkedList<>();
        for (var child : ctx.args) {
            args.add((Expression)visit(child));
        }

        return new Call(new Id(ctx.name),
                        args,
                        ctx.start);
    }

    @Override
    public ASTNode visitFormal(CPLangParser.FormalContext ctx) {
        return new Formal(new Type(ctx.type), new Id(ctx.name), ctx.start);
    }

    @Override
    public ASTNode visitVarDef(CPLangParser.VarDefContext ctx) {
        if (ctx.init != null)
            return new VarDef(
                new Type(ctx.type),
                new Id(ctx.name),
                (Expression)visit(ctx.init),
                ctx.start
            );
        else
            return new VarDef(
                new Type(ctx.type),
                new Id(ctx.name),
                null,
                ctx.start
            );
    }

    @Override
    public ASTNode visitFuncDef(CPLangParser.FuncDefContext ctx) {
        LinkedList<Formal> formals = new LinkedList<>();
        for (var formal : ctx.formals)
            formals.add((Formal)visit(formal));

        return new FuncDef(
            new Type(ctx.type),
            new Id(ctx.name),
            formals,
            (Expression)visit(ctx.body),
            ctx.start
        );
    }

    @Override
    public ASTNode visitProg(CPLangParser.ProgContext ctx) {
        LinkedList<ASTNode> stmts = new LinkedList<>();
        for (var child : ctx.children) {
            ASTNode stmt = visit(child);
            if (stmt != null) {
                stmts.add(stmt);
            }
        }

        return new Program(stmts, ctx.start);
    }
};