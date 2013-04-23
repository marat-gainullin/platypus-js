/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.metadata;

import java.sql.Types;

import com.bearsoft.rowset.RowsetBaseTest;
import com.bearsoft.rowset.utils.RowsetUtils;

/**
 * 
 * @author mg
 */
public class DataTypeInfoTest extends RowsetBaseTest {

	public DataTypeInfoTest() {
	}

	public void testCompareTest() {
		System.out.println("compareTest");
		DataTypeInfo typeInfo1 = new DataTypeInfo(Types.ARRAY, RowsetUtils.getTypeName(Types.ARRAY));
		DataTypeInfo typeInfo2 = new DataTypeInfo(Types.ARRAY, RowsetUtils.getTypeName(Types.SQLXML));
		assertFalse(typeInfo1.equals(typeInfo2));

		typeInfo2.setTypeName(RowsetUtils.getTypeName(Types.ARRAY));
		assertEquals(typeInfo1, typeInfo2);
	}

	public void testCopyTest() {
		System.out.println("copyTest");
		DataTypeInfo typeInfo1 = new DataTypeInfo(Types.ARRAY, RowsetUtils.getTypeName(Types.ARRAY));
		DataTypeInfo typeInfo2 = typeInfo1.copy();

		assertEquals(typeInfo1.getType(), Types.ARRAY);
		assertEquals(typeInfo1.getTypeName(), RowsetUtils.getTypeName(Types.ARRAY));

		assertEquals(typeInfo2.getType(), Types.ARRAY);
		assertEquals(typeInfo2.getTypeName(), RowsetUtils.getTypeName(Types.ARRAY));
	}

	public void testNullTypeNameTest() {
		System.out.println("nullTypeNameTest");
		DataTypeInfo typeInfo1 = new DataTypeInfo(Types.ARRAY, null);
		assertEquals(typeInfo1.getTypeName(), RowsetUtils.getTypeName(Types.ARRAY));
		DataTypeInfo typeInfo2 = new DataTypeInfo(Types.VARCHAR, null);
		assertEquals(typeInfo2.getTypeName(), RowsetUtils.getTypeName(Types.VARCHAR));
		typeInfo2.setTypeName("Some string type");
		assertEquals(typeInfo2.getTypeName(), "Some string type");
		typeInfo2.setType(Types.NVARCHAR);
		assertEquals(typeInfo2.getTypeName(), RowsetUtils.getTypeName(Types.NVARCHAR));
	}
}
