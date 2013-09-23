/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.mousetools;

import com.eas.client.controls.geopane.Cursors;
import com.eas.client.controls.geopane.mousetools.MouseToolCapability;
import com.eas.client.geo.GisUtilities;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.dbcontrols.map.DbMap;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author mg
 */
public class MouseCursorer extends MapTool {

    protected Point mouseDown;
    private final JLabel hint = new JLabel();

    public MouseCursorer(DbMap aMap) {
        super(aMap);
        hint.setPreferredSize(new Dimension(10, 10));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseDown == null) {
            try {
                showHint(e.getPoint());
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

    private void showHint(Point aPoint) {
        if (aPoint != null) {
            try {
                Point2D.Double cartPt = map.getPane().awtScreen2Cartesian(aPoint);
                Point2D.Double goePt = map.getPane().cartesian2Geo(cartPt);
                com.vividsolutions.jts.geom.Point pt = GisUtilities.createPoint(goePt);
                List<SelectionEntry> geoms = map.nonSelectableHit(pt);
                if (geoms != null && geoms.size() > 0) {
                    String hintText = map.beforeToolTipShow(geoms.get(0).getRow(), geoms.get(0).getEntity());
                    if (hintText != null && !hintText.isEmpty()) {
                        hint.setToolTipText(hintText);
                        map.getPane().add(hint, pt, 0);
                        map.getPane().repaint();
                    }
                } else {
                    map.getPane().remove(hint);
                    map.getPane().repaint();
                }
            } catch (Exception ex) {
                Logger.getLogger(MouseCursorer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
