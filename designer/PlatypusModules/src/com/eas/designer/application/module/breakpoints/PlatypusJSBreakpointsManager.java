/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.breakpoints;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.request.EventRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.netbeans.api.debugger.LazyDebuggerManagerListener;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.ClassLoadUnloadBreakpoint;
import org.netbeans.api.debugger.jpda.ClassVariable;
import org.netbeans.api.debugger.jpda.JPDABreakpoint;
import org.netbeans.api.debugger.jpda.JPDAClassType;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.api.debugger.jpda.LineBreakpoint;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointEvent;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointListener;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
@DebuggerServiceRegistration(types = LazyDebuggerManagerListener.class)
public class PlatypusJSBreakpointsManager extends DebuggerManagerAdapter {

    private final Map<JPDADebugger, ScriptsHandler> scriptHandlers = new HashMap<>();

    public PlatypusJSBreakpointsManager() {
        super();
    }

    @Override
    public Breakpoint[] initBreakpoints() {
        return null;
    }

    @Override
    public String[] getProperties() {
        return new String[]{DebuggerManager.PROP_BREAKPOINTS_INIT,
            DebuggerManager.PROP_BREAKPOINTS,
            DebuggerManager.PROP_DEBUGGER_ENGINES};
    }

    @Override
    public synchronized void breakpointAdded(Breakpoint aBreakpoint) {
        if (aBreakpoint != null && "JSLineBreakpoint".equals(aBreakpoint.getClass().getSimpleName())) {
            scriptHandlers.values().stream().forEach((handler) -> {
                try {
                    handler.addBreakpoint(aBreakpoint);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
        }
    }

    @Override
    public synchronized void breakpointRemoved(Breakpoint aBreakpoint) {
        if (aBreakpoint != null && "JSLineBreakpoint".equals(aBreakpoint.getClass().getSimpleName())) {
            scriptHandlers.values().stream().forEach((handler) -> {
                try {
                    handler.removeBreakpoint(aBreakpoint);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
        }
    }

    private class DebuggerAtttachListener implements PropertyChangeListener {

        private final JPDADebugger debugger;

        public DebuggerAtttachListener(JPDADebugger aDebugger) {
            super();
            debugger = aDebugger;
        }

        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            synchronized (PlatypusJSBreakpointsManager.this) {
                if (pce.getNewValue() instanceof Integer && (Integer) pce.getNewValue() == JPDADebugger.STATE_RUNNING) {
                    debugger.removePropertyChangeListener(JPDADebugger.PROP_STATE, DebuggerAtttachListener.this);
                    if (!scriptHandlers.containsKey(debugger)) {
                        ScriptsHandler handler = new ScriptsHandler(debugger);
                        scriptHandlers.put(debugger, handler);
                        for (Breakpoint bp : DebuggerManager.getDebuggerManager().getBreakpoints()) {
                            breakpointAdded(bp);
                        }
                    }
                }
            }
        }

    }

    @Override
    public synchronized void engineAdded(DebuggerEngine engine) {
        JPDADebugger debugger = engine.lookupFirst(null, JPDADebugger.class);
        if (debugger != null) {
            debugger.addPropertyChangeListener(JPDADebugger.PROP_STATE, new DebuggerAtttachListener(debugger));
        }
    }

    @Override
    public void engineRemoved(DebuggerEngine engine) {
        JPDADebugger debugger = engine.lookupFirst(null, JPDADebugger.class);
        if (debugger != null) {
            ScriptsHandler handler = scriptHandlers.remove(debugger);
            if (handler != null) {
                handler.destroy();
            }
        }
    }

    private final class ScriptsHandler implements JPDABreakpointListener {

        private final JPDADebugger debugger;

        private volatile Set<String> scriptClassesTrack;

        private final ClassLoadUnloadBreakpoint newClassesInternalBreakpoint;
        private final Map<String, List<MethodBreakpoint>> internalBreakpoints = new HashMap<>();
        private final Map<String, Set<Breakpoint>> pendingBreakpoints = new HashMap<>();

        private final Map<Breakpoint, TargetBreakpointHandler> acceptedBreakpoints = new HashMap<>();

        ScriptsHandler(JPDADebugger aDebugger) {
            super();
            debugger = aDebugger;
            newClassesInternalBreakpoint = ClassLoadUnloadBreakpoint.create(SCRIPTS_CLASS_PREFIX + "*",
                    false,
                    ClassLoadUnloadBreakpoint.TYPE_CLASS_LOADED);
            newClassesInternalBreakpoint.setHidden(true);
            newClassesInternalBreakpoint.setSuspend(EventRequest.SUSPEND_ALL);
            newClassesInternalBreakpoint.addJPDABreakpointListener(this);
            DebuggerManager.getDebuggerManager().addBreakpoint(newClassesInternalBreakpoint);
        }

        private Set<String> track() {
            if (scriptClassesTrack == null || scriptClassesTrack.isEmpty()) {
                scriptClassesTrack = new HashSet<>();
                debugger.getAllClasses().stream().forEach((JPDAClassType aJPDAClass) -> {
                    if (aJPDAClass.getName().startsWith(SCRIPTS_CLASS_PREFIX)) {
                        scriptClassesTrack.add(aJPDAClass.getName());
                    }
                });
            }
            return scriptClassesTrack;
        }

        synchronized void addBreakpoint(Breakpoint aSourceBreakpoint) throws Exception {
            FileObject sourceFO = (FileObject) aSourceBreakpoint.getClass().getMethod("getFileObject", new Class[]{}).invoke(aSourceBreakpoint, new Object[]{});
            String fileName = sourceFO.getName();
            Set<Breakpoint> pendingUnderFile = pendingBreakpoints.get(fileName);
            if (pendingUnderFile == null) {
                pendingUnderFile = new HashSet<>();
                pendingBreakpoints.put(fileName, pendingUnderFile);
            }
            pendingUnderFile.add(aSourceBreakpoint);
            updateInternalBreakpoint(fileName);
        }

        synchronized void removeBreakpoint(Breakpoint aSourceBreakpoint) throws Exception {
            FileObject sourceFO = (FileObject) aSourceBreakpoint.getClass().getMethod("getFileObject", new Class[]{}).invoke(aSourceBreakpoint, new Object[]{});
            String fileName = sourceFO.getName();
            Set<Breakpoint> pendingUnderFile = pendingBreakpoints.get(fileName);
            if (pendingUnderFile != null) {
                pendingUnderFile.remove(aSourceBreakpoint);
                if (pendingUnderFile.isEmpty()) {
                    pendingBreakpoints.remove(fileName);
                    List<MethodBreakpoint> internalBPs = internalBreakpoints.remove(fileName);
                    internalBPs.stream().forEach((MethodBreakpoint internalBreakpoint) -> {
                        internalBreakpoint.removeJPDABreakpointListener(this);
                        DebuggerManager.getDebuggerManager().removeBreakpoint(internalBreakpoint);
                    });
                }
            }
            TargetBreakpointHandler handler = acceptedBreakpoints.remove(aSourceBreakpoint);
            if (handler != null) {
                handler.destroy();
            }
        }

        synchronized void destroy() {
            newClassesInternalBreakpoint.removeJPDABreakpointListener(this);
            DebuggerManager.getDebuggerManager().removeBreakpoint(newClassesInternalBreakpoint);
            for (List<MethodBreakpoint> internalBPs : internalBreakpoints.values()) {
                internalBPs.stream().forEach((MethodBreakpoint internalBreakpoint) -> {
                    internalBreakpoint.removeJPDABreakpointListener(this);
                    DebuggerManager.getDebuggerManager().removeBreakpoint(internalBreakpoint);
                });
            }
            internalBreakpoints.clear();
            pendingBreakpoints.clear();
            for (TargetBreakpointHandler handler : acceptedBreakpoints.values()) {
                handler.destroy();
            }
            acceptedBreakpoints.clear();
        }

        /**
         * A new script class is loaded/initialized.
         */
        @Override
        public synchronized void breakpointReached(JPDABreakpointEvent event) {
            if (debugger == event.getDebugger()) {
                try {
                    if (event.getSource() == newClassesInternalBreakpoint) {
                        // A new script class is loaded.
                        Variable scriptClass = event.getVariable();
                        if (scriptClass instanceof ClassVariable) {
                            JPDAClassType scriptType = (JPDAClassType) scriptClass.getClass().getMethod("getReflectedType").invoke(scriptClass);
                            track().add(scriptType.getName());
                            for (String fileName : internalBreakpoints.keySet().toArray(new String[]{})) {
                                if (scriptType.getName().contains(fileName)) {
                                    updateInternalBreakpoint(fileName);
                                }
                            }
                        }
                    } else if (event.getSource() instanceof MethodBreakpoint) {
                        MethodBreakpoint internalBreakpoint = (MethodBreakpoint) event.getSource();
                        if ("*".equals(internalBreakpoint.getMethodName())) {
                            JPDAThread thread = event.getThread();
                            CallStackFrame topFrame = thread.getCallStack(0, 1)[0];
                            String currentClassName = topFrame.getClassName();
                            for (String fileName : internalBreakpoints.keySet().toArray(new String[]{})) {
                                if (currentClassName.contains(fileName)) {
                                    checkSourceBreakpoints(currentClassName, fileName);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    // no op
                }
                event.resume();
            }
        }

        protected void updateInternalBreakpoint(String fileName) {
            List<MethodBreakpoint> internalBPs = internalBreakpoints.remove(fileName);
            if (internalBPs != null) {
                internalBPs.stream().forEach((MethodBreakpoint internalBreakpoint) -> {
                    internalBreakpoint.removeJPDABreakpointListener(this);
                    DebuggerManager.getDebuggerManager().removeBreakpoint(internalBreakpoint);
                });
            }
            //
            internalBPs = new ArrayList<>();
            for (String aClassName : track()) {
                if (aClassName.contains(fileName)) {
                    MethodBreakpoint maskedInternalBreakpoint = MethodBreakpoint.create(aClassName, "*");
                    maskedInternalBreakpoint.setHidden(true);
                    maskedInternalBreakpoint.setSuspend(EventRequest.SUSPEND_NONE);
                    maskedInternalBreakpoint.addJPDABreakpointListener(this);
                    internalBPs.add(maskedInternalBreakpoint);
                    DebuggerManager.getDebuggerManager().addBreakpoint(maskedInternalBreakpoint);
                }
            }
            internalBreakpoints.put(fileName, internalBPs);
        }

        protected void checkSourceBreakpoints(String aScriptClassName, String aFileName) throws Exception, AbsentInformationException {
            if (aScriptClassName.startsWith(SCRIPTS_CLASS_PREFIX)) {
                Set<Breakpoint> breakpointsToSend = pendingBreakpoints.get(aFileName);
                for (Breakpoint breakpointToSend : breakpointsToSend) {
                    if (!acceptedBreakpoints.containsKey(breakpointToSend)) {
                        TargetBreakpointHandler handler = new TargetBreakpointHandler(debugger, breakpointToSend, aScriptClassName);
                        boolean accepted = handler.send();
                        if (accepted) {
                            acceptedBreakpoints.put(breakpointToSend, handler);
                        } else {
                            handler.destroy();
                        }
                    }
                }
            }
        }

        protected static final String SCRIPTS_CLASS_PREFIX = "jdk.nashorn.internal.scripts.Script$";

    }

    private static final class TargetBreakpointHandler {

        private final JPDADebugger debugger;
        private final Breakpoint sourceBreakpoint;
        private final LineBreakpoint targetBreakpoint;
        private final PropertyChangeListener sourceBreakpointReflector;

        TargetBreakpointHandler(JPDADebugger aDebugger, Breakpoint aJsBreakpoint, String aPreferredClassName) throws Exception {
            super();
            debugger = aDebugger;
            sourceBreakpoint = aJsBreakpoint;
            URL url = (URL) sourceBreakpoint.getClass().getMethod("getURL", new Class[]{}).invoke(sourceBreakpoint, new Object[]{});
            int lineNumber = (Integer) sourceBreakpoint.getClass().getMethod("getLineNumber", new Class[]{}).invoke(sourceBreakpoint, new Object[]{});
            targetBreakpoint = LineBreakpoint.create(url.toString(), lineNumber);
            targetBreakpoint.setHidden(true);
            targetBreakpoint.setPreferredClassName(aPreferredClassName);
            targetBreakpoint.setSuspend(JPDABreakpoint.SUSPEND_EVENT_THREAD);
            targetBreakpoint.setSession(debugger);
            if (!sourceBreakpoint.isEnabled()) {
                targetBreakpoint.disable();
            }
            sourceBreakpointReflector = new BrekpointPropertiesPropagator(targetBreakpoint);
            sourceBreakpoint.addPropertyChangeListener(sourceBreakpointReflector);
        }

        boolean send() {
            DebuggerManager.getDebuggerManager().addBreakpoint(targetBreakpoint);
            return targetBreakpoint.getValidity() == Breakpoint.VALIDITY.VALID;
        }

        void destroy() {
            sourceBreakpoint.removePropertyChangeListener(sourceBreakpointReflector);
            DebuggerManager.getDebuggerManager().removeBreakpoint(targetBreakpoint);
        }

        private static class BrekpointPropertiesPropagator implements PropertyChangeListener {

            private final LineBreakpoint targetBreakpoint;

            BrekpointPropertiesPropagator(LineBreakpoint aTargetBreakpoint) {
                super();
                targetBreakpoint = aTargetBreakpoint;
            }

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                switch (propertyName) {
                    case Breakpoint.PROP_ENABLED:
                        if (Boolean.TRUE.equals(evt.getNewValue())) {
                            targetBreakpoint.enable();
                        }
                        if (Boolean.FALSE.equals(evt.getNewValue())) {
                            targetBreakpoint.disable();
                        }
                        break;
                }
            }
        }
    }
}
