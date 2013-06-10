/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.debugger.jmx.server.BreakpointsMBean;
import com.eas.debugger.jmx.server.DebuggerMBean;
import java.util.concurrent.Future;
import org.netbeans.api.project.Project;

/**
 *
 * @author mg
 */
public class DebuggerEnvironment {

    // Weak elements. They are absent while attaching
    public Future<Integer> runningProgram;
    public String runningElement;
    
    public String host;
    public int port;
    public DebuggerMBean mDebugger;
    public BreakpointsMBean mBreakpoints;
    public MBeanDebuggerListener mDebuggerListener;
    public Project project;

    public DebuggerEnvironment(Project aProject) {
        project = aProject;
    }
}
