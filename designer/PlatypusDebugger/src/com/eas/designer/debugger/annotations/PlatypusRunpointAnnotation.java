/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.annotations;

import org.openide.text.Annotation;
import org.openide.text.Line;

/**
 *
 * @author mg
 */
public class PlatypusRunpointAnnotation extends Annotation {

    /**
     * Annotation type constant.
     */
    public static final String CURRENT_LINE_ANNOTATION_TYPE = "CurrentPC";

    public PlatypusRunpointAnnotation() {
        super();
    }

    @Override
    public String getAnnotationType() {
        return CURRENT_LINE_ANNOTATION_TYPE;
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
}
