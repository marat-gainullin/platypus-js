/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.containers;

import com.eas.controls.ContainerDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;
import javax.swing.JSplitPane;

/**
 *
 * @author mg
 */
public class SplitDesignInfo extends ContainerDesignInfo {

    protected double resizeWeight;
    protected boolean continuousLayout;
    protected int dividerLocation = 84;
    protected boolean oneTouchExpandable;
    protected int orientation = JSplitPane.HORIZONTAL_SPLIT;
    protected int dividerSize = 5;
    protected String leftComponent;
    protected String rightComponent;

    public SplitDesignInfo() {
        super();
    }

    @Serial
    public double getResizeWeight() {
        return resizeWeight;
    }

    @Serial
    public void setResizeWeight(double aValue) {
        double oldValue = resizeWeight;
        resizeWeight = aValue;
        firePropertyChange("resizeWeight", oldValue, resizeWeight);
    }

    @Serial
    public boolean isContinuousLayout() {
        return continuousLayout;
    }

    @Serial
    public void setContinuousLayout(boolean aValue) {
        boolean oldValue = continuousLayout;
        continuousLayout = aValue;
        firePropertyChange("continuousLayout", oldValue, continuousLayout);
    }

    @Serial
    public int getDividerLocation() {
        return dividerLocation;
    }

    @Serial
    public void setDividerLocation(int aValue) {
        int oldValue = dividerLocation;
        dividerLocation = aValue;
        firePropertyChange("dividerLocation", oldValue, dividerLocation);
    }

    @Serial
    public boolean isOneTouchExpandable() {
        return oneTouchExpandable;
    }

    @Serial
    public void setOneTouchExpandable(boolean aValue) {
        boolean oldValue = oneTouchExpandable;
        oneTouchExpandable = aValue;
        firePropertyChange("oneTouchExpandable", oldValue, oneTouchExpandable);
    }

    @Serial
    public int getOrientation() {
        return orientation;
    }

    @Serial
    public void setOrientation(int aValue) {
        int oldValue = orientation;
        orientation = aValue;
        firePropertyChange("orientation", oldValue, orientation);
    }

    @Serial
    public int getDividerSize() {
        return dividerSize;
    }

    @Serial
    public void setDividerSize(int aValue) {
        int oldValue = dividerSize;
        dividerSize = aValue;
        firePropertyChange("dividerSize", oldValue, dividerSize);
    }

    @Serial
    public String getLeftComponent() {
        return leftComponent;
    }

    @Serial
    public void setLeftComponent(String aValue) {
        String oldValue = leftComponent;
        leftComponent = aValue;
        firePropertyChange("leftComponent", oldValue, leftComponent);
    }

    @Serial
    public String getRightComponent() {
        return rightComponent;
    }

    @Serial
    public void setRightComponent(String aValue) {
        String oldValue = rightComponent;
        rightComponent = aValue;
        firePropertyChange("rightComponent", oldValue, rightComponent);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final SplitDesignInfo other = (SplitDesignInfo) obj;
        if (Math.abs(resizeWeight - other.resizeWeight) < 1e-10) {
            return false;
        }
        if (dividerLocation != other.dividerLocation) {
            return false;
        }
        if (continuousLayout != other.continuousLayout) {
            return false;
        }
        if (oneTouchExpandable != other.oneTouchExpandable) {
            return false;
        }
        if (orientation != other.orientation) {
            return false;
        }
        if (dividerSize != other.dividerSize) {
            return false;
        }
        if ((this.leftComponent == null) ? (other.leftComponent != null) : !this.leftComponent.equals(other.leftComponent)) {
            return false;
        }
        if ((this.rightComponent == null) ? (other.rightComponent != null) : !this.rightComponent.equals(other.rightComponent)) {
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
        if (aValue instanceof SplitDesignInfo) {
            SplitDesignInfo source = (SplitDesignInfo) aValue;
            resizeWeight = source.resizeWeight;
            continuousLayout = source.continuousLayout;
            dividerLocation = source.dividerLocation;
            oneTouchExpandable = source.oneTouchExpandable;
            orientation = source.orientation;
            dividerSize = source.dividerSize;
            leftComponent = source.leftComponent != null ? new String(source.leftComponent.toCharArray()) : null;
            rightComponent = source.rightComponent != null ? new String(source.rightComponent.toCharArray()) : null;
        }
    }
}
