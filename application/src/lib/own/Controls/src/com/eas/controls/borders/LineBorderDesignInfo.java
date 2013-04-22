/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.borders;

import com.eas.controls.DesignInfo;
import com.eas.gui.CascadedStyle;
import com.eas.store.Serial;
import java.awt.Color;

/**
 *
 * @author mg
 */
public class LineBorderDesignInfo extends BorderDesignInfo {

    protected int thickness = 1;
    protected Color lineColor = Color.black;
    protected boolean roundedCorners;

    @Serial
    public int getThickness() {
        return thickness;
    }

    @Serial
    public void setThickness(int aValue) {
        int oldValue = thickness;
        thickness = aValue;
        firePropertyChange("thickness", oldValue, thickness);
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color aValue) {
        Color oldValue = lineColor;
        lineColor = aValue;
        firePropertyChange("lineColor", oldValue, lineColor);
    }

    @Serial
    public String getBorderLineColor() {
        return lineColor != null ? CascadedStyle.encodeColor(lineColor) : null;
    }

    @Serial
    public void setBorderLineColor(String aValue) {
        Color oldValue = lineColor;
        if (aValue != null) {
            lineColor = Color.decode(aValue);
        } else {
            lineColor = null;
        }
        firePropertyChange("lineColor", oldValue, lineColor);
    }

    @Serial
    public boolean isRoundedCorners() {
        return roundedCorners;
    }

    @Serial
    public void setRoundedCorners(boolean aValue) {
        boolean oldValue = roundedCorners;
        roundedCorners = aValue;
        firePropertyChange("roundedCorners", oldValue, roundedCorners);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final LineBorderDesignInfo other = (LineBorderDesignInfo) obj;
        if (thickness != other.thickness) {
            return false;
        }
        if (this.lineColor != other.lineColor && (this.lineColor == null || !this.lineColor.equals(other.lineColor))) {
            return false;
        }
        if (roundedCorners != other.roundedCorners) {
            return false;
        }
        return true;
    }

    protected void assign(LineBorderDesignInfo aSource) {
        thickness = aSource.thickness;
        lineColor = aSource.lineColor != null ? new Color(aSource.lineColor.getRed(), aSource.lineColor.getGreen(), aSource.lineColor.getBlue(), aSource.lineColor.getAlpha()) : null;
        roundedCorners = aSource.roundedCorners;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof LineBorderDesignInfo) {
            assign((LineBorderDesignInfo) aSource);
        }
    }

    @Override
    public void accept(BorderDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
    
}
