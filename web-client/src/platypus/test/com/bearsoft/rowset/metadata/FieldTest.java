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
public class FieldTest extends RowsetBaseTest{

    public FieldTest() {
    }

    public void testConstructors() {
        System.out.println("testConstructors");
        Field instance = new Field("field1");
        assertEquals(instance.getName(), "field1");
        instance = new Field("field1", "fDesc1");
        assertEquals(instance.getName(), "field1");
        assertEquals(instance.getDescription(), "fDesc1");
        instance = new Field("field1", "fDesc1", DataTypeInfo.DECIMAL);
        assertEquals(instance.getName(), "field1");
        assertEquals(instance.getDescription(), "fDesc1");
        assertEquals(instance.getTypeInfo(), DataTypeInfo.DECIMAL);
        Field instance1 = new Field(instance);
        assertEquals(instance, instance1);
    }

    /**
     * Test of isFk method, of class Field.
     */
    public void testIsFk() {
        System.out.println("isFk");
        Field instance = new Field();
        ForeignKeySpec fk = new ForeignKeySpec("testSchema", "testTable1", "t_ref", "fk_constraint_80s", ForeignKeySpec.ForeignKeyRule.SETDEFAULT, ForeignKeySpec.ForeignKeyRule.SETNULL, false, "testSchema", "testTable", "t_id", "constraint_80s");
        instance.setFk(fk);
        boolean expResult = true;
        boolean result = instance.isFk();
        assertEquals(expResult, result);
    }

    /**
     * Test of isPk method, of class Field.
     */
    public void testIsPk() {
        System.out.println("isPk");
        Field instance = new Field("t_id", "пк");
        instance.setPk(true);
        assertTrue(instance.isPk());
        assertEquals(instance.getName(), "t_id");
    }

    /**
     * Test of getFk method, of class Field.
     */
    public void testGetFk() {
        System.out.println("getFk");
        Field instance = new Field();
        ForeignKeySpec fk = new ForeignKeySpec("testSchema", "testTable1", "t_ref", "fk_constraint_80s", ForeignKeySpec.ForeignKeyRule.SETDEFAULT, ForeignKeySpec.ForeignKeyRule.SETNULL, false, "testSchema", "testTable", "t_id", "constraint_80s");
        instance.setFk(fk);
        boolean expResult = true;
        boolean result = instance.isFk();
        assertEquals(expResult, result);
        assertEquals(fk.copy(), instance.getFk());
    }

    /**
     * Test of isReadonly method, of class Field.
     */
    public void testProperties() {
        System.out.println("Properties");
    }

    /**
     * Test of equals method, of class Field.
     */
    public void testEquals() {
        System.out.println("equals");
        Field instance = new Field();
        ForeignKeySpec fk = new ForeignKeySpec("testSchema", "testTable1", "t_ref", "fk_constraint_80s", ForeignKeySpec.ForeignKeyRule.SETDEFAULT, ForeignKeySpec.ForeignKeyRule.SETNULL, false, "testSchema", "testTable", "t_id", "constraint_80s");
        instance.setFk(fk);
        boolean expResult = true;
        boolean result = instance.isFk();
        assertEquals(expResult, result);
        assertEquals(fk.copy(), instance.getFk());
        Field instance1 = instance.copy();
        assertTrue(instance.equals(instance1));
    }

