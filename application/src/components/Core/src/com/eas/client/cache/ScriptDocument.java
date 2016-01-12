/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.script.AmdPropertiesAnnotationsMiner;
import com.eas.script.BaseAnnotationsMiner;
import com.eas.script.JsDoc;
import com.eas.script.Scripts;
import java.util.*;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.BinaryNode;
import jdk.nashorn.internal.ir.CallNode;
import jdk.nashorn.internal.ir.Expression;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
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
        FunctionNode ast = Scripts.parseJs(aSource);
        // For AMD modules
        ast.accept(new BaseAnnotationsMiner(source) {

            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
            }

            private void amdNameByToken(CallNode callNode, long aToken) {
                if (prevComments.containsKey(aToken)) {
                    long prevComment = prevComments.get(aToken);
                    String defineCallComment = source.getString(prevComment);
                    JsDoc jsDoc = new JsDoc(defineCallComment);
                    jsDoc.parseAnnotations();
                    commentedDefineCall(callNode, jsDoc);
                }
            }

            @Override
            public boolean enterCallNode(CallNode callNode) {
                Expression func = callNode.getFunction();
                if (func instanceof AccessNode) {
                    AccessNode an = (AccessNode) func;
                    if ("define".equals(an.getProperty())) {
                        amdNameByToken(callNode, an.getBase().getToken());
                    }
                } else if (func instanceof IdentNode) {
                    IdentNode in = (IdentNode) func;
                    if ("define".equals(in.getName())) {
                        amdNameByToken(callNode, in.getToken());
                    }
                }
                return super.enterCallNode(callNode);
            }

            @Override
            public boolean enterBinaryNode(BinaryNode binaryNode) {
                if (scopeLevel == AMD_CONSTRUCTORS_SCOPE_LEVEL && binaryNode.isAssignment() && !binaryNode.isSelfModifying()) {
                    if (binaryNode.getAssignmentDest() instanceof AccessNode) {
                        AccessNode left = (AccessNode) binaryNode.getAssignmentDest();
                        if (left.getBase() instanceof IdentNode) {
                            long ft = left.getBase().getToken();
                            if (prevComments.containsKey(ft)) {
                                long prevComment = prevComments.get(ft);
                                commentedProperty(left.getProperty(), source.getString(prevComment));
                            }
                            property(left.getProperty(), binaryNode.getAssignmentSource());
                        }
                    }
                }
                return super.enterBinaryNode(binaryNode);
            }

            protected void commentedDefineCall(CallNode aCallNode, JsDoc aJsDoc) {
                aJsDoc.getAnnotations().stream().forEach((JsDoc.Tag tag) -> {
                    if (tag.getName().equalsIgnoreCase(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                        tag.getParams().stream().forEach((role) -> {
                            moduleAllowedRoles.add(role);
                        });
                    }
                });
            }

            protected void commentedProperty(String aPropertyName, String aComment) {
                readPropertyRoles(aPropertyName, aComment);
            }

            protected void property(String aPropertyName, Expression aValue) {
                if (!aPropertyName.contains(".")) {
                    functionProperties.add(aPropertyName);
                }
            }
        });
        // For global scope modules
        Set<String> thisAliases = new HashSet<>();//Scripts.getThisAliases(PlatypusFilesSupport.extractModuleConstructor(ast, aName));
        ast.accept(new BaseAnnotationsMiner(source) {

            @Override
            public boolean enterBinaryNode(BinaryNode binaryNode) {
                if (scopeLevel == GLOBAL_CONSTRUCTORS_SCOPE_LEVEL && binaryNode.isAssignment() && !binaryNode.isSelfModifying()) {
                    if (binaryNode.getAssignmentDest() instanceof AccessNode) {
                        AccessNode left = (AccessNode) binaryNode.getAssignmentDest();
                        if (left.getBase() instanceof IdentNode && thisAliases.contains(((IdentNode) left.getBase()).getName())) {
                            long ft = left.getBase().getToken();
                            if (prevComments.containsKey(ft)) {
                                long prevComment = prevComments.get(ft);
                                commentedProperty(left.getProperty(), source.getString(prevComment));
                            }
                            property(left.getProperty(), binaryNode.getAssignmentSource());
                        }
                    }
                }
                return super.enterBinaryNode(binaryNode);
            }

            @Override
            public boolean enterFunctionNode(FunctionNode fn) {
                if (scopeLevel == TOP_SCOPE_LEVEL && fn != ast && !fn.isAnonymous()) {
                    if (cx.result == null) {
                        cx.result = fn;
                    }
                    cx.functions++;
                }
                return super.enterFunctionNode(fn);
            }

            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
                if (scopeLevel == GLOBAL_CONSTRUCTORS_SCOPE_LEVEL) {
                    JsDoc jsDoc = new JsDoc(aComment);
                    jsDoc.parseAnnotations();
                    if(jsDoc.containsTag(JsDoc.Tag.CONSTRUCTOR_TAG)){
                    }
                    jsDoc.getAnnotations().stream().forEach((JsDoc.Tag tag) -> {
                        moduleAnnotations.add(tag);
                        if (tag.getName().equalsIgnoreCase(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                            tag.getParams().stream().forEach((role) -> {
                                moduleAllowedRoles.add(role);
                            });
                        }
                    });
                }
            }

            protected void commentedProperty(String aPropertyName, String aComment) {
                readPropertyRoles(aPropertyName, aComment);
            }

            protected void property(String aPropertyName, Expression aValue) {
                if (!aPropertyName.contains(".")) {
                    functionProperties.add(aPropertyName);
                }
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
