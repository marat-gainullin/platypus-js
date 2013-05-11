/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.metadata;

import com.bearsoft.rowset.serial.BinaryRowsetReader;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.serial.BinaryRowsetWriter;
import java.sql.SQLException;
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
     * Test of isEmpty method, of class Fields.
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
     * Test of getDefaultNamePrefix method, of class Fields.
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
     * Test of setTableDescription method, of class Fields.
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
     * Test of generateNewName method, of class Fields.
     */
    @Test
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
    @Test
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
     * Test of add at index method, of class Fields.
     */
    @Test
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
     * Test of reorder fields, of class Fields.
     */
    @Test
    public void testReorderFields() {
        System.out.println("reorderFields");
        Fields instance = new Fields();
        Field field1 = new Field("field1");
        instance.add(field1);
        Field field2 = new Field("field2");
        instance.add(field2);
        Field field3 = new Field("field3");
        instance.add(field3);
        assertEquals(instance.get(1), field1);
        assertEquals(instance.get(2), field2);
        assertEquals(instance.get(3), field3);
        //reorder
        int[] order1 = {2, 1, 3};
        instance.reorder(order1);
        assertEquals(instance.get(2), field1);
        assertEquals(instance.get(1), field2);
        assertEquals(instance.get(3), field3);
        //reorder back
        instance.reorder(order1);
        assertEquals(instance.get(1), field1);
        assertEquals(instance.get(2), field2);
        assertEquals(instance.get(3), field3);
        //wrong arguments
        int[] order2 = {1, 2, 3, 4};
        try {
            instance.reorder(order2);
            fail("Error: reorder with wrong arguments.");
        } catch (IllegalArgumentException ex) {
        }
        int[] order3 = {1, 2, 2};
        try {
            instance.reorder(order3);
            fail("Error: reorder with wrong arguments.");
        } catch (IllegalArgumentException ex) {
        }
        int[] order4 = {1, 2, 4};
        try {
            instance.reorder(order4);
            fail("Error: reorder with wrong arguments.");
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test of createNewField method, of class Fields.
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testClone() {
        System.out.println("clone");
        testCopyImpl();
    }

    protected void testCopyImpl() {
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

        Fields result = instance.clone();
        assertEquals(expResult.getFieldsCount(), result.getFieldsCount());
        for (int i = 0; i < expResult.getFieldsCount(); i++) {
            assertEquals(expResult.get(i + 1), result.get(i + 1));
        }
    }

    @Test
    public void fkSerializationTest() throws Exception {
        Fields fields = new Fields();

        ForeignKeySpec fk = new ForeignKeySpec("sampleSchema", "sampleTable", "sampleField", "cName12345", ForeignKeySpec.ForeignKeyRule.SETDEFAULT, ForeignKeySpec.ForeignKeyRule.SETNULL, false, "testSchema", "testTable", "t_id", "constraint_80s");
        Field f1 = new Field("field1");
        assertEquals(f1.getName(), "field1");
        Field f2 = new Field("field1", "fDesc1");
        f2.setFk(fk);
        assertTrue(f2.isFk());
        assertEquals(f2.getName(), "field1");
        assertEquals(f2.getDescription(), "fDesc1");
        Field f3 = new Field("sampleField", "fDesc1", DataTypeInfo.DECIMAL);
        assertEquals(f3.getName(), "sampleField");
        assertEquals(f3.getDescription(), "fDesc1");
        assertEquals(f3.getTypeInfo(), DataTypeInfo.DECIMAL);
        Field f4 = new Field(f3);
        assertEquals(f4, f3);

        fields.add(f1);
        fields.add(f2);
        fields.add(f3);
        fields.add(f4);

        Rowset rowset = new Rowset(fields);

        BinaryRowsetWriter writer = new BinaryRowsetWriter();
        byte[] serialized = writer.write(rowset);
        BinaryRowsetReader reader = new BinaryRowsetReader();
        Rowset read = reader.read(serialized);
        assertEquals(1, read.getFields().getForeinKeys().size());
        Field fkField = read.getFields().getForeinKeys().get(0);
        assertEquals(fkField, f2);
        fkField.getFk().setCName("new fk constraint name");
        assertFalse(fkField.isEqual(f2));
    }

    /**
     * Test of copy method, of class Fields.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        testCopyImpl();
    }

    /**
     * Test of getColumnCount method, of class Fields.
     */
    @Test
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
    @Test
    public void testFindColumn() throws SQLException {
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
    @Test
    public void testFindNullColumn() throws SQLException {
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
