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

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.org.netbeans.modules.form.assistant.AssistantModel;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.*;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.menu.MenuEditLayer;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteItem;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteUtils;
import com.eas.client.forms.components.CheckBox;
import com.eas.client.forms.components.RadioButton;
import com.eas.client.forms.containers.ButtonGroup;
import com.eas.client.forms.containers.ScrollPane;
import com.eas.client.forms.containers.TabbedPane;
import com.eas.client.forms.menu.MenuBar;
import java.awt.AWTKeyStroke;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import org.netbeans.spi.palette.PaletteController;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

/**
 * A transparent layer (glass pane) handling user operations in designer (mouse
 * and keyboard events) and painting selection and drag&drop feedback.
 * Technically, this is a layer in PlatypusFormLayoutView, placed over
 * ComponentLayer.
 *
 * @author Tran Duc Trung, Tomas Pavek
 */
public class HandleLayer extends JPanel {

    public class MouseProcessor extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)
                    && !draggingEnded && !endDragging(null)) {
                showContextMenu(e.getPoint());
            }
            highlightPanel(e, true);
            e.consume();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (HandleLayer.this.isVisible()) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (formDesigner.getDesignerMode() == PlatypusFormLayoutView.MODE_SELECT
                            && !draggingEnded && !endDragging(e)) {   // there was no dragging, so mouse release may have other meaning
                        boolean modifier = e.isControlDown() || e.isAltDown() || e.isShiftDown();
                        /*
                         if ((resizeType & DESIGNER_RESIZING) != 0
                         && e.getClickCount() == 2
                         && !modifier
                         && !viewOnly) {   // doubleclick on designer's resizing border
                         setUserDesignerSize();
                         } else 
                         */
                        if (prevLeftMousePoint != null
                                && prevLeftMousePoint.distance(e.getPoint()) <= 3
                                && !modifier) {   // second click on the same place in a component
                            RADComponent<?> radComp = getRadComponentAt(e.getPoint(), COMP_SELECTED);
                            if (radComp != null) {
                                formDesigner.startInPlaceEditing(radComp);
                            }
                        } else if (e.getClickCount() == 1
                                && !e.isAltDown()
                                && !e.isControlDown()) {   // plain click or shift click
                            if (mouseOnVisual(e.getPoint())) {
                                selectComponent(e, false);
                            } // otherwise Other Components node selected in mousePressed
                        }
                    }

                    prevLeftMousePoint = lastLeftMousePoint;
                    lastLeftMousePoint = null;
                }
                e.consume();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (formDesigner.getDesignerMode() == PlatypusFormLayoutView.MODE_ADD) {
                formDesigner.requestActive();
                PaletteItem item = PaletteUtils.getSelectedItem();
                if (formDesigner.getMenuEditLayer().isPossibleNewMenuComponent(item)) {
                    formDesigner.getMenuEditLayer().startNewMenuComponentPickAndPlop(item, e.getPoint());
                    return;
                }
                if (null != item) {
                    StatusDisplayer.getDefault().setStatusText(
                            FormUtils.getFormattedBundleString(
                                    "FMT_MSG_AddingComponent", // NOI18N
                                    new String[]{item.getNode().getDisplayName()}));
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (componentDrag != null && formDesigner.getDesignerMode() == PlatypusFormLayoutView.MODE_ADD) {
                componentDrag.move(null);
                repaint();
                StatusDisplayer.getDefault().setStatusText(""); // NOI18N
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (HandleLayer.this.isVisible()) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (formDesigner.getDesignerMode() != PlatypusFormLayoutView.MODE_SELECT) {
                        formDesigner.toggleSelectionMode(); // calls endDragging(null)
                        repaint();
                    } else if (endDragging(null)) { // there was dragging, now canceled
                        repaint();
                    } else if (!SwingUtilities.isLeftMouseButton(e)) {
                        // no dragging, ensure a component is selected for conext menu
                        if (!mouseOnVisual(e.getPoint())) {
                            selectOtherComponentsNode();
                        } else {
                            // [we used to only select the component if there was nothing selected
                            //  on current position, but changed to always select - #94543]
                            RADComponent<?> hitRadComp = selectComponent(e, true);
                            processMouseClickInLayoutSupport(hitRadComp, e);
                        }
                        draggingEnded = false; // reset flag preventing dragging from start
                    }
                    e.consume();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    lastLeftMousePoint = e.getPoint();
                    boolean modifier = e.isControlDown() || e.isAltDown() || e.isShiftDown();
                    if (formDesigner.getDesignerMode() == PlatypusFormLayoutView.MODE_SELECT) {
                        checkResizing(e);
                        if (!(e.isShiftDown() && e.isAltDown() && e.isControlDown())) {
                            if (!mouseOnVisual(lastLeftMousePoint)) {
                                if ((resizeType == 0) && (selectedComponentAt(lastLeftMousePoint, 0) == null)) {
                                    selectOtherComponentsNode();
                                }
                            } // Shift+left is reserved for interval or area selection,
                            // applied on mouse release or mouse dragged; ignore it here.
                            else if (resizeType == 0 // no resizing
                                    && (e.getClickCount() != 2 || !processDoubleClick(e)) // no doubleclick
                                    && (!e.isShiftDown() || e.isAltDown())) {
                                RADComponent<?> hitRadComp = selectComponent(e, true);
                                if (!modifier) { // plain single click
                                    processMouseClickInLayoutSupport(hitRadComp, e);
                                }
                            }
                        }
                        draggingEnded = false; // reset flag preventing dragging from start
                    } else if (!viewOnly) { // form can be modified
                        if (formDesigner.getDesignerMode() == PlatypusFormLayoutView.MODE_ADD) {
                            endDragging(e);
                            if (!e.isShiftDown()) {
                                formDesigner.toggleSelectionMode();
                            }
                            // otherwise stay in adding mode
                        }
                    }
                    e.consume();
                }
            }
        }

        // ---------
        // MouseMotionListener implementation
        @Override
        public void mouseDragged(MouseEvent e) {
            if (formDesigner.getDesignerMode() == PlatypusFormLayoutView.MODE_SELECT) {
                // dragging makes sense only selection mode
                Point p = e.getPoint();
                if (lastMousePosition != null) {
                    lastXPosDiff = p.x - lastMousePosition.x;
                    lastYPosDiff = p.y - lastMousePosition.y;
                }

                if (!draggingEnded && !anyDragger() && lastLeftMousePoint != null) { // no dragging yet
                    if (!viewOnly
                            && !e.isControlDown() && (!e.isShiftDown() || e.isAltDown())
                            && (resizeType != 0 || lastLeftMousePoint.distance(p) > 6)) {   // start component dragging
                        RADVisualComponent<?>[] draggedComps
                                = (resizeType & DESIGNER_RESIZING) == 0 ? getComponentsToDrag()
                                        : new RADVisualComponent<?>[]{formDesigner.getTopDesignComponent()};
                        if (draggedComps != null) {
                            if (resizeType == 0) {
                                componentDrag = new ExistingComponentDrag(
                                        draggedComps, lastLeftMousePoint, e.getModifiers());
                            } else {
                                componentDrag = new ResizeComponentDrag(
                                        draggedComps, lastLeftMousePoint, resizeType & ~DESIGNER_RESIZING);
                            }
                        }
                    }
                    if (componentDrag == null // component dragging has not started
                            && lastLeftMousePoint.distance(p) > 4
                            && !e.isAltDown() && !e.isControlDown()) {
                        // check for possible selection dragging
                        RADComponent<?> topComp = formDesigner.getTopDesignComponent();
                        RADComponent<?> comp = getRadComponentAt(lastLeftMousePoint, COMP_DEEPEST);
                        if (topComp != null
                                && (e.isShiftDown() || comp == null || comp == topComp || comp.getParentComponent() == null)) {
                            // start selection dragging
                            selectionDragger = new SelectionDragger(lastLeftMousePoint);
                        }
                    }
                }

                if (componentDrag != null) {
                    componentDrag.move(e);
                    highlightPanel(e, false);
                    repaint();
                } else if (selectionDragger != null) {
                    selectionDragger.drag(p);
                    repaint();
                }

                lastMousePosition = p;
                e.consume();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Point p = e.getPoint();
            if (lastMousePosition != null) {
                lastXPosDiff = p.x - lastMousePosition.x;
                lastYPosDiff = p.y - lastMousePosition.y;
            }
            if (formDesigner.getDesignerMode() == PlatypusFormLayoutView.MODE_ADD) {
                PaletteItem item = PaletteUtils.getSelectedItem();
                if (null == item) {
                    if (null != componentDrag) {
                        endDragging(e);
                    }
                    return;
                }
                if (componentDrag == null) {
                    // first move event, pre-create visual component to be added
                    if (item.getComponentClassName().indexOf('.') == -1) // Issue 79573
                    {
                        String message = FormUtils.getBundleString("MSG_DefaultPackageBean"); // NOI18N
                        NotifyDescriptor nd = new NotifyDescriptor.Message(message, NotifyDescriptor.WARNING_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                        formDesigner.toggleSelectionMode();
                        return;
                    }
                    componentDrag = new NewComponentDrag(item);
                }
                componentDrag.move(e);
                repaint();
            } else if (formDesigner.getDesignerMode() == PlatypusFormLayoutView.MODE_SELECT
                    && !anyDragger()) {
                checkResizing(e);
            }
            highlightPanel(e, false);
            lastMousePosition = p;
        }
    }
    // constants for mode parameter of getRadComponentAt(Point, int) method
    public static final int COMP_DEEPEST = 0; // get the deepest component (at given position)
    public static final int COMP_SELECTED = 1; // get the deepest selected component
    public static final int COMP_ABOVE_SELECTED = 2; // get the component above the deepest selected component
    public static final int COMP_UNDER_SELECTED = 3; // get the component under the deepest selected component
    private static final int DESIGNER_RESIZING = 256; // flag for resizeType
    private static MessageFormat resizingHintFormat;
    private static MessageFormat sizeHintFormat;
    private PlatypusFormLayoutView formDesigner;
    private boolean viewOnly;
    private ComponentDrag componentDrag;
    private JPanel dragPanel;
    private Point lastMousePosition;
    private int lastXPosDiff;
    private int lastYPosDiff;
    private Point lastLeftMousePoint;
    private Point prevLeftMousePoint;
    private boolean draggingEnded; // prevents dragging from starting inconveniently
    private int resizeType;
    private boolean draggingSuspended;
    private SelectionDragger selectionDragger;
    private Image resizeHandle;
    private DropTarget dropTarget;
    private NewComponentDropListener dropListener;
    private MouseProcessor mouseProcessor = new MouseProcessor();
    private static FormLoaderSettings formSettings = FormLoaderSettings.getInstance();

    // -------
    HandleLayer(PlatypusFormLayoutView aView) {
        super();
        formDesigner = aView;
        addMouseListener(mouseProcessor);
        addMouseMotionListener(mouseProcessor);
        setLayout(null);

        // Hack - the panel is used to ensure correct painting of dragged components
        dragPanel = new JPanel();
        dragPanel.setLayout(null);
        dragPanel.setBounds(-1, -1, 0, 0);
        add(dragPanel);

        // set Ctrl+TAB and Ctrl+Shift+TAB as focus traversal keys - to have
        // TAB and Shift+TAB free for component selection
        Set<AWTKeyStroke> keys = new HashSet<>();
        keys.add(
                AWTKeyStroke.getAWTKeyStroke(9,
                        InputEvent.CTRL_DOWN_MASK,
                        true));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                keys);
        keys.clear();
        keys.add(
                AWTKeyStroke.getAWTKeyStroke(9,
                        InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK,
                        true));
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                keys);

        getAccessibleContext().setAccessibleName(
                FormUtils.getBundleString("ACSN_HandleLayer")); // NOI18N
        getAccessibleContext().setAccessibleDescription(
                FormUtils.getBundleString("ACSD_HandleLayer")); // NOI18N

        dropListener = new NewComponentDropListener();
        dropTarget = new DropTarget(this, dropListener);
    }

    public boolean isSuspended() {
        return draggingSuspended;
    }

    public void suspend() {
        draggingSuspended = true;
    }

    public void resume() {
        draggingSuspended = false;
    }

    public MouseProcessor getMouseProcessor() {
        return mouseProcessor;
    }

    //expose the drop listener so the MenuEditLayer can access it
    public DropTargetListener getNewComponentDropListener() {
        return dropListener;
    }

    void setViewOnly(boolean aValue) {
        if (viewOnly != aValue) {
            if (aValue) {
                dropTarget.removeDropTargetListener(dropListener);
            } else {
                try {
                    dropTarget.addDropTargetListener(dropListener);
                } catch (TooManyListenersException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
            viewOnly = aValue;
        }
    }

    private FormModel getFormModel() {
        return formDesigner.getFormModel();
    }

    private RADComponentCreator getComponentCreator() {
        return formDesigner.getFormModel().getComponentCreator();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (componentDrag != null) {
            if (!isSuspended()) {
                try {
                    FormLAF.setUseDesignerDefaults(getFormModel());
                    componentDrag.paintFeedback(g2);
                } finally {
                    FormLAF.setUseDesignerDefaults(null);
                }
            }
        } else { // just paint the selection of selected components
            g2.setColor(formSettings.getSelectionBorderColor());
            boolean painted = false;
            try {
                paintButtonGroups(g2);
                boolean inLayout = selectedComponentsInSameVisibleContainer();
                for (RADVisualComponent<?> radComp : formDesigner.getSelectedLayoutComponents()) {
                    RADVisualComponent<?> layoutRadComp = formDesigner.componentToLayoutComponent(radComp);
                    if (layoutRadComp != null) {
                        radComp = layoutRadComp;
                    }
                    paintSelection(g2, radComp, inLayout);
                }
                painted = true;
            } finally {
                // Make sure that problems in selection painting
                // doesn't cause endless stream of exceptions.
                if (!painted) {
                    formDesigner.clearSelection();
                }
            }
            if (selectionDragger != null) {
                Stroke oldStroke = g2.getStroke();
                g2.setStroke(getPaintStroke());
                selectionDragger.paintDragFeedback(g2);
                g2.setStroke(oldStroke);
            }
        }
    }
    protected static final int anchorArcSize = 5;

    /**
     * @param inLayout indicates whether to paint layout related decorations
     * (layout relations in container and resize handles)
     */
    private void paintSelection(Graphics2D g, RADVisualComponent<?> radComp, boolean inLayout) {
        Component component = formDesigner.getComponent(radComp);
        Container parent = component != null ? component.getParent() : null;

        if (parent != null && component != null && component.isShowing()) {
            Rectangle selRect = component.getBounds();
            Rectangle selParentRect = parent.getBounds();
            convertRectangleFromComponent(selRect, parent);
            convertRectangleFromComponent(selParentRect, parent.getParent());
            Rectangle visible = new Rectangle(0, 0, parent.getWidth(), parent.getHeight());
            visible = convertVisibleRectangleFromComponent(visible, parent);
            if (formDesigner.getTopDesignComponent() != radComp && radComp.getParentLayoutSupport() != null && radComp.getParentLayoutSupport().getLayoutDelegate() instanceof MarginLayoutSupport) {
                MarginLayoutSupport mLayoutSupport = (MarginLayoutSupport) radComp.getParentLayoutSupport().getLayoutDelegate();
                LayoutConstraints<?> constraints = mLayoutSupport.getConstraints(radComp.getComponentIndex());
                assert constraints instanceof MarginLayoutSupport.MarginLayoutConstraints;
                MarginLayoutSupport.MarginLayoutConstraints mlc = (MarginLayoutSupport.MarginLayoutConstraints) constraints;
                Color oldColor = g.getColor();
                g.setColor(formSettings.getGuidingLineColor());
                Stroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL, 0, new float[]{1, 3}, 0);
                Stroke oldStroke = g.getStroke();
                g.setStroke(stroke);
                Object oldHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                try {
                    if (mlc.getConstraintsObject().getLeft() != null) {
                        g.drawLine(selParentRect.x, selRect.y + selRect.height / 2, selRect.x, selRect.y + selRect.height / 2);
                        g.fillArc(selParentRect.x - anchorArcSize, selRect.y + selRect.height / 2 - anchorArcSize, anchorArcSize * 2, anchorArcSize * 2, -90, 180);
                    }
                    if (mlc.getConstraintsObject().getTop() != null) {
                        g.drawLine(selRect.x + selRect.width / 2 - 1, selParentRect.y, selRect.x + selRect.width / 2 - 1, selRect.y);
                        g.fillArc(selRect.x + selRect.width / 2 - anchorArcSize, selParentRect.y - anchorArcSize, anchorArcSize * 2, anchorArcSize * 2, -180, 180);
                    }
                    if (mlc.getConstraintsObject().getRight() != null) {
                        g.drawLine(selRect.x + selRect.width, selRect.y + selRect.height / 2, selParentRect.x + selParentRect.width, selRect.y + selRect.height / 2);
                        g.fillArc(selParentRect.x + selParentRect.width - anchorArcSize, selRect.y + selRect.height / 2 - anchorArcSize, anchorArcSize * 2, anchorArcSize * 2, 90, 180);
                    }
                    if (mlc.getConstraintsObject().getBottom() != null) {
                        g.drawLine(selRect.x + selRect.width / 2 - 1, selRect.y + selRect.height, selRect.x + selRect.width / 2 - 1, selParentRect.y + selParentRect.height);
                        g.fillArc(selRect.x + selRect.width / 2 - anchorArcSize, selParentRect.y + selParentRect.height - anchorArcSize, anchorArcSize * 2, anchorArcSize * 2, 0, 180);
                    }
                } finally {
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
                    g.setStroke(oldStroke);
                    g.setColor(oldColor);
                }
            }
            int resizable = 0;
            if (inLayout) {
                resizable = getComponentResizable(radComp);
            }
            if (resizable == 0) {
                selRect = selRect.intersection(visible);
            }
            int correction = formSettings.getSelectionBorderSize() % 2;
            int x = selRect.x - correction;
            int y = selRect.y - correction;
            int width = selRect.width + correction;
            int height = selRect.height + correction;
            Stroke oldStroke = g.getStroke();
            g.setStroke(getPaintStroke());
            g.drawRect(x, y, width, height);
            g.setStroke(oldStroke);
            if (inLayout) {
                Image lresizeHandle = resizeHandle();
                int iconHeight = lresizeHandle.getHeight(null);
                int iconWidth = lresizeHandle.getWidth(null);
                if ((resizable & LayoutSupportManager.RESIZE_LEFT) != 0) {
                    g.drawImage(lresizeHandle, x - iconWidth + 1, y + (height - iconHeight) / 2, null);
                    if ((resizable & LayoutSupportManager.RESIZE_UP) != 0) {
                        g.drawImage(lresizeHandle, x - iconWidth + 1, y - iconHeight + 1, null);
                    }
                    if ((resizable & LayoutSupportManager.RESIZE_DOWN) != 0) {
                        g.drawImage(lresizeHandle, x - iconWidth + 1, y + height, null);
                    }
                }
                if ((resizable & LayoutSupportManager.RESIZE_RIGHT) != 0) {
                    g.drawImage(lresizeHandle, x + width, y + (height - iconHeight) / 2, null);
                    if ((resizable & LayoutSupportManager.RESIZE_UP) != 0) {
                        g.drawImage(lresizeHandle, x + width, y - iconHeight + 1, null);
                    }
                    if ((resizable & LayoutSupportManager.RESIZE_DOWN) != 0) {
                        g.drawImage(lresizeHandle, x + width, y + height, null);
                    }
                }
                if ((resizable & LayoutSupportManager.RESIZE_UP) != 0) {
                    g.drawImage(lresizeHandle, x + (width - iconWidth) / 2, y - iconHeight + 1, null);
                }
                if ((resizable & LayoutSupportManager.RESIZE_DOWN) != 0) {
                    g.drawImage(lresizeHandle, x + (width - iconWidth) / 2, y + height, null);
                }
            }
        }
    }
    /**
     * Length of the (shortest) lines in <code>ButtonGroup</code> visualization.
     */
    private static final int BUTTON_GROUP_OFFSET = 5;
    /**
     * Determines whether the primary division (in visualization of
     * <code>ButtonGroup</code>s) should be into columns or rows.
     */
    private static final boolean BUTTON_GROUP_COLUMNS_FIRST = false;

    /**
     * Visualization of <code>ButtonGroup</code>s.
     *
     * @param g graphics object.
     */
    private void paintButtonGroups(Graphics2D g) {
        Map<Object, java.util.List<AbstractButton>> buttonGroups = null;

        // Find buttonGroups of all selected components.
        for (RADComponent<?> radComp : formDesigner.getSelectedComponents()) {
            // Check whether radcomp is a member of some ButtonGroup
            Object buttonGroup = buttonGroupOfComponent(radComp);
            // Check whether radcomp is some ButtonGroup
            if ((buttonGroup == null) && (radComp.getBeanInstance() instanceof ButtonGroup)) {
                buttonGroup = radComp.getBeanInstance();
            }
            if (buttonGroup != null) {
                if (buttonGroups == null) {
                    buttonGroups = new HashMap<>();
                }
                java.util.List<AbstractButton> members = buttonGroups.get(buttonGroup);
                if (members == null) {
                    members = new ArrayList<>();
                    buttonGroups.put(buttonGroup, members);
                }
            }
        }

        if (buttonGroups != null) {
            // Find all components that use the same button groups as the selected components
            for (RADComponent<?> radComp : getFormModel().getComponentList()) {
                Object buttonGroup = buttonGroupOfComponent(radComp);
                if (buttonGroup != null) {
                    java.util.List<AbstractButton> members = buttonGroups.get(buttonGroup);
                    if (members != null) { // Can be null if no button from this group is selected
                        members.add((AbstractButton) formDesigner.getComponent(radComp));
                    }
                }
            }

            // Visualize individual button groups
            for (java.util.List<AbstractButton> buttons : buttonGroups.values()) {
                if (buttons.size() > 1) {
                    Map<AbstractButton, Rectangle> bounds = new IdentityHashMap<>();
                    for (AbstractButton button : buttons) {
                        Point shift = formDesigner.pointFromComponentToHandleLayer(new Point(0, 0), button);
                        Rectangle bound = new Rectangle(shift.x, shift.y, button.getWidth(), button.getHeight());
                        bounds.put(button, bound);
                    }
                    paintButtonGroup(g, buttons, BUTTON_GROUP_COLUMNS_FIRST, true, bounds, true);
                }
            }
        }
    }

    /**
     * Returns button group assigned to the given component.
     *
     * @param radComp component whose button group should be returned.
     * @return button group assigned to the given component or <code>null</code>
     * if no button group is assigned or if the component is not a subclass * *
     * of <code>AbstractButton</code>.
     */
    private ButtonGroup buttonGroupOfComponent(RADComponent<?> radComp) {
        if (radComp instanceof RADVisualComponent<?>
                && formDesigner.isInDesigner((RADVisualComponent<?>) radComp)) {
            if (AbstractButton.class.isAssignableFrom(radComp.getBeanClass())) {
                FormProperty<ComponentReference<ButtonGroup>> prop = radComp.<FormProperty<ComponentReference<ButtonGroup>>>getProperty("buttonGroup"); // NOI18N
                if (prop != null) {
                    try {
                        ComponentReference<ButtonGroup> value = prop.getValue();
                        if (value != null && value.getComponent() != null) {
                            return value.getComponent().getBeanInstance();
                        } else {
                            return null;
                        }
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Visualizes a button group (or its part).
     *
     * @param g graphics object.
     * @param buttons buttons in the group.
     * @param columns determines whether the buttons should be divided into
     * columns (or rows) primarily.
     * @param root determines whether the whole button group is visualized or
     * just a part.
     * @param compBounds bounds of buttons in the group.
     * @param lastSuccessful determines whether the last division into
     * columns/rows was successful, see issue 136370
     * @return the lowest x-coordinate (resp. y-coordinate) of the group if
     * column is set to <code>true</code> (resp. <code>false</code>).
     */
    private int paintButtonGroup(Graphics g, java.util.List<AbstractButton> buttons,
            boolean columns, boolean root, Map<AbstractButton, Rectangle> compBounds, boolean lastSuccessful) {
        // Preprocessing of information about starts/ends of individual buttons.
        // maps coordinates to the number of buttons starting/ending at this coordinate
        SortedMap<Integer, int[]> bounds = new TreeMap<>();
        for (AbstractButton button : buttons) {
            Rectangle bound = compBounds.get(button);
            int start, end;
            if (columns) {
                start = bound.x;
                end = start + bound.width;
            } else {
                start = bound.y;
                end = start + bound.height;
            }
            paintButtonGroupInsertCount(bounds, start, true);
            paintButtonGroupInsertCount(bounds, end, false);
        }

        // Find cuts
        java.util.List<Integer> cuts = new ArrayList<>();
        int between = 0;
        for (Map.Entry<Integer, int[]> entry : bounds.entrySet()) {
            int[] counts = entry.getValue();
            between -= counts[1];
            if ((between <= 0) && (counts[0] > 0)) {
                cuts.add(entry.getKey());
            }
            between += counts[0];
        }

        // Cut into sub-groups
        java.util.List<AbstractButton>[] groups = (java.util.List<AbstractButton>[]) new java.util.List<?>[cuts.size()];
        for (int i = 0; i < cuts.size(); i++) {
            groups[i] = new ArrayList<>();
        }
        for (AbstractButton button : buttons) {
            Rectangle bound = compBounds.get(button);
            int start = columns ? bound.x : bound.y;
            int index = 0;
            for (Integer cut : cuts) {
                if (cut > start) {
                    break;
                } else {
                    index++;
                }
            }
            groups[--index].add(button);
        }

        // Issue 136370, this set of components cannot be separated neither
        // into columns not into rows. We split them manually into singletons.
        if (!lastSuccessful && (cuts.size() == 1)) {
            return -1;
        }

        // Visualize sub-groups
        int[] starts;
        int minStart;
        boolean ok = false;
        out:
        do {
            starts = new int[cuts.size()];
            minStart = Integer.MAX_VALUE;
            boolean succesful = (cuts.size() > 1);
            for (int i = 0; i < cuts.size(); i++) {
                if (groups[i].size() > 1) {
                    starts[i] = paintButtonGroup(g, groups[i], !columns, root && !succesful, compBounds, succesful);
                    if (starts[i] == -1) { // Issue 136370
                        assert (cuts.size() == 1);
                        // Cuts must be sorted
                        java.util.List<int[]> cutOrder = new ArrayList<>(buttons.size());
                        for (int j = 0; j < buttons.size(); j++) {
                            AbstractButton button = buttons.get(j);
                            Rectangle bound = compBounds.get(button);
                            cutOrder.add(new int[]{columns ? bound.x : bound.y, j});
                        }
                        Collections.sort(cutOrder, (int[] i1, int[] i2) -> (i1[0] == i2[0]) ? (i1[1] - i2[1]) : (i1[0] - i2[0]));
                        cuts = new ArrayList<>(buttons.size());
                        groups = (java.util.List<AbstractButton>[]) new java.util.List<?>[buttons.size()];
                        for (int[] ii : cutOrder) {
                            AbstractButton button = buttons.get(ii[1]);
                            groups[cuts.size()] = Collections.<AbstractButton>singletonList(button);
                            cuts.add(ii[0]);
                        }
                        continue out;
                    }
                } else {
                    Rectangle bound = compBounds.get(groups[i].get(0));
                    starts[i] = columns ? bound.y : bound.x;
                }
                if (minStart > starts[i]) {
                    minStart = starts[i];
                }
            }
            minStart -= BUTTON_GROUP_OFFSET;
            ok = true;
        } while (!ok);

        // Visualize connection of sub-groups
        int count = 0;
        int min = 0;
        int max = 0;
        for (Integer cut : cuts) {
            int off = 0;
            if (groups[count].size() == 1) {
                AbstractButton button = groups[count].get(0);
                Rectangle bound = compBounds.get(button);
                if ((button instanceof RadioButton) || (button instanceof CheckBox)) {
                    Dimension dim = button.getPreferredSize();
                    Insets insets = button.getInsets();
                    int textPos = columns ? button.getHorizontalTextPosition() : button.getVerticalTextPosition();
                    Icon icon = button.getIcon();
                    if (icon == null) {
                        icon = UIManager.getIcon((button instanceof RadioButton) ? "RadioButton.icon" : "CheckBox.icon"); // NOI18N
                    }
                    if (icon != null) {
                        off = columns ? icon.getIconWidth() : icon.getIconHeight();
                        off /= 2;
                        if ((textPos == SwingConstants.LEADING) || (textPos == SwingConstants.TOP) || (textPos == SwingConstants.LEFT)) {
                            off = (columns ? dim.width : dim.height) - off;
                            off -= columns ? insets.right : insets.bottom;
                        } else if (textPos == SwingConstants.CENTER) {
                            off = columns ? (dim.width + insets.left - insets.right) / 2
                                    : (dim.height + insets.top - insets.bottom) / 2;
                        } else {
                            off += columns ? insets.left : insets.top;
                        }
                    }
                    starts[count] += columns ? insets.top : insets.left;
                    int diff = columns ? (bound.width - dim.width) : (bound.height - dim.height);
                    int alignment = columns ? button.getHorizontalAlignment() : button.getVerticalAlignment();
                    if ((alignment == SwingConstants.TRAILING) || (alignment == SwingConstants.BOTTOM) || (alignment == SwingConstants.RIGHT)) {
                        off += diff;
                    } else if (alignment == SwingConstants.CENTER) {
                        off += diff / 2;
                    }
                    int oppDiff = columns ? (bound.height - dim.height) : (bound.width - dim.width);
                    int oppAlignment = columns ? button.getVerticalAlignment() : button.getHorizontalAlignment();
                    if ((oppAlignment == SwingConstants.TRAILING) || (oppAlignment == SwingConstants.BOTTOM) || (oppAlignment == SwingConstants.RIGHT)) {
                        starts[count] += oppDiff;
                    } else if (oppAlignment == SwingConstants.CENTER) {
                        starts[count] += oppDiff / 2;
                    }
                } else {
                    off = (columns ? bound.width : bound.height) / 2;
                }
            } else {
                off = -BUTTON_GROUP_OFFSET;
            }
            if (count == 0) {
                min = cut + off;
            }
            if (count == cuts.size() - 1) {
                max = cut + off;
            }
            if (cuts.size() > 1) {
                if (columns) {
                    g.drawLine(cut + off, starts[count], cut + off, minStart);
                } else {
                    g.drawLine(starts[count], cut + off, minStart, cut + off);
                }
            }
            count++;
        }
        if (cuts.size() > 1) {
            if (!root) {
                min = bounds.firstKey();
            }
            if (columns) {
                g.drawLine(min, minStart, max, minStart);
            } else {
                g.drawLine(minStart, min, minStart, max);
            }
        }

        return bounds.firstKey();
    }

    // Helper method of paintButtonGroup()
    private void paintButtonGroupInsertCount(SortedMap<Integer, int[]> bounds, int value, boolean start) {
        int[] counts = bounds.get(value);
        if (counts == null) {
            counts = new int[2];
            bounds.put(value, counts);
        }
        counts[start ? 0 : 1]++;
    }

    private Image resizeHandle() {
        if (resizeHandle == null) {
            resizeHandle = ImageUtilities.loadImageIcon("com/bearsoft/org/netbeans/modules/form/resources/resize_handle.png", false).getImage(); // NOI18N
        }
        return resizeHandle;
    }
    // paint stroke cached
    private static int lastPaintWidth = -1;
    private Stroke paintStroke;

    private Stroke getPaintStroke() {
        int width = formSettings.getSelectionBorderSize();
        if (lastPaintWidth != width) {
            paintStroke = null;
        }
        if (paintStroke == null) {
            paintStroke = new BasicStroke(width);
            lastPaintWidth = width;
        }
        return paintStroke;
    }

    void maskDraggingComponents() {
        if (componentDrag != null) {
            componentDrag.maskDraggingComponents();
        }
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_TAB || e.getKeyChar() == '\t') {
            if (!e.isControlDown()) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    RADVisualComponent<?> nextComp = formDesigner.getNextVisualComponent(!e.isShiftDown());
                    if (nextComp != null) {
                        formDesigner.setSelectedComponent(nextComp);
                    }
                }
                e.consume();
            }
        } else if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_F2) {
            if (!viewOnly && e.getID() == KeyEvent.KEY_RELEASED) {
                java.util.List<RADComponent<?>> selected = formDesigner.getSelectedComponents();
                if (selected.size() == 1) { // just one component is selected
                    RADComponent<?> comp = selected.get(0);
                    if (formDesigner.getDesignerMode() == PlatypusFormLayoutView.MODE_SELECT) {
                        // in selection mode SPACE starts in-place editing
                        formDesigner.startInPlaceEditing(comp);
                    }
                }
            }
            e.consume();
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            if (formDesigner.getDesignerMode() != PlatypusFormLayoutView.MODE_SELECT) {
                formDesigner.toggleSelectionMode(); // also calls endDragging(null)
                repaint();
                e.consume();
            }
            if (endDragging(null)) {
                repaint();
                e.consume();
            }
        } else if ((keyCode == KeyEvent.VK_CONTEXT_MENU)
                || ((keyCode == KeyEvent.VK_F10) && e.isShiftDown())) { // Shift F10 invokes context menu
            Point p = null;
            java.util.List<RADComponent<?>> selected = formDesigner.getSelectedComponents();
            if (!selected.isEmpty()) {
                RADComponent<?> radComp = selected.get(0);
                Component comp = formDesigner.getComponent(radComp);
                p = convertPointFromComponent(comp.getLocation(), comp.getParent());
                showContextMenu(p);
                e.consume();
            }
        } else if (e.getID() == KeyEvent.KEY_PRESSED
                && (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP
                || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT)) {
            java.util.List<RADVisualComponent<?>> selected = formDesigner.getSelectedLayoutComponents();
            for (int i = selected.size() - 1; i >= 0; i--) {
                RADVisualComponent<?> comp = selected.get(i);
                if (comp == formDesigner.getTopDesignComponent()) {
                    selected.remove(i);
                }
            }
            RADVisualContainer<?> sameParent = FormUtils.getSameParent(selected);
            if (sameParent != null && sameParent.getLayoutSupport() != null) {
                FormLoaderSettings settings = FormLoaderSettings.getInstance();
                boolean oldGridOnPos = settings.getApplyGridToPosition();
                boolean oldGridOnSize = settings.getApplyGridToSize();
                settings.setApplyGridToPosition(false);
                settings.setApplyGridToSize(false);
                try {
                    Rectangle[] compsBounds = new Rectangle[selected.size()];
                    RADVisualComponent<?>[] comps = new RADVisualComponent<?>[selected.size()];
                    for (int i = 0; i < selected.size(); i++) {
                        comps[i] = selected.get(i);
                        JComponent replicant = formDesigner.getComponent(comps[i]);
                        compsBounds[i] = SwingUtilities.convertRectangle(replicant.getParent(), replicant.getBounds(), this);
                    }
                    int keyboardResizeType = 0;
                    int dX = e.isControlDown() ? 1 : settings.getGridX();
                    int dY = e.isControlDown() ? 1 : settings.getGridY();
                    int x = 0;
                    int y = 0;
                    if (keyCode == KeyEvent.VK_DOWN) {
                        y = dY;
                        keyboardResizeType |= LayoutSupportManager.RESIZE_DOWN;
                    } else if (keyCode == KeyEvent.VK_UP) {
                        y = -dY;
                        keyboardResizeType |= LayoutSupportManager.RESIZE_DOWN;
                    } else if (keyCode == KeyEvent.VK_RIGHT) {
                        x = dX;
                        keyboardResizeType |= LayoutSupportManager.RESIZE_RIGHT;
                    } else if (keyCode == KeyEvent.VK_LEFT) {
                        x = -dX;
                        keyboardResizeType |= LayoutSupportManager.RESIZE_RIGHT;
                    }
                    if (e.isShiftDown()) {
                        ComponentDragger dragger = new ComponentDragger(
                                formDesigner,
                                HandleLayer.this,
                                comps,
                                compsBounds,
                                new Point(0, 0),
                                keyboardResizeType);
                        dragger.dropComponents(new Point(x, y), sameParent);
                    } else {
                        ComponentDragger dragger = new ComponentDragger(
                                formDesigner,
                                HandleLayer.this,
                                comps,
                                compsBounds,
                                new Point(0, 0),
                                sameParent);
                        dragger.dropComponents(new Point(x, y), sameParent);
                    }
                } finally {
                    settings.setApplyGridToPosition(oldGridOnPos);
                    settings.setApplyGridToSize(oldGridOnSize);
                }
            }
            e.consume();
        } else if (((keyCode == KeyEvent.VK_D) || (keyCode == KeyEvent.VK_E)) && e.isAltDown() && e.isControlDown() && (e.getID() == KeyEvent.KEY_PRESSED)) {
            //
        } else if (((keyCode == KeyEvent.VK_W)) && e.isAltDown() && e.isControlDown() && (e.getID() == KeyEvent.KEY_PRESSED)) {
            //
        } else if (((keyCode == KeyEvent.VK_S)) && e.isAltDown() && e.isControlDown() && (e.getID() == KeyEvent.KEY_PRESSED)) {
            // start layout test recording
        }
        super.processKeyEvent(e);
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    // -------
    /**
     * Returns radcomponent for visual component at given location.
     *
     * @param point - location in component layer's coordinates
     * @param mode - defines what level in the hierarchy to prefer (in order to
     * distinguish between the leaf components and their parents): COMP_DEEPEST
     * - get the component which is the deepest in the hierarchy (leaf
     * component) COMP_SELECTED - get the deepest selected component
     * COMP_ABOVE_SELECTED - get the component above the deepest selected
     * component COMP_UNDER_SELECTED - get the component under the deepest
     * selected component
     * @returns the radcomponent at given point If no component is currently
     * selected then: for COMP_SELECTED the deepest component is returned for
     * COMP_ABOVE_SELECTED the deepest component is returned for
     * COMP_UNDER_SELECTED the top component is returned
     */
    public RADVisualComponent<?> getRadComponentAt(Point point, int mode) {
        Component[] deepComps = getDeepestComponentsAt(formDesigner.getComponentLayer(), point);
        if (deepComps != null) {
            int dIndex = 0;
            Component comp = deepComps[dIndex];

            // find the component satisfying point and mode
            RADVisualComponent<?> topRadComp = formDesigner.getTopDesignComponent();
            RADVisualComponent<?> firstRadComp = null;
            RADVisualComponent<?> currRadComp;
            RADVisualComponent<?> prevRadComp = null;

            do {
                // Component layer may contain only visual components
                currRadComp = (RADVisualComponent<?>) formDesigner.getRadComponent(comp);
                if (currRadComp != null && !isDraggedComponent(currRadComp)) {
                    if (firstRadComp == null) {
                        firstRadComp = currRadComp;
                    }
                    switch (mode) {
                        case COMP_DEEPEST:
                            return currRadComp;

                        case COMP_SELECTED:
                            if (formDesigner.isComponentSelected(currRadComp)) {
                                return currRadComp;
                            }
                            if (currRadComp == topRadComp) {
                                return firstRadComp; // nothing selected - return the deepest
                            }
                            break;

                        case COMP_ABOVE_SELECTED:
                            if (prevRadComp != null
                                    && formDesigner.isComponentSelected(prevRadComp)) {
                                return currRadComp;
                            }
                            if (currRadComp == topRadComp) {
                                return firstRadComp; // nothing selected - return the deepest
                            }
                            break;

                        case COMP_UNDER_SELECTED:
                            if (formDesigner.isComponentSelected(currRadComp)) {
                                return prevRadComp != null
                                        ? prevRadComp : topRadComp;
                            }
                            if (currRadComp == topRadComp) {
                                return topRadComp; // nothing selected - return the top
                            }
                            break;
                    }
                    prevRadComp = currRadComp;
                }
                comp = dIndex + 1 < deepComps.length
                        ? deepComps[++dIndex] : comp.getParent();
            } while (comp != null);

            return firstRadComp;
        } else {
            return null;
        }
    }

    private static Component[] getDeepestComponentsAt(Container parent,
            Point point) {
        Component deepestComp = SwingUtilities.getDeepestComponentAt(parent, point.x, point.y);
        if (deepestComp == null) {
            return null;
        }

        Container deepestParent = deepestComp.getParent();
        Point deepestPosition = SwingUtilities.convertPoint(parent, point, deepestParent);
        java.util.List<Component> compList = null; // in most cases there will be just one component
        for (int i = 0, n = deepestParent.getComponentCount(); i < n; i++) {
            Component comp = deepestParent.getComponent(i);
            Point p = comp.getLocation();
            if (comp != deepestComp && comp.isVisible()
                    && comp.contains(deepestPosition.x - p.x, deepestPosition.y - p.y)) {
                if (compList == null) {
                    compList = new ArrayList<>(n - i + 1);
                    compList.add(deepestComp);
                }
                compList.add(comp);
            }
        }

        if (compList == null) { // just one component
            return new Component[]{deepestComp};
        } else {
            return compList.toArray(new Component[compList.size()]);
        }
    }

    private RADVisualContainer<?> getRadContainerAt(Point point, int mode) {
        RADComponent<?> radComp = getRadComponentAt(point, mode);
        if (radComp != null) {
            if (radComp instanceof RADVisualContainer<?>) {
                return (RADVisualContainer<?>) radComp;
            }
            if (radComp instanceof RADVisualComponent<?>) {
                return radComp.getParentComponent();
            }
        }
        return null;
    }

    /**
     * Selects component at the position e.getPoint() on component layer. What
     * component is selected further depends on whether CTRL or ALT keys are
     * hold.
     */
    private RADComponent<?> selectComponent(MouseEvent e, boolean mousePressed) {
        RADVisualComponent<?> hitRadComp;
        if (formDesigner.getSelectedComponents().size() > 1
                && mousePressed
                && !e.isShiftDown() && !e.isControlDown() && !e.isAltDown()) {
            // If multiple components already selected and some of them is on
            // current mouse position, keep this component selected on mouse
            // pressed (i.e. don't try to selected a possible subcomponent).
            // This is to ease dragging of multiple scrollpanes or containers
            // covered entirely by subcomponents.
            // OTOH mouse release should cancel the multiselection - if no
            // dragging happened.
            hitRadComp = selectedComponentAt(e.getPoint(), 0);
            if (hitRadComp != null) {
                return hitRadComp;
            }
        }

        int selMode = !e.isAltDown() ? COMP_DEEPEST
                : (!e.isShiftDown() ? COMP_ABOVE_SELECTED : COMP_UNDER_SELECTED);
        hitRadComp = getRadComponentAt(e.getPoint(), selMode);

        // Help with selecting a component in scroll pane (e.g. JTable of zero size).
        // Prefer selcting the component rather than the scrollpane if the view port
        // or header is clicked.
        if (hitRadComp != null && !e.isAltDown()
                && hitRadComp instanceof RADVisualContainer<?>) {
            RADVisualComponent<?>[] sub = ((RADVisualContainer<?>) hitRadComp).getSubComponents();
            Component scroll = formDesigner.getComponent(hitRadComp);
            if (sub.length > 0 && scroll instanceof ScrollPane) {
                Point p = e.getPoint();
                convertPointToComponent(p, scroll);
                Component clicked = SwingUtilities.getDeepestComponentAt(scroll, p.x, p.y);
                while (clicked != null && clicked != scroll) {
                    if (clicked instanceof JViewport) {
                        hitRadComp = sub[0];
                        break;
                    }
                    clicked = clicked.getParent();
                }
            }
        }

        if ((e.isControlDown() || e.isShiftDown()) && !e.isAltDown()) {
            if (hitRadComp != null) {
                // Shift adds to selection, Ctrl toggles selection,
                // other components selection is not affected
                if (!formDesigner.isComponentSelected(hitRadComp)) {
                    formDesigner.addComponentToSelection(hitRadComp);
                } else if (!e.isShiftDown()) {
                    formDesigner.removeComponentFromSelection(hitRadComp);
                }
            }
        } else if (hitRadComp != null) {
            formDesigner.setSelectedComponent(hitRadComp);
        } else {
            formDesigner.clearSelection();
        }

        return hitRadComp;
    }

    private void selectOtherComponentsNode() {
        FormEditor formEditor = formDesigner.getFormEditor();
        FormInspector ci = FormInspector.getInstance();
        Node[] selectedNode = new Node[]{formEditor.getOthersContainerNode()};

        try {
            ci.setSelectedNodes(selectedNode, formDesigner);
            formDesigner.clearSelectionImpl();
            formDesigner.repaintSelection();
        } catch (java.beans.PropertyVetoException ex) {
            org.openide.ErrorManager.getDefault().notify(
                    org.openide.ErrorManager.INFORMATIONAL, ex);
        }

        formDesigner.setActivatedNodes(selectedNode);
    }

    private boolean processDoubleClick(MouseEvent e) {
        if (e.isShiftDown() || e.isControlDown()) {
            return false;
        }

        RADComponent<?> radComp = getRadComponentAt(e.getPoint(), COMP_SELECTED);
        if (radComp == null) {
            return true;
        }

        if (e.isAltDown()) {
            if (radComp == formDesigner.getTopDesignComponent()) {
                radComp = radComp.getParentComponent();
                if (radComp == null) {
                    return true;
                }
            } else {
                return false;
            }
        }

        Node node = radComp.getNodeReference();
        if (node != null) {
            Action action = node.getPreferredAction();
            if (action != null) {// && action.isEnabled()) {
                action.actionPerformed(new ActionEvent(
                        node, ActionEvent.ACTION_PERFORMED, "")); // NOI18N
                prevLeftMousePoint = null; // to prevent inplace editing on mouse release
                return true;
            }
        }

        return false;
    }

    private void processMouseClickInLayoutSupport(RADComponent<?> radComp,
            MouseEvent e) {
        if (formDesigner.getMenuEditLayer().isVisible()) {
            if (!formDesigner.getMenuEditLayer().isMenuLayerComponent(radComp)) {
                formDesigner.getMenuEditLayer().hideMenuLayer();
            }
        }
        if (radComp != null && radComp.getBeanClass().getName().equals(javax.swing.JMenu.class.getName())) {
            formDesigner.openMenu(radComp);
        }
        if (radComp instanceof RADVisualComponent<?>) {
            RADVisualContainer<?> radCont = radComp instanceof RADVisualContainer
                    ? (RADVisualContainer<?>) radComp
                    : (RADVisualContainer<?>) radComp.getParentComponent();
            LayoutSupportManager laysup = radCont != null
                    ? radCont.getLayoutSupport() : null;
            if (laysup != null) {
                Container cont = formDesigner.getComponent(radCont);
                Container contDelegate = radCont.getContainerDelegate(cont);
                Point p = convertPointToComponent(e.getPoint(), contDelegate);
                laysup.processMouseClick(p, cont, contDelegate);
            }
        }
    }

    private void showContextMenu(Point popupPos) {
        Node[] selectedNodes = formDesigner.getSelectedComponentNodes();
        Action[] selectedNodesActions = NodeOp.findActions(selectedNodes);
        java.util.List<Lookup> selectedNodesLookups = new ArrayList<>();
        for (Node n : selectedNodes) {
            selectedNodesLookups.add(n.getLookup());
        }
            // Some actions need TopComponent's action map to be in context lookup
        // PasteAction for example. MVC pattern is preserved because
        // we construct ProxyLookup here and than in will be lost and collected.
        selectedNodesLookups.add(Lookups.singleton(formDesigner.getActionMap()));
        JPopupMenu popup = Utilities.actionsToPopup(selectedNodesActions, new ProxyLookup(selectedNodesLookups.toArray(new Lookup[]{})));
        if (popup != null) {
            popup.show(HandleLayer.this, popupPos.x, popupPos.y);
        }
    }

    // --------
    private boolean anyDragger() {
        return componentDrag != null || selectionDragger != null;
    }

    private RADVisualComponent<?>[] getComponentsToDrag() {
        // all selected components must be visible in the designer and have the
        // same parent; redundant sub-contained components must be filtered out
        java.util.List<RADComponent<?>> selectedComps = formDesigner.getSelectedComponents();
        java.util.List<RADComponent<?>> workingComps = new ArrayList<>(selectedComps.size());
        java.util.List<String> workingIds = null;
        RADVisualContainer<?> parent = null;

        //outside of a frame, there are no selected components so just return null
        if (selectedComps.isEmpty()) {
            return null;
        }

        for (RADComponent<?> radComp : selectedComps) {
            boolean subcontained = false;
            for (RADComponent<?> radComp2 : selectedComps) {
                if (radComp2 != radComp && radComp2.isParentComponent(radComp)) {
                    subcontained = true;
                    break;
                }
            }
            if (!subcontained) {
                RADVisualContainer<?> radCont = (RADVisualContainer<?>) radComp.getParentComponent();

                if (substituteForContainer(radCont)) {
                    // hack: if trying to drag something in scrollpane,
                    // drag the whole scrollpane instead
                    radComp = radCont;
                    radCont = (RADVisualContainer<?>) radComp.getParentComponent();
                }

                if (parent != null) {
                    if (parent != radCont) {
                        return null; // components in different containers
                    }
                } else {
                    if (radCont == null || !formDesigner.isInDesigner(radCont)) {
                        return null; // out of visible tree
                    }
                    parent = radCont;
                    if (radCont.getLayoutSupport() == null) { // new layout
                        workingIds = new ArrayList<>(selectedComps.size());
                    }
                }
                workingComps.add(radComp);
                if (workingIds != null) {
                    workingIds.add(radComp.getName());
                }
            }
        }
        return workingComps.isEmpty() ? null
                : workingComps.toArray(new RADVisualComponent<?>[]{});
    }

    boolean endDragging(MouseEvent e) {
        if (anyDragger()) {
            if (resizeType != 0) {
                resizeType = 0;
                Cursor cursor = getCursor();
                if (cursor != null && cursor.getType() != Cursor.DEFAULT_CURSOR) {
                    setCursor(Cursor.getDefaultCursor());
                }
                if (getToolTipText() != null) {
                    setToolTipText(null);
                }
            }
            boolean done = true;
            if (componentDrag != null) {
                boolean retVal = true;
                try {
                    retVal = componentDrag.end(e);
                } finally {
                    if (retVal) {
                        componentDrag = null;
                        draggingEnded = true;
                        repaint();
                    } else {
                        done = false;
                    }
                }
            } else if (selectionDragger != null) {
                if (e != null) {
                    selectionDragger.drop(e.getPoint());
                }
                selectionDragger = null;
            }
            if (done) {
                draggingEnded = true;
                StatusDisplayer.getDefault().setStatusText(""); // NOI18N
            }
            getFormModel().getAssistantModel().setContext("select"); // NOI18N
            return done;
        } else {
            return false;
        }
    }

    private boolean isDraggedComponent(RADComponent<?> radComp) {
        if (componentDrag != null && componentDrag.movingComponents != null) {
            for (RADComponent<?> c : componentDrag.movingComponents) {
                if (c == radComp || c.isParentComponent(radComp)) {
                    return true;
                }
            }
        }
        return false;
    }
    // Highlighted panel
    private JPanel darkerPanel = null;

    private static class HighlightBorder extends javax.swing.border.LineBorder {

        HighlightBorder(Color color, int thickness) {
            super(color, thickness);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            // Hack - don't affect component's content
            return new Insets(0, 0, 0, 0);
        }
    }

    // Highlights panel below mouse cursor.
    private void highlightPanel(MouseEvent e, boolean recheck) {
        Component[] comps = getDeepestComponentsAt(formDesigner.getComponentLayer(), e.getPoint());
        if (comps == null) {
            return;
        }
        Component comp = comps[comps.length - 1];
        // Component layer may contain only visual components
        RADVisualComponent<?> radcomp = (RADVisualComponent<?>) formDesigner.getRadComponent(comp);
        if ((radcomp != null) && !(radcomp instanceof RADVisualContainer<?>)) {
            radcomp = radcomp.getParentComponent();
            comp = radcomp != null ? formDesigner.getComponent(radcomp) : null;
        }
        if ((radcomp == null) || (radcomp == formDesigner.getTopDesignComponent())
                || (!(comp instanceof JPanel))) {
            comp = null;
        }
        JPanel panel = (JPanel) comp;
        if ((darkerPanel != panel) || (recheck && !shouldHighlightPanel(panel, radcomp))) {
            if (darkerPanel != null) {
                // Reset only HighlightBorder border
                if (darkerPanel.getBorder() instanceof HighlightBorder) {
                    darkerPanel.setBorder(null);
                }
                darkerPanel = null;
            }
            if (shouldHighlightPanel(panel, radcomp)) {
                panel.setBorder(new HighlightBorder(darkerPanelColor(panel.getBackground()), 1));
                darkerPanel = panel;
            }
        }
    }

    private boolean shouldHighlightPanel(JPanel panel, RADVisualComponent<?> radPanel) {
        if (panel != null) {
            if (panel.getBorder() != null) { // Maybe we should highlight also panels with EmptyBorder
                return false;
            }
            if (!(panel.getBackground() instanceof javax.swing.plaf.UIResource)) {
                return false;
            }
            if (radPanel == formDesigner.getTopDesignComponent()) {
                return false;
            }
            if ((formDesigner.getDesignerMode() == PlatypusFormLayoutView.MODE_SELECT)
                    && formDesigner.getSelectedLayoutComponents().contains(radPanel)) {
                return false;
            }
            if (radPanel instanceof RADVisualContainer<?>) {
                RADVisualContainer<?> radCont = (RADVisualContainer<?>) radPanel;
                RADVisualContainer<?> parent = radCont.getParentComponent();
                if (parent != null) {
                    LayoutSupportManager manager = parent.getLayoutSupport();
                    if ((manager != null) && manager.isDedicated()) {
                        return false;
                    }
                    JPanel realPanel = (JPanel) formDesigner.getComponent(radPanel);
                    Component parentBean = (Component) parent.getBeanInstance();
                    Component realParent = formDesigner.getComponent(parent);
                    if (realParent.getSize().equals(realPanel.getSize()) && realPanel.getLocation().equals(new Point(0, 0))) {
                        if (parentBean instanceof JPanel) {
                            return shouldHighlightPanel((JPanel) parentBean, parent);
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return (panel != null);
    }

    private static Color darkerPanelColor(Color color) {
        double factor = 0.9;
        return new Color((int) (color.getRed() * factor),
                (int) (color.getGreen() * factor),
                (int) (color.getBlue() * factor));
    }

    // Check the mouse cursor if it is at position where a component or the
    // designer can be resized. Change mouse cursor accordingly.
    private void checkResizing(MouseEvent e) {
        /**
         * The folowong condition is related to component dragging, when
         * resizing top design component.
         */
        if (formDesigner.getTopDesignComponent() != null/* && formDesigner.getTopDesignComponent().getParentComponent() == null*/) {
            int resizing = checkComponentsResizing(e);
            if (resizing == 0) {
                resizing = checkDesignerResizing(e);
                if (resizing == 0) {
                    if (getToolTipText() != null) {
                        setToolTipText(null);
                    }
                } else if (getToolTipText() == null) {
                    Dimension size = formDesigner.getTopDesignComponent().getBeanInstance().getSize();

                    MessageFormat mf;
                    if (viewOnly) {
                        if (sizeHintFormat == null) {
                            sizeHintFormat = new MessageFormat(
                                    FormUtils.getBundleString("FMT_HINT_DesignerSize")); // NOI18N                                            
                        }
                        mf = sizeHintFormat;
                    } else {
                        if (resizingHintFormat == null) {
                            resizingHintFormat = new MessageFormat(
                                    FormUtils.getBundleString("FMT_HINT_DesignerResizing")); // NOI18N                                            
                        }
                        mf = resizingHintFormat;
                    }

                    String hint = mf.format(
                            new Object[]{new Integer(size.width),
                                new Integer(size.height)});
                    setToolTipText(hint);
                    ToolTipManager.sharedInstance().mouseEntered(e);
                }
            } else if (getToolTipText() != null) {
                setToolTipText(null);
            }

            if (resizing != 0 && !viewOnly) {
                setResizingCursor(resizing);
            } else {
                Cursor cursor = getCursor();
                if (cursor != null && cursor.getType() != Cursor.DEFAULT_CURSOR) {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }

    // Check the mouse cursor if it is at position where designer can be
    // resized.
    private int checkDesignerResizing(MouseEvent e) {
        if (!e.isAltDown() && !e.isControlDown() && !e.isShiftDown()) {
            ComponentLayer compLayer = formDesigner.getComponentLayer();
            int resizing = getSelectionResizable(
                    e.getPoint(),
                    compLayer.getComponentContainer(),
                    compLayer.getDesignerOutsets().right + 2);

            resizeType = validDesignerResizing(resizing)
                    ? resizing | DESIGNER_RESIZING : 0;
        } else {
            resizeType = 0;
        }

        return resizeType;
    }

    // Check whether given resize type is valid for designer.
    private boolean validDesignerResizing(int resizing) {
        return resizing == (LayoutSupportManager.RESIZE_DOWN
                | LayoutSupportManager.RESIZE_RIGHT)
                || resizing == LayoutSupportManager.RESIZE_DOWN
                || resizing == LayoutSupportManager.RESIZE_RIGHT;
    }

    // Check the mouse cursor if it is at position where a component (or more
    // components) can be resized.
    private int checkComponentsResizing(MouseEvent e) {
        resizeType = 0;
        if (e.isAltDown() || e.isControlDown() || e.isShiftDown()) {
            return 0;
        }

        // check selected components whether they are in the same container
        if (!selectedComponentsInSameVisibleContainer()) {
            return 0;
        }

        Point p = e.getPoint();
        RADComponent<?> compAtPoint = selectedComponentAt(p, 6);

        if (!(compAtPoint instanceof RADVisualComponent<?>)) {
            return 0;
        }
        resizeType = getComponentResizable(p, (RADVisualComponent<?>) compAtPoint);
        return resizeType;
    }

    private boolean selectedComponentsInSameVisibleContainer() {
        RADVisualContainer<?> parent = null;
        for (RADComponent<?> _comp : formDesigner.getSelectedComponents()) {
            if (_comp instanceof RADVisualComponent<?>) {
                RADVisualComponent<?> comp = (RADVisualComponent<?>) _comp;
                if (comp == null) {
                    return false; // not visible in designer
                }
                if (parent == null) {
                    parent = comp.getParentComponent();
                    if (!formDesigner.isInDesigner(parent)) {
                        return false; // not visible in designer
                    }
                } else if (comp.getParentComponent() != parent) {
                    return false; // different parent
                }
            }
        }
        return true;
    }

    // Returns selected component at the given point (even outside the designer area).
    private RADVisualComponent<?> selectedComponentAt(Point p, int borderSize) {
        RADVisualComponent<?> compAtPoint = null;
        java.util.List<RADVisualComponent<?>> selected = formDesigner.getSelectedLayoutComponents();
        for (RADComponent<?> radComp : selected) {
            if (radComp instanceof RADVisualComponent<?> && formDesigner.isInDesigner((RADVisualComponent<?>) radComp)) {
                Component comp = formDesigner.getComponent(radComp);
                Rectangle rect = new Rectangle(-borderSize, -borderSize, comp.getWidth() + 2 * borderSize, comp.getHeight() + 2 * borderSize);
                convertRectangleFromComponent(rect, comp);
                if (rect.contains(p)) {
                    compAtPoint = (RADVisualComponent<?>) radComp;
                }
            }
        }
        return compAtPoint;
    }

    // Check how possible component resizing (obtained from layout support)
    // matches with mouse position on component selection border. 
    private int getComponentResizable(Point p, RADVisualComponent<?> radComp) {
        int resizable = getComponentResizable(radComp);
        if (resizable != 0) {
            Component comp = formDesigner.getComponent(radComp);
            resizable &= getSelectionResizable(p, comp, 6);
        }
        return resizable;
    }

    private int getComponentResizable(RADVisualComponent<?> radComp) {
        RADVisualContainer<?> radCont = radComp.getParentComponent();
        if (radCont == null || radComp == formDesigner.getTopDesignComponent()) {
            return 0;
        }
        Component comp = formDesigner.getComponent(radComp);
        int resizable = 0;
        LayoutSupportManager laySup = radCont.getLayoutSupport();
        Container cont = formDesigner.getComponent(radCont);
        if (cont != null) { // might be null if component just enclosed in container not yet cloned
            Container contDel = radCont.getContainerDelegate(cont);
            resizable = laySup.getResizableDirections(
                    cont, contDel,
                    comp, radCont.getIndexOf(radComp));
        }
        return resizable;
    }

    // Compute possible resizing directions according to mouse position on
    // component selection border.
    private int getSelectionResizable(Point p, Component comp, int borderWidth) {
        if (comp == null) {
            return 0;
        }

        int resizable = 0;

        Rectangle r = new Rectangle(0, 0, comp.getWidth(), comp.getHeight());
        convertRectangleFromComponent(r, comp);
        r.grow(borderWidth, borderWidth);
        if (r.contains(p)) {
            r.grow(-borderWidth, -borderWidth);
            r.grow(-3, -3);
            if (r.width < 0) {
                r.width = 0;
            }
            if (r.height < 0) {
                r.height = 0;
            }

            if (p.y >= r.y + r.height) {
                resizable |= LayoutSupportManager.RESIZE_DOWN;
            } else if (p.y < r.y) {
                resizable |= LayoutSupportManager.RESIZE_UP;
            }
            if (p.x >= r.x + r.width) {
                resizable |= LayoutSupportManager.RESIZE_RIGHT;
            } else if (p.x < r.x) {
                resizable |= LayoutSupportManager.RESIZE_LEFT;
            }
        }

        return resizable;
    }

    private void setResizingCursor(int resizeType) {
        Cursor cursor = null;
        if ((resizeType & LayoutSupportManager.RESIZE_UP) != 0) {
            if ((resizeType & LayoutSupportManager.RESIZE_LEFT) != 0) {
                cursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
            } else if ((resizeType & LayoutSupportManager.RESIZE_RIGHT) != 0) {
                cursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
            } else {
                cursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
            }
        } else if ((resizeType & LayoutSupportManager.RESIZE_DOWN) != 0) {
            if ((resizeType & LayoutSupportManager.RESIZE_LEFT) != 0) {
                cursor = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
            } else if ((resizeType & LayoutSupportManager.RESIZE_RIGHT) != 0) {
                cursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
            } else {
                cursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
            }
        } else if ((resizeType & LayoutSupportManager.RESIZE_LEFT) != 0) {
            cursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
        } else if ((resizeType & LayoutSupportManager.RESIZE_RIGHT) != 0) {
            cursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
        }
        if (cursor == null) {
            cursor = Cursor.getDefaultCursor();
        }
        setCursor(cursor);
    }
    /*
     private void setUserDesignerSize() {
     NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
     FormUtils.getBundleString("CTL_SetDesignerSize_Label"), // NOI18N
     FormUtils.getBundleString("CTL_SetDesignerSize_Title")); // NOI18N
     Dimension size = formDesigner.getComponentLayer().getDesignerSize();
     input.setInputText(Integer.toString(size.width) + ", " // NOI18N
     + Integer.toString(size.height));

     if (DialogDisplayer.getDefault().notify(input) == NotifyDescriptor.OK_OPTION) {
     String txt = input.getInputText();
     int i = txt.indexOf(',');
     if (i > 0) {
     int n = txt.length();
     try {
     int w = Integer.parseInt(txt.substring(0, i));
     i++;
     while (i < n && txt.charAt(i) == ' ') {
     i++;
     }
     int h = Integer.parseInt(txt.substring(i, n));
     if (w >= 0 && h >= 0) {
     size = new Dimension(w, h);
     formDesigner.setDesignerSize(size, null);
     setToolTipText(null);
     setCursor(Cursor.getDefaultCursor());
     }
     } catch (NumberFormatException ex) {
     } // silently ignore, do nothing
     }
     }
     }
     */

    private LayoutConstraints<?> getConstraintsAtPoint(RADVisualComponent<?> radComp, Point point, Point hotSpot) {
        RADVisualContainer<?> radCont = radComp instanceof RADVisualContainer
                ? (RADVisualContainer<?>) radComp
                : (RADVisualContainer<?>) radComp.getParentComponent();
        LayoutSupportManager laysup = radCont != null
                ? radCont.getLayoutSupport() : null;

        Container cont = formDesigner.getComponent(radCont);
        Container contDel = radCont.getContainerDelegate(cont);
        Point p = convertPointToComponent(point.x, point.y, contDel);
        return laysup.getNewConstraints(cont, contDel, null, -1, p, hotSpot);
    }

    private static boolean substituteForContainer(RADVisualContainer<?> radCont) {
        return radCont != null
                && radCont.getBeanClass().isAssignableFrom(ScrollPane.class)
                && radCont.getSubComponents().length > 0;
    }

    // ------
    boolean mouseOnVisual(Point p) {
        Rectangle r = formDesigner.getComponentLayer().getDesignerOuterBounds();
        return r.contains(p);
    }

    // NOTE: does not create a new Point instance
    private Point convertPointFromComponent(Point p, Component sourceComp) {
        return formDesigner.pointFromComponentToHandleLayer(p, sourceComp);
    }

    private Point convertPointFromComponent(int x, int y, Component sourceComp) {
        return formDesigner.pointFromComponentToHandleLayer(new Point(x, y), sourceComp);
    }

    // NOTE: does not create a new Point instance
    private Point convertPointToComponent(Point p, Component targetComp) {
        return formDesigner.pointFromHandleToComponentLayer(p, targetComp);
    }

    private Point convertPointToComponent(int x, int y, Component targetComp) {
        return formDesigner.pointFromHandleToComponentLayer(new Point(x, y), targetComp);
    }

    // NOTE: does not create a new Rectangle instance
    private Rectangle convertRectangleFromComponent(Rectangle rect,
            Component sourceComp) {
        Point p = convertPointFromComponent(rect.x, rect.y, sourceComp);
        rect.x = p.x;
        rect.y = p.y;
        return rect;
    }

    // NOTE: does not create a new Rectangle instance
    Rectangle convertRectangleToComponent(Rectangle rect,
            Component targetComp) {
        Point p = convertPointToComponent(rect.x, rect.y, targetComp);
        rect.x = p.x;
        rect.y = p.y;
        return rect;
    }

    Rectangle convertVisibleRectangleFromComponent(Rectangle rect, Component comp) {
        Component parent;
        while (!formDesigner.isCoordinatesRoot(comp)) {
            parent = comp.getParent();
            Rectangle size = new Rectangle(0, 0, parent.getWidth(), parent.getHeight());
            rect.translate(comp.getX(), comp.getY());
            rect = rect.intersection(size);
            comp = parent;
        }
        comp = this;
        while (!formDesigner.isCoordinatesRoot(comp)) {
            rect.translate(-comp.getX(), -comp.getY());
            comp = comp.getParent();
        }
        return rect;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        return super.getToolTipText(e);
    }

    // ----------
    private class SelectionDragger {

        private Point startPoint;
        private Point lastPoint;

        public SelectionDragger(Point aPoint) {
            startPoint = aPoint;
        }

        public void paintDragFeedback(Graphics g) {
            if (startPoint != null && lastPoint != null) {
                Rectangle r = getRectangle();
                g.drawRect(r.x, r.y, r.width, r.height);
            }
        }

        public void drag(Point p) {
            lastPoint = p;
        }

        public void drop(Point endPoint) {
            if (startPoint != null && endPoint != null) {
                lastPoint = endPoint;
                java.util.List<RADVisualComponent<?>> toSelect = new ArrayList<>();
                collectSelectedComponents(
                        getRectangle(),
                        formDesigner.getComponentLayer().getComponentContainer(),
                        toSelect);

                RADVisualComponent<?>[] selected = new RADVisualComponent<?>[toSelect.size()];
                toSelect.toArray(selected);
                formDesigner.setSelectedComponents(selected);
            }
        }

        private Rectangle getRectangle() {
            int x = startPoint.x <= lastPoint.x ? startPoint.x : lastPoint.x;
            int y = startPoint.y <= lastPoint.y ? startPoint.y : lastPoint.y;
            int w = lastPoint.x - startPoint.x;
            if (w < 0) {
                w = -w;
            }
            int h = lastPoint.y - startPoint.y;
            if (h < 0) {
                h = -h;
            }

            return new Rectangle(x, y, w, h);
        }

        private boolean collectSelectedComponents(Rectangle selRect,
                Container cont,
                java.util.List<RADVisualComponent<?>> toSelect) {
            java.util.List<Component> subContainers = new ArrayList<>();

            Component[] comps;
            if (cont instanceof TabbedPane) {
                Component selectedTab = ((TabbedPane) cont).getSelectedComponent();
                comps = (selectedTab == null) ? new Component[0] : new Component[]{selectedTab};
            } else {
                comps = cont.getComponents();
            }
            for (Component comp : comps) {
                Rectangle bounds = convertRectangleFromComponent(comp.getBounds(), cont);
                boolean intersects = selRect.intersects(bounds);
                RADComponent<?> radComp = formDesigner.getRadComponent(comp);
                if (radComp != null && intersects && radComp instanceof RADVisualComponent<?>) {
                    toSelect.add((RADVisualComponent<?>) radComp);
                }
                if (intersects && comp instanceof Container) {
                    subContainers.add(comp);
                }
            }

            if (toSelect.size() > 1
                    || (toSelect.size() == 1 && subContainers.isEmpty())) {
                return true;
            }

            RADComponent<?> theOnlyOne = toSelect.size() == 1 ? toSelect.get(0) : null;

            for (int i = 0; i < subContainers.size(); i++) {
                toSelect.clear();
                if (collectSelectedComponents(selRect,
                        (Container) subContainers.get(i),
                        toSelect)) {
                    return true;
                }
            }

            if (theOnlyOne != null && theOnlyOne instanceof RADVisualComponent<?>) {
                toSelect.add((RADVisualComponent<?>) theOnlyOne);
                return true;
            }

            return false;
        }
    }

    // -------
    private abstract class ComponentDrag {

        RADVisualComponent<?>[] movingComponents;
        boolean draggableLayoutComponents;
        RADVisualContainer<?> targetContainer;
        RADVisualContainer<?> fixedTarget;
        Component[] showingComponents;
        Rectangle[] originalBounds; // in coordinates of HandleLayer
        Rectangle compoundBounds; // compound from original bounds
        Rectangle[] movingBounds; // in coordinates of ComponentLayer
        Point hotSpot; // in coordinates of ComponentLayer
        Point convertPoint; // from HandleLayer to ComponentLayer (top visual component)
        boolean realDrag;

        // ctor for adding new
        ComponentDrag() {
            if (formDesigner.getTopDesignComponentView() == null) {
                convertPoint = new Point(0, 0);
            } else {
                convertPoint = convertPointFromComponent(0, 0, formDesigner.getTopDesignComponentView());
            }
        }

        // ctor for moving and resizing
        ComponentDrag(RADVisualComponent<?>[] components, Point aHotSpot) {
            this();
            setMovingComponents(components);
            int count = components.length;
            showingComponents = new Component[count]; // [provisional - just one component can be moved]
            originalBounds = new Rectangle[count];
            movingBounds = new Rectangle[count];
            for (int i = 0; i < count; i++) {
                showingComponents[i] = formDesigner.getComponent(movingComponents[i]);
                originalBounds[i] = showingComponents[i].getBounds();
                convertRectangleFromComponent(originalBounds[i], showingComponents[i].getParent());
                compoundBounds = compoundBounds != null
                        ? compoundBounds.union(originalBounds[i]) : originalBounds[i];
                movingBounds[i] = new Rectangle();
                movingBounds[i].width = originalBounds[i].width;
                movingBounds[i].height = originalBounds[i].height;
            }

            hotSpot = aHotSpot == null
                    ? new Point(4, 4)
                    : new Point(aHotSpot.x - convertPoint.x, aHotSpot.y - convertPoint.y);
        }

        final void setMovingComponents(RADVisualComponent<?>[] components) {
            movingComponents = components;
            if (components != null && components.length > 0 && components[0] != null) {
                draggableLayoutComponents = !components[0].isMenuComponent();
            } else {
                draggableLayoutComponents = false;
            }
        }

        final RADVisualContainer<?> getSourceContainer() {
            return movingComponents != null && movingComponents.length > 0
                    && formDesigner.getTopDesignComponent() != movingComponents[0]
                            ? movingComponents[0].getParentComponent() : null;
        }

        final boolean isTopComponent() {
            return movingComponents != null && movingComponents.length > 0
                    && formDesigner.getTopDesignComponent() == movingComponents[0];
        }

        final boolean isDraggableLayoutComponent() {
            return draggableLayoutComponents;
        }

        final RADVisualContainer<?> getTargetContainer(Point p, int modifiers) {
            if (fixedTarget != null) {
                return fixedTarget;
            } else {
                int mode = (modifiers & InputEvent.ALT_MASK) != 0 ? COMP_SELECTED : COMP_DEEPEST;
                RADVisualContainer<?> radCont = HandleLayer.this.getRadContainerAt(p, mode);
                if ((radCont != null) && (radCont.getLayoutSupport() == null)) {
                    RADVisualContainer<?> dirRadCont = HandleLayer.this.getRadContainerAt(
                            getMoveDirectionSensitivePoint(p, modifiers), mode);
                    if ((dirRadCont != null) && (dirRadCont.getLayoutSupport() == null)) {
                        radCont = dirRadCont;
                    }
                }
                if (movingComponents != null) {
                    java.util.List<RADVisualComponent<?>> comps = Arrays.asList(movingComponents);
                    while (comps.contains(radCont)) {
                        radCont = radCont.getParentComponent();
                    }
                }
                if (substituteForContainer(radCont)) {
                    radCont = radCont.getParentComponent();
                }
                return radCont;
            }
        }

        private Point getMoveDirectionSensitivePoint(Point p, int modifiers) {
            if (lastMousePosition != null
                    && compoundBounds != null
                    && (modifiers & (InputEvent.ALT_MASK | InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)) == 0) {
                if (compoundBounds.width <= 0 || compoundBounds.height <= 0) {
                    return p;
                }
                int x;
                int y;
                if (lastXPosDiff != 0 && lastYPosDiff != 0) {
                    double dx = lastXPosDiff;
                    double dy = lastYPosDiff;
                    double d = Math.abs(dy / dx);
                    double r = compoundBounds.getHeight() / compoundBounds.getWidth();
                    if (d > r) {
                        x = p.x + (int) Math.round(compoundBounds.getHeight() / d / 2.0) * (lastXPosDiff > 0 ? 1 : -1);
                        y = p.y - convertPoint.y - hotSpot.y + compoundBounds.y + (lastYPosDiff > 0 ? compoundBounds.height : 0);
                    } else {
                        x = p.x - convertPoint.x - hotSpot.x + compoundBounds.x + (lastXPosDiff > 0 ? compoundBounds.width : 0);
                        y = p.y + (int) Math.round(compoundBounds.getWidth() * d / 2.0) * (lastYPosDiff > 0 ? 1 : -1);
                    }
                } else {
                    x = lastXPosDiff == 0 ? p.x
                            : p.x - convertPoint.x - hotSpot.x + compoundBounds.x + (lastXPosDiff > 0 ? compoundBounds.width : 0);
                    y = lastYPosDiff == 0 ? p.y
                            : p.y - convertPoint.y - hotSpot.y + compoundBounds.y + (lastYPosDiff > 0 ? compoundBounds.height : 0);
                }
                Rectangle boundaries = formDesigner.getComponentLayer().getDesignerInnerBounds();
                // don't let the component component fall into non-visual area easily
                if (x < boundaries.x && x + 8 >= boundaries.x) {
                    x = boundaries.x;
                } else if (x > boundaries.x + boundaries.width && x - 8 < boundaries.x + boundaries.width) {
                    x = boundaries.x + boundaries.width - 1;
                }
                if (y < boundaries.y && y + 8 >= boundaries.y) {
                    y = boundaries.y;
                } else if (y > boundaries.y + boundaries.height && y - 8 < boundaries.y + boundaries.height) {
                    y = boundaries.y + boundaries.height - 1;
                }
                return new Point(x, y);
            } else {
                return p;
            }
        }

        final void move(MouseEvent e) {
            if (e == null) {
                move(null, 0);
            } else {
                move(e.getPoint(), e.getModifiers());
            }
        }

        void move(Point p, int modifiers) {
            if (p != null) {
                targetContainer = getTargetContainer(p, modifiers);
                // support for highlights in menu containers
                // hack: this only checks the first component.
                if (movingComponents != null) {
                    formDesigner.getMenuEditLayer().rolloverContainer(null);
                }
                if (realDrag && isDraggableLayoutComponent()
                        && targetContainer != null
                        && targetContainer.getLayoutSupport() != null) {
                    oldMove(p);
                } else {
                    getFormModel().getAssistantModel().setContext("generalPosition"); // NOI18N
                }
                for (int i = 0; i < movingBounds.length; i++) {
                    movingBounds[i].x = p.x - convertPoint.x - hotSpot.x + originalBounds[i].x - convertPoint.x;
                    movingBounds[i].y = p.y - convertPoint.y - hotSpot.y + originalBounds[i].y - convertPoint.y;
                }
            } else {
                for (int i = 0; i < movingBounds.length; i++) {
                    movingBounds[i].x = Integer.MIN_VALUE;
                }
            }
        }

        final void maskDraggingComponents() {
            if (!isTopComponent() && showingComponents != null) {
                for (int i = 0; i < showingComponents.length; i++) {
                    Rectangle r = movingBounds[i];
                    showingComponents[i].setBounds(r.x + Short.MIN_VALUE, r.y + Short.MIN_VALUE, r.width, r.height);
                }
            }
        }

        final void paintFeedback(Graphics2D g) {
            if (movingBounds.length >= 1 && movingBounds[0].x != Integer.MIN_VALUE) {
                for (int i = 0; i < showingComponents.length; i++) {
                    Graphics gg = g.create(movingBounds[i].x + convertPoint.x,
                            movingBounds[i].y + convertPoint.y,
                            movingBounds[i].width + 1,
                            movingBounds[i].height + 1);
                    if (realDrag && isDraggableLayoutComponent()
                            && ((targetContainer != null && targetContainer.getLayoutSupport() != null)
                            || (targetContainer == null && isTopComponent()))) {
                        if (!isTopComponent()) {
                            doLayout(showingComponents[i]);
                            oldPaintFeedback(g, gg, i);
                        }
                    } else { // non-visual area
                        doLayout(showingComponents[i]);
                        paintDraggedComponent(showingComponents[i], gg);
                    }
                }
            }
        }

        final boolean end(final MouseEvent e) {
            dragPanel.removeAll();
            boolean retVal;
            if (e == null) {
                retVal = end(null, 0);
            } else {
                retVal = end(e.getPoint(), e.getModifiers());
            }
            if (retVal) {
                movingComponents = null;
                targetContainer = null;
                fixedTarget = null;
                showingComponents = null;
            } else {
                // re-init in next AWT round - to have the designer updated
                EventQueue.invokeLater(() -> {
                    init();
                    move(e);
                });
            }
            return retVal;
        }

        // methods to extend/override ---
        void init() {
            if (showingComponents != null) {
                // showing components need to be in a container to paint
                // correctly (relates to newly added components);
                RADVisualContainer<?> sourceCont = getSourceContainer();
                boolean oldSource = sourceCont != null && sourceCont.getLayoutSupport() != null;
                dragPanel.removeAll();
                for (int i = 0; i < showingComponents.length; i++) {
                    Component comp = showingComponents[i];
                    if (comp.getParent() == null) {
                        dragPanel.add(comp);
                    } else if (oldSource) {
                        comp.setVisible(false);
                        // VisualReplicator makes it visible again...
                    }
                    avoidDoubleBuffering(comp);
                }
            }
        }

        private void avoidDoubleBuffering(Component comp) {
            if (comp instanceof JComponent) {
                ((JComponent) comp).setDoubleBuffered(false);
            }
            if (comp instanceof Container) {
                Container cont = (Container) comp;
                for (int i = 0; i < cont.getComponentCount(); i++) {
                    avoidDoubleBuffering(cont.getComponent(i));
                }
            }
        }

        boolean end(Point p, int modifiers) {
            // clear the rollover just in case it was set
            formDesigner.getMenuEditLayer().clearRollover();
            return true;
        }

        public void oldMove(Point p) {
        }

        void oldPaintFeedback(Graphics2D g, Graphics gg, int index) {
        }
    }

    private void doLayout(Component component) {
        if (component instanceof Container) {
            Container cont = (Container) component;
            cont.doLayout();
            for (int i = 0, n = cont.getComponentCount(); i < n; i++) {
                Component comp = cont.getComponent(i);
                doLayout(comp);
            }
        }
    }

    private static void paintDraggedComponent(Component comp, Graphics g) {
        try {
            if (comp instanceof JComponent && g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                Composite oldComposite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                try {
                    comp.paint(g);
                } finally {
                    g2d.setComposite(oldComposite);
                }
            }
        } catch (RuntimeException ex) { // inspired by bug #62041 (JProgressBar bug #5035852)
            org.openide.ErrorManager.getDefault().notify(
                    org.openide.ErrorManager.INFORMATIONAL, ex);
        }
    }

    // for moving existing components
    private class ExistingComponentDrag extends ComponentDrag {

        private int modifiers; // for the layout support
        private ComponentDragger dragger; // drags components in the layout support

        ExistingComponentDrag(RADVisualComponent<?>[] comps,
                Point hotspot, // in HandleLayer coordinates
                int aModifiers) {
            super(comps, hotspot);
            modifiers = aModifiers;
            init();
        }

        @Override
        final void init() {
            RADVisualContainer<?> radCont = getSourceContainer();
            for (int i = 0; i < showingComponents.length; i++) {
                //compIds[i] = movingComponents[i].getName();
                originalBounds[i].x -= convertPoint.x;
                originalBounds[i].y -= convertPoint.y;
            }
            if ((modifiers & InputEvent.ALT_MASK) != 0) {
                // restricted dragging - within the same container, or one level up
                fixedTarget = (modifiers & InputEvent.SHIFT_MASK) != 0
                        || formDesigner.getTopDesignComponent() == radCont
                                ? radCont : radCont.getParentComponent();
            }

            // layout component dragger requires coordinates related to HandleLayer
            for (int i = 0; i < originalBounds.length; i++) {
                originalBounds[i].x += convertPoint.x;
                originalBounds[i].y += convertPoint.y;
            }
            dragger = new ComponentDragger(
                    formDesigner,
                    HandleLayer.this,
                    movingComponents,
                    originalBounds,
                    new Point(hotSpot.x + convertPoint.x, hotSpot.y + convertPoint.y),
                    fixedTarget);
            realDrag = true;
            super.init();
        }

        @Override
        boolean end(Point p, int modifiers) {
            // clear the rollover just in case it was set
            formDesigner.getMenuEditLayer().clearRollover();
            RADVisualContainer<?> originalCont = getSourceContainer();
            // fail if trying to move a menu component to a non-menu container
            if (MenuEditLayer.containsMenuTypeComponent(movingComponents)) {
                if (!MenuEditLayer.isValidMenuContainer(targetContainer)) {
                    formDesigner.updateContainerLayout(originalCont);
                    return true;
                }
            }
            // fail if trying to move a non-menu component into a menu container
            if (!MenuEditLayer.containsMenuTypeComponent(movingComponents)) {
                if (MenuEditLayer.isValidMenuContainer(targetContainer)) {
                    formDesigner.updateContainerLayout(originalCont);
                    return true;
                }
            }
            if (p != null) {
                if (targetContainer == null || targetContainer.getLayoutSupport() != null) {
                    // dropped in layout support, or on non-visual area
                    dragger.dropComponents(p, targetContainer);
                }
            } else { // canceled
                formDesigner.updateContainerLayout(originalCont);
            }
            return true;
        }

        @Override
        public void oldMove(Point p) {
            dragger.drag(p, targetContainer);
        }

        @Override
        void oldPaintFeedback(Graphics2D g, Graphics gg, int index) {
            // don't paint if component dragged from layout (may have strange size)
            Component comp = showingComponents[index];
            paintDraggedComponent(comp, gg);
            dragger.paintDragFeedback(g);
        }
    }

    // for resizing existing components
    private class ResizeComponentDrag extends ComponentDrag {

        private int resizeType;
        private Dimension originalSize;
        private ComponentDragger dragger; // drags components in the layout support

        ResizeComponentDrag(RADVisualComponent<?>[] comps,
                Point hotspot, // in HandleLayer coordinates
                int aResizeType) {
            super(comps, hotspot);
            resizeType = aResizeType;
            init();
        }

        @Override
        final void init() {
            RADVisualContainer<?> sourceCont = getSourceContainer();
            if (isTopComponent()) {
                RADVisualContainer<?> formRootContainer = ((RADVisualContainer<?>) movingComponents[0]);
                realDrag = formRootContainer.getLayoutSupport() != null && formRootContainer.getLayoutSupport().getLayoutDelegate() != null;
                fixedTarget = null;
                originalSize = formDesigner.getTopDesignComponent().getBeanInstance().getSize();
            } else if (sourceCont != null) {
                realDrag = sourceCont.getLayoutSupport() != null;
                fixedTarget = sourceCont;
            }
            if (realDrag) { // layout support
                dragger = new ComponentDragger(
                        formDesigner,
                        HandleLayer.this,
                        movingComponents,
                        originalBounds,
                        new Point(hotSpot.x + convertPoint.x, hotSpot.y + convertPoint.y),
                        resizeType);
            }
            super.init();
        }

        @Override
        boolean end(Point p, int modifiers) {
            if (p != null) {
                if (targetContainer != null) {
                    dragger.dropComponents(p, targetContainer);
                }
                if (isTopComponent()) {
                    Dimension newSize = new Dimension(movingBounds[0].width, movingBounds[0].height);
                    formDesigner.getFormModel().fireComponentPropertyChanged(
                            formDesigner.getTopDesignComponent(), "width", originalSize.width, newSize.width);
                    formDesigner.getFormModel().fireComponentPropertyChanged(
                            formDesigner.getTopDesignComponent(), "height", originalSize.height, newSize.height);
                }
            } else { // resizing canceled
                if (isTopComponent()) {
                    changeTopCompSize(originalSize);
                } else { // add resized component back
                    formDesigner.updateContainerLayout(getSourceContainer()); //, false);
                }
            }
            return true;
        }

        protected void changeTopCompSize(Dimension newSize) {
            try {
                formDesigner.getFormModel().setUndoRedoRecording(false);
                try {
                    RADProperty<Integer> widthProp = formDesigner.getTopDesignComponent().<RADProperty<Integer>>getProperty("width");
                    if (widthProp != null) {
                        widthProp.setValue(newSize.width);
                    }
                    RADProperty<Integer> heightProp = formDesigner.getTopDesignComponent().<RADProperty<Integer>>getProperty("height");
                    if (heightProp != null) {
                        heightProp.setValue(newSize.height);
                    }
                } finally {
                    formDesigner.getFormModel().setUndoRedoRecording(true);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        void move(Point p, int modifiers) {
            targetContainer = getTargetContainer(p, modifiers);

            if (isTopComponent()) {
                Rectangle r = formDesigner.getTopDesignComponent().getBeanInstance().getBounds();//formDesigner.getComponentLayer().getDesignerInnerBounds();
                int w = r.width;
                int h = r.height;
                if ((resizeType & LayoutSupportManager.RESIZE_DOWN) != 0) {
                    h = p.y - r.y;
                    if (h < 0) {
                        h = 0;
                    }
                }
                if ((resizeType & LayoutSupportManager.RESIZE_RIGHT) != 0) {
                    w = p.x - r.x;
                    if (w < 0) {
                        w = 0;
                    }
                }
                movingBounds[0].width = w;
                movingBounds[0].height = h;
                /*
                 * Will be returned to working code without RADVisualFormContainer when structural changes 
                 * of replicants/real components in the form will be clear.
                 * Also, we have to invectigate undo/redo functioning.
                 */
                if (isTopComponent()/* && formDesigner.getTopDesignComponent() instanceof RADVisualFormContainer*/) {
                    Dimension size = new Dimension(movingBounds[0].width, movingBounds[0].height);
                    changeTopCompSize(size);
                    /*
                     formDesigner.getComponentLayer().setDesignerSize(size);
                     formDesigner.getComponentLayer().revalidate();
                     */
                }
            } else {
                if (realDrag && targetContainer != null && targetContainer.getLayoutSupport() != null) {
                    oldMove(p);
                    for (int i = 0; i < movingBounds.length; i++) {
                        int xchange = p.x - convertPoint.x - hotSpot.x;
                        if ((resizeType & LayoutSupportManager.RESIZE_LEFT) != 0) {
                            movingBounds[i].x = originalBounds[i].x - convertPoint.x + xchange;
                            xchange = -xchange;
                        } else {
                            movingBounds[i].x = originalBounds[i].x - convertPoint.x;
                        }
                        if ((resizeType & (LayoutSupportManager.RESIZE_RIGHT | LayoutSupportManager.RESIZE_LEFT)) != 0) {
                            movingBounds[i].width = originalBounds[i].width + xchange;
                        }
                        int ychange = p.y - convertPoint.y - hotSpot.y;
                        if ((resizeType & LayoutSupportManager.RESIZE_UP) != 0) {
                            movingBounds[i].y = originalBounds[i].y - convertPoint.y + ychange;
                            ychange = -ychange;
                        } else {
                            movingBounds[i].y = originalBounds[i].y - convertPoint.y;
                        }
                        if ((resizeType & (LayoutSupportManager.RESIZE_DOWN | LayoutSupportManager.RESIZE_UP)) != 0) {
                            movingBounds[i].height = originalBounds[i].height + ychange;
                        }
                    }
                } else {
                    super.move(p, modifiers);
                }
            }
        }

        @Override
        public void oldMove(Point p) {
            dragger.drag(p, targetContainer);
            getFormModel().getAssistantModel().setContext("generalResizing"); // NOI18N
        }

        @Override
        void oldPaintFeedback(Graphics2D g, Graphics gg, int index) {
            paintDraggedComponent(showingComponents[index], gg);
            dragger.paintDragFeedback(g);
        }
    }

    // for moving a component being newly added
    private class NewComponentDrag extends ComponentDrag {

        private PaletteItem paletteItem;
        RADComponent<?> addedComponent;
        private int index = - 1; // for the layout support
        private LayoutConstraints<?> constraints; // for the layout support

        NewComponentDrag(PaletteItem paletteItem) {
            super();
            this.paletteItem = paletteItem;
            showingComponents = new Component[1];
            init();
        }

        @Override
        final void init() { // can be re-inited
            RADVisualComponent<?> precreated
                    = getComponentCreator().precreateVisualComponent(
                            paletteItem.getComponentClassSource());
            if (precreated != null) {
                if (movingComponents == null) {
                    setMovingComponents(new RADVisualComponent<?>[]{precreated});
                } else { // continuing adding - new instance of the same component
                    movingComponents[0] = precreated;
                }
                showingComponents[0] = (Component) precreated.getBeanInstance();

                Dimension size = showingComponents[0].getPreferredSize();
                if (originalBounds == null) { // new adding
                    hotSpot = new Point();
                    originalBounds = new Rectangle[]{new Rectangle(convertPoint.x, convertPoint.y, size.width, size.height)};
                    movingBounds = new Rectangle[]{new Rectangle(0, 0, size.width, size.height)};
                } else { // repeated adding of the same component type, reuse last bounds
                    movingBounds[0].width = size.width;
                    movingBounds[0].height = size.height;
                    originalBounds[0] = movingBounds[0];
                    movingBounds[0] = new Rectangle(movingBounds[0]);
                    originalBounds[0].x += convertPoint.x;
                    originalBounds[0].y += convertPoint.y;
                }
                compoundBounds = originalBounds[0];
                hotSpot.x = movingBounds[0].x + size.width / 2 - 4;
                hotSpot.y = movingBounds[0].y + size.height / 2;
                if (hotSpot.x < movingBounds[0].x) {
                    hotSpot.x = movingBounds[0].x;
                }
                realDrag = true;
            } else {
                if (paletteItem.getComponentClass() != null) {
                    // non-visual component - present it as icon
                    Node node = paletteItem.getNode();
                    Image icon;
                    if (node == null) {
                        icon = paletteItem.getIcon(java.beans.BeanInfo.ICON_COLOR_16x16);
                        if (icon == null) {
                            icon = ImageUtilities.loadImage("com/bearsoft/org/netbeans/modules/form/resources/form.gif"); // NOI18N
                        }
                    } else {
                        icon = node.getIcon(java.beans.BeanInfo.ICON_COLOR_16x16);
                    }
                    showingComponents[0] = new JLabel(new ImageIcon(icon));
                    Dimension dim = showingComponents[0].getPreferredSize();
                    hotSpot = new Point(dim.width / 2, dim.height / 2);
                    if (hotSpot.x < 0) {
                        hotSpot.x = 0;
                    }
                    originalBounds = new Rectangle[]{new Rectangle(convertPoint.x, convertPoint.y, dim.width, dim.height)};
                    showingComponents[0].setBounds(originalBounds[0]);
                    movingBounds = new Rectangle[]{showingComponents[0].getBounds()};
                    realDrag = false;
                } else {
                    // The corresponding class cannot be loaded - cancel the drag.
                    showingComponents = null;
                    movingBounds = new Rectangle[0];
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            formDesigner.toggleSelectionMode(); // calls endDragging(null)
                        }
                    });
                }
            }
            super.init();
        }

        /**
         * Overrides end(Point,int) in ComponentDrag to support adding new
         * components
         */
        @Override
        boolean end(Point p, int modifiers) {
            // clear the rollover just in case it was set
            formDesigner.getMenuEditLayer().clearRollover();
            if (p != null) {
                targetContainer = getTargetContainer(p, modifiers);
                if (movingComponents != null) {
                    try {
                        // there is a precreated visual component
                        boolean oldLayout;
                        LayoutConstraints<?> lconstraints; // for layout
                        int lindex = -1;
                        if (targetContainer != null) {
                            oldLayout = targetContainer.getLayoutSupport() != null;
                            Point posInComp = new Point(hotSpot.x - originalBounds[0].x + convertPoint.x,
                                    hotSpot.y - originalBounds[0].y + convertPoint.y);
                            lconstraints = oldLayout && isDraggableLayoutComponent()
                                    ? getConstraintsAtPoint(targetContainer, p, posInComp) : null;
                            if (lconstraints == null) {
                                Container cont = formDesigner.getComponent(targetContainer);
                                Container contDel = targetContainer.getContainerDelegate(cont);
                                LayoutSupportManager laysup = targetContainer != null
                                        ? targetContainer.getLayoutSupport() : null;
                                lindex = laysup.getNewIndex(cont, contDel, targetContainer.getBeanInstance(), -1, p, hotSpot);
                            }
                        } else {
                            oldLayout = false;
                            lconstraints = null;
                        }
                        addedComponent = movingComponents[0];
                        // add the component to FormModel
                        boolean added = getComponentCreator().addPrecreatedComponent(targetContainer, lindex, lconstraints);
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                } else { // component not precreated ...
                    RADComponent<?> targetComponent = targetContainer;
                    int mode = ((modifiers & InputEvent.ALT_MASK) != 0) ? COMP_SELECTED : COMP_DEEPEST;
                    RADComponent<?> hittedComponent = HandleLayer.this.getRadComponentAt(p, mode);
                    if ((hittedComponent instanceof RADModelGrid && GridColumnsNode.class.isAssignableFrom(paletteItem.getComponentClass()))) {
                        targetComponent = hittedComponent;
                    }
                    addedComponent = getComponentCreator().createComponent(
                            paletteItem.getComponentClassSource(), targetComponent, null, false);
                    if (addedComponent == null) {
                        repaint();
                    }
                }
                if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
                    return false;
                }
            } else {
                getComponentCreator().releasePrecreatedComponent();
            }
            formDesigner.toggleSelectionMode();
            return true;
        }

        @Override
        public void oldMove(Point p) {
            LayoutSupportManager laysup = targetContainer.getLayoutSupport();
            Container cont = formDesigner.getComponent(targetContainer);
            Container contDel = targetContainer.getContainerDelegate(cont);
            Point posInCont = convertPointToComponent(p.x, p.y, contDel);
            Point posInComp = new Point(hotSpot.x - originalBounds[0].x + convertPoint.x,
                    hotSpot.y - originalBounds[0].y + convertPoint.y);
            index = laysup.getNewIndex(cont, contDel,
                    showingComponents[0], -1,
                    posInCont, posInComp);
            constraints = laysup.getNewConstraints(cont, contDel,
                    showingComponents[0], -1,
                    posInCont, posInComp);
        }

        @Override
        void oldPaintFeedback(Graphics2D g, Graphics gg, int index) {
            paintDraggedComponent(showingComponents[0], gg);
            LayoutSupportManager laysup = targetContainer.getLayoutSupport();
            Container cont = formDesigner.getComponent(targetContainer);
            Container contDel = targetContainer.getContainerDelegate(cont);
            Point contPos = convertPointFromComponent(0, 0, contDel);
            g.setColor(formSettings.getSelectionBorderColor());
            g.setStroke(ComponentDragger.dashedStroke1);
            g.translate(contPos.x, contPos.y);
            laysup.paintDragFeedback(cont, contDel,
                    showingComponents[0],
                    constraints, this.index,
                    g);
            g.translate(-contPos.x, -contPos.y);
        }
    }

    private class NewComponentDropListener implements DropTargetListener {

        private NewComponentDrop newComponentDrop;
        private int dropAction;
        /**
         * Assistant context requested by newComponentDrop.
         */
        private String dropContext;
        /**
         * Additional assistant context requested by newComponentDrop.
         */
        private String additionalDropContext;

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            try {
                dropAction = dtde.getDropAction();
                newComponentDrop = null;
                Transferable transferable = dtde.getTransferable();
                PaletteItem item = null;
                if (dtde.isDataFlavorSupported(PaletteController.ITEM_DATA_FLAVOR)) {
                    Lookup itemLookup = (Lookup) transferable.getTransferData(PaletteController.ITEM_DATA_FLAVOR);
                    item = itemLookup.lookup(PaletteItem.class);
                } else {
                    Lookup.Template<NewComponentDropProvider> template = new Lookup.Template<>(NewComponentDropProvider.class);
                    Collection<? extends NewComponentDropProvider> providers = Lookup.getDefault().lookup(template).allInstances();
                    for (NewComponentDropProvider provider : providers) {
                        newComponentDrop = provider.processTransferable(getFormModel(), transferable);
                        if (newComponentDrop != null) {
                            dropContext = null;
                            AssistantModel aModel = getFormModel().getAssistantModel();
                            String preContext = aModel.getContext();
                            item = newComponentDrop.getPaletteItem(dtde);
                            String postContext = aModel.getContext();
                            if (!preContext.equals(postContext)) {
                                dropContext = postContext;
                                additionalDropContext = aModel.getAdditionalContext();
                            }
                            break;
                        }
                    }
                }
                //switch to the menu layer if this is a menu component other than JMenuBar
                if (item != null && MenuEditLayer.isMenuRelatedComponentClass(item.getComponentClass())
                        && !MenuBar.class.isAssignableFrom(item.getComponentClass())) {
                    if (!formDesigner.getMenuEditLayer().isDragProxying()) {
                        formDesigner.getMenuEditLayer().startNewMenuComponentDragAndDrop(item);
                        return;
                    }
                }
                if (item != null) {
                    if (item.getComponentClassName().indexOf('.') != -1) // Issue 79573
                    {
                        componentDrag = new NewComponentDrag(item);
                        componentDrag.move(dtde.getLocation(), 0);
                        repaint();
                    } else {
                        dtde.rejectDrag();
                    }
                } else {
                    dtde.rejectDrag();
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }

        @Override
        public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
            if (componentDrag != null) {
                if ((newComponentDrop != null) && (dropAction != dtde.getDropAction())) {
                    dragExit(dtde);
                    dragEnter(dtde);
                    return;
                }
                componentDrag.move(dtde.getLocation(), 0);
                if (dropContext != null) {
                    getFormModel().getAssistantModel().setContext(dropContext, additionalDropContext);
                }
                repaint();
            }
        }

        @Override
        public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {
        }

        @Override
        public void dragExit(java.awt.dnd.DropTargetEvent dte) {
            if (componentDrag != null) {
                endDragging(null);
                repaint();
            }
        }

        @Override
        public void drop(java.awt.dnd.DropTargetDropEvent dtde) {
            if (componentDrag != null) {
                NewComponentDrag newComponentDrag = ((NewComponentDrag) componentDrag);
                try {
                    newComponentDrag.end(dtde.getLocation(), 0);
                } finally {
                    componentDrag = null;
                    draggingEnded = true;
                }
                if (newComponentDrag.addedComponent != null) {
                    String id = newComponentDrag.addedComponent.getName();
                    if (newComponentDrop != null) {
                        String droppedOverName = null;
                        if (!(newComponentDrag.addedComponent instanceof RADVisualComponent<?>)) {
                            RADComponent<?> comp = getRadComponentAt(dtde.getLocation(), COMP_DEEPEST);
                            if (comp != null) {
                                droppedOverName = comp.getName();
                            }
                        }
                        newComponentDrop.componentAdded(id, droppedOverName);
                    }
                }
                formDesigner.toggleSelectionMode();
                formDesigner.requestActive();
            }
        }
    }
}
