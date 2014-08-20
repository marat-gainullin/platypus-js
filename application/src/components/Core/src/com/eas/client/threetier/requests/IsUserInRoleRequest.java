/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author mg
 */
public class IsUserInRoleRequest extends Request {

    private String roleName;

    public IsUserInRoleRequest() {
        super(Requests.rqIsUserInRole);
    }

    public IsUserInRoleRequest(String aRoleName) throws Exception {
        this();
        if (aRoleName == null || aRoleName.isEmpty()) {
            throw new Exception("Role name is reqired parameter!");
        }
        roleName = aRoleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String aValue) {
        roleName = aValue;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        private boolean role;

        public Response(boolean aRole) {
            super();
            role = aRole;
        }

        public boolean isRole() {
            return role;
        }

        public void setRole(boolean aValue) {
            role = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
