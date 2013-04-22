/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.borders;

import com.eas.controls.ControlDesignInfo;
import com.eas.controls.DesignInfo;
import com.eas.gui.CascadedStyle;
import com.eas.store.Serial;
import java.awt.Color;

/**
 *
 * @author mg
 */
public class BevelBorderDesignInfo extends BorderDesignInfo {

    protected int bevelType;
    protected Color highlightOuterColor;
    protected Color highlightInnerColor;
    protected Color shadowInnerColor;
    protected Color shadowOuterColor;

    @Serial
    public int getBevelType() {
        return bevelType;
    }

    @Serial
    public void setBevelType(int aValue) {
        int oldValue = bevelType;
        bevelType = aValue;
        firePropertyChange("bevelType", oldValue, bevelType);
    }

    public Color getHighlightOuterColor() {
        return highlightOuterColor;
    }

    public void setHighlightOuterColor(Color aValue) {
        Color oldValue = highlightOuterColor;
        highlightOuterColor = aValue;
        firePropertyChange("highlightOuterColor", oldValue, highlightOuterColor);
    }

    public Color getHighlightInnerColor() {
        return highlightInnerColor;
    }

    public void setHighlightInnerColor(Color aValue) {
        Color oldValue = highlightInnerColor;
        highlightInnerColor = aValue;
        firePropertyChange("highlightInnerColor", oldValue, highlightInnerColor);
    }

    public Color getShadowInnerColor() {
        return shadowInnerColor;
    }

    public void setShadowInnerColor(Color aValue) {
        Color oldValue = shadowInnerColor;
        shadowInnerColor = aValue;
        firePropertyChange("shadowInnerColor", oldValue, shadowInnerColor);
    }

    public Color getShadowOuterColor() {
        return shadowOuterColor;
    }

    public void setShadowOuterColor(Color aValue) {
        Color oldValue = shadowOuterColor;
        shadowOuterColor = aValue;
        firePropertyChange("shadowOuterColor", oldValue, shadowOuterColor);
    }

    @Serial
    public String getHighlightBevelOuterColor() {
        return highlightOuterColor != null ? CascadedStyle.encodeColor(highlightOuterColor) : null;
    }

    @Serial
    public void setHighlightBevelOuterColor(String aValue) {
        Color oldValue = highlightOuterColor;
        if (aValue != null) {
            highlightOuterColor = Color.decode(aValue);
        } else {
            highlightOuterColor = null;
        }
        firePropertyChange("highlightOuterColor", oldValue, highlightOuterColor);
    }

    @Serial
    public String getHighlightBevelInnerColor() {
        return highlightInnerColor != null ? CascadedStyle.encodeColor(highlightInnerColor) : null;
    }

    @Serial
    public void setHighlightBevelInnerColor(String aValue) {
        Color oldValue = highlightInnerColor;
        if (aValue != null) {
            highlightInnerColor = Color.decode(aValue);
        } else {
            highlightInnerColor = null;
        }
        firePropertyChange("highlightInnerColor", oldValue, highlightInnerColor);
    }

    @Serial
    public String getShadowBevelInnerColor() {
        return shadowInnerColor != null ? CascadedStyle.encodeColor(shadowInnerColor) : null;
    }

    @Serial
    public void setShadowBevelInnerColor(String aValue) {
        Color oldValue = shadowInnerColor;
        if (aValue != null) {
            shadowInnerColor = Color.decode(aValue);
        } else {
            shadowInnerColor = null;
        }
        firePropertyChange("shadowInnerColor", oldValue, shadowInnerColor);
    }

    @Serial
    public String getShadowBevelOuterColor() {
        return shadowOuterColor != null ? CascadedStyle.encodeColor(shadowOuterColor) : null;
    }

    @Serial
    public void setShadowBevelOuterColor(String aValue) {
        Color oldValue = shadowOuterColor;
        if (aValue != null) {
            shadowOuterColor = Color.decode(aValue);
        } else {
            shadowOuterColor = null;
        }
        firePropertyChange("shadowOuterColor", oldValue, shadowOuterColor);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final BevelBorderDesignInfo other = (BevelBorderDesignInfo) obj;

        if (this.bevelType != other.bevelType) {
            return false;
        }
        if (this.highlightOuterColor != other.highlightOuterColor && (this.highlightOuterColor == null || !this.highlightOuterColor.equals(other.highlightOuterColor))) {
            return false;
        }
        if (this.highlightInnerColor != other.highlightInnerColor && (this.highlightInnerColor == null || !this.highlightInnerColor.equals(other.highlightInnerColor))) {
            return false;
        }
        if (this.shadowInnerColor != other.shadowInnerColor && (this.shadowInnerColor == null || !this.shadowInnerColor.equals(other.shadowInnerColor))) {
            return false;
        }
        if (this.shadowOuterColor != other.shadowOuterColor && (this.shadowOuterColor == null || !this.shadowOuterColor.equals(other.shadowOuterColor))) {
            return false;
        }
        return true;
    }

    protected void assign(BevelBorderDesignInfo aSource) {
        bevelType = aSource.bevelType;
        highlightOuterColor = aSource.highlightOuterColor != null ? new Color(aSource.highlightOuterColor.getRed(), aSource.highlightOuterColor.getGreen(), aSource.highlightOuterColor.getBlue(), aSource.highlightOuterColor.getAlpha()) : null;
        highlightInnerColor = aSource.highlightInnerColor != null ? new Color(aSource.highlightInnerColor.getRed(), aSource.highlightInnerColor.getGreen(), aSource.highlightInnerColor.getBlue(), aSource.highlightInnerColor.getAlpha()) : null;
        shadowInnerColor = aSource.shadowInnerColor != null ? new Color(aSource.shadowInnerColor.getRed(), aSource.shadowInnerColor.getGreen(), aSource.shadowInnerColor.getBlue(), aSource.shadowInnerColor.getAlpha()) : null;
        shadowOuterColor = aSource.shadowOuterColor != null ? new Color(aSource.shadowOuterColor.getRed(), aSource.shadowOuterColor.getGreen(), aSource.shadowOuterColor.getBlue(), aSource.shadowOuterColor.getAlpha()) : null;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof BevelBorderDesignInfo) {
            assign((BevelBorderDesignInfo) aSource);
        }
    }

    @Override
    public void accept(BorderDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
