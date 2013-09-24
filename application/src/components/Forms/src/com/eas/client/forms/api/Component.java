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

    @ScriptFunction(jsDocText = "Gets the parent of this component.")
    public Container<?> getParent() {
        return getContainerWrapper(delegate.getParent() instanceof JViewport && delegate.getParent().getParent() instanceof JScrollPane ? delegate.getParent().getParent() : delegate.getParent());
    }

    public String getName() {
        return delegate.getName();
    }

    @ScriptFunction(jsDocText = "Overrides the default FocusTraversalPolicy for this"
    + "Component's focus traversal cycle by unconditionally "
    + "setting the specified Component as the next "
    + "Component in the cycle, and this Component "
    + "as the specified Component's previous "
    + "component in the cycle.")
    public Component<?> getNextFocusableComponent() {
        return getComponentWrapper(delegate.getNextFocusableComponent());
    }

    @ScriptFunction
    public void setNextFocusableComponent(Component<?> aValue) {
        delegate.setNextFocusableComponent(unwrap(aValue));
    }

    @ScriptFunction(jsDocText = "An error message of this component."
    + "Validation procedure may set this property and subsequent focus lost event will clear it.")
    public String getError() {
        return errorMessage;
    }

    public void setError(String aValue) {
        errorMessage = aValue;
    }

    @ScriptFunction(jsDocText = "The background color of this component.")
    public ScriptColor getBackground() {
        return new ScriptColor(delegate.getBackground());
    }

    @ScriptFunction
    public void setBackground(Color aValue) {
        delegate.setBackground(aValue);
    }

    @ScriptFunction(jsDocText = "The foreground color of this component.")
    public Color getForeground() {
        return new ScriptColor(delegate.getForeground());
    }

    @ScriptFunction
    public void setForeground(Color aValue) {
        delegate.setForeground(aValue);
    }

    @ScriptFunction(jsDocText = "Determines whether this component should be visible when its parent is visible.")
    public boolean isVisible() {
        return delegate.isVisible();
    }

    @ScriptFunction
    public void setVisible(boolean aValue) {
        delegate.setVisible(aValue);
    }

    @ScriptFunction(jsDocText = "Determines whether this component may be focused.")
    public boolean isFocusable() {
        return delegate.isFocusable();
    }

    @ScriptFunction
    public void setFocusable(boolean aValue) {
        delegate.setFocusable(aValue);
    }

    @ScriptFunction(jsDocText = "Determines whether this component is enabled. An enabled component "
    + "can respond to user input and generate events. Components are "
    + "enabled initially by default.")
    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    @ScriptFunction
    public void setEnabled(boolean aValue) {
        delegate.setEnabled(aValue);
    }

    @ScriptFunction(jsDocText = "The tooltip string that has been set with.")
    public String getToolTipText() {
        return delegate.getToolTipText();
    }

    @ScriptFunction
    public void setToolTipText(String aValue) {
        delegate.setToolTipText(aValue);
    }

    @ScriptFunction(jsDocText = "True if this component is completely opaque.")
    public boolean isOpaque() {
        return delegate.isOpaque();
    }

    @ScriptFunction
    public void setOpaque(boolean aValue) {
        delegate.setOpaque(aValue);
    }

    @ScriptFunction(jsDocText = "A PopupMenu that assigned for this component.")
    public PopupMenu getComponentPopupMenu() {
        return (PopupMenu) getContainerWrapper(delegate.getComponentPopupMenu());
    }

    @ScriptFunction
    public void setComponentPopupMenu(PopupMenu aMenu) {
        delegate.setComponentPopupMenu((JPopupMenu) unwrap(aMenu));
    }

    @ScriptFunction(jsDocText = "The font of this component.")
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

    @ScriptFunction(jsDocText = "The font of this component.")
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

    @ScriptFunction(jsDocText = "Mouse clicked event.")
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

    @ScriptFunction(jsDocText = "Mouse dragged event.")
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

    @ScriptFunction(jsDocText = "Mouse entered over the component event.")
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

    @ScriptFunction(jsDocText = "Mouse exited from the component event.")
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

    @ScriptFunction(jsDocText = "Mouse moved event.")
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

    @ScriptFunction(jsDocText = "Mouse pressed event.")
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

    @ScriptFunction(jsDocText = "Mouse released event.")
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

    @ScriptFunction(jsDocText = "Mouse wheel moved event.")
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

    @ScriptFunction(jsDocText = "Main acion performed event.")
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

    @ScriptFunction(jsDocText = "Component added event.")
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

    @ScriptFunction(jsDocText = "Component hidden event.")
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

    @ScriptFunction(jsDocText = "Component moved event.")
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

    @ScriptFunction(jsDocText = "Component removed event.")
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

    @ScriptFunction(jsDocText = "Component resized event.")
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

    @ScriptFunction(jsDocText = "Component shown event.")
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

    @ScriptFunction(jsDocText = "Keyboard focus gained by the component event.")
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

    @ScriptFunction(jsDocText = "Keyboard focus lost by the component event.")
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

    @ScriptFunction(jsDocText = "Key pressed event.")
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

    @ScriptFunction(jsDocText = "Key released event.")
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

    @ScriptFunction(jsDocText = "Key typed event.")
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

    @ScriptFunction(jsDocText = "Horizontal coordinate of the component.")
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

    @ScriptFunction(jsDocText = "Vertical coordinate of the component.")
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

    @ScriptFunction(jsDocText = "Width of the component.")
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

    @ScriptFunction(jsDocText = "Height of the component.")
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

    @ScriptFunction(jsDocText = "Tries to focus this component.")
    public void focus(){
        delegate.requestFocus();
    }
    
    @Override
    @ScriptFunction(jsDocText = "Returns a string representing the specified object.")
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
    @ScriptFunction(jsDoc = "Native API. Returns low level swing component. Applicable only in J2SE swing client.")
    public JComponent getComponent() {
        return delegate;
    }

    @ScriptFunction(jsDoc = "Native API. Returns low level html element. Applicable only in HTML5 client.")
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
