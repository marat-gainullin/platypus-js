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

import com.bearsoft.org.netbeans.modules.form.FormLoaderSettings;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.FormPropertyContext;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.*;
import java.awt.*;
import java.beans.*;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import org.openide.util.ImageUtilities;

/**
 * Support class for AbsoluteLayout - for absolute positioning and sizing of
 * components using AbsoluteConstraints. This is an example of support for
 * layout manager using component constraints as complex objects initialized by
 * constructor with parameters mapped to properties. AbsoluteLayoutSupport is
 * also the superclass of NullLayoutSupport and JLayeredPane support, so it is a
 * bit more complicated than would be necessary for simple implementation.
 *
 * @author Tomas Pavek
 */
public class AbsoluteLayoutSupport extends AbstractLayoutSupport {

    /**
     * The icon for AbsoluteLayout.
     */
    private static String iconURL =
            "com/bearsoft/org/netbeans/modules/form/layoutsupport/resources/AbsoluteLayout.gif"; // NOI18N
    /**
     * The icon for AbsoluteLayout.
     */
    private static String icon32URL =
            "com/bearsoft/org/netbeans/modules/form/layoutsupport/resources/AbsoluteLayout32.gif"; // NOI18N
    private static FormLoaderSettings formSettings = FormLoaderSettings.getInstance();

