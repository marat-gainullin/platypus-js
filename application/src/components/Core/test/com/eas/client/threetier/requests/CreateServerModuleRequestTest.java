/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.utils.IDGenerator;
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
public class CreateServerModuleRequestTest
{
    public CreateServerModuleRequestTest()
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
     * Test of getModuleName method, of class CreateServerModuleRequest.
     */
    @Test
    public void testGetModuleName()
    {
        System.out.println("getModuleName");
        String expResult = String.valueOf(IDGenerator.genID());
        CreateServerModuleRequest instance = new CreateServerModuleRequest(IDGenerator.genID(), expResult);
        String result = instance.getModuleName();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeData method, of class CreateServerModuleRequest.
     * @throws Exception
     */
    @Test
    public void testWriteData() throws Exception
    {
        System.out.println("writeData");
        String moduleName = String.valueOf(IDGenerator.genID());
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        CreateServerModuleRequest instance = new CreateServerModuleRequest(IDGenerator.genID(), moduleName);
        PlatypusRequestWriter bodyWriter = new PlatypusRequestWriter(outStream);
        instance.accept(bodyWriter);
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(RequestsTags.TAG_MODULE_NAME, reader.getNextTag());
        assertEquals(moduleName, reader.getString());
        assertEquals(CoreTags.TAG_EOF, reader.getNextTag());
    }

    /**
     * Test of readData method, of class CreateServerModuleRequest.
     * @throws Exception 
     */
    @Test
    public void testReadData() throws Exception
    {
        System.out.println("readData");
        String moduleName = String.valueOf(IDGenerator.genID());
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(RequestsTags.TAG_MODULE_NAME, moduleName);
        writer.flush();
        
        CreateServerModuleRequest instance = new CreateServerModuleRequest(IDGenerator.genID());
        PlatypusRequestReader bodyReader = new PlatypusRequestReader(outStream.toByteArray());
        instance.accept(bodyReader);
        
        assertEquals(moduleName, instance.getModuleName());
    }
}
