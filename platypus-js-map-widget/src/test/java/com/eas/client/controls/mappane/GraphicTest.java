/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.controls.mappane;

import java.awt.Point;
import java.awt.Polygon;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class GraphicTest {

    public static Point[] points = {new Point(0, 0), new Point(10, -8), new Point(5, -20), new Point(13, 25)};
    public static Polygon[] polies = {new Polygon(new int[]{-10,-20,-30}, new int[]{-21,32,16}, 3), new Polygon(new int[]{22,4,34,21}, new int[]{17,8,12,6}, 4)};

    @Test
    public void dummyTest()
    {
    }
}
