/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.ErrorResponse;
import com.eas.client.threetier.binary.PlatypusResponseReader;
import com.eas.client.threetier.binary.PlatypusResponseWriter;
import com.eas.client.threetier.binary.RequestsTags;
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
public class ErrorResponseTest {

    public ErrorResponseTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testWrite() throws Exception {
        System.out.println("write");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ErrorResponse instance = new ErrorResponse(1, "Test error");
        ProtoWriter writer = new ProtoWriter(outStream);
        PlatypusResponseWriter.write(instance, writer);
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(RequestsTags.TAG_ERROR_RESPONSE, reader.getNextTag());
        assertEquals(instance.getRequestID(), reader.getLong());
        assertEquals(RequestsTags.TAG_RESPONSE_DATA, reader.getNextTag());
        PlatypusResponseReader bodyReader = new PlatypusResponseReader(reader.getSubStreamData());
        ErrorResponse instance1 = new ErrorResponse(instance.getRequestID(), "");
        instance1.accept(bodyReader);
        assertEquals(instance.getError(), instance1.getError());
        assertEquals(RequestsTags.TAG_RESPONSE_END, reader.getNextTag());
        assertEquals(0, reader.getCurrentTagSize());
        assertEquals(CoreTags.TAG_EOF, reader.getNextTag());
        assertEquals(0, reader.getCurrentTagSize());
    }
}
