/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.mousetools;

import com.eas.client.controls.geopane.JGeoPane;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author mg
 */
public class MouseClickAlerter extends GeoPaneTool {

    public MouseClickAlerter(JGeoPane aPane) {
        super(aPane);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            Point2D.Double cartesianPt = pane.awtScreen2Cartesian(e.getPoint());
            Point2D.Double geoPt = pane.cartesian2Geo(cartesianPt);
            pane.fireMapClicked(e, cartesianPt, geoPt);
        } catch (NoninvertibleTransformException | FactoryException | TransformException ex) {
            Logger.getLogger(MouseClickAlerter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isCapable(MouseToolCapability aCapability) {
        return aCapability == MouseToolCapability.BUTTONS;
    }
}
