/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.eas.client.forms.api.components.model.ModelGrid;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.rt.columns.ScriptableColumn;
import com.eas.designer.application.module.completion.BeanCompletionItem;
import com.eas.designer.application.module.completion.CompletionContext;
import com.eas.designer.application.module.completion.CompletionPoint;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class DbGridCompletionContext extends CompletionContext {

    DbGrid grid;

    public DbGridCompletionContext(DbGrid aDbGrid) {
        super(ModelGrid.class);
        grid = aDbGrid;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        super.applyCompletionItems(point, offset, resultSet);    
        List<DbGridColumn> linearColumns = new ArrayList<>();
        enumerateColumns(grid.getHeader(), linearColumns);
        fillColumns(linearColumns, resultSet, point);
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        List<DbGridColumn> linearColumns = new ArrayList<>();
        enumerateColumns(grid.getHeader(), linearColumns);
        DbGridColumn targetCol = null;
        for (DbGridColumn col : linearColumns) {
            if (col.getName() != null && !col.getName().isEmpty() && col.getName().equals(token.name)) {
                targetCol = col;
            }
        }
        if (targetCol != null) {
            return new CompletionContext(ScriptableColumn.class);
        }
        return null;
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
}
