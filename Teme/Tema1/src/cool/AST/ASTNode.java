package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.Token;

public abstract class ASTNode {
	private final String symbol;

	public ASTNode(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public abstract <T> T accept(ASTVisitor<T> visitor);
}
