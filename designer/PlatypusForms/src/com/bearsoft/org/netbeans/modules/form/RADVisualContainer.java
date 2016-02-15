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

import com.bearsoft.org.netbeans.modules.form.RADVisualComponent.MenuType;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutNode;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportDelegate;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportManager;
import com.eas.client.forms.containers.ScrollPane;
import com.eas.client.forms.menu.CheckMenuItem;
import com.eas.client.forms.menu.Menu;
import com.eas.client.forms.menu.MenuBar;
import com.eas.client.forms.menu.MenuItem;
import com.eas.client.forms.menu.MenuSeparator;
import com.eas.client.forms.menu.RadioMenuItem;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import org.openide.ErrorManager;

public class RADVisualContainer<C extends Container> extends RADVisualComponent<C> implements ComponentContainer {

    private java.util.List<RADVisualComponent<?>> subComponents = new ArrayList<>(10);
    private LayoutSupportManager layoutSupport;
    private LayoutNode layoutNode;
    private Method containerDelegateGetter;
    private boolean noContainerDelegate;
    private static Map<MenuType, Class<?>[]> supportedMenus;

    @Override
    protected void setBeanInstance(C beanInstance) {
        containerDelegateGetter = null;
        noContainerDelegate = false;

        super.setBeanInstance(beanInstance);

        // need new layout support for new container bean
        if (layoutSupport == null) {
            layoutSupport = new LayoutSupportManager(this);
        }
    }

    @Override
    public void setInModel(boolean in) {
        boolean alreadyIn = isInModel();
        super.setInModel(in);
        if (in && !alreadyIn && layoutSupport != null) {
            // deferred initialization from pre-creation
            try {
                layoutSupport.initializeLayoutDelegate();
            } catch (Exception ex) {
                // [not reported - but very unlikely to happen - only for new container with custom layout]
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                layoutSupport.setUnknownLayoutDelegate();//(false);
            }
        }
    }

    public void setLayoutSupportDelegate(LayoutSupportDelegate layoutDelegate)
            throws Exception {
        layoutSupport.setLayoutDelegate(layoutDelegate);
        setLayoutNodeReference(null);
    }

    public LayoutSupportManager getLayoutSupport() {
        return layoutSupport;
    }

    void checkLayoutSupport() {
        if (layoutSupport == null) {
            layoutSupport = new LayoutSupportManager(this);
        }
    }

    private void refillContainerInstance() {
        Container cont = getContainerDelegate(getBeanInstance());
        cont.removeAll();
        if (!(cont instanceof ScrollPane)) { // Issue 128797
            cont.setLayout(null); // Issue 77904
        }
        subComponents.stream().forEach((sub) -> {
            cont.add(sub.getBeanInstance());
        });
    }

    public boolean hasDedicatedLayoutSupport() {
        return layoutSupport != null && layoutSupport.isDedicated();
    }

