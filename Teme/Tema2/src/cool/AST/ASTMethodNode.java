package cool.AST;

import cool.symbols.MethodSymbol;
import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class ASTMethodNode extends ASTClassContentNode {
	private final Token name;
	private final Token retType;
	private final List<ASTFormalNode> params;
	private final ASTExpressionNode body;
	MethodSymbol methodSymbol;

	public ASTMethodNode(
			ParserRuleContext context,
			Token name,
			Token retType,
			List<ASTFormalNode> params,
			ASTExpressionNode body
	) {
		super(context, "method");
		this.name = name;
		this.retType = retType;
		this.params = params;
		this.body = body;
	}

	public MethodSymbol getMethodSymbol() {
		return methodSymbol;
	}

	public void setMethodSymbol(MethodSymbol methodSymbol) {
		this.methodSymbol = methodSymbol;
	}

	public Token getName() {
		return name;
	}

	public Token getRetType() {
		return retType;
	}

	public List<ASTFormalNode> getParams() {
		return params;
	}

	public ASTExpressionNode getBody() {
		return body;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