    /**
     * Gets the supported layout manager class - AbsoluteLayout.
     *
     * @return the class supported by this delegate
     */
    @Override
    public Class<?> getSupportedClass() {
        return AbsoluteLayout.class;
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
     * This method is called when switching layout - giving an opportunity to
     * convert the previous constrainst of components to constraints of the new
     * layout (this layout). For AbsoluteLayout, we can simply create new
     * constraints from positions and sizes of real components.
     *
     * @param previousConstraints [input] layout constraints of components in
     * the previous layout
     * @param currentConstraints [output] array of converted constraints for the
     * new layout - to be filled
     * @param components [input] real components in a real container having the
     * previous layout
     */
    @Override
    public void convertConstraints(LayoutConstraints<?>[] previousConstraints,
            LayoutConstraints<?>[] currentConstraints,
            Component[] components) {
        if (currentConstraints == null || components == null) {
            return;
        }

        for (int i = 0; i < currentConstraints.length; i++) {
            if (currentConstraints[i] == null) {
                Rectangle bounds = components[i].getBounds();
                Dimension prefSize = components[i].getPreferredSize();
                int x = bounds.x;
                int y = bounds.y;
                int w = computeConstraintSize(bounds.width, -1, prefSize.width);
                int h = computeConstraintSize(bounds.height, -1, prefSize.height);

                currentConstraints[i] = new AbsoluteLayoutConstraints(x, y, w, h);
            }
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
     * @param index position (index) of the component in its container; -1 if
     * there's no dragged component
     * @param posInCont position of mouse in the container delegate
     * @param posInComp position of mouse in the dragged component; null if
     * there's no dragged component
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
        int x = posInCont.x;
        int y = posInCont.y;
        int w = -1;
        int h = -1;

        LayoutConstraints<?> constr = getConstraints(index);

        if (component != null) {
            int currentW;
            int currentH;

            if (constr instanceof AbsoluteLayoutConstraints) {
                currentW = ((AbsoluteLayoutConstraints) constr).w;
                currentH = ((AbsoluteLayoutConstraints) constr).h;
            } else {
                currentW = -1;
                currentH = -1;
            }

            Dimension size = component.getSize();
            Dimension prefSize = component.getPreferredSize();

            w = computeConstraintSize(size.width, currentW, prefSize.width);
            h = computeConstraintSize(size.height, currentH, prefSize.height);
        }

        if (posInComp != null) {
            x -= posInComp.x;
            y -= posInComp.y;
        }

        if (formSettings.getApplyGridToPosition()) {
            x = computeGridSize(x, formSettings.getGridX());
            y = computeGridSize(y, formSettings.getGridY());
        }

        assistantParams = new Object[]{x, y};
        return createNewConstraints(constr, x, y, w, h);
    }
    private Object[] assistantParams;

    @Override
    public String getAssistantContext() {
        return "absoluteLayout"; // NOI18N
    }

    @Override
    public Object[] getAssistantParams() {
        return assistantParams;
    }

    /**
     * This method paints a dragging feedback for a component dragged over a
     * container (or just for mouse cursor being moved over container, without
     * any component). For AbsoluteLayout, it simply paints a rectangle
     * corresponding to the component position and size.
     *
     * @param container instance of a real container over/in which the component
     * is dragged
     * @param containerDelegate effective container delegate of the container
     * (for layout managers we always use container delegate instead of the
     * container)
     * @param component the real component being dragged, can be null
     * @param newConstraints component layout constraints to be presented
     * @param newIndex component's index position to be presented; not used for
     * AbsoluteLayout
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
        Rectangle r = ((AbstractAbsoluteLayoutConstraints) newConstraints).getBounds();
        int w = r.width;
        int h = r.height;

        if (w == -1 || h == -1) {
            // JInternalFrame.getPreferredSize() behaves suspiciously
            Dimension pref = component instanceof javax.swing.JInternalFrame
                    ? component.getSize() : component.getPreferredSize();
            if (w == -1) {
                w = pref.width;
            }
            if (h == -1) {
                h = pref.height;
            }
        }

        if (w < 4) {
            w = 4;
        }
        if (h < 4) {
            h = 4;
        }

        g.drawRect(r.x, r.y, w, h);

        return true;
    }

    /**
     * Provides resizing options for given component. It can combine the
     * bit-flag constants RESIZE_UP, RESIZE_DOWN, RESIZE_LEFT, RESIZE_RIGHT.
     *
     * @param container instance of a real container in which the component is
     * to be resized
     * @param containerDelegate effective container delegate of the container
     * (e.g. like content pane of JFrame)
     * @param component real component to be resized
     * @param index position of the component in its container
     * @return resizing options for the component; 0 if no resizing is possible
     */
    @Override
    public int getResizableDirections(Container container,
            Container containerDelegate,
            Component component,
            int index) {
        return RESIZE_UP | RESIZE_DOWN | RESIZE_LEFT | RESIZE_RIGHT;
    }

    /**
     * This method should calculate layout constraints for a component being
     * resized.
     *
     * @param container instance of a real container in which the component is
     * resized
     * @param containerDelegate effective container delegate of the container
     * (e.g. like content pane of JFrame)
     * @param component real component being resized
     * @param index position of the component in its container
     * @param sizeChanges Insets object with size differences
     * @param posInCont position of mouse in the container delegate
     * @return component layout constraints for resized component; null if
     * resizing is not possible or not implemented
     */
    @Override
    public LayoutConstraints<?> getResizedConstraints(Container container,
            Container containerDelegate,
            Component component,
            int index,
            Rectangle originalBounds,
            Insets sizeChanges,
            Point posInCont) {
        int x, y, w, h;
        x = originalBounds.x;
        y = originalBounds.y;
        w = originalBounds.width;
        h = originalBounds.height;

        Dimension prefSize = component.getPreferredSize();
        int currentW, currentH;

        LayoutConstraints<?> constr = getConstraints(index);
        if (constr instanceof AbsoluteLayoutConstraints) {
            Rectangle r = ((AbsoluteLayoutConstraints) constr).getBounds();
            currentW = r.width;
            currentH = r.height;
        } else {
            currentW = computeConstraintSize(w, -1, prefSize.width);
            currentH = computeConstraintSize(h, -1, prefSize.height);
        }

        int x2 = x + w;
        int y2 = y + h;

        if (sizeChanges.left + sizeChanges.right == 0) {
            w = currentW; // no change
        } else { // compute resized width and x coordinate
            w += sizeChanges.left + sizeChanges.right;
            w = w <= 0 ? -1 : computeConstraintSize(w, currentW, prefSize.width);

            if (w > 0) {
                if (formSettings.getApplyGridToSize()) {
                    int gridW = computeGridSize(w, formSettings.getGridX());
                    x -= sizeChanges.left
                            + (gridW - w) * sizeChanges.left
                            / (sizeChanges.left + sizeChanges.right);
                    w = gridW;
                }
            } else if (sizeChanges.left != 0) {
                x = x2 - prefSize.width;
            }
        }

        if (sizeChanges.top + sizeChanges.bottom == 0) {
            h = currentH; // no change
        } else { // compute resized height and y coordinate
            h += sizeChanges.top + sizeChanges.bottom;
            h = h <= 0 ? -1 : computeConstraintSize(h, currentH, prefSize.height);

            if (h > 0) {
                if (formSettings.getApplyGridToSize()) {
                    int gridH = computeGridSize(h, formSettings.getGridY());
                    y -= sizeChanges.top
                            + (gridH - h) * sizeChanges.top
                            / (sizeChanges.top + sizeChanges.bottom);
                    h = gridH;
                }
            } else if (sizeChanges.top != 0) {
                y = y2 - prefSize.height;
            }
        }

        return createNewConstraints(constr, x, y, w, h);
    }

    // -------
    /**
     * This method is called from readComponentCode method to read layout
     * constraints of a component from code (AbsoluteConstraints in this case).
     *
     * @param constrExp CodeExpression object of the constraints (taken from add
     * method in the code)
     * @param constrCode CodeGroup to be filled with the relevant constraints
     * initialization code; not needed here because AbsoluteConstraints object
     * is represented only by a single code expression (based on constructor)
     * and no statements
     * @param compExp CodeExpression of the component for which the constraints
     * are read (not needed here)
     * @return LayoutConstraints based on information read form code
     * @Override protected LayoutConstraints readConstraintsCode(CodeExpression
     * constrExp, CodeGroup constrCode, CodeExpression compExp) {
     * AbsoluteLayoutConstraints constr = new AbsoluteLayoutConstraints(0, 0,
     * -1, -1);
     *
     * CodeExpression[] params = constrExp.getOrigin().getCreationParameters();
     * if (params.length == 4) { // reading is done in AbsoluteLayoutConstraints
     * constr.readPropertyExpressions(params, 0); }
     *
     * return constr; }
     */
    /**
     * Called from createComponentCode method, creates code for a component
     * layout constraints (opposite to readConstraintsCode).
     *
     * @param constrCode CodeGroup to be filled with constraints code; not
     * needed here because AbsoluteConstraints object is represented only by a
     * single constructor code expression and no statements
     * @param constr layout constraints metaobject representing the constraints
     * @param compExp CodeExpression object representing the component; not
     * needed here
     * @return created CodeExpression representing the layout constraints
     * @Override protected CodeExpression createConstraintsCode(CodeGroup
     * constrCode, LayoutConstraints constr, CodeExpression compExp, int index)
     * { if (!(constr instanceof AbsoluteLayoutConstraints)) { return null; }
     *
     * AbsoluteLayoutConstraints absConstr = (AbsoluteLayoutConstraints) constr;
     * // code expressions for constructor parameters are created in //
     * AbsoluteLayoutConstraints CodeExpression[] params =
     * absConstr.createPropertyExpressions( getCodeStructure(), 0); return
     * getCodeStructure().createExpression(getConstraintsConstructor(), params);
     * }
     */
    /**
     * This method is called to get a default component layout constraints
     * metaobject in case it is not provided (e.g. in addComponents method).
     *
     * @return the default LayoutConstraints object for the supported layout
     */
    @Override
    protected LayoutConstraints<?> createDefaultConstraints() {
        return new AbsoluteLayoutConstraints(0, 0, -1, -1);
    }

    // --------
    protected LayoutConstraints<?> createNewConstraints(
            LayoutConstraints<?> currentConstr,
            int x, int y, int w, int h) {
        return new AbsoluteLayoutConstraints(x, y, w, h);
    }

    private static int computeConstraintSize(int newSize,
            int currSize,
            int prefSize) {
        return newSize != -1 && (newSize != prefSize
                || (currSize != -1 && currSize == prefSize))
                ? newSize : -1;
    }

    private static int computeGridSize(int size, int step) {
        if (step <= 0) {
            return size;
        }
        int mod = size % step;
        return mod >= step / 2 ? size + step - mod : size - mod;
    }
    /*
     private static Constructor getConstraintsConstructor() {
     if (constrConstructor == null) {
     try {
     constrConstructor = AbsoluteConstraints.class.getConstructor(
     new Class<?>[]{Integer.TYPE, Integer.TYPE,
     Integer.TYPE, Integer.TYPE});
     } catch (NoSuchMethodException ex) { // should not happen
     ErrorManager.getDefault().notify(ex);
     }
     }
     return constrConstructor;
     }
     */
    // -------------

    /**
     * LayoutConstraints implementation class for AbsoluteConstraints.
     */
    public static class AbsoluteLayoutConstraints extends AbstractAbsoluteLayoutConstraints implements LayoutConstraints<AbsoluteConstraints> {
        
        public AbsoluteLayoutConstraints(int aX, int aY, int aW, int aH) {
            super(aX, aY, aW, aH);
        }
        
        @Override
        public AbsoluteConstraints getConstraintsObject() {
            return new AbsoluteConstraints(x, y, w, h);
        }

        @Override
        public AbsoluteLayoutConstraints cloneConstraints() {
            return new AbsoluteLayoutConstraints(x, y, w, h);
        }
    }
    
    public static class AbstractAbsoluteLayoutConstraints {

        int x, y, w, h; // position and size
        private FormProperty<?>[] properties;
        boolean nullMode;
        Component refComponent;

        public AbstractAbsoluteLayoutConstraints(int aX, int aY, int aW, int aH) {
            x = aX;
            y = aY;
            w = aW;
            h = aH;
        }

        public FormProperty<?>[] getProperties() {
            if (properties == null) {
                properties = createProperties();
            }
            return properties;
        }
        
        /**
         * No undo redo will be generated
         */
        public void offset(int aX, int aY)
        {
            x += aX;
            y += aY;
        }
        
        // --------
        public Rectangle getBounds() {
            return new Rectangle(x, y, w, h);
        }

        protected FormProperty<?>[] createProperties() {
            return new FormProperty<?>[]{
                        new FormProperty<Integer>("AbsoluteLayoutConstraints posx", // NOI18N
                        Integer.TYPE,
                        getBundle().getString("PROP_posx"), // NOI18N
                        getBundle().getString("HINT_posx")) { // NOI18N
                            @Override
                            public Integer getValue() {
                                return x;
                            }

                            @Override
                            public void setValue(Integer value) {
                                Integer oldValue = x;
                                x = value;
                                propertyValueChanged(oldValue, value);
                            }

                            @Override
                            public void setPropertyContext(FormPropertyContext ctx) { // disabling this method due to limited persistence
                            } // capabilities (compatibility with previous versions)
                        },
                        new FormProperty<Integer>("AbsoluteLayoutConstraints posy", // NOI18N
                        Integer.TYPE,
                        getBundle().getString("PROP_posy"), // NOI18N
                        getBundle().getString("HINT_posy")) { // NOI18N
                            @Override
                            public Integer getValue() {
                                return y;
                            }

                            @Override
                            public void setValue(Integer value) {
                                Integer oldValue = y;
                                y = value;
                                propertyValueChanged(oldValue, value);
                            }

                            @Override
                            public String getJavaInitializationString() {
                                if (nullMode && refComponent != null && !isChanged()) {
                                    return Integer.toString(refComponent.getPreferredSize().height);
                                }
                                return super.getJavaInitializationString();
                            }

                            @Override
                            public void setPropertyContext(FormPropertyContext ctx) { // disabling this method due to limited persistence
                            } // capabilities (compatibility with previous versions)
                        },
                        new FormProperty<Integer>("AbsoluteLayoutConstraints width", // NOI18N
                        Integer.TYPE,
                        getBundle().getString("PROP_width"), // NOI18N
                        getBundle().getString("HINT_width")) { // NOI18N
                            protected SizeEditor editor;

                            @Override
                            public PropertyEditor getPropertyEditor() {
                                if (editor == null) {
                                    editor = new SizeEditor();
                                }
                                return editor;
                            }

                            @Override
                            public Integer getValue() {
                                return w;
                            }

                            @Override
                            public void setValue(Integer value) {
                                Integer oldValue = w;
                                w = value;
                                propertyValueChanged(oldValue, value);
                            }

                            @Override
                            public boolean supportsDefaultValue() {
                                return true;
                            }

                            @Override
                            public Integer getDefaultValue() {
                                return -1;
                            }

                            @Override
                            public Object getValue(String key) {
                                if ("canEditAsText".equals(key)) // NOI18N
                                {
                                    return Boolean.TRUE;
                                }
                                return super.getValue(key);
                            }

                            @Override
                            public String getJavaInitializationString() {
                                if (nullMode && refComponent != null && !isChanged()) {
                                    return Integer.toString(refComponent.getPreferredSize().width);
                                }
                                return super.getJavaInitializationString();
                            }

                            @Override
                            public void setPropertyContext(FormPropertyContext ctx) { // disabling this method due to limited persistence
                            } // capabilities (compatibility with previous versions)
                        },
                        new FormProperty<Integer>("AbsoluteLayoutConstraints height", // NOI18N
                        Integer.TYPE,
                        getBundle().getString("PROP_height"), // NOI18N
                        getBundle().getString("HINT_height")) {
                            protected SizeEditor editor;

                            @Override
                            public PropertyEditor getPropertyEditor() {
                                if (editor == null) {
                                    editor = new SizeEditor();
                                }
                                return editor;
                            }

                            @Override
                            public Integer getValue() {
                                return h;
                            }

                            @Override
                            public void setValue(Integer value) {
                                Integer oldValue = getValue();
                                h = value.intValue();
                                propertyValueChanged(oldValue, value);
                            }

                            @Override
                            public boolean supportsDefaultValue() {
                                return true;
                            }

                            @Override
                            public Integer getDefaultValue() {
                                return -1;
                            }

                            @Override
                            public Object getValue(String key) {
                                if ("canEditAsText".equals(key)) // NOI18N
                                {
                                    return Boolean.TRUE;
                                }
                                return super.getValue(key);
                            }

                            @Override
                            public String getJavaInitializationString() {
                                if (nullMode && refComponent != null && !isChanged()) {
                                    return Integer.toString(refComponent.getPreferredSize().height);
                                }
                                return super.getJavaInitializationString();
                            }

                            @Override
                            public void setPropertyContext(FormPropertyContext ctx) { // disabling this method due to limited persistence
                            } // capabilities (compatibility with previous versions)
                        }
                    };
        }
    }

    // -----------
    /**
     * PropertyEditor for width and height properties of
     * AbsoluteLayoutConstraints.
     */
    public static final class SizeEditor extends PropertyEditorSupport {

        final Integer prefValue = new Integer(-1);
        final String prefTag = getBundle().getString("VALUE_preferred"); // NOI18N

        @Override
        public String[] getTags() {
            return new String[]{prefTag};
        }

        @Override
        public String getAsText() {
            Object value = getValue();
            return prefValue.equals(value)
                    ? prefTag : value.toString();
        }

        @Override
        public void setAsText(String str) {
            if (prefTag.equals(str)) {
                setValue(prefValue);
            } else {
                try {
                    setValue(new Integer(Integer.parseInt(str)));
                } catch (NumberFormatException e) {
                } // ignore
            }
        }

        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return value != null ? value.toString() : null;
        }
    }
}
