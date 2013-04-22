/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormDataObject;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormSupport;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualFormContainer;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.eas.client.forms.FormRunner;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.rt.columns.ScriptableColumn;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.BeanCompletionItem;
import com.eas.designer.application.module.completion.JsCompletionProvider;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author mg
 */
public class FormsCompletionProvider extends JsCompletionProvider {

    @Override
    protected void fillCompletionPoint(PlatypusModuleDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet, Document doc, int caretOffset) throws Exception {
        if (dataObject instanceof PlatypusFormDataObject) {
            PlatypusFormDataObject formDataObject = (PlatypusFormDataObject) dataObject;
            PlatypusFormSupport support = formDataObject.getLookup().lookup(PlatypusFormSupport.class);
            if (support.loadForm()) {
                FormModel fm = support.getFormModel();
                assert fm != null;
                if (point.context.length == 0) {
                    fillTopLevel(fm, point, resultSet);
                } else if (point.context.length == 1) {
                    fillFirstLevel(fm, point, resultSet);
                } else if (point.context.length == 2) {// <gridName>.<colName>.
                    RADComponent<?> comp = compByName(fm, point.context[0]);
                    if (comp != null && comp instanceof RADModelGrid) {
                        DbGrid modelGrid = ((RADModelGrid) comp).getBeanInstance();
                        List<DbGridColumn> linearColumns = new ArrayList<>();
                        enumerateColumns(modelGrid.getHeader(), linearColumns);
                        DbGridColumn targetCol = null;
                        for (DbGridColumn col : linearColumns) {
                            if (col.getName() != null && !col.getName().isEmpty() && col.getName().equals(point.context[1])) {
                                targetCol = col;
                            }
                        }
                        if (targetCol != null) {
                            fillJavaEntities(ScriptableColumn.class, point, resultSet);
                        }
                    }
                }
            }
        }
    }

    protected void fillFirstLevel(FormModel fm, CompletionPoint point, CompletionResultSet resultSet) {
        // <comp>.
        RADComponent<?> comp = compByName(fm, point.context[0]);
        if (comp != null) {
            Class<?> platypusControlClass = FormUtils.getPlatypusControlClass(comp.getBeanClass()); 
            fillJavaEntities(platypusControlClass, point, resultSet);
            if (comp instanceof RADModelGrid) {
                DbGrid dbGrid = ((RADModelGrid) comp).getBeanInstance();
                List<DbGridColumn> linearColumns = new ArrayList<>();
                enumerateColumns(dbGrid.getHeader(), linearColumns);
                fillColumns(linearColumns, resultSet, point);
            }
        } else if (FormRunner.VIEW_SCRIPT_NAME.equals(point.context[0])) {
            fillJavaEntities(Container.class, point, resultSet);
        }
    }

    protected void enumerateColumns(List<DbGridColumn> columns, List<DbGridColumn> res) {
        for (DbGridColumn dCol : columns) {
            res.add(dCol);
            if (dCol.hasChildren()) {
                enumerateColumns(dCol.getChildren(), res);
            }
        }
    }

    protected void fillColumns(List<DbGridColumn> columns, CompletionResultSet resultSet, CompletionPoint point) {
        for (DbGridColumn dCol : columns) {
            if (dCol.getName() != null && !dCol.getName().isEmpty()) {
                addItem(resultSet, point.filter, new BeanCompletionItem(dCol.getClass(), dCol.getName(), null, point.caretBeginWordOffset, point.caretEndWordOffset));
            }
        }
    }

    protected void fillTopLevel(FormModel fm, CompletionPoint point, CompletionResultSet resultSet) {
        for (RADComponent<?> comp : fm.getOrderedComponentList()) {
            if (comp.getName() != null && !comp.getName().isEmpty()) {
                // <comp>
                if (point.filter == null || point.filter.isEmpty() || comp.getName().toLowerCase().startsWith(point.filter.toLowerCase())) {
                    String compName = comp.getName();
                    if (RADVisualFormContainer.FORM_NAME.equals(compName)) {
                        continue;
                    }
                    addItem(resultSet, point.filter, new BeanCompletionItem(FormUtils.getPlatypusControlClass(comp.getBeanClass()), compName, null, point.caretBeginWordOffset, point.caretEndWordOffset));
                }
            }
        }
        addItem(resultSet, point.filter, new BeanCompletionItem(Container.class, "view", null, point.caretBeginWordOffset, point.caretEndWordOffset));
    }

    protected RADComponent<?> compByName(FormModel aModel, String aName) {
        RADComponent<?> targetComp = null;
        for (RADComponent<?> comp : aModel.getOrderedComponentList()) {
            if (comp.getName() != null && !comp.getName().isEmpty() && comp.getName().equals(aName)) {
                targetComp = comp;
            }
        }
        return targetComp;
    }
}
