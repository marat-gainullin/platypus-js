/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.model;

import com.bearsoft.routing.Connector;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.EntityFieldRef;
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.DmAction;
import com.eas.client.model.gui.SettingsDialog;
import com.eas.client.model.gui.edits.CollectionPropertyNameEdit;
import com.eas.client.model.gui.edits.DeleteReferenceRelationEdit;
import com.eas.client.model.gui.edits.NewReferenceRelationEdit;
import com.eas.client.model.gui.edits.ScalarPropertyNameEdit;
import com.eas.client.model.gui.selectors.AppElementSelectorCallback;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.AddQueryAction;
import com.eas.client.model.gui.view.ScalarCollectionView;
import com.eas.client.model.gui.view.entities.ApplicationEntityView;
import com.eas.client.model.gui.view.entities.EntityView;
import static com.eas.client.model.gui.view.model.ModelView.connectorWidth;
import static com.eas.client.model.gui.view.model.ModelView.connectorsStroke;
import static com.eas.client.model.gui.view.model.ModelView.hittedConnectorsStroke;
import com.eas.client.model.store.ApplicationModel2XmlDom;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.script.Scripts;
import com.eas.xml.dom.XmlDom2String;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class ApplicationModelView extends ModelView<ApplicationDbEntity, ApplicationDbModel> {

    protected final static Stroke fkConnectorsStroke = new BasicStroke(connectorWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[]{5, 4}, 0);
    protected final static Stroke fkHittedConnectorsStroke = new BasicStroke(connectorWidth + 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[]{5, 4}, 0);
    protected final static Stroke fkSelectedConnectorsStroke = new BasicStroke(connectorWidth + 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[]{5, 4}, 0);
    protected AppElementSelectorCallback appElementSelector;

    public ApplicationModelView(TablesSelectorCallback aSelectorCallback, AppElementSelectorCallback aAppElementSelector) {
        super(aSelectorCallback);
        appElementSelector = aAppElementSelector;
        putActions();
    }

    public ApplicationModelView(ApplicationDbModel aModel, TablesSelectorCallback aSelectorCallback, AppElementSelectorCallback aAppElementSelector) {
        super(aModel, aSelectorCallback);
        appElementSelector = aAppElementSelector;
        putActions();
    }

    protected final void putActions() {
        getActionMap().put(AddQueryAction.class.getSimpleName(), new AddQueryAction(this, undoSupport, appElementSelector));
        getActionMap().put(PropertiesNamesAction.class.getSimpleName(), new PropertiesNamesAction());
    }

    @Override
    protected TableRef prepareTableRef4Selection() {
        return new TableRef();
    }

    @Override
    protected ApplicationDbModel transformDocToModel(Document aDoc) throws Exception {
        ApplicationDbModel lmodel = new ApplicationDbModel(model.getQueries());
        lmodel.accept(new XmlDom2ApplicationModel<>(aDoc));
        return lmodel;
    }

    @Override
    protected boolean isParametersEntity(ApplicationDbEntity aEntity) {
        return false;
    }

    @Override
    protected boolean isAnyDeletableEntities() {
        return isAnySelectedEntities() && selectedEntities.stream().anyMatch((sEntity) -> (!isParametersEntity(sEntity)));
    }

    @Override
    protected EntityView<ApplicationDbEntity> createGenericEntityView(ApplicationDbEntity aEntity) throws Exception {
        if (aEntity.getQueryName() != null && !aEntity.getQueryName().isEmpty()) {
            try {
                model.getQueries().getQuery(aEntity.getQueryName(), Scripts.getSpace(), null, null);
            } catch (Exception ex) {
                Logger.getLogger(ApplicationModelView.class.getName()).log(Level.WARNING, ex.getMessage());
            }
        }
        aEntity.validateQuery();
        return new ApplicationEntityView(aEntity, entitiesViewsMover);
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

    protected static String generateRelationId(Relation<ApplicationDbEntity> rel) {
        String relId = rel.getLeftEntity().getName() != null && !rel.getLeftEntity().getName().isEmpty() ? rel.getLeftEntity().getName() : String.valueOf(rel.getLeftEntity().getEntityId());
        relId += "." + rel.getLeftField().getName();
        relId += " -> ";
        relId += rel.getRightEntity().getName() != null && !rel.getRightEntity().getName().isEmpty() ? rel.getRightEntity().getName() : String.valueOf(rel.getRightEntity().getEntityId());
        relId += "." + rel.getRightField().getName();
        return relId;
    }

    public static interface ForeignKeyBindingTask {

        public void run(ReferenceRelation<ApplicationDbEntity> aRelation);
    }

    /**
     * ORM designer implementation. Finds all primary keys and binds them with
     * foreign keys. Binding process is different for runtime and design and so,
     * binding task parameter is accepted.
     *
     * @param aTask
     */
    public void complementReferenceRelationsByKeys(ForeignKeyBindingTask aTask) {
        complementReferenceRelations(aTask, model);
    }

    public static void complementReferenceRelations(ForeignKeyBindingTask aTask, ApplicationDbModel model) {
        Set<String> alreadyRels = new HashSet<>();
        model.getReferenceRelations().stream().forEach((rel) -> {
            alreadyRels.add(generateRelationId(rel));
        });
        Map<String, Set<EntityFieldRef<ApplicationDbEntity>>> pksByTable = new HashMap<>();
        model.getEntities().values().stream().forEach((entity) -> {
            Fields fields = entity.getFields();
            if (fields != null) {
                fields.getPrimaryKeys().stream().forEach((field) -> {
                    Set<EntityFieldRef<ApplicationDbEntity>> pks = pksByTable.get(field.getTableName());
                    if (pks == null) {
                        pks = new HashSet<>();
                        pksByTable.put(field.getTableName(), pks);
                    }
                    pks.add(new EntityFieldRef(entity, field));
                });
            }
        });
        model.getEntities().values().stream().forEach((entity) -> {
            Fields fields = entity.getFields();
            if (fields != null) {
                fields.getForeinKeys().stream().forEach((Field field) -> {
                    assert field.getFk() != null;
                    String tableName = field.getFk().getReferee().getTable();
                    String fieldName = field.getFk().getReferee().getField();
                    Set<EntityFieldRef<ApplicationDbEntity>> pks = pksByTable.get(tableName);
                    if (pks != null) {
                        pks.stream().forEach((EntityFieldRef<ApplicationDbEntity> pk) -> {
                            if (pk.field.getOriginalName().equalsIgnoreCase(fieldName)) {
                                ReferenceRelation<ApplicationDbEntity> relation = new ReferenceRelation<>(entity, field, pk.entity, pk.field);
                                String relId = generateRelationId(relation);
                                if (!alreadyRels.contains(relId)) {
                                    alreadyRels.add(relId);
                                    aTask.run(relation);
                                }
                            }
                        });
                    }
                });
            }
        });
    }
    protected ForeignKeyBindingTask referenceRelaionEditsGenerator = (ReferenceRelation<ApplicationDbEntity> aRelation) -> {
        NewReferenceRelationEdit edit = new NewReferenceRelationEdit(aRelation);
        edit.redo();
        undoSupport.postEdit(edit);
    };

    @Override
    protected void copySelectedEntities() {
        ApplicationDbModel copied = new ApplicationDbModel(model.getQueries());
        selectedEntities.stream().forEach((ApplicationDbEntity ent) -> {
            if (ent != null) {
                copied.getEntities().put(ent.getEntityId(), ent);
            }
        });
        Document doc = ApplicationModel2XmlDom.transform(copied);
        String sEntity = XmlDom2String.transform(doc);
        string2SystemClipboard(sEntity);
    }

    @Override
    public boolean isSelectedDeletableRelations() {
        return super.isSelectedDeletableRelations() && selectedRelations.stream().anyMatch((rel) -> (!(rel instanceof ReferenceRelation<?>)));
    }

    @Override
    protected void doDeleteEntity(ApplicationDbEntity entity) {
        undoSupport.beginUpdate();
        try {
            Set<ReferenceRelation<ApplicationDbEntity>> toDel = model.getReferenceRelationsByEntity(entity);
            for (ReferenceRelation<ApplicationDbEntity> rel : toDel) {
                DeleteReferenceRelationEdit edit = new DeleteReferenceRelationEdit(rel);
                edit.redo();
                undoSupport.postEdit(edit);
            }
            super.doDeleteEntity(entity);
        } finally {
            undoSupport.endUpdate();
        }
    }

    @Override
    public Rectangle doAddTable(TableRef rSelected, int aX, int aY) {
        undoSupport.beginUpdate();
        try {
            Rectangle rect = super.doAddTable(rSelected, aX, aY);
            complementReferenceRelationsByKeys(referenceRelaionEditsGenerator);
            return rect;
        } finally {
            undoSupport.endUpdate();
        }
    }

    @Override
    public void doPasteEntity(ApplicationDbEntity aEntity) {
        undoSupport.beginUpdate();
        try {
            super.doPasteEntity(aEntity);
            complementReferenceRelationsByKeys(referenceRelaionEditsGenerator);
        } finally {
            undoSupport.endUpdate();
        }
    }

    @Override
    public void doAddQuery(String aApplicationElementId, int aX, int aY) throws Exception {
        undoSupport.beginUpdate();
        try {
            super.doAddQuery(aApplicationElementId, aX, aY);
            complementReferenceRelationsByKeys(referenceRelaionEditsGenerator);
        } finally {
            undoSupport.endUpdate();
        }
    }

    @Override
    public void setModel(ApplicationDbModel aModel) {
        if (model != null) {
            model.getReferenceRelations().stream().forEach((rel) -> {
                rel.getChangeSupport().removePropertyChangeListener(relationPolylinePropagator);
            });
        }
        super.setModel(aModel); //To change body of generated methods, choose Tools | Templates.
        if (model != null) {
            model.getReferenceRelations().stream().forEach((rel) -> {
                rel.getChangeSupport().addPropertyChangeListener(relationPolylinePropagator);
            });
        }
    }

    @Override
    protected Set<Relation<ApplicationDbEntity>> modelRelationsToBeRerouted() {
        Set<Relation<ApplicationDbEntity>> rels = new HashSet<>();
        rels.addAll(super.modelRelationsToBeRerouted());
        rels.addAll(model.getReferenceRelations());
        return rels;
    }

    @Override
    protected boolean isSelectedDeletableFields() {
        return false;
    }

    @Override
    protected void paintConnector(Graphics2D g2d, Relation<ApplicationDbEntity> aRel, Connector aConnector, Stroke aConnectorsStroke) {
        if (aRel instanceof ReferenceRelation<?>) {
            Stroke oldStroke = g2d.getStroke();
            if (oldStroke == connectorsStroke) {
                g2d.setStroke(fkConnectorsStroke);
            } else if (oldStroke == hittedConnectorsStroke) {
                g2d.setStroke(fkHittedConnectorsStroke);
            } else if (oldStroke == selectedConnectorsStroke) {
                g2d.setStroke(fkSelectedConnectorsStroke);
            }
            try {
                super.paintConnector(g2d, aRel, aConnector, aConnectorsStroke);
            } finally {
                g2d.setStroke(oldStroke);
            }
        } else {
            super.paintConnector(g2d, aRel, aConnector, aConnectorsStroke);
        }
    }

    public class PropertiesNamesAction extends DmAction {

        public PropertiesNamesAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                Window w = SwingUtilities.getWindowAncestor(ApplicationModelView.this);
                if (w instanceof JFrame) {
                    ReferenceRelation<ApplicationDbEntity> relation = (ReferenceRelation<ApplicationDbEntity>) getSelectedRelations().iterator().next();
                    ScalarCollectionView view = new ScalarCollectionView(relation);
                    SettingsDialog dlg = new SettingsDialog((JFrame) w, view, true, () -> true);
                    Dimension size = new Dimension(400, 200);
                    dlg.setTitle(getDmActionText());
                    dlg.setSize(size);
                    dlg.setMinimumSize(size);

                    dlg.setVisible(true);
                    if (dlg.isOkClose()) {
                        String oldScalarName = relation.getScalarPropertyName();
                        String oldCollectionName = relation.getCollectionPropertyName();
                        String newScalarName = view.getScalarPropertyName();
                        String newCollectionName = view.getCollectionPropertyName();
                        if ((oldScalarName == null ? newScalarName != null : !oldScalarName.equals(newScalarName))
                                || (oldCollectionName == null ? newCollectionName != null : !oldCollectionName.equals(newCollectionName))) {
                            undoSupport.beginUpdate();
                            try {
                                if (oldScalarName == null ? newScalarName != null : !oldScalarName.equals(newScalarName)) {
                                    ScalarPropertyNameEdit edit = new ScalarPropertyNameEdit(relation, oldScalarName, newScalarName);
                                    edit.redo();
                                    undoSupport.postEdit(edit);
                                }
                                if (oldCollectionName == null ? newCollectionName != null : !oldCollectionName.equals(newCollectionName)) {
                                    CollectionPropertyNameEdit edit = new CollectionPropertyNameEdit(relation, oldCollectionName, newCollectionName);
                                    edit.redo();
                                    undoSupport.postEdit(edit);
                                }
                            } finally {
                                undoSupport.endUpdate();
                            }
                        }
                    }
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return isSelectedRelations() && getSelectedRelations().size() == 1 && getSelectedRelations().iterator().next() instanceof ReferenceRelation<?>;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(PropertiesNamesAction.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(PropertiesNamesAction.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return null;//IconCache.getIcon("");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return null;
        }
    }
}
