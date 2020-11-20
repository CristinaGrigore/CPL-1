public interface ASTVisitor<T> {
	T visit(IntNode intNode);
	T visit(IdNode idNode);
	T visit(IfNode ifNode);
	T visit(BlockNode blockNode);
	T visit(TypeNode typeNode);
	T visit(VarDefNode varDefNode);
	T visit(PlusNode plusNode);
}
