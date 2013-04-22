/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.mousetools;

import com.eas.client.controls.geopane.Cursors;
import com.eas.client.controls.geopane.mousetools.MouseToolCapability;
import com.eas.client.geo.GisUtilities;
import com.eas.dbcontrols.map.DbMap;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class MouseCursorer extends MapTool {

    protected Point mouseDown;

    public MouseCursorer(DbMap aMap) {
        super(aMap);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseDown == null) {
            try {
                configureCursors(e);
            } catch (Exception ex) {
                Logger.getLogger(MouseCursorer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        try {
            configureCursors(e);
        } catch (Exception ex) {
            Logger.getLogger(MouseCursorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        mouseDown = null;
    }

    public void configureCursors(MouseEvent e) throws Exception {
        Object mode = map.getTools().getInstalled();
        if (mode == MouseTools.VIEW) {
            ensureCursor(Cursors.ZOOM);
        } else if (mode == MouseTools.NAVIGATION) {
            Point2D.Double cartPt = map.getPane().awtScreen2Cartesian(e.getPoint());
            Point2D.Double goePt = map.getPane().cartesian2Geo(cartPt);
            boolean shapeSelected = map.selectedGeometryHitted(GisUtilities.createPoint(goePt));
            boolean pointSelected = map.selectedPointHitted(GisUtilities.createPoint(goePt));
            if (shapeSelected || pointSelected) {
                if (pointSelected) {
                    ensureCursor(Cursors.PAN_POINT);
                } else {
                    ensureCursor(Cursors.HAND);
                }
            } else {
                ensureCursor(Cursors.ZOOM);
            }
        } else if (mode == MouseTools.SELECTION) {
            Point2D.Double cartPt = map.getPane().awtScreen2Cartesian(e.getPoint());
            Point2D.Double goePt = map.getPane().cartesian2Geo(cartPt);
            boolean shapeSelected = map.selectedGeometryHitted(GisUtilities.createPoint(goePt));
            boolean pointSelected = map.selectedPointHitted(GisUtilities.createPoint(goePt));
            if (shapeSelected || pointSelected) {
                if (pointSelected) {
                    ensureCursor(Cursors.PAN_POINT);
                } else {
                    ensureCursor(Cursors.HAND);
                }
            } else {
                ensureCursor(Cursors.CROSS);
            }
        } else if (mode == MouseTools.EDITING) {

            ensureCursor(Cursors.DRAW);
        } else if (mode == MouseTools.VERTICES_EDITING) {
            Point2D.Double cartPt = map.getPane().awtScreen2Cartesian(e.getPoint());
            Point2D.Double goePt = map.getPane().cartesian2Geo(cartPt);
            boolean shapeSelected = map.selectedPointHitted(GisUtilities.createPoint(goePt));
            if (shapeSelected) {
                ensureCursor(Cursors.PAN_POINT);
            } else {
                ensureCursor(Cursors.CROSS);
            }
        }
    }

    @Override
    public boolean isCapable(MouseToolCapability aCapability) {
        return aCapability == MouseToolCapability.MOTION || aCapability == MouseToolCapability.BUTTONS;
    }

    private void ensureCursor(Cursor aCursor) {
        if (pane.getCursor() != aCursor) {
            pane.setCursor(aCursor);
        }
    }
}
