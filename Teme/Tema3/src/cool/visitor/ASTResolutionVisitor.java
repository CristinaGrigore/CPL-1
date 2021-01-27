package cool.visitor;

import cool.AST.*;
import cool.parser.CoolParser;
import cool.scopes.CaseScope;
import cool.scopes.Scope;
import cool.scopes.SymbolTable;
import cool.symbols.IdSymbol;
import cool.symbols.TypeSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.Objects;
import java.util.stream.Collectors;

public class ASTResolutionVisitor implements ASTVisitor<TypeSymbol> {
	Scope scope;

	@Override
	public TypeSymbol visit(ASTProgramNode programNode) {
		programNode.getClasses().forEach(this::visit);
		return null;
	}

	@Override
	public TypeSymbol visit(ASTClassNode classNode) {
		if (classNode.getType() == null) {
			return null;
		}

		scope = classNode.getType();
		classNode.getContent().forEach(node -> node.accept(this));
		var type = classNode.getType();
		var parent = type.getParent();

		int i = type.getParentNumMethods() * 4;
		int j = parent != null ? ((TypeSymbol)parent).getNumAttrib() + 12 : 12;
		for (var node : classNode.getContent()) {
			if (node instanceof ASTMethodNode) {
				var methodSymbol = ((ASTMethodNode)node).getMethodSymbol();
				if (methodSymbol.getOverriddenMethod() == null) {
					methodSymbol.setOffset(i);
					i += 4;
				}
			} else {
				var idSymbol = ((ASTAttributeNode)node).getIdSymbol();
				idSymbol.makeAttribute();
				idSymbol.setOffset(j);
				j += 4;
			}
		}

		return null;
	}

	@Override
	public TypeSymbol visit(ASTFormalNode formalNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTMethodNode methodNode) {
		var methodSymbol = methodNode.getMethodSymbol();
		if (methodSymbol == null) {
			return null;
		}

		var methodName = methodNode.getName().getText();
		var classScope = (TypeSymbol)scope;
		var retRawType = methodSymbol.getReturnType();

		var overriddenMethod = ((TypeSymbol)classScope.getParent()).lookupMethod(methodName);
		if (overriddenMethod != null) {
			methodSymbol.setOverriddenMethod(overriddenMethod);

			var overriddenType = overriddenMethod.getReturnType();
			if (retRawType != overriddenType) {
				SymbolTable.error(
						methodNode.getContext(),
						methodNode.getRetType(),
						"Class " + classScope.getName() + " overrides method " + methodName
								+ " but changes return type from " + overriddenType + " to " + retRawType
				);
				return null;
			}

			var formals = methodNode.getMethodSymbol().getFormals();
			var overriddenFormals = overriddenMethod.getFormals();
			if (formals.size() != overriddenFormals.size()) {
				SymbolTable.error(
						methodNode.getContext(),
						methodNode.getName(),
						"Class " + classScope.getName() + " overrides method " + methodName
								+ " with different number of formal parameters"
				);
				return null;
			}

			TypeSymbol formalSymb, overriddenSymb;
			for (int i = 0; i != formals.size(); ++i) {
				formalSymb = formals.get(i).getType();
				overriddenSymb = overriddenFormals.get(i).getType();

				if (formalSymb == null || overriddenSymb == null) {
					continue;
				}

				if (!formalSymb.inherits(overriddenSymb)) {
					SymbolTable.error(
							methodNode.getContext(),
							((CoolParser.MethodDefContext)methodNode.getContext()).params.get(i).stop,
							"Class " + classScope.getName() + " overrides method " + methodName
									+ " but changes type of formal parameter " + formals.get(i).getName() + " from "
									+ overriddenSymb.getName() + " to " + formalSymb.getName()
					);
					return null;
				}
			}
		}

		scope = methodSymbol;
		var body = methodNode.getBody();
		var bodyType = methodNode.getBody().accept(this);
		if (bodyType != null) {
			if (!bodyType.getName().equals(retRawType.getName())) {
				bodyType = getActualType(bodyType.getName());
				if (!bodyType.inherits(retRawType)) {
					SymbolTable.error(
							methodNode.getContext(),
							body.getContext().start,
							"Type " + bodyType + " of the body of method " + methodName
									+ " is incompatible with declared return type " + retRawType
					);
				}
			}
		}
		scope = classScope;

		return null;
	}

