/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package com.bearsoft.org.netbeans.modules.form;

import java.awt.*;
import java.beans.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.security.*;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 * A multiplexing PropertyEditor used in the form editor. It allows multiple
 * editors to be used with one currently selected.
 *
 * @author Ian Formanek
 */
public class FormPropertyEditor<T> implements PropertyEditor,
        PropertyChangeListener,
        ExPropertyEditor {

    private T value = null;
    private boolean valueEdited;
    private FormProperty<T> property;
    private WeakReference<PropertyEnv> propertyEnv;
    private PropertyEditor[] allEditors;
    private PropertyEditor currentEditor;
    private PropertyChangeSupport changeSupport;

    /**
     * Crates a new FormPropertyEditor
     */
    FormPropertyEditor(FormProperty<T> aProperty) throws IllegalAccessException, InvocationTargetException {
        property = aProperty;
        value = aProperty.getValue();
    }

    Class<?> getPropertyType() {
        return property.getValueType();
    }

    FormProperty<T> getProperty() {
        return property;
    }

    FormPropertyContext getPropertyContext() {
        return property.getPropertyContext();
    }

    PropertyEnv getPropertyEnv() {
        return propertyEnv != null ? propertyEnv.get() : null;
    }

    PropertyEditor getCurrentEditor() {
        if (currentEditor == null) {
            getAllEditors();
            setCurrentEditor(allEditors[0]);
        }
        return currentEditor;
    }

    public void setCurrentEditor(PropertyEditor aEditor) {
        if (currentEditor != null) {
            currentEditor.removePropertyChangeListener(this);
        }
        currentEditor = aEditor;
        if (currentEditor != null) {
            currentEditor.addPropertyChangeListener(this);
        }
    }

    // -----------------------------------------------------------------------------
    // PropertyChangeListener implementation
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        PropertyEditor prEd = getCurrentEditor();
        if (prEd != null) {
            value = (T) prEd.getValue();
            valueEdited = false;
        }
        // we run this as privileged to avoid security problems - because
        // the property change can be fired from untrusted property editor code
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                FormPropertyEditor.this.firePropertyChange();
                return null;
            }
        });
    }

    // -----------------------------------------------------------------------------
    // PropertyEditor implementation
    /**
     * Set(or change) the object that is to be edited.
     *
     * @param newValue The new target object to be edited. Note that this object
     * should not be modified by the PropertyEditor, rather the PropertyEditor
     * should create a new object to hold any modified value.
     */
    @Override
    public void setValue(Object newValue) {
        value = (T) newValue;
        valueEdited = false;

        PropertyEditor prEd = getCurrentEditor();
        if (prEd != null) {
            prEd.setValue(value);
        }
    }

    void setEditedValue(T newValue) {
        value = newValue;
        // the value comes from custom editing where the selected editor can be
        // different at this moment than the current editor of the edited property
        valueEdited = true;
        firePropertyChange();
    }

    /**
     * Gets the value of the property.
     *
     * @return The value of the property.
     */
    @Override
    public T getValue() {
        if (!valueEdited) {
            PropertyEditor prEd = getCurrentEditor();
            if (prEd != null) {
                return (T) prEd.getValue();
            }
        }
        return value;
    }

    // -----------------------------------------------------------------------------
    /**
     * Determines whether the class will honor the painValue method.
     *
     * @return True if the class will honor the paintValue method.
     */
    @Override
    public boolean isPaintable() {
        PropertyEditor prEd = getCurrentEditor();
        return prEd != null ? prEd.isPaintable() : false;
    }

    /**
     * Paint a representation of the value into a given area of screen real
     * estate. Note that the propertyEditor is responsible for doing its own
     * clipping so that it fits into the given rectangle. <p> If the
     * PropertyEditor doesn't honor paint requests(see isPaintable) this method
     * should be a silent noop.
     *
     * @param gfx Graphics object to paint into.
     * @param box Rectangle within graphics object into which we should paint.
     */
    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        PropertyEditor prEd = getCurrentEditor();
        if (prEd != null) {
            prEd.paintValue(gfx, box);
        }
    }

    // -----------------------------------------------------------------------------
    /**
     * This method is intended for use when generating Java code to set the
     * value of the property. It should return a fragment of Java code that can
     * be used to initialize a variable with the current property value. <p>
     * Example results are "2", "new Color(127,127,34)", "Color.orange", etc.
     *
     * @return A fragment of Java code representing an initializer for the
     * current value.
     */
    @Override
    public String getJavaInitializationString() {
        PropertyEditor prEd = getCurrentEditor();
        return prEd != null ? prEd.getJavaInitializationString() : null;
    }

    // -----------------------------------------------------------------------------
    /**
     * Gets the property value as a string suitable for presentation to a human
     * to edit.
     *
     * @return The property value as a string suitable for presentation to a
     * human to edit. <p> Returns "null" is the value can't be expressed as a
     * string. <p> If a non-null value is returned, then the PropertyEditor
     * should be prepared to parse that string back in setAsText().
     */
    @Override
    public String getAsText() {
        /*
         if (value == BeanSupport.NO_VALUE) {
         if (NO_VALUE_TEXT == null) {
         NO_VALUE_TEXT = FormUtils.getBundleString("CTL_ValueNotSet"); // NOI18N
         }
         return NO_VALUE_TEXT;
         }
         */
        PropertyEditor prEd = getCurrentEditor();
        return prEd != null ? prEd.getAsText() : null;
    }

    /**
     * Sets the property value by parsing a given String. May raise
     * java.lang.IllegalArgumentException if either the String is badly
     * formatted or if this kind of property can't be expressed as text.
     *
     * @param text The string to be parsed.
     * @throws java.lang.IllegalArgumentException when the specified text does
     * not represent valid value.
     */
    @Override
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        PropertyEditor prEd = getCurrentEditor();
        if (prEd != null) {
            prEd.setAsText(text);
            setValue(prEd.getValue());
        }
    }

    // -----------------------------------------------------------------------------
    /**
     * If the property value must be one of a set of known tagged values, then
     * this method should return an array of the tag values. This can be used to
     * represent(for example) enum values. If a PropertyEditor supports tags,
     * then it should support the use of setAsText with a tag value as a way of
     * setting the value.
     *
     * @return The tag values for this property. May be null if this property
     * cannot be represented as a tagged value.
     *
     */
    @Override
    public String[] getTags() {
        PropertyEditor prEd = getCurrentEditor();
        return prEd != null ? prEd.getTags() : null;
    }

    // -----------------------------------------------------------------------------
    /**
     * A PropertyEditor may chose to make available a full custom Component that
     * edits its property value. It is the responsibility of the PropertyEditor
     * to hook itself up to its editor Component itself and to report property
     * value changes by firing a PropertyChange event. <P> The higher-level code
     * that calls getCustomEditor may either embed the Component in some larger
     * property sheet, or it may put it in its own individual dialog, or ...
     *
     * @return A java.awt.Component that will allow a human to directly edit the
     * current property value. May be null if this is not supported.
     */
    @Override
    public Component getCustomEditor() {
        Component customEditor;
        PropertyEditor prEd = getCurrentEditor();
        if (prEd != null && prEd.supportsCustomEditor()) {
            customEditor = prEd.getCustomEditor();
            if (customEditor instanceof Window) {
                return customEditor;
            }
        } else {
            customEditor = null;
        }
        return new FormCustomEditor(this, customEditor);
    }

    /**
     * Determines whether the propertyEditor can provide a custom editor.
     *
     * @return True if the propertyEditor can provide a custom editor.
     */
    @Override
    public boolean supportsCustomEditor() {
        PropertyEditor[] editors = getAllEditors();
        if (!property.canWrite()) { // read only property
            for (int i = 0; i < editors.length; i++) {
                if (editors[i].supportsCustomEditor()) {
                    return true;
                }
            }
            return false;
        }
        // writable property
        if (editors.length > 1) {
            return true; // we must  at least allow to choose the editor
        }
        if (editors.length == 1) {
            return editors[0].supportsCustomEditor();
        }
        return false;
    }

    synchronized PropertyEditor[] getAllEditors() {
        if (allEditors == null) {
            allEditors = FormPropertyEditorManager.getAllEditors(property);
            for (PropertyEditor editor : allEditors) {
                if (editor instanceof FormAwareEditor) {
                    ((FormAwareEditor) editor).setContext(property.getPropertyContext().getFormModel(), property);
                }
            }
        }
        return allEditors;
    }

    /**
     * Register a listener for the PropertyChange event. The class will fire a
     * PropertyChange value whenever the value is updated.
     *
     * @param l An object to be invoked when a PropertyChange event is fired.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        synchronized (this) {
            if (changeSupport == null) {
                changeSupport = new PropertyChangeSupport(this);
            }
        }
        changeSupport.addPropertyChangeListener(l);
    }

    /**
     * Remove a listener for the PropertyChange event.
     *
     * @param l The PropertyChange listener to be removed.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(l);
        }
    }

    /**
     * Report that we have been modified to any interested listeners.
     */
    void firePropertyChange() {
        if (changeSupport != null) {
            changeSupport.firePropertyChange(null, null, null);
        }
    }

    // -------------
    // ExPropertyEditor implementation
    /**
     * This method is called by the IDE to pass the environment to the property
     * editor.
     *
     * @param env environment.
     */
    @Override
    public void attachEnv(PropertyEnv env) {
        propertyEnv = new WeakReference<>(env);
        PropertyEditor prEd = getCurrentEditor();
        if (prEd instanceof ExPropertyEditor) {
            ((ExPropertyEditor) prEd).attachEnv(env);
        }
    }

    // ---------
    // delegating hashCode() and equals(Object) methods to modifiedEditor - for
    // PropertyPanel mapping property editors to PropertyEnv
    @Override
    public int hashCode() {
        PropertyEditor prEd = getCurrentEditor();
        return prEd != null ? prEd.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null ? hashCode() == obj.hashCode() : false;
    }
}
