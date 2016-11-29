/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane;

import java.awt.Point;

/**
 * Contains boundaries in tiles space
 */
public class TilesBoundaries {

    public static final TilesBoundaries EMPTY = new TilesBoundaries(1, 1, -1, -1);

    public TilesBoundaries() {
        super();
    }

    public TilesBoundaries(int aMinX, int aMaxX, int aMinY, int aMaxY) {
        this();
        minX = aMinX;
        maxX = aMaxX;
        minY = aMinY;
        maxY = aMaxY;
    }
    public int minX;
    public int maxX;
    public int minY;
    public int maxY;

    public TilesBoundaries expanded(int aInset) {
        return new TilesBoundaries(minX - aInset, maxX + aInset, minY - aInset, maxY + aInset);
    }

    /**
     * Tests if coordinates passed in are contained in this boudaries.
     * @param x
     * @param y
     * @return True if contains, false otherwise
     */
    public boolean contains(int x, int y) {
        return minX <= x && x <= maxX && minY <= y && y < maxY;
    }

    /**
     * Tests if coordinates passed in are contained in this boudaries.
     * @param aPt
     * @return True if contains, false otherwise
     */
    public boolean contains(Point aPt) {
        return contains(aPt.x, aPt.y);
    }
}
