/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

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
        Point pB = GisUtilities.createPoint(10, 10);
        Point pE = GisUtilities.createPoint(10, 20);
        Polygon pl = GisUtilities.createPolygon(pB, pE);
        pl = GisUtilities.addPoint2Polygon(pl, GisUtilities.createPoint(20, 20));
        pl = GisUtilities.addPoint2Polygon(pl, GisUtilities.createPoint(20, 10));
        Point p = GisUtilities.createPoint(15, 15);
        assertTrue("Not contains", pl.contains(p));
    }
}
