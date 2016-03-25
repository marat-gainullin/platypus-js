/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application;

/**
 *
 * @author mg
 */
public class PlatypusUtils {

    public static final String COMMON_ENCODING_NAME = "utf-8";
    public static final String DATAFOLDER_REORDERABLE_PROP_NAME = "DataFolder.Index.reorderable";
    public static final String ELEMENTS_SOURCES_GROUP = "applicationElements";
    public static final String ELEMENTS_API_GROUP = "apiElements";
    public static final String ELEMENTS_LIB_GROUP = "libElements";
/*
    public static List<String> achieveSchemas(String aUrl, String aUser, String aPassword) throws Exception {
        List<String> schemas = new ArrayList<>();
        try (java.sql.Connection conn = DriverManager.getConnection(aUrl, aUser, aPassword)) {
            try (ResultSet rs = conn.getMetaData().getSchemas()) {
                while (rs.next()) {
                    String schema = rs.getString(ClientConstants.JDBCCOLS_TABLE_SCHEM);
                    if (schema != null) {
                        schemas.add(schema);
                    }
                }
                return schemas;
            }
        }
    }

    public static void createSchema(String aUrl, String aUser, String aPassword, String aSchema) throws Exception {
        String dialect = SQLUtils.dialectByUrl(aUrl);
        SqlDriver driver = SQLUtils.getSqlDriver(dialect);
        try (java.sql.Connection conn = DriverManager.getConnection(aUrl, aUser, aPassword)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(driver.getSql4CreateSchema(aSchema, null));
            }
        }
    }
*/
}
