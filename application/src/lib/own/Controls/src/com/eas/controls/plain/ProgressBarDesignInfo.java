/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.controls.ControlDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class ProgressBarDesignInfo extends ControlDesignInfo {

    protected boolean borderPainted = true;
    protected boolean indeterminate;
    protected int maximum = 100;
    protected int minimum;
    protected int orientation;
    protected String string;
    protected boolean stringPainted;
    protected int value;

    public ProgressBarDesignInfo()
    {
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
    public boolean isIndeterminate() {
        return indeterminate;
    }

    @Serial
    public void setIndeterminate(boolean aValue) {
        boolean oldValue = indeterminate;
        indeterminate = aValue;
        firePropertyChange("indeterminate", oldValue, indeterminate);
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
    public String getString() {
        return string;
    }

    @Serial
    public void setString(String aValue) {
        String oldValue = string;
        string = aValue;
        firePropertyChange("string", oldValue, string);
    }

    @Serial
    public boolean isStringPainted() {
        return stringPainted;
    }

    @Serial
    public void setStringPainted(boolean aValue) {
        boolean oldValue = stringPainted;
        stringPainted = aValue;
        firePropertyChange("stringPainted", oldValue, stringPainted);
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
        ProgressBarDesignInfo other = (ProgressBarDesignInfo) obj;
        if (this.borderPainted != other.borderPainted) {
            return false;
        }
        if (this.indeterminate != other.indeterminate) {
            return false;
        }
        if (this.maximum != other.maximum) {
            return false;
        }
        if (this.minimum != other.minimum) {
            return false;
        }
        if (this.orientation != other.orientation) {
            return false;
        }
        if ((this.string == null) ? (other.string != null) : !this.string.equals(other.string)) {
            return false;
        }
        if (this.stringPainted != other.stringPainted) {
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
        if (aValue instanceof ProgressBarDesignInfo) {
            ProgressBarDesignInfo source = (ProgressBarDesignInfo) aValue;
            borderPainted = source.borderPainted;
            indeterminate = source.indeterminate;
            maximum = source.maximum;
            minimum = source.minimum;
            orientation = source.orientation;
            string = source.string != null ? new String(source.string.toCharArray()) : null;
            stringPainted = source.stringPainted;
            value = source.value;
        }
    }
}
