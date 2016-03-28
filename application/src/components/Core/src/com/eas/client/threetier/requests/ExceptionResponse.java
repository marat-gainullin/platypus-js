/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Response;

/**
 *
 * @author pk
 */
public class ExceptionResponse extends Response {

    private String errorMessage;

    public ExceptionResponse() {
        super();
    }

    public ExceptionResponse(Throwable aException) {
        super();
        errorMessage = aException.getMessage() != null && !aException.getMessage().isEmpty() ? aException.getMessage() : aException.toString();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String aValue) {
        errorMessage = aValue;
    }

    @Override
    public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }
}
