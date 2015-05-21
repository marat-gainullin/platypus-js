/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.eas.client.SQLUtils;
import com.eas.client.model.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.ErrorManager;

/**
 *
 * @author vv
 */
public class CommonTypesEditor extends SelectIntEditor {

    private static final Map<Integer, String> typesNames = new HashMap<>();

    static {
        typesNames.put(java.sql.Types.ARRAY, "ARRAY");
        typesNames.put(java.sql.Types.BIGINT, "BIGINT");
        typesNames.put(java.sql.Types.BINARY, "BINARY");
        typesNames.put(java.sql.Types.BIT, "BIT");
        typesNames.put(java.sql.Types.BLOB, "BLOB");
        typesNames.put(java.sql.Types.BOOLEAN, "BOOLEAN");
        typesNames.put(java.sql.Types.CHAR, "CHAR");
        typesNames.put(java.sql.Types.CLOB, "CLOB");
        typesNames.put(java.sql.Types.DATALINK, "DATALINK");
        typesNames.put(java.sql.Types.DATE, "DATE");
        typesNames.put(java.sql.Types.DECIMAL, "DECIMAL");
        typesNames.put(java.sql.Types.DISTINCT, "DISTINCT");
        typesNames.put(java.sql.Types.DOUBLE, "DOUBLE");
        typesNames.put(java.sql.Types.FLOAT, "FLOAT");
        typesNames.put(java.sql.Types.INTEGER, "INTEGER");
        typesNames.put(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT");
        typesNames.put(java.sql.Types.LONGNVARCHAR, "LONGNVARCHAR");
        typesNames.put(java.sql.Types.LONGVARBINARY, "LONGVARBINARY");
        typesNames.put(java.sql.Types.LONGVARCHAR, "LONGVARCHAR");
        typesNames.put(java.sql.Types.NCHAR, "NCHAR");
        typesNames.put(java.sql.Types.NCLOB, "NCLOB");
        typesNames.put(java.sql.Types.NULL, "NULL");
        typesNames.put(java.sql.Types.NUMERIC, "NUMERIC");
        typesNames.put(java.sql.Types.NVARCHAR, "NVARCHAR");
        typesNames.put(java.sql.Types.OTHER, "OTHER");
        typesNames.put(java.sql.Types.REAL, "REAL");
        typesNames.put(java.sql.Types.REF, "REF");
        typesNames.put(java.sql.Types.ROWID, "ROWID");
        typesNames.put(java.sql.Types.SMALLINT, "SMALLINT");
        typesNames.put(java.sql.Types.SQLXML, "SQLXML");
        typesNames.put(java.sql.Types.STRUCT, "STRUCT");
        typesNames.put(java.sql.Types.TIME, "TIME");
        typesNames.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
        typesNames.put(java.sql.Types.TINYINT, "TINYINT");
        typesNames.put(java.sql.Types.VARBINARY, "VARBINARY");
        typesNames.put(java.sql.Types.VARCHAR, "VARCHAR");
    }

    private CommonTypesEditor(String[] aKeys, int[] aValues) {
        super(aKeys, aValues);
    }

    public static synchronized CommonTypesEditor getNewInstanceFor(Model model) {
        List<Integer> typeIndexesList = new ArrayList<>();
        for (Integer e : typesNames.keySet()) {
            try {
                if (model.isTypeSupported(e)) {
                    typeIndexesList.add(e);
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        int[] types = new int[typeIndexesList.size()];
        int i = 0;
        for (Integer e : typeIndexesList) {
            types[i++] = e.intValue();
        }
        String[] typeNames = new String[types.length];
        for (int j = 0; j < typeNames.length; j++) {
            typeNames[j] = SQLUtils.getLocalizedTypeName(types[j]);
        }
        return new CommonTypesEditor(typeNames, types);
    }

    @Override
    public String getAsText() {
        Integer val = (Integer) getValue();
        if (val != null) {
            return SQLUtils.getLocalizedTypeName(val);
        } else {
            return "";//NOI18N
        }
    }
}
