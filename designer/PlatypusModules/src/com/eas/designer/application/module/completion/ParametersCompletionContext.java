/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class ParametersCompletionContext extends CompletionContext {

    Fields parameters;

    public ParametersCompletionContext(Fields aParameters) {
        super(Parameters.class);
        parameters = aParameters;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        if (scriptClass != null) {
            fillJavaCompletionItems(point, resultSet);
        }
        fillFieldsValues(parameters, point, resultSet);
        addItem(resultSet, point.filter, new BeanCompletionItem(Parameters.class, METADATA_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        if (METADATA_SCRIPT_NAME.equals(token)) {
            return new MetadataCompletionContext(parameters);
        } else {
            return null;
        }
    }
}
