/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.mousetools;

import com.eas.client.controls.geopane.mousetools.MouseToolCapability;
import com.eas.util.gis.GeometryUtils;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.dbcontrols.map.DbMap;
import com.vividsolutions.jts.geom.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author mg
 */
public class MousePointSelector extends MapTool {

    public MousePointSelector(DbMap aMap) {
        super(aMap);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            try {
                Point2D.Double cartesianDest = pane.awtScreen2Cartesian(e.getPoint());
                Point2D.Double geoDest = pane.cartesian2Geo(cartesianDest);
                Point selectorPoint = GeometryUtils.createPoint(geoDest);
                List<SelectionEntry> hitted = map.hit(selectorPoint);
                filterHitted(selectorPoint, hitted);
                if (!e.isControlDown()) {
                    map.getSelection().clear();
                }
                map.select(hitted);
            } catch (Exception ex) {
                Logger.getLogger(MousePointSelector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected void filterHitted(Point aHitPoint, List<SelectionEntry> aHitted) {
    }

    @Override
    public boolean isCapable(MouseToolCapability aCapability) {
        return aCapability == MouseToolCapability.BUTTONS;
    }
}
