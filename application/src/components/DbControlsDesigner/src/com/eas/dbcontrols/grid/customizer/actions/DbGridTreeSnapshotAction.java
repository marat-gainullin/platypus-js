/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.customizer.actions;

import com.eas.dbcontrols.DesignIconCache;
import com.eas.dbcontrols.actions.DbControlChangeAction;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.grid.DbGridTreeDesignInfo;
import com.eas.dbcontrols.grid.customizer.DbGridCustomizer;
import com.eas.dbcontrols.grid.edits.DbGridTreeEdit;
import java.awt.event.ActionEvent;
import javax.swing.Action;

/**
 *
 * @author mg
 */
public abstract class DbGridTreeSnapshotAction extends DbControlChangeAction {

    protected DbGridCustomizer customizer;

    public DbGridTreeSnapshotAction(DbGridCustomizer aCustomizer) {
        super();
        customizer = aCustomizer;
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon(getIconName()));
    }

    protected String getIconName() {
        return "16x16/ellipsis.gif";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    protected DbGridTreeDesignInfo getInfo() {
        if (customizer.getDesignInfo() instanceof DbGridDesignInfo) {
            DbGridDesignInfo info = (DbGridDesignInfo) customizer.getDesignInfo();
            if (info != null) {
                DbGridTreeDesignInfo cellInfo = info.getTreeDesignInfo();
                return cellInfo;
            }
        }
        return null;
    }

    protected boolean isSingleQueryTree() {
        DbGridTreeDesignInfo info = getInfo();
        if (info != null) {
            return info.getTreeKind() == DbGridTreeDesignInfo.ONE_FIELD_ONE_QUERY_TREE_KIND;
        }
        return false;
    }

    protected boolean isAddQueriesTree() {
        DbGridTreeDesignInfo info = getInfo();
        if (info != null) {
            return info.getTreeKind() == DbGridTreeDesignInfo.FIELD_2_PARAMETER_TREE_KIND;
        }
        return false;
    }

    protected boolean isScriptTree() {
        DbGridTreeDesignInfo info = getInfo();
        if (info != null) {
            return info.getTreeKind() == DbGridTreeDesignInfo.SCRIPT_PARAMETERS_TREE_KIND;
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (customizer != null && isEnabled() && customizer.getDesignInfo() instanceof DbGridDesignInfo) {
            DbGridDesignInfo cinfo = (DbGridDesignInfo) customizer.getDesignInfo();
            DbGridTreeDesignInfo info = cinfo.getTreeDesignInfo();
            DbGridTreeDesignInfo before = (DbGridTreeDesignInfo) info.copy();
            DbGridTreeDesignInfo after = (DbGridTreeDesignInfo) info.copy();
            processChangedDesignInfo(after);
            if (!before.isEqual(after)) {
                DbGridTreeEdit edit = new DbGridTreeEdit(cinfo, before, after);
                edit.redo();
                customizer.getUndoSupport().postEdit(edit);
            }
        }
    }

    protected abstract void processChangedDesignInfo(DbGridTreeDesignInfo after);
}
