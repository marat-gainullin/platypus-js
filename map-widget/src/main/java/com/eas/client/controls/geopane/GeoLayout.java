/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author mg
 */
public class GeoLayout implements LayoutManager2 {

    protected static final double PI = 180;
    protected static final double HALFPI = 90;
    
    protected static final GeometryFactory gFactory = new GeometryFactory();
    protected Map<Component, Point> layouted = new HashMap<>();

    @Override
    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        Insets insets = parent.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        return dim;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof Point) {
            Point pt = (Point) constraints;
            if(pt.getX() > PI)
                pt.getCoordinate().x = PI;
            if(pt.getX() < -PI)
                pt.getCoordinate().x = -PI;
            if(pt.getY() > HALFPI)
                pt.getCoordinate().y = HALFPI;
            if(pt.getY() < -HALFPI)
                pt.getCoordinate().y = -HALFPI;
            layouted.put(comp, pt);
        } else {
            throw new IllegalArgumentException("GeoLayout constraints should be of geotools Point class");
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        addLayoutComponent(comp, gFactory.createPoint(new Coordinate(0, 0)));
    }

    @Override
    public void invalidateLayout(Container parent) {
        // we have no information to cache. so nothing should be invalidated
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        layouted.remove(comp);
    }

    @Override
    public void layoutContainer(Container parent) {
        if (parent instanceof JGeoPane) {
            Dimension size = parent.getSize();
            JGeoPane pane = (JGeoPane) parent;
            try {
                for (Entry<Component, Point> entry : layouted.entrySet()) {
                    Component comp = entry.getKey();
                    Point point = entry.getValue();
                    Point2D.Double geoPoint = new Point2D.Double(point.getX(), point.getY());
                    try {
                        Point2D.Double cartesianPoint = pane.geo2Cartesian(geoPoint);
                        java.awt.Point screenPoint = pane.cartesian2Screen(cartesianPoint);
                        Dimension compSize = comp.getPreferredSize();
                        screenPoint.x += size.width / 2 - compSize.width / 2 - pane.getGridOffsetX();
                        screenPoint.y += size.height / 2 - compSize.height / 2 - pane.getGridOffsetY();
                        comp.setBounds(screenPoint.x, screenPoint.y, compSize.width, compSize.height);
                    } catch (TransformException ex) {
                        continue; // Some component have bad constraint, so skip it.
                    }
                }
            } catch (FactoryException ex) {
                Logger.getLogger(GeoLayout.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new IllegalArgumentException("GeoLayout can only layout JGeoPane containers");
        }
    }
}
