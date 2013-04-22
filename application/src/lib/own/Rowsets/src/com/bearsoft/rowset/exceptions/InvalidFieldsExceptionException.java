/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.exceptions;

/**
 * Exception that is thrown while setting wrong feilds definition to the existing <code>Rowset</code>
 * @author mg
 */
public class InvalidFieldsExceptionException extends RowsetException {

    /**
     * Standard exception constructor with description of the exception throwing.
     * @param aMsg The exception throwing description.
     */
    public InvalidFieldsExceptionException(String aMsg) {
        super(aMsg);
    }
}
