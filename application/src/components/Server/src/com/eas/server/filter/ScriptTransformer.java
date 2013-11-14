/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.filter;

import com.eas.client.AppCache;
import com.eas.client.ClientConstants;
import com.eas.client.metadata.ApplicationElement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.*;
import org.mozilla.javascript.ast.*;

/**
 *
 * @author kl, mg refactoring
 */
public class ScriptTransformer {

//    public static final String SELF_NAME = "_self";
    public static final String ERROR_BAD_AST = "bad Ast";
    public static final String ERROR_DEPENDECIES_PARSE_ERROR = "dependencies parse error";
    public static final String MODULES = "Modules";
    public static final String GET = "get";
    public static final String MODULE = "Module";
    public static final String SERVER_MODULE = "ServerModule";
    public static final String FORM = "Form";
    public static final String REPORT = "Report";
    public static final String SERVER_REPORT = "ServerReport";
    //private Name _this = new Name(0, SELF_NAME);
    private Set<String> dependencies = new HashSet<>();
    private Set<String> queryDependencies = new HashSet<>();
    private Set<String> serverDependencies = new HashSet<>();
    private Set<String> dynamicDependencies = new HashSet<>();
    //private Stack<Scope> scopeStack = new Stack<>();
    private AstRoot sourceRoot;
    private String source;
    private AppCache cache;
    private ErrorReporter compilationErrorReporter;
    //private Set<String> externalVariables = new HashSet<>();

    public ScriptTransformer(String aSource) {
        this(aSource, null);
    }

    public ScriptTransformer(String aSource, AppCache aCache) {
        super();
        source = aSource;
        cache = aCache;
    }

    /*
    private void recursiveScopeSetup(Map<Node, List<Node>> aTree, Node aParent) {
        List<Node> children = aTree.get(aParent);
        if (children != null && children.size() > 0) {
            if (aParent instanceof Scope && !(aParent instanceof AstRoot)) {
                Scope scope = (Scope) aParent;
                scope.setParentScope(scopeStack.peek());
                scopeStack.push(scope);
            }
            try {
                for (Node node : children) {
                    if (node instanceof Name) {
                        node.setScope(scopeStack.peek());
                    }
                    recursiveScopeSetup(aTree, node);
                }
            } finally {
                if (aParent instanceof Scope && !(aParent instanceof AstRoot)) {
                    scopeStack.pop();
                }
            }
        }
    }
*/
    private Map<Node, List<Node>> createAstTree(AstRoot aRoot) {
        final Map<Node, List<Node>> tree = new HashMap<>();
        aRoot.visit(new NodeVisitor() {
            @Override
            public boolean visit(AstNode node) {
                List<Node> children;
                if (tree.containsKey(node.getParent())) {
                    children = tree.get(node.getParent());
                } else {
                    children = new ArrayList<>();
                }
                children.add(node);
                tree.put(node.getParent(), children);
                return true;
            }
        });
        return tree;
    }

