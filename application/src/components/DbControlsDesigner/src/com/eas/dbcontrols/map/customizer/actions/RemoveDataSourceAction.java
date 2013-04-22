/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.DesignIconCache;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import com.eas.dbcontrols.map.customizer.edits.ModifyFeaturesEdit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JList;

/**
 *
 * @author pk
 */
public class RemoveDataSourceAction extends DbMapChangeAction {

    public RemoveDataSourceAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/delete.png"));
    }

    private void checkSelection(List<RowsetFeatureDescriptor> aFeatures) {
        int selIndex = customizer.getFeaturesList().getSelectedIndex();
        int modelSize = aFeatures.size();
        if (modelSize > 0) {
            if (selIndex >= modelSize) {
                customizer.getFeaturesList().setSelectedIndex(modelSize-1);
            }
        } else {
            customizer.getFeaturesList().clearSelection();
        }
    }

    @Override
    public boolean isEnabled() {
        if (customizer != null) {
            final JList featuresList = ((DbMapCustomizer) customizer).getFeaturesList();
            final int[] selectedIndices = featuresList.getSelectedIndices();
            return selectedIndices != null && selectedIndices.length == 1;
        } else {
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (customizer != null && customizer.getDatamodel() != null && customizer instanceof DbMapCustomizer && customizer.getBean() instanceof DbMap) {
            final DbMap map = (DbMap) customizer.getBean();
            final Set<Integer> selectedIndices = buildIntegerSet(((DbMapCustomizer) customizer).getDataSourcesList().getSelectedIndices());
            if (selectedIndices != null && selectedIndices.size() > 0) {
                /*
                final DbMapDesignInfo infoAfter = new DbMapDesignInfo(), infoBefore = new DbMapDesignInfo();
                infoBefore.assign(map.getDesignInfo());
                infoAfter.assign(map.getDesignInfo());
                List<RowsetFeatureDescriptor> remainingFeatures = onlyRemainingFeatures(infoBefore.getFeatures(), selectedIndices);
                infoAfter.setFeatures(remainingFeatures);
                checkSelection(remainingFeatures);
                final DbControlChangeEdit edit = new DbControlChangeEdit(map.getDesignInfo(), infoBefore, infoAfter);
*/
                List<RowsetFeatureDescriptor> remainingFeatures = onlyRemainingFeatures(map.getDesignInfo().getFeatures(), selectedIndices);
                final ModifyFeaturesEdit edit = new ModifyFeaturesEdit(map.getDesignInfo(), map.getDesignInfo().getFeatures(), remainingFeatures);
                edit.redo();
                customizer.getUndoSupport().postEdit(edit);
            }
        }
    }

    private Set<Integer> buildIntegerSet(int[] selectedIndices) {
        final Set<Integer> result = new HashSet<>();
        for (int i = 0; i < selectedIndices.length; i++) {
            result.add(selectedIndices[i]);
        }
        return result;
    }

    private List<RowsetFeatureDescriptor> onlyRemainingFeatures(List<RowsetFeatureDescriptor> featuresBefore, Set<Integer> excludedIndices) {
        final List<RowsetFeatureDescriptor> remainingFeatures = new ArrayList<>(featuresBefore.size() - excludedIndices.size());
        for (int i = 0; i < featuresBefore.size(); i++) {
            if (!excludedIndices.contains(i)) {
                remainingFeatures.add(featuresBefore.get(i));
            }
        }
        return remainingFeatures;
    }
}
