/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.script.JsDoc;
import com.eas.script.ParsedJs;
import com.eas.script.Scripts;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.BinaryNode;
import jdk.nashorn.internal.ir.CallNode;
import jdk.nashorn.internal.ir.Expression;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.LiteralNode;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.VarNode;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import jdk.nashorn.internal.runtime.Source;

/**
 * Implementation service support for script related tasks.
 *
 * @author pk, mg refactoring
 */
public class ScriptDocument {

    public static class ModuleDocument {

        protected final List<JsDoc.Tag> moduleAnnotations = new ArrayList<>();
        /**
         * User roles that have access to all module's functions, if empty all
         * users allowed
         */
        protected final Set<String> moduleAllowedRoles = new HashSet<>();
        /**
         * Roles that have access to specific properties, if empty all users are
         * allowed
         */
        protected final Map<String, Set<String>> propertyAllowedRoles = new HashMap<>();

        protected final Map<String, Set<JsDoc.Tag>> propertyAnnotations = new HashMap<>();
        /**
         * Functions that may be accessed over network via RPC
         */
        protected final Set<String> functionProperties = new HashSet<>();
    }

    private final Map<String, ModuleDocument> modules = new HashMap<>();

    protected ScriptDocument() {
        super();
    }

    public static ScriptDocument parse(String aSource, String aName) {
        ScriptDocument doc = new ScriptDocument();
        doc.readScriptModules(aSource, aName);
        return doc;
    }

    public Set<String> getModuleNames() {
        return Collections.unmodifiableSet(modules.keySet());
    }

    public Set<String> getModuleAllowedRoles(String aModuleName) {
        return modules.containsKey(aModuleName) ? modules.get(aModuleName).moduleAllowedRoles : Collections.emptySet();
    }

    public Map<String, Set<String>> getModulePropertyAllowedRoles(String aModuleName) {
        return modules.containsKey(aModuleName) ? modules.get(aModuleName).propertyAllowedRoles : Collections.emptyMap();
    }

    public List<JsDoc.Tag> getModuleAnnotations(String aModuleName) {
        return modules.containsKey(aModuleName) ? Collections.unmodifiableList(modules.get(aModuleName).moduleAnnotations) : null;
    }

    public Map<String, Set<JsDoc.Tag>> getModulePropertyAnnotations(String aModuleName) {
        return modules.containsKey(aModuleName) ? Collections.unmodifiableMap(modules.get(aModuleName).propertyAnnotations) : Collections.emptyMap();
    }

    public boolean hasModuleAnnotation(String aModuleName, String anAnnotation) {
        return modules.containsKey(aModuleName) ? modules.get(aModuleName).moduleAnnotations.stream().anyMatch((JsDoc.Tag aTag) -> {
            return aTag.getName().equalsIgnoreCase(anAnnotation);
        }) : false;
    }

    public JsDoc.Tag getModuleAnnotation(String aModuleName, String anAnnotation) {
        return modules.containsKey(aModuleName) ? modules.get(aModuleName).moduleAnnotations.stream().filter((JsDoc.Tag aTag) -> {
            return aTag.getName().equalsIgnoreCase(anAnnotation);
        }).findAny().get() : null;
    }

    public Set<String> getModuleFunctionProperties(String aModuleName) {
        return modules.containsKey(aModuleName) ? Collections.unmodifiableSet(modules.get(aModuleName).functionProperties) : Collections.emptySet();
    }

