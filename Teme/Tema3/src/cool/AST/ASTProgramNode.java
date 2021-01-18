package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class ASTProgramNode extends ASTNode {
	private final List<ASTClassNode> classes;

	public ASTProgramNode(ParserRuleContext context, List<ASTClassNode> classes) {
		super(context, "program");
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
