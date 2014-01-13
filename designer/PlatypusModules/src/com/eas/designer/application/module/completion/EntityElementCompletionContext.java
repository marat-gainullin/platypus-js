/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.script.ScriptableRowset;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class EntityElementCompletionContext extends CompletionContext {

    ApplicationDbEntity entity;
    
    public EntityElementCompletionContext(ApplicationDbEntity anEntity) {
        super(ScriptableRowset.class);
        entity = anEntity;
    }
    
    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        fillFieldsValues(entity.getFields(), point, resultSet);
    }
    
    protected static void fillFieldsValues(Fields aFields, CompletionPoint point, CompletionResultSet resultSet) {
        for (Field field : aFields.toCollection()) {
            addItem(resultSet, point.getFilter(), new FieldCompletionItem(field, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        }
    }
}
