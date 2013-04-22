/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.mousetools;

import com.eas.client.controls.geopane.Cursors;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.dbcontrols.map.DbMap;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author mg
 */
public class MouseGeometriesMover extends MousePointsMover {

    public MouseGeometriesMover(DbMap aMap) {
        super(aMap);
    }

    @Override
    protected List<SelectionEntry> obtainSelection2Move() throws Exception {
        if (map.selectedGeometryHitted(geoPt)) {
            List<SelectionEntry> points = map.hitSelection(geoPt);
            if (points != null && !points.isEmpty()) {
                return points;
            } else {
                return map.getSelection().getSelection();
            }
        } else {
            return null;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (SwingUtilities.isLeftMouseButton(e)) {
            try {
                if (map.selectedGeometryHitted(geoPt)) {
                    if (map.hitSelection(geoPt).isEmpty()) {
                        pane.setCursor(Cursors.PAN);
                    } else {
                        pane.setCursor(Cursors.PAN_POINT);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(MouseGeometriesMover.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
