package cool.visitor;

import cool.AST.*;
import cool.scopes.SymbolTable;
import cool.symbols.TypeSymbol;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.HashMap;

public class CodeGenVisitor implements ASTVisitor<ST> {
	private final STGroupFile templates;
	private long ifCount;

	private final ST constants;
	private final ST classNames;
	private final ST classObjs;
	private final ST protObjs;
	private final ST dispTables;
	private final ST methods;

	private final HashMap<String, ST> strings;
	private final HashMap<Integer, ST> ints;

	public CodeGenVisitor() {
		templates = new STGroupFile("cgen.stg");
		strings = new HashMap<>();
		ints = new HashMap<>();
		ifCount = 0;

		var intZero = templates.getInstanceOf("intConst").add("val", 0);
		ints.put(0, intZero);
		strings.put(
				"",
				templates.getInstanceOf("stringConst")
						.add("str", "")
						.add("len", 0)
						.add("wordCount", 5)
		);

		classNames = templates.getInstanceOf("sequence");
		classObjs = templates.getInstanceOf("sequence");
		dispTables = templates.getInstanceOf("sequence");
		protObjs = templates.getInstanceOf("sequence");
		methods = templates.getInstanceOf("sequenceSpaced");

		SymbolTable.globals.getSymbols().values().forEach(clss -> {
			if (clss != TypeSymbol.SELF_TYPE) {
				var className = clss.getName();

				dispTables.add("e", ((TypeSymbol)clss).getDispTable(templates));
				classObjs.add("e", templates.getInstanceOf("objTabEntry").add("class", clss));
				classNames.add("e", "\t.word\tstr_const_" + clss);
				methods.add("e", ((TypeSymbol)clss).getInitMethod(templates));

				var classNameLen = className.length();
				if (!ints.containsKey(classNameLen)) {
					ints.put(
							classNameLen,
							templates.getInstanceOf("intConst").add("val", classNameLen)
					);
				}
				strings.put(
						className,
						templates.getInstanceOf("stringConst")
								.add("str", className)
								.add("len", classNameLen)
								.add("wordCount", (classNameLen + 1) / 4 + 5)
				);

				protObjs.add("e", ((TypeSymbol)clss).getProtObj(templates));
			}
		});

		constants = templates.getInstanceOf("sequence");
		ints.values().forEach(ct -> constants.add("e", ct));
		strings.values().forEach(ct -> constants.add("e", ct));
	}

	@Override
	public ST visit(ASTProgramNode programNode) {
		programNode.getClasses().forEach(this::visit);

		ST program = templates.getInstanceOf("program");
		program.add("consts", constants);
		program.add("classNames", classNames);
		program.add("classObjs", classObjs);
		program.add("protObjs", protObjs);
		program.add("dispTables", dispTables);
		program.add("methods", methods);

		return program;
	}

	@Override
	public ST visit(ASTClassNode classNode) {
		classNode.getContent().forEach(content -> {
			if (content instanceof ASTMethodNode) {
				var methodST = content.accept(this)
						.add("class", classNode.getType());
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
		return templates.getInstanceOf("method")
				.add("name", methodNode.getMethodSymbol())
				.add("body", methodNode.getBody().accept(this));
	}

	@Override
	public ST visit(ASTAttributeNode attributeNode) {
		return null;
	}

	@Override
	public ST visit(ASTLocalVarNode localVarNode) {
		return null;
	}

	@Override
	public ST visit(ASTIntNode intNode) {
		return null;
	}

	@Override
	public ST visit(ASTIdNode idNode) {
		return null;
	}

	@Override
	public ST visit(ASTBoolNode boolNode) {
		return null;
	}

	@Override
	public ST visit(ASTStringNode stringNode) {
		return null;
	}

	@Override
	public ST visit(ASTAssignNode assignNode) {
		return null;
	}

	@Override
	public ST visit(ASTNewNode newNode) {
		return null;
	}

	@Override
	public ST visit(ASTDispatchNode dispatchNode) {
		return null;
	}

	@Override
	public ST visit(ASTBinaryOperatorNode binaryOpNode) {
		return null;
	}

	@Override
	public ST visit(ASTUnaryOperatorNode unaryOpNode) {
		return null;
	}

	@Override
	public ST visit(ASTIfNode ifNode) {
		return null;
	}

	@Override
	public ST visit(ASTWhileNode whileNode) {
		return null;
	}

	@Override
	public ST visit(ASTLetNode letNode) {
		return null;
	}

	@Override
	public ST visit(ASTCaseBranchNode caseBranchNode) {
		return null;
	}

	@Override
	public ST visit(ASTCaseNode caseNode) {
		return null;
	}

	@Override
	public ST visit(ASTBlockNode blockNode) {
		return null;
	}
}
