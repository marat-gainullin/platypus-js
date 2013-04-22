/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.AppClient;
import com.eas.client.ClientConstants;
import com.eas.client.forms.FormRunner;
import com.eas.client.forms.FormRunnerPrototype;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.reports.ReportRunner;
import com.eas.client.reports.ReportRunnerPrototype;
import com.eas.client.reports.ServerReportProxyPrototype;
import com.eas.client.scripts.CompiledScriptDocuments;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptResolver;
import com.eas.client.scripts.ScriptResolverHost;
import com.eas.client.scripts.ScriptRunner;
import com.eas.client.scripts.ScriptRunnerPrototype;
import com.eas.client.scripts.ServerScriptProxyPrototype;
import com.eas.client.settings.PlatypusConnectionSettings;
import com.eas.client.threetier.PlatypusNativeClient;
import com.eas.client.threetier.http.PlatypusHttpClient;
import com.eas.script.ScriptUtils;
import java.security.AccessControlException;
import static org.junit.Assert.*;
import org.junit.*;
import org.mozilla.javascript.Function;

/**
 *
 * @author vv
 */
public class ScriptRunnerSecurityTest {

    public static final String UNSECURE_MODULE_ID = "133189621201879";
    public static final String SECURE_MODULE_ID = "133189149718000";
    public static final String SECURE_FUNCTION_MODULE_ID = "133189629266067";
    public static final String UNSECURE_FORM_ID = "133239635501682";
    public static final String SECURE_FORM_ID = "133239639702802";
    public static final String SECURE_FUNCTION_FORM_ID = "133300884117270";
    public static final String UNSECURE_REPORT_ID = "133239663976162";
    public static final String SECURE_REPORT_ID = "133239674465069";
    public static final String SECURE_REPORT_FORM_ID = "133300898183656";
    public static final String DEFAULT_USER_NAME = "test";
    /**
     * This user has no roles
     */
    public static final String USER0_NAME = "testuser0";
    /**
     * This user has role1
     */
    public static final String USER1_NAME = "testuser1";
    /**
     * This user has role1, role2
     */
    public static final String USER2_NAME = "testuser2";
    /**
     * This user has role3
     */
    public static final String USER3_NAME = "testuser3";
    public static final String USER_PASSWORD = "test";
    public static final String UNKNOWN_MODULE_TYPE_MESSAGE = "Unknown module type";
    public static final String UNKNOWN_TEST_TYPE_MESSAGE = "Unknown test type";
    private static AppClient appClient;
    private static AppClient httpClient;
    private static CompiledScriptDocuments scriptDocuments;
    private static CompiledScriptDocumentsHost scriptDocumentsHost;
    private static ScriptResolver scriptResolver;
    private static ScriptResolverHost scriptResolverHost;
    private static ScriptRunner unsecureScriptRunner;
    private static ScriptRunner secureScriptRunner;
    private static ScriptRunner secureFunctionScriptRunner;

    public ScriptRunnerSecurityTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        PlatypusConnectionSettings nativeSettings = new PlatypusConnectionSettings();
        nativeSettings.setUrl("platypus://localhost:8500/");
        nativeSettings.setName("Test native connection");
        appClient = new PlatypusNativeClient(nativeSettings);

