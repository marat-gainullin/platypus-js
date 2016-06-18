/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.script.Scripts;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
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
        SqlQuery b = new SqlQuery((DatabasesClient) null);
        assertNull(b.getSqlText());
        assertTrue(b.getParametersBinds().isEmpty());
        b.setSqlText(PARSABLE_WITH_PARAMS_QUERY_TEXT);
        assertEquals(b.getSqlText(), PARSABLE_WITH_PARAMS_QUERY_TEXT);
        assertTrue(b.getParametersBinds().isEmpty());
        b.putParameter("param1", Scripts.NUMBER_TYPE_NAME, 1);
        b.putParameter("param2", Scripts.STRING_TYPE_NAME, PARAM2_VALUE);
        assertEquals(2, b.getParameters().getParametersCount());
    }

    private static final String PARSABLE_WITH_PARAMS_QUERY_TEXT = ""
            + "/**" + ExpressionDeParser.EOL
            + " * :param1" + ExpressionDeParser.EOL
            + " */" + ExpressionDeParser.EOL
            + "select 'some string :param2 literal', * from ATABLE where FIELD1 > :param1 and FIELD2 = :param2 or FIELD1 < :param1";
    @Test
    public void toJdbcParams() throws Exception {
        SqlQuery b = new SqlQuery((DatabasesClient) null);
        b.setSqlText(PARSABLE_WITH_PARAMS_QUERY_TEXT);
        b.putParameter("param1", Scripts.NUMBER_TYPE_NAME, 1);
        b.putParameter("param2", Scripts.STRING_TYPE_NAME, PARAM2_VALUE);
        SqlCompiledQuery compiled = b.compile(null, null);
        assertEquals(""
                + "/**" + ExpressionDeParser.EOL
                + " * :param1" + ExpressionDeParser.EOL
                + " */ " + ExpressionDeParser.EOL
                + "Select 'some string :param2 literal', * " + ExpressionDeParser.EOL
                + "From ATABLE" + ExpressionDeParser.EOL
                + " Where FIELD1 > ?" + ExpressionDeParser.EOL
                + " and FIELD2 = ? or FIELD1 < ?", compiled.getSqlClause());
        assertEquals(3, compiled.getParameters().getParametersCount());
        assertEquals(Scripts.NUMBER_TYPE_NAME, compiled.getParameters().get(1).getType());
        assertEquals(1, compiled.getParameters().get(1).getValue());
        assertEquals(Scripts.STRING_TYPE_NAME, compiled.getParameters().get(2).getType());
        assertEquals(PARAM2_VALUE, compiled.getParameters().get(2).getValue());
        assertEquals(Scripts.NUMBER_TYPE_NAME, compiled.getParameters().get(3).getType());
        assertEquals(1, compiled.getParameters().get(3).getValue());
    }
    
    private static final String UNPARSABLE_WITH_PARAMS_QUERY_TEXT = ""
            + "sel ect 'some string :param2 literal', * from ATABLE where FIELD1 > :param1 and FIELD2 = :param2 or FIELD1 < :param1";
    @Test
    public void toJdbcParamsUnparsable() throws Exception {
        SqlQuery b = new SqlQuery((DatabasesClient) null);
        b.setSqlText(UNPARSABLE_WITH_PARAMS_QUERY_TEXT);
        b.putParameter("param1", Scripts.NUMBER_TYPE_NAME, 1);
        b.putParameter("param2", Scripts.STRING_TYPE_NAME, PARAM2_VALUE);
        SqlCompiledQuery compiled = b.compile(null, null);
        assertEquals("sel ect 'some string :param2 literal', * from ATABLE where FIELD1 > ? and FIELD2 = ? or FIELD1 < ?", compiled.getSqlClause());
        assertEquals(3, compiled.getParameters().getParametersCount());
        assertEquals(Scripts.NUMBER_TYPE_NAME, compiled.getParameters().get(1).getType());
        assertEquals(1, compiled.getParameters().get(1).getValue());
        assertEquals(Scripts.STRING_TYPE_NAME, compiled.getParameters().get(2).getType());
        assertEquals(PARAM2_VALUE, compiled.getParameters().get(2).getValue());
        assertEquals(Scripts.NUMBER_TYPE_NAME, compiled.getParameters().get(3).getType());
        assertEquals(1, compiled.getParameters().get(3).getValue());
    }
}
