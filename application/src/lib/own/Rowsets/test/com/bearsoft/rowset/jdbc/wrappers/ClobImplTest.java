/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.jdbc.wrappers;

import com.bearsoft.rowset.wrappers.jdbc.ClobImpl;
import com.bearsoft.rowset.compacts.CompactClob;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class ClobImplTest {

    protected static char wrongTestChar = (char) 'q';
    protected static char[] testData = new char[]{'e', 'v', 'r', 'u', 'g', 'w', 'm', 's', '[', '7'};
    protected static char[] truncatedTestData = new char[]{'e', 'v', 'r', 'u', 'g'};
    protected static char[] writtenTestData = new char[]{'l', 'r', '3', 'k', 'a', ',', ';', 'd', '&', '='};

    @Test
    public void freeTest() throws SQLException {
        System.out.println("freeTest");
        CompactClob cl = new CompactClob();
        cl.setData(new String(testData));
        ClobImpl rcl = new ClobImpl(cl);
        rcl.free();
        try {
            rcl.getAsciiStream();
            assert false;
        } catch (SQLException ex) {
        }
    }

    @Test
    public void getSubStringTest() throws SQLException {
        System.out.println("getSubStringTest");
        CompactClob cl = new CompactClob();
        cl.setData(new String(testData));
        ClobImpl rcl = new ClobImpl(cl);
        for (int pos = 1; pos <= testData.length; pos++) {
            String substring = rcl.getSubString(pos, 1);
            assertNotNull(substring);
            assertEquals(substring.length(), 1);
            assertEquals(substring.charAt(0), testData[pos - 1]);
        }
        for (int pos = 1; pos <= testData.length - 1; pos++) {
            String substring = rcl.getSubString(pos, 2);
            assertNotNull(substring);
            assertEquals(substring.length(), 2);
            assertEquals(substring.charAt(0), testData[pos - 1]);
            assertEquals(substring.charAt(1), testData[pos]);
        }
        for (int pos = 1; pos <= testData.length - 2; pos++) {
            String substring = rcl.getSubString(pos, 3);
            assertNotNull(substring);
            assertEquals(substring.length(), 3);
            assertEquals(substring.charAt(0), testData[pos - 1]);
            assertEquals(substring.charAt(1), testData[pos]);
            assertEquals(substring.charAt(2), testData[pos + 1]);
        }
        String substring = rcl.getSubString(1, testData.length);
        assertNotNull(substring);
        assertEquals(substring.length(), testData.length);
        assertEquals(substring, new String(testData));

        for (int pos = 1; pos <= testData.length; pos++) {
            substring = rcl.getSubString(1, 0);
            assertNotNull(substring);
            assertEquals(substring.length(), 0);
        }
    }

    @Test
    public void getInputStream1Test() throws SQLException, IOException {
        System.out.println("getInputStream1Test");
        CompactClob cl = new CompactClob();
        cl.setData(new String(testData));
        ClobImpl rcl = new ClobImpl(cl);
        Reader is = rcl.getCharacterStream();
        assertEquals(rcl.length(), testData.length);
        for (int i = 0; i < testData.length; i++) {
            char b = (char) is.read();
            assertEquals(b, testData[i]);
        }
        assertEquals(rcl.length(), testData.length);
    }

    @Test
    public void getInputStream2Test() throws SQLException, IOException {
        System.out.println("getInputStream2Test");
        CompactClob cl = new CompactClob();
        cl.setData(new String(testData));
        ClobImpl rcl = new ClobImpl(cl);
        for (int pos = 1; pos <= testData.length; pos++) {
            Reader is = rcl.getCharacterStream(pos, 1);
            assertEquals(rcl.length(), testData.length);
            char[] readData = new char[1];
            is.read(readData);
            assertNotNull(readData);
            assertEquals(readData.length, 1);
            assertEquals(readData[0], testData[pos - 1]);
            assertEquals(-1, is.read());
        }
        Reader is = rcl.getCharacterStream(1, testData.length);
        char[] readData = new char[testData.length];
        for (int pos = 1; pos <= testData.length; pos++) {
            assertEquals(1, is.read(readData, pos - 1, 1));
            if (pos == testData.length) {
                assertEquals(-1, is.read(readData, pos - 1, 1));
            } else {
                assertEquals(0, is.read(readData, pos - 1, 0));
            }
        }
        assertArrayEquals(readData, testData);
    }

    @Test
    public void truncateTest() throws SQLException {
        System.out.println("truncateTest");
        CompactClob cl = new CompactClob();
        cl.setData(new String(testData));
        ClobImpl rcl = new ClobImpl(cl);
        rcl.truncate(truncatedTestData.length);
        assertEquals(new String(truncatedTestData), cl.getData());
        rcl.truncate(0);
        assertEquals(new String(new char[]{}), cl.getData());
        cl = new CompactClob();
        cl.setData(new String(testData));
        rcl = new ClobImpl(cl);
        rcl.truncate(testData.length);
        assertEquals(new String(testData), cl.getData());
    }

    @Test
    public void setStreamTest() throws SQLException, IOException {
        System.out.println("setStreamTest");
        for (int pos = 1; pos <= testData.length; pos++) {
            CompactClob cl = new CompactClob();
            cl.setData(new String(testData));
            ClobImpl rcl = new ClobImpl(cl);
            Writer os = rcl.setCharacterStream(pos);
            os.write(writtenTestData);
            os.close();
            assertEquals(rcl.length(), writtenTestData.length + pos - 1);
        }
        int exceptionsCount = 0;
        try {
            CompactClob cl = new CompactClob();
            cl.setData(new String(testData));
            ClobImpl rcl = new ClobImpl(cl);
            rcl.setCharacterStream(0);
            assert false;
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        // let's test simple adding with setStream capability.
        // this adding would be started after last position, saving last character, from been rewritten.
        CompactClob cl = new CompactClob();
        cl.setData(new String(testData));
        ClobImpl rcl = new ClobImpl(cl);
        rcl.setCharacterStream(rcl.length() + 1);
        try {
            cl = new CompactClob();
            cl.setData(new String(testData));
            rcl = new ClobImpl(cl);
            rcl.setCharacterStream(rcl.length() + 2);
            assert false;
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        assertEquals(2, exceptionsCount);
    }

    @Test
    public void position1Test() throws SQLException {
        System.out.println("position1Test");
        CompactClob cl = new CompactClob();
        cl.setData(new String(testData));
        ClobImpl rcl = new ClobImpl(cl);
        for (int start = 1; start <= testData.length; start++) {
            assertEquals(rcl.position(new String(new char[]{testData[start - 1]}), 1), start);
        }
        for (int start = 1; start <= testData.length; start++) {
            assertEquals(-1, rcl.position(new String(new char[]{wrongTestChar}), start));
        }
    }

    @Test
    public void position2Test() throws SQLException {
        System.out.println("position2Test");
        CompactClob cl = new CompactClob();
        cl.setData(new String(testData));
        ClobImpl rcl = new ClobImpl(cl);
        for (int start = 1; start <= testData.length; start++) {
            assertEquals(rcl.position(new ClobImpl(new CompactClob(new String(new char[]{testData[start - 1]}))), 1), start);
        }
        for (int start = 1; start <= testData.length; start++) {
            assertEquals(-1, rcl.position(new ClobImpl(new CompactClob(new String(new char[]{wrongTestChar}))), start));
        }
    }

    @Test
    public void setString1Test() throws SQLException {
        System.out.println("setString1Test");
        CompactClob cl = new CompactClob();
        cl.setData(new String(testData));
        ClobImpl rcl = new ClobImpl(cl);
        for (int pos = 1; pos <= rcl.length(); pos++) {
            rcl.setString(pos, new String(new char[]{wrongTestChar}));
        }
        assertEquals(testData.length, rcl.length());
        for (int pos = 1; pos <= rcl.length(); pos++) {
            assertEquals(wrongTestChar, rcl.getSubString(pos, 1).charAt(0));
        }
        int exceptionsCount = 0;
        try {
            rcl.setString(0, new String(new char[]{wrongTestChar}));
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        try {
            rcl.setString(rcl.length() + 1, new String(new char[]{wrongTestChar}));
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        assertEquals(2, exceptionsCount);
    }

    @Test
    public void setString2Test() throws SQLException {
        System.out.println("setString2Test");
        // do
        CompactClob cl = new CompactClob();
        cl.setData(new String(testData));
        ClobImpl rcl = new ClobImpl(cl);
        for (int pos = 1; pos <= rcl.length(); pos++) {
            rcl.setString(pos, new String(writtenTestData), pos - 1, 1);
        }
        // check
        assertEquals(testData.length, rcl.length());
        for (int pos = 1; pos <= rcl.length(); pos++) {
            assertEquals(writtenTestData[pos - 1], rcl.getSubString(pos, 1).charAt(0));
        }
        // do in another way
        cl = new CompactClob();
        cl.setData(new String(testData));
        rcl = new ClobImpl(cl);
        for (int pos = 1; pos <= rcl.length() - 3; pos++) {
            rcl.setString(pos, new String(writtenTestData), pos - 1, 4);
        }
        // check
        assertEquals(testData.length, rcl.length());
        for (int pos = 1; pos <= rcl.length(); pos++) {
            assertEquals(writtenTestData[pos - 1], rcl.getSubString(pos, 1).charAt(0));
        }
        // do in another way once more
        cl = new CompactClob();
        cl.setData(new String(testData));
        rcl = new ClobImpl(cl);
        rcl.setString(1, new String(writtenTestData), 0, writtenTestData.length);
        // check
        assertEquals(testData.length, rcl.length());
        for (int pos = 1; pos <= rcl.length(); pos++) {
            assertEquals(writtenTestData[pos - 1], rcl.getSubString(pos, 1).charAt(0));
        }

        int exceptionsCount = 0;
        try {
            rcl.setString(0, new String(writtenTestData), 0, writtenTestData.length);
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        try {
            rcl.setString(rcl.length() + 1, new String(writtenTestData), 0, writtenTestData.length);
        } catch (SQLException ex) {
            ++exceptionsCount;
        }
        rcl.setString(rcl.length() + 1 - truncatedTestData.length, new String(truncatedTestData), 0, truncatedTestData.length);
        rcl.setString(rcl.length() + 2 - truncatedTestData.length, new String(truncatedTestData), 0, truncatedTestData.length);
        assertEquals(2, exceptionsCount);
        assertEquals(testData.length+1, rcl.length());
    }
}
