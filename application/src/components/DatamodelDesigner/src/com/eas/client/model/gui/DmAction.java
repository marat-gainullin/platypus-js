/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui;

import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 *
 * @author mg
 */
public abstract class DmAction extends AbstractAction {

    public DmAction() {
        super();
        KeyStroke accel = getDmActionAccelerator();
        String hintAdd = "";
        if (accel != null) {
            hintAdd = " \t(";
            if (accel.getModifiers() != 0) {
                hintAdd += KeyEvent.getKeyModifiersText(accel.getModifiers()) + "+";
            }
            hintAdd += KeyEvent.getKeyText(accel.getKeyCode()) + ")";
        }
        putValue(Action.ACCELERATOR_KEY, accel);
        putValue(Action.NAME, getDmActionText());
        putValue(Action.SHORT_DESCRIPTION, getDmActionHint() + hintAdd);
        putValue(Action.SMALL_ICON, getDmActionSmallIcon());
        putValue(Action.LARGE_ICON_KEY, getDmActionSmallIcon());
        setEnabled(false);
    }

    public abstract String getDmActionText();

    public abstract String getDmActionHint();

    public abstract Icon getDmActionSmallIcon();

    public abstract KeyStroke getDmActionAccelerator();
}

