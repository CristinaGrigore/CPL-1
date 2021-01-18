package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public abstract class ASTUnaryOperatorNode extends ASTExpressionNode {
	private final ASTExpressionNode op;

	public ASTUnaryOperatorNode(ParserRuleContext context, Token symbol, ASTExpressionNode op) {
		super(context, symbol.getText());
		this.op = op;
	}

	public ASTExpressionNode getOp() {
		return op;
	}
}
