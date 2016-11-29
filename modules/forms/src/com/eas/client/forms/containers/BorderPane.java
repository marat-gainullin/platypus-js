/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.containers;

import com.eas.client.forms.Forms;
import com.eas.client.forms.HasChildren;
import com.eas.client.forms.HasContainerEvents;
import com.eas.client.forms.HasJsName;
import com.eas.client.forms.HorizontalPosition;
import com.eas.client.forms.VerticalPosition;
import com.eas.client.forms.Widget;
import com.eas.client.forms.events.ActionEvent;
import com.eas.client.forms.events.ComponentEvent;
import com.eas.client.forms.events.MouseEvent;
import com.eas.client.forms.events.rt.ControlEventsIProxy;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.design.Undesignable;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.EventMethod;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class BorderPane extends JPanel implements HasPublished, HasContainerEvents, HasChildren, HasJsName, Widget {

    public BorderPane() {
        this(0, 0);
    }

    public BorderPane(int hgap) {
        this(hgap, 0);
    }
    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A container with Border Layout.\n"
            + " * @param hgap the horizontal gap (optional).\n"
            + " * @param vgap the vertical gap (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"hgap", "vgap"})
    public BorderPane(int hgap, int vgap) {
        super(new BorderLayout(hgap, vgap));
    }

    @ScriptFunction(jsDoc = JS_NAME_DOC)
    @Override
    public String getName() {
        return super.getName();
    }

    @ScriptFunction
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @ScriptFunction(jsDoc = GET_NEXT_FOCUSABLE_COMPONENT_JSDOC)
    @Override
    public JComponent getNextFocusableComponent() {
        return (JComponent) super.getNextFocusableComponent();
    }

    @ScriptFunction
    @Override
    public void setNextFocusableComponent(JComponent aValue) {
        super.setNextFocusableComponent(aValue);
    }

    protected String errorMessage;

    @ScriptFunction(jsDoc = ERROR_JSDOC)
    @Override
    public String getError() {
        return errorMessage;
    }

    @ScriptFunction
    @Override
    public void setError(String aValue) {
        errorMessage = aValue;
    }

    @ScriptFunction(jsDoc = BACKGROUND_JSDOC)
    @Override
    public Color getBackground() {
        return super.getBackground();
    }

    @ScriptFunction
    @Override
    public void setBackground(Color aValue) {
        super.setBackground(aValue);
    }

    @ScriptFunction(jsDoc = FOREGROUND_JSDOC)
    @Override
    public Color getForeground() {
        return super.getForeground();
    }

    @ScriptFunction
    @Override
    public void setForeground(Color aValue) {
        super.setForeground(aValue);
    }

    @ScriptFunction(jsDoc = VISIBLE_JSDOC)
    @Override
    public boolean getVisible() {
        return super.isVisible();
    }

    @ScriptFunction
    @Override
    public void setVisible(boolean aValue) {
        super.setVisible(aValue);
    }

    @ScriptFunction(jsDoc = FOCUSABLE_JSDOC)
    @Override
    public boolean getFocusable() {
        return super.isFocusable();
    }

    @ScriptFunction
    @Override
    public void setFocusable(boolean aValue) {
        super.setFocusable(aValue);
    }

    @ScriptFunction(jsDoc = ENABLED_JSDOC)
    @Override
    public boolean getEnabled() {
        return super.isEnabled();
    }

    @ScriptFunction
    @Override
    public void setEnabled(boolean aValue) {
        super.setEnabled(aValue);
    }

    @ScriptFunction(jsDoc = TOOLTIP_TEXT_JSDOC)
    @Override
    public String getToolTipText() {
        return super.getToolTipText();
    }

    @ScriptFunction
    @Override
    public void setToolTipText(String aValue) {
        super.setToolTipText(aValue);
    }

    @ScriptFunction(jsDoc = OPAQUE_TEXT_JSDOC)
    @Override
    public boolean getOpaque() {
        return super.isOpaque();
    }

    @ScriptFunction
    @Override
    public void setOpaque(boolean aValue) {
        super.setOpaque(aValue);
    }

    @ScriptFunction(jsDoc = COMPONENT_POPUP_MENU_JSDOC)
    @Override
    public JPopupMenu getComponentPopupMenu() {
        return super.getComponentPopupMenu();
    }

    @ScriptFunction
    @Override
    public void setComponentPopupMenu(JPopupMenu aMenu) {
        super.setComponentPopupMenu(aMenu);
    }

    @ScriptFunction(jsDoc = FONT_JSDOC)
    @Override
    public Font getFont() {
        return super.getFont();
    }

    @ScriptFunction
    @Override
    public void setFont(Font aFont) {
        super.setFont(aFont);
    }

    @ScriptFunction(jsDoc = CURSOR_JSDOC)
    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }

    @ScriptFunction
    @Override
    public void setCursor(Cursor aCursor) {
        super.setCursor(aCursor);
    }

    @ScriptFunction(jsDoc = LEFT_JSDOC)
    @Override
    public int getLeft() {
        return super.getLocation().x;
    }

    @ScriptFunction
    @Override
    public void setLeft(int aValue) {
        if (super.getParent() != null && super.getParent().getLayout() instanceof MarginLayout) {
            MarginLayout.ajustLeft(this, aValue);
        }
        super.setLocation(aValue, getTop());
    }

    @ScriptFunction(jsDoc = TOP_JSDOC)
    @Override
    public int getTop() {
        return super.getLocation().y;
    }

    @ScriptFunction
    @Override
    public void setTop(int aValue) {
        if (super.getParent() != null && super.getParent().getLayout() instanceof MarginLayout) {
            MarginLayout.ajustTop(this, aValue);
        }
        super.setLocation(getLeft(), aValue);
    }

    @ScriptFunction(jsDoc = WIDTH_JSDOC)
    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @ScriptFunction
    @Override
    public void setWidth(int aValue) {
        Widget.setWidth(this, aValue);
    }

    @ScriptFunction(jsDoc = HEIGHT_JSDOC)
    @Override
    public int getHeight() {
        return super.getHeight();
    }

    @ScriptFunction
    @Override
    public void setHeight(int aValue) {
        Widget.setHeight(this, aValue);
    }

    @ScriptFunction(jsDoc = FOCUS_JSDOC)
    @Override
    public void focus() {
        super.requestFocus();
    }

    // Native API
    @ScriptFunction(jsDoc = NATIVE_COMPONENT_JSDOC)
    @Undesignable
    @Override
    public JComponent getComponent() {
        return this;
    }

    @ScriptFunction(jsDoc = NATIVE_ELEMENT_JSDOC)
    @Undesignable
    @Override
    public Object getElement() {
        return null;
    }

    private static final String HGAP_JSDOC = ""
            + "/**\n"
            + "* Horizontal gap between center and border components.\n"
            + "*/";

    @ScriptFunction(jsDoc = HGAP_JSDOC)
    public int getHgap() {
        return ((BorderLayout) super.getLayout()).getHgap();
    }

    @ScriptFunction
    public void setHgap(int aValue) {
        ((BorderLayout) super.getLayout()).setHgap(aValue);
        super.revalidate();
        super.repaint();
    }

    private static final String VGAP_JSDOC = ""
            + "/**\n"
            + "* Vertical gap between center and border components.\n"
            + "*/";

    @ScriptFunction(jsDoc = VGAP_JSDOC)
    public int getVgap() {
        return ((BorderLayout) super.getLayout()).getVgap();
    }

    @ScriptFunction
    public void setVgap(int aValue) {
        ((BorderLayout) super.getLayout()).setVgap(aValue);
        super.revalidate();
        super.repaint();
    }
    
    public void add(JComponent aComp, int aPlace) {
        if (aComp != null) {
            checkCenterComponent();
            String place;
            switch (aPlace) {
                case HorizontalPosition.LEFT:
                    place = BorderLayout.WEST;
                    break;
                case HorizontalPosition.CENTER:
                    place = BorderLayout.CENTER;
                    break;
                case HorizontalPosition.RIGHT:
                    place = BorderLayout.EAST;
                    break;
                case VerticalPosition.TOP:
                    place = BorderLayout.NORTH;
                    break;
                case VerticalPosition.BOTTOM:
                    place = BorderLayout.SOUTH;
                    break;
                default:
                    place = BorderLayout.CENTER;
                    break;
            }
            super.add(aComp, place);
            super.revalidate();
            super.repaint();
        }
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + " * Appends the specified component to this container on the specified placement.\n"
            + " * @param component the component to add.\n"
            + " * @param place the placement in the container: <code>HorizontalPosition.LEFT</code>, <code>HorizontalPosition.CENTER</code>, <code>HorizontalPosition.RIGHT</code>, <code>VerticalPosition.TOP</code> or <code>VerticalPosition.BOTTOM</code> (optional).\n"
            + " * @param size the size of the component by the provided place direction (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component", "place", "size"})
    public void add(JComponent aComp, Integer aPlace, Integer aSize) {
        aPlace = aPlace != null ? aPlace : HorizontalPosition.CENTER;
        if (aPlace != HorizontalPosition.CENTER && aSize != null) {
            Dimension prefSize = aComp.getPreferredSize();
            if (aPlace == HorizontalPosition.LEFT || aPlace == HorizontalPosition.RIGHT) {
                aComp.setPreferredSize(new Dimension(aSize, prefSize.height));
            } else {
                aComp.setPreferredSize(new Dimension(prefSize.width, aSize));
            }
        }
        add(aComp, aPlace.intValue());
    }

    public void add(JComponent aComp) {
        add(aComp, HorizontalPosition.CENTER);
    }

    @ScriptFunction(jsDoc = CHILD_JSDOC, params = "index")
    @Override
    public JComponent child(int aIndex) {
        return (JComponent) super.getComponent(aIndex);
    }

    @ScriptFunction(jsDoc = CHILDREN_JSDOC)
    @Undesignable
    @Override
    public JComponent[] children() {
        List<JComponent> ch = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            ch.add(child(i));
        }
        return ch.toArray(new JComponent[]{});
    }

    @ScriptFunction(jsDoc = REMOVE_JSDOC, params = {"component"})
    @Override
    public void remove(JComponent aComp) {
        super.remove(aComp);
        super.revalidate();
        super.repaint();
    }

    @ScriptFunction(jsDoc = CLEAR_JSDOC)
    @Override
    public void clear() {
        super.removeAll();
        super.revalidate();
        super.repaint();
    }

    @ScriptFunction(jsDoc = COUNT_JSDOC)
    @Undesignable
    @Override
    public int getCount() {
        return super.getComponentCount();
    }

    private static final String LEFT_COMPONENT_JSDOC = ""
            + "/**\n"
            + "* The component added using HorizontalPosition.LEFT constraint.\n"
            + "* If no component at this constraint then set to <code>null</code>.\n"
            + "*/";

    @ScriptFunction(jsDoc = LEFT_COMPONENT_JSDOC)
    @Undesignable
    public JComponent getLeftComponent() {
        BorderLayout layout = (BorderLayout) super.getLayout();
        java.awt.Component target = layout.getLayoutComponent(BorderLayout.WEST);
        if (target == null) {
            target = layout.getLayoutComponent(BorderLayout.LINE_START);
        }
        return (JComponent) target;
    }

    @ScriptFunction
    public void setLeftComponent(JComponent aComp) {
        checkCenterComponent();
        BorderLayout layout = (BorderLayout) super.getLayout();
        java.awt.Component oldComp = layout.getLayoutComponent(BorderLayout.WEST);
        if (oldComp == null) {
            oldComp = layout.getLayoutComponent(BorderLayout.LINE_START);
        }
        if (oldComp != null) {
            super.remove(oldComp);
            super.revalidate();
            super.repaint();
        }
        add(aComp, HorizontalPosition.LEFT);
    }

    private static final String TOP_COMPONENT_JSDOC = ""
            + "/**\n"
            + "* The component added using HorizontalPosition.TOP constraint.\n"
            + "* If no component at the container on this constraint then set to <code>null</code>.\n"
            + "*/";

    @ScriptFunction(jsDoc = TOP_COMPONENT_JSDOC)
    @Undesignable
    public JComponent getTopComponent() {
        BorderLayout layout = (BorderLayout) super.getLayout();
        java.awt.Component target = layout.getLayoutComponent(BorderLayout.NORTH);
        if (target == null) {
            target = layout.getLayoutComponent(BorderLayout.PAGE_START);
        }
        return (JComponent) target;
    }

    public void setTopComponent(JComponent aComp) {
        checkCenterComponent();
        BorderLayout layout = (BorderLayout) super.getLayout();
        java.awt.Component oldComp = layout.getLayoutComponent(BorderLayout.NORTH);
        if (oldComp == null) {
            oldComp = layout.getLayoutComponent(BorderLayout.PAGE_START);
        }
        if (oldComp != null) {
            super.remove(oldComp);
            super.revalidate();
            super.repaint();
        }
        add(aComp, VerticalPosition.TOP);
    }

    private static final String RIGHT_COMPONENT_JSDOC = ""
            + "/**\n"
            + "* The component added using HorizontalPosition.RIGHT constraint.\n"
            + "* If no component at the container on this constraint then set to <code>null</code>.\n"
            + "*/";

    @ScriptFunction(jsDoc = RIGHT_COMPONENT_JSDOC)
    @Undesignable
    public JComponent getRightComponent() {
        BorderLayout layout = (BorderLayout) super.getLayout();
        java.awt.Component target = layout.getLayoutComponent(BorderLayout.EAST);
        if (target == null) {
            target = layout.getLayoutComponent(BorderLayout.LINE_END);
        }
        return (JComponent) target;
    }

    @ScriptFunction
    public void setRightComponent(JComponent aComp) {
        checkCenterComponent();
        BorderLayout layout = (BorderLayout) super.getLayout();
        java.awt.Component oldComp = layout.getLayoutComponent(BorderLayout.EAST);
        if (oldComp == null) {
            oldComp = layout.getLayoutComponent(BorderLayout.LINE_END);
        }
        if (oldComp != null) {
            super.remove(oldComp);
            super.revalidate();
            super.repaint();
        }
        add(aComp, HorizontalPosition.RIGHT);
    }

    private static final String BOTTOM_COMPONENT_JSDOC = ""
            + "/**\n"
            + "* The component added using HorizontalPosition.BOTTOM constraint.\n"
            + "* If no component at the container on this constraint then set to <code>null</code>.\n"
            + "*/";

    @ScriptFunction(jsDoc = BOTTOM_COMPONENT_JSDOC)
    @Undesignable
    public JComponent getBottomComponent() {
        BorderLayout layout = (BorderLayout) super.getLayout();
        java.awt.Component target = layout.getLayoutComponent(BorderLayout.SOUTH);
        if (target == null) {
            target = layout.getLayoutComponent(BorderLayout.PAGE_END);
        }
        return (JComponent) target;
    }

    @ScriptFunction
    public void setBottomComponent(JComponent aComp) {
        checkCenterComponent();
        BorderLayout layout = (BorderLayout) super.getLayout();
        java.awt.Component oldComp = layout.getLayoutComponent(BorderLayout.SOUTH);
        if (oldComp == null) {
            oldComp = layout.getLayoutComponent(BorderLayout.PAGE_END);
        }
        if (oldComp != null) {
            super.remove(oldComp);
            super.revalidate();
            super.repaint();
        }
        add(aComp, VerticalPosition.BOTTOM);
    }

    private void checkCenterComponent() throws IllegalStateException {
        if (getCenterComponent() != null) {
            throw new IllegalStateException("No widget can be added after center widget");
        }
    }

    private static final String CENTER_COMPONENT_JSDOC = ""
            + "/**\n"
            + "* The component added using HorizontalPosition.CENTER constraint.\n"
            + "* If no component at the container on this constraint then set to <code>null</code>.\n"
            + "*/";

    @ScriptFunction(jsDoc = CENTER_COMPONENT_JSDOC)
    @Undesignable
    public JComponent getCenterComponent() {
        BorderLayout layout = (BorderLayout) super.getLayout();
        return (JComponent) layout.getLayoutComponent(BorderLayout.CENTER);
    }

    @ScriptFunction
    public void setCenterComponent(JComponent aComp) {
        checkCenterComponent();
        BorderLayout layout = (BorderLayout) super.getLayout();
        java.awt.Component oldComp = layout.getLayoutComponent(BorderLayout.CENTER);
        if (oldComp != null) {
            super.remove(oldComp);
            super.revalidate();
            super.repaint();
        }
        add(aComp, VerticalPosition.CENTER);
    }

    protected JSObject published;

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    protected ControlEventsIProxy eventsProxy = new ControlEventsIProxy(this);

    @ScriptFunction(jsDoc = ON_MOUSE_CLICKED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseClicked() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseClicked);
    }

    @ScriptFunction
    @Override
    public void setOnMouseClicked(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseClicked, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_DRAGGED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseDragged() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseDragged);
    }

    @ScriptFunction
    @Override
    public void setOnMouseDragged(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseDragged, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_ENTERED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseEntered() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseEntered);
    }

    @ScriptFunction
    @Override
    public void setOnMouseEntered(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseEntered, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_EXITED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseExited() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseExited);
    }

    @ScriptFunction
    @Override
    public void setOnMouseExited(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseExited, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_MOVED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseMoved() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseMoved);
    }

    @ScriptFunction
    @Override
    public void setOnMouseMoved(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseMoved, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_PRESSED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMousePressed() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mousePressed);
    }

    @ScriptFunction
    @Override
    public void setOnMousePressed(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mousePressed, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_RELEASED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseReleased() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseReleased);
    }

    @ScriptFunction
    @Override
    public void setOnMouseReleased(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseReleased, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_WHEEL_MOVED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseWheelMoved() {
        return eventsProxy.getOnMouseWheelMoved();
    }

    @ScriptFunction
    @Override
    public void setOnMouseWheelMoved(JSObject aValue) {
        eventsProxy.setOnMouseWheelMoved(aValue);
    }

    @ScriptFunction(jsDoc = ON_ACTION_PERFORMED_JSDOC)
    @EventMethod(eventClass = ActionEvent.class)
    @Undesignable
    @Override
    public JSObject getOnActionPerformed() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.actionPerformed);
    }

    @ScriptFunction
    @Override
    public void setOnActionPerformed(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.actionPerformed, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_HIDDEN_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentHidden() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentHidden);
    }

    @ScriptFunction
    @Override
    public void setOnComponentHidden(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentHidden, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_MOVED_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentMoved() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentMoved);
    }

    @ScriptFunction
    @Override
    public void setOnComponentMoved(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentMoved, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_RESIZED_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentResized() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentResized);
    }

    @ScriptFunction
    @Override
    public void setOnComponentResized(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentResized, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_SHOWN_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentShown() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentShown);
    }

    @ScriptFunction
    @Override
    public void setOnComponentShown(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentShown, aValue);
    }

    @ScriptFunction(jsDoc = ON_FOCUS_GAINED_JSDOC)
    @EventMethod(eventClass = FocusEvent.class)
    @Undesignable
    @Override
    public JSObject getOnFocusGained() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.focusGained);
    }

    @ScriptFunction
    @Override
    public void setOnFocusGained(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.focusGained, aValue);
    }

    @ScriptFunction(jsDoc = ON_FOCUS_LOST_JSDOC)
    @EventMethod(eventClass = FocusEvent.class)
    @Undesignable
    @Override
    public JSObject getOnFocusLost() {
        return eventsProxy != null ? eventsProxy.getHandlers().get(ControlEventsIProxy.focusLost) : null;
    }

    @ScriptFunction
    @Override
    public void setOnFocusLost(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.focusLost, aValue);
    }

    @ScriptFunction(jsDoc = ON_KEY_PRESSED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    @Undesignable
    @Override
    public JSObject getOnKeyPressed() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.keyPressed);
    }

    @ScriptFunction
    @Override
    public void setOnKeyPressed(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.keyPressed, aValue);
    }

    @ScriptFunction(jsDoc = ON_KEY_RELEASED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    @Undesignable
    @Override
    public JSObject getOnKeyReleased() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.keyReleased);
    }

    @ScriptFunction
    @Override
    public void setOnKeyReleased(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.keyReleased, aValue);
    }

    @ScriptFunction(jsDoc = ON_KEY_TYPED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    @Undesignable
    @Override
    public JSObject getOnKeyTyped() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.keyTyped);
    }

    @ScriptFunction
    @Override
    public void setOnKeyTyped(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.keyTyped, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_ADDED_JSDOC)
    @EventMethod(eventClass = ContainerEvent.class)
    @Override
    public JSObject getOnComponentAdded() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentAdded);
    }

    @ScriptFunction
    @Override
    public void setOnComponentAdded(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentAdded, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_REMOVED_JSDOC)
    @EventMethod(eventClass = ContainerEvent.class)
    @Override
    public JSObject getOnComponentRemoved() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentRemoved);
    }

    @ScriptFunction
    @Override
    public void setOnComponentRemoved(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentRemoved, aValue);
    }
    
    // published parent
    @ScriptFunction(name = "parent", jsDoc = PARENT_JSDOC)
    @Override
    public Widget getParentWidget() {
        return Forms.lookupPublishedParent(this);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] count:%d", super.getName() != null ? super.getName() : "", getClass().getSimpleName(), getCount());
    }
}
