package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class ASTCaseNode extends ASTExpressionNode {
	private final Token token;
	private final ASTExpressionNode var;
	private final List<ASTCaseBranchNode> branches;

	public ASTCaseNode(ParserRuleContext context, Token token, ASTExpressionNode var, List<ASTCaseBranchNode> branches) {
		super(context, "case");
		this.token = token;
		this.var = var;
		this.branches = branches;
	}

	public Token getToken() {
		return token;
	}

	public ASTExpressionNode getVar() {
		return var;
	}

	public List<ASTCaseBranchNode> getBranches() {
		return branches;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
