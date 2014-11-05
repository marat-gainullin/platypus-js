/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.veers;

import com.bearsoft.gui.grid.columns.TableColumnHandler;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.application.ApplicationModel;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.ScalarDbControl;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.rt.DummyCellEditor;
import com.eas.dbcontrols.grid.rt.columns.model.RowModelColumn;
import com.eas.dbcontrols.visitors.DbSwingFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumn;

/**
 *
 * @author mg
 */
public class VeerColumnsHandler implements TableColumnHandler {

    protected ApplicationModel<?, ?> model;
    protected DbGridColumn designColumn;

    public VeerColumnsHandler(ApplicationModel<?, ?> aModel, DbGridColumn aDesignInfo) {
        super();
        model = aModel;
        designColumn = aDesignInfo;
    }

    @Override
    public void handle(TableColumn tc) {
        try {
            DbControlDesignInfo controldi = designColumn.getCellDesignInfo().getCellControlInfo();
            assert controldi != null;
            DbSwingFactory rendererFactory = new DbSwingFactory();
            controldi.accept(rendererFactory);
            assert rendererFactory.getComp() instanceof ScalarDbControl;
            ScalarDbControl rendererControl = (ScalarDbControl) rendererFactory.getComp();
            rendererControl.setStandalone(false);
            rendererControl.setBorderless(true);
            rendererControl.setModel(model);
            tc.setCellRenderer(rendererControl);
            if (tc.getIdentifier() instanceof RowModelColumn) {
                RowModelColumn rCol = (RowModelColumn) tc.getIdentifier();
                rCol.setView(rendererControl);
            }
            if (designColumn.isEditable()) {
                DbSwingFactory editorFactory = new DbSwingFactory();
                controldi.accept(editorFactory);
                assert editorFactory.getComp() instanceof ScalarDbControl;
                ScalarDbControl editorControl = (ScalarDbControl) editorFactory.getComp();
                editorControl.setStandalone(false);
                editorControl.setBorderless(true);
                Field field = DbControlsUtils.resolveField(model, designColumn.getCellDesignInfo().getCellValueField());
                editorControl.setModel(model);
                editorControl.extraCellControls(null, field.isNullable());
                tc.setCellEditor(editorControl);
                if (tc.getIdentifier() instanceof RowModelColumn) {
                    RowModelColumn rCol = (RowModelColumn) tc.getIdentifier();
                    rCol.setEditor(editorControl);
                }
            } else {
                tc.setCellEditor(new DummyCellEditor());
            }
        } catch (Exception ex) {
            Logger.getLogger(VeerColumnsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
