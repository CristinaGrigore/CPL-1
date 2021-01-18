package cool.AST;

import cool.symbols.TypeSymbol;
import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class ASTClassNode extends ASTNode {
	private final Token name;
	private final Token baseName;
	private final List<ASTClassContentNode> content;
	private TypeSymbol type;

	public ASTClassNode(ParserRuleContext context, Token name, Token baseName, List<ASTClassContentNode> content) {
		super(context, "class");
		this.name = name;
		this.baseName = baseName;
		this.content = content;
	}

	public TypeSymbol getType() {
		return type;
	}

	public void setType(TypeSymbol type) {
		this.type = type;
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
		return visitor.visit(this);
	}
}
