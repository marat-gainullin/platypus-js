/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.jdbc;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetConverter;
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
 * Reader for jdbc result sets sources.
 * Performs reading of a whole rowset and particular subset of rows for the rowset.
 * Reading utilizes converters to produce application-specific data while reading.
 * @see Converter
 * @author mg
 */
public class JdbcReader {

    protected static final String ROWSET_MISSING_EXCEPTION_MSG = "aResultSet argument must be non null";
    protected Converter converter = null;

    /**
     * Default constructor. Uses RowsetConverter as a converter.
     * @see RowsetConverter
     */
    public JdbcReader() {
        super();
        converter = new RowsetConverter();
    }

    /**
     * Constructs a reader for rowset, using specified converter.
     * @param aConverter A converter to be used in the reading process.
     * @see Converter
     */
    public JdbcReader(Converter aConverter) {
        super();
        converter = aConverter;
    }

    /**
     * Returns the converter, used by this reader.
     * @return Converter, used by this reader.
     */
    public Converter getConverter() {
        return converter;
    }

    /**
     * Sets the converter, used by this reader.
     * @param aConverter Converter to use while reading.
     */
    public void setConverter(Converter aConverter) {
        converter = aConverter;
    }

    /**
     * Reads data from ResultSet object and creates new Rowset based on the data.
     * Warning! The rowset returned doesn't log it's changes.
     * @param aPageSize Page size of reading process. May be less then zero to indicate that whole data should be fetched.
     * @param aResultSet
     * @return New Rowset object created.
     * @throws SQLException
     * @throws InvalidColIndexException
     * @throws RowsetException
     */
    public Rowset readRowset(ResultSet aResultSet, int aPageSize) throws SQLException {
        try {
            if (aResultSet != null) {
                ResultSetMetaData jdbcFields = aResultSet.getMetaData();
                Fields fields = new Fields();
                for (int i = 1; i <= jdbcFields.getColumnCount(); i++) {
                    Field field = new Field();
                    field.setName(jdbcFields.getColumnName(i));
                    field.setDescription(jdbcFields.getColumnLabel(i));

                    field.setNullable(jdbcFields.isNullable(i) == ResultSetMetaData.columnNullable);

                    DataTypeInfo typeInfo = new DataTypeInfo();
                    typeInfo.setSqlType(jdbcFields.getColumnType(i));
                    typeInfo.setSqlTypeName(jdbcFields.getColumnTypeName(i));
                    typeInfo.setJavaClassName(jdbcFields.getColumnClassName(i));
                    field.setTypeInfo(typeInfo);

                    field.setSize(jdbcFields.getColumnDisplaySize(i));
                    field.setPrecision(jdbcFields.getPrecision(i));
                    field.setScale(jdbcFields.getScale(i));
                    field.setSigned(jdbcFields.isSigned(i));

                    field.setTableName(jdbcFields.getTableName(i));
                    field.setSchemaName(jdbcFields.getSchemaName(i));
                    fields.add(field);
                }
                Rowset rowset = new Rowset(fields);
                List<Row> rows = readRows(fields, aResultSet, aPageSize);
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
     * Reads all rows from result set, returning them as an ArrayList collection.
     * @param aFields Fields instance to be used as rowset's metadata.
     * @param aResultSet A result set to read from.
     * @param aPageSize Page size of reading process. May be less then zero to indicate that whole data should be fetched.
     * @return List of rows hd been read.
     * @throws SQLException
     * @throws InvalidColIndexException
     * @throws RowsetException
     * @see List
     */
    public List<Row> readRows(Fields aFields, ResultSet aResultSet, int aPageSize) throws SQLException {
        try {
            if (aResultSet != null) {
                List<Row> rows = new ArrayList<>();
                while ((aPageSize <= 0 || rows.size() < aPageSize) && aResultSet.next()) {
                    Row row = readRow(aFields, aResultSet);
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
     * @param aResultSet Result set to read from.
     * @return The row had been read.
     * @throws SQLException
     * @throws InvalidColIndexException
     * @throws RowsetException
     */
    protected Row readRow(Fields aFields, ResultSet aResultSet) throws SQLException, InvalidColIndexException, RowsetException {
        if (aResultSet != null) {
            Row row = new Row(aFields);
            for (int i = 1; i <= aFields.getFieldsCount(); i++) {
                Object jdbcObject = null;
                if (converter != null) {
                    jdbcObject = converter.getFromJdbcAndConvert2RowsetCompatible(aResultSet, i, aFields.get(i).getTypeInfo());
                } else {
                    jdbcObject = aResultSet.getObject(i);
                }
                row.setColumnObject(i, jdbcObject);
            }
            return row;
        }
        return null;
    }
}
