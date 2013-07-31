/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.eas.client.model.gui.DatamodelDesignUtils;
import java.sql.ParameterMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vv
 */
public class ParmeterModeEditor extends SelectIntEditor {

    private static int[] modes;
    private static String[] modeNames;

    private ParmeterModeEditor(String[] aKeys, int[] aValues) {
        super(aKeys, aValues);
    }

    public static synchronized ParmeterModeEditor getNewInstance() {
        if (modes == null) {
            assert modeNames == null;
            modes = new int[]{ParameterMetaData.parameterModeIn, ParameterMetaData.parameterModeOut, ParameterMetaData.parameterModeInOut, ParameterMetaData.parameterModeUnknown};
            String[] paramTypesStrKeys = { "in", "out", "inOut", "unknown" }; //NOI18N
            List<String> modeNamesList = new ArrayList<>();
            for (int k = 0; k < modes.length; k++) {
                modeNamesList.add(DatamodelDesignUtils.getLocalizedString(paramTypesStrKeys[k]));
            }
            modeNames = modeNamesList.toArray(new String[modes.length]);
        }
        return new ParmeterModeEditor(modeNames, modes);
    }
}
