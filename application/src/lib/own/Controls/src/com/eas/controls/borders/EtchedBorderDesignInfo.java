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
public class EtchedBorderDesignInfo extends BorderDesignInfo {

    protected int etchType = 1;
    protected Color highlightColor;
    protected Color shadowColor;

    @Serial
    public int getEtchType() {
        return etchType;
    }

    @Serial
    public void setEtchType(int aValue) {
        int oldValue = etchType;
        etchType = aValue;
        firePropertyChange("etchType", oldValue, etchType);
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(Color aValue) {
        Color oldValue = highlightColor;
        highlightColor = aValue;
        firePropertyChange("highlightColor", oldValue, highlightColor);
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(Color aValue) {
        Color oldValue = shadowColor;
        shadowColor = aValue;
        firePropertyChange("shadowColor", oldValue, shadowColor);
    }

    @Serial
    public String getEtcheHighlightColor() {
        return highlightColor != null ? CascadedStyle.encodeColor(highlightColor) : null;
    }

    @Serial
    public void setEtcheHighlightColor(String aValue) {
        Color oldValue = highlightColor;
        if (aValue != null) {
            highlightColor = Color.decode(aValue);
        } else {
            highlightColor = null;
        }
        firePropertyChange("highlightColor", oldValue, highlightColor);
    }

    @Serial
    public String getEtcheShadowColor() {
        return shadowColor != null ? CascadedStyle.encodeColor(shadowColor) : null;
    }

    @Serial
    public void setEtcheShadowColor(String aValue) {
        Color oldValue = shadowColor;
        if (aValue != null) {
            shadowColor = Color.decode(aValue);
        } else {
            shadowColor = null;
        }
        firePropertyChange("shadowColor", oldValue, shadowColor);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final EtchedBorderDesignInfo other = (EtchedBorderDesignInfo) obj;
        if (etchType != other.etchType) {
            return false;
        }
        if (this.highlightColor != other.highlightColor && (this.highlightColor == null || !this.highlightColor.equals(other.highlightColor))) {
            return false;
        }
        if (this.shadowColor != other.shadowColor && (this.shadowColor == null || !this.shadowColor.equals(other.shadowColor))) {
            return false;
        }
        return true;
    }

    protected void assign(EtchedBorderDesignInfo aSource) {
        etchType = aSource.etchType;
        highlightColor = aSource.highlightColor != null ? new Color(aSource.highlightColor.getRed(), aSource.highlightColor.getGreen(), aSource.highlightColor.getBlue(), aSource.highlightColor.getAlpha()) : null;
        shadowColor = aSource.shadowColor != null ? new Color(aSource.shadowColor.getRed(), aSource.shadowColor.getGreen(), aSource.shadowColor.getBlue(), aSource.shadowColor.getAlpha()) : null;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof EtchedBorderDesignInfo) {
            assign((EtchedBorderDesignInfo) aSource);
        }
    }

    @Override
    public void accept(BorderDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
