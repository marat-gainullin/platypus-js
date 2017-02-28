/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.JdbcField;
import com.eas.script.Scripts;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Reader for jdbc result sets sources. Performs reading of a whole rowset and
 * particular subset of rows for the rowset. Reading utilizes converters to
 * produce application-specific data while reading.
 *
 * @author mg
 */
public class JdbcReader {

    public interface GeometryReader {

        public String readGeometry(Wrapper aRs, int aColumnIndex, Connection aConnection) throws SQLException;
    }

    public interface TypesResolver {

        public String toApplicationType(int aJdbcType, String aRDBMSType);
    }

    protected static final String RESULTSET_MISSING_EXCEPTION_MSG = "aResultSet argument must be non null";
    protected Fields expectedFields;
    protected GeometryReader gReader;
    protected TypesResolver resolver;

    /**
     *
     * @param aReader
     * @param aResolver
     */
    protected JdbcReader(GeometryReader aReader, TypesResolver aResolver) {
        super();
        gReader = aReader;
        resolver = aResolver;
    }

    /**
     *
     * @param aExpectedFields Fields expected to be in read rowset
     * @param aReader
     * @param aResolver
     */
    public JdbcReader(Fields aExpectedFields, GeometryReader aReader, TypesResolver aResolver) {
        this(aReader, aResolver);
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
                Fields readFields = readFields(lowLevelJdbcFields);
                return readRows(expectedFields != null && !expectedFields.isEmpty() ? expectedFields : readFields, readFields, aResultSet, aPageSize, aResultSet.getStatement().getConnection());
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

    public Fields readFields(ResultSetMetaData jdbcFields) throws SQLException {
        Fields appFields = new Fields();
        for (int i = 1; i <= jdbcFields.getColumnCount(); i++) {
            Field appField = new JdbcField();
            String columnLabel = jdbcFields.getColumnLabel(i);// Column label in jdbc is the name of platypus property
            String columnName = jdbcFields.getColumnName(i);
            appField.setName(columnLabel != null && !columnLabel.isEmpty() ? columnLabel : columnName);
            appField.setOriginalName(columnName);

            appField.setNullable(jdbcFields.isNullable(i) == ResultSetMetaData.columnNullable);
            appField.setType(resolver.toApplicationType(jdbcFields.getColumnType(i), jdbcFields.getColumnTypeName(i)));

            String schemaName = jdbcFields.getSchemaName(i);
            String tableName = jdbcFields.getTableName(i);
            if (schemaName != null && !schemaName.isEmpty()) {
                tableName = schemaName + "." + tableName;
            }
            appField.setTableName(tableName);
            appFields.add(appField);
        }
        return appFields;
    }

    /**
     * Reads all rows from result set, returning them as an ArrayList
     * collection.
     *
     * @param aExpectedFields
     * @param aReadFields Fields instance to be used as rowset's metadata.
     * @param aResultSet A result set to read from.
     * @param aPageSize Page size of reading process. May be less then zero to
     * indicate that whole data should be fetched.
     * @return Array of rows had been read.
     * @throws SQLException
     */
    protected Collection<Map<String, Object>> readRows(Fields aExpectedFields, Fields aReadFields, ResultSet aResultSet, int aPageSize, Connection aConnection) throws SQLException {
        try {
            if (aResultSet != null) {
                Collection<Map<String, Object>> oRows = new ArrayList<>();
                while ((aPageSize <= 0 || oRows.size() < aPageSize) && aResultSet.next()) {
                    Map<String, Object> jsRow = readRow(aExpectedFields, aReadFields, aResultSet, aConnection);
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
     * @param aReadFields
     * @param aResultSet Result set to read from.
     * @return The row had been read.
     * @throws SQLException
     */
    protected Map<String, Object> readRow(Fields aExpectedFields, Fields aReadFields, ResultSet aResultSet, Connection aConnection) throws SQLException {
        if (aResultSet != null) {
            assert aExpectedFields != null;
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= aReadFields.getFieldsCount(); i++) {
                Field readField = aReadFields.get(i);
                Field expectedField = aExpectedFields.get(readField.getName());
                Field field = expectedField != null ? expectedField : readField;
                Object appObject;
                if (Scripts.GEOMETRY_TYPE_NAME.equals(field.getType())) {
                    appObject = gReader.readGeometry(aResultSet, i, aConnection);
                } else {
                    appObject = JdbcFlowProvider.get(aResultSet, i);
                }
                row.put(field.getName(), appObject);
            }
            return row;
        }
        return null;
    }
}
