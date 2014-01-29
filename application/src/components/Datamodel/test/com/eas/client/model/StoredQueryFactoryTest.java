/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.ClientConstants;
import com.eas.client.DbClient;
import com.eas.client.exceptions.NoSuchEntityException;
import com.eas.client.queries.SqlQuery;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.settings.SettingsConstants;
import com.eas.script.JsDoc;
import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk, mg
 */
public class StoredQueryFactoryTest extends BaseTest {

    public StoredQueryFactoryTest() {
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

    @Test
    public void testFirstAnnotationsComma1() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n"
                + " * %s %s   , %s,%s\n"
                + " * \n"
                + " * \n"
                + " */\n"
                + "select from dual", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        StoredQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testFirstAnnotationsComma2() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s   , %s,%s\r\n"
                + " * \n\r"
                + " * \r\n"
                + " */\n"
                + "select from dual", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        StoredQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testFirstAnnotationsSpace() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " * \n\r"
                + " * \r\n"
                + " */\n"
                + "select from dual", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        StoredQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testLastAnnotationsSpace() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format("select from dual"
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " * \n\r"
                + " * \r\n"
                + " */\n"
                + "", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        StoredQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testMiddleAnnotationsSpace1() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * select from dual\r\n"
                + " * %s %s    %s %s\r\n"
                + " * \r\n"
                + " */\n"
                + "", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        StoredQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testMiddleAnnotationsSpace2() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " * select from dual\r\n"
                + " * \r\n"
                + " */\n"
                + "", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        StoredQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testMiddleReadAnnotationsSpace2() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " * select from dual\r\n"
                + " * \r\n"
                + " */\n"
                + "", JsDoc.Tag.ROLES_ALLOWED_READ_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        StoredQueryFactory.putRolesMutatables(q);
        assertEquals(0, q.getWriteRoles().size());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testMiddleWriteAnnotationsSpace2() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " * select from dual\r\n"
                + " * \r\n"
                + " */\n"
                + "", JsDoc.Tag.ROLES_ALLOWED_WRITE_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        StoredQueryFactory.putRolesMutatables(q);
        assertEquals(0, q.getReadRoles().size());
        assertEquals(3, q.getWriteRoles().size());
        assertTrue(q.getWriteRoles().contains(role1));
        assertTrue(q.getWriteRoles().contains(role2));
        assertTrue(q.getWriteRoles().contains(role3));
    }

    @Test
    public void testMiddleReadWriteAnnotationsSpace2() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " */\n"
                + "select \r\n"
                + "/**\n"
                + " * %s %s    %s \r\n"
                + " * \r\n"
                + " */\n"
                + "from dual",
                JsDoc.Tag.ROLES_ALLOWED_READ_TAG, role1, role2, role3,
                JsDoc.Tag.ROLES_ALLOWED_WRITE_TAG, role1, role2);
        SqlQuery q = new SqlQuery(null, sqlText);
        StoredQueryFactory.putRolesMutatables(q);
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
        assertEquals(2, q.getWriteRoles().size());
        assertTrue(q.getWriteRoles().contains(role1));
        assertTrue(q.getWriteRoles().contains(role2));
        assertFalse(q.getWriteRoles().contains(role3));
    }

    @Test
    public void testCompilingWithSubqueries() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQuery2.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                assertEquals("SELECT T0.ORDER_NO, 'Some text' AS VALUE_FIELD_1, TABLE1.ID, TABLE1.F1, TABLE1.F3, T0.AMOUNT FROM TABLE1, TABLE2,  (/**\n"
                        + " * @name namedQuery4Tests\n"
                        + "*/\n"
                        + "Select goodOrder.ORDER_ID as ORDER_NO, goodOrder.AMOUNT, customers.CUSTOMER_NAME as CUSTOMER \nFrom GOODORDER goodOrder\n Inner Join CUSTOMER customers on (goodOrder.CUSTOMER = customers.CUSTOMER_ID)\n and (goodOrder.AMOUNT > customers.CUSTOMER_NAME)\n Where :P4 = goodOrder.GOOD)  T0  WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)",
                        testQuery.getSqlText());
                assertEquals(6, testQuery.getFields().getFieldsCount());
                for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
                    Field fieldMtd = testQuery.getFields().get(i + 1);
                    assertNotNull(fieldMtd);
                    if (i == 0 || i == 5) {
                        assertNotNull(fieldMtd.getDescription());
                    } else {
                        assertNull(fieldMtd.getDescription());
                    }
                }
                assertEquals(4, testQuery.getParameters().getParametersCount());
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testCompilingWithSubqueriesBad() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQuery8BadMetadata.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                assertEquals("SELECT T0.ORDER_NO, 'Some text', TABLE1.ID, TABLE1.F1, TABLE1.F3, T0.AMOUNT FROM TABLE1, TABLE2,  (/**\n"
                        + " * @name 128082898425059\n"
                        + "*/\n"
                        + "Select goodOrder.ORDER_ID as ORDER_NO, goodOrder.AMOUNT, customers.CUSTOMER_NAME as CUSTOMER \nFrom GOODORDER goodOrder\n Inner Join CUSTOMER customers on (goodOrder.CUSTOMER = customers.CUSTOMER_ID)\n and (goodOrder.AMOUNT > customers.CUSTOMER_NAME)\n Where :P4 = goodOrder.GOOD)  T0  WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)",
                        testQuery.getSqlText());
                assertEquals(6, testQuery.getFields().getFieldsCount());
                for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
                    Field fieldMtd = testQuery.getFields().get(i + 1);
                    assertNotNull(fieldMtd);
                    if (i == 0 || i == 5) {
                        assertNotNull(fieldMtd.getDescription());
                    } else {
                        assertNull(fieldMtd.getDescription());
                    }
                }
                assertEquals(4, testQuery.getParameters().getParametersCount());
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testAsteriskMetadata() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQuery3.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                assertEquals("SELECT * FROM TABLE1, TABLE2,  (/**\n"
                        + " * @name 128082898425059\n"
                        + "*/\n"
                        + "Select goodOrder.ORDER_ID as ORDER_NO, goodOrder.AMOUNT, customers.CUSTOMER_NAME as CUSTOMER \nFrom GOODORDER goodOrder\n Inner Join CUSTOMER customers on (goodOrder.CUSTOMER = customers.CUSTOMER_ID)\n and (goodOrder.AMOUNT > customers.CUSTOMER_NAME)\n Where :P4 = goodOrder.GOOD)  T0  WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)",
                        testQuery.getSqlText());
                assertEquals(11, testQuery.getFields().getFieldsCount());
                for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
                    Field fieldMtd = testQuery.getFields().get(i + 1);
                    assertNotNull(fieldMtd);
                }
                assertEquals(4, testQuery.getParameters().getParametersCount());
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testBadSubquery() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQueryBadSubQuery.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                fail("Query could not compiled.");
            } catch (Exception ex) {
                if (!(ex instanceof NoSuchEntityException)) {
                    throw new Exception("We must got NoSuchEntityException error.", ex);
                }
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testAsteriskMetadata_NewDesignSerialization() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQuery3_newDesignSerialization.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                assertEquals("SELECT * FROM TABLE1, TABLE2,  (/**\n"
                        + " * @name 128082898425059\n"
                        + "*/\n"
                        + "Select goodOrder.ORDER_ID as ORDER_NO, goodOrder.AMOUNT, customers.CUSTOMER_NAME as CUSTOMER \nFrom GOODORDER goodOrder\n Inner Join CUSTOMER customers on (goodOrder.CUSTOMER = customers.CUSTOMER_ID)\n and (goodOrder.AMOUNT > customers.CUSTOMER_NAME)\n Where :P4 = goodOrder.GOOD)  T0  WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)",
                        testQuery.getSqlText());
                assertEquals(11, testQuery.getFields().getFieldsCount());
                for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
                    Field fieldMtd = testQuery.getFields().get(i + 1);
                    assertNotNull(fieldMtd);
                }
                assertEquals(4, testQuery.getParameters().getParametersCount());
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testPartialTablesAsteriskMetadata() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQuery3_PartialTablesAsterisk.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                assertEquals("SELECT TABLE1.*, TABLE2.FiELdB FROM TABLE1, TABLE2,  (/**\n"
                        + " * @name namedQuery4Tests\n"
                        + "*/\n"
                        + "Select goodOrder.ORDER_ID as ORDER_NO, goodOrder.AMOUNT, customers.CUSTOMER_NAME as CUSTOMER \nFrom GOODORDER goodOrder\n Inner Join CUSTOMER customers on (goodOrder.CUSTOMER = customers.CUSTOMER_ID)\n and (goodOrder.AMOUNT > customers.CUSTOMER_NAME)\n Where :P4 = goodOrder.GOOD)  T0  WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)",
                        testQuery.getSqlText());
                assertEquals(5, testQuery.getFields().getFieldsCount());
                for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
                    Field fieldMtd = testQuery.getFields().get(i + 1);
                    assertNotNull(fieldMtd);
                }
                assertEquals(4, testQuery.getParameters().getParametersCount());
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testWithoutAliases_Schema_NonSchema_Schema_Columns() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQuery4_Schema_NonSchema_Select.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                assertEquals("SELECT EAS.MTD_EntitiES.MDENt_ID, MTD_EntitiES.MDENT_NAME, EAS.MTD_EntitiES.MDENT_TYPe, MDENT_ORDER FROM EaS.MTD_EntitiES",
                        testQuery.getSqlText());
                assertEquals(4, testQuery.getFields().getFieldsCount());
                for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
                    Field fieldMtd = testQuery.getFields().get(i + 1);
                    assertNotNull(fieldMtd);
                }
                assertEquals(0, testQuery.getParameters().getParametersCount());
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testPrimaryKey() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQuery4.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                Fields metadata = testQuery.getFields();
                assertNotNull(metadata);
                assertTrue(metadata.getFieldsCount() > 0);
                assertTrue(metadata.get(1).isPk());
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testPrimaryAndForeignKeysWithNamedOutputColumns() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQuery5.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                Fields fields = testQuery.getFields();
                assertNotNull(fields);
                assertTrue(fields.getFieldsCount() == 2);
                assertTrue(fields.get(1).isPk());
                assertTrue(fields.get(2).isPk());
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testPrimaryAndForeignKeysWithAsterisk() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQuery6.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                Fields fields = testQuery.getFields();
                assertNotNull(fields);
                assertTrue(fields.getFieldsCount() == 23);
                assertNotNull(fields.get("MDENT_ID"));
                assertTrue(fields.get("MDENT_ID").isPk());
                assertNotNull(fields.get("MDLOG_ID"));
                assertTrue(fields.get("MDLOG_ID").isPk());
                assertFalse(fields.getPrimaryKeys().isEmpty());
                assertEquals(2, fields.getPrimaryKeys().size());
                assertEquals("MDENT_ID", fields.getPrimaryKeys().get(0).getName());
                assertEquals("MDLOG_ID", fields.getPrimaryKeys().get(1).getName());
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testGetQuery() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryContent = readQueryContent(RESOURCES_PREFIX + "testQuery1.xml");
            String queryId = insertEntity(client, queryContent);
            try {
                SqlQuery testQuery = queryFactory.getQuery(queryId);
                Fields metadata = testQuery.getFields();
                assertEquals(3, metadata.getFieldsCount());
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testGetEmptyQuery() throws Exception {
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            StoredQueryFactory queryFactory = new StoredQueryFactory(client);
            String queryId = insertEmptyEntity(client);
            try {
                try {
                    SqlQuery testQuery = queryFactory.getQuery(queryId);
                    fail("Empty query must lead to an exception, but it doesn't. Why?");
                } catch (Exception ex) {
                    //fine. there muist be an exception
                }
            } finally {
                deleteEntity(queryId, client);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void testReadingQueryFromFile() throws IOException {
        String query = readQueryContent(RESOURCES_PREFIX + "testQuery1.xml");
        assertFalse(query.isEmpty());
    }

    private void deleteEntity(String aId, DbClient aClient) throws Exception {
        SqlQuery deleteEntity = new SqlQuery(aClient, "delete from MTD_ENTITIES where MDENT_ID = :id");
        deleteEntity.putParameter("id", DataTypeInfo.VARCHAR, aId);
        deleteEntity.compile().enqueueUpdate();
        int rowsAffected = aClient.commit(null);
        assertEquals(1, rowsAffected);
    }

    private String insertEmptyEntity(DbClient aClient) throws Exception {
        SqlQuery insertEntity = new SqlQuery(aClient, "insert into MTD_ENTITIES (MDENT_ID, MDENT_TYPE, MDENT_NAME) values (:id, :type, :name)");
        insertEntity.putParameter("type", DataTypeInfo.DECIMAL, ClientConstants.ET_QUERY);
        insertEntity.putParameter("name", DataTypeInfo.VARCHAR, "Test Query");
        String randomId = IDGenerator.genID().toString();
        insertEntity.putParameter("id", DataTypeInfo.VARCHAR, randomId);
        insertEntity.compile().enqueueUpdate(); // if no rows inserted - such entity exists
        assert randomId != null;
        int rowsAffected = aClient.commit(null);
        assertEquals(1, rowsAffected);
        return randomId;
    }

    private String insertEntity(DbClient aClient, String queryContent) throws Exception {
        String aQueryId = IDGenerator.genID().toString();
        SqlQuery insertEntity = new SqlQuery(aClient, "insert into MTD_ENTITIES (MDENT_ID, MDENT_TYPE, MDENT_NAME, MDENT_CONTENT_TXT) values (:id, :type, :name, :content)");
        insertEntity.putParameter("type", DataTypeInfo.DECIMAL, ClientConstants.ET_QUERY);
        insertEntity.putParameter("name", DataTypeInfo.VARCHAR, "Test Query");
        insertEntity.putParameter("content", DataTypeInfo.LONGVARCHAR, queryContent);
        insertEntity.putParameter("id", DataTypeInfo.VARCHAR, aQueryId);
        insertEntity.compile().enqueueUpdate();
        assert aQueryId != null;
        int rowsAffected = aClient.commit(null);
        assertEquals(1, rowsAffected);
        return aQueryId;
    }

    private String readQueryContent(String resourceName) throws IOException {
        InputStream is = StoredQueryFactoryTest.class.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            is = StoredQueryFactoryTest.class.getClassLoader().getResourceAsStream(resourceName.substring(1));
        }
        byte[] sData = BinaryUtils.readStream(is, -1);
        return new String(sData, SettingsConstants.COMMON_ENCODING);
    }
}
