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

import com.bearsoft.org.netbeans.modules.form.layoutsupport.*;
import java.awt.Component;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import org.openide.nodes.*;

/**
 *
 * @author Ian Formanek
 * @param <C>
 */
public class RADVisualComponent<C extends Component> extends RADComponent<C> {

    private final Map<String, LayoutConstraints<?>> constraints = new HashMap<>();
    private FormProperty<?>[] constraintsProperties;
    private ConstraintsListenerConvertor constraintsListener;

    enum MenuType {

        JMenuItem, JCheckBoxMenuItem, JRadioButtonMenuItem,
        JMenu, JPopupMenu, JSeparator
    }

    public Map<String, LayoutConstraints<?>> getConstraints() {
        return constraints;
    }
    // -----------------------------------------------------------------------------
    // Public interface

    /**
     * @return The index of this component within visual components of its
     * parent
     */
    public final int getComponentIndex() {
        RADVisualContainer<?> parent = getParentComponent();
        return parent != null ? parent.getIndexOf(this) : -1;
    }

    public final LayoutSupportManager getParentLayoutSupport() {
        RADVisualContainer<?> parent = getParentComponent();
        return parent != null ? parent.getLayoutSupport() : null;
    }

    boolean isMenuTypeComponent() {
        return MenuElement.class.isAssignableFrom(getBeanClass()) && !JMenuBar.class.isAssignableFrom(getBeanClass());
    }

    /**
     * Returns whether this component is treated specially as a menu component.
     * Not only it must be of particluar Swing menu class, but must also be used
     * as a menu, not as normal visual component. Technically it must be either
     * contained in another menu, or be a menu bar of a window.
     *
     * @return whether the component is a menu used in another menu or as menu
     * bar in a window
     */
    public boolean isMenuComponent() {
        if (isMenuTypeComponent()) {
            RADVisualContainer<?> parent = getParentComponent();
            if ((parent == null && !isInModel())
                    || (parent != null && parent.isMenuTypeComponent())) {
                return true;
            }
        }
        return false;
    }

    static MenuType getMenuType(Class<?> cl) {
        if (MenuElement.class.isAssignableFrom(cl)) {
            if (JMenu.class.isAssignableFrom(cl)) {
                return MenuType.JMenu;
            }
            if (JCheckBoxMenuItem.class.isAssignableFrom(cl)) {
                return MenuType.JCheckBoxMenuItem;
            }
            if (JRadioButtonMenuItem.class.isAssignableFrom(cl)) {
                return MenuType.JRadioButtonMenuItem;
            }
            if (JMenuItem.class.isAssignableFrom(cl)) {
                return MenuType.JMenuItem;
            }
            if (JPopupMenu.class.isAssignableFrom(cl)) {
                return MenuType.JPopupMenu;
            }
        } else if (JSeparator.class.isAssignableFrom(cl)) {
            return MenuType.JSeparator;
        }
        return null;
    }

    @Override
    public void setStoredName(String name) {
        super.setStoredName(name);
        C comp = getBeanInstance();
        if(comp != null)
            comp.setName(getName());
    }

    // -----------------------------------------------------------------------------
    // Layout constraints management
    /**
     * Sets component's constraints description for given layout-support class.
     *
     * @param layoutDelegateClass class of the layout delegate these constraints
     * belong to.
     * @param constr layout constraints.
     */
    public void setLayoutConstraints(Class<?> layoutDelegateClass,
            LayoutConstraints<?> constr) {
        if (constr != null) {
            constraints.put(layoutDelegateClass.getName(), constr);
        }
    }

    /**
     * Gets component's constraints description for given layout-support class.
     *
     * @param layoutDelegateClass class of the layout delegate.
     * @return layout constraints for the given layout delegate.
     */
    public LayoutConstraints<?> getLayoutConstraints(Class<?> layoutDelegateClass) {
        return constraints.get(layoutDelegateClass.getName());
    }

