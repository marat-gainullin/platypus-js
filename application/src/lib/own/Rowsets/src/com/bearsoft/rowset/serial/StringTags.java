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

    public static String PROPERTIES = "properties";
        public static String SESSION = "session";
        public static String DATABASE = "database";
        public static String POSITION = "position";

    public static String METADATA = "metadata";
        public static String FIELD = "field";
            public static String FIELD_NAME = "name";
            public static String FIELD_DESCRIPTION = "description";
            public static String FIELD_TYPE = "type";
            public static String FIELD_TYPENAME = "typename";
            public static String FIELD_CLASSNAME = "classname";
            public static String FIELD_SIZE = "size";
            public static String FIELD_SCALE = "scale";
            public static String FIELD_PRECISION = "precision";
            public static String FIELD_SIGNED = "signed";
            public static String FIELD_NULLABLE = "nullable";
            public static String FIELD_READONLY = "readonly";
            public static String FIELD_PK = "pk";
            public static String FIELD_FK = "fk";
            public static String FIELD_TABLENAME = "tableName";
            public static String FIELD_SCHEMANAME = "schemaName";
                public static String PK_DATABASE = DATABASE;
                public static String PK_SCHEMANAME = FIELD_SCHEMANAME;
                public static String PK_TABLENAME = FIELD_TABLENAME;
                public static String PK_FIELD_NAME = FIELD_NAME;
                public static String CNAME = "cName";
    public static String CURRENT_DATA = "currentData";
    public static String ORIGINAL_DATA = "originalData";
        public static String ROW = "row";
            public static String VALUE = "value";
}