	@Override
	public TypeSymbol visit(ASTAttributeNode attributeNode) {
		var symbol = attributeNode.getIdSymbol();
		if (symbol == null) {
			return null;
		}

		var typeSymbol= getActualType(symbol.getType().getName());
		var value = attributeNode.getValue();
		if (value != null) {
			var valueRawType = value.accept(this);
			var valueType = getActualType(valueRawType.getName());

			if (!valueType.inherits(typeSymbol)) {
				SymbolTable.error(
						attributeNode.getContext(),
						value.getContext().start,
						"Type " + valueRawType +  " of initialization expression of attribute "  + symbol
								+ " is incompatible with declared type " + typeSymbol
				);
			}
		}

		return typeSymbol;
	}

	@Override
	public TypeSymbol visit(ASTLocalVarNode localVarNode) {
		if (localVarNode.getIdSymbol() == null) {
			return null;
		}

		var varName = localVarNode.getName().getText();
		var varType = localVarNode.getType().getText();
		var varRawType = (TypeSymbol)SymbolTable.globals.lookup(varType);

		var varTypeSymbol = getActualType(varType);
		if (varTypeSymbol == null) {
			SymbolTable.error(
					localVarNode.getContext(),
					localVarNode.getType(),
					"Let variable " + varName + " has undefined type " + varType
			);
			localVarNode.setIdSymbol(null);

			return null;
		}

		var value = localVarNode.getValue();
		if (value != null) {
			var valueType = value.accept(this);
			if (valueType != null && !valueType.inherits(varTypeSymbol)) {
				SymbolTable.error(
						localVarNode.getContext(),
						value.getContext().start,
						"Type " + valueType + " of initialization expression of identifier " + varName
								+ " is incompatible with declared type " + varTypeSymbol
				);
			}
		}

		localVarNode.getIdSymbol().setType(varRawType);

		return null;
	}

	@Override
	public TypeSymbol visit(ASTIntNode intNode) {
		return TypeSymbol.INT;
	}

	@Override
	public TypeSymbol visit(ASTIdNode idNode) {
		var idName = idNode.getSymbol();

		var idSymbol = (IdSymbol)scope.lookup(idName);
		if (idSymbol == null) {
			SymbolTable.error(
					idNode.getContext(),
					idNode.getContext().getStop(),
					"Undefined identifier " + idName
			);
			return null;
		}

		return idSymbol.getType();
	}

	@Override
	public TypeSymbol visit(ASTBoolNode boolNode) {
		return TypeSymbol.BOOL;
	}

	@Override
	public TypeSymbol visit(ASTStringNode stringNode) {
		return TypeSymbol.STRING;
	}

	@Override
	public TypeSymbol visit(ASTAssignNode assignNode) {
		var idName = assignNode.getId().getText();
		if (idName.equals("self")) {
			SymbolTable.error(assignNode.getContext(), assignNode.getId(), "Cannot assign to self");
			return null;
		}

		var idSymbol = (IdSymbol)scope.lookup(idName);
		if (idSymbol == null) {
			return null;
		}

		var idType = getActualType(idSymbol.getType().getName());
		var value = assignNode.getValue();
		var exprRawType = value.accept(this);

		if (idType == null || exprRawType == null) {
			return null;
		}

		var exprType = getActualType(exprRawType.getName());
		if (!exprType.inherits(idType)) {
			SymbolTable.error(
					assignNode.getContext(),
					assignNode.getValue().getContext().start,
					"Type " + exprRawType + " of assigned expression is incompatible with declared type "
							+ idSymbol.getType() + " of identifier " + idName
			);
		}

		return exprRawType;
	}

	@Override
	public TypeSymbol visit(ASTNewNode newNode) {
		var typeName = newNode.getType().getText();
		var rawType = (TypeSymbol)SymbolTable.globals.lookup(typeName);
		var type = getActualType(typeName);
		if (type == null) {
			SymbolTable.error(
					newNode.getContext(),
					newNode.getType(),
					"new is used with undefined type " + typeName
			);
			return null;
		}

		return rawType;
	}

