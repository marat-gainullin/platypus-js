/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameters;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationEntity;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import static com.eas.designer.application.module.completion.CompletionContext.isPropertyGet;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import static com.eas.designer.application.module.completion.ModuleCompletionContext.METADATA_SCRIPT_NAME;
import static com.eas.designer.application.module.completion.ModuleCompletionContext.PARAMS_SCRIPT_NAME;
import jdk.nashorn.internal.ir.IndexNode;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 * A completion context for a data model entity.
 * @author vv
 */
public class EntityCompletionContext extends CompletionContext {

    protected static final String CURSOR_ENTITY_PROPERTY_NAME = "cursor";//NOI18N
    ApplicationDbEntity entity;

    public EntityCompletionContext(ApplicationDbEntity anEntity) {
        super(ApplicationEntity.class);
        entity = anEntity;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        if (getScriptClass() != null) {
            fillJavaCompletionItems(point, resultSet);
        }
        addItem(resultSet, point.getFilter(), new BeanCompletionItem(Parameters.class, PARAMS_SCRIPT_NAME, null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        addItem(resultSet, point.getFilter(), new BeanCompletionItem(Fields.class, METADATA_SCRIPT_NAME, null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        if (isPropertyGet(token, METADATA_SCRIPT_NAME)) {
            return new MetadataCompletionContext(entity.getFields());
        } else if(isPropertyGet(token, CURSOR_ENTITY_PROPERTY_NAME)) {
            return getElementCompletionContext();
        } else if (token.node instanceof IndexNode && ( ((IndexNode)token.node).getIndex().getType() == null || !((IndexNode)token.node).getIndex().getType().isString())) { 
            return new EntityElementCompletionContext(entity);
        }else if (isPropertyGet(token, PARAMS_SCRIPT_NAME)) {
            return new ParametersCompletionContext(entity.getQuery());
        } else {
            return null;
        }
    }
    
    public CompletionContext getElementCompletionContext() {
        return new EntityElementCompletionContext(entity);
    }
}
