/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.model;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.model.gui.edits.AccessibleCompoundEdit;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.NewEntityEdit;
import com.eas.client.model.gui.edits.fields.DeleteFieldEdit;
import com.eas.client.model.gui.edits.fields.NewFieldEdit;
import com.eas.client.model.gui.selectors.AppElementSelectorCallback;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.AddQueryAction;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.entities.QueryEntityView;
import com.eas.client.model.gui.view.entities.QueryParametersEntityView;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.client.model.store.QueryModel2XmlDom;
import com.eas.client.model.store.XmlDom2QueryModel;
import com.eas.xml.dom.XmlDom2String;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.undo.CannotRedoException;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class QueryModelView extends ModelView<QueryEntity, QueryModel> {

    protected AppElementSelectorCallback appElementSelector;

    public QueryModelView(QueryModel aModel, TablesSelectorCallback aSelectorCallback, AppElementSelectorCallback aAppElementSelector) {
        super(aModel, aSelectorCallback);
        appElementSelector = aAppElementSelector;
        toParameterConnectorColor = toFieldConnectorColor;
        putAddQueryAction();
    }

    @Override
    protected void doCreateEntityViews() throws Exception {
        super.doCreateEntityViews();
        EntityView<QueryEntity> eView = createEntityView(model.getParametersEntity());
        addEntityView(eView);
    }

    @Override
    protected TableRef prepareTableRef4Selection() {
        TableRef tr = new TableRef();
        tr.datasourceName = model.getDatasourceName();
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
    public EntityView<QueryEntity> getParametersView() {
        if (entityViews != null) {
            return entityViews.get(QueryModel.PARAMETERS_ENTITY_ID);
        } else {
            return null;
        }
    }

    @Override
    protected EntityView<QueryEntity> createGenericEntityView(QueryEntity aEntity) throws Exception {
        if (isParametersEntity(aEntity)) {
            return new QueryParametersEntityView((QueryParametersEntity) aEntity, entitiesViewsMover);
        } else {
            if (aEntity.getQueryName() != null && !aEntity.getQueryName().isEmpty()) {
                model.getQueries().getQuery(aEntity.getQueryName(), null, null, null);
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
            for (SelectedField<QueryEntity> t : selectedFields) {
                if (!(t.entity instanceof QueryParametersEntity)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected void deleteSelectedFields() {
        if (isSelectedDeletableFields()) {
            QueryEntity selectedEntity = selectedFields.iterator().next().entity;
            assert selectedEntity instanceof QueryParametersEntity;
            EntityView<QueryEntity> qeView = getEntityView(selectedEntity);
            assert qeView instanceof QueryParametersEntityView;
            int oldSelectionLeadIndex = qeView.getFieldsList().getSelectionModel().getLeadSelectionIndex();
            AccessibleCompoundEdit section = new AccessibleCompoundEdit();
            Set<SelectedField<QueryEntity>> toDelete = new HashSet<>(selectedFields);
            clearSelection();
            for (SelectedField<QueryEntity> t : toDelete) {
                Set<Relation<QueryEntity>> toDel = Entity.getInOutRelationsByEntityField(t.entity, t.field);
                for (Relation rel : toDel) {
                    DeleteRelationEdit drEdit = new DeleteRelationEdit(rel);
                    drEdit.redo();
                    section.addEdit(drEdit);
                }
                DeleteFieldEdit edit = new DeleteFieldEdit(t.entity, t.field);
                edit.redo();
                section.addEdit(edit);
            }
            section.end();
            undoSupport.postEdit(section);
            if (oldSelectionLeadIndex != -1) {
                int listSize = qeView.getFieldsList().getModel().getSize();
                if (oldSelectionLeadIndex >= listSize) {
                    oldSelectionLeadIndex = listSize - 1;
                }
                if (oldSelectionLeadIndex >= 0 && oldSelectionLeadIndex < listSize) {
                    qeView.getFieldsList().getSelectionModel().setSelectionInterval(oldSelectionLeadIndex, oldSelectionLeadIndex);
                }
            }
        }
    }

    @Override
    protected void copySelectedEntities() {
        QueryModel copied = new QueryModel(model.getQueries());
        selectedFields.stream().forEach((ef) -> {
            if (ef.entity instanceof QueryParametersEntity) {
                copied.getParameters().add(ef.field);
            }
        });
        selectedEntities.stream().forEach((QueryEntity ent) -> {
            if (ent != null && !(ent instanceof QueryParametersEntity)) {
                copied.getEntities().put(ent.getEntityId(), ent);
            }
        });
        Document doc = QueryModel2XmlDom.transform(copied);
        String content = XmlDom2String.transform(doc);
        string2SystemClipboard(content);
    }

    @Override
    protected void pasteEntities(QueryModel pastedModel, List<QueryEntity> entitiesPasted) throws CannotRedoException {
        pasteParameters(pastedModel);
        super.pasteEntities(pastedModel, entitiesPasted);
    }

    private void pasteParameters(QueryModel sourceModel) {
        undoSupport.beginUpdate();
        try {
            for (Field field : sourceModel.getParameters().toCollection()) {
                field.setName(getParameterName(field.getName()));
                NewFieldEdit edit = new NewFieldEdit(getParametersView().getEntity(), field);
                edit.redo();
                undoSupport.postEdit(edit);
            }
        } finally {
            undoSupport.endUpdate();
        }
    }

    private String getParameterName(String paramName) {
        String s = paramName;
        int i = 1;
        while (model.getParameters().get(s) != null) {
            s = String.format(NAME_PATTERN, paramName, i++);
        }
        return s;
    }

}
