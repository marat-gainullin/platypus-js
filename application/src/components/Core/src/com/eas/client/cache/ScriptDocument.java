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

        public ModuleDocument() {
            super();
        }

        public Set<String> getAllowedRoles() {
            return Collections.unmodifiableSet(moduleAllowedRoles);
        }

        public Map<String, Set<String>> getPropertyAllowedRoles() {
            return Collections.unmodifiableMap(propertyAllowedRoles);
        }

        public List<JsDoc.Tag> geеAnnotations() {
            return Collections.unmodifiableList(moduleAnnotations);
        }

        public Map<String, Set<JsDoc.Tag>> getPropertyAnnotations() {
            return Collections.unmodifiableMap(propertyAnnotations);
        }

        public boolean hasAnnotation(String anAnnotation) {
            return moduleAnnotations.stream().anyMatch((JsDoc.Tag aTag) -> {
                return aTag.getName().equalsIgnoreCase(anAnnotation);
            });
        }

        public List<JsDoc.Tag> getAnnotations() {
            return Collections.unmodifiableList(moduleAnnotations);
        }

        public JsDoc.Tag getAnnotation(String anAnnotation) {
            Optional<JsDoc.Tag> found = moduleAnnotations.stream().filter((JsDoc.Tag aTag) -> {
                return aTag.getName().equalsIgnoreCase(anAnnotation);
            }).findAny();
            return found.isPresent() ? found.get() : null;
        }

        public Set<String> getFunctionProperties() {
            return Collections.unmodifiableSet(functionProperties);
        }

        void parseAnnotations(String commentText) {
            JsDoc jsDoc = new JsDoc(commentText);
            jsDoc.parseAnnotations();
            jsDoc.getAnnotations().stream().forEach((JsDoc.Tag tag) -> {
                moduleAnnotations.add(tag);
                if (tag.getName().equalsIgnoreCase(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                    tag.getParams().stream().forEach((role) -> {
                        moduleAllowedRoles.add(role);
                    });
                }
            });
        }

        private void readPropertyRoles(String aPropertyName, String aJsDocBody) {
            if (aJsDocBody != null) {
                JsDoc jsDoc = new JsDoc(aJsDocBody);
                jsDoc.parseAnnotations();
                jsDoc.getAnnotations().stream().forEach((JsDoc.Tag tag) -> {
                    Set<JsDoc.Tag> tags = propertyAnnotations.get(aPropertyName);
                    if (tags == null) {
                        tags = new HashSet<>();
                        propertyAnnotations.put(aPropertyName, tags);
                    }
                    tags.add(tag);
                    if (tag.getName().equalsIgnoreCase(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                        Set<String> roles = propertyAllowedRoles.get(aPropertyName);
                        if (roles == null) {
                            roles = new HashSet<>();
                        }
                        for (String role : tag.getParams()) {
                            roles.add(role);
                        }
                        propertyAllowedRoles.put(aPropertyName, roles);
                    }
                });
            }
        }
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

    public Map<String, ModuleDocument> getModules() {
        return Collections.unmodifiableMap(modules);
    }

    /**
     * Reads script annotations. Annotations, accompanied with
     *
     * @name annotation are the 'module annotations'. Annotations, followed by
     * any property assignment are the 'property annotations'. Property
     * annotations will be taken into account while accessing through modules.
     * @param aSource
     */
    private void readScriptModules(String aSource, String aDefaultModuleName) {
        assert aSource != null : "JavaScript source can't be null";
        Source source = Source.sourceFor(aDefaultModuleName, aSource);
        ParsedJs parsed = Scripts.parseJs(aSource);
        if (parsed != null) {
            LexicalContext context = new LexicalContext();
            FunctionNode ast = parsed.getAst();
            Map<Long, Long> prevComments = parsed.getPrevComments();
            ast.accept(new NodeVisitor(context) {
                protected final int GLOBAL_CONSTRUCTORS_BODY_SCOPE_LEVEL = 2;
                protected final int AMD_CONSTRUCTORS_BODY_SCOPE_LEVEL = 3;

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

                private void defineCall(CallNode callNode, long aCommentableToken) {
                    amdDefineCall = callNode;

                    String moduleName;
                    if (callNode.getArgs().size() == 3 && callNode.getArgs().get(0) instanceof LiteralNode
                            && ((LiteralNode) callNode.getArgs().get(0)).getType().isString()) {
                        moduleName = ((LiteralNode) callNode.getArgs().get(0)).getString();
                    } else {
                        moduleName = null;
                    }
                    mdModule = new ModuleDocument();
                    Set<String> allowedRoles = new HashSet<>();
                    if (prevComments.containsKey(aCommentableToken)) {
                        long prevComment = prevComments.get(aCommentableToken);
                        String defineCallComment = source.getString(prevComment);
                        mdModule.parseAnnotations(defineCallComment);
                    }
                    mdModule.moduleAllowedRoles.addAll(allowedRoles);
                    if (moduleName == null || moduleName.isEmpty()) {
                        moduleName = aDefaultModuleName;
                    }
                    if (modules.containsKey(moduleName)) {
                        Logger.getLogger(ScriptDocument.class.getName()).log(Level.WARNING, "Module with name \"{0}\" ia already defined in script {1}.", new Object[]{moduleName != null ? moduleName : "null", aDefaultModuleName});
                    }
                    modules.put(moduleName, mdModule);
                }

                @Override
                public Node leaveCallNode(CallNode callNode) {
                    if (callNode == amdDefineCall) {
                        amdDefineCall = null;
                        mdModule = null;
                    }
                    return super.leaveCallNode(callNode);
                }

                @Override
                public boolean enterVarNode(VarNode varNode) {
                    if (mdConstructor != null && context.getCurrentFunction() == mdConstructor) {
                        if (varNode.getAssignmentSource() instanceof IdentNode) {
                            IdentNode in = (IdentNode) varNode.getAssignmentSource();
                            if (Scripts.THIS_KEYWORD.equals(in.getName())) {
                                mdThisAliases.add(varNode.getAssignmentDest().getName());
                            }
                        }
                    }
                    return super.enterVarNode(varNode);
                }

                @Override
                public boolean enterBinaryNode(BinaryNode binaryNode) {
                    // For global scope modules
                    if (scopeLevel == GLOBAL_CONSTRUCTORS_BODY_SCOPE_LEVEL && binaryNode.isAssignment() && !binaryNode.isSelfModifying()) {
                        if (binaryNode.getAssignmentDest() instanceof AccessNode) {
                            AccessNode left = (AccessNode) binaryNode.getAssignmentDest();
                            if (left.getBase() instanceof IdentNode && mdThisAliases.contains(((IdentNode) left.getBase()).getName())) {
                                long ft = left.getBase().getToken();
                                String comment = null;
                                if (prevComments.containsKey(ft)) {
                                    long prevComment = prevComments.get(ft);
                                    comment = source.getString(prevComment);
                                }
                                processProperty(left.getProperty(), binaryNode.getAssignmentSource(), comment);
                            }
                        }
                    }
                    // For AMD modules
                    if (scopeLevel == AMD_CONSTRUCTORS_BODY_SCOPE_LEVEL && binaryNode.isAssignment() && !binaryNode.isSelfModifying()) {
                        if (binaryNode.getAssignmentDest() instanceof AccessNode) {
                            AccessNode left = (AccessNode) binaryNode.getAssignmentDest();
                            if (left.getBase() instanceof IdentNode) {
                                long ft = left.getBase().getToken();
                                String comment = null;
                                if (prevComments.containsKey(ft)) {
                                    long prevComment = prevComments.get(ft);
                                    comment = source.getString(prevComment);
                                }
                                processProperty(left.getProperty(), binaryNode.getAssignmentSource(), comment);
                            }
                        }
                    }
                    return super.enterBinaryNode(binaryNode);
                }

                protected ModuleDocument mdModule;// current module form nearest scope
                protected FunctionNode mdConstructor;// current AMD/GMD constructor
                protected Set<String> mdThisAliases = new HashSet<String>() {
                    {
                        add(Scripts.THIS_KEYWORD);
                    }
                };

                @Override
                public boolean enterFunctionNode(FunctionNode functionNode) {
                    scopeLevel++;
                    if (!functionNode.isAnonymous() && scopeLevel == GLOBAL_CONSTRUCTORS_BODY_SCOPE_LEVEL) {
                        mdModule = new ModuleDocument();
                        mdConstructor = functionNode;
                        long ft = functionNode.getFirstToken();
                        if (prevComments.containsKey(ft)) {
                            long prevComment = prevComments.get(ft);
                            String commentText = source.getString(prevComment);
                            mdModule.parseAnnotations(commentText);
                        }
                        modules.put(functionNode.getName(), mdModule);
                    }
                    return super.enterFunctionNode(functionNode);
                }

                @Override
                public Node leaveFunctionNode(FunctionNode functionNode) {
                    if (functionNode == mdConstructor) {
                        mdConstructor = null;
                        mdModule = null;
                        mdThisAliases = new HashSet<String>() {
                            {
                                add(Scripts.THIS_KEYWORD);
                            }
                        };
                    }
                    scopeLevel--;
                    return super.leaveFunctionNode(functionNode);
                }

                protected void processProperty(String aPropertyName, Expression aValue, String aComment) {
                    if (mdModule != null) {
                        if (!aPropertyName.contains(".")) {
                            mdModule.functionProperties.add(aPropertyName);
                            if (aComment != null) {
                                mdModule.readPropertyRoles(aPropertyName, aComment);
                            }
                        }
                    }
                }
            });
        }
    }
}
