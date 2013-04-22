/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.queries.Query;
import com.eas.client.settings.PlatypusConnectionSettings;
import com.eas.client.threetier.PlatypusNativeClient;
import com.eas.client.threetier.PlatypusThreeTierFlowProvider;
import com.eas.client.threetier.requests.AppElementRequest;
import com.eas.client.threetier.requests.IsAppElementActualRequest;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Random;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk, mg
 */
public class PlatypusClientTest {

    public static final String TEST_LOGIN = "testuser1";// has role1
    public static final String TEST_ROLE = "role1";// form testuser1
    public static final String TEST_PASSWD = "test";
    public static final String TEST_QUERY_ID = "130875374904760";
    public static final String TEST_SERVER_MODULE_ID = "130892365750900";
    public static final String TEST_SERVER_MODULE_NAME = TEST_SERVER_MODULE_ID;
    public static AppClient client;

    public PlatypusClientTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        PlatypusConnectionSettings settings = new PlatypusConnectionSettings();
        settings.setUrl("platypus://localhost:8500/");
        settings.setName("Test connection");
        client = new PlatypusNativeClient(settings);
        client.login("test", "test".toCharArray());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (client != null) {
            client.shutdown();
        }
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of executeQuery method, of class PlatypusClient.
     *
     * @throws Exception
     */
    @Test
    public void testExecuteQuery() throws Exception {
        System.out.println("executeQuery");
        Long aDbId = null;
        AppClient instance = client;
        instance.login("test", "test".toCharArray());
        PlatypusQuery query = instance.getAppQuery(PlatypusClientTest.TEST_QUERY_ID);
        assertEquals(query.getDbId(), aDbId);
        Rowset result = query.execute();
        assertNotNull(result);
        //TODO check for metadata of the returned rowset.
    }

    /**
     * Test of login method, of class PlatypusClient.
     *
     * @throws Exception
     */
    @Test
    public void testLogin() throws Exception {
        System.out.println("login");
        String userName = "test";
        char[] password = "test".toCharArray();
        AppClient instance = client;
        String result = instance.login(userName, password);
        assertNotNull(result);
    }

    /**
     * Test of getAppCache method, of class PlatypusClient.
     *
     * @throws Exception
     */
    @Test
    public void testGetAppCache() throws Exception {
        System.out.println("getAppCache");
        AppClient instance = client;
        AppCache result = instance.getAppCache();
        assertNotNull(result);
    }

    /**
     * Test of getLoginPrincipal method, of class PlatypusClient.
     *
     * @throws Exception
     */
    @Test
    public void testGetPrincipal() throws Exception {
        System.out.println("getPrincipal");
        AppClient instance = client;
        instance.login("test", "test".toCharArray());
        PlatypusPrincipal result = instance.getPrincipal();
        assertEquals("test", result.getName());
    }

    @Test
    public void testStartAppElement() throws Exception {
        System.out.println("startAppElement");
        AppClient instance = client;
        String startAppElement = instance.getStartAppElement();
        assertEquals(startAppElement, TEST_SERVER_MODULE_ID);
    }

    @Test
    public void testAppElementOfBadTypeRequest() throws Exception {
        AppElementRequest rq = new AppElementRequest(IDGenerator.genID(), PlatypusClientTest.TEST_QUERY_ID);
        AppClient instance = client;
        instance.executeRequest(rq);
        assertNotNull(rq.getResponse());
        assertNull(((AppElementRequest.Response) rq.getResponse()).getAppElement());
    }

    @Test
    public void testAppElementRequestAndIsAppElementActual() throws Exception {
        AppClient instance = client;
        AppElementRequest rq1 = new AppElementRequest(IDGenerator.genID(), PlatypusClientTest.TEST_SERVER_MODULE_ID);
        instance.executeRequest(rq1);
        assertNotNull(rq1.getResponse());
        ApplicationElement appElement = ((AppElementRequest.Response) rq1.getResponse()).getAppElement();
        assertNotNull(appElement);
        IsAppElementActualRequest rq2 = new IsAppElementActualRequest(IDGenerator.genID(), PlatypusClientTest.TEST_SERVER_MODULE_ID, appElement.getTxtContentLength(), appElement.getTxtCrc32());
        instance.executeRequest(rq2);
        assertNotNull(rq2.getResponse());
        IsAppElementActualRequest.Response response2 = (IsAppElementActualRequest.Response) rq2.getResponse();
        assertTrue(response2.isActual());
    }

