/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.model;

import com.eas.client.metadata.TableRef;
import com.eas.client.model.gui.edits.NewEntityEdit;
import com.eas.client.model.gui.selectors.AppElementSelectorCallback;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.AddQueryAction;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.entities.QueryEntityView;
import com.eas.client.model.gui.view.entities.QueryParametersEntityView;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.client.model.store.XmlDom2QueryModel;
import java.awt.Rectangle;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class QueryModelView extends ModelView<QueryEntity, QueryParametersEntity, QueryModel> {

    protected AppElementSelectorCallback appElementSelector;

    public QueryModelView(TablesSelectorCallback aSelectorCallback, AppElementSelectorCallback aAppElementSelector) {
        super(aSelectorCallback);
        appElementSelector = aAppElementSelector;
        putAddQueryAction();
    }

    public QueryModelView(QueryModel aModel, TablesSelectorCallback aSelectorCallback, AppElementSelectorCallback aAppElementSelector) {
        super(aModel, aSelectorCallback);
        appElementSelector = aAppElementSelector;
        putAddQueryAction();
    }

    @Override
    protected TableRef prepareTableRef4Selection() {
        TableRef tr = new TableRef();
        tr.dbId = model.getDatasourceName();
        return tr;
    }

    @Override
    protected QueryModel transformDocToModel(Document aDoc) throws Exception {
        return XmlDom2QueryModel.transform(model.getBasesProxy(), model.getQueries(), aDoc);
    }

    @Override
    protected boolean isParametersEntity(QueryEntity aEntity) {
        return aEntity instanceof QueryParametersEntity;
    }

    @Override
    protected EntityView<QueryEntity> createGenericEntityView(QueryEntity aEntity) throws Exception {
        if (isParametersEntity(aEntity)) {
            return new QueryParametersEntityView((QueryParametersEntity) aEntity, entitiesViewsMover);
        } else {
            if (aEntity.getQueryName() != null && !aEntity.getQueryName().isEmpty()) {
                model.getQueries().getQuery(aEntity.getQueryName(), null, null);
            }
            aEntity.validateQuery();
            return new QueryEntityView(aEntity, entitiesViewsMover);
        }
    }

    @Override
    public void doAddQuery(String aAppElementName, int aX, int aY) throws Exception {
        if (aAppElementName != null && model != null) {
            Rectangle rect = findPlaceForEntityAdd(aX, aY);
            QueryEntity entity = model.newGenericEntity();
            entity.setX(rect.x);
            entity.setY(rect.y);
            entity.setWidth(rect.width);
            entity.setHeight(rect.height);
            entity.setQueryName(aAppElementName);
            NewEntityEdit edit = new NewEntityEdit(model, entity);
            edit.redo();
            undoSupport.postEdit(edit);
        }
    }

    @Override
    protected boolean isPasteable(QueryEntity aEntity) {
        return !isParametersEntity(aEntity);
    }

    @Override
    protected void prepareEntityForPaste(QueryEntity aEntity) {
        if (model.getEntityById(aEntity.getEntityId()) != null) {
            aEntity.regenerateId();
        }
        findPlaceForEntityPaste(aEntity);
        if (aEntity.getTableSchemaName() != null && aEntity.getTableSchemaName().isEmpty()) {
            aEntity.setTableSchemaName(null);
        }
    }

    @Override
    protected QueryModel newModelInstance() {
        return new QueryModel(model.getBasesProxy(), model.getQueries());
    }

    protected final void putAddQueryAction() {
        getActionMap().put(AddQueryAction.class.getSimpleName(), new AddQueryAction(this, undoSupport, appElementSelector));
    }

    @Override
    protected boolean isAnyDeletableEntities() {
        if (isAnySelectedEntities()) {
            boolean res = false;
            for (QueryEntity sEntity : selectedEntities) {
                if (!(sEntity instanceof QueryParametersEntity)) {
                    return true;
                }
            }
            return res;
        } else {
            return false;
        }
    }

    @Override
    protected boolean isSelectedDeletableFields() {
        if (selectedFields != null && !selectedFields.isEmpty()) {
            for (EntityFieldTuple t : selectedFields) {
                if (!(t.entity instanceof QueryParametersEntity)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
