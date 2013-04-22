/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.geo.FeatureStyleDescriptor;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import java.awt.Color;

/**
 *
 * @author pk
 */
public class SelectHaloColorAction extends SelectColorAction {

    public SelectHaloColorAction(DbMapCustomizer aCustomizer)
    {
        super(aCustomizer);
    }

    @Override
    protected String getChooserTitle(RowsetFeatureDescriptor feature)
    {
        return "Select label halo color...";
    }

    @Override
    protected Color getChooserInitialValue(RowsetFeatureDescriptor feature)
    {
        return feature.getStyle().getHaloColor();
    }

    @Override
    protected void setSelectedValue(FeatureStyleDescriptor style, Color color)
    {
        style.setHaloColor(color);
    }


}
