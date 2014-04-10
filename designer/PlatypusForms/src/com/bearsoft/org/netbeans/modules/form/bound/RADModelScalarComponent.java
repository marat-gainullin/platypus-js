/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.eas.dbcontrols.DbControlPanel;

/**
 * @author mg
 *
 * This class represents a standard form editor wrapper for model-aware scalar
 * visual component.
 * @see RADModelMapLayer
 */
public class RADModelScalarComponent<M extends DbControlPanel> extends RADVisualComponent<M> {
    public interface ValueHostListener extends ModelControlListener {

        public Object onSelect(Object aField);

        public Object onRender(Object evt);
    }
}
