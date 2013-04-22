/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.customizer.actions;

import com.eas.dbcontrols.DesignIconCache;
import com.eas.dbcontrols.actions.DbControlChangeAction;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.grid.DbGridRowsColumnsDesignInfo;
import com.eas.dbcontrols.grid.customizer.DbGridCustomizer;
import com.eas.dbcontrols.grid.customizer.columnstree.DbGridColumnsTreeModel;
import com.eas.dbcontrols.grid.edits.DbColumnChangeEdit;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.Action;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author mg
 */
public abstract class DbGridColumnSnapshotAction extends DbControlChangeAction {

    public static void makeColumnVisible(JTree aTree, DbGridColumn aCol) {
        if (aTree != null) {
            TreePath lPath = DbGridColumnsTreeModel.constructTreePath2Node(((DbGridColumnsTreeModel) aTree.getModel()).getDummyRoot(), aCol);
            if (lPath != null) {
                aTree.clearSelection();
                if (existPath(aTree, lPath)) {
                    TreePath parentPath = lPath.getParentPath();
                    if (parentPath != null) {
                        aTree.expandPath(parentPath);
                    }
                    aTree.setSelectionPath(lPath);
                    aTree.makeVisible(lPath);
                    aTree.scrollPathToVisible(lPath);
                }
            }
        }
    }

    private static boolean existPath(JTree aTree, TreePath lPath) {
        if (aTree != null && lPath != null) {
            Object[] lpath = lPath.getPath();
            TreeModel tm = aTree.getModel();
            if (lpath != null && tm != null) {
                Object lParent = tm.getRoot();
                if (lpath.length > 0 && lpath[0] == lParent) {
                    for (int i = 1; i < lpath.length; i++) {
                        Object lnode = lpath[i];
                        if (tm.getIndexOfChild(lParent, lnode) == -1) {
                            return false;
                        } else {
                            lParent = lnode;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isNamePresent(String aName) {
        if (customizer.getDesignInfo() instanceof DbGridDesignInfo) {
            DbGridDesignInfo info = (DbGridDesignInfo) customizer.getDesignInfo();
            List<DbGridColumn> cols = info.getHeader();
            if (cols != null) {
                DbGridColumn currentCol = getInfo();
                return isNamePresent(aName, cols, currentCol);
            }
        }
        return false;
    }

    protected boolean isNamePresent(String aName, List<DbGridColumn> aCols, DbGridColumn col2Exclude) {
        if (aCols != null && aName != null && !aName.isEmpty()) {
            for (int i = 0; i < aCols.size(); i++) {
                DbGridColumn col = aCols.get(i);
                if (col != null) {
                    if (col != col2Exclude) {
                        String colName = col.getName();
                        if (colName != null && !colName.isEmpty()) {
                            if (colName.toLowerCase().equals(aName.toLowerCase())) {
                                return true;
                            }
                        }
                    }
                    if (isNamePresent(aName, col.getChildren(), col2Exclude)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    protected DbGridCustomizer customizer;

    public DbGridColumnSnapshotAction(DbGridCustomizer aCustomizer) {
        super();
        customizer = aCustomizer;
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon(getIconName()));
    }

    protected String getIconName() {
        return "16x16/ellipsis.gif";
    }

    protected DbGridColumn getInfo() {
        if (customizer != null && customizer instanceof DbGridCustomizer) {
            DbGridCustomizer cust = (DbGridCustomizer) customizer;
            if (cust.treeColumns != null) {
                return DbGridColumnsStructureSnapshotAction.getSingleSelectedColumn(cust.treeColumns);
            }
        }
        return null;
    }

    protected DbGridRowsColumnsDesignInfo getRcInfo() {
        if (customizer.getDesignInfo() instanceof DbGridDesignInfo) {
            DbGridDesignInfo info = (DbGridDesignInfo) customizer.getDesignInfo();
            if (info != null) {
                DbGridRowsColumnsDesignInfo rcInfo = info.getRowsColumnsDesignInfo();
                return rcInfo;
            }
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (customizer != null && isEnabled()) {
            if (customizer != null && customizer instanceof DbGridCustomizer) {
                DbGridCustomizer cust = (DbGridCustomizer) customizer;
                if (cust.treeColumns != null) {
                    DbGridColumn column = DbGridColumnsStructureSnapshotAction.getSingleSelectedColumn(cust.treeColumns);
                    if (column != null) {
                        DbGridColumn before = column.lightCopy();
                        DbGridColumn after = column.lightCopy();
                        processChangedDesignInfo(after);
                        if (!before.lightIsEqual(after)) {
                            DbColumnChangeEdit edit = new DbColumnChangeEdit(cust.getScriptHost(), column, before, after);
                            edit.redo();
                            customizer.getUndoSupport().postEdit(edit);
                        }
                    }
                }
            }
        }
    }

    protected abstract void processChangedDesignInfo(DbGridColumn after);
}
