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
import java.lang.reflect.*;

/**
 * Implementation of properties for (meta)components (class RADComponent).
 * RADComponent is used to get the component instance and PropertyDescriptor
 * provides read and write methods to get and set property values.
 *
 * @author Tomas Pavek
 * @param <T>
 */
public class RADProperty<T> extends FormProperty<T> {

    private final RADComponent<?> component;
    private final PropertyDescriptor desc;
    private T defaultValue;

    public RADProperty(RADComponent<?> aRadComp, PropertyDescriptor propdesc) throws IllegalAccessException, InvocationTargetException {
        super(propdesc.getName(),
                (Class<T>) propdesc.getPropertyType(),
                propdesc.getDisplayName(),
                propdesc.getShortDescription());

        component = aRadComp;
        desc = propdesc;

        if (desc.getWriteMethod() == null && !isDetachedWrite()) {
            setAccessType(NO_WRITE);
        } else if (desc.getReadMethod() == null && !isDetachedRead()) {
            setAccessType(DETACHED_READ);
        } // assuming a bean property is at least readable or writeable
        if (!isDetachedRead() && desc.getReadMethod() == null) {
            throw new IllegalAccessException("Not a readable property: " + desc.getName()); // NOI18N
        }
        if (desc.getReadMethod() != null) {
            defaultValue = (T) desc.getReadMethod().invoke(BeanSupport.getDefaultInstance(component.getBeanClass()), new Object[]{});
        }
    }

    public RADComponent<?> getRADComponent() {
        return component;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return desc;
    }

    @Override
    public T getValue() throws IllegalAccessException,
            InvocationTargetException {
        Method readMethod = desc.getReadMethod();
        if (readMethod == null) {
            throw new IllegalAccessException("Not a readable property: " + desc.getName()); // NOI18N
        }
        return (T) readMethod.invoke(component.getBeanInstance(), new Object[0]);
    }

    @Override
    public void setValue(T value) throws IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException {
        Method writeMethod = desc.getWriteMethod();
        if (writeMethod == null) {
            throw new IllegalAccessException("Not a writeable property: " + desc.getName()); // NOI18N
        }

        Object beanInstance = component.getBeanInstance();
        // invoke the setter method
        T oldValue = getValue();
        writeMethod.invoke(beanInstance, new Object[]{value});
        propertyValueChanged(oldValue, value);
    }

    @Override
    public boolean supportsDefaultValue() {
        return true;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean canWrite() {
        return component.isReadOnly() ? false : super.canWrite();
    }

    public RADComponent<?> getComponent() {
        return component;
    }

    // Descriptor for fake-properties (not real, design-time only) that
    // need to pretend they are of certain type although without both
    // getter and setter. Used e.g. by ButtonGroupProperty.
    static class FakePropertyDescriptor extends PropertyDescriptor {

        Class<?> propType;

        FakePropertyDescriptor(String name, Class<?> type) throws IntrospectionException {
            super(name, null, null);
            propType = type;
        }

        @Override
        public Class<?> getPropertyType() {
            return propType;
        }
    }
}
