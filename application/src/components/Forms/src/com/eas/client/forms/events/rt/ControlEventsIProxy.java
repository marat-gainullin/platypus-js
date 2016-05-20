package com.eas.client.forms.events.rt;

import com.eas.client.forms.components.rt.HasValue;
import com.eas.client.forms.events.EventsWrapper;
import com.eas.script.Scripts;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ControlEventsIProxy implements MouseListener,
        MouseWheelListener,
        ComponentListener,
        MouseMotionListener,
        ContainerListener,
        ActionListener,
        FocusListener,
        PropertyChangeListener,
        KeyListener {

    public static final String EVENTS_PROXY_PROPERTY_NAME = "eventsProxy";
    public static final int mouseClicked = 1;
    public static final int mousePressed = 2;
    public static final int mouseReleased = 3;
    public static final int mouseEntered = 4;
    public static final int mouseExited = 5;
    public static final int mouseWheelMoved = 6;
    public static final int componentResized = 8;
    public static final int componentMoved = 9;
    public static final int componentShown = 10;
    public static final int componentHidden = 11;
    public static final int mouseDragged = 12;
    public static final int mouseMoved = 13;
    public static final int actionPerformed = 15;
    public static final int focusGained = 16;
    public static final int focusLost = 17;
    public static final int propertyChange = 18;
    public static final int keyTyped = 19;
    public static final int keyPressed = 20;
    public static final int keyReleased = 21;
    public static final int componentAdded = 22;
    public static final int componentRemoved = 23;
    public static final int valueChanged = 24;
    protected static final int CONTROL_EVENT_LAST = valueChanged;
    protected Component mHandlee;
    protected JSObject eventThis;
    protected Map<Integer, JSObject> handlers = new HashMap<>();

    public ControlEventsIProxy() {
        super();
    }

    public ControlEventsIProxy(Component aHandlee) {
        super();
        setHandlee(aHandlee);
    }

    public JSObject getEventThis() {
        return eventThis;
    }

    public void setEventThis(JSObject aValue) {
        eventThis = aValue;
    }

    public Map<Integer, JSObject> getHandlers() {
        return handlers;
    }

    protected Object wrapEvent(Object anEvent) {
        if (anEvent instanceof java.awt.event.MouseEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.MouseEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.KeyEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.KeyEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.FocusEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.FocusEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.ContainerEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.ContainerEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.ComponentEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.ComponentEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.ActionEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.ActionEvent) anEvent);
        } else if (anEvent instanceof java.beans.PropertyChangeEvent) {
            anEvent = EventsWrapper.wrap((java.beans.PropertyChangeEvent) anEvent);
        }
        return anEvent;
    }

    protected Object executeEvent(final int aEventId, final Object anEvent) {
        try {
            JSObject handler = handlers.get(aEventId);
            if (handler != null) {
                return Scripts.getSpace().toJava(handler.call(eventThis, new Object[]{Scripts.getSpace().toJs(wrapEvent(anEvent))}));
            } else {
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(ControlEventsIProxy.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    public final void setHandlee(Component aHandlee) {
        try {
            if (mHandlee != aHandlee) {
                if (mHandlee != null) {
                    unregisterEvents();
                }
                mHandlee = aHandlee;
                if (mHandlee != null) {
                    registerEvents();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ControlEventsIProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void reflectionInvokeARListener(String name, Class aClass) {
        reflectionInvokeARListener(mHandlee, name, aClass, this);
    }

    public static void reflectionInvokeARListener(Component mHandlee, String name, Class<?> aListenerClass, Object aListenerInstance) {
        if (mHandlee != null) {
            Class[] mparams = new Class[]{aListenerClass};
            try {
                Method setter = mHandlee.getClass().getMethod(name, mparams);
                if (setter != null) {
                    setter.invoke(mHandlee, aListenerInstance);
                }
            } catch (Exception e) {
                /* ignore*/
            }
        }
    }

    public boolean reflectionIsARListener(String name, Class aClass) {
        if (mHandlee != null) {
            Class[] mparams = new Class[1];
            try {
                mparams[0] = aClass;
                Method setter = mHandlee.getClass().getMethod(name, mparams);
                return (setter != null);
            } catch (Exception e) {/* ignore*/

            }
        }
        return false;
    }

    public void registerEvents() {
        if (mHandlee != null) {
            if (mHandlee instanceof JComponent) {
                ((JComponent) mHandlee).putClientProperty(EVENTS_PROXY_PROPERTY_NAME, this);
            }
            mHandlee.addKeyListener(this);
            mHandlee.addMouseListener(this);
            mHandlee.addMouseMotionListener(this);
            if (getOnMouseWheelMoved() != null) {
                mHandlee.addMouseWheelListener(this);
            }
            mHandlee.addComponentListener(this);
            mHandlee.addFocusListener(this);
            mHandlee.addPropertyChangeListener(this);
            if (mHandlee instanceof Container) {
                ((Container) mHandlee).addContainerListener(this);
            }
            reflectionInvokeARListener("addActionListener", ActionListener.class);
            reflectionInvokeARListener("addChangeListener", ChangeListener.class);
            reflectionInvokeARListener("addItemListener", ItemListener.class);
        }
    }

    public void unregisterEvents() {
        if (mHandlee != null) {
            mHandlee.removeMouseListener(this);
            mHandlee.removeKeyListener(this);
            mHandlee.removeMouseMotionListener(this);
            mHandlee.removeMouseWheelListener(this);
            mHandlee.removeComponentListener(this);
            mHandlee.removeFocusListener(this);
            mHandlee.removePropertyChangeListener(this);
            reflectionInvokeARListener("removeActionListener", ActionListener.class);
            reflectionInvokeARListener("removeChangeListener", ChangeListener.class);
            reflectionInvokeARListener("removeItemListener", ItemListener.class);
            if (mHandlee instanceof JComponent) {
                ((JComponent) mHandlee).putClientProperty(EVENTS_PROXY_PROPERTY_NAME, null);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        executeEvent(mouseClicked, e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        executeEvent(mousePressed, e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        executeEvent(mouseReleased, e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        executeEvent(mouseEntered, e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        executeEvent(mouseExited, e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        executeEvent(mouseWheelMoved, e);
    }

    public JSObject getOnMouseWheelMoved() {
        return handlers.get(mouseWheelMoved);
    }

    public void setOnMouseWheelMoved(JSObject aValue) {
        JSObject oldValue = getOnMouseWheelMoved();
        handlers.put(mouseWheelMoved, aValue);
        if (mHandlee != null) {
            if (oldValue == null && aValue != null) {
                mHandlee.addMouseWheelListener(this);
            } else if (oldValue != null && aValue == null) {
                mHandlee.removeMouseWheelListener(this);
            }
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        executeEvent(componentResized, e);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        executeEvent(componentMoved, e);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        executeEvent(componentShown, e);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        executeEvent(componentHidden, e);
    }

    @Override
    public void componentAdded(ContainerEvent e) {
        executeEvent(componentAdded, e);
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
        executeEvent(componentRemoved, e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        executeEvent(mouseDragged, e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        executeEvent(mouseMoved, e);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        executeEvent(actionPerformed, e);
    }

    @Override
    public void focusGained(FocusEvent e) {
        executeEvent(focusGained, e);
    }

    @Override
    public void focusLost(FocusEvent e) {
        executeEvent(focusLost, e);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (HasValue.VALUE_PROP_NAME.equals(evt.getPropertyName())) {
            executeEvent(valueChanged, evt);
        } else {
            executeEvent(propertyChange, evt);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        executeEvent(keyTyped, e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        executeEvent(keyPressed, e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        executeEvent(keyReleased, e);
    }
}
