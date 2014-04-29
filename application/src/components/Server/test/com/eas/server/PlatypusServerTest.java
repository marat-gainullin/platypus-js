/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.DatabaseAppCache;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.settings.DbConnectionSettings;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class PlatypusServerTest {

    private static PlatypusServer server;
    private static int TEST_PORT = 60000;

    public PlatypusServerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        String url = "jdbc:oracle:thin:@asvr:1521/adb";
        String login = "eas";
        String passwd = "eas";
        if (!url.startsWith("jdbc:")) {
            throw new Exception("Only jdbc urls are supported. Unsupported URL " + url);
        }
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl(url);
        settings.setUser(login);
        settings.setPassword(passwd);
        GeneralResourceProvider.getInstance().registerDatasource("testDb", settings);
        SSLContext sslContext = ServerMain.createSSLContext();
        server = new PlatypusServer(new ScriptedDatabasesClient(new DatabaseAppCache("jndi://testDb"), "testDb", true, null), sslContext, new InetSocketAddress[]{new InetSocketAddress("localhost", TEST_PORT)}, new HashMap<Integer, String>(), null, null, null, new HashSet<String>(), null);
        server.start();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (server == null) {
            fail("Sever didn't started.");
        } else {
            server.stop(2, TimeUnit.SECONDS);
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void dummyTest() {
    }

}
