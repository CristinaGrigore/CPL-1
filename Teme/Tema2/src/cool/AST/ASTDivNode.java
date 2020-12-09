package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ASTDivNode extends ASTBinaryOperatorNode {
	public ASTDivNode(ParserRuleContext context, Token symbol, ASTExpressionNode leftOp, ASTExpressionNode rightOp) {
		super(context, symbol, leftOp, rightOp);
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