    public String transform() {
        CompilerEnvirons compilerEnv = CompilerEnvirons.ideEnvirons();
        compilerEnv.setRecordingLocalJsDocComments(true);
        compilerEnv.setAllowSharpComments(true);

        compilationErrorReporter = compilerEnv.getErrorReporter();
        Parser p = new Parser(compilerEnv, compilationErrorReporter);
        sourceRoot = p.parse(source, "", 0);
        //scopeStack.push(sourceRoot);
        //Map<Node, List<Node>> newTree = createAstTree(sourceRoot);
        //recursiveScopeSetup(newTree, sourceRoot);
        //final List<Runnable> treeMutationTasks = new ArrayList<>();
        sourceRoot.visit(new NodeVisitor() {
            public static final String REQUIRE_FUNCTION_NAME = "require";

            @Override
            public boolean visit(final AstNode node) {
                /*if (node instanceof VariableDeclaration) {
                    final List<VariableInitializer> variables = ((VariableDeclaration) node).getVariables();
                    assert !variables.isEmpty() : ERROR_BAD_AST;
                    final VariableInitializer anyVariable = (VariableInitializer) variables.get(0);
                    assert !anyVariable.isDestructuring() : ERROR_BAD_AST;
                    final Name name = (Name) anyVariable.getTarget();
                    if (isTopLevel(name)) {
                        treeMutationTasks.add(new Runnable() {
                            @Override
                            public void run() {
                                if (node.getParent() instanceof ForLoop) {
                                    ForLoop loop = (ForLoop) node.getParent();
                                    VariableDeclaration varInit = (VariableDeclaration) loop.getInitializer();
                                    List<VariableInitializer> variables = varInit.getVariables();
                                    assert !variables.isEmpty() : ERROR_BAD_AST;
                                    if (variables.size() == 1) {
                                        loop.setInitializer(variableInitializer2Assignment(variables.get(0)));
                                    } else {
                                        InfixExpression mainInfix, infix;
                                        Assignment assign = variableInitializer2Assignment(variables.get(0));
                                        infix = new InfixExpression();
                                        infix.setOperator(Token.COMMA);
                                        infix.setLeft(assign);
                                        mainInfix = infix;
                                        for (int i = 1; i < variables.size(); i++) {
                                            assign = variableInitializer2Assignment(variables.get(i));
                                            if (i < variables.size() - 1) {
                                                infix.setRight(new InfixExpression());
                                                infix = (InfixExpression) infix.getRight();
                                                infix.setLeft(assign);
                                            } else {
                                                infix.setRight(assign);
                                            }
                                            infix.setOperator(Token.COMMA);
                                        }
                                        loop.setInitializer(mainInfix);
                                    }
                                } else if (node.getParent() instanceof ForInLoop) {
                                    ForInLoop loop = (ForInLoop) node.getParent();
                                    if (node == loop.getIterator()) {
                                        VariableDeclaration varInit = (VariableDeclaration) loop.getIterator();
                                        List<VariableInitializer> variables = varInit.getVariables();
                                        Assignment assignment = variableInitializer2Assignment(variables.get(0));
                                        loop.getParent().addChildBefore(new ExpressionStatement(assignment), loop);
                                        if (node == loop.getIterator()) {
                                            loop.setIterator(new PropertyGet(_this, name));
                                        }
                                    }
                                } else {
                                    for (VariableInitializer variable : variables) {
                                        Assignment assignment = variableInitializer2Assignment(variable);
                                        node.getParent().addChildBefore(new ExpressionStatement(assignment), node);
                                    }
                                    node.getParent().removeChild(node);
                                }
                            }
                        });
                    } else if ((anyVariable.getInitializer() instanceof Name && isTopLevel((Name) anyVariable.getInitializer()))) {
                        treeMutationTasks.add(new Runnable() {
                            @Override
                            public void run() {
                                anyVariable.setInitializer(new PropertyGet(_this, (Name) anyVariable.getInitializer()));
                            }
                        });
                    }
                } else */if (node instanceof Name) {
                    attemptToParseDependenciesFromNode(node);
                    /*
                    final Name name = (Name) node;
                    if (isTopLevel(name) && !isObjectLiteralPropName(name)) {
                        if (!(name.getParent() instanceof PropertyGet) || ((PropertyGet) name.getParent()).getProperty() != name) {
                            if (node.getParent() instanceof FunctionNode) {
                                final FunctionNode oldFuncNode = (FunctionNode) node.getParent();
                                final PropertyGet property = new PropertyGet(_this, name);
                                final FunctionNode newFuncNode = new FunctionNode(0, new Name(0, ""));
                                newFuncNode.setParams(oldFuncNode.getParams());
                                newFuncNode.setBody(oldFuncNode.getBody());
                                treeMutationTasks.add(new Runnable() {
                                    @Override
                                    public void run() {
                                        oldFuncNode.getParent().replaceChild(oldFuncNode, new ExpressionStatement(new Assignment(Token.ASSIGN, property, newFuncNode, 0)));
                                    }
                                });
                            } else if (node.getParent() instanceof FunctionCall) {
                                FunctionCall funcCall = (FunctionCall) node.getParent();
                                if (node == funcCall.getTarget()) {
                                    funcCall.setTarget(new PropertyGet(_this, name));
                                } else {
                                    List<AstNode> funcArgs = funcCall.getArguments();
                                    for (Iterator<AstNode> it = funcArgs.iterator(); it.hasNext();) {
                                        AstNode funcArgNode = it.next();
                                        if (node == funcArgNode) {
                                            funcArgs.set(funcArgs.indexOf(funcArgNode), new PropertyGet(_this, name));
                                        }
                                    }
                                }
                            } else if (node.getParent() instanceof ReturnStatement) {
                                ReturnStatement retStmt = (ReturnStatement) node.getParent();
                                retStmt.setReturnValue(new PropertyGet(_this, name));
                            } else if (node.getParent() instanceof InfixExpression) {
                                InfixExpression infixExpr = (InfixExpression) node.getParent();
                                if (node == infixExpr.getLeft()) {
                                    infixExpr.setLeft(new PropertyGet(_this, name));
                                } else if (node == infixExpr.getRight()) {
                                    infixExpr.setRight(new PropertyGet(_this, name));
                                } else {
                                    assert false : ERROR_BAD_AST;
                                }
                            } else if (node.getParent() instanceof ConditionalExpression) {
                                ConditionalExpression condExpr = (ConditionalExpression) node.getParent();
                                if (node == condExpr.getTrueExpression()) {
                                    condExpr.setTrueExpression(new PropertyGet(_this, name));
                                } else if (node == condExpr.getFalseExpression()) {
                                    condExpr.setFalseExpression(new PropertyGet(_this, name));
                                } else if (node == condExpr.getTestExpression()) {
                                    condExpr.setTestExpression(new PropertyGet(_this, name));
                                } else {
                                    assert false : ERROR_BAD_AST;
                                }
                            } else if (node.getParent() instanceof ArrayLiteral) {
                                ArrayLiteral arrList = (ArrayLiteral) node.getParent();
                                List<AstNode> elements = arrList.getElements();
                                elements.set(elements.indexOf(name), new PropertyGet(_this, name));
                            } else if (node.getParent() instanceof UnaryExpression) {
                                UnaryExpression unaryExpr = (UnaryExpression) node.getParent();
                                unaryExpr.setOperand(new PropertyGet(_this, name));
                            } else if (node.getParent() instanceof ForInLoop) {
                                ForInLoop loop = (ForInLoop) node.getParent();
                                if (node == loop.getIteratedObject()) {
                                    loop.setIteratedObject(new PropertyGet(_this, name));
                                } else if (node == loop.getIterator()) {
                                    loop.setIterator(new PropertyGet(_this, name));
                                }
                            } else if (node.getParent() instanceof ElementGet) {
                                final ElementGet elGet = (ElementGet) node.getParent();
                                if (node == elGet.getElement()) {
                                    elGet.setElement(new PropertyGet(_this, name));
                                } else if (node == elGet.getTarget()) {
                                    elGet.setTarget(new PropertyGet(_this, name));
                                }
                            } else if (node.getParent() instanceof SwitchCase) {
                                final SwitchCase switchCase = (SwitchCase) node.getParent();
                                if (node == switchCase.getExpression()) {
                                    switchCase.setExpression(new PropertyGet(_this, name));
                                }
                            } else if (node.getParent() instanceof SwitchStatement) {
                                final SwitchStatement switchStatement = (SwitchStatement) node.getParent();
                                if (node == switchStatement.getExpression()) {
                                    switchStatement.setExpression(new PropertyGet(_this, name));
                                }
                            } else if (node.getParent() instanceof IfStatement) {
                                final IfStatement ifStatement = (IfStatement) node.getParent();
                                if (node == ifStatement.getCondition()) {
                                    ifStatement.setCondition(new PropertyGet(_this, name));
                                }
                            } else if (node.getParent() instanceof ParenthesizedExpression) {
                                final ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) node.getParent();
                                if (node == parenthesizedExpression.getExpression()) {
                                    parenthesizedExpression.setExpression(new PropertyGet(_this, name));
                                }
                            }
                        }
                    } else {
                        if (node.getParent().getParent() instanceof VariableDeclaration) {
                            VariableDeclaration varDecl = (VariableDeclaration) node.getParent().getParent();
                            if (varDecl.getType() == Token.CONST) {
                                varDecl.setType(Token.VAR);
                            }
                        }
                    }*/
                } else if (node instanceof StringLiteral && node.getParent() instanceof ArrayLiteral && node.getParent().getParent() instanceof FunctionCall) {
                    StringLiteral sl = (StringLiteral) node;
                    FunctionCall call = (FunctionCall) node.getParent().getParent();
                    if (call.getArguments() != null && !call.getArguments().isEmpty() && call.getArguments().get(0) == node.getParent()
                            && call.getTarget() instanceof Name && REQUIRE_FUNCTION_NAME.equals(((Name) call.getTarget()).getIdentifier())) {
                        dynamicDependencies.add(sl.getValue());
                    }
                } else if (node instanceof StringLiteral && node.getParent() instanceof FunctionCall) {
                    StringLiteral sl = (StringLiteral) node;
                    FunctionCall call = (FunctionCall) node.getParent();
                    if (call.getArguments() != null && !call.getArguments().isEmpty() && call.getArguments().get(0) == node
                            && call.getTarget() instanceof Name && REQUIRE_FUNCTION_NAME.equals(((Name) call.getTarget()).getIdentifier())) {
                        dynamicDependencies.add(sl.getValue());
                    }
                }
                for (Iterator it = node.iterator(); it.hasNext();) {
                    if (!(it.next() instanceof AstNode)) {
                        return false;
                    }

                }
                return true;
            }
        });
        /*
        for (Runnable task : treeMutationTasks) {
            task.run();
        }
        externalVariables.clear();
        */ 
        dependencies.removeAll(dynamicDependencies);
        serverDependencies.removeAll(dynamicDependencies);
        return sourceRoot.toSource();
    }

