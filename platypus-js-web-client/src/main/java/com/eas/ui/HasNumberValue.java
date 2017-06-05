package com.eas.ui;

/**
 *
 * @author mgainullin
 */
public interface HasNumberValue {

    public void increment();

    public void decrement();

    public Double getMin();

    public void setMin(Double aValue);

    public Double getMax();

    public void setMax(Double aValue);

    public Double getStep();

    public void setStep(Double aValue);
}
