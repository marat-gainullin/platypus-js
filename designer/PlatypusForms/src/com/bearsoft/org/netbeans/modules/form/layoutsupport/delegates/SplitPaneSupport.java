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
import java.awt.*;
import java.beans.*;
import javax.swing.JSplitPane;

/**
 * Dedicated layout support class for JSplitPane.
 *
 * @author Tomas Pavek
 */
public class SplitPaneSupport extends AbstractLayoutSupport {

    /**
     * Gets the supported layout manager class - JSplitPane.
     *
     * @return the class supported by this delegate
     */
    @Override
    public Class<?> getSupportedClass() {
        return JSplitPane.class;
    }

    /**
     * This method calculates layout constraints for a component dragged over a
     * container (or just for mouse cursor being moved over container, without
     * any component).
     *
     * @param container instance of a real container over/in which the component
     * is dragged
     * @param containerDelegate effective container delegate of the container
     * @param component the real component being dragged, not needed here
     * @param index position (index) of the component in its container; not
     * needed here
     * @param posInCont position of mouse in the container
     * @param posInComp position of mouse in the dragged component; not needed
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
        if (!(container instanceof JSplitPane)) {
            return null;
        }

        assistantParams = findFreePosition();
        return new SplitLayoutConstraints(assistantParams);
    }
    private String assistantParams;

    @Override
    public String getAssistantContext() {
        return "splitPaneLayout"; // NOI18N
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
     * @param component the real component being dragged; not needed here
     * @param newConstraints component layout constraints to be presented
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
        if (!(container instanceof JSplitPane)) {
            return false;
        }

        String position = (String) newConstraints.getConstraintsObject();
        if (position == null) {
            return false;
        }

        JSplitPane splitPane = (JSplitPane) container;
        int orientation = splitPane.getOrientation();

        Dimension sz = splitPane.getSize();
        Insets insets = container.getInsets();
        sz.width -= insets.left + insets.right;
        sz.height -= insets.top + insets.bottom;

        Rectangle rect = new Rectangle(insets.left, insets.top, sz.width, sz.height);

        if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
            Component left = splitPane.getLeftComponent();
            Component right = splitPane.getRightComponent();

            if (JSplitPane.LEFT.equals(position)) {
                if ((right == null) || (right == component)) {
                    rect.width = sz.width / 2;
                } else {
                    rect.width = right.getBounds().x - rect.x;
                }
            } else {
                if ((left == null) || (left == component)) {
                    rect.x = insets.left + sz.width / 2;
                    rect.width = sz.width - rect.x;
                } else {
                    rect.x = left.getBounds().x + left.getBounds().width;
                    rect.width = sz.width - rect.x;
                }
            }
        } else {
            Component top = splitPane.getTopComponent();
            Component bottom = splitPane.getBottomComponent();

            if (JSplitPane.TOP.equals(position)) {
                if ((bottom == null) || (bottom == component)) {
                    rect.height /= 2;
                } else {
                    rect.height = bottom.getBounds().y - rect.y;
                }
            } else {
                if ((top == null) || (top == component)) {
                    rect.y = insets.top + sz.height / 2;
                    rect.height = sz.height - rect.y;
                } else {
                    rect.y = top.getBounds().y + top.getBounds().height;
                    rect.height = sz.height - rect.y;
                }
            }
        }
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
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
        if (container instanceof JSplitPane) {
            for (int i = 0; i < components.length; i++) {
                JSplitPane splitPane = (JSplitPane) container;

                int descPos = convertPosition(getConstraints(i + index));
                if (descPos == 0) {
                    splitPane.setLeftComponent(components[i]);
                } else if (descPos == 1) {
                    splitPane.setRightComponent(components[i]);
                }
            }
        }
    }

    /**
     * Removes a real component from a real container.
     *
     * @param container instance of a real container
     * @param containerDelegate effective container delegate of the container
     * @param component component to be removed
     * @return whether it was possible to remove the component (some containers
     * may not support removing individual components reasonably)
     */
    @Override
    public boolean removeComponentFromContainer(Container container,
            Container containerDelegate,
            Component component) {
        if (!(containerDelegate instanceof JSplitPane)) {
            return false; // should not happen
        }
        return super.removeComponentFromContainer(container, containerDelegate, component);
    }

    @Override
    protected LayoutConstraints<?> createDefaultConstraints() {
        return new SplitLayoutConstraints(findFreePosition());
    }

    // ------------
    private int convertPosition(LayoutConstraints<?> desc) {
        if (desc != null) {
            Object position = desc.getConstraintsObject();
            if (JSplitPane.LEFT.equals(position) || JSplitPane.TOP.equals(position)) {
                return 0;
            }
            if (JSplitPane.RIGHT.equals(position) || JSplitPane.BOTTOM.equals(position)) {
                return 1;
            }
        }
        return -1;
    }

    private String findFreePosition() {
        int leftTop = 0, rightBottom = 0;
        int orientation = JSplitPane.HORIZONTAL_SPLIT;

        for (int i = 0, n = getComponentCount(); i < n; i++) {
            LayoutConstraints<?> constraints = getConstraints(i);
            if (!(constraints instanceof SplitLayoutConstraints)) {
                continue;
            }

            int constrPos = convertPosition(constraints);
            if (constrPos == 0) {
                leftTop++;
            } else if (constrPos == 1) {
                rightBottom++;
            }
        }

        if (leftTop == 0 || leftTop < rightBottom) {
            return orientation == JSplitPane.HORIZONTAL_SPLIT
                    ? JSplitPane.LEFT : JSplitPane.TOP;
        } else {
            return orientation == JSplitPane.HORIZONTAL_SPLIT
                    ? JSplitPane.RIGHT : JSplitPane.BOTTOM;
        }
    }

    // -----------
    /**
     * LayoutConstraints implementation holding component position in
     * JSplitPane.
     */
    public static class SplitLayoutConstraints implements LayoutConstraints<String> {

        private String position;
        private FormProperty<?>[] properties;

        public SplitLayoutConstraints(String aPosition) {
            position = aPosition;
        }

        @Override
        public FormProperty<?>[] getProperties() {
            if (properties == null) {
                properties = new FormProperty<?>[]{
                    new FormProperty<String>(
                    "splitPosition", // NOI18N
                    String.class,
                    getBundle().getString("PROP_splitPos"), // NOI18N
                    getBundle().getString("HINT_splitPos")) // NOI18N
                    {
                        protected SplitPositionEditor editor;

                        @Override
                        public PropertyEditor getPropertyEditor() {
                            if (editor == null) {
                                editor = new SplitPositionEditor();
                            }
                            return editor;
                        }

                        @Override
                        public String getValue() {
                            return position;
                        }

                        @Override
                        public void setValue(String value) {
                            String oldValue = getValue();
                            position = value;
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
            return position;
        }

        @Override
        public SplitLayoutConstraints cloneConstraints() {
            return new SplitLayoutConstraints(position);
        }
    }

    static class SplitPositionEditor extends PropertyEditorSupport {

        private final String[] values = {
            JSplitPane.LEFT,
            JSplitPane.RIGHT,
            JSplitPane.TOP,
            JSplitPane.BOTTOM
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
    }
}
