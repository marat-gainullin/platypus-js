/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util.edits;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @param <T> Type of the property
 * @author pk
 */
public class ModifyBeanPropertyEdit<T> extends AbstractUndoableEdit
{
    private Object bean;
    private String propertyName;
    private T oldValue;
    private T newValue;
    private Method setter;

    public ModifyBeanPropertyEdit(Class<T> clazz, Object bean, String propertyName, T oldValue, T newValue) throws NoSuchMethodException
    {
        this.bean = bean;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.setter = bean.getClass().getMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), clazz);
    }

    @Override
    public void die()
    {
        bean = null;
        propertyName = null;
        oldValue = null;
        newValue = null;
        setter = null;
        super.die();
    }

    @Override
    public void redo() throws CannotRedoException
    {
        try
        {
            super.redo();
            setter.invoke(bean, newValue);
        } catch (IllegalAccessException ex)
        {
            Logger.getLogger(ModifyBeanPropertyEdit.class.getName()).log(Level.SEVERE, null, ex);
            throw new CannotRedoException();
        } catch (IllegalArgumentException ex)
        {
            Logger.getLogger(ModifyBeanPropertyEdit.class.getName()).log(Level.SEVERE, null, ex);
            throw new CannotRedoException();
        } catch (InvocationTargetException ex)
        {
            Logger.getLogger(ModifyBeanPropertyEdit.class.getName()).log(Level.SEVERE, null, ex);
            throw new CannotRedoException();
        }
    }

    @Override
    public void undo() throws CannotUndoException
    {
        try
        {
            super.undo();
            setter.invoke(bean, oldValue);
        } catch (IllegalAccessException ex)
        {
            Logger.getLogger(ModifyBeanPropertyEdit.class.getName()).log(Level.SEVERE, null, ex);
            throw new CannotRedoException();
        } catch (IllegalArgumentException ex)
        {
            Logger.getLogger(ModifyBeanPropertyEdit.class.getName()).log(Level.SEVERE, null, ex);
            throw new CannotRedoException();
        } catch (InvocationTargetException ex)
        {
            Logger.getLogger(ModifyBeanPropertyEdit.class.getName()).log(Level.SEVERE, null, ex);
            throw new CannotRedoException();
        }
    }

    public static void showNoSetterError(final JComponent parent, final String propertyName, final Object bean)
    {
        JOptionPane.showMessageDialog(parent, String.format("Error, no setter for property %s in %s", propertyName, String.valueOf(bean)), "Internal error", JOptionPane.ERROR_MESSAGE);
    }
}
