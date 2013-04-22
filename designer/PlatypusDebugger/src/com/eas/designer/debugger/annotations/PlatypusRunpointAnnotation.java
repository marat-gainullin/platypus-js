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

    public PlatypusRunpointAnnotation() {
        super();
    }

    @Override
    public String getAnnotationType() {
        return "Platypus-Runpoint-Annotation";
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

    public static void annotateLine(Line line) {
        if (line != null) {
            PlatypusRunpointAnnotation annotation = new PlatypusRunpointAnnotation();
            annotation.attach(line);
        }
    }
}
