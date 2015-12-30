package com.eas.client.scripts;

import com.eas.concurrent.CallableConsumer;
import com.eas.script.Scripts;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.CallNode;
import jdk.nashorn.internal.ir.Expression;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.LiteralNode;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;

/**
 *
 * @author kl, mg refactoring
 */
public class DependenciesWalker {

    public static final String REQUIRE_FUNCTION_NAME = "require";
    public static final String DEFINE_FUNCTION_NAME = "define";
    public static final String MODEL = "model";
    public static final String LOAD_ENTITY = "loadEntity";
    public static final String SERVER_MODULE = "ServerModule";
    public static final String RPC_PROXY = "Proxy";
    private final Set<String> localFunctions = new HashSet<>();
    private final Set<String> dependenceLikeIdentifiers = new HashSet<>();
    private final Set<String> dependencies = new HashSet<>();
    private final Set<String> queryDependencies = new HashSet<>();
    private final Set<String> serverDependencies = new HashSet<>();
    private final Set<String> dynamicDependencies = new HashSet<>();
    private FunctionNode sourceRoot;
    private String source;
    private CallableConsumer<Boolean, String> validator;

    public DependenciesWalker(String aSource) {
        this(aSource, null);
    }

    public DependenciesWalker(String aSource, CallableConsumer<Boolean, String> aValidator) {
        super();
        source = aSource;
        validator = aValidator;
    }

