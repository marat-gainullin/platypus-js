/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.events;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.event.MouseEvent;

/**
 *
 * @author mg
 */
public class MapClickedEvent extends MapMouseEvent {

    public MapClickedEvent(MouseEvent aAwtEvent, Geometry aCartesianPoint, Geometry aGeoPoint) {
        super(aAwtEvent, aCartesianPoint, aGeoPoint);
    }

    public int getClickCount() {
        return awtEvent.getClickCount();
    }
}
