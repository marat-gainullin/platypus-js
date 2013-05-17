/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

/**
 *
 * @author pk, mg refactoring
 */
public class Requests
{
    public static final int rqHello = 1;
    //public static final int rqKeepAlive = 2;
    public static final int rqOutHash = 3;
    //public static final int rqLogin = 4;
    public static final int rqStartAppElement = 5;
    public static final int rqAppQuery = 6;
    public static final int rqExecuteQuery = 7;
    public static final int rqCommit = 8;
//    public static final int rqRollback = 9;// since commit transfer all the changes, than network rollback is obsolete
    //public static final int rqIsAppElementActual = 10;
    public static final int rqAppElement = 11;
    public static final int rqCreateServerModule = 12;
    public static final int rqDisposeServerModule = 13;
    public static final int rqExecuteServerModuleMethod = 14;
    //public static final int rqAppElementChanged = 15;
//  public static final int rqDbTableChanged = 16;// it present in Java SE client, but not in browser client
    //public static final int rqIsUserInRole = 17;
    public static final int rqLogout = 18;
    public static final int rqExecuteReport = 19;
    //public static final int rqGetServerModuleProperty = 20;
    //public static final int rqSetServerModuleProperty = 21;
}
