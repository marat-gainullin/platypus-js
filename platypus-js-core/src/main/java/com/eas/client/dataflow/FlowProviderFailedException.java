/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

/**
 * Exception, raised when any flow provider malfunction.
 *
 * @author mg
 * @see FlowProvider
 */
public class FlowProviderFailedException extends Exception {

    /**
     * Standard constructor of the exception
     *
     * @param aCause Exception, has caused this exception throwing.
     */
    public FlowProviderFailedException(Exception aCause, String aEntityName) {
        super("While handling entity: " + aEntityName, aCause);
    }

    /**
     * Standard constructor of the exception
     *
     * @param aMsg Description of the exception throwing cause.
     */
    public FlowProviderFailedException(String aMsg) {
        super(aMsg);
    }
}
