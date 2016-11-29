/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components;

import com.eas.client.forms.Forms;
import com.eas.client.forms.HasComponentEvents;
import com.eas.client.forms.HasJsName;
import com.eas.client.forms.HasOnValueChange;
import com.eas.client.forms.Orientation;
import com.eas.client.forms.Widget;
import com.eas.client.forms.events.ActionEvent;
import com.eas.client.forms.events.ComponentEvent;
import com.eas.client.forms.events.MouseEvent;
import com.eas.client.forms.components.rt.VSlider;
import com.eas.client.forms.events.ValueChangeEvent;
import com.eas.client.forms.events.rt.ControlEventsIProxy;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.design.Undesignable;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.EventMethod;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Slider extends VSlider implements HasOnValueChange, HasPublished, HasComponentEvents, HasJsName, Widget {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * Slider component.\n"
            + " * @param min the minimum value (optional)\n"
            + " * @param max the maximum value (optional)\n"
            + " * @param value the initial value (optional)\n"
            + " */";
    
    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"min", "max", "value"})
    public Slider(int min, int max, int value) {
        super(Orientation.HORIZONTAL, min, max, value);
        setOpaque(false);
    }

    public Slider() {
        this(0, 100, 0);
    }
/*
    public Slider(int aOrientation) {
        this(aOrientation, 0, 0, 0);
    }
*/
    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Slider's value. Can't be null.\n"
            + " */")
    @Undesignable
    @Override
    public int getValue() {
        return super.getValue();
    }

    @ScriptFunction
    @Override
    public void setValue(int aValue) {
        super.setValue(aValue);
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

    @Override
    public String toString() {
        return String.format("%s [%s]", super.getName() != null ? super.getName() : "", getClass().getSimpleName());
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

//    @ScriptFunction(jsDoc = ""
//            + "/**\n"
//            + " * This slider's vertical or horizontal orientation: Orientation.VERTICAL or Orientation.HORIZONTAL\n"
//            + " */")
    /*
    @Override
    public int getOrientation() {
        if (super.getOrientation() == JSlider.HORIZONTAL) {
            return Orientation.HORIZONTAL;
        } else if (super.getOrientation() == JSlider.VERTICAL) {
            return Orientation.VERTICAL;
        } else {
            return Orientation.HORIZONTAL;
        }
    }

    @ScriptFunction
    @Override
    public void setOrientation(int aOrientation) {
        super.setOrientation(orientation);
    }
*/
    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The minimum value supported by the slider.\n"
            + " */")
    @Override
    public int getMinimum() {
        return super.getMinimum();
    }

    @ScriptFunction
    @Override
    public void setMinimum(int aValue) {
        super.setMinimum(aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The maximum value supported by the slider.\n"
            + " */")
    @Override
    public int getMaximum() {
        return super.getMaximum();
    }

    @ScriptFunction
    @Override
    public void setMaximum(int aValue) {
        super.setMaximum(aValue);
    }

    @ScriptFunction
    public String getText() throws Exception {
        int value = super.getValue();
        return String.valueOf(value);
    }

    @ScriptFunction
    public void setText(String aValue) throws Exception {
        setValue(Integer.valueOf(aValue));
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
    
    @ScriptFunction(jsDoc = ON_VALUE_CHANED_JSDOC)
    @EventMethod(eventClass = ValueChangeEvent.class)
    @Undesignable
    @Override
    public JSObject getOnValueChange() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.valueChanged);
    }

    @ScriptFunction
    @Override
    public void setOnValueChange(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.valueChanged, aValue);
    }

    // published parent
    @ScriptFunction(name = "parent", jsDoc = PARENT_JSDOC)
    @Override
    public Widget getParentWidget() {
        return Forms.lookupPublishedParent(this);
    }
}
