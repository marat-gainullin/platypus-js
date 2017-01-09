/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.controls.geopane;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author mg
 */
public class TileUtils {
    
    public static Rectangle expandRectFromCell(int x, int y, int aTileSize) {
        Rectangle result = new Rectangle();
        result.x = x * aTileSize;
        result.y = y * aTileSize;
        result.width = result.height = aTileSize;
        return result;
    }

    public static Rectangle expandRectFromCell(Point ptKey, int aTileSize) {
        return expandRectFromCell(ptKey.x, ptKey.y, aTileSize);
    }

    protected static Insets calcConstraintingInsets(Rectangle aSubject, Rectangle aConstraint) {
        Insets insets = new Insets(0, 0, 0, 0);
        if (aConstraint.x > aSubject.x) {
            insets.left = aConstraint.x - aSubject.x;
        }
        if (aConstraint.y > aSubject.y) {
            insets.top = aConstraint.y - aSubject.y;
        }
        if ((aSubject.x + aSubject.width) > (aConstraint.x + aConstraint.width)) {
            insets.right = (aSubject.x + aSubject.width) - (aConstraint.x + aConstraint.width);
        }
        if ((aSubject.y + aSubject.height) > (aConstraint.y + aConstraint.height)) {
            insets.bottom = (aSubject.y + aSubject.height) - (aConstraint.y + aConstraint.height);
        }
        return insets;
    }

    public static Rectangle calcControlRect(int x, int y, Rectangle aClip, int aTileSize) {        
        Rectangle result = expandRectFromCell(x, y, aTileSize);
        Insets insets = calcConstraintingInsets(result, aClip);
        result.x += insets.left;
        result.y += insets.top;
        result.width -= insets.left + insets.right;
        result.height -= insets.top + insets.bottom;
        return result;
    }

    public static Rectangle calcImageRect(int x, int y, Rectangle aClip, int aTileSize) {
        Rectangle result = expandRectFromCell(x, y, aTileSize);
        Insets insets = calcConstraintingInsets(result, aClip);
        result.x = insets.left;
        result.y = insets.top;
        result.width -= insets.left + insets.right;
        result.height -= insets.top + insets.bottom;
        return result;
    }

    public static Rectangle2D.Double expandRectFromCell(double x, double y, double aTileSize) {
        Rectangle2D.Double result = new Rectangle2D.Double();
        result.x = x * aTileSize;
        result.y = y * aTileSize;
        result.width = result.height = aTileSize;
        return result;
    }

    protected static class DoubleInsets
    {
        double left;
        double top;
        double right;
        double bottom;
    }

    protected static DoubleInsets calcConstraintingInsets(Rectangle2D.Double aSubject, Rectangle2D.Double aConstraint) {
        DoubleInsets insets = new DoubleInsets();
        if (aConstraint.x > aSubject.x) {
            insets.left = aConstraint.x - aSubject.x;
        }
        if (aConstraint.y > aSubject.y) {
            insets.top = aConstraint.y - aSubject.y;
        }
        if ((aSubject.x + aSubject.width) > (aConstraint.x + aConstraint.width)) {
            insets.right = (aSubject.x + aSubject.width) - (aConstraint.x + aConstraint.width);
        }
        if ((aSubject.y + aSubject.height) > (aConstraint.y + aConstraint.height)) {
            insets.bottom = (aSubject.y + aSubject.height) - (aConstraint.y + aConstraint.height);
        }
        return insets;
    }

    public static Rectangle2D.Double calcControlRect(int x, int y, Rectangle2D.Double aClip, double aTileSize) {
        Rectangle2D.Double result = expandRectFromCell(x, y, aTileSize);
        DoubleInsets insets = calcConstraintingInsets(result, aClip);
        result.x += insets.left;
        result.y += insets.top;
        result.width -= insets.left + insets.right;
        result.height -= insets.top + insets.bottom;
        return result;
    }
}
