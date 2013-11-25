/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.debugger.jmx.server;

/**
 *
 * @author mg
 */
public interface DebuggerMBean {

    public static final String DEBUGGER_MBEAN_NAME = "Platypus debugger:name=Debugger";
    public static final String URL_TAG_NAME = "Url";
    public static final String LINE_TAG_NAME = "LineNo";
    public static final String THREAD_NAME_TAG_NAME = "Thread";
    public static final String FUNCTION_NAME_TAG_NAME = "function";

    public static final String TAGS_SEPARATOR = "|";
    public static final String VALUE_SEPARATOR = " ";
    public static final String BREAK_ATTRIBUTE_NAME = "breakInfo";

    public void pause() throws Exception;

    public void continueRun() throws Exception;

    public void step() throws Exception;

    public void stepInto() throws Exception;

    public void stepOut() throws Exception;

    public void stop() throws Exception;

    public String evaluate(String aExpression) throws Exception;
    
    public String[] locals() throws Exception;
    
    public String[] props(String aExpression) throws Exception;

    public String[][] getCallStack() throws Exception;
    
    public int currentFrame() throws Exception;
    
    public void setCurrentFrame(int aValue) throws Exception;
}