	@Override
	public TypeSymbol visit(ASTDispatchNode dispatchNode) {
		var caller = dispatchNode.getCaller();
		var actualCallerType = caller != null ?
				caller.accept(this)
				: getActualType("SELF_TYPE");
		var callerType = caller != null ?
				getActualType(caller.accept(this).getName())
				: getActualType("SELF_TYPE");

		var actualObj = dispatchNode.getActualCaller();
		if (actualObj != null) {
			var actualTypeName = actualObj.getText();
			if (actualTypeName.equals("SELF_TYPE")) {
				SymbolTable.error(
						dispatchNode.getContext(),
						dispatchNode.getActualCaller(),
						"Type of static dispatch cannot be SELF_TYPE"
				);
				return TypeSymbol.OBJECT;
			}

   			var actualType = (TypeSymbol)SymbolTable.globals.lookup(actualTypeName);
			if (actualType == null) {
				SymbolTable.error(
						dispatchNode.getContext(),
						dispatchNode.getActualCaller(),
						"Type " + actualObj.getText() + " of static dispatch is undefined"
				);
				return TypeSymbol.OBJECT;
			}

			if (!callerType.inherits(actualType)) {
				SymbolTable.error(
						dispatchNode.getContext(),
						dispatchNode.getActualCaller(),
						"Type " + actualType + " of static dispatch is not a superclass of type " + callerType
				);
				return TypeSymbol.OBJECT;
			}

			callerType = actualType;
		}
		dispatchNode.setCallerType(callerType);

		var methodName = dispatchNode.getCallee().getText();
		var methodSymb = callerType.lookupMethod(methodName);
		if (methodSymb == null) {
			SymbolTable.error(
					dispatchNode.getContext(),
					dispatchNode.getCallee(),
					"Undefined method " + methodName + " in class " + callerType
			);
			return TypeSymbol.OBJECT;
		}

		var actualTypes = dispatchNode
				.getParams()
				.stream()
				.map(expr -> expr.accept(this))
				.collect(Collectors.toList());
		var formalIds = methodSymb.getFormals();
		if (actualTypes.size() != formalIds.size()) {
			SymbolTable.error(
					dispatchNode.getContext(),
					dispatchNode.getCallee(),
					"Method " + methodName + " of class " + callerType
							+ " is applied to wrong number of arguments"
			);
			return TypeSymbol.OBJECT;
		}

		for (int i = 0; i != actualTypes.size(); ++i) {
			if (!actualTypes.get(i).inherits(formalIds.get(i).getType())) {
				SymbolTable.error(
						dispatchNode.getContext(),
						getToken(dispatchNode.getContext(), i),
						"In call to method " + methodName + " of class " + callerType + ", actual type "
								+ actualTypes.get(i) + " of formal parameter " + formalIds.get(i)
								+ " is incompatible with declared type " + formalIds.get(i).getType()
				);
			}
		}

		if (methodSymb.getReturnType() == TypeSymbol.SELF_TYPE) {
			return actualCallerType;
		}

		return methodSymb.getReturnType();
	}

	private Token getToken(ParserRuleContext context, int idx) {
		if (context instanceof CoolParser.ImplicitDispatchContext) {
			return ((CoolParser.ImplicitDispatchContext)context).params.get(idx).start;
		} else {
			return ((CoolParser.ExplicitDispatchContext)context).params.get(idx).start;
		}
	}

	@Override
	public TypeSymbol visit(ASTBinaryOperatorNode binaryOpNode) {
		var leftSymb = binaryOpNode.getLeftOp().accept(this);
		var rightSymb = binaryOpNode.getRightOp().accept(this);

		if (leftSymb == null || rightSymb == null) {
			if (binaryOpNode instanceof ASTRelOpNode) {
				return TypeSymbol.BOOL;
			}
			return TypeSymbol.INT;
		}

		var leftName = leftSymb.getName();
		var rightName = rightSymb.getName();

		if (binaryOpNode.getSymbol().equals("=")) {
			if (TypeSymbol.notEqCompatible(leftSymb, rightSymb)) {
				SymbolTable.error(
						binaryOpNode.getContext(),
						((CoolParser.RelOpContext)binaryOpNode.getContext()).op,
						"Cannot compare " + leftName + " with " + rightName
				);
			}
		} else {
			if (leftSymb != TypeSymbol.INT && rightSymb == TypeSymbol.INT) {
				SymbolTable.error(
						binaryOpNode.getContext(),
						binaryOpNode.getContext().start,
						"Operand of " + binaryOpNode.getSymbol() + " has type " + leftSymb
								+ " instead of Int"
				);
			}
			if (leftSymb == TypeSymbol.INT && rightSymb != TypeSymbol.INT) {
				SymbolTable.error(
						binaryOpNode.getContext(),
						binaryOpNode.getContext().stop,
						"Operand of " + binaryOpNode.getSymbol() + " has type " + rightSymb
								+ " instead of Int"
				);
			}
		}

		return binaryOpNode instanceof ASTRelOpNode ? TypeSymbol.BOOL : TypeSymbol.INT;
	}

