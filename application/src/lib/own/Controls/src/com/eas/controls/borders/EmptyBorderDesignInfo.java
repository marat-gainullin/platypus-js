/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.borders;

import com.eas.controls.DesignInfo;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class EmptyBorderDesignInfo extends BorderDesignInfo {

    protected int left = 1, right = 1, top = 1, bottom = 1;

    @Serial
    public int getLeft() {
        return left;
    }

    @Serial
    public void setLeft(int aValue) {
        int oldValue = left;
        left = aValue;
        firePropertyChange("left", oldValue, left);
    }

    @Serial
    public int getRight() {
        return right;
    }

    @Serial
    public void setRight(int aValue) {
        int oldValue = right;
        right = aValue;
        firePropertyChange("right", oldValue, right);
    }

    @Serial
    public int getTop() {
        return top;
    }

    @Serial
    public void setTop(int aValue) {
        int oldValue = top;
        top = aValue;
        firePropertyChange("top", oldValue, top);
    }

    @Serial
    public int getBottom() {
        return bottom;
    }

    @Serial
    public void setBottom(int aValue) {
        int oldValue = bottom;
        bottom = aValue;
        firePropertyChange("bottom", oldValue, bottom);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final EmptyBorderDesignInfo other = (EmptyBorderDesignInfo) obj;
        if (left != other.left) {
            return false;
        }
        if (right != other.right) {
            return false;
        }
        if (top != other.top) {
            return false;
        }
        if (bottom != other.bottom) {
            return false;
        }
        return true;
    }

    protected void assign(EmptyBorderDesignInfo aSource) {
        left = aSource.left;
        right = aSource.right;
        top = aSource.top;
        bottom = aSource.bottom;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof EmptyBorderDesignInfo) {
            assign((EmptyBorderDesignInfo) aSource);
        }
    }

    @Override
    public void accept(BorderDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