    @Test
    public void testIsUserInRole() throws Exception {
        PlatypusConnectionSettings settings = new PlatypusConnectionSettings();
        settings.setUrl("platypus://localhost:8500/");
        settings.setName("Test connection");
        AppClient instance = new PlatypusNativeClient(settings);
        instance.login(TEST_LOGIN, TEST_PASSWD.toCharArray());
        IsUserInRoleRequest rq = new IsUserInRoleRequest(IDGenerator.genID(), TEST_ROLE);
        instance.executeRequest(rq);
        assertNotNull(rq.getResponse());
        assertTrue(((IsUserInRoleRequest.Response) rq.getResponse()).isRole());
        IsUserInRoleRequest rq2 = new IsUserInRoleRequest(IDGenerator.genID(), TEST_ROLE + "badRole");
        instance.executeRequest(rq2);
        assertNotNull(rq2.getResponse());
        assertFalse(((IsUserInRoleRequest.Response) rq2.getResponse()).isRole());
    }

    /**
     * Test of commit method, of class PlatypusClient.
     *
     * @throws Exception
     */
    @Test
    public void testCommit() throws Exception {
        System.out.println("commit");
        AppClient instance = client;
        instance.login("test", "test".toCharArray());
        //TODO do some rowsets modifications.
        instance.commit();
    }

    @Test
    public void testUpdatingRowset() throws SQLException, IOException, Exception {
        AppClient instance = client;
        for (int i = 0; i < 10; i++) {
            instance.login("test", "test".toCharArray());
            tormentData(instance);
        }
    }

    @Test
    public void ambiguousChangeTest() throws Exception {
        threeTablesTest(client);
    }
    public static final String AMBIGUOUS_QUERY_ID = "134564170799279";
    public static final String COMMAND_QUERY_ID = "134570075809763";
    public static final BigDecimal NEW_RECORD_ID = BigDecimal.valueOf(4125L);
    public static final String NEW_RECORD_NAME_G = "test gname";
    public static final String NEW_RECORD_NAME_T = "test tname";
    public static final String NEW_RECORD_NAME_K = "test kname";

