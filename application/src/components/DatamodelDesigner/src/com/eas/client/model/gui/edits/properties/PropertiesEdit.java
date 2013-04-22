/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.edits.properties;

import com.eas.client.model.gui.edits.DatamodelEdit;
import com.eas.client.model.Entity;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class PropertiesEdit<E extends Entity<?, ?, E>> extends DatamodelEdit {

    protected String propName;
    protected Object beforeValue;
    protected Object afterValue;
    protected E entity;

    public PropertiesEdit(E aEntity, String aPropName, Object aBeforeValue, Object aAfterValue) {
        super();
        propName = aPropName;
        beforeValue = aBeforeValue;
        afterValue = aAfterValue;
        entity = aEntity;
    }

    public void setAfterValue(Object aValue) {
        afterValue = aValue;
    }

    public void setBeforeValue(Object aValue) {
        beforeValue = aValue;
    }

    public void setPropName(String aValue) {
        propName = aValue;
    }

    public E getEntity() {
        return entity;
    }

    public Object getAfterValue() {
        return afterValue;
    }

    public Object getBeforeValue() {
        return beforeValue;
    }

    public String getPropName() {
        return propName;
    }

    @Override
    protected void redoWork() {
        try {
            setValueToEntity(entity, propName, afterValue);
        } catch (Exception ex) {
            Logger.getLogger(PropertiesEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void undoWork() {
        try {
            setValueToEntity(entity, propName, beforeValue);
        } catch (Exception ex) {
            Logger.getLogger(PropertiesEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static  <E extends Entity<?, ?, E>> void setValueToEntity(E aEntity, String aPropName, Object aValue) throws Exception {
        String setMethodName = "set" + aPropName.substring(0, 1).toUpperCase();
        if (aPropName.length() > 1) {
            setMethodName += aPropName.substring(1);
        }
        Method method = aEntity.getClass().getMethod(setMethodName, Object.class);
        method.invoke(aEntity, aValue);
    }

    public static <E extends Entity<?, ?, E>> Object getValueFromEntity(E aEntity, String aPropName) throws Exception {
        String setMethodName = "get" + aPropName.substring(0, 1).toUpperCase();
        if (aPropName.length() > 1) {
            setMethodName += aPropName.substring(1);
        }
        Method method = aEntity.getClass().getMethod(setMethodName, Object.class);
        return method.invoke(aEntity);
    }

    @Override
    public boolean isSignificant() {
        return (beforeValue != null && !beforeValue.equals(afterValue))
                || (beforeValue == null && afterValue != null);
    }
}
