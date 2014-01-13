/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.script.ScriptableRowset;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import static com.eas.designer.application.module.completion.CompletionContext.isPropertyGet;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionTokenType;
import static com.eas.designer.application.module.completion.ModuleCompletionContext.METADATA_SCRIPT_NAME;
import static com.eas.designer.application.module.completion.ModuleCompletionContext.PARAMS_SCRIPT_NAME;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class EntityCompletionContext extends CompletionContext {

    protected static final String CURSOR_ENTITY_PROPERTY_NAME = "cursor";//NOI18N
    ApplicationDbEntity entity;

    public EntityCompletionContext(ApplicationDbEntity anEntity) {
        super(ScriptableRowset.class);
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
        } else if (CompletionTokenType.ELEMENT_GET == token.type && !isQuotedString(token.name)) { 
            return new EntityElementCompletionContext(entity);
        }else if (isPropertyGet(token, PARAMS_SCRIPT_NAME)) {
            return new ParametersCompletionContext(entity.getQuery().getParameters());
        } else {
            return null;
        }
    }
    
    public CompletionContext getElementCompletionContext() {
        return new EntityElementCompletionContext(entity);
    }
}
