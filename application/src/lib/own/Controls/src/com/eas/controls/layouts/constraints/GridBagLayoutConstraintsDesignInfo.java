/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts.constraints;

import com.eas.store.Serial;
import com.eas.controls.DesignInfo;
import com.eas.controls.SerialInsets;
import java.awt.Insets;

/**
 *
 * @author mg
 */
public class GridBagLayoutConstraintsDesignInfo extends LayoutConstraintsDesignInfo {

    protected int anchor;
    protected int fill;
    protected int gridheight;
    protected int gridwidth;
    protected int gridx;
    protected int gridy;
    protected Insets insets;
    protected int ipadx;
    protected int ipady;
    protected double weightx;
    protected double weighty;

    public GridBagLayoutConstraintsDesignInfo()
    {
        super();
    }

    @Serial
    public int getAnchor() {
        return anchor;
    }

    @Serial
    public void setAnchor(int aValue) {
        int oldValue = anchor;
        anchor = aValue;
        firePropertyChange("anchor", oldValue, anchor);
    }

    @Serial
    public int getFill() {
        return fill;
    }

    @Serial
    public void setFill(int aValue) {
        int oldValue = fill;
        fill = aValue;
        firePropertyChange("fill", oldValue, fill);
    }

    @Serial
    public int getGridheight() {
        return gridheight;
    }

    @Serial
    public void setGridheight(int aValue) {
        int oldValue = gridheight;
        gridheight = aValue;
        firePropertyChange("gridheight", oldValue, gridheight);
    }

    @Serial
    public int getGridwidth() {
        return gridwidth;
    }

    @Serial
    public void setGridwidth(int aValue) {
        int oldValue = gridwidth;
        gridwidth = aValue;
        firePropertyChange("gridwidth", oldValue, gridwidth);
    }

    @Serial
    public int getGridx() {
        return gridx;
    }

    @Serial
    public void setGridx(int aValue) {
        int oldValue = gridx;
        gridx = aValue;
        firePropertyChange("gridx", oldValue, gridx);
    }

    @Serial
    public int getGridy() {
        return gridy;
    }

    @Serial
    public void setGridy(int aValue) {
        int oldValue = gridy;
        gridy = aValue;
        firePropertyChange("gridy", oldValue, gridy);
    }

    public Insets getInsets() {
        return insets;
    }

    public void setInsets(Insets aValue) {
        Insets oldValue = insets;
        insets = aValue;
        firePropertyChange("insets", oldValue, insets);
    }

    @Serial
    public SerialInsets getEasInsets() {
        return insets != null ? new SerialInsets(insets) : null;
    }

    @Serial
    public void setEasInsets(SerialInsets aValue) {
        Insets oldValue = insets;
        if (aValue != null) {
            insets = aValue.getDelegate();
        } else {
            insets = null;
        }
        firePropertyChange("insets", oldValue, insets);
    }

    @Serial
    public int getIpadx() {
        return ipadx;
    }

    @Serial
    public void setIpadx(int aValue) {
        int oldValue = ipadx;
        ipadx = aValue;
        firePropertyChange("ipadx", oldValue, ipadx);
    }

    @Serial
    public int getIpady() {
        return ipady;
    }

    @Serial
    public void setIpady(int aValue) {
        int oldValue = ipady;
        ipady = aValue;
        firePropertyChange("ipady", oldValue, ipady);
    }

    @Serial
    public double getWeightx() {
        return weightx;
    }

    @Serial
    public void setWeightx(double aValue) {
        double oldValue = weightx;
        weightx = aValue;
        firePropertyChange("weightx", oldValue, weightx);
    }

    @Serial
    public double getWeighty() {
        return weighty;
    }

    @Serial
    public void setWeighty(double aValue) {
        double oldValue = weighty;
        weighty = aValue;
        firePropertyChange("weighty", oldValue, weighty);
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
        final GridBagLayoutConstraintsDesignInfo other = (GridBagLayoutConstraintsDesignInfo) obj;
        if (this.anchor != other.anchor) {
            return false;
        }
        if (this.fill != other.fill) {
            return false;
        }
        if (this.gridheight != other.gridheight) {
            return false;
        }
        if (this.gridwidth != other.gridwidth) {
            return false;
        }
        if (this.gridx != other.gridx) {
            return false;
        }
        if (this.gridy != other.gridy) {
            return false;
        }
        if (this.insets != other.insets && (this.insets == null || !this.insets.equals(other.insets))) {
            return false;
        }
        if (this.ipadx != other.ipadx) {
            return false;
        }
        if (this.ipady != other.ipady) {
            return false;
        }
        if (Double.doubleToLongBits(this.weightx) != Double.doubleToLongBits(other.weightx)) {
            return false;
        }
        if (Double.doubleToLongBits(this.weighty) != Double.doubleToLongBits(other.weighty)) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof GridBagLayoutConstraintsDesignInfo) {
            GridBagLayoutConstraintsDesignInfo source = (GridBagLayoutConstraintsDesignInfo) aSource;
            anchor = source.anchor;
            fill = source.fill;
            gridheight = source.gridheight;
            gridwidth = source.gridwidth;
            gridx = source.gridx;
            gridy = source.gridy;
            insets = source.insets != null ? new Insets(source.insets.top, source.insets.left, source.insets.bottom, source.insets.right) : null;
            ipadx = source.ipadx;
            ipady = source.ipady;
            weightx = source.weightx;
            weighty = source.weighty;
        }
    }
}
