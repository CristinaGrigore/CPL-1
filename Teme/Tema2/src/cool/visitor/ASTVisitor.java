package cool.visitor;

import cool.AST.*;

public interface ASTVisitor<T> {
	T visit(ASTProgramNode programNode);
	T visit(ASTClassNode classNode);
	T visit(ASTFormalNode formalNode);
	T visit(ASTMethodNode methodNode);
	T visit(ASTAttributeNode attributeNode);
	T visit(ASTLocalVarNode localVarNode);
	T visit(ASTIntNode intNode);
	T visit(ASTIdNode idNode);
	T visit(ASTBoolNode boolNode);
	T visit(ASTStringNode stringNode);
	T visit(ASTAssignNode assignNode);
	T visit(ASTNewNode newNode);
	T visit(ASTDispatchNode dispatchNode);
	T visit(ASTBinaryOperatorNode binaryOpNode);
	T visit(ASTUnaryOperatorNode unaryOpNode);
	T visit(ASTIfNode ifNode);
	T visit(ASTWhileNode whileNode);
	T visit(ASTLetNode letNode);
	T visit(ASTCaseBranchNode caseBranchNode);
	T visit(ASTCaseNode caseNode);
	T visit(ASTBlockNode blockNode);
}
