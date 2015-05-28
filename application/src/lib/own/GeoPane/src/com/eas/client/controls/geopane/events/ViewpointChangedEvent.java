/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.events;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.vividsolutions.jts.geom.Geometry;
import java.awt.geom.AffineTransform;
import jdk.nashorn.api.scripting.JSObject;

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
    protected JSObject published;

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
