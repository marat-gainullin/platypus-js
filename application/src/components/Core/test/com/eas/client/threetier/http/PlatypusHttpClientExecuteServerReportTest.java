/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.PlatypusClientSecurityTest;
import com.eas.client.settings.PlatypusConnectionSettings;
import com.eas.client.threetier.requests.ExecuteServerReportRequest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kl
 */
public class PlatypusHttpClientExecuteServerReportTest {

    protected PlatypusConnectionSettings settings;
    protected PlatypusConnectionSettings badUrlSettings;
    private String MODULE_NAME = "TestReportCore";
    protected PlatypusHttpClient client;

    @Before
    public void prepareSettings() throws Exception {
        settings = new PlatypusConnectionSettings();
        settings.setName("Platypus http test connection");
        settings.setUrl(PlatypusHttpTestConstants.HTTP_REQUEST_URL);
        badUrlSettings = new PlatypusConnectionSettings();
        badUrlSettings.setName("Platypus http bad url test connection");
        badUrlSettings.setUrl(PlatypusHttpTestConstants.HTTP_REQUEST_BAD_URL);
        try {
            client = new PlatypusHttpClient(settings);
            client.login(PlatypusClientSecurityTest.USER0_NAME, PlatypusClientSecurityTest.USER_PASSWORD.toCharArray());
            client.createServerModule(MODULE_NAME);// for parameters passing to report as stateless module
        } catch (Exception ex) {
            Logger.getLogger(PlatypusHttpClientExecuteServerReportTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @After
    public void disconnect() {
        try {
            client.logout();
            assertNull(client.getPrincipal());
            client.shutdown();
        } catch (Exception ex) {
            Logger.getLogger(PlatypusHttpClientExecuteServerReportTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Test
    public void testExecuteServerReport() throws Exception {
        System.out.println("executeServerReport");
        ExecuteServerReportRequest rq = new ExecuteServerReportRequest(IDGenerator.genID(), MODULE_NAME);
        client.executeRequest(rq);
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
        client.executeRequest(srq);
        byte[] res = ((ExecuteServerReportRequest.Response) srq.getResponse()).getResult();
        assertNotNull(res);
        assertTrue(res.length == 17408);

    }
}
