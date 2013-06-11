/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.bearsoft.rowset.Rowset;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class RowsetFeatureDescriptorTest extends GeoBaseTest
{
    private static ApplicationDbModel datamodel;
    private static ApplicationDbEntity colaMarkets;

    public RowsetFeatureDescriptorTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        GeoBaseTest.setUpClass();
        datamodel = new ApplicationDbModel(dbClient);
        datamodel.setRuntime(true);
        colaMarkets = new ApplicationDbEntity(datamodel);
        colaMarkets.regenerateId();
        colaMarkets.setTableName("COLA_MARKETS");
        datamodel.addEntity(colaMarkets);
        final Rowset rowset = colaMarkets.getRowset();
        assertNotNull(rowset);
        assertTrue(rowset.size() > 0);
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of getEntity method, of class RowsetFeatureDescriptor.
     */
    @Test
    public void testGetEntity()
    {
        System.out.println("getEntity");
        RowsetFeatureDescriptor instance = new RowsetFeatureDescriptor("colaMarkets", colaMarkets);
        assertEquals(colaMarkets, instance.getEntity());
        assertEquals("colaMarkets", instance.getTypeName());
    }

    /**
     * Test of setEntity method, of class RowsetFeatureDescriptor.
     */
    @Test
    public void testSetEntity()
    {
        System.out.println("setEntity");
        RowsetFeatureDescriptor instance = new RowsetFeatureDescriptor();
        assertNull(instance.getEntity());
        instance.setEntity(colaMarkets);
        assertNull(instance.getTypeName());
        assertEquals(colaMarkets, instance.getEntity());
    }

    /**
     * Test of getTypeName method, of class RowsetFeatureDescriptor.
     */
    @Test
    public void testGetTypeName()
    {
        System.out.println("getTypeName");
        RowsetFeatureDescriptor instance = new RowsetFeatureDescriptor();
        assertNull(instance.getTypeName());
        instance.setTypeName("cola markets");
        assertEquals("cola markets", instance.getTypeName());
    }

    /**
     * Test of setTypeName method, of class RowsetFeatureDescriptor.
     */
    @Test
    public void testSetTypeName()
    {
        System.out.println("setTypeName");
        RowsetFeatureDescriptor instance = new RowsetFeatureDescriptor();
        assertNull(instance.getTypeName());
        instance.setTypeName("cola markets");
        assertEquals("cola markets", instance.getTypeName());
    }

    /**
     * Test of toString method, of class RowsetFeatureDescriptor.
     */
    @Test
    public void testToString()
    {
        System.out.println("toString");
        RowsetFeatureDescriptor instance = new RowsetFeatureDescriptor("colaMarkets", colaMarkets);
        assertTrue(instance.toString().startsWith("Features rowset, type colaMarkets, entity com.eas.client.model.application.ApplicationDbEntity@"));
    }

    @Test
    public void testCloning() throws CloneNotSupportedException
    {
        System.out.println("clone");
        RowsetFeatureDescriptor instance = new RowsetFeatureDescriptor("colaMarkets", colaMarkets);
        RowsetFeatureDescriptor cloned = instance.copy();
        assertNotSame(instance, cloned);
        assertEquals(instance.getCrsWkt(), cloned.getCrsWkt());
        assertEquals(instance.getEntity(), cloned.getEntity());
        assertEquals(instance.getGeometryBinding(), cloned.getGeometryBinding());
        assertEquals(instance.getRef(), cloned.getRef());
        assertEquals(instance.getTypeName(), cloned.getTypeName());
    }

}
