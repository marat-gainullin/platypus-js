/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.eas.dbcontrols.DbControlPanel;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
/*
    public interface ValueHostListener extends ModelControlListener {

        public Object onSelect(Object aField);

        public Object onRender(Object aRowId, Object aCell, Object aRow);
    }
*/
    @Override
    protected EventSetDescriptor[] getEventSetDescriptors() {
        try {
            List<EventSetDescriptor> descs = new ArrayList<>();
            descs.addAll(Arrays.asList(super.getEventSetDescriptors()));
            descs.add(new EventSetDescriptor("value", ValueHostListener.class, ValueHostListener.class.getMethods(), null, null));
            return descs.toArray(new EventSetDescriptor[]{});
        } catch (IntrospectionException ex) {
            Logger.getLogger(RADModelScalarComponent.class.getName()).log(Level.SEVERE, null, ex);
            return super.getEventSetDescriptors();
        }
    }
}
