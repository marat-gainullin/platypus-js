/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import com.eas.client.DatabasesClientWithResource;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class RelationsTest extends BaseTest {

    @Test
    public void attainabilityTest() throws Exception {
        System.out.println("attainabilityTest. Field to parameter, parameter to parameter and field to parameter relations test");
        try (DatabasesClientWithResource resource = BaseTest.initDevelopTestClient()) {
            ApplicationDbModel model = new ApplicationDbModel(resource.getClient());
            ApplicationDbEntity entity11 = model.newGenericEntity();
            entity11.setTableName("GOODORDER");
            ApplicationDbEntity entity21 = model.newGenericEntity();
            entity21.setQueryId("124349292311931632");
            ApplicationDbEntity entity22 = model.newGenericEntity();
            entity22.setQueryId("124349292311931632");
            ApplicationDbEntity entity31 = model.newGenericEntity();
            entity31.setQueryId("124349292311931632");
            ApplicationDbEntity entity32 = model.newGenericEntity();
            entity32.setQueryId("124349292311931632");
            ApplicationDbEntity entity33 = model.newGenericEntity();
            entity33.setQueryId("124349292311931632");
            ApplicationDbEntity entity41 = model.newGenericEntity();
            entity41.setQueryId("124349292311931632");

            assertTrue(model.getEntities().isEmpty());// Parameters entity is always present
            assertNotNull(model.getParametersEntity());

            Relation<ApplicationDbEntity> rel11_21 = new Relation<>(entity11, entity11.getFields().get("AMOUNT"), entity21, entity21.getQuery().getParameters().get("amount"));
            Relation<ApplicationDbEntity> rel11_22 = new Relation<>(entity11, entity11.getFields().get("AMOUNT"), entity22, entity22.getQuery().getParameters().get("amount"));

            Relation<ApplicationDbEntity> rel21_31 = new Relation<>(entity21, entity21.getQuery().getParameters().get("amount"), entity31, entity31.getQuery().getParameters().get("amount"));
            Relation<ApplicationDbEntity> rel21_33 = new Relation<>(entity21, entity21.getFields().get("amount"), entity33, entity33.getQuery().getParameters().get("amount"));
            Relation<ApplicationDbEntity> rel21_31_ = new Relation<>(entity21, entity21.getFields().get("ORDER_ID"), entity31, entity31.getFields().get("ORDER_ID"));

            Relation<ApplicationDbEntity> rel22_32 = new Relation<>(entity22, entity22.getQuery().getParameters().get("amount"), entity32, entity32.getQuery().getParameters().get("amount"));
            Relation<ApplicationDbEntity> rel22_33_ = new Relation<>(entity22, entity22.getFields().get("ORDER_ID"), entity33, entity33.getFields().get("ORDER_ID"));

            Relation<ApplicationDbEntity> rel11_41 = new Relation<>(entity11, entity11.getFields().get("AMOUNT"), entity41, entity41.getQuery().getParameters().get("amount"));
            Relation<ApplicationDbEntity> rel32_41 = new Relation<>(entity32, entity32.getFields().get("AMOUNT"), entity41, entity41.getFields().get("amount"));

            model.addEntity(entity11);
            model.addEntity(entity21);
            model.addEntity(entity22);
            model.addEntity(entity31);
            model.addEntity(entity32);
            model.addEntity(entity33);
            model.addEntity(entity41);

            assertEquals(model.getEntities().size(), 7);// 7 user entities and parameters entity
            assertNotNull(model.getParametersEntity());

            model.addRelation(rel11_21);
            model.addRelation(rel11_22);
            model.addRelation(rel21_31);
            model.addRelation(rel21_33);
            model.addRelation(rel21_31_);
            model.addRelation(rel22_32);
            model.addRelation(rel22_33_);
            model.addRelation(rel11_41);
            model.addRelation(rel32_41);

            assertEquals(model.getRelations().size(), 9);

            model.setRuntime(true);
            // pks control section
            assertEquals(1, entity11.getFields().getPrimaryKeys().size());
            assertEquals(1, entity21.getFields().getPrimaryKeys().size());
            assertEquals(1, entity22.getFields().getPrimaryKeys().size());
            assertEquals(1, entity31.getFields().getPrimaryKeys().size());
            assertEquals(1, entity32.getFields().getPrimaryKeys().size());
            assertEquals(1, entity33.getFields().getPrimaryKeys().size());
            assertEquals(1, entity41.getFields().getPrimaryKeys().size());
            ////

            // 1 layer
            assertNotNull(entity11.getRowset());
            assertTrue(entity11.getRowset().size() > 0);
            List<Field> keyCols = entity11.getRowset().getFields().getPrimaryKeys();
            assertNotNull(keyCols);
            assertEquals(keyCols.size(), 1);
            List<SortingCriterion> criteria = new ArrayList<>();
            criteria.add(new SortingCriterion(entity11.getRowset().getFields().find(keyCols.get(0).getName()), true));
            entity11.getRowset().sort(new RowsComparator(criteria));
            entity11.getRowset().beforeFirst();
            while (entity11.getRowset().next()) {
                // 2 layer
                assertNotNull(entity21.getRowset());
                assertNotNull(entity22.getRowset());

                assertTrue(entity21.getRowset().size() > 0);
                assertTrue(entity22.getRowset().size() > 0);

                // 3 layer
                assertNotNull(entity31.getRowset());
                assertNotNull(entity32.getRowset());
                assertNotNull(entity33.getRowset());

                assertTrue(entity31.getRowset().size() > 0);
                assertTrue(entity32.getRowset().size() > 0);
                assertTrue(entity33.getRowset().size() > 0);

                // 4 layer
                assertNotNull(entity41.getRowset());

                assertTrue(entity41.getRowset().size() > 0);
            }
            entity11.getRowset().first();
            // inter layer
            assertTrue(entity21.getRowset().size() > entity31.getRowset().size());

            assertEquals(entity22.getRowset().size(), entity32.getRowset().size());

            assertTrue(entity22.getRowset().size() > entity31.getRowset().size());
            assertTrue(entity32.getRowset().size() > entity31.getRowset().size());

            assertEquals(entity11.getRowset().getObject(entity11.getRowset().getFields().find("amount")), entity41.getQuery().getParameters().get("amount").getValue());

            entity32.getRowset().beforeFirst();
            while (entity32.getRowset().next()) {
                BigDecimal bdAmount = (BigDecimal) entity32.getRowset().getObject(entity32.getRowset().getFields().find("amount"));
                double amount = bdAmount.doubleValue();
                entity41.getRowset().beforeFirst();
                while (entity41.getRowset().next()) {
                    BigDecimal _bdAmount = (BigDecimal) entity41.getRowset().getObject(entity41.getRowset().getFields().find("amount"));
                    double _amount = _bdAmount.doubleValue();
                    assertEquals((Double) amount, (Double) _amount);
                }
            }
        }
    }
}
