/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.customizer.columnstree;

import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.DesignIconCache;
import com.eas.dbcontrols.grid.DbGridColumn;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author mg
 */
public class DbGridColumnsTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component lcomp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (lcomp != null && lcomp instanceof DefaultTreeCellRenderer && value != null && value instanceof DbGridColumn) {
            DefaultTreeCellRenderer dr = (DefaultTreeCellRenderer) lcomp;
            DbGridColumn col = (DbGridColumn) value;
            if (col.getBackground() != null) {
                dr.setBackground(col.getBackground());
            }
            String title = col.getTitle();
            if (col.getName() != null && !col.getName().isEmpty()) {
                if (title != null && !title.isEmpty()) {
                    title += " [" + col.getName() + "]";
                } else {
                    title = col.getName();
                }
            }
            if (title == null || title.isEmpty()) {
                title = "<>";
            }
            if (title != null) {
                dr.setText(title);
            }
            dr.setFont(DbControlsUtils.toNativeFont(col.getHeaderStyle().getFont()));
            Icon icon = null;
            if (col.getChildren() == null || col.getChildren().isEmpty()) {
                icon = DesignIconCache.getIcon("16x16/column.png");
            } else {
                icon = DesignIconCache.getIcon("16x16/columns.png");
            }
            dr.setIcon(icon);
            dr.setClosedIcon(icon);
            dr.setOpenIcon(icon);
            dr.setLeafIcon(icon);
        }
        return lcomp;
    }
}
