package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.Token;

public class ASTBoolNode extends ASTExpressionNode {
	public ASTBoolNode(Token symbol) {
		super(symbol.getText());
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
