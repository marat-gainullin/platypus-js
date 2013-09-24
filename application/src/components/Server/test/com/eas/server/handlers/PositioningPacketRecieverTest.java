/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.sensors.positioning.PositioningPacket;
import java.util.Date;
import java.util.regex.Matcher;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author AB
 */
public class PositioningPacketRecieverTest {
    
    public PositioningPacketRecieverTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

     /**
     * Test of putWaitingRequest method, of class PositioningPacketReciever.
     */
    @Test
    public void testUrlParse() {
        System.out.println("urlParse");
        String aUrl = "http://ya.ru:80 https://ab:ab@google.com:8080 ftp://mail.ru:23 tst://rambler.ru";
        Matcher m = PositioningPacketReciever.URL_PATTERN.matcher(aUrl);
        StringBuilder result = new StringBuilder();
        String delimiter = "";
        while (m.find()) {
            result.append(delimiter);
            result.append(m.group("SCHEMA"));
            result.append("://");
            if (m.group("USER") != null) {
                result.append(m.group("USER"));
                result.append(":");
                if (m.group("PASS") != null) {
                    result.append(m.group("PASS"));
                }
                result.append("@");
            }
            result.append(m.group("URL"));
            if (m.group("PORT") != null) {
                 result.append(":");
                result.append(m.group("PORT"));
            }
            delimiter = " ";
        }
        assertEquals(aUrl, result.toString());
    }

    /**
     * Test of putWaitingRequest method, of class PositioningPacketReciever.
     *
     * @throws Exception
     */
    @Test
    public void testSendData() throws Exception {
        System.out.println("sendData");
        PositioningPacketReciever reciever = new PositioningPacketReciever(null, null);
        PositioningPacket packet = new PositioningPacket();
        packet.setImei("111111111111111");
        packet.setTime(new Date());
        packet.setValidity(true);
        packet.setSOS(false);
        packet.setLatitude(40.99f);
        packet.setLongtitude(57.01f);
        Object session = reciever.send(packet, "localhost", 8181, "https", "", "", "/testHttpPushServlet/testHttpPushServlet");
        assert session instanceof IoSession;
        IoSession ioSession = (IoSession) session;
        ReadFuture read = ioSession.read();
        if (read.awaitUninterruptibly(10000)) {
            Integer statusCode = (Integer)ioSession.getAttribute("status");
            assertEquals(statusCode.intValue(), 200);
        } else {
            fail("No data for read in session.");
        }
        session = reciever.send(packet, "localhost", 8080, "http", "", "", "/testHttpPushServlet/testHttpPushServlet");
        assert session instanceof IoSession;
        ioSession = (IoSession) session;
        read = ioSession.read();
        if (read.awaitUninterruptibly(10000)) {
            Integer statusCode = (Integer)ioSession.getAttribute("status");
            assertEquals(statusCode.intValue(), 200);
        } else {
            fail("No data for read in session.");
        }
    }
}
