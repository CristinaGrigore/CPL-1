package cool.visitor;

import cool.AST.*;
import cool.parser.CoolParser;
import cool.parser.CoolParserBaseVisitor;

import java.util.stream.Collectors;

public class ASTConstructionVisitor extends CoolParserBaseVisitor<ASTNode> {
	@Override
	public ASTNode visitProgram(CoolParser.ProgramContext ctx) {
		var classList = ctx.classes
				.stream()
				.map(node -> (ASTClassNode)visitClassDef(node))
				.collect(Collectors.toList());
		return new ASTProgramNode(ctx, classList);
	}

	@Override
	public ASTNode visitClassDef(CoolParser.ClassDefContext ctx) {
		var contentList = ctx.content
				.stream()
				.map(node -> (ASTClassContentNode)visit(node))
				.collect(Collectors.toList());

		return new ASTClassNode(ctx, ctx.name, ctx.baseName, contentList);
	}

	@Override
	public ASTNode visitMemberDef(CoolParser.MemberDefContext ctx) {
		var value = ctx.value != null
				? (ASTExpressionNode)visit(ctx.value)
				: null;

		return new ASTAttributeNode(
				ctx,
				ctx.ID().getSymbol(),
				ctx.TYPE().getSymbol(),
				value
		);
	}

	@Override
	public ASTNode visitMethodDef(CoolParser.MethodDefContext ctx) {
		var paramsList = ctx.params
				.stream()
				.map(node -> (ASTFormalNode)visitFormal(node))
				.collect(Collectors.toList());

		return new ASTMethodNode(
				ctx,
				ctx.ID().getSymbol(),
				ctx.TYPE().getSymbol(),
				paramsList,
				(ASTExpressionNode)ctx.body.accept(this)
		);
	}

	@Override
	public ASTNode visitVarDef(CoolParser.VarDefContext ctx) {
		var value = ctx.value != null
				? (ASTExpressionNode)visit(ctx.value)
				: null;

		return new ASTLocalVarNode(
				ctx,
				ctx.ID().getSymbol(),
				ctx.TYPE().getSymbol(),
				value
		);
	}

	@Override
	public ASTNode visitCaseBranch(CoolParser.CaseBranchContext ctx) {
		return new ASTCaseBranchNode(
				ctx,
				ctx.ID().getSymbol(),
				ctx.TYPE().getSymbol(),
				(ASTExpressionNode)visit(ctx.body)
		);
	}

	@Override
	public ASTNode visitNew(CoolParser.NewContext ctx) {
		return new ASTNewNode(ctx, ctx.TYPE().getSymbol());
	}

	@Override
	public ASTNode visitMinus(CoolParser.MinusContext ctx) {
		return new ASTMinusNode(
				ctx,
				ctx.MINUS().getSymbol(),
				(ASTExpressionNode)visit(ctx.leftOp),
				(ASTExpressionNode)visit(ctx.rightOp)
		);
	}

	@Override
	public ASTNode visitMult(CoolParser.MultContext ctx) {
		return new ASTMultNode(
				ctx,
				ctx.MULT().getSymbol(),
				(ASTExpressionNode)visit(ctx.leftOp),
				(ASTExpressionNode)visit(ctx.rightOp)
		);
	}

	@Override
	public ASTNode visitPlus(CoolParser.PlusContext ctx) {
		return new ASTPlusNode(
				ctx,
				ctx.PLUS().getSymbol(),
				(ASTExpressionNode)visit(ctx.leftOp),
				(ASTExpressionNode)visit(ctx.rightOp)
		);
	}

	@Override
	public ASTNode visitDiv(CoolParser.DivContext ctx) {
		return new ASTDivNode(
				ctx,
				ctx.DIV().getSymbol(),
				(ASTExpressionNode)visit(ctx.leftOp),
				(ASTExpressionNode)visit(ctx.rightOp)
		);
	}

	@Override
	public ASTNode visitBool(CoolParser.BoolContext ctx) {
		return new ASTBoolNode(ctx, ctx.BOOL().getSymbol());
	}

	@Override
	public ASTNode visitString(CoolParser.StringContext ctx) {
		return new ASTStringNode(ctx, ctx.STRING().getSymbol());
	}

