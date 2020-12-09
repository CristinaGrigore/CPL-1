package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;

public abstract class ASTExpressionNode extends ASTNode {
	public ASTExpressionNode(ParserRuleContext context, String symbol) {
		super(context, symbol);
	}
}
