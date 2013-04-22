/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.eas.dbcontrols.DbControlPanel;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class RADColumnView<M extends DbControlPanel> extends RADModelScalarComponent<M> {

    protected static Set<String> hiddenProps = new HashSet<>(Arrays.asList(new String[]{
        "visible",
        "background",
        "foreground",
        "border",
        "toolTipText",
        "text",
        "selected",
        "model",
        "datamodelElement",
        "componentPopupMenu",
        "cursor",
        "editable",
        "enabled",
        "focusable",
        "font",
        "icon",
        "nextFocusableComponent",
        "opaque"
    }));
    
    public RADColumnView() {
        super();
        setStoredName("view");
    }

    @Override
    protected RADProperty createBeanProperty(PropertyDescriptor desc, Object[] propAccessClsf, Object[] propParentChildDepClsf) {
        if (!hiddenProps.contains(desc.getName())) {
            return super.createBeanProperty(desc, propAccessClsf, propParentChildDepClsf);
        } else {
            return null;
        }
    }

    @Override
    protected EventSetDescriptor[] getEventSetDescriptors() {
        return new EventSetDescriptor[]{};
    }
}
