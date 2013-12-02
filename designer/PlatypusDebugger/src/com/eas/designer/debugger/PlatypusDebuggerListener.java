/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.designer.debugger.annotations.PlatypusBreakpointAnnotation;
import java.io.IOException;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;

/**
 *
 * @author mg
 */
public class PlatypusDebuggerListener extends DebuggerManagerAdapter {

    @Override
    public void breakpointAdded(Breakpoint breakpoint) {
        if (breakpoint instanceof PlatypusBreakpoint) {
            PlatypusBreakpoint pBreak = (PlatypusBreakpoint) breakpoint;
            if (PlatypusBreakpointAnnotation.getCurrentLine() != null) {
                pBreak.clearAnnotations();
                PlatypusBreakpointAnnotation.addAnnotation(pBreak);
            }
            if (pBreak.isEnabled()) {
                DebuggerEngine[] engines = DebuggerManager.getDebuggerManager().getDebuggerEngines();
                for (DebuggerEngine engine : engines) {
                    DebuggerEnvironment env = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
                    if (env != null && env.mBreakpoints != null) {
                        pBreak.remoteAdd(env.mBreakpoints);
                    }
                }
            }
        }
    }

    @Override
    public void breakpointRemoved(Breakpoint breakpoint) {
        if (breakpoint instanceof PlatypusBreakpoint) {
            PlatypusBreakpoint pBreak = (PlatypusBreakpoint) breakpoint;
            pBreak.clearAnnotations();
            DebuggerEngine[] engines = DebuggerManager.getDebuggerManager().getDebuggerEngines();
            for (DebuggerEngine engine : engines) {
                DebuggerEnvironment env = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
                if (env != null && env.mBreakpoints != null) {
                    pBreak.remoteRemove(env.mBreakpoints);
                }
            }
        }
    }

    @Override
    public void engineRemoved(DebuggerEngine engine) {
        try {
            DebuggerEnvironment env = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
            if (env != null) {
                MBeanDebuggerListener dbgListener = env.mDebuggerListener;
                dbgListener.die();
                MBeanServerConnection jmxConnection = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, MBeanServerConnection.class);
                ObjectName mBeanName = new ObjectName(DebuggerMBean.DEBUGGER_MBEAN_NAME);
                jmxConnection.removeNotificationListener(mBeanName, dbgListener);
                if (env.runningProgram != null) {// The program was created by the debugger, and so kill it too
                    env.runningProgram.cancel(true);
                }
            }
        } catch (MalformedObjectNameException | InstanceNotFoundException | ListenerNotFoundException | IOException ex) {
            Logger.getLogger(PlatypusDebuggerListener.class.getName()).info(ex.getMessage());
        }
    }
}
