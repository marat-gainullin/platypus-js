/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;
import java.security.Principal;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author pk, mg, bl, vv
 */
public abstract class PlatypusPrincipal implements Principal, HasPublished {

    protected Object published;
    
    private final String name;
    
    public PlatypusPrincipal(String aName) {
        super();
        name = aName;
    }

    private static final String NAME_JS_DOC = "/**\n"
            + "* The username..\n"
            + "*/";
    
    @ScriptFunction(jsDoc = NAME_JS_DOC)
    @Override
    public String getName() {
        return name;
    }
    
    private static final String HAS_ROLE_JS_DOC = ""
            + "/**\n"
            + "* Checks if a user have a specified role.\n"
            + "* @param role a role's name to test.\n"
            + "* @param onSuccess A success callback. Optional."
            + "* @param onFailure A failure callback. Optional."
            + "* @return <code>true</code> if the user has the provided role\n"
            + "*/";
    
    @ScriptFunction(jsDoc = HAS_ROLE_JS_DOC)
    public abstract boolean hasRole(String aRole, Consumer<Boolean> onSuccess, Consumer<Exception> onFailure) throws Exception;

    public boolean hasAnyRole(Set<String> aRoles) throws Exception {
        if (aRoles != null && !aRoles.isEmpty()) {
            for (String role : aRoles) {
                if (hasRole(role, null, null)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return super.toString() + "{username: \"" + name + "\"}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlatypusPrincipal)) {
            return false;
        }
        PlatypusPrincipal that = (PlatypusPrincipal) o;
        if (this.getName().equals(that.getName())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public void setPublished(Object aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

}
