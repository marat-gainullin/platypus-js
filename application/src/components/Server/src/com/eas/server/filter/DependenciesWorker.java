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
public class DependenciesWorker {

    public static final String ERROR_BAD_AST = "bad Ast";
    public static final String ERROR_DEPENDECIES_PARSE_ERROR = "dependencies parse error";
    public static final String MODULES = "Modules";
    public static final String GET = "get";
    public static final String MODULE = "Module";
    public static final String SERVER_MODULE = "ServerModule";
    public static final String FORM = "Form";
    public static final String REPORT = "Report";
    public static final String SERVER_REPORT = "ServerReport";
    private Set<String> dependencies = new HashSet<>();
    private Set<String> queryDependencies = new HashSet<>();
    private Set<String> serverDependencies = new HashSet<>();
    private Set<String> dynamicDependencies = new HashSet<>();
    private AstRoot sourceRoot;
    private String source;
    private AppCache cache;
    private ErrorReporter compilationErrorReporter;

    public DependenciesWorker(String aSource) {
        this(aSource, null);
    }

    public DependenciesWorker(String aSource, AppCache aCache) {
        super();
        source = aSource;
        cache = aCache;
    }

    public String transform() {
        CompilerEnvirons compilerEnv = CompilerEnvirons.ideEnvirons();
        compilerEnv.setRecordingLocalJsDocComments(true);
        compilerEnv.setAllowSharpComments(true);

        compilationErrorReporter = compilerEnv.getErrorReporter();
        Parser p = new Parser(compilerEnv, compilationErrorReporter);
        sourceRoot = p.parse(source, "", 0);
        sourceRoot.visit(new NodeVisitor() {
            public static final String REQUIRE_FUNCTION_NAME = "require";

            @Override
            public boolean visit(final AstNode node) {
                if (node instanceof Name) {
                    attemptToParseDependenciesFromNode(node);
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
                    Logger.getLogger(DependenciesWorker.class.getName()).log(Level.SEVERE, null, ex);
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
