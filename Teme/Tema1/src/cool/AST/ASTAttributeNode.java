package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.Token;

public class ASTAttributeNode extends ASTClassContentNode {
	private final Token name;
	private final Token type;
	private final ASTExpressionNode value;

	public ASTAttributeNode(Token name, Token type, ASTExpressionNode value) {
		super("attribute");
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public Token getName() {
		return name;
	}

	public Token getType() {
		return type;
	}

	public ASTExpressionNode getValue() {
		return value;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
