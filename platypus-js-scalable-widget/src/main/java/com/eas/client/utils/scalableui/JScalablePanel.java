/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.utils.scalableui;

import com.sun.java.swing.SwingUtilities3;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.ListUI;
import javax.swing.plaf.TableUI;
import javax.swing.plaf.TreeUI;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;

/**
 *
 * @author Mg
 */
public class JScalablePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, DropTargetListener {

    protected AutoScrollTimerTask autoscrollingTask;
    protected Timer autoscrollingTimer;
    protected DefaultDesktopManager dummyDesktopManager = new DefaultDesktopManager();
    protected EventsTargetPanel eventsTarget;
    protected DrawWallPanel drawWall;
    protected CardLayout cl = new CardLayout();
    protected Component dragTarget;
    protected Point innerDropTargetPoint;
    protected Component innerDropTargetComponent;
    protected int enterSelectedIndex = -1;
    protected TreePath enterSelectedPath;
    protected JScrollPane autoscrollingPane;
    protected Point autoscrollingDirection;
    protected Component oldInnerDropTarget;
    protected Point prevMouseXY;
    protected boolean forceHidePopups;
    protected Set<ScaleListener> scaleListeners = new HashSet<>();
    protected Set<JTextComponent> editingTexts = new HashSet<>();
    protected static final float FLOAT_TOLERANCE = 1e-10f;
    protected static final float WHEEL_ZOOM_STEP = 0.1f;
    protected float SCALE_STEP_VALUE = WHEEL_ZOOM_STEP;
    protected static final float ZOOM_MAXIMUM = 1e+10f;
    protected static final float ZOOM_MINIMUM = 1e-10f;
    protected static final int AUTO_SCROLLING_DELAY = 100;
    protected static final int INSET_ZONE = 10;
    protected static final String TEXT_COMPS_EDITABLE = "isTextEditable";
    protected static final String TEXT_COMPS_BACKGROUND = "TextBackground";
    protected static final String TEXT_COMPS_HINT = "TextComponentHint";
    public static final String MOUSE_PRESSED_PT_CLIENT_PROPERTY = "mousePressedPt";
    protected static final ResourceBundle localizations = ResourceBundle.getBundle("com/eas/client/utils/scalableui/localizations");
    protected float autoscrollingVelocityFactor = 3f;

    protected static MouseEvent cloneMouseEvent(MouseEvent me, Object source, int eId, Point pt) {
        if (source != null && source instanceof Component) {
            return new MouseEvent((Component) source, eId, me.getWhen(), me.getModifiers(), pt.x, pt.y, me.getXOnScreen(), me.getYOnScreen(), me.getClickCount(), me.isPopupTrigger(), me.getButton());
        } else {
            return me;
        }
    }

    protected static MouseWheelEvent cloneMouseWheelEvent(MouseWheelEvent me, Object source, Point pt) {
        if (source != null && source instanceof Component) {
            return new MouseWheelEvent((Component) source, me.getID(), me.getWhen(), me.getModifiers(), pt.x, pt.y, me.getXOnScreen(), me.getYOnScreen(), me.getClickCount(), me.isPopupTrigger(), me.getScrollType(), me.getScrollAmount(), me.getWheelRotation());
        } else {
            return me;
        }
    }

