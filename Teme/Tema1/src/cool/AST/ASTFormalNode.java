package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.Token;

public class ASTFormalNode extends ASTNode {
	private final Token name;
	private final Token type;

	public ASTFormalNode(Token name, Token type) {
		super("formal");
		this.name = name;
		this.type = type;
	}

	public Token getName() {
		return name;
	}

	public Token getType() {
		return type;
	}

	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
