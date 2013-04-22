/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import java.security.AccessControlException;
import java.sql.SQLException;

/**
 *
 * @author pk
 */
public class ErrorResponse extends Response {

    private String error;
    private boolean accessControl;
    private String sqlState;
    private Integer sqlErrorCode;

    public ErrorResponse(long requestId, String aError) {
        super(requestId);
        error = aError;
        if (error == null) {
            throw new IllegalArgumentException("Creating error response for request " + getRequestID() + " with no error message!");
        }
    }

    public ErrorResponse(long requestId, SQLException aException) {
        super(requestId);
        error = aException.getMessage() != null && !aException.getMessage().isEmpty() ? aException.getMessage() : aException.getClass().getSimpleName();
        sqlState = aException.getSQLState();
        sqlErrorCode = aException.getErrorCode();
    }

    public ErrorResponse(long requestId, AccessControlException aException) {
        super(requestId);
        accessControl = true;
        error = aException.getMessage() != null && !aException.getMessage().isEmpty() ? aException.getMessage() : aException.getClass().getSimpleName();
    }

    public String getError() {
        return error;
    }

    public void setError(String aValue) {
        error = aValue;
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
