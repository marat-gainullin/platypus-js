/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.customizer;

import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.datamodel.ModelElementRef;
import com.eas.dbcontrols.DbControlsDesignUtils;
import com.eas.dbcontrols.FieldRefRenderer;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JList;

/**
 *
 * @author mg
 */
public class FieldsComboListCellRenderer extends FieldRefRenderer
{
    protected Font fieldsFont = null;
    protected ModelElementRef dmElement = null;
    protected DsParametersComboModel comboModel = null;

    public FieldsComboListCellRenderer(Font aFont, DsParametersComboModel aComboModel)
    {
        super();
        fieldsFont = aFont;
        comboModel = aComboModel;
        dmElement = new ModelElementRef();
        dmElement.setField(false);
        dmElement.setField(null);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        Component lcomp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(lcomp != null && lcomp instanceof FieldRefRenderer &&
           value != null && value instanceof Parameter)
        {
            FieldRefRenderer fr = (FieldRefRenderer)lcomp;
            Parameter pmd = (Parameter)value;
            if(comboModel != null)
            {
                dmElement.setEntityId(comboModel.getEntityID());
                dmElement.setField(pmd);
                DbControlsDesignUtils.setupDatamodelElementRenderer(comboModel.getDatamodel(), dmElement, fr, fieldsFont);
            }
        }
        return lcomp;
    }
}
