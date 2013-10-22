/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.DatabasesClient;
import com.eas.client.DbClient;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.client.login.SystemPlatypusPrincipal;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.scripts.CompiledScriptDocuments;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptRunner;
import com.eas.client.settings.DbConnectionSettings;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class ApplicationScriptsTest {
    
    @Test
    public void dummy(){
    }
    
    public static DbClient initDevelopTestClient() throws Exception {
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl("jdbc:oracle:thin:@asvr/adb");
        settings.setUser("eas");
        settings.setPassword("eas");
        settings.setSchema("eas");
        settings.setMaxStatements(1);
        return new DatabasesClient(settings);
    }

    protected static class TestPrincipalHost implements PrincipalHost {

        @Override
        public PlatypusPrincipal getPrincipal() {
            return new SystemPlatypusPrincipal();
        }
    }

    protected static class TestScriptDocumentsHost implements CompiledScriptDocumentsHost {

        protected CompiledScriptDocuments scriptDocuments;

        public TestScriptDocumentsHost(CompiledScriptDocuments aScriptDocuments) {
            scriptDocuments = aScriptDocuments;
        }

        @Override
        public CompiledScriptDocuments getDocuments() {
            return scriptDocuments;
        }

        @Override
        public void defineJsClass(String string, ApplicationElement ae) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static void scriptTest(String aModuleId) throws Exception {
        System.out.println("stating script test for the " + aModuleId + ". Expecting some exceptions while failure.");
        DbClient client = initDevelopTestClient();
        try {
            ScriptRunner script = new ScriptRunner(aModuleId, client, ScriptRunner.initializePlatypusStandardLibScope(), new TestPrincipalHost(), new TestScriptDocumentsHost(new ClientCompiledScriptDocuments(client)), new Object[]{});
            script.execute();
        } finally {
            client.shutdown();
        }
        System.out.println("script test for " + aModuleId + " has been completed successfully!");
    }
    
}
