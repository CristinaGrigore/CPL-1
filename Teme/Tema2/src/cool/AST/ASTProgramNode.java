package cool.AST;

import cool.visitor.ASTVisitor;

import java.util.List;

public class ASTProgramNode extends ASTNode {
	private final List<ASTClassNode> classes;

	public ASTProgramNode(List<ASTClassNode> classes) {
		super("program");
		this.classes = classes;
	}

	public List<ASTClassNode> getClasses() {
		return classes;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
