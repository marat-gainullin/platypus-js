/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.eas.client.geo.datastore.DatamodelDataStore;
import com.eas.client.geo.datastore.RowsFeatureReader;
import com.eas.client.geo.datastore.RowsFeatureSource;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.vividsolutions.jts.geom.Polygon;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

/**
 *
 * @author pk
 */
public class DatamodelDataStoreTest extends GeoBaseTest
{
    private Map<String, RowsetFeatureDescriptor> map;
    private ApplicationDbModel datamodel;

    public DatamodelDataStoreTest()
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        datamodel = new ApplicationDbModel(dbClient);
        datamodel.setRuntime(true);
        map = new HashMap<>();
        ApplicationDbEntity e = new ApplicationDbEntity(datamodel);
        e.regenerateId();
        e.setTableName("COLA_MARKETS");
        datamodel.addEntity(e);
        final Rowset rowset = e.getRowset();
        assertNotNull(rowset);
        assertTrue(rowset.size() > 0);
        map.put(e.getTableName(), new RowsetFeatureDescriptor(e.getTableName(), e));
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of getTypeNames method, of class DatamodelDataStore.
     * @throws Exception
     */
    @Test
    public void testGetTypeNames() throws Exception
    {
        System.out.println("getTypeNames");
        DatamodelDataStore myDS = new DatamodelDataStore();
        String[] expResult = new String[0];
        String[] result = myDS.getTypeNames();
        assertArrayEquals(expResult, result);
        //TODO put some datasources into myDS and check the type names to be equal to datasources names.
        myDS.setFeatureDescriptors(map);
        expResult = new String[]
                {
                    "COLA_MARKETS"
                };
        result = myDS.getTypeNames();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getSchema method, of class DatamodelDataStore.
     * @throws Exception
     */
    @Test
    public void testGetSchema() throws Exception
    {
        System.out.println("getSchema");
        final DatamodelDataStore myDS = new DatamodelDataStore();
        myDS.setFeatureDescriptors(map);
        //TODO test for SRS.
        final SimpleFeatureType schema = myDS.getSchema("COLA_MARKETS");
        final String[] names = new String[]
        {
            "MKT_ID", "NAME", "SHAPE", DatamodelDataStore.ROW_ATTR_NAME
        };
        assertEquals(names.length, schema.getAttributeCount());
        AttributeDescriptor descriptor = schema.getDescriptor("MKT_ID");
        assertEquals(BigDecimal.class, descriptor.getType().getBinding());
        assertEquals("MKT_ID", descriptor.getLocalName());
        descriptor = schema.getDescriptor("NAME");
        assertEquals(String.class, descriptor.getType().getBinding());
        assertEquals("NAME", descriptor.getLocalName());
        descriptor = schema.getDescriptor("SHAPE");
        assertEquals(Polygon.class, descriptor.getType().getBinding());
        assertEquals("SHAPE", descriptor.getLocalName());
        descriptor = schema.getDescriptor(DatamodelDataStore.ROW_ATTR_NAME);
        assertEquals(Row.class, descriptor.getType().getBinding());
        assertEquals(DatamodelDataStore.ROW_ATTR_NAME, descriptor.getLocalName());
    }

    /**
     * Test of getFeatureReader method, of class DatamodelDataStore.
     * @throws Exception
     */
    @Test
    public void testGetFeatureReader() throws Exception
    {
        System.out.println("getFeatureReader");
        final DatamodelDataStore myDS = new DatamodelDataStore();
        myDS.setFeatureDescriptors(map);
        FeatureReader reader = myDS.getFeatureReader("COLA_MARKETS");
        assertNotNull(reader);
        assertTrue(reader instanceof RowsFeatureReader);
        final FeatureSource<SimpleFeatureType, SimpleFeature> source = myDS.getFeatureSource("COLA_MARKETS");
        assertNotNull(source);
    }

    /**
     * Test of getFeatureDescriptors method, of class DatamodelDataStore.
     */
    @Test
    public void testGetFeatureDSes()
    {
        System.out.println("getFeatureDSes");
        DatamodelDataStore instance = new DatamodelDataStore();
        assertEquals(0, instance.getFeatureDescriptors().size());
        instance.setFeatureDescriptors(map);
        assertEquals(map, instance.getFeatureDescriptors());
    }

    /**
     * Test of setFeatureDescriptors method, of class DatamodelDataStore.
     */
    @Test
    public void testSetFeatureDSes()
    {
        System.out.println("setFeatureDSes");
        DatamodelDataStore instance = new DatamodelDataStore();
        //Test setting as map.
        instance.setFeatureDescriptors(map);
        assertEquals(map, instance.getFeatureDescriptors());
        //Clear dses for the next test.
        instance.setFeatureDescriptors(new HashMap<String, RowsetFeatureDescriptor>());
        assertFalse(map.equals(instance.getFeatureDescriptors()));
        // Test setting as a collection
        final List<RowsetFeatureDescriptor> expResult = new ArrayList<>(map.values());
        instance.setFeatureDescriptors(map.values());
        assertEquals(map, instance.getFeatureDescriptors());
    }

    /**
     * Test of getFeatureSource method, of class DatamodelDataStore.
     * @throws Exception 
     */
    @Test
    public void testGetFeatureSource() throws Exception
    {
        System.out.println("getFeatureSource");
        DatamodelDataStore instance = new DatamodelDataStore();
        instance.setFeatureDescriptors(map);
        RowsFeatureSource source = (RowsFeatureSource) instance.getFeatureSource("COLA_MARKETS");
    }
}
