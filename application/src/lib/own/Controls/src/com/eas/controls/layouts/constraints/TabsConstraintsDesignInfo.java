/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.controls.layouts.constraints;

import com.eas.controls.DesignInfo;
import com.eas.gui.CascadedStyle;
import com.eas.store.Serial;
import java.awt.Color;

/**
 *
 * @author mg
 */
public class TabsConstraintsDesignInfo extends LayoutConstraintsDesignInfo{

    // tab in tabbed pane properties
    protected String tabTitle;
    protected String tabIcon;
    protected String tabDisabledIcon;
    protected String tabTooltipText;
    protected Color tabBackground;
    protected Color tabForeground;

    @Serial
    public String getTabTitle() {
        return tabTitle;
    }

    @Serial
    public void setTabTitle(String aValue) {
        String oldValue = tabTitle;
        tabTitle = aValue;
        firePropertyChange("tabTitle", oldValue, tabTitle);
    }

    @Serial
    public String getTabIcon() {
        return tabIcon;
    }

    @Serial
    public void setTabIcon(String aValue) {
        String oldValue = tabIcon;
        tabIcon = aValue;
        firePropertyChange("tabIcon", oldValue, tabIcon);
    }

    @Serial
    public String getTabDisabledIcon() {
        return tabDisabledIcon;
    }

    @Serial
    public void setTabDisabledIcon(String aValue) {
        String oldValue = tabDisabledIcon;
        tabDisabledIcon = aValue;
        firePropertyChange("tabDisabledIcon", oldValue, tabDisabledIcon);
    }

    @Serial
    public String getTabTooltipText() {
        return tabTooltipText;
    }

    @Serial
    public void setTabTooltipText(String aValue) {
        String oldValue = tabTooltipText;
        tabTooltipText = aValue;
        firePropertyChange("tabTooltipText", oldValue, tabTooltipText);
    }

    @Serial
    public String getTabBackgroundColor() {
        return tabBackground != null ? CascadedStyle.encodeColor(tabBackground) : null;
    }

    @Serial
    public void setTabBackgroundColor(String aValue) {
        Color oldValue = tabBackground;
        if (aValue != null) {
            tabBackground = Color.decode(aValue);
        } else {
            tabBackground = null;
        }
        firePropertyChange("tabBackground", oldValue, tabBackground);
    }

    public Color getTabBackground() {
        return tabBackground;
    }

    public void setTabBackground(Color aValue) {
        Color oldValue = tabBackground;
        tabBackground = aValue;
        firePropertyChange("tabBackground", oldValue, tabBackground);
    }

    @Serial
    public String getTabForegroundColor() {
        return tabForeground != null ? CascadedStyle.encodeColor(tabForeground) : null;
    }

    @Serial
    public void setTabForegroundColor(String aValue) {
        Color oldValue = tabForeground;
        if (aValue != null) {
            tabForeground = Color.decode(aValue);
        } else {
            tabForeground = null;
        }
        firePropertyChange("tabForeground", oldValue, tabForeground);
    }

    public Color getTabForeground() {
        return tabForeground;
    }

    public void setTabForeground(Color aValue) {
        Color oldValue = tabForeground;
        tabForeground = aValue;
        firePropertyChange("tabForeground", oldValue, tabForeground);
    }

    @Override
    public void accept(ConstraintsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        TabsConstraintsDesignInfo other = (TabsConstraintsDesignInfo)obj;
        if ((this.tabTitle == null) ? (other.tabTitle != null) : !this.tabTitle.equals(other.tabTitle)) {
            return false;
        }
        if ((this.tabIcon == null) ? (other.tabIcon != null) : !this.tabIcon.equals(other.tabIcon)) {
            return false;
        }
        if ((this.tabDisabledIcon == null) ? (other.tabDisabledIcon != null) : !this.tabDisabledIcon.equals(other.tabDisabledIcon)) {
            return false;
        }
        if ((this.tabTooltipText == null) ? (other.tabTooltipText != null) : !this.tabTooltipText.equals(other.tabTooltipText)) {
            return false;
        }
        if (this.tabBackground != other.tabBackground && (this.tabBackground == null || !this.tabBackground.equals(other.tabBackground))) {
            return false;
        }
        if (this.tabForeground != other.tabForeground && (this.tabForeground == null || !this.tabForeground.equals(other.tabForeground))) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof TabsConstraintsDesignInfo) {
            TabsConstraintsDesignInfo source = (TabsConstraintsDesignInfo) aSource;
            tabTitle = source.tabTitle != null ? new String(source.tabTitle.toCharArray()) : null;
            tabIcon = source.tabIcon != null ? new String(source.tabIcon.toCharArray()) : null;
            tabDisabledIcon = source.tabDisabledIcon != null ? new String(source.tabDisabledIcon.toCharArray()) : null;
            tabTooltipText = source.tabTooltipText != null ? new String(source.tabTooltipText.toCharArray()) : null;
            tabBackground = source.tabBackground != null ? new Color(source.tabBackground.getRed(), source.tabBackground.getGreen(), source.tabBackground.getBlue(), source.tabBackground.getAlpha()) : null;
            tabForeground = source.tabForeground != null ? new Color(source.tabForeground.getRed(), source.tabForeground.getGreen(), source.tabForeground.getBlue(), source.tabForeground.getAlpha()) : null;
        }
    }
}
