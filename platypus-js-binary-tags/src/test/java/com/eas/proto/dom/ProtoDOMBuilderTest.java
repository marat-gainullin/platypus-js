/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.proto.dom;

import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author pk
 */
public class ProtoDOMBuilderTest {

    private static final int TAG_SQL_QUERY = 1;
    private static final int TAG_DATABASE_ID = 2;
    private static final int TAG_SQL_PARAMETER = 3;
    private static final int TAG_SQL_PARAMETER_TYPE = 4;
    private static final int TAG_SQL_PARAMETER_VALUE = 5;

    public ProtoDOMBuilderTest() {
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
     * Test of buildDOM method, of class ProtoDOMBuilder.
     * @throws Exception 
     */
    @Test
    public void testBuildDOM() throws Exception {
        System.out.println("buildDOM");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(TAG_SQL_QUERY, "select * from dual.dummy");
        writer.put(TAG_DATABASE_ID, 28348934723984L);
        for (int i = 0; i < 3; i++) {
            writer.put(TAG_SQL_PARAMETER);
            ByteArrayOutputStream subStream = new ByteArrayOutputStream();
            ProtoWriter subWriter = new ProtoWriter(subStream);
            subWriter.put(TAG_SQL_PARAMETER_TYPE, i);
            subWriter.put(TAG_SQL_PARAMETER_VALUE, new BigDecimal(i));
            subWriter.put(100, 65370);
            subWriter.flush();
            writer.put(CoreTags.TAG_STREAM, subStream.toByteArray());
        }
        BigDecimal entityId = new BigDecimal(124349290914389600L);
        writer.put(101, entityId);
        writer.put(102, entityId);
        writer.flush();
        ProtoNode result = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertEquals("select * from dual.dummy", result.getChild(TAG_SQL_QUERY).getString());
        assertEquals(28348934723984L, result.getChild(TAG_DATABASE_ID).getLong());
        assertEquals(entityId, result.getChild(101).getBigDecimal());
        assertEquals(BigDecimal.valueOf(entityId.longValue()), result.getChild(102).getBigDecimal());
        Iterator<ProtoNode> iterator = result.iterator();
        int paramCount = 0;
        while (iterator.hasNext()) {
            ProtoNode node = iterator.next();
            if (node.getNodeTag() == TAG_SQL_PARAMETER) {
                assertEquals(paramCount, node.getChild(TAG_SQL_PARAMETER_TYPE).getInt());
                BigDecimal bd = node.getChild(TAG_SQL_PARAMETER_VALUE).getBigDecimal();
                assertEquals(new BigDecimal(paramCount), bd);
                assertEquals(65370, node.getChild(100).getInt());
                paramCount++;
            }
        }
        assertEquals(3, paramCount);
    }

    @Test
    public void testEmptySubStream() throws Exception {
        ByteArrayOutputStream subOut = new ByteArrayOutputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtoWriter pw = new ProtoWriter(out);
        pw.put(TEST_TAG_1);
        pw.put(CoreTags.TAG_STREAM, subOut);
        pw.put(TEST_TAG_2, "test");
        pw.flush();
        ProtoReader pr = new ProtoReader(new ByteArrayInputStream(out.toByteArray()));
        assertEquals(TEST_TAG_1, pr.getNextTag());
        byte[] subStreamData = pr.getSubStreamData();
        assertTrue(subStreamData != null);
        assertEquals(0, subStreamData.length);
        assertEquals(TEST_TAG_2, pr.getNextTag());
        String readTestData = pr.getString();
        assertEquals("test", readTestData);
        
        ProtoNode node = ProtoDOMBuilder.buildDOM(out.toByteArray());
    }
    
    private static final int TEST_TAG_1 = 10;
    private static final int TEST_TAG_11 = 11;
    private static final int TEST_TAG_2 = 20;

    @Test
    public void testCompressedSubStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtoWriter pw = new ProtoWriter(out);
        pw.put(TEST_TAG_1, "sample");
        ByteArrayOutputStream subOut = new ByteArrayOutputStream();
        ProtoWriter subPw = new ProtoWriter(subOut);
        subPw.put(TEST_TAG_11, "subSample");

        ByteArrayOutputStream zSubOut = new ByteArrayOutputStream();
        try (ZipOutputStream zStream = new ZipOutputStream(zSubOut)) {
            ZipEntry ze = new ZipEntry("testData");
            zStream.putNextEntry(ze);
            zStream.write(subOut.toByteArray());
            zStream.flush();
        }

        pw.put(TEST_TAG_2);
        pw.put(CoreTags.TAG_COMPRESSED_STREAM, zSubOut.toByteArray());

        byte written[] = out.toByteArray();

        ProtoNode readDom = ProtoDOMBuilder.buildDOM(written);
        assertTrue(readDom.containsChild(TEST_TAG_1));
        assertTrue(readDom.containsChild(TEST_TAG_2));
        assertEquals("sample", readDom.getChild(TEST_TAG_1).getString());
        ProtoNode subNode = readDom.getChild(TEST_TAG_2);
        assertTrue(subNode.containsChild(TEST_TAG_11));
        assertEquals("subSample", subNode.getChild(TEST_TAG_11).getString());
    }
}
