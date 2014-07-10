/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.PlatypusClientSecurityTest;
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

    private final String MODULE_NAME = "TestReportCore";
    protected PlatypusHttpClient client;

    @Before
    public void prepareSettings() throws Exception {
        try {
            client = new PlatypusHttpClient(PlatypusHttpTestConstants.HTTP_REQUEST_URL);
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
        fail("There should be a test of ordinary server method call with report response.");
    }

}
