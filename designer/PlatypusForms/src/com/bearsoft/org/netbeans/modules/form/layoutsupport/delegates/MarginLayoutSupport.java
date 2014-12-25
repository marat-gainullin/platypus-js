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
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADComponentCreator;
import com.bearsoft.org.netbeans.modules.form.RADVisualContainer;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.AbstractLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutConstraints;
import com.eas.client.forms.layouts.Margin;
import com.eas.client.forms.layouts.MarginConstraints;
import com.eas.client.forms.layouts.MarginLayout;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
public class MarginLayoutSupport extends AbstractLayoutSupport {

    /**
     * The icon for MarginLayout.
     */
    private static final String iconURL =
            "com/bearsoft/org/netbeans/modules/form/layoutsupport/resources/AbsoluteLayout.gif"; // NOI18N
    /**
     * The icon for MarginLayout.
     */
    private static final String icon32URL =
            "com/bearsoft/org/netbeans/modules/form/layoutsupport/resources/AbsoluteLayout32.gif"; // NOI18N
    private static final FormLoaderSettings formSettings = FormLoaderSettings.getInstance();
    final static private String stateIllegalMessage = "Отсутствуют необходимые значения для вычисления  {0}";

    /**
     * Gets the supported layout manager class - AbsoluteLayout.
     *
     * @return the class supported by this delegate
     */
    @Override
    public Class<?> getSupportedClass() {
        return MarginLayout.class;
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
        if (currentConstraints != null && components != null && components.length > 0) {
            //Container cont = components[0].getParent();
            //int contW = cont.getWidth();
            //int contH = cont.getHeight();

            for (int i = 0; i < currentConstraints.length; i++) {
                MarginLayoutConstraints mlc = (MarginLayoutConstraints) currentConstraints[i];
                Rectangle bounds = components[i].getBounds();
                //Dimension prefSize = components[i].getPreferredSize();
                int x = bounds.x;
                int y = bounds.y;
                int w = bounds.width;
                int h = bounds.height;
                if (mlc == null) {
                    currentConstraints[i] = new MarginLayoutConstraints(new Margin(x, true), new Margin(y, true), null, null, new Margin(w, true), new Margin(h, true));
                } else {
                    MarginConstraints mc = (MarginConstraints) currentConstraints[i].getConstraintsObject();
                    mc.setLeft(new Margin(x, true));
                    mc.setTop(new Margin(y, true));
                    mc.setWidth(new Margin(w, true));
                    mc.setHeight(new Margin(h, true));
                    mc.setRight(null);
                    mc.setBottom(null);
                }
            }
        }
    }

