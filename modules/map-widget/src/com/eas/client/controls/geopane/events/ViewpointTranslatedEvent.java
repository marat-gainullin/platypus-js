/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.events;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.geom.AffineTransform;

/**
 * Event class holding information about translate change of the viewpoint transformation.
 * It holds information about new viewpoint transformation and translates on x and y axis that have lead to
 * such transformation.
 * @author mg
 */
public class ViewpointTranslatedEvent extends ViewpointChangedEvent {

    protected double dx;
    protected double dy;

    public ViewpointTranslatedEvent(AffineTransform aTransform, double aDx, double aDy, Geometry aGeoAreaOfInterest, Geometry aCartesianAreaOfInterest) {
        super(aTransform, aGeoAreaOfInterest, aCartesianAreaOfInterest);
        dx = aDx;
        dy = aDy;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }
}
