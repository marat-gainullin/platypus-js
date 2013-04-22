/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.customizer.actions;

import com.eas.dbcontrols.DesignIconCache;
import com.eas.dbcontrols.actions.DbControlChangeAction;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.grid.DbGridRowsColumnsDesignInfo;
import com.eas.dbcontrols.grid.customizer.DbGridCustomizer;
import com.eas.dbcontrols.grid.edits.DbGridRowsColumnsEdit;
import java.awt.event.ActionEvent;
import javax.swing.Action;

/**
 *
 * @author mg
 */
public abstract class DbGridRowsColumnsSnapshotAction extends DbControlChangeAction {

    protected DbGridCustomizer customizer;

    public DbGridRowsColumnsSnapshotAction(DbGridCustomizer aCustomizer) {
        super();
        customizer = aCustomizer;
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon(getIconName()));
    }

    protected String getIconName() {
        return "16x16/ellipsis.gif";
    }

    protected DbGridRowsColumnsDesignInfo getInfo() {
        if (customizer.getDesignInfo() instanceof DbGridDesignInfo) {
            DbGridDesignInfo info = (DbGridDesignInfo) customizer.getDesignInfo();
            if (info != null) {
                DbGridRowsColumnsDesignInfo cellInfo = info.getRowsColumnsDesignInfo();
                return cellInfo;
            }
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (customizer != null && isEnabled() && customizer.getDesignInfo() instanceof DbGridDesignInfo) {
            DbGridDesignInfo cinfo = (DbGridDesignInfo) customizer.getDesignInfo();
            DbGridRowsColumnsDesignInfo info = cinfo.getRowsColumnsDesignInfo();
            DbGridRowsColumnsDesignInfo before = (DbGridRowsColumnsDesignInfo) info.copy();
            DbGridRowsColumnsDesignInfo after = (DbGridRowsColumnsDesignInfo) info.copy();
            processChangedDesignInfo(after);
            if (!before.isEqual(after)) {
                DbGridRowsColumnsEdit edit = new DbGridRowsColumnsEdit(customizer.getScriptHost(), cinfo, before, after);
                edit.redo();
                customizer.getUndoSupport().postEdit(edit);
            }
        }
    }

    protected abstract void processChangedDesignInfo(DbGridRowsColumnsDesignInfo after);
}
