package com.eas.client.queries;

import com.eas.client.DatabasesClient;
import com.eas.client.DatabasesClientWithResource;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.client.TestConstants;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.script.Scripts;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author pk
 */
public class SqlQueryTest {

    public static final String PARAM2_VALUE = "qwerty";
    private static final String TWO_PARAMS_QUERY = "select * from ATABLE where FIELD1 > :param1 and FIELD2 = :param2 or FIELD1 < :param1";
    private static DatabasesClientWithResource resource;

    public SqlQueryTest() {
    }

    @BeforeClass
    public static void init() throws Exception {
        String url = System.getProperty(TestConstants.DATASOURCE_URL_1);
        if (url == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_URL_1 + TestConstants.PROPERTY_ERROR);
        }
        String user = System.getProperty(TestConstants.DATASOURCE_USER_1);
        if (user == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_USER_1 + TestConstants.PROPERTY_ERROR);
        }
        String passwd = System.getProperty(TestConstants.DATASOURCE_PASSWORD_1);
        if (passwd == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_PASSWORD_1 + TestConstants.PROPERTY_ERROR);
        }
        String schema = System.getProperty(TestConstants.DATASOURCE_SCHEMA_1);
        if (schema == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_SCHEMA_1 + TestConstants.PROPERTY_ERROR);
        }

        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl(url);
        settings.setUser(user);
        settings.setPassword(passwd);
        settings.setSchema(schema);
        settings.setMaxConnections(1);
        settings.setMaxStatements(1);
        resource = new DatabasesClientWithResource(settings);
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
        b.setSqlText(TWO_PARAMS_QUERY);
        assertEquals(b.getSqlText(), TWO_PARAMS_QUERY);
        assertTrue(b.getParametersBinds().isEmpty());
        b.putParameter("param1", Scripts.NUMBER_TYPE_NAME, 1);
        b.putParameter("param2", Scripts.STRING_TYPE_NAME, PARAM2_VALUE);
        assertEquals(2, b.getParameters().getParametersCount());
    }

    @Test
    public void testCompiling() throws Exception {
        SqlQuery b = new SqlQuery(resource.getClient());
        b.setSqlText(TWO_PARAMS_QUERY);
        b.putParameter("param1", Scripts.NUMBER_TYPE_NAME, 1);
        b.putParameter("param2", Scripts.STRING_TYPE_NAME, PARAM2_VALUE);
        SqlCompiledQuery q = b.compile();
        assertEquals(q.getSqlClause(), "select * from ATABLE where FIELD1 > ? and FIELD2 = ? or FIELD1 < ?");
        assertEquals(3, q.getParameters().getParametersCount());
        assertEquals(Scripts.NUMBER_TYPE_NAME, q.getParameters().get(1).getType());
        assertEquals(1, q.getParameters().get(1).getValue());
        assertEquals(Scripts.STRING_TYPE_NAME, q.getParameters().get(2).getType());
        assertEquals(PARAM2_VALUE, q.getParameters().get(2).getValue());
        assertEquals(Scripts.NUMBER_TYPE_NAME, q.getParameters().get(3).getType());
        assertEquals(1, q.getParameters().get(3).getValue());
    }
}