    public JScalablePanel() {
        super();
        setLayout(cl);

        autoscrollingTask = new AutoScrollTimerTask();
        autoscrollingTimer = new Timer(AUTO_SCROLLING_DELAY, autoscrollingTask);

        setOpaque(false);
        EventsTargetPanel leventsTarget = new EventsTargetPanel();
        drawWall = new DrawWallPanel(leventsTarget);
        add(drawWall, DrawWallPanel.class.getSimpleName());
        add(leventsTarget, EventsTargetPanel.class.getSimpleName());
        eventsTarget = leventsTarget;
        drawWall.addMouseListener(this);
        drawWall.addMouseMotionListener(this);
        drawWall.addMouseWheelListener(this);
        if (PopupFactory.getSharedInstance() == null || !(PopupFactory.getSharedInstance() instanceof ScalablePopupFactory)) {
            PopupFactory.setSharedInstance(new ScalablePopupFactory(PopupFactory.getSharedInstance()));
        }
        try {
            ScalableTransferHandler sclTHandler = new ScalableTransferHandler(this);
            setTransferHandler(sclTHandler);
            getDropTarget().addDropTargetListener(this);
        } catch (TooManyListenersException ex) {
            Logger.getLogger(JScalablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public float getScaleStepValue() {
        return SCALE_STEP_VALUE;
    }

    public void setSCALE_STEP_VALUE(float aStep) {
        SCALE_STEP_VALUE = aStep;
        if (SCALE_STEP_VALUE <= 0) {
            SCALE_STEP_VALUE = 0.2f;
        }
    }

    public void zoomIn() {
        scaleBy(SCALE_STEP_VALUE);
    }

    public void zoomOut() {
        scaleBy(-SCALE_STEP_VALUE);
    }

    @Override
    public void setLayout(LayoutManager mgr) {
        if (eventsTarget != null) {
            eventsTarget.setLayout(mgr);
        } else {
            super.setLayout(mgr);
        }
    }

    @Override
    public Component add(Component comp) {
        if (eventsTarget != null) {
            return eventsTarget.add(comp);
        } else {
            return super.add(comp);
        }
    }

    @Override
    public Component add(Component comp, int index) {
        if (eventsTarget != null) {
            return eventsTarget.add(comp, index);
        } else {
            return add(comp, index);
        }
    }

    @Override
    public void add(Component comp, Object constraints) {
        if (eventsTarget != null) {
            eventsTarget.add(comp, constraints);
        } else {
            super.add(comp, constraints);
        }
    }

    @Override
    public Component add(String name, Component comp) {
        if (eventsTarget != null) {
            return eventsTarget.add(name, comp);
        } else {
            return super.add(name, comp);
        }
    }

    @Override
    public void add(Component comp, Object constraints, int index) {
        if (eventsTarget != null) {
            eventsTarget.add(comp, constraints, index);
        } else {
            super.add(comp, constraints, index);
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        checkComponents();
    }

    protected void checkComponents(JComponent root) {
        root.addKeyListener(this);
        SwingUtilities3.setDelegateRepaintManager(root, new ZoomRepaintManager());
        if (root instanceof JInternalFrame) {
            JInternalFrame lIf = (JInternalFrame) root;
            if (lIf.getDesktopIcon() != null) {
                checkComponents(lIf.getDesktopIcon());
            }
        }
        if (root instanceof JScrollPane) {
            JScrollPane scrPane = (JScrollPane) root;
            JViewport vport = scrPane.getViewport();
            if (vport != null) {
                vport.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
                Component view = vport.getView();
                if (view instanceof JComponent) {
                    ((JComponent) view).setOpaque(false);
                }
            }
        }
        if (root instanceof JComboBox) {
            ((JComboBox) root).setUI(new ScalableComboUI(((JComboBox) root).getUI()));
        }
        if (root instanceof JTextComponent) {
            JTextComponent tc = (JTextComponent) root;
            tc.putClientProperty(TEXT_COMPS_EDITABLE, tc.isEditable());
            tc.putClientProperty(TEXT_COMPS_BACKGROUND, tc.getBackground());
            tc.putClientProperty(TEXT_COMPS_HINT, tc.getToolTipText());
            tc.setEditable(false);
            tc.setBackground(Color.lightGray);
            tc.setToolTipText(localizations.getString("Click2Edit"));
        }
        for (int i = 0; i < root.getComponentCount(); i++) {
            Component comp = root.getComponent(i);
            if (comp instanceof JComponent) {
                checkComponents(((JComponent) comp));
            }
        }
    }

    public void checkComponents() {
        if (eventsTarget != null) {
            EventsTargetPanel leventsTarget = eventsTarget;
            eventsTarget = null;
            try {
                checkComponents(leventsTarget);
                addKeyListener(this);
                drawWall.addKeyListener(this);
            } finally {
                eventsTarget = leventsTarget;
            }
        }
    }

    public void removeAllPopups() {
        if (eventsTarget != null) {
            for (int i = 0; i < eventsTarget.getComponentCount(); i++) {
                if (eventsTarget.getComponent(i) instanceof ScalablePopup.ScalablePopupPanel) {
                    removeAllPopups((JComponent) eventsTarget.getComponent(i));
                }
            }
        }
    }

    protected void removeAllPopups(JComponent root) {
        if (root instanceof JPopupMenu) {
            JPopupMenu lppm = (JPopupMenu) root;
            forceHidePopups = true;
            try {
                lppm.setVisible(false);
            } finally {
                forceHidePopups = false;
            }
        } else {
            for (int i = root.getComponentCount() - 1; i >= 0; i--) {
                Component lcomp = root.getComponent(i);
                if (lcomp instanceof JComponent) {
                    removeAllPopups((JComponent) lcomp);
                }
            }
        }
    }

    public boolean isForceHidePopups() {
        return forceHidePopups;
    }

    public float getScale() {
        if (eventsTarget != null) {
            return eventsTarget.getScale();
        } else {
            return 1f;
        }
    }

    public void fitRectangle(Rectangle aRect, float aScale) {
        if (aScale <= 0) {
            aScale = 0.1f;
        }
        Container pCont = getParent();
        if (aRect != null && pCont != null && pCont instanceof JViewport) {
            JViewport vp = (JViewport) pCont;
            Dimension windowSize = vp.getExtentSize();
            if (windowSize != null) {
                float wF = (float) windowSize.width * aScale / (float) aRect.width;
                float hF = (float) windowSize.height * aScale / (float) aRect.height;
                float scale = 1;
                if (wF < hF) {
                    scale = wF;
                } else {
                    scale = hF;
                }
                setScale(scale);
                Point center = new Point(Math.round((aRect.x + aRect.width / 2) * scale), Math.round((aRect.y + aRect.height / 2) * scale));
                final Rectangle fitRect = new Rectangle(center.x - windowSize.width / 2, center.y - windowSize.height / 2, windowSize.width, windowSize.height);
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        scrollRectToVisible(fitRect);
                    }
                });
            }
        }
    }

    public void makeVisible(Rectangle aRect) {
        Container pCont = getParent();
        if (aRect != null && pCont != null && pCont instanceof JViewport) {
            JViewport vp = (JViewport) pCont;
            Dimension windowSize = vp.getExtentSize();
            if (windowSize != null) {
                Point center = new Point(Math.round((aRect.x + aRect.width / 2) * getScale()), Math.round((aRect.y + aRect.height / 2) * getScale()));
                final Rectangle fitRect = new Rectangle(center.x - windowSize.width / 2, center.y - windowSize.height / 2, windowSize.width, windowSize.height);
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        scrollRectToVisible(fitRect);
                    }
                });
            }
        }
    }

    public boolean isPainting() {
        if (eventsTarget != null) {
            return eventsTarget.isPainting();
        } else {
            return false;
        }
    }

    public void scaleBy(float aScale) {
        setScale(getScale() + aScale);
    }

    public void setScale(float aScale) {
        if (!isAutoscrollingActive() && (dragTarget == null || dragTarget instanceof JMenuItem)) {
            if (Math.abs(aScale) < FLOAT_TOLERANCE) {
                aScale = aScale / Math.abs(aScale) * FLOAT_TOLERANCE;
            }
            if (aScale > ZOOM_MAXIMUM) {
                aScale = ZOOM_MAXIMUM;
            }
            if (aScale < ZOOM_MINIMUM) {
                aScale = ZOOM_MINIMUM;
            }

            eventsTarget.setScale(aScale);
            Dimension lpref = eventsTarget.getPreferredSize();
            setSize(lpref);
            fireScaleChanged();
            if (getScale() != 1.0f) {
                clearEditingTexts();
            }
            removeAllPopups();
        }
    }

    public float getAutoscrollingVelocityFactor() {
        return autoscrollingVelocityFactor;
    }

    public void setAutoscrollingVelocityFactor(float factor) {
        autoscrollingVelocityFactor = factor;
        if (autoscrollingVelocityFactor < 0) {
            autoscrollingVelocityFactor = Math.abs(autoscrollingVelocityFactor);
        }
        if (Math.abs(autoscrollingVelocityFactor) < FLOAT_TOLERANCE) {
            autoscrollingVelocityFactor = 2 * FLOAT_TOLERANCE;
        }
    }

    public void addScaleListener(ScaleListener scaleListener) {
        if (!scaleListeners.contains(scaleListener)) {
            scaleListeners.add(scaleListener);
        }
    }

    public void removeScaleListener(ScaleListener scaleListener) {
        scaleListeners.remove(scaleListener);
    }

    public void fireScaleChanged() {
        Iterator<ScaleListener> lit = scaleListeners.iterator();
        if (lit != null) {
            while (lit.hasNext()) {
                ScaleListener sl = lit.next();
                sl.scaleChanged(eventsTarget.getOldScale(), eventsTarget.getScale());
            }
        }
    }

    protected void clearEditingTexts() {
        Iterator<JTextComponent> lit = editingTexts.iterator();
        if (lit != null) {
            while (lit.hasNext()) {
                JTextComponent ltc = lit.next();
                ltc.putClientProperty(TEXT_COMPS_EDITABLE, ltc.isEditable());
                ltc.putClientProperty(TEXT_COMPS_BACKGROUND, ltc.getBackground());
                ltc.putClientProperty(TEXT_COMPS_HINT, ltc.getToolTipText());

                ltc.setEditable(false);
                ltc.setBackground(Color.lightGray);
                ltc.setToolTipText(localizations.getString("Click2Edit"));
                if (ltc.getCaret() != null) {
                    ltc.getCaret().setVisible(false);
                }
            }
        }
        editingTexts.clear();
    }

    public EventsTargetPanel getContentPanel() {
        return eventsTarget;
    }

    public DrawWallPanel getDrawWall() {
        return drawWall;
    }

    protected ScalablePopup.ScalablePopupPanel findPopupPanelAt(Container root, Point pt) {
        if (pt == null) {
            return null;
        }
        Point lUnscaledPt = new Point(Math.round(pt.x / getScale()), Math.round(pt.y / getScale()));
        Component lfound = findComponentAtXY(root, lUnscaledPt.x, lUnscaledPt.y, false, true, null);
        if (lfound instanceof ScalablePopup.ScalablePopupPanel) {
            return (ScalablePopup.ScalablePopupPanel) lfound;
        }
        return null;
    }

    protected Component findComponentAt(Container root, Point pt) {
        if (pt == null) {
            return null;
        }
        Point lUnscaledPt = new Point(Math.round(pt.x / getScale()), Math.round(pt.y / getScale()));
        return findComponentAtXY(root, lUnscaledPt.x, lUnscaledPt.y, false, false, null);
    }

    protected JScrollPane findScrollPaneAt(Container root, Point pt) {
        if (pt == null) {
            return null;
        }
        Point lUnscaledPt = new Point(Math.round(pt.x / getScale()), Math.round(pt.y / getScale()));
        JScrollPane innerSp = findScrollPaneAtXY(root, lUnscaledPt.x, lUnscaledPt.y, null);
        if (innerSp == null) {
            Point convertedPt = SwingUtilities.convertPoint(this, pt, root);
            while (root != null && (!(root instanceof JScrollPane) || !isInnerInsetPt((JScrollPane) root, convertedPt))) {
                root = root.getParent();
                convertedPt = SwingUtilities.convertPoint(this, pt, root);
            }
            if (root instanceof JScrollPane && isInnerInsetPt((JScrollPane) root, convertedPt)) {
                return (JScrollPane) root;
            }
        }
        return innerSp;
    }

    protected static boolean acceptMouseTarget(Component comp) {
        return !(comp instanceof JLabel);
    }

    protected Component findComponentAtXY(Container root, int x, int y, boolean ignoreEnabled, boolean stopAtPopupPanel, Component toExclude) {
        synchronized (getTreeLock()) {
            if (!(root.contains(x, y) && (ignoreEnabled || root.isEnabled()))) {
                return null;
            } else if (root.contains(x, y) && stopAtPopupPanel && root instanceof ScalablePopup.ScalablePopupPanel) {
                return root;
            }
            int ncomponents = root.getComponentCount();
            Component component[] = root.getComponents();

            for (int i = 0; i < ncomponents; i++) {
                Component comp = component[i];
                if (toExclude == comp) {
                    continue;
                }
                if (comp != null && comp.isLightweight()) {
                    if (comp instanceof Container) {
                        comp = findComponentAtXY(((Container) comp), x - comp.getX(),
                                y - comp.getY(), ignoreEnabled, stopAtPopupPanel, toExclude);
                    } else {
                        comp = comp.getComponentAt(x - comp.getX(), y - comp.getY());
                    }

                    if (comp != null && comp.isVisible()
                            && (ignoreEnabled || comp.isEnabled())
                            && acceptMouseTarget(comp)) {
                        return comp;
                    }
                }
            }
            return root;
        }
    }

    protected JScrollPane findScrollPaneAtXY(Container root, int x, int y, Component toExclude) {
        Point lTestPt = new Point(x, y);
        synchronized (getTreeLock()) {
            if (!root.contains(x, y)) {
                return null;
            } else if (root.contains(x, y) && root instanceof JScrollPane && isInnerInsetPt((JScrollPane) root, lTestPt)) {
                return (JScrollPane) root;
            }

            int ncomponents = root.getComponentCount();
            Component component[] = root.getComponents();

            for (int i = 0; i < ncomponents; i++) {
                Component comp = component[i];
                if (toExclude == comp) {
                    continue;
                }
                if (comp != null && comp.isLightweight()) {
                    if (comp instanceof Container) {
                        comp = findScrollPaneAtXY(((Container) comp), x - comp.getX(),
                                y - comp.getY(), toExclude);
                    } else {
                        comp = comp.getComponentAt(x - comp.getX(), y - comp.getY());
                    }

                    if (comp != null && comp.isVisible()
                            && comp instanceof JScrollPane) {
                        return (JScrollPane) comp;
                    }
                }
            }
            if (root != null && root.isVisible() && root instanceof JScrollPane && isInnerInsetPt((JScrollPane) root, lTestPt)) {
                return (JScrollPane) root;
            }
            return null;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        dragTarget = null;
        innerDropTargetComponent = null;
        enterSelectedIndex = -1;
        enterSelectedPath = null;
        cancelAutoScrolling();

        Component lc = findComponentAt(eventsTarget, e.getPoint());
        if (lc != null) {
            componentReleased = lc;
            if (lc instanceof JTextComponent && e.getClickCount() > 1) {
                JTextComponent tc = (JTextComponent) lc;
                Object lPropEditable = tc.getClientProperty(TEXT_COMPS_EDITABLE);
                if (lPropEditable != null && lPropEditable instanceof Boolean) {
                    tc.setEditable((Boolean) lPropEditable);
                }
                Object lPropBackground = tc.getClientProperty(TEXT_COMPS_BACKGROUND);
                if (lPropBackground != null && lPropBackground instanceof Color) {
                    tc.setBackground((Color) lPropBackground);
                }
                Object lPropHint = tc.getClientProperty(TEXT_COMPS_HINT);
                if (lPropHint == null) {
                    tc.setToolTipText(null);
                }
                if (lPropHint != null && lPropHint instanceof String) {
                    tc.setToolTipText((String) lPropHint);
                }
                if (tc.isEditable()) {
                    if (tc.getCaret() != null) {
                        tc.getCaret().setVisible(true);
                    }
                    scaleWithStableMouse(1f / getScale() - 1, e.getPoint());
                    editingTexts.add(tc);
                }
            }
            Point lpt = convertPoint(e.getPoint(), lc);
            MouseListener[] mlisteners = lc.getMouseListeners();
            if (mlisteners != null) {
                if (componentPressed == componentReleased) {
                    for (int i = 0; i < mlisteners.length; i++) {
                        mlisteners[i].mouseClicked(cloneMouseEvent(e, lc, e.getID(), lpt));
                    }
                }
            }
        }
    }

    protected void hideCurrentPopup() {
        ScalablePopup.ScalablePopupPanel lpnl = (ScalablePopup.ScalablePopupPanel) eventsTarget.getComponent(0);
        final JComponent lcont = lpnl.getContents();
        if (lcont != null) {
            forceHidePopups = true;
            try {
                lcont.setVisible(false);
            } finally {
                forceHidePopups = false;
            }
        }
    }

    public boolean isTransferHandler(Component aComp) {
        return aComp.getDropTarget() != null;
    }

    public DropTarget getTransferHandler(Component aComp) {
        return aComp.getDropTarget();
    }
    private Component componentPressed = null;

    @Override
    public void mousePressed(MouseEvent e) {
        dragTarget = null;
        innerDropTargetComponent = null;
        Component lWasOwner = null;
        ScalablePopup.ScalablePopupPanel lppm = findPopupPanelAt(eventsTarget, e.getPoint());
        if (lppm == null
                && eventsTarget.getComponentCount() > 0
                && eventsTarget.getComponent(0) instanceof ScalablePopup.ScalablePopupPanel) {
            ScalablePopup.ScalablePopupPanel lWasppm = (ScalablePopup.ScalablePopupPanel) eventsTarget.getComponent(0);
            lWasOwner = lWasppm.getContentsOwner();
            hideCurrentPopup();
        }

        Component lc = findComponentAt(eventsTarget, e.getPoint());
        if (lc != null) {
            componentPressed = lc;
            if(SwingUtilities.isLeftMouseButton(e))
                dragTarget = lc;
            Point lpt = convertPoint(e.getPoint(), lc);
            dragTargetInnerPt = lpt;
            MouseListener[] mlisteners = lc.getMouseListeners();
            if (mlisteners != null) {
                if (lc instanceof JMenuItem) {
                    for (int i = 0; i < mlisteners.length; i++) {
                        mlisteners[i].mouseReleased(cloneMouseEvent(e, lc, MouseEvent.MOUSE_RELEASED, lpt));
                    }
                } else {
                    for (int i = 0; i < mlisteners.length; i++) {
                        mlisteners[i].mousePressed(cloneMouseEvent(e, lc, e.getID(), lpt));
                    }
                    lc.requestFocus();
                }
            }
            if (eventsTarget.getComponentCount() > 0
                    && eventsTarget.getComponent(0) instanceof ScalablePopup.ScalablePopupPanel) {
                ScalablePopup.ScalablePopupPanel lWasppm = (ScalablePopup.ScalablePopupPanel) eventsTarget.getComponent(0);
                Component lNewOwner = lWasppm.getContentsOwner();
                if (lNewOwner == lWasOwner) {
                    hideCurrentPopup();
                }
            }
        }
    }
    private Component componentReleased = null;

    @Override
    public void mouseReleased(MouseEvent e) {
        cancelAutoScrolling();
        Component lc = null;
        if (dragTarget != null) {
            lc = dragTarget;
        }
        dragTarget = null;
        if (lc == null) {
            lc = findComponentAt(eventsTarget, e.getPoint());
        }
        if (lc != null) {
            Point lpt = convertPoint(e.getPoint(), lc);

            if (lc instanceof JComponent && SwingUtilities.isRightMouseButton(e)) {
                JComponent ljc = (JComponent) lc;
                JPopupMenu jpm = ljc.getComponentPopupMenu();
                if (jpm != null) {
                    ljc.putClientProperty(MOUSE_PRESSED_PT_CLIENT_PROPERTY, lpt);
                    jpm.show(lc, lpt.x, lpt.y);
                }
            }
            MouseListener[] mlisteners = lc.getMouseListeners();

            if (mlisteners != null) {
                if (lc instanceof JMenuItem) {
                    for (int i = 0; i < mlisteners.length; i++) {
                        mlisteners[i].mouseReleased(cloneMouseEvent(e, lc, MouseEvent.MOUSE_RELEASED, lpt));
                    }
                } else {
                    for (int i = 0; i < mlisteners.length; i++) {
                        mlisteners[i].mouseReleased(cloneMouseEvent(e, lc, e.getID(), lpt));
                    }
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        prevMouseXY = e.getPoint();
        Component lc = findComponentAt(eventsTarget, e.getPoint());
        if (lc != null) {
            Point lpt = convertPoint(e.getPoint(), lc);
            MouseListener[] mlisteners = lc.getMouseListeners();
            if (mlisteners != null) {
                for (int i = 0; i < mlisteners.length; i++) {
                    mlisteners[i].mouseEntered(cloneMouseEvent(e, lc, e.getID(), lpt));
                }
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        cancelAutoScrolling();
        prevMouseXY = null;
        Component lc = findComponentAt(eventsTarget, e.getPoint());
        if (lc != null) {
            Point lpt = convertPoint(e.getPoint(), lc);
            MouseListener[] mlisteners = lc.getMouseListeners();
            if (mlisteners != null) {
                for (int i = 0; i < mlisteners.length; i++) {
                    mlisteners[i].mouseExited(cloneMouseEvent(e, lc, e.getID(), lpt));
                }
            }
        }
    }

    protected class AutoScrollTimerTask extends Object implements ActionListener {

        MouseEvent event = null;

        public AutoScrollTimerTask() {
            super();
        }

        public AutoScrollTimerTask(MouseEvent e) {
            this();
            event = e;
        }

        public MouseEvent getEvent() {
            return event;
        }

        public void incEvent(Point increment) {
            if (event != null) {
                Point eventPoint = event.getPoint();
                eventPoint.x += increment.x;
                eventPoint.y += increment.y;
                event = cloneMouseEvent(event, event.getSource(), event.getID(), eventPoint);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            autoScrollingStep(this);
        }

        public void setEvent(MouseEvent e) {
            event = e;
        }
    }

    protected JDesktopIcon iifDesktopIcon(Object aSource) {
        Component lparent = null;
        if (aSource instanceof Component) {
            lparent = (Component) aSource;
            while (lparent != null && !(lparent instanceof JDesktopIcon)) {
                lparent = lparent.getParent();
            }
            if (lparent != null) {
                return (JDesktopIcon) lparent;
            }
        }
        return null;
    }

    protected JInternalFrame iifInternalFrame(Object aSource) {
        Component lparent = null;
        if (aSource instanceof Component) {
            lparent = (Component) aSource;
            while (lparent != null && !(lparent instanceof JInternalFrame)) {
                lparent = lparent.getParent();
            }
            if (lparent != null) {
                return (JInternalFrame) lparent;
            }
        }
        return null;
    }

    protected boolean isAutoscrollingActive() {
        return autoscrollingTimer != null && autoscrollingTimer.isRunning();
    }
    protected Point dragTargetInnerPt = null;

    protected void autoScrollingStep(AutoScrollTimerTask asTask) {
        if (autoscrollingPane != null && autoscrollingDirection != null && asTask != null) {
            JScrollPane lautoscrollingPane = autoscrollingPane;
            Component lviewComp = lautoscrollingPane.getViewport().getView();
            if (lviewComp instanceof JComponent) {
                JComponent lview = (JComponent) lviewComp;
                Point lautoscrollingDirection = autoscrollingDirection;
                Rectangle viewRect = lautoscrollingPane.getViewport().getViewRect();
                Rectangle viewRectBeforeMovement = (Rectangle) viewRect.clone();
                viewRect.translate(lautoscrollingDirection.x, lautoscrollingDirection.y);
                lview.scrollRectToVisible(viewRect);
                Rectangle viewRectAfterMovement = lautoscrollingPane.getViewport().getViewRect();
                lautoscrollingDirection.x = viewRectAfterMovement.x - viewRectBeforeMovement.x;
                lautoscrollingDirection.y = viewRectAfterMovement.y - viewRectBeforeMovement.y;
                if (lautoscrollingDirection.x != 0
                        || lautoscrollingDirection.y != 0) {
                    MouseEvent e = asTask.getEvent();
                    if (e != null && dragTarget != null) {
                        JDesktopPane ldp = null;
                        DesktopManager ldm = null;
                        JInternalFrame lif = null;
                        JDesktopIcon ldi = null;
                        lif = iifInternalFrame(dragTarget);
                        if (lif != null && lif.getDesktopPane() != null) {
                            ldp = lif.getDesktopPane();
                            ldm = ldp.getDesktopManager();
                        } else {
                            ldi = iifDesktopIcon(dragTarget);
                            if (ldi != null && ldi.getDesktopPane() != null) {
                                ldp = ldi.getDesktopPane();
                                ldm = ldp.getDesktopManager();
                            }
                        }
                        if (ldp != null && ldm != null && (lif != null || ldi != null)) {
                            MouseListener[] mlisteners = dragTarget.getMouseListeners();
                            MouseMotionListener[] mmlisteners = dragTarget.getMouseMotionListeners();
                            if (mlisteners != null && mmlisteners != null) {
                                ldp.setDesktopManager(dummyDesktopManager);
                                try {
                                    for (int i = 0; i < mlisteners.length; i++) {
                                        mlisteners[i].mousePressed(cloneMouseEvent(e, dragTarget, e.getID(), dragTargetInnerPt));
                                    }
                                } finally {
                                    ldp.setDesktopManager(ldm);
                                }
                                asTask.incEvent(lautoscrollingDirection);
                                MouseEvent afterMoveE = asTask.getEvent();
                                Point afterMovePt = convertPoint(afterMoveE.getPoint(), dragTarget);
                                Dimension beforeDragSize = dragTarget.getSize();
                                int lbeforeWidth = dragTarget.getWidth();
                                int lbeforeHeight = dragTarget.getHeight();
                                for (int i = 0; i < mmlisteners.length; i++) {
                                    mmlisteners[i].mouseDragged(cloneMouseEvent(e, dragTarget, e.getID(), afterMovePt));
                                }
                                Dimension afterDragSize = dragTarget.getSize();
                                if (beforeDragSize != null && afterDragSize != null && !beforeDragSize.equals(afterDragSize) && dragTarget != null && dragTarget instanceof JComponent) {
                                    int ldx = afterDragSize.width - beforeDragSize.width;
                                    int ldy = afterDragSize.height - beforeDragSize.height;
                                    JComponent jDragTarget = (JComponent) dragTarget;
                                    Insets insts = jDragTarget.getInsets();
                                    if (dragTargetInnerPt.x >= lbeforeWidth - insts.right) {
                                        dragTargetInnerPt.x += ldx;
                                    }
                                    if (dragTargetInnerPt.y >= lbeforeHeight - insts.bottom) {
                                        dragTargetInnerPt.y += ldy;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                cancelAutoScrolling();
            }
        }
    }

    protected Point getInnerInsetPt(JScrollPane testComp, Point aPt) {
        Point res = new Point(0, 0);
        if (testComp != null && aPt != null) {
            int autoscrollZone = Math.round(INSET_ZONE * autoscrollingVelocityFactor);
            Rectangle viewRect = autoscrollingPane.getBounds();
            JScrollBar vSb = autoscrollingPane.getVerticalScrollBar();
            if (vSb != null && vSb.isVisible()) {
                viewRect.width -= vSb.getWidth();
            }
            JScrollBar hSb = autoscrollingPane.getHorizontalScrollBar();
            if (hSb != null && hSb.isVisible()) {
                viewRect.height -= hSb.getHeight();
            }
            if (aPt.x > 0 && aPt.x < INSET_ZONE) {
                res.x = -autoscrollZone;
            } else if (aPt.x > viewRect.width - INSET_ZONE && aPt.x < viewRect.width) {
                res.x = autoscrollZone;
            }
            if (aPt.y > 0 && aPt.y < INSET_ZONE) {
                res.y = -autoscrollZone;
            } else if (aPt.y > viewRect.height - INSET_ZONE && aPt.y < viewRect.height) {
                res.y = autoscrollZone;
            }
        }
        return res;
    }

    protected boolean isInnerInsetPt(JScrollPane testComp, Point aPt) {
        if (testComp == null) {
            return false;
        }
        Rectangle bounds = testComp.getBounds();
        bounds.x = 0;
        bounds.y = 0;
        JScrollBar vSb = testComp.getVerticalScrollBar();
        if (vSb != null && vSb.isVisible()) {
            bounds.width -= vSb.getWidth();
        }
        JScrollBar hSb = testComp.getHorizontalScrollBar();
        if (hSb != null && hSb.isVisible()) {
            bounds.height -= hSb.getHeight();
        }
        return ((aPt.x > 0 && aPt.x < INSET_ZONE)
                || (aPt.x > bounds.width - INSET_ZONE && aPt.x < bounds.width)
                || (aPt.y > 0 && aPt.y < INSET_ZONE)
                || (aPt.y > bounds.height - INSET_ZONE && aPt.y < bounds.height));
    }

    protected void cancelAutoScrolling() {
        autoscrollingTimer.stop();
        autoscrollingPane = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e != null) {
            cancelAutoScrolling();
            Point lcurrPoint = e.getPoint();

            Component lprevc = findComponentAt(eventsTarget, prevMouseXY);
            Component lcurrentc = findComponentAt(eventsTarget, lcurrPoint);
            checkMouseEnterExit(e, lcurrPoint, lprevc, lcurrentc);


            JScrollPane llc = findScrollPaneAt(eventsTarget, lcurrPoint);
            Component lc = null;

            if (dragTarget == null) {
                lc = findComponentAt(eventsTarget, e.getPoint());
                dragTarget = lc;
            } else {
                lc = dragTarget;
            }

            if (lc != null) {   // schedule autoscrolling task if needed
                if (dragTarget != null && llc != null
                        && dragTarget != llc && llc.isAncestorOf(dragTarget)) {
                    if (llc.getViewport() != null && llc.getViewport().getView() != null) {
                        Component lviewcomp = llc.getViewport().getView();
                        if (lviewcomp instanceof JComponent) {
                            JComponent lview = (JComponent) lviewcomp;
                            if (lview.getAutoscrolls()) {
                                Point lMaybeInnerInsetPt = null;
                                if (isAncestorOf(llc)) {
                                    lMaybeInnerInsetPt = convertPoint(e.getPoint(), llc);
                                } else {
                                    lMaybeInnerInsetPt = SwingUtilities.convertPoint(this, lcurrPoint, llc);
                                }

                                if (isInnerInsetPt(llc, lMaybeInnerInsetPt)
                                        && !(dragTarget instanceof JScrollBar)) {
                                    autoscrollingPane = llc;
                                    autoscrollingDirection = getInnerInsetPt(llc, lMaybeInnerInsetPt);
                                    autoscrollingTask.setEvent(e);
                                    autoscrollingTimer.start();
                                }
                            }
                        }
                    }
                }
                Point lpt = convertPoint(e.getPoint(), lc);
                MouseMotionListener[] mlisteners = lc.getMouseMotionListeners();
                if (mlisteners != null) {
                    Dimension beforeDragSize = lc.getSize();
                    int lbeforeWidth = dragTarget.getWidth();
                    int lbeforeHeight = dragTarget.getHeight();
                    //
                    for (int i = 0; i < mlisteners.length; i++) {
                        mlisteners[i].mouseDragged(cloneMouseEvent(e, lc, e.getID(), lpt));
                    }
                    //
                    Dimension afterDragSize = lc.getSize();
                    if (beforeDragSize != null && afterDragSize != null && lc == dragTarget && dragTarget != null && !beforeDragSize.equals(afterDragSize)
                            && dragTarget instanceof JComponent) {
                        int ldx = afterDragSize.width - beforeDragSize.width;
                        int ldy = afterDragSize.height - beforeDragSize.height;
                        JComponent jDragTarget = (JComponent) dragTarget;
                        Insets insts = jDragTarget.getInsets();
                        if (dragTargetInnerPt.x >= lbeforeWidth - insts.right) {
                            dragTargetInnerPt.x += ldx;
                        }
                        if (dragTargetInnerPt.y >= lbeforeHeight - insts.bottom) {
                            dragTargetInnerPt.y += ldy;
                        }
                    }
                }
            }
            prevMouseXY = lcurrPoint;
        }
    }

    protected void checkMouseEnterExit(MouseEvent e, Point lnewPoint, Component lprevc, Component lc) {
        if (lprevc != lc) {
            if (lprevc != null) {
                Point lpt = convertPoint(lnewPoint, lprevc);
                if (!lprevc.contains(lpt)) {
                    MouseListener[] lprevmml = lprevc.getMouseListeners();
                    for (int i = 0; i < lprevmml.length; i++) {
                        lprevmml[i].mouseExited(cloneMouseEvent(e, lprevc, MouseEvent.MOUSE_EXITED, lpt));
                    }
                }
            }
            if (lc != null) {
                Point lpt = convertPoint(lnewPoint, lc);
                if (lc.contains(lpt)) {
                    MouseListener[] lcmml = lc.getMouseListeners();
                    for (int i = 0; i < lcmml.length; i++) {
                        lcmml[i].mouseEntered(cloneMouseEvent(e, lc, MouseEvent.MOUSE_ENTERED, lpt));
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point lnewPoint = e.getPoint();
        Component lprevc = findComponentAt(eventsTarget, prevMouseXY);
        Component lc = findComponentAt(eventsTarget, lnewPoint);
        checkMouseEnterExit(e, lnewPoint, lprevc, lc);
        if (dragTarget == null) {
            if (lc != null) {
                Point lpt = convertPoint(lnewPoint, lc);
                MouseMotionListener[] mlisteners = lc.getMouseMotionListeners();
                if (mlisteners != null) {
                    for (int i = 0; i < mlisteners.length; i++) {
                        mlisteners[i].mouseMoved(cloneMouseEvent(e, lc, e.getID(), lpt));
                    }
                }

                configureCursor(lc);

                if (lc instanceof JComponent) {
                    JComponent ljc = (JComponent) lc;
                    String toolTip = ljc.getToolTipText();
                    drawWall.setToolTipText(toolTip);
                } else {
                    drawWall.setToolTipText(null);
                }
            }
        } else {
            cancelAutoScrolling();
        }
        prevMouseXY = e.getPoint();
    }

    public void fit() {
        Dimension lSize = eventsTarget.getPreferredSize();
        lSize.height = Math.round(lSize.height);
        lSize.width = Math.round(lSize.width);
        JScrollPane lsp = (JScrollPane) getParent().getParent();
        Dimension lSpSize = lsp.getSize();
        float XScale = (float) lSpSize.width / (float) lSize.width;
        float YScale = (float) lSpSize.height / (float) lSize.height;
        if (XScale < YScale) {
            scaleBy(XScale);
        } else {
            scaleBy(YScale);
        }
    }

    protected Point calcLeftBottomRes() {
        Point lpt = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        if (getParent() != null && getParent().getParent() != null
                && getParent().getParent() instanceof JScrollPane) {
            JScrollPane lsp = (JScrollPane) getParent().getParent();
            Dimension lSpSize = lsp.getSize();

            JScrollBar lhsb = lsp.getHorizontalScrollBar();
            JScrollBar lvsb = lsp.getVerticalScrollBar();
            int lx = 0;
            int ly = 0;
            if (lhsb != null && lhsb.isVisible()) {
                lx = lhsb.getMaximum() - lhsb.getValue() - lSpSize.width;
            }
            if (lvsb != null && lvsb.isVisible()) {
                ly = lvsb.getMaximum() - lvsb.getValue() - lSpSize.height;
            }

            lpt.x = Math.max(0, lx);
            lpt.y = Math.max(0, ly);
        }
        return lpt;
    }

    protected void scaleWithStableMouse(float aScalePortion, Point aPt) {
        if (aPt != null) {
            if (getParent() != null && getParent() instanceof JViewport) {
                JViewport lvp = (JViewport) getParent();

                Rectangle lOldRect = eventsTarget.getBounds();
                Point loldMousePos = convertPoint(aPt, eventsTarget);

                Rectangle lbsViewRect = lvp.getViewRect();
                // Set scale
                scaleBy(aScalePortion);
                // Apply scale
                Point lvps = lvp.getViewPosition();
                lvps.x += 1;
                lvp.setViewPosition(lvps);
                lvps = lvp.getViewPosition();
                lvps.x -= 1;
                lvp.setViewPosition(lvps);

                Rectangle lNewRect = eventsTarget.getBounds();
                Point lnewMousePos = convertPoint(aPt, eventsTarget);

                lnewMousePos.x = Math.round((lnewMousePos.x - loldMousePos.x) * getScale());
                lnewMousePos.y = Math.round((lnewMousePos.y - loldMousePos.y) * getScale());

                if (lNewRect.width < lOldRect.width) {
                    int lAlreadyMovedX = Math.max(0, lbsViewRect.x + lbsViewRect.width - lNewRect.width);
                    lnewMousePos.x -= lAlreadyMovedX;
                    lnewMousePos.x = Math.max(0, lnewMousePos.x);
                }
                if (lNewRect.height < lOldRect.height) {
                    int lAlreadyMovedY = Math.max(0, lbsViewRect.y + lbsViewRect.height - lNewRect.height);
                    lnewMousePos.y -= lAlreadyMovedY;
                    lnewMousePos.y = Math.max(0, lnewMousePos.y);
                }
                boolean ldebug = true;
                if (ldebug) {
                    scrollBy(lnewMousePos);
                }
            } else {
                setScale(getScale() + aScalePortion);
            }
        }
    }

    protected void scaleWithoutStableMouse(float aScalePortion) {
        setScale(getScale() + aScalePortion);
    }

    protected void setScaleWithoutStableMouse(float aScale) {
        setScale(aScale);
    }

    protected void scrollBy(Point toScrollBy) {
        if (toScrollBy != null) {
            Rectangle lrt = getVisibleRect();
            JScrollPane lsp = null;
            if (getParent() != null && getParent().getParent() instanceof JScrollPane) {
                lsp = (JScrollPane) getParent().getParent();
                if (getParent() instanceof JViewport) {
                    JViewport lvp = (JViewport) getParent();
                    lrt = lvp.getViewRect();
                }
            }
            lrt.x -= toScrollBy.x;
            lrt.y -= toScrollBy.y;
            scrollRectToVisible(lrt);
        } else {
            toScrollBy = null;
        }
    }

    protected void configureCursor(Component aComp) {
        if (!isAutoscrollingActive()) {
            if (aComp.isCursorSet()) {
                drawWall.setCursor(aComp.getCursor());
            } else {
                drawWall.setCursor(null);
            }
        }
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        scaleWithStableMouse(-WHEEL_ZOOM_STEP * e.getWheelRotation(), e.getPoint());
        Component lc = findComponentAt(eventsTarget, e.getPoint());
        if (lc != null) {
            Point lpt = convertPoint(e.getPoint(), lc);
            MouseWheelListener[] mlisteners = lc.getMouseWheelListeners();
            if (mlisteners != null) {
                for (int i = 0; i < mlisteners.length; i++) {
                    mlisteners[i].mouseWheelMoved(cloneMouseWheelEvent(e, lc, lpt));
                }
            }
            configureCursor(lc);
        }
    }

    protected Point convertPoint(Point point, Component dest) {
        return eventsTarget.convertPoint(point, dest);
    }

    public void setContent(Component aComp) {
        eventsTarget.removeAll();
        eventsTarget.add(aComp, BorderLayout.CENTER);
        checkComponents();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_MULTIPLY) {
            fit();
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        Component lc = findComponentAt(eventsTarget, dtde.getLocation());
        oldInnerDropTarget = innerDropTargetComponent;
        innerDropTargetComponent = lc;
        // Save selection on enter
        if (innerDropTargetComponent != null) {
            if (innerDropTargetComponent instanceof JList) {
                JList list = (JList) innerDropTargetComponent;
                enterSelectedIndex = list.getSelectedIndex();
            }
            if (innerDropTargetComponent instanceof JTree) {
                JTree tree = (JTree) innerDropTargetComponent;
                enterSelectedPath = tree.getSelectionPath();
            }
            if (innerDropTargetComponent instanceof JTable) {
                JTable jTable = (JTable) innerDropTargetComponent;
            }
        }
//        if(lc != null)
//        {
//            if(lc.getDropTarget() != null)
//            {
//                Point lpt = convertPoint(dtde.getLocation(), lc);
//                lc.getDropTarget().dragEnter(new DropTargetDragEvent(dtde.getDropTargetContext(), lpt, dtde.getDropAction(), dtde.getSourceActions()));
//            }
//        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        cancelAutoScrolling();
        Point lnewPoint = dtde.getLocation();

        Component lc = findComponentAt(eventsTarget, dtde.getLocation());
        oldInnerDropTarget = innerDropTargetComponent;
        innerDropTargetComponent = lc;
        // Restore selection on enter
        if (innerDropTargetComponent != oldInnerDropTarget && oldInnerDropTarget != null) {
            if (oldInnerDropTarget instanceof JList) {
                JList list = (JList) oldInnerDropTarget;
                if (enterSelectedIndex == -1) {
                    list.clearSelection();
                } else {
                    list.setSelectedIndex(enterSelectedIndex);
                }
            }
            if (oldInnerDropTarget instanceof JTree) {
                JTree tree = (JTree) oldInnerDropTarget;
                if (enterSelectedPath == null) {
                    tree.clearSelection();
                } else {
                    tree.setSelectionPath(enterSelectedPath);
                }
            }
            if (oldInnerDropTarget instanceof JTable) {
                JTable jTable = (JTable) oldInnerDropTarget;
            }
        }
        // Save selection on enter
        if (innerDropTargetComponent != oldInnerDropTarget && innerDropTargetComponent != null) {
            if (innerDropTargetComponent instanceof JList) {
                JList list = (JList) innerDropTargetComponent;
                enterSelectedIndex = list.getSelectedIndex();
            }
            if (innerDropTargetComponent instanceof JTree) {
                JTree tree = (JTree) innerDropTargetComponent;
                enterSelectedPath = tree.getSelectionPath();
            }
            if (innerDropTargetComponent instanceof JTable) {
                JTable jTable = (JTable) innerDropTargetComponent;
            }
        }
        // Proceed...
        innerDropTargetPoint = convertPoint(dtde.getLocation(), innerDropTargetComponent);
        if (innerDropTargetComponent != null && innerDropTargetComponent != dragTarget) {
            if (innerDropTargetComponent instanceof JList) {
                JList list = (JList) innerDropTargetComponent;
                ListUI lui = list.getUI();
                int cellIndex2Select = lui.locationToIndex(list, innerDropTargetPoint);
                if (cellIndex2Select > -1) {
                    list.setSelectedIndex(cellIndex2Select);
                }
            }
            if (innerDropTargetComponent instanceof JTree) {
                JTree tree = (JTree) innerDropTargetComponent;
                TreeUI tui = tree.getUI();
                TreePath Path2Select = tui.getClosestPathForLocation(tree, innerDropTargetPoint.x, innerDropTargetPoint.y);
                if (Path2Select != null) {
                    tree.setSelectionPath(Path2Select);
                }
            }
            if (innerDropTargetComponent instanceof JTable) {
                JTable jTable = (JTable) innerDropTargetComponent;
                TableUI tui = jTable.getUI();
            }
        }

        JScrollPane llc = findScrollPaneAt(eventsTarget, lnewPoint);

        /*
        String log1 = "";
        if(dragTarget != null)
        log1 = "dragTarget != null; ";
        else
        log1 = "dragTarget == null; ";
        String log2 = "";
        if(llc != null)
        log2 = "llc != null";
        else
        log2 = "llc == null; ";
        log1 += log2;
        System.out.println(log1);
         */
        // schedule autoscrolling task if needed
        if (dragTarget != null && dragTarget != llc && llc instanceof JScrollPane) {
            Point lMaybeInnerInsetPt = null;
            if (isAncestorOf(llc)) {
                lMaybeInnerInsetPt = convertPoint(lnewPoint, llc);
            } else {
                lMaybeInnerInsetPt = SwingUtilities.convertPoint(this, lnewPoint, llc);
            }
            if (llc.getViewport() != null && llc.getViewport().getView() != null) {
                Component lviewcomp = llc.getViewport().getView();
                if (lviewcomp instanceof JComponent) {
                    JComponent lview = (JComponent) lviewcomp;
                    if (lview.getAutoscrolls()) {
                        if (isInnerInsetPt(llc, lMaybeInnerInsetPt)
                                && !(dragTarget instanceof JScrollBar)) {
                            autoscrollingPane = llc;
                            autoscrollingDirection = getInnerInsetPt(llc, lMaybeInnerInsetPt);
                            autoscrollingTask.setEvent(null);
                            autoScrollingStep(autoscrollingTask);
                            autoscrollingTimer.start();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        oldInnerDropTarget = null;
        innerDropTargetComponent = null;

        //dragTarget = null; - need to stay old value - to save autoscrolling fuctionality
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        oldInnerDropTarget = null;
        innerDropTargetComponent = null;
        dragTarget = null;
    }

    public Component getInnerDropTargetComponent() {
        return innerDropTargetComponent;
    }

    public Point getInnerDropTargetPoint() {
        return innerDropTargetPoint;
    }
}
