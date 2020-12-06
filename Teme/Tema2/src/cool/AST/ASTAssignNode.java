package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.Token;

public class ASTAssignNode extends ASTExpressionNode {
	private final Token id;
	private final ASTExpressionNode value;

	public ASTAssignNode(Token id, ASTExpressionNode value) {
		super("<-");
		this.id = id;
		this.value = value;
	}

	public Token getId() {
		return id;
	}

	public ASTExpressionNode getValue() {
		return value;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
