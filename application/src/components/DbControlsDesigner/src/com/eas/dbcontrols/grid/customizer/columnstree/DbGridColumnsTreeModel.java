/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.customizer.columnstree;

import com.eas.dbcontrols.ScriptEvents;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.edits.DbColumnChangeEdit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author mg
 */
public class DbGridColumnsTreeModel extends Object implements TreeModel {

    protected DbGridColumn dummyRoot = new DummyRootDbGridColumn();
    protected Set<TreeModelListener> listeners = new HashSet<>();
    protected UndoableEditSupport undoSupport = null;

    public DbGridColumnsTreeModel() {
        super();
    }
    protected ScriptEvents scriptEvents;

    public ScriptEvents getScriptEvents() {
        return scriptEvents;
    }

    public void setScriptEvents(ScriptEvents aScriptEvents) {
        scriptEvents = aScriptEvents;
    }

    public void setRootChildren(List<DbGridColumn> aChildren) {
        dummyRoot.setChildren(aChildren);
    }

    public void setUndoSupport(UndoableEditSupport aUndoSupport) {
        undoSupport = aUndoSupport;
    }

    public void fireStructureChanged() {
        Iterator<TreeModelListener> tlIt = listeners.iterator();
        if (tlIt != null) {
            Object[] lpath = new Object[1];
            lpath[0] = dummyRoot;
            TreeModelEvent event = new TreeModelEvent(this, lpath);
            while (tlIt.hasNext()) {
                TreeModelListener tml = tlIt.next();
                tml.treeStructureChanged(event);
            }
        }
    }

    public void fireColumnChanged(DbGridColumn aCol) {
        if (aCol != null) {
            Iterator<TreeModelListener> tlIt = listeners.iterator();
            if (tlIt != null) {
                TreePath lpath = constructTreePath2Node(dummyRoot, aCol);
                TreeModelEvent event = new TreeModelEvent(this, lpath);
                while (tlIt.hasNext()) {
                    TreeModelListener tml = tlIt.next();
                    tml.treeNodesChanged(event);
                }
            }
        }
    }

    @Override
    public Object getRoot() {
        return dummyRoot;
    }

    public DbGridColumn getDummyRoot() {
        return dummyRoot;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent != null && parent instanceof DbGridColumn) {
            DbGridColumn cParent = (DbGridColumn) parent;
            List<DbGridColumn> cols = cParent.getChildren();
            if (cols != null && index >= 0 && index < cols.size()) {
                return cols.get(index);
            }
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent != null && parent instanceof DbGridColumn) {
            DbGridColumn cParent = (DbGridColumn) parent;
            List<DbGridColumn> cols = cParent.getChildren();
            if (cols != null) {
                return cols.size();
            }
        }
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        if (node != null && node instanceof DbGridColumn) {
            DbGridColumn cNode = (DbGridColumn) node;
            List<DbGridColumn> cols = cNode.getChildren();
            if (cols != null) {
                return cols.isEmpty();
            }
        }
        return true;
    }

    @Override
    public void valueForPathChanged(TreePath ep, Object newValue) {
        if (ep != null && undoSupport != null) {
            Object oEdited = ep.getLastPathComponent();
            if (oEdited != null && oEdited instanceof DbGridColumn) {
                DbGridColumn col = (DbGridColumn) oEdited;
                String newTitle = null;
                if (newValue != null && newValue instanceof String) {
                    newTitle = (String) newValue;
                    String oldTitle = col.getTitle();
                    if ((newTitle != null && !newTitle.equals(oldTitle)) || (newTitle == null && oldTitle != null)) {
                        DbGridColumn before = col.lightCopy();
                        DbGridColumn after = col.lightCopy();
                        after.setTitle(newTitle);
                        DbColumnChangeEdit edit = new DbColumnChangeEdit(scriptEvents, col, before, after);
                        edit.redo();
                        undoSupport.postEdit(edit);
                    }
                }
            }
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent != null && parent instanceof DbGridColumn) {
            DbGridColumn cParent = (DbGridColumn) parent;
            List<DbGridColumn> cols = cParent.getChildren();
            if (cols != null) {
                return cols.indexOf(child);
            }
        }
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    private static Object[] constructObjectPath2Node(DbGridColumn aDummyRoot, DbGridColumn aNode) {
        if (aNode != null) {
            List<DbGridColumn> vPath = new ArrayList<>();
            DbGridColumn lParent = aNode;
            while (lParent != null) {
                vPath.add(0, lParent);
                lParent = lParent.getParent();
            }
            if (vPath.isEmpty() || (vPath.get(0) != aDummyRoot && aDummyRoot.containsInChildren(vPath.get(0)))) {
                vPath.add(0, aDummyRoot);
            }
            return vPath.toArray();
        } else {
            return null;
        }
    }

    public static TreePath constructTreePath2Node(DbGridColumn aDummyRoot, DbGridColumn aNode) {
        Object[] oPath = constructObjectPath2Node(aDummyRoot, aNode);
        TreePath path = new TreePath(oPath);
        return path;
    }
}
