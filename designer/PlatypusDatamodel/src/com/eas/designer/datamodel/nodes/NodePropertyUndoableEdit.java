/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import java.lang.reflect.Method;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author mg
 */
public class NodePropertyUndoableEdit implements UndoableEdit {

    protected Object subject;
    protected String propertyName;
    protected Object oldValue;
    protected Object newValue;
    protected Method setMethod;
    protected Class<?> argClass;
    protected NodePropertiesUndoRecorder undoRecorder;

    public NodePropertyUndoableEdit(NodePropertiesUndoRecorder aUndoRecorder, Object aSubject, String aPropertyName, Object aOldValue, Object aNewValue) throws Exception {
        super();
        undoRecorder = aUndoRecorder;
        subject = aSubject;
        propertyName = aPropertyName;
        oldValue = aOldValue;
        newValue = aNewValue;

        String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        argClass = aNewValue != null ? aNewValue.getClass() : aOldValue.getClass();

        try {
            setMethod = aSubject.getClass().getMethod(methodName, argClass);
        } catch (NoSuchMethodException ex) {
            if (argClass == Integer.class) {
                argClass = int.class;
            } else if (argClass == Boolean.class) {
                argClass = boolean.class;
            }
            setMethod = aSubject.getClass().getMethod(methodName, argClass);
        }
    }

    public Object getSubject() {
        return subject;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    @Override
    public void undo() throws CannotUndoException {
        try {
            undoRecorder.setUndoing(true);
            try {
                setMethod.invoke(subject, oldValue);
            } finally {
                undoRecorder.setUndoing(false);
            }
        } catch (final Exception ex) {
            throw new CannotUndoException() {
                @Override
                public Throwable getCause() {
                    return ex;
                }
            };
        }
    }

    @Override
    public boolean canUndo() {
        return subject != null;
    }

    @Override
    public void redo() throws CannotRedoException {
        try {
            undoRecorder.setUndoing(true);
            try {
                setMethod.invoke(subject, newValue);
            } finally {
                undoRecorder.setUndoing(false);
            }
        } catch (final Exception ex) {
            throw new CannotUndoException() {
                @Override
                public Throwable getCause() {
                    return ex;
                }
            };
        }
    }

    @Override
    public boolean canRedo() {
        return subject != null;
    }

    @Override
    public void die() {
        subject = null;
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return subject != null && propertyName != null && !propertyName.isEmpty();
    }

    @Override
    public String getPresentationName() {
        return propertyName;
    }

    @Override
    public String getUndoPresentationName() {
        return propertyName;
    }

    @Override
    public String getRedoPresentationName() {
        return propertyName;
    }
}
