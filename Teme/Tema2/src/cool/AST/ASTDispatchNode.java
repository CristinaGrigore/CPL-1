package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class ASTDispatchNode extends ASTExpressionNode {
	private final ASTExpressionNode caller;
	private final Token actualCaller;
	private final Token callee;
	private final List<ASTExpressionNode> params;

	public ASTDispatchNode(
			ParserRuleContext context,
			String type,
			ASTExpressionNode caller,
			Token actualCaller,
			Token callee,
			List<ASTExpressionNode> params
	) {
		super(context, type);
		this.caller = caller;
		this.actualCaller = actualCaller;
		this.callee = callee;
		this.params = params;
	}

	public ASTExpressionNode getCaller() {
		return caller;
	}

	public Token getActualCaller() {
		return actualCaller;
	}

	public Token getCallee() {
		return callee;
	}

	public List<ASTExpressionNode> getParams() {
		return params;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
