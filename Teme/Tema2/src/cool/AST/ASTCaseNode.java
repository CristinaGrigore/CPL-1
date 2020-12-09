package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class ASTCaseNode extends ASTExpressionNode {
	private final ASTExpressionNode var;
	private final List<ASTCaseBranchNode> branches;

	public ASTCaseNode(ParserRuleContext context, ASTExpressionNode var, List<ASTCaseBranchNode> branches) {
		super(context, "case");
		this.var = var;
		this.branches = branches;
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
