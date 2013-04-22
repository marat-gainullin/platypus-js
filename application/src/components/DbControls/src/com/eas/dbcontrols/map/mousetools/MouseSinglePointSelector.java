/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.mousetools;

import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.dbcontrols.map.DbMap;
import com.vividsolutions.jts.geom.Point;
import java.util.List;

/**
 *
 * @author mg
 */
public class MouseSinglePointSelector extends MousePointSelector {

    public MouseSinglePointSelector(DbMap aMap) {
        super(aMap);
    }

    @Override
    protected void filterHitted(Point aHitPoint, List<SelectionEntry> aHitted) {
        if (!aHitted.isEmpty()) {
            double distance = Double.MAX_VALUE;
            SelectionEntry resEntry = null;
            for (SelectionEntry sEntry : aHitted) {
                double lDistance = sEntry.getViewShape().distance(aHitPoint);
                if (lDistance < distance) {
                    distance = lDistance;
                    resEntry = sEntry;
                }
            }
            assert resEntry != null;
            aHitted.clear();
            aHitted.add(resEntry);
        }
    }
}
