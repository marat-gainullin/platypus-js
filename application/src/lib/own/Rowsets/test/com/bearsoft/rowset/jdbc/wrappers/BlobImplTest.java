/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.jdbc.wrappers;

import com.bearsoft.rowset.wrappers.jdbc.BlobImpl;
import com.bearsoft.rowset.compacts.CompactBlob;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class BlobImplTest {

    protected static byte wrongTestByte = (byte) 0xce;
    protected static byte[] testData = new byte[]{0x3e, 0xf, 0x34, 0x3b, 0x2a, (byte) 0xff, (byte) 0xf3, (byte) 0xa7, 0x3c, 0x65};
    protected static byte[] truncatedTestData = new byte[]{0x3e, 0xf, 0x34, 0x3b, 0x2a};
    protected static byte[] writtenTestData = new byte[]{0x2e, 0x4f, 0x42, 0x3c, 0x2b, (byte) 0xcf, (byte) 0xb3, (byte) 0xad, (byte) 0xdc, (byte) 0x56};

    @Test
    public void freeTest() throws SQLException {
        System.out.println("freeTest");
        CompactBlob bl = new CompactBlob();
        bl.setData(testData);
        BlobImpl rbl = new BlobImpl(bl);
        rbl.free();
        try {
            rbl.getBinaryStream();
            assert false;
        } catch (SQLException ex) {
        }
    }

    @Test
    public void getBytesTest() throws SQLException {
        System.out.println("getBytesTest");
        CompactBlob bl = new CompactBlob();
        bl.setData(testData);
        BlobImpl rbl = new BlobImpl(bl);
        for (int pos = 1; pos <= testData.length; pos++) {
            byte[] bytes = rbl.getBytes(pos, 1);
            assertNotNull(bytes);
            assertEquals(bytes.length, 1);
            assertEquals(bytes[0], testData[pos - 1]);
        }
        for (int pos = 1; pos <= testData.length - 1; pos++) {
            byte[] bytes = rbl.getBytes(pos, 2);
            assertNotNull(bytes);
            assertEquals(bytes.length, 2);
            assertEquals(bytes[0], testData[pos - 1]);
            assertEquals(bytes[1], testData[pos]);
        }
        for (int pos = 1; pos <= testData.length - 2; pos++) {
            byte[] bytes = rbl.getBytes(pos, 3);
            assertNotNull(bytes);
            assertEquals(bytes.length, 3);
            assertEquals(bytes[0], testData[pos - 1]);
            assertEquals(bytes[1], testData[pos]);
            assertEquals(bytes[2], testData[pos + 1]);
        }
        byte[] bytes = rbl.getBytes(1, testData.length);
        assertNotNull(bytes);
        assertEquals(bytes.length, testData.length);
        assertArrayEquals(bytes, testData);

        for (int pos = 1; pos <= testData.length; pos++) {
            bytes = rbl.getBytes(1, 0);
            assertNotNull(bytes);
            assertEquals(bytes.length, 0);
        }
    }

    @Test
    public void getInputStream1Test() throws SQLException, IOException {
        System.out.println("getInputStream1Test");
        CompactBlob bl = new CompactBlob();
        bl.setData(testData);
        BlobImpl rbl = new BlobImpl(bl);
        InputStream is = rbl.getBinaryStream();
        assertEquals(is.available(), testData.length);
        assertEquals(rbl.length(), testData.length);
        int i = 0;
        while (is.available() > 0) {
            byte b = (byte) is.read();
            assertEquals(b, testData[i]);
            i++;
        }
        assertEquals(i, testData.length);
    }

    @Test
    public void getInputStream2Test() throws SQLException, IOException {
        System.out.println("getInputStream2Test");
        CompactBlob bl = new CompactBlob();
        bl.setData(testData);
        BlobImpl rbl = new BlobImpl(bl);
        for (int pos = 1; pos <= testData.length; pos++) {
            InputStream is = rbl.getBinaryStream(pos, 1);
            assertEquals(is.available(), 1);
            assertEquals(rbl.length(), testData.length);
            byte[] readData = new byte[1];
            is.read(readData);
            assertNotNull(readData);
            assertEquals(readData.length, 1);
            assertEquals(readData[0], testData[pos - 1]);
        }
        InputStream is = rbl.getBinaryStream(1, testData.length);
        byte[] readData = new byte[testData.length];
        for (int pos = 1; pos <= testData.length; pos++) {
            assertEquals(1, is.read(readData, pos - 1, 1));
            if (pos == testData.length) {
                assertEquals(-1, is.read(readData, pos - 1, 0));
            } else {
                assertEquals(0, is.read(readData, pos - 1, 0));
            }
        }
        assertArrayEquals(readData, testData);
    }

    @Test
    public void truncateTest() throws SQLException {
        System.out.println("truncateTest");
        CompactBlob bl = new CompactBlob();
        bl.setData(testData);
        BlobImpl rbl = new BlobImpl(bl);
        rbl.truncate(truncatedTestData.length);
        assertArrayEquals(truncatedTestData, bl.getData());
        rbl.truncate(0);
        assertArrayEquals(new byte[]{}, bl.getData());
        bl = new CompactBlob();
        bl.setData(testData);
        rbl = new BlobImpl(bl);
        rbl.truncate(testData.length);
        assertArrayEquals(testData, bl.getData());
    }

    @Test
    public void setStreamTest() throws SQLException, IOException {
        System.out.println("setStreamTest");
        for (int pos = 1; pos <= testData.length; pos++) {
            CompactBlob bl = new CompactBlob();
            bl.setData(testData);
            BlobImpl rbl = new BlobImpl(bl);
            OutputStream os = rbl.setBinaryStream(pos);
            os.write(writtenTestData);
            os.close();
            assertEquals(rbl.length(), writtenTestData.length + pos - 1);
        }
        int exceptionsCount = 0;
        try {
            CompactBlob bl = new CompactBlob();
            bl.setData(testData);
            BlobImpl rbl = new BlobImpl(bl);
            rbl.setBinaryStream(0);
            assert false;
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        // let's test simple adding with setStream capability.
        // this adding would be started after last position, saving last byte, from been rewritten.
        CompactBlob bl = new CompactBlob();
        bl.setData(testData);
        BlobImpl rbl = new BlobImpl(bl);
        rbl.setBinaryStream(rbl.length() + 1);
        try {
            bl = new CompactBlob();
            bl.setData(testData);
            rbl = new BlobImpl(bl);
            rbl.setBinaryStream(rbl.length() + 2); // exception must be thrown
            assert false;
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        assertEquals(2, exceptionsCount);
    }

    @Test
    public void position1Test() throws SQLException {
        System.out.println("position1Test");
        CompactBlob bl = new CompactBlob();
        bl.setData(testData);
        BlobImpl rbl = new BlobImpl(bl);
        for (int start = 1; start <= testData.length; start++) {
            assertEquals(start, rbl.position(new byte[]{testData[start - 1]}, 1));
        }
        for (int start = 1; start <= testData.length; start++) {
            assertEquals(-1, rbl.position(new byte[]{wrongTestByte}, start));
        }
        int exceptionsCount = 0;
        for (int start = 1; start <= testData.length; start++) {
            try {
                rbl.position(new byte[]{}, start);
            } catch (SQLException ex) {
                exceptionsCount++;
            }
        }
        assertEquals(exceptionsCount, testData.length);
    }

    @Test
    public void position2Test() throws SQLException {
        System.out.println("position2Test");
        CompactBlob bl = new CompactBlob();
        bl.setData(testData);
        BlobImpl rbl = new BlobImpl(bl);
        for (int start = 1; start <= testData.length; start++) {
            assertEquals(rbl.position(new BlobImpl(new CompactBlob(new byte[]{testData[start - 1]})), 1), start);
        }
        for (int start = 1; start <= testData.length; start++) {
            assertEquals(-1, rbl.position(new BlobImpl(new CompactBlob(new byte[]{wrongTestByte})), start));
        }
    }

    @Test
    public void setBytes1Test() throws SQLException {
        System.out.println("setBytes1Test");
        CompactBlob bl = new CompactBlob();
        bl.setData(testData);
        BlobImpl rbl = new BlobImpl(bl);
        for (int pos = 1; pos <= rbl.length(); pos++) {
            rbl.setBytes(pos, new byte[]{wrongTestByte});
        }
        assertEquals(testData.length, rbl.length());
        for (int pos = 1; pos <= rbl.length(); pos++) {
            assertEquals(wrongTestByte, rbl.getBytes(pos, 1)[0]);
        }
        int exceptionsCount = 0;
        try {
            rbl.setBytes(0, new byte[]{wrongTestByte});
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        try {
            rbl.setBytes(rbl.length() + 1, new byte[]{wrongTestByte});
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        assertEquals(2, exceptionsCount);
    }

    @Test
    public void setBytes2Test() throws SQLException {
        System.out.println("setBytes2Test");
        // do
        CompactBlob bl = new CompactBlob();
        bl.setData(testData);
        BlobImpl rbl = new BlobImpl(bl);
        for (int pos = 1; pos <= rbl.length(); pos++) {
            rbl.setBytes(pos, writtenTestData, pos - 1, 1);
        }
        // check
        assertEquals(testData.length, rbl.length());
        for (int pos = 1; pos <= rbl.length(); pos++) {
            assertEquals(writtenTestData[pos - 1], rbl.getBytes(pos, 1)[0]);
        }
        // do in another way
        bl = new CompactBlob();
        bl.setData(testData);
        rbl = new BlobImpl(bl);
        for (int pos = 1; pos <= rbl.length() - 3; pos++) {
            rbl.setBytes(pos, writtenTestData, pos - 1, 4);
        }
        // check
        assertEquals(testData.length, rbl.length());
        for (int pos = 1; pos <= rbl.length(); pos++) {
            assertEquals(writtenTestData[pos - 1], rbl.getBytes(pos, 1)[0]);
        }
        // do in another way once more
        bl = new CompactBlob();
        bl.setData(testData);
        rbl = new BlobImpl(bl);
        rbl.setBytes(1, writtenTestData, 0, writtenTestData.length);
        // check
        assertEquals(testData.length, rbl.length());
        for (int pos = 1; pos <= rbl.length(); pos++) {
            assertEquals(writtenTestData[pos - 1], rbl.getBytes(pos, 1)[0]);
        }

        int exceptionsCount = 0;
        try {
            rbl.setBytes(0, writtenTestData, 0, writtenTestData.length);
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        try {
            rbl.setBytes(rbl.length() + 1, writtenTestData, 0, writtenTestData.length);
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        rbl.setBytes(rbl.length() + 1 - truncatedTestData.length, truncatedTestData, 0, truncatedTestData.length);
        assertEquals(2, exceptionsCount);
        // let' test blob extending capability
        rbl.setBytes(rbl.length() + 2 - truncatedTestData.length, truncatedTestData, 0, truncatedTestData.length);
        assertEquals(testData.length + 1, rbl.length());
    }
}
