/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.metadata;

import com.bearsoft.rowset.RowsetBaseTest;


/**
 *
 * @author mg
 */
public class ParametersTest extends RowsetBaseTest{

    public ParametersTest() {
    }

    /**
     * Test of isEmpty method, of class Fields.
     */
    public void testIsEmpty() {
        System.out.println("isEmpty");
        Fields instance = new Parameters();
        boolean expResult = true;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultNamePrefix method, of class Fields.
     */
    public void testGetDefaultNamePrefix() {
        System.out.println("getDefaultNamePrefix");
        Fields instance = new Parameters();
        String expResult = "Param";
        String result = instance.getDefaultNamePrefix();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTableDescription method, of class Fields.
     */
    public void testSetTableDescription() {
        System.out.println("setTableDescription");
        String tableDescription = "table1";
        Fields instance = new Parameters();
        instance.setTableDescription(tableDescription);
        String expResult = "table1";
        String result = instance.getTableDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of generateNewName method, of class Fields.
     */
    public void testGenerateNewName_0args() {
        System.out.println("generateNewName");
        Fields instance = new Parameters();
        String expResult = "Param1";
        String result = instance.generateNewName();
        assertEquals(expResult, result);
    }

    /**
     * Test of generateNewName method, of class Fields.
     */
    public void testGenerateNewName_String() {
        System.out.println("generateNewName");
        String aPrefix = "TF_";
        Fields instance = new Parameters();
        String expResult = "TF_1";
        String result = instance.generateNewName(aPrefix);
        assertEquals(expResult, result);
    }

    /**
     * Test of isNameAlreadyPresent method, of class Fields.
     */
    public void testIsNameAlreadyPresent() {
        System.out.println("isNameAlreadyPresent");
        String aName = "sampleName";

        Field aField2Skip = null;
        Fields instance = new Parameters();
        Field field = new Parameter(aName, "Примерный параметр");
        instance.add(field);
        boolean expResult = true;
        boolean result = instance.isNameAlreadyPresent(aName, aField2Skip);
        assertEquals(expResult, result);
        aField2Skip = field;
        expResult = false;
        result = instance.isNameAlreadyPresent(aName, aField2Skip);
        assertEquals(expResult, result);
    }

    /**
     * Test of createNewField method, of class Fields.
     */
    public void testCreateNewField_0args() {
        System.out.println("createNewField");
        Fields instance = new Parameters();
        Field unexpResult = null;
        Field result = instance.createNewField();
        assertNotSame(unexpResult, result);
    }

    /**
     * Test of createNewField method, of class Fields.
     */
    public void testCreateNewField_String() {
        System.out.println("createNewField");
        String aName = "sampleName";
        Fields instance = new Parameters();
        Field unexpResult = null;
        Field result = instance.createNewField(aName);
        instance.add(result);
        assertNotSame(unexpResult, result);
        assertEquals(result.getName(), aName);
    }

    /**
     * Test of getFieldsCount method, of class Fields.
     */
    public void testGetFieldsCount() {
        System.out.println("getFieldsCount");
        Fields instance = new Parameters();
        Field field = instance.createNewField();
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);
    }

    /**
     * Test of clear method, of class Fields.
     */
    public void testClear() {
        System.out.println("clear");
        Fields instance = new Parameters();
        Field field = instance.createNewField();
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);
        instance.clear();
        result = instance.getFieldsCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of remove method, of class Fields.
     */
    public void testRemove() {
        System.out.println("remove");
        Fields instance = new Parameters();
        Field field = instance.createNewField();
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);
        instance.remove(field);
        result = instance.getFieldsCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of get method, of class Fields.
     */
    public void testGet_int() {
        System.out.println("get(int)");
        Parameters instance = new Parameters();
        Parameter field = (Parameter)instance.createNewField();
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);
        assertEquals(instance.get(1), field);
    }

    /**
     * Test of get method, of class Fields.
     */
    public void testGet_String() {
        System.out.println("get(String)");
        Parameters instance = new Parameters();
        Parameter field = (Parameter)instance.createNewField("sample");
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);
        assertEquals(instance.get("sample"), field);
    }

    /**
     * Test of clone method, of class Fields.
     */
    public void testClone() {
        System.out.println("clone");
        Parameters instance = new Parameters();
        Parameters expResult = instance;
        Parameters result = (Parameters)instance.copy();
        assertEquals(expResult.getFieldsCount(), result.getFieldsCount());
        for (int i = 0; i < expResult.getFieldsCount(); i++) {
            assertEquals(expResult.get(i + 1), result.get(i + 1));
        }
    }

    /**
     * Test of copy method, of class Fields.
     */
    public void testCopy() {
        System.out.println("copy");
        Parameters instance = new Parameters();
        Parameters expResult = instance;
        Parameters result = instance.copy();
        assertEquals(expResult.getFieldsCount(), result.getFieldsCount());
        for (int i = 0; i < expResult.getFieldsCount(); i++) {
            assertEquals(expResult.get(i + 1), result.get(i + 1));
        }
    }

    /**
     * Test of getColumnCount method, of class Fields.
     */
    public void testGetColumnCount() throws Exception {
        System.out.println("getParameterCount");
        Parameters instance = new Parameters();
        Field field = instance.createNewField("sample");
        int expResult = 0;
        int result = instance.getParametersCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getParametersCount();
        assertEquals(1, result);
        assertEquals(instance.get("sample"), field);
    }

    /**
     * Test of findColumn method, of class Fields.
     */
    public void testFindColumn() throws Exception {
        System.out.println("findColumn");
        Parameters instance = new Parameters();
        Field field = instance.createNewField("sample");
        int expResult = 0;
        int result = instance.getParametersCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.find("sample");
        assertEquals(1, result);
    }
}