/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.mousetools;

import com.eas.client.controls.geopane.mousetools.RectZoomer;
import com.eas.client.geo.GisUtilities;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.dbcontrols.map.DbMap;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author mg
 */
public class MouseRectSelector extends RectZoomer {

    protected DbMap map;

    public MouseRectSelector(DbMap aMap) {
        super(aMap.getPane());
        map = aMap;
    }

    @Override
    protected boolean isBeginDragValid() throws Exception {
        Point2D.Double cartesianPt = pane.awtScreen2Cartesian(mouseDown);
        Point2D.Double geoPt = pane.cartesian2Geo(cartesianPt);
        return !map.selectedGeometryHitted(GisUtilities.createPoint(geoPt));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (mouseDown != null) {
                try {
                    Point ptSource = mouseDown;
                    Point ptDest = e.getPoint();
                    if (!ptSource.equals(ptDest)) {
                        Point2D.Double cartesianSource = pane.awtScreen2Cartesian(ptSource);
                        Point2D.Double geoSource = pane.cartesian2Geo(cartesianSource);
                        Point2D.Double cartesianDest = pane.awtScreen2Cartesian(ptDest);
                        Point2D.Double geoDest = pane.cartesian2Geo(cartesianDest);
                        Polygon selectorPoly = GisUtilities.constructRectyPolygon(geoSource, geoDest);
                        List<SelectionEntry> hitted = map.hit(selectorPoly, true);
                        Rectangle2D.Double constraintRect = new Rectangle2D.Double(Math.min(geoSource.x, geoDest.x), Math.min(geoSource.y, geoDest.y), Math.abs(geoDest.x - geoSource.x), Math.abs(geoDest.y - geoSource.y));
                        filterHitted(constraintRect, hitted);
                        if (!e.isControlDown()) {
                            map.getSelection().clear();
                        }
                        map.select(hitted);
                    }
                    mouseDown = null;
                } catch (Exception ex) {
                    Logger.getLogger(RectZoomer.class.getName()).log(Level.SEVERE, null, ex);
                    mouseDown = null;
                    pane.clearCaches();
                    pane.repaint();
                }
            }
            pane.setSelectionRectangle(null);
        }
    }

    protected void filterHitted(Rectangle2D.Double aHitArea, List<SelectionEntry> aHitted) {
    }
}
