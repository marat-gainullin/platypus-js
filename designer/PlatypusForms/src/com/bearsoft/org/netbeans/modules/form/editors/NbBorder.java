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
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.org.netbeans.modules.form.*;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor.NbImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.Icon;
import javax.swing.border.*;
import org.openide.ErrorManager;
import org.openide.nodes.*;

/**
 * A support class holding metadata for borders (javax.swing.border.Border),
 * similar to RADComponent.
 *
 * @author Tomas Pavek
 */
public class NbBorder {

    private Border theBorder;
    private FormPropertyContext propertyContext = null;
    private FormProperty<?>[] properties = null;
    private Map<String, FormProperty<?>> propertiesMap = null;
    // -------------------------
    // constructors

    public NbBorder(Class<? extends Border> borderClass) throws Exception {
        super();
        theBorder = (Border) BeanSupport.createBeanInstance(borderClass);
    }

    public NbBorder(Border border) {
        setBorder(border);
    }

    public NbBorder(NbBorder aNbBorder, FormPropertyContext propertyContext)
            throws Exception {
        this(aNbBorder.getBorderClass());
        setPropertyContext(propertyContext);
        createProperties();
        int copyMode = FormUtils.CHANGED_ONLY | FormUtils.DISABLE_CHANGE_FIRING;
        FormUtils.copyProperties(
                aNbBorder.getProperties(),
                properties, copyMode);
    }

