/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.bearsoft.rowset.utils;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.Utils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

/**
 * 
 * @author mg
 */
public class RowsetUtils {

	public static Object generatePkValueByType(int colType) {
		switch (colType) {
		case java.sql.Types.TINYINT:
		case java.sql.Types.SMALLINT:
		case java.sql.Types.INTEGER:
		case java.sql.Types.BIGINT:
		case java.sql.Types.FLOAT:
		case java.sql.Types.REAL:
		case java.sql.Types.DOUBLE:
		case java.sql.Types.NUMERIC:
		case java.sql.Types.DECIMAL:
			return IDGenerator.genId();
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGVARCHAR:
		case java.sql.Types.NCHAR:
		case java.sql.Types.NVARCHAR:
		case java.sql.Types.LONGNVARCHAR:
			return String.valueOf(IDGenerator.genId());
		case java.sql.Types.DATE:
		case java.sql.Types.TIME:
		case java.sql.Types.TIMESTAMP:
			return new Date((long) IDGenerator.genId());
		case java.sql.Types.BIT:
		case java.sql.Types.BINARY:
		case java.sql.Types.VARBINARY:
		case java.sql.Types.LONGVARBINARY:
		case java.sql.Types.NULL:
		case java.sql.Types.OTHER:
		case java.sql.Types.JAVA_OBJECT:
		case java.sql.Types.DISTINCT:
		case java.sql.Types.STRUCT:
		case java.sql.Types.ARRAY:
		case java.sql.Types.BLOB:
		case java.sql.Types.CLOB:
		case java.sql.Types.REF:
		case java.sql.Types.DATALINK:
		case java.sql.Types.ROWID:
		case java.sql.Types.SQLXML:
		case java.sql.Types.BOOLEAN:
		case java.sql.Types.NCLOB:
			break;
		}
		assert false;
		return null;
	}

	public static final int INOPERABLE_TYPE_MARKER = 0;
	public static final Object UNDEFINED_SQL_VALUE = new Object();
	public static final Map<Integer, String> typesNames = new HashMap();

	static {
		typesNames.put(java.sql.Types.ARRAY, "ARRAY");
		typesNames.put(java.sql.Types.BIGINT, "BIGINT");
		typesNames.put(java.sql.Types.BINARY, "BINARY");
		typesNames.put(java.sql.Types.BIT, "BIT");
		typesNames.put(java.sql.Types.BLOB, "BLOB");
		typesNames.put(java.sql.Types.BOOLEAN, "BOOLEAN");
		typesNames.put(java.sql.Types.CHAR, "CHAR");
		typesNames.put(java.sql.Types.CLOB, "CLOB");
		typesNames.put(java.sql.Types.DATALINK, "DATALINK");
		typesNames.put(java.sql.Types.DATE, "DATE");
		typesNames.put(java.sql.Types.DECIMAL, "DECIMAL");
		typesNames.put(java.sql.Types.DISTINCT, "DISTINCT");
		typesNames.put(java.sql.Types.DOUBLE, "DOUBLE");
		typesNames.put(java.sql.Types.FLOAT, "FLOAT");
		typesNames.put(java.sql.Types.INTEGER, "INTEGER");
		typesNames.put(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT");
		typesNames.put(java.sql.Types.LONGNVARCHAR, "LONGNVARCHAR");
		typesNames.put(java.sql.Types.LONGVARBINARY, "LONGVARBINARY");
		typesNames.put(java.sql.Types.LONGVARCHAR, "LONGVARCHAR");
		typesNames.put(java.sql.Types.NCHAR, "NCHAR");
		typesNames.put(java.sql.Types.NCLOB, "NCLOB");
		typesNames.put(java.sql.Types.NULL, "NULL");
		typesNames.put(java.sql.Types.NUMERIC, "NUMERIC");
		typesNames.put(java.sql.Types.NVARCHAR, "NVARCHAR");
		typesNames.put(java.sql.Types.OTHER, "OTHER");
		typesNames.put(java.sql.Types.REAL, "REAL");
		typesNames.put(java.sql.Types.REF, "REF");
		typesNames.put(java.sql.Types.ROWID, "ROWID");
		typesNames.put(java.sql.Types.SMALLINT, "SMALLINT");
		typesNames.put(java.sql.Types.SQLXML, "SQLXML");
		typesNames.put(java.sql.Types.STRUCT, "STRUCT");
		typesNames.put(java.sql.Types.TIME, "TIME");
		typesNames.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
		typesNames.put(java.sql.Types.TINYINT, "TINYINT");
		typesNames.put(java.sql.Types.VARBINARY, "VARBINARY");
		typesNames.put(java.sql.Types.VARCHAR, "VARCHAR");
	}

	public static boolean isTypeCompatible2JavaClass(int jdbcType,
			Class<?> aClass) {
		return true;
	}

	public static String getTypeName(int type) {
		return typesNames.get(type);
	}

	public static Locator generatePkLocator(Rowset aRowset) {
		List<Field> pks = aRowset.getFields().getPrimaryKeys();
		Locator res = aRowset.createLocator();
		res.beginConstrainting();
		try {
			for (Field pk : pks) {
				res.addConstraint(aRowset.getFields().find(pk.getName()));
			}
		} finally {
			res.endConstrainting();
		}
		return res;
	}

	public static native Field unwrapField(JavaScriptObject aValue)
			throws Exception/*-{
		return aValue.unwrap();
	}-*/;

	protected static native boolean isNullInJsArray(
			JavaScriptObject fieldsValues, int aIndex) throws Exception/*-{
		return (fieldsValues[aIndex] == null);
	}-*/;

	public static Object extractValueFromJsArray(JsArrayMixed fieldsValues,
			int aIndex) throws Exception {
		Object v = null;
		if (!isNullInJsArray(fieldsValues, aIndex)) {
			v = Utils.unwrap(fieldsValues.getObject(aIndex));
		}
		return v;
	}
}
