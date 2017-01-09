/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.mousetools;

import com.eas.client.controls.geopane.JGeoPane;
import com.eas.client.controls.geopane.actions.ZoomInAction;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author mg
 */
public class RectZoomer extends GeoPaneTool {

    protected Point mouseDown;

    public RectZoomer(JGeoPane aPane) {
        super(aPane);
    }

    protected boolean isBeginDragValid() throws Exception {
        return true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            try {
                mouseDown = e.getPoint();
                if (!isBeginDragValid()) {
                    mouseDown = null;
                }
            } catch (Exception ex) {
                Logger.getLogger(RectZoomer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (mouseDown != null) {
                try {
                    double coef = 1;
                    Point ptSource = mouseDown;
                    Point ptDest = e.getPoint();
                    if (!ptSource.equals(ptDest)) {
                        if (Math.abs(ptDest.x - ptSource.x) > 5 || Math.abs(ptDest.y - ptSource.y) > 5) {
                            Point ptCenter = new Point((ptSource.x + ptDest.x) / 2, (ptSource.y + ptDest.y) / 2);
                            if (ptSource.x < ptDest.x
                                    && ptSource.y < ptDest.y) {
                                Dimension size = pane.getSize();
                                double sx = (double) size.width / ((double) ptDest.x - (double) ptSource.x);
                                double sy = (double) size.height / ((double) ptDest.y - (double) ptSource.y);
                                coef = Math.min(sx, sy);
                                coef = pane.snapScale(coef);
                            } else if (ptSource.x > ptDest.x
                                    && ptSource.y > ptDest.y) {
                                coef = 1 / ZoomInAction.DEFAULT_ZOOM_FACTOR_STEP;
                            }
                            Point2D.Double screenCenterBefore = pane.awtScreen2Cartesian(ptCenter);
                            pane.scaleView(coef, coef, false);
                            Point2D.Double screenCenterAfter = pane.awtScreen2Cartesian(ptCenter);
                            pane.translateView(screenCenterAfter.x - screenCenterBefore.x, screenCenterAfter.y - screenCenterBefore.y, true);
                        }
                        pane.repaint();
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

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseDown != null) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                Rectangle selRect = new Rectangle();
                selRect.x = Math.min(e.getPoint().x, mouseDown.x);
                selRect.y = Math.min(e.getPoint().y, mouseDown.y);
                selRect.width = Math.abs(e.getPoint().x - mouseDown.x);
                selRect.height = Math.abs(e.getPoint().y - mouseDown.y);
                Dimension size = pane.getSize();
                selRect.translate(-size.width / 2, -size.height / 2);
                pane.setSelectionRectangle(selRect);
                pane.repaint();
            }
        }
    }

    @Override
    public boolean isCapable(MouseToolCapability aCapability) {
        return aCapability == MouseToolCapability.BUTTONS
                || aCapability == MouseToolCapability.MOTION;
    }
}
