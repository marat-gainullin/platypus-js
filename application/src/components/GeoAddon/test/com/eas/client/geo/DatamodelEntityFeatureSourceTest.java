/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.bearsoft.rowset.Rowset;
import com.eas.client.geo.datastore.DatamodelDataStore;
import com.eas.client.geo.datastore.RowsFeatureSource;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import java.io.IOException;
import java.util.HashMap;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.feature.FeatureCollection;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @author pk
 */
@RunWith(JMock.class)
public class DatamodelEntityFeatureSourceTest extends GeoBaseTest {

    private ApplicationDbModel datamodel;
    private HashMap<String, RowsetFeatureDescriptor> map;
    private DatamodelDataStore dataStore;
    private Mockery context = new JUnit4Mockery();

    public DatamodelEntityFeatureSourceTest() {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        datamodel = new ApplicationDbModel(dbClient);
        datamodel.setRuntime(true);
        map = new HashMap<>();
        ApplicationDbEntity e = new ApplicationDbEntity(datamodel);
        e.regenerateId();
        e.setTableName("COLA_MARKETS");
        datamodel.addEntity(e);
        e.validateQuery();
        final Rowset rowset = e.getRowset();
        assertNotNull(rowset);
        rowset.refresh();
        assertTrue(rowset.size() > 0);
        map.put(e.getTableName(), new RowsetFeatureDescriptor(e.getTableName(), e));
        dataStore = new DatamodelDataStore();
        dataStore.setFeatureDescriptors(map);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDataStore method, of class RowsFeatureSource.
     *
     * @throws IOException
     */
    @Test
    public void testGetDataStore() throws IOException {
        System.out.println("getDataStore");
        RowsFeatureSource instance = (RowsFeatureSource) dataStore.getFeatureSource("COLA_MARKETS");
        assertEquals(dataStore, instance.getDataStore());
    }

    /**
     * Test of addFeatureListener method, of class RowsFeatureSource.
     *
     * @throws IOException
     * @Test public void testAddFeatureListener() throws IOException { try {
     * System.out.println("addFeatureListener"); final RowsFeatureSource
     * instance = (RowsFeatureSource)
     * dataStore.getFeatureSource("COLA_MARKETS"); final FeatureListener
     * listener = context.mock(FeatureListener.class);
     * instance.addFeatureListener(listener); context.checking(new
     * Expectations() {
     *
     * {
     * oneOf(listener).changed((FeatureEvent) with(allOf(
     * aNonNull(FeatureEvent.class), hasProperty("bounds",
     * aNull(ReferencedEnvelope.class)), hasProperty("filter",
     * aNull(Filter.class)), hasProperty("source", same(instance)),
     * hasProperty("type", equalTo(Type.CHANGED))))); } });
     * map.get("COLA_MARKETS").getEntity().refresh(); } catch (SQLException ex)
     * { throw new IOException(ex); } }
     */
    /**
     * Test of removeFeatureListener method, of class RowsFeatureSource.
     *
     * @throws IOException
     */
    @Test
    public void testRemoveFeatureListener() throws IOException {
        System.out.println("removeFeatureListener");
        RowsFeatureSource instance = (RowsFeatureSource) dataStore.getFeatureSource("COLA_MARKETS");
        final FeatureListener listener = context.mock(FeatureListener.class);
        context.checking(new Expectations() {
            {
                never(listener).changed(with(any(FeatureEvent.class)));
            }
        });
        instance.addFeatureListener(listener);
        instance.removeFeatureListener(listener);
    }

    /**
     * Test of getSchema method, of class RowsFeatureSource.
     *
     * @throws IOException
     */
    @Test
    public void testGetSchema() throws IOException {
        System.out.println("getSchema");
        RowsFeatureSource instance = (RowsFeatureSource) dataStore.getFeatureSource("COLA_MARKETS");
        SimpleFeatureType result = instance.getSchema();
        assertEquals("COLA_MARKETS", result.getTypeName());
    }

    /**
     * Test of getFeatures method, of class RowsFeatureSource.
     *
     * @throws IOException
     */
    @Test
    public void testGetFeatures() throws IOException {
        System.out.println("getFeatures");
        RowsFeatureSource instance = (RowsFeatureSource) dataStore.getFeatureSource("COLA_MARKETS");
        final FeatureCollection<SimpleFeatureType, SimpleFeature> features = instance.getFeatures();
        assertEquals(4, features.size());
    }
}