    public static void threeTablesTest(AppClient client) throws Exception {
        Query query = client.getAppQuery(AMBIGUOUS_QUERY_ID);
        Rowset rowset = query.execute();
        int oldRowsetSize = rowset.size();
        assertTrue(oldRowsetSize > 1);
        Fields fields = rowset.getFields();
        Row row = new Row(fields);
        row.setColumnObject(fields.find("tid"), NEW_RECORD_ID);
        Field gid = row.getFields().get("gid");
        assertNotNull(gid);
        // original name check
        assertEquals(gid.getName(), "gid");
        assertEquals(gid.getOriginalName(), "");// original name isn't transferred across the network according to security reasons.
        Field tid = row.getFields().get("tid");
        assertNotNull(tid);
        // original name check
        assertEquals(tid.getName(), "tid");
        assertEquals(tid.getOriginalName(), "");// original name isn't transferred across the network according to security reasons.
        Field kname = row.getFields().get("kname");
        assertNotNull(kname);
        // original name check
        assertEquals(kname.getName(), "kname");
        assertEquals(kname.getOriginalName(), "");// original name isn't transferred across the network according to security reasons.
        // Create operation
        rowset.insertAt(row, true, 1, 
                fields.find("gname"), "-g- must be overwritten", 
                fields.find("tname"), "-t- must be overwritten", 
                fields.find("kname"), "-k- must be overwritten");
        assertNotNull(row.getColumnObject(fields.find("gid")));
        assertNotNull(row.getColumnObject(fields.find("tid")));
        assertNotNull(row.getColumnObject(fields.find("kid")));
        // Update operation
        row.setColumnObject(fields.find("gid"), NEW_RECORD_ID);
        // initialization was performed for "tid" field
        row.setColumnObject(fields.find("kid"), NEW_RECORD_ID);
        assertEquals(row.getColumnObject(fields.find("gid")), NEW_RECORD_ID);
        assertEquals(row.getColumnObject(fields.find("tid")), NEW_RECORD_ID);
        assertEquals(row.getColumnObject(fields.find("kid")), NEW_RECORD_ID);
        //
        Query command = client.getAppQuery(COMMAND_QUERY_ID);
        command.putParameter("gid", DataTypeInfo.DECIMAL, NEW_RECORD_ID);
        command.putParameter("gname", DataTypeInfo.VARCHAR, NEW_RECORD_NAME_G);
        command.enqueueUpdate();
        //rowset.updateObject(fiedls.find("gname"), NEW_RECORD_NAME_G);
        rowset.updateObject(fields.find("tname"), NEW_RECORD_NAME_T);
        rowset.updateObject(fields.find("kname"), NEW_RECORD_NAME_K);
        client.commit();
        assertTrue(client.getChangeLog().isEmpty());
        rowset.refresh();
        fields = rowset.getFields();
        assertEquals(oldRowsetSize + 1, rowset.size());

        Row newRow = null;
        rowset.beforeFirst();
        while (rowset.next()) {
            if (NEW_RECORD_ID.intValue() == rowset.getInt(fields.find("gid"))) {
                newRow = rowset.getCurrentRow();
                break;
            }
        }
        assertNotNull(newRow);
        assertEquals(newRow.getColumnObject(fields.find("gid")), NEW_RECORD_ID);
        assertEquals(newRow.getColumnObject(fields.find("tid")), NEW_RECORD_ID);
        assertEquals(newRow.getColumnObject(fields.find("kid")), NEW_RECORD_ID);
        assertEquals(newRow.getColumnObject(fields.find("gname")), NEW_RECORD_NAME_G);
        assertEquals(newRow.getColumnObject(fields.find("tname")), NEW_RECORD_NAME_T);
        assertEquals(newRow.getColumnObject(fields.find("kname")), NEW_RECORD_NAME_K);
        // Delete operation
        rowset.delete();
        client.commit();
        rowset.refresh();
        fields = rowset.getFields();
        assertEquals(oldRowsetSize, rowset.size());

        newRow = null;
        rowset.beforeFirst();
        while (rowset.next()) {
            if (NEW_RECORD_ID.intValue() == rowset.getInt(fields.find("gid"))) {
                newRow = rowset.getCurrentRow();
                break;
            }
        }
        assertNull(newRow);
    }

    public static void tormentData(AppClient aAppClient) throws RowsetException, InvalidCursorPositionException, Exception {
        String queryId = "129232748904692";
        PlatypusQuery query = aAppClient.getAppQuery(queryId);
        Rowset rowset = query.execute();

        assertNotNull(rowset.getFlowProvider());
        assertTrue(rowset.getFlowProvider() instanceof PlatypusThreeTierFlowProvider);
        PlatypusThreeTierFlowProvider flow = (PlatypusThreeTierFlowProvider) rowset.getFlowProvider();
        assertEquals(flow.getEntityId(), queryId);
        assertTrue(rowset.getFields().get(1).isPk());
        assertTrue(rowset.size() > 0);

        rowset.beforeFirst();
        boolean rowMet = false;
        int newValue = (new Random()).nextInt();
        while (rowset.next()) {
            Integer id = rowset.getInt(rowset.getFields().find("CUSTOMER_ID"));
            assertNotNull(id);
            if (id == 2) {
                rowMet = true;
                assertNull(rowset.getObject(rowset.getFields().find("CUSTOMER_ADDRESS")));
                rowset.updateObject(rowset.getFields().find("CUSTOMER_ADDRESS"), String.valueOf(newValue));
            }
        }
        assertTrue(rowMet);
        aAppClient.commit();
        rowset.refresh();
        assertTrue(rowset.size() > 0);
        rowMet = false;
        rowset.beforeFirst();
        while (rowset.next()) {
            Integer id = rowset.getInt(rowset.getFields().find("CUSTOMER_ID"));
            assertNotNull(id);
            if (id == 2) {
                assertEquals(String.valueOf(newValue), rowset.getString(rowset.getFields().find("CUSTOMER_ADDRESS")));
                rowset.updateObject(rowset.getFields().find("CUSTOMER_ADDRESS"), null);
                rowMet = true;
            }
        }
        assertTrue(rowMet);
        aAppClient.commit();
    }
}
