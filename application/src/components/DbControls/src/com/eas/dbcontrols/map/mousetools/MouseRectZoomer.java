/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.map.mousetools;

import com.eas.client.controls.geopane.mousetools.RectZoomer;
import com.eas.client.geo.GisUtilities;
import com.eas.dbcontrols.map.DbMap;
import java.awt.geom.Point2D;

/**
 *
 * @author mg
 */
public class MouseRectZoomer extends RectZoomer{

    protected DbMap map;

    public MouseRectZoomer(DbMap aMap) {
        super(aMap.getPane());
        map = aMap;
    }

    @Override
    protected boolean isBeginDragValid() throws Exception {
        Point2D.Double cartesianPt = pane.awtScreen2Cartesian(mouseDown);
        Point2D.Double geoPt = pane.cartesian2Geo(cartesianPt);
        return !map.selectedGeometryHitted(GisUtilities.createPoint(geoPt));
    }

}
