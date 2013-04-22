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
import org.mozilla.javascript.Script;
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
    private Script script;
    private AstRoot ast;
    private Set<String> depencies = new HashSet<>();
    private List<Tag> moduleAnnotations;
    /**
     * User roles that have access to all module's functions, if empty all users allowed
     */
    private Set<String> moduleAllowedRoles = new HashSet<>();
    /**
     * Roles have that access to concrete functions, if empty all users allowed 
     */
    private Map<String, Set<String>> functionAllowedRoles = new HashMap<>();

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

    public Script getScript() {
        return script;
    }

    public void setScript(Script aScript) {
        script = aScript;
    }

    public Set<String> getModuleAllowedRoles() {
        return moduleAllowedRoles;
    }

    public void setModuleAllowedRoles(Set<String> aModuleAllowedRoles) {
        moduleAllowedRoles = aModuleAllowedRoles;
    }

    public Map<String, Set<String>> getFunctionAllowedRoles() {
        return functionAllowedRoles;
    }

    public void setFunctionAllowedRoles(Map<String, Set<String>> aFunctionAllowedRoles) {
        functionAllowedRoles = aFunctionAllowedRoles;
    }
    
    public List<Tag> getModuleAnnotations() {
        return moduleAnnotations != null ? Collections.unmodifiableList(moduleAnnotations) : null;
    }

    public void readScriptAnnotations() {
        ast = ScriptUtils.parseJs(scriptSource);
        readModuleAnnotations();
        ast.visit(new NodeVisitor() {

            @Override
            public boolean visit(AstNode node) {
                if (node.getParent() == null) { //visit only the top level
                    return true;
                }
                if (node instanceof FunctionNode) {
                    readFunctionRoles(((FunctionNode) node).getName(), node.getJsDoc());
                }
                return false;
            }
        });
    }

    private void readModuleAnnotations() {
        assert ast != null;
        moduleAnnotations = new ArrayList<>();
        SortedSet<Comment> comments = ast.getComments();
        if (comments != null) {
            for (Comment comment : comments) {
                if (comment.getCommentType().equals(Token.CommentType.JSDOC)) {
                    JsDoc jsDoc = new JsDoc(comment.getValue());
                    if (jsDoc.isModuleLevel()) {
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

    private void readFunctionRoles(String aFunctionName, String aJsDocBody) {
        if (aJsDocBody != null) {
            JsDoc jsDoc = new JsDoc(aJsDocBody);
            if (!jsDoc.isModuleLevel()) {
                jsDoc.parseAnnotations();
                for (Tag tag : jsDoc.getAnnotations()) {
                    if (tag.getName().equals(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                        Set<String> roles = functionAllowedRoles.get(tag.getName());
                        if (roles == null) {
                            roles = new HashSet<>();
                        }
                        for (String role : tag.getParams()) {
                            roles.add(role);
                        }
                        functionAllowedRoles.put(aFunctionName, roles);
                    }
                }
            }
        }
    }
    
    public Document toDom() throws Exception
    {
        return ScriptDocument2Dom.scriptDocument2Dom(this);
    }
}
