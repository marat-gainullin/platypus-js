/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.gui.beans;

import com.jeta.forms.gui.common.FormUtils;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author Marat
 */
public class EventsIProxy extends Object implements MouseListener,
        MouseWheelListener,
        ChangeListener,
        ComponentListener,
        MouseMotionListener,
        //ContainerListener,
        ItemListener,
        ActionListener,
        //HierarchyListener,
        FocusListener,
        PropertyChangeListener,
        KeyListener,
        WindowListener,
        InternalFrameListener
{

    private JETABean bean = null;
    private Component mHandlee = null;

    public EventsIProxy(JETABean abean)
    {
        super();
        bean = abean;
    }

    public void setHandlee(Component aHandlee)
    {
        try
        {
            if (mHandlee != aHandlee)
            {
                if (mHandlee != null)
                {
                    unregisterEvents();
                }
                mHandlee = aHandlee;
                if (mHandlee != null)
                {
                    registerEvents();
                }
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(EventsIProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void reflectionInvokeARListener(String name, Class aClass)
    {
        if (mHandlee != null)
        {
            Class[] mparams = new Class[1];
            try
            {
                mparams[0] = aClass;
                Method setter = mHandlee.getClass().getMethod(name, mparams);
                if (setter != null)
                {
                    setter.invoke(mHandlee, this);
                }
            }
            catch (Exception e)
            {
                /* ignore*/
            }
        }
    }

    public boolean reflectionIsARListener(String name, Class aClass)
    {
        if (mHandlee != null)
        {
            Class[] mparams = new Class[1];
            try
            {
                mparams[0] = aClass;
                Method setter = mHandlee.getClass().getMethod(name, mparams);
                return (setter != null);
            }
            catch (Exception e)
            {/* ignore*/

            }
        }
        return false;
    }

    public void registerEvents()
    {
        if ((!FormUtils.isDesignMode() || FormUtils.isRuntime() || FormUtils.isSemiRuntime() /*|| ((bean != null) && bean.getTopLevelAncestor() instanceof RunFrame)*/) &&
                bean != null && mHandlee != null && bean.isAnyEvents())
        {
            mHandlee.addKeyListener(this);
            mHandlee.addMouseListener(this);
            mHandlee.addMouseMotionListener(this);
            mHandlee.addMouseWheelListener(this);
            mHandlee.addComponentListener(this);
            mHandlee.addFocusListener(this);
            mHandlee.addPropertyChangeListener(this);
            if (mHandlee instanceof Window)
            {
                ((Window) mHandlee).addWindowListener(this);
            }
            if (mHandlee instanceof JInternalFrame)
            {
                ((JInternalFrame) mHandlee).addInternalFrameListener(this);
            }
            reflectionInvokeARListener("addActionListener", ActionListener.class);
            reflectionInvokeARListener("addChangeListener", ChangeListener.class);
            reflectionInvokeARListener("addItemListener", ItemListener.class);
        }
    }

    public void unregisterEvents()
    {
        if ((!FormUtils.isDesignMode() || FormUtils.isRuntime() || FormUtils.isSemiRuntime() /*|| ((bean != null) && bean.getTopLevelAncestor() instanceof RunFrame)*/) &&
                mHandlee != null && bean.isAnyEvents())
        {
            mHandlee.removeMouseListener(this);
            mHandlee.removeKeyListener(this);
            mHandlee.removeMouseMotionListener(this);
            mHandlee.removeMouseWheelListener(this);
            mHandlee.removeComponentListener(this);
            mHandlee.removeFocusListener(this);
            mHandlee.removePropertyChangeListener(this);
            if (mHandlee instanceof Window)
            {
                ((Window) mHandlee).removeWindowListener(this);
            }
            if (mHandlee instanceof JInternalFrame)
            {
                ((JInternalFrame) mHandlee).removeInternalFrameListener(this);
            }
            reflectionInvokeARListener("removeActionListener", ActionListener.class);
            reflectionInvokeARListener("removeChangeListener", ChangeListener.class);
            reflectionInvokeARListener("removeItemListener", ItemListener.class);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        bean.executeEvent("mouseClicked", e);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        bean.executeEvent("mousePressed", e);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        bean.executeEvent("mouseReleased", e);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        bean.executeEvent("mouseEntered", e);
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        bean.executeEvent("mouseExited", e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        bean.executeEvent("mouseWheelMoved", e);
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        bean.executeEvent("stateChanged", e);
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        bean.executeEvent("componentResized", e);
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
        bean.executeEvent("componentMoved", e);
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
        bean.executeEvent("componentShown", e);
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {
        bean.executeEvent("componentHidden", e);
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        bean.executeEvent("mouseDragged", e);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        bean.executeEvent("mouseMoved", e);
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        bean.executeEvent("itemStateChanged", e);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (bean.getDelegate() == e.getSource())
        {
            bean.executeEvent("actionPerformed", e);
        }
        else
        {
            Object o = e.getSource();
            if (o != null && o instanceof JComponent)
            {
                JComponent comp = (JComponent) o;
                Object prop = comp.getClientProperty(FormUtils.SCRIPT_ACTION_NAME);
                if (prop != null && prop instanceof String)
                {
                    bean.executeSource((String) prop, e);
                }
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        bean.executeEvent("focusGained", e);
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        bean.executeEvent("focusLost", e);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        bean.executeEvent("propertyChange", evt);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        bean.executeEvent("keyTyped", e);
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        bean.executeEvent("keyPressed", e);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        bean.executeEvent("keyReleased", e);
    }

    protected class JETAWindowEvent
    {

        protected int id = -1;
        protected Component component = null;
        protected Object source = null;

        JETAWindowEvent(WindowEvent e)
        {
            super();
            id = e.getID();
            component = e.getWindow();
            source = e.getSource();
        }

        JETAWindowEvent(InternalFrameEvent e)
        {
            super();
            id = e.getID();
            component = e.getInternalFrame();
            source = e.getSource();
        }

        public int getId()
        {
            return id;
        }

        public Component getComponent()
        {
            return component;
        }

        public Object getSource()
        {
            return source;
        }
    }

    protected JETAWindowEvent wrapWindowEvent(WindowEvent e)
    {
        return new JETAWindowEvent(e);
    }

    protected JETAWindowEvent wrapWindowEvent(InternalFrameEvent e)
    {
        return new JETAWindowEvent(e);
    }

    @Override
    public void windowOpened(WindowEvent e)
    {
        bean.executeEvent("windowOpened", wrapWindowEvent(e));
    }

    @Override
    public void windowClosing(WindowEvent e)
    {
        bean.executeEvent("windowClosing", wrapWindowEvent(e));
    }

    @Override
    public void windowClosed(WindowEvent e)
    {
        bean.executeEvent("windowClosed", wrapWindowEvent(e));
    }

    @Override
    public void windowIconified(WindowEvent e)
    {
        bean.executeEvent("windowIconified", wrapWindowEvent(e));
    }

    @Override
    public void windowDeiconified(WindowEvent e)
    {
        bean.executeEvent("windowDeiconified", wrapWindowEvent(e));
    }

    @Override
    public void windowActivated(WindowEvent e)
    {
        bean.executeEvent("windowActivated", wrapWindowEvent(e));
    }

    @Override
    public void windowDeactivated(WindowEvent e)
    {
        bean.executeEvent("windowDeactivated", wrapWindowEvent(e));
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e)
    {
        bean.executeEvent("windowOpened", wrapWindowEvent(e));
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e)
    {
        bean.executeEvent("windowClosing", wrapWindowEvent(e));
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e)
    {
        bean.executeEvent("windowClosed", wrapWindowEvent(e));
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e)
    {
        bean.executeEvent("windowIconified", wrapWindowEvent(e));
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e)
    {
        bean.executeEvent("windowDeiconified", wrapWindowEvent(e));
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e)
    {
        bean.executeEvent("windowActivated", wrapWindowEvent(e));
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e)
    {
        bean.executeEvent("windowDeactivated", wrapWindowEvent(e));
    }
}