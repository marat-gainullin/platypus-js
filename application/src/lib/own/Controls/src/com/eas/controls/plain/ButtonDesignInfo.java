/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.controls.ControlDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;
import java.awt.Insets;
import javax.swing.SwingConstants;

/**
 *
 * @author mg
 */
public class ButtonDesignInfo extends ControlDesignInfo {

    protected boolean borderPainted = true;
    protected boolean contentAreaFilled = true;
    protected boolean focusPainted = true;
    protected int iconTextGap = 4;
    protected boolean rolloverEnabled = true;
    protected boolean defaultCapable;
    protected boolean selected;
    protected Insets margin;
    protected int verticalAlignment = SwingConstants.CENTER;
    protected int verticalTextPosition = SwingConstants.CENTER;
    protected int horizontalAlignment = SwingConstants.CENTER;
    protected int horizontalTextPosition = SwingConstants.TRAILING;
    //
    protected String disabledIcon;
    protected String disabledSelectedIcon;
    protected String icon;
    protected String pressedIcon;
    protected String rolloverIcon;
    protected String rolloverSelectedIcon;
    protected String selectedIcon;
    protected String text;
    protected String buttonGroup;

    public ButtonDesignInfo() {
        super();
        autoscrolls = false;
    }

    @Serial
    public boolean isBorderPainted() {
        return borderPainted;
    }

    @Serial
    public void setBorderPainted(boolean aValue) {
        boolean oldValue = borderPainted;
        borderPainted = aValue;
        firePropertyChange("borderPainted", oldValue, borderPainted);
    }

    @Serial
    public boolean isContentAreaFilled() {
        return contentAreaFilled;
    }

    @Serial
    public void setContentAreaFilled(boolean aValue) {
        boolean oldValue = contentAreaFilled;
        contentAreaFilled = aValue;
        firePropertyChange("contentAreaFilled", oldValue, contentAreaFilled);
    }

    @Serial
    public boolean isFocusPainted() {
        return focusPainted;
    }

