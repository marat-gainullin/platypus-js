/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.borders;

import com.eas.controls.DesignInfo;
import com.eas.gui.CascadedStyle;
import com.eas.store.ClassedSerial;
import com.eas.store.PropertiesSimpleFactory;
import com.eas.store.Serial;
import com.eas.store.SerialFont;
import java.awt.Color;
import java.awt.Font;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class TitledBorderDesignInfo extends BorderDesignInfo implements PropertiesSimpleFactory {

    protected String title;
    protected BorderDesignInfo border;
    protected int titlePosition;
    protected int titleJustification = 4;
    protected Font titleFont;
    protected Color titleColor;

    @Serial
    public String getTitle() {
        return title;
    }

    @Serial
    public void setTitle(String aValue) {
        String oldValue = title;
        title = aValue;
        firePropertyChange("title", oldValue, title);
    }

    @ClassedSerial(propertyClassHint = "type")
    public BorderDesignInfo getBorder() {
        return border;
    }

    @ClassedSerial(propertyClassHint = "type")
    public void setBorder(BorderDesignInfo aValue) {
        BorderDesignInfo oldValue = border;
        border = aValue;
        firePropertyChange("border", oldValue, border);
    }

    @Serial
    public int getTitlePosition() {
        return titlePosition;
    }

    @Serial
    public void setTitlePosition(int aValue) {
        int oldValue = titlePosition;
        titlePosition = aValue;
        firePropertyChange("titlePosition", oldValue, titlePosition);
    }

    @Serial
    public int getTitleJustification() {
        return titleJustification;
    }

    @Serial
    public void setTitleJustification(int aValue) {
        int oldValue = titleJustification;
        titleJustification = aValue;
        firePropertyChange("titleJustification", oldValue, titleJustification);
    }

    public Font getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(Font aValue) {
        Font oldValue = titleFont;
        titleFont = aValue;
        firePropertyChange("titleFont", oldValue, titleFont);
    }

    @Serial
    public SerialFont getEasTitleFont() {
        return titleFont != null ? new SerialFont(titleFont) : null;
    }

    @Serial
    public void setEasTitleFont(SerialFont aValue) {
        Font oldValue = titleFont;
        if (aValue != null) {
            titleFont = aValue.getDelegate();
        } else {
            titleFont = null;
        }
        firePropertyChange("titleFont", oldValue, titleFont);
    }

    public Color getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(Color aValue) {
        Color oldValue = titleColor;
        titleColor = aValue;
        firePropertyChange("titleColor", oldValue, titleColor);
    }

    @Serial
    public String getBorderTitleColor() {
        return titleColor != null ? CascadedStyle.encodeColor(titleColor) : null;
    }

    @Serial
    public void setBorderTitleColor(String aValue) {
        Color oldValue = titleColor;
        if (aValue != null) {
            titleColor = Color.decode(aValue);
        } else {
            titleColor = null;
        }
        firePropertyChange("titleColor", oldValue, titleColor);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final TitledBorderDesignInfo other = (TitledBorderDesignInfo) obj;
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if (!border.isEqual(other.border)) {
            return false;
        }
        if (titlePosition != other.titlePosition) {
            return false;
        }
        if (titleJustification != other.titleJustification) {
            return false;
        }
        if (this.titleFont != other.titleFont && (this.titleFont == null || !this.titleFont.equals(other.titleFont))) {
            return false;
        }
        if (this.titleColor != other.titleColor && (this.titleColor == null || !this.titleColor.equals(other.titleColor))) {
            return false;
        }
        return true;
    }

    protected void assign(TitledBorderDesignInfo aSource) {
        title = aSource.title != null ? new String(aSource.title.toCharArray()) : null;
        border = aSource.border != null ? (BorderDesignInfo) aSource.border.copy() : null;
        titlePosition = aSource.titlePosition;
        titleJustification = aSource.titleJustification;
        setTitleFont((aSource.getTitleFont() != null ? aSource.getTitleFont().deriveFont(aSource.getTitleFont().getStyle(), (float) aSource.getTitleFont().getSize()) : null));
        titleColor = aSource.titleColor != null ? new Color(aSource.titleColor.getRed(), aSource.titleColor.getGreen(), aSource.titleColor.getBlue(), aSource.titleColor.getAlpha()) : null;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof TitledBorderDesignInfo) {
            assign((TitledBorderDesignInfo) aSource);
        }
    }

    @Override
    public void accept(BorderDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public Object createPropertyObjectInstance(String aSimpleClassName) {
        Object maybeBorder = (new BorderPropertyFactory()).createPropertyObjectInstance(aSimpleClassName);
        if (maybeBorder != null) {
            return maybeBorder;
        } else {
            Logger.getLogger(TitledBorderDesignInfo.class.getName()).severe(String.format("Unknown border type occured: %s", aSimpleClassName));
            return null;
        }
    }
}
