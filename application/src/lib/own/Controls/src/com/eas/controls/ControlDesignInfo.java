/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls;

import com.eas.controls.borders.BorderDesignInfo;
import com.eas.controls.borders.BorderPropertyFactory;
import com.eas.controls.layouts.constraints.*;
import com.eas.gui.CascadedStyle;
import com.eas.store.ClassedSerial;
import com.eas.store.PropertiesSimpleFactory;
import com.eas.store.Serial;
import com.eas.store.SerialFont;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public abstract class ControlDesignInfo extends DesignInfo implements PropertiesSimpleFactory {

    public static final String FONT = "font";
    // identification
    protected String name;
    // references
    protected String parent;
    protected String componentPopupMenu;
    protected String nextFocusableComponent;
    // data
    protected boolean autoscrolls = true;
    protected Color background;
    protected Color foreground;
    protected boolean enabled = true;
    protected boolean focusable = true;
    protected Font font;
    protected Dimension designedPreferredSize;
    protected Dimension minimumSize;
    protected Dimension preferredSize;
    protected Dimension maximumSize;
    protected boolean opaque = true;
    protected String toolTipText;
    protected int cursor = Cursor.DEFAULT_CURSOR;
    protected boolean visible = true;
    protected BorderDesignInfo border;
    // layout constraints
    protected LayoutConstraintsDesignInfo constraints;

    public ControlDesignInfo() {
        super();
    }

    public abstract void accept(ControlsDesignInfoVisitor aVisitor);

    @Serial
    public String getName() {
        return name;
    }

    @Serial
    public void setName(String aValue) {
        String oldValue = name;
        name = aValue;
        firePropertyChange("name", oldValue, name);
    }

    @Serial
    public String getParent() {
        return parent;
    }

    @Serial
    public void setParent(String aValue) {
        String oldValue = parent;
        parent = aValue;
        firePropertyChange("parent", oldValue, parent);
    }

    @ClassedSerial(propertyClassHint = "type")
    public LayoutConstraintsDesignInfo getConstraints() {
        return constraints;
    }

    @ClassedSerial(propertyClassHint = "type")
    public void setConstraints(LayoutConstraintsDesignInfo aValue) {
        LayoutConstraintsDesignInfo oldValue = constraints;
        constraints = aValue;
        firePropertyChange("constraints", oldValue, constraints);
    }

    @Serial
    public boolean isAutoscrolls() {
        return autoscrolls;
    }

    @Serial
    public void setAutoscrolls(boolean aValue) {
        boolean oldValue = autoscrolls;
        autoscrolls = aValue;
        firePropertyChange("autoscrolls", oldValue, autoscrolls);
    }

    @Serial
    public String getBackgroundColor() {
        return background != null ? CascadedStyle.encodeColor(background) : null;
    }

    @Serial
    public void setBackgroundColor(String aValue) {
        Color oldValue = background;
        if (aValue != null) {
            background = Color.decode(aValue);
        } else {
            background = null;
        }
        firePropertyChange("background", oldValue, background);
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color aValue) {
        Color oldValue = background;
        background = aValue;
        firePropertyChange("background", oldValue, background);
    }

    @Serial
    public String getForegroundColor() {
        return foreground != null ? CascadedStyle.encodeColor(foreground) : null;
    }

    @Serial
    public void setForegroundColor(String aValue) {
        try {
            Color oldValue = foreground;
            if (aValue != null) {
                foreground = Color.decode(aValue);
            } else {
                foreground = null;
            }
            firePropertyChange("foreground", oldValue, foreground);
        } catch (NumberFormatException ex) {
            Logger.getLogger(ControlDesignInfo.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color aValue) {
        Color oldValue = foreground;
        foreground = aValue;
        firePropertyChange("foreground", oldValue, foreground);
    }

    @Serial
    public boolean isEnabled() {
        return enabled;
    }

    @Serial
    public void setEnabled(boolean aValue) {
        boolean oldValue = enabled;
        enabled = aValue;
        firePropertyChange("enabled", oldValue, enabled);
    }

    @Serial
    public boolean isFocusable() {
        return focusable;
    }

    @Serial
    public void setFocusable(boolean aValue) {
        boolean oldValue = focusable;
        focusable = aValue;
        firePropertyChange("focusable", oldValue, focusable);
    }

    @Serial
    public SerialFont getEasFont() {
        return font != null ? new SerialFont(font) : null;
    }

    @Serial
    public void setEasFont(SerialFont aValue) {
        Font oldValue = font;
        if (aValue != null) {
            font = aValue.getDelegate();
        } else {
            font = null;
        }
        firePropertyChange("font", oldValue, font);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font aValue) {
        Font oldValue = font;
        font = aValue;
        firePropertyChange("font", oldValue, aValue);
    }

    @Serial
    public String getComponentPopupMenu() {
        return componentPopupMenu;
    }

    @Serial
    public void setComponentPopupMenu(String aValue) {
        String oldValue = componentPopupMenu;
        componentPopupMenu = aValue;
        firePropertyChange("componentPopupMenu", oldValue, componentPopupMenu);
    }

    @Serial
    public String getNextFocusableComponent() {
        return nextFocusableComponent;
    }

    @Serial
    public void setNextFocusableComponent(String aValue) {
        String oldValue = nextFocusableComponent;
        nextFocusableComponent = aValue;
        firePropertyChange("nextFocusableComponent", oldValue, nextFocusableComponent);
    }

    @Serial
    public SerialDimension getEasMinimumSize() {
        return minimumSize != null ? new SerialDimension(minimumSize) : null;
    }

    @Serial
    public void setEasMinimumSize(SerialDimension aValue) {
        Dimension oldValue = minimumSize;
        if (aValue != null) {
            minimumSize = aValue.getDelegate();
        } else {
            minimumSize = null;
        }
        firePropertyChange("minimumSize", oldValue, minimumSize);
    }

    public Dimension getMinimumSize() {
        return minimumSize;
    }

    public void setMinimumSize(Dimension aValue) {
        Dimension oldValue = minimumSize;
        minimumSize = aValue;
        firePropertyChange("minimumSize", oldValue, minimumSize);
    }

    @Serial
    public String getPrefWidth() {
        return designedPreferredSize != null ? designedPreferredSize.width + "px" : null;
    }

    @Serial
    public void setPrefWidth(String aValue) {
        if (designedPreferredSize == null) {
            designedPreferredSize = new Dimension();
        }
        if (aValue.length() > 2 && aValue.endsWith("px")) {
            designedPreferredSize.width = Integer.parseInt(aValue.substring(0, aValue.length() - 2));
        }
    }

    @Serial
    public String getPrefHeight() {
        return designedPreferredSize != null ? designedPreferredSize.height + "px" : null;
    }

    @Serial
    public void setPrefHeight(String aValue) {
        if (designedPreferredSize == null) {
            designedPreferredSize = new Dimension();
        }
        if (aValue.length() > 2 && aValue.endsWith("px")) {
            designedPreferredSize.height = Integer.parseInt(aValue.substring(0, aValue.length() - 2));
        }
    }

    public Dimension getDesignedPreferredSize() {
        return designedPreferredSize;
    }

    public void setDesignedPreferredSize(Dimension aValue) {
        designedPreferredSize = aValue;
    }

    @Serial
    public SerialDimension getEasPreferredSize() {
        return preferredSize != null ? new SerialDimension(preferredSize) : null;
    }

    @Serial
    public void setEasPreferredSize(SerialDimension aValue) {
        Dimension oldValue = preferredSize;
        if (aValue != null) {
            preferredSize = aValue.getDelegate();
        } else {
            preferredSize = null;
        }
        firePropertyChange("preferredSize", oldValue, preferredSize);
    }

    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public void setPreferredSize(Dimension aValue) {
        Dimension oldValue = preferredSize;
        preferredSize = aValue;
        firePropertyChange("preferredSize", oldValue, preferredSize);
    }

    @Serial
    public SerialDimension getEasMaximumSize() {
        return maximumSize != null ? new SerialDimension(maximumSize) : null;
    }

    @Serial
    public void setEasMaximumSize(SerialDimension aValue) {
        Dimension oldValue = maximumSize;
        if (aValue != null) {
            maximumSize = aValue.getDelegate();
        } else {
            maximumSize = null;
        }
        firePropertyChange("maximumSize", oldValue, maximumSize);
    }

    public Dimension getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(Dimension aValue) {
        Dimension oldValue = maximumSize;
        maximumSize = aValue;
        firePropertyChange("maximumSize", oldValue, maximumSize);
    }

    @Serial
    public boolean isOpaque() {
        return opaque;
    }

    @Serial
    public void setOpaque(boolean aValue) {
        boolean oldValue = opaque;
        opaque = aValue;
        firePropertyChange("opaque", oldValue, opaque);
    }

    @Serial
    public String getToolTipText() {
        return toolTipText;
    }

    @Serial
    public void setToolTipText(String aValue) {
        String oldValue = toolTipText;
        toolTipText = aValue;
        firePropertyChange("toolTipText", oldValue, toolTipText);
    }

    @Serial
    public int getCursor() {
        return cursor;
    }

    @Serial
    public void setCursor(int aValue) {
        int oldValue = cursor;
        cursor = aValue;
        firePropertyChange("cursor", oldValue, cursor);
    }

    @Serial
    public boolean isVisible() {
        return visible;
    }

    @Serial
    public void setVisible(boolean aValue) {
        boolean oldValue = visible;
        visible = aValue;
        firePropertyChange("visible", oldValue, visible);
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

    @Override
    public boolean isEqual(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ControlDesignInfo other = (ControlDesignInfo) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.parent == null) ? (other.parent != null) : !this.parent.equals(other.parent)) {
            return false;
        }
        if (this.autoscrolls != other.autoscrolls) {
            return false;
        }
        if (this.background != other.background && (this.background == null || !this.background.equals(other.background))) {
            return false;
        }
        if (this.foreground != other.foreground && (this.foreground == null || !this.foreground.equals(other.foreground))) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.focusable != other.focusable) {
            return false;
        }
        if (this.font != other.font && (this.font == null || !this.font.equals(other.font))) {
            return false;
        }
        if ((this.nextFocusableComponent == null) ? (other.nextFocusableComponent != null) : !this.nextFocusableComponent.equals(other.nextFocusableComponent)) {
            return false;
        }
        if ((this.componentPopupMenu == null) ? (other.componentPopupMenu != null) : !this.componentPopupMenu.equals(other.componentPopupMenu)) {
            return false;
        }
        if (this.minimumSize != other.minimumSize && (this.minimumSize == null || !this.minimumSize.equals(other.minimumSize))) {
            return false;
        }
        if (this.preferredSize != other.preferredSize && (this.preferredSize == null || !this.preferredSize.equals(other.preferredSize))) {
            return false;
        }
        if (this.maximumSize != other.maximumSize && (this.maximumSize == null || !this.maximumSize.equals(other.maximumSize))) {
            return false;
        }
        if (this.opaque != other.opaque) {
            return false;
        }
        if ((this.toolTipText == null) ? (other.toolTipText != null) : !this.toolTipText.equals(other.toolTipText)) {
            return false;
        }
        if (this.cursor != other.cursor) {
            return false;
        }
        if (this.visible != other.visible) {
            return false;
        }
        if (this.border != other.border && (this.border == null || !this.border.isEqual(other.border))) {
            return false;
        }
        if ((this.constraints == null) ? (other.constraints != null) : !this.constraints.isEqual(other.constraints)) {
            return false;
        }
        return true;
    }

    protected void assign(ControlDesignInfo aSource) {
        if (aSource != null) {
            name = aSource.name != null ? new String(aSource.name.toCharArray()) : null;
            parent = aSource.parent != null ? new String(aSource.parent.toCharArray()) : null;
            autoscrolls = aSource.autoscrolls;
            background = aSource.background != null ? new Color(aSource.background.getRed(), aSource.background.getGreen(), aSource.background.getBlue(), aSource.background.getAlpha()) : null;
            foreground = aSource.foreground != null ? new Color(aSource.foreground.getRed(), aSource.foreground.getGreen(), aSource.foreground.getBlue(), aSource.foreground.getAlpha()) : null;
            enabled = aSource.enabled;
            focusable = aSource.focusable;
            setFont((aSource.getFont() != null ? aSource.getFont().deriveFont(aSource.getFont().getStyle(), (float) aSource.getFont().getSize()) : null));
            componentPopupMenu = aSource.componentPopupMenu != null ? new String(aSource.componentPopupMenu.toCharArray()) : null;
            nextFocusableComponent = aSource.nextFocusableComponent != null ? new String(aSource.nextFocusableComponent.toCharArray()) : null;
            minimumSize = aSource.minimumSize != null ? new Dimension(aSource.minimumSize.width, aSource.minimumSize.height) : null;
            preferredSize = aSource.preferredSize != null ? new Dimension(aSource.preferredSize.width, aSource.preferredSize.height) : null;
            maximumSize = aSource.maximumSize != null ? new Dimension(aSource.maximumSize.width, aSource.maximumSize.height) : null;
            opaque = aSource.opaque;
            toolTipText = aSource.toolTipText != null ? new String(aSource.toolTipText.toCharArray()) : null;
            cursor = aSource.cursor;
            visible = aSource.visible;
            border = aSource.border != null ? (BorderDesignInfo) aSource.border.copy() : null;
            constraints = aSource.constraints != null ? (LayoutConstraintsDesignInfo) aSource.constraints.copy() : null;
        }
    }

    @Override
    public void assign(DesignInfo aValue) {
        if (aValue instanceof ControlDesignInfo) {
            assign((ControlDesignInfo) aValue);
        }
    }

    @Override
    public Object createPropertyObjectInstance(String aSimpleClassName) {
        if (AbsoluteConstraintsDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new AbsoluteConstraintsDesignInfo();
        } else if (MarginConstraintsDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new MarginConstraintsDesignInfo();
        } else if (BorderLayoutConstraintsDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new BorderLayoutConstraintsDesignInfo();
        } else if (CardLayoutConstraintsDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new CardLayoutConstraintsDesignInfo();
        } else if (GridBagLayoutConstraintsDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new GridBagLayoutConstraintsDesignInfo();
        } else if (LayersLayoutConstraintsDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new LayersLayoutConstraintsDesignInfo();
        } else if (TabsConstraintsDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new TabsConstraintsDesignInfo();
        } else {
            Object maybeBorder = (new BorderPropertyFactory()).createPropertyObjectInstance(aSimpleClassName);
            if (maybeBorder != null) {
                return maybeBorder;
            } else {
                Logger.getLogger(ControlDesignInfo.class.getName()).severe(String.format("Unknown constraints or border type occured: %s", aSimpleClassName));
                return null;
            }
        }
    }
}
