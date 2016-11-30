/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.gui;

import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 *
 * @author mg
 */
public class ResultingDialog extends JDialog
{
    public class EscapeAction extends AbstractAction
    {
        public EscapeAction()
        {
            super();
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        }

        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }


    public int result = 0;

    public ResultingDialog(Window parentWindow, String aTitle, ModalityType mType)
    {
        super(parentWindow, aTitle, mType);
        ActionMap am = getRootPane().getActionMap();
        EscapeAction escapeAction = new EscapeAction();
        am.put(EscapeAction.class.getSimpleName(), escapeAction);
        InputMap im = getRootPane().getInputMap();
        im.put((KeyStroke) escapeAction.getValue(Action.ACCELERATOR_KEY),EscapeAction.class.getSimpleName());
    }

}
