/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.events;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.geom.AffineTransform;

/**
 * Event class holding information about scale change of the viewpoint transformation.
 * It holds information about new viewpoint transformation and scale on x and y axis that have lead to
 * such transformation.
 * @author mg
 */
public class ViewpointScaledEvent extends ViewpointChangedEvent {

    protected double scaleX;
    protected double scaleY;

    public ViewpointScaledEvent(AffineTransform aTransform, double aScaleX, double aScaleY, Geometry aAreaOfInterest, Geometry aCartesianAreaOfInterest) {
        super(aTransform, aAreaOfInterest, aCartesianAreaOfInterest);
        scaleX = aScaleX;
        scaleY = aScaleY;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }
}
