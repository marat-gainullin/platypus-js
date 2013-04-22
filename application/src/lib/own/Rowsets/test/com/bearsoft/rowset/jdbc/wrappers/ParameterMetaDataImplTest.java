/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.jdbc.wrappers;

import com.bearsoft.rowset.wrappers.jdbc.ParameterMetaDataImpl;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import java.math.BigDecimal;
import java.sql.ParameterMetaData;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Марат
 */
public class ParameterMetaDataImplTest {

    public ParameterMetaDataImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    protected ParameterMetaDataImpl parameters = null;

    @Before
    public void setUp() {
        Parameters instance = new Parameters();
        Parameters expResult = instance;

        Parameter f1 = new Parameter("field1");
        assertEquals(f1.getName(), "field1");
        Parameter f2 = new Parameter("field2", "fDesc2");
        assertEquals(f2.getName(), "field2");
        assertEquals(f2.getDescription(), "fDesc2");
        Parameter f3 = new Parameter("field3", "fDesc3", DataTypeInfo.DECIMAL);
        assertEquals(f3.getName(), "field3");
        assertEquals(f3.getDescription(), "fDesc3");
        assertEquals(f3.getTypeInfo(), DataTypeInfo.DECIMAL);
        Parameter f4 = new Parameter(f3);
        assertEquals(f4, f3);
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
        f3.setMode(ParameterMetaData.parameterModeIn);

        f1.setTypeInfo(DataTypeInfo.TIMESTAMP);
        instance.add(f1);
        instance.add(f2);
        instance.add(f3);
        instance.add(f4);

        Fields result = instance.clone();
        assertEquals(expResult.getFieldsCount(), result.getFieldsCount());
        for (int i = 0; i < expResult.getFieldsCount(); i++) {
            assertEquals(expResult.get(i + 1), result.get(i + 1));
        }
        parameters = new ParameterMetaDataImpl(instance);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of unwrap method, of class ParameterMetaDataImpl.
     */
    @Test
    public void testUnwrap() throws Exception {
        System.out.println("unwrap");
        Class iface = Parameters.class;
        Object result = parameters.unwrap(iface);
        assertTrue(result instanceof Parameters);
    }

    /**
     * Test of isWrapperFor method, of class ParameterMetaDataImpl.
     */
    @Test
    public void testIsWrapperFor() throws Exception {
        System.out.println("isWrapperFor");
        Class iface = Parameters.class;
        assertTrue(parameters.isWrapperFor(iface));
    }

    @Test
    public void testMethodedProperties() throws SQLException {
        assertEquals(parameters.getParameterClassName(3), BigDecimal.class.getName());
        assertEquals(parameters.getParameterClassName(1), java.sql.Timestamp.class.getName());

        assertEquals(parameters.getParameterType(3), java.sql.Types.DECIMAL);
        assertEquals(parameters.getParameterType(1), java.sql.Types.TIMESTAMP);

        assertEquals(parameters.getParameterTypeName(3), "Decimal78");
        assertEquals(parameters.getParameterTypeName(1), "TIMESTAMP");

        assertEquals(parameters.getPrecision(3), 5);
        assertEquals(parameters.getPrecision(1), 0);

        assertEquals(parameters.getScale(3), 10);
        assertEquals(parameters.getScale(1), 0);

        assertEquals(parameters.isNullable(3), ParameterMetaData.parameterNoNulls);
        assertEquals(parameters.isNullable(1), ParameterMetaData.parameterNullable);

        assertEquals(parameters.isSigned(3), false);
        assertEquals(parameters.isSigned(1), true);

        assertEquals(parameters.getParameterMode(3), ParameterMetaData.parameterModeIn);
        assertEquals(parameters.getParameterMode(1), ParameterMetaData.parameterModeIn);

    }
}
