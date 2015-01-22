/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.client.forms.Form;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author Марат
 */
class FormRootProperty<T> extends FormProperty<T> {
    protected T defaultValue;
    protected PropertyDescriptor desc;
    protected Form form;

    public FormRootProperty(FormModel aFormModel, PropertyDescriptor aDescriptor) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        super(aDescriptor.getName(), (Class<T>) aDescriptor.getPropertyType(), aDescriptor.getDisplayName(), aDescriptor.getShortDescription());
        desc = aDescriptor;
        form = aFormModel.getForm();
        if (desc.getReadMethod() != null) {
            defaultValue = (T) desc.getReadMethod().invoke(BeanSupport.getDefaultInstance(Form.class), new Object[]{});
        }
    }

    @Override
    public boolean supportsDefaultValue() {
        return true;
    }

    @Override
    public T getValue() throws IllegalAccessException, InvocationTargetException {
        Method readMethod = desc.getReadMethod();
        if (readMethod == null) {
            throw new IllegalAccessException("Not a readable property: " + desc.getName()); // NOI18N
        }
        return (T) readMethod.invoke(form, new Object[0]);
    }

    @Override
    public void setValue(T value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method writeMethod = desc.getWriteMethod();
        if (writeMethod == null) {
            throw new IllegalAccessException("Not a writeable property: " + desc.getName()); // NOI18N
        }
        // invoke the setter method
        T oldValue = getValue();
        writeMethod.invoke(form, new Object[]{value});
        propertyValueChanged(oldValue, value);
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }
    
}
