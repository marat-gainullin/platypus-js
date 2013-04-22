/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.designer.application.module.PlatypusModuleDataObject;
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
    public void applyCompletionItems(JsCompletionProvider.CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        fillEntities(dataObject, resultSet, point);
        fillJavaCompletionItems(point, resultSet);
    }

    @Override
    public CompletionContext getChildContext(String fieldName, int offset) throws Exception {
        ApplicationDbEntity entity = dataObject.getModel().getEntityByName(fieldName);
        if (entity != null) {
            return new EntityCompletionContext(entity);
        }
        return null;
    }
}
