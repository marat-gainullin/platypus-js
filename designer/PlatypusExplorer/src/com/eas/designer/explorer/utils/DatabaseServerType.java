/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.utils;

import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public enum DatabaseServerType {

    H2("H2", "jdbc:h2:tcp://%s:%s/%s", 9092, "~/test", NbBundle.getMessage(DatabaseServerType.class, "H2Info.text")), //NOI18N
    ORACLE("Oracle Db", "jdbc:oracle:thin:@//%s:%s/%s", 1521, "", ""), //NOI18N
    POSTGRESQL("PostgreSQL", "jdbc:postgresql://%s:%s/%s", 5432, "", ""), //NOI18N
    MYSQL("MySQL", "jdbc:mysql://%s:%s/%s", 3306, "", ""), //NOI18N
    SQL_SERVER("SQL Server", "jdbc:sqlserver://%s:%s;databaseName=%s", 1433, "", ""), //NOI18N
    DB2("DB2", "jdbc:db2://%s:%s/%s", 50000, "", ""); //NOI18N
    public final String name;
    public final String jdbcUrlTemplate;
    public final int defaultPort;
    public final String databaseTemplate;
    public final String info;

    DatabaseServerType(String aName, String aJdbcUrlTemplate, int aDefaultPort, String aDatabaseTemplate, String anInfo) {
        name = aName;
        jdbcUrlTemplate = aJdbcUrlTemplate;
        defaultPort = aDefaultPort;
        databaseTemplate = aDatabaseTemplate;
        info = anInfo;
    }

    @Override
    public String toString() {
        return name;
    }

    public String buildUrl(String host, int port, String database) {
        return String.format(jdbcUrlTemplate, host, port, database);
    }
}
