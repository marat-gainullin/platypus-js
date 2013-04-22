/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.annotations;

import com.eas.designer.debugger.DebuggerConstants;
import com.eas.designer.debugger.PlatypusBreakpoint;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.spi.debugger.ui.BreakpointAnnotation;
import org.netbeans.spi.debugger.ui.EditorContextDispatcher;
import org.openide.filesystems.FileObject;
import org.openide.text.Line;
import org.openide.util.Utilities;

/**
 *
 * @author mg
 */
public class PlatypusDisabledBreakpointAnnotation extends BreakpointAnnotation {

    protected PlatypusBreakpoint breakpoint;

    public PlatypusDisabledBreakpointAnnotation(PlatypusBreakpoint aBreakpoint) {
        super();
        breakpoint = aBreakpoint;
    }

    @Override
    public Breakpoint getBreakpoint() {
        return breakpoint;
    }

    @Override
    public String getAnnotationType() {
        return "Platypus-Disabled-Breakpoint-Annotation";
    }

    @Override
    public String getShortDescription() {
        if (getAttachedAnnotatable() instanceof Line) {
            Line l = (Line) getAttachedAnnotatable();
            return l.getDisplayName();
        } else {
            return "";
        }
    }

    private static boolean isJsFile(FileObject fo) {
        return fo != null ? DebuggerConstants.JAVASRIPT_MIME_TYPE.equals(fo.getMIMEType()) : false;
    }

    public static Line getCurrentLine() {
        FileObject fo = Utilities.actionsGlobalContext().lookup(FileObject.class);
        if (isJsFile(fo)) {
            return EditorContextDispatcher.getDefault().getCurrentLine();
        } else {
            return null;
        }
    }

    public static void addAnnotation(PlatypusBreakpoint pBreak) {
        Line line = getCurrentLine();
        if (line != null) {
            PlatypusDisabledBreakpointAnnotation annotation = new PlatypusDisabledBreakpointAnnotation(pBreak);
            annotation.attach(line);
            pBreak.addAnnotation(annotation);
        }
    }
}
