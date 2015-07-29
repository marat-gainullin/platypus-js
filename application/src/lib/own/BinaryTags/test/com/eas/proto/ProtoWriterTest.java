/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.proto;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class ProtoWriterTest {

    public ProtoWriterTest() {
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

    /**
     * Test of put method, of class ProtoWriter.
     * @throws Exception
     */
    @Test
    public void testPut_int_String() throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(1, "");
        writer.flush();
        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        ProtoReader reader = new ProtoReader(inStream);
        assertEquals(1, reader.getNextTag());
        assertEquals("", reader.getString());
    }

    /**
     * Test of put method, of class ProtoWriter.
     * @throws Exception
     */
    @Test
    public void testPut_int_BigDecimal() throws Exception {
        BigDecimal val1 = new BigDecimal(0), val2 = new BigDecimal(10000), val3 = new BigDecimal(0.3747);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(1, val1);
        writer.put(2, val2);
        writer.put(3, val3);
        writer.flush();
        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        ProtoReader reader = new ProtoReader(inStream);
        assertEquals(1, reader.getNextTag());
        assertEquals(val1, reader.getBigDecimal());
        assertEquals(val2, reader.getBigDecimal(2));
        assertEquals(val3, reader.getBigDecimal(3));
    }
    
    private static final int TEST_TAG_1 = 10;
    private static final int TEST_TAG_11 = 11;
    private static final int TEST_TAG_2 = 20;
    
    @Test
    public void testCompressedSubStream() throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtoWriter pw = new ProtoWriter(out);
        pw.put(TEST_TAG_1, "sample");
        ByteArrayOutputStream subOut = new ByteArrayOutputStream();
        ProtoWriter subPw = new ProtoWriter(subOut);
        subPw.put(TEST_TAG_11, "subSample");
        
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        try (ZipOutputStream zStream = new ZipOutputStream(dataStream)) {
            zStream.putNextEntry(new ZipEntry("testData"));
            zStream.write(subOut.toByteArray());
            zStream.flush();
        }
        
        pw.put(TEST_TAG_2);
        pw.put(CoreTags.TAG_COMPRESSED_STREAM, dataStream.toByteArray());
        
        byte written[] = out.toByteArray();
        
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(written));
        int tag1 = reader.getNextTag();
        assertEquals(tag1, TEST_TAG_1);
        assertEquals("sample", reader.getString());
        
        int tag2 = reader.getNextTag();
        assertEquals(tag2, TEST_TAG_2);
        InputStream subStream = reader.getSubStream();
        assertNotNull(subStream);
        ProtoReader subReader = new ProtoReader(subStream);
        int subTag = subReader.getNextTag();
        assertEquals(subTag, TEST_TAG_11);
        assertEquals("subSample", subReader.getString());
    }
}
