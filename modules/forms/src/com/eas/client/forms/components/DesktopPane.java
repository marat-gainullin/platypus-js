package com.eas.client.forms.components;

import com.eas.client.forms.Form;
import com.eas.client.forms.Forms;
import com.eas.client.forms.PlatypusInternalFrame;
import com.eas.client.forms.HasComponentEvents;
import com.eas.client.forms.HasJsName;
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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class DesktopPane extends JDesktopPane implements HasPublished, HasComponentEvents, HasJsName, Widget {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * Desktop pane panel component.\n"
            + " * This component can be used for creating a multi-document GUI or a virtual desktop.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public DesktopPane() {
        super();
        setBackground(Color.GRAY.brighter());
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

    private static final String MINIMIZE_ALL_JSDOC = ""
            + "/**\n"
            + "* Minimizes all frames on the pane.\n"
            + "*/";

    @ScriptFunction(jsDoc = MINIMIZE_ALL_JSDOC)
    public void minimizeAll() throws PropertyVetoException {
        for (JInternalFrame f : getAllFrames()) {
            if (f.isIconifiable() && !f.isIcon()) {
                f.setIcon(true);
            }
        }
    }

    private static final String RESTORE_ALL_JSDOC = ""
            + "/**\n"
            + "* Restores frames original state and location.\n"
            + "*/";

    @ScriptFunction(jsDoc = RESTORE_ALL_JSDOC)
    public void restoreAll() throws PropertyVetoException {
        for (JInternalFrame f : getAllFrames()) {
            if (f.isIcon()) {
                f.setIcon(false);
            }
            if (f.isMaximum()) {
                f.setMaximum(false);
            }
        }
    }

    private static final String MAXIMIZE_ALL_JSDOC = ""
            + "/**\n"
            + "* Maximizes all frames on the pane.\n"
            + "*/";

    @ScriptFunction(jsDoc = MAXIMIZE_ALL_JSDOC)
    public void maximizeAll() throws PropertyVetoException {
        for (JInternalFrame f : getAllFrames()) {
            if (f.isIcon()) {
                f.setIcon(false);
            }
            if (f.isMaximizable()) {
                f.setMaximum(true);
            }
        }
    }

    private static final String CLOSE_ALL_JSDOC = ""
            + "/**\n"
            + "* Closes all frames on the pane.\n"
            + "*/";

    @ScriptFunction(jsDoc = CLOSE_ALL_JSDOC)
    public void closeAll() {
        for (JInternalFrame f : getAllFrames()) {
            getDesktopManager().closeFrame(f);
        }
    }

    private static final String FORMS_JSDOC = ""
            + "/**\n"
            + "* An array of all frames on the pane.\n"
            + "*/";

    @ScriptFunction(jsDoc = FORMS_JSDOC)
    @Undesignable
    public Form[] getForms() {
        List<Form> forms = new ArrayList<>();
        for (JInternalFrame f : getAllFrames()) {
            if (f instanceof PlatypusInternalFrame) {
                PlatypusInternalFrame pif = (PlatypusInternalFrame) f;
                if (pif.getOwner() != null) {
                    forms.add(pif.getOwner());
                }
            }
        }
        return forms.toArray(new Form[]{});
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
    
    // published parent
    @ScriptFunction(name = "parent", jsDoc = PARENT_JSDOC)
    @Override
    public Widget getParentWidget() {
        return Forms.lookupPublishedParent(this);
    }
}
