/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.designer.application.module.completion.CompletionPoint;
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
        fillFields(entity.getFields(), point, resultSet);
    }
}
