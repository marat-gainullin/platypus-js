/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.eas.script.ScriptFunction;
import java.security.Principal;
import java.util.Set;

/**
 *
 * @author pk, mg, bl, vv
 */
public abstract class PlatypusPrincipal implements Principal {

    private final String name;

    public PlatypusPrincipal(String aName) {
        if (aName == null) {
            throw new NullPointerException();
        }
        name = aName;
    }

    @ScriptFunction(jsDoc = "Checks if a user have a specified role")
    public abstract boolean hasRole(String aRole) throws Exception;

    public boolean hasAnyRole(Set<String> aRoles) throws Exception {
        if (aRoles != null && !aRoles.isEmpty()) {
            for (String role : aRoles) {
                if (hasRole(role)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    @ScriptFunction(jsDoc = "Generates a string representation of an object")
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

    @ScriptFunction(jsDoc = "Returns name of the user")
    @Override
    public String getName() {
        return name;
    }
}
