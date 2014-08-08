package com.eas.client.scripts;

import com.eas.client.AppCache;
import com.eas.client.ClientConstants;
import com.eas.client.metadata.ApplicationElement;
import com.eas.script.ScriptUtils;
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
    public static final String MODULES = "Modules";
    public static final String GET = "get";
    public static final String CREATE = "create";    
    public static final String MODEL = "model";
    public static final String LOAD_ENTITY = "loadEntity";
    public static final String MODULE = "Module";
    public static final String SERVER_MODULE = "ServerModule";
    public static final String FORM = "Form";
    public static final String REPORT = "Report";
    public static final String SERVER_REPORT = "ServerReport";
    private final Set<String> localFunctions = new HashSet<>();
    private final Set<String> dependencies = new HashSet<>();
    private final Set<String> queryDependencies = new HashSet<>();
    private final Set<String> serverDependencies = new HashSet<>();
    private final Set<String> dynamicDependencies = new HashSet<>();
    private FunctionNode sourceRoot;
    private String source;
    private AppCache cache;

    public DependenciesWalker(String aSource) {
        this(aSource, null);
    }

    public DependenciesWalker(String aSource, AppCache aCache) {
        super();
        source = aSource;
        cache = aCache;
    }

    public void walk() {
        sourceRoot = ScriptUtils.parseJs(source);
        sourceRoot.accept(new NodeVisitor<LexicalContext>(new LexicalContext()) {

            private final Stack<CallNode> calls = new Stack<>();

            @Override
            public boolean enterCallNode(CallNode callNode) {
                calls.push(callNode);
                return super.enterCallNode(callNode);
            }

            @Override
            public Node leaveCallNode(CallNode callNode) {
                calls.pop();
                return super.leaveCallNode(callNode);
            }

            @Override
            public boolean enterLiteralNode(LiteralNode<?> literalNode) {
                if (literalNode.getType().isString() && !calls.empty()) {
                    String value = literalNode.getString();
                    CallNode lastCall = calls.peek();
                    boolean arrayAtFirstArg = lastCall.getArgs().size() >= 1 && lastCall.getArgs().get(0) instanceof LiteralNode.ArrayLiteralNode;
                    boolean atFirstArg = lastCall.getArgs().size() >= 1 && lastCall.getArgs().get(0) == literalNode;
                    Expression fe = lastCall.getFunction();
                    if (fe instanceof AccessNode && ((AccessNode) fe).getProperty() instanceof IdentNode) {
                        AccessNode lastAccess = (AccessNode) fe;
                        String funcName = ((IdentNode) lastAccess.getProperty()).getName();
                        if (REQUIRE_FUNCTION_NAME.equals(funcName)) {
                            if (arrayAtFirstArg) {
                                LiteralNode.ArrayLiteralNode a = (LiteralNode.ArrayLiteralNode) lastCall.getArgs().get(0);
                                if (Arrays.asList(a.getArray()).contains(literalNode)) {
                                    dynamicDependencies.add(value);
                                }
                            }
                            if (atFirstArg) {
                                dynamicDependencies.add(value);
                            }
                        } else if (/*lastCall.isNew() && */atFirstArg) {
                            switch (funcName) {
                                case FORM:
                                case MODULE:
                                    putDependence(value);
                                    break;
                                case REPORT:
                                case SERVER_REPORT:
                                case SERVER_MODULE:
                                    putServerDependence(value);
                                    break;
                                case GET:
                                case CREATE:
                                    AccessNode baseAccess = (AccessNode) lastAccess.getBase();
                                    if (baseAccess.getProperty() instanceof IdentNode) {
                                        String baseName = ((IdentNode) baseAccess.getProperty()).getName();
                                        if (MODULES.equals(baseName) && (GET.equals(funcName) || CREATE.equals(funcName))) {
                                            putDependence(value);
                                        }
                                    }
                                    break;
                                case LOAD_ENTITY:
                                    if (lastAccess.getBase() instanceof IdentNode) {
                                        String baseName = ((IdentNode) lastAccess.getBase()).getName();
                                        if (MODEL.equals(baseName) && LOAD_ENTITY.equals(funcName)) {
                                            putQueryDependence(value);
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                }
                return super.enterLiteralNode(literalNode);
            }

            @Override
            public boolean enterIdentNode(IdentNode identNode) {
                String name = identNode.getName();
                if (cache != null) {
                    try {                        
                        ApplicationElement appElement = cache.get(name);
                        if (appElement != null) {
                            if (appElement.getType() == ClientConstants.ET_COMPONENT
                                    || appElement.getType() == ClientConstants.ET_FORM
                                    || appElement.getType() == ClientConstants.ET_REPORT) {
                                putDependence(name);
                            } else {
                                Logger.getLogger(DependenciesWalker.class.getName()).log(Level.WARNING, "Possible name duplication (JavaScript indentifier {0} found that is the same with non-module application element).", appElement.getName());
                            }
                        }// ordinary script class
                    } catch (Exception ex) {
                        Logger.getLogger(DependenciesWalker.class.getName()).log(Level.SEVERE, ex.getMessage());
                    }
                }
                return super.enterIdentNode(identNode);
            }

            private int scopeLevel;

            @Override
            public boolean enterFunctionNode(FunctionNode functionNode) {
                scopeLevel++;
                if (scopeLevel == 2 && !functionNode.isAnonymous()) {
                    localFunctions.add(functionNode.getName());
                }
                return super.enterFunctionNode(functionNode);
            }

            @Override
            public Node leaveFunctionNode(FunctionNode functionNode) {
                scopeLevel--;
                return super.leaveFunctionNode(functionNode);
            }

        });
        dependencies.removeAll(localFunctions);
        dependencies.removeAll(dynamicDependencies);
        serverDependencies.removeAll(dynamicDependencies);
    }

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

    /**
     * @return the serverDependencies
     */
    public Set<String> getServerDependencies() {
        return serverDependencies;
    }

    /**
     * @param aEntityId
     */
    public void putServerDependence(String aEntityId) {
        if (!serverDependencies.contains(aEntityId)) {
            serverDependencies.add(aEntityId);
        }
    }
}