    /**
     * Test of getName method, of class Field.
     */
    public void testGetName() {
        System.out.println("getName");
        Field instance = new Field("testName", "");
        String expResult = "testName";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class Field.
     */
    public void testSetName() {
        System.out.println("setName");
        String name = "testName";
        Field instance = new Field();
        instance.setName(name);
        String result = instance.getName();
        assertEquals(name, result);
    }

    /**
     * Test of getDescription method, of class Field.
     */
    public void testGetDescription() {
        System.out.println("getDescription");
        Field instance = new Field("testName", "testDesc");
        String expResult = "testDesc";
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDescription method, of class Field.
     */
    public void testSetDescription() {
        System.out.println("setDescription");
        String description = "testDescription";
        Field instance = new Field("testName", "");
        instance.setDescription(description);
        String result = instance.getDescription();
        String expResult = "testDescription";
        assertEquals(expResult, result);
    }

    /**
     * Test of getType method, of class Field.
     */
    public void testGetType() {
        System.out.println("getType");
        Field instance = new Field();
        instance.setTypeInfo(DataTypeInfo.BIGINT);
        DataTypeInfo expResult = DataTypeInfo.BIGINT;
        DataTypeInfo result = instance.getTypeInfo();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSchemaName method, of class Field.
     */
    public void testSetSchemaName() {
        System.out.println("setSchemaName");
        String schemaName = "testSchema";
        Field instance = new Field();
        instance.setSchemaName(schemaName);
        assertEquals(instance.getSchemaName(), "testSchema");
    }

    /**
     * Test of setTableName method, of class Field.
     */
    public void testSetTableName() {
        System.out.println("setTableName");
        String tableName = "testTable";
        Field instance = new Field();
        instance.setTableName(tableName);
        assertEquals(instance.getTableName(), "testTable");
    }

    /**
     * Test of setSize method, of class Field.
     */
    public void testSetSize() {
        System.out.println("setSize");
        int size = 10;
        Field instance = new Field();
        instance.setSize(size);
        assertEquals(10, instance.getSize());
    }

    /**
     * Test of setScale method, of class Field.
     */
    public void testSetScale() {
        System.out.println("setScale");
        int scale = 5;
        Field instance = new Field();
        instance.setScale(scale);
        assertEquals(5, instance.getScale());
    }

    /**
     * Test of setPrecision method, of class Field.
     */
    public void testSetPrecision() {
        System.out.println("setPrecision");
        int precision = 3;
        Field instance = new Field();
        instance.setPrecision(precision);
        assertEquals(3, instance.getPrecision());
    }

    /**
     * Test of setSigned method, of class Field.
     */
    public void testSetSigned() {
        System.out.println("setSigned");
        Field instance = new Field();
        instance.setSigned(false);
        assertEquals(false, instance.isSigned());
        instance.setSigned(true);
        assertEquals(true, instance.isSigned());
    }

    /**
     * Test of setNullable method, of class Field.
     */
    public void testSetNullable() {
        System.out.println("setNullable");
        Field instance = new Field();
        instance.setNullable(false);
        assertEquals(false, instance.isNullable());
        instance.setNullable(true);
        assertEquals(true, instance.isNullable());
    }

    /**
     * Test of getTypeName method, of class Field.
     */
    public void testGetTypeName() {
        System.out.println("getTypeName");
        Field instance = new Field();
        String expResult = "VARCHAR";
        instance.setTypeInfo(DataTypeInfo.VARCHAR);

        String result = instance.getTypeInfo().getTypeName();
        assertEquals(expResult, result);

        instance.getTypeInfo().setTypeName("VARCHAR23");
        assertEquals(instance.getTypeInfo().getTypeName(), "VARCHAR23");
        assertEquals(instance.getTypeInfo().getType(), java.sql.Types.VARCHAR);
    }

    /**
     * Test of setTypeName method, of class Field.
     */
    public void testSetTypeName() {
        System.out.println("setTypeName");
        Field instance = new Field();
        String expResult = "VARCHAR";
        instance.setTypeInfo(DataTypeInfo.VARCHAR);
        String result = instance.getTypeInfo().getTypeName();
        assertEquals(expResult, result);
        instance.getTypeInfo().setTypeName("VARCHAR23");
        assertEquals(instance.getTypeInfo().getTypeName(), "VARCHAR23");
        assertNotSame(instance.getTypeInfo(), DataTypeInfo.VARCHAR);
        assertEquals(instance.getTypeInfo().getType(), java.sql.Types.VARCHAR);
    }

    /**
     * Test of copy method, of class Field.
     */
    public void testCopy() {
        System.out.println("copy");
        Field instance = new Field();
        Field expResult = instance;
        instance.setName("sampleName");
        instance.setTableName("sampleTable");
        instance.setSchemaName("sampleSchema");
        instance.setSize(45);
        instance.setSigned(false);
        instance.setReadonly(true);
        instance.setScale(30);
        instance.setPrecision(5);
        instance.setPk(true);
        ForeignKeySpec fk = new ForeignKeySpec("sampleSchema", "sampleTable", "sampleField", "cName12345", ForeignKeySpec.ForeignKeyRule.SETDEFAULT, ForeignKeySpec.ForeignKeyRule.SETNULL, false, "testSchema", "testTable", "t_id", "constraint_80s");
        instance.setFk(fk);

        instance.setDescription("some field");
        instance.setNullable(false);

        instance.setTypeInfo(DataTypeInfo.DOUBLE);

        Field result = instance.copy();
        assertEquals(expResult, result);
    }
}
