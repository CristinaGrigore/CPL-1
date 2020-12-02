package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.stream.Collectors;

public class ASTClassNode extends ASTNode {
	private final Token name;
	private final Token baseName;
	private final List<ASTClassContentNode> content;

	public ASTClassNode(Token name, Token baseName, List<ASTClassContentNode> content) {
		super("class");
		this.name = name;
		this.baseName = baseName;
		this.content = content;
	}

	public Token getName() {
		return name;
	}

	public Token getBaseName() {
		return baseName;
	}

	public List<ASTClassContentNode> getContent() {
		return content;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return null;
	}
}
