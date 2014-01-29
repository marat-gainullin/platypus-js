/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata;

import java.util.Map;

/**
 *
 * @author vy
 */
public class DBStructure {
    private String databaseDialect;
    private Map<String, TableStructure> tablesStructure;

    public DBStructure() {
    }
    
    public DBStructure(Map<String, TableStructure> aTablesStructure, String aDatabaseDialect) {
        tablesStructure = aTablesStructure;
        databaseDialect = aDatabaseDialect;
    }
    
    /**
     * @return the databaseDialect
     */
    public String getDatabaseDialect() {
        return databaseDialect;
    }

    /**
     * @param databaseDialect the databaseDialect to set
     */
    public void setDatabaseDialect(String aDatabaseDialect) {
        databaseDialect = aDatabaseDialect;
    }

    /**
     * @return the tablesStructure
     */
    public Map<String, TableStructure> getTablesStructure() {
        return tablesStructure;
    }

    /**
     * @param tablesStructure the tablesStructure to set
     */
    public void setTablesStructure(Map<String, TableStructure> aTablesStructure) {
        tablesStructure = aTablesStructure;
    }
    
}
