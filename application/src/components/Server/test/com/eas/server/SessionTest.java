/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.login.AppPlatypusPrincipal;
import java.io.IOException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class SessionTest {

    public SessionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of cleanup method, of class Session.
     * @throws IOException 
     */
    @Test
    public void testCleanup() throws IOException {
        System.out.println("cleanup");
        Session instance = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("test", null));
        instance.cleanup();
    }

    /**
     * Test of getSessionId method, of class Session.
     */
    @Test
    public void testGetSessionId() {
        System.out.println("getSessionId");
        String sessionID = String.valueOf(IDGenerator.genID());
        Session instance = new Session(null, sessionID, new AppPlatypusPrincipal("test", null));
        String result = instance.getId();
        assertEquals(sessionID, result);
    }

    /**
     * Test of getCTime method, of class Session.
     */
    @Test
    public void testGetCTime() {
        System.out.println("getCTime");
        Session instance = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("test", null));
        long result = instance.getCTime();
        assertTrue(result > 0);
    }

    /**
     * Test of getATime method, of class Session.
     */
    @Test
    public void testGetATime() {
        System.out.println("getATime");
        Session instance = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("test", null));
        long expResult = instance.accessed();
        long result = instance.getATime();
        assertEquals(expResult, result);
    }

    /**
     * Test of accessed method, of class Session.
     */
    @Test
    public void testAccessed() {
        System.out.println("accessed");
        Session instance = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("test", null));
        long result = instance.accessed();
    }

    /**
     * Test of getUser method, of class Session.
     */
    @Test
    public void testGetUser() {
        System.out.println("getUser");
        Session instance = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("test", null));
        assertEquals("test", instance.getUser());
    }
}