/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.customizer.columnstree;

import com.eas.dbcontrols.grid.DbGridColumn;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author mg
 */
public class DbGridColumnsTreeEditor extends DefaultTreeCellEditor
{
    public DbGridColumnsTreeEditor(JTree tree, DefaultTreeCellRenderer renderer)
    {
        super(tree, renderer);
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
    {
        if(renderer != null)
            renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
        if(value != null && value instanceof DbGridColumn)
        {
            DbGridColumn col = (DbGridColumn)value;
            value = col.getTitle();
        }
        return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
    }

    @Override
    public boolean isCellEditable(EventObject event)
    {
        return true;
    }

    
}
