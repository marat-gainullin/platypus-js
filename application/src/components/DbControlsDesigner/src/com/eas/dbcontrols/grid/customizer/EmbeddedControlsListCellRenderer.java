/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.customizer;

import com.eas.dbcontrols.DbControlsDesignUtils;
import java.awt.Component;
import java.awt.Image;
import java.beans.BeanInfo;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

/**
 *
 * @author mg
 */
public class EmbeddedControlsListCellRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        Component lcomp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(lcomp != null && lcomp instanceof DefaultListCellRenderer &&
           value != null && value instanceof Class<?>)
        {
            Class<?> controlClass = (Class<?>)value;
            DefaultListCellRenderer dr = (DefaultListCellRenderer)lcomp;
            BeanInfo bi = DbControlsDesignUtils.getSampleBeanInfo(controlClass);
            if(bi != null)
            {
                Image im = bi.getIcon(BeanInfo.ICON_COLOR_16x16);
                if(im != null && im instanceof Icon)
                    dr.setIcon((Icon)im);
            }
            dr.setText(DbControlsDesignUtils.getLocalizedString(controlClass.getSimpleName()));
        }
        return lcomp;
    }
}
