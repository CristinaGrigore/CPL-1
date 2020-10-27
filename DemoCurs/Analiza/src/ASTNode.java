import org.antlr.v4.runtime.Token;

// Generam AST din arborele de parsare
public abstract class ASTNode {
	public abstract <T> T accept(ASTVisitor<T> visitor);
}

abstract class Expression extends ASTNode {
	Token token;

	public Expression(Token token) { this.token = token; }
}

class IntNode extends Expression {
	public IntNode(Token token) {
		super(token);
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

class IdNode extends Expression {
	public IdNode(Token token) {
		super(token);
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

class IfNode extends Expression {
	Expression cond;
	Expression thenBranch;
	Expression elseBranch;

	public IfNode(
		Expression cond,
		Expression thenBranch,
		Expression elseBranch,
		Token token
	) {
		super(token);
		this.cond = cond;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