    private String getIdent(AstNode aNode) {
        if (aNode != null) {
            NewExpression expr = (NewExpression) aNode.getParent();
            List<AstNode> arguments = expr.getArguments();
            if (!arguments.isEmpty()) {
                if (arguments.get(0) instanceof StringLiteral
                        || arguments.get(0) instanceof NumberLiteral) {
                    String ident = null;
                    if (arguments.get(0) instanceof StringLiteral) {
                        ident = ((StringLiteral) arguments.get(0)).getValue();
                    } else if (arguments.get(0) instanceof NumberLiteral) {
                        ident = ((NumberLiteral) arguments.get(0)).getValue();
                    }
                    arguments.clear();
                    return ident;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void attemptToParseDependenciesFromNode(AstNode aNode) {
        Name name = (Name) aNode;
        if (name.getIdentifier().equals(GET) && name.getParent() instanceof PropertyGet) {
            if (((Name) ((PropertyGet) name.getParent()).getTarget()).getIdentifier().equals(MODULES)) {
                assert aNode.getParent().getParent() instanceof FunctionCall : ERROR_DEPENDECIES_PARSE_ERROR;
                FunctionCall funcCall = (FunctionCall) aNode.getParent().getParent();
                List<AstNode> arguments = funcCall.getArguments();
                if (!arguments.isEmpty() && arguments.get(0) instanceof StringLiteral) {
                    putDependence(((StringLiteral) arguments.get(0)).getValue());
                }
            }
        } else if(name.getIdentifier().equals("loadEntity") && name.getParent() instanceof PropertyGet){
            AstNode target = ((PropertyGet) name.getParent()).getTarget();
            if(target instanceof PropertyGet)
                target = ((PropertyGet)target).getProperty();
            if(target instanceof Name && ((Name) target).getIdentifier().equals("model")){
                assert aNode.getParent().getParent() instanceof FunctionCall : ERROR_DEPENDECIES_PARSE_ERROR;
                FunctionCall funcCall = (FunctionCall) aNode.getParent().getParent();
                List<AstNode> arguments = funcCall.getArguments();
                if (!arguments.isEmpty() && arguments.get(0) instanceof StringLiteral) {
                    putQueryDependence(((StringLiteral) arguments.get(0)).getValue());
                }
            }
        } else if (aNode.getParent() instanceof NewExpression) {
            if (name.getIdentifier().equals(FORM)
                    || name.getIdentifier().equals(MODULE)) {
                String ident = getIdent(aNode);
                if (ident != null) {
                    convertNameIdentifier(name, ident);
                    putDependence(ident);
                }
            } else if (name.getIdentifier().equals(REPORT)
                    || name.getIdentifier().equals(SERVER_REPORT)
                    || name.getIdentifier().equals(SERVER_MODULE)) {
                String ident = getIdent(aNode);
                if (ident != null) {
                    convertNameIdentifier(name, ident);
                    putServerDependencies(ident);
                }
            } else if (cache != null) {
                try {
                    ApplicationElement appElement = cache.get(name.getIdentifier());
                    if (appElement != null && (appElement.getType() == ClientConstants.ET_COMPONENT || appElement.getType() == ClientConstants.ET_FORM)) {
                        putDependence(name.getIdentifier());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ScriptTransformer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void convertNameIdentifier(Name aName, String aIdentifier) {
        switch (aName.getIdentifier()) {
            case REPORT:
            case SERVER_REPORT:
                aName.setIdentifier(SERVER_REPORT + aIdentifier);
                break;
            case SERVER_MODULE:
                aName.setIdentifier(SERVER_MODULE + aIdentifier);
                break;
            default:
                if (Character.isDigit(aIdentifier.charAt(0))) {
                    aName.setIdentifier(aName.getIdentifier() + aIdentifier);
                } else {
                    aName.setIdentifier(aIdentifier);
                }
                break;
        }
    }

    /*
    private Assignment variableInitializer2Assignment(VariableInitializer variable) {
        assert variable.getTarget() instanceof Name : ERROR_BAD_AST;
        AstNode left = variable.getTarget();
        if (isTopLevel((Name) variable.getTarget())) {
            left = new PropertyGet(_this, (Name) variable.getTarget());
        }

        AstNode right = variable.getInitializer();
        if (variable.getInitializer() instanceof Name && isTopLevel((Name) variable.getInitializer())) {
            right = new PropertyGet(_this, (Name) variable.getInitializer());
        } else if (variable.getInitializer() == null) {
            right = new KeywordLiteral(0, 4, Token.NULL);
        }
        Assignment assignment = new Assignment(Token.ASSIGN, left, right, 0);
        return assignment;
    }
    
    private boolean isTopLevel(Name aName) {
        return aName.getScope().getDefiningScope(aName.getIdentifier()) instanceof AstRoot
                || (aName.getScope().getDefiningScope(aName.getIdentifier()) == null
                && externalVariables.contains(aName.getIdentifier()));
    }
    
    private boolean isObjectLiteralPropName(Name aName){
        return aName != null && aName.getParent() instanceof ObjectProperty && ((ObjectProperty)aName.getParent()).getLeft() == aName;
    }
    */ 

    private void putDependence(String entityId) {
        if (!dependencies.contains(entityId)) {
            dependencies.add(entityId);
        }
    }
    
    private void putQueryDependence(String entityId) {
        if (!queryDependencies.contains(entityId)) {
            queryDependencies.add(entityId);
        }
    }

    public Set<String> getQueryDependencies() {
        return queryDependencies;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public ErrorReporter getErrorReporter() {
        return compilationErrorReporter;
    }

    /**
     * @return the externalVariables
     */
    /*
    public Set<String> getExternalVariables() {
        return externalVariables;
    }

    /**
     * @param externalVariables the externalVariables to set
     */
    /*
    public void setExternalVariables(Set<String> aExternalVariables) {
        externalVariables = aExternalVariables;
    }

    public void addExternalVariable(String aVarName) {
        if (aVarName != null && !aVarName.isEmpty()
                && !"null".equals(aVarName)
                && !externalVariables.contains(aVarName)) {
            externalVariables.add(aVarName);
        }
    }*/

    /**
     * @return the serverDependencies
     */
    public Set<String> getServerDependencies() {
        return serverDependencies;
    }

    /**
     * @param aServerDependence
     */
    public void putServerDependencies(String aServerDependence) {
        if (!serverDependencies.contains(aServerDependence)) {
            serverDependencies.add(aServerDependence);
        }
    }
}
