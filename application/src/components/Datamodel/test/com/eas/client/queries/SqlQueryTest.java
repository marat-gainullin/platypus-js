/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.client.metadata.DataTypeInfo;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author pk
 */
public class SqlQueryTest {

    public static final String PARAM2_VALUE = "qwerty";
    private static final String TWO_PARAMS_QUERY = "select * from ATABLE where FIELD1 > :param1 and FIELD2 = :param2 or FIELD1 < :param1";

    public SqlQueryTest() {
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

    @Test
    public void testCreation() {
        SqlQuery b = new SqlQuery((DatabasesClient)null);
        assertNull(b.getSqlText());
        assertTrue(b.getParametersBinds().isEmpty());
        b.setSqlText(TWO_PARAMS_QUERY);
        assertEquals(b.getSqlText(), TWO_PARAMS_QUERY);
        assertTrue(b.getParametersBinds().isEmpty());
        b.putParameter("param1", DataTypeInfo.INTEGER, 1);
        b.putParameter("param2", DataTypeInfo.VARCHAR, PARAM2_VALUE);
        assertEquals(2, b.getParameters().getParametersCount());
    }

    @Test
    public void testCompiling() throws Exception {
        SqlQuery b = new SqlQuery((DatabasesClient)null);
        b.setSqlText(TWO_PARAMS_QUERY);
        b.putParameter("param1", DataTypeInfo.INTEGER, 1);
        b.putParameter("param2", DataTypeInfo.VARCHAR, PARAM2_VALUE);
        SqlCompiledQuery q = b.compile();
        assertEquals(q.getSqlClause(), "select * from ATABLE where FIELD1 > ? and FIELD2 = ? or FIELD1 < ?");
        assertEquals(3, q.getParameters().getParametersCount());
        assertEquals(java.sql.Types.INTEGER, q.getParameters().get(1).getTypeInfo().getSqlType());
        assertEquals(1, q.getParameters().get(1).getValue());
        assertEquals(java.sql.Types.VARCHAR, q.getParameters().get(2).getTypeInfo().getSqlType());
        assertEquals(PARAM2_VALUE, q.getParameters().get(2).getValue());
        assertEquals(java.sql.Types.INTEGER, q.getParameters().get(3).getTypeInfo().getSqlType());
        assertEquals(1, q.getParameters().get(3).getValue());
    }
}
