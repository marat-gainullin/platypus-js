/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.exceptions;

/**
 * Exception class for situation, when you tyr to add some serializer for type,
 * typename and class name, that already present in serializers container.
 * @author mg
 */
public class AlreadyExistSerializerException extends RowsetException {

    protected int sqlType;
    protected String sqlTypeName;
    protected String javaClassName;

    /**
     * Exception constructor.
     * @param aSqlType Sql-type of the added serializer.
     * @param aSqlTypeName Sql-type name of the added serializer.
     * @param aJavaClassName Java class name of the added serializer.
     * @see java.sql.Types
     */
    public AlreadyExistSerializerException(int aSqlType, String aSqlTypeName, String aJavaClassName) {
        super("Custom serializer already exists. Try to review sqlType, sqlTypeName and javaClassName in this exception.");
        sqlType = aSqlType;
        sqlTypeName = aSqlTypeName;
        javaClassName = aJavaClassName;
    }

    /**
     * Returns sql type of the exception subject.
     * @return Sql type of the exception subject - serializer.
     */
    public int getSqlType() {
        return sqlType;
    }

    /**
     * Returns sql type name of the exception subject.
     * @return Sql type name of the exception subject - serializer.
     */
    public String getSqlTypename() {
        return sqlTypeName;
    }

    /**
     * Returns java class name of the exception subject.
     * @return Java class name of the exception subject - serializer.
     */
    public String getJavaClassName() {
        return javaClassName;
    }
}