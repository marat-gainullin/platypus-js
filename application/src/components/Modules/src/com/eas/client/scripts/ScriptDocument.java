/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.script.AnnotationsMiner;
import com.eas.script.JsDoc;
import com.eas.script.JsDoc.Tag;
import com.eas.script.ScriptUtils;
import java.util.*;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.runtime.Source;

/**
 * Implementation service support for script related tasks. It is cached and
 * therefor purged from time to time. Don't store references on it in your
 * client code!
 *
 * @author pk, mg refactoring
 */
public class ScriptDocument {

    private List<Tag> moduleAnnotations;
    /**
     * User roles that have access to all module's functions, if empty all users
     * allowed
     */
    private Set<String> moduleAllowedRoles = new HashSet<>();
    /**
     * Roles that have access to specific properties, if empty all users are
     * allowed
     */
    private Map<String, Set<String>> propertyAllowedRoles = new HashMap<>();

    public ScriptDocument(String aSource) {
        super();
        readScriptAnnotations(aSource);
    }
    
    public Set<String> getModuleAllowedRoles() {
        return moduleAllowedRoles;
    }

    public void setModuleAllowedRoles(Set<String> aModuleAllowedRoles) {
        moduleAllowedRoles = aModuleAllowedRoles;
    }

    public Map<String, Set<String>> getPropertyAllowedRoles() {
        return propertyAllowedRoles;
    }

    public void setPropertyAllowedRoles(Map<String, Set<String>> aAllowedRoles) {
        propertyAllowedRoles = aAllowedRoles;
    }

    public List<Tag> getModuleAnnotations() {
        return moduleAnnotations != null ? Collections.unmodifiableList(moduleAnnotations) : null;
    }

    public boolean hasModuleAnnotation(String anAnnotation) {
        return moduleAnnotations != null && moduleAnnotations.stream().anyMatch((Tag aTag) -> {
            return aTag.getName().equals(anAnnotation);
        });
    }

    /**
     * Reads script annotations. Annotations, accompanied with
     *
     * @param aSource
     * @name annotation are the 'module annotations'. Annotations, followed by
     * any property assignment are the 'property annotations'. Property
     * annotations will be taken into account while accessing through modules.
     */
    private void readScriptAnnotations(String aSource) {
        assert aSource != null : "JavaScript source can't be null";
        moduleAnnotations = new ArrayList<>();
        propertyAllowedRoles.clear();
        Source source = new Source("", aSource);
        FunctionNode ast = ScriptUtils.parseJs(source);
        ast.accept(new AnnotationsMiner(source) {

            @Override
            protected void commentedFunction(FunctionNode aFunction, String aComment) {
                if (scopeLevel == 2) {
                    JsDoc jsDoc = new JsDoc(aComment);
                    jsDoc.parseAnnotations();
                    jsDoc.getAnnotations().stream().forEach((Tag tag) -> {
                        moduleAnnotations.add(tag);
                        if (tag.getName().equals(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                            tag.getParams().stream().forEach((role) -> {
                                moduleAllowedRoles.add(role);
                            });
                        }
                    });
                }
            }

            @Override
            protected void commentedProperty(IdentNode aProperty, String aComment) {
                readPropertyRoles(aProperty.getPropertyName(), aComment);
            }

        });
    }

    private void readPropertyRoles(String aPropertyName, String aJsDocBody) {
        if (aJsDocBody != null) {
            JsDoc jsDoc = new JsDoc(aJsDocBody);
            jsDoc.parseAnnotations();
            for (Tag tag : jsDoc.getAnnotations()) {
                if (tag.getName().equals(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                    Set<String> roles = propertyAllowedRoles.get(aPropertyName);
                    if (roles == null) {
                        roles = new HashSet<>();
                    }
                    for (String role : tag.getParams()) {
                        roles.add(role);
                    }
                    propertyAllowedRoles.put(aPropertyName, roles);
                }
            }
        }
    }
}
