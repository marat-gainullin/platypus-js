/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.metadata;

import com.bearsoft.rowset.utils.RowsetUtils;
import java.sql.Types;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author mg
 */
public class DataTypeInfoTest {

    @Test
    public void compareTest()
    {
        System.out.println("compareTest");
        DataTypeInfo typeInfo1 = new DataTypeInfo(Types.ARRAY, RowsetUtils.getTypeName(Types.ARRAY), "");
        DataTypeInfo typeInfo2 = new DataTypeInfo(Types.ARRAY, RowsetUtils.getTypeName(Types.ARRAY), java.sql.Array.class.getName());
        assertFalse(typeInfo1.equals(typeInfo2));

        typeInfo1.setJavaClassName(java.sql.Array.class.getName());
        assertEquals(typeInfo1, typeInfo2);
    }

    @Test
    public void copyTest()
    {
        System.out.println("copyTest");
        DataTypeInfo typeInfo1 = new DataTypeInfo(Types.ARRAY, RowsetUtils.getTypeName(Types.ARRAY), java.sql.Array.class.getName());
        DataTypeInfo typeInfo2 = typeInfo1.copy();

        assertEquals(typeInfo1.getSqlType(), Types.ARRAY);
        assertEquals(typeInfo1.getSqlTypeName(), RowsetUtils.getTypeName(Types.ARRAY));
        assertEquals(typeInfo1.getJavaClassName(), java.sql.Array.class.getName());

        assertEquals(typeInfo2.getSqlType(), Types.ARRAY);
        assertEquals(typeInfo2.getSqlTypeName(), RowsetUtils.getTypeName(Types.ARRAY));
        assertEquals(typeInfo2.getJavaClassName(), java.sql.Array.class.getName());
    }

    @Test
    public void nullTypeNameTest()
    {
        System.out.println("nullTypeNameTest");
        DataTypeInfo typeInfo1 = new DataTypeInfo(Types.ARRAY, null, java.sql.Array.class.getName());
        assertEquals(typeInfo1.getSqlTypeName(), RowsetUtils.getTypeName(Types.ARRAY));
        DataTypeInfo typeInfo2 = new DataTypeInfo(Types.VARCHAR, null, String.class.getName());
        assertEquals(typeInfo2.getSqlTypeName(), RowsetUtils.getTypeName(Types.VARCHAR));
        typeInfo2.setSqlTypeName("Some string type");
        assertEquals(typeInfo2.getSqlTypeName(), "Some string type");
        typeInfo2.setSqlType(Types.NVARCHAR);
        assertEquals(typeInfo2.getSqlTypeName(), RowsetUtils.getTypeName(Types.NVARCHAR));
    }
}
