/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.breakpoints;

import com.eas.client.cache.PlatypusFiles;
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
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.HashSet;
import jdk.internal.dynalink.support.NameCodec;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.jpda.ClassLoadUnloadBreakpoint;
import org.netbeans.api.debugger.jpda.ClassVariable;
import org.netbeans.api.debugger.jpda.JPDABreakpoint;
import org.netbeans.api.debugger.jpda.JPDAClassType;
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

    public static final String SCRIPTS_CLASS_PREFIX = "jdk.nashorn.internal.scripts.Script$";

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

    private static String fileNameToClassNameFragment(String aFileName) {
        final int index = aFileName.lastIndexOf(PlatypusFiles.JAVASCRIPT_FILE_END);
        if (index != -1) {
            aFileName = aFileName.substring(0, index);
        }

        aFileName = aFileName.replace('.', '_').replace('-', '_');
        return NameCodec.encode(aFileName);
    }
/*
    private static final String DANGEROUS_CHARS = "\\/.;:$[]<>";

    private static String replaceDangerChars(final String name) {
        final int len = name.length();
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < len; i++) {
            final char ch = name.charAt(i);
            if (DANGEROUS_CHARS.indexOf(ch) != -1) {
                buf.append('_');
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }
*/
    private final class ScriptsHandler implements JPDABreakpointListener {

        private final JPDADebugger debugger;

        private volatile Set<String> scriptClassesTrack;

        private final ClassLoadUnloadBreakpoint newClassesInternalBreakpoint;
        private final Map<String, Set<Breakpoint>> pendingBreakpoints = new HashMap<>();

        private final Map<Breakpoint, Map<String, TargetBreakpointHandler>> acceptedBreakpoints = new HashMap<>();

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

        private String[] classesByName(String aPattern) {
            return track().stream().filter((String aClassName) -> {
                return aClassName.contains(aPattern);
            }).toArray((int aSize) -> {
                return new String[aSize];
            });
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

            for (String scriptClassName : classesByName(fileNameToClassNameFragment(fileName))) {
                checkSourceBreakpoint(aSourceBreakpoint, scriptClassName);
            }
        }

        synchronized void removeBreakpoint(Breakpoint aSourceBreakpoint) throws Exception {
            FileObject sourceFO = (FileObject) aSourceBreakpoint.getClass().getMethod("getFileObject", new Class[]{}).invoke(aSourceBreakpoint, new Object[]{});
            String fileName = sourceFO.getName();
            Set<Breakpoint> pendingUnderFile = pendingBreakpoints.get(fileName);
            if (pendingUnderFile != null) {
                pendingUnderFile.remove(aSourceBreakpoint);
                if (pendingUnderFile.isEmpty()) {
                    pendingBreakpoints.remove(fileName);
                }
            }
            Map<String, TargetBreakpointHandler> handlers = acceptedBreakpoints.remove(aSourceBreakpoint);
            if (handlers != null) {
                handlers.values().stream().forEach((handler) -> {
                    handler.destroy();
                });
            }
        }

        synchronized void destroy() {
            newClassesInternalBreakpoint.removeJPDABreakpointListener(this);
            DebuggerManager.getDebuggerManager().removeBreakpoint(newClassesInternalBreakpoint);
            pendingBreakpoints.clear();
            acceptedBreakpoints.values().stream().forEach((handlers) -> {
                handlers.values().stream().forEach((handler) -> {
                    handler.destroy();
                });
            });
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
                        // A new script class has been loaded.
                        Variable scriptClass = event.getVariable();
                        if (scriptClass instanceof ClassVariable) {
                            JPDAClassType scriptType = (JPDAClassType) scriptClass.getClass().getMethod("getReflectedType").invoke(scriptClass);
                            String scriptClassName = scriptType.getName();
                            track().add(scriptClassName);
                            for (String fileName : pendingBreakpoints.keySet().toArray(new String[]{})) {
                                if (scriptClassName.contains(fileNameToClassNameFragment(fileName))) {
                                    checkSourceBreakpoints(scriptClassName, fileName);
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

        protected void checkSourceBreakpoints(String aScriptClassName, String aFileName) throws Exception, AbsentInformationException {
            if (aScriptClassName.startsWith(SCRIPTS_CLASS_PREFIX)) {
                Set<Breakpoint> breakpointsToSend = pendingBreakpoints.get(aFileName);
                for (Breakpoint breakpointToSend : breakpointsToSend) {
                    checkSourceBreakpoint(breakpointToSend, aScriptClassName);
                }
            }
        }

        protected void checkSourceBreakpoint(Breakpoint breakpointToSend, String aScriptClassName) throws Exception {
            TargetBreakpointHandler handler = new TargetBreakpointHandler(debugger, breakpointToSend, aScriptClassName);
            boolean accepted = handler.send();
            if (accepted) {
                Map<String, TargetBreakpointHandler> handlers = acceptedBreakpoints.get(breakpointToSend);
                if (handlers == null) {
                    handlers = new HashMap<>();
                    acceptedBreakpoints.put(breakpointToSend, handlers);
                }
                TargetBreakpointHandler oldHandler = handlers.put(aScriptClassName, handler);
                if (oldHandler != null) {
                    oldHandler.destroy();
                }
            } else {
                handler.destroy();
            }
        }
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
            return targetBreakpoint.getValidity() != Breakpoint.VALIDITY.INVALID;
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
