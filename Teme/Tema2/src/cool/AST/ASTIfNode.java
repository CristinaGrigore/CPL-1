package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ASTIfNode extends ASTExpressionNode {
	private final ASTExpressionNode cond;
	private final ASTExpressionNode thenBranch;
	private final ASTExpressionNode elseBranch;

	public ASTIfNode(
			ParserRuleContext context,
			ASTExpressionNode cond,
			ASTExpressionNode thenBranch,
			ASTExpressionNode elseBranch
	) {
		super(context, "if");
		this.cond = cond;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	public ASTExpressionNode getCond() {
		return cond;
	}

	public ASTExpressionNode getThenBranch() {
		return thenBranch;
	}

	public ASTExpressionNode getElseBranch() {
		return elseBranch;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
