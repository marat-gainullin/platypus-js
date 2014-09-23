/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.PlatypusClientTest;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.settings.PlatypusConnectionSettings;
import com.eas.client.threetier.HelloRequest;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.requests.*;
import com.eas.script.ScriptUtils;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kl, mg refactoring
 */
public class PlatypusHttpClientTest {

    public static final int EXECUTE_SERVER_MODULE_METHOD_COUNT = 10;
    PlatypusConnectionSettings settings;
    PlatypusHttpClient client;
    String sessionId;

    public PlatypusConnectionSettings getSettigs() {
        if (settings == null) {
            settings = new PlatypusConnectionSettings();
            settings.setName("Platypus http test connection");
            settings.setUrl(PlatypusHttpTestConstants.HTTP_REQUEST_URL);
        }
        return settings;
    }

    private String createServerModule(String serverModuleName) throws Exception {
        CreateServerModuleRequest rq = new CreateServerModuleRequest(IDGenerator.genID(), serverModuleName);
        client.executeRequest(rq);
        assertTrue(rq.getResponse() instanceof CreateServerModuleResponse);
        CreateServerModuleResponse response = (CreateServerModuleResponse) rq.getResponse();
        String moduleName = response.getModuleName();
        assertEquals(moduleName, String.valueOf(serverModuleName));
        return moduleName;
    }

    private void disposeServerModule(String moduleName) throws Exception {
        DisposeServerModuleRequest killRq = new DisposeServerModuleRequest(IDGenerator.genID(), moduleName);
        client.executeRequest(killRq);
        assertTrue(killRq.getResponse() instanceof DisposeServerModuleRequest.Response);
    }

    private void easyRequestTest(Request rq) throws Exception {
        try {
            client.executeRequest(rq);
        } catch (Exception e) {
            fail("Error in " + rq.getClass().getName() + " sending");
        }
    }

    private Object executeServerModuleMethod(String moduleName, String methodName, String aTestParamOne, Integer aTestParamTwo) throws Exception {
        ExecuteServerModuleMethodRequest execModuleMethodRq = new ExecuteServerModuleMethodRequest(IDGenerator.genID(), moduleName, methodName, new Object[]{aTestParamOne, aTestParamTwo});
        client.executeRequest(execModuleMethodRq);
        assertTrue(execModuleMethodRq.getResponse() instanceof ExecuteServerModuleMethodRequest.Response);
        ExecuteServerModuleMethodRequest.Response execResponse = (ExecuteServerModuleMethodRequest.Response) execModuleMethodRq.getResponse();
        return execResponse.getResult();
    }

    private void validateRowsetResponse(RowsetResponse response) {
        assertNotNull(response.getRowset());
        assertTrue(response.getRowset().getFields().toCollection().size() > 0);
        assertTrue(response.getRowset().size() > 0);
    }

    @Before
    public void beforeTest() throws Exception {
        if (client == null) {
            client = new PlatypusHttpClient(PlatypusHttpTestConstants.HTTP_REQUEST_URL);
            ScriptUtils.init();
        }
        client.login(PlatypusClientTest.TEST_LOGIN, PlatypusClientTest.TEST_PASSWD.toCharArray());
    }

    @After
    public void afterTest() throws Exception {
        client.logout();
    }

    @Test
    public void testAppQueryRequest() throws Exception {
        PlatypusQuery query = client.getAppQuery(PlatypusClientTest.TEST_QUERY_ID);
        assertNotNull(query);
    }

    @Test
    public void testCommitRequest() throws Exception {
        client.commit();
    }

    @Test
    public void testUpdateRowsetRequest() throws Exception {
        PlatypusClientTest.tormentData(client);
    }

    @Test
    public void ambiguousChangesTest() throws Exception {
        PlatypusClientTest.threeTablesTest(client);
    }

    @Test
    public void testExecuteServerModuleMethodRequest() throws Exception {
        String serverModuleName = PlatypusClientTest.TEST_SERVER_MODULE_NAME;
        String moduleName = createServerModule(serverModuleName);
        try {
            assertEquals(moduleName, String.valueOf(serverModuleName));

            String aTestParamOne = "testParamOneValue";
            Integer aTestParamTwo = 10;
            String methodName = "testMethod";
            Object result = null;
            for (int i = 0; i < EXECUTE_SERVER_MODULE_METHOD_COUNT; i++) {
                result = executeServerModuleMethod(moduleName, methodName, aTestParamOne, aTestParamTwo);
                assertEquals(aTestParamTwo + 10, result);
                assertTrue(result instanceof Integer);
                aTestParamTwo = (Integer)result;
            }
            assertEquals(10 + 10 * EXECUTE_SERVER_MODULE_METHOD_COUNT, result);
        } finally {
            disposeServerModule(moduleName);
        }
    }

