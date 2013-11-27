/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.designer.debugger.actions.ToggleBreakpointAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.debugger.ActionsManager;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.spi.debugger.ActionsProvider;
import org.netbeans.spi.debugger.ActionsProviderListener;
import org.openide.ErrorManager;

/**
 *
 * @author mg
 */
@ActionsProvider.Registration(path = DebuggerConstants.DEBUGGER_SERVICERS_PATH, activateForMIMETypes = {DebuggerConstants.JAVASRIPT_MIME_TYPE})
public class PlatypusDebuggerActionsProvider extends ActionsProvider {

    protected static final Set<Object> supportedAction = new HashSet<>();

    static {
        supportedAction.add(DebuggerConstants.ACTION_ENABLED_CHANGED);// Only for action's state changed events
        supportedAction.add(ActionsManager.ACTION_CONTINUE);
        supportedAction.add(ActionsManager.ACTION_KILL);
        supportedAction.add(ActionsManager.ACTION_START);
        supportedAction.add(ActionsManager.ACTION_PAUSE);
        supportedAction.add(ActionsManager.ACTION_STEP_INTO);
        supportedAction.add(ActionsManager.ACTION_STEP_OVER);
        supportedAction.add(ActionsManager.ACTION_STEP_OUT);
        //supportedAction.add(ActionsManager.ACTION_RUN_TO_CURSOR);
        supportedAction.add(ActionsManager.ACTION_TOGGLE_BREAKPOINT);
    }
    protected Set<ActionsProviderListener> listeners = new HashSet<>();

    @Override
    public Set<Object> getActions() {
        return Collections.unmodifiableSet(supportedAction);
    }

    @Override
    public void doAction(Object action) {
        if (DebuggerConstants.ACTION_ENABLED_CHANGED.equals(action)) {
            fireActionsStateChanged();
            return;
        }
        if (isEnabled(action)) {
            try {
                DebuggerEngine engine = DebuggerManager.getDebuggerManager().getCurrentEngine();
                DebuggerEnvironment env = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
                if (ActionsManager.ACTION_START.equals(action)) {
                    DebuggerUtils.startDebugging(env);
                } else if (ActionsManager.ACTION_KILL.equals(action)) {
                    DebuggerUtils.killEngine(engine);
                } else if (ActionsManager.ACTION_TOGGLE_BREAKPOINT.equals(action)) {
                    ToggleBreakpointAction.toggleBreakpoint();
                } else {
                    env.mDebuggerListener.cancelStoppedAnnotation();
                    if (ActionsManager.ACTION_STEP_OVER.equals(action)) {
                        env.mDebugger.step();
                    } else if (ActionsManager.ACTION_STEP_INTO.equals(action)) {
                        env.mDebugger.stepInto();
                    } else if (ActionsManager.ACTION_STEP_OUT.equals(action)) {
                        env.mDebugger.stepOut();
                    } else if (ActionsManager.ACTION_CONTINUE.equals(action)) {
                        env.mDebugger.continueRun();
                    } else if (ActionsManager.ACTION_PAUSE.equals(action)) {
                        env.mDebugger.pause();
                    }
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
            fireActionsStateChanged();
        }
    }

    @Override
    public boolean isEnabled(Object action) {
        if (DebuggerConstants.ACTION_ENABLED_CHANGED.equals(action)) {
            return true;
        }
        DebuggerEngine engine = DebuggerManager.getDebuggerManager().getCurrentEngine();
        if (engine != null) {
            MBeanDebuggerListener listener = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class).mDebuggerListener;
            boolean running = listener.isRunning();
            if (ActionsManager.ACTION_START.equals(action)) {
                return listener.isHaveBeenRun();
            } else if (ActionsManager.ACTION_KILL.equals(action)) {
                return true;
            } else if (ActionsManager.ACTION_PAUSE.equals(action)) {
                return running;
            } else if (ActionsManager.ACTION_STEP_OVER.equals(action)) {
                return !running;
            } else if (ActionsManager.ACTION_STEP_INTO.equals(action)) {
                return !running;
            } else if (ActionsManager.ACTION_STEP_OUT.equals(action)) {
                return !running;
            } else if (ActionsManager.ACTION_CONTINUE.equals(action)) {
                return !running && listener.isHaveBeenRun();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void addActionsProviderListener(ActionsProviderListener l) {
        listeners.add(l);
    }

    @Override
    public void removeActionsProviderListener(ActionsProviderListener l) {
        listeners.remove(l);
    }

    public void fireActionsStateChanged() {
        for (ActionsProviderListener l : listeners) {
            for (Object action : supportedAction) {
                l.actionStateChange(action, isEnabled(action));
            }
        }
    }
}
