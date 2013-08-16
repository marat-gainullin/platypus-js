/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.AppClient;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class ServerModulesTest {

    public static final String SESSION_STATEFULL_MODULE_NAME = "TestStatefullServerModule";
    public static final String SESSION_STATELESS_MODULE_NAME = "TestStatelessServerModule";
    public static final String TASK_STATEFULL_MODULE_NAME = "TestServerTaskModule";
    // stateless task modules are impossible
    public static final String TES_METHOD_NAME = "testCounter";

    @BeforeClass
    public static void setUpClass() throws Exception {
        ScriptRunnerSecurityTest.initClients();
    }

    @Test
    public void statelessSessionModule() throws Exception {
        statelessSessionModule(ScriptRunnerSecurityTest.nativeClient);
        statelessSessionModule(ScriptRunnerSecurityTest.httpClient);
    }

    @Test
    public void statefullSessionModule() throws Exception {
        statefullSessionModule(ScriptRunnerSecurityTest.nativeClient);
        statefullSessionModule(ScriptRunnerSecurityTest.httpClient);
    }

    @Test
    public void statefullTaskModule() throws Exception {
        statefullTaskModule(ScriptRunnerSecurityTest.nativeClient, 0);
        statefullTaskModule(ScriptRunnerSecurityTest.nativeClient, 20);
        statefullTaskModule(ScriptRunnerSecurityTest.httpClient, 0);
        statefullTaskModule(ScriptRunnerSecurityTest.httpClient, 20);
    }

    protected void statelessSessionModule(AppClient aClient) throws Exception {
        ScriptRunnerSecurityTest.setupScriptDocuments(aClient);
        aClient.login(ScriptRunnerSecurityTest.USER1_NAME, ScriptRunnerSecurityTest.USER_PASSWORD.toCharArray());//USER1 has permission for every module of these
        try {
            Object res = aClient.executeServerModuleMethod(SESSION_STATELESS_MODULE_NAME, TES_METHOD_NAME, 10);
            Object res1 = aClient.executeServerModuleMethod(SESSION_STATELESS_MODULE_NAME, TES_METHOD_NAME, 10);
            assertEquals(res, new Long(10));
            assertEquals(res1, new Long(10));
        } finally {
            aClient.logout();
        }
    }

    protected void statefullSessionModule(AppClient aClient) throws Exception {
        ScriptRunnerSecurityTest.setupScriptDocuments(aClient);
        aClient.login(ScriptRunnerSecurityTest.USER1_NAME, ScriptRunnerSecurityTest.USER_PASSWORD.toCharArray());//USER1 has permission for every module of these
        try {
            Object res = aClient.executeServerModuleMethod(SESSION_STATEFULL_MODULE_NAME, TES_METHOD_NAME, 10);
            Object res1 = aClient.executeServerModuleMethod(SESSION_STATEFULL_MODULE_NAME, TES_METHOD_NAME, 10);
            assertEquals(res, new Long(10));
            assertEquals(res1, new Long(20));
        } finally {
            aClient.logout();
        }
    }

    protected void statefullTaskModule(AppClient aClient, int aDelta) throws Exception {
        ScriptRunnerSecurityTest.setupScriptDocuments(aClient);
        aClient.login(ScriptRunnerSecurityTest.USER1_NAME, ScriptRunnerSecurityTest.USER_PASSWORD.toCharArray());//USER1 has permission for every module of these
        try {
            Object res = aClient.executeServerModuleMethod(TASK_STATEFULL_MODULE_NAME, TES_METHOD_NAME, 10);
            Object res1 = aClient.executeServerModuleMethod(TASK_STATEFULL_MODULE_NAME, TES_METHOD_NAME, 10);
            assertEquals(new Long(10+aDelta), res);
            assertEquals(new Long(20+aDelta), res1);
        } finally {
            aClient.logout();
        }
    }
}
