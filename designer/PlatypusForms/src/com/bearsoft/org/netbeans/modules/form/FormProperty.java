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

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * This class provides basic implementation of properties used in form module
 * which are generated in the java code. FormProperty can use multiple property
 * editors (via FormPropertyEditor) and special "design values" (holding some
 * additional data - FormDesignValue implementations).
 *
 * FormProperty is an "interface" object that provides general access to one
 * property of some other object (called "target object"). To make it work, only
 * some connection to the target object must be implemented. There are two
 * (abstract) methods for this purpose in FormProperty class: public Object
 * getTargetValue(); public void setTargetValue(Object value);
 *
 * NOTE: Binding to target object can be switched off for reading or writing by
 * setting access type of property to DETACHED_READ or DETACHED_WRITE.
 *
 * There are some further methods (potentially suitable) for custom
 * implementation (overriding the default implementation): public boolean
 * supportsDefaultValue(); public Object getDefaultValue(); public
 * PropertyEditor getExpliciteEditor();
 *
 * NOTE: Properties are created for nodes and presented in property sheet. Node
 * object that owns properties should listen to the CURRENT_EDITOR property
 * change on each property and call firePropertySetsChange(...) to notify the
 * sheet about changing current property editor of a property.
 *
 * @author Tomas Pavek
 * @param <T>
 */
public abstract class FormProperty<T> extends Node.Property<T> {

    // --------------------
    // constants
    public static final String PROP_VALUE = "propertyValue"; // NOI18N
    // "Access type" of the property (in relation to the target object).
    // There are three levels of restriction possible:
    //   NORMAL_RW - no restriction on both property and target object
    //   DETACHED_READ, DETACHED_WRITE - no reading or writing on the target
    //       object (it is "detached"; the value is cached by the property)
    //   NO_READ, NO_WRITE - it is not possible to perform read or write on
    //       the property (so neither on the target object)
    public static final int NORMAL_RW = 0;
    public static final int DETACHED_READ = 1; // no reading from target (bit 0)
    public static final int DETACHED_WRITE = 2; // no writing to target (bit 1)
    private static final int NO_READ_PROP = 4; // bit 2
    private static final int NO_WRITE_PROP = 8; // bit 3
    public static final int NO_READ = DETACHED_READ | NO_READ_PROP; // no reading from property (bits 0,2)
    public static final int NO_WRITE = DETACHED_WRITE | NO_WRITE_PROP; // no writing to property (bits 1,3)
    public static final String CHANGE_IMMEDIATE_PROP_NAME = "changeImmediate";
    public static final String NULL_VALUE_PROP_NAME = "nullValue";
    // ------------------------
    // variables
    protected int accessType = NORMAL_RW;
    private PropertyChangeSupport changeSupport;
    private VetoableChangeSupport vetoableChangeSupport;
    private boolean fireChanges = true;
    protected Boolean settedToDefault;

    // ---------------------------
    // constructors
    protected FormProperty(String name, Class<T> type,
            String displayName, String shortDescription) {
        super(type);
        setValue(CHANGE_IMMEDIATE_PROP_NAME, Boolean.FALSE); // NOI18N
        setValue(NULL_VALUE_PROP_NAME, "");// TODO: Due to error in NetBeans StringEditor, we have to tune here. Tuning is bad, because of emtyText absence. String literal "nullValue" is used because of ObjectEditor.PROP_NULL is inaccessible.
        setName(name);
        setDisplayName(displayName);
        setShortDescription(getDescriptionWithType(shortDescription, getValueType()));
        setHidden("name".equals(name));
    }

    protected FormProperty(Class<T> type) {
        super(type);
        setValue(CHANGE_IMMEDIATE_PROP_NAME, Boolean.FALSE); // NOI18N
    }

    static String getDescriptionWithType(String description, Class<?> valueType) {
        String type = org.openide.util.Utilities.getClassName(valueType);
        return description == null
                ? FormUtils.getFormattedBundleString("HINT_PropertyType", // NOI18N
                        new Object[]{type})
                : FormUtils.getFormattedBundleString("HINT_PropertyTypeWithDescription", // NOI18N
                        new Object[]{type, description});
    }

