/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.spin;

import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.store.Serial;
import java.math.BigDecimal;

/**
 *
 * @author mg
 */
public class DbSpinDesignInfo extends DbControlDesignInfo {

    public static final String PROP_MAX = "max";
    public static final String PROP_MIN = "min";
    public static final String PROP_STEP = "step";
    protected Double min;
    protected Double max;
    protected double step = 1;
    protected String emptyText;

    public DbSpinDesignInfo() {
        super();
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final DbSpinDesignInfo other = (DbSpinDesignInfo) obj;
        if (this.min != other.min) {
            return false;
        }
        if (this.max != other.max) {
            return false;
        }
        if (this.step != other.step) {
            return false;
        }
        if ((this.emptyText == null) ? (other.emptyText != null) : !this.emptyText.equals(other.emptyText)) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        super.assign(aSource);
        if (aSource != null && aSource instanceof DbSpinDesignInfo) {
            DbSpinDesignInfo aInfo = (DbSpinDesignInfo) aSource;
            setMin(aInfo.getMin());
            setMax(aInfo.getMax());
            setStep(aInfo.getStep());
            setEmptyText(aInfo.getEmptyText());
        }
    }

    public Class<?> getControlClass() {
        return DbSpin.class;
    }

    public BigDecimal getMinNumber() {
        return min != -Double.MAX_VALUE ? new BigDecimal(min) : null;
    }

    public BigDecimal getMaxNumber() {
        return max != Double.MAX_VALUE ? new BigDecimal(max) : null;
    }

    @Serial
    public Double getMin() {
        return min;
    }

    @Serial
    public void setMin(Double aValue) {
        Double old = min;
        min = aValue;
        firePropertyChange(PROP_MIN, old, aValue);
    }

    @Serial
    public Double getMax() {
        return max;
    }

    @Serial
    public void setMax(Double aValue) {
        Double old = max;
        max = aValue;
        firePropertyChange(PROP_MAX, old, aValue);
    }

    @Serial
    public double getStep() {
        return step;
    }

    @Serial
    public void setStep(double aValue) {
        double old = step;
        step = aValue;
        firePropertyChange(PROP_STEP, old, aValue);
    }

    @Serial
    public String getEmptyText() {
        return emptyText;
    }

    @Serial
    public void setEmptyText(String aValue) {
        String oldValue = emptyText;
        emptyText = aValue;
        firePropertyChange("emptyText", oldValue, emptyText);
    }

    @Override
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
