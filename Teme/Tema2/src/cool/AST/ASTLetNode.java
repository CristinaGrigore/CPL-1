package cool.AST;

import cool.visitor.ASTVisitor;

import java.util.List;

public class ASTLetNode extends ASTExpressionNode{
	private final List<ASTLocalVarNode> locals;
	private final ASTExpressionNode body;

	public ASTLetNode(List<ASTLocalVarNode> locals, ASTExpressionNode body) {
		super("let");
		this.locals = locals;
		this.body = body;
	}

	public List<ASTLocalVarNode> getLocals() {
		return locals;
	}

	public ASTExpressionNode getBody() {
		return body;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
