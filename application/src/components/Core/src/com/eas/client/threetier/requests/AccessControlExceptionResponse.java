/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import java.security.AccessControlException;
import javax.security.auth.AuthPermission;

/**
 *
 * @author mg
 */
public class AccessControlExceptionResponse extends ExceptionResponse {

    private boolean notLoggedIn;

    public AccessControlExceptionResponse() {
        super();
    }

    public AccessControlExceptionResponse(AccessControlException aException) {
        super(aException);
        if (aException.getPermission() instanceof AuthPermission) {
            notLoggedIn = true;
        }
    }

    public boolean isNotLoggedIn() {
        return notLoggedIn;
    }

    public void setNotLoggedIn(boolean aValue) {
        notLoggedIn = aValue;
    }

    @Override
    public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

}
