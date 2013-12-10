/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.designer.application.module.completion.JsCompletionProvider.CompletionPoint;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class EntityCompletionContext extends CompletionContext {

    ApplicationDbEntity entity;

    public EntityCompletionContext(ApplicationDbEntity anEntity) {
        super(ScriptableRowset.class);
        entity = anEntity;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        if (scriptClass != null) {
            fillJavaCompletionItems(point, resultSet);
        }
        addItem(resultSet, point.filter, new BeanCompletionItem(entity.getFields().getClass(), METADATA_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
    }

    @Override
    public CompletionContext getChildContext(String fieldName, int offset) throws Exception {
        if (METADATA_SCRIPT_NAME.equalsIgnoreCase(fieldName)) {
            return new MetadataCompletionContext(entity);
        } else if (CURSOR_ENTITY_PROPERTY_NAME.equalsIgnoreCase(fieldName)) {
            return new EntityElementCompletionContext(entity);
        } else {
            return null;
        }
    }
}
