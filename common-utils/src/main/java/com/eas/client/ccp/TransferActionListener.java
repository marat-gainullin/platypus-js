/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.ccp;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author pk
 */
public class TransferActionListener implements ActionListener, PropertyChangeListener
{
    private JComponent focusOwner = null;
    private ActionListener defaultListener = null;

    public TransferActionListener()
    {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener("permanentFocusOwner", this);
    }

    public TransferActionListener(ActionListener aDefaultListener)
    {
        this();
        defaultListener = aDefaultListener;
    }

    public void propertyChange(PropertyChangeEvent e)
    {
        Object o = e.getNewValue();
        if (o instanceof JComponent)
            focusOwner = (JComponent) o;
        else
            focusOwner = null;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (focusOwner == null)
            return;
        Action a = null;
        if (e.getSource() instanceof AbstractButton)
        {
            Action sourceAction = ((AbstractButton) e.getSource()).getAction();
            if (sourceAction == TransferHandler.getCopyAction())
                a = focusOwner.getActionMap().get("copy");
            else if (sourceAction == TransferHandler.getCutAction())
                a = focusOwner.getActionMap().get("cut");
            else if (sourceAction == TransferHandler.getPasteAction())
                a = focusOwner.getActionMap().get("paste");
        }
        else
        {
            String action = (String) e.getActionCommand();
            a = focusOwner.getActionMap().get(action);
            /* It is possible, that CCP actions in focusOwner actionMap are capitalised
             * It is worth to check capitalized words in such case, as people tend
             * to name such actions in a "natural" user-friendly way. */
            if (a == null && "copy".equals(action))
                a = focusOwner.getActionMap().get("Copy");
            if (a == null && "cut".equals(action))
                a = focusOwner.getActionMap().get("Cut");
            if (a == null && "paste".equals(action))
                a = focusOwner.getActionMap().get("Paste");
            /* Also, it is worth to check the opposite case, e.g. actionCommand is
             * capitalised, and actionMap entry is lower-cased. */
            if (a == null && "Copy".equals(action))
                a = focusOwner.getActionMap().get("copy");
            if (a == null && "Cut".equals(action))
                a = focusOwner.getActionMap().get("cut");
            if (a == null && "Paste".equals(action))
                a = focusOwner.getActionMap().get("paste");
        }
        if (a != null)
            a.actionPerformed(new ActionEvent(focusOwner, ActionEvent.ACTION_PERFORMED, null));
        else if (defaultListener != null)
            defaultListener.actionPerformed(e);
    }
}
