/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.serial;

/**
 * Tags enumeration, used for text serialization.
 * @author mg
 */
public class StringTags {

    public static final String PROPERTIES = "properties";
        public static final String SESSION = "session";
        public static final String DATABASE = "database";
        public static final String POSITION = "position";

    public static final String METADATA = "metadata";
        public static final String FIELD = "field";
            public static final String FIELD_NAME = "name";
            public static final String FIELD_DESCRIPTION = "description";
            public static final String FIELD_TYPE = "type";
            public static final String FIELD_TYPENAME = "typename";
            public static final String FIELD_CLASSNAME = "classname";
            public static final String FIELD_SIZE = "size";
            public static final String FIELD_SCALE = "scale";
            public static final String FIELD_PRECISION = "precision";
            public static final String FIELD_SIGNED = "signed";
            public static final String FIELD_NULLABLE = "nullable";
            public static final String FIELD_READONLY = "readonly";
            public static final String FIELD_PK = "pk";
            public static final String FIELD_FK = "fk";
            public static final String FIELD_TABLENAME = "tableName";
            public static final String FIELD_SCHEMANAME = "schemaName";
                public static final String PK_DATABASE = DATABASE;
                public static final String PK_SCHEMANAME = FIELD_SCHEMANAME;
                public static final String PK_TABLENAME = FIELD_TABLENAME;
                public static final String PK_FIELD_NAME = FIELD_NAME;
                public static final String CNAME = "cName";
    public static final String CURRENT_DATA = "currentData";
    public static final String ORIGINAL_DATA = "originalData";
        public static final String ROW = "row";
            public static final String VALUE = "value";
}
