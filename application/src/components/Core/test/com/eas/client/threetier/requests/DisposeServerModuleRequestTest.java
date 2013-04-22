/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.Requests;
import com.eas.client.threetier.binary.PlatypusRequestReader;
import com.eas.client.threetier.binary.PlatypusRequestWriter;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.proto.CoreTags;
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
public class DisposeServerModuleRequestTest
{
    public DisposeServerModuleRequestTest()
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
     * Test of getModuleID method, of class DisposeServerModuleRequest.
     */
    @Test
    public void testGetScriptID()
    {
        System.out.println("getScriptID");
        String expResult = IDGenerator.genID().toString();
        DisposeServerModuleRequest instance = new DisposeServerModuleRequest(IDGenerator.genID(), expResult);
        assertEquals(Requests.rqDisposeServerModule, instance.getType());
        String result = instance.getModuleName();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeData method, of class DisposeServerModuleRequest.
     * @throws Exception
     */
    @Test
    public void testWriteData() throws Exception
    {
        System.out.println("writeData");
        String moduleID = IDGenerator.genID().toString();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DisposeServerModuleRequest instance = new DisposeServerModuleRequest(IDGenerator.genID(), moduleID);
        PlatypusRequestWriter bodyWriter = new PlatypusRequestWriter(outStream);
        instance.accept(bodyWriter);
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(RequestsTags.TAG_MODULE_NAME, reader.getNextTag());
        assertEquals(moduleID, reader.getString());
        assertEquals(CoreTags.TAG_EOF, reader.getNextTag());
    }

    /**
     * Test of readData method, of class DisposeServerModuleRequest.
     * @throws Exception 
     */
    @Test
    public void testReadData() throws Exception
    {
        System.out.println("readData");
        String moduleId = IDGenerator.genID().toString();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(RequestsTags.TAG_MODULE_NAME, moduleId);
        writer.flush();
        DisposeServerModuleRequest instance = new DisposeServerModuleRequest(IDGenerator.genID());
        PlatypusRequestReader bodyReader = new PlatypusRequestReader(outStream.toByteArray());
        instance.accept(bodyReader);
        assertEquals(moduleId, instance.getModuleName());
    }
}
