/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.model.Entity;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import java.util.Collection;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 * This class represents a data model completion context.
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
        fillEntities(dataObject.getModel().getEntities().values(), resultSet, point);
        fillJavaCompletionItems(point, resultSet);
    }

    @Override
    public CompletionContext getChildContext(CompletionPoint.CompletionToken token, int offset) throws Exception {
        ApplicationDbEntity entity = dataObject.getModel().getEntityByName(token.name);
        if (entity != null) {
            return new EntityCompletionContext(entity);
        }
        return null;
    }
    
    protected void fillEntities(Collection<? extends Entity> entities, CompletionResultSet resultSet, CompletionPoint point) throws Exception {
        for (Entity appEntity : entities) {
            if (appEntity.getName() != null && !appEntity.getName().isEmpty()) {
                addItem(resultSet, point.getFilter(), new BeanCompletionItem(ApplicationEntity.class, appEntity.getName(), null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
            }
        }
    }
}