    public NbBorder copy(FormProperty<?> formProperty) {
        try {
            return new NbBorder(this, new FormPropertyContext.SubProperty(formProperty));
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        return null;
    }

    public final void setBorder(Border border) {
        theBorder = border;
    }

    public Class<? extends Border> getBorderClass() {
        return theBorder.getClass();
    }

    public String getDisplayName() {
        return org.openide.util.Utilities.getShortClassName(theBorder.getClass());
    }

    /**
     * Sets FormPropertyContext for properties. This should be called before
     * properties are created or used after property context had changed.
     */
    public final void setPropertyContext(FormPropertyContext propertyContext) {
        if (properties != null && this.propertyContext != propertyContext) {
            for (int i = 0; i < properties.length; i++) {
                if (!properties[i].getValueType().isPrimitive()) {
                    properties[i].setPropertyContext(propertyContext);
                }
            }
        }

        this.propertyContext = propertyContext;
    }

    // FormPropertyContainer implementation
    public FormProperty<?>[] getProperties() {
        if (properties == null) {
            createProperties();
        }
        return properties;
    }

    protected Map<String, FormProperty<?>> getPropertiesMap() {
        if (propertiesMap == null) {
            propertiesMap = new HashMap<>();
            FormProperty<?>[] props = getProperties();
            for (FormProperty<?> prop : props) {
                propertiesMap.put(prop.getName(), prop);
            }
        }
        return propertiesMap;
    }

    public <P extends FormProperty<?>> P getPropertyOfName(String name) {
        return (P) getPropertiesMap().get(name);
    }

    private void createProperties() {
        FormLAF.executeWithLookAndFeel(propertyContext.getFormModel(), new Runnable() {
            @Override
            public void run() {
                createPropertiesInLAFBlock();
            }
        });
    }

    private void createPropertiesInLAFBlock() {
        BeanInfo bInfo;
        try {
            bInfo = FormUtils.getBeanInfo(theBorder.getClass());
        } catch (IntrospectionException ex) {
            return;
        }
        PropertyDescriptor[] props = bInfo.getPropertyDescriptors();

        List<FormProperty<?>> nodeProps = new ArrayList<>();
        for (int i = 0; i < props.length; i++) {
            PropertyDescriptor pd = props[i];
            if (!pd.isHidden()) {
                try {
                    InnerProperty<?> prop = new InnerProperty<>(propertyContext, pd);
                    nodeProps.add(prop);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
        properties = nodeProps.toArray(new FormProperty<?>[]{});
    }

    public String getJavaInitializationString() {
        return theBorder.getClass().getName();
    }

    private Color color(String name) throws IllegalAccessException, InvocationTargetException {
        FormProperty<Color> prop = this.<FormProperty<Color>>getPropertyOfName(name);
        if (prop != null) {
            return prop.getValue();
        } else {
            return null;
        }
    }

    private Icon icon(String name) throws IllegalAccessException, InvocationTargetException {
        FormProperty<?> prop = getPropertyOfName(name);
        if (prop != null) {
            if (prop.getValue() instanceof Icon) {
                return (Icon) prop.getValue();
            } else if (prop.getValue() instanceof NbImageIcon) {
                return ((NbImageIcon) prop.getValue()).getIcon();
            }
        }
        return null;
    }

    private int num(String name) throws IllegalAccessException, InvocationTargetException {
        FormProperty<Number> prop = this.<FormProperty<Number>>getPropertyOfName(name);
        if (prop != null && prop.getValue() != null) {
            return prop.getValue().intValue();
        } else {
            return 0;
        }
    }

    private boolean bool(String name) throws IllegalAccessException, InvocationTargetException {
        FormProperty<Boolean> prop = this.<FormProperty<Boolean>>getPropertyOfName(name);
        if (prop != null && prop.getValue() != null) {
            return prop.getValue().booleanValue();
        } else {
            return false;
        }
    }

    private Font font(String name) throws IllegalAccessException, InvocationTargetException {
        FormProperty<Font> prop = this.<FormProperty<Font>>getPropertyOfName(name);
        if (prop != null && prop.getValue() != null) {
            return prop.getValue();
        } else {
            return null;
        }
    }

    private String string(String name) throws IllegalAccessException, InvocationTargetException {
        FormProperty<String> prop = this.<FormProperty<String>>getPropertyOfName(name);
        if (prop != null && prop.getValue() != null) {
            return prop.getValue();
        } else {
            return null;
        }
    }

    private NbBorder border(String name) throws IllegalAccessException, InvocationTargetException {
        FormProperty<NbBorder> prop = this.<FormProperty<NbBorder>>getPropertyOfName(name);
        if (prop != null && prop.getValue() != null) {
            return prop.getValue();
        } else {
            return null;
        }
    }

    private Insets insets(String name) throws IllegalAccessException, InvocationTargetException {
        FormProperty<Insets> prop = this.<FormProperty<Insets>>getPropertyOfName(name);
        if (prop != null && prop.getValue() != null) {
            return prop.getValue();
        } else {
            return null;
        }
    }

    private static Border border(NbBorder aValue) throws IllegalAccessException, InvocationTargetException {
        if (aValue != null) {
            Class<?> cls = aValue.getBorderClass();
            if (LineBorder.class.isAssignableFrom(cls)) {
                return new LineBorder(aValue.color("lineColor"), aValue.num("thickness"), aValue.bool("roundedCorners"));
            } else if (EtchedBorder.class.isAssignableFrom(cls)) {
                return new EtchedBorder(aValue.num("etchType"),
                        aValue.color("highlightColor"),
                        aValue.color("shadowColor"));
            } else if (TitledBorder.class.isAssignableFrom(cls)) {
                return new TitledBorder(border(aValue.border("border")), aValue.string("title"), aValue.num("titleJustification"), aValue.num("titlePosition"), aValue.font("titleFont"), aValue.color("titleColor"));
            } else if (CompoundBorder.class.isAssignableFrom(cls)) {
                return new CompoundBorder(border(aValue.border("outsideBorder")), border(aValue.border("insideBorder")));
            } else if (MatteBorder.class.isAssignableFrom(cls)) {
                Insets borderInsets = aValue.insets("borderInsets");
                if (aValue.icon("tileIcon") != null) {
                    return new MatteBorder(borderInsets.top, borderInsets.left, borderInsets.bottom, borderInsets.right, aValue.icon("tileIcon"));
                } else {
                    return new MatteBorder(borderInsets.top, borderInsets.left, borderInsets.bottom, borderInsets.right, aValue.color("matteColor"));
                }
            } else if (SoftBevelBorder.class.isAssignableFrom(cls)) {
                return new SoftBevelBorder(aValue.num("bevelType"), aValue.color("highlightOuterColor"), aValue.color("highlightInnerColor"), aValue.color("shadowOuterColor"), aValue.color("shadowInnerColor"));
            } else if (BevelBorder.class.isAssignableFrom(cls)) {
                return new BevelBorder(aValue.num("bevelType"), aValue.color("highlightOuterColor"), aValue.color("highlightInnerColor"), aValue.color("shadowOuterColor"), aValue.color("shadowInnerColor"));
            } else if (EmptyBorder.class.isAssignableFrom(cls)) {
                Insets borderInsets = aValue.insets("borderInsets");
                return new EmptyBorder(borderInsets.top, borderInsets.left, borderInsets.bottom, borderInsets.right);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Border toBorder() throws IllegalAccessException, InvocationTargetException {
        return border(this);
    }
    // -----------------------

    public class InnerProperty<T> extends FormProperty<T> {

        private PropertyDescriptor desc;
        private T value;
        private T defaultValue;

        public InnerProperty(FormPropertyContext propertyContext,
                PropertyDescriptor aDesc) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            super(propertyContext,
                    aDesc.getName(),
                    (Class<T>) aDesc.getPropertyType(),
                    aDesc.getDisplayName(),
                    aDesc.getShortDescription());

            desc = aDesc;

            if (aDesc.getWriteMethod() == null) {
                setAccessType(DETACHED_WRITE);
            } else if (aDesc.getReadMethod() == null) {
                setAccessType(DETACHED_READ);
            }
            if (desc.getReadMethod() != null) {
                defaultValue = (T) desc.getReadMethod().invoke(BeanSupport.getDefaultInstance(getBorderClass()), new Object[]{});
                if (defaultValue instanceof Border) {
                    defaultValue = (T) new NbBorder((Border) defaultValue);
                }
                if (theBorder != null) {
                    value = (T) desc.getReadMethod().invoke(theBorder, new Object[]{});
                    if (value instanceof Border) {
                        value = (T) new NbBorder((Border) value);
                    }
                } else {
                    value = defaultValue;
                }
            }
        }
        protected PropertyEditor editor;

        @Override
        public PropertyEditor getPropertyEditor() {
            if (editor == null) {
                if (desc.getPropertyEditorClass() != null) {
                    try {
                        editor = (PropertyEditor) desc.getPropertyEditorClass().newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
                if (editor == null) {
                    editor = super.getPropertyEditor();
                }
            }
            return editor;
        }

        @Override
        public void setValue(T aValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            T oldValue = getValue();
            value = aValue;
            if (oldValue != value) {
                setChanged(defaultValue != value);
                propertyValueChanged(oldValue, aValue);
            }
        }

        @Override
        public T getValue()
                throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        @Override
        public boolean supportsDefaultValue() {
            return true;
        }

        @Override
        public T getDefaultValue() {
            return defaultValue;
        }

        protected Method getWriteMethod() {
            return desc.getWriteMethod();
        }

        // Issue 73245 explains why is this method overriden
        @Override
        public boolean equals(Object property) {
            return (this == property);
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }
}
