package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class ASTMethodNode extends ASTClassContentNode {
	private final Token name;
	private final Token retType;
	private final List<ASTFormalNode> params;
	private final List<ASTExpressionNode> body;

	public ASTMethodNode(
			Token name,
			Token retType,
			List<ASTFormalNode> params,
			List<ASTExpressionNode> body
	) {
		super("method");
		this.name = name;
		this.retType = retType;
		this.params = params;
		this.body = body;
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

	public List<ASTExpressionNode> getBody() {
		return body;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
