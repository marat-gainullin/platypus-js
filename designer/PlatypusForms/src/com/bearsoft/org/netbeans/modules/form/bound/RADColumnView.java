/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.eas.client.forms.components.model.ModelComponentDecorator;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class RADColumnView<M extends ModelComponentDecorator> extends RADModelScalarComponent<M> {

    protected static Set<String> hiddenProps = new HashSet<>(Arrays.asList(new String[]{
        "visible",
        "background",
        "foreground",
        "border",
        "toolTipText",
        "text",
        "selected",
        "model",
        "field",
        "componentPopupMenu",
        "cursor",
        "editable",
        "enabled",
        "focusable",
        "font",
        "icon",
        "left",
        "top",
        "width",
        "height",
        "error",
        "nextFocusableComponent",
        "opaque"
    }));
    
    public RADColumnView() {
        super();
        setInModel(false);
        setStoredName("view");
    }

    @Override
    protected RADProperty createBeanProperty(PropertyDescriptor desc) {
        if (!hiddenProps.contains(desc.getName())) {
            return super.createBeanProperty(desc);
        } else {
            return null;
        }
    }
}
