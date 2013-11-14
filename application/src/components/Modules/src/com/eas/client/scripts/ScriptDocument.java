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
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.*;
import org.w3c.dom.Document;

/**
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
    private Function function;
    private AstRoot ast;
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
    }

    public Set<String> getDepencies() {
        return depencies;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function aScript) {
        function = aScript;
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

    public String generateTopLevelNamedFunctionsContainer() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (String aName : topLevelNamedFunctionsNames) {
            if(sb.length() > 1)
                sb.append(",");
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
        ast = ScriptUtils.parseJs(scriptSource);
        ast.visit(new NodeVisitor() {
            @Override
            public boolean visit(AstNode node) {
                if (node.getParent() == ast.getAstRoot() && node instanceof FunctionNode) {
                    String fName = ((FunctionNode) node).getName();
                    if (fName != null && !fName.isEmpty()) {
                        topLevelNamedFunctionsNames.add(fName);
                    }
                }
                if (node.getJsDoc() != null && node instanceof Assignment) {
                    Assignment as = (Assignment) node;
                    if (as.getLeft() instanceof PropertyGet) {
                        PropertyGet pg = (PropertyGet) as.getLeft();
                        Name n = pg.getProperty();
                        readPropertyRoles(n.getIdentifier(), node.getJsDoc());
                    }
                }
                return true;
            }
        });
        readModuleAnnotations();
    }

    private void readModuleAnnotations() {
        assert ast != null;
        moduleAnnotations = new ArrayList<>();
        SortedSet<Comment> comments = ast.getComments();
        if (comments != null) {
            for (Comment comment : comments) {
                if (comment.getCommentType().equals(Token.CommentType.JSDOC)) {
                    JsDoc jsDoc = new JsDoc(comment.getValue());
                    if (jsDoc.containsModuleName()) {
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
            }
        }
    }

    private void readPropertyRoles(String aPropertyName, String aJsDocBody) {
        if (aJsDocBody != null) {
            JsDoc jsDoc = new JsDoc(aJsDocBody);
            if (!jsDoc.containsModuleName()) {
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

    public Document toDom() throws Exception {
        return ScriptDocument2Dom.scriptDocument2Dom(this);
    }
}
