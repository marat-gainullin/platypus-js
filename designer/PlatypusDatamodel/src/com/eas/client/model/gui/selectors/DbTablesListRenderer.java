/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.metadata.Fields;
import com.eas.client.model.gui.IconCache;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author mg
 */
public class DbTablesListRenderer extends DefaultListCellRenderer {

    public DbTablesListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value != null && value instanceof String) {
            String sValue = (String) value;
            if (list.getModel() instanceof DbTablesListModel) {
                int dotIdx = sValue.indexOf(".");
                if (dotIdx != -1) {
                    String ownerName = sValue.substring(0, dotIdx);
                    String tableName = sValue.substring(dotIdx + 1);
                    String cachedName = tableName;
                    value = tableName; 
                    DbTablesListModel model = (DbTablesListModel) list.getModel();
                    try {
                        if (model.getMdCache().getDatasourceSchema() == null || !model.getMdCache().getDatasourceSchema().equalsIgnoreCase(ownerName)) {
                            cachedName = ownerName + "." + tableName;
                        }
                        if (model.getMdCache().containsTableMetadata(cachedName)) {
                            Fields fields = model.getMdCache().getTableMetadata(cachedName);
                            String tDesc = fields.getTableDescription();
                            if (tDesc != null && !tDesc.isEmpty()) {
                                value = (String) value + " ( " + tDesc + " )";
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(DbTablesListRenderer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        Component rComp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (rComp instanceof JLabel) {
            JLabel lbl = (JLabel) rComp;
            lbl.setIcon(IconCache.getIcon("table.png"));
        }
        return rComp;
    }
}
