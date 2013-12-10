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
import com.eas.client.scripts.ScriptRunner;
import com.eas.client.scripts.ScriptRunnerPrototype;
import com.eas.client.scripts.ServerScriptProxyPrototype;
import com.eas.client.settings.PlatypusConnectionSettings;
import com.eas.client.threetier.PlatypusNativeClient;
import com.eas.client.threetier.http.PlatypusHttpClient;
import com.eas.script.ScriptUtils;
import com.eas.script.ScriptUtils.ScriptAction;
import java.security.AccessControlException;
import org.junit.*;
import static org.junit.Assert.*;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;

/**
 *
 * @author vv
 */
public class ScriptRunnerSecurityTest {

    public static final String UNSECURE_MODULE_ID = "UnsecureModule";
    public static final String SECURE_MODULE_ID = "SecureModule";
    public static final String SECURE_FUNCTION_MODULE_ID = "SecureFunctionModule";
    public static final String UNSECURE_FORM_ID = "UnsecureForm";
    public static final String SECURE_FORM_ID = "SecureForm";
    public static final String SECURE_FUNCTION_FORM_ID = "ScureFunctionForm";
    public static final String UNSECURE_REPORT_ID = "UnsecureReport";
    public static final String SECURE_REPORT_ID = "SecureReport";
    public static final String SECURE_REPORT_FORM_ID = "SecureFunctionReport";
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
    public static AppClient nativeClient;
    public static AppClient httpClient;
    private static CompiledScriptDocuments scriptDocuments;
    private static CompiledScriptDocumentsHost scriptDocumentsHost;
    private static ScriptRunner unsecureScriptRunner;
    private static ScriptRunner secureScriptRunner;
    private static ScriptRunner secureFunctionScriptRunner;

    public ScriptRunnerSecurityTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        initClients();
    }

    public static void initClients() throws Exception {
        if (nativeClient == null) {
            PlatypusConnectionSettings nativeSettings = new PlatypusConnectionSettings();
            nativeSettings.setUrl("platypus://localhost:8500/");
            nativeSettings.setName("Test native connection");
            nativeClient = new PlatypusNativeClient(nativeSettings);
            PlatypusConnectionSettings httpSettings = new PlatypusConnectionSettings();
            httpSettings.setName("Platypus http test connection");
            httpSettings.setUrl("http://localhost:8080/application/");
            httpClient = new PlatypusHttpClient(httpSettings);
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
        setupModules(ClientConstants.ET_COMPONENT, nativeClient);
        testSecureExecuteClientMethod(nativeClient);
    }

    @Test
    public void testSecureExecuteClientComponentMethodHttp() throws Exception {
        setupModules(ClientConstants.ET_COMPONENT, httpClient);
        testSecureExecuteClientMethod(httpClient);
    }

    @Test
    public void testSecureExecuteClientFormMethod() throws Exception {
        setupModules(ClientConstants.ET_FORM, nativeClient);
        testSecureExecuteClientMethod(nativeClient);
    }

    @Test
    public void testSecureExecuteClientFormMethodHttp() throws Exception {
        setupModules(ClientConstants.ET_FORM, httpClient);
        testSecureExecuteClientMethod(httpClient);
    }

    @Test
    public void testSecureExecuteClientReportMethod() throws Exception {
        setupModules(ClientConstants.ET_REPORT, nativeClient);
        testSecureExecuteClientMethod(nativeClient);
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

    public static void setupScriptDocuments(AppClient client) {
        scriptDocuments = new ClientCompiledScriptDocuments(client);
    }

    private void setupModules(int moduleType, AppClient client) throws Exception {
        setupScriptDocuments(client);
        client.login(USER1_NAME, USER_PASSWORD.toCharArray());//USER1 has permission for every module of these
        try {
            unsecureScriptRunner = getModule(moduleType, SecurityTestType.UNSECURE, client);
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
                return new ScriptRunner(getComponentAppElementId(testType), client, ScriptRunner.initializePlatypusStandardLibScope(), client, scriptDocumentsHost, new Object[]{});
            case ClientConstants.ET_FORM:
                return new FormRunner(getFormAppElementId(testType), client, ScriptRunner.initializePlatypusStandardLibScope(), client, scriptDocumentsHost, new Object[]{});
            case ClientConstants.ET_REPORT:
                return new ReportRunner(getReportAppelementId(testType), client, ScriptRunner.initializePlatypusStandardLibScope(), client, scriptDocumentsHost, new Object[]{});
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

    private void assertHasPermissionExecuteMethod(final ScriptRunner aRunner, final boolean hasPermission) throws Exception {
        ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                if (hasPermission) {
                    Function fun = (Function) aRunner.get("test");
                    Object result = fun.call(cx, aRunner, aRunner, new Object[0]);
                    assertNotNull(result);
                    assertEquals(result, "test");
                } else {
                    try {
                        Function fun = (Function) aRunner.get("test");
                        Object result = fun.call(cx, aRunner, aRunner, new Object[0]);
                        fail("Access permission assertion failed");
                    } catch (Exception ex) {
                        assertTrue(ex instanceof AccessControlException);
                    }
                }
                return null;
            }
        });
    }
}