    /**
     * This method calculates layout constraints for a component dragged over a
     * container (or just for mouse cursor being moved over container, without
     * any component).
     *
     * @param aContainer
     * @param aContainerDelegate effective container delegate of the container
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
    public LayoutConstraints<?> getNewConstraints(Container aContainer,
            Container aContainerDelegate,
            Component component,
            int index,
            Point posInCont,
            Point posInComp) {
        int x = posInCont.x;
        int y = posInCont.y;
        int w;
        int h;

        Container container = aContainerDelegate != null ? aContainerDelegate : aContainer;
        Dimension containerSize = container.getSize();

        MarginLayoutConstraints constraints = (MarginLayoutConstraints) getConstraints(index);

        if (component != null) {
            Dimension size = component.getSize();
            Dimension prefSize = component.getPreferredSize();

            int currentW = constraints != null ? constraints.getWidth(container) : prefSize.width;
            int currentH = constraints != null ? constraints.getHeight(container) : prefSize.height;
            w = size.width > 0 ? size.width : currentW;
            h = size.height > 0 ? size.height : currentH;

        } else {
            if (constraints != null) {
                w = constraints.getWidth(container);
                h = constraints.getHeight(container);
            } else {// acceptNewComponents() have to take care of theese -1 values
                w = -1;
                h = -1;
            }
        }

        if (posInComp != null) {
            x -= posInComp.x;
            y -= posInComp.y;
        }

        if (formSettings.getApplyGridToPosition()) {
            x = snapToGrid(x, formSettings.getGridX());
            y = snapToGrid(y, formSettings.getGridY());
        }

        assistantParams = new Object[]{x, y};
        if (constraints == null) {
            Margin mLeft = new Margin(x, true);
            Margin mTop = new Margin(y, true);
            Margin mRight = null;
            Margin mBottom = null;
            if (x > containerSize.width - containerSize.width / 3) {
                mLeft = null;
                mRight = new Margin(containerSize.width - x - w, true);
            }
            if (y > containerSize.height - containerSize.height / 3) {
                mTop = null;
                mBottom = new Margin(containerSize.height - y - h, true);
            }
            return new MarginLayoutConstraints(
                    mLeft,
                    mTop,
                    mRight,
                    mBottom,
                    new Margin(w, true), new Margin(h, true));
        } else {
            //формирование абсолютных координат
            return mutateConstraints(container, constraints, x, y, w, h);
        }
    }
    private Object[] assistantParams;

    @Override
    public String getAssistantContext() {
        return "marginLayout"; // NOI18N
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
     * @param aContainer
     * @param component the real component being dragged, can be null
     * @param aContainerDelegate
     * @param newConstraints component layout constraints to be presented
     * @param newIndex component's index position to be presented; not used for
     * AbsoluteLayout
     * @param g Graphics object for painting (with color and line style set)
     * @return whether any feedback was painted (true in this case)
     */
    @Override
    public boolean paintDragFeedback(Container aContainer,
            Container aContainerDelegate,
            Component component,
            LayoutConstraints<?> newConstraints,
            int newIndex,
            Graphics g) {
        Container container = aContainerDelegate != null ? aContainerDelegate : aContainer;
        Rectangle r = ((MarginLayoutConstraints) newConstraints).getBounds(container);
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

        //добавлено
        component.setBounds(r); //установить размер компонента
        drawLinesNearComponent(g, container, component);   //рисовать линии между компонентами      
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
     * @param aContainer
     * @param component real component being resized
     * @param aContainerDelegate
     * @param index position of the component in its container
     * @param originalBounds
     * @param sizeChanges Insets object with size differences
     * @param posInCont position of mouse in the container delegate
     * @return component layout constraints for resized component; null if
     * resizing is not possible or not implemented
     */
    @Override
    public LayoutConstraints<?> getResizedConstraints(Container aContainer,
            Container aContainerDelegate,
            Component component,
            int index,
            Rectangle originalBounds,
            Insets sizeChanges,
            Point posInCont) {
        Container container = aContainerDelegate != null ? aContainerDelegate : aContainer;
        int x1 = originalBounds.x - sizeChanges.left;
        int y1 = originalBounds.y - sizeChanges.top;
        int x2 = x1 + originalBounds.width + sizeChanges.left + sizeChanges.right;
        int y2 = y1 + originalBounds.height + sizeChanges.top + sizeChanges.bottom;

        if (formSettings.getApplyGridToPosition()) {
            if (sizeChanges.left != 0) {
                x1 = snapToGrid(x1, formSettings.getGridX());
            }
            if (sizeChanges.top != 0) {
                y1 = snapToGrid(y1, formSettings.getGridY());
            }
        }
        if (formSettings.getApplyGridToSize()) {
            if (sizeChanges.right != 0) {
                x2 = snapToGrid(x2, formSettings.getGridX());
            }
            if (sizeChanges.bottom != 0) {
                y2 = snapToGrid(y2, formSettings.getGridY());
            }
        }
        int w = x2 - x1;
        int h = y2 - y1;
        return mutateConstraints(container, (MarginLayoutConstraints) getConstraints(index), x1, y1, w >= 0 ? w : 0, h >= 0 ? h : 0);
    }

    /**
     * This method is called to get a default component layout constraints
     * metaobject in case it is not provided (e.g. in addComponents method).
     *
     * @return the default LayoutConstraints object for the supported layout
     */
    @Override
    protected LayoutConstraints<?> createDefaultConstraints() {
        return new MarginLayoutConstraints(new Margin(10, true), new Margin(10, true), new Margin(10, true), new Margin(10, true), new Margin(10, true), new Margin(10, true));
    }

    private static int snapToGrid(int size, int step) {
        if (step <= 0) {
            return size;
        }
        int mod = size % step;
        return mod >= step / 2 ? size + step - mod : size - mod;
    }

    @Override
    public void acceptNewComponents(RADComponent<?>[] comps, LayoutConstraints<?>[] constraints, int index) {
        super.acceptNewComponents(comps, constraints, index);
        for (int i = 0; i < constraints.length; i++) {
            RADComponent<?> radComp = comps[i];
            Component comp = (Component) radComp.getBeanInstance();
            if (constraints[i] != null) {
                Dimension size = comp.getSize();
                MarginConstraints mConstraints = (MarginConstraints) constraints[i].getConstraintsObject();
                if (mConstraints.getHeight() != null && mConstraints.getHeight().value == -1) {
                    mConstraints.getHeight().value = size.height;
                    if (mConstraints.getBottom() != null) {
                        mConstraints.getBottom().value -= 1;
                        mConstraints.getBottom().value -= size.height;
                    }
                }
                if (mConstraints.getWidth() != null && mConstraints.getWidth().value == -1) {
                    mConstraints.getWidth().value = size.width;
                    if (mConstraints.getRight() != null) {
                        mConstraints.getRight().value -= 1;
                        mConstraints.getRight().value -= size.width;
                    }
                }
            } else {
                Dimension size = RADComponentCreator.prepareDefaultLayoutSize(comp, radComp instanceof RADVisualContainer<?>);
                if (size == null) {
                    size = comp.getPreferredSize();
                }
                constraints[i] = createDefaultConstraints();
                MarginConstraints mConstraints = (MarginConstraints) constraints[i].getConstraintsObject();
                mConstraints.getHeight().value = size.height;
                mConstraints.getWidth().value = size.width;
            }
        }
    }

    //проверка свойств при корректирвке
    @Override
    public void acceptComponentLayoutChange(int index, PropertyChangeEvent ev)
            throws PropertyVetoException {
        LayoutConstraints<?> constr = getConstraints(index);
        if (constr == null) {
            throw new PropertyVetoException("Constraints == null", ev);
        }
        if (!(constr instanceof MarginLayoutConstraints)) {
            throw new PropertyVetoException("Constraints не типа MarginConstraints ", ev);
        }
    }

    //возвращает расстояние между компонентами
    public Pair getDistance(Graphics g, Container container, Rectangle r1, Rectangle r2) {
        //компоненты пересекаются - расстояние = 0
        if (!r1.intersects(r2)) {
            Point p1 = null, p2 = null;
            int min = Integer.MAX_VALUE;
            Point pmin1 = null, pmin2 = null;
            for (int i = 0; i < 4; i++) {
                if (i == 0) {
                    p1 = new Point(r1.x, r1.y);
                }
                if (i == 1) {
                    p1 = new Point(r1.x, r1.y + r1.height);
                }
                if (i == 2) {
                    p1 = new Point(r1.x + r1.width, r1.y);
                }
                if (i == 3) {
                    p1 = new Point(r1.x + r1.width, r1.y + r1.height);
                }
                for (int j = 0; j < 4; j++) {
                    if (j == 0) {
                        p2 = new Point(r2.x, r2.y);
                    }
                    if (j == 1) {
                        p2 = new Point(r2.x, r2.y + r2.height);
                    }
                    if (j == 2) {
                        p2 = new Point(r2.x + r2.width, r2.y);
                    }
                    if (j == 3) {
                        p2 = new Point(r2.x + r2.width, r2.y + r2.height);
                    }

                    double d1 = p1.x - p2.x;
                    double d2 = p1.y - p2.y;
                    double dist = Math.sqrt(d1 * d1 + d2 * d2);
                    int l = (int) Math.round(dist);
                    if (l < min) {
                        min = l;
                        pmin1 = p1;
                        pmin2 = p2;
                    }
                }
            }
            assert pmin1 != null && pmin2 != null;
            Pair pret = new Pair(pmin1, pmin2);//прямоугольник(диагональ) расположенный между компонентами
            return pret;
        } else {
            return null;
        }
    }
    //рисует линии 

    public void drawLines(Graphics g, Pair pret, Container container, Rectangle r1, Rectangle r2) {
        Rectangle rrW = null, rrH = null;
        if (pret.p1.x < pret.p2.x && pret.p1.y < pret.p2.y) {//направление слева-направо
            rrH = new Rectangle(pret.p1.x, 0, pret.p2.x - pret.p1.x, container.getHeight());
            rrW = new Rectangle(0, pret.p1.y, container.getWidth(), pret.p2.y - pret.p1.y);
        } else if (pret.p1.x > pret.p2.x && pret.p1.y > pret.p2.y) {
            rrH = new Rectangle(pret.p2.x, 0, pret.p1.x - pret.p2.x, container.getHeight());
            rrW = new Rectangle(0, pret.p2.y, container.getWidth(), pret.p1.y - pret.p2.y);
        } //направление справа-налево
        else if (pret.p1.x < pret.p2.x && pret.p1.y > pret.p2.y) {
            rrH = new Rectangle(pret.p1.x, 0, pret.p2.x, container.getHeight());
            rrW = new Rectangle(0, pret.p2.y, container.getWidth(), pret.p1.y - pret.p2.y);
        } else if (pret.p1.x > pret.p2.x && pret.p1.y < pret.p2.y) {
            rrH = new Rectangle(pret.p2.x, 0, pret.p1.x - pret.p2.x, container.getHeight());
            rrW = new Rectangle(0, pret.p1.y, container.getWidth(), pret.p2.y - pret.p1.y);
        }
        if (rrH != null && !rrH.intersects(r1) && !rrH.intersects(r2)) {//вертикальный прямоугольник не пересекается с компонентом
            Graphics2D gd2 = (Graphics2D) g;
            Color oldColor = gd2.getColor();
            Stroke oldStroke = gd2.getStroke();
            //создаем "кисть" для рисования
            BasicStroke pen1 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 5.0f, new float[]{5.0f, 2.0f}, 0.0f);
            //BasicStroke pen1 = new BasicStroke(0.5f); //толщина линии 2
            gd2.setStroke(pen1);
            gd2.setColor(formSettings.getGuidingLineColor());
            int mi = getMinH(r1, r2) - formSettings.getGridY();
            int ma = getMaxH(r1, r2) + formSettings.getGridY();
            int w = 30;

            if (rrH.getWidth() <= formSettings.getGridX()) {//рисуем вертикальные линии
                gd2.drawLine(rrH.x, mi, rrH.x, ma);
            } else if (rrH.getWidth() <= formSettings.getGridX() * 2) {//рисуем вертикальные линии
                gd2.drawLine(rrH.x, mi, rrH.x, ma);
                gd2.drawLine(rrH.x + (w / 3), mi, rrH.x + (w / 3), ma);
            } else if (rrH.getWidth() <= formSettings.getGridX() * 3) {//рисуем вертикальные линии
                gd2.drawLine(rrH.x, mi, rrH.x, ma);
                gd2.drawLine(rrH.x + w / 3, mi, rrH.x + w / 3, ma);
                gd2.drawLine(rrH.x + (w / 3) * 2, mi, rrH.x + (w / 3) * 2, ma);
            }
            gd2.setColor(oldColor);
            gd2.setStroke(oldStroke);
        }
        if (rrW != null && !rrW.intersects(r1) && !rrW.intersects(r2)) {//горизонтальный прямоугольник не пересекается с компонентом
            Graphics2D gd2 = (Graphics2D) g;
            Color oldColor = gd2.getColor();
            Stroke oldStroke = gd2.getStroke();
            //создаем "кисть" для рисования
            BasicStroke pen1 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 5.0f, new float[]{5.0f, 2.0f}, 0.0f);
            //BasicStroke pen1 = new BasicStroke(0.5f); //толщина линии 2
            gd2.setStroke(pen1);
            gd2.setColor(formSettings.getGuidingLineColor());
            int mi = getMinW(r1, r2) - formSettings.getGridX();
            int ma = getMaxW(r1, r2) + formSettings.getGridX();
            int h = 30;


            if (rrW.getHeight() <= formSettings.getGridY()) {//рисуем горизонтальные линии                                      
                gd2.drawLine(mi, rrW.y, ma, rrW.y);
            } else if (rrW.getHeight() <= formSettings.getGridY() * 2) {//рисуем горизонтальные линии                   
                gd2.drawLine(mi, rrW.y, ma, rrW.y);
                gd2.drawLine(mi, rrW.y + (h / 3), ma, rrW.y + (h / 3));
            } else if (rrW.getHeight() <= formSettings.getGridY() * 3) {//рисуем горизонтальные линии                   
                gd2.drawLine(mi, rrW.y, ma, rrW.y);
                gd2.drawLine(mi, rrW.y + h / 3, ma, rrW.y + h / 3);
                gd2.drawLine(mi, rrW.y + (h / 3) * 2, ma, rrW.y + (h / 3) * 2);
            }
            gd2.setColor(oldColor);
            gd2.setStroke(oldStroke);
        }
    }
    //рисует линии 

    public void drawLinesW(Rectangle rrW, Graphics g, Pair pret, Container container, Rectangle r1, Rectangle r2) {
        if (rrW != null && !rrW.intersects(r1) && !rrW.intersects(r2)) {//горизонтальный прямоугольник не пересекается с компонентом
            Graphics2D gd2 = (Graphics2D) g;
            Color oldColor = gd2.getColor();
            Stroke oldStroke = gd2.getStroke();
            try {
                //создаем "кисть" для рисования
                BasicStroke pen1 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 5.0f, new float[]{5.0f, 2.0f}, 0.0f);
                //BasicStroke pen1 = new BasicStroke(0.5f); //толщина линии 2
                gd2.setStroke(pen1);
                gd2.setColor(formSettings.getGuidingLineColor());
                int mi = getMinW(r1, r2) - formSettings.getGridX();
                int ma = getMaxW(r1, r2) + formSettings.getGridX();
                int h = formSettings.getGridY() * 3;

                if (rrW.height <= formSettings.getGridY()) {//рисуем горизонтальные линии                                      
                    gd2.drawLine(mi, rrW.y, ma, rrW.y);
                } else if (rrW.height <= formSettings.getGridY() * 2) {//рисуем горизонтальные линии                   
                    gd2.drawLine(mi, rrW.y, ma, rrW.y);
                    gd2.drawLine(mi, rrW.y + (h / 3), ma, rrW.y + (h / 3));
                } else if (rrW.height <= formSettings.getGridY() * 3) {//рисуем горизонтальные линии                   
                    gd2.drawLine(mi, rrW.y, ma, rrW.y);
                    gd2.drawLine(mi, rrW.y + h / 3, ma, rrW.y + h / 3);
                    gd2.drawLine(mi, rrW.y + (h / 3) * 2, ma, rrW.y + (h / 3) * 2);
                }
            } finally {
                gd2.setColor(oldColor);
                gd2.setStroke(oldStroke);
            }
        }
    }
    //рисует линии 

    public void drawLinesH(Rectangle rrH, Graphics g, Pair pret, Container container, Rectangle r1, Rectangle r2) {
        if (rrH != null && !rrH.intersects(r1) && !rrH.intersects(r2)) {//вертикальный прямоугольник не пересекается с компонентом
            Graphics2D gd2 = (Graphics2D) g;
            Color oldColor = gd2.getColor();
            Stroke oldStroke = gd2.getStroke();
            try {
                //создаем "кисть" для рисования
                BasicStroke pen1 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 5.0f, new float[]{5.0f, 2.0f}, 0.0f);
                //BasicStroke pen1 = new BasicStroke(0.5f); //толщина линии 2
                gd2.setStroke(pen1);
                gd2.setColor(formSettings.getGuidingLineColor());
                int mi = getMinH(r1, r2) - formSettings.getGridY();
                int ma = getMaxH(r1, r2) + formSettings.getGridY();
                int w = formSettings.getGridX() * 3;

                if (rrH.getWidth() <= formSettings.getGridX()) {//рисуем вертикальные линии
                    gd2.drawLine(rrH.x, mi, rrH.x, ma);
                } else if (rrH.getWidth() <= formSettings.getGridX() * 2) {//рисуем вертикальные линии
                    gd2.drawLine(rrH.x, mi, rrH.x, ma);
                    gd2.drawLine(rrH.x + (w / 3), mi, rrH.x + (w / 3), ma);
                } else if (rrH.getWidth() <= formSettings.getGridX() * 3) {//рисуем вертикальные линии
                    gd2.drawLine(rrH.x, mi, rrH.x, ma);
                    gd2.drawLine(rrH.x + w / 3, mi, rrH.x + w / 3, ma);
                    gd2.drawLine(rrH.x + (w / 3) * 2, mi, rrH.x + (w / 3) * 2, ma);
                }
            } finally {
                gd2.setColor(oldColor);
                gd2.setStroke(oldStroke);
            }
        }

    }
    //рисует линии между двумя компонентами

    public void drawBoundLines(Graphics g, Rectangle r1, Rectangle r2) {
        int mih = getMinH(r1, r2) - formSettings.getGridY();
        int mah = getMaxH(r1, r2) + formSettings.getGridY();
        int miw = getMinW(r1, r2) - formSettings.getGridX();
        int maw = getMaxW(r1, r2) + formSettings.getGridX();
        Graphics2D gd2 = (Graphics2D) g;
        Color oldColor = gd2.getColor();
        Stroke oldStroke = gd2.getStroke();
        //создаем "кисть" для рисования
        BasicStroke pen1 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 5.0f, new float[]{5.0f, 2.0f}, 0.0f);
        //BasicStroke pen1 = new BasicStroke(0.5f); //толщина линии 2
        gd2.setStroke(pen1);
        gd2.setColor(formSettings.getGuidingLineColor());
        if (r1.y == r2.y) {
            gd2.drawLine(miw, r1.y, maw, r2.y);
        }
        if (r1.y == r2.y + r2.height) {
            gd2.drawLine(miw, r1.y, maw, r1.y);
        }
        if (r1.y + r1.height == r2.y) {
            gd2.drawLine(miw, r2.y, maw, r2.y);
        }
        if (r1.y + r1.height == r2.y + r2.height) {
            gd2.drawLine(miw, r2.y + r2.height, maw, r2.y + r2.height);
        }

        if (r1.x == r2.x) {
            gd2.drawLine(r1.x, mih, r2.x, mah);
        }
        if (r1.x == r2.x + r2.width) {
            gd2.drawLine(r1.x, mih, r1.x, mah);
        }
        if (r1.x + r1.width == r2.x) {
            gd2.drawLine(r2.x, mih, r2.x, mah);
        }
        if (r1.x + r1.width == r2.x + r2.width) {
            gd2.drawLine(r2.x + r2.width, mih, r2.x + r2.width, mah);
        }
    }
    //проверяет наличие пересечений с другими компонентами

    public boolean isIntersect(Rectangle r, Component comp, Rectangle r1, Rectangle r2) {
        Container cont = comp.getParent();
        Component[] comps = cont.getComponents();
        int count = comps.length;
        boolean isIntersect = false;
        for (int i = 0; i < count; i++) {
            if (!r.equals(r1) && !r.equals(r2)) {
                isIntersect = true;
            }
        }
        return isIntersect;
    }
    //границы горизонтальных прямоугольноков

    public int getMinW(Rectangle r1, Rectangle r2) {
        return Math.min(r1.x, r2.x);
    }

    public int getMaxW(Rectangle r1, Rectangle r2) {
        return Math.max(r1.x + r1.width, r2.x + r2.width);
    }
    //границы  вертикальных прямоугольноков

    public int getMinH(Rectangle r1, Rectangle r2) {
        return Math.min(r1.y, r2.y);
    }

    public int getMaxH(Rectangle r1, Rectangle r2) {
        return Math.max(r1.y + r1.height, r2.y + r2.height);
    }

    // возвращает массив ближайших компонентов
    public void drawLinesNearComponent(Graphics g, Container aContainer, Component aComponent) {
        Rectangle contBounds = aContainer.getBounds();
        Rectangle compBounds = aComponent.getBounds();
        java.util.List<Rectangle> compsBounds = new ArrayList<>();
        for (Component comp : aContainer.getComponents()) {
            if (aComponent != comp) //исключить сравнение с собой
            {
                compsBounds.add(comp.getBounds());
            }
        }
        compsBounds.add(new Rectangle(0, 0, 0, contBounds.height));
        compsBounds.add(new Rectangle(contBounds.width, 0, 0, contBounds.height));
        compsBounds.add(new Rectangle(0, 0, contBounds.width, 0));
        compsBounds.add(new Rectangle(0, contBounds.height, contBounds.width, 0));
        int minH = Integer.MAX_VALUE, minW = Integer.MAX_VALUE, minWH = Integer.MAX_VALUE;
        Rectangle minh = null, minw = null, minwh = null;
        Pair pminh = null, pminw = null, pminwh = null;
        Rectangle rrH = null, rrW = null, rrHH = null, rrWW = null;
        for (Rectangle neighbourBounds : compsBounds) {
            Pair pret = getDistance(g, aContainer, compBounds, neighbourBounds);
            if (pret != null) {
                if (pret.p1.x < pret.p2.x && pret.p1.y < pret.p2.y) {//направление слева-направо
                    rrH = new Rectangle(pret.p1.x, 0, pret.p2.x - pret.p1.x, contBounds.height);
                    rrW = new Rectangle(0, pret.p1.y, contBounds.width, pret.p2.y - pret.p1.y);
                } else if (pret.p1.x > pret.p2.x && pret.p1.y > pret.p2.y) {
                    rrH = new Rectangle(pret.p2.x, 0, pret.p1.x - pret.p2.x, contBounds.height);
                    rrW = new Rectangle(0, pret.p2.y, contBounds.width, pret.p1.y - pret.p2.y);
                } else if (pret.p1.x < pret.p2.x && pret.p1.y > pret.p2.y) {//направление справа-налево
                    rrH = new Rectangle(pret.p1.x, 0, pret.p2.x - pret.p1.x, contBounds.height);
                    rrW = new Rectangle(0, pret.p2.y, contBounds.width, pret.p1.y - pret.p2.y);
                } else if (pret.p1.x > pret.p2.x && pret.p1.y < pret.p2.y) {
                    rrH = new Rectangle(pret.p2.x, 0, pret.p1.x - pret.p2.x, contBounds.height);
                    rrW = new Rectangle(0, pret.p1.y, contBounds.width, pret.p2.y - pret.p1.y);
                }
                //ищем два ближайших компонента
                int dx = Math.abs(pret.p1.x - pret.p2.x);
                int dy = Math.abs(pret.p1.y - pret.p2.y);

                if (dy < minH) {
                    minH = dy;
                    minh = neighbourBounds;
                    pminh = pret;
                    rrWW = rrW;
                }
                if (dx < minW) {
                    minW = dx;
                    minw = neighbourBounds;
                    pminw = pret;
                    rrHH = rrH;
                }
            }
        }
        if (pminw != null && rrWW != null) {//рисуем горизонтальные линии               
            drawLinesW(rrWW, g, pminw, aContainer, compBounds, minw);
        }
        if (pminh != null && rrHH != null) {//рисуем вертикальные линии               
            drawLinesH(rrHH, g, pminh, aContainer, compBounds, minh);
        }
        drawBoundLines(g, compBounds, minw);
        drawBoundLines(g, compBounds, minh);
    }

    //формирует координаты  c изменением переданного constraints
    public static MarginLayoutConstraints mutateConstraints(Container container, MarginLayoutConstraints aSource, int x, int y, int w, int h) {
        //формирование абсолютных координат
        MarginConstraints innnerSource = aSource.getConstraintsObject();
        Margin mleft = innnerSource.getLeft() != null ? innnerSource.getLeft().copy() : null;
        Margin mtop = innnerSource.getTop() != null ? innnerSource.getTop().copy() : null;
        Margin mright = innnerSource.getRight() != null ? innnerSource.getRight().copy() : null;
        Margin mbottom = innnerSource.getBottom() != null ? innnerSource.getBottom().copy() : null;
        Margin mwidth = innnerSource.getWidth() != null ? innnerSource.getWidth().copy() : null;
        Margin mheight = innnerSource.getHeight() != null ? innnerSource.getHeight().copy() : null;
        MarginLayoutConstraints dest = new MarginLayoutConstraints(mleft, mtop, mright, mbottom, mwidth, mheight);
        mutate(dest.getConstraintsObject(), container.getWidth(), container.getHeight(), x, y, w, h);
        return dest;
    }

    public static void mutate(MarginConstraints aConstraints, int containerWidth, int containerHeight, int x, int y, int w, int h) {
        Margin mleft = aConstraints.getLeft();
        Margin mtop = aConstraints.getTop();
        Margin mright = aConstraints.getRight();
        Margin mbottom = aConstraints.getBottom();
        Margin mwidth = aConstraints.getWidth();
        Margin mheight = aConstraints.getHeight();
        if (mleft != null) {
            mleft.setPlainValue(x, containerWidth);
        }
        if (mright != null) {
            mright.setPlainValue(containerWidth - x - w, containerWidth);
        }
        if (mwidth != null) {
            mwidth.setPlainValue(w, containerWidth);
        }
        if (mtop != null) {
            mtop.setPlainValue(y, containerHeight);
        }
        if (mbottom != null) {
            mbottom.setPlainValue(containerHeight - y - h, containerHeight);
        }
        if (mheight != null) {
            mheight.setPlainValue(h, containerHeight);
        }
    }

    /**
     * LayoutConstraints implementation class for MarginConstraints.
     */
    public static class MarginLayoutConstraints implements LayoutConstraints<MarginConstraints> {

        MarginConstraints constraints;
        private FormProperty<Margin> mtop;
        private FormProperty<Margin> mbottom;
        private FormProperty<Margin> mleft;
        private FormProperty<Margin> mright;
        private FormProperty<Margin> mwidth;
        private FormProperty<Margin> mheight;
        private FormProperty<?>[] properties;

        public MarginLayoutConstraints(Margin left, Margin top, Margin right, Margin bottom, Margin width, Margin height) {
            this(new MarginConstraints(left, top, right, bottom, width, height));
        }

        public MarginLayoutConstraints(MarginConstraints aConstraints) {
            super();
            constraints = aConstraints;
        }
        
        public FormProperty<Margin> getMLeft() {
            checkProperties();
            return mleft;
        }

        public FormProperty<Margin> getMBottom() {
            checkProperties();
            return mbottom;
        }

        public FormProperty<Margin> getMRight() {
            checkProperties();
            return mright;
        }

        public FormProperty<Margin> getMTop() {
            checkProperties();
            return mtop;
        }

        public FormProperty<Margin> getMWidth() {
            checkProperties();
            return mwidth;
        }

        public FormProperty<Margin> getMHeight() {
            checkProperties();
            return mheight;
        }

        @Override
        public FormProperty<?>[] getProperties() {
            checkProperties();
            return properties;
        }

        public void checkProperties() {
            if (properties == null) {
                properties = createProperties();
            }
        }
        
        @Override
        public MarginConstraints getConstraintsObject() {
            return constraints;
        }

        @Override
        public MarginLayoutConstraints cloneConstraints() {
            return new MarginLayoutConstraints(
                    constraints.getLeft() != null ? constraints.getLeft().copy() : null,
                    constraints.getTop() != null ? constraints.getTop().copy() : null,
                    constraints.getRight() != null ? constraints.getRight().copy() : null,
                    constraints.getBottom() != null ? constraints.getBottom().copy() : null,
                    constraints.getWidth() != null ? constraints.getWidth().copy() : null,
                    constraints.getHeight() != null ? constraints.getHeight().copy() : null);
        }

        // возвращает rectangle для указанного контейнера
        public Rectangle getBounds(Container cont) {
            int left, top, right, bottom, w, h;
            left = getLeft(cont);
            top = getTop(cont);
            right = getRight(cont);
            bottom = getBottom(cont);
            w = getWidth(cont);
            h = getHeight(cont);

            return new Rectangle(left, top, w, h);
        }

        public int getWidth(Container cont) {
            int w = 0;
            if (constraints.getWidth() != null) {
                Margin mrg = constraints.getWidth();
                if (!mrg.absolute) {//параметр в %                     
                    float k = ((float) mrg.value) / 100;
                    w = Math.round(k * cont.getWidth());
                } else {
                    w = mrg.value;
                }
            } else {
                if (constraints.getLeft() != null && constraints.getRight() != null) {
                    w = cont.getWidth() - getLeft(cont) - getRight(cont);
                } else {
                    throw new IllegalStateException(String.format(stateIllegalMessage, "ширины"));
                }
            }
            return w;

        }

        public int getHeight(Container cont) {
            int h = 0;
            if (constraints.getHeight() != null) {
                Margin mrg = constraints.getHeight();
                if (!mrg.absolute) {//параметр в % 
                    float k = ((float) mrg.value) / 100;
                    h = Math.round(k * cont.getHeight());
                } else {
                    h = mrg.value;
                }
            } else {
                if (constraints.getTop() != null && constraints.getBottom() != null) {
                    h = cont.getHeight() - getTop(cont) - getBottom(cont);
                } else {
                    throw new IllegalStateException(String.format(stateIllegalMessage, "высоты"));
                }
            }
            return h;
        }

        public int getLeft(Container cont) {
            int left = 0;
            if (constraints.getLeft() != null) {
                Margin mrg = constraints.getLeft();
                if (!mrg.absolute) {//параметр в % 
                    float k = ((float) mrg.value) / 100;
                    left = Math.round(k * cont.getWidth());
                } else {
                    left = mrg.value;
                }
            } else {
                if (constraints.getRight() != null && constraints.getWidth() != null) {
                    left = cont.getWidth() - getRight(cont) - getWidth(cont);
                } else {
                    throw new IllegalStateException(String.format(stateIllegalMessage, "левой координаты"));
                }
            }
            return left;
        }

        public int getTop(Container cont) {
            int top = 0;
            if (constraints.getTop() != null) {
                Margin mrg = constraints.getTop();
                if (!mrg.absolute) {//параметр в % 
                    float k = ((float) mrg.value) / 100;
                    top = Math.round(k * cont.getHeight());
                } else {
                    top = mrg.value;
                }
            } else {
                if (constraints.getBottom() != null && constraints.getHeight() != null) {
                    top = cont.getHeight() - getBottom(cont) - getHeight(cont);
                } else {
                    throw new IllegalStateException(String.format(stateIllegalMessage, "верхней координаты"));
                }
            }
            return top;
        }

        public int getRight(Container cont) {
            int right = 0;
            if (constraints.getRight() != null) {
                Margin mrg = constraints.getRight();
                if (!mrg.absolute) {//параметр в % 
                    float k = ((float) mrg.value) / 100;
                    right = Math.round(k * cont.getWidth());
                } else {
                    right = mrg.value;
                }
                if (constraints.getLeft() != null && constraints.getWidth() != null) {//высота перекрывает
                    right = cont.getWidth() - getLeft(cont) - getWidth(cont);
                }
            } else {
                if (constraints.getLeft() != null && constraints.getWidth() != null) {
                    right = cont.getWidth() - getLeft(cont) - getWidth(cont);
                } else {
                    throw new IllegalStateException(String.format(stateIllegalMessage, "правой координаты"));
                }
            }
            return right;
        }

        public int getBottom(Container cont) {
            int bottom = 0;
            int top = 0;
            if (constraints.getBottom() != null) {
                Margin mrg = constraints.getBottom();
                if (!mrg.absolute) {//параметр в % 
                    float k = ((float) mrg.value) / 100;
                    bottom = Math.round(k * cont.getHeight());
                } else {
                    bottom = mrg.value;
                }
                if (constraints.getTop() != null && constraints.getHeight() != null) {//высота перекрывает
                    bottom = cont.getHeight() - getTop(cont) - getHeight(cont);
                }
            } else {
                if (constraints.getTop() != null && constraints.getHeight() != null) {
                    bottom = cont.getHeight() - getTop(cont) - getHeight(cont);
                } else {
                    throw new IllegalStateException(String.format(stateIllegalMessage, "нижней координаты"));
                }
                //если есть высота bottom неважен
            }
            return bottom;
        }

        protected FormProperty<?>[] createProperties() {
            mleft = new FormProperty<Margin>("left", // NOI18N
                    Margin.class,
                    getBundle().getString("PROPM_posl"), // NOI18N
                    getBundle().getString("HINTM_posl")) {
                protected MarginEditor editor;

                @Override
                public PropertyEditor getPropertyEditor() {
                    if (editor == null) {
                        editor = new MarginEditor();
                    }
                    return editor;
                }

                @Override
                public Margin getValue() throws IllegalAccessException, InvocationTargetException {
                    return constraints.getLeft();
                }

                @Override
                public void setValue(Margin aValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (aValue != null || (constraints.getWidth() != null && constraints.getRight() != null)) {
                        Margin oldValue = getValue();
                        constraints.setLeft(aValue);
                        propertyValueChanged(oldValue, aValue);
                    }
                }

                @Override
                public boolean supportsDefaultValue() {
                    return true;
                }

                @Override
                public Margin getDefaultValue() {
                    return null;
                }
            };
            mtop = new FormProperty<Margin>("top", // NOI18N
                    Margin.class,
                    getBundle().getString("PROPM_post"), // NOI18N
                    getBundle().getString("HINTM_post")) {
                protected MarginEditor editor;

                @Override
                public PropertyEditor getPropertyEditor() {
                    if (editor == null) {
                        editor = new MarginEditor();
                    }
                    return editor;
                }

                @Override
                public Margin getValue() throws IllegalAccessException, InvocationTargetException {
                    return constraints.getTop();
                }

                @Override
                public void setValue(Margin aValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (aValue != null || (constraints.getHeight() != null && constraints.getBottom() != null)) {
                        Margin oldValue = getValue();
                        constraints.setTop(aValue);
                        propertyValueChanged(oldValue, aValue);
                    }
                }

                @Override
                public boolean supportsDefaultValue() {
                    return true;
                }

                @Override
                public Margin getDefaultValue() {
                    return null;
                }
            };
            mright = new FormProperty<Margin>("right", // NOI18N
                    Margin.class,
                    getBundle().getString("PROPM_posr"), // NOI18N
                    getBundle().getString("HINTM_posr")) {
                protected MarginEditor editor;

                @Override
                public PropertyEditor getPropertyEditor() {
                    if (editor == null) {
                        editor = new MarginEditor();
                    }
                    return editor;
                }

                @Override
                public Margin getValue() throws IllegalAccessException, InvocationTargetException {
                    return constraints.getRight();
                }

                @Override
                public void setValue(Margin aValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (aValue != null || (constraints.getWidth() != null && constraints.getLeft() != null)) {
                        Margin oldValue = getValue();
                        constraints.setRight(aValue);
                        propertyValueChanged(oldValue, aValue);
                    }
                }

                @Override
                public boolean supportsDefaultValue() {
                    return true;
                }

                @Override
                public Margin getDefaultValue() {
                    return null;
                }
            };
            mbottom = new FormProperty<Margin>("bottom", // NOI18N
                    Margin.class,
                    getBundle().getString("PROPM_posb"), // NOI18N
                    getBundle().getString("HINTM_posb")) { // NOI18N
                protected MarginEditor editor;

                @Override
                public PropertyEditor getPropertyEditor() {
                    if (editor == null) {
                        editor = new MarginEditor();
                    }
                    return editor;
                }

                @Override
                public Margin getValue() {
                    return constraints.getBottom();
                }

                @Override
                public void setValue(Margin aValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (aValue != null || (constraints.getHeight() != null && constraints.getTop() != null)) {
                        Margin oldValue = getValue();
                        constraints.setBottom(aValue);
                        propertyValueChanged(oldValue, aValue);
                    }
                }

                @Override
                public boolean supportsDefaultValue() {
                    return true;
                }

                @Override
                public Margin getDefaultValue() {
                    return null;
                }
            };
            mwidth = new FormProperty<Margin>("width", // NOI18N
                    Margin.class,
                    getBundle().getString("PROPM_width"), // NOI18N
                    getBundle().getString("HINTM_width")) { // NOI18N
                protected MarginEditor editor;

                @Override
                public PropertyEditor getPropertyEditor() {
                    if (editor == null) {
                        editor = new MarginEditor();
                    }
                    return editor;
                }

                @Override
                public Margin getValue() {
                    return constraints.getWidth();
                }

                @Override
                public void setValue(Margin aValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (aValue != null || (constraints.getLeft() != null && constraints.getRight() != null)) {
                        Margin oldValue = getValue();
                        constraints.setWidth(aValue);
                        propertyValueChanged(oldValue, aValue);
                    }
                }

                @Override
                public boolean supportsDefaultValue() {
                    return true;
                }

                @Override
                public Margin getDefaultValue() {
                    return null;
                }
            };
            mheight = new FormProperty<Margin>("height", // NOI18N
                    Margin.class,
                    getBundle().getString("PROPM_height"), // NOI18N
                    getBundle().getString("HINTM_height")) { // NOI18N
                protected MarginEditor editor;

                @Override
                public PropertyEditor getPropertyEditor() {
                    if (editor == null) {
                        editor = new MarginEditor();
                    }
                    return editor;
                }

                @Override
                public Margin getValue() {
                    return constraints.getHeight();
                }

                @Override
                public void setValue(Margin aValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (aValue != null || (constraints.getTop() != null && constraints.getBottom() != null)) {
                        Margin oldValue = getValue();
                        constraints.setHeight(aValue);
                        propertyValueChanged(oldValue, aValue);
                    }
                }

                @Override
                public boolean supportsDefaultValue() {
                    return true;
                }

                @Override
                public Margin getDefaultValue() {
                    return null;
                }
            };
            return new FormProperty<?>[]{mleft, mtop, mright, mbottom, mwidth, mheight};
        }
    }

    public static class Pair {

        public Point p1, p2;

        public Pair(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    protected static class MarginEditor extends PropertyEditorSupport {

        @Override
        public String getAsText() {
            return super.getAsText();
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            Margin m = Margin.parse(text);
            setValue(m);
        }

        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return value != null ? value.toString() : null;
        }
    }
}
