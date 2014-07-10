/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.AppClient;
import com.eas.client.ClientConstants;
import com.eas.client.scripts.PlatypusScriptedResource;
import com.eas.client.threetier.PlatypusNativeClient;
import com.eas.client.threetier.http.PlatypusHttpClient;
import com.eas.script.ScriptUtils;
import java.security.AccessControlException;
import jdk.nashorn.api.scripting.JSObject;
import org.junit.*;
import static org.junit.Assert.*;

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
    public static final String SECURE_FUNCTION_REPORT_ID = "SecureFunctionReport";
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
    private static JSObject unsecureScriptRunner;
    private static JSObject secureScriptRunner;
    private static JSObject secureFunctionScriptRunner;

    public ScriptRunnerSecurityTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        initClients();
    }

    public static void initClients() throws Exception {
        if (nativeClient == null) {
            nativeClient = new PlatypusNativeClient("platypus://localhost:8500/");
            httpClient = new PlatypusHttpClient("http://localhost:8080/application/");
            ScriptUtils.init();
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

    private void setupModules(int moduleType, AppClient client) throws Exception {
        client.login(USER1_NAME, USER_PASSWORD.toCharArray());//USER1 has permission for every module of these
        try {
            PlatypusScriptedResource.initForTests(client, client);
            for (String moduleName : new String[]{
                UNSECURE_MODULE_ID,
                SECURE_MODULE_ID,
                SECURE_FUNCTION_MODULE_ID,
                UNSECURE_FORM_ID,
                SECURE_FORM_ID,
                SECURE_FUNCTION_FORM_ID,
                UNSECURE_REPORT_ID,
                SECURE_REPORT_ID,
                SECURE_FUNCTION_REPORT_ID
            }) {
                PlatypusScriptedResource.executeScriptResource(moduleName);
            }
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

    private JSObject getModule(int moduleType, SecurityTestType testType, AppClient client) throws Exception {
        switch (moduleType) {
            case ClientConstants.ET_COMPONENT:
                return ScriptUtils.createModule(getComponentAppElementId(testType));
            //return new ScriptRunner(getComponentAppElementId(testType), client, ScriptRunner.initializePlatypusStandardLibScope(), client, scriptDocumentsHost, new Object[]{});
            case ClientConstants.ET_FORM:
                return ScriptUtils.createModule(getFormAppElementId(testType));
            //return new FormRunner(getFormAppElementId(testType), client, ScriptRunner.initializePlatypusStandardLibScope(), client, scriptDocumentsHost, new Object[]{});
            case ClientConstants.ET_REPORT:
                return ScriptUtils.createModule(getReportAppElementId(testType));
            //return new ReportRunner(getReportAppElementId(testType), client, ScriptRunner.initializePlatypusStandardLibScope(), client, scriptDocumentsHost, new Object[]{});
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

    private static String getReportAppElementId(SecurityTestType testType) {
        switch (testType) {
            case UNSECURE:
                return UNSECURE_REPORT_ID;
            case SECURE:
                return SECURE_REPORT_ID;
            case SECURE_FUNCTION:
                return SECURE_FUNCTION_REPORT_ID;
        }
        throw new IllegalArgumentException(UNKNOWN_TEST_TYPE_MESSAGE);
    }

    private void assertHasPermissionExecuteMethod(final JSObject aRunner, final boolean hasPermission) throws Exception {
        if (hasPermission) {
            JSObject fun = (JSObject) aRunner.getMember("test");
            Object result = fun.call(aRunner, new Object[]{});
            assertNotNull(result);
            assertEquals(result, "test");
        } else {
            try {
                JSObject fun = (JSObject) aRunner.getMember("test");
                Object result = fun.call(aRunner, new Object[]{});
                fail("Access permission assertion failed");
            } catch (Exception ex) {
                assertTrue(ex instanceof AccessControlException);
            }
        }
    }
}
