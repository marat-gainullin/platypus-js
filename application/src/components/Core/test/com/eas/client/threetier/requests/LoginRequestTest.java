/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.threetier.requests;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.binary.PlatypusRequestReader;
import com.eas.client.threetier.binary.PlatypusRequestWriter;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.proto.ProtoWriter;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import java.io.ByteArrayOutputStream;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author pk
 */
public class LoginRequestTest {

    public LoginRequestTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getLogin method, of class LoginRequest.
     */
    @Test
    public void testGetLogin()
    {
        System.out.println("getLogin");
        LoginRequest instance = new LoginRequest(IDGenerator.genID(), "login", "passw");
        assertEquals("login", instance.getLogin());
    }

    /**
     * Test of getPassword method, of class LoginRequest.
     */
    @Test
    public void testGetPassword()
    {
        System.out.println("getPassword");
        LoginRequest instance = new LoginRequest(IDGenerator.genID(), "login", "passw");
        assertEquals("passw", instance.getPassword());
    }

    /**
     * Test of writeData method, of class LoginRequest.
     * @throws Exception
     */
    @Test
    public void testWriteData() throws Exception
    {
        System.out.println("writeData");
        ByteArrayOutputStream outStream =new ByteArrayOutputStream();
        LoginRequest instance = new LoginRequest(IDGenerator.genID(), "ll", "pp");
        PlatypusRequestWriter bodyWriter = new PlatypusRequestWriter(outStream);
        instance.accept(bodyWriter);
        final ProtoNode input = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(input.containsChild(RequestsTags.TAG_LOGIN));
        assertTrue(input.containsChild(RequestsTags.TAG_PASSWORD));
        assertEquals("ll", input.getChild(RequestsTags.TAG_LOGIN).getString());
        assertEquals("pp", input.getChild(RequestsTags.TAG_PASSWORD).getString());
    }

    /**
     * Test of readData method, of class LoginRequest.
     * @throws Exception 
     */
    @Test
    public void testReadData() throws Exception
    {
        System.out.println("readData");
        ByteArrayOutputStream outStream =new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(RequestsTags.TAG_LOGIN, "log");
        writer.put(RequestsTags.TAG_PASSWORD, "pass");
        writer.flush();
        LoginRequest instance = new LoginRequest(1);
        PlatypusRequestReader bodyReader = new PlatypusRequestReader(outStream.toByteArray());
        instance.accept(bodyReader);
        assertEquals("log", instance.getLogin());
        assertEquals("pass", instance.getPassword());
    }

}