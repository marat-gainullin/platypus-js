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

import com.bearsoft.org.netbeans.modules.form.ComponentReference;
import com.bearsoft.org.netbeans.modules.form.FormCookie;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.NamedPropertyEditor;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADComponent.ComponentRef;
import com.eas.client.forms.containers.ButtonGroup;
import java.beans.*;
import java.util.*;
import javax.swing.JPopupMenu;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;
import org.openide.nodes.PropertyEditorRegistration;
import org.openide.util.NbBundle;

/**
 * Property editor allowing to choose a component from all components in form
 * (FormModel). Choice can be restricted to certain bean types.
 *
 * @author Tomas Pavek
 */
@PropertyEditorRegistration(targetType = {javax.swing.JComponent.class, JPopupMenu.class, ButtonGroup.class})
public class ComponentChooserEditor extends PropertyEditorSupport implements ExPropertyEditor, NamedPropertyEditor {

    private FormModel formModel;
    private FormProperty<?> prop;
    private final List<RADComponent<?>> components = new ArrayList<>();
    
    public ComponentChooserEditor() {
        super();
    }

    @Override
    public void setValue(Object aValue) {
        if (aValue == null || aValue instanceof ComponentRef) {
            super.setValue((ComponentRef<?>) aValue);
        } else if (aValue instanceof RADComponent<?>) {
            super.setValue(new ComponentRef<>((RADComponent<?>) aValue, formModel));
        } else if (aValue instanceof String) {
            super.setValue(new ComponentRef<>((String) aValue, formModel));
        } else {
            super.setValue(null);
        }
    }

    @Override
    public String[] getTags() {
        List<RADComponent<?>> compList = getComponents();
        String[] names = new String[compList.size()];
        for (int i = 0; i < compList.size(); i++) {
            names[i] = compList.get(i).getName();
        }
        Arrays.sort(names);
        return names;
    }

    @Override
    public String getAsText() {
        ComponentRef<?> val = (ComponentRef<?>)super.getValue();
        return val != null && val.getComponent() != null ? val.getComponent().getName() : "";
    }

    @Override
    public void setAsText(String aValue) {
        if (aValue == null || aValue.equals("")) {
            setValue(null);
        } else {
            RADComponent<?> comp = formModel.getRADComponent(aValue);
            if (comp != null) {
                setValue(new ComponentRef<>(aValue, formModel));
            } else {
                setValue(null);
            }
        }
    }

    @Override
    public String getJavaInitializationString() {
        ComponentRef<?> val = (ComponentRef<?>)super.getValue();
        return val != null ? val.getJavaInitString() : null;
    }

    @Override
    public void attachEnv(PropertyEnv aEnv) {
        aEnv.getFeatureDescriptor().setValue("canEditAsText", Boolean.TRUE); // NOI18N
        Object bean = aEnv.getBeans()[0];
        if (bean instanceof Node) {
            Node node = (Node) bean;
            FormCookie formCookie = node.getLookup().lookup(FormCookie.class);
            if (formCookie != null && aEnv.getFeatureDescriptor() instanceof FormProperty<?>) {
                formModel = formCookie.getFormModel();
                prop = (FormProperty<?>) aEnv.getFeatureDescriptor();
            }
        }
    }

    public FormModel getFormModel() {
        return formModel;
    }

    protected List<RADComponent<?>> getComponents() {
        components.clear();
        if (formModel != null) {
            formModel.getAllComponents().stream().filter((radComp) -> (acceptBean(radComp))).forEach((radComp) -> {
                components.add(radComp);
            });
        }
        return components;
    }

    protected boolean acceptBean(RADComponent<?> comp) {
        return prop.getValueType().isAssignableFrom((Class<?>) comp.getBeanClass());
    }

    // NamedPropertyEditor implementation
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "CTL_ComponentChooserEditor_DisplayName"); // NOI18N
    }
}
