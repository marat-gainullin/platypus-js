/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.actions;

import com.eas.client.controls.geopane.JGeoPane;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.KeyStroke;

/**
 *
 * @author mg
 */
public class RightAction extends GeoPaneAction {

    public RightAction(JGeoPane aPane) {
        super(aPane);
        putValue(Action.NAME, " > ");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            pane.translateGrid(-2, 0);
            pane.repaint();
        } catch (Exception ex) {
            Logger.getLogger(RightAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
