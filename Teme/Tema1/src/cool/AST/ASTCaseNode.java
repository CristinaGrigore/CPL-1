package cool.AST;

import cool.visitor.ASTVisitor;

import java.util.List;

public class ASTCaseNode extends ASTExpressionNode {
	private final ASTExpressionNode var;
	private final List<ASTCaseBranchNode> branches;

	public ASTCaseNode(ASTExpressionNode var, List<ASTCaseBranchNode> branches) {
		super("case");
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