	@Override
	public ASTNode visitIsvoid(CoolParser.IsvoidContext ctx) {
		return new ASTIsVoidNode(ctx, ctx.ISVOID().getSymbol(), (ASTExpressionNode)visit(ctx.expr()));
	}

	@Override
	public ASTNode visitBlock(CoolParser.BlockContext ctx) {
		return new ASTBlockNode(
				ctx,
				ctx.body
				.stream()
				.map(node -> (ASTExpressionNode)visit(node))
				.collect(Collectors.toList())
		);
	}

	@Override
	public ASTNode visitWhile(CoolParser.WhileContext ctx) {
		return new ASTWhileNode(
				ctx,
				(ASTExpressionNode)visit(ctx.cond),
				(ASTExpressionNode)visit(ctx.body)
		);
	}

	@Override
	public ASTNode visitRelOp(CoolParser.RelOpContext ctx) {
		return new ASTRelOpNode(
				ctx,
				ctx.op,
				(ASTExpressionNode)visit(ctx.leftOp),
				(ASTExpressionNode)visit(ctx.rightOp)
		);
	}

	@Override
	public ASTNode visitNot(CoolParser.NotContext ctx) {
		return new ASTNotNode(ctx, ctx.NOT().getSymbol(), (ASTExpressionNode)visit(ctx.expr()));
	}

	@Override
	public ASTNode visitInt(CoolParser.IntContext ctx) {
		return new ASTIntNode(ctx, ctx.start);
	}

	@Override
	public ASTNode visitNeg(CoolParser.NegContext ctx) {
		return new ASTNegNode(ctx, ctx.NEG().getSymbol(), (ASTExpressionNode)visit(ctx.expr()));
	}

	@Override
	public ASTNode visitParen(CoolParser.ParenContext ctx) {
		return ctx.op.accept(this);
	}

	@Override
	public ASTNode visitLet(CoolParser.LetContext ctx) {
		var localVars = ctx.members
				.stream()
				.map(node -> (ASTLocalVarNode)visit(node))
				.collect(Collectors.toList());

		return new ASTLetNode(ctx, localVars, (ASTExpressionNode)visit(ctx.body));
	}

	@Override
	public ASTNode visitId(CoolParser.IdContext ctx) {
		return new ASTIdNode(ctx, ctx.start);
	}

	@Override
	public ASTNode visitIf(CoolParser.IfContext ctx) {
		return new ASTIfNode(
				ctx,
				(ASTExpressionNode)visit(ctx.cond),
				(ASTExpressionNode)visit(ctx.thenBranch),
				(ASTExpressionNode)visit(ctx.elseBranch)
		);
	}

	@Override
	public ASTNode visitCase(CoolParser.CaseContext ctx) {
		var branchesList = ctx.branches
				.stream()
				.map(node -> (ASTCaseBranchNode)visit(node))
				.collect(Collectors.toList());

		return new ASTCaseNode(ctx, ctx.start, (ASTExpressionNode)visit(ctx.var), branchesList);
	}

	@Override
	public ASTNode visitAssign(CoolParser.AssignContext ctx) {
		return new ASTAssignNode(ctx, ctx.ID().getSymbol(),(ASTExpressionNode)visit(ctx.expr()));
	}

	@Override
	public ASTNode visitFormal(CoolParser.FormalContext ctx) {
		return new ASTFormalNode(ctx, ctx.ID().getSymbol(), ctx.TYPE().getSymbol());
	}

	@Override
	public ASTNode visitImplicitDispatch(CoolParser.ImplicitDispatchContext ctx) {
		var paramsList = ctx.params
				.stream()
				.map(node -> (ASTExpressionNode)visit(node))
				.collect(Collectors.toList());

		return new ASTDispatchNode(
				ctx,
				"implicit dispatch",
				null,
				null,
				ctx.ID().getSymbol(),
				paramsList
		);
	}

	@Override
	public ASTNode visitExplicitDispatch(CoolParser.ExplicitDispatchContext ctx) {
		var paramsList = ctx.params
				.stream()
				.map(node -> (ASTExpressionNode)visit(node))
				.collect(Collectors.toList());

		var type = ctx.TYPE();
		var typeSymbol = type != null ? type.getSymbol() : null;

		return new ASTDispatchNode(
				ctx,
				".",
				(ASTExpressionNode)visit(ctx.caller),
				typeSymbol,
				ctx.ID().getSymbol(),
				paramsList
		);
	}
}
