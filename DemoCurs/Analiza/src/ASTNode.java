import org.antlr.v4.runtime.Token;

import java.util.List;

public abstract class ASTNode {
	Token token;

	public ASTNode(Token token) {
		this.token = token;
	}

	public Token getToken() {
		return token;
	}

	public abstract <T> T accept(ASTVisitor<T> visitor);
}

abstract class Expression extends ASTNode {
	Token token;

	public Expression(Token token) {
		super(token);
	}
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
	protected IdSymbol symbol;
	protected Scope scope;

	public IdNode(Token token) {
		super(token);
	}

	public void setSymbol(IdSymbol symbol) {
		this.symbol = symbol;
	}

	public IdSymbol getSymbol() {
		return symbol;
	}

	public Scope getScope() {
		return scope;
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
			Token token) {
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

class PlusNode extends Expression {
	Expression left;
	Expression right;

	public PlusNode(Token op, Expression left, Expression right) {
		super(op);
		this.left = left;
		this.right = right;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

class TypeNode extends ASTNode {
	public TypeNode(Token token) {
		super(token);
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

class VarDefNode extends ASTNode {
	IdNode id;
	TypeNode Type;

	public VarDefNode(Token token, IdNode id, TypeNode type) {
		super(token);
		this.id = id;
		Type = type;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}

class BlockNode extends Expression {
	List<ASTNode> statements;

	public BlockNode(Token token, List<ASTNode> statements) {
		super(token);
		this.statements = statements;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
