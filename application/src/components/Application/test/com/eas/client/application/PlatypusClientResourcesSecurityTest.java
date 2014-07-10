/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.AppCache;
import com.eas.client.AppClient;
import com.eas.client.ClientConstants;
import static com.eas.client.application.PlatypusClientSecurityTest.*;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.threetier.PlatypusNativeClient;
import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author vv
 */
public class PlatypusClientResourcesSecurityTest {

    public static final String UNSECURE_FORM_ID = "UnsecureForm";
    public static final String SECURE_FORM_ID = "SecureForm";
    public static final String UNSECURE_REPORT_ID = "UnsecureReport";
    public static final String SECURE_REPORT_ID = "SecureReport";
    public static final String SECURITY_TESTS_STRUCTURE_DIAGRAM_ID = "133215611839876";
    public static final String SECURITY_TESTS_FOLDER_ID = "133189145962527";
    public static final String RECYCLED_ELEMENTS_ID = "0";
    private static AppClient appClient;

    public PlatypusClientResourcesSecurityTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        appClient = new PlatypusNativeClient("platypus://localhost:8500/");    
    }

    private static void clearFileCacheDirectory() throws Exception {
        File userHome = new File(System.getProperty(ClientConstants.USER_HOME_PROP_NAME));
        File platypusHome = new File(userHome, ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME);
        File entitiesCache = new File(platypusHome, ClientConstants.ENTITIES_CACHE_DIRECTORY_NAME);
        deleteDirectory(entitiesCache);
        entitiesCache.mkdir();
        appClient.getAppCache().clear();
    }

    private static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (appClient != null) {
            appClient.shutdown();
        }
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of secured get Platypus resources
     * @throws Exception
     */
    @Test
    public void testSecureGetResources() throws Exception {
        System.out.println("secureGetResources");
        clearFileCacheDirectory();
        appClient.login(USER0_NAME, USER_PASSWORD.toCharArray());
        assertHasPermissionGetAppElement(appClient, UNSECURE_MODULE_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_MODULE_ID, false);
        assertHasPermissionGetAppElement(appClient, SECURE_FUNCTION_MODULE_ID, false);
        assertHasPermissionGetAppElement(appClient, UNSECURE_FORM_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_FORM_ID, false);
        assertHasPermissionGetAppElement(appClient, UNSECURE_REPORT_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_REPORT_ID, false);
        assertGetEmptyAppElements(appClient);
        appClient.logout();
        
        clearFileCacheDirectory();  
        appClient.login(USER1_NAME, USER_PASSWORD.toCharArray());
        assertHasPermissionGetAppElement(appClient, UNSECURE_MODULE_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_MODULE_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_FUNCTION_MODULE_ID, true);
        assertHasPermissionGetAppElement(appClient, UNSECURE_FORM_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_FORM_ID, true);
        assertHasPermissionGetAppElement(appClient, UNSECURE_REPORT_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_REPORT_ID, true);
        assertGetEmptyAppElements(appClient);
        appClient.logout();
        
        clearFileCacheDirectory();  
        appClient.login(USER2_NAME, USER_PASSWORD.toCharArray());
        assertHasPermissionGetAppElement(appClient, UNSECURE_MODULE_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_MODULE_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_FUNCTION_MODULE_ID, true);
        assertHasPermissionGetAppElement(appClient, UNSECURE_FORM_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_FORM_ID, true);
        assertHasPermissionGetAppElement(appClient, UNSECURE_REPORT_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_REPORT_ID, true);
        assertGetEmptyAppElements(appClient);
        appClient.logout();
        
        clearFileCacheDirectory();  
        appClient.login(USER3_NAME, USER_PASSWORD.toCharArray());
        assertHasPermissionGetAppElement(appClient, UNSECURE_MODULE_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_MODULE_ID, false);
        assertHasPermissionGetAppElement(appClient, SECURE_FUNCTION_MODULE_ID, true);
        assertHasPermissionGetAppElement(appClient, UNSECURE_FORM_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_FORM_ID, false);
        assertHasPermissionGetAppElement(appClient, UNSECURE_REPORT_ID, true);
        assertHasPermissionGetAppElement(appClient, SECURE_REPORT_ID, false);
        assertGetEmptyAppElements(appClient);
        appClient.logout();
    }

    private void assertGetEmptyAppElements(AppClient client) throws Exception {
        assertGetEmptyAppElement(client, SECURITY_TESTS_STRUCTURE_DIAGRAM_ID);
        assertGetEmptyAppElement(client, SECURITY_TESTS_FOLDER_ID);
        assertGetEmptyAppElement(client, RECYCLED_ELEMENTS_ID);
        assertGetEmptyAppElement(client, UNSECURE_QUERY_ID);
        assertGetEmptyAppElement(client, SECURE_QUERY_ID);
        assertGetEmptyAppElement(client, SECURE_READ_QUERY_ID);
        assertGetEmptyAppElement(client, SECURE_WRITE_QUERY_ID);
    }

    private void assertHasPermissionGetAppElement(AppClient client, String appElementId, boolean hasPermission) throws Exception {
        AppCache appCache = client.getAppCache();
        assertNotNull(appCache);
        ApplicationElement appElement = null;
        if (hasPermission) {
            appElement = appCache.get(appElementId);
            assertNotNull(appElement);
        } else {
            try {
                appElement = appCache.get(appElementId);
                fail(String.format("Get resource permission assertion failed for %s application element", appElementId));
            } catch (Exception ex) {
                //NO OP
            }
        }
    }

    private void assertGetEmptyAppElement(AppClient client, String appElementId) throws Exception {
        AppCache appCache = client.getAppCache();
        ApplicationElement appElement = appCache.get(appElementId);
        assertNull("Appliction element is not empty.", appElement);
    }
}
