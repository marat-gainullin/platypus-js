/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.platypus.PlatypusResponseWriter;
import com.eas.client.threetier.platypus.RequestsTags;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class ResponseTest
{
    public ResponseTest()
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

    /**
     * Test of write method, of class Response.
     * @throws Exception
     */
    @Test
    public void testWrite() throws Exception
    {
        System.out.println("write");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        KeepAliveRequest.Response instance = new KeepAliveRequest.Response(1);
        PlatypusResponseWriter.write(instance, writer);
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(RequestsTags.TAG_RESPONSE, reader.getNextTag());
        assertEquals(instance.getRequestID(), reader.getLong());
        assertEquals(RequestsTags.TAG_RESPONSE_DATA, reader.getNextTag());
        assertEquals(0, reader.getCurrentTagSize());
        assertEquals(CoreTags.TAG_STREAM, reader.getNextTag());
        assertEquals(0, reader.getCurrentTagSize());
        assertEquals(RequestsTags.TAG_RESPONSE_END, reader.getNextTag());
        assertEquals(0, reader.getCurrentTagSize());
    }

    /**
     * Test of getRequestID method, of class Response.
     */
    @Test
    public void testGetRequestID()
    {
        System.out.println("getRequestID");
        KeepAliveRequest.Response instance = new KeepAliveRequest.Response(1);
        long expResult = 1L;
        long result = instance.getRequestID();
        assertEquals(expResult, result);
    }
}
