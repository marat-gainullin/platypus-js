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
public class UpAction extends GeoPaneAction {

    public UpAction(JGeoPane aPane) {
        super(aPane);
        putValue(Action.NAME, " < ");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            pane.translateGrid(0, 2);
            pane.repaint();
        } catch (Exception ex) {
            Logger.getLogger(UpAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
