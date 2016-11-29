/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.containers;

import com.eas.client.forms.Forms;
import com.eas.client.forms.HasChildren;
import com.eas.client.forms.HasContainerEvents;
import com.eas.client.forms.HasJsName;
import com.eas.client.forms.Widget;
import com.eas.client.forms.events.ActionEvent;
import com.eas.client.forms.events.ItemEvent;
import com.eas.client.forms.events.ComponentEvent;
import com.eas.client.forms.events.MouseEvent;
import com.eas.client.forms.events.rt.ControlEventsIProxy;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.client.forms.layouts.CardLayout;
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
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class CardPane extends JPanel implements HasPublished, HasContainerEvents, HasChildren, HasJsName, Widget {

    protected JSObject onItemSelected;
    protected ItemListener cardsChangeListener = (java.awt.event.ItemEvent e) -> {
        try {
            Object oItem = e.getItem();
            JSObject jsItem = oItem instanceof HasPublished ? ((HasPublished)oItem).getPublished() : null;
            onItemSelected.call(getPublished(), new Object[]{new ItemEvent(CardPane.this, jsItem).getPublished()});
        } catch (Exception ex) {
            Logger.getLogger(CardPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    };

    public CardPane() {
        this(0, 0);
    }

    public CardPane(int hgap) {
        this(hgap, 0);
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A container with Card Layout. It treats each component in the container as a card. Only one card is visible at a time, and the container acts as a stack of cards.\n"
            + " * @param hgap the horizontal gap (optional).\n"
            + " * @param vgap the vertical gap (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"hgap", "vgap"})
    public CardPane(int hgap, int vgap) {
        super(new CardLayout(hgap, vgap));
        CardLayout layout = (CardLayout) super.getLayout();
        layout.addChangeListener(cardsChangeListener);
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

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Event that is fired when one of the components is selected in this card pane.\n"
            + " */")
    @EventMethod(eventClass = ItemEvent.class)
    public JSObject getOnItemSelected() {
        return onItemSelected;
    }

    @ScriptFunction
    public void setOnItemSelected(JSObject aValue) {
        if (onItemSelected != aValue) {
            onItemSelected = aValue;
        }
    }

    private static final String HGAP_JSDOC = ""
            + "/**\n"
            + "* Horizontal gap between card and container's edge.\n"
            + "*/";

    @ScriptFunction(jsDoc = HGAP_JSDOC)
    public int getHgap() {
        return ((CardLayout) super.getLayout()).getHgap();
    }

    @ScriptFunction
    public void setHgap(int aValue) {
        ((CardLayout) super.getLayout()).setHgap(aValue);
        super.revalidate();
        super.repaint();
    }

    private static final String VGAP_JSDOC = ""
            + "/**\n"
            + "* Vertical gap between card and container's edge.\n"
            + "*/";

    @ScriptFunction(jsDoc = VGAP_JSDOC)
    public int getVgap() {
        return ((CardLayout) super.getLayout()).getVgap();
    }

    @ScriptFunction
    public void setVgap(int aValue) {
        ((CardLayout) super.getLayout()).setVgap(aValue);
        super.revalidate();
        super.repaint();
    }
    
    private static final String ADD_JSDOC = ""
            + "/**\n"
            + " * Appends the component to this container with the specified name.\n"
            + " * @param component the component to add.\n"
            + " * @param cardName the name of the card.\n"
            + " */";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component", "cardName"})
    public void add(JComponent aComp, String aCardName) {
        if (aComp != null) {
            super.add(aComp, aCardName);
            super.revalidate();
            super.repaint();
        }
    }

    //@ScriptFunction(jsDoc = CHILD_JSDOC) child(String) is published and it is enough for both methods
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

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Gets child component, associated with the specified card.\n"
            + " * @param cardName Name of the card.\n"
            + " * @return the child component.\n"
            + " */", params = {"cardName"})
    public JComponent child(String aCardName) {
        CardLayout layout = (CardLayout) super.getLayout();
        return (JComponent) layout.getComponent(aCardName);
    }

    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + "* Flips to the component that was added to this layout with the specified name.\n"
            + "* @param name the card name\n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_JSDOC, params = {"name"})
    public void show(String aCardName) {
        CardLayout layout = (CardLayout) super.getLayout();
        layout.show(this, aCardName);
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
