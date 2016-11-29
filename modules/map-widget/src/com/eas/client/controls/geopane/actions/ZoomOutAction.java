/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.actions;

import com.eas.client.controls.geopane.JGeoPane;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.KeyStroke;

/**
 *
 * @author mg
 */
public class ZoomOutAction extends GeoPaneAction {

    public ZoomOutAction(JGeoPane aPane) {
        super(aPane);
        putValue(Action.NAME, " + ");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Point2D.Double screenCenterBefore = pane.screen2Cartesian(new Point(0, 0));
            pane.scaleView(1/ZoomInAction.DEFAULT_ZOOM_FACTOR_STEP, 1/ZoomInAction.DEFAULT_ZOOM_FACTOR_STEP, false);
            Point2D.Double screenCenterAfter = pane.screen2Cartesian(new Point(0, 0));
            pane.translateView(screenCenterAfter.x-screenCenterBefore.x, screenCenterAfter.y-screenCenterBefore.y, true);
            pane.repaint();
        } catch (Exception ex) {
            Logger.getLogger(ZoomInAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
