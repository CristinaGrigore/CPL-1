package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ASTWhileNode extends ASTExpressionNode {
	private final ASTExpressionNode cond;
	private final ASTExpressionNode body;

	public ASTWhileNode(ParserRuleContext context, ASTExpressionNode cond, ASTExpressionNode body) {
		super(context, "while");
		this.cond = cond;
		this.body = body;
	}

	public ASTExpressionNode getCond() {
		return cond;
	}

	public ASTExpressionNode getBody() {
		return body;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
