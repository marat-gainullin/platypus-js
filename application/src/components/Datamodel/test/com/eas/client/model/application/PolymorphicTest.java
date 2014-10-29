/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.BaseModelTest;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryEntity;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class PolymorphicTest extends BaseModelTest {

    @Test
    public void applicationEntityTest() {
        System.out.println("applicationEntityTest");
        ApplicationDbModel dm = new ApplicationDbModel();
        ApplicationDbEntity entity = dm.newGenericEntity();
        assertNotNull(entity);
        assertNotNull(dm.getParametersEntity());
    }

    @Test
    public void queryEntityTest() {
        System.out.println("slaveEntityTest");
        QueryModel dm = new QueryModel();
        QueryEntity entity = dm.newGenericEntity();
        assertNotNull(entity);
        assertNotNull(dm.getParametersEntity());
    }

    @Test
    public void fieldsEntityTest() {
        System.out.println("fieldsEntityTest");
        DbSchemeModel dm = new DbSchemeModel();
        FieldsEntity entity = dm.newGenericEntity();
        assertNotNull(entity);
        assertNull(dm.getParametersEntity());// Db Schema diagram have no parameters!
    }

    @Test
    public void tables_n_SchemasNames_Forms_Test() {
        System.out.println("Forms polymorphic test");
        // entities calls vs datamodel data in class hierarchy
        String dbId = "4546465";
        String schemaName = "sampleSchema";
        String tableName = "sampleTable";
        ApplicationDbModel dm = new ApplicationDbModel();
        ApplicationDbEntity entity = dm.newGenericEntity();
        entity.setTableDatasourceName(dbId);
        entity.setTableSchemaName(schemaName);
        entity.setTableName(tableName);
        assertEquals(entity.getTableDatasourceName(), dbId);
        assertEquals(entity.getTableSchemaName(), schemaName);
        assertEquals(entity.getTableName(), tableName);
    }

    @Test
    public void tables_n_SchemasNames_Queries_Test() {
        System.out.println("Queries polymorphic test");
        // entities calls vs query model data in class hierarchy
        String dbId = "4546465";
        String schemaName = "sampleSchema";
        String tableName = "sampleTable";
        QueryModel qm = new QueryModel();
        qm.setDbId(dbId);
        QueryEntity entity = qm.newGenericEntity();

        String dbId1 = "89096465";
        entity.setTableDatasourceName(dbId1);
        entity.setTableSchemaName(schemaName);
        entity.setTableName(tableName);

        assertEquals(entity.getTableDatasourceName(), dbId);
        assertEquals(entity.getTableSchemaName(), schemaName);
        assertEquals(entity.getTableName(), tableName);

        // SlaveEntity instances can't be responsible on database. Only model is responsible.
        assertFalse(entity.getTableDatasourceName().equals(dbId1));
    }

    @Test
    public void tables_n_SchemasNames_DbSctructure_Test() {
        System.out.println("DbSctructure polymorphic test");
        // entities calls vs db structure model data in class hierarchy
        String dbId = "4546465";
        String schemaName = "sampleSchema";
        String tableName = "sampleTable";
        DbSchemeModel sm = new DbSchemeModel();
        sm.setDbId(dbId);
        sm.setSchema(schemaName);

        FieldsEntity entity = sm.newGenericEntity();
        String dbId1 = "89096465";
        String schemaName1 = "entitySampleSchema";
        entity.setTableDatasourceName(dbId1);
        entity.setTableSchemaName(schemaName1);
        entity.setTableName(tableName);

        assertEquals(entity.getTableDatasourceName(), dbId);
        assertEquals(entity.getTableSchemaName(), schemaName);
        assertEquals(entity.getTableName(), tableName);

        assertFalse(entity.getTableDatasourceName().equals(dbId1));
        assertFalse(entity.getTableSchemaName().equals(schemaName1));
    }
}
