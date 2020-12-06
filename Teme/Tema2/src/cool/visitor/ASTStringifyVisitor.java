package cool.visitor;

import cool.AST.*;

import java.util.stream.Collectors;

public class ASTStringifyVisitor implements ASTVisitor<String> {
       private int indent;

       public ASTStringifyVisitor() {
               indent = 0;
       }

       private String getIndent() {
               return " ".repeat(indent);
       }

       private String getStartingSymbol(ASTNode node) {
               return getIndent() + node.getSymbol() + "\n";
       }

       @Override
       public String visit(ASTProgramNode programNode) {
               String str = getStartingSymbol(programNode);

               indent += 2;
               str += programNode
                               .getClasses()
                               .stream()
                               .map(this::visit)
                               .collect(Collectors.joining(""));
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTClassNode classNode) {
               String str = getIndent() + classNode.getSymbol() + "\n";

               indent += 2;
               str += getIndent() + classNode.getName().getText() + "\n";

               var baseName = classNode.getBaseName();
               if (baseName != null) {
                       str += getIndent() + baseName.getText() + "\n";
               }

               str += classNode
                               .getContent()
                               .stream()
                               .map(node -> node.accept(this))
                               .collect(Collectors.joining(""));
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTFormalNode formalNode) {
               String str = getStartingSymbol(formalNode);

               indent += 2;
               str += getIndent() + formalNode.getName().getText() + "\n";
               str += getIndent() + formalNode.getType().getText() + "\n";
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTMethodNode methodNode) {
               String str = getStartingSymbol(methodNode);

               indent += 2;
               str += getIndent() + methodNode.getName().getText() + "\n";
               str += methodNode
                               .getParams()
                               .stream()
                               .map(this::visit)
                               .collect(Collectors.joining(""));
               str += getIndent() + methodNode.getRetType().getText() + "\n";
               str += methodNode
                               .getBody()
                               .stream()
                               .map(node -> node.accept(this))
                               .collect(Collectors.joining(""));
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTIntNode intNode) {
               return getStartingSymbol(intNode);
       }

       @Override
       public String visit(ASTBoolNode boolNode) {
               return getStartingSymbol(boolNode);
       }

       @Override
       public String visit(ASTStringNode stringNode) {
               return getStartingSymbol(stringNode);
       }

       @Override
       public String visit(ASTAttributeNode attributeNode) {
               String str = getStartingSymbol(attributeNode);

               indent += 2;
               str += getIndent() + attributeNode.getName().getText() + "\n";
               str += getIndent() + attributeNode.getType().getText() + "\n";

               var value = attributeNode.getValue();
               if (value != null) {
                       str += value.accept(this);
               }
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTLocalVarNode localVarNode) {
               String str = getStartingSymbol(localVarNode);

               indent += 2;
               str += getIndent() + localVarNode.getName().getText() + "\n";
               str += getIndent() + localVarNode.getType().getText() + "\n";

               var value = localVarNode.getValue();
               if (value != null) {
                       str += value.accept(this);
               }
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTAssignNode assignNode) {
               String str = getStartingSymbol(assignNode);

               indent += 2;
               str += getIndent() + assignNode.getId().getText() + "\n";
               str += assignNode.getValue().accept(this);
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTNewNode newNode) {
               String str = getIndent() + newNode.getSymbol() + "\n";

               indent += 2;
               str += getIndent() + newNode.getType().getText() + "\n";
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTBinaryOperatorNode binaryOpNode) {
               String str = getStartingSymbol(binaryOpNode);

               indent += 2;
               str += binaryOpNode.getLeftOp().accept(this);
               str += binaryOpNode.getRightOp().accept(this);
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTUnaryOperatorNode unaryOpNode) {
               String str = getStartingSymbol(unaryOpNode);

               indent += 2;
               str += unaryOpNode.getOp().accept(this);
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTDispatchNode dispatchNode) {
               String str = getStartingSymbol(dispatchNode);

               indent += 2;
               var caller = dispatchNode.getCaller();
               if (caller != null) {
                       str += caller.accept(this);
               }

               var actualCaller = dispatchNode.getActualCaller();
               if (actualCaller != null) {
                       str += getIndent() + actualCaller.getText() + "\n";
               }

               str +=  getIndent() + dispatchNode.getCallee().getText() + "\n";
               str += dispatchNode
                               .getParams()
                               .stream()
                               .map(node -> node.accept(this))
                               .collect(Collectors.joining(""));
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTIfNode ifNode) {
               String str = getStartingSymbol(ifNode);

               indent += 2;
               str += ifNode.getCond().accept(this);
               str += ifNode.getThenBranch().accept(this);
               str += ifNode.getElseBranch().accept(this);
               indent -= 2;

               return  str;
       }

       @Override
       public String visit(ASTWhileNode whileNode) {
               String str = getStartingSymbol(whileNode);

               indent += 2;
               str += whileNode.getCond().accept(this);
               str += whileNode.getBody().accept(this);
               indent -= 2;

               return  str;
       }

       @Override
       public String visit(ASTLetNode letNode) {
               String str = getStartingSymbol(letNode);

               indent += 2;
               str += letNode
                               .getLocals()
                               .stream()
                               .map(this::visit)
                               .collect(Collectors.joining(""));
               str += letNode.getBody().accept(this);
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTCaseBranchNode caseBranchNode) {
               String str = getStartingSymbol(caseBranchNode);

               indent += 2;
               str += getIndent() + caseBranchNode.getId().getText() + "\n";
               str += getIndent() + caseBranchNode.getType().getText() + "\n";
               str += caseBranchNode.getBody().accept(this);
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTCaseNode caseNode) {
               String str = getStartingSymbol(caseNode);

               indent += 2;
               str += caseNode.getVar().accept(this);
               str += caseNode
                               .getBranches()
                               .stream()
                               .map(this::visit)
                               .collect(Collectors.joining(""));
               indent -= 2;

               return str;
       }

       @Override
       public String visit(ASTBlockNode blockNode) {
               String str = getStartingSymbol(blockNode);

               indent += 2;
               str += blockNode
                               .getExpressions()
                               .stream()
                               .map(node -> node.accept(this))
                               .collect(Collectors.joining(""));
               indent -= 2;

               return str;
       }
}