        PlatypusConnectionSettings httpSettings = new PlatypusConnectionSettings();
        httpSettings.setName("Platypus http test connection");
        httpSettings.setUrl("http://localhost:8080/application/");
        httpClient = new PlatypusHttpClient(httpSettings);
        scriptResolverHost = new ScriptResolverHost() {
            @Override
            public ScriptResolver getResolver() {
                return scriptResolver;
            }
        };
        scriptDocumentsHost = new CompiledScriptDocumentsHost() {
            @Override
            public CompiledScriptDocuments getDocuments() {
                return scriptDocuments;
            }

            @Override
            public void defineJsClass(String aClassName, ApplicationElement aAppElement) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        ScriptRunnerPrototype.init(ScriptUtils.getScope(), true);
        ServerScriptProxyPrototype.init(ScriptUtils.getScope(), true);
        ServerReportProxyPrototype.init(ScriptUtils.getScope(), true);
        ReportRunnerPrototype.init(ScriptUtils.getScope(), true);
        FormRunnerPrototype.init(ScriptUtils.getScope(), true);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSecureExecuteClientComponentMethod() throws Exception {
        setupModules(ClientConstants.ET_COMPONENT, appClient);
        testSecureExecuteClientMethod(appClient);
    }

    @Test
    public void testSecureExecuteClientComponentMethodHttp() throws Exception {
        setupModules(ClientConstants.ET_COMPONENT, httpClient);
        testSecureExecuteClientMethod(httpClient);
    }

    @Test
    public void testSecureExecuteClientFormMethod() throws Exception {
        setupModules(ClientConstants.ET_FORM, appClient);
        testSecureExecuteClientMethod(appClient);
    }

    @Test
    public void testSecureExecuteClientFormMethodHttp() throws Exception {
        setupModules(ClientConstants.ET_FORM, httpClient);
        testSecureExecuteClientMethod(httpClient);
    }

    @Test
    public void testSecureExecuteClientReportMethod() throws Exception {
        setupModules(ClientConstants.ET_REPORT, appClient);
        testSecureExecuteClientMethod(appClient);
    }

    @Test
    public void testSecureExecuteClientReportMethodHttp() throws Exception {
        setupModules(ClientConstants.ET_REPORT, httpClient);
        testSecureExecuteClientMethod(httpClient);
    }

    private void testSecureExecuteClientMethod(AppClient client) throws Exception {
        System.out.println("secureExecuteMethod");
        client.login(USER0_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionExecuteMethod(unsecureScriptRunner, true);
            assertHasPermissionExecuteMethod(secureScriptRunner, false);
            assertHasPermissionExecuteMethod(secureFunctionScriptRunner, false);
        } finally {
            client.logout();
        }

        client.login(USER1_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionExecuteMethod(unsecureScriptRunner, true);
            assertHasPermissionExecuteMethod(secureScriptRunner, true);
            assertHasPermissionExecuteMethod(secureFunctionScriptRunner, false);
        } finally {
            client.logout();
        }

        client.login(USER2_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionExecuteMethod(unsecureScriptRunner, true);
            assertHasPermissionExecuteMethod(secureScriptRunner, true);
            assertHasPermissionExecuteMethod(secureFunctionScriptRunner, false);
        } finally {
            client.logout();
        }

        client.login(USER3_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionExecuteMethod(unsecureScriptRunner, true);
            assertHasPermissionExecuteMethod(secureScriptRunner, false);
            assertHasPermissionExecuteMethod(secureFunctionScriptRunner, true);
        } finally {
            client.logout();
        }
    }

    private static enum SecurityTestType {

        UNSECURE, SECURE, SECURE_FUNCTION;
    }

    private void setupModules(int moduleType, AppClient client) throws Exception {
        scriptDocuments = new ClientCompiledScriptDocuments(client);
        client.login(USER1_NAME, USER_PASSWORD.toCharArray());//USER1 has permission for every module of these
        try {
            unsecureScriptRunner = getModule(moduleType, SecurityTestType.UNSECURE, client);
            scriptResolver = new ClientScriptResolver();
            assertNotNull(unsecureScriptRunner);
            secureScriptRunner = getModule(moduleType, SecurityTestType.SECURE, client);
            assertNotNull(secureScriptRunner);
            secureFunctionScriptRunner = getModule(moduleType, SecurityTestType.SECURE_FUNCTION, client);
            assertNotNull(secureFunctionScriptRunner);
        } finally {
            client.logout();
        }
    }

    private ScriptRunner getModule(int moduleType, SecurityTestType testType, AppClient client) throws Exception {
        switch (moduleType) {
            case ClientConstants.ET_COMPONENT:
                return new ScriptRunner(getComponentAppElementId(testType), client, ScriptUtils.getScope(), client, scriptDocumentsHost, scriptResolverHost);
            case ClientConstants.ET_FORM:
                return new FormRunner(getFormAppElementId(testType), client, ScriptUtils.getScope(), client, scriptDocumentsHost, scriptResolverHost);
            case ClientConstants.ET_REPORT:
                return new ReportRunner(getReportAppelementId(testType), client, ScriptUtils.getScope(), client, scriptDocumentsHost, scriptResolverHost);
        }
        throw new IllegalArgumentException(UNKNOWN_MODULE_TYPE_MESSAGE);//NOI18N
    }

    private static String getComponentAppElementId(SecurityTestType testType) {
        switch (testType) {
            case UNSECURE:
                return UNSECURE_MODULE_ID;
            case SECURE:
                return SECURE_MODULE_ID;
            case SECURE_FUNCTION:
                return SECURE_FUNCTION_MODULE_ID;
        }
        throw new IllegalArgumentException(UNKNOWN_TEST_TYPE_MESSAGE);
    }

    private static String getFormAppElementId(SecurityTestType testType) {
        switch (testType) {
            case UNSECURE:
                return UNSECURE_FORM_ID;
            case SECURE:
                return SECURE_FORM_ID;
            case SECURE_FUNCTION:
                return SECURE_FUNCTION_FORM_ID;
        }
        throw new IllegalArgumentException(UNKNOWN_TEST_TYPE_MESSAGE);
    }

    private static String getReportAppelementId(SecurityTestType testType) {
        switch (testType) {
            case UNSECURE:
                return UNSECURE_REPORT_ID;
            case SECURE:
                return SECURE_REPORT_ID;
            case SECURE_FUNCTION:
                return SECURE_REPORT_FORM_ID;
        }
        throw new IllegalArgumentException(UNKNOWN_TEST_TYPE_MESSAGE);
    }

    private void assertHasPermissionExecuteMethod(ScriptRunner sr, boolean hasPermission) throws Exception {
        if (hasPermission) {
            Function fun = (Function) sr.get("test");
            Object result = fun.call(ScriptUtils.enterContext(), sr, sr, new Object[0]);
            assertNotNull(result);
            assertEquals(result, "test");
        } else {
            try {
                Function fun = (Function) sr.get("test");
                Object result = fun.call(ScriptUtils.enterContext(), sr, sr, new Object[0]);
                fail("Access permission assertion failed");
            } catch (Exception ex) {
                assertTrue(ex instanceof AccessControlException);
            }
        }
    }
}
