/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.mousetools;

import com.eas.client.controls.geopane.mousetools.GeoPaneTool;
import com.eas.dbcontrols.map.DbMap;

/**
 *
 * @author mg
 */
public abstract class MapTool extends GeoPaneTool {

    protected DbMap map;

    public MapTool(DbMap aMap) {
        super(aMap.getPane());
        map = aMap;
    }

}
