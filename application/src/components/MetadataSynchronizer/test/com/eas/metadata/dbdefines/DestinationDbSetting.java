/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.dbdefines;

import com.eas.metadata.testdefine.DbTestDefine;
import com.eas.metadata.dbdefines.DbConnection;

/**
 *
 * @author vy
 */
    public class DestinationDbSetting {
        private DbConnection dbConnection;
        private DbTestDefine.Database database;

        public DestinationDbSetting(DbConnection aDbConnection, DbTestDefine.Database aDatabase) {
            dbConnection = aDbConnection;
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
