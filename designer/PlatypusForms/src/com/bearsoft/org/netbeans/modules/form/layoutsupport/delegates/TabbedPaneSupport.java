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
package com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates;

import com.bearsoft.org.netbeans.modules.form.*;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.*;
import java.awt.*;
import java.lang.reflect.Method;
import javax.swing.*;
import org.openide.ErrorManager;

/**
 * Dedicated layout support class for JTabbedPane.
 *
 * @author Tomas Pavek
 */
public class TabbedPaneSupport extends AbstractLayoutSupport {

    private int selectedTab = -1;
    private static Method addTabMethod1;
    private static Method addTabMethod2;
    private static Method addTabMethod3;

    /**
     * Gets the supported layout manager class - JTabbedPane.
     *
     * @return the class supported by this delegate
     */
    @Override
    public Class<?> getSupportedClass() {
        return JTabbedPane.class;
    }

    /**
     * Removes one component from the layout (at metadata level). The code
     * structures describing the layout is updated immediately.
     *
     * @param index index of the component in the layout
     */
    @Override
    public void removeComponent(int index) {
        super.removeComponent(index);
        if (selectedTab >= getComponentCount()) {
            selectedTab = getComponentCount() - 1;
        }
    }

    /**
     * This method is called when user clicks on the container in form designer.
     * For JTabbedPane, we it switch the selected TAB.
     *
     * @param p Point of click in the container
     * @param real instance of the container when the click occurred
     * @param containerDelegate effective container delegate of the container
     */
    @Override
    public void processMouseClick(Point p,
            Container container,
            Container containerDelegate) {
        if (!(container instanceof JTabbedPane)) {
            return;
        }

        JTabbedPane tabbedPane = (JTabbedPane) container;
        int n = tabbedPane.getTabCount();
        for (int i = 0; i < n; i++) {
            Rectangle rect = tabbedPane.getBoundsAt(i);
            if ((rect != null) && rect.contains(p)) {
                selectedTab = i;
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * This method is called when a component is selected in Component
     * Inspector.
     *
     * @param index position (index) of the selected component in container
     */
    @Override
    public void selectComponent(int index) {
        selectedTab = index; // remember as selected tab
    }

    /**
     * In this method, the layout delegate has a chance to "arrange" real
     * container instance additionally - some other way that cannot be done
     * through layout properties and added components.
     *
     * @param container instance of a real container to be arranged
     * @param containerDelegate effective container delegate of the container
     */
    @Override
    public void arrangeContainer(Container container,
            Container containerDelegate) {
        if (container instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) container;
            if (selectedTab >= 0) {
                if (tabbedPane.getTabCount() > selectedTab) {
                    // select the tab
                    tabbedPane.setSelectedIndex(selectedTab);

                    // workaround for JTabbedPane bug 4190719
                    Component comp = tabbedPane.getSelectedComponent();
                    if (comp != null) {
                        comp.setVisible(true);
                    }
                    tabbedPane.repaint();
                }
            } else if (tabbedPane.getTabCount() > 0) {
                // workaround for JTabbedPane bug 4190719
                tabbedPane.getComponentAt(0).setVisible(true);
            }
        }
    }

    /**
     * This method should calculate position (index) for a component dragged
     * over a container (or just for mouse cursor being moved over container,
     * without any component).
     *
     * @param container instance of a real container over/in which the component
     * is dragged
     * @param containerDelegate effective container delegate of the container
     * @param component the real component being dragged; not needed here
     * @param index position (index) of the component in its current container;
     * not needed here
     * @param posInCont position of mouse in the container delegate; not needed
     * @param posInComp position of mouse in the dragged component; not needed
     * @return index corresponding to the position of the component in the
     * container
     */
    @Override
    public int getNewIndex(Container container,
            Container containerDelegate,
            Component component,
            int index,
            Point posInCont,
            Point posInComp) {
        if (!(container instanceof JTabbedPane)) {
            return -1;
        }
        return ((JTabbedPane) container).getTabCount();
    }

    @Override
    public String getAssistantContext() {
        return "tabbedPaneLayout"; // NOI18N
    }

    /**
     * This method paints a dragging feedback for a component dragged over a
     * container (or just for mouse cursor being moved over container, without
     * any component).
     *
     * @param container instance of a real container over/in which the component
     * is dragged
     * @param containerDelegate effective container delegate of the container
     * @param component the real component being dragged; not needed here
     * @param newConstraints component layout constraints to be presented; not
     * used for JTabbedPane
     * @param newIndex component's index position to be presented; not needed
     * @param g Graphics object for painting (with color and line style set)
     * @return whether any feedback was painted (true in this case)
     */
    @Override
    public boolean paintDragFeedback(Container container,
            Container containerDelegate,
            Component component,
            LayoutConstraints<?> newConstraints,
            int newIndex,
            Graphics g) {
        if (!(container instanceof JTabbedPane)) {
            return false;
        }

        JTabbedPane tabbedPane = (JTabbedPane) container;
        if ((tabbedPane.getTabCount() == 0) || (component == tabbedPane.getComponentAt(0))) {
            Dimension sz = container.getSize();
            Insets insets = container.getInsets();
            sz.width -= insets.left + insets.right;
            sz.height -= insets.top + insets.bottom;
            g.drawRect(0, 0, sz.width, sz.height);
        } else {
            Rectangle rect = tabbedPane.getComponentAt(0).getBounds();
            g.drawRect(rect.x, rect.y, rect.width, rect.height);
        }
        return true;
    }

    /**
     * Adds real components to given container (according to layout constraints
     * stored for the components).
     *
     * @param container instance of a real container to be added to
     * @param containerDelegate effective container delegate of the container
     * @param components components to be added
     * @param index position at which to add the components to container
     */
    @Override
    public void addComponentsToContainer(Container container,
            Container containerDelegate,
            Component[] components,
            int index) {
        if (!(container instanceof JTabbedPane)) {
            return;
        }

        for (int i = 0; i < components.length; i++) {
            LayoutConstraints<?> constraints = getConstraints(i + index);
            if (constraints instanceof TabLayoutConstraints) {
                JTabbedPane tabbedPane = (JTabbedPane) container;
                try {
                    Object title = constraints.getProperties()[0].getValue();
                    Object icon = constraints.getProperties()[1].getValue();
                    Object tooltip = constraints.getProperties()[2].getValue();

                    tabbedPane.insertTab(
                            title instanceof String ? (String) title : null,
                            icon instanceof Icon ? (Icon) icon : null,
                            components[i],
                            tooltip instanceof String ? (String) tooltip : null,
                            index + i);
                } catch (Exception ex) {
                    org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ex);
                }
            }
        }
    }

    /**
     * This method is called to get a default component layout constraints
     * metaobject in case it is not provided (e.g. in addComponents method).
     *
     * @return the default LayoutConstraints object for the supported layout;
     * null if no component constraints are used
     */
    @Override
    protected LayoutConstraints<?> createDefaultConstraints() {
        return new TabLayoutConstraints("tab" + (getComponentCount())); // NOI18N
    }

    // ----------
    // tab, icon, component, tooltip
    private static Method getAddTabMethod1() {
        if (addTabMethod1 == null) {
            try {
                addTabMethod1 = JTabbedPane.class.getMethod(
                        "addTab", // NOI18N
                        new Class<?>[]{String.class, Icon.class,
                            Component.class, String.class});
            } catch (NoSuchMethodException ex) { // should not happen
                ErrorManager.getDefault().notify(ex);
            }
        }
        return addTabMethod1;
    }

    // tab, icon, component
    private static Method getAddTabMethod2() {
        if (addTabMethod2 == null) {
            try {
                addTabMethod2 = JTabbedPane.class.getMethod(
                        "addTab", // NOI18N
                        new Class<?>[]{String.class, Icon.class,
                            Component.class});
            } catch (NoSuchMethodException ex) { // should not happen
                ErrorManager.getDefault().notify(ex);
            }
        }
        return addTabMethod2;
    }

    // tab, component
    /*
     private static Method getAddTabMethod3() {
     if (addTabMethod3 == null) {
     try {
     addTabMethod3 = JTabbedPane.class.getMethod(
     "addTab", // NOI18N
     new Class<?>[]{String.class, Component.class});
     } catch (NoSuchMethodException ex) { // should not happen
     ErrorManager.getDefault().notify(ex);
     }
     }
     return addTabMethod3;
     }
     */
    // ----------
    /**
     * LayoutConstraints implementation for managing JTabbedPane tab parameters.
     */
    public static class TabLayoutConstraints implements LayoutConstraints<String> {

        private String title;
        private IconEditor.NbImageIcon icon;
        private String toolTip;
        private FormProperty<?>[] properties;

        public TabLayoutConstraints(String aTitle) {
            super();
            title = aTitle;
        }

        public TabLayoutConstraints(String aTitle, IconEditor.NbImageIcon aIcon, String aToolTip) {
            super();
            title = aTitle;
            icon = aIcon;
            toolTip = aToolTip;
        }

        public String getTitle() {
            return title;
        }

        public IconEditor.NbImageIcon getIcon() {
            return icon;
        }

        public String getToolTip() {
            return toolTip;
        }

        // -----------
        @Override
        public FormProperty<?>[] getProperties() {
            if (properties == null) {
                properties = new FormProperty<?>[]{
                    new FormProperty<String>("tabTitle", // NOI18N
                    String.class,
                    getBundle().getString("PROP_tabTitle"), // NOI18N
                    getBundle().getString("HINT_tabTitle")) { // NOI18N
                        @Override
                        public String getValue() {
                            return title;
                        }

                        @Override
                        public void setValue(String value) {
                            String oldValue = getValue();
                            title = value;
                            propertyValueChanged(oldValue, value);
                        }
                    },
                    new FormProperty<ImageIcon>("tabIcon", // NOI18N
                    ImageIcon.class,
                    getBundle().getString("PROP_tabIcon"), // NOI18N
                    getBundle().getString("HINT_tabIcon")) { // NOI18N

                        @Override
                        public ImageIcon getValue() {
                            return icon;
                        }

                        @Override
                        public void setValue(ImageIcon aValue) {
                            IconEditor.NbImageIcon oldValue = (IconEditor.NbImageIcon)getValue();
                            if (icon != aValue) {
                                icon = (IconEditor.NbImageIcon)aValue;
                                propertyValueChanged(oldValue, aValue);
                            }
                        }

                        @Override
                        public boolean supportsDefaultValue() {
                            return true;
                        }

                        @Override
                        public ImageIcon getDefaultValue() {
                            return null;
                        }
                    },
                    new FormProperty<String>("tabToolTip", // NOI18N
                    String.class,
                    getBundle().getString("PROP_tabToolTip"), // NOI18N
                    getBundle().getString("HINT_tabToolTip")) { // NOI18N
                        @Override
                        public String getValue() {
                            return toolTip;
                        }

                        @Override
                        public void setValue(String value) {
                            String oldValue = getValue();
                            toolTip = value;
                            propertyValueChanged(oldValue, value);
                        }

                        @Override
                        public boolean supportsDefaultValue() {
                            return true;
                        }

                        @Override
                        public String getDefaultValue() {
                            return null;
                        }
                    }
                };
            }

            return properties;
        }

        @Override
        public String getConstraintsObject() {
            return title;
        }

        @Override
        public TabLayoutConstraints cloneConstraints() {
            TabLayoutConstraints constr = new TabLayoutConstraints(title);
            com.bearsoft.org.netbeans.modules.form.FormUtils.copyProperties(
                    getProperties(),
                    constr.getProperties(),
                    FormUtils.CHANGED_ONLY | FormUtils.DISABLE_CHANGE_FIRING);
            return constr;
        }
    }
}
