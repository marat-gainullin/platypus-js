/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.bearsoft.org.netbeans.modules.form.editors.EntityJSObjectFieldEditor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author mg
 */
public class EntityJSObjectFieldProperty extends RADProperty<String> {

    protected EntityJSObjectFieldEditor editor;

    public EntityJSObjectFieldProperty(RADComponent<?> aRadComp, PropertyDescriptor propdesc, String aPrefix) throws IllegalAccessException, InvocationTargetException {
        super(aRadComp, propdesc);
        editor = new EntityJSObjectFieldEditor(aPrefix);
        editor.setFormModel(aRadComp.getFormModel());
        editor.setProperty(this);
        editor.setComp(aRadComp);
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return editor;
    }

}
