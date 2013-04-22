/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.output;

import com.eas.client.SQLUtils;
import com.eas.client.model.gui.view.FieldsTypeIconsCache;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.openide.ErrorManager;

/**
 *
 * @author mg
 */
public class TypeRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        try {
            if (value instanceof Integer) {
                int sqlType = ((Integer) value).intValue();
                Component rComp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (rComp instanceof JLabel) {
                    Icon icon = FieldsTypeIconsCache.getIcon16(sqlType);
                    ((JLabel) rComp).setIcon(icon);
                    String typeDisplayName = SQLUtils.getLocalizedTypeName(sqlType);
                    if (typeDisplayName != null) {
                        ((JLabel) rComp).setText(typeDisplayName);
                    }
                }
                return rComp;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
