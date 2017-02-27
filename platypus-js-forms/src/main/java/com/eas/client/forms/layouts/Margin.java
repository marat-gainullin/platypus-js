/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.layouts;

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
    
    public static Margin parse(String aValue) {
        if (aValue != null && !aValue.trim().isEmpty()) {
            aValue = aValue.trim();
            if (aValue.endsWith("px")) {
                String val = aValue.substring(0, aValue.length() - 2);
                return new Margin(Integer.parseInt(val), true);
            } else if (aValue.endsWith("%")) {
                String val = aValue.substring(0, aValue.length() - 1);
                return new Margin(Integer.parseInt(val), false);
            } else {
                return new Margin(Integer.parseInt(aValue), true);
            }
        }
        return null;
    }
    
}
