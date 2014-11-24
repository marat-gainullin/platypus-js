package com.eas.client.forms.api.components.model;

import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.controls.DummyControlValue;
import com.eas.dbcontrols.CellRenderEvent;
import com.eas.dbcontrols.DbControlRowsetListener;
import com.eas.dbcontrols.DummyHasPublished;
import com.eas.design.Designable;
import com.eas.gui.CascadedStyle;
import com.eas.script.ScriptUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author mg
 */
public abstract class DbControlPanel extends JPanel implements ScalarModelWidget {

    public DbControlPanel() {
        super();
    }

    @Override
    public void beginUpdate() {
        assert (updateCounter >= 0);
        updateCounter++;
    }

    @Override
    public boolean isUpdating() {
        return updateCounter > 0;
    }

    @Override
    public void endUpdate() {
        assert (updateCounter > 0);
        updateCounter--;
    }

    protected CascadedStyle styleValue;
    protected Object displayingValue;

    @Override
    public Object achiveDisplayValue(Object aValue) throws Exception {
        return aValue;
    }

    protected void acceptCellValue(Object aValue) throws Exception {
        if (standalone && published != null
                && onRender != null) {
            Object dataToProcess = aValue instanceof CellData ? ((CellData) aValue).data : aValue;
            final CellData cd = new CellData(new CascadedStyle(), dataToProcess, achiveDisplayValue(dataToProcess));
            Boolean handled;
            Row row = null;
            if (rsEntity != null && !rsEntity.getRowset().isBeforeFirst() && !rsEntity.getRowset().isAfterLast()) {
                row = rsEntity.getRowset().getCurrentRow();
            }
            Object[] rowIds = null;
            if (row != null) {
                rowIds = row.getPKValues();
            }
            CellRenderEvent event = new CellRenderEvent(new DummyHasPublished(published), rowIds != null && rowIds.length > 0 ? (rowIds.length > 1 ? rowIds : rowIds[0]) : null, null, cd, row);
            Object retValue = ScriptUtils.toJava(onRender.call(published, new Object[]{event.getPublished()}));
            if (retValue instanceof Boolean) {
                handled = (Boolean) retValue;
            } else {
                handled = false;
            }
            if (Boolean.TRUE.equals(handled)) {
                try {
                    cd.data = ScriptUtils.toJava(cd.data);
                    cd.display = ScriptUtils.toJava(cd.display);
                    aValue = cd;
                } catch (Exception ex) {
                    Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (aValue instanceof CellData) {
            CellData cd = (CellData) aValue;
            editingValue = cd.data;
            styleValue = cd.style;
            displayingValue = cd.display;
        } else {
            editingValue = aValue;
            styleValue = null;
            displayingValue = null;
        }
    }

    // datamodel interacting
    protected ModelElementRef datamodelElement;
    protected ApplicationModel<?, ?> model;
    protected ApplicationEntity<?, ?, ?> rsEntity;
    protected int colIndex;

    protected void bind() throws Exception {
        assert model != null;
        if (datamodelElement != null) {
            ApplicationEntity<?, ?, ?> entity = model.getEntityById(datamodelElement.getEntityId());
            if (entity != null && datamodelElement.getFieldName() != null && !datamodelElement.getFieldName().isEmpty()) {
                Rowset rowset = entity.getRowset();
                if (rowset != null) {
                    rsEntity = entity;
                    colIndex = rowset.getFields().find(datamodelElement.getFieldName());
                    if (rowsetListener != null) {
                        rowset.removeRowsetListener(rowsetListener);
                    }
                    rowsetListener = new DbControlRowsetListener(this);
                    rowset.addRowsetListener(rowsetListener);
                }
            }
        }
    }

    @Override
    public ApplicationEntity<?, ?, ?> getBaseEntity() {
        return rsEntity;
    }

    @Override
    public Object getValueFromRowset() throws Exception {
        if (rsEntity != null && rsEntity.getRowset() != null && colIndex > 0) {
            if (!rsEntity.getRowset().isBeforeFirst() && !rsEntity.getRowset().isAfterLast()) {
                Object value = rsEntity.getRowset().getObject(colIndex);
                /*
                if (value == null && datamodelElement != null) {
                    value = rsEntity.getSubstituteRowsetObject(datamodelElement.getFieldName());
                }
                */
                return value;
            }
        }
        return null;
    }

    @Override
    public boolean setValue2Rowset(Object aValue) throws Exception {
        if (!(aValue instanceof DummyControlValue) && !isUpdating() && rsEntity != null
                && rsEntity.getRowset() != null && colIndex > 0) {
            if (!rsEntity.getRowset().isBeforeFirst() && !rsEntity.getRowset().isAfterLast()) {
                try {
                    return rsEntity.getRowset().updateObject(colIndex, aValue);
                } catch (Exception ex) {
                    setEditingValue(rsEntity.getRowset().getObject(colIndex));
                }
            }
        }
        return false;
    }

    @Override
    public int getColIndex() {
        return colIndex;
    }

    @Override
    public ApplicationModel<?, ?> getModel() {
        return model;
    }

    @Override
    public void setModel(ApplicationModel<?, ?> aModel) throws Exception {
        ApplicationModel<?, ?> oldValue = model;
        if (model != aModel) {
            if (model != null) {
                unbind();
            }
            model = aModel;
            firePropertyChange("datamodel", oldValue, model);
        }
    }

    @Designable(displayName = "field", category = "model")
    @Override
    public ModelElementRef getDatamodelElement() {
        return datamodelElement;
    }

    @Override
    public void setDatamodelElement(ModelElementRef aValue) throws Exception {
        if (datamodelElement != aValue) {
            datamodelElement = aValue;
        }
    }
    
    protected void unbind() throws Exception {
        if (rsEntity != null && rowsetListener != null) {
            rsEntity.getRowset().removeRowsetListener(rowsetListener);
        }
    }

}
