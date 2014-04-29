/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.events;

import com.eas.script.HasPublished;
import com.vividsolutions.jts.geom.Geometry;
import java.awt.geom.AffineTransform;

/**
 * Base class to view point changes related events
 *
 * @author mg
 */
public class ViewpointChangedEvent implements HasPublished {

    protected AffineTransform newViewTransform;
    protected Geometry areaOfInterest;
    protected Geometry cartesianAreaOfInterest;
    //
    protected Object published;

    public ViewpointChangedEvent(AffineTransform aTransform, Geometry aAreaOfInterest, Geometry aCartesianAreaOfInterest) {
        super();
        newViewTransform = new AffineTransform(aTransform);
        areaOfInterest = aAreaOfInterest;
        cartesianAreaOfInterest = aCartesianAreaOfInterest;
    }

    /**
     * Returns new view point transformation. It returns a copy of real
     * transformation, and so any changes made to retruned instance will take no
     * effect.
     *
     * @return New view point transformation matrix in the foem of
     * AffineTransform instance.
     * @see AffineTransform
     */
    public AffineTransform getNewViewTransform() {
        return newViewTransform;
    }

    /**
     * Returns new area of interest.
     *
     * @return Area of interest used by GeoPane after view point transformation.
     */
    public Geometry getAreaOfInterest() {
        return areaOfInterest;
    }

    public Geometry getCartesianAreaOfInterest() {
        return cartesianAreaOfInterest;
    }

    @Override
    public Object getPublished() {
        return published;
    }

    @Override
    public void setPublished(Object aValue) {
        published = aValue;
    }
}
