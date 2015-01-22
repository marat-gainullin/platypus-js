/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.designer.application.module.completion.BeanCompletionItem;
import com.eas.designer.application.module.completion.CompletionContext;
import com.eas.designer.application.module.completion.CompletionPoint;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.table.TableColumn;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class ModelGridCompletionContext extends CompletionContext {

    ModelGrid grid;

    public ModelGridCompletionContext(ModelGrid aDbGrid) {
        super(ModelGrid.class);
        grid = aDbGrid;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        super.applyCompletionItems(point, offset, resultSet);    
        List<ModelColumn> linearColumns = new ArrayList<>();
        fillColumns(linearColumns, resultSet, point);
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        Enumeration<TableColumn> tCols = grid.getColumnModel().getColumns();
        while(tCols.hasMoreElements()){
            ModelColumn col = (ModelColumn)tCols.nextElement();
            if (col.getName() != null && !col.getName().isEmpty() && col.getName().equals(token.name)) {
            return new CompletionContext(ModelColumn.class);
            }
        }
        return null;
    }

    protected void fillColumns(List<ModelColumn> columns, CompletionResultSet resultSet, CompletionPoint point) {
        for (ModelColumn mCol : columns) {
            if (mCol.getName() != null && !mCol.getName().isEmpty()) {
                addItem(resultSet, point.getFilter(), new BeanCompletionItem(mCol.getClass(), mCol.getName(), null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
            }
        }
    }
}
