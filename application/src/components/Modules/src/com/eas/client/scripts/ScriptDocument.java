/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.model.application.ApplicationModel;
import com.eas.client.scripts.store.ScriptDocument2Dom;
import com.eas.script.JsDoc;
import com.eas.script.JsDoc.Tag;
import com.eas.script.ScriptUtils;
import java.util.*;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ast.*;
import org.w3c.dom.Document;

/**
 * Implementation service support for script related tasks. It is cached and
 * therefor purged from time to time. Don't store references on it in your
 * client code!
 *
 * @author pk, mg refactoring
 */
public class ScriptDocument {

    private String entityId;
    private String title;
    protected long txtContentLength;
    protected long txtCrc32;
    private ApplicationModel<?, ?, ?, ?> model;
    private String scriptSource;
    private Function constructor;
    private AstRoot ast;
    private FunctionNode firstFunction;
    private Set<String> topLevelNamedFunctionsNames = new HashSet<>();
    private Set<String> depencies = new HashSet<>();
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

    public ScriptDocument(ApplicationModel<?, ?, ?, ?> aModel, String aSource) {
        super();
        model = aModel;
        scriptSource = aSource;
    }

    public ScriptDocument(String aEntityId, ApplicationModel<?, ?, ?, ?> aModel, String aSource) {
        this(aModel, aSource);
        entityId = aEntityId;
    }

    public ApplicationModel<?, ?, ?, ?> getModel() {
        return model;
    }

    public void setModel(ApplicationModel<?, ?, ?, ?> aModel) {
        model = aModel;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String aEntityId) {
        entityId = aEntityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String aValue) {
        title = aValue;
    }

    public long getTxtContentLength() {
        return txtContentLength;
    }

    public void setTxtContentLength(long aValue) {
        txtContentLength = aValue;
    }

    public long getTxtCrc32() {
        return txtCrc32;
    }

    public void setTxtCrc32(long aValue) {
        txtCrc32 = aValue;
    }

    public String getScriptSource() {
        return scriptSource;
    }

    public void setScriptSource(String aScriptSource) {
        scriptSource = aScriptSource;
        ast = null;
    }

    public Set<String> getDepencies() {
        return depencies;
    }

    public Function getFunction() {
        return constructor;
    }

    public void setFunction(Function aFunction) {
        constructor = aFunction;
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

    public Set<String> getTopLevelNamedFunctionsNames() {
        return topLevelNamedFunctionsNames;
    }

    public String filterSource() {
        return filterSource(null, null);
    }

    public String filterSource(String bodyPreCode, String bodyPostCode) {
        String toInsert = "; this[\"" + ScriptUtils.HANDLERS_PROP_NAME + "\"]=" + generateTopLevelNamedFunctionsContainer() + ";";
        if (firstFunction == null) {
            throw new IllegalStateException("Missing first function in " + String.valueOf(entityId) + ". May be bad source?");
        }
        int functionsCaptureInsertAt = firstFunction.getAbsolutePosition() + firstFunction.getLength() - 1;
        if (bodyPreCode == null) {
            return scriptSource.substring(0, functionsCaptureInsertAt) + toInsert + (bodyPostCode != null ? bodyPostCode : "") + scriptSource.substring(functionsCaptureInsertAt, scriptSource.length());
        } else {
            AstNode body = firstFunction.getBody();
            int bodyPreCodeInsertAt = body.getAbsolutePosition() + 1;
            return scriptSource.substring(0, bodyPreCodeInsertAt) + bodyPreCode + scriptSource.substring(bodyPreCodeInsertAt, functionsCaptureInsertAt) + toInsert + (bodyPostCode != null ? bodyPostCode : "") + scriptSource.substring(functionsCaptureInsertAt, scriptSource.length());
        }
    }

    public String generateTopLevelNamedFunctionsContainer() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (String aName : topLevelNamedFunctionsNames) {
            if (sb.length() > 1) {
                sb.append(",");
            }
            sb.append("\"").append(aName).append("\"").append(":").append(aName);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Reads script annotations. Annotations, accompanied with
     *
     * @name annotation are the 'module annotations'. Annotations, followed by
     * any property assignment are the 'property annotations'. Property
     * annotations will be taken into account while accessing through modules.
     * WARNING!!! This method is suited for analyzing of plain scripts, e.g.
     * platypus modules constructors bodies. If scriptSource will be reshaped
     * into form of: function Module1(){ this.member = ...}, than this method
     * should be refactored to take into account inner scope of top-level
     * function.
     */
    public void readScriptAnnotations() {
        assert scriptSource != null : "Javascript source can't be null";
        if (ast == null) {
            moduleAnnotations = new ArrayList<>();
            topLevelNamedFunctionsNames.clear();
            propertyAllowedRoles.clear();
            ast = ScriptUtils.parseJs(scriptSource);
            firstFunction = null;
            ast.visit(new NodeVisitor() {
                @Override
                public boolean visit(AstNode node) {
                    if (node instanceof FunctionNode && node.getParent() instanceof Block && node.getParent().getParent() instanceof FunctionNode && node.getParent().getParent().getParent() == ast.getAstRoot()) {
                        String fName = ((FunctionNode) node).getName();
                        if (fName != null && !fName.isEmpty()) {
                            topLevelNamedFunctionsNames.add(fName);
                        }
                    }
                    if (firstFunction == null && node instanceof FunctionNode && node.getParent() == ast.getAstRoot()) {
                        firstFunction = (FunctionNode) node;
                    }
                    if (node.getJsDoc() != null) {
                        if (node instanceof Assignment) {
                            Assignment as = (Assignment) node;
                            if (as.getLeft() instanceof PropertyGet) {
                                PropertyGet pg = (PropertyGet) as.getLeft();
                                Name n = pg.getProperty();
                                readPropertyRoles(n.getIdentifier(), node.getJsDoc());
                            }
                        }
                        if (node instanceof FunctionNode && node.getParent() == ast.getAstRoot()) {
                            JsDoc jsDoc = new JsDoc(node.getJsDocNode().getValue());
                            jsDoc.parseAnnotations();
                            for (Tag tag : jsDoc.getAnnotations()) {
                                moduleAnnotations.add(tag);
                                if (tag.getName().equals(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                                    for (String role : tag.getParams()) {
                                        moduleAllowedRoles.add(role);
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
            });
        }
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

    public Document toDom() throws Exception {
        return ScriptDocument2Dom.scriptDocument2Dom(this);
    }
}
