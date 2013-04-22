/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.debugger.jmx.server;

/**
 * Interface to breakpoints handler.
 * Used in JMX client.
 * @author mg
 */
public interface BreakpointsMBean {

    public static final String BREAKPOINTS_MBEAN_NAME = "Platypus debugger:name=Breakpoints";
    
    public boolean isBreakable(String sourceModuleId, int aLineNumber);

    public boolean hasBreakpoint(String sourceModuleId, int aLineNumber);
    
    public boolean addBreakpoint(String sourceModuleId, int aLineNumber);

    public boolean removeBreakpoint(String sourceModuleId, int aLineNumber);

    /**
     * Returns whether breakpoint is on the specified line.
     * @param sourceModuleId
     * @param aLineNumber
     * @return True if a breakpoint is set on the line after the toggling ooperation.
     */
    public boolean toggleBreakpoint(String sourceModuleId, int aLineNumber);
}
