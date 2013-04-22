/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.debugger.jmx.server.BreakpointsMBean;
import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.designer.debugger.annotations.PlatypusBreakpointAnnotation;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerListener;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.Watch;

/**
 *
 * @author mg
 */
public class PlatypusDebuggerListener implements DebuggerManagerListener {

    private static PlatypusDebuggerListener instance = null;

    public static void checkListening() {
        if (instance == null) {
            instance = new PlatypusDebuggerListener();
            DebuggerManager.getDebuggerManager().addDebuggerListener(instance);
        }
    }

    @Override
    public Breakpoint[] initBreakpoints() {
        return null;
    }

    @Override
    public void breakpointAdded(Breakpoint breakpoint) {
        if (breakpoint instanceof PlatypusBreakpoint) {
            PlatypusBreakpoint pBreak = (PlatypusBreakpoint) breakpoint;
            pBreak.clearAnnotations();
            PlatypusBreakpointAnnotation.addAnnotation(pBreak);
            if (pBreak.isEnabled()) {
                DebuggerEngine[] engines = DebuggerManager.getDebuggerManager().getDebuggerEngines();
                for (DebuggerEngine engine : engines) {
                    BreakpointsMBean breakpoints = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class).mBreakpoints;
                    pBreak.remoteAdd(breakpoints);
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
                BreakpointsMBean breakpoints = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class).mBreakpoints;
                pBreak.remoteRemove(breakpoints);
            }
        }
    }

    @Override
    public void initWatches() {
    }

    @Override
    public void watchAdded(Watch watch) {
    }

    @Override
    public void watchRemoved(Watch watch) {
    }

    @Override
    public void sessionAdded(Session session) {
    }

    @Override
    public void sessionRemoved(Session session) {
    }

    @Override
    public void engineAdded(DebuggerEngine engine) {
    }

    @Override
    public void engineRemoved(DebuggerEngine engine) {
        try {
            MBeanDebuggerListener dbgListener = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class).mDebuggerListener;
            dbgListener.die();
            MBeanServerConnection jmxConnection = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, MBeanServerConnection.class);
            ObjectName mBeanName = new ObjectName(DebuggerMBean.DEBUGGER_MBEAN_NAME);
            jmxConnection.removeNotificationListener(mBeanName, dbgListener);
            Future<Integer> f = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class).runningProgram;
            if (f != null) {
                f.cancel(true);
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusDebuggerListener.class.getName()).info(ex.getMessage());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}
