/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.dbdefines;

/**
 *
 * @author vy
 */
public class PKeyDefine {

    private String tableName;
    private String[] fieldsNames;

    public PKeyDefine(String aTableName, String[] aFieldsNames) {
        tableName = aTableName;
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
