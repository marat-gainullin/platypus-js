/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.controls.ControlDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;
import javax.swing.SwingConstants;

/**
 *
 * @author mg
 */
public class LabelDesignInfo extends ControlDesignInfo {

    protected String icon;
    protected String text;
    protected int iconTextGap;
    protected String disabledIcon;
    protected int horizontalAlignment = SwingConstants.LEADING;
    protected int horizontalTextPosition = SwingConstants.TRAILING;
    protected int verticalAlignment = SwingConstants.CENTER;
    protected int verticalTextPosition = SwingConstants.CENTER;
    protected String labelFor;
    protected Character displayedMnemonic;
    protected int displayedMnemonicIndex;

    public LabelDesignInfo()
    {
        super();
        opaque = false;
    }
    
    @Serial
    public String getDisabledIcon() {
        return disabledIcon;
    }

    @Serial
    public void setDisabledIcon(String aValue) {
        String oldValue = disabledIcon;
        disabledIcon = aValue;
        firePropertyChange("disabledIcon", oldValue, disabledIcon);
    }

    @Serial
    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    @Serial
    public void setHorizontalAlignment(int aValue) {
        int oldValue = horizontalAlignment;
        horizontalAlignment = aValue;
        firePropertyChange("horizontalAlignment", oldValue, horizontalAlignment);
    }

    @Serial
    public int getHorizontalTextPosition() {
        return horizontalTextPosition;
    }

    @Serial
    public void setHorizontalTextPosition(int aValue) {
        int oldValue = horizontalTextPosition;
        horizontalTextPosition = aValue;
        firePropertyChange("horizontalTextPosition", oldValue, horizontalTextPosition);
    }

    @Serial
    public String getIcon() {
        return icon;
    }

    @Serial
    public void setIcon(String aValue) {
        String oldValue = icon;
        icon = aValue;
        firePropertyChange("icon", oldValue, icon);
    }

    @Serial
    public String getText() {
        return text;
    }

    @Serial
    public void setText(String aValue) {
        String oldValue = text;
        text = aValue;
        firePropertyChange("text", oldValue, text);
    }

    @Serial
    public int getIconTextGap() {
        return iconTextGap;
    }

    @Serial
    public void setIconTextGap(int aValue) {
        int oldValue = iconTextGap;
        iconTextGap = aValue;
        firePropertyChange("iconTextGap", oldValue, iconTextGap);
    }

    @Serial
    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    @Serial
    public void setVerticalAlignment(int aValue) {
        int oldValue = verticalAlignment;
        verticalAlignment = aValue;
        firePropertyChange("verticalAlignment", oldValue, verticalAlignment);
    }

    @Serial
    public int getVerticalTextPosition() {
        return verticalTextPosition;
    }

    @Serial
    public void setVerticalTextPosition(int aValue) {
        int oldValue = verticalTextPosition;
        verticalTextPosition = aValue;
        firePropertyChange("verticalTextPosition", oldValue, verticalTextPosition);
    }

    @Serial
    public String getLabelFor() {
        return labelFor;
    }

    @Serial
    public void setLabelFor(String aValue) {
        String oldValue = labelFor;
        labelFor = aValue;
        firePropertyChange("labelFor", oldValue, labelFor);
    }

    @Serial
    public Character getDisplayedMnemonic() {
        return displayedMnemonic;
    }

    @Serial
    public void setDisplayedMnemonic(Character aValue) {
        Character oldValue = displayedMnemonic;
        displayedMnemonic = aValue;
        firePropertyChange("displayedMnemonic", oldValue, displayedMnemonic);
    }

    @Serial
    public int getDisplayedMnemonicIndex() {
        return displayedMnemonicIndex;
    }

    @Serial
    public void setDisplayedMnemonicIndex(int aValue) {
        int oldValue = displayedMnemonicIndex;
        displayedMnemonicIndex = aValue;
        firePropertyChange("displayedMnemonicIndex", oldValue, displayedMnemonicIndex);
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        LabelDesignInfo other = (LabelDesignInfo) obj;
        if ((this.icon == null) ? (other.icon != null) : !this.icon.equals(other.icon)) {
            return false;
        }
        if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
            return false;
        }
        if ((this.disabledIcon == null) ? (other.disabledIcon != null) : !this.disabledIcon.equals(other.disabledIcon)) {
            return false;
        }
        if ((this.labelFor == null) ? (other.labelFor != null) : !this.labelFor.equals(other.labelFor)) {
            return false;
        }
        if (iconTextGap != other.iconTextGap) {
            return false;
        }
        if (horizontalAlignment != other.horizontalAlignment) {
            return false;
        }
        if (horizontalTextPosition != other.horizontalTextPosition) {
            return false;
        }
        if (verticalAlignment != other.verticalAlignment) {
            return false;
        }
        if (verticalTextPosition != other.verticalTextPosition) {
            return false;
        }
        if ((this.displayedMnemonic == null) ? (other.displayedMnemonic != null) : !this.displayedMnemonic.equals(other.displayedMnemonic)) {
            return false;
        }
        if (displayedMnemonicIndex != other.displayedMnemonicIndex) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aValue) {
        super.assign(aValue);
        if (aValue instanceof LabelDesignInfo) {
            LabelDesignInfo source = (LabelDesignInfo) aValue;
            displayedMnemonic = source.displayedMnemonic;
            displayedMnemonicIndex = source.displayedMnemonicIndex;
            iconTextGap = source.iconTextGap;
            horizontalAlignment = source.horizontalAlignment;
            horizontalTextPosition = source.horizontalTextPosition;
            verticalAlignment = source.verticalAlignment;
            verticalTextPosition = source.verticalTextPosition;
            icon = source.icon != null ? new String(source.icon.toCharArray()) : null;
            text = source.text != null ? new String(source.text.toCharArray()) : null;
            disabledIcon = source.disabledIcon != null ? new String(source.disabledIcon.toCharArray()) : null;
            labelFor = source.labelFor != null ? new String(source.labelFor.toCharArray()) : null;
        }
    }
}
