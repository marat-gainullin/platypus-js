/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.jdbc.wrappers;

import com.bearsoft.rowset.wrappers.jdbc.NClobImpl;
import com.bearsoft.rowset.wrappers.jdbc.ResultSetImpl;
import com.bearsoft.rowset.wrappers.jdbc.ClobImpl;
import com.bearsoft.rowset.wrappers.jdbc.BlobImpl;
import com.bearsoft.rowset.utils.RowsetUtils;
import java.io.IOException;
import java.sql.Blob;
import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.compacts.CompactClob;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.sql.Clob;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetBaseTest;
import com.bearsoft.rowset.RowsetConverter;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class ResultSetImplTest extends RowsetBaseTest {

    @Test
    public void getTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException, SQLException {
        System.out.println("Trying to get all the data through the wrapper");
        Rowset rs = initRowset();
        ResultSet jdbcRs = new ResultSetImpl(rs, new RowsetConverter());
        ResultSetMetaData jdbcMd = jdbcRs.getMetaData();
        assertTrue(jdbcRs.isWrapperFor(Rowset.class));
        assertSame(jdbcRs.unwrap(Rowset.class), rs);
        assertTrue(jdbcMd.isWrapperFor(Fields.class));
        assertSame(jdbcMd.unwrap(Fields.class), rs.getFields());
        int j = 1;
        jdbcRs.beforeFirst();
        while (jdbcRs.next()) {
            for (int i = 1; i <= jdbcMd.getColumnCount(); i++) {
                switch (i) {
                    case 1:
                        long l1 = jdbcRs.getLong(i);
                        long l2 = jdbcRs.getLong(jdbcMd.getColumnLabel(i));
                        long l3 = jdbcRs.getLong(jdbcMd.getColumnName(i));
                        assertEquals(l1, l2);
                        assertEquals(l1, l3);
                        Long tl = (Long) testData[j - 1][i - 1];
                        assertEquals(l1, (long) tl);

                        int i1 = jdbcRs.getInt(i);
                        int i2 = jdbcRs.getInt(jdbcMd.getColumnLabel(i));
                        int i3 = jdbcRs.getInt(jdbcMd.getColumnName(i));
                        assertEquals(i1, i2);
                        assertEquals(i1, i3);
                        Long ti = (Long) testData[j - 1][i - 1];
                        assertEquals(i1, ti.intValue());

                        float f1 = jdbcRs.getFloat(i);
                        float f2 = jdbcRs.getFloat(jdbcMd.getColumnLabel(i));
                        float f3 = jdbcRs.getFloat(jdbcMd.getColumnName(i));
                        assertEquals(f1, f2, 1e-12);
                        assertEquals(f1, f3, 1e-12);
                        Long tf = (Long) testData[j - 1][i - 1];
                        assertEquals(f1, tf.floatValue(), 1e-12);

                        double d1 = jdbcRs.getDouble(i);
                        double d2 = jdbcRs.getDouble(jdbcMd.getColumnLabel(i));
                        double d3 = jdbcRs.getDouble(jdbcMd.getColumnName(i));
                        assertEquals(d1, d2, 1e-12);
                        assertEquals(d1, d3, 1e-12);
                        Long td = (Long) testData[j - 1][i - 1];
                        assertEquals(d1, td.doubleValue(), 1e-12);

                        short s1 = jdbcRs.getShort(i);
                        short s2 = jdbcRs.getShort(jdbcMd.getColumnLabel(i));
                        short s3 = jdbcRs.getShort(jdbcMd.getColumnName(i));
                        assertEquals(s1, s2, 1e-5);
                        assertEquals(s1, s3, 1e-5);
                        Long ts = (Long) testData[j - 1][i - 1];
                        assertEquals(f1, ts.shortValue(), 1e-12);
                        break;
                    case 2:
                        boolean b1 = jdbcRs.getBoolean(i);
                        boolean b2 = jdbcRs.getBoolean(jdbcMd.getColumnLabel(i));
                        boolean b3 = jdbcRs.getBoolean(jdbcMd.getColumnName(i));
                        assertEquals(b1, b2);
                        assertEquals(b1, b3);
                        Boolean tb = (Boolean) testData[j - 1][i - 1];
                        assertEquals(b1, (boolean) tb);

                        int bi1 = jdbcRs.getInt(i);
                        int bi2 = jdbcRs.getInt(jdbcMd.getColumnLabel(i));
                        int bi3 = jdbcRs.getInt(jdbcMd.getColumnName(i));
                        assertEquals(bi1, bi2);
                        assertEquals(bi1, bi3);
                        tb = (Boolean) testData[j - 1][i - 1];
                        assertEquals(bi1, tb ? 1 : 0);

                        short si1 = jdbcRs.getShort(i);
                        short si2 = jdbcRs.getShort(jdbcMd.getColumnLabel(i));
                        short si3 = jdbcRs.getShort(jdbcMd.getColumnName(i));
                        assertEquals(si1, si2);
                        assertEquals(si1, si3);
                        tb = (Boolean) testData[j - 1][i - 1];
                        assertEquals(si1, tb ? 1 : 0);

                        byte bb1 = jdbcRs.getByte(i);
                        byte bb2 = jdbcRs.getByte(jdbcMd.getColumnLabel(i));
                        byte bb3 = jdbcRs.getByte(jdbcMd.getColumnName(i));
                        assertEquals(bb1, bb2);
                        assertEquals(bb1, bb3);
                        tb = (Boolean) testData[j - 1][i - 1];
                        assertEquals(bb1, tb ? 1 : 0);
                        break;
                    case 3:
                    case 8:
                        String ss1 = jdbcRs.getString(i);
                        String ss2 = jdbcRs.getString(jdbcMd.getColumnLabel(i));
                        String ss3 = jdbcRs.getString(jdbcMd.getColumnName(i));
                        assertEquals(ss1, ss2);
                        assertEquals(ss1, ss3);
                        String tss = (String) testData[j - 1][i - 1];
                        assertEquals(ss1, tss);

                        ss1 = jdbcRs.getNString(i);
                        ss2 = jdbcRs.getNString(jdbcMd.getColumnLabel(i));
                        ss3 = jdbcRs.getNString(jdbcMd.getColumnName(i));
                        assertEquals(ss1, ss2);
                        assertEquals(ss1, ss3);
                        tss = (String) testData[j - 1][i - 1];
                        assertEquals(ss1, tss);

                        ss1 = jdbcRs.getNString(i);
                        ss2 = jdbcRs.getNString(jdbcMd.getColumnLabel(i));
                        ss3 = jdbcRs.getNString(jdbcMd.getColumnName(i));
                        assertEquals(ss1, ss2);
                        assertEquals(ss1, ss3);
                        tss = (String) testData[j - 1][i - 1];
                        assertEquals(ss1, tss);

                        Clob cs1 = jdbcRs.getClob(i);
                        Clob cs2 = jdbcRs.getClob(jdbcMd.getColumnLabel(i));
                        Clob cs3 = jdbcRs.getClob(jdbcMd.getColumnName(i));
                        assertEquals(cs1.getSubString(1, (int) cs1.length()), cs2.getSubString(1, (int) cs2.length()));
                        assertEquals(cs1.getSubString(1, (int) cs1.length()), cs3.getSubString(1, (int) cs3.length()));
                        tss = (String) testData[j - 1][i - 1];
                        assertEquals(cs1.getSubString(1, (int) cs1.length()), tss);

                        cs1 = jdbcRs.getNClob(i);
                        cs2 = jdbcRs.getNClob(jdbcMd.getColumnLabel(i));
                        cs3 = jdbcRs.getNClob(jdbcMd.getColumnName(i));
                        assertEquals(cs1.getSubString(1, (int) cs1.length()), cs2.getSubString(1, (int) cs2.length()));
                        assertEquals(cs1.getSubString(1, (int) cs1.length()), cs3.getSubString(1, (int) cs3.length()));
                        tss = (String) testData[j - 1][i - 1];
                        assertEquals(cs1.getSubString(1, (int) cs1.length()), tss);
                        break;
                    case 4:
                        Date dd1 = jdbcRs.getDate(i);
                        Date dd2 = jdbcRs.getDate(jdbcMd.getColumnLabel(i));
                        Date dd3 = jdbcRs.getDate(jdbcMd.getColumnName(i));
                        assertEquals(dd1, dd2);
                        assertEquals(dd1, dd3);
                        java.util.Date tdd = (java.util.Date) testData[j - 1][i - 1];
                        if (tdd != null) {
                            assertEquals(dd1, new Date(tdd.getTime()));
                        } else {
                            assertNull(dd1);
                            assertNull(tdd);
                        }

                        Time dt1 = jdbcRs.getTime(i);
                        Time dt2 = jdbcRs.getTime(jdbcMd.getColumnLabel(i));
                        Time dt3 = jdbcRs.getTime(jdbcMd.getColumnName(i));
                        assertEquals(dt1, dt2);
                        assertEquals(dt1, dt3);
                        java.util.Date tdt = (java.util.Date) testData[j - 1][i - 1];
                        if (tdt != null) {
                            assertEquals(dt1, new Time(tdt.getTime()));
                        } else {
                            assertNull(dt1);
                            assertNull(tdt);
                        }

                        Timestamp dts1 = jdbcRs.getTimestamp(i);
                        Timestamp dts2 = jdbcRs.getTimestamp(jdbcMd.getColumnLabel(i));
                        Timestamp dts3 = jdbcRs.getTimestamp(jdbcMd.getColumnName(i));
                        assertEquals(dts1, dts2);
                        assertEquals(dts1, dts3);
                        java.util.Date tdts = (java.util.Date) testData[j - 1][i - 1];
                        if (tdts != null) {
                            assertEquals(dts1, new Timestamp(tdts.getTime()));
                        } else {
                            assertNull(dts1);
                            assertNull(tdts);
                        }
                        break;
                    case 5:
                        assertNull(jdbcRs.getObject(i));
                        assertTrue(jdbcRs.getInt(i) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getBoolean(i) == false && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getByte(i) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getFloat(i) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getDouble(i) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getShort(i) == 0 && jdbcRs.wasNull());
                        assertNull(jdbcRs.getBigDecimal(i));
                        assertNull(jdbcRs.getArray(i));
                        assertNull(jdbcRs.getRef(i));
                        assertNull(jdbcRs.getRowId(i));
                        assertNull(jdbcRs.getString(i));
                        assertNull(jdbcRs.getClob(i));
                        assertNull(jdbcRs.getBlob(i));
                        assertNull(jdbcRs.getDate(i));
                        assertNull(jdbcRs.getTime(i));
                        assertNull(jdbcRs.getTimestamp(i));

                        assertNull(jdbcRs.getObject(jdbcMd.getColumnLabel(i)));
                        assertTrue(jdbcRs.getInt(jdbcMd.getColumnLabel(i)) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getBoolean(jdbcMd.getColumnLabel(i)) == false && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getByte(jdbcMd.getColumnLabel(i)) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getFloat(jdbcMd.getColumnLabel(i)) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getDouble(jdbcMd.getColumnLabel(i)) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getShort(jdbcMd.getColumnLabel(i)) == 0 && jdbcRs.wasNull());
                        assertNull(jdbcRs.getBigDecimal(jdbcMd.getColumnLabel(i)));
                        assertNull(jdbcRs.getArray(jdbcMd.getColumnLabel(i)));
                        assertNull(jdbcRs.getRef(jdbcMd.getColumnLabel(i)));
                        assertNull(jdbcRs.getRowId(jdbcMd.getColumnLabel(i)));
                        assertNull(jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        assertNull(jdbcRs.getClob(jdbcMd.getColumnLabel(i)));
                        assertNull(jdbcRs.getBlob(jdbcMd.getColumnLabel(i)));
                        assertNull(jdbcRs.getDate(jdbcMd.getColumnLabel(i)));
                        assertNull(jdbcRs.getTime(jdbcMd.getColumnLabel(i)));
                        assertNull(jdbcRs.getTimestamp(jdbcMd.getColumnLabel(i)));

                        assertNull(jdbcRs.getObject(jdbcMd.getColumnName(i)));
                        assertTrue(jdbcRs.getInt(jdbcMd.getColumnName(i)) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getBoolean(jdbcMd.getColumnName(i)) == false && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getByte(jdbcMd.getColumnName(i)) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getFloat(jdbcMd.getColumnName(i)) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getDouble(jdbcMd.getColumnName(i)) == 0 && jdbcRs.wasNull());
                        assertTrue(jdbcRs.getShort(jdbcMd.getColumnName(i)) == 0 && jdbcRs.wasNull());
                        assertNull(jdbcRs.getBigDecimal(jdbcMd.getColumnName(i)));
                        assertNull(jdbcRs.getArray(jdbcMd.getColumnName(i)));
                        assertNull(jdbcRs.getRef(jdbcMd.getColumnName(i)));
                        assertNull(jdbcRs.getRowId(jdbcMd.getColumnName(i)));
                        assertNull(jdbcRs.getString(jdbcMd.getColumnName(i)));
                        assertNull(jdbcRs.getClob(jdbcMd.getColumnName(i)));
                        assertNull(jdbcRs.getBlob(jdbcMd.getColumnName(i)));
                        assertNull(jdbcRs.getDate(jdbcMd.getColumnName(i)));
                        assertNull(jdbcRs.getTime(jdbcMd.getColumnName(i)));
                        assertNull(jdbcRs.getTimestamp(jdbcMd.getColumnName(i)));
                        break;
                    case 6:
                        BigDecimal bd1 = jdbcRs.getBigDecimal(i);
                        BigDecimal bd2 = jdbcRs.getBigDecimal(jdbcMd.getColumnLabel(i));
                        BigDecimal bd3 = jdbcRs.getBigDecimal(jdbcMd.getColumnName(i));
                        assertEquals(bd1, bd2);
                        assertEquals(bd1, bd3);
                        BigDecimal tbd = (BigDecimal) testData[j - 1][i - 1];
                        if (tbd != null) {
                            assertEquals(bd1, tbd);
                            assertFalse(jdbcRs.wasNull());
                        } else {
                            assertTrue(jdbcRs.wasNull());
                        }

                        f1 = jdbcRs.getFloat(i);
                        f2 = jdbcRs.getFloat(jdbcMd.getColumnLabel(i));
                        f3 = jdbcRs.getFloat(jdbcMd.getColumnName(i));
                        assertEquals(f1, f2, 1e-12);
                        assertEquals(f1, f3, 1e-12);
                        tbd = (BigDecimal) testData[j - 1][i - 1];
                        if (tbd != null) {
                            assertEquals(f1, tbd.floatValue(), 1e-12);
                            assertFalse(jdbcRs.wasNull());
                        } else {
                            assertTrue(jdbcRs.wasNull());
                        }

                        d1 = jdbcRs.getDouble(i);
                        d2 = jdbcRs.getDouble(jdbcMd.getColumnLabel(i));
                        d3 = jdbcRs.getDouble(jdbcMd.getColumnName(i));
                        assertEquals(d1, d2, 1e-12);
                        assertEquals(d1, d3, 1e-12);
                        tbd = (BigDecimal) testData[j - 1][i - 1];
                        if (tbd != null) {
                            assertEquals(d1, tbd.doubleValue(), 1e-12);
                            assertFalse(jdbcRs.wasNull());
                        } else {
                            assertTrue(jdbcRs.wasNull());
                        }
                        break;
                    case 7:
                        bd1 = jdbcRs.getBigDecimal(i);
                        bd2 = jdbcRs.getBigDecimal(jdbcMd.getColumnLabel(i));
                        bd3 = jdbcRs.getBigDecimal(jdbcMd.getColumnName(i));
                        assertEquals(bd1, bd2);
                        assertEquals(bd1, bd3);
                        BigInteger tbi = (BigInteger) testData[j - 1][i - 1];
                        if (tbi != null) {
                            assertEquals(bd1.intValue(), tbi.intValue());
                            assertFalse(jdbcRs.wasNull());
                        } else {
                            assertTrue(jdbcRs.wasNull());
                        }

                        bi1 = jdbcRs.getInt(i);
                        bi2 = jdbcRs.getInt(jdbcMd.getColumnLabel(i));
                        bi3 = jdbcRs.getInt(jdbcMd.getColumnName(i));
                        assertEquals(bi1, bi2);
                        assertEquals(bi1, bi3);
                        tbi = (BigInteger) testData[j - 1][i - 1];
                        if (tbi != null) {
                            assertEquals(bi1, tbi.intValue());
                            assertFalse(jdbcRs.wasNull());
                        } else {
                            assertTrue(jdbcRs.wasNull());
                        }

                        f1 = jdbcRs.getFloat(i);
                        f2 = jdbcRs.getFloat(jdbcMd.getColumnLabel(i));
                        f3 = jdbcRs.getFloat(jdbcMd.getColumnName(i));
                        assertEquals(f1, f2, 1e-12);
                        assertEquals(f1, f3, 1e-12);
                        tbi = (BigInteger) testData[j - 1][i - 1];
                        if (tbi != null) {
                            assertEquals(f1, tbi.floatValue(), 1e-12);
                            assertFalse(jdbcRs.wasNull());
                        } else {
                            assertTrue(jdbcRs.wasNull());
                        }

                        d1 = jdbcRs.getDouble(i);
                        d2 = jdbcRs.getDouble(jdbcMd.getColumnLabel(i));
                        d3 = jdbcRs.getDouble(jdbcMd.getColumnName(i));
                        assertEquals(d1, d2, 1e-12);
                        assertEquals(d1, d3, 1e-12);
                        tbi = (BigInteger) testData[j - 1][i - 1];
                        if (tbi != null) {
                            assertEquals(d1, tbi.doubleValue(), 1e-12);
                            assertFalse(jdbcRs.wasNull());
                        } else {
                            assertTrue(jdbcRs.wasNull());
                        }
                        break;
                }
            }
            j++;
        }
    }

    @Test
    public void updateTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException, SQLException {
        System.out.println("Trying to update all the data through the wrapper");
        Rowset rs = initRowset();
        ResultSet jdbcRs = new ResultSetImpl(rs, new RowsetConverter());
        ResultSetMetaData jdbcMd = jdbcRs.getMetaData();
        assertTrue(jdbcRs.isWrapperFor(Rowset.class));
        assertSame(jdbcRs.unwrap(Rowset.class), rs);
        assertTrue(jdbcMd.isWrapperFor(Fields.class));
        assertSame(jdbcMd.unwrap(Fields.class), rs.getFields());
        int j = 1;
        jdbcRs.beforeFirst();
        while (jdbcRs.next()) {
            for (int i = 1; i <= jdbcMd.getColumnCount(); i++) {
                switch (i) {
                    case 1:
                        int i1 = jdbcRs.getInt(i);
                        ++i1;
                        jdbcRs.updateInt(i, i1);
                        jdbcRs.updateNull(i);
                        jdbcRs.updateLong(i, i1);
                        jdbcRs.updateNull(i);
                        jdbcRs.updateFloat(i, i1);
                        jdbcRs.updateNull(i);

                        jdbcRs.updateInt(jdbcMd.getColumnLabel(i), i1);
                        jdbcRs.updateNull(i);
                        jdbcRs.updateLong(jdbcMd.getColumnLabel(i), i1);
                        jdbcRs.updateNull(i);
                        jdbcRs.updateFloat(jdbcMd.getColumnLabel(i), i1);
                        jdbcRs.updateNull(i);

                        jdbcRs.updateInt(jdbcMd.getColumnName(i), i1);
                        jdbcRs.updateNull(i);
                        jdbcRs.updateLong(jdbcMd.getColumnName(i), i1);
                        jdbcRs.updateNull(i);
                        jdbcRs.updateFloat(jdbcMd.getColumnName(i), i1);
                        jdbcRs.updateNull(i);
                        break;
                    case 2:
                        boolean b1 = jdbcRs.getBoolean(i);
                        b1 = !b1;
                        jdbcRs.updateInt(i, b1 ? 1 : 0);
                        if (b1) {
                            assertTrue(jdbcRs.getBoolean(i));
                        } else {
                            assertFalse(jdbcRs.getBoolean(i));
                        }
                        jdbcRs.updateNull(i);

                        jdbcRs.updateLong(i, b1 ? 1 : 0);
                        if (b1) {
                            assertTrue(jdbcRs.getBoolean(i));
                        } else {
                            assertFalse(jdbcRs.getBoolean(i));
                        }
                        jdbcRs.updateNull(i);
                        jdbcRs.updateFloat(i, b1 ? 1 : 0);
                        if (b1) {
                            assertTrue(jdbcRs.getBoolean(i));
                        } else {
                            assertFalse(jdbcRs.getBoolean(i));
                        }
                        jdbcRs.updateNull(i);

                        jdbcRs.updateInt(jdbcMd.getColumnLabel(i), b1 ? 1 : 0);
                        if (b1) {
                            assertTrue(jdbcRs.getBoolean(jdbcMd.getColumnLabel(i)));
                        } else {
                            assertFalse(jdbcRs.getBoolean(jdbcMd.getColumnLabel(i)));
                        }
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateLong(jdbcMd.getColumnLabel(i), b1 ? 1 : 0);
                        if (b1) {
                            assertTrue(jdbcRs.getBoolean(jdbcMd.getColumnLabel(i)));
                        } else {
                            assertFalse(jdbcRs.getBoolean(jdbcMd.getColumnLabel(i)));
                        }
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateFloat(jdbcMd.getColumnLabel(i), b1 ? 1 : 0);
                        if (b1) {
                            assertTrue(jdbcRs.getBoolean(jdbcMd.getColumnLabel(i)));
                        } else {
                            assertFalse(jdbcRs.getBoolean(jdbcMd.getColumnLabel(i)));
                        }
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));

                        jdbcRs.updateInt(jdbcMd.getColumnName(i), b1 ? 1 : 0);
                        if (b1) {
                            assertTrue(jdbcRs.getBoolean(jdbcMd.getColumnName(i)));
                        } else {
                            assertFalse(jdbcRs.getBoolean(jdbcMd.getColumnName(i)));
                        }
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateLong(jdbcMd.getColumnName(i), b1 ? 1 : 0);
                        if (b1) {
                            assertTrue(jdbcRs.getBoolean(jdbcMd.getColumnName(i)));
                        } else {
                            assertFalse(jdbcRs.getBoolean(jdbcMd.getColumnName(i)));
                        }
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateFloat(jdbcMd.getColumnName(i), b1 ? 1 : 0);
                        if (b1) {
                            assertTrue(jdbcRs.getBoolean(jdbcMd.getColumnName(i)));
                        } else {
                            assertFalse(jdbcRs.getBoolean(jdbcMd.getColumnName(i)));
                        }
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        break;
                    case 3:
                        String oldS1 = jdbcRs.getString(i);
                        String s1 = oldS1;// + "_updated";
                        CompactClob cl = new CompactClob(s1);
                        // update by index methods
                        jdbcRs.updateNull(i);
                        jdbcRs.updateString(i, s1);
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateNString(i, s1);
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);

                        jdbcRs.updateClob(i, new ClobImpl(cl));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateAsciiStream(i, cl.getAsciiStream());
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateAsciiStream(i, cl.getAsciiStream(), 6);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateAsciiStream(i, cl.getAsciiStream(), 6L);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);

                        jdbcRs.updateCharacterStream(i, cl.getCharacterStream());
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateCharacterStream(i, cl.getCharacterStream(), 6);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateCharacterStream(i, cl.getCharacterStream(), 6L);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);

                        jdbcRs.updateNClob(i, new NClobImpl(cl));
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateNCharacterStream(i, cl.getCharacterStream());
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateNCharacterStream(i, cl.getCharacterStream(), 6);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateNCharacterStream(i, cl.getCharacterStream(), 6L);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(i));
                        jdbcRs.updateNull(i);

                        // update by label methods
                        jdbcRs.updateString(jdbcMd.getColumnLabel(i), s1);
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateNString(jdbcMd.getColumnLabel(i), s1);
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));

                        jdbcRs.updateClob(jdbcMd.getColumnLabel(i), new ClobImpl(cl));
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateAsciiStream(jdbcMd.getColumnLabel(i), cl.getAsciiStream());
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateAsciiStream(jdbcMd.getColumnLabel(i), cl.getAsciiStream(), 6);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateAsciiStream(jdbcMd.getColumnLabel(i), cl.getAsciiStream(), 6L);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));

                        jdbcRs.updateCharacterStream(jdbcMd.getColumnLabel(i), cl.getCharacterStream());
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateCharacterStream(jdbcMd.getColumnLabel(i), cl.getCharacterStream(), 6);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateCharacterStream(jdbcMd.getColumnLabel(i), cl.getCharacterStream(), 6L);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));

                        jdbcRs.updateNClob(jdbcMd.getColumnLabel(i), new NClobImpl(cl));
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateNCharacterStream(jdbcMd.getColumnLabel(i), cl.getCharacterStream());
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateNCharacterStream(jdbcMd.getColumnLabel(i), cl.getCharacterStream(), 6);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateNCharacterStream(jdbcMd.getColumnLabel(i), cl.getCharacterStream(), 6L);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));

                        // update by name methods
                        jdbcRs.updateString(jdbcMd.getColumnName(i), s1);
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateNString(jdbcMd.getColumnName(i), s1);
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));

                        jdbcRs.updateClob(jdbcMd.getColumnName(i), new ClobImpl(cl));
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateAsciiStream(jdbcMd.getColumnName(i), cl.getAsciiStream());
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateAsciiStream(jdbcMd.getColumnName(i), cl.getAsciiStream(), 6);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateAsciiStream(jdbcMd.getColumnName(i), cl.getAsciiStream(), 6L);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));

                        jdbcRs.updateCharacterStream(jdbcMd.getColumnName(i), cl.getCharacterStream());
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateCharacterStream(jdbcMd.getColumnName(i), cl.getCharacterStream(), 6);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateCharacterStream(jdbcMd.getColumnName(i), cl.getCharacterStream(), 6L);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));

                        jdbcRs.updateNClob(jdbcMd.getColumnName(i), new NClobImpl(cl));
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateNCharacterStream(jdbcMd.getColumnName(i), cl.getCharacterStream());
                        assertEquals(((String) testData[j - 1][i - 1]), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateNCharacterStream(jdbcMd.getColumnName(i), cl.getCharacterStream(), 6);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateNCharacterStream(jdbcMd.getColumnName(i), cl.getCharacterStream(), 6L);
                        assertEquals(((String) testData[j - 1][i - 1]).substring(0, 6), jdbcRs.getString(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        break;
                    case 4:
                        Date dt1 = jdbcRs.getDate(i);
                        if (dt1 != null) {
                            Date dt2 = new Date(dt1.getTime() + 100);
                            Time t2 = new Time(dt1.getTime() + 100);
                            Timestamp ts2 = new Timestamp(dt1.getTime() + 100);

                            jdbcRs.updateDate(i, dt2);
                            assertEquals(jdbcRs.getDate(i).getTime(), dt2.getTime());
                            jdbcRs.updateNull(i);
                            jdbcRs.updateTime(i, t2);
                            assertEquals(jdbcRs.getDate(i).getTime(), dt2.getTime());
                            jdbcRs.updateNull(i);
                            jdbcRs.updateTimestamp(i, ts2);
                            assertEquals(jdbcRs.getDate(i).getTime(), dt2.getTime());
                            jdbcRs.updateNull(i);

                            jdbcRs.updateDate(jdbcMd.getColumnName(i), dt2);
                            assertEquals(jdbcRs.getDate(jdbcMd.getColumnName(i)).getTime(), dt2.getTime());
                            jdbcRs.updateNull(jdbcMd.getColumnName(i));
                            jdbcRs.updateTime(jdbcMd.getColumnName(i), t2);
                            assertEquals(jdbcRs.getDate(jdbcMd.getColumnName(i)).getTime(), dt2.getTime());
                            jdbcRs.updateNull(jdbcMd.getColumnName(i));
                            jdbcRs.updateTimestamp(jdbcMd.getColumnName(i), ts2);
                            assertEquals(jdbcRs.getDate(jdbcMd.getColumnName(i)).getTime(), dt2.getTime());
                            jdbcRs.updateNull(jdbcMd.getColumnName(i));

                            jdbcRs.updateDate(jdbcMd.getColumnLabel(i), dt2);
                            assertEquals(jdbcRs.getDate(jdbcMd.getColumnLabel(i)).getTime(), dt2.getTime());
                            jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                            jdbcRs.updateTime(jdbcMd.getColumnLabel(i), t2);
                            assertEquals(jdbcRs.getDate(jdbcMd.getColumnLabel(i)).getTime(), dt2.getTime());
                            jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                            jdbcRs.updateTimestamp(jdbcMd.getColumnLabel(i), ts2);
                            assertEquals(jdbcRs.getDate(jdbcMd.getColumnLabel(i)).getTime(), dt2.getTime());
                            jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        }
                        break;
                    case 5:
                        break;
                    case 6:
                        int bd = jdbcRs.getInt(i);
                        int bd1 = bd + 1;
                        jdbcRs.updateInt(i, bd1);
                        assertEquals(bd1, jdbcRs.getInt(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateLong(i, bd1);
                        assertEquals(bd1, jdbcRs.getInt(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateFloat(i, bd1);
                        assertEquals(bd1, jdbcRs.getInt(i));
                        jdbcRs.updateNull(i);
                        jdbcRs.updateBigDecimal(i, BigDecimal.valueOf(bd1));
                        assertEquals(BigDecimal.valueOf(bd1), jdbcRs.getBigDecimal(i));
                        jdbcRs.updateNull(i);

                        jdbcRs.updateInt(jdbcMd.getColumnLabel(i), bd1);
                        assertEquals(bd1, jdbcRs.getInt(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateLong(jdbcMd.getColumnLabel(i), bd1);
                        assertEquals(bd1, jdbcRs.getInt(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateFloat(jdbcMd.getColumnLabel(i), bd1);
                        assertEquals(bd1, jdbcRs.getInt(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));
                        jdbcRs.updateBigDecimal(jdbcMd.getColumnLabel(i), BigDecimal.valueOf(bd1));
                        assertEquals(BigDecimal.valueOf(bd1), jdbcRs.getBigDecimal(jdbcMd.getColumnLabel(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnLabel(i));

                        jdbcRs.updateInt(jdbcMd.getColumnName(i), bd1);
                        assertEquals(bd1, jdbcRs.getInt(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateLong(jdbcMd.getColumnName(i), bd1);
                        assertEquals(bd1, jdbcRs.getInt(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateFloat(jdbcMd.getColumnName(i), bd1);
                        assertEquals(bd1, jdbcRs.getInt(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        jdbcRs.updateBigDecimal(jdbcMd.getColumnName(i), BigDecimal.valueOf(bd1));
                        assertEquals(BigDecimal.valueOf(bd1), jdbcRs.getBigDecimal(jdbcMd.getColumnName(i)));
                        jdbcRs.updateNull(jdbcMd.getColumnName(i));
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                }
            }
            ++j;
        }
    }

    @Test
    public void getBlobsTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException, SQLException, UnsupportedEncodingException, IOException {
        System.out.println("getBlobTest");
        Rowset rs = initRowset();
        String sampleData = "sample dta for blob field";
        String encoding = "utf-8";
        CompactBlob blob = new CompactBlob(sampleData.getBytes(encoding));
        rs.getFields().get(8).setTypeInfo(DataTypeInfo.BLOB);
        rs.beforeFirst();
        while (rs.next()) {
            rs.getCurrentRow().setColumnObject(8, blob);
        }
        ResultSet jdbcRs = new ResultSetImpl(rs, new RowsetConverter());
        ResultSetMetaData jdbcMd = jdbcRs.getMetaData();
        assertTrue(jdbcRs.isWrapperFor(Rowset.class));
        assertSame(jdbcRs.unwrap(Rowset.class), rs);
        assertTrue(jdbcMd.isWrapperFor(Fields.class));
        assertSame(jdbcMd.unwrap(Fields.class), rs.getFields());
        jdbcRs.beforeFirst();
        while (jdbcRs.next()) {
            for (int i = 1; i <= jdbcMd.getColumnCount(); i++) {
                if (i == 8) {
                    checkBlobValue(sampleData, encoding, jdbcRs, i, jdbcMd);
                }
            }
        }
    }

    @Test
    public void updateBlobTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException, SQLException, UnsupportedEncodingException, IOException {
        System.out.println("updateBlobTest");
        Rowset rs = initRowset();

        rs.getFields().get(8).setTypeInfo(DataTypeInfo.BLOB);
        String sampleData = "sample dta for blob field";
        String sampleData1 = "sample dta for blob field";
        String encoding = "utf-8";
        CompactBlob blob = new CompactBlob(sampleData.getBytes(encoding));
        Blob blob1 = new BlobImpl(new CompactBlob(sampleData1.getBytes(encoding)));
        rs.getFields().get(8).setTypeInfo(DataTypeInfo.BLOB);
        rs.beforeFirst();
        while (rs.next()) {
            rs.getCurrentRow().setColumnObject(8, blob);
        }
        ResultSet jdbcRs = new ResultSetImpl(rs, new RowsetConverter());
        ResultSetMetaData jdbcMd = jdbcRs.getMetaData();
        assertTrue(jdbcRs.isWrapperFor(Rowset.class));
        assertSame(jdbcRs.unwrap(Rowset.class), rs);
        assertTrue(jdbcMd.isWrapperFor(Fields.class));
        assertSame(jdbcMd.unwrap(Fields.class), rs.getFields());
        int j = 1;
        jdbcRs.beforeFirst();
        while (jdbcRs.next()) {
            for (int i = 1; i <= jdbcMd.getColumnCount(); i++) {
                if (i == 8) {
                    // check indexed methods
                    jdbcRs.updateObject(i, blob1);
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(i, blob1);
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(i, blob1.getBinaryStream());
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(i, blob1.getBinaryStream(), blob1.length());
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(i, blob1.getBinaryStream(), 10);
                    checkBlobValue(sampleData1.substring(0, 10), encoding, jdbcRs, i, jdbcMd);
                    // check column label methods
                    jdbcRs.updateObject(jdbcMd.getColumnLabel(i), blob1);
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(jdbcMd.getColumnLabel(i), blob1);
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(jdbcMd.getColumnLabel(i), blob1.getBinaryStream());
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(jdbcMd.getColumnLabel(i), blob1.getBinaryStream(), blob1.length());
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(jdbcMd.getColumnLabel(i), blob1.getBinaryStream(), 10);
                    checkBlobValue(sampleData1.substring(0, 10), encoding, jdbcRs, i, jdbcMd);
                    // check column name methods
                    jdbcRs.updateObject(jdbcMd.getColumnName(i), blob1);
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(jdbcMd.getColumnName(i), blob1);
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(jdbcMd.getColumnName(i), blob1.getBinaryStream());
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(jdbcMd.getColumnName(i), blob1.getBinaryStream(), blob1.length());
                    checkBlobValue(sampleData1, encoding, jdbcRs, i, jdbcMd);

                    jdbcRs.updateBlob(jdbcMd.getColumnName(i), blob1.getBinaryStream(), 10);
                    checkBlobValue(sampleData1.substring(0, 10), encoding, jdbcRs, i, jdbcMd);
                }
            }
        }
    }

    protected Object checkBlobValue(String etalonData, String encoding, ResultSet jdbcRs, int aColIndex, ResultSetMetaData jdbcMd) throws IOException, UnsupportedEncodingException, SQLException {
        Object o = jdbcRs.getObject(aColIndex);
        assertNotNull(o);
        assertTrue(o instanceof Blob);
        Blob b = jdbcRs.getBlob(aColIndex);
        assertEquals(etalonData, new String(RowsetUtils.readStream(b.getBinaryStream(), -1), encoding));
        assertEquals(etalonData, new String(b.getBytes(1, (int) b.length()), encoding));
        assertEquals(etalonData.substring(0, 6), new String(RowsetUtils.readStream(b.getBinaryStream(1, 6), -1), encoding));
        assertEquals(etalonData, new String(RowsetUtils.readStream(jdbcRs.getBinaryStream(aColIndex), -1), encoding));
        assertEquals(etalonData, new String(jdbcRs.getBytes(aColIndex), encoding));
        o = jdbcRs.getObject(jdbcMd.getColumnLabel(aColIndex));
        assertNotNull(o);
        assertTrue(o instanceof Blob);
        b = jdbcRs.getBlob(jdbcMd.getColumnLabel(aColIndex));
        assertEquals(etalonData, new String(RowsetUtils.readStream(b.getBinaryStream(), -1), encoding));
        assertEquals(etalonData, new String(b.getBytes(1, (int) b.length()), encoding));
        assertEquals(etalonData.substring(0, 6), new String(RowsetUtils.readStream(b.getBinaryStream(1, 6), -1), encoding));
        assertEquals(etalonData, new String(RowsetUtils.readStream(jdbcRs.getBinaryStream(jdbcMd.getColumnLabel(aColIndex)), -1), encoding));
        assertEquals(etalonData, new String(jdbcRs.getBytes(jdbcMd.getColumnLabel(aColIndex)), encoding));
        o = jdbcRs.getObject(jdbcMd.getColumnName(aColIndex));
        assertNotNull(o);
        assertTrue(o instanceof Blob);
        b = jdbcRs.getBlob(jdbcMd.getColumnName(aColIndex));
        assertEquals(etalonData, new String(RowsetUtils.readStream(b.getBinaryStream(), -1), encoding));
        assertEquals(etalonData, new String(b.getBytes(1, (int) b.length()), encoding));
        assertEquals(etalonData.substring(0, 6), new String(RowsetUtils.readStream(b.getBinaryStream(1, 6), -1), encoding));
        assertEquals(etalonData, new String(RowsetUtils.readStream(jdbcRs.getBinaryStream(jdbcMd.getColumnName(aColIndex)), -1), encoding));
        assertEquals(etalonData, new String(jdbcRs.getBytes(jdbcMd.getColumnName(aColIndex)), encoding));
        return o;
    }

    @Test
    public void deleteTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException, SQLException, UnsupportedEncodingException, IOException {
        System.out.println("deleteTest");
        Rowset rs = initRowset();
        ResultSet jdbcRs = new ResultSetImpl(rs, new RowsetConverter());
        ResultSetMetaData jdbcMd = jdbcRs.getMetaData();
        assertTrue(jdbcRs.isWrapperFor(Rowset.class));
        assertSame(jdbcRs.unwrap(Rowset.class), rs);
        assertTrue(jdbcMd.isWrapperFor(Fields.class));
        assertSame(jdbcMd.unwrap(Fields.class), rs.getFields());
        while (jdbcRs.last()) {
            jdbcRs.deleteRow();
        }
        assertTrue(rs.isEmpty());
        jdbcRs.afterLast();
        assertFalse(jdbcRs.previous());
        jdbcRs.beforeFirst();
        assertFalse(jdbcRs.next());
        jdbcRs.beforeFirst();
        assertFalse(jdbcRs.first());
    }

    @Test
    public void partialDeleteTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException, SQLException, UnsupportedEncodingException, IOException {
        System.out.println("partialDeleteTest");
        Rowset rs = initRowset();
        int oldSize = rs.size();
        ResultSet jdbcRs = new ResultSetImpl(rs, new RowsetConverter());
        ResultSetMetaData jdbcMd = jdbcRs.getMetaData();
        assertTrue(jdbcRs.isWrapperFor(Rowset.class));
        assertSame(jdbcRs.unwrap(Rowset.class), rs);
        assertTrue(jdbcMd.isWrapperFor(Fields.class));
        assertSame(jdbcMd.unwrap(Fields.class), rs.getFields());
        jdbcRs.beforeFirst();
        assertTrue(jdbcRs.next());
        jdbcRs.deleteRow();
        assertTrue(jdbcRs.next());
        jdbcRs.deleteRow();
        jdbcRs.beforeFirst();
        assertTrue(jdbcRs.next());
        assertFalse(rs.isEmpty());
        assertEquals(rs.size(), oldSize - 2);
    }
}
