package com.eas.dbcontrols;

import com.eas.client.forms.api.components.model.ScalarDbControl;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbControlRowsetListener extends RowsetAdapter {

    protected ScalarDbControl control;

    public DbControlRowsetListener(ScalarDbControl aControl) {
        super();
        control = aControl;
    }

    @Override
    public boolean willScroll(RowsetScrollEvent event) {
        if (control.getModel() != null && control.isFieldContentModified()) {
            try {
                control.setValue2Rowset(control.getCellEditorValue());
            } catch (Exception ex) {
                Logger.getLogger(DbControlRowsetListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        acceptValue();
    }

    protected void acceptValue() {
        if (control.getModel() != null) {
            control.beginUpdate();
            try {
                try {
                    control.setEditingValue(control.getValueFromRowset());
                } catch (Exception ex) {
                    Logger.getLogger(DbControlRowsetListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            } finally {
                control.endUpdate();
            }
        }
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        acceptValue();
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        acceptValue();
    }

    @Override
    public void rowsetSaved(RowsetSaveEvent event) {
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        acceptValue();
    }

    @Override
    public void rowsetScrolled(RowsetScrollEvent event) {
        acceptValue();
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
        acceptValue();
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        acceptValue();
    }

    @Override
    public void rowChanged(RowChangeEvent event) {
        if (control.getModel() != null) {
            if (control.getColIndex() == event.getFieldIndex()) {
                control.beginUpdate();
                try {
                    try {
                        control.setEditingValue(control.getValueFromRowset());
                    } catch (Exception ex) {
                        Logger.getLogger(DbControlRowsetListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } finally {
                    control.endUpdate();
                }
            }
        }
    }
}
