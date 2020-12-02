import org.antlr.v4.runtime.*;

public interface ASTVisitor<T> {
    T visit(Id id);
    T visit(Int intt);
    T visit(If iff);
    T visit(FloatNum floaty);
    T visit(Bool bool);
    T visit(Assign assign);
    T visit(Relational rel);
    T visit(Plus plus);
    T visit(Minus minus);
    T visit(Mult mult);
    T visit(Div div);
    T visit(UnaryMinus uMinus);
    T visit(Call call);
    T visit(Type type);
    T visit(Formal formal);
    T visit(VarDef varDef);
    T visit(FuncDef funcDef);
    T visit(Program program);

    public static void error(Token token, String message) {
        System.err.println("line " + token.getLine()
                + ":" + (token.getCharPositionInLine() + 1)
                + ", " + message);
    }
}
