/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util.args;

/**
 *
 * @author mg
 */
public class ThreadsArgsConsumer {

    public static final String CMD_SWITCHS_PREFIX = "-";
    // parameters names
    public static final String MAX_JDBC_THREADS_CONF_PARAM = "max-jdbc-threads";
    public static final String MAX_HTTP_THREADS_CONF_PARAM = "max-http-threads";
    public static final String MAX_PLATYPUS_THREADS_CONF_PARAM = "max-platypus-threads";
    public static final String MAX_SERVICES_THREADS_CONF_PARAM = "max-services-threads";
    // error messages
    public static final String BAD_JDBC_THREADS_MSG = "jdbc max threads not specified";
    public static final String BAD_HTTP_THREADS_MSG = "http max threads not specified";
    public static final String BAD_PLATYPUS_THREADS_MSG = "platypus max threads not specified";
    public static final String BAD_SERVICES_THREADS_MSG = "services max threads not specified";

    protected int maxJdbcTreads = 25;
    protected int maxHttpTreads = 25;
    protected int maxPlatypusTreads = 25;
    protected int maxServicesTreads = 25;

    public int consume(String[] args, int i) throws Exception {
        if ((CMD_SWITCHS_PREFIX + MAX_JDBC_THREADS_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                String value = args[i + 1];
                maxJdbcTreads = Double.valueOf(value).intValue();
            } else {
                throw new IllegalArgumentException(BAD_JDBC_THREADS_MSG);
            }
        } else if ((CMD_SWITCHS_PREFIX + MAX_HTTP_THREADS_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                String value = args[i + 1];
                maxHttpTreads = Double.valueOf(value).intValue();
            } else {
                throw new IllegalArgumentException(BAD_HTTP_THREADS_MSG);
            }
        } else if ((CMD_SWITCHS_PREFIX + MAX_PLATYPUS_THREADS_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                String value = args[i + 1];
                maxPlatypusTreads = Double.valueOf(value).intValue();
            } else {
                throw new IllegalArgumentException(BAD_PLATYPUS_THREADS_MSG);
            }
        } else if ((CMD_SWITCHS_PREFIX + MAX_SERVICES_THREADS_CONF_PARAM).equalsIgnoreCase(args[i])) {
            if (i + 1 < args.length) {
                String value = args[i + 1];
                maxServicesTreads = Double.valueOf(value).intValue();
            } else {
                throw new IllegalArgumentException(BAD_SERVICES_THREADS_MSG);
            }
        } else {
            return 0;
        }
        return 2;
    }

    public int getMaxJdbcTreads() {
        return maxJdbcTreads;
    }

    public int getMaxHttpTreads() {
        return maxHttpTreads;
    }

    public int getMaxPlatypusTreads() {
        return maxPlatypusTreads;
    }

    public int getMaxServicesTreads() {
        return maxServicesTreads;
    }
}
