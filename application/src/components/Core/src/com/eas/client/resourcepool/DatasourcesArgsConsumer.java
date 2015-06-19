/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.resourcepool;

import com.eas.client.settings.DbConnectionSettings;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class DatasourcesArgsConsumer {

    public static final String CMD_SWITCHS_PREFIX = "-";
    // parameters names
    public static final String DB_RESOURCE_CONF_PARAM = "datasource";
    public static final String DB_URL_CONF_PARAM = "dburl";
    public static final String DB_USERNAME_CONF_PARAM = "dbuser";
    public static final String DB_PASSWORD_CONF_PARAM = "dbpassword";
    public static final String DB_SCHEMA_CONF_PARAM = "dbschema";
    public static final String DB_MAX_CONNECTIONS_CONF_PARAM = "maxconnections";
    public static final String DB_MAX_STATEMENTS_CONF_PARAM = "maxstatements";
    // error messages
    public static final String BAD_DB_RESOURCE_MSG = "Datasource name not specified";
    public static final String BAD_DB_USERNAME_MSG = "dbuser value not specified";
    public static final String BAD_DB_PASSWORD_MSG = "dbpassword value not specified";
    public static final String BAD_DB_MAX_CONNECTIONS_MSG = "Bad maxconnections option";
    public static final String BAD_DB_MAX_STATEMENTS_MSG = "Bad maxstatements option";
    public static final String BAD_DB_RESOURCE_TIMEOUT_MSG = "Bad resourcetimeout option";
    public static final String WITHOUT_DATASOURCE_MSG = " without datasource detected";

    protected List<DbConnectionSettings> parsedDatasources = new ArrayList<>();

    public int consume(String[] args, int i) throws Exception {
        if ((CMD_SWITCHS_PREFIX + DB_RESOURCE_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                String datasourcename = args[i + 1];
                DbConnectionSettings settings = new DbConnectionSettings();
                settings.setMaxConnections(BearResourcePool.DEFAULT_MAXIMUM_SIZE);
                settings.setMaxStatements(BearResourcePool.DEFAULT_MAXIMUM_SIZE * 5);
                settings.setName(datasourcename);
                parsedDatasources.add(settings);
            } else {
                throw new IllegalArgumentException(BAD_DB_RESOURCE_MSG);
            }
        } else if ((CMD_SWITCHS_PREFIX + DB_URL_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                if (!parsedDatasources.isEmpty()) {
                    parsedDatasources.get(parsedDatasources.size() - 1).setUrl(args[i + 1]);
                } else {
                    throw new IllegalArgumentException(DB_URL_CONF_PARAM + WITHOUT_DATASOURCE_MSG);
                }
            } else {
                throw new IllegalArgumentException(BAD_DB_USERNAME_MSG);
            }
        } else if ((CMD_SWITCHS_PREFIX + DB_USERNAME_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                if (!parsedDatasources.isEmpty()) {
                    parsedDatasources.get(parsedDatasources.size() - 1).setUser(args[i + 1]);
                } else {
                    throw new IllegalArgumentException(DB_USERNAME_CONF_PARAM + WITHOUT_DATASOURCE_MSG);
                }
            } else {
                throw new IllegalArgumentException(BAD_DB_USERNAME_MSG);
            }
        } else if ((CMD_SWITCHS_PREFIX + DB_PASSWORD_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                if (!parsedDatasources.isEmpty()) {
                    parsedDatasources.get(parsedDatasources.size() - 1).setPassword(args[i + 1]);
                } else {
                    throw new IllegalArgumentException(DB_PASSWORD_CONF_PARAM + WITHOUT_DATASOURCE_MSG);
                }
            } else {
                throw new IllegalArgumentException(BAD_DB_PASSWORD_MSG);
            }
        } else if ((CMD_SWITCHS_PREFIX + DB_SCHEMA_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                if (!parsedDatasources.isEmpty()) {
                    parsedDatasources.get(parsedDatasources.size() - 1).setSchema(args[i + 1]);
                } else {
                    throw new IllegalArgumentException(DB_SCHEMA_CONF_PARAM + WITHOUT_DATASOURCE_MSG);
                }
            } else {
                throw new IllegalArgumentException(BAD_DB_PASSWORD_MSG);
            }
        } else if ((CMD_SWITCHS_PREFIX + DB_MAX_CONNECTIONS_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                if (!parsedDatasources.isEmpty()) {
                    parsedDatasources.get(parsedDatasources.size() - 1).setMaxConnections(Integer.valueOf(args[i + 1]));
                } else {
                    throw new IllegalArgumentException(DB_MAX_CONNECTIONS_CONF_PARAM + WITHOUT_DATASOURCE_MSG);
                }
            } else {
                throw new IllegalArgumentException(BAD_DB_MAX_CONNECTIONS_MSG);
            }
        } else if ((CMD_SWITCHS_PREFIX + DB_MAX_STATEMENTS_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                if (!parsedDatasources.isEmpty()) {
                    parsedDatasources.get(parsedDatasources.size() - 1).setMaxStatements(Integer.valueOf(args[i + 1]));
                } else {
                    throw new IllegalArgumentException(DB_MAX_STATEMENTS_CONF_PARAM + WITHOUT_DATASOURCE_MSG);
                }
            } else {
                throw new IllegalArgumentException(BAD_DB_MAX_STATEMENTS_MSG);
            }
        } else {
            return 0;
        }
        return 2;
    }

    public void registerDatasources() throws SQLException {
        parsedDatasources.stream().forEach((settings) -> {
            GeneralResourceProvider.getInstance().registerDatasource(settings.getName(), settings);
            Logger.getLogger(DatasourcesArgsConsumer.class.getName()).log(Level.INFO, "Datasource \"{0}\" has been registered", settings.getName());
        });
    }

}
