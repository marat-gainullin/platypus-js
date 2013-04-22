/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts.constraints;

import com.eas.controls.DesignInfo;
import com.eas.controls.layouts.margin.Margin;
import com.eas.store.Serial;

/**
 *
 * @author lkolesnikov
 */
public class MarginConstraintsDesignInfo extends LayoutConstraintsDesignInfo {

    public static final String UNIT_PX = "px";
    public static final String UNIT_PCT = "%";
    //
    private Margin left;
    private Margin top;
    private Margin right;
    private Margin bottom;
    private Margin width;        // ширина компонента
    private Margin height;       // высота 

    public MarginConstraintsDesignInfo() {
        super();
    }

    public Margin getMarginBottom() {
        return bottom;
    }

    public void setMarginBottom(Margin aValue) {
        Margin oldValue = bottom;
        bottom = aValue;
        firePropertyChange("bottom", oldValue, bottom);
    }

    public Margin getMarginHeight() {
        return height;
    }

    public void setMarginHeight(Margin aValue) {
        Margin oldValue = height;
        height = aValue;
        firePropertyChange("height", oldValue, height);
    }

    public Margin getMarginLeft() {
        return left;
    }

    public void setMarginLeft(Margin aValue) {
        Margin oldValue = left;
        left = aValue;
        firePropertyChange("left", oldValue, left);
    }

    public Margin getMarginRight() {
        return right;
    }

    public void setMarginRight(Margin aValue) {
        Margin oldValue = right;
        right = aValue;
        firePropertyChange("right", oldValue, right);
    }

    public Margin getMarginTop() {
        return top;
    }

    public void setMarginTop(Margin aValue) {
        Margin oldValue = top;
        top = aValue;
        firePropertyChange("top", oldValue, top);
    }

    public Margin getMarginWidth() {
        return width;
    }

    public void setMarginWidth(Margin aValue) {
        Margin oldValue = width;
        width = aValue;
        firePropertyChange("width", oldValue, width);
    }

    //********************************************************
    public static Margin parseMargin(String aValue) {
        if (aValue != null && !aValue.trim().isEmpty()) {
            aValue = aValue.trim();
            if (aValue.endsWith("px")) {
                String val = aValue.substring(0, aValue.length() - 2);
                return new Margin(Integer.parseInt(val), true);
            } else if (aValue.endsWith("%")) {
                String val = aValue.substring(0, aValue.length() - 1);
                return new Margin(Integer.parseInt(val), false);
            } else {
                return new Margin(Integer.parseInt(aValue), true);
            }
        }
        return null;
    }
    
    @Serial
    public String getLeft() {
        return left != null ? left.toString() : null;
    }

    @Serial
    public void setLeft(String aValue) {
        left = parseMargin(aValue);
    }

    @Serial
    public String getTop() {
        return top != null ? top.toString() : null;
    }

    @Serial
    public void setTop(String aValue) {
        top = parseMargin(aValue);
    }

    @Serial
    public String getRight() {
        return right != null ? right.toString() : null;
    }

    @Serial
    public void setRight(String aValue) {
        right = parseMargin(aValue);
    }

    @Serial
    public String getBottom() {
        return bottom != null ? bottom.toString() : null;
    }

    @Serial
    public void setBottom(String aValue) {
        bottom = parseMargin(aValue);
    }

    @Serial
    public String getHeight() {
        return height != null ? height.toString() : null;
    }

    @Serial
    public void setHeight(String aValue) {
        height = parseMargin(aValue);
    }

    @Serial
    public String getWidth() {
        return width != null ? width.toString() : null;
    }

    @Serial
    public void setWidth(String aValue) {
        width = parseMargin(aValue);
    }

    @Override
    public void accept(ConstraintsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public boolean isEqual(Object obj) {
        if(!(obj instanceof MarginConstraintsDesignInfo)) {
            return false;
        }
        if (!super.isEqual(obj)) {
            return false;
        }
        final MarginConstraintsDesignInfo other = (MarginConstraintsDesignInfo) obj;
        
        if (left != other.left && (left == null || !left.isEqual(other.left))) {
            return false;
        }
        if (top != other.top && (top == null || !top.isEqual(other.top))) {
            return false;
        }
        if (right != other.right && (right == null || !right.isEqual(other.right))) {
            return false;
        }
        if (bottom != other.bottom && (bottom == null || !bottom.isEqual(other.bottom))) {
            return false;
        }
        if (width != other.width && (width == null || !width.isEqual(other.width))) {
            return false;
        }
        if (height != other.height && (height == null || !height.isEqual(other.height))) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof MarginConstraintsDesignInfo) {
            MarginConstraintsDesignInfo source = (MarginConstraintsDesignInfo) aSource;
            left = source.left != null ? new Margin(source.left.value, source.left.absolute) : null;
            top = source.top != null ? new Margin(source.top.value, source.top.absolute) : null;
            right = source.right != null ? new Margin(source.right.value, source.right.absolute) : null;
            bottom = source.bottom != null ? new Margin(source.bottom.value, source.bottom.absolute) : null;
            width = source.width != null ? new Margin(source.width.value, source.width.absolute) : null;
            height = source.height != null ? new Margin(source.height.value, source.height.absolute) : null;
        }
    }
}
