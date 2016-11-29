/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events.rt;

import com.eas.client.forms.events.EventsWrapper;
import com.eas.util.exceptions.ClosedManageException;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author mg
 */
public class WindowEventsIProxy extends ControlEventsIProxy implements
        WindowListener, WindowStateListener,
        InternalFrameListener, PropertyChangeListener {

    public static final int windowOpened = CONTROL_EVENT_LAST + 1;
    public static final int windowClosing = CONTROL_EVENT_LAST + 2;
    public static final int windowClosed = CONTROL_EVENT_LAST + 3;
    public static final int windowIconified = CONTROL_EVENT_LAST + 4;
    public static final int windowRestored = CONTROL_EVENT_LAST + 5;
    public static final int windowMaximized = CONTROL_EVENT_LAST + 6;
    public static final int windowActivated = CONTROL_EVENT_LAST + 7;
    public static final int windowDeactivated = CONTROL_EVENT_LAST + 8;
    protected boolean opened;

    public WindowEventsIProxy() {
        super();
    }

    @Override
    public void registerEvents() {
        super.registerEvents();
        if (mHandlee != null) {
            if (mHandlee instanceof Window) {
                ((Window) mHandlee).addWindowListener(this);
                ((Window) mHandlee).addWindowStateListener(this);
            }
            if (mHandlee instanceof JInternalFrame) {
                ((JInternalFrame) mHandlee).addInternalFrameListener(this);
                ((JInternalFrame) mHandlee).addPropertyChangeListener(this);
            }
        }
    }

    @Override
    public void unregisterEvents() {
        super.unregisterEvents();
        if (mHandlee != null) {
            if (mHandlee instanceof Window) {
                ((Window) mHandlee).removeWindowListener(this);
                ((Window) mHandlee).removeWindowStateListener(this);
            }
            if (mHandlee instanceof JInternalFrame) {
                ((JInternalFrame) mHandlee).removeInternalFrameListener(this);
                ((JInternalFrame) mHandlee).removePropertyChangeListener(this);
            }
        }
    }

    @Override
    protected Object wrapEvent(Object anEvent) {
        if (anEvent instanceof java.awt.event.WindowEvent) {
            return EventsWrapper.wrap((java.awt.event.WindowEvent) anEvent);
        } else {
            return super.wrapEvent(anEvent);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if (!opened) {
            executeEvent(windowOpened, e);
            opened = true;
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        Object execEvent = executeEvent(windowClosing, e);
        if (Boolean.FALSE.equals(execEvent)) {
            throw new ClosedManageException();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (opened) {
            executeEvent(windowClosed, e);
            opened = false;
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
        executeEvent(windowIconified, e);
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        executeEvent(windowRestored, e);
    }

    @Override
    public void windowActivated(WindowEvent e) {
        executeEvent(windowActivated, e);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        executeEvent(windowDeactivated, e);
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        if (e.getNewState() == Frame.MAXIMIZED_BOTH) {
            executeEvent(windowMaximized, e);
        } else if (e.getNewState() == Frame.NORMAL && e.getOldState() == Frame.MAXIMIZED_BOTH) {
            executeEvent(windowRestored, e);
        }
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
        executeEvent(windowOpened, e);
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        Object execEvent = executeEvent(windowClosing, e);
        if (Boolean.FALSE.equals(execEvent)) {
            throw new ClosedManageException();
        }
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        executeEvent(windowClosed, e);
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {
        executeEvent(windowIconified, e);
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
        executeEvent(windowRestored, e);
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        executeEvent(windowActivated, e);
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
        executeEvent(windowDeactivated, e);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof JInternalFrame && JInternalFrame.IS_MAXIMUM_PROPERTY.equals(evt.getPropertyName())) {
            JInternalFrame source = (JInternalFrame) evt.getSource();
            if (Boolean.FALSE.equals(evt.getOldValue()) && Boolean.TRUE.equals(evt.getNewValue())) {
                executeEvent(windowMaximized, evt);
            } else if (Boolean.TRUE.equals(evt.getOldValue()) && Boolean.FALSE.equals(evt.getNewValue())
                    && !source.isIcon()) {
                executeEvent(windowRestored, evt);
            }
        }
    }
}
