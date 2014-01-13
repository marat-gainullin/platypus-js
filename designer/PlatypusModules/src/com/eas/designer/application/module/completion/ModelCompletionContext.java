/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.model.Entity;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import static com.eas.designer.application.module.completion.ModuleCompletionContext.PARAMS_SCRIPT_NAME;
import java.util.Collection;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class ModelCompletionContext extends CompletionContext {

    PlatypusModuleDataObject dataObject;

    public ModelCompletionContext(PlatypusModuleDataObject aDataObject) {
        super(ApplicationModel.class);
        dataObject = aDataObject;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        addItem(resultSet, point.getFilter(), new BeanCompletionItem(dataObject.getModel().getParametersEntity().getRowset().getClass(), PARAMS_SCRIPT_NAME, null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        fillEntities(dataObject.getModel().getEntities().values(), resultSet, point);
        fillJavaCompletionItems(point, resultSet);
    }

    @Override
    public CompletionContext getChildContext(CompletionPoint.CompletionToken token, int offset) throws Exception {
        if (PARAMS_SCRIPT_NAME.equals(token.name)) {
            return new ParametersCompletionContext(dataObject.getModel().getParametersEntity().getFields());
        }
        ApplicationDbEntity entity = dataObject.getModel().getEntityByName(token.name);
        if (entity != null) {
            return new EntityCompletionContext(entity);
        }
        return null;
    }
    
    protected void fillEntities(Collection<? extends Entity> entities, CompletionResultSet resultSet, CompletionPoint point) throws Exception {
        for (Entity appEntity : entities) {
            if (appEntity.getName() != null && !appEntity.getName().isEmpty()) {
                addItem(resultSet, point.getFilter(), new BeanCompletionItem(ScriptableRowset.class, appEntity.getName(), null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
            }
        }
    }
}
