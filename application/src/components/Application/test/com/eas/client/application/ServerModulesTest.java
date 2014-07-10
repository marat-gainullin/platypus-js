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
    public static final String TEST_METHOD_NAME = "testCounter";

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
    
    @Test
    public void statefullServerModule() throws Exception {
        ApplicationScriptsTest.serverScriptTest("ServerModuleTests");
    }
    
    @Test
    public void serverReportTest() throws Exception {
        ApplicationScriptsTest.serverScriptTest("ServerReportTests");
    }
    
    protected void statelessSessionModule(AppClient aClient) throws Exception {
        aClient.login(ScriptRunnerSecurityTest.USER1_NAME, ScriptRunnerSecurityTest.USER_PASSWORD.toCharArray());//USER1 has permission for every module of these
        try {
            Object res = aClient.executeServerModuleMethod(SESSION_STATELESS_MODULE_NAME, TEST_METHOD_NAME, 10);
            Object res1 = aClient.executeServerModuleMethod(SESSION_STATELESS_MODULE_NAME, TEST_METHOD_NAME, 10);
            assertEquals(res, 10);
            assertEquals(res1, 10);
        } finally {
            aClient.logout();
        }
    }

    protected void statefullSessionModule(AppClient aClient) throws Exception {
        aClient.login(ScriptRunnerSecurityTest.USER1_NAME, ScriptRunnerSecurityTest.USER_PASSWORD.toCharArray());//USER1 has permission for every module of these
        try {
            Object res = aClient.executeServerModuleMethod(SESSION_STATEFULL_MODULE_NAME, TEST_METHOD_NAME, 10);
            Object res1 = aClient.executeServerModuleMethod(SESSION_STATEFULL_MODULE_NAME, TEST_METHOD_NAME, 10);
            assertEquals(res, 10);
            assertEquals(res1, 20);
        } finally {
            aClient.logout();
        }
    }

    protected void statefullTaskModule(AppClient aClient, int aDelta) throws Exception {
        aClient.login(ScriptRunnerSecurityTest.USER1_NAME, ScriptRunnerSecurityTest.USER_PASSWORD.toCharArray());//USER1 has permission for every module of these
        try {
            Object res = aClient.executeServerModuleMethod(TASK_STATEFULL_MODULE_NAME, TEST_METHOD_NAME, 10);
            Object res1 = aClient.executeServerModuleMethod(TASK_STATEFULL_MODULE_NAME, TEST_METHOD_NAME, 10);
            assertEquals(10 + aDelta, res);
            assertEquals(20 + aDelta, res1);
        } finally {
            aClient.logout();
        }
    }
}
