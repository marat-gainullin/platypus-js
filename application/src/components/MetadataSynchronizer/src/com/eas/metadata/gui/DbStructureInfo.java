/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.gui;

/**
 *
 * @author vy
 */
public class DbStructureInfo {

    public enum COMPARE_TYPE {
        EQUAL, NOT_EQUAL, NOT_SAME, NOT_EXIST
    };
    private String value;
    private COMPARE_TYPE compareType = null;

    public DbStructureInfo(String aValue) {
        this(aValue, null);
    }

    public DbStructureInfo(String aValue, COMPARE_TYPE aCompareType) {
        value = aValue;
        compareType = aCompareType;
    }

    @Override
    public String toString() {
        return value;
    }

    public COMPARE_TYPE getCompareType() {
        return compareType;
    }

    public void setCompareType(COMPARE_TYPE aCompareType) {
        compareType = aCompareType;
    }
}
