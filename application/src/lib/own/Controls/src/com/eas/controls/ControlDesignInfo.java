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
    // events
    protected String mouseClicked;
    protected String mousePressed;
    protected String mouseReleased;
    protected String mouseEntered;
    protected String mouseExited;
    protected String mouseWheelMoved;
    protected String mouseDragged;
    protected String mouseMoved;
    protected String keyTyped;
    protected String keyPressed;
    protected String keyReleased;
    protected String stateChanged;
    protected String componentResized;
    protected String componentMoved;
    protected String componentShown;
    protected String componentHidden;
    protected String componentAdded;
    protected String componentRemoved;
    protected String itemStateChanged;
    protected String actionPerformed;
    protected String focusGained;
    protected String focusLost;
    protected String propertyChange;

    public ControlDesignInfo() {
        super();
    }

    public abstract void accept(ControlsDesignInfoVisitor aVisitor);

    @Serial
    public String getMouseClicked() {
        return mouseClicked;
    }

    @Serial
    public void setMouseClicked(String aValue) {
        String oldValue = mouseClicked;
        mouseClicked = aValue;
        firePropertyChange("mouseClicked", oldValue, mouseClicked);
    }

    @Serial
    public String getMousePressed() {
        return mousePressed;
    }

    @Serial
    public void setMousePressed(String aValue) {
        String oldValue = mousePressed;
        mousePressed = aValue;
        firePropertyChange("mousePressed", oldValue, mousePressed);
    }

    @Serial
    public String getMouseReleased() {
        return mouseReleased;
    }

    @Serial
    public void setMouseReleased(String aValue) {
        String oldValue = mouseReleased;
        mouseReleased = aValue;
        firePropertyChange("mouseReleased", oldValue, mouseReleased);
    }

    @Serial
    public String getMouseEntered() {
        return mouseEntered;
    }

    @Serial
    public void setMouseEntered(String aValue) {
        String oldValue = mouseEntered;
        mouseEntered = aValue;
        firePropertyChange("mouseEntered", oldValue, mouseEntered);
    }

    @Serial
    public String getMouseExited() {
        return mouseExited;
    }

    @Serial
    public void setMouseExited(String aValue) {
        String oldValue = mouseExited;
        mouseExited = aValue;
        firePropertyChange("mouseExited", oldValue, mouseExited);
    }

    @Serial
    public String getMouseWheelMoved() {
        return mouseWheelMoved;
    }

    @Serial
    public void setMouseWheelMoved(String aValue) {
        String oldValue = mouseWheelMoved;
        mouseWheelMoved = aValue;
        firePropertyChange("mouseWheelMoved", oldValue, mouseWheelMoved);
    }

    @Serial
    public String getStateChanged() {
        return stateChanged;
    }

    @Serial
    public void setStateChanged(String aValue) {
        String oldValue = stateChanged;
        stateChanged = aValue;
        firePropertyChange("stateChanged", oldValue, stateChanged);
    }

    @Serial
    public String getComponentResized() {
        return componentResized;
    }

    @Serial
    public void setComponentResized(String aValue) {
        String oldValue = componentResized;
        componentResized = aValue;
        firePropertyChange("componentResized", oldValue, componentResized);
    }

    @Serial
    public String getComponentMoved() {
        return componentMoved;
    }

    @Serial
    public void setComponentMoved(String aValue) {
        String oldValue = componentMoved;
        componentMoved = aValue;
        firePropertyChange("componentMoved", oldValue, componentMoved);
    }

    @Serial
    public String getComponentShown() {
        return componentShown;
    }

    @Serial
    public void setComponentShown(String aValue) {
        String oldValue = componentShown;
        componentShown = aValue;
        firePropertyChange("componentShown", oldValue, componentShown);
    }

    @Serial
    public String getComponentHidden() {
        return componentHidden;
    }

    @Serial
    public void setComponentHidden(String aValue) {
        String oldValue = componentHidden;
        componentHidden = aValue;
        firePropertyChange("componentHidden", oldValue, componentHidden);
    }

    @Serial
    public String getComponentAdded() {
        return componentAdded;
    }

    @Serial
    public void setComponentAdded(String aValue) {
        String oldValue = componentAdded;
        componentAdded = aValue;
        firePropertyChange("componentAdded", oldValue, componentAdded);
    }

    @Serial
    public String getComponentRemoved() {
        return componentRemoved;
    }

    @Serial
    public void setComponentRemoved(String aValue) {
        String oldValue = componentRemoved;
        componentRemoved = aValue;
        firePropertyChange("componentRemoved", oldValue, componentRemoved);
    }

    @Serial
    public String getMouseDragged() {
        return mouseDragged;
    }

    @Serial
    public void setMouseDragged(String aValue) {
        String oldValue = mouseDragged;
        mouseDragged = aValue;
        firePropertyChange("mouseDragged", oldValue, mouseDragged);
    }

    @Serial
    public String getMouseMoved() {
        return mouseMoved;
    }

    @Serial
    public void setMouseMoved(String aValue) {
        String oldValue = mouseMoved;
        mouseMoved = aValue;
        firePropertyChange("mouseMoved", oldValue, mouseMoved);
    }

    @Serial
    public String getItemStateChanged() {
        return itemStateChanged;
    }

    @Serial
    public void setItemStateChanged(String aValue) {
        String oldValue = itemStateChanged;
        itemStateChanged = aValue;
        firePropertyChange("itemStateChanged", oldValue, itemStateChanged);
    }

    @Serial
    public String getActionPerformed() {
        return actionPerformed;
    }

    @Serial
    public void setActionPerformed(String aValue) {
        String oldValue = actionPerformed;
        actionPerformed = aValue;
        firePropertyChange("actionPerformed", oldValue, actionPerformed);
    }

    @Serial
    public String getFocusGained() {
        return focusGained;
    }

    @Serial
    public void setFocusGained(String aValue) {
        String oldValue = focusGained;
        focusGained = aValue;
        firePropertyChange("focusGained", oldValue, focusGained);
    }

    @Serial
    public String getFocusLost() {
        return focusLost;
    }

    @Serial
    public void setFocusLost(String aValue) {
        String oldValue = focusLost;
        focusLost = aValue;
        firePropertyChange("focusLost", oldValue, focusLost);
    }

    @Serial
    public String getPropertyChange() {
        return propertyChange;
    }

    @Serial
    public void setPropertyChange(String aValue) {
        String oldValue = propertyChange;
        propertyChange = aValue;
        firePropertyChange("propertyChange", oldValue, propertyChange);
    }

    @Serial
    public String getKeyTyped() {
        return keyTyped;
    }

    @Serial
    public void setKeyTyped(String aValue) {
        String oldValue = keyTyped;
        keyTyped = aValue;
        firePropertyChange("keyTyped", oldValue, keyTyped);
    }

    @Serial
    public String getKeyPressed() {
        return keyPressed;
    }

    @Serial
    public void setKeyPressed(String aValue) {
        String oldValue = keyPressed;
        keyPressed = aValue;
        firePropertyChange("keyPressed", oldValue, keyPressed);
    }

    @Serial
    public String getKeyReleased() {
        return keyReleased;
    }

    @Serial
    public void setKeyReleased(String aValue) {
        String oldValue = keyReleased;
        keyReleased = aValue;
        firePropertyChange("keyReleased", oldValue, keyReleased);
    }

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
        if ((this.mouseClicked == null) ? (other.mouseClicked != null) : !this.mouseClicked.equals(other.mouseClicked)) {
            return false;
        }
        if ((this.mousePressed == null) ? (other.mousePressed != null) : !this.mousePressed.equals(other.mousePressed)) {
            return false;
        }
        if ((this.mouseReleased == null) ? (other.mouseReleased != null) : !this.mouseReleased.equals(other.mouseReleased)) {
            return false;
        }
        if ((this.mouseEntered == null) ? (other.mouseEntered != null) : !this.mouseEntered.equals(other.mouseEntered)) {
            return false;
        }
        if ((this.mouseExited == null) ? (other.mouseExited != null) : !this.mouseExited.equals(other.mouseExited)) {
            return false;
        }
        if ((this.mouseWheelMoved == null) ? (other.mouseWheelMoved != null) : !this.mouseWheelMoved.equals(other.mouseWheelMoved)) {
            return false;
        }
        if ((this.stateChanged == null) ? (other.stateChanged != null) : !this.stateChanged.equals(other.stateChanged)) {
            return false;
        }
        if ((this.componentResized == null) ? (other.componentResized != null) : !this.componentResized.equals(other.componentResized)) {
            return false;
        }
        if ((this.componentMoved == null) ? (other.componentMoved != null) : !this.componentMoved.equals(other.componentMoved)) {
            return false;
        }
        if ((this.componentShown == null) ? (other.componentShown != null) : !this.componentShown.equals(other.componentShown)) {
            return false;
        }
        if ((this.componentHidden == null) ? (other.componentHidden != null) : !this.componentHidden.equals(other.componentHidden)) {
            return false;
        }
        if ((this.componentAdded == null) ? (other.componentAdded != null) : !this.componentAdded.equals(other.componentAdded)) {
            return false;
        }
        if ((this.componentRemoved == null) ? (other.componentRemoved != null) : !this.componentRemoved.equals(other.componentRemoved)) {
            return false;
        }
        if ((this.mouseDragged == null) ? (other.mouseDragged != null) : !this.mouseDragged.equals(other.mouseDragged)) {
            return false;
        }
        if ((this.mouseMoved == null) ? (other.mouseMoved != null) : !this.mouseMoved.equals(other.mouseMoved)) {
            return false;
        }
        if ((this.itemStateChanged == null) ? (other.itemStateChanged != null) : !this.itemStateChanged.equals(other.itemStateChanged)) {
            return false;
        }
        if ((this.actionPerformed == null) ? (other.actionPerformed != null) : !this.actionPerformed.equals(other.actionPerformed)) {
            return false;
        }
        if ((this.focusGained == null) ? (other.focusGained != null) : !this.focusGained.equals(other.focusGained)) {
            return false;
        }
        if ((this.focusLost == null) ? (other.focusLost != null) : !this.focusLost.equals(other.focusLost)) {
            return false;
        }
        if ((this.propertyChange == null) ? (other.propertyChange != null) : !this.propertyChange.equals(other.propertyChange)) {
            return false;
        }
        if ((this.keyTyped == null) ? (other.keyTyped != null) : !this.keyTyped.equals(other.keyTyped)) {
            return false;
        }
        if ((this.keyPressed == null) ? (other.keyPressed != null) : !this.keyPressed.equals(other.keyPressed)) {
            return false;
        }
        if ((this.keyReleased == null) ? (other.keyReleased != null) : !this.keyReleased.equals(other.keyReleased)) {
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
            mouseClicked = aSource.mouseClicked != null ? new String(aSource.mouseClicked.toCharArray()) : null;
            mousePressed = aSource.mousePressed != null ? new String(aSource.mousePressed.toCharArray()) : null;
            mouseReleased = aSource.mouseReleased != null ? new String(aSource.mouseReleased.toCharArray()) : null;
            mouseEntered = aSource.mouseEntered != null ? new String(aSource.mouseEntered.toCharArray()) : null;
            mouseExited = aSource.mouseExited != null ? new String(aSource.mouseExited.toCharArray()) : null;
            mouseWheelMoved = aSource.mouseWheelMoved != null ? new String(aSource.mouseWheelMoved.toCharArray()) : null;
            stateChanged = aSource.stateChanged != null ? new String(aSource.stateChanged.toCharArray()) : null;
            componentResized = aSource.componentResized != null ? new String(aSource.componentResized.toCharArray()) : null;
            componentMoved = aSource.componentMoved != null ? new String(aSource.componentMoved.toCharArray()) : null;
            componentShown = aSource.componentShown != null ? new String(aSource.componentShown.toCharArray()) : null;
            componentHidden = aSource.componentHidden != null ? new String(aSource.componentHidden.toCharArray()) : null;
            componentAdded = aSource.componentAdded != null ? new String(aSource.componentAdded.toCharArray()) : null;
            componentRemoved = aSource.componentRemoved != null ? new String(aSource.componentRemoved.toCharArray()) : null;

            mouseDragged = aSource.mouseDragged != null ? new String(aSource.mouseDragged.toCharArray()) : null;
            mouseMoved = aSource.mouseMoved != null ? new String(aSource.mouseMoved.toCharArray()) : null;
            itemStateChanged = aSource.itemStateChanged != null ? new String(aSource.itemStateChanged.toCharArray()) : null;
            actionPerformed = aSource.actionPerformed != null ? new String(aSource.actionPerformed.toCharArray()) : null;
            focusGained = aSource.focusGained != null ? new String(aSource.focusGained.toCharArray()) : null;
            focusLost = aSource.focusLost != null ? new String(aSource.focusLost.toCharArray()) : null;
            propertyChange = aSource.propertyChange != null ? new String(aSource.propertyChange.toCharArray()) : null;
            keyTyped = aSource.keyTyped != null ? new String(aSource.keyTyped.toCharArray()) : null;
            keyPressed = aSource.keyPressed != null ? new String(aSource.keyPressed.toCharArray()) : null;
            keyReleased = aSource.keyReleased != null ? new String(aSource.keyReleased.toCharArray()) : null;
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
