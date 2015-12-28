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
import com.eas.util.PropertiesUtils;
import jdk.nashorn.api.scripting.JSObject;
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
        addItem(resultSet, point.getFilter(), new BeanCompletionItem(Parameters.class, ModuleCompletionContext.PARAMS_SCRIPT_NAME, null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        addItem(resultSet, point.getFilter(), new BeanCompletionItem(Fields.class, ModuleCompletionContext.METADATA_SCRIPT_NAME, null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        PropertiesUtils.PropBox cursorProp = new PropertiesUtils.PropBox();
        cursorProp.name = "cursor";
        cursorProp.jsDoc = ""
                + "/**\n"
                + " * Cursor property is one of entity's data array elements.\n"
                + " * cursor is used in grid as 'current' row and by model's requery links as parameters' source.\n"
                + " */";
        cursorProp.typeName = JSObject.class.getSimpleName();
        addItem(resultSet, point.getFilter(), new PropertyCompletionItem(cursorProp, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        
        PropertiesUtils.PropBox onScrolledProp = new PropertiesUtils.PropBox();
        onScrolledProp.name = "onScrolled";
        onScrolledProp.jsDoc = ""
                + "/**\n"
                + " * The handler function for the 'cursor' property has been changed (i.e. entity has been scrolled).\n"
                + " */";
        onScrolledProp.typeName = JSObject.class.getSimpleName();
        addItem(resultSet, point.getFilter(), new PropertyCompletionItem(onScrolledProp, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));

        PropertiesUtils.PropBox onInsertedProp = new PropertiesUtils.PropBox();
        onInsertedProp.name = "onInserted";
        onInsertedProp.jsDoc = ""
                + "/**\n"
                + " * The handler function for an element has been appended to entity's data array.\n"
                + " */";
        onInsertedProp.typeName = JSObject.class.getSimpleName();
        addItem(resultSet, point.getFilter(), new PropertyCompletionItem(onInsertedProp, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));

        PropertiesUtils.PropBox onDeletedProp = new PropertiesUtils.PropBox();
        onDeletedProp.name = "onDeleted";
        onDeletedProp.jsDoc = ""
                + "/**\n"
                + " * The handler function for an element has been removed from entity's data array.\n"
                + " */";
        onDeletedProp.typeName = JSObject.class.getSimpleName();
        addItem(resultSet, point.getFilter(), new PropertyCompletionItem(onDeletedProp, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        ModuleCompletionContext.ARRAY_ITERATION_FUNCTIONS_NAMES.forEach((String aArrayMethod)->{
            addItem(resultSet, point.getFilter(), new JsFunctionCompletionItem(aArrayMethod, null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        });
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        if (isPropertyGet(token, ModuleCompletionContext.METADATA_SCRIPT_NAME)) {
            return new MetadataCompletionContext(entity.getFields());
        } else if(isPropertyGet(token, CURSOR_ENTITY_PROPERTY_NAME)) {
            return getElementCompletionContext();
        } else if (token.node instanceof IndexNode && ( ((IndexNode)token.node).getIndex().getType() == null || !((IndexNode)token.node).getIndex().getType().isString())) { 
            return new EntityElementCompletionContext(entity);
        }else if (isPropertyGet(token, ModuleCompletionContext.PARAMS_SCRIPT_NAME)) {
            return new ParametersCompletionContext(entity.getQuery());
        } else {
            return null;
        }
    }
    
    public CompletionContext getElementCompletionContext() {
        return new EntityElementCompletionContext(entity);
    }
}
