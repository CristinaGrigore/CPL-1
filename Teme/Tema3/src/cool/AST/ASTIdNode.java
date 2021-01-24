package cool.AST;

import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ASTIdNode extends ASTExpressionNode {

	public ASTIdNode(ParserRuleContext context, Token symbol) {
		super(context, symbol.getText());
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
