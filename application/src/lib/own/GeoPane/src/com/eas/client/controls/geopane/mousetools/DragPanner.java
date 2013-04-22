/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.mousetools;

import com.eas.client.controls.geopane.Cursors;
import com.eas.client.controls.geopane.JGeoPane;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author mg
 */
public class DragPanner extends GeoPaneTool {

    public DragPanner(JGeoPane aPane) {
        super(aPane);
    }
    protected Point mouseDown;
    protected Point mousePrev;

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            mouseDown = e.getPoint();
            mousePrev = mouseDown;
            pane.setCursor(Cursors.PAN);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        try {
            if (SwingUtilities.isRightMouseButton(e)) {
                pane.translateGrid(0, 0, true);
                mouseDown = null;
            }
        } catch (Exception ex) {
            Logger.getLogger(DragPanner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (SwingUtilities.isRightMouseButton(e)) {
            try {
                assert mousePrev != null;
                pane.setCursor(Cursors.PAN);
                Point mouseCurrent = e.getPoint();
                pane.translateGrid(mouseCurrent.x - mousePrev.x, mouseCurrent.y - mousePrev.y, false);
                mousePrev = e.getPoint();
                pane.repaint();
            } catch (Exception ex) {
                Logger.getLogger(DragPanner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isCapable(MouseToolCapability aCapability) {
        return aCapability == MouseToolCapability.MOTION
                || aCapability == MouseToolCapability.BUTTONS;
    }
}
