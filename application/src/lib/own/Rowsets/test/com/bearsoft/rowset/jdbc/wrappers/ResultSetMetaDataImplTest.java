/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.jdbc.wrappers;

import com.bearsoft.rowset.wrappers.jdbc.ResultSetMetaDataImpl;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Марат
 */
public class ResultSetMetaDataImplTest {

    public ResultSetMetaDataImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    protected ResultSetMetaDataImpl fields = null;

    @Before
    public void setUp() {
        Fields instance = new Fields();
        Fields expResult = instance;

        Field f1 = new Field("field1");
        assertEquals(f1.getName(), "field1");
        Field f2 = new Field("field2", "fDesc2");
        assertEquals(f2.getName(), "field2");
        assertEquals(f2.getDescription(), "fDesc2");
        Field f3 = new Field("field3", "fDesc3", DataTypeInfo.DECIMAL);
        assertEquals(f3.getName(), "field3");
        assertEquals(f3.getDescription(), "fDesc3");
        assertEquals(f3.getTypeInfo().getSqlType(), java.sql.Types.DECIMAL);
        Field f4 = new Field(f3);
        assertTrue(f4.isEqual(f3));
        f3.setNullable(false);
        f3.setPrecision(5);
        f3.setReadonly(true);
        f3.setScale(10);
        f3.setSchemaName("sampleSchema");
        f3.setSigned(false);
        f3.setSize(15);
        f3.setTableName("sampleTable");
        f3.getTypeInfo().setSqlTypeName("Decimal78");
        f3.getTypeInfo().setJavaClassName(BigDecimal.class.getName());

        f1.getTypeInfo().setSqlType(java.sql.Types.TIMESTAMP);
        f1.getTypeInfo().setSqlTypeName(RowsetUtils.getTypeName(java.sql.Types.TIMESTAMP));
        f1.getTypeInfo().setJavaClassName(java.sql.Timestamp.class.getName());
        instance.add(f1);
        instance.add(f2);
        instance.add(f3);
        instance.add(f4);

        Fields result = instance.clone();
        assertEquals(expResult.getFieldsCount(), result.getFieldsCount());
        for (int i = 0; i < expResult.getFieldsCount(); i++) {
            assertTrue(expResult.get(i + 1).isEqual(result.get(i + 1)));
        }
        fields = new ResultSetMetaDataImpl(instance);
    }

    /**
     * Test of getColumnCount method, of class ResultSetMetaDataImpl.
     */
    @Test
    public void testGetColumnCount() {
        System.out.println("getColumnCount");
        int expResult = 4;
        int result = fields.getColumnCount();
        assertEquals(expResult, result);
    }

    @Test
    public void testMethodedProperties() throws SQLException {
        assertEquals(fields.getCatalogName(3), null);
        assertEquals(fields.getCatalogName(1), null);

        assertEquals(fields.getColumnClassName(3), BigDecimal.class.getName());
        assertEquals(fields.getColumnClassName(1), java.sql.Timestamp.class.getName());

        assertEquals(fields.getColumnDisplaySize(3), 15);
        assertEquals(fields.getColumnDisplaySize(1), 0);

        assertEquals(fields.getColumnLabel(3), "field3");//"fDesc3"); Unfortunately, JDBC treats column label as an expression following by 'as' clause of sql query and so it's the column identifier
        assertEquals(fields.getColumnLabel(2), "field2");//"fDesc2");

        assertEquals(fields.getColumnName(3), "field3");
        assertEquals(fields.getColumnName(1), "field1");

        assertEquals(fields.getColumnType(3), java.sql.Types.DECIMAL);
        assertEquals(fields.getColumnType(1), java.sql.Types.TIMESTAMP);

        assertEquals(fields.getColumnTypeName(3), "Decimal78");
        assertEquals(fields.getColumnTypeName(1), "TIMESTAMP");

        assertEquals(fields.getPrecision(3), 5);
        assertEquals(fields.getPrecision(1), 0);

        assertEquals(fields.getScale(3), 10);
        assertEquals(fields.getScale(1), 0);

        assertEquals(fields.getSchemaName(3), "sampleSchema");
        assertEquals(fields.getSchemaName(1), null);

        assertEquals(fields.getTableName(3), "sampleTable");
        assertEquals(fields.getTableName(1), null);

        assertEquals(fields.isAutoIncrement(3), false);
        assertEquals(fields.isAutoIncrement(1), false);

        assertEquals(fields.isCaseSensitive(3), false);
        assertEquals(fields.isCaseSensitive(1), false);

        assertEquals(fields.isCurrency(3), false);
        assertEquals(fields.isCurrency(1), false);

        assertEquals(fields.isDefinitelyWritable(3), false);
        assertEquals(fields.isDefinitelyWritable(1), true);

        assertEquals(fields.isNullable(3), ResultSetMetaData.columnNoNulls);
        assertEquals(fields.isNullable(1), ResultSetMetaData.columnNullable);

        assertEquals(fields.isReadOnly(3), true);
        assertEquals(fields.isReadOnly(1), false);

        assertEquals(fields.isSearchable(3), true);
        assertEquals(fields.isSearchable(1), true);

        assertEquals(fields.isSigned(3), false);
        assertEquals(fields.isSigned(1), true);

        assertEquals(fields.isWritable(3), false);
        assertEquals(fields.isWritable(1), true);
    }

    /**
     * Test of unwrap method, of class ResultSetMetaDataImpl.
     */
    @Test
    public void testUnwrap() throws Exception {
        System.out.println("unwrap");
        Class iface = Fields.class;
        Object result = fields.unwrap(iface);
        assertTrue(result instanceof Fields);
    }

    /**
     * Test of isWrapperFor method, of class ResultSetMetaDataImpl.
     */
    @Test
    public void testIsWrapperFor() throws Exception {
        System.out.println("isWrapperFor");
        Class iface = Fields.class;
        assertTrue(fields.isWrapperFor(iface));
    }
}
