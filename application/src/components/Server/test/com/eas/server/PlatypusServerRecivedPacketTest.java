/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.DatabasesClient;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.sensors.positioning.PacketReciever;
import com.eas.sensors.positioning.PositioningPacket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author ab
 */
public class PlatypusServerRecivedPacketTest {

    private static PlatypusServer server;
    private static int TEST_PORT = 60000;

    public PlatypusServerRecivedPacketTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        String url = "jdbc:oracle:thin:@asvr:1521/adb";
        String login = "eas";
        String passwd = "eas";
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl(url);
        settings.getInfo().setProperty("user", login);
        settings.getInfo().setProperty("password", passwd);
        SSLContext sslContext = ServerMain.createSSLContext();
        InetSocketAddress[] addresses = new InetSocketAddress[]{new InetSocketAddress("localhost", TEST_PORT), 
                                                                new InetSocketAddress("localhost", TEST_PORT+1)};
        Map<Integer, String> ports = new HashMap<>();
        ports.put(TEST_PORT, "platypus");
        ports.put(TEST_PORT+1, "asc6");
        Set<String> modules = new HashSet<>();
        modules.add("134148360246876");//"asc6"
        server = new PlatypusServer(new DatabasesClient(settings), sslContext, addresses, ports, null, null, null, modules, null);
        server.start();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (server == null) {
            fail("Sever didn't started.");
        } else {
            server.stop(2, TimeUnit.SECONDS);
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

    @Test
    public void testSimpleConnecting() throws UnknownHostException, IOException {
        PacketReciever reciever = new com.eas.server.handlers.PositioningPacketReciever(server, "134148360246876");
        try {
            Calendar cl = Calendar.getInstance();
            cl.set(2012, 7, 5, 10, 20, 30);
            for (int i = 0; i < 20; i++) {
                PositioningPacket ps = new PositioningPacket();
                ps.setImei("111111111111111");
                ps.setAltitude(23.56f);
                ps.setAzimuth(33.56f);
                ps.setLongtitude(123.56f);
                ps.setLatitude(63.56f);
                ps.setSpeed(123.6f);
                ps.setValidity(true);
                ps.setTime(cl.getTime());
                reciever.received(ps);
                cl.roll(Calendar.MINUTE, i*2);
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusServerRecivedPacketTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }
        try {
            Thread.sleep(3*1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlatypusServerRecivedPacketTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