    // ---------------
    // Properties
    @Override
    protected synchronized void createPropertySets(List<Node.PropertySet> propSets) {
        super.createPropertySets(propSets);
        if (constraintsProperties == null) {
            createConstraintsProperties();
        }
        if (constraintsProperties != null && constraintsProperties.length > 0) {
            propSets.add(new Node.PropertySet("layout", // NOI18N
                    FormUtils.getBundleString("CTL_LayoutTab"), // NOI18N
                    FormUtils.getBundleString("CTL_LayoutTabHint")) // NOI18N
                    {
                        @Override
                        public FormProperty<?>[] getProperties() {
                            FormProperty<?>[] props = getConstraintsProperties();
                            return (props == null) ? NO_PROPERTIES : props;
                        }
                    });
        }
    }

    @Override
    protected void clearProperties() {
        resetConstraintsProperties(false);
        super.clearProperties();
        constraintsProperties = null;
    }

    // ---------
    // constraints properties
    public synchronized FormProperty<?>[] getConstraintsProperties() {
        if (constraintsProperties == null) {
            createConstraintsProperties();
        }
        return constraintsProperties;
    }

    public void resetConstraintsProperties() {
        resetConstraintsProperties(true);
    }

    protected void resetConstraintsProperties(boolean fireChangeToNode) {
        if (constraintsProperties != null) {
            for (int i = 0; i < constraintsProperties.length; i++) {
                nameToProperty.remove(constraintsProperties[i].getName());
                constraintsProperties[i].removePropertyChangeListener(getConstraintsListener());
                constraintsProperties[i].removeVetoableChangeListener(getConstraintsListener());
            }
            constraintsProperties = null;
            propertySets = null;
            if (fireChangeToNode) {
                RADComponentNode node = getNodeReference();
                if (node != null) {
                    node.fireComponentPropertySetsChange();
                }
            }
        }
    }

    private void createConstraintsProperties() {
        assert constraintsProperties == null;
        constraintsProperties = NO_PROPERTIES;
        LayoutSupportManager layoutSupport = getParentLayoutSupport();
        if (layoutSupport != null) {
            LayoutConstraints<?> constr = layoutSupport.getConstraints(this);
            if (constr != null) {
                constraintsProperties = constr.getProperties();
                for (int i = 0; i < constraintsProperties.length; i++) {
                    constraintsProperties[i].addVetoableChangeListener(getConstraintsListener());
                    constraintsProperties[i].addPropertyChangeListener(getConstraintsListener());
                    if (isReadOnly() || !isValid()) {
                        int type = constraintsProperties[i].getAccessType() | FormProperty.NO_WRITE;
                        constraintsProperties[i].setAccessType(type);
                    }
                }
            }
        }
    }

    private ConstraintsListenerConvertor getConstraintsListener() {
        if (constraintsListener == null) {
            constraintsListener = new ConstraintsListenerConvertor();
        }
        return constraintsListener;
    }

    private class ConstraintsListenerConvertor implements VetoableChangeListener, PropertyChangeListener {

        @Override
        public void vetoableChange(PropertyChangeEvent ev)
                throws PropertyVetoException {
            Object source = ev.getSource();
            String eventName = ev.getPropertyName();
            assert source instanceof FormProperty;
            if (FormProperty.PROP_VALUE.equals(eventName)) {
                LayoutSupportManager layoutSupport = getParentLayoutSupport();
                int index = getComponentIndex();
                LayoutConstraints<?> constraints
                        = layoutSupport.getConstraints(index);

                ev = new PropertyChangeEvent(constraints,
                        ((FormProperty<?>) source).getName(),
                        ev.getOldValue(),
                        ev.getNewValue());

                layoutSupport.componentLayoutChanged(index, ev);
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent ev) {
            assert ev.getSource() instanceof FormProperty<?>;
            LayoutSupportManager layoutSupport = getParentLayoutSupport();
            int index = getComponentIndex();
            LayoutConstraints<?> constraints = layoutSupport.getConstraints(index);
            ev = new PropertyChangeEvent(constraints,
                    ((FormProperty<?>) ev.getSource()).getName(),
                    ev.getOldValue(),
                    ev.getNewValue());
            try {
                layoutSupport.componentLayoutChanged(index, ev);
            } catch (PropertyVetoException ex) {
            } // should not happen
        }
    }
}
