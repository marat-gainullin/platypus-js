/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import java.sql.Types;
import java.util.Date;

import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;

/**
 * Converter has to convert some value to and from rowset's internal
 * representation.
 * 
 * @author mg
 */
public class Converter {

	public enum JSTYPE {
		UNSUPPORTED, NUMBER, STRING, DATE, BOOL
	}

	public static JSTYPE toJsType(DataTypeInfo aTypeInfo) {
		switch (aTypeInfo.getType()) {
		// Some strange types. No one knows how to work with them.
		case Types.JAVA_OBJECT:
		case Types.DATALINK:
		case Types.DISTINCT:
		case Types.NULL:
		case Types.ROWID:
		case Types.REF:
		case Types.SQLXML:
		case Types.ARRAY:
		case Types.STRUCT:
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
		case Types.BLOB:
		case Types.OTHER:
			return JSTYPE.UNSUPPORTED;
			// clobs
		case Types.CLOB:
		case Types.NCLOB:
			return JSTYPE.STRING;
			// numbers
		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.INTEGER:
		case Types.REAL:
		case Types.FLOAT:
		case Types.DOUBLE:
			return JSTYPE.NUMBER;
			// strings
		case Types.CHAR:
		case Types.NCHAR:
		case Types.VARCHAR:
		case Types.NVARCHAR:
		case Types.LONGVARCHAR:
		case Types.LONGNVARCHAR:
			return JSTYPE.STRING;
			// booleans
		case Types.BOOLEAN:
		case Types.BIT:
			return JSTYPE.BOOL;
			// dates, times
		case Types.DATE:
		case Types.TIMESTAMP:
		case Types.TIME:
			return JSTYPE.DATE;
		default:
			return JSTYPE.STRING;
		}
	}

	/**
	 * Converts some value to rowset internal compatible representation. For
	 * example, there are some types constructed above java.sql.Types.STRUCT
	 * type, according to some type name, specific for database. After
	 * converting, applications would be able to use converted value in their's
	 * specific tasks. On another hand, method may be used as converter from
	 * some value of wide range classes to value of predefined class. Example:
	 * The same number may be represented by object such range of classes:
	 * Float, Double, Short and others. After converting it would be object of
	 * BigDecimal class. So, we see two use cases of this method: (1) Converting
	 * from value retrived from database to internal application's
	 * representation. (2) Converting from value of wide range of classes to
	 * value of one(right) predefined class.
	 * 
	 * @return Converted value.
	 * @throws RowsetException
	 * @see DataTypeInfo
	 */
	public static Object convert2RowsetCompatible(Object aValue, DataTypeInfo aTypeInfo) throws RowsetException {
		if (aValue != null) {
			JSTYPE jsType = toJsType(aTypeInfo);
			if (jsType == JSTYPE.UNSUPPORTED) {
				return aValue;
			} else if (jsType == JSTYPE.BOOL) {
				if (aValue instanceof Number) {
					return ((Number) aValue).intValue() == 1;
				} else if (aValue instanceof String) {
					String s = (String) aValue;
					return !s.isEmpty();
				} else if (aValue instanceof Boolean) {
					return ((Boolean) aValue);
				} else if (aValue instanceof Date) {
					return !((Date) aValue).equals(new Date(0));
				}
			} else if (jsType == JSTYPE.NUMBER) {
				// target type - Double
				if (aValue instanceof Number) {
					return Double.valueOf(((Number) aValue).doubleValue());
				} else if (aValue instanceof String) {
					return Double.valueOf((String) aValue);
				} else if (aValue instanceof Boolean) {
					return Double.valueOf(((Boolean) aValue) ? 1 : 0);
				} else if (aValue instanceof Date) {
					return Double.valueOf(((Date) aValue).getTime());
				}
			} else if (jsType == JSTYPE.STRING) {
				if (aValue instanceof Number) {
					return ((Number) aValue).toString();
				} else if (aValue instanceof String) {
					return (String) aValue;
				} else if (aValue instanceof Boolean) {
					Boolean b = ((Boolean) aValue);
					return Boolean.TRUE.equals(b) ? b.toString() : "";
				} else if (aValue instanceof Date)
					return ((Date) aValue).toString();
				// else if (aValue instanceof CompactClob)
				// return ((CompactClob)aValue).getData()
			} else if (jsType == JSTYPE.DATE) {
				if (aValue instanceof Number) {
					return new Date(((Number) aValue).longValue());
				} else if (aValue instanceof String) {
					return new Date(Long.valueOf((String) aValue));
				} else if (aValue instanceof Boolean) {
					return new Date(Long.valueOf(((Boolean) aValue) ? 1 : 0));
				} else if (aValue instanceof Date) {
					return ((Date) aValue);
				} else {
					return aValue;
				}
			}
		}
		return aValue;
	}

}
