/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.controls;

import com.eas.store.Serial;
import java.awt.Point;

/**
 *
 * @author mg
 */
public class SerialPoint {

    protected Point delegate;

    public SerialPoint() {
        delegate = new Point(0, 0);
    }

    public SerialPoint(Point aDelegate) {
        super();
        delegate = aDelegate;
    }

    public Point getDelegate() {
        return delegate;
    }

    @Serial
    public int getX()
    {
        return delegate.x;
    }

    @Serial
    public void setX(int aValue)
    {
        delegate.x = aValue;
    }

    @Serial
    public int getY()
    {
        return delegate.y;
    }

    @Serial
    public void setY(int aValue)
    {
        delegate.y = aValue;
    }
}