	@Override
	public TypeSymbol visit(ASTUnaryOperatorNode unaryOpNode) {
		TypeSymbol opType = unaryOpNode.getOp().accept(this);
		if (opType == null) {
			return null;
		}

		if (unaryOpNode instanceof ASTNegNode && opType != TypeSymbol.INT) {
			SymbolTable.error(
					unaryOpNode.getContext(),
					unaryOpNode.getContext().stop,
					"Operand of " + unaryOpNode.getSymbol() + " has type " + opType.getName()
							+ " instead of Int"
			);
			return TypeSymbol.INT;
		}
		if (unaryOpNode instanceof ASTNotNode && opType != TypeSymbol.BOOL) {
			SymbolTable.error(
					unaryOpNode.getContext(),
					unaryOpNode.getContext().stop,
					"Operand of " + unaryOpNode.getSymbol() + " has type " + opType.getName()
							+ " instead of Bool"
			);
			return TypeSymbol.BOOL;
		}
		if (unaryOpNode instanceof ASTIsVoidNode) {
			return TypeSymbol.BOOL;
		}

		return opType;
	}

	@Override
	public TypeSymbol visit(ASTIfNode ifNode) {
		var condType = ifNode.getCond().accept(this);
		if (condType != TypeSymbol.BOOL) {
			SymbolTable.error(
					ifNode.getContext(),
					((CoolParser.IfContext)ifNode.getContext()).cond.start,
					"If condition has type " + condType + " instead of Bool"
			);
		}

		return TypeSymbol.getLCA(
				ifNode.getThenBranch().accept(this),
				ifNode.getElseBranch().accept(this)
		);
	}

	@Override
	public TypeSymbol visit(ASTWhileNode whileNode) {
		var condType = whileNode.getCond().accept(this);
		if (condType != TypeSymbol.BOOL) {
			SymbolTable.error(
					whileNode.getContext(),
					((CoolParser.WhileContext)whileNode.getContext()).cond.start,
					"While condition has type " + condType + " instead of Bool"
			);
		}

		whileNode.getBody().accept(this);

		return TypeSymbol.OBJECT;
	}

	@Override
	public TypeSymbol visit(ASTLetNode letNode) {
		scope = letNode.getLetSymbol();
		IdSymbol localSymbol;

		for (var localVar : letNode.getLocals()) {
			localVar.accept(this);

			localSymbol = localVar.getIdSymbol();
			if (localSymbol != null) {
				scope.add(localSymbol);
			}
		}

		var retValue = letNode.getBody().accept(this);
		scope = scope.getParent();

		return retValue;
	}

	@Override
	public TypeSymbol visit(ASTCaseBranchNode caseBranchNode) {
		var typeName = caseBranchNode.getType().getText();
		if (typeName.equals("SELF_TYPE")) {
			return null;
		}

		var caseType = getActualType(typeName);
		if (caseType == null) {
			SymbolTable.error(
					caseBranchNode.getContext(),
					caseBranchNode.getType(),
					"Case variable " + caseBranchNode.getId().getText() + " has undefined type " + typeName
			);
			return null;
		}
		caseBranchNode.setTypeSymbol(caseType);

		var caseScope = new CaseScope(scope);
		var id = new IdSymbol(caseBranchNode.getId().getText());
		id.setType(caseType);
		caseScope.add(id);

		id.setOffset(-4);
		caseBranchNode.setScope(caseScope);

		scope = caseScope;
		var retType = caseBranchNode.getBody().accept(this);
		scope = caseScope.getParent();

		return retType;
	}

	@Override
	public TypeSymbol visit(ASTCaseNode caseNode) {
		return caseNode
				.getBranches()
				.stream()
				.map(br -> br.accept(this))
				.filter(Objects::nonNull)
				.reduce(TypeSymbol::getLCA).orElse(null);
	}

	@Override
	public TypeSymbol visit(ASTBlockNode blockNode) {
		return blockNode
				.getExpressions()
				.stream()
				.map(expr -> expr.accept(this))
				.filter(Objects::nonNull)
				.reduce((first, second) -> second).orElse(null);
	}

	private TypeSymbol getActualType(String typeName) {
		if (!typeName.equals("SELF_TYPE")) {
			return (TypeSymbol)SymbolTable.globals.lookup(typeName);
		}

		Scope currentScope = scope;
		while (!(currentScope instanceof TypeSymbol)) {
			currentScope = currentScope.getParent();
		}

		return (TypeSymbol)currentScope;
	}
}
