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
public class MatteBorderDesignInfo extends EmptyBorderDesignInfo {

    protected Color matteColor = Color.black;
    protected String tileIcon;

    public Color getMatteColor() {
        return matteColor;
    }

    public void setMatteColor(Color aValue) {
        Color oldValue = matteColor;
        matteColor = aValue;
        firePropertyChange("matteColor", oldValue, matteColor);
    }

    @Serial
    public String getBorderMatteColor() {
        return matteColor != null ? CascadedStyle.encodeColor(matteColor) : null;
    }

    @Serial
    public void setBorderMatteColor(String aValue) {
        Color oldValue = matteColor;
        if (aValue != null) {
            matteColor = Color.decode(aValue);
        } else {
            matteColor = null;
        }
        firePropertyChange("mattecolor", oldValue, matteColor);
    }

    @Serial
    public String getTileIcon() {
        return tileIcon;
    }

    @Serial
    public void setTileIcon(String aValue) {
        String oldValue = tileIcon;
        tileIcon = aValue;
        firePropertyChange("tileIcon", oldValue, tileIcon);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final MatteBorderDesignInfo other = (MatteBorderDesignInfo) obj;
        if (this.matteColor != other.matteColor && (this.matteColor == null || !this.matteColor.equals(other.matteColor))) {
            return false;
        }
        if ((this.tileIcon == null) ? (other.tileIcon != null) : !this.tileIcon.equals(other.tileIcon)) {
            return false;
        }
        return true;
    }

    protected void assign(MatteBorderDesignInfo aSource) {
        matteColor = aSource.matteColor != null ? new Color(aSource.matteColor.getRed(), aSource.matteColor.getGreen(), aSource.matteColor.getBlue(), aSource.matteColor.getAlpha()) : null;
        tileIcon = aSource.tileIcon != null ? new String(aSource.tileIcon.toCharArray()) : null;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof MatteBorderDesignInfo) {
            assign((MatteBorderDesignInfo) aSource);
        }
    }

    @Override
    public void accept(BorderDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }    
}
