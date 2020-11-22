import org.antlr.v4.runtime.Token;
import java.util.*;

// Rădăcina ierarhiei de clase reprezentând nodurile arborelui de sintaxă
// abstractă (AST). Singura metodă permite primirea unui visitor.
public abstract class ASTNode {
    // Reținem un token descriptiv al nodului, pentru a putea afișa ulterior
    // informații legate de linia și coloana eventualelor erori semantice.
    protected Token token;

    ASTNode(Token token) {
        this.token = token;
    }
    
    Token getToken() {
        return token;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }
}

// Orice expresie.
abstract class Expression extends ASTNode {
    Expression(Token token) {
        super(token);
    }
}

// Identificatori
class Id extends Expression {
    private IdSymbol symbol;
    private Scope scope;

    Id(Token token) {
        super(token);
    }
    
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    IdSymbol getSymbol() {
        return symbol;
    }

    void setSymbol(IdSymbol symbol) {
        this.symbol = symbol;
    }
    
    Scope getScope() {
        return scope;
    }

    void setScope(Scope scope) {
        this.scope = scope;
    }
}

// Literali întregi
class Int extends Expression {
    Int(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Construcția if.
class If extends Expression {
    // Sunt necesare trei câmpuri pentru cele trei componente ale expresiei.
    Expression cond;
    Expression thenBranch;
    Expression elseBranch;

    If(Expression cond,
       Expression thenBranch,
       Expression elseBranch,
       Token start) {
        super(start);
        this.cond = cond;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class FloatNum extends Expression {
    FloatNum(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Bool extends Expression {
    Bool(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Token-ul pentru un Assign va fi '='
class Assign extends Expression {
    Id id;
    Expression expr;

    Assign(Id id, Expression expr, Token token) {
        super(token);
        this.id = id;
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Pentru un Relational avem 'op=(LT | LE | EQUAL)' ca reprezentare
class Relational extends Expression {
    Expression left;
    Expression right;

    Relational(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Pentru un PlusMinus avem 'op=(PLUS | MINUS)' ca reprezentare
class Plus extends Expression {
    Expression left;
    Expression right;

    Plus(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Minus extends Expression {
    Expression left;
    Expression right;

    Minus(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Pentru un MultDiv avem 'op=(MULT | DIV)' ca reprezentare
class Mult extends Expression {
    Expression left;
    Expression right;

    Mult(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Div extends Expression {
    Expression left;
    Expression right;

    Div(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Pentru UnaryMinus, operatorul va fi '-'
class UnaryMinus extends Expression {
    Expression expr;

    UnaryMinus(Expression expr, Token op) {
        super(op);
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Apelul unei functii poate avea oricate argumente. Le vom salva intr-o lista
class Call extends Expression {
    Id id;
    LinkedList<Expression> args;

    Call(Id id, LinkedList<Expression> args, Token start) {
        super(start);
        this.id = id;
        this.args = args;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

abstract class Definition extends ASTNode {
    Definition(Token token) {
        super(token);
    }
}

class Type extends ASTNode {
    Type(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Argumentul din definita unei functii
class Formal extends ASTNode {
    Type type;
    Id id;

    Formal(Type type, Id id, Token token) {
        super(token);
        this.type = type;
        this.id = id;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Initializarea poate sa lipseasca
class VarDef extends Definition {
    Type type;
    Id id;
    Expression initValue;

    VarDef(Type type, Id id, Expression initValue, Token token) {
        super(token);
        this.type = type;
        this.id = id;
        this.initValue = initValue;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Definirea unei functii
class FuncDef extends Definition {
    Type type;
    Id id;
    LinkedList<Formal> formals;
    Expression body;

    FuncDef(Type type, Id id, LinkedList<Formal> formals, Expression body, Token token) {
        super(token);
        this.type = type;
        this.id = id;
        this.formals = formals;
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Program extends ASTNode {
    LinkedList<ASTNode> stmts;

    Program(LinkedList<ASTNode> stmts, Token token) {
        super(token);
        this.stmts = stmts;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
