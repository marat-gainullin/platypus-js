/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.designer.debugger.annotations.PlatypusBreakpointAnnotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.debugger.ActionsManager;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.spi.debugger.ActionsProvider;
import org.netbeans.spi.debugger.ActionsProviderListener;
import org.netbeans.spi.debugger.ActionsProviderSupport;
import org.openide.ErrorManager;
import org.openide.text.Line;

/**
 *
 * @author mg
 */
@ActionsProvider.Registrations({
    @ActionsProvider.Registration(path = DebuggerConstants.DEBUGGER_SERVICERS_PATH, activateForMIMETypes = {DebuggerConstants.JAVASRIPT_MIME_TYPE}),
    @ActionsProvider.Registration(actions = {"toggleBreakpoint"}, activateForMIMETypes = {DebuggerConstants.JAVASRIPT_MIME_TYPE})})
public class PlatypusDebuggerActionsProvider extends ActionsProviderSupport {

    protected static final Set<Object> supportedActions = new HashSet<>();

    static {
        supportedActions.add(DebuggerConstants.ACTION_ENABLED_CHANGED);// Only for action's state changed events
        supportedActions.add(ActionsManager.ACTION_START);// after program has ran and when break are to be posted
        supportedActions.add(ActionsManager.ACTION_CONTINUE);
        supportedActions.add(ActionsManager.ACTION_KILL);
        supportedActions.add(ActionsManager.ACTION_PAUSE);
        supportedActions.add(ActionsManager.ACTION_STEP_INTO);
        supportedActions.add(ActionsManager.ACTION_STEP_OVER);
        supportedActions.add(ActionsManager.ACTION_STEP_OUT);
        //supportedAction.add(ActionsManager.ACTION_RUN_TO_CURSOR);
        supportedActions.add(ActionsManager.ACTION_TOGGLE_BREAKPOINT);
    }
    protected Set<ActionsProviderListener> listeners = new HashSet<>();

    public PlatypusDebuggerActionsProvider() {
        super();
        updateEnabledActions();
    }

    @Override
    public Set<Object> getActions() {
        return Collections.unmodifiableSet(supportedActions);
    }

    @Override
    public void doAction(Object action) {
        if (isEnabled(action)) {
            try {
                DebuggerEngine engine = DebuggerManager.getDebuggerManager().getCurrentEngine();
                DebuggerEnvironment env = engine != null ? engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class) : null;
                if (ActionsManager.ACTION_START.equals(action)) {
                    DebuggerUtils.startDebugging(env);
                } else if (ActionsManager.ACTION_KILL.equals(action)) {
                    DebuggerUtils.killEngine(engine);
                } else if (ActionsManager.ACTION_TOGGLE_BREAKPOINT.equals(action)) {
                    toggleBreakpoint();
                } else if(env != null){
                    assert env.mDebuggerListener != null && env.mDebugger != null;
                    if (ActionsManager.ACTION_STEP_OVER.equals(action)) {
                        env.mDebuggerListener.cancelStoppedAnnotation();
                        env.mDebugger.step();
                    } else if (ActionsManager.ACTION_STEP_INTO.equals(action)) {
                        env.mDebuggerListener.cancelStoppedAnnotation();
                        env.mDebugger.stepInto();
                    } else if (ActionsManager.ACTION_STEP_OUT.equals(action)) {
                        env.mDebuggerListener.cancelStoppedAnnotation();
                        env.mDebugger.stepOut();
                    } else if (ActionsManager.ACTION_CONTINUE.equals(action)) {
                        env.mDebuggerListener.cancelStoppedAnnotation();
                        env.mDebugger.continueRun();
                    } else if (ActionsManager.ACTION_PAUSE.equals(action)) {
                        env.mDebugger.pause();
                    }
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        updateEnabledActions();
    }

    private void updateEnabledActions() {
        boolean running = false;
        boolean haveBeenRun = false;
        DebuggerEngine engine = DebuggerManager.getDebuggerManager().getCurrentEngine();
        if (engine != null) {
            DebuggerEnvironment env = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
            if (env != null) {
                MBeanDebuggerListener listener = env.mDebuggerListener;
                if (listener != null) {
                    haveBeenRun = listener.isHaveBeenRan();
                    running = listener.isRunning();
                }
            }
        }
        setEnabled(ActionsManager.ACTION_START, !haveBeenRun);
        setEnabled(ActionsManager.ACTION_KILL, engine != null);
        setEnabled(ActionsManager.ACTION_CONTINUE, !running && haveBeenRun);
        setEnabled(ActionsManager.ACTION_PAUSE, running);
        setEnabled(ActionsManager.ACTION_STEP_INTO, !running && haveBeenRun);
        setEnabled(ActionsManager.ACTION_STEP_OUT, !running && haveBeenRun);
        setEnabled(ActionsManager.ACTION_STEP_OVER, !running && haveBeenRun);
        //setEnabled(ActionsManager.ACTION_RUN_TO_CURSOR, !running && haveBeenRun);
        setEnabled(ActionsManager.ACTION_TOGGLE_BREAKPOINT, true);
        setEnabled(DebuggerConstants.ACTION_ENABLED_CHANGED, true);
    }

    public static void toggleBreakpoint() {
        Line line = PlatypusBreakpointAnnotation.getCurrentLine();
        if (line != null) {
            Breakpoint[] breakpoints = DebuggerManager.getDebuggerManager().getBreakpoints();
            boolean found = false;
            for (int i = 0; i < breakpoints.length; i++) {
                if (breakpoints[i] instanceof PlatypusBreakpoint && ((PlatypusBreakpoint) breakpoints[i]).getLine().equals(line)) {
                    DebuggerManager.getDebuggerManager().removeBreakpoint(breakpoints[i]);
                    found = true;
                }
            }
            if (!found) {
                DebuggerManager.getDebuggerManager().addBreakpoint(new PlatypusBreakpoint(line));
            }
        }
    }
}
