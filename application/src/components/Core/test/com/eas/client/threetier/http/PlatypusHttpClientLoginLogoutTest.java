/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.PlatypusClientTest;
import javax.security.auth.login.LoginException;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author kl
 */
public class PlatypusHttpClientLoginLogoutTest {

    @Test
    public void loginLogout() throws Exception {
        PlatypusHttpClient client = new PlatypusHttpClient(PlatypusHttpTestConstants.HTTP_REQUEST_URL);
        String sessionId = client.login(PlatypusClientTest.TEST_LOGIN, PlatypusClientTest.TEST_PASSWD.toCharArray());
        if (sessionId == null || sessionId.isEmpty()) {
            fail("Failed to retrive session id");
        }
        client.logout();
        assertNull(client.getPrincipal());
        client.shutdown();
    }

    @Test
    public void loginBadUrl() throws Exception {
        PlatypusHttpClient client = new PlatypusHttpClient(PlatypusHttpTestConstants.HTTP_REQUEST_BAD_URL);
        try {
            String sessionId = client.login(PlatypusClientTest.TEST_LOGIN, PlatypusClientTest.TEST_PASSWD.toCharArray());
            if (sessionId == null || sessionId.isEmpty()) {
                fail("Failed to retrive session id");
            }
            fail("Bad url should produce an exception");
        } catch (LoginException ex) {
            // ok. Got Login Exception
        }
        client.shutdown();
    }
}