    @Serial
    public void setFocusPainted(boolean aValue) {
        boolean oldValue = focusPainted;
        focusPainted = aValue;
        firePropertyChange("focusPainted", oldValue, focusPainted);
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
    public boolean isRolloverEnabled() {
        return rolloverEnabled;
    }

    @Serial
    public void setRolloverEnabled(boolean aValue) {
        boolean oldValue = rolloverEnabled;
        rolloverEnabled = aValue;
        firePropertyChange("rolloverEnabled", oldValue, rolloverEnabled);
    }

    @Serial
    public boolean isDefaultCapable() {
        return defaultCapable;
    }

    @Serial
    public void setDefaultCapable(boolean aValue) {
        boolean oldValue = defaultCapable;
        defaultCapable = aValue;
        firePropertyChange("defaultCapable", oldValue, defaultCapable);
    }

    @Serial
    public boolean isSelected() {
        return selected;
    }

    @Serial
    public void setSelected(boolean aValue) {
        boolean oldValue = selected;
        selected = aValue;
        firePropertyChange("selected", oldValue, selected);
    }

    public Insets getMargin() {
        return margin;
    }

    public void setMargin(Insets aValue) {
        Insets oldValue = margin;
        margin = aValue;
        firePropertyChange("margin", oldValue, margin);
    }

    @Serial
    public String getMargins() {
        return margin != null ? String.format("top:%d left:%d bottom:%d right:%d", margin.top, margin.left, margin.bottom, margin.right) : null;
    }

    @Serial
    public void setMargins(String aValue) {
        if (aValue != null) {
            Insets value = new Insets(0, 0, 0, 0);
            String[] sValues = aValue.split("\\s+");
            if (sValues != null) {
                for (String sValue : sValues) {
                    String[] sNameVal = sValue.split(":");
                    if (sNameVal.length == 2) {
                        switch (sNameVal[0]) {
                            case "top":
                                value.top = Integer.valueOf(sNameVal[1]);
                                break;
                            case "left":
                                value.left = Integer.valueOf(sNameVal[1]);
                                break;
                            case "bottom":
                                value.bottom = Integer.valueOf(sNameVal[1]);
                                break;
                            case "right":
                                value.right = Integer.valueOf(sNameVal[1]);
                                break;
                        }
                    }
                }
            }
            setMargin(value);
        }
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
    public String getDisabledSelectedIcon() {
        return disabledSelectedIcon;
    }

    @Serial
    public void setDisabledSelectedIcon(String aValue) {
        String oldValue = disabledSelectedIcon;
        disabledSelectedIcon = aValue;
        firePropertyChange("disabledSelectedIcon", oldValue, disabledSelectedIcon);
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
    public String getPressedIcon() {
        return pressedIcon;
    }

    @Serial
    public void setPressedIcon(String aValue) {
        String oldValue = pressedIcon;
        pressedIcon = aValue;
        firePropertyChange("pressedIcon", oldValue, pressedIcon);
    }

    @Serial
    public String getRolloverIcon() {
        return rolloverIcon;
    }

    @Serial
    public void setRolloverIcon(String aValue) {
        String oldValue = rolloverIcon;
        rolloverIcon = aValue;
        firePropertyChange("rolloverIcon", oldValue, rolloverIcon);
    }

    @Serial
    public String getRolloverSelectedIcon() {
        return rolloverSelectedIcon;
    }

    @Serial
    public void setRolloverSelectedIcon(String aValue) {
        String oldValue = rolloverSelectedIcon;
        rolloverSelectedIcon = aValue;
        firePropertyChange("rolloverSelectedIcon", oldValue, rolloverSelectedIcon);
    }

    @Serial
    public String getSelectedIcon() {
        return selectedIcon;
    }

    @Serial
    public void setSelectedIcon(String aValue) {
        String oldValue = selectedIcon;
        selectedIcon = aValue;
        firePropertyChange("selectedIcon", oldValue, selectedIcon);
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
    public String getButtonGroup() {
        return buttonGroup;
    }

    @Serial
    public void setButtonGroup(String aValue) {
        String oldValue = buttonGroup;
        buttonGroup = aValue;
        firePropertyChange("buttonGroup", oldValue, buttonGroup);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        ButtonDesignInfo other = (ButtonDesignInfo) obj;
        if (borderPainted != other.borderPainted) {
            return false;
        }
        if (contentAreaFilled != other.contentAreaFilled) {
            return false;
        }
        if (focusPainted != other.focusPainted) {
            return false;
        }
        if (iconTextGap != other.iconTextGap) {
            return false;
        }
        if (rolloverEnabled != other.rolloverEnabled) {
            return false;
        }
        if (defaultCapable != other.defaultCapable) {
            return false;
        }
        if (selected != other.selected) {
            return false;
        }
        if (this.margin != other.margin && (this.margin == null || !this.margin.equals(other.margin))) {
            return false;
        }
        if (verticalAlignment != other.verticalAlignment) {
            return false;
        }
        if (verticalTextPosition != other.verticalTextPosition) {
            return false;
        }
        if (horizontalAlignment != other.horizontalAlignment) {
            return false;
        }
        if (horizontalTextPosition != other.horizontalTextPosition) {
            return false;
        }
        if ((this.disabledIcon == null) ? (other.disabledIcon != null) : !this.disabledIcon.equals(other.disabledIcon)) {
            return false;
        }
        if ((this.disabledSelectedIcon == null) ? (other.disabledSelectedIcon != null) : !this.disabledSelectedIcon.equals(other.disabledSelectedIcon)) {
            return false;
        }
        if ((this.icon == null) ? (other.icon != null) : !this.icon.equals(other.icon)) {
            return false;
        }
        if ((this.pressedIcon == null) ? (other.pressedIcon != null) : !this.pressedIcon.equals(other.pressedIcon)) {
            return false;
        }
        if ((this.rolloverIcon == null) ? (other.rolloverIcon != null) : !this.rolloverIcon.equals(other.rolloverIcon)) {
            return false;
        }
        if ((this.rolloverSelectedIcon == null) ? (other.rolloverSelectedIcon != null) : !this.rolloverSelectedIcon.equals(other.rolloverSelectedIcon)) {
            return false;
        }
        if ((this.selectedIcon == null) ? (other.selectedIcon != null) : !this.selectedIcon.equals(other.selectedIcon)) {
            return false;
        }
        if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
            return false;
        }
        if ((this.buttonGroup == null) ? (other.buttonGroup != null) : !this.buttonGroup.equals(other.buttonGroup)) {
            return false;
        }
        return true;
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public void assign(DesignInfo aValue) {
        super.assign(aValue);
        if (aValue instanceof ButtonDesignInfo) {
            ButtonDesignInfo source = (ButtonDesignInfo) aValue;
            borderPainted = source.borderPainted;
            contentAreaFilled = source.contentAreaFilled;
            focusPainted = source.focusPainted;
            iconTextGap = source.iconTextGap;
            rolloverEnabled = source.rolloverEnabled;
            defaultCapable = source.defaultCapable;
            selected = source.selected;
            margin = source.getMargin() != null ? new Insets(source.getMargin().top, source.getMargin().left, source.getMargin().bottom, source.getMargin().right) : null;
            verticalAlignment = source.verticalAlignment;
            verticalTextPosition = source.verticalTextPosition;
            horizontalAlignment = source.horizontalAlignment;
            horizontalTextPosition = source.horizontalTextPosition;

            disabledIcon = source.disabledIcon != null ? new String(source.disabledIcon.toCharArray()) : null;
            disabledSelectedIcon = source.disabledSelectedIcon != null ? new String(source.disabledSelectedIcon.toCharArray()) : null;
            icon = source.icon != null ? new String(source.icon.toCharArray()) : null;
            pressedIcon = source.pressedIcon != null ? new String(source.pressedIcon.toCharArray()) : null;
            rolloverIcon = source.rolloverIcon != null ? new String(source.rolloverIcon.toCharArray()) : null;
            rolloverSelectedIcon = source.rolloverSelectedIcon != null ? new String(source.rolloverSelectedIcon.toCharArray()) : null;
            selectedIcon = source.selectedIcon != null ? new String(source.selectedIcon.toCharArray()) : null;
            text = source.text != null ? new String(source.text.toCharArray()) : null;
            buttonGroup = source.buttonGroup != null ? new String(source.buttonGroup.toCharArray()) : null;
        }
    }
}
