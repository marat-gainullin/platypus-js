/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import com.eas.client.forms.api.containers.BoxPane;
import com.eas.client.forms.api.containers.FlowPane;
import com.eas.client.forms.api.menu.PopupMenu;
import com.eas.client.scripts.ScriptColor;
import com.eas.controls.events.ControlEventsIProxy;
import com.eas.controls.layouts.margin.MarginLayout;
import com.eas.gui.CascadedStyle;
import com.eas.gui.Cursor;
import com.eas.gui.CursorFactory;
import com.eas.gui.Font;
import com.eas.script.NativeJavaHostObject;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public abstract class Component<D extends JComponent> {

    protected Font font;
    protected Cursor cursor;
    protected String errorMessage;
    protected D delegate;
    protected NativeJavaHostObject jsWrapper;

    @ScriptFunction(jsDoc = "Gets the parent of this component.")
    public Container<?> getParent() {
        return getContainerWrapper(delegate.getParent() instanceof JViewport && delegate.getParent().getParent() instanceof JScrollPane ? delegate.getParent().getParent() : delegate.getParent());
    }

    public String getName() {
        return delegate.getName();
    }
    private static final String GET_NEXT_FOCUSABLE_COMPONENT_JSDOC = "/**\n"
            + "* Overrides the default focus traversal policy for this"
            + " component's focus traversal cycle by unconditionally"
            + " setting the specified component as the next"
            + " component in the cycle, and this component"
            + " as the specified component's previous"
            + " component in the cycle.\n"
            + "*/";

    @ScriptFunction(jsDoc = GET_NEXT_FOCUSABLE_COMPONENT_JSDOC)
    public Component<?> getNextFocusableComponent() {
        return getComponentWrapper(delegate.getNextFocusableComponent());
    }

    @ScriptFunction
    public void setNextFocusableComponent(Component<?> aValue) {
        delegate.setNextFocusableComponent(unwrap(aValue));
    }
    private static final String ERROR_JSDOC = "/**\n"
            + "* An error message of this component."
            + " Validation procedure may set this property and subsequent focus lost event will clear it.\n"
            + "*/";

    @ScriptFunction(jsDoc = ERROR_JSDOC)
    public String getError() {
        return errorMessage;
    }

    public void setError(String aValue) {
        errorMessage = aValue;
    }
    private static final String BACKGROUND_JSDOC = "/**\n"
            + "* The background color of this component.\n"
            + "*/";

    @ScriptFunction(jsDoc = BACKGROUND_JSDOC)
    public ScriptColor getBackground() {
        return new ScriptColor(delegate.getBackground());
    }

    @ScriptFunction
    public void setBackground(Color aValue) {
        delegate.setBackground(aValue);
    }
    private static final String FOREGROUND_JSDOC = "/**\n"
            + "* The foreground color of this component.\n"
            + "*/";

    @ScriptFunction(jsDoc = FOREGROUND_JSDOC)
    public Color getForeground() {
        return new ScriptColor(delegate.getForeground());
    }

    @ScriptFunction
    public void setForeground(Color aValue) {
        delegate.setForeground(aValue);
    }
    private static final String VISIBLE_JSDOC = "/**\n"
            + "* Determines whether this component should be visible when its parent is visible.\n"
            + "*/";

    @ScriptFunction(jsDoc = VISIBLE_JSDOC)
    public boolean getVisible() {
        return delegate.isVisible();
    }

    @ScriptFunction
    public void setVisible(boolean aValue) {
        delegate.setVisible(aValue);
    }
    private static final String FOCUSABLE_JSDOC = "/**\n"
            + "* Determines whether this component may be focused.\n"
            + "*/";

    @ScriptFunction(jsDoc = FOCUSABLE_JSDOC)
    public boolean getFocusable() {
        return delegate.isFocusable();
    }

    @ScriptFunction
    public void setFocusable(boolean aValue) {
        delegate.setFocusable(aValue);
    }
    private static final String ENABLED_JSDOC = "/**\n"
            + "Determines whether this component is enabled. An enabled component "
            + "can respond to user input and generate events. Components are "
            + "enabled initially by default."
            + "*/";

    @ScriptFunction(jsDoc = ENABLED_JSDOC)
    public boolean getEnabled() {
        return delegate.isEnabled();
    }

    @ScriptFunction
    public void setEnabled(boolean aValue) {
        delegate.setEnabled(aValue);
    }
    private static final String TOOLTIP_TEXT_JSDOC = "/**\n"
            + "* The tooltip string that has been set with.\n"
            + "*/";

    @ScriptFunction(jsDoc = TOOLTIP_TEXT_JSDOC)
    public String getToolTipText() {
        return delegate.getToolTipText();
    }

    @ScriptFunction
    public void setToolTipText(String aValue) {
        delegate.setToolTipText(aValue);
    }

    private static final String OPAQUE_TEXT_JSDOC = "/**\n"
            + "* True if this component is completely opaque.\n"
            + "*/";
    @ScriptFunction(jsDoc = OPAQUE_TEXT_JSDOC)
    public boolean getOpaque() {
        return delegate.isOpaque();
    }

    @ScriptFunction
    public void setOpaque(boolean aValue) {
        delegate.setOpaque(aValue);
    }

    private static final String COMPONENT_POPUP_MENU_JSDOC = "/**\n"
            + "* <code>PopupMenu</code> that assigned for this component.\n"
            + "*/";
    @ScriptFunction(jsDoc = COMPONENT_POPUP_MENU_JSDOC)
    public PopupMenu getComponentPopupMenu() {
        return (PopupMenu) getContainerWrapper(delegate.getComponentPopupMenu());
    }

    @ScriptFunction
    public void setComponentPopupMenu(PopupMenu aMenu) {
        delegate.setComponentPopupMenu((JPopupMenu) unwrap(aMenu));
    }

     private static final String FONT_JSDOC = "/**\n"
            + "* The font of this component.\n"
            + "*/";
    @ScriptFunction(jsDoc = FONT_JSDOC)
    public Font getFont() {
        if (font == null) {
            font = new Font(delegate.getFont().getFamily(), CascadedStyle.nativeFontStyleToFontStyle(delegate.getFont()), delegate.getFont().getSize());
        }
        return font;
    }

    @ScriptFunction
    public void setFont(Font aFont) {
        font = aFont;
        //apply
        if (aFont != null) {
            delegate.setFont(new java.awt.Font(aFont.getFamily(), CascadedStyle.fontStyleToNativeFontStyle(aFont.getStyle()), aFont.getSize()));
        } else {
            delegate.setFont(null);
        }
    }

    private static final String CURSOR_JSDOC = "/**\n"
            + "* Current <code>Cursor</code> of this component.\n"
            + "*/";
    @ScriptFunction(jsDoc = CURSOR_JSDOC)
    public Cursor getCursor() {
        if (cursor == null && delegate.getCursor() != null) {
            cursor = CursorFactory.getCursor(delegate.getCursor());
        }
        return cursor;
    }

    @ScriptFunction
    public void setCursor(Cursor aCursor) {
        cursor = aCursor;
        delegate.setCursor(cursor != null ? cursor.unwrap() : null);
    }

    private static final String ON_MOUSE_CLICKED_JSDOC = "/**\n"
            + "* Mouse clicked event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_MOUSE_CLICKED_JSDOC)
    public Function getOnMouseClicked() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.mouseClicked) : null;
    }

    @ScriptFunction
    public void setOnMouseClicked(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.mouseClicked, aValue);
        }
    }

    private static final String ON_MOUSE_DRAGGED_JSDOC = "/**\n"
            + "* Mouse dragged event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_MOUSE_DRAGGED_JSDOC)
    public Function getOnMouseDragged() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.mouseDragged) : null;
    }

    @ScriptFunction
    public void setOnMouseDragged(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.mouseDragged, aValue);
        }
    }

    private static final String ON_MOUSE_ENTERED_JSDOC = "/**\n"
            + "* Mouse entered over the component event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_MOUSE_ENTERED_JSDOC)
    public Function getOnMouseEntered() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.mouseEntered) : null;
    }

    @ScriptFunction
    public void setOnMouseEntered(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.mouseEntered, aValue);
        }
    }

    private static final String ON_MOUSE_EXITED_JSDOC = "/**\n"
            + "* Mouse exited over the component event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_MOUSE_EXITED_JSDOC)
    public Function getOnMouseExited() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.mouseExited) : null;
    }

    @ScriptFunction
    public void setOnMouseExited(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.mouseExited, aValue);
        }
    }

    private static final String ON_MOUSE_MOVED_JSDOC = "/**\n"
            + "* Mouse moved event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_MOUSE_MOVED_JSDOC)
    public Function getOnMouseMoved() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.mouseMoved) : null;
    }

    @ScriptFunction
    public void setOnMouseMoved(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.mouseMoved, aValue);
        }
    }

    private static final String ON_MOUSE_PRESSED_JSDOC = "/**\n"
            + "* Mouse pressed event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_MOUSE_PRESSED_JSDOC)
    public Function getOnMousePressed() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.mousePressed) : null;
    }

    @ScriptFunction
    public void setOnMousePressed(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.mousePressed, aValue);
        }
    }
    
    private static final String ON_MOUSE_RELEASED_JSDOC = "/**\n"
            + "* Mouse released event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_MOUSE_RELEASED_JSDOC)
    public Function getOnMouseReleased() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.mouseReleased) : null;
    }

    @ScriptFunction
    public void setOnMouseReleased(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.mouseReleased, aValue);
        }
    }

    private static final String ON_MOUSE_WHEEL_MOVED_JSDOC = "/**\n"
            + "* Mouse wheel moved event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_MOUSE_WHEEL_MOVED_JSDOC)
    public Function getOnMouseWheelMoved() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.mouseWheelMoved) : null;
    }

    @ScriptFunction
    public void setOnMouseWheelMoved(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.mouseWheelMoved, aValue);
        }
    }

    private static final String ON_ACTION_PERFORMED_JSDOC = "/**\n"
            + "* Main action performed event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_ACTION_PERFORMED_JSDOC)
    public Function getOnActionPerformed() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.actionPerformed) : null;
    }

    @ScriptFunction
    public void setOnActionPerformed(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.actionPerformed, aValue);
        }
    }

    private static final String ON_COMPONENT_ADDED_JSDOC = "/**\n"
            + "* Component added event hanler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_COMPONENT_ADDED_JSDOC)
    public Function getOnComponentAdded() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.componentAdded) : null;
    }

    @ScriptFunction
    public void setOnComponentAdded(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.componentAdded, aValue);
        }
    }

    private static final String ON_COMPONENT_HIDDEN_JSDOC = "/**\n"
            + "* Component hidden event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_COMPONENT_HIDDEN_JSDOC)
    public Function getOnComponentHidden() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.componentHidden) : null;
    }

    @ScriptFunction
    public void setOnComponentHidden(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.componentHidden, aValue);
        }
    }

    private static final String ON_COMPONENT_MOVED_JSDOC = "/**\n"
            + "* Component moved event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_COMPONENT_MOVED_JSDOC)
    public Function getOnComponentMoved() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.componentMoved) : null;
    }

    @ScriptFunction
    public void setOnComponentMoved(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.componentMoved, aValue);
        }
    }

    private static final String ON_COMPONENT_REMOVED_JSDOC = "/**\n"
            + "* Component removed event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_COMPONENT_REMOVED_JSDOC)
    public Function getOnComponentRemoved() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.componentRemoved) : null;
    }

    @ScriptFunction
    public void setOnComponentRemoved(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.componentRemoved, aValue);
        }
    }

    private static final String ON_COMPONENT_RESIZED_JSDOC = "/**\n"
            + "* Component resized event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_COMPONENT_RESIZED_JSDOC)
    public Function getOnComponentResized() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.componentResized) : null;
    }

    @ScriptFunction
    public void setOnComponentResized(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.componentResized, aValue);
        }
    }

    private static final String ON_COMPONENT_SHOWN_JSDOC = "/**\n"
            + "* Component shown event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_COMPONENT_SHOWN_JSDOC)
    public Function getOnComponentShown() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.componentShown) : null;
    }

    @ScriptFunction
    public void setOnComponentShown(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.componentShown, aValue);
        }
    }

    private static final String ON_FOCUS_GAINED_JSDOC = "/**\n"
            + "* Keyboard focus gained by the component event.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_FOCUS_GAINED_JSDOC)
    public Function getOnFocusGained() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.focusGained) : null;
    }

    @ScriptFunction
    public void setOnFocusGained(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.focusGained, aValue);
        }
    }

    private static final String ON_FOCUS_LOST_JSDOC = "/**\n"
            + "* Keyboard focus lost by the component event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_FOCUS_LOST_JSDOC)
    public Function getOnFocusLost() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.focusLost) : null;
    }

    @ScriptFunction
    public void setOnFocusLost(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.focusLost, aValue);
        }
    }

    private static final String ON_KEY_PRESSED_JSDOC = "/**\n"
            + "* Key pressed event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_KEY_PRESSED_JSDOC)
    public Function getOnKeyPressed() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.keyPressed) : null;
    }

    @ScriptFunction
    public void setOnKeyPressed(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.keyPressed, aValue);
        }
    }

    private static final String ON_KEY_RELEASED_JSDOC = "/**\n"
            + "* Key released event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_KEY_RELEASED_JSDOC)
    public Function getOnKeyReleased() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.keyReleased) : null;
    }

    @ScriptFunction
    public void setOnKeyReleased(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.keyReleased, aValue);
        }
    }
    
    private static final String ON_KEY_TYPED_JSDOC = "/**\n"
            + "* Key typed event handler function.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ON_KEY_TYPED_JSDOC)
    public Function getOnKeyTyped() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.keyTyped) : null;
    }

    @ScriptFunction
    public void setOnKeyTyped(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.keyTyped, aValue);
        }
    }

    private static final String LEFT_JSDOC = "/**\n"
            + "* Horizontal coordinate of the component.\n"
            + "*/";
    @ScriptFunction(jsDoc = LEFT_JSDOC)
    public int getLeft() {
        return delegate.getLocation().x;
    }

    @ScriptFunction
    public void setLeft(int aValue) {
        if (delegate.getParent() != null && delegate.getParent().getLayout() instanceof MarginLayout) {
            MarginLayout.ajustLeft(delegate, aValue);
        }
        delegate.setLocation(aValue, getTop());
    }

    private static final String TOP_JSDOC = "/**\n"
            + "* Vertical coordinate of the component.\n"
            + "*/";
    @ScriptFunction(jsDoc = TOP_JSDOC)
    public int getTop() {
        return delegate.getLocation().y;
    }

    @ScriptFunction
    public void setTop(int aValue) {
        if (delegate.getParent() != null && delegate.getParent().getLayout() instanceof MarginLayout) {
            MarginLayout.ajustTop(delegate, aValue);
        }
        delegate.setLocation(getLeft(), aValue);
    }

    private static final String WIDTH_JSDOC = "/**\n"
            + "* Width of the component.\n"
            + "*/";
    @ScriptFunction(jsDoc = WIDTH_JSDOC)
    public int getWidth() {
        return delegate.getSize().width;
    }

    @ScriptFunction
    public void setWidth(int aValue) {
        if (delegate.getParent() != null && delegate.getParent().getLayout() instanceof MarginLayout) {
            MarginLayout.ajustWidth(delegate, aValue);
        } else if (delegate.getParent() instanceof JViewport && delegate.getParent().getParent() instanceof JScrollPane) {
            /*
             if (!delegate.isPreferredSizeSet()) {
             clearPrefSize = true;
             }
             */
            delegate.setPreferredSize(new Dimension(aValue, getHeight()));
        } else if (getParent() instanceof BoxPane) {
            delegate.setPreferredSize(new Dimension(aValue, getHeight()));
        } else if (getParent() instanceof FlowPane) {
            delegate.setPreferredSize(new Dimension(aValue, getHeight()));
        }
        delegate.setSize(aValue, getHeight());
    }

    private static final String HEIGHT_JSDOC = "/**\n"
            + "* Height of the component.\n"
            + "*/";
    @ScriptFunction(jsDoc = HEIGHT_JSDOC)
    public int getHeight() {
        return delegate.getSize().height;
    }

    @ScriptFunction
    public void setHeight(int aValue) {
        if (delegate.getParent() != null && delegate.getParent().getLayout() instanceof MarginLayout) {
            MarginLayout.ajustHeight(delegate, aValue);
        } else if (delegate.getParent() instanceof JViewport && delegate.getParent().getParent() instanceof JScrollPane) {
            /*
             if (!delegate.isPreferredSizeSet()) {
             clearPrefSize = true;
             }
             */
            delegate.setPreferredSize(new Dimension(getWidth(), aValue));
        } else if (getParent() instanceof BoxPane) {
            delegate.setPreferredSize(new Dimension(getWidth(), aValue));
        } else if (getParent() instanceof FlowPane) {
            delegate.setPreferredSize(new Dimension(getWidth(), aValue));
        }
        delegate.setSize(getWidth(), aValue);
    }

    private static final String FOCUS_JSDOC = "/**\n"
            + "* Tries to acquire focus for this component.\n"
            + "*/";
    @ScriptFunction(jsDoc = FOCUS_JSDOC)
    public void focus() {
        delegate.requestFocus();
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", delegate.getName() != null ? delegate.getName() : "", getClass().getSimpleName());
    }

    protected D getDelegate() {
        return delegate;
    }

    protected void setDelegate(D aDelegate) {
        delegate = aDelegate;
        if (delegate != null) {
            delegate.putClientProperty(ScriptUtils.WRAPPER_PROP_NAME, this);
        }
    }

    protected NativeJavaHostObject getJsWrapper() {
        return jsWrapper;
    }

    protected void setJsWrapper(NativeJavaHostObject aValue) {
        jsWrapper = aValue;
    }

    // Native API
    private static final String NATIVE_COMPONENT_JSDOC = "/**\n"
            + "* Native API. Returns low level swing component. Applicable only in J2SE swing client.\n"
            + "*/";
    @ScriptFunction(jsDoc = NATIVE_COMPONENT_JSDOC)
    public JComponent getComponent() {
        return delegate;
    }

    private static final String NATIVE_ELEMENT_JSDOC = "/**\n"
            + "* Native API. Returns low level html element. Applicable only in HTML5 client.\n"
            + "*/";
    @ScriptFunction(jsDoc = NATIVE_ELEMENT_JSDOC)
    public Object getElement() {
        return null;
    }

    protected static Component<?> lookupApiComponent(JComponent aComp) {
        JComponent comp = aComp;
        while (comp.getParent() != null && comp.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME) == null) {
            comp = (JComponent) comp.getParent();
        }
        return (Component<?>) comp.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME);
    }

    protected static Container<?> getContainerWrapper(java.awt.Component aComponent) {
        JComponent aComp = aComponent instanceof JComponent ? (JComponent) aComponent : null;
        if (aComp != null && (aComp.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME) == null
                || aComp.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME) instanceof Container)) {
            return (Container<?>) aComp.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME);
        } else {
            return null;
        }
    }

    protected static Component<?> getComponentWrapper(java.awt.Component aComponent) {
        JComponent aComp = aComponent instanceof JComponent ? (JComponent) aComponent : null;
        if (aComp != null && (aComp.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME) == null
                || aComp.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME) instanceof Component)) {
            return (Component<?>) aComp.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME);
        } else {
            return null;
        }
    }

    protected static ControlEventsIProxy checkEventsProxy(JComponent aComp) {
        ControlEventsIProxy proxy = getEventsProxy(aComp);
        if (proxy == null) {
            proxy = new FormEventsIProxy();
            proxy.setHandlee(aComp);
        }
        return proxy;
    }

    protected static ControlEventsIProxy getEventsProxy(JComponent aComp) {
        if (aComp != null && (aComp.getClientProperty(ControlEventsIProxy.EVENTS_PROXY_PROPERTY_NAME) == null
                || aComp.getClientProperty(ControlEventsIProxy.EVENTS_PROXY_PROPERTY_NAME) instanceof ControlEventsIProxy)) {
            return (ControlEventsIProxy) aComp.getClientProperty(ControlEventsIProxy.EVENTS_PROXY_PROPERTY_NAME);
        } else {
            return null;
        }
    }

    protected static JComponent unwrap(Component<?> aComp) {
        return aComp != null ? aComp.getDelegate() : null;
    }
}
