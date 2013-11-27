/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.annotations;

import com.eas.designer.debugger.PlatypusBreakpoint;

/**
 *
 * @author mg
 */
public class PlatypusDisabledBreakpointAnnotation extends PlatypusBreakpointAnnotation {

    /**
     * Annotation type constant.
     */
    public static final String DISABLED_BREAKPOINT_ANNOTATION_TYPE = "DisabledBreakpoint"; // NOI18N

    public PlatypusDisabledBreakpointAnnotation(PlatypusBreakpoint aBreakpoint) {
        super(aBreakpoint);
    }

    @Override
    public String getAnnotationType() {
        return DISABLED_BREAKPOINT_ANNOTATION_TYPE;
    }
}