    /**
     * @param container container.
     * @return The JavaBean visual container represented by this
     * RADVisualComponent
     */
    public JComponent getContainerDelegate(Component container) {
        if (container instanceof RootPaneContainer/*
                 && container.getClass().getName().startsWith("javax.swing.")*/) // NOI18N
        {
            return (JComponent) ((RootPaneContainer) container).getContentPane();
        }
        if (container.getClass().equals(JRootPane.class)) {
            return (JComponent) ((JRootPane) container).getContentPane();
        }

        JComponent containerDelegate = (JComponent) container;
        // Do not attempt to find container delegate if the classes
        // don't match. This can happen when ViewConverter was used. 
        // Happens for JApplet, for example.
        if (getBeanClass().isAssignableFrom(container.getClass())) {
            Method m = getContainerDelegateMethod();
            if (m != null) {
                try {
                    containerDelegate = (JComponent) m.invoke(container, new Object[0]);
                    if ((containerDelegate == null) && (container instanceof ScrollPane)) {
                        ScrollPane scrollPane = (ScrollPane) container;
                        scrollPane.setViewportView(null); // force recreation of viewport
                        containerDelegate = (JComponent) m.invoke(container, new Object[0]);
                    }
                } catch (Exception ex) {
                    org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ex);
                }
            }
        }
        return containerDelegate;
    }

    public Method getContainerDelegateMethod() {
        if (containerDelegateGetter == null && !noContainerDelegate) {
            String delegateGetterName = getContainerDelegateGetterName();
            if (delegateGetterName == null
                    && (RootPaneContainer.class.isAssignableFrom(getBeanClass())
                    || JRootPane.class.isAssignableFrom(getBeanClass()))) {
                delegateGetterName = "getContentPane"; // NOI18N
            }
            if (delegateGetterName != null) {
                try {
                    containerDelegateGetter
                            = getBeanClass().getMethod(
                                    delegateGetterName, new Class<?>[]{});
                } catch (NoSuchMethodException ex) {
                    org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ex);
                }
            } else {
                noContainerDelegate = true;
            }
        }
        return containerDelegateGetter;
    }

    String getContainerDelegateGetterName() {
        Object value = getBeanInfo().getBeanDescriptor().getValue("containerDelegate"); // NOI18N

        if (value instanceof String) {
            return (String) value;
        } else {
            return null;
        }
    }

    public void setLayoutNodeReference(LayoutNode aNode) {
        layoutNode = aNode;
    }

    public LayoutNode getLayoutNodeReference() {
        return layoutNode;
    }

    boolean shouldHaveLayoutNode() {
        return layoutSupport != null && layoutSupport.shouldHaveNode();
    }

    public boolean canAddComponent(Class<?> compClass) {
        if (isMenuTypeComponent()) {
            // this is a menu container accepting certain types of menus
            Class<?>[] possibleClasses = getPossibleSubmenus(getMenuType(getBeanClass()));
            if (possibleClasses != null) {
                for (Class<?> cls : possibleClasses) {
                    if (cls.isAssignableFrom(compClass)) {
                        return true;
                    }
                }
            }
            return false;
        } else if (getMenuType(compClass) != null && !MenuBar.class.isAssignableFrom(getBeanClass())) {
            // otherwise don't accept menu components
            return false;
        } else if (Component.class.isAssignableFrom(compClass)) {
            // visual component can be added to visual container
            // exception: avoid adding components to scroll pane that already contains something
            if (ScrollPane.class.isAssignableFrom(getBeanClass())
                    && (((ScrollPane) getBeanInstance()).getViewport() != null)
                    && (((ScrollPane) getBeanInstance()).getViewport().getView() != null)) {
                return false;
            }
            return true;
        }
        return false;
    }

    boolean canHaveMenu(Class<?> menuClass) {
        return MenuBar.class.isAssignableFrom(menuClass)
                && RootPaneContainer.class.isAssignableFrom(getBeanClass());
    }

    private static Class<?>[] getPossibleSubmenus(MenuType menuContainerType) {
        if (supportedMenus == null) {
            supportedMenus = new EnumMap<>(MenuType.class);
            supportedMenus.put(MenuType.JMenu,
                    new Class<?>[]{MenuItem.class,
                        CheckMenuItem.class,
                        RadioMenuItem.class,
                        Menu.class,
                        MenuSeparator.class});
            supportedMenus.put(MenuType.JPopupMenu,
                    new Class<?>[]{MenuItem.class,
                        CheckMenuItem.class,
                        RadioMenuItem.class,
                        Menu.class,
                        MenuSeparator.class});
        }
        return supportedMenus.get(menuContainerType);
    }

    // -----------------------------------------------------------------------------
    // SubComponents Management
    /**
     * @return visual subcomponents (not the menu component)
     */
    public RADVisualComponent<?>[] getSubComponents() {
        RADVisualComponent<?>[] components = new RADVisualComponent<?>[subComponents.size()];
        subComponents.toArray(components);
        return components;
    }

    public RADVisualComponent<?> getSubComponent(int index) {
        return subComponents.get(index);
    }

    // the following methods implement ComponentContainer interface
    /**
     * @return all subcomponents (including the menu component)
     */
    @Override
    public RADVisualComponent<?>[] getSubBeans() {
        int n = subComponents.size();
        RADVisualComponent<?>[] components = new RADVisualComponent<?>[n];
        subComponents.toArray(components);
        return components;
    }

    @Override
    public int getSubBeansCount() {
        return subComponents.size();
    }
        
    @Override
    public void initSubComponents(RADComponent<?>[] initComponents) {
        if (subComponents == null) {
            subComponents = new ArrayList<>(initComponents.length);
        } else {
            subComponents.clear();
        }
        for (int i = 0; i < initComponents.length; i++) {
            RADComponent<?> radComp = initComponents[i];
            subComponents.add((RADVisualComponent<?>) radComp);
            radComp.setParent(this);
        }
        if (layoutSupport == null) {
            refillContainerInstance();
        }
    }

    @Override
    public void reorderSubComponents(int[] perm) {
        RADVisualComponent<?>[] components = new RADVisualComponent<?>[subComponents.size()];
        LayoutConstraints<?>[] constraints;
        if (layoutSupport != null) {
            layoutSupport.removeAll();
            constraints = new LayoutConstraints<?>[subComponents.size()];
        } else {
            constraints = null;
        }
        for (int i = 0; i < perm.length; i++) {
            RADVisualComponent<?> radComp = subComponents.get(i);
            components[perm[i]] = radComp;
            if (constraints != null) {
                constraints[perm[i]] = layoutSupport.getStoredConstraints(radComp);
            }
        }
        subComponents.clear();
        subComponents.addAll(java.util.Arrays.asList(components));
        if (layoutSupport != null) {
            layoutSupport.addComponents(components, constraints, 0);
        } else {
            refillContainerInstance();
        }
    }

    @Override
    public void add(RADComponent<?> comp) {
        add((RADVisualComponent<?>) comp, -1);
    }

    public void add(RADVisualComponent<?> radComp, int index) {
        if (index == -1) {
            index = subComponents.size();
        }
        subComponents.add(index, radComp);
        if (layoutSupport == null) {
            Component comp = radComp.getBeanInstance();
            getContainerDelegate(getBeanInstance()).add(comp, index);
        }
        radComp.setParent(this);
        // force constraints properties creation
        radComp.getConstraintsProperties();
    }

    @Override
    public void remove(RADComponent<?> radComp) {
        if (radComp instanceof RADVisualComponent<?>) {
            int index = subComponents.indexOf(radComp);
            if (layoutSupport != null) {
                layoutSupport.removeComponent((RADVisualComponent<?>) radComp, index);
            } else {
                getContainerDelegate(getBeanInstance()).remove(index);
            }
            if (subComponents.remove((RADVisualComponent<?>) radComp)) {
                radComp.setParent(null);
            }
        }
    }

    @Override
    public int getIndexOf(RADComponent<?> comp) {
        return subComponents.lastIndexOf(comp);
    }
}
