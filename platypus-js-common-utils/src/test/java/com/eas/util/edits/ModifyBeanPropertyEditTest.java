/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util.edits;

import com.eas.util.edits.ModifyBeanPropertyEdit;
import com.eas.util.edits.ModifyBeanPropertyEdit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class ModifyBeanPropertyEditTest
{
    public ModifyBeanPropertyEditTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
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
     * Test of die method, of class ModifyBeanPropertyEdit.
     * @throws NoSuchMethodException
     */
    @Test
    public void testDie() throws NoSuchMethodException
    {
        System.out.println("die");
        ABean bean = new ABean("123", 123, false);
        // test for string property
        ModifyBeanPropertyEdit instance = new ModifyBeanPropertyEdit<>(Integer.class, bean, "intProp", 123, 321);
        instance.die();
        // we cannot check whether reference to bean was deleted.
    }

    /**
     * Test of redo method, of class ModifyBeanPropertyEdit.
     * @throws NoSuchMethodException
     */
    @Test
    public void testRedo() throws NoSuchMethodException
    {
        System.out.println("redo");
        ABean bean = new ABean("123", 123, false);
        // test for string property
        ModifyBeanPropertyEdit instance = new ModifyBeanPropertyEdit<>(Integer.class, bean, "intProp", 123, 321);
        instance.undo();
        assertEquals(123, bean.getIntProp().intValue());
        instance.redo();
        assertEquals(321, bean.getIntProp().intValue());
        // test for integer property
        instance = new ModifyBeanPropertyEdit<>(String.class, bean, "stringProp", "123", "321");
        instance.undo();
        assertEquals("123", bean.stringProp);
        instance.redo();
        assertEquals("321", bean.stringProp);
        // test assignment of nulls
        instance = new ModifyBeanPropertyEdit<>(Boolean.class, bean, "boolProp", false, null);
        instance.undo();
        assertEquals(false, bean.getBoolProp());
        instance.redo();
        assertNull(bean.getBoolProp());
        // test for non-existent property
        try
        {
            instance = new ModifyBeanPropertyEdit<>(Double.class, bean, "cost", 12.3, 32.2);
            fail("Edit found non-existant methods");
        } catch (NoSuchMethodException ex)
        {
            // ok, there is no setter, nor getter
            System.out.println("Non-existant property 'cost' lead to error. Fine!");
        }
        // test for incompatible values type
        try
        {
            instance = new ModifyBeanPropertyEdit<>(String.class, bean, "boolProp", "false", "true");
            fail("Edit allowed creation with values incompatible to property type.");
        } catch (NoSuchMethodException ex)
        {
            System.out.println("Assignment of String to boolProp lead to exception. Fine!");
        }
    }

    /**
     * Test of undo method, of class ModifyBeanPropertyEdit.
     * @throws NoSuchMethodException 
     */
    @Test
    public void testUndo() throws NoSuchMethodException
    {
        System.out.println("undo");
        ABean bean = new ABean("321", 321, true);
        // test for string property
        ModifyBeanPropertyEdit instance = new ModifyBeanPropertyEdit<>(Integer.class, bean, "intProp", 123, 321);
        assertEquals(321, bean.getIntProp().intValue());
        instance.undo();
        assertEquals(123, bean.getIntProp().intValue());
        // test for integer property
        instance = new ModifyBeanPropertyEdit<>(String.class, bean, "stringProp", "123", "321");
        assertEquals("321", bean.stringProp);
        instance.undo();
        assertEquals("123", bean.stringProp);
    }

    private class ABean
    {
        private String stringProp;
        private Integer intProp;
        private Boolean boolProp;

        public ABean()
        {
        }

        public ABean(String stringProp, Integer intProp, Boolean boolProp)
        {
            this.stringProp = stringProp;
            this.intProp = intProp;
            this.boolProp = boolProp;
        }

        public Boolean getBoolProp()
        {
            return boolProp;
        }

        public void setBoolProp(Boolean boolProp)
        {
            this.boolProp = boolProp;
        }

        public Integer getIntProp()
        {
            return intProp;
        }

        public void setIntProp(Integer intProp)
        {
            this.intProp = intProp;
        }

        public String getStringProp()
        {
            return stringProp;
        }

        public void setStringProp(String stringProp)
        {
            this.stringProp = stringProp;
        }
    }
}
