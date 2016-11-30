/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.actions;

import com.eas.client.controls.geopane.GeoPaneUtils;
import com.eas.client.controls.geopane.JGeoPane;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.NoninvertibleTransformException;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author mg
 */
public class InfoAction extends GeoPaneAction {

    public InfoAction(JGeoPane aPane) {
        super(aPane);
        putValue(Action.NAME, "i");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String infoMessage = pane.viewToString() + "\n";
        String aoiCartesian = null;
        String aoiGeo = null;
        String projectionWkt = null;
        try {
            aoiCartesian = pane.aoiToCartesianString();
        } catch (NoninvertibleTransformException ex) {
            aoiCartesian = GeoPaneUtils.getString("screenInvalidCartesian");
        }
        try {
            aoiGeo = pane.aoiToGeoString();
        } catch (NoninvertibleTransformException | FactoryException | TransformException ex) {
            aoiGeo = GeoPaneUtils.getString("screenInvalidGeo");
        }
        try
        {
            projectionWkt = pane.getGeneralMapContext().getViewport().getBounds().getCoordinateReferenceSystem().toWKT();
        }catch(UnsupportedOperationException ex){
            projectionWkt = GeoPaneUtils.getString("projectionWktUnavailable");
        }
        infoMessage += aoiCartesian;
        infoMessage += "\n";
        infoMessage += aoiGeo;
        infoMessage += "\n";
        infoMessage += projectionWkt;
        JOptionPane.showInputDialog(pane, "", GeoPaneUtils.getString("Info"), JOptionPane.INFORMATION_MESSAGE, null, null, GeoPaneUtils.getString(infoMessage));
    }
}
