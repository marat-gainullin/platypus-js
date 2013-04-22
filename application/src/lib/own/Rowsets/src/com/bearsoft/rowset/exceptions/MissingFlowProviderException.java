/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.exceptions;

/**
 * Exception, throwing for situation, when data flow process
 * is in prgress, but flow provider is missing.
 * @author mg
 */
public class MissingFlowProviderException extends RowsetException {

    /**
     * No parameters exception constructor.
     * The exception throwing cause description is cerated inside the constructor.
     */
    public MissingFlowProviderException() {
        super("can't apply changes or achieve data without any flow provider");
    }
}
