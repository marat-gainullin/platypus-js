/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.dbdefines;

/**
 *
 * @author vy
 */
public class TableDefine {

    private String tableName;
    private String pkFieldName;
    private String description;

    public TableDefine(String aTableName, String aPkFieldName, String aDescription) {
        tableName = aTableName;
        pkFieldName = aPkFieldName;
        description = aDescription;
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
     * @return the pkFieldName
     */
    public String getPkFieldName() {
        return pkFieldName;
    }

    /**
     * @param pkFieldName the pkFieldName to set
     */
    public void setPkFieldName(String pkFieldName) {
        this.pkFieldName = pkFieldName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
