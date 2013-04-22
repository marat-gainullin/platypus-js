/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.serial;

import com.bearsoft.rowset.exceptions.AlreadyExistSerializerException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for rowset serializers, both readers and writers.
 * @author mg
 */
public class RowsetSerializer {

    protected Map<DataTypeInfo, CustomSerializer> serializers = new HashMap<>();

    /**
     * Default constructor for <code>RowsetSerializer</code> class.
     * @param aRowset
     */
    public RowsetSerializer() {
        super();
    }

    /**
     * Adds a cutom serializer, according to it's sql type, type name and java class name.
     * @param aSqlType Sql type from <code>java.sql.Types</code>
     * @param aSqlTypeName Sql type name for <code>aSqlType</code>
     * @param javaClassName Class name generated while reading metadata from database. Typically it's name from jdbc driver typemap.
     * @param aSerializer Serializer to be added.
     * @throws AlreadyExistSerializerException
     * @see java.sql.Types
     */
    public void addSerializer(DataTypeInfo aTypeInfo, CustomSerializer aSerializer) throws AlreadyExistSerializerException {
        if (!serializers.containsKey(aTypeInfo)) {
            serializers.put(aTypeInfo, aSerializer);
        } else {
            throw new AlreadyExistSerializerException(aTypeInfo.getSqlType(), aTypeInfo.getSqlTypeName(), aTypeInfo.getJavaClassName());
        }
    }

    /**
     * Returns a cutom serializer, according to it's sql type, type name and java class name.
     * @param aSqlType Sql type from <code>java.sql.Types</code>
     * @param aSqlTypeName Sql type name for <code>aSqlType</code>
     * @param javaClassName Class name generated while reading metadata from database. Typically it's name from jdbc driver typemap.
     * @return Serializer or null if no such serializer found.
     * @see java.sql.Types
     */
    public CustomSerializer getSerializer(DataTypeInfo aTypeInfo) {
        return serializers.get(aTypeInfo);
    }

    /**
     * Returns whether a serializer, according to it's sql type, type name and java class name present in the rowset serializer.
     * @param aSqlType Sql type from <code>java.sql.Types</code>
     * @param aSqlTypeName Sql type name for <code>aSqlType</code>
     * @param javaClassName Class name generated while reading metadata from database. Typically it's name from jdbc driver typemap.
     * @return Whether serializer is found.
     * @see java.sql.Types
     */
    public boolean containsSerializer(DataTypeInfo aTypeInfo) {
        return serializers.containsKey(aTypeInfo);
    }

    /**
     * Costructs a vector of custom serializers, previously installed in the serializer.
     * Serializers vector is constructed according to information from rowset's fields.
     * @return ArrayList of custom serializers.
     */
    protected List<CustomSerializer> achieveCustomSerializers(Fields fields) {
        ArrayList<CustomSerializer> customSerializers = new ArrayList<>();
        customSerializers.ensureCapacity(fields.getFieldsCount());
        for (int j = 1; j <= fields.getFieldsCount(); j++) {
            customSerializers.add(null);
            Field field = fields.get(j);
            CustomSerializer lSerializer = getSerializer(field.getTypeInfo());
            customSerializers.set(j - 1, lSerializer);
        }
        return customSerializers;
    }
}