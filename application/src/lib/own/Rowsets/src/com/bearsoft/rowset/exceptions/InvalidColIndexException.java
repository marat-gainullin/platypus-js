/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.exceptions;

/**
 * Tis Exception is intended to be thrown when wrong column index supplied
 * to <code>Row, Rowset, Fields, Parameters</code> classes methods.
 * @see com.bearsoft.rowset.Row
 * @see com.bearsoft.rowset.Rowset
 * @see com.bearsoft.rowset.Fields
 * @see com.bearsoft.rowset.Parameters
 */
public class InvalidColIndexException extends RowsetException {

    /**
     * Standard exception constructor with description of the exception throwing.
     * @param aMsg The exception throwing description.
     */
    public InvalidColIndexException(String aMsg) {
        super(aMsg);
    }
}
