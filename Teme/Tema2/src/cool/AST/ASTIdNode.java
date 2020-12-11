package cool.AST;

import cool.symbols.IdSymbol;
import cool.visitor.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ASTIdNode extends ASTExpressionNode {
//	private IdSymbol idSymbol;

	public ASTIdNode(ParserRuleContext context, Token symbol) {
		super(context, symbol.getText());
	}

//	public IdSymbol getIdSymbol() {
//		return idSymbol;
//	}
//
//	public void setIdSymbol(IdSymbol idSymbol) {
//		this.idSymbol = idSymbol;
//	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
