/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.concurrent.CallableConsumer;
import com.eas.designer.application.module.completion.BeanCompletionItem;
import com.eas.designer.application.module.completion.CompletionContext;
import com.eas.designer.application.module.completion.CompletionPoint;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import java.util.List;
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
        fillColumns(resultSet, point);
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        return walkColumns(grid.getHeader(), (GridColumnsNode node) -> {
            ModelColumn mCol = (ModelColumn) node.getTableColumn();
            if (mCol.getName() != null && !mCol.getName().isEmpty() && mCol.getName().equals(token.name)) {
                return new CompletionContext(node.getClass());
            }else
                return null;
        });
    }

    protected CompletionContext walkColumns(List<GridColumnsNode> aNodes, CallableConsumer<CompletionContext, GridColumnsNode> aWorker) throws Exception {
        CompletionContext ctx = null;
        for (int i = 0; i < aNodes.size(); i++) {
            GridColumnsNode node = aNodes.get(i);
            ctx = aWorker.call(node);
            if (ctx != null) {
                break;
            } else {
                ctx = walkColumns(node.getChildren(), aWorker);
                if (ctx != null) {
                    break;
                }
            }
        }
        return ctx;
    }

    protected void fillColumns(CompletionResultSet resultSet, CompletionPoint point) throws Exception {
        walkColumns(grid.getHeader(), (GridColumnsNode node) -> {
            ModelColumn mCol = (ModelColumn) node.getTableColumn();
            if (mCol.getName() != null && !mCol.getName().isEmpty()) {
                addItem(resultSet, point.getFilter(), new BeanCompletionItem(node.getClass(), mCol.getName(), null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
            }
            return null;
        });
    }
}
