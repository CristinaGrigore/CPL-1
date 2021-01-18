package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class ASTNode {
	private final String symbol;
	private final ParserRuleContext context;

	public ASTNode(ParserRuleContext context,  String symbol) {
		this.context = context;
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public ParserRuleContext getContext() {
		return context;
	}

	public abstract <T> T accept(ASTVisitor<T> visitor);
}
