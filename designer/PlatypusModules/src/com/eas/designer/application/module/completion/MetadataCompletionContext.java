/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.application.ApplicationDbEntity;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class MetadataCompletionContext extends CompletionContext {
    
    ApplicationDbEntity entity;
    
    public MetadataCompletionContext(ApplicationDbEntity anEntity) {
        super(null);
        entity = anEntity;
    }
    
    @Override
    public void applyCompletionItems(JsCompletionProvider.CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        fillFieldsValues(entity.getFields(), point, resultSet);
    }
    
    @Override
    public CompletionContext getChildContext(String fieldName, int offset) throws Exception {
        return new CompletionContext(Field.class);
    }
    
}
