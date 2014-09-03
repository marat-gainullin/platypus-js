/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.platypus.PlatypusResponseReader;
import com.eas.client.threetier.platypus.PlatypusResponseWriter;
import com.eas.client.threetier.platypus.RequestsTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author pk
 */
public class LoginResponseTest
{
    public LoginResponseTest()
    {
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
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testGetSessionID()
    {
        final String sessionId = String.valueOf(IDGenerator.genID());
        LoginRequest.Response rsp = new LoginRequest.Response(1, sessionId);
        assertEquals(sessionId, rsp.getSessionId());
    }

    @Test
    public void testWriteData() throws Exception
    {
        System.out.println("writeData");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final String sessionId = String.valueOf(IDGenerator.genID());
        LoginRequest.Response rsp = new LoginRequest.Response(1, sessionId);
        PlatypusResponseWriter bodyWriter = new PlatypusResponseWriter(outStream);
        rsp.accept(bodyWriter);
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(sessionId, reader.getString(RequestsTags.TAG_SESSION_ID));
    }

    /**
     * Test of readData method, of class LoginRequest.
     * @throws Exception
     */
    @Test
    public void testReadData() throws Exception
    {
        System.out.println("readData");
        final String sessionId = String.valueOf(IDGenerator.genID());
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(RequestsTags.TAG_SESSION_ID, sessionId);
        writer.flush();
        LoginRequest.Response rsp = new LoginRequest.Response(1, null);
        PlatypusResponseReader bodyReader = new PlatypusResponseReader(outStream.toByteArray());
        rsp.accept(bodyReader);
        assertEquals(sessionId, rsp.getSessionId());
    }
}
