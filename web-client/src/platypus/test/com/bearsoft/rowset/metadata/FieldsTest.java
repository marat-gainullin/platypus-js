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
public class FieldsTest extends RowsetBaseTest{

    public FieldsTest() {
    }

    /**
     * Test of isEmpty method, of class Fields.
     */
    public void testIsEmpty() {
        System.out.println("isEmpty");
        Fields instance = new Fields();
        boolean expResult = true;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultNamePrefix method, of class Fields.
     */
    public void testGetDefaultNamePrefix() {
        System.out.println("getDefaultNamePrefix");
        Fields instance = new Fields();
        String expResult = "Field";
        String result = instance.getDefaultNamePrefix();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTableDescription method, of class Fields.
     */
    public void testSetTableDescription() {
        System.out.println("setTableDescription");
        String tableDescription = "table1";
        Fields instance = new Fields();
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
        Fields instance = new Fields();
        String expResult = "Field1";
        String result = instance.generateNewName();
        assertEquals(expResult, result);
        instance.add(new Field(expResult));
        result = instance.generateNewName();
        assertEquals("Field2", result);
    }

    /**
     * Test of generateNewName method, of class Fields.
     */
    public void testGenerateNewName_String() {
        System.out.println("generateNewName");
        String aPrefix = "TF_";
        Fields instance = new Fields();
        String expResult = "TF_1";
        String result = instance.generateNewName(aPrefix);
        assertEquals(expResult, result);
        instance.add(new Field(expResult));
        result = instance.generateNewName(aPrefix);
        assertEquals("TF_2", result);
        instance.add(new Field(result));
        result = instance.generateNewName();
        assertEquals("Field1", result);
        instance.add(new Field(result));
        result = instance.generateNewName(aPrefix);
        assertEquals("TF_3", result);
        instance.add(new Field(result));
        result = instance.generateNewName();
        assertEquals("Field2", result);
    }

    /**
     * Test of isNameAlreadyPresent method, of class Fields.
     */
    public void testIsNameAlreadyPresent() {
        System.out.println("isNameAlreadyPresent");
        String aName = "sampleName";

        Field aField2Skip = null;
        Fields instance = new Fields();
        Field field = new Field(aName, "Примерное поле");
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
     * Test of add at index method, of class Fields.
     */
    public void testAdd_2args() {
        System.out.println("add at index");
        Fields instance = new Fields();
        String expResult = "Field1";
        String result = instance.generateNewName();
        assertEquals(expResult, result);
        instance.add(new Field(expResult));
        result = instance.generateNewName();
        assertEquals("Field2", result);
        instance.add(1, new Field(result));
        assertEquals(instance.find("Field2"), 1);
        assertEquals(instance.find("Field1"), 2);
    }
    /**
     * Test of createNewField method, of class Fields.
     */
    public void testCreateNewField_0args() {
        System.out.println("createNewField");
        Fields instance = new Fields();
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
        Fields instance = new Fields();
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
        Fields instance = new Fields();
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
        Fields instance = new Fields();
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
        Fields instance = new Fields();
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
        System.out.println("get");
        Fields instance = new Fields();
        Field field = instance.createNewField();
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
        System.out.println("get");
        Fields instance = new Fields();
        Field field = instance.createNewField("sample");
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
        copyImpl();
    }

    protected void copyImpl()
    {
        Fields instance = new Fields();
        Fields expResult = instance;

        Field f1 = new Field("field1");
        assertEquals(f1.getName(), "field1");
        Field f2 = new Field("field1", "fDesc1");
        assertEquals(f2.getName(), "field1");
        assertEquals(f2.getDescription(), "fDesc1");
        Field f3 = new Field("field1", "fDesc1", DataTypeInfo.DECIMAL);
        assertEquals(f3.getName(), "field1");
        assertEquals(f3.getDescription(), "fDesc1");
        assertEquals(f3.getTypeInfo(), DataTypeInfo.DECIMAL);
        Field f4 = new Field(f3);
        assertEquals(f4, f3);

        instance.add(f1);
        instance.add(f2);
        instance.add(f3);
        instance.add(f4);

        Fields result = instance.copy();
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
        copyImpl();
    }

    /**
     * Test of getColumnCount method, of class Fields.
     */
    public void testGetColumnCount() throws Exception {
        System.out.println("getColumnCount");
        Fields instance = new Fields();
        Field field = instance.createNewField("sample");
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);
        assertEquals(instance.get("sample"), field);
    }

    /**
     * Test of findColumn method, of class Fields.
     */
    public void testFindColumn() throws Exception {
        System.out.println("findColumn");
        Fields instance = new Fields();
        Field field = instance.createNewField("sample");
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.find("sample");
        assertEquals(1, result);
    }

    /**
     * Test of findColumn method with null argument.
     */
    public void testFindNullColumn() throws Exception {
        System.out.println("findNullColumn");
        Fields instance = new Fields();
        Field field = instance.createNewField("sample");
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.find(null);
        assertEquals(expResult, result);
    }
}
