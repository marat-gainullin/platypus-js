/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.exceptions;

/**
 * Exception class intended to indicate an erronous call to method refresh of the FlowProvider interface, when flow provider is paged.
 * @author mg
 * @see com.bearsoft.rowset.dataflow.FlowProvider
 */
public class FlowProviderPagedException extends FlowProviderFailedException{

    /**
     * @inheritDoc
     */
    public FlowProviderPagedException(Exception aCause)
    {
        super(aCause);
    }

    /**
     * @inheritDoc
     */
    public FlowProviderPagedException(String aMsg)
    {
        super(aMsg);
    }
}
