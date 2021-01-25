package cool.AST;

import cool.scopes.CaseScope;
import cool.symbols.TypeSymbol;
import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ASTCaseBranchNode extends ASTNode {
	private final Token id;
	private final Token type;
	private final ASTExpressionNode body;
	private TypeSymbol typeSymbol;
	private CaseScope scope;

	public ASTCaseBranchNode(ParserRuleContext context, Token id, Token type, ASTExpressionNode body) {
		super(context, "case branch");
		this.id = id;
		this.type = type;
		this.body = body;
	}

	public CaseScope getScope() {
		return scope;
	}

	public void setScope(CaseScope scope) {
		this.scope = scope;
	}

	public TypeSymbol getTypeSymbol() {
		return typeSymbol;
	}

	public void setTypeSymbol(TypeSymbol typeSymbol) {
		this.typeSymbol = typeSymbol;
	}

	public Token getId() {
		return id;
	}

	public Token getType() {
		return type;
	}

	public ASTExpressionNode getBody() {
		return body;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
