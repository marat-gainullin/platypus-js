/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.proto;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.eas.proto.dom.ProtoNode;
import com.eas.proto.dom.ProtoDOMBuilder;
import java.math.BigInteger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Types;
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

    /**
     * Test of putJDBCCompatible method, of class ProtoWriter.
     * @throws Exception
     */
    @Test
    public void testPutJDBCCompatible() throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.putJDBCCompatible(1, Types.NUMERIC, 234L);
        writer.putJDBCCompatible(2, Types.BIGINT, null);
        writer.flush();
        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        ProtoReader reader = new ProtoReader(inStream);
        assertEquals(1, reader.getNextTag());
        assertEquals(new BigDecimal(234L), reader.getJDBCCompatible(Types.NUMERIC));
        assertEquals(2, reader.getNextTag());
        assertNull(reader.getJDBCCompatible(Types.BIGINT));

    }

    @Test
    public void testLongAsBigInt() throws IOException, ProtoReaderException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        final long val1 = 124350724232787552L;
        writer.putJDBCCompatible(1, Types.BIGINT, new BigDecimal(val1));
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(1, reader.getNextTag());
        assertEquals(BigInteger.valueOf(val1), reader.getJDBCCompatible(1, Types.BIGINT));
    }

    @Test
    public void testLongAsBIGINT() throws IOException, ProtoReaderException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        final long val1 = 124350724232787552L;
        writer.putJDBCCompatible(1, Types.BIGINT, new BigDecimal(val1));
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(1, reader.getNextTag());
        assertEquals(BigInteger.valueOf(val1), reader.getJDBCCompatible(1, Types.BIGINT));

        ProtoNode node = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(node.containsChild(1));
        assertEquals(BigInteger.valueOf(val1), node.getChild(1).getJDBCCompatible(Types.BIGINT));
    }

    @Test
    public void testLongAsDECIMAL() throws IOException, ProtoReaderException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        final long val1 = 124350724232787552L;
        writer.putJDBCCompatible(1, Types.DECIMAL, new BigDecimal(val1));
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(1, reader.getNextTag());
        assertEquals(BigDecimal.valueOf(val1), reader.getJDBCCompatible(1, Types.DECIMAL));

        ProtoNode node = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(node.containsChild(1));
        assertEquals(BigDecimal.valueOf(val1), node.getChild(1).getJDBCCompatible(Types.DECIMAL));
    }

    @Test
    public void testLongAsNUMERIC() throws IOException, ProtoReaderException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        final long val1 = 124350724232787552L;
        writer.putJDBCCompatible(1, Types.NUMERIC, new BigDecimal(val1));
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(1, reader.getNextTag());
        assertEquals(BigDecimal.valueOf(val1), reader.getJDBCCompatible(1, Types.NUMERIC));

        ProtoNode node = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(node.containsChild(1));
        assertEquals(BigDecimal.valueOf(val1), node.getChild(1).getJDBCCompatible(Types.NUMERIC));
    }

    @Test
    public void testShortAsSmallInt() throws IOException, ProtoReaderException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        final short val1 = 12435;
        writer.putJDBCCompatible(1, Types.SMALLINT, val1);
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(1, reader.getNextTag());
        assertEquals(val1, reader.getJDBCCompatible(1, Types.SMALLINT));

        ProtoNode node = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(node.containsChild(1));
        assertEquals(val1, node.getChild(1).getJDBCCompatible(Types.SMALLINT));
    }

    @Test
    public void testFloatAsFLOAT() throws IOException, ProtoReaderException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        final float val1 = 124.2f;
        writer.putJDBCCompatible(1, Types.FLOAT, val1);
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(1, reader.getNextTag());
        assertEquals(val1, reader.getJDBCCompatible(1, Types.FLOAT));

        ProtoNode node = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(node.containsChild(1));
        assertTrue(Math.abs(val1 - (Float) node.getChild(1).getJDBCCompatible(Types.FLOAT)) < 1e-5);
    }

    @Test
    public void testDoubleAsREAL() throws IOException, ProtoReaderException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        final double val1 = 12435.2f;
        writer.putJDBCCompatible(1, Types.DOUBLE, val1);
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(1, reader.getNextTag());
        assertEquals(val1, reader.getJDBCCompatible(1, Types.DOUBLE));

        ProtoNode node = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(node.containsChild(1));
        assertEquals(val1, node.getChild(1).getJDBCCompatible(Types.DOUBLE));
    }

    @Test
    public void testDoubleAsDOUBLE() throws IOException, ProtoReaderException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        final double val1 = 12435.2f;
        writer.putJDBCCompatible(1, Types.DOUBLE, val1);
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(outStream.toByteArray()));
        assertEquals(1, reader.getNextTag());
        assertEquals(val1, reader.getJDBCCompatible(1, Types.DOUBLE));

        ProtoNode node = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(node.containsChild(1));
        assertEquals(val1, node.getChild(1).getJDBCCompatible(Types.DOUBLE));
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
