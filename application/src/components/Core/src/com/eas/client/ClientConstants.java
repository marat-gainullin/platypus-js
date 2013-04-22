/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client;

/**
 *
 * @author Marat
 */
public class ClientConstants {

    // Eas metadata tables and fields names
    public static final String T_MTD_ENTITIES = "MTD_ENTITIES";
    public static final String F_MDENT_ID = "MDENT_ID";
    public static final String F_MDENT_CONTENT_TXT = "MDENT_CONTENT_TXT";
    public static final String F_MDENT_NAME = "MDENT_NAME";
    public static final String F_MDENT_PARENT_ID = "MDENT_PARENT_ID";
    public static final String F_MDENT_TYPE = "MDENT_TYPE";
    public static final String F_MDENT_ORDER = "MDENT_ORDER";
    public static final String F_MDENT_CONTENT_TXT_SIZE = "MDENT_CONTENT_TXT_SIZE";
    public static final String F_MDENT_CONTENT_TXT_CRC32 = "MDENT_CONTENT_TXT_CRC32";
    public static final String T_MTD_USERS = "MTD_USERS";
    public static final String T_MTD_GROUPS = "MTD_GROUPS";
    public static final String F_USR_NAME = "USR_NAME";
    public static final String F_USR_PASSWD = "USR_PASSWD";
    public static final String F_USR_FORM = "USR_FORM";
    public static final String F_USR_CONTEXT = "USR_CONTEXT";
    public static final String F_GROUP_NAME = "GROUP_NAME";
    public static final String F_USR_PHONE = "USR_PHONE";
    public static final String F_USR_EMAIL = "USR_EMAIL";
    public static final String T_MTD_VERSION = "MTD_VERSION";
    public static final String F_VERSION_VALUE = "VERSION_VALUE";
    public static final String T_MTD_LOCALIZATION = "MTD_LOCALIZATION";
    public static final String F_MDLOC_ID = "MDLOC_ID";
    public static final String F_MDLOC_NAME = "MDLOC_NAME";
    public static final String F_MDLOC_TRANSLATION = "MDLOC_TRANSLATION";
    public static final String APP_ELEMENT_SQL_PARAM_NAME = "md_id";
    public static final String SQL_SELECT_MD = "select * from " + ClientConstants.T_MTD_ENTITIES + " where " + ClientConstants.T_MTD_ENTITIES + "." + ClientConstants.F_MDENT_ID + "=:"+APP_ELEMENT_SQL_PARAM_NAME;
    public static final String JDBCFKS_FKTABLE_SCHEM = "FKTABLE_SCHEM";
    public static final String JDBCFKS_FKTABLE_NAME = "FKTABLE_NAME";
    public static final String JDBCFKS_FKCOLUMN_NAME = "FKCOLUMN_NAME";
    public static final String JDBCFKS_FK_NAME = "FK_NAME";
    public static final String JDBCFKS_FKUPDATE_RULE = "UPDATE_RULE";
    public static final String JDBCFKS_FKDELETE_RULE = "DELETE_RULE";
    public static final String JDBCFKS_FKDEFERRABILITY = "DEFERRABILITY";
    public static final String JDBCFKS_FKPKTABLE_SCHEM = "PKTABLE_SCHEM";
    public static final String JDBCFKS_FKPKTABLE_NAME = "PKTABLE_NAME";
    public static final String JDBCFKS_FKPKCOLUMN_NAME = "PKCOLUMN_NAME";
    public static final String JDBCFKS_FKPK_NAME = "PK_NAME";
    public static final String JDBCCOLS_TABLE_SCHEM = "TABLE_SCHEM";
    public static final String JDBCCOLS_TABLE_NAME = "TABLE_NAME";
    public static final String JDBCCOLS_TABLE_DESC = "TABLE_DESCRIPTION";
    public static final String JDBCCOLS_COLUMN_NAME = "COLUMN_NAME";
    public static final String JDBCCOLS_REMARKS = "REMARKS";
    public static final String JDBCCOLS_DATA_TYPE = "DATA_TYPE";
    public static final String JDBCCOLS_TYPE_NAME = "TYPE_NAME";
    public static final String JDBCCOLS_COLUMN_SIZE = "COLUMN_SIZE";
    public static final String JDBCCOLS_DECIMAL_DIGITS = "DECIMAL_DIGITS";
    public static final String JDBCCOLS_NUM_PREC_RADIX = "NUM_PREC_RADIX";
    public static final String JDBCCOLS_NULLABLE = "NULLABLE";
    public static final String JDBCPKS_TABLE_SCHEM = JDBCCOLS_TABLE_SCHEM;
    public static final String JDBCPKS_TABLE_NAME = JDBCCOLS_TABLE_NAME;
    public static final String JDBCPKS_COLUMN_NAME = JDBCCOLS_COLUMN_NAME;
    public static final String JDBCPKS_CONSTRAINT_NAME = "PK_NAME";
    public static final String JDBCIDX_TABLE_SCHEM = JDBCCOLS_TABLE_SCHEM;
    public static final String JDBCIDX_TABLE_NAME = JDBCCOLS_TABLE_NAME;
    public static final String JDBCIDX_COLUMN_NAME = JDBCCOLS_COLUMN_NAME;
    public static final String JDBCIDX_NON_UNIQUE = "NON_UNIQUE";      //boolean => Can index values be non-unique. false when TYPE is tableIndexStatistic
    public static final String JDBCIDX_INDEX_QUALIFIER = "INDEX_QUALIFIER"; //String => index catalog (may be null); null when TYPE is tableIndexStatistic
    public static final String JDBCIDX_INDEX_NAME = "INDEX_NAME";      //String => index name; null when TYPE is tableIndexStatistic
    public static final String JDBCIDX_TYPE = "TYPE";            //short => index type:
    //tableIndexStatistic - this identifies table statistics that are returned in conjuction with a table's index descriptions
    //tableIndexClustered - this is a clustered index
    //tableIndexHashed - this is a hashed index
    //tableIndexOther - this is some other style of index
    public static final String JDBCIDX_ORDINAL_POSITION = "ORDINAL_POSITION";//short => column sequence number within index; zero when TYPE is tableIndexStatistic
    public static final String JDBCIDX_ASC_OR_DESC = "ASC_OR_DESC";//String => column sort sequence, "A" => ascending, "D" => descending, may be null if sort sequence is not supported; null when TYPE is tableIndexStatistic
    public static final String JDBCPKS_TABLE_TYPE_FIELD_NAME = "TABLE_TYPE";
    public static final String JDBCPKS_TABLE_TYPE_TABLE = "TABLE";
    public static final String JDBCPKS_TABLE_TYPE_VIEW = "VIEW";
    
