/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.eas.client.forms.components.model.ModelComponentDecorator;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author mg
 *
 * This class represents a standard form editor wrapper for model-aware scalar
 * visual component.
 * @see RADModelMapLayer
 */
public class RADModelScalarComponent<M extends ModelComponentDecorator> extends RADVisualComponent<M> {

    public RADModelScalarComponent() {
        super();
    }

    @Override
    protected RADProperty<?> createCheckedBeanProperty(PropertyDescriptor desc) throws InvocationTargetException, IllegalAccessException {
        switch (desc.getName()) {
            case "field":
                return new EntityJSObjectFieldProperty(this, desc, "cursor.");
            case "displayField":// only ModelCombo case
                return new EntityJSObjectFieldProperty(this, desc, "");
            default:
                return super.createCheckedBeanProperty(desc);
        }
    }

}