    @Test
    public void testAppElementChangedRequest() throws Exception {
        AppElementChangedRequest rq = new AppElementChangedRequest(IDGenerator.genID(), null, PlatypusClientTest.TEST_QUERY_ID);
        client.executeRequest(rq);
        assertTrue(rq.getResponse() instanceof AppElementChangedRequest.Response);
    }

    @Test
    public void testDbTableChangedRequest() throws Exception {
        DbTableChangedRequest rq = new DbTableChangedRequest(IDGenerator.genID(), null, "eas", "MTD_USERS");
        client.executeRequest(rq);
        assertTrue(rq.getResponse() instanceof DbTableChangedRequest.Response);
    }

    @Test
    public void testExecuteQueryRequest() throws Exception {
        ExecuteQueryRequest rq = new ExecuteQueryRequest(IDGenerator.genID(), PlatypusClientTest.TEST_QUERY_ID, new Parameters(), client.getAppQuery(PlatypusClientTest.TEST_QUERY_ID).getFields());
        client.executeRequest(rq);
        assertTrue(rq.getResponse() instanceof RowsetResponse);
        RowsetResponse response = (RowsetResponse) rq.getResponse();
        validateRowsetResponse(response);
        response.getRowset().first();
        Integer appElementsCount = response.getRowset().getInt(1);
        assertNotNull(appElementsCount);
        assertTrue(appElementsCount > 100);
    }

    @Test
    public void testHelloRequest() throws Exception {
        HelloRequest rq = new HelloRequest(IDGenerator.genID());
        easyRequestTest(rq);
    }

    @Test
    public void testKeepAliveRequest() throws Exception {
        KeepAliveRequest rq = new KeepAliveRequest(IDGenerator.genID());
        easyRequestTest(rq);
    }

    @Test
    public void testAppElementOfBadTypeRequest() throws Exception {
        AppElementRequest rq = new AppElementRequest(IDGenerator.genID(), PlatypusClientTest.TEST_QUERY_ID);
        client.executeRequest(rq);
        assertNotNull(rq.getResponse());
        assertNull(((AppElementRequest.Response) rq.getResponse()).getAppElement());
    }

    @Test
    public void testAppElementRequestAndIsAppElementActual() throws Exception {
        AppElementRequest rq1 = new AppElementRequest(IDGenerator.genID(), PlatypusClientTest.TEST_SERVER_MODULE_ID);
        client.executeRequest(rq1);
        assertNotNull(rq1.getResponse());
        ApplicationElement appElement = ((AppElementRequest.Response) rq1.getResponse()).getAppElement();
        assertNotNull(appElement);
        IsAppElementActualRequest rq2 = new IsAppElementActualRequest(IDGenerator.genID(), PlatypusClientTest.TEST_SERVER_MODULE_ID, appElement.getTxtContentLength(), appElement.getTxtCrc32());
        client.executeRequest(rq2);
        assertNotNull(rq2.getResponse());
        IsAppElementActualRequest.Response response2 = (IsAppElementActualRequest.Response) rq2.getResponse();
        assertTrue(response2.isActual());
    }

    @Test
    public void testStartAppElementRequest() throws Exception {
        CredentialRequest rq = new CredentialRequest(IDGenerator.genID());
        client.executeRequest(rq);
        assertNotNull(rq.getResponse());
        String startAppElement = ((CredentialRequest.Response) rq.getResponse()).getAppElementId();
        assertEquals("FormsAPI", startAppElement);
    }

    @Test
    public void testIsUserInRole() throws Exception {
        IsUserInRoleRequest rq = new IsUserInRoleRequest(IDGenerator.genID(), PlatypusClientTest.TEST_ROLE);
        client.executeRequest(rq);
        assertNotNull(rq.getResponse());
        assertTrue(((IsUserInRoleRequest.Response) rq.getResponse()).isRole());
    }
}
