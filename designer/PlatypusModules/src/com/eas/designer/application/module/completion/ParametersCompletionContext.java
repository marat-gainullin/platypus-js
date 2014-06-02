/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.model.application.ApplicationEntity;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import static com.eas.designer.application.module.completion.ModuleCompletionContext.METADATA_SCRIPT_NAME;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 * This class represents a data model or entity parameters completion context.
 * @author vv
 */
public class ParametersCompletionContext extends CompletionContext {

    Fields parameters;

    public ParametersCompletionContext(Fields aParameters) {
        super(ApplicationEntity.class);
        parameters = aParameters;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        if (getScriptClass() != null) {
            fillJavaCompletionItems(point, resultSet);
        }
        EntityElementCompletionContext.fillFieldsValues(parameters, point, resultSet);
        addItem(resultSet, point.getFilter(), new BeanCompletionItem(Parameters.class, METADATA_SCRIPT_NAME, null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        if (isPropertyGet(token, METADATA_SCRIPT_NAME)) {
            return new MetadataCompletionContext(parameters);
        } else {
            return null;
        }
    }
}
