/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.debugger.jmx.server.BreakpointsMBean;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.debugger.annotations.PlatypusBreakpointAnnotation;
import com.eas.designer.debugger.annotations.PlatypusDisabledBreakpointAnnotation;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.spi.debugger.ui.BreakpointAnnotation;
import org.openide.filesystems.FileObject;
import org.openide.text.Line;

/**
 *
 * @author mg
 */
public class PlatypusBreakpoint extends Breakpoint {

    protected boolean enabled = true;
    protected Line line;
    protected List<BreakpointAnnotation> annotations = new ArrayList<>();

    public PlatypusBreakpoint(Line aLine) {
        super();
        line = aLine;
    }

    public void remoteAdd(BreakpointsMBean aBreakpoints) {
        if (aBreakpoints != null && line != null && isEnabled()) {
            int lineNo = line.getLineNumber();
            FileObject file = line.getLookup().lookup(FileObject.class);
            if (file != null) {
                String url = IndexerQuery.file2AppElementId(file);
                if (aBreakpoints.isBreakable(url, lineNo)) {
                    aBreakpoints.addBreakpoint(url, lineNo);
                } else {
                    disable();
                }
            }
        }
    }

    public void remoteRemove(BreakpointsMBean aBreakpoints) {
        if (aBreakpoints != null && line != null) {
            int lineNo = line.getLineNumber();
            FileObject file = line.getLookup().lookup(FileObject.class);
            if (file != null) {
                String url = IndexerQuery.file2AppElementId(file);
                aBreakpoints.removeBreakpoint(url, lineNo);
            }
        }
    }

    public Line getLine() {
        return line;
    }

    public void addAnnotation(BreakpointAnnotation aAnnotation) {
        annotations.add(aAnnotation);
        aAnnotation.attach(line);
    }

    public void removeAnnotation(BreakpointAnnotation aAnnotation) {
        annotations.remove(aAnnotation);
        aAnnotation.detach();
    }

    public void clearAnnotations() {
        for (BreakpointAnnotation annotation : annotations.toArray(new BreakpointAnnotation[]{})) {
            annotation.detach();
        }
        annotations.clear();
    }

    /**
     * Test whether the breakpoint is enabled.
     *
     * @return <code>true</code> if so
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Disables the breakpoint.
     */
    @Override
    public void disable() {
        if (enabled) {
            enabled = false;
            for (int i = annotations.size() - 1; i >= 0; i--) {
                BreakpointAnnotation annotation = annotations.get(i);
                if (annotation instanceof PlatypusBreakpointAnnotation) {
                    annotation.detach();
                    annotations.remove(i);
                }
            }
            PlatypusDisabledBreakpointAnnotation annotation = new PlatypusDisabledBreakpointAnnotation(this);
            annotation.attach(line);
            annotations.add(annotation);
            DebuggerEngine[] engines = DebuggerManager.getDebuggerManager().getDebuggerEngines();
            for (DebuggerEngine engine : engines) {
                BreakpointsMBean breakpoints = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class).mBreakpoints;
                remoteRemove(breakpoints);
            }
            firePropertyChange(PROP_ENABLED, Boolean.TRUE, Boolean.FALSE);
        }
    }

    /**
     * Enables the breakpoint.
     */
    @Override
    public void enable() {
        if (!enabled) {
            enabled = true;
            for (int i = annotations.size() - 1; i >= 0; i--) {
                BreakpointAnnotation annotation = annotations.get(i);
                if (annotation instanceof PlatypusDisabledBreakpointAnnotation) {
                    annotation.detach();
                    annotations.remove(i);
                }
            }
            PlatypusBreakpointAnnotation annotation = new PlatypusBreakpointAnnotation(this);
            annotation.attach(line);
            annotations.add(annotation);
            DebuggerEngine[] engines = DebuggerManager.getDebuggerManager().getDebuggerEngines();
            for (DebuggerEngine engine : engines) {
                BreakpointsMBean breakpoints = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class).mBreakpoints;
                remoteAdd(breakpoints);
            }
            firePropertyChange(PROP_ENABLED, Boolean.FALSE, Boolean.TRUE);
        }
    }
}
