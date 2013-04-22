/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.mousetools;

import com.eas.client.controls.geopane.JGeoPane;
import com.eas.client.controls.geopane.actions.ZoomInAction;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class WheelZoomer extends GeoPaneTool {

    public WheelZoomer(JGeoPane aPane) {
        super(aPane);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        try {
            Point2D.Double screenCenterBefore = pane.awtScreen2Cartesian(e.getPoint());
            if (e.getWheelRotation() > 0) {
                pane.scaleView(1 / ZoomInAction.DEFAULT_ZOOM_FACTOR_STEP, 1 / ZoomInAction.DEFAULT_ZOOM_FACTOR_STEP, false);
            } else {
                pane.scaleView(ZoomInAction.DEFAULT_ZOOM_FACTOR_STEP, ZoomInAction.DEFAULT_ZOOM_FACTOR_STEP, false);
            }
            Point2D.Double screenCenterAfter = pane.awtScreen2Cartesian(e.getPoint());
            pane.translateView(screenCenterAfter.x - screenCenterBefore.x, screenCenterAfter.y - screenCenterBefore.y, true);
            pane.repaint();
        } catch (Exception ex) {
            Logger.getLogger(ZoomInAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isCapable(MouseToolCapability aCapability) {
        return aCapability == MouseToolCapability.WHEEL;
    }
}
