/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.binary.PlatypusResponseReader;
import com.eas.client.threetier.binary.PlatypusResponseWriter;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;
import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author pk
 */
public class CreateServerModuleResponseTest
{
    public CreateServerModuleResponseTest()
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
     * Test of getModuleID method, of class CreateServerModuleResponse.
     */
    @Test
    public void testGetModuleID()
    {
        System.out.println("getModuleID");
        Set<String> funcs = Collections.emptySet();
        CreateServerModuleResponse instance = new CreateServerModuleResponse(IDGenerator.genID(), "Test module ID", funcs, true);
        String expResult = "Test module ID";
        String result = instance.getModuleName();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeData method, of class CreateServerModuleResponse.
     * @throws Exception 
     */
    @Test
    public void testWriteData() throws Exception
    {
        System.out.println("writeData");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Set<String> funcs = Collections.emptySet();
        CreateServerModuleResponse instance = new CreateServerModuleResponse(IDGenerator.genID(), "Test module ID", funcs, true);
        PlatypusResponseWriter bodyWriter = new PlatypusResponseWriter(outStream);
        instance.accept(bodyWriter);
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(RequestsTags.TAG_MODULE_ID, reader.getNextTag());
        assertEquals("Test module ID", reader.getString());
        assertEquals(CoreTags.TAG_EOF, reader.getNextTag());
    }

    /**
     * Test of readData method, of class CreateServerModuleResponse.
     * @throws Exception
     */
    @Test
    public void testReadData() throws Exception
    {
        System.out.println("readData");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(RequestsTags.TAG_MODULE_ID, "Test module ID");
        writer.flush();
        CreateServerModuleResponse instance = new CreateServerModuleResponse(IDGenerator.genID(), null, null, true);
        PlatypusResponseReader bodyReader = new PlatypusResponseReader(outStream.toByteArray());
        instance.accept(bodyReader);
        assertEquals("Test module ID", instance.getModuleName());
    }
}
