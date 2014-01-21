/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.PlatypusNativeClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class ExecuteServerReportRequestTest {

    private static PlatypusNativeClient appClient;
    static String MODULE_NAME = "TestReportCore";

    public ExecuteServerReportRequestTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        appClient = new PlatypusNativeClient("platypus://localhost:8500/");
        try {
            appClient.login("testuser3", "test".toCharArray());
            appClient.createServerModule(MODULE_NAME);
        } catch (Exception ex) {
            Logger.getLogger(ExecuteServerReportRequestTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        assertNotNull(appClient);
        appClient.disposeServerModule(MODULE_NAME);
        appClient.logout();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testExecuteServerReport() throws Exception {
        System.out.println("executeServerReport");
        ExecuteServerReportRequest rq = new ExecuteServerReportRequest(IDGenerator.genID(), MODULE_NAME);
        appClient.executeRequest(rq);
        byte[] result = ((ExecuteServerReportRequest.Response) rq.getResponse()).getResult();
        assertNotNull(result);
        assertTrue(result.length == 18432);
    }

    @Test
    public void testExecuteServerReportWithSetParams() throws Exception {
        System.out.println("executeServerReportWithSetParams");
        ExecuteServerReportRequest srq = new ExecuteServerReportRequest(IDGenerator.genID(), MODULE_NAME);
        ExecuteServerReportRequest.NamedArgument[] args = {new ExecuteServerReportRequest.NamedArgument("id", 124772775584390l)};
        srq.setArguments(args);
        appClient.executeRequest(srq);
        byte[] res = ((ExecuteServerReportRequest.Response) srq.getResponse()).getResult();
        assertNotNull(res);
        assertTrue(res.length == 17408);
    }
}
