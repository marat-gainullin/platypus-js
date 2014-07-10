/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.AppClient;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.threetier.PlatypusNativeClient;
import com.eas.client.threetier.http.PlatypusHttpClient;
import com.eas.client.threetier.http.PlatypusHttpTestConstants;
import com.eas.client.threetier.requests.CreateServerModuleResponse;
import com.eas.script.ScriptUtils;
import java.security.AccessControlException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class PlatypusClientSecurityTest {

    public static final String UNSECURE_MODULE_ID = "UnsecureModule";
    public static final String UNSECURE_NONPUBLIC_MODULE_ID = "UnsecureNonPublicModule";
    public static final String SECURE_MODULE_ID = "SecureModule";
    public static final String SECURE_FUNCTION_MODULE_ID = "SecureFunctionModule";
    private static final String TEST_METHOD_NAME = "test";
    public static final String UNSECURE_QUERY_ID = "133189616153303";
    public static final String SECURE_QUERY_ID = "133189623152950";
    public static final String SECURE_READ_QUERY_ID = "133189634694079";
    public static final String SECURE_WRITE_QUERY_ID = "133189638245364";
    public static final String UNSECURE_INSERT_QUERY_ID = "133215649272244";
    public static final String SECURE_INSERT_QUERY_ID = "133215768558007";
    public static final String SECURE_INSERT_READ_QUERY_ID = "133215982321595";
    public static final String SECURE_INSERT_WRITE_QUERY_ID = "133215988588849";
    public static final String DELETE_TEMP_DATA_QUERY_ID = "133215630470014";
    public static final String PK_PARAMETRER_NAME = "pKey";
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
     * This user has role1 and role2
     */
    public static final String USER2_NAME = "testuser2";
    /**
     * This user has role3
     */
    public static final String USER3_NAME = "testuser3";
    public static final String USER_PASSWORD = "test";
    private static AppClient appClient;
    private static AppClient httpClient;
    private static int rowId;

    public PlatypusClientSecurityTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        ScriptUtils.init();
        appClient = new PlatypusNativeClient("platypus://localhost:8500/");
        httpClient = new PlatypusHttpClient(PlatypusHttpTestConstants.HTTP_REQUEST_URL);
        removeTempData(); // if any
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (appClient != null) {
            removeTempData();
            appClient.shutdown();
        }
    }

    private static void removeTempData() throws Exception {
        appClient.login(DEFAULT_USER_NAME, USER_PASSWORD.toCharArray());
        appClient.enqueueUpdate(DELETE_TEMP_DATA_QUERY_ID, new Parameters());
        appClient.commit();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of secured execute method for Platypus native client
     *
     * @throws Exception
     */
    @Test
    public void testSecureExecuteMethodNativeClient() throws Exception {
        testSecureExecuteMethod(appClient);
    }

    /**
     * Test of secured execute read query for Platypus native client
     *
     * @throws Exception
     */
    @Test
    public void testExecuteQueryReadNativeClient() throws Exception {
        testExecuteQueryRead(appClient);
    }

    /**
     * Test of secured execute update query for Platypus native client
     *
     * @throws Exception
     */
    @Test
    public void testExecuteQueryUpdateNativeClient() throws Exception {
        testExecuteQueryUpdate(appClient);
    }

    /**
     * Test of secured execute insert for Platypus native client
     *
     * @throws Exception
     */
    @Test
    public void testExecuteQueryInsertNativeClient() throws Exception {
        testExecuteQueryInsert(appClient);
    }

    /**
     * Test of secured execute method for Platypus HTTP client
     *
     * @throws Exception
     */
    @Test
    public void testSecureExecuteMethodHttpClient() throws Exception {
        testSecureExecuteMethod(httpClient);
    }

    /**
     * Test of secured execute read query for Platypus HTTP client
     *
     * @throws Exception
     */
    @Test
    public void testExecuteQueryReadHttpClient() throws Exception {
        testExecuteQueryRead(httpClient);
    }

    /**
     * Test of secured execute update query for Platypus HTTP client
     *
     * @throws Exception
     */
    @Test
    public void testExecuteQueryUpdateHttpClient() throws Exception {
        testExecuteQueryUpdate(httpClient);
    }

    /**
     * Test of secured execute insert for Platypus HTTP client
     *
     * @throws Exception
     */
    @Test
    public void testExecuteQueryInsertHttpClient() throws Exception {
        testExecuteQueryInsert(httpClient);
    }

    @Test
    public void testExecuteMethodOfNonPublicModuleHttpClient() throws Exception {
        testExecuteMethodOfNonPublicModule(httpClient);
    }

    @Test
    public void testExecuteMethodOfNonPublicModuleNativeClient() throws Exception {
        testExecuteMethodOfNonPublicModule(appClient);
    }

    private void testExecuteMethodOfNonPublicModule(AppClient client) throws Exception {
        client.login(USER0_NAME, USER_PASSWORD.toCharArray());
        try {
            try {
                client.createServerModule(UNSECURE_NONPUBLIC_MODULE_ID);
                client.executeServerModuleMethod(UNSECURE_NONPUBLIC_MODULE_ID, TEST_METHOD_NAME);
                fail("Public access permission assertion failed");
            } catch (AccessControlException ex) {
                // Fine! Exception must be here, due to module has no @public annotation
            }
        } finally {
            client.logout();
        }
    }

    /**
     * Test of secured execute method for users with various assigned roles
     *
     * @throws Exception
     */
    private void testSecureExecuteMethod(AppClient client) throws Exception {
        System.out.println("secureExecuteMethod");
        client.login(USER0_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionExecuteMethod(client, UNSECURE_MODULE_ID, true);
            assertHasPermissionExecuteMethod(client, SECURE_MODULE_ID, false);
            assertHasPermissionExecuteMethod(client, SECURE_FUNCTION_MODULE_ID, false);
        } finally {
            client.logout();
        }

        client.login(USER1_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionExecuteMethod(client, UNSECURE_MODULE_ID, true);
            assertHasPermissionExecuteMethod(client, SECURE_MODULE_ID, true);
            assertHasPermissionExecuteMethod(client, SECURE_FUNCTION_MODULE_ID, false);
        } finally {
            client.logout();
        }

        client.login(USER2_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionExecuteMethod(client, UNSECURE_MODULE_ID, true);
            assertHasPermissionExecuteMethod(client, SECURE_MODULE_ID, true);
            assertHasPermissionExecuteMethod(client, SECURE_FUNCTION_MODULE_ID, false);
        } finally {
            client.logout();
        }

        client.login(USER3_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionExecuteMethod(client, UNSECURE_MODULE_ID, true);
            assertHasPermissionExecuteMethod(client, SECURE_MODULE_ID, false);
            assertHasPermissionExecuteMethod(client, SECURE_FUNCTION_MODULE_ID, true);
        } finally {
            client.logout();
        }
    }

    /**
     * Test of secured execute read query for users with various assigned roles
     *
     * @throws Exception
     */
    private void testExecuteQueryRead(AppClient client) throws Exception {
        System.out.println("executeQuery");
        client.login(USER0_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionReadQuery(client, UNSECURE_QUERY_ID, true);
            assertHasPermissionReadQuery(client, SECURE_QUERY_ID, false);
            assertHasPermissionReadQuery(client, SECURE_READ_QUERY_ID, false);
            assertHasPermissionReadQuery(client, SECURE_WRITE_QUERY_ID, true);
        } finally {
            client.logout();
        }

        client.login(USER1_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionReadQuery(client, UNSECURE_QUERY_ID, true);
            assertHasPermissionReadQuery(client, SECURE_QUERY_ID, true);
            assertHasPermissionReadQuery(client, SECURE_READ_QUERY_ID, false);
            assertHasPermissionReadQuery(client, SECURE_WRITE_QUERY_ID, true);
        } finally {
            client.logout();
        }

        client.login(USER2_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionReadQuery(client, UNSECURE_QUERY_ID, true);
            assertHasPermissionReadQuery(client, SECURE_QUERY_ID, true);
            assertHasPermissionReadQuery(client, SECURE_READ_QUERY_ID, true);
            assertHasPermissionReadQuery(client, SECURE_WRITE_QUERY_ID, true);
        } finally {
            client.logout();
        }

        client.login(USER3_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionReadQuery(client, UNSECURE_QUERY_ID, true);
            assertHasPermissionReadQuery(client, SECURE_QUERY_ID, false);
            assertHasPermissionReadQuery(client, SECURE_READ_QUERY_ID, false);
            assertHasPermissionReadQuery(client, SECURE_WRITE_QUERY_ID, true);
        } finally {
            client.logout();
        }
    }

    /**
     * Test of secured execute update query for users with various assigned
     * roles
     *
     * @throws Exception
     */
    private void testExecuteQueryUpdate(AppClient client) throws Exception {
        System.out.println("executeUpdateQuery");
        client.login(USER0_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionUpdate(client, UNSECURE_QUERY_ID, true);
            assertHasPermissionUpdate(client, SECURE_QUERY_ID, false);
            assertHasPermissionUpdate(client, SECURE_WRITE_QUERY_ID, false);
        } finally {
            client.logout();
        }

        client.login(USER1_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionUpdate(client, UNSECURE_QUERY_ID, true);
            assertHasPermissionUpdate(client, SECURE_QUERY_ID, true);
            assertHasPermissionUpdate(client, SECURE_WRITE_QUERY_ID, true);
        } finally {
            client.logout();
        }

        client.login(USER2_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionUpdate(client, UNSECURE_QUERY_ID, true);
            assertHasPermissionUpdate(client, SECURE_QUERY_ID, true);
            assertHasPermissionUpdate(client, SECURE_WRITE_QUERY_ID, true);
        } finally {
            client.logout();
        }

        client.login(USER3_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionUpdate(client, UNSECURE_QUERY_ID, true);
            assertHasPermissionUpdate(client, SECURE_QUERY_ID, false);
            assertHasPermissionUpdate(client, SECURE_WRITE_QUERY_ID, true);
        } finally {
            client.logout();
        }
    }

    /**
     * Test of secured execute insert query for users with various assigned
     * roles
     *
     * @throws Exception
     */
    private void testExecuteQueryInsert(AppClient client) throws Exception {
        System.out.println("executeInsertQuery");
        client.login(USER0_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionInsert(client, UNSECURE_INSERT_QUERY_ID, true);
            assertHasPermissionInsert(client, SECURE_INSERT_QUERY_ID, false);
            assertHasPermissionInsert(client, SECURE_INSERT_READ_QUERY_ID, true);
            assertHasPermissionInsert(client, SECURE_INSERT_WRITE_QUERY_ID, false);
        } finally {
            client.logout();
        }

        client.login(USER1_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionInsert(client, UNSECURE_INSERT_QUERY_ID, true);
            assertHasPermissionInsert(client, SECURE_INSERT_QUERY_ID, true);
            assertHasPermissionInsert(client, SECURE_INSERT_READ_QUERY_ID, true);
            assertHasPermissionInsert(client, SECURE_INSERT_WRITE_QUERY_ID, false);
        } finally {
            client.logout();
        }

        client.login(USER2_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionInsert(client, UNSECURE_INSERT_QUERY_ID, true);
            assertHasPermissionInsert(client, SECURE_INSERT_QUERY_ID, true);
            assertHasPermissionInsert(client, SECURE_INSERT_READ_QUERY_ID, true);
            assertHasPermissionInsert(client, SECURE_INSERT_WRITE_QUERY_ID, false);
        } finally {
            client.logout();
        }

        client.login(USER3_NAME, USER_PASSWORD.toCharArray());
        try {
            assertHasPermissionInsert(client, UNSECURE_INSERT_QUERY_ID, true);
            assertHasPermissionInsert(client, SECURE_INSERT_QUERY_ID, false);
            assertHasPermissionInsert(client, SECURE_INSERT_READ_QUERY_ID, true);
            assertHasPermissionInsert(client, SECURE_INSERT_WRITE_QUERY_ID, true);
        } finally {
            client.logout();
        }
    }

    private void assertHasPermissionUpdate(AppClient client, String queryId, boolean hasPermission) throws Exception {
        if (hasPermission) {
            executeUpdateMethod(client, queryId);
        } else {
            try {
                executeUpdateMethod(client, queryId);
                fail("Access permission assertion failed");
            } catch (AccessControlException ex) {
                // Fine! Exception must be here, due to user has not a permission
            }
        }
    }

    private static void executeUpdateMethod(AppClient client, String queryId) throws Exception {
        PlatypusQuery query = client.getAppQuery(queryId);
        Rowset rowset = query.execute();
        assertTrue(rowset.size() > 0);
        rowset.beforeFirst();
        rowset.next();
        rowset.updateObject(rowset.getFields().find("CUSTOMER_ADDRESS"), String.format("г. Кракозябры, ул Кырозубры, д. %d", IDGenerator.genID()));
        client.commit();
    }

    private void assertHasPermissionReadQuery(AppClient client, String queryId, boolean hasPermission) throws Exception {
        if (hasPermission) {
            PlatypusQuery query = client.getAppQuery(queryId);
            Rowset result = query.execute();
            assertNotNull(result);
        } else {
            try {
                PlatypusQuery query = client.getAppQuery(queryId);
                query.execute();
                fail("Access permission assertion failed");
            } catch (AccessControlException ex) {
                // Fine! Exception must be here, due to user has not a permission
            }
        }
    }

    private void assertHasPermissionExecuteMethod(AppClient client, String moduleId, boolean hasPermission) throws Exception {
        CreateServerModuleResponse resp = client.createServerModule(moduleId);
        if (hasPermission) {
            if (!resp.isPermitted()) {
                throw new AccessControlException(moduleId + " is not permitted");
            }
            Object result = client.executeServerModuleMethod(moduleId, TEST_METHOD_NAME);
            assertNotNull(result);
            assertEquals(result, "test");
        } else {
            try {
                // first chance for exception to throw
                if (!resp.isPermitted()) {
                    throw new AccessControlException(moduleId + " is not permitted");
                }
                // second chance for exception to throw
                client.executeServerModuleMethod(moduleId, TEST_METHOD_NAME);
                fail("Access permission assertion failed");
            } catch (AccessControlException ex) {
                // Fine! Exception must be here, due to user has no the permission
            }
        }
    }

    private void assertHasPermissionInsert(AppClient client, String queryId, boolean hasPermission) throws Exception {
        if (hasPermission) {
            executeInsert(client, queryId);
        } else {
            try {
                executeInsert(client, queryId);
                fail("Access permission assertion failed");
            } catch (AccessControlException ex) {
                // Fine! Exception must be here, due to user has not a permission
            }
        }
    }

    private void executeInsert(AppClient client, String queryId) throws Exception {
        Parameters params = new Parameters();
        Parameter param = new Parameter(PK_PARAMETRER_NAME);
        param.setTypeInfo(DataTypeInfo.DECIMAL);
        param.setValue(getRowId());
        params.add(param);
        client.enqueueUpdate(queryId, params);
        client.commit();
    }

    private int getRowId() {
        return rowId++;
    }
}
