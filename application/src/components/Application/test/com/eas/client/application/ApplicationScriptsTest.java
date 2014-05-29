/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.DatabasesClientWithResource;
import static com.eas.client.application.ScriptRunnerSecurityTest.httpClient;
import static com.eas.client.application.ScriptRunnerSecurityTest.nativeClient;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.client.login.SystemPlatypusPrincipal;
import com.eas.client.scripts.PlatypusScriptedResource;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.threetier.PlatypusNativeClient;
import com.eas.client.threetier.http.PlatypusHttpClient;
import com.eas.script.ScriptUtils;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class ApplicationScriptsTest {

    @Test
    public void dummy() {
    }

    public static DatabasesClientWithResource initDevelopTestClient() throws Exception {
        DbConnectionSettings settings = new DbConnectionSettings("jdbc:oracle:thin:@asvr/adb", "eas", "eas", "eas", null);
        settings.setMaxStatements(1);
        ScriptUtils.init();
        return new DatabasesClientWithResource(settings);
    }

    protected static class TestPrincipalHost implements PrincipalHost {

        @Override
        public PlatypusPrincipal getPrincipal() {
            return new SystemPlatypusPrincipal();
        }
    }

    public static void scriptTest(String aModuleId) throws Exception {
        System.out.println("starting script test for the " + aModuleId + ". Expecting some exceptions while failure.");
        try (DatabasesClientWithResource resource = initDevelopTestClient()) {
            ScriptUtils.createModule(aModuleId);
            //ScriptRunner script = new ScriptRunner(aModuleId, resource.getClient(), ScriptRunner.initializePlatypusStandardLibScope(), new TestPrincipalHost(), new TestScriptDocumentsHost(new ClientCompiledScriptDocuments(resource.getClient())), new Object[]{});
            //script.execute();
        }
        System.out.println("script test for " + aModuleId + " has been completed successfully!");
    }

    public static void serverScriptTest(String aModuleId) throws Exception {
        ScriptUtils.init();
        nativeClient = new PlatypusNativeClient("platypus://localhost:8500/");
        PlatypusScriptedResource.initForTests(nativeClient, nativeClient);
        System.out.println("starting script test for the " + aModuleId + ". Expecting some exceptions while failure.");
        nativeClient.login("testuser1", "test".toCharArray());
        PlatypusScriptedResource.executeScriptResource(aModuleId);
        if (ScriptUtils.createModule(aModuleId) == null) {
            throw new Exception("Module " + aModuleId + " is not found in Platypus server.");
        }
        nativeClient.logout();
        httpClient = new PlatypusHttpClient("http://localhost:8080/application/application/");
        PlatypusScriptedResource.initForTests(httpClient, httpClient);
        httpClient.login("testuser1", "test".toCharArray());
        PlatypusScriptedResource.executeScriptResource(aModuleId);
        if (ScriptUtils.createModule(aModuleId) == null) {
            throw new Exception("Module " + aModuleId + " is not found in EE server.");
        }
        httpClient.logout();
        System.out.println("script test for " + aModuleId + " has been completed successfully!");
    }

}
