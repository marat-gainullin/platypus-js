/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts.margin;

/**
 *
 * @author lkolesnikov
 */
public class Margin {

    public int value;
    public boolean absolute = true; //true - точки, false - проценты

    public Margin() {
        super();
    }

    public Margin(int aValue, boolean aAbsolute) {
        this();
        value = aValue;
        absolute = aAbsolute;
    }

    public Margin copy() {
        return new Margin(value, absolute);
    }

    public boolean isEqual(Object that) {
        if (that == null) {
            return false;
        }
        if (this == that) {
            return true;
        }
        Margin other = (Margin) that;
        return other.value == value && other.absolute == absolute;
    }

    @Override
    public String toString() {
        String res = String.valueOf(value);
        return absolute ? res + "px" : res + "%";
    }

    public int calcPlainValue(int aScale) {
        if (absolute) {
            return value;
        } else {
            return Math.round(value / 100.0f * aScale);
        }
    }

    public void setPlainValue(int aValue, int aScale) {
        if (absolute) {
            value = aValue;
        } else {
            float k = (float) aValue / (float) aScale;
            value = Math.round(k * 100);
        }
    }
}
