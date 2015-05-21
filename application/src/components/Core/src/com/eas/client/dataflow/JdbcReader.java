/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Reader for jdbc result sets sources. Performs reading of a whole rowset and
 * particular subset of rows for the rowset. Reading utilizes converters to
 * produce application-specific data while reading.
 *
 * @see Converter
 * @author mg
 */
public class JdbcReader {

    protected static final String RESULTSET_MISSING_EXCEPTION_MSG = "aResultSet argument must be non null";
    protected Fields expectedFields;

    /**
     * Default constructor. Uses RowsetConverter as a converter.
     *
     * @see RowsetConverter
     */
    public JdbcReader() {
        super();
    }

    /**
     * Constructs a reader for rowset, using specified converter.
     *
     * @param aExpectedFields Fields expexted to be in read rowset
     * @see Converter
     */
    public JdbcReader(Fields aExpectedFields) {
        super();
        expectedFields = aExpectedFields;
    }

    /**
     * Reads data from ResultSet object and creates new Rowset based on the
     * data. Warning! The rowset returned doesn't log it's changes.
     *
     * @param aPageSize Page size of reading process. May be less then zero to
     * indicate that whole data should be fetched.
     * @param aResultSet
     * @return New Rowset object created.
     * @throws SQLException
     */
    public Collection<Map<String, Object>> readRowset(ResultSet aResultSet, int aPageSize) throws SQLException {
        try {
            if (aResultSet != null) {
                ResultSetMetaData lowLevelJdbcFields = aResultSet.getMetaData();
                Fields jdbcFields = readFields(lowLevelJdbcFields);
                return readRows(expectedFields != null && !expectedFields.isEmpty() ? expectedFields : jdbcFields, jdbcFields, aResultSet, aPageSize);
            } else {
                throw new SQLException(RESULTSET_MISSING_EXCEPTION_MSG);
            }
        } catch (Exception ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new SQLException(ex);
            }
        }
    }

    public static Fields readFields(ResultSetMetaData lowLevelJdbcFields) throws SQLException {
        Fields jdbcFields = new Fields();
        for (int i = 1; i <= lowLevelJdbcFields.getColumnCount(); i++) {
            Field field = new Field();
            String columnLabel = lowLevelJdbcFields.getColumnLabel(i);// Column label in jdbc is the name of platypus property
            String columnName = lowLevelJdbcFields.getColumnName(i);
            field.setName(columnLabel != null && !columnLabel.isEmpty() ? columnLabel : columnName);
            field.setOriginalName(columnName);

            field.setNullable(lowLevelJdbcFields.isNullable(i) == ResultSetMetaData.columnNullable);

            DataTypeInfo typeInfo = new DataTypeInfo();
            typeInfo.setSqlType(lowLevelJdbcFields.getColumnType(i));
            typeInfo.setSqlTypeName(lowLevelJdbcFields.getColumnTypeName(i));
            typeInfo.setJavaClassName(lowLevelJdbcFields.getColumnClassName(i));
            field.setTypeInfo(typeInfo);

            field.setSize(lowLevelJdbcFields.getColumnDisplaySize(i));
            field.setPrecision(lowLevelJdbcFields.getPrecision(i));
            field.setScale(lowLevelJdbcFields.getScale(i));
            field.setSigned(lowLevelJdbcFields.isSigned(i));

            field.setTableName(lowLevelJdbcFields.getTableName(i));
            field.setSchemaName(lowLevelJdbcFields.getSchemaName(i));
            jdbcFields.add(field);
        }
        return jdbcFields;
    }

    /**
     * Reads all rows from result set, returning them as an ArrayList
     * collection.
     *
     * @param aExpectedFields
     * @param aJdbcFields Fields instance to be used as rowset's metadata.
     * @param aResultSet A result set to read from.
     * @param aPageSize Page size of reading process. May be less then zero to
     * indicate that whole data should be fetched.
     * @return Array of rows had been read.
     * @throws SQLException
     */
    protected static Collection<Map<String, Object>> readRows(Fields aExpectedFields, Fields aJdbcFields, ResultSet aResultSet, int aPageSize) throws SQLException {
        try {
            if (aResultSet != null) {
                Collection<Map<String, Object>> oRows = new ArrayList<>();
                while ((aPageSize <= 0 || oRows.size() < aPageSize) && aResultSet.next()) {
                    Map<String, Object> jsRow = readRow(aExpectedFields, aJdbcFields, aResultSet);
                    oRows.add(jsRow);
                }
                return oRows;
            } else {
                throw new SQLException(RESULTSET_MISSING_EXCEPTION_MSG);
            }
        } catch (Exception ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new SQLException(ex);
            }
        }
    }

    /**
     * Reads single row from result set, returning it as a result.
     *
     * @param aExpectedFields
     * @param aJdbcFields
     * @param aResultSet Result set to read from.
     * @return The row had been read.
     * @throws SQLException
     */
    protected static Map<String, Object> readRow(Fields aExpectedFields, Fields aJdbcFields, ResultSet aResultSet) throws SQLException {
        if (aResultSet != null) {
            assert aExpectedFields != null;
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= aJdbcFields.getFieldsCount(); i++) {
                Field jdbcField = aJdbcFields.get(i);
                Field expectedField = aExpectedFields.get(jdbcField.getName());
                if (expectedField != null) {
                    Object appObject = Converter.get(aResultSet, i, expectedField.getTypeInfo());
                    //appObject = Scripts.toJs(appObject);
                    row.put(expectedField.getName(), appObject);
                } else {
                    Object appObject = Converter.get(aResultSet, i, jdbcField.getTypeInfo());
                    //appObject = Scripts.toJs(appObject);
                    row.put(jdbcField.getName(), appObject);
                }
            }
            return row;
        }
        return null;
    }
}
