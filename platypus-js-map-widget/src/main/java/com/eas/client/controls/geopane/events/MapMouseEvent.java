/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.events;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.vividsolutions.jts.geom.Geometry;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class MapMouseEvent implements HasPublished {

    protected MouseEvent awtEvent;
    protected Geometry cartesianPoint;
    protected Geometry geoPoint;
    //
    protected JSObject published;

    public MapMouseEvent(MouseEvent aAwtEvent, Geometry aCartesianPoint, Geometry aGeoPoint) {
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

    public boolean isLeftButtton() {
        return SwingUtilities.isLeftMouseButton(awtEvent);
    }

    public boolean isRightButtton() {
        return SwingUtilities.isRightMouseButton(awtEvent);
    }

    public boolean isMiddleButtton() {
        return SwingUtilities.isMiddleMouseButton(awtEvent);
    }

    public boolean isControlDown() {
        return awtEvent.isControlDown();
    }

    public boolean isShiftDown() {
        return awtEvent.isShiftDown();
    }

    @Override
    public JSObject getPublished() {
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }
}
