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

    protected int type;
    protected String typeName;

    /**
     * Exception constructor.
     * @param aType Type of the added serializer.
     * @param aTypeName Type name of the added serializer.
     * @see java.sql.Types
     */
    public AlreadyExistSerializerException(int aType, String aTypeName) {
        super("Custom serializer already exists. Try to review type and typeName in this exception.");
        type = aType;
        typeName = aTypeName;
    }

    /**
     * Returns type of the exception subject.
     * @return type of the exception subject - serializer.
     */
    public int getType() {
        return type;
    }

    /**
     * Returns type name of the exception subject.
     * @return type name of the exception subject - serializer.
     */
    public String getTypeName() {
        return typeName;
    }

}