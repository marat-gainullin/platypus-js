package com.eas.client.scripts;

import com.eas.concurrent.CallableConsumer;
import com.eas.script.ParsedJs;
import com.eas.script.Scripts;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.CallNode;
import jdk.nashorn.internal.ir.Expression;
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
    private final Set<String> dependencies = new HashSet<>();
    private final Set<String> queryDependencies = new HashSet<>();
    private final Set<String> serverDependencies = new HashSet<>();
    private ParsedJs parsedSource;
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
        parsedSource = Scripts.parseJs(source);
        SymbolsResolver outerDefinedFinder = new SymbolsResolver();
        parsedSource.getAst().accept(outerDefinedFinder);
        parsedSource.getAst().accept(new NodeVisitor<LexicalContext>(new LexicalContext()) {

            private final Deque<CallNode> calls = new LinkedList<>();

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
                if (literalNode.getType().isString() && !calls.isEmpty()) {
                    String value = literalNode.getString();
                    CallNode lastCall = calls.peek();
                    //boolean arrayAtFirstArg = lastCall.getArgs().size() >= 1 && lastCall.getArgs().get(0) instanceof LiteralNode.ArrayLiteralNode;
                    boolean atFirstArg = lastCall.getArgs().size() >= 1 && lastCall.getArgs().get(0) == literalNode;
                    Expression fe = lastCall.getFunction();
                    if (fe instanceof AccessNode) {
                        AccessNode lastAccess = (AccessNode) fe;
                        String funcName = lastAccess.getProperty();
                        if (/*lastCall.isNew() && */atFirstArg) {
                            switch (funcName) {
                                case RPC_PROXY:
                                case SERVER_MODULE:
                                    putServerDependence(value);
                                    break;
                                case LOAD_ENTITY:
                                    if (lastAccess.getBase() instanceof IdentNode) {
                                        //String baseName = ((IdentNode) lastAccess.getBase()).getName();
                                        if (LOAD_ENTITY.equals(funcName)) {
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
        });
        dependencies.removeAll(Arrays.asList(new String[]{REQUIRE_FUNCTION_NAME, DEFINE_FUNCTION_NAME}));
        outerDefinedFinder.getGlobalSymbols().forEach((String aIfGlobalDependency) -> {
            try {
                if (!REQUIRE_FUNCTION_NAME.equals(aIfGlobalDependency)
                        && !DEFINE_FUNCTION_NAME.equals(aIfGlobalDependency)
                        && validator != null && validator.call(aIfGlobalDependency)) {
                    dependencies.add(aIfGlobalDependency);
                }
            } catch (Exception ex) {
                Logger.getLogger(DependenciesWalker.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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
}