    /**
     * Reads script annotations. Annotations, accompanied with
     *
     * @name annotation are the 'module annotations'. Annotations, followed by
     * any property assignment are the 'property annotations'. Property
     * annotations will be taken into account while accessing through modules.
     * @param aSource
     */
    private void readScriptModules(String aSource, String aName) {
        assert aSource != null : "JavaScript source can't be null";
        Source source = Source.sourceFor(aName, aSource);
        ParsedJs parsed = Scripts.parseJs(aSource);
        LexicalContext context = new LexicalContext();
        FunctionNode ast = parsed.getAst();
        Map<Long, Long> prevComments = parsed.getPrevComments();
        ast.accept(new NodeVisitor(context) {
            protected final int GLOBAL_CONSTRUCTORS_SCOPE_LEVEL = 2;
            protected final int AMD_CONSTRUCTORS_SCOPE_LEVEL = 3;

            private int scopeLevel;

            @Override
            public boolean enterCallNode(CallNode callNode) {
                Expression func = callNode.getFunction();
                if (func instanceof AccessNode) {
                    AccessNode an = (AccessNode) func;
                    if (DEFINE_FUNC_NAME.equals(an.getProperty())) {
                        defineCall(callNode, an.getBase().getToken());
                    }
                } else if (func instanceof IdentNode) {
                    IdentNode in = (IdentNode) func;
                    if (DEFINE_FUNC_NAME.equals(in.getName())) {
                        defineCall(callNode, in.getToken());
                    }
                }
                return super.enterCallNode(callNode);
            }
            private static final String DEFINE_FUNC_NAME = "define";

            protected CallNode amdDefineCall;
            protected ModuleDocument amdModule;

            private void defineCall(CallNode callNode, long aCommentableToken) {
                amdDefineCall = callNode;

                String moduleName;
                if (callNode.getArgs().size() == 3 && callNode.getArgs().get(0) instanceof LiteralNode
                        && ((LiteralNode) callNode.getArgs().get(0)).getType().isString()) {
                    moduleName = ((LiteralNode) callNode.getArgs().get(0)).getString();
                } else {
                    moduleName = null;
                }
                Set<String> allowedRoles = new HashSet<>();
                if (prevComments.containsKey(aCommentableToken)) {
                    long prevComment = prevComments.get(aCommentableToken);
                    String defineCallComment = source.getString(prevComment);
                    JsDoc jsDoc = new JsDoc(defineCallComment);
                    jsDoc.parseAnnotations();
                    if (moduleName == null || moduleName.isEmpty()) {
                        JsDoc.Tag moduleTag = jsDoc.getTag(JsDoc.Tag.MODULE_TAG);
                        if (moduleTag != null && !moduleTag.getParams().isEmpty()) {
                            moduleName = moduleTag.getParams().get(0);
                        }
                    }
                    jsDoc.getAnnotations().stream().forEach((JsDoc.Tag tag) -> {
                        if (tag.getName().equalsIgnoreCase(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                            tag.getParams().stream().forEach((role) -> {
                                allowedRoles.add(role);
                            });
                        }
                    });
                }
                amdModule = new ModuleDocument();
                amdModule.moduleAllowedRoles.addAll(allowedRoles);
                if (modules.containsKey(moduleName)) {
                    Logger.getLogger(ScriptDocument.class.getName()).log(Level.WARNING, "Module with name \"{0}\" ia already defined in script {1}.", new Object[]{moduleName != null ? moduleName : "null", aName});
                }
                modules.put(moduleName, amdModule);
            }

            @Override
            public Node leaveCallNode(CallNode callNode) {
                if (callNode == amdDefineCall) {
                    amdDefineCall = null;
                    amdModule = null;
                }
                return super.leaveCallNode(callNode);
            }

            @Override
            public boolean enterVarNode(VarNode varNode) {
                if (gmdConstructor != null && context.getCurrentFunction() == gmdConstructor) {
                    if (varNode.getAssignmentSource() instanceof IdentNode) {
                        IdentNode in = (IdentNode) varNode.getAssignmentSource();
                        if (Scripts.THIS_KEYWORD.equals(in.getName())) {
                            gmdThisAliases.add(varNode.getAssignmentDest().getName());
                        }
                    }
                }
                return super.enterVarNode(varNode);
            }

            @Override
            public boolean enterBinaryNode(BinaryNode binaryNode) {
                // For global scope modules
                if (scopeLevel == GLOBAL_CONSTRUCTORS_SCOPE_LEVEL && binaryNode.isAssignment() && !binaryNode.isSelfModifying()) {
                    if (binaryNode.getAssignmentDest() instanceof AccessNode) {
                        AccessNode left = (AccessNode) binaryNode.getAssignmentDest();
                        if (left.getBase() instanceof IdentNode && gmdThisAliases.contains(((IdentNode) left.getBase()).getName())) {
                            long ft = left.getBase().getToken();
                            if (prevComments.containsKey(ft)) {
                                long prevComment = prevComments.get(ft);
                                commentedProperty(left.getProperty(), source.getString(prevComment));
                            }
                            processProperty(left.getProperty(), binaryNode.getAssignmentSource());
                        }
                    }
                }
                // For AMD modules
                if (scopeLevel == AMD_CONSTRUCTORS_SCOPE_LEVEL && binaryNode.isAssignment() && !binaryNode.isSelfModifying()) {
                    if (binaryNode.getAssignmentDest() instanceof AccessNode) {
                        AccessNode left = (AccessNode) binaryNode.getAssignmentDest();
                        if (left.getBase() instanceof IdentNode) {
                            long ft = left.getBase().getToken();
                            if (prevComments.containsKey(ft)) {
                                long prevComment = prevComments.get(ft);
                                commentedProperty(left.getProperty(), source.getString(prevComment));
                            }
                            processProperty(left.getProperty(), binaryNode.getAssignmentSource());
                        }
                    }
                }
                return super.enterBinaryNode(binaryNode);
            }

            protected FunctionNode gmdConstructor;// current GMS constructor
            protected Set<String> gmdThisAliases = new HashSet<String>() {
                {
                    add(Scripts.THIS_KEYWORD);
                }
            };

            @Override
            public boolean enterFunctionNode(FunctionNode functionNode) {
                scopeLevel++;
                if (!functionNode.isAnonymous() && scopeLevel == GLOBAL_CONSTRUCTORS_SCOPE_LEVEL - 1) {
                    ModuleDocument module = new ModuleDocument();
                    gmdConstructor = functionNode;
                    modules.put(gmdConstructor.getName(), module);
                    long ft = functionNode.getFirstToken();
                    if (prevComments.containsKey(ft)) {
                        long prevComment = prevComments.get(ft);
                        String commentText = source.getString(prevComment);
                        JsDoc jsDoc = new JsDoc(commentText);
                        jsDoc.parseAnnotations();
                        jsDoc.getAnnotations().stream().forEach((JsDoc.Tag tag) -> {
                            module.moduleAnnotations.add(tag);
                            if (tag.getName().equalsIgnoreCase(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                                tag.getParams().stream().forEach((role) -> {
                                    module.moduleAllowedRoles.add(role);
                                });
                            }
                        });
                    }
                }
                return super.enterFunctionNode(functionNode);
            }

            @Override
            public Node leaveFunctionNode(FunctionNode functionNode) {
                if (functionNode == gmdConstructor) {
                    gmdConstructor = null;
                    gmdThisAliases = new HashSet<String>() {
                        {
                            add(Scripts.THIS_KEYWORD);
                        }
                    };
                }
                scopeLevel--;
                return super.leaveFunctionNode(functionNode);
            }

            protected void commentedProperty(String aPropertyName, String aComment) {
                if (gmdConstructor != null) {
                    ModuleDocument module = modules.get(gmdConstructor.getName());
                    readPropertyRoles(module, aPropertyName, aComment);
                }
            }

            protected void processProperty(String aPropertyName, Expression aValue) {
                if (gmdConstructor != null) {
                    if (!aPropertyName.contains(".")) {
                        ModuleDocument module = modules.get(gmdConstructor.getName());
                        module.functionProperties.add(aPropertyName);
                    }
                }
            }
        });
    }

    private void readPropertyRoles(ModuleDocument aModule, String aPropertyName, String aJsDocBody) {
        if (aJsDocBody != null) {
            JsDoc jsDoc = new JsDoc(aJsDocBody);
            jsDoc.parseAnnotations();
            jsDoc.getAnnotations().stream().forEach((JsDoc.Tag tag) -> {
                Set<JsDoc.Tag> tags = aModule.propertyAnnotations.get(aPropertyName);
                if (tags == null) {
                    tags = new HashSet<>();
                    aModule.propertyAnnotations.put(aPropertyName, tags);
                }
                tags.add(tag);
                if (tag.getName().equalsIgnoreCase(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                    Set<String> roles = aModule.propertyAllowedRoles.get(aPropertyName);
                    if (roles == null) {
                        roles = new HashSet<>();
                    }
                    for (String role : tag.getParams()) {
                        roles.add(role);
                    }
                    aModule.propertyAllowedRoles.put(aPropertyName, roles);
                }
            });
        }
    }
}
