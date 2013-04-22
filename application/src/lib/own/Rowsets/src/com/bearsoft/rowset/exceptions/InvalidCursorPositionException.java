/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.exceptions;

/**
 * Exception class intended to hold information about rowset's cursor errors.
 * @author mg
 */
public class InvalidCursorPositionException extends RowsetException {

    /**
     * Standard exception constructor with description of the exception throwing.
     * @param aMsg The exception throwing description.
     */
    public InvalidCursorPositionException(String aMsg) {
        super(aMsg);
    }
}
