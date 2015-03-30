/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationEntity;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 * A completion context for an data model entity row.
 * @author vv
 */
public class EntityElementCompletionContext extends CompletionContext {

    ApplicationDbEntity entity;

    public EntityElementCompletionContext(ApplicationDbEntity anEntity) {
        super(ApplicationEntity.class);
        entity = anEntity;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        fillFieldsValues(entity.getFields(), point, resultSet);
    }

    protected static void fillFieldsValues(Fields aFields, CompletionPoint point, CompletionResultSet resultSet) {
        if (aFields != null) {
            for (Field field : aFields.toCollection()) {
                addItem(resultSet, point.getFilter(), new FieldCompletionItem(field, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
            }
        } else {
            throw new IllegalStateException("ORM properties are unavailable");
        }
    }
}
