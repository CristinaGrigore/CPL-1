package cool.AST;

import cool.symbols.IdSymbol;
import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ASTLocalVarNode extends ASTClassContentNode {
	private final Token name;
	private final Token type;
	private final ASTExpressionNode value;
	private IdSymbol idSymbol;

	public ASTLocalVarNode(ParserRuleContext context, Token name, Token type, ASTExpressionNode value) {
		super(context, "local");
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public IdSymbol getIdSymbol() {
		return idSymbol;
	}

	public void setIdSymbol(IdSymbol idSymbol) {
		this.idSymbol = idSymbol;
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
