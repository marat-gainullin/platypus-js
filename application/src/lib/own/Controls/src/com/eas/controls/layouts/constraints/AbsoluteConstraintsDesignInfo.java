/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.controls.layouts.constraints;

import com.eas.controls.DesignInfo;
import com.eas.controls.SerialDimension;
import com.eas.controls.SerialPoint;
import com.eas.store.Serial;
import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author mg
 */
public class AbsoluteConstraintsDesignInfo extends LayoutConstraintsDesignInfo{

    protected Point location;
    protected Dimension size;

    @Serial
    public SerialPoint getEasLocation() {
        return location != null ? new SerialPoint(location) : null;
    }

    @Serial
    public void setEasLocation(SerialPoint aValue) {
        Point oldValue = location;
        if (aValue != null) {
            location = aValue.getDelegate();
        } else {
            location = null;
        }
        firePropertyChange("location", oldValue, location);
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point aValue) {
        Point oldValue = location;
        location = aValue;
        firePropertyChange("location", oldValue, location);
    }

    @Serial
    public SerialDimension getEasSize() {
        return size != null ? new SerialDimension(size) : null;
    }

    @Serial
    public void setEasSize(SerialDimension aValue) {
        Dimension oldValue = size;
        if (aValue != null) {
            size = aValue.getDelegate();
        } else {
            size = null;
        }
        firePropertyChange("size", oldValue, size);
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension aValue) {
        Dimension oldValue = size;
        size = aValue;
        firePropertyChange("size", oldValue, size);
    }

    @Override
    public void accept(ConstraintsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        AbsoluteConstraintsDesignInfo other = (AbsoluteConstraintsDesignInfo)obj;
        if (this.location != other.location && (this.location == null || !this.location.equals(other.location))) {
            return false;
        }
        if (this.size != other.size && (this.size == null || !this.size.equals(other.size))) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof AbsoluteConstraintsDesignInfo) {
            AbsoluteConstraintsDesignInfo source = (AbsoluteConstraintsDesignInfo) aSource;
            location = source.location != null ? new Point(source.location.x, source.location.y) : null;
            size = source.size != null ? new Dimension(source.size.width, source.size.height) : null;
        }
    }
}
