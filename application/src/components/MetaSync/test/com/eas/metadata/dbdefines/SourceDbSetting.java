/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.dbdefines;

import com.eas.metadata.testdefine.DbTestDefine;

/**
 *
 * @author vy
 */
public class SourceDbSetting {

    private DbConnection dbConnection;
    private DbTestDefine dbTestDefine;
    private DbTestDefine.Database database;

    public SourceDbSetting(DbConnection aDbConnection, DbTestDefine.Database aDatabase, DbTestDefine aDbTestDefine) {
        dbConnection = aDbConnection;
        dbTestDefine = aDbTestDefine;
        database = aDatabase;
    }

    /**
     * @return the dbConnection
     */
    public DbConnection getDbConnection() {
        return dbConnection;
    }

    /**
     * @param dbConnection the dbConnection to set
     */
    public void setDbConnection(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    /**
     * @return the dbTestDefine
     */
    public DbTestDefine getDbTestDefine() {
        return dbTestDefine;
    }

    /**
     * @param dbTestDefine the dbTestDefine to set
     */
    public void setDbTestDefine(DbTestDefine dbTestDefine) {
        this.dbTestDefine = dbTestDefine;
    }

    /**
     * @return the database
     */
    public DbTestDefine.Database getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(DbTestDefine.Database database) {
        this.database = database;
    }
}
