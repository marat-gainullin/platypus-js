/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.DatabasesClientWithResource;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.client.login.SystemPlatypusPrincipal;
import com.eas.client.scripts.ScriptDocuments;
import com.eas.client.scripts.ScriptDocumentsHost;
import com.eas.client.settings.DbConnectionSettings;
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
        return new DatabasesClientWithResource(settings);
    }

    protected static class TestPrincipalHost implements PrincipalHost {

        @Override
        public PlatypusPrincipal getPrincipal() {
            return new SystemPlatypusPrincipal();
        }
    }

    protected static class TestScriptDocumentsHost implements ScriptDocumentsHost {

        protected ScriptDocuments scriptDocuments;

        public TestScriptDocumentsHost(ScriptDocuments aScriptDocuments) {
            scriptDocuments = aScriptDocuments;
        }

        @Override
        public ScriptDocuments getDocuments() {
            return scriptDocuments;
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

}
