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
import com.eas.script.Scripts;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;

/**
 *
 * @author mg
 */
public class FieldsTypeIconsCache {

    public static final Map<Object, String> icons16 = new HashMap<>();
    //public static final Map<Object, String> icons24 = new HashMap<>();
    //public static final Map<Object, String> icons32 = new HashMap<>();

    static {
        icons16.put(null, "datatypes/16x16/blob.png");
        icons16.put(Scripts.NUMBER_TYPE_NAME, "datatypes/16x16/number.png");
        icons16.put(Scripts.BOOLEAN_TYPE_NAME, "datatypes/16x16/flag.png");
        icons16.put(Scripts.STRING_TYPE_NAME, "datatypes/16x16/text.png");
        icons16.put(Scripts.DATE_TYPE_NAME, "datatypes/16x16/date.png");
        icons16.put(Scripts.GEOMETRY_TYPE_NAME, "datatypes/16x16/geometry.png");
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