    // ----------------------------------------
    // getter, setter & related methods
    @Override
    public String getHtmlDisplayName() {
        if (!isDefaultValue()) {
            return "<b>" + getDisplayName(); // NOI18N
        } else {
            return null;
        }
    }

    @Override
    public boolean canRead() {
        return (accessType & NO_READ_PROP) == 0;
    }

    public boolean isDetachedRead() {
        return (accessType & DETACHED_READ) == 0;
    }

    public boolean isDetachedWrite() {
        return (accessType & DETACHED_WRITE) == 0;
    }

    @Override
    public boolean canWrite() {
        return (accessType & NO_WRITE_PROP) == 0;
    }

    public int getAccessType() {
        return accessType;
    }

    public void setAccessType(int aValue) {
        accessType = aValue;
    }

    /**
     * Returns whether this property has a default value (false by default). If
     * any subclass provides default value, it should override this and
     * getDefaultValue() methods.
     *
     * @return true if there is a default value, false otherwise
     */
    @Override
    public boolean supportsDefaultValue() {
        return false;
    }

    @Override
    public final boolean isDefaultValue() {
        try {
            if (settedToDefault == null) {
                settedToDefault = supportsDefaultValue() ? Objects.equals(getValue(), getDefaultValue()) : false;
            }
            return settedToDefault;
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    /**
     * Returns a default value of this property. If any subclass provides
     * default value, it should override this and supportsDefaultValue()
     * methods.
     *
     * @return the default value (null by default :)
     */
    public T getDefaultValue() {
        return null;
    }

    /**
     * Restores the property to its default value.
     *
     * @throws java.lang.IllegalAccessException when there is an access problem.
     * @throws java.lang.reflect.InvocationTargetException when there is an
     * invocation problem.
     */
    @Override
    public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
        T oldValue = null;
        T defValue = getDefaultValue();

        if (canRead()) {
            try {  // get the old value (still the current)
                oldValue = getValue();
            } catch (Exception e) {
                // no problem -> keep null
            }
        }
        if (!Objects.deepEquals(defValue, oldValue)) {
            setValue(defValue);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        synchronized (this) {
            if (changeSupport == null) {
                changeSupport = new PropertyChangeSupport(this);
            }
        }
        changeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(l);
        }
    }

    public void addVetoableChangeListener(VetoableChangeListener l) {
        synchronized (this) {
            if (vetoableChangeSupport == null) {
                vetoableChangeSupport = new VetoableChangeSupport(this);
            }
        }
        vetoableChangeSupport.addVetoableChangeListener(l);
    }

    public void removeVetoableChangeListener(VetoableChangeListener l) {
        if (vetoableChangeSupport != null) {
            vetoableChangeSupport.removeVetoableChangeListener(l);
        }
    }

    public boolean isChangeFiring() {
        return fireChanges;
    }

    public void setChangeFiring(boolean fire) {
        fireChanges = fire;
    }

    protected final void propertyValueChanged(T old, T current) {
        settedToDefault = null;// invalidate default state
        if (fireChanges) {
            try {
                firePropertyChange(PROP_VALUE, old, current);
            } catch (PropertyVetoException ex) {
                boolean oldFireChanges = fireChanges;
                fireChanges = false;
                try {
                    setValue(old);
                } catch (Exception ex2) {
                    // ignore
                }
                fireChanges = oldFireChanges;
            }
        }
    }

    private void firePropertyChange(String propName, T old, T current)
            throws PropertyVetoException {
        if (vetoableChangeSupport != null) {
            vetoableChangeSupport.fireVetoableChange(propName, old, current);
        }
        if (changeSupport != null) {
            changeSupport.firePropertyChange(propName, old, current);
        }
    }

    /**
     * Gets the java code initializing the property value. It is obtained from
     * current property editor. Example: "Button 1"
     *
     * @return initialization string.
     */
    public String getJavaInitializationString() {
        try {
            Object value = getValue();
            if (value == null) {
                return "null"; // NOI18N
            }
            if (value == BeanSupport.NO_VALUE) {
                return null;
            }

            PropertyEditor ed = getPropertyEditor();
            if (ed == null) {
                return null;
            }
            if (ed.getValue() != value) {
                ed.setValue(value);
            }
            return ed.getJavaInitializationString();
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
        }
        return null;
    }
}
