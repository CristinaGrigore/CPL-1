package cool.visitor;

import cool.AST.*;
import cool.compiler.Compiler;
import cool.parser.CoolParser;
import cool.scopes.Scope;
import cool.scopes.SymbolTable;
import cool.symbols.IdSymbol;
import cool.symbols.TypeSymbol;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CodeGenVisitor implements ASTVisitor<ST> {
	private final STGroupFile templates;
	private long cnt;
	private long nextIdx;
	private long caseCnt;
	Scope scope;

	private final ST constants;
	private final ST classNames;
	private final ST classObjs;
	private final ST protObjs;
	private final ST dispTables;
	private final ST methods;

	private final HashMap<String, Integer> strings;
	private final HashSet<Integer> ints;

	private String addString(String s) {
		int label;
		if (!strings.containsKey(s)) {
			int len = s.length();

			label = strings.size();
			strings.put(s, label);

			ST stringST = templates.getInstanceOf("stringConst")
					.add("label", label)
					.add("tag", TypeSymbol.STRING.getTag())
					.add("str", s)
					.add("len", addInt(len))
					.add("wordCount", (len + 1) / 4 + 5);
			constants.add("e", stringST);
		} else {
			label = strings.get(s);
		}

		return "str_const_" + label;
	}

	private String addInt(int n) {
		if (!ints.contains(n)) {
			ints.add(n);

			ST intST = templates.getInstanceOf("intConst")
					.add("tag", TypeSymbol.INT.getTag())
					.add("val", n);
			constants.add("e", intST);
		}

		return "int_const_" + n;
	}

	private ST makeInitMethod(ASTClassNode node) {
		var type = node.getType();
		var attribs = node
				.getContent()
				.stream()
				.filter(content -> content instanceof ASTAttributeNode)
				.map(attrib -> attrib.accept(this))
				.filter(Objects::nonNull)
				.map(ST::render)
				.collect(Collectors.joining("\n"));

		return templates.getInstanceOf("initMethod")
				.add("class", type)
				.add("parent", type.getParentName())
				.add("attrib", attribs);
	}

	public CodeGenVisitor() {
		templates = new STGroupFile("cgen.stg");
		strings = new HashMap<>();
		ints = new HashSet<>();
		cnt = 0;

		constants = templates.getInstanceOf("sequence");
		classNames = templates.getInstanceOf("sequence");
		classObjs = templates.getInstanceOf("sequence");
		dispTables = templates.getInstanceOf("sequence");
		protObjs = templates.getInstanceOf("sequence");
		methods = templates.getInstanceOf("sequenceSpaced");

		addInt(0);
		addString("");

		SymbolTable.globals.getSymbols()
				.values()
				.stream()
				.sorted(Comparator.comparingInt(ts -> ((TypeSymbol)ts).getTag()))
				.forEach(clss -> {
					if (clss != TypeSymbol.SELF_TYPE) {
						var className = clss.getName();
						dispTables.add("e", ((TypeSymbol)clss).getDispTable(templates));
						classObjs.add("e", templates.getInstanceOf("objTabEntry")
								.add("class", clss));
						classNames.add("e", "\t.word\t" + addString(className));
						protObjs.add("e", ((TypeSymbol)clss).getProtObj(templates));
						addInt(className.length());
					}
				});

		methods.add("e", TypeSymbol.OBJECT.getInitMethod(templates))
				.add("e", TypeSymbol.INT.getInitMethod(templates))
				.add("e", TypeSymbol.BOOL.getInitMethod(templates))
				.add("e", TypeSymbol.STRING.getInitMethod(templates))
				.add("e", TypeSymbol.IO.getInitMethod(templates));
	}

	@Override
	public ST visit(ASTProgramNode programNode) {
		programNode.getClasses().forEach(this::visit);

		ST intTag = templates.getInstanceOf("basicTag")
				.add("name", "int")
				.add("tag", TypeSymbol.INT.getTag());
		ST boolTag = templates.getInstanceOf("basicTag")
				.add("name", "bool")
				.add("tag", TypeSymbol.BOOL.getTag());
		ST stringTag = templates.getInstanceOf("basicTag")
				.add("name", "string")
				.add("tag", TypeSymbol.STRING.getTag());
		ST basicTags = templates.getInstanceOf("sequence")
				.add("e", intTag)
				.add("e", boolTag)
				.add("e", stringTag);

		return templates.getInstanceOf("program")
				.add("basicTags", basicTags)
				.add("boolTag", TypeSymbol.BOOL.getTag())
				.add("consts", constants)
				.add("classNames", classNames)
				.add("classObjs", classObjs)
				.add("protObjs", protObjs)
				.add("dispTables", dispTables)
				.add("methods", methods);
	}

	@Override
	public ST visit(ASTClassNode classNode) {
		scope = classNode.getType();

		methods.add("e", makeInitMethod(classNode));

		classNode.getContent().forEach(content -> {
			if (content instanceof ASTMethodNode) {
				ST methodST = content.accept(this).add("class", classNode.getType());
				methods.add("e", methodST);
			}
		});
		return null;
	}

	@Override
	public ST visit(ASTFormalNode formalNode) {
		return null;
	}

	@Override
	public ST visit(ASTMethodNode methodNode) {
		scope = methodNode.getMethodSymbol();
		var methodST = templates.getInstanceOf("method")
				.add("name", methodNode.getMethodSymbol())
				.add("body", methodNode.getBody().accept(this))
				.add("numPops", methodNode.getParams().size() * 4 + 12);
		scope = scope.getParent();

		return methodST;
	}

	@Override
	public ST visit(ASTAttributeNode attributeNode) {
		var expr = attributeNode.getValue();
		if (expr == null) {
			return null;
		}

		return templates.getInstanceOf("storeAttrib")
				.add("val", expr.accept(this))
				.add("offset", attributeNode.getIdSymbol().getOffset());
	}

	@Override
	public ST visit(ASTLocalVarNode localVarNode) {
		var valueExpr= localVarNode.getValue();
		var varSymbol = localVarNode.getIdSymbol();
		var varType = varSymbol.getType();

		String value;
		if (valueExpr == null) {
			if (varType == TypeSymbol.INT) {
				value = "\tla\t\t$a0 int_const_0";
			} else if (varType == TypeSymbol.STRING) {
				value = "\tla\t\t$a0 str_const_0";
			} else if (varType == TypeSymbol.BOOL) {
				value = "\tla\t\t$a0 bool_const_false";
			} else {
				value = "\tli\t\t$a0 0";
			}
		} else {
			value = valueExpr.accept(this).render();
		}

		return templates.getInstanceOf("storeVar")
				.add("val", value)
				.add("offset", varSymbol.getOffset());
	}

	@Override
	public ST visit(ASTIntNode intNode) {
		return templates.getInstanceOf("literal")
				.add("addr", addInt(Integer.parseInt(intNode.getSymbol())));
	}

	@Override
	public ST visit(ASTIdNode idNode) {
		var idName = idNode.getSymbol();
		if (idName.equals("self")) {
			return templates.getInstanceOf("self");
		}

		var idSymbol = (IdSymbol)scope.lookup(idName);
		ST idST;

		if (idSymbol.isAttribute()) {
			idST = templates.getInstanceOf("attribute");
		} else {
			idST = templates.getInstanceOf("loadVar");
		}

		return idST.add("offset", idSymbol.getOffset());
	}

	@Override
	public ST visit(ASTBoolNode boolNode) {
		return templates.getInstanceOf("literal")
				.add("addr", "bool_const_" + boolNode.getSymbol());
	}

	@Override
	public ST visit(ASTStringNode stringNode) {
		return templates.getInstanceOf("literal")
				.add("addr", addString(stringNode.getSymbol()));
	}

	@Override
	public ST visit(ASTAssignNode assignNode) {
		ST value = assignNode.getValue().accept(this);
		var idSymbol = (IdSymbol)scope.lookup(assignNode.getId().getText());
		ST assignST;

		if (idSymbol.isAttribute()) {
			assignST = templates.getInstanceOf("storeAttrib");
		} else {
			assignST = templates.getInstanceOf("storeVar");
		}

		return assignST.add("val", value).add("offset", idSymbol.getOffset());
	}

	@Override
	public ST visit(ASTNewNode newNode) {
		var type = newNode.getType().getText();
		if (type.equals("SELF_TYPE")) {
			return templates.getInstanceOf("newSelfType");
		}

		return templates.getInstanceOf("new").add("type", type);
	}

	@Override
	public ST visit(ASTDispatchNode dispatchNode) {
		var method = dispatchNode.getCallee().getText();
		int offset = dispatchNode.getCallerType().lookupMethod(method).getOffset();

		var reversedParams = dispatchNode.getParams();
		Collections.reverse(reversedParams);

		var params = reversedParams.stream().map(paramExpr -> {
			var param = paramExpr.accept(this);
			return templates.getInstanceOf("dispatchParam")
					.add("param", param).render();
		}).collect(Collectors.joining("\n"));

		ST dispatch = templates.getInstanceOf("dispatch")
				.add("method", method)
				.add("idx", cnt++)
				.add("params", params)
				.add("offset", offset)
				.add("filename", getFileName(dispatchNode))
				.add("line", dispatchNode.getCallee().getLine());

		var exprCaller = dispatchNode.getCaller();
		var caller = exprCaller != null && !exprCaller.getSymbol().equals("self")
				? exprCaller.accept(this)
				: null;
		if (caller != null) {
			dispatch.add("explicit", caller);
		}

		var specificCaller = dispatchNode.getActualCaller();
		if (specificCaller != null) {
			dispatch.add("specific", specificCaller.getText());
		}

		return dispatch;
	}

	@Override
	public ST visit(ASTBinaryOperatorNode binaryOpNode) {
		if (!(binaryOpNode instanceof ASTRelOpNode)) {
			ST mathST = templates.getInstanceOf("arithm")
					.add("expr1", binaryOpNode.getLeftOp().accept(this))
					.add("expr2", binaryOpNode.getRightOp().accept(this));

			if (binaryOpNode instanceof ASTPlusNode) {
				return mathST.add("op", "add");
			} else if (binaryOpNode instanceof ASTMinusNode) {
				return mathST.add("op", "sub");
			} else if (binaryOpNode instanceof ASTMultNode) {
				return mathST.add("op", "mul");
			} else {
				return mathST.add("op", "div");
			}
		} else if (binaryOpNode.getSymbol().equals("=")) {
			return templates.getInstanceOf("equal")
					.add("expr1", binaryOpNode.getLeftOp().accept(this))
					.add("expr2", binaryOpNode.getRightOp().accept(this))
					.add("cnt", cnt++);
		} else {
			return templates.getInstanceOf("cmp")
					.add("expr1", binaryOpNode.getLeftOp().accept(this))
					.add("expr2", binaryOpNode.getRightOp().accept(this))
					.add("op", binaryOpNode.getSymbol().equals("<") ? "blt" : "ble")
					.add("cnt", cnt++);
		}
	}

	@Override
	public ST visit(ASTUnaryOperatorNode unaryOpNode) {
		if (unaryOpNode instanceof ASTIsVoidNode) {
			return templates.getInstanceOf("isvoid")
					.add("expr", unaryOpNode.getOp().accept(this))
					.add("cnt", cnt++);
		} else if (unaryOpNode instanceof ASTNotNode) {
			return templates.getInstanceOf("not")
					.add("expr", unaryOpNode.getOp().accept(this))
					.add("cnt", cnt++);
		} else {
			return templates.getInstanceOf("neg")
					.add("expr", unaryOpNode.getOp().accept(this));
		}
	}

	@Override
	public ST visit(ASTIfNode ifNode) {
		return templates.getInstanceOf("if")
				.add("cond", ifNode.getCond().accept(this))
				.add("then", ifNode.getThenBranch().accept(this))
				.add("els", ifNode.getElseBranch().accept(this))
				.add("cnt", cnt++);
	}

	@Override
	public ST visit(ASTWhileNode whileNode) {
		return templates.getInstanceOf("while")
				.add("cond", whileNode.getCond().accept(this))
				.add("body", whileNode.getBody().accept(this))
				.add("cnt", cnt++);
	}

	@Override
	public ST visit(ASTLetNode letNode) {
		ST initST = templates.getInstanceOf("initLet")
				.add("space", letNode.getLocals().size() * -4);
		ST letST = templates.getInstanceOf("sequence").add("e", initST);
		int i = -4;

		scope = letNode.getLetSymbol();
		for (var local : letNode.getLocals()) {
			local.getIdSymbol().setOffset(i);
			letST.add("e", visit(local));
			i -= 4;
		}

		letST.add("e", letNode.getBody().accept(this));
		scope = scope.getParent();

		ST finST = templates.getInstanceOf("initLet")
				.add("space", letNode.getLocals().size() * 4);
		letST.add("e", finST);

		return letST;
	}

	@Override
	public ST visit(ASTCaseBranchNode caseBranchNode) {
		scope = caseBranchNode.getScope();
		var type = caseBranchNode.getTypeSymbol();
		ST brST = templates.getInstanceOf("caseBranch")
				.add("expr", caseBranchNode.getBody().accept(this))
				.add("lo", type.getTag())
				.add("hi", type.getMaxTag())
				.add("cnt", caseCnt)
				.add("nextIdx", nextIdx++);
		scope = scope.getParent();

		return brST;
	}

	@Override
	public ST visit(ASTCaseNode caseNode) {
		var branches = caseNode.getBranches();
		branches.sort(Comparator.comparingInt(br -> -br.getTypeSymbol().getTag()));
		var branchesCode = branches.stream()
				.map(this::visit)
				.map(ST::render)
				.collect(Collectors.joining("\n"));

		return templates.getInstanceOf("case")
				.add("expr", caseNode.getVar().accept(this))
				.add("branches", branchesCode)
				.add("cnt", caseCnt++)
				.add("filename", getFileName(caseNode))
				.add("line", caseNode.getToken().getLine());
	}

	private String getFileName(ASTNode node) {
		var ctx = node.getContext();
		while (!(ctx.getParent() instanceof CoolParser.ProgramContext)) {
			ctx = ctx.getParent();
		}

		return addString(new File(Compiler.fileNames.get(ctx)).getName());
	}

	@Override
	public ST visit(ASTBlockNode blockNode) {
		ST blockST = templates.getInstanceOf("sequence");
		blockNode.getExpressions().forEach(expr -> blockST.add("e", expr.accept(this)));

		return blockST;
	}
}
