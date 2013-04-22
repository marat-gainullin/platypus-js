/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Converter has to convert some value to and from rowset's internal representation.
 * @author mg
 */
public interface Converter {

    /**
     * Gets some value from jdbc ResultSet using appropriate method and than calls
     * Converter.convert2RowsetCompatible() method
     * @see Converter#convert2RowsetCompatible(java.lang.Object, com.bearsoft.rowset.metadata.DataTypeInfo)
     * @see DataTypeInfo
     */
    public Object getFromJdbcAndConvert2RowsetCompatible(ResultSet aRs, int aColIndex, DataTypeInfo aTypeInfo) throws RowsetException;

    /**
     * Converts some value to rowset internal compatible representation.
     * For example, there are some types constructed above java.sql.Types.STRUCT type, according to some type name, specific for database.
     * After converting, applications would be able to use converted value in their's specific tasks.
     * On another hand, method may be used as converter from some value of wide range classes to value of predefined class.
     * Example: The same number may be represented by object such range of classes: Float, Double, Short and others.
     * After converting it would be object of BigDecimal class. So, we see two use cases of this method:
     * (1) Converting from value retrived from database to internal application's representation.
     * (2) Converting from value of wide range of classes to value of one(right) predefined class.
     * @param aSqlType Type identifier from <code>java.sql.Types</code>.
     * @param aSqlTypeName Type name from database.
     * @param javaClassName Java class name from jdbc driver typemap.
     * @return Converted value.
     * @throws RowsetException
     * @see DataTypeInfo
     * @see java.sql.Types
     */
    public Object convert2RowsetCompatible(Object aValue, DataTypeInfo aTypeInfo) throws RowsetException;

    /**
     * Converts some value from rowset's internal representation to JDBC compatible representation.
     * After converting, we aught to be able to set converted value to jdbc statement object as a parameter.
     * For example, there are some types constructed above java.sql.Types.STRUCT type, according to some type name, specific for database.
     * @param aSqlType Type identifier from <code>java.sql.Types</code>.
     * @param aValue Value that is to be setted to statement. May be null.
     * @param aSqlTypeName Type name from database.
     * @param javaClassName Java class name from jdbc driver typemap.
     * @return Converted value.
     * @throws RowsetException
     * @see DataTypeInfo
     * @see java.sql.Types
     */
    public Object convert2JdbcCompatible(Object aValue, DataTypeInfo aTypeInfo) throws RowsetException;
    /**
     * Converts some value from rowset's internal representation to JDBC compatible representation.
     * Converted value is assigned to the statement at the specified parameter index.
     * It's considered, that null values are processed in a normal way and they are legal.
     * After converting, we ought to be able to set converted value to jdbc statement object as a parameter.
     * For example, there are some types constructed above java.sql.Types.STRUCT type, according to some type name, specific for database.
     * @param aSqlType Type identifier from <code>java.sql.Types</code>.
     * @param aValue Value that is to be setted to statement. May be null.
     * @param aSqlTypeName Type name from database.
     * @param javaClassName Java class name from jdbc driver typemap.
     * @param aParameterIndex An index within the statement at which parameter value is to be setted.
     * @param aStmt Statement to assign parameters to.
     * @throws RowsetException
     * @see java.sql.Types
     */
    public void convert2JdbcAndAssign(Object aValue, DataTypeInfo aTypeInfo, Connection aConn, int aParameterIndex, PreparedStatement aStmt) throws RowsetException;
}
