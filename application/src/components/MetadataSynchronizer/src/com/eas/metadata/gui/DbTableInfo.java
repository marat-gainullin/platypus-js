/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.gui;

/**
 *
 * @author vy
 */
public class DbTableInfo extends DbStructureInfo {

    private boolean editable = false;
    private boolean choice = false;
    private String name;

    public DbTableInfo(String aName, String aValue) {//, STRUCTURE_TYPE aStructureType) {
        this(aName, aValue, false);
    }

    public DbTableInfo(String aName, String aValue, boolean editable) {
        super(aValue, DbStructureInfo.COMPARE_TYPE.EQUAL);
        name = aName;
        this.editable = editable;
    }

    public boolean isChoice() {
        return choice;
    }

    public void setChoice(boolean aChoice) {
        choice = aChoice;
    }

    public String getName() {
        return name;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
