/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

/**
 * Exception class intended to indicate an erronous call to method nextPage of
 * the FlowProvider interface, when flow provider is not paged.
 *
 * @author mg
 */
public class FlowProviderNotPagedException extends FlowProviderFailedException {

    /**
     * {@inheritDoc}
     */
    public FlowProviderNotPagedException(Exception aCause, String aEntityName) {
        super(aCause, aEntityName);
    }

    /**
     * {@inheritDoc}
     */
    public FlowProviderNotPagedException(String aMsg) {
        super(aMsg);
    }
}
