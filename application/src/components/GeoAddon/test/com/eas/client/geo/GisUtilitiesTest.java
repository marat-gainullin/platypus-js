/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.eas.util.gis.GeometryUtils;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author AB
 */
public class GisUtilitiesTest {
    
    public GisUtilitiesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of Polygon contains Point.
     */
    @Test
    public void isPointIn() {
        Point pB = GeometryUtils.createPoint(10, 10);
        Point pE = GeometryUtils.createPoint(10, 20);
        Polygon pl = GeometryUtils.createPolygon(pB, pE);
        pl = GeometryUtils.addPoint2Polygon(pl, GeometryUtils.createPoint(20, 20));
        pl = GeometryUtils.addPoint2Polygon(pl, GeometryUtils.createPoint(20, 10));
        Point p = GeometryUtils.createPoint(15, 15);
        assertTrue("Not contains", pl.contains(p));
    }
}
