/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import org.netbeans.spi.editor.completion.CompletionResultSet;
import com.eas.client.SqlQuery;
import com.eas.client.metadata.Parameters;

/**
 * This class represents a data model or entity parameters completion context.
 *
 * @author vv
 */
public class ParametersCompletionContext extends CompletionContext {

    SqlQuery query;

    public ParametersCompletionContext(SqlQuery aQuery) {
        super(null);
        query = aQuery;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        if (getScriptClass() != null) {
            fillJavaCompletionItems(point, resultSet);
        }
        if (query != null) {
            EntityElementCompletionContext.fillFieldsValues(query.getParameters(), point, resultSet);
        } else {
            addItem(resultSet, point.getFilter(), new BeanCompletionItem(Object.class, "model is not connected", null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        }
        addItem(resultSet, point.getFilter(), new BeanCompletionItem(Parameters.class, ModuleCompletionContext.METADATA_SCRIPT_NAME, null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
    }

    @Override
    public CompletionContext getChildContext(CompletionPoint.CompletionToken token, int offset) throws Exception {
        if (query != null && isPropertyGet(token, ModuleCompletionContext.METADATA_SCRIPT_NAME)) {
            return new MetadataCompletionContext(query.getParameters());
        } else {
            return null;
        }
    }
}
