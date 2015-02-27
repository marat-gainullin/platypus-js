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

import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.*;
import com.eas.client.forms.Orientation;
import com.eas.client.forms.layouts.BoxLayout;
import java.awt.*;
import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JPanel;
import org.openide.util.ImageUtilities;

/**
 * Support class for BoxLayout. This is an example of support for layout manager
 * which is not a JavaBean - some general functionality from
 * AbstractLayoutSupport must be overridden and handled differently.
 *
 * @author Tran Duc Trung, Tomas Pavek
 */
// Expects ltr orientation of the designer
public class BoxLayoutSupport extends AbstractLayoutSupport {

    /**
     * The icon for BoxLayout.
     */
    private static final String iconURL
            = "com/bearsoft/org/netbeans/modules/form/beaninfo/swing/boxLayout.gif"; // NOI18N
    /**
     * The icon for BoxLayout.
     */
    private static final String icon32URL
            = "com/bearsoft/org/netbeans/modules/form/beaninfo/swing/boxLayout32.gif"; // NOI18N

    private FormProperty<?>[] properties;

    /**
     * Gets the supported layout manager class - BoxLayout.
     *
     * @return the class supported by this delegate
     */
    @Override
    public Class<?> getSupportedClass() {
        return BoxLayout.class;
    }

    /**
     * Provides an icon to be used for the layout node in Component Inspector.
     * Only 16x16 color icon is required.
     *
     * @param type is one of BeanInfo constants: ICON_COLOR_16x16,
     * ICON_COLOR_32x32, ICON_MONO_16x16, ICON_MONO_32x32
     * @return icon to be displayed for node in Component Inspector
     */
    @Override
    public Image getIcon(int type) {
        switch (type) {
            case BeanInfo.ICON_COLOR_16x16:
            case BeanInfo.ICON_MONO_16x16:
                return ImageUtilities.loadImage(iconURL);
            default:
                return ImageUtilities.loadImage(icon32URL);
        }
    }

    /**
     * This method is called after a property of the layout is changed by the
     * user. The delagate implementation may check whether the layout is valid
     * after the change and throw PropertyVetoException if the change should be
     * reverted.
     *
     * @param ev PropertyChangeEvent object describing the change
     * @throws java.beans.PropertyVetoException
     */
    @Override
    public void acceptContainerLayoutChange(PropertyChangeEvent ev)
            throws PropertyVetoException {
        updateLayoutInstance();
        super.acceptContainerLayoutChange(ev);
    }

    /**
     * This method calculates position (index) for a component dragged over a
     * container (or just for mouse cursor being moved over container, without
     * any component).
     *
     * @param container instance of a real container over/in which the component
     * is dragged
     * @param containerDelegate effective container delegate of the container
     * (for layout managers we always use container delegate instead of the
     * container)
     * @param component the real component being dragged; not needed here
     * @param index position (index) of the component in its current container;
     * not needed here
     * @param posInCont position of mouse in the container delegate
     * @param posInComp position of mouse in the dragged component; not needed
     * here
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
        if (!(containerDelegate.getLayout() instanceof BoxLayout)) {
            return -1;
        }

        assistantParams = 0;
        Component[] components = containerDelegate.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == component) {
                assistantParams--;
                continue;
            }
            Rectangle b = components[i].getBounds();
            if (getOrientation() == Orientation.HORIZONTAL) {
                if (posInCont.x < b.x + b.width / 2) {
                    assistantParams += i;
                    return i;
                }
            } else {
                if (posInCont.y < b.y + b.height / 2) {
                    assistantParams += i;
                    return i;
                }
            }
        }

        assistantParams += components.length;
        return components.length;
    }
    private int assistantParams;

    @Override
    public String getAssistantContext() {
        return "boxLayout"; // NOI18N
    }

    @Override
    public Object[] getAssistantParams() {
        return new Object[]{assistantParams + 1};
    }

    /**
     * This method paints a dragging feedback for a component dragged over a
     * container (or just for mouse cursor being moved over container, without
     * any component).
     *
     * @param container instance of a real container over/in which the component
     * is dragged
     * @param containerDelegate effective container delegate of the container
     * (for layout managers we always use container delegate instead of the
     * container)
     * @param component the real component being dragged, not needed here
     * @param newConstraints component layout constraints to be presented; not
     * used for BoxLayout
     * @param newIndex component's index position to be presented
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
        if (containerDelegate.getLayout() instanceof BoxLayout) {
            Dimension containerSize = containerDelegate.getSize();

            Component[] components = containerDelegate.getComponents();
            Rectangle rect;

            if ((components.length == 0) || ((components.length == 1) && (components[0] == component))) {
                Insets ins = containerDelegate.getInsets();
                rect = (getOrientation() == Orientation.HORIZONTAL)
                        ? new Rectangle(ins.left, ins.top,
                                30, containerSize.height)
                        : new Rectangle(ins.left, ins.top,
                                containerSize.width, 20);
            } else if (newIndex < 0 || newIndex >= components.length) {
                Component comp = components[components.length - 1];
                if (comp == component) {
                    comp = components[components.length - 2];
                }
                Rectangle b = comp.getBounds();
                rect = (getOrientation() == Orientation.HORIZONTAL)
                        ? new Rectangle(b.x + b.width - 10, b.y, 30, b.height)
                        : new Rectangle(b.x, b.y + b.height - 10, b.width, 20);
            } else {
                Rectangle b = components[newIndex].getBounds();
                rect = (getOrientation() == Orientation.HORIZONTAL)
                        ? new Rectangle(b.x - 10, b.y, 30, b.height)
                        : new Rectangle(b.x, b.y - 10, b.width, 20);
            }

            g.drawRect(rect.x, rect.y, rect.width, rect.height);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets up the layout (without adding components) on a real container,
     * according to the internal metadata representation. This method must
     * override AbstractLayoutSupport because BoxLayout instance cannot be used
     * universally - new instance must be created for each container.
     *
     * @param container instance of a real container to be set
     * @param containerDelegate effective container delegate of the container;
     * for layout managers we always use container delegate instead of the
     * container
     */
    @Override
    public void setLayoutToContainer(Container container,
            Container containerDelegate) {
        containerDelegate.setLayout(cloneLayoutInstance(container,
                containerDelegate));
    }

