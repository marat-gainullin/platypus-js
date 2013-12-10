/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class MetadataCompletionContext extends CompletionContext {
    
    Fields fields;
    
    public MetadataCompletionContext(Fields aFields) {
        super(null);
        fields = aFields;
    }
    
    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        fillFieldsValues(fields, point, resultSet);
    }
    
    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        return new CompletionContext(Field.class);
    }
    
}
