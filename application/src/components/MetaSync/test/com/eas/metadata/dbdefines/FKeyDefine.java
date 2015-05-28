/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.dbdefines;

import com.eas.client.metadata.ForeignKeySpec;

/**
 *
 * @author vy
 */
public class FKeyDefine {

    private String tableName;
    private String name;
    private String referTableName;
    private ForeignKeySpec.ForeignKeyRule deleteRule;
    private ForeignKeySpec.ForeignKeyRule updateRule;
    private boolean deferrable;
    private String[] fieldsNames;

    public FKeyDefine(String aTableName, String aName, String aReferTableName, ForeignKeySpec.ForeignKeyRule aDeleteRule, ForeignKeySpec.ForeignKeyRule aUpdateRule, boolean aDeferrable, String[] aFieldsNames) {
        tableName = aTableName;
        name = aName;
        referTableName = aReferTableName;
        deleteRule = aDeleteRule;
        updateRule = aUpdateRule;
        deferrable = aDeferrable;
        fieldsNames = aFieldsNames;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the referTableName
     */
    public String getReferTableName() {
        return referTableName;
    }

    /**
     * @param referTableName the referTableName to set
     */
    public void setReferTableName(String referTableName) {
        this.referTableName = referTableName;
    }

    /**
     * @return the deleteRule
     */
    public ForeignKeySpec.ForeignKeyRule getDeleteRule() {
        return deleteRule;
    }

    /**
     * @param deleteRule the deleteRule to set
     */
    public void setDeleteRule(ForeignKeySpec.ForeignKeyRule deleteRule) {
        this.deleteRule = deleteRule;
    }

    /**
     * @return the updateRule
     */
    public ForeignKeySpec.ForeignKeyRule getUpdateRule() {
        return updateRule;
    }

    /**
     * @param updateRule the updateRule to set
     */
    public void setUpdateRule(ForeignKeySpec.ForeignKeyRule updateRule) {
        this.updateRule = updateRule;
    }

    /**
     * @return the deferrable
     */
    public boolean isDeferrable() {
        return deferrable;
    }

    /**
     * @param deferrable the deferrable to set
     */
    public void setDeferrable(boolean deferrable) {
        this.deferrable = deferrable;
    }

    /**
     * @return the fieldsNames
     */
    public String[] getFieldsNames() {
        return fieldsNames;
    }

    /**
     * @param fieldsNames the fieldsNames to set
     */
    public void setFieldsNames(String[] fieldsNames) {
        this.fieldsNames = fieldsNames;
    }
}
