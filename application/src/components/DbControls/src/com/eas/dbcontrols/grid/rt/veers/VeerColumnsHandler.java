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
import com.eas.script.ScriptUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumn;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class VeerColumnsHandler implements TableColumnHandler {

    protected ApplicationModel<?, ?, ?, ?> model;
    protected DbGridColumn designColumn;

    public VeerColumnsHandler(ApplicationModel<?, ?, ?, ?> aModel, DbGridColumn aDesignInfo) {
        super();
        model = aModel;
        designColumn = aDesignInfo;
    }

    protected Function getHandler(String aHandlerName) {
        if (aHandlerName != null && !aHandlerName.isEmpty() && model != null && model.getScriptThis() != null) {
            Object oHandlers = model.getScriptThis().get(ScriptUtils.HANDLERS_PROP_NAME, model.getScriptThis());
            if (oHandlers instanceof Scriptable) {
                Scriptable sHandlers = (Scriptable) oHandlers;
                Object oHandler = sHandlers.get(aHandlerName, sHandlers);
                if (oHandler instanceof Function) {
                    return (Function) oHandler;
                }
            }
        }
        return null;
    }

    @Override
    public void handle(TableColumn tc) {
        try {
            DbControlDesignInfo controldi = designColumn.getCellDesignInfo().getCellControlInfo();
            assert controldi != null;
            DbSwingFactory rendererFactory = new DbSwingFactory(null, null);
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
                DbSwingFactory editorFactory = new DbSwingFactory(null, null);
                controldi.accept(editorFactory);
                assert editorFactory.getComp() instanceof ScalarDbControl;
                ScalarDbControl editorControl = (ScalarDbControl) editorFactory.getComp();
                editorControl.setStandalone(false);
                editorControl.setBorderless(true);
                Field field = DbControlsUtils.resolveField(model, designColumn.getCellDesignInfo().getCellValueField());
                editorControl.setModel(model);
                editorControl.extraCellControls(getHandler(designColumn.getSelectFunction()), field.isNullable());
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
