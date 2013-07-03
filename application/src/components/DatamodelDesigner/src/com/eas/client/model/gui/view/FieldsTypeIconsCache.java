/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.view;

import com.eas.client.model.gui.IconCache;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;

/**
 *
 * @author Marat
 */
public class FieldsTypeIconsCache {

    public static final Map<Object, String> icons16 = new HashMap<>();
    public static final Map<Object, String> icons24 = new HashMap<>();
    public static final Map<Object, String> icons32 = new HashMap<>();

    static {
        icons16.put(java.sql.Types.ARRAY, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.BIGINT, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.BINARY, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.BIT, "datatypes/16x16/flag.png");
        icons16.put(java.sql.Types.BLOB, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.BOOLEAN, "datatypes/16x16/flag.png");
        icons16.put(java.sql.Types.CHAR, "datatypes/16x16/text.png");
        icons16.put(java.sql.Types.CLOB, "datatypes/16x16/text.png");
        icons16.put(java.sql.Types.DATALINK, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.DATE, "datatypes/16x16/date.png");
        icons16.put(java.sql.Types.DECIMAL, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.DISTINCT, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.DOUBLE, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.FLOAT, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.INTEGER, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.JAVA_OBJECT, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.LONGNVARCHAR, "datatypes/16x16/text.png");
        icons16.put(java.sql.Types.LONGVARBINARY, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.LONGVARCHAR, "datatypes/16x16/text.png");
        icons16.put(java.sql.Types.NCHAR, "datatypes/16x16/text.png");
        icons16.put(java.sql.Types.NCLOB, "datatypes/16x16/text.png");
        icons16.put(java.sql.Types.NULL, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.NUMERIC, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.NVARCHAR, "datatypes/16x16/text.png");
        icons16.put(java.sql.Types.OTHER, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.REAL, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.REF, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.ROWID, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.SMALLINT, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.SQLXML, "datatypes/16x16/xml.png");
        icons16.put(java.sql.Types.STRUCT, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.TIME, "datatypes/16x16/date.png");
        icons16.put(java.sql.Types.TIMESTAMP, "datatypes/16x16/date.png");
        icons16.put(java.sql.Types.TINYINT, "datatypes/16x16/number.png");
        icons16.put(java.sql.Types.VARBINARY, "datatypes/16x16/blob.png");
        icons16.put(java.sql.Types.VARCHAR, "datatypes/16x16/text.png");
        // By type name icon. Type names must be in uppercase
        // This types placed here directly because they are java.sql.Types.OTHER types,
        // and they can't be expressed in standard jdbc typespace.
        icons16.put("JTS_GEOMETRY", "datatypes/16x16/geometry.png");
        icons16.put("POINT", "datatypes/16x16/geometry.png");
        icons16.put("LINE", "datatypes/16x16/geometry.png");
        icons16.put("LSEG", "datatypes/16x16/geometry.png");
        icons16.put("BOX", "datatypes/16x16/geometry.png");
        icons16.put("BOX2D", "datatypes/16x16/geometry.png");
        icons16.put("BOX3D", "datatypes/16x16/geometry.png");
        icons16.put("PATH", "datatypes/16x16/geometry.png");
        icons16.put("POLYGON", "datatypes/16x16/geometry.png");
        icons16.put("CIRCLE", "datatypes/16x16/geometry.png");
        icons16.put("SPHEROID", "datatypes/16x16/geometry.png");
        icons16.put("GEOMETRY", "datatypes/16x16/geometry.png");

        icons24.put(new Integer(java.sql.Types.ARRAY), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.BIGINT), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.BINARY), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.BIT), "datatypes/24x24/flag.png");
        icons24.put(new Integer(java.sql.Types.BLOB), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.BOOLEAN), "datatypes/24x24/flag.png");
        icons24.put(new Integer(java.sql.Types.CHAR), "datatypes/24x24/text.png");
        icons24.put(new Integer(java.sql.Types.CLOB), "datatypes/24x24/text.png");
        icons24.put(new Integer(java.sql.Types.DATALINK), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.DATE), "datatypes/24x24/date.png");
        icons24.put(new Integer(java.sql.Types.DECIMAL), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.DISTINCT), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.DOUBLE), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.FLOAT), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.INTEGER), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.JAVA_OBJECT), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.LONGNVARCHAR), "datatypes/24x24/text.png");
        icons24.put(new Integer(java.sql.Types.LONGVARBINARY), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.LONGVARCHAR), "datatypes/24x24/text.png");
        icons24.put(new Integer(java.sql.Types.NCHAR), "datatypes/24x24/text.png");
        icons24.put(new Integer(java.sql.Types.NCLOB), "datatypes/24x24/text.png");
        icons24.put(new Integer(java.sql.Types.NULL), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.NUMERIC), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.NVARCHAR), "datatypes/24x24/text.png");
        icons24.put(new Integer(java.sql.Types.OTHER), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.REAL), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.REF), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.ROWID), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.SMALLINT), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.SQLXML), "datatypes/24x24/xml.png");
        icons24.put(new Integer(java.sql.Types.STRUCT), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.TIME), "datatypes/24x24/date.png");
        icons24.put(new Integer(java.sql.Types.TIMESTAMP), "datatypes/24x24/date.png");
        icons24.put(new Integer(java.sql.Types.TINYINT), "datatypes/24x24/number.png");
        icons24.put(new Integer(java.sql.Types.VARBINARY), "datatypes/24x24/blob.png");
        icons24.put(new Integer(java.sql.Types.VARCHAR), "datatypes/24x24/text.png");

        icons32.put(new Integer(java.sql.Types.ARRAY), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.BIGINT), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.BINARY), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.BIT), "datatypes/32x32/flag.png");
        icons32.put(new Integer(java.sql.Types.BLOB), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.BOOLEAN), "datatypes/32x32/flag.png");
        icons32.put(new Integer(java.sql.Types.CHAR), "datatypes/32x32/text.png");
        icons32.put(new Integer(java.sql.Types.CLOB), "datatypes/32x32/text.png");
        icons32.put(new Integer(java.sql.Types.DATALINK), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.DATE), "datatypes/32x32/date.png");
        icons32.put(new Integer(java.sql.Types.DECIMAL), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.DISTINCT), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.DOUBLE), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.FLOAT), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.INTEGER), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.JAVA_OBJECT), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.LONGNVARCHAR), "datatypes/32x32/text.png");
        icons32.put(new Integer(java.sql.Types.LONGVARBINARY), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.LONGVARCHAR), "datatypes/32x32/text.png");
        icons32.put(new Integer(java.sql.Types.NCHAR), "datatypes/32x32/text.png");
        icons32.put(new Integer(java.sql.Types.NCLOB), "datatypes/32x32/text.png");
        icons32.put(new Integer(java.sql.Types.NULL), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.NUMERIC), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.NVARCHAR), "datatypes/32x32/text.png");
        icons32.put(new Integer(java.sql.Types.OTHER), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.REAL), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.REF), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.ROWID), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.SMALLINT), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.SQLXML), "datatypes/32x32/xml.png");
        icons32.put(new Integer(java.sql.Types.STRUCT), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.TIME), "datatypes/32x32/date.png");
        icons32.put(new Integer(java.sql.Types.TIMESTAMP), "datatypes/32x32/date.png");
        icons32.put(new Integer(java.sql.Types.TINYINT), "datatypes/32x32/number.png");
        icons32.put(new Integer(java.sql.Types.VARBINARY), "datatypes/32x32/blob.png");
        icons32.put(new Integer(java.sql.Types.VARCHAR), "datatypes/32x32/text.png");
    }

    public static Icon getIcon16(int columnType) {
        String iconName = icons16.get(columnType);
        Icon lic = null;
        if (iconName != null) {
            lic = IconCache.getIcon(iconName);
        }
        return lic;
    }

    public static Icon getIcon16(String aTypeName) {
        String iconName = icons16.get(aTypeName);
        Icon lic = null;
        if (iconName != null) {
            lic = IconCache.getIcon(iconName);
        }
        return lic;
    }

    public static Icon getIcon24(int columnType) {
        String iconName = icons16.get(columnType);
        Icon lic = null;
        if (iconName != null) {
            lic = IconCache.getIcon(iconName);
        }
        return lic;
    }

    public static Icon getIcon32(int columnType) {
        String iconName = icons16.get(columnType);
        Icon lic = null;
        if (iconName != null) {
            lic = IconCache.getIcon(iconName);
        }
        return lic;
    }

    public static Icon getPkIcon16() {
        return IconCache.getIcon("datatypes/16x16/pk.png");
    }

    public static Icon getPkIcon24() {
        return IconCache.getIcon("datatypes/24x24/pk.png");
    }

    public static Icon getPkIcon32() {
        return IconCache.getIcon("datatypes/32x32/pk.png");
    }

    public static Icon getFkIcon16() {
        return IconCache.getIcon("datatypes/16x16/fk.png");
    }

    public static Icon getFkIcon24() {
        return IconCache.getIcon("datatypes/24x24/fk.png");
    }

    public static Icon getFkIcon32() {
        return IconCache.getIcon("datatypes/32x32/fk.png");
    }

    public static Icon getParameterIcon16() {
        return IconCache.getIcon("param16.png");
    }
}
