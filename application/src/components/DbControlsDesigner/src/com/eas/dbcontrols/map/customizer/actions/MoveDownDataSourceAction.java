/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import com.eas.dbcontrols.map.customizer.FeaturesListModel;
import com.eas.dbcontrols.map.customizer.edits.ModifyFeaturesEdit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;

/**
 *
 * @author pk
 */
public class MoveDownDataSourceAction extends DbMapChangeAction
{
    public MoveDownDataSourceAction(DbMapCustomizer aCustomizer)
    {
        super(aCustomizer);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        final JList featuresList = ((DbMapCustomizer) customizer).getFeaturesList();
        final DbMapDesignInfo designInfo = ((FeaturesListModel) featuresList.getModel()).getDesignInfo();
        final int[] selectedIndices = featuresList.getSelectedIndices();
        if (selectedIndices != null && selectedIndices.length > 0 && selectedIndices[selectedIndices.length - 1] < (designInfo.getFeatures().size() - 1))
        {
            final List<RowsetFeatureDescriptor> oldValue = designInfo.getFeatures(),
                    newValue = new ArrayList<>(oldValue.size());
            int selectedCounter = selectedIndices.length - 1;
            for (int i = oldValue.size() - 1; i >= 0; i--)
                if (selectedCounter >= 0 && i == selectedIndices[selectedCounter])
                {
                    newValue.add(1, oldValue.get(i));
                    selectedCounter--;
                }
                else
                    newValue.add(0, oldValue.get(i));
            final int[] newSelectedIndices = new int[selectedIndices.length];
            for (int i = 0; i < selectedIndices.length; i++)
                newSelectedIndices[i] = selectedIndices[i] + 1;
            final ModifyFeaturesEdit edit = new ModifyFeaturesEdit(designInfo, oldValue, newValue);
            designInfo.setFeatures(newValue);
            customizer.getUndoSupport().postEdit(edit);
            featuresList.requestFocus();
            featuresList.setSelectedIndices(newSelectedIndices);
        }
    }
}
