/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.model;

import com.eas.client.metadata.TableRef;
import com.eas.client.model.gui.selectors.AppElementSelectorCallback;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.AddQueryAction;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.entities.QueryEntityView;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.client.model.store.XmlDom2QueryModel;
import javax.swing.Action;
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
        tr.dbId = model.getDbId();
        return tr;
    }

    @Override
    protected QueryModel transformDocToModel(Document aDoc) throws Exception {
        return XmlDom2QueryModel.transform(model.getClient(), aDoc);
    }

    @Override
    protected boolean isParametersEntity(QueryEntity aEntity) {
        return aEntity instanceof QueryParametersEntity;
    }

    @Override
    protected EntityView<QueryEntity> createGenericEntityView(QueryEntity aEntity) {
        return new QueryEntityView(aEntity, entitiesViewsMover);
    }

    @Override
    protected boolean isPasteable(QueryEntity aEntityToPaste) {
        return !(aEntityToPaste instanceof QueryParametersEntity);
    }

    @Override
    protected void prepareEntityForPaste(QueryEntity aEntity) {
        if (model.getEntityById(aEntity.getEntityID()) != null) {
            aEntity.regenerateID();
        }
        if (aEntity.getTableSchemaName() != null && aEntity.getTableSchemaName().isEmpty()) {
            aEntity.setTableSchemaName(null);
        }
    }

    @Override
    protected QueryModel newModelInstance() {
        return new QueryModel(model.getClient());
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
