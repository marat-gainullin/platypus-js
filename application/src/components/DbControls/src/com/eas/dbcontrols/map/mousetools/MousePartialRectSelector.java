/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.map.mousetools;

import com.eas.client.geo.GisUtilities;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.dbcontrols.map.DbMap;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 *
 * @author mg
 */
public class MousePartialRectSelector extends MouseRectSelector{

    public MousePartialRectSelector(DbMap aMap)
    {
        super(aMap);
    }

    @Override
    protected boolean isBeginDragValid() throws Exception {
        Point2D.Double cartesianPt = pane.awtScreen2Cartesian(mouseDown);
        Point2D.Double geoPt = pane.cartesian2Geo(cartesianPt);
        return !map.selectedPointHitted(GisUtilities.createPoint(geoPt));
    }

    @Override
    protected void filterHitted(Rectangle2D.Double aHitArea, List<SelectionEntry> aHitted) {
        for (int i = aHitted.size() - 1; i >= 0; i--) {
            SelectionEntry sEntry = aHitted.get(i);
            if (!aHitArea.contains(sEntry.getViewShape().getX(), sEntry.getViewShape().getY())) {
                aHitted.remove(i);
            }
        }
    }
}
