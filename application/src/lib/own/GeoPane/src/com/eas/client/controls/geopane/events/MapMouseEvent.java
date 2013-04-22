/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.controls.geopane.events;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 *
 * @author mg
 */
public class MapMouseEvent {

    protected MouseEvent awtEvent;
    protected Geometry cartesianPoint;
    protected Geometry geoPoint;

    public MapMouseEvent(MouseEvent aAwtEvent, Geometry aCartesianPoint, Geometry aGeoPoint)
    {
        super();
        awtEvent = aAwtEvent;
        cartesianPoint = aCartesianPoint;
        geoPoint = aGeoPoint;
    }

    public Point getAwtScreenPoint() {
        return awtEvent.getPoint();
    }

    public Geometry getCartesianPoint() {
        return cartesianPoint;
    }

    public Geometry getGeoPoint() {
        return geoPoint;
    }

    public boolean isLeftButtton()
    {
        return SwingUtilities.isLeftMouseButton(awtEvent);
    }

    public boolean isRightButtton()
    {
        return SwingUtilities.isRightMouseButton(awtEvent);
    }

    public boolean isMiddleButtton()
    {
        return SwingUtilities.isMiddleMouseButton(awtEvent);
    }

    public boolean isControlDown()
    {
        return awtEvent.isControlDown();
    }

    public boolean isShiftDown()
    {
        return awtEvent.isShiftDown();
    }
}
