/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.exceptions;

/**
 * The base exception class for the rowsets implementation library.
 * @author mg
 */
public class RowsetException extends Exception {

    /**
     * First standard constructor, accepting exception description.
     * @param aMsg Description of the exception cause.
     */
    public RowsetException(String aMsg) {
        super(aMsg);
    }

    /**
     * Second standard constructor, accepting previous exception as this exception cause.
     * @param aCourse
     */
    public RowsetException(Throwable aCourse) {
        super(aCourse);
    }
}
