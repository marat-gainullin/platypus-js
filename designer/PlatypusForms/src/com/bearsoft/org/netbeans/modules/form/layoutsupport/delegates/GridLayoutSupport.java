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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.BeanInfo;
import org.openide.util.ImageUtilities;

/**
 * Support class for GridLayout. This is an example of very simple layout with
 * no constraints; just basic drag & drop is implemented.
 *
 * @author Tran Duc Trung, Tomas Pavek
 */
public class GridLayoutSupport extends AbstractLayoutSupport {

    /**
     * The icon for GridLayout.
     */
    private static final String iconURL =
            "com/bearsoft/org/netbeans/modules/form/beaninfo/swing/gridLayout.gif"; // NOI18N
    /**
     * The icon for GridLayout.
     */
    private static final String icon32URL =
            "com/bearsoft/org/netbeans/modules/form/beaninfo/swing/gridLayout32.gif"; // NOI18N
    
    private FormProperty<?>[] properties;
    
    @Override
    protected FormProperty<?>[] getProperties() {
        if(properties == null)
            properties = new FormProperty[]{
                new FormProperty<Integer>(
                "rows", // NOI18N
                Integer.TYPE,
                getBundle().getString("PROP_rows"), // NOI18N
                getBundle().getString("HINT_rows")) {

                    @Override
                    public Integer getValue() {
                        return ((GridLayout)getRadLayout().getBeanInstance()).getRows();
                    }

                    @Override
                    public void setValue(Integer aValue) {
                        int oldValue = getValue();
                        int rows = aValue != null ? aValue : 0;
                        ((GridLayout)getRadLayout().getBeanInstance()).setRows(rows);
                        propertyValueChanged(oldValue, rows);
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
                "columns", // NOI18N
                Integer.TYPE,
                getBundle().getString("PROP_cols"), // NOI18N
                getBundle().getString("HINT_cols")) {

                    @Override
                    public Integer getValue() {
                        return ((GridLayout)getRadLayout().getBeanInstance()).getColumns();
                    }

                    @Override
                    public void setValue(Integer aValue) {
                        int oldValue = getValue();
                        int cols = aValue != null ? aValue : 0;
                        ((GridLayout)getRadLayout().getBeanInstance()).setColumns(cols);
                        propertyValueChanged(oldValue, cols);
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
                "hgap", // NOI18N
                Integer.TYPE,
                getBundle().getString("PROP_hgap"), // NOI18N
                getBundle().getString("HINT_hgap")) {

                    @Override
                    public Integer getValue() {
                        return ((GridLayout)getRadLayout().getBeanInstance()).getHgap();
                    }

                    @Override
                    public void setValue(Integer aValue) {
                        int oldValue = getValue();
                        int hgap = aValue != null ? aValue : 0;
                        ((GridLayout)getRadLayout().getBeanInstance()).setHgap(hgap);
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
                        return ((GridLayout)getRadLayout().getBeanInstance()).getVgap();
                    }

                    @Override
                    public void setValue(Integer aValue) {
                        int oldValue = getValue();
                        int vgap = aValue != null ? aValue : 0;
                        ((GridLayout)getRadLayout().getBeanInstance()).setVgap(vgap);
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
     * Gets the supported layout manager class - GridLayout.
     *
     * @return the class supported by this delegate
     */
    @Override
    public Class<?> getSupportedClass() {
        return GridLayout.class;
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
        if (!(containerDelegate.getLayout() instanceof GridLayout)) {
            return -1;
        }

        Component[] components = containerDelegate.getComponents();
        GridLayout layout = (GridLayout) containerDelegate.getLayout();
        int nrows = layout.getRows();
        int ncols = layout.getColumns();

        if ((nrows <= 0 && ncols <= 0) || components.length == 0) {
            return components.length;
        }

        if (nrows != 0) {
            ncols = (components.length + nrows - 1) / nrows;
        } else {
            nrows = (components.length + ncols - 1) / ncols;
        }

        Dimension sz = containerDelegate.getSize();
        Insets insets = containerDelegate.getInsets();
        sz.width -= insets.left + insets.right;
        sz.height -= insets.top + insets.bottom;

        int colwidth = sz.width / ncols;
        if (colwidth <= 0) {
            assistantParams = components.length;
            return components.length;
        }
        int col = (posInCont.x - insets.left + colwidth / 2) / colwidth;

        int rowheight = sz.height / nrows;
        if (rowheight <= 0) {
            assistantParams = components.length;
            return components.length;
        }
        int row = (posInCont.y - insets.top) / rowheight;

        int newIndex = row * ncols + col;
        newIndex = newIndex >= components.length ? components.length : newIndex;
        assistantParams = newIndex;
        return newIndex;
    }

    @Override
    protected LayoutManager cloneLayoutInstance(Container container, Container containerDelegate) throws Exception {
        GridLayout layout = (GridLayout)getRadLayout().getBeanInstance();
        return new GridLayout(layout.getRows(), layout.getColumns(), layout.getHgap(), layout.getVgap());
    }
    
    private int assistantParams;

    @Override
    public String getAssistantContext() {
        return "gridLayout"; // NOI18N
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
     * @param containerDelegate effective container delegate of the container;
     * for layout managers we always use container delegate instead of the
     * container
     * @param component the real component being dragged, not needed here
     * @param newConstraints component layout constraints to be presented; not
     * used for GridLayout
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
        if (containerDelegate.getLayout() instanceof GridLayout) {
            Component[] components = containerDelegate.getComponents();
            GridLayout layout = (GridLayout) containerDelegate.getLayout();
            int dx = 12 + layout.getHgap() / 2;
            int x = 0, w = 24, y = 0, h = 0;

            if ((newIndex <= 0) || ((components.length == 1) && (components[0] == component))) {
                if ((components.length > 1) || ((components.length == 1) && (components[0] != component))) {
                    Component comp = components[0];
                    if (comp == component) {
                        comp = components[1];
                    }
                    Rectangle b = comp.getBounds();
                    x = b.x - dx;
                    y = b.y;
                    h = b.height;
                } else {
                    Insets ins = containerDelegate.getInsets();
                    x = ins.left + 1;
                    w = containerDelegate.getWidth() - ins.right - ins.left - 2;
                    y = ins.top + 1;
                    h = containerDelegate.getHeight() - ins.bottom - ins.top - 2;
                }
            } else if ((newIndex >= components.length)
                    || ((newIndex == components.length - 1) && (components[newIndex] == component))) {
                Component comp = components[components.length - 1];
                if (comp == component) {
                    comp = components[components.length - 2];
                }
                Rectangle b = comp.getBounds();
                x = b.x + b.width - dx;
                y = b.y;
                h = b.height;
            } else {
                Component comp = components[newIndex];
                if (comp == component) {
                    comp = components[newIndex + 1];
                }
                Rectangle b = comp.getBounds();
                x = b.x - dx;
                y = b.y;
                h = b.height;
            }

            g.drawRect(x, y, w, h);
            return true;
        } else {
            return false;
        }
    }    
}