    @Override
    public void addComponentsToContainer(Container container,
            Container containerDelegate,
            Component[] components,
            int index) {
        // Issue 63955 and JDK bug 4294758
        ((LayoutManager2) containerDelegate.getLayout()).invalidateLayout(containerDelegate);
        /*
         int axis = ((BoxLayout) containerDelegate.getLayout()).getAxis();
         for (Component comp : components) {
         if (comp instanceof JComponent) {
         comp.setPreferredSize(comp.getSize());
         ((JComponent) comp).setAlignmentX(1.0f);
         ((JComponent) comp).setAlignmentY(1.0f);
         SwingFactory.prefToMaxForBox(axis, comp);
         }
         }
         */
        super.addComponentsToContainer(container, containerDelegate, components, index);
    }

    // ------------
    /**
     * Creates a default instance of LayoutManager (for internal use). This
     * method must override AbstractLayoutSupport because BoxLayout is not a
     * bean (so it cannot be created automatically).
     *
     * @return new instance of BoxLayout
     */
    @Override
    protected LayoutManager createDefaultLayoutInstance() {
        return new BoxLayout(new JPanel(), BoxLayout.X_AXIS);
    }

    /**
     * Cloning method - creates a clone of the reference LayoutManager instance
     * (for external use). This method must override AbstractLayoutSupport
     * because BoxLayout is not a bean (so it cannot be cloned automatically).
     *
     * @param container instance of a real container in whose container delegate
     * the layout manager will be probably used
     * @param containerDelegate effective container delegate of the container
     * @return cloned instance of BoxLayout
     */
    @Override
    protected LayoutManager cloneLayoutInstance(Container container,
            Container containerDelegate) {
        int axis = BoxLayout.X_AXIS;
        if (getOrientation() == Orientation.HORIZONTAL) {
            axis = BoxLayout.X_AXIS;
        } else if (getOrientation() == Orientation.VERTICAL) {
            axis = BoxLayout.Y_AXIS;
        }
        BoxLayout layout = (BoxLayout) getRadLayout().getBeanInstance();
        return new BoxLayout(containerDelegate, axis, layout.getHgap(), layout.getVgap());
    }

