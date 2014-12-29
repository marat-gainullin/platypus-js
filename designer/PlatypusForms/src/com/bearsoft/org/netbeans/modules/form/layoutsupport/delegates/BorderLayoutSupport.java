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
import com.bearsoft.org.netbeans.modules.form.layoutsupport.*;
import com.bearsoft.org.netbeans.modules.form.resources.Resources;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.beans.*;
import java.util.*;
import org.openide.util.ImageUtilities;

/**
 * Support class for BorderLayout. This is an example of support for layout
 * manager using simple component constraints (String).
 *
 * @author Tran Duc Trung, Tomas Pavek, Jan Stola
 */
// Expects ltr orientation of designer
public class BorderLayoutSupport extends AbstractLayoutSupport {

    /**
     * The icon for BorderLayout.
     */
    private static final String iconURL =
            "com/bearsoft/org/netbeans/modules/form/beaninfo/swing/borderLayout.gif"; // NOI18N
    /**
     * The icon for BorderLayout.
     */
    private static final String icon32URL =
            "com/bearsoft/org/netbeans/modules/form/beaninfo/swing/borderLayout32.gif"; // NOI18N
    
    private FormProperty[] properties;
    
    @Override
    protected FormProperty<?>[] getProperties() {
        if(properties == null)
            properties = new FormProperty[]{
                new FormProperty<Integer>(
                "hgap", // NOI18N
                Integer.TYPE,
                getBundle().getString("PROP_hgap"), // NOI18N
                getBundle().getString("HINT_hgap")) {

                    @Override
                    public Integer getValue() throws IllegalAccessException {
                        return ((BorderLayout)getRadLayout().getBeanInstance()).getHgap();
                    }

                    @Override
                    public void setValue(Integer aValue) throws IllegalAccessException, IllegalArgumentException {
                        int oldValue = getValue();
                        int hgap = aValue != null ? aValue : 0;
                        ((BorderLayout)getRadLayout().getBeanInstance()).setHgap(hgap);
                        propertyValueChanged(oldValue, hgap);
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
                    public Integer getValue() {
                        return ((BorderLayout)getRadLayout().getBeanInstance()).getVgap();
                    }

                    @Override
                    public void setValue(Integer aValue) {
                        int oldValue = getValue();
                        int vgap = aValue != null ? aValue : 0;
                        ((BorderLayout)getRadLayout().getBeanInstance()).setVgap(vgap);
                        propertyValueChanged(oldValue, vgap);
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
        return properties;
    }

    /**
     * Gets the supported layout manager class - BorderLayout.
     *
     * @return the class supported by this delegate
     */
    @Override
    public Class<?> getSupportedClass() {
        return BorderLayout.class;
    }

    /**
     * Provides a display name for the layout node - derived from the name of
     * supported class here.
     *
     * @return display name of supported layout
     */
    @Override
    public String getDisplayName() {
        return Resources.getBundle().getString("NAME_java-awt-"+getSupportedClass().getSimpleName());
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
     * This method calculates layout constraints for a component dragged over a
     * container (or just for mouse cursor being moved over container, without
     * any component).
     *
     * @param container instance of a real container over/in which the component
     * is dragged
     * @param containerDelegate effective container delegate of the container
     * (for layout managers we always use container delegate instead of the
     * container)
     * @param component the real component being dragged, can be null
     * @param index position (index) of the component in its container; not
     * needed for BorderLayout
     * @param posInCont position of mouse in the container delegate
     * @param posInComp position of mouse in the dragged component; not needed
     * for BorderLayout
     * @return new LayoutConstraints object corresponding to the position of the
     * component in the container
     */
    @Override
    public LayoutConstraints<?> getNewConstraints(Container container,
            Container containerDelegate,
            Component component,
            int index,
            Point posInCont,
            Point posInComp) {
        if (component != null && component.getParent() != containerDelegate) {
            component = null;
        }

        String primary = BorderLayout.CENTER;
        String alternateX = null;
        String alternateY = null;

        int w = containerDelegate.getSize().width;
        int h = containerDelegate.getSize().height;

        Insets contInsets = containerDelegate.getInsets();
        int marginW = getMargin(w - contInsets.left - contInsets.right);
        int marginH = getMargin(h - contInsets.top - contInsets.bottom);

        int xC = 1; // center by default (0 - left, 1 - center, 2 - right)
        int yC = 1; // center by default (0 - top, 1 - center, 2 - bottom)

        if (w > 25) {
            if (posInCont.x < contInsets.left + marginW) {
                xC = 0; // left
            } else if (posInCont.x >= w - marginW - contInsets.right) {
                xC = 2; // right
            }
        }
        if (h > 25) {
            if (posInCont.y < contInsets.top + marginH) {
                yC = 0; // top
            } else if (posInCont.y >= h - marginH - contInsets.bottom) {
                yC = 2; // bottom
            }
        }

        if (xC == 0) {
            primary = BorderLayout.LINE_START;
        } else if (xC == 2) {
            primary = BorderLayout.LINE_END;
        } else {
            alternateX = posInCont.x - contInsets.left
                    < (w - contInsets.left - contInsets.right) / 2
                    ? BorderLayout.LINE_START : BorderLayout.LINE_END;
        }

        if (yC == 0) { // top
            alternateX = primary;
            primary = BorderLayout.PAGE_START;
        } else if (yC == 2) { // bottom
            alternateX = primary;
            primary = BorderLayout.PAGE_END;
        } else {
            alternateY = posInCont.y - contInsets.top
                    < (h - contInsets.top - contInsets.bottom) / 2
                    ? BorderLayout.PAGE_START : BorderLayout.PAGE_END;
        }

        String[] suggested = new String[]{primary, alternateY, alternateX};
        String[] free = findFreePositions();

        for (int i = 0; i < suggested.length; i++) {
            String str = suggested[i];
            if (str == null) {
                continue;
            }

            for (int j = 0; j < free.length; j++) {
                if (free[j].equals(str)) {
                    if (isAWTContainer()) {
                        str = (String) toAbsolute(str);
                    }
                    assistantParams = str;
                    return new BorderLayoutConstraints(str);
                }
            }

            if (component != null) {
                int idx = getComponentOnPosition(str);
                if (containerDelegate.getComponent(idx) == component) {
                    if (isAWTContainer()) {
                        str = (String) toAbsolute(str);
                    }
                    assistantParams = str;
                    return new BorderLayoutConstraints(str);
                }
            }
        }
        if (isAWTContainer()) {
            free[0] = (String) toAbsolute(free[0]);
        }
        assistantParams = free[0];
        return new BorderLayoutConstraints(free[0]);
    }

    private boolean isAWTContainer() {
        // hack for CDC: only use absolute constraints for AWT components
        Container cont = getLayoutContext().getPrimaryContainer();
        return cont != null
                && !(cont instanceof javax.swing.JComponent)
                && !(cont instanceof javax.swing.RootPaneContainer);
    }
    private String assistantParams;

    @Override
    public String getAssistantContext() {
        return "borderLayout"; // NOI18N
    }

    @Override
    public Object[] getAssistantParams() {
        return new Object[]{assistantParams};
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
     * @param component the real component being dragged, can be null
     * @param newConstraints component layout constraints to be presented
     * @param newIndex component's index position to be presented; not used for
     * BorderLayout
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
        String position = (String) newConstraints.getConstraintsObject();
        Component[] comps = containerDelegate.getComponents();
        int index;

        Dimension contSize = containerDelegate.getSize();
        Insets contInsets = containerDelegate.getInsets();
        Dimension compPrefSize =
                component != null ? component.getPreferredSize() : new Dimension(0, 0);

        int x1, y1, x2, y2;
        int marginW = getMargin(contSize.width - contInsets.left - contInsets.right);
        int marginH = getMargin(contSize.height - contInsets.top - contInsets.bottom);
        switch (position) {
            case BorderLayout.PAGE_START:
            case BorderLayout.NORTH:
                x1 = contInsets.left;
                x2 = contSize.width - contInsets.right;
                y1 = contInsets.top;
                y2 = contInsets.top + (compPrefSize.height > 0
                        ? compPrefSize.height : marginH);
                break;
            case BorderLayout.PAGE_END:
            case BorderLayout.SOUTH:
                x1 = contInsets.left;
                x2 = contSize.width - contInsets.right;
                y1 = contSize.height - contInsets.bottom
                        - (compPrefSize.height > 0 ? compPrefSize.height : marginH);
                y2 = contSize.height - contInsets.bottom;
                break;
            default:
                // LINE_START, LINE_END or CENTER
                switch (position) {
                    case BorderLayout.LINE_START:
                    case BorderLayout.WEST:
                        x1 = contInsets.left;
                        x2 = contInsets.left + (compPrefSize.width > 0
                                ? compPrefSize.width : marginW);
                        break;
                    case BorderLayout.LINE_END:
                    case BorderLayout.EAST:
                        x1 = contSize.width - contInsets.right
                                - (compPrefSize.width > 0 ? compPrefSize.width : marginW);
                        x2 = contSize.width - contInsets.right;
                        break;
                    default:
                        // CENTER
                        index = getComponentOnPosition(BorderLayout.LINE_START);
                        x1 = contInsets.left;
                        if (index >= 0) {
                            x1 += comps[index].getSize().width;
                        }
                        index = getComponentOnPosition(BorderLayout.LINE_END);
                        x2 = contSize.width - contInsets.right;
                        if (index >= 0) {
                            x2 -= comps[index].getSize().width;
                        }
                        break;
                }
                // y1 and y2 are the same for LINE_START, LINE_END and CENTER
                index = getComponentOnPosition(BorderLayout.PAGE_START);
                y1 = contInsets.top;
                if (index >= 0) {
                    y1 += comps[index].getSize().height;
                }
                index = getComponentOnPosition(BorderLayout.PAGE_END);
                y2 = contSize.height - contInsets.bottom;
                if (index >= 0) {
                    y2 -= comps[index].getSize().height;
                }
                break;
        }

        if (x1 >= x2) {
            x1 = contInsets.left;
            x2 = contSize.width - contInsets.right;
            if (x1 >= x2) {
                return true; // container is too small
            }
        }
        if (y1 >= y2) {
            y1 = contInsets.top;
            x2 = contSize.height - contInsets.bottom;
            if (y1 >= y2) {
                return true; // container is too small
            }
        }
        g.drawRect(x1, y1, x2 - x1 - 1, y2 - y1 - 1);
        return true;
    }

    /**
     * This method is called to get a default component layout constraints
     * metaobject in case it is not provided (e.g. in addComponents method).
     *
     * @return the default LayoutConstraints object for the supported layout
     */
    @Override
    protected LayoutConstraints<?> createDefaultConstraints() {
        String pos = findFreePositions()[0];
        if (isAWTContainer()) {
            pos = (String) toAbsolute(pos);
        }
        return new BorderLayoutConstraints(pos);
    }

    // ----------------
    private String[] findFreePositions() {
        java.util.List<String> positions = new ArrayList<>(6);

        if (getComponentOnPosition(BorderLayout.CENTER) == -1) {
            positions.add(BorderLayout.CENTER);
        }
        if (getComponentOnPosition(BorderLayout.PAGE_START) == -1) {
            positions.add(BorderLayout.PAGE_START);
        }
        if (getComponentOnPosition(BorderLayout.PAGE_END) == -1) {
            positions.add(BorderLayout.PAGE_END);
        }
        if (getComponentOnPosition(BorderLayout.LINE_END) == -1) {
            positions.add(BorderLayout.LINE_END);
        }
        if (getComponentOnPosition(BorderLayout.LINE_START) == -1) {
            positions.add(BorderLayout.LINE_START);
        }
        if (positions.isEmpty()) {
            positions.add(BorderLayout.CENTER);
        }
        String[] free = new String[positions.size()];
        positions.toArray(free);
        return free;
    }

    private int getComponentOnPosition(String position) {
        java.util.List<LayoutConstraints<?>> constraints = getConstraintsList();
        if (constraints == null) {
            return -1;
        }
        position = (String) toAbsolute(position);
        for (int i = 0, n = constraints.size(); i < n; i++) {
            LayoutConstraints<?> constr = constraints.get(i);
            if (constr != null && position.equals(toAbsolute(constr.getConstraintsObject()))) {
                return i;
            }
        }
        return -1;
    }

    private static Object toAbsolute(Object constraint) {
        if (BorderLayout.LINE_START.equals(constraint)) {
            constraint = BorderLayout.WEST;
        } else if (BorderLayout.LINE_END.equals(constraint)) {
            constraint = BorderLayout.EAST;
        } else if (BorderLayout.PAGE_START.equals(constraint)) {
            constraint = BorderLayout.NORTH;
        } else if (BorderLayout.PAGE_END.equals(constraint)) {
            constraint = BorderLayout.SOUTH;
        }
        return constraint;
    }

    private int getMargin(int size) {
        int margin = size / 8;
        if (margin < 10) {
            margin = 10;
        }
        if (margin > 50) {
            margin = 50;
        }
        return margin;
    }

    // ----------------
    /**
     * LayoutConstraints implementation class for component constraints of BorderLayout.
     */
    public static class BorderLayoutConstraints implements LayoutConstraints<String> {

        private String direction;
        private FormProperty<?>[] properties;

        public BorderLayoutConstraints(String aDirection) {
            super();
            direction = aDirection;
        }

        @Override
        public FormProperty<?>[] getProperties() {
            if (properties == null) {
                properties = new FormProperty<?>[]{
                    new FormProperty<String>(
                    "place", // NOI18N
                    String.class,
                    getBundle().getString("PROP_direction"), // NOI18N
                    getBundle().getString("HINT_direction")) // NOI18N
                    {
                        protected BorderDirectionEditor editor;

                        @Override
                        public PropertyEditor getPropertyEditor() {
                            if (editor == null) {
                                editor = new BorderDirectionEditor();
                            }
                            return editor;
                        }

                        @Override
                        public String getValue() {
                            return direction;
                        }

                        @Override
                        public void setValue(String value) {
                            String oldValue = getValue();
                            direction = value;
                            propertyValueChanged(oldValue, value);
                        }
                    }
                };
                properties[0].setValue("NOI18N", Boolean.TRUE); // NOI18N
            }

            return properties;
        }

        @Override
        public String getConstraintsObject() {
            return direction;
        }

        @Override
        public BorderLayoutConstraints cloneConstraints() {
            return new BorderLayoutConstraints(direction);
        }
    }

    // ---------
    /**
     * PropertyEditor for the BorderLayout constraints property.
     */
    static class BorderDirectionEditor extends PropertyEditorSupport {

        private final String[] values = {
            BorderLayout.CENTER,
            BorderLayout.WEST,
            BorderLayout.EAST,
            BorderLayout.NORTH,
            BorderLayout.SOUTH
        };
        private final String[] javaInitStrings = {
            "java.awt.BorderLayout.CENTER", // NOI18N
            "java.awt.BorderLayout.WEST", // NOI18N
            "java.awt.BorderLayout.EAST", // NOI18N
            "java.awt.BorderLayout.NORTH", // NOI18N
            "java.awt.BorderLayout.SOUTH" // NOI18N
        };

        @Override
        public String[] getTags() {
            return values;
        }

        @Override
        public String getAsText() {
            return (String) getValue();
        }

        @Override
        public void setAsText(String str) {
            for (int i = 0; i < values.length; i++) {
                if (str.equals(values[i])) {
                    setValue(str);
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
}
