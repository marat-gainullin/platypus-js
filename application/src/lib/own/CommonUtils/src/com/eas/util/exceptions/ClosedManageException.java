/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util.exceptions;

/**
 *
 * @author AB
 */
public class ClosedManageException extends RuntimeException {
    private String exceptionMessage;

    public ClosedManageException() {
        exceptionMessage = null;
    }

    public ClosedManageException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String toString() {
        return "Message from formClosing: " + exceptionMessage;
    }
    
}
