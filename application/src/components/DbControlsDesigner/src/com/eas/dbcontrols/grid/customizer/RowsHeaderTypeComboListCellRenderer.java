/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.customizer;

import com.eas.dbcontrols.DbControlsDesignUtils;
import com.eas.dbcontrols.grid.DbGridRowsColumnsDesignInfo;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author mg
 */
public class RowsHeaderTypeComboListCellRenderer extends DefaultListCellRenderer{

    public RowsHeaderTypeComboListCellRenderer()
    {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        Component lcomp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(lcomp instanceof DefaultListCellRenderer && value != null && value instanceof Integer)
        {
            DefaultListCellRenderer lbl = (DefaultListCellRenderer)lcomp;
            String lText;
            int type = (Integer)value;
            switch(type)
            {
                case DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_NONE:
                    lText = DbControlsDesignUtils.getLocalizedString("ROWS_HEADER_TYPE_NONE");
                break;
                case DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_USUAL:
                    lText = DbControlsDesignUtils.getLocalizedString("ROWS_HEADER_TYPE_USUAL");
                break;
                case DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_CHECKBOX:
                    lText = DbControlsDesignUtils.getLocalizedString("ROWS_HEADER_TYPE_CHECKBOX");
                break;
                case DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_RADIOBUTTON:
                    lText = DbControlsDesignUtils.getLocalizedString("ROWS_HEADER_TYPE_RADIOBUTTON");
                break;
                default:
                    lText = value.toString();
            }
            lbl.setText(lText);
        }
        return lcomp;
    }
}
