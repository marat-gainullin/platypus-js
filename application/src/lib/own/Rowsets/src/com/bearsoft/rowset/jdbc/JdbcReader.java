/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.jdbc;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetConverter;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for jdbc result sets sources. Performs reading of a whole rowset and
 * particular subset of rows for the rowset. Reading utilizes converters to
 * produce application-specific data while reading.
 *
 * @see Converter
 * @author mg
 */
public class JdbcReader {

    protected static final String ROWSET_MISSING_EXCEPTION_MSG = "aResultSet argument must be non null";
    protected Converter converter;
    protected Fields expectedFields;

    /**
     * Default constructor. Uses RowsetConverter as a converter.
     *
     * @see RowsetConverter
     */
    public JdbcReader() {
        super();
        converter = new RowsetConverter();
    }

    /**
     * Constructs a reader for rowset, using specified converter.
     *
     * @param aConverter A converter to be used in the reading process.
     * @see Converter
     */
    public JdbcReader(Converter aConverter) {
        super();
        converter = aConverter;
    }

    /**
     * Constructs a reader for rowset, using specified converter.
     *
     * @param aConverter A converter to be used in the reading process.
     * @param aExpectedFields Fields expexted to be in read rowset
     * @see Converter
     */
    public JdbcReader(Converter aConverter, Fields aExpectedFields) {
        super();
        converter = aConverter;
        expectedFields = aExpectedFields;
    }

    /**
     * Returns the converter, used by this reader.
     *
     * @return Converter, used by this reader.
     */
    public Converter getConverter() {
        return converter;
    }

    /**
     * Sets the converter, used by this reader.
     *
     * @param aConverter Converter to use while reading.
     */
    public void setConverter(Converter aConverter) {
        converter = aConverter;
    }

    /**
     * Reads data from ResultSet object and creates new Rowset based on the
     * data. Warning! The rowset returned doesn't log it's changes.
     *
     * @param aPageSize Page size of reading process. May be less then zero to
     * indicate that whole data should be fetched.
     * @param aFlow
     * @param aResultSet
     * @return New Rowset object created.
     * @throws SQLException
     */
    public Rowset readRowset(ResultSet aResultSet, FlowProvider aFlow, int aPageSize) throws SQLException {
        try {
            if (aResultSet != null) {
                ResultSetMetaData lowLevelJdbcFields = aResultSet.getMetaData();
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
                Rowset rowset = new Rowset(expectedFields != null && !expectedFields.isEmpty() ? expectedFields : jdbcFields);
                List<Row> rows = readRows(rowset.getFields(), jdbcFields, aResultSet, aPageSize, aFlow, converter);
                rowset.setCurrent(rows);
                rowset.currentToOriginal();
                return rowset;
            } else {
                throw new SQLException(ROWSET_MISSING_EXCEPTION_MSG);
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
     * Reads all rows from result set, returning them as an ArrayList
     * collection.
     *
     * @param aExpectedFields
     * @param aJdbcFields Fields instance to be used as rowset's metadata.
     * @param aResultSet A result set to read from.
     * @param aPageSize Page size of reading process. May be less then zero to
     * indicate that whole data should be fetched.
     * @param aFlow
     * @param aConverter
     * @return List of rows hd been read.
     * @throws SQLException
     * @see List
     */
    protected static List<Row> readRows(Fields aExpectedFields, Fields aJdbcFields, ResultSet aResultSet, int aPageSize, FlowProvider aFlow, Converter aConverter) throws SQLException {
        try {
            if (aResultSet != null) {
                List<Row> rows = new ArrayList<>();
                while ((aPageSize <= 0 || rows.size() < aPageSize) && aResultSet.next()) {
                    Row row = readRow(aExpectedFields, aJdbcFields, aResultSet, aFlow, aConverter);
                    rows.add(row);
                }
                return rows;
            } else {
                throw new SQLException(ROWSET_MISSING_EXCEPTION_MSG);
            }
        } catch (SQLException | RowsetException ex) {
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
     * @param aFlow
     * @param aConverter
     * @return The row had been read.
     * @throws SQLException
     * @throws InvalidColIndexException
     * @throws RowsetException
     */
    protected static Row readRow(Fields aExpectedFields, Fields aJdbcFields, ResultSet aResultSet, FlowProvider aFlow, Converter aConverter) throws SQLException, InvalidColIndexException, RowsetException {
        if (aResultSet != null) {
            assert aExpectedFields != null;
            Row row = new Row(aFlow.getEntityId(), aExpectedFields);
            for (int i = 1; i <= aJdbcFields.getFieldsCount(); i++) {
                Field jdbcField = aJdbcFields.get(i);
                Object appObject;
                if (aConverter != null) {
                    appObject = aConverter.getFromJdbcAndConvert2RowsetCompatible(aResultSet, i, jdbcField.getTypeInfo());
                } else {
                    appObject = aResultSet.getObject(i);
                }
                if (aExpectedFields.find(jdbcField.getName()) == 0) {
                    throw new InvalidColIndexException(String.format("Field with name \"%s\" not found in output fields.", jdbcField.getName()));
                }
                row.setColumnObject(aExpectedFields.find(jdbcField.getName()), appObject);
            }
            return row;
        }
        return null;
    }
}
