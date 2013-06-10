/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.filter.FunctionExpressionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.filter.capability.FunctionName;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author AB
 */
public class FunctionEndArrowAngle extends FunctionExpressionImpl {

    public static FunctionName NAME = new FunctionNameImpl("endArrowAngle", Double.class,
            FunctionNameImpl.parameter("linestring", LineString.class),
            FunctionNameImpl.parameter("wkt", String.class));

    public FunctionEndArrowAngle() {
        super(NAME);
    }

    @Override
    public Object evaluate(Object feature) {
        LineString ls;
        MathTransform mathTrans;
        try { // attempt to get value and perform conversion
            ls = (LineString) getExpression(0).evaluate(feature, LineString.class);
            MathTransformFactory mathFactory = ReferencingFactoryFinder.getMathTransformFactory(null);
            mathTrans = mathFactory.createFromWKT(getExpression(1).toString());

        } catch (FactoryRegistryException | FactoryException e) {
            // probably a type error
            throw new IllegalArgumentException(
                    "Filter Function problem for function endPoint argument #0 - expected type Geometry");
        }
        assert mathTrans instanceof MathTransform2D;
        MathTransform2D math = (MathTransform2D) mathTrans;
        if (ls == null || ls.getNumPoints() == 1) {
            return null;
        }

        CoordinateSequence cs = ls.getCoordinateSequence();
        Point2D.Double last = new Point2D.Double(cs.getX(cs.size() - 1), cs.getY(cs.size() - 1));
        Point2D.Double prev = new Point2D.Double(cs.getX(cs.size() - 2), cs.getY(cs.size() - 2));
        Point2D.Double pt;
        Point2D.Double cartLast;
        Point2D.Double cartPrev;
        try {
            pt = new Point2D.Double();
            cartLast = (Point2D.Double) math.transform(last, pt);
            pt = new Point2D.Double();
            cartPrev = (Point2D.Double) math.transform(prev, pt);
            double dx = (cartLast.getX() - cartPrev.getX());
            double dy = (cartLast.getY() - cartPrev.getY());

            return Math.toDegrees(Math.atan2(dy, dx));
        } catch (TransformException ex) {
            Logger.getLogger(FunctionEndArrowAngle.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
