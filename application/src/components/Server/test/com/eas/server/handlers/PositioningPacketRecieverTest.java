/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

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
     * Test of send method, of class PositioningPacketReciever.
     */
    @Test
    public void testServiceUrl() {
        System.out.println("serviceUrl");
        PositioningPacketReciever.ServiceUrl su = new PositioningPacketReciever.ServiceUrl("WialonIPS://81.20.100.54:9090");
        String aHost = "81.20.100.54";
        int aPort = 9090;
        String aProtocolName = "WialonIPS";
        assertEquals(aHost, su.getHost());
        assertEquals(aPort, su.getPort());
        assertEquals(aProtocolName, su.getProtocol());
        su = new PositioningPacketReciever.ServiceUrl("WialonIPS://optimus.altsoft.biz:9090");
        aHost = "optimus.altsoft.biz";
        assertEquals(aHost, su.getHost());
        assertEquals(aPort, su.getPort());
        assertEquals(aProtocolName, su.getProtocol());
    }
}
