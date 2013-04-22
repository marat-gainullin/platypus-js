/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.customizer.actions;

import com.eas.dbcontrols.actions.DbControlChangeAction;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.grid.customizer.DbGridCustomizer;
import com.eas.dbcontrols.grid.customizer.columnstree.DbGridColumnsTreeModel;
import com.eas.dbcontrols.grid.edits.DbGridHeaderStructureEdit;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author mg
 */
public abstract class DbGridColumnsStructureSnapshotAction extends DbControlChangeAction {

    protected DbGridCustomizer customizer;

    public DbGridColumnsStructureSnapshotAction(DbGridCustomizer aCustomizer) {
        super();
        customizer = aCustomizer;
        putValue(Action.NAME, getValue(Action.SHORT_DESCRIPTION));
        putValue(Action.SHORT_DESCRIPTION, null);
    }

    @Override
    public boolean isEnabled() {
        return customizer != null;
    }

    protected List<DbGridColumn> getInfo() {
        if (customizer.getDesignInfo() instanceof DbGridDesignInfo) {
            DbGridDesignInfo info = (DbGridDesignInfo) customizer.getDesignInfo();
            if (info != null) {
                return info.getHeader();
            }
        }
        return null;
    }

    protected DbGridColumn getProcessedColumn() {
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isEnabled()) {
            if (customizer != null) {
                DbGridDesignInfo cinfo = (DbGridDesignInfo) customizer.getDesignInfo();
                DbGridHeaderStructureEdit edit = new DbGridHeaderStructureEdit(customizer.getScriptHost(), cinfo);
                if (changeStructure(edit)) {
                    try {
                        customizer.treeColumns.clearSelection();
                        DbGridColumnsTreeModel tModel = (DbGridColumnsTreeModel) customizer.treeColumns.getModel();
                        cinfo.setHeader(tModel.getDummyRoot().getChildren());
                        edit.grabStructureAfter();
                        customizer.getUndoSupport().postEdit(edit);
                        cinfo.firePropertyChange(DbGridDesignInfo.HEADER, null, cinfo.getHeader());
                        fireStructureChanged();
                        edit.synchronizeEventsRedo();
                        customizer.treeColumns.requestFocus();
                        DbGridColumn col = getProcessedColumn();
                        if (col != null) {
                            setSelectedColumn(col);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(DbGridColumnsStructureSnapshotAction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    protected boolean isSingleSelectedNode() {
        if (customizer != null && customizer instanceof DbGridCustomizer) {
            DbGridCustomizer cust = (DbGridCustomizer) customizer;
            return (!cust.treeColumns.isSelectionEmpty() && cust.treeColumns.getSelectionCount() == 1);
        }
        return false;
    }

    protected DbGridColumn getSingleSelectedColumn() {
        if (isSingleSelectedNode()) {
            if (customizer instanceof DbGridCustomizer) {
                DbGridCustomizer cust = (DbGridCustomizer) customizer;
                return getSingleSelectedColumn(cust.treeColumns);
            }
        }
        return null;
    }

    protected void setSelectedColumn(DbGridColumn aCol) {
        if (customizer instanceof DbGridCustomizer) {
            DbGridCustomizer cust = (DbGridCustomizer) customizer;
            if (cust.treeColumns != null) {
                if (aCol != null) {
                    DbGridColumnSnapshotAction.makeColumnVisible(cust.treeColumns, aCol);
                } else {
                    cust.treeColumns.clearSelection();
                }
            }
        }
    }

    public static DbGridColumn getSingleSelectedColumn(JTree aTree) {
        if (aTree != null) {
            TreePath tp = aTree.getSelectionPath();
            if (tp != null) {
                Object oColumn = tp.getLastPathComponent();
                if (oColumn != null && oColumn instanceof DbGridColumn) {
                    return (DbGridColumn) oColumn;
                }
            }
        }
        return null;
    }

    protected void fireStructureChanged() {
        if (customizer != null && customizer instanceof DbGridCustomizer) {
            DbGridCustomizer cust = (DbGridCustomizer) customizer;
            TreeModel tm = cust.treeColumns.getModel();
            if (tm != null && tm instanceof DbGridColumnsTreeModel) {
                DbGridColumnsTreeModel gtm = (DbGridColumnsTreeModel) tm;
                gtm.fireStructureChanged();
            }
        }
    }

    protected abstract boolean changeStructure(DbGridHeaderStructureEdit aEdit);
}
