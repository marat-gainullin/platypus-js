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
package com.bearsoft.org.netbeans.modules.form.layoutsupport;

import com.bearsoft.org.netbeans.modules.form.*;
import com.bearsoft.org.netbeans.modules.form.actions.SelectLayoutAction;
import java.awt.*;
import java.beans.*;
import java.security.*;
import java.util.*;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;

/**
 * @author Tomas Pavek
 */
public class LayoutNode extends FormNode
        implements RADComponentCookie, FormPropertyCookie {

    private LayoutSupportManager layoutSupport;
    private Action[] layoutActions;

    public LayoutNode(RADVisualContainer<?> cont) {
        super(Children.LEAF, cont.getFormModel());
        layoutSupport = cont.getLayoutSupport();
        setName(layoutSupport.getDisplayName());
        cont.setLayoutNodeReference(this);
    }

    // RADComponentCookie
    @Override
    public RADComponent<?> getRADComponent() {
        return layoutSupport.getRadContainer();
    }

    // FormPropertyCookie
    @Override
    public FormProperty<?> getProperty(String name) {
        FormProperty<?> prop = layoutSupport.getLayoutProperty(name);
        return prop instanceof FormProperty<?> ? (FormProperty<?>) prop : null;
    }

    public void fireLayoutPropertiesChange() {
        firePropertyChange(null, null, null);
    }

    public void fireLayoutPropertySetsChange() {
        firePropertySetsChange(null, null);
    }

    @Override
    public Image getIcon(int iconType) {
        return layoutSupport.getIcon(iconType);
    }

    @Override
    public Node.PropertySet[] getPropertySets() {
        return layoutSupport.getPropertySets();
    }

    @Override
    public Action[] getActions(boolean context) {
        if (layoutActions == null) { // from AbstractNode
            java.util.List<Action> actionsList = new ArrayList<>(10);
            if (!layoutSupport.getRadContainer().isReadOnly()) {
                actionsList.add(SystemAction.get(SelectLayoutAction.class));
                actionsList.add(null);
            }
            actionsList.addAll(Arrays.asList(super.getActions(context)));
            layoutActions = actionsList.toArray(new Action[]{});
        }
        return layoutActions;
    }

    @Override
    public boolean hasCustomizer() {
        return !layoutSupport.getRadContainer().isReadOnly()
                && layoutSupport.getCustomizerClass() != null;
    }

    @Override
    protected Component createCustomizer() {
        Class<?> customizerClass = layoutSupport.getCustomizerClass();
        if (customizerClass == null) {
            return null;
        }

        Component supportCustomizer = layoutSupport.getSupportCustomizer();
        if (supportCustomizer != null) {
            return supportCustomizer;
        }

        // create bean customizer for layout manager
        Object customizerObject;
        try {
            customizerObject = customizerClass.newInstance();
        } catch (InstantiationException e) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            return null;
        } catch (IllegalAccessException e) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            return null;
        }

        if (customizerObject instanceof Component
                && customizerObject instanceof Customizer) {
            Customizer customizer = (Customizer) customizerObject;
            customizer.setObject(
                    layoutSupport.getPrimaryContainerDelegate().getLayout());

            customizer.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    FormProperty<?>[] properties;
                    if (evt.getPropertyName() != null) {
                        FormProperty<?> changedProperty =
                                layoutSupport.getLayoutProperty(evt.getPropertyName());
                        if (changedProperty != null) {
                            properties = new FormProperty<?>[]{changedProperty};
                        } else {
                            return; // non-existing property?
                        }
                    } else {
                        properties = layoutSupport.getAllProperties();
                        evt = null;
                    }

                    updatePropertiesFromCustomizer(properties, evt);
                }
            });

            return (Component) customizer;
        }

        return null;
    }

    private void updatePropertiesFromCustomizer(
            final FormProperty<?>[] properties,
            final PropertyChangeEvent evt) {
        // just for sure we run this as privileged to avoid security problems,
        // the property change can be fired from untrusted bean customizer code
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    PropertyChangeEvent ev = evt == null ? null
                            : new PropertyChangeEvent(
                            layoutSupport.getLayoutDelegate(),
                            evt.getPropertyName(),
                            evt.getOldValue(), evt.getNewValue());
                    if (ev != null) {
                        layoutSupport.containerLayoutChanged(ev);
                    }

                    if (ev == null) // anonymous property changed
                    {
                        layoutSupport.containerLayoutChanged(null);
                    }
                    // [but this probably won't do anything...]
                } catch (PropertyVetoException ex) {
                    // the change is not accepted, but what can we do here?
                    // java.beans.Customizer has no veto capabilities
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
                return null;
            }
        });
    }
}
