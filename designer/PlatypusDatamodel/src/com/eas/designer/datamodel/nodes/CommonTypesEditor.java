/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.SQLUtils;
import com.eas.client.model.Model;
import java.util.ArrayList;
import java.util.List;
import org.openide.ErrorManager;

/**
 *
 * @author vv
 */
public class CommonTypesEditor extends SelectIntEditor {

    private static String[] typeNames;
    private static int[] types;
    private static Model model;

    private CommonTypesEditor(String[] aKeys, int[] aValues) {
        super(aKeys, aValues);
    }

    public static synchronized CommonTypesEditor getNewInstanceFor(Model aModel) {
        if (model == null || model != aModel) {
            model = aModel;
            List<Integer> typeIndexesList = new ArrayList<>();
            for (Integer e : RowsetUtils.typesNames.keySet()) {
                try {
                    if (model.isTypeSupported(e)) {
                        typeIndexesList.add(e);
                    }
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
            types = new int[typeIndexesList.size()];
            int i = 0;
            for (Integer e : typeIndexesList) {
                types[i++] = e.intValue();
            }
            typeNames = new String[types.length];
            for (int j = 0; j < typeNames.length; j++) {
                typeNames[j] = SQLUtils.getLocalizedTypeName(types[j]);
            }
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
