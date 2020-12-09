package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class ASTBlockNode extends ASTExpressionNode {
	private final List<ASTExpressionNode> expressions;

	public ASTBlockNode(ParserRuleContext context, List<ASTExpressionNode> expressions) {
		super(context, "block");
		this.expressions = expressions;
	}

	public List<ASTExpressionNode> getExpressions() {
		return expressions;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
