/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.metadata;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class ParameterTest {

    public ParameterTest() {
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
    public void testConstructors() {
        System.out.println("testConstructors");
        Parameter instance = new Parameter("field1");
        assertEquals(instance.getName(), "field1");
        instance = new Parameter("field1", "fDesc1");
        assertEquals(instance.getName(), "field1");
        assertEquals(instance.getDescription(), "fDesc1");
        instance = new Parameter("field1", "fDesc1", DataTypeInfo.DECIMAL);
        assertEquals(instance.getName(), "field1");
        assertEquals(instance.getDescription(), "fDesc1");
        assertEquals(instance.getTypeInfo(), DataTypeInfo.DECIMAL);
        Parameter instance1 = new Parameter((Field) instance);
        assertTrue(instance.isEqual(instance1));
        instance.setMode(10);
        instance.setValue("jdfshksdhf");
        instance.setDefaultValue(null);
        instance.setSelectionForm(487684L);
        instance1 = new Parameter(instance);
        assertTrue(instance.isEqual(instance1));
    }

    /**
     * Test of equals method, of class Parameter.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Parameter instance = new Parameter("sampleParam");
        Parameter instance1 = new Parameter("sampleParam");
        boolean expResult = true;
        boolean result = instance.isEqual(instance1);
        assertEquals(expResult, result);
        instance1 = new Parameter("sampleParam1");
        result = instance.isEqual(instance1);
        expResult = false;
        assertEquals(expResult, result);

        // same values tests
        instance = new Parameter("sampleParam");
        instance.setMode(2);
        instance1 = new Parameter("sampleParam");
        instance1.setMode(2);
        assertTrue(instance.isEqual(instance1));

        instance = new Parameter("sampleParam");
        instance.setSelectionForm(876876L);
        instance1 = new Parameter("sampleParam");
        instance1.setSelectionForm(876876L);
        assertTrue(instance.isEqual(instance1));

        instance = new Parameter("sampleParam");
        instance.setValue(40);
        instance1 = new Parameter("sampleParam");
        instance1.setValue(40);
        assertTrue(instance.isEqual(instance1));

        instance = new Parameter("sampleParam");
        instance.setDefaultValue(4);
        instance1 = new Parameter("sampleParam");
        instance1.setDefaultValue(4);
        assertTrue(instance.isEqual(instance1));


        // different values tests
        instance = new Parameter("sampleParam");
        instance.setMode(2);
        instance1 = new Parameter("sampleParam");
        instance1.setMode(20);
        assertFalse(instance.isEqual(instance1));

        instance = new Parameter("sampleParam");
        instance.setSelectionForm(876876L);
        instance1 = new Parameter("sampleParam");
        instance1.setSelectionForm(8776L);
        assertFalse(instance.isEqual(instance1));

        instance = new Parameter("sampleParam");
        instance.setValue(40);
        instance1 = new Parameter("sampleParam");
        instance1.setValue(400);
        assertFalse(instance.isEqual(instance1));

        instance = new Parameter("sampleParam");
        instance.setDefaultValue(4);
        instance1 = new Parameter("sampleParam");
        instance1.setDefaultValue(40);
        assertFalse(instance.isEqual(instance1));
    }
}
