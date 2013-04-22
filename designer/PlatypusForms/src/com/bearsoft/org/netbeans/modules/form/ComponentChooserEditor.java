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
import java.util.*;
import org.openide.util.NbBundle;

/**
 * Property editor allowing to choose a component from all components in form
 * (FormModel). Choice can be restricted to certain bean types.
 *
 * @author Tomas Pavek
 */
public class ComponentChooserEditor implements PropertyEditor,
        FormAwareEditor,
        NamedPropertyEditor {

    public static final int ALL_COMPONENTS = 0;
    public static final int NONVISUAL_COMPONENTS = 3;
    private static final String NULL_REF_NAME = "null"; // NOI18N
    private static final String INVALID_REF_NAME = "default"; // NOI18N
    private static String noneText = null;
    private static String invalidText = null;
    private static String defaultText = null;
    private FormModel formModel;
    private List<RADComponent<?>> components = new ArrayList<>();
    private Class<?>[] beanTypes = null;
    private int componentCategory = 0;
    private ComponentRef<?> value;
    private PropertyChangeSupport changeSupport;

    public ComponentChooserEditor() {
    }

    public ComponentChooserEditor(Class<?>[] componentTypes) {
        beanTypes = componentTypes;
    }

    // --------------
    // PropertyEditor implementation
    @Override
    public void setValue(Object aValue) {
        if (aValue == null || aValue instanceof ComponentRef) {
            value = (ComponentRef<?>) aValue;
        } else if (aValue instanceof RADComponent<?>) {
            value = new ComponentRef<>((RADComponent<?>) aValue, formModel);
        } else if (aValue instanceof String) {
            value = new ComponentRef<>((String) aValue, formModel);
        } else {
            value = null;
        }
        firePropertyChange();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String[] getTags() {
        List<RADComponent<?>> compList = getComponents();
        int extraValues = 0;
        int count = 0;
        String[] names;

        if (isDefaultValue()) {
            extraValues = 2;
            count = compList.size() + extraValues;
            names = new String[count];
            names[0] = defaultString();
        } else {
            extraValues = 1;
            count = compList.size() + extraValues;
            names = new String[count];
        }
        names[extraValues - 1] = noneString();

        if (count > extraValues) {
            for (int i = extraValues; i < count; i++) {
                names[i] = compList.get(i - extraValues).getName();
            }
            Arrays.sort(names, 1, count);
        }

        return names;
    }

    private boolean isDefaultValue() {
        return getValue() == null;
    }

    @Override
    public String getAsText() {
        if (isDefaultValue()) {
            return defaultString();
        }
        if (value == null) {
            return noneString();
        }
        if (value.getComponent() == null) {
            return invalidString();
        }
        return value.getComponent().getName();
    }

    @Override
    public void setAsText(String str) {
        if (str == null || str.equals("") || str.equals(noneString())) // NOI18N
        {
            setValue(null);
        } else {
            if (defaultString().equals(str)) {
                setValue(null);
            } else {
                setValue(new ComponentRef<>(str, formModel));
            }
        }
    }

    @Override
    public String getJavaInitializationString() {
        return value != null ? value.getJavaInitString() : null;
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
        if (changeSupport == null) {
            changeSupport = new PropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(l);
        }
    }

    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    }

    @Override
    public java.awt.Component getCustomEditor() {
        return null;
    }

    @Override
    public boolean supportsCustomEditor() {
        return false;
    }

    // ----------------
    // FormAwareEditor implementation
    @Override
    public void setContext(FormModel model, FormProperty<?> prop) {
        formModel = model;
    }

    public FormModel getFormModel() {
        return formModel;
    }

    public void setBeanTypes(Class<?>[] types) {
        beanTypes = types;
    }

    public Class<?>[] getBeanTypes() {
        return beanTypes;
    }

    public void setComponentCategory(int cat) {
        componentCategory = cat;
    }

    public int getComponentCategory() {
        return componentCategory;
    }
    // ---------

    protected List<RADComponent<?>> getComponents() {
        components.clear();
        if (formModel != null) {
            Collection<RADComponent<?>> comps;
            if (componentCategory == NONVISUAL_COMPONENTS) {
                comps = formModel.getNonVisualComponents();
            } else {
                comps = formModel.getAllComponents();
            }

            for (RADComponent<?> radComp : comps) {
                if (acceptBean(radComp)) {
                    components.add(radComp);
                }
            }
        }
        return components;
    }

    protected boolean acceptBean(RADComponent<?> comp) {
        if (beanTypes == null) {
            return true;
        }
        boolean match = false;
        for (int i = 0; i < beanTypes.length && !match; i++) {
            match = beanTypes[i].isAssignableFrom(comp.getBeanClass());
        }
        return match;
    }

    protected String noneString() {
        if (noneText == null) {
            noneText = FormUtils.getBundleString("CTL_NoComponent"); // NOI18N
        }
        return noneText;
    }

    protected String defaultString() {
        if (defaultText == null) {
            defaultText = FormUtils.getBundleString("CTL_DefaultComponent"); // NOI18N
        }
        return defaultText;
    }

    protected String invalidString() {
        if (invalidText == null) {
            invalidText = FormUtils.getBundleString("CTL_InvalidReference"); // NOI18N
        }
        return invalidText;
    }

    // ------
    protected final void firePropertyChange() {
        if (changeSupport != null) {
            changeSupport.firePropertyChange(null, null, null);
        }
    }

    // NamedPropertyEditor implementation
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "CTL_ComponentChooserEditor_DisplayName"); // NOI18N
    }

    // ------------
    public static class ComponentRef<C> implements ComponentReference<C> {

        private FormModel formModel;
        private String componentName;
        private RADComponent<C> component;

        public ComponentRef(String name, FormModel aFormModel) {
            super();
            componentName = name;
            formModel = aFormModel;
        }

        ComponentRef(RADComponent<C> radComp, FormModel aFormModel) {
            super();
            componentName = radComp.getName();
            component = radComp;
            formModel = aFormModel;
        }

        @Override
        public boolean equals(Object obj) {
            boolean equal;

            if (obj instanceof ComponentRef<?>) {
                ComponentRef<C> ref = (ComponentRef<C>) obj;

                equal = (ref.component == component);
                if (componentName == null) {
                    equal = equal && (ref.componentName == null);
                } else {
                    equal = equal && componentName.equals(ref.componentName);
                }
            } else {
                equal = (obj instanceof RADComponent<?> && obj == component);
            }

            return equal;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + (this.componentName != null ? this.componentName.hashCode() : 0);
            hash = 89 * hash + (this.component != null ? this.component.hashCode() : 0);
            return hash;
        }

        String getJavaInitString() {
            checkComponent();

            if (component != null) {
                if (component == component.getFormModel().getTopRADComponent()) {
                    return "this"; // NOI18N
                }
            } else if (!NULL_REF_NAME.equals(componentName)) {
                return null; // invalid reference
            }
            return componentName;
        }

        @Override
        public RADComponent<C> getComponent() {
            checkComponent();
            return component;
        }

        private void checkComponent() {
            if (component == null
                    && !NULL_REF_NAME.equals(componentName)
                    && !INVALID_REF_NAME.equals(componentName)) {
                component = (RADComponent<C>)formModel.getRADComponent(componentName);
            } else if (component != null) {
                if (!component.isInModel()) {
                    component = null;
                } else {
                    componentName = component.getName();
                }
            }
        }
    }
}
