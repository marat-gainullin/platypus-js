/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.model;

import com.eas.client.metadata.TableRef;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ApplicationDbParametersEntity;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.client.model.gui.selectors.AppElementSelectorCallback;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.AddQueryAction;
import com.eas.client.model.gui.view.entities.ApplicationEntityView;
import com.eas.client.model.gui.view.entities.ApplicationParametersEntityView;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import javax.swing.Action;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class ApplicationModelView extends ModelView<ApplicationDbEntity, ApplicationDbParametersEntity, ApplicationDbModel> {

    protected AppElementSelectorCallback appElementSelector;

    public ApplicationModelView(TablesSelectorCallback aSelectorCallback, AppElementSelectorCallback aAppElementSelector) {
        super(aSelectorCallback);
        appElementSelector = aAppElementSelector;
        putAddQueryAction();
    }

    public ApplicationModelView(TablesSelectorCallback aSelectorCallback, AppElementSelectorCallback aAppElementSelector, Action aEntityEditAction) {
        super(aSelectorCallback);
        appElementSelector = aAppElementSelector;
        putAddQueryAction();
    }

    public ApplicationModelView(ApplicationDbModel aModel, Action aEntityEditAction, TablesSelectorCallback aSelectorCallback, AppElementSelectorCallback aAppElementSelector) {
        super(aModel, aSelectorCallback);
        appElementSelector = aAppElementSelector;
        putAddQueryAction();
    }

    @Override
    protected ApplicationDbModel newModelInstance() {
        return new ApplicationDbModel(model.getClient());
    }

    protected final void putAddQueryAction() {
        getActionMap().put(AddQueryAction.class.getSimpleName(), new AddQueryAction(this, undoSupport, appElementSelector));
    }

    @Override
    protected TableRef prepareTableRef4Selection() {
        return new TableRef();
    }

    @Override
    protected ApplicationDbModel transformDocToModel(Document aDoc) throws Exception {
        ApplicationDbModel lmodel = newModelInstance();
        lmodel.accept(new XmlDom2ApplicationModel<ApplicationDbEntity>(aDoc));
        return lmodel;
    }

    @Override
    protected boolean isParametersEntity(ApplicationDbEntity aEntity) {
        return aEntity instanceof ApplicationParametersEntity;
    }

    @Override
    protected boolean isAnyDeletableEntities() {
        if (isAnySelectedEntities()) {
            boolean res = false;
            for (ApplicationDbEntity sEntity : selectedEntities) {
                if (!isParametersEntity(sEntity)) {
                    return true;
                }
            }
            return res;
        } else {
            return false;
        }
    }

    @Override
    protected EntityView<ApplicationDbEntity> createGenericEntityView(ApplicationDbEntity aEntity) {
        return isParametersEntity(aEntity) ? new ApplicationParametersEntityView((ApplicationDbParametersEntity) aEntity, entitiesViewsMover) : new ApplicationEntityView(aEntity, entitiesViewsMover);
    }

    @Override
    protected boolean isPasteable(ApplicationDbEntity aEntityToPaste) {
        return !isParametersEntity(aEntityToPaste);
    }

    @Override
    protected void prepareEntityForPaste(ApplicationDbEntity aEntity) {
        if (model.getEntityById(aEntity.getEntityId()) != null) {
            aEntity.regenerateId();
        }
        findPlaceForEntityPaste(aEntity);
    }

    @Override
    protected boolean isSelectedDeletableFields() {
        if (selectedFields != null && !selectedFields.isEmpty()) {
            for (EntityFieldTuple t : selectedFields) {
                if (!(t.entity instanceof ApplicationParametersEntity)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
