package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public abstract class ASTClassContentNode extends ASTNode {
	public ASTClassContentNode(ParserRuleContext context, String symbol) {
		super(context, symbol);
	}
}
