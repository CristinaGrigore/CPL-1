package cool.AST;

import cool.visitor.ASTVisitor;

import java.util.List;

public class ASTBlockNode extends ASTExpressionNode {
	private final List<ASTExpressionNode> expressions;

	public ASTBlockNode(List<ASTExpressionNode> expressions) {
		super("block");
		this.expressions = expressions;
	}

	public List<ASTExpressionNode> getExpressions() {
		return expressions;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
