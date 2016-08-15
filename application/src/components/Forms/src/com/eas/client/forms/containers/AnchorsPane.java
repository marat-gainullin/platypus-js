/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.containers;

import com.eas.client.forms.Anchors;
import com.eas.client.forms.Forms;
import com.eas.client.forms.HasChildren;
import com.eas.client.forms.HasContainerEvents;
import com.eas.client.forms.HasJsName;
import com.eas.client.forms.Ordering;
import com.eas.client.forms.Widget;
import com.eas.client.forms.events.ActionEvent;
import com.eas.client.forms.events.ComponentEvent;
import com.eas.client.forms.events.MouseEvent;
import com.eas.client.forms.events.rt.ControlEventsIProxy;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.client.forms.layouts.Margin;
import com.eas.client.forms.layouts.MarginConstraints;
import com.eas.design.Undesignable;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.EventMethod;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class AnchorsPane extends JPanel implements HasPublished, HasContainerEvents, HasChildren, HasJsName, Widget {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A container with Anchors Layout.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public AnchorsPane() {
        super(new MarginLayout());
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

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + " * Appends the specified component to the container with specified placement.\n"
            + " * @param component the component to add.\n"
            + " * @param anchors the anchors object for the component, can contain the following properties: left, width, right, top, height, bottom.\n"
            + " */";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component", "anchors"})
    public void add(JComponent aComp, Object oAnchors) {
        if (aComp != null) {
            MarginConstraints c;
            if (oAnchors != null) {
                if (oAnchors instanceof MarginConstraints) {
                    c = (MarginConstraints) oAnchors;
                } else {
                    c = oAnchors instanceof Anchors ? anchors2MarginConstraints((Anchors) oAnchors) : scriptable2MarginConstraints((JSObject) oAnchors);
                }
            } else {
                Point location = aComp.getLocation();
                Dimension size = aComp.getSize();
                c = new MarginConstraints(new Margin(location.x, true), new Margin(location.y, true), null, null, new Margin(size.width, true), new Margin(size.height, true));
            }
            super.add(aComp, c);
            super.revalidate();
            super.repaint();
        }
    }

    private boolean paintingChildren;

    @Override
    public Component getComponent(int n) {
        if (paintingChildren) {
            int count = super.getComponentCount() - 1;
            return super.getComponent(count - n);
        } else {
            return super.getComponent(n);
        }
    }

    @Override
    protected void paintChildren(Graphics g) {
        paintingChildren = true;
        try {
            super.paintChildren(g);
        } finally {
            paintingChildren = false;
        }
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

    protected MarginConstraints scriptable2MarginConstraints(JSObject aAnchors) {
        Object oLeft = aAnchors.hasMember("left") ? aAnchors.getMember("left") : null;
        Object oWidth = aAnchors.hasMember("width") ? aAnchors.getMember("width") : null;
        Object oTop = aAnchors.hasMember("top") ? aAnchors.getMember("top") : null;
        Object oHeight = aAnchors.hasMember("height") ? aAnchors.getMember("height") : null;
        Object oRight = aAnchors.hasMember("right") ? aAnchors.getMember("right") : null;
        Object oBottom = aAnchors.hasMember("bottom") ? aAnchors.getMember("bottom") : null;
        Margin left = Margin.parse(oLeft != null ? JSType.toString(oLeft) : null);
        Margin width = Margin.parse(oWidth != null ? JSType.toString(oWidth) : null);
        Margin right = Margin.parse(oRight != null ? JSType.toString(oRight) : null);
        Margin top = Margin.parse(oTop != null ? JSType.toString(oTop) : null);
        Margin height = Margin.parse(oHeight != null ? JSType.toString(oHeight) : null);
        Margin bottom = Margin.parse(oBottom != null ? JSType.toString(oBottom) : null);
        return new MarginConstraints(left, top, right, bottom, width, height);
    }

    protected MarginConstraints anchors2MarginConstraints(Anchors aAnchors) {
        Object oLeft = aAnchors.getLeft();
        Object oWidth = aAnchors.getWidth();
        Object oTop = aAnchors.getTop();
        Object oHeight = aAnchors.getHeight();
        Object oRight = aAnchors.getRight();
        Object oBottom = aAnchors.getBottom();
        Margin left = Margin.parse(oLeft != null ? JSType.toString(oLeft) : null);
        Margin width = Margin.parse(oWidth != null ? JSType.toString(oWidth) : null);
        Margin right = Margin.parse(oRight != null ? JSType.toString(oRight) : null);
        Margin top = Margin.parse(oTop != null ? JSType.toString(oTop) : null);
        Margin height = Margin.parse(oHeight != null ? JSType.toString(oHeight) : null);
        Margin bottom = Margin.parse(oBottom != null ? JSType.toString(oBottom) : null);
        return new MarginConstraints(left, top, right, bottom, width, height);
    }

    public void toFront(JComponent aComp) {
        Ordering.toFront(this, aComp);
    }

    public void toBack(JComponent aComp) {
        Ordering.toBack(this, aComp);
    }

    private static final String TO_FRONT_JSDOC = ""
            + "/**\n"
            + "* Brings the specified component to front on this panel.\n"
            + "* @param component the component.\n"
            + "* @param count steps to move the component (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = TO_FRONT_JSDOC, params = {"component", "count"})
    public void toFront(JComponent aComp, int aCount) {
        Ordering.toFront(this, aComp, aCount);
    }

    private static final String TO_BACK_JSDOC = ""
            + "/**\n"
            + "* Brings the specified component to back on this panel.\n"
            + "* @param component the component.\n"
            + "* @param count steps to move the component (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = TO_BACK_JSDOC)
    public void toBack(JComponent aComp, int aCount) {
        Ordering.toBack(this, aComp, aCount);
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
    @Undesignable
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
    @Undesignable
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