    public void walk() {
        sourceRoot = Scripts.parseJs(source);
        sourceRoot.accept(new NodeVisitor<LexicalContext>(new LexicalContext()) {

            private final Deque<CallNode> calls = new LinkedList<>();

            @Override
            public boolean enterCallNode(CallNode callNode) {
                calls.push(callNode);
                if (callNode.getFunction() instanceof IdentNode) {
                    IdentNode toBeCalled = (IdentNode) callNode.getFunction();
                    processIdentNode(toBeCalled);
                }
                return super.enterCallNode(callNode);
            }

            @Override
            public Node leaveCallNode(CallNode callNode) {
                calls.pop();
                return super.leaveCallNode(callNode);
            }

            @Override
            public boolean enterIdentNode(IdentNode identNode) {
                processIdentNode(identNode);
                return super.enterIdentNode(identNode);
            }

            @Override
            public boolean enterLiteralNode(LiteralNode<?> literalNode) {
                if (literalNode.getType().isString() && !calls.isEmpty()) {
                    String value = literalNode.getString();
                    CallNode lastCall = calls.peek();
                    boolean arrayAtFirstArg = lastCall.getArgs().size() >= 1 && lastCall.getArgs().get(0) instanceof LiteralNode.ArrayLiteralNode;
                    boolean atFirstArg = lastCall.getArgs().size() >= 1 && lastCall.getArgs().get(0) == literalNode;
                    Expression fe = lastCall.getFunction();
                    if (fe instanceof IdentNode) {
                        IdentNode defineCall = (IdentNode) fe;
                        String funcName = defineCall.getName();
                        if (REQUIRE_FUNCTION_NAME.equals(funcName)) {
                            if (arrayAtFirstArg) {
                                LiteralNode.ArrayLiteralNode a = (LiteralNode.ArrayLiteralNode) lastCall.getArgs().get(0);
                                if (a.getElementExpressions().contains(literalNode)) {
                                    dynamicDependencies.add(value);
                                }
                            }
                            if (atFirstArg) {
                                dynamicDependencies.add(value);
                            }
                        }
                    } else if (fe instanceof AccessNode) {
                        AccessNode lastAccess = (AccessNode) fe;
                        String funcName = lastAccess.getProperty();
                        if (REQUIRE_FUNCTION_NAME.equals(funcName)) {
                            if (arrayAtFirstArg) {
                                LiteralNode.ArrayLiteralNode a = (LiteralNode.ArrayLiteralNode) lastCall.getArgs().get(0);
                                if (a.getElementExpressions().contains(literalNode)) {
                                    dynamicDependencies.add(value);
                                }
                            }
                            if (atFirstArg) {
                                dynamicDependencies.add(value);
                            }
                        } else if (/*lastCall.isNew() && */atFirstArg) {
                            switch (funcName) {
                                case RPC_PROXY:
                                case SERVER_MODULE:
                                    putServerDependence(value);
                                    break;
                                case LOAD_ENTITY:
                                    if (lastAccess.getBase() instanceof IdentNode) {
                                        String baseName = ((IdentNode) lastAccess.getBase()).getName();
                                        if (MODEL.equals(baseName) && LOAD_ENTITY.equals(funcName)) {
                                            queryDependencies.add(value);
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                }
                return super.enterLiteralNode(literalNode);
            }

            private void processIdentNode(IdentNode identNode) {
                String name = identNode.getName();
                dependenceLikeIdentifiers.add(name);
                try {
                    if (validator != null && validator.call(name) && defineCallbackScopeLevel <= 0) {
                        dependencies.add(name);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(DependenciesWalker.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            }

            private int scopeLevel;
            private int defineCallbackScopeLevel;

            @Override
            public boolean enterFunctionNode(FunctionNode functionNode) {
                scopeLevel++;
                if (scopeLevel == 2 && !functionNode.isAnonymous()) {
                    localFunctions.add(functionNode.getName());
                }
                if (!calls.isEmpty()) {
                    CallNode lastCall = calls.peek();
                    Expression ifDefineCall = lastCall.getFunction();
                    if (ifDefineCall instanceof AccessNode) {
                        AccessNode acc = (AccessNode) ifDefineCall;
                        if(DEFINE_FUNCTION_NAME.equals(acc.getProperty())){
                            defineCallbackScopeLevel++;
                        }
                        if (DEFINE_FUNCTION_NAME.equals(acc.getProperty()) || REQUIRE_FUNCTION_NAME.equals(acc.getProperty())) {
                            functionNode.getParameters().stream().forEach((definerArg) -> {
                                dynamicDependencies.add(definerArg.getName());
                            });
                        }
                    } else if (ifDefineCall instanceof IdentNode) {
                        IdentNode toBeCalled = (IdentNode) lastCall.getFunction();
                        if (DEFINE_FUNCTION_NAME.equals(toBeCalled.getName())){
                            defineCallbackScopeLevel++;
                        }
                        if (DEFINE_FUNCTION_NAME.equals(toBeCalled.getName()) || REQUIRE_FUNCTION_NAME.equals(toBeCalled.getName())) {
                            functionNode.getParameters().stream().forEach((definerArg) -> {
                                dynamicDependencies.add(definerArg.getName());
                            });
                        }
                    }
                }
                return super.enterFunctionNode(functionNode);
            }

            @Override
            public Node leaveFunctionNode(FunctionNode functionNode) {
                scopeLevel--;
                if (!calls.isEmpty()) {
                    CallNode lastCall = calls.peek();
                    Expression ifDefineCall = lastCall.getFunction();
                    if (ifDefineCall instanceof AccessNode) {
                        AccessNode acc = (AccessNode) ifDefineCall;
                        if(DEFINE_FUNCTION_NAME.equals(acc.getProperty())){
                            defineCallbackScopeLevel--;
                        }
                    } else if (ifDefineCall instanceof IdentNode) {
                        IdentNode toBeCalled = (IdentNode) lastCall.getFunction();
                        if (DEFINE_FUNCTION_NAME.equals(toBeCalled.getName())){
                            defineCallbackScopeLevel--;
                        }
                    }
                }
                return super.leaveFunctionNode(functionNode);
            }

        });
        dependencies.removeAll(localFunctions);
        dependencies.removeAll(dynamicDependencies);
    }

    public Set<String> getQueryDependencies() {
        return queryDependencies;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    /**
     * @return the serverDependencies
     */
    public Set<String> getServerDependencies() {
        return serverDependencies;
    }

    /**
     * @param aModuleName
     */
    public void putServerDependence(String aModuleName) {
        if (!serverDependencies.contains(aModuleName)) {
            serverDependencies.add(aModuleName);
        }
    }

    public Set<String> getDependenceLikeIdentifiers() {
        return dependenceLikeIdentifiers;
    }

}
