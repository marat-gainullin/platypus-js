/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.store.Serial;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.DesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;

/**
 *
 * @author mg
 */
public class SliderDesignInfo extends ControlDesignInfo {

    protected int extent;
    protected boolean inverted;
    protected int majorTickSpacing;
    protected int maximum = 100;
    protected int minimum;
    protected int minorTickSpacing;
    protected int orientation;
    protected boolean paintLabels;
    protected boolean paintTicks;
    protected boolean paintTrack = true;
    protected boolean snapToTicks;
    protected int value;

    public SliderDesignInfo()
    {
        super();
    }

    @Serial
    public int getExtent() {
        return extent;
    }

    @Serial
    public void setExtent(int aValue) {
        int oldValue = extent;
        extent = aValue;
        firePropertyChange("extent", oldValue, extent);
    }

    @Serial
    public boolean isInverted() {
        return inverted;
    }

    @Serial
    public void setInverted(boolean aValue) {
        boolean oldValue = inverted;
        inverted = aValue;
        firePropertyChange("inverted", oldValue, inverted);
    }

    @Serial
    public int getMajorTickSpacing() {
        return majorTickSpacing;
    }

    @Serial
    public void setMajorTickSpacing(int aValue) {
        int oldValue = majorTickSpacing;
        majorTickSpacing = aValue;
        firePropertyChange("majorTickSpacing", oldValue, majorTickSpacing);
    }

    @Serial
    public int getMaximum() {
        return maximum;
    }

    @Serial
    public void setMaximum(int aValue) {
        int oldValue = maximum;
        maximum = aValue;
        firePropertyChange("maximum", oldValue, maximum);
    }

    @Serial
    public int getMinimum() {
        return minimum;
    }

    @Serial
    public void setMinimum(int aValue) {
        int oldValue = minimum;
        minimum = aValue;
        firePropertyChange("minimum", oldValue, minimum);
    }

    @Serial
    public int getMinorTickSpacing() {
        return minorTickSpacing;
    }

    @Serial
    public void setMinorTickSpacing(int aValue) {
        int oldValue = minorTickSpacing;
        minorTickSpacing = aValue;
        firePropertyChange("minorTickSpacing", oldValue, minorTickSpacing);
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
    public boolean isPaintLabels() {
        return paintLabels;
    }

    @Serial
    public void setPaintLabels(boolean aValue) {
        boolean oldValue = paintLabels;
        paintLabels = aValue;
        firePropertyChange("paintLabels", oldValue, paintLabels);
    }

    @Serial
    public boolean isPaintTicks() {
        return paintTicks;
    }

    @Serial
    public void setPaintTicks(boolean aValue) {
        boolean oldValue = paintTicks;
        paintTicks = aValue;
        firePropertyChange("paintTicks", oldValue, paintTicks);
    }

    @Serial
    public boolean isPaintTrack() {
        return paintTrack;
    }

    @Serial
    public void setPaintTrack(boolean aValue) {
        boolean oldValue = paintTrack;
        paintTrack = aValue;
        firePropertyChange("paintTrack", oldValue, paintTrack);
    }

    @Serial
    public boolean isSnapToTicks() {
        return snapToTicks;
    }

    @Serial
    public void setSnapToTicks(boolean aValue) {
        boolean oldValue = snapToTicks;
        snapToTicks = aValue;
        firePropertyChange("snapToTicks", oldValue, snapToTicks);
    }

    @Serial
    public int getValue() {
        return value;
    }

    @Serial
    public void setValue(int aValue) {
        int oldValue = value;
        value = aValue;
        firePropertyChange("value", oldValue, value);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        SliderDesignInfo other = (SliderDesignInfo) obj;
        if (this.extent != other.extent) {
            return false;
        }
        if (this.inverted != other.inverted) {
            return false;
        }
        if (this.majorTickSpacing != other.majorTickSpacing) {
            return false;
        }
        if (this.maximum != other.maximum) {
            return false;
        }
        if (this.minimum != other.minimum) {
            return false;
        }
        if (this.minorTickSpacing != other.minorTickSpacing) {
            return false;
        }
        if (this.orientation != other.orientation) {
            return false;
        }
        if (this.paintLabels != other.paintLabels) {
            return false;
        }
        if (this.paintTicks != other.paintTicks) {
            return false;
        }
        if (this.paintTrack != other.paintTrack) {
            return false;
        }
        if (this.snapToTicks != other.snapToTicks) {
            return false;
        }
        if (this.value != other.value) {
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
        if (aValue instanceof SliderDesignInfo) {
            SliderDesignInfo source = (SliderDesignInfo) aValue;
            extent = source.extent;
            inverted = source.inverted;
            majorTickSpacing = source.majorTickSpacing;
            maximum = source.maximum;
            minimum = source.minimum;
            minorTickSpacing = source.minorTickSpacing;
            orientation = source.orientation;
            paintLabels = source.paintLabels;
            paintTicks = source.paintTicks;
            paintTrack = source.paintTrack;
            snapToTicks = source.snapToTicks;
            value = source.value;
        }
    }
}
