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
public class SelectFillColorAction extends SelectColorAction{

    public SelectFillColorAction(DbMapCustomizer aCustomizer)
    {
        super(aCustomizer);
    }

    @Override
    protected String getChooserTitle(RowsetFeatureDescriptor feature)
    {
        return "Select fill color...";
    }

    @Override
    protected Color getChooserInitialValue(RowsetFeatureDescriptor feature)
    {
        return feature.getStyle().getFillColor();
    }

    @Override
    protected void setSelectedValue(FeatureStyleDescriptor style, Color color)
    {
        style.setFillColor(color);
    }

}
