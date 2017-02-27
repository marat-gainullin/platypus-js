/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.metadata;

/**
 * Tags enumeration, used for binary serialization.
 * @author mg
 */
public class FieldsTags {

    // properties
    /*
    public static final int SESSION           = 1;
    public static final int CURSOR_POSITION   = 2;
    public static final int TRANSACTED        = 3;
    */

    //public static final int METADATA          = 10;
        public static final int FIELD                 = 1;
            public static final int FIELD_NAME        = 2;
            public static final int FIELD_DESCRIPTION = 3;
            public static final int FIELD_TYPE        = 4;
            public static final int FIELD_TYPENAME    = 5;
            public static final int FIELD_CLASSNAME   = 6;
            public static final int FIELD_SIZE        = 7;
            public static final int FIELD_SCALE       = 8;
            public static final int FIELD_PRECISION   = 9;
            public static final int FIELD_SIGNED      = 10;
            public static final int FIELD_NULLABLE    = 11;
            public static final int FIELD_READONLY    = 12;
            public static final int FIELD_TABLENAME   = 13;
            public static final int FIELD_SCHEMANAME  = 14;
            public static final int FIELD_PK          = 15;
            public static final int FIELD_FK          = 16;
                public static final int FK_SCHEMANAME      = 17;
                public static final int FK_TABLENAME       = 18;
                public static final int FK_FIELD_NAME      = 19;
                public static final int FK_UPDATE_RULE     = 20;
                public static final int FK_DELETE_RULE     = 21;
                public static final int FK_DEFERRABLE      = 22;
                public static final int FK_CNAME           = 23;

                public static final int PK_SCHEMANAME      = 24;
                public static final int PK_TABLENAME       = 25;
                public static final int PK_FIELD_NAME      = 26;
                public static final int PK_CNAME           = 27;
                /*
    public static final int DATA       = 20;
        public static final int ROW               = 1;
            public static final int DELETED             = 1;
            public static final int INSERTED            = 2;
            public static final int UPDATED             = 3;
                public static final int UPDATED_INDEX       = 1;
            public static final int CURRENT_VALUE       = 4;
            public static final int ORIGINAL_VALUE      = 5;
    public static final int PARAMETERS = 30;
                */
}
