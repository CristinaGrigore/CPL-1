package cool.AST;

import org.antlr.v4.runtime.Token;

public abstract class ASTBinaryOperatorNode extends ASTExpressionNode {
	private final ASTExpressionNode leftOp;
	private final ASTExpressionNode rightOp;

	public ASTBinaryOperatorNode(Token symbol, ASTExpressionNode leftOp, ASTExpressionNode rightOp) {
		super(symbol.getText());
		this.leftOp = leftOp;
		this.rightOp = rightOp;
	}

	public ASTExpressionNode getLeftOp() {
		return leftOp;
	}

	public ASTExpressionNode getRightOp() {
		return rightOp;
	}
}
