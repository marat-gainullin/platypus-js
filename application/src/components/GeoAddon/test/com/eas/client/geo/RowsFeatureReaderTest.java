/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.bearsoft.rowset.Rowset;
import com.eas.client.geo.datastore.DatamodelDataStore;
import com.eas.client.geo.datastore.RowsFeatureReader;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author pk
 */
public class RowsFeatureReaderTest extends GeoBaseTest {

    private ApplicationDbModel model;
    private DatamodelDataStore ds;

    public RowsFeatureReaderTest() {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        model = new ApplicationDbModel(dbClient);
        model.requery();
        final Map<String, RowsetFeatureDescriptor> map = new HashMap<>();
        ApplicationDbEntity e = model.newGenericEntity();
        e.regenerateId();
        e.setTableName("COLA_MARKETS");
        e.validateQuery();
        assertNotNull(e.getRowset());
        model.addEntity(e);
        map.put(e.getTableName(), new RowsetFeatureDescriptor(e.getTableName(), e));
        ds = new DatamodelDataStore();
        ds.setFeatureDescriptors(map);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getFeatureType method, of class RowsFeatureReader.
     *
     * @throws IOException
     */
    @Test
    public void testGetFeatureType() throws IOException {
        System.out.println("getFeatureType");
        RowsFeatureReader reader = (RowsFeatureReader) ds.getFeatureReader("COLA_MARKETS");
        FeatureType expResult = ds.getSchema("COLA_MARKETS");
        FeatureType result = reader.getFeatureType();
        assertEquals(expResult, result);
    }

    /**
     * Test of next method, of class RowsFeatureReader.
     *
     * @throws Exception
     */
    @Test
    public void testNext() throws Exception {
        System.out.println("next");
        final Rowset rowset = model.getEntityByTableName("COLA_MARKETS").getRowset();
        rowset.beforeFirst();
        final int rowsetSize = rowset.size();
        // RowsFeatureReader positions rowset at beforeFirst on its creation.
        final RowsFeatureReader reader = (RowsFeatureReader) ds.getFeatureReader("COLA_MARKETS");
        for (int i = 0; i < rowsetSize; i++) {
            final SimpleFeature ft = reader.next();
            rowset.next();
            /* RowsFeatureReader repositions rowset at next record each time next() is called.
             * We can take values both from rowset and reader and compare them.
             */
            for (int j = 1; j <= rowset.getFields().getFieldsCount(); j++) {
                assertEquals(rowset.getObject(j), ft.getAttribute(rowset.getFields().get(j).getName()));
            }
        }
    }

    /**
     * Test of hasNext method, of class RowsFeatureReader.
     *
     * @throws Exception
     */
    @Test
    public void testHasNext() throws Exception {
        System.out.println("hasNext");
        final Rowset rowset = model.getEntityByTableName("COLA_MARKETS").getRowset();
        final int rowsetSize = rowset.size();
        final RowsFeatureReader reader = (RowsFeatureReader) ds.getFeatureReader("COLA_MARKETS");
        for (int i = 0; i < rowsetSize; i++) {
            assertTrue(reader.hasNext());
            reader.next();
        }
        assertFalse(reader.hasNext());
    }

    /**
     * Test of close method, of class RowsFeatureReader.
     *
     * @throws Exception
     */
    @Test
    public void testClose() throws Exception {
        System.out.println("close");
        final RowsFeatureReader reader = (RowsFeatureReader) ds.getFeatureReader("COLA_MARKETS");
        reader.close();
    }
}