    /**
     * Since BoxLayout is not a bean, we must specify its properties explicitly.
     * This method is called from getPropertySets() implementation to obtain the
     * default property set for the layout (assuming there's only one property
     * set). So it woul be also possible to override (more generally)
     * getPropertySets() instead.
     *
     * @return array of properties of the layout manager
     */
    @Override
    protected FormProperty<?>[] getProperties() {
        if (properties == null) {
            // we cannot use RADProperty because "axis" is not a real
            // bean property - we must create a special FormProperty
            properties = new FormProperty<?>[]{
                new FormProperty<Integer>(
                "orientation", // NOI18N
                Integer.TYPE,
                getBundle().getString("PROP_axis"), // NOI18N
                getBundle().getString("HINT_axis")) // NOI18N
                {
                    protected OrientationEditor editor;

                    @Override
                    public PropertyEditor getPropertyEditor() {
                        if (editor == null) {
                            editor = new OrientationEditor();
                        }
                        return editor;
                    }

                    @Override
                    public Integer getValue() {
                        return getOrientation();
                    }

                    @Override
                    public void setValue(Integer value) {
                        Integer oldValue = getValue();
                        setOrientation(value);
                        propertyValueChanged(oldValue, value);
                    }

                    @Override
                    public boolean supportsDefaultValue() {
                        return true;
                    }

                    @Override
                    public Integer getDefaultValue() {
                        return Orientation.HORIZONTAL;
                    }
                },
                new FormProperty<Integer>(
                "hgap", // NOI18N
                Integer.TYPE,
                getBundle().getString("PROP_hgap"), // NOI18N
                getBundle().getString("HINT_hgap")) {

                    @Override
                    public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                        BoxLayout layout = (BoxLayout) getRadLayout().getBeanInstance();
                        return layout.getHgap();
                    }

                    @Override
                    public void setValue(Integer aValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                        BoxLayout layout = (BoxLayout) getRadLayout().getBeanInstance();
                        int oldValue = layout.getHgap();
                        layout.setHgap(aValue != null ? aValue : 0);
                        propertyValueChanged(oldValue, layout.getHgap());
                    }

                    @Override
                    public boolean supportsDefaultValue() {
                        return true;
                    }

                    @Override
                    public Integer getDefaultValue() {
                        return 0;
                    }
                }, // NOI18N
                new FormProperty<Integer>(
                "vgap", // NOI18N
                Integer.TYPE,
                getBundle().getString("PROP_vgap"), // NOI18N
                getBundle().getString("HINT_vgap")) {

                    @Override
                    public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                        BoxLayout layout = (BoxLayout) getRadLayout().getBeanInstance();
                        return layout.getVgap();
                    }

                    @Override
                    public void setValue(Integer aValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                        BoxLayout layout = (BoxLayout) getRadLayout().getBeanInstance();
                        int oldValue = layout.getVgap();
                        layout.setVgap(aValue != null ? aValue : 0);
                        propertyValueChanged(oldValue, layout.getVgap());
                    }

                    @Override
                    public boolean supportsDefaultValue() {
                        return true;
                    }

                    @Override
                    public Integer getDefaultValue() {
                        return 0;
                    }
                } // NOI18N
            };
        }
        return properties;
    }

    private int getOrientation() {
        BoxLayout layout = (BoxLayout) getRadLayout().getBeanInstance();
        int axis = layout.getAxis();
        if (axis == BoxLayout.Y_AXIS || axis == BoxLayout.PAGE_AXIS) {
            return Orientation.VERTICAL;
        } else {
            return Orientation.HORIZONTAL;
        }
    }

    private void setOrientation(int aValue) {
        BoxLayout layout = (BoxLayout) getRadLayout().getBeanInstance();
        if (aValue == Orientation.VERTICAL) {
            layout.setAxis(BoxLayout.Y_AXIS);
        } else {
            layout.setAxis(BoxLayout.X_AXIS);
        }
    }

    /**
     * Method to obtain just one propetry of given name. Must be override
     * AbstractLayoutSupport because alternative properties are used for
     * BoxLayout (see getProperties method)
     *
     * @param propName
     * @return layout property of given name
     */
    @Override
    protected FormProperty<?> getProperty(String propName) {
        return "axis".equals(propName) ? getProperties()[0] : null; // NOI18N
    }

    public static final class OrientationEditor extends PropertyEditorSupport {

        private final String[] tags = {
            getBundle().getString("VALUE_axis_x"), // NOI18N
            getBundle().getString("VALUE_axis_y") // NOI18N
        };
        private final Integer[] values = {
            Orientation.HORIZONTAL,
            Orientation.VERTICAL
        };
        private final String[] javaInitStrings = {
            BoxLayout.class.getName() + ".X_AXIS", // NOI18N
            BoxLayout.class.getName() + ".Y_AXIS" // NOI18N
        };

        @Override
        public String[] getTags() {
            return tags;
        }

        @Override
        public String getAsText() {
            Object value = getValue();
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(value)) {
                    return tags[i];
                }
            }
            return null;
        }

        @Override
        public void setAsText(String str) {
            for (int i = 0; i < values.length; i++) {
                if (tags[i].equals(str)) {
                    setValue(values[i]);
                    break;
                }
            }
        }

        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(value)) {
                    return javaInitStrings[i];
                }
            }
            return null;
        }
    }
    
    @Override
    public void addComponents(RADComponent<?>[] newComps, LayoutConstraints<?>[] newConstraints, int index) {
        // no op here because box layout has no per-component constraints.
    }

    @Override
    public void removeComponent(int index) {
        // no op here because box layout has no per-component constraints.
    }
        
}
