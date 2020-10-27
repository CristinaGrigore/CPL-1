public interface ASTVisitor<T> {
	public T visit(IntNode node);
	public T visit(IdNode node);
	public T visit(IfNode node);
}
