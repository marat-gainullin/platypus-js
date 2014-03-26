/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.utils;

import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public enum DatabaseServerType {

    H2("H2", "jdbc:h2:tcp://%s:%s/%s", 9092, "~/test", //NOI18N
            NbBundle.getMessage(DatabaseServerType.class, "H2Info.text"),//NOI18N
            "org.h2.Driver"), //NOI18N
    ORACLE("Oracle Db", "jdbc:oracle:thin:@//%s:%s/%s", 1521, "", "", "oracle.jdbc.OracleDriver"), //NOI18N
    POSTGRESQL("PostgreSQL", "jdbc:postgresql://%s:%s/%s", 5432, "", "", "org.postgresql.Driver"), //NOI18N
    MYSQL("MySQL", "jdbc:mysql://%s:%s/%s", 3306, "", "", "com.mysql.jdbc.Driver"), //NOI18N
    SQL_SERVER("SQL Server", "jdbc:sqlserver://%s:%s;databaseName=%s", 1433, "", "", "net.sourceforge.jtds.jdbc.Driver"), //NOI18N
    DB2("DB2", "jdbc:db2://%s:%s/%s", 50000, "", "", "com.ibm.db2.jcc.DB2Driver"); //NOI18N
    public final String name;
    public final String jdbcUrlTemplate;
    public final int defaultPort;
    public final String databaseTemplate;
    public final String info;
    public final String jdbcClassName;

    DatabaseServerType(String aName, String aJdbcUrlTemplate, int aDefaultPort, String aDatabaseTemplate, String anInfo, String aJdbcClassName) {
        name = aName;
        jdbcUrlTemplate = aJdbcUrlTemplate;
        defaultPort = aDefaultPort;
        databaseTemplate = aDatabaseTemplate;
        info = anInfo;
        jdbcClassName = aJdbcClassName;
    }

    @Override
    public String toString() {
        return name;
    }

    public String buildUrl(String host, int port, String database) {
        return String.format(jdbcUrlTemplate, host, port, database);
    }
}
