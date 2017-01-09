/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import java.sql.SQLException;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author mg
 */
public class FieldsTest {

    public FieldsTest() {
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

    /**
     * Test of isEmpty method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        Fields instance = new Fields();
        boolean expResult = true;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultNamePrefix method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testGetDefaultNamePrefix() {
        System.out.println("getDefaultNamePrefix");
        Fields instance = new Fields();
        String expResult = "Field";
        String result = instance.getDefaultNamePrefix();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTableDescription method, of class FieldsMetaDataImpl.
     */
    @Test
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
     * Test of generateNewName method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testGenerateNewName_0args() {
        System.out.println("generateNewName");
        Fields instance = new Fields();
        String expResult = "Field";
        String result = instance.generateNewName();
        assertEquals(expResult, result);
    }

    /**
     * Test of isNameAlreadyPresent method, of class FieldsMetaDataImpl.
     */
    @Test
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
     * Test of createNewField method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testCreateNewField_0args() {
        System.out.println("createNewField");
        Fields instance = new Fields();
        Field unexpResult = null;
        Field result = new Field(instance.generateNewName());
        assertNotSame(unexpResult, result);
    }

    /**
     * Test of createNewField method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testCreateNewField_String() {
        System.out.println("createNewField");
        String newName = "sampleName";
        Fields fields = new Fields();
        Field newField = new Field(fields.generateNewName(newName));
        fields.add(newField);
        assertNotSame(null, newField);
        assertEquals(newName, newField.getName());
        Field newField1 = new Field(fields.generateNewName(newName));
        fields.add(newField1);
        assertNotSame(null, newField1);
        assertEquals(newName + "1", newField1.getName());
    }

    /**
     * Test of getFieldsCount method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testGetFieldsCount() {
        System.out.println("getFieldsCount");
        Fields instance = new Fields();
        Field field = new Field(instance.generateNewName());
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);
    }

    /**
     * Test of clear method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        Fields instance = new Fields();
        Field field = new Field(instance.generateNewName());
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
     * Test of remove method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        Fields instance = new Fields();
        Field field = new Field(instance.generateNewName());
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
     * Test of indexOf method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testIndexOf() {
        System.out.println("indexOf");
        Fields instance = new Fields();
        Field field = new Field(instance.generateNewName());
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);

        expResult = 1;
        result = instance.find(field.getName());
        assertEquals(expResult, result);
    }

    /**
     * Test of get method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testGet_int() {
        System.out.println("get");
        Fields instance = new Fields();
        Field field = new Field(instance.generateNewName());
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);
        assertEquals(instance.get(1), field);
    }

    /**
     * Test of get method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testGet_String() {
        System.out.println("get");
        Fields instance = new Fields();
        Field field = new Field(instance.generateNewName("sample"));
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);
        assertEquals(instance.get("sample"), field);
    }

    /**
     * Test of clone method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testClone() {
        System.out.println("clone");
        Fields instance = new Fields();
        Fields expResult = instance;
        Fields result = instance.copy();
        assertEquals(expResult.getFieldsCount(), result.getFieldsCount());
        for (int i = 0; i < expResult.getFieldsCount(); i++) {
            assertEquals(expResult.get(i + 1), result.get(i + 1));
        }
    }

    /**
     * Test of copy method, of class FieldsMetaDataImpl.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        Fields instance = new Fields();
        Fields expResult = instance;
        Fields result = instance.copy();
        assertEquals(expResult.getFieldsCount(), result.getFieldsCount());
        for (int i = 0; i < expResult.getFieldsCount(); i++) {
            assertEquals(expResult.get(i + 1), result.get(i + 1));
        }
    }

    /**
     * Test of getColumnCount method, of class FieldsMetaDataImpl.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testGetColumnCount() throws SQLException {
        System.out.println("getColumnCount");
        Fields instance = new Fields();
        Field field = new Field(instance.generateNewName("sample"));
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.getFieldsCount();
        assertEquals(1, result);
        assertEquals(instance.get("sample"), field);
    }

    /**
     * Test of findColumn method, of class FieldsMetaDataImpl.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testFindColumn() throws SQLException {
        System.out.println("findColumn");
        Fields instance = new Fields();
        Field field = new Field(instance.generateNewName("sample"));
        int expResult = 0;
        int result = instance.getFieldsCount();
        assertEquals(expResult, result);
        instance.add(field);
        result = instance.find("sample");
        assertEquals(1, result);
    }

}