    public static final String F_TABLE_COMMENTS_COMMENT_FIELD_NAME = "COMMENTS";
    public static final String F_COLUMNS_COMMENTS_FIELD_FIELD_NAME = "COLUMN_NAME";
    public static final String F_COLUMNS_COMMENTS_COMMENT_FIELD_NAME = "COMMENTS";
    public static final String F_TABLE_COMMENTS_NAME_FIELD_NAME = "TABLE_NAME";
    
    // Java properties names
    public static final String USER_DIR_PROP_NAME = "user.dir";
    public static final String USER_HOME_PROP_NAME = "user.home";
    public static final String LINE_SEPARATOR_PROP_NAME = "line.separator";
    //public static final String TEMP_DIR_PROP_NAME = "java.io.tmpdir";
    public static final String USER_HOME_PLATYPUS_DIRECTORY_NAME = ".platypus";
    public static final String ENTITIES_CACHE_DIRECTORY_NAME = "entitiesCache";
    public static final String CRLF = "\r\n";
    // Типы элементов приложения
    public static final int ET_CONNECTION = 10;
    public static final int ET_COMPONENT = 20;
    public static final int ET_REPORT = 40;
    public static final int ET_QUERY = 50;
    public static final int ET_DB_SCHEME = 60;
    public static final int ET_FOLDER = 70;
    public static final int ET_FORM = 90;
    // Типы элементов метаданных БД
    public static final int DB_MD_TYPE_TABLES = 1;
    public static final int DB_MD_TYPE_SCHEMAS = 2;
    // Some settings
    public static final int QUERY_TIMEOUT = 3600;// secs
    // Error messages
    public static final String NO_SETTINGS_FILE = "No or bad settings file: ";
    public static final String NO_JAVA_PROPERTY = "No java property: ";
    public static final String QUERY_ID_PREFIX = "q";
    public static final String DB_CONNECTION_USER_PROP_NAME = "user";
    public static final String DB_CONNECTION_PASSWORD_PROP_NAME = "password";
    public static final String DB_CONNECTION_SCHEMA_PROP_NAME = "schema";
    public static final String DB_CONNECTION_CURRENT_SCHEMA_PROP_NAME = "currentSchema";
    public static final String DB_CONNECTION_URL_PROP_NAME = "url";
    public static final String DB_CONNECTION_DIALECT_PROP_NAME = "dialect";
    public static final String SERVER_PROPERTY_ORACLE_DIALECT = "Oracle";
    public static final String SERVER_PROPERTY_MSSQL_DIALECT = "MsSql";
    public static final String SERVER_PROPERTY_DERBY_DIALECT = "Derby";
    public static final String SERVER_PROPERTY_POSTGRE_DIALECT = "Postgre";
    public static final String SERVER_PROPERTY_MYSQL_DIALECT = "MySql";
    public static final String SERVER_PROPERTY_DB2_DIALECT = "Db2";
    public static final String SERVER_PROPERTY_H2_DIALECT = "H2";
    public static final long APP_DB_DUMMY_ID = -1l;
}
