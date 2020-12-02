package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.Token;

public class ASTIsVoidNode extends  ASTUnaryOperatorNode {
	public ASTIsVoidNode(Token symbol, ASTExpressionNode op) {
		super(symbol, op);
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
