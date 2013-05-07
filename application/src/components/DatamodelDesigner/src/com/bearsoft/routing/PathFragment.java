/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.routing;

import com.bearsoft.routing.graph.Vertex;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author mg
 */
public class PathFragment {

    public Rectangle rect;
    public int pastCost = Integer.MAX_VALUE;
    public int futureCost;
    public Point point;
    public Vertex<PathFragment> previous;

    public PathFragment(Rectangle aRect) {
        super();
        rect = aRect;
    }

    public void clear() {
        pastCost =  Integer.MAX_VALUE;
        futureCost = 0;
        point = null;
        previous = null;
    }
}
