package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ASTCaseBranchNode extends ASTNode {
	private final Token id;
	private final Token type;
	private final ASTExpressionNode body;

	public ASTCaseBranchNode(ParserRuleContext context, Token id, Token type, ASTExpressionNode body) {
		super(context, "case branch");
		this.id = id;
		this.type = type;
		this.body = body;
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
