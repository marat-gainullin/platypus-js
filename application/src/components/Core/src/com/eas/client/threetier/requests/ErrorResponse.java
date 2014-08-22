/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import java.security.AccessControlException;
import java.sql.SQLException;

/**
 *
 * @author pk
 */
public class ErrorResponse extends Response {

    private String errorMessage;
    private boolean accessControl;
    private String sqlState;
    private Integer sqlErrorCode;

    public ErrorResponse() {
        super();
    }
    
    public ErrorResponse(String aErrorMessage) {
        super();
        errorMessage = aErrorMessage;
        if (errorMessage == null) {
            throw new IllegalArgumentException("Creating error response with no error message!");
        }
    }

    public ErrorResponse(SQLException aException) {
        super();
        errorMessage = aException.getMessage() != null && !aException.getMessage().isEmpty() ? aException.getMessage() : aException.getClass().getSimpleName();
        sqlState = aException.getSQLState();
        sqlErrorCode = aException.getErrorCode();
    }

    public ErrorResponse(AccessControlException aException) {
        super();
        accessControl = true;
        errorMessage = aException.getMessage() != null && !aException.getMessage().isEmpty() ? aException.getMessage() : aException.getClass().getSimpleName();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String aValue) {
        errorMessage = aValue;
    }

    public Integer getSqlErrorCode() {
        return sqlErrorCode;
    }
    
    public void setSqlErrorCode(Integer aValue) {
        sqlErrorCode = aValue;
    }

    public String getSqlState() {
        return sqlState;
    }

    public void setSqlState(String aValue) {
        sqlState = aValue;
    }

    public boolean isAccessControl() {
        return accessControl;
    }

    public void setAccessControl(boolean aValue) {
        accessControl = aValue;
    }

    @Override
    public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }
}
