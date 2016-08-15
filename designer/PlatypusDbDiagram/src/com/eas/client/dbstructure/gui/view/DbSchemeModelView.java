/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.view;

import com.eas.client.DatabasesClient;
import com.eas.client.MetadataCache;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.IconCache;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.gui.edits.*;
import com.eas.client.model.gui.SettingsDialog;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.ForeignKeySpec.ForeignKeyRule;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.*;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.DmAction;
import com.eas.client.model.gui.edits.AccessibleCompoundEdit;
import com.eas.client.model.gui.edits.DeleteEntityEdit;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.NewEntityEdit;
import com.eas.client.model.gui.edits.NewRelationEdit;
import com.eas.client.model.gui.edits.fields.DeleteFieldEdit;
import com.eas.client.model.gui.view.model.SelectedField;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.model.ModelView;
import com.eas.client.model.store.DbSchemeModel2XmlDom;
import com.eas.client.model.store.XmlDom2DbSchemeModel;
import com.eas.client.sqldrivers.GenericSqlDriver;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.designer.application.dbdiagram.nodes.TableFieldNode;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CompoundEdit;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class DbSchemeModelView extends ModelView<FieldsEntity, DbSchemeModel> {

    protected SqlActionsController sqlController;

    @Override
    public void doAddQuery(String aAppQueryName, int aX, int aY) throws Exception {
        // No op. We can't add queries to db-diagram model
    }

    public int addFkRelations(boolean directChanging, String only4Table) throws Exception {
        assert model instanceof DbSchemeModel;
        int added = 0;
        Map<Long, FieldsEntity> entities = model.getEntities();
        if (entities != null && !entities.isEmpty()) {
            Map<String, FieldsEntity> entitiesByTableName = new HashMap<>();
            for (FieldsEntity entity : entities.values()) {
                if (entity != null) {
                    entitiesByTableName.put(entity.getTableName().toLowerCase(), entity);
                }
            }
            MetadataCache mdCache = model.getBasesProxy().getMetadataCache(model.getDatasourceName());
            String schema = model.getSchema();
            for (FieldsEntity entity : entities.values()) {
                if (entity != null) {
                    try {
                        String leftTableName = entity.getTableName();
                        String fullLeftTableName = leftTableName;
                        if (schema != null && !schema.isEmpty()) {
                            fullLeftTableName = schema + "." + fullLeftTableName;
                        }
                        Fields fields = mdCache.getTableMetadata(fullLeftTableName);
                        if (fields != null) {
                            List<Field> dbFks = fields.getForeinKeys();
                            if (dbFks != null) {
                                for (Field fkField : dbFks) {
                                    if (fkField != null && fkField.getFk() != null && fkField.getFk() instanceof ForeignKeySpec) {
                                        ForeignKeySpec fkSpec = fkField.getFk();
                                        String refereeTableName = fkSpec.getReferee().getTable();
                                        if (refereeTableName != null
                                                && (only4Table == null || only4Table.equalsIgnoreCase(refereeTableName) || only4Table.equalsIgnoreCase(leftTableName))
                                                && entitiesByTableName.containsKey(refereeTableName.toLowerCase())) {
                                            FieldsEntity refereeEntity = entitiesByTableName.get(refereeTableName.toLowerCase());
                                            boolean alreadyExist = isRelationAlreadyDefined(entity, entity.getFields().get(fkSpec.getField()), refereeEntity, refereeEntity.getFields().get(fkSpec.getReferee().getField()));
                                            if (!alreadyExist) {
                                                Relation<FieldsEntity> fkRelation = new Relation<>(entity, entity.getFields().get(fkSpec.getField()), refereeEntity, refereeEntity.getFields().get(fkSpec.getReferee().getField()));
                                                fkRelation.setFkName(fkSpec.getCName());
                                                fkRelation.setFkUpdateRule(fkSpec.getFkUpdateRule());
                                                fkRelation.setFkDeleteRule(fkSpec.getFkDeleteRule());
                                                fkRelation.setFkDeferrable(fkSpec.getFkDeferrable());
                                                if (directChanging) {
                                                    model.addRelation(fkRelation);
                                                } else {
                                                    NewRelationEdit<FieldsEntity> edit = new NewRelationEdit<>(fkRelation);
                                                    edit.redo();
                                                    undoSupport.postEdit(edit);
                                                }
                                                added++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(ModelView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return added;
    }

    public static boolean isRelationAlreadyDefined(FieldsEntity leftEntity, Field leftField, FieldsEntity rightEntity, Field rightField) {
        if (leftEntity != null && rightEntity != null
                && leftField != null
                && rightField != null) {
            Set<Relation<FieldsEntity>> inRels = rightEntity.getInRelations();
            if (inRels != null) {
                for (Relation<FieldsEntity> rel : inRels) {
                    if (rel != null) {
                        FieldsEntity lEntity = rel.getLeftEntity();
                        if (lEntity == leftEntity) {
                            if (leftField == rel.getLeftField()
                                    && rightField == rel.getRightField()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected TableRef prepareTableRef4Selection() {
        TableRef oldValue = new TableRef();
        oldValue.datasourceName = model.getDatasourceName();
        oldValue.schema = model.getSchema();
        if (oldValue.schema == null) {
            oldValue.schema = "";
        }
        return oldValue;
    }

    @Override
    protected TableRef[] selectTableRef(TableRef oldValue) throws Exception {
        TableRef[] selected = super.selectTableRef(oldValue);
        if (selected != null) {
            List<TableRef> filtered = new ArrayList<>();
            for (TableRef tr : selected) {
                if (model.getEntityByTableName(tr.tableName) == null) {
                    filtered.add(tr);
                }
            }
            return filtered.toArray(new TableRef[0]);
        } else {
            return null;
        }
    }

    @Override
    protected DbSchemeModel transformDocToModel(Document aDoc) throws Exception {
        return XmlDom2DbSchemeModel.transform(model.getBasesProxy(), aDoc);
    }

    @Override
    protected boolean isParametersEntity(FieldsEntity aEntity) {
        return false;
    }

    @Override
    protected EntityView<FieldsEntity> createGenericEntityView(FieldsEntity aEntity) throws Exception {
        aEntity.validateQuery();
        return new TableEntityView(aEntity, entitiesViewsMover);
    }

    @Override
    protected void checkPastingName(FieldsEntity toPaste) {
        int lNameCounter = 0;
        String defTableName = "table";
        String lName = toPaste.getName();
        if (lName == null || lName.isEmpty()) {
            lName = defTableName;
        }
        while (model.isNamePresent(lName, toPaste, (Field) null)) {
            lNameCounter++;
            lName = toPaste.getName();
            if (lName == null || lName.isEmpty()) {
                lName = defTableName;
            }
            lName += String.valueOf(lNameCounter);
        }
        toPaste.setName(lName);
    }

    @Override
    protected boolean isPasteable(FieldsEntity aEntityToPaste) {
        return true;
    }

    @Override
    protected void prepareEntityForPaste(FieldsEntity aEntity) {
        if (model.getEntityById(aEntity.getEntityId()) != null) {
            aEntity.regenerateId();
        }
        findPlaceForEntityPaste(aEntity);
    }

    @Override
    protected void deleteSelectedFields() {
        if (isSelectedDeletableFields()) {
            FieldsEntity selectedEntity = selectedFields.iterator().next().entity;
            EntityView<FieldsEntity> feView = getEntityView(selectedEntity);
            int oldSelectionLeadIndex = feView.getFieldsList().getSelectionModel().getLeadSelectionIndex();
            AccessibleCompoundEdit section = new AccessibleCompoundEdit();
            Set<SelectedField<FieldsEntity>> toDelete = new HashSet<>(selectedFields);
            clearSelection();
            for (SelectedField<FieldsEntity> t : toDelete) {
                Set<Relation<FieldsEntity>> toDel = FieldsEntity.getInOutRelationsByEntityField(t.entity, t.field);
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
                int listSize = feView.getFieldsList().getModel().getSize();
                if (oldSelectionLeadIndex >= listSize) {
                    oldSelectionLeadIndex = listSize - 1;
                }
                if (oldSelectionLeadIndex >= 0 && oldSelectionLeadIndex < listSize) {
                    feView.getFieldsList().getSelectionModel().setSelectionInterval(oldSelectionLeadIndex, oldSelectionLeadIndex);
                }
            }
        }
    }

    public Relation<FieldsEntity> getSelectedFkFieldRelation() {
        if (isSelectedEntities() && getSelectedEntities().size() == 1) {
            FieldsEntity fe = getSelectedEntities().iterator().next();
            EntityView<FieldsEntity> fev = getEntityView(fe);
            Field field = fev.getSelectedField();
            if (field != null && field.isFk()) {
                for (Relation<FieldsEntity> rel : fe.getOutRelations()) {
                    if (field == rel.getLeftField()) {
                        return rel;
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected boolean isAnyDeletableEntities() {
        return isAnySelectedEntities();
    }

    @Override
    protected boolean isSelectedDeletableFields() {
        return isSelectedFieldsOnOneEntity() && !isSelectedPk();
    }

    private boolean isSelectedPk() {
        for (SelectedField<FieldsEntity> eft : selectedFields) {
            if (eft.field.isPk()) {
                return true;
            }
        }
        return false;
    }

    public class AddTableFieldAction extends ModelView.AddField {

        @Override
        public boolean isEnabled() {
            return editable && (getSelectedEntities().size() == 1 || isSelectedFieldsOnOneEntity());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                FieldsEntity entity = (FieldsEntity) getEntity();
                DbSchemeModel model = entity.getModel();
                if (model != null) {
                    try {
                        JdbcField field = NewFieldEdit.createField(entity);
                        PropertySheet ps = new PropertySheet();
                        TableFieldNode fieldNode = new TableFieldNode(field, Lookups.fixed(entity)) {

                            // setXXX() methods are overrided here to avoid in database edits generation by TableFieldNode
                            @Override
                            public void setName(String val) {
                                field.setName(val);
                            }

                            @Override
                            public void setType(String val) {
                                try {
                                    field.setType(val);
                                    DatabasesClient client = model.getBasesProxy();
                                    String datasourceName = model.getDatasourceName();
                                    SqlDriver driver = client.getMetadataCache(datasourceName).getDatasourceSqlDriver();
                                    driver.getTypesResolver().resolveSize((JdbcField) field);
                                } catch (Exception ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }

                            @Override
                            public void setDescription(String val) {
                                field.setDescription(val);
                            }

                            @Override
                            public void setSize(Integer val) {
                                ((JdbcField) field).setSize(val);
                            }

                            @Override
                            public void setScale(Integer val) {
                                ((JdbcField) field).setScale(val);
                            }

                            @Override
                            public void setNullable(Boolean val) {
                                ((JdbcField) field).setNullable(val);
                            }

                        };
                        ps.setNodes(new Node[]{fieldNode});
                        DialogDescriptor dd = new DialogDescriptor(ps, NbBundle.getMessage(DbSchemeModelView.class, "MSG_NewSchemeFieldDialogTitle"));
                        if (DialogDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
                            NewFieldEdit fieldEdit = new NewFieldEdit(sqlController, entity, field);
                            fieldEdit.redo();
                            undoSupport.notSavable();
                            undoSupport.beginUpdate();
                            try {
                                undoSupport.postEdit(fieldEdit);
                                com.eas.client.model.gui.edits.fields.NewFieldEdit edit = new com.eas.client.model.gui.edits.fields.NewFieldEdit(entity, fieldEdit.getField());
                                edit.redo();
                                undoSupport.postEdit(edit);
                                checkActions();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                            } finally {
                                undoSupport.endUpdate();
                            }
                            EntityView<FieldsEntity> efView = getEntityView(entity);
                            efView.getFieldsList().setSelectedValue(field, true);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        @Override
        protected Entity getEntity() {
            if (!getSelectedEntities().isEmpty()) {
                return getSelectedEntities().iterator().next();
            } else if (!getSelectedFields().isEmpty()) {
                return getSelectedFields().iterator().next().entity;
            }
            throw new IllegalStateException();
        }
    }

    public class AddTableAction extends ModelView<FieldsEntity, DbSchemeModel>.AddTable {

        @Override
        public boolean isEnabled() {
            return super.isEnabled();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            undoSupport.beginUpdate();
            try {
                try {
                    super.actionPerformed(e);
                    if (justSelected != null) {
                        for (TableRef tr : justSelected) {
                            try {
                                addFkRelations(false, tr.tableName);
                            } catch (Exception ex) {
                                Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        refreshView();
                    });
                }
            } finally {
                undoSupport.endUpdate();
            }
        }
    }

    public class CreateTableAction extends DmAction {

        public CreateTableAction() {
            super();
            putValue(Action.LARGE_ICON_KEY, IconCache.getIcon("24x24/newTable.png"));
        }

        @Override
        public boolean isEnabled() {
            return editable && isShowing();
        }

        @Override
        public String getDmActionText() {
            return NbBundle.getMessage(DbStructureUtils.class, CreateTableAction.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return NbBundle.getMessage(DbStructureUtils.class, CreateTableAction.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("16x16/newTable.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String tableName = "";
            while (tableName != null && tableName.isEmpty()) {
                tableName = JOptionPane.showInputDialog(DbSchemeModelView.this, NbBundle.getMessage(DbStructureUtils.class, "inputTableName"), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.QUESTION_MESSAGE);
            }
            if (tableName != null) {
                CompoundEdit section = new DbStructureCompoundEdit();
                CreateTableEdit edit = new CreateTableEdit(sqlController, tableName);
                try {
                    edit.redo();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                section.addEdit(edit);
                try {
                    Rectangle rect = findPlaceForEntityAdd(0, 0);
                    FieldsEntity entity = model.newGenericEntity();
                    entity.setModel(model);
                    entity.setX(rect.x);
                    entity.setY(rect.y);
                    entity.setWidth(rect.width);
                    entity.setHeight(rect.height);
                    entity.setTableName(tableName);
                    NewEntityEdit<FieldsEntity, DbSchemeModel> entityEdit = new NewEntityEdit<>(model, entity);
                    entityEdit.redo();
                    section.addEdit(entityEdit);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                } finally {
                    section.end();
                    undoSupport.postEdit(section);
                }
            }
        }
    }

    public class DropTableAction extends DmAction {

        public DropTableAction() {
            super();
            putValue(Action.LARGE_ICON_KEY, IconCache.getIcon("24x24/dropTable.png"));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return editable && isShowing() && isAnySelectedEntities();
        }

        @Override
        public String getDmActionText() {
            return NbBundle.getMessage(DbStructureUtils.class, DropTableAction.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return NbBundle.getMessage(DbStructureUtils.class, DropTableAction.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("16x16/dropTable.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            processEntity(getSelectedEntities(), sqlController, e);
        }

        private int getRecordsCount(FieldsEntity tableEntity) {
            if (tableEntity != null) {
                try {
                    String fullTableName = tableEntity.getTableName();
                    String schemaName = tableEntity.getTableSchemaName();
                    if (schemaName != null && !schemaName.isEmpty()) {
                        fullTableName = schemaName + "." + fullTableName;
                    }
                    SqlCompiledQuery query = new SqlCompiledQuery(model.getBasesProxy(), tableEntity.getTableDatasourceName(), "select count(*) cnt from " + fullTableName);
                    Integer count = query.executeQuery((ResultSet r) -> {
                        if (r.next()) {
                            Object cnt = r.getObject(1);
                            if (cnt instanceof Number) {
                                return ((Number) cnt).intValue();
                            } else {
                                return 0;
                            }
                        } else {
                            return 0;
                        }
                    }, null, null, null);
                    return count != null ? count : 0;
                } catch (Exception ex) {
                    Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return 0;
        }

        private void processEntity(Set<FieldsEntity> tableEntities, SqlActionsController sqlController, ActionEvent e) {
            if (tableEntities != null) {
                for (FieldsEntity tableEntity : tableEntities) {
                    // act with table and it's entity.
                    // Dropping foreign keys, table and removing theirs entities and relations
                    Set<Relation<FieldsEntity>> inRels = tableEntity.getInRelations();
                    int rCount = getRecordsCount(tableEntity);
                    String msg = null;
                    if (rCount == 0 && !inRels.isEmpty()) {
                        msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureInRelationsPresent", tableEntity.getTableName(), String.valueOf(inRels.size()));
                    } else if (rCount > 0 && inRels.isEmpty()) {
                        msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureDataPresent", tableEntity.getTableName(), String.valueOf(rCount));
                    } else if (rCount > 0 && !inRels.isEmpty()) {
                        msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureInRelationsDataPresent", tableEntity.getTableName(), String.valueOf(inRels.size()), String.valueOf(rCount));
                    }
                    if (msg == null || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(DbSchemeModelView.this, msg, NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                        editDbDiagram(tableEntity, sqlController, e);
                    }
                }
            }
        }

        public void editDbDiagram(FieldsEntity tableEntity, SqlActionsController sqlController, ActionEvent e) {
            ActionMap am = getActionMap();
            Action removeTableAction = am.get(ModelView.Delete.class.getSimpleName());
            EntityView<FieldsEntity> eView = getEntityView(tableEntity);
            DropTableEdit edit = new DropTableEdit(sqlController, tableEntity.getTableName(), eView.getFields(), tableEntity);
            try {
                edit.redo();
            } catch (Exception ex) {
                int userChoice = JOptionPane.showConfirmDialog(DbSchemeModelView.this, ex.getLocalizedMessage() + " \n" + NbBundle.getMessage(DbStructureUtils.class, "removeFromDiagram"), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (userChoice == JOptionPane.YES_OPTION) {
                    if (removeTableAction != null) {
                        removeTableAction.actionPerformed(e);
                    }
                }
                return;
            }
            undoSupport.beginUpdate();
            try {
                undoSupport.postEdit(edit);
                removeTableAction.actionPerformed(e);
            } finally {
                undoSupport.endUpdate();
            }
        }
    }

    public class DropFkRemoveTableAction extends ModelView<FieldsEntity, DbSchemeModel>.Delete {

        @Override
        public boolean isEnabled() {
            return editable && super.isEnabled();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                if (!isAnySelectedEntities() && isSelectedRelations()) {
                    // act with relations - dropping foreign keys
                    Set<Relation<FieldsEntity>> rels = getSelectedRelations();
                    assert rels != null && !rels.isEmpty();
                    List<DropFkEdit> dEdits = new ArrayList<>();
                    try {
                        for (Relation<FieldsEntity> rel2Del : rels) {
                            ForeignKeySpec fkSpec = DbStructureUtils.constructFkSpecByRelation(rel2Del);
                            DropFkEdit dEdit = new DropFkEdit(sqlController, fkSpec, rel2Del.getLeftField());
                            dEdit.redo();
                            dEdits.add(dEdit);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    undoSupport.notSavable();
                    undoSupport.beginUpdate();
                    try {
                        for (DropFkEdit dEdit : dEdits) {
                            undoSupport.postEdit(dEdit);
                        }
                        try {
                            super.actionPerformed(e);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } finally {
                        undoSupport.endUpdate();
                    }
                } else if (!isSelectedRelations() && isSelectedFieldsOnOneEntity()) { // drop fields
                    dropFields(e);
                } else { // drop table or delete from this diagram
                    int dialogResult = getDeleteTypeOption();
                    switch (dialogResult) {
                        case JOptionPane.YES_OPTION:
                            super.actionPerformed(e);
                            break;
                        case JOptionPane.NO_OPTION:
                            dropTables(getSelectedEntities(), sqlController, e);
                            break;
                    }
                }
            }
        }

        protected int getDeleteTypeOption() {
            Object[] options = {
                NbBundle.getMessage(DbStructureUtils.class, "dlgDeleteFromDiagram"),
                NbBundle.getMessage(DbStructureUtils.class, "dlgDropTable"),
                NbBundle.getMessage(DbStructureUtils.class, "dlgCancel")
            };
            return JOptionPane.showOptionDialog(DbSchemeModelView.this,
                    NbBundle.getMessage(DbStructureUtils.class, "dlgDeleteTableMsg"),
                    NbBundle.getMessage(DbStructureUtils.class, "dlgDeleteTableTitle"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);
        }

        private void dropFields(ActionEvent e) {
            if (isEnabled()) {
                // Remove fields from database and the diagram
                FieldsEntity entity = getSelectedFields().iterator().next().entity;
                CompoundEdit section = new DbStructureCompoundEdit();
                for (SelectedField<FieldsEntity> etf : getSelectedFields()) {
                    if (etf.field.isFk()) {
                        try {
                            DropFkEdit edit = new DropFkEdit(sqlController, etf.field.getFk(), etf.field);
                            edit.redo();
                            section.addEdit(edit);
                        } catch (CannotRedoException ex) {
                            section.end();
                            if (section.isSignificant()) {
                                undoSupport.postEdit(section);
                            }
                            JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                // act with indexes
                List<DbTableIndexSpec> indexes = entity.getIndexes();
                if (indexes != null) {
                    // collect indexes to drop
                    Set<DbTableIndexSpec> indexes2Drop = new HashSet<>();
                    for (SelectedField<FieldsEntity> etf : getSelectedFields()) {
                        Field field = etf.field;
                        // find out what indexes are consist of rsmd field
                        for (DbTableIndexSpec index : indexes) {
                            int colIndex = index.indexOfColumnByName(field.getName());
                            if (colIndex != -1) {
                                indexes2Drop.add(index);
                            }
                        }
                    }

                    for (DbTableIndexSpec index : indexes2Drop) {
                        int indexOrder = indexes.indexOf(index);
                        DropIndexEdit diEdit = new DropIndexEdit(sqlController, entity, index, indexOrder);
                        try {
                            diEdit.redo();
                            section.addEdit(diEdit);
                        } catch (CannotRedoException ex) {
                            Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                }
                // act with fields    
                for (SelectedField<FieldsEntity> etf : getSelectedFields()) {
                    JdbcField field = (JdbcField) etf.field;
                    if (!field.isPk()) {
                        try {
                            dropField(entity, section, field, e);
                        } catch (CannotRedoException ex) {
                            section.end();
                            if (section.isSignificant()) {
                                undoSupport.postEdit(section);
                            }
                            JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                // Remove fields and all of the referencing relations from the diagram
                section.end();
                undoSupport.notSavable();
                undoSupport.beginUpdate();
                try {
                    undoSupport.postEdit(section);
                    super.actionPerformed(e);
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }

        private boolean dropField(FieldsEntity entity, CompoundEdit aEdit, JdbcField field, ActionEvent e) {
            if (entity != null) {
                // act with field.
                // Dropping foreign keys, table and removing theirs entities and relations
                Set<Relation<FieldsEntity>> sourceInRels = entity.getInRelations();
                Set<Relation<FieldsEntity>> inRels = new HashSet<>();
                for (Relation<FieldsEntity> rel : sourceInRels) {
                    if (rel.getRightField() == field) {
                        inRels.add(rel);
                    }
                }
                int rCount = DbStructureUtils.getRecordsCountByField(entity, field.getName());
                String msg = null;
                if (rCount == 0 && !inRels.isEmpty()) {
                    msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureFieldInRelationsPresent", String.valueOf(inRels.size()), null);
                } else if (rCount > 0 && inRels.isEmpty()) {
                    msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureFieldDataPresent", String.valueOf(rCount), null);
                } else if (rCount > 0 && !inRels.isEmpty()) {
                    msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureFieldInRelationsDataPresent", String.valueOf(inRels.size()), String.valueOf(rCount));
                }
                if (msg == null || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(DbSchemeModelView.this, msg, NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                    return doDropField(entity, aEdit, field, e);
                }
            }
            return true;
        }

        private boolean doDropField(FieldsEntity entity, CompoundEdit aEdit, JdbcField field, ActionEvent e) {
            DropFieldEdit edit = new DropFieldEdit(sqlController, field, entity);
            edit.redo();
            aEdit.addEdit(edit);
            return true;
        }

        private void dropTables(Set<FieldsEntity> aTableEntities, SqlActionsController sqlController, ActionEvent e) {
            if (aTableEntities != null) {
                Set<FieldsEntity> tableEntities = new HashSet<>();
                for (FieldsEntity tableEntity : aTableEntities) {
                    // Act with table and it's entity.
                    // Dropping foreign keys, table and removing theirs entities and relations.
                    Set<Relation<FieldsEntity>> inRels = tableEntity.getInRelations();
                    int rCount = getRecordsCount(tableEntity);
                    String msg = null;
                    if (rCount == 0 && !inRels.isEmpty()) {
                        msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureInRelationsPresent", tableEntity.getTableName(), String.valueOf(inRels.size()), null);
                    } else if (rCount > 0 && inRels.isEmpty()) {
                        msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureDataPresent", tableEntity.getTableName(), String.valueOf(rCount), null);
                    } else if (rCount > 0 && !inRels.isEmpty()) {
                        msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureInRelationsDataPresent", tableEntity.getTableName(), String.valueOf(inRels.size()), String.valueOf(rCount));
                    }
                    if (msg == null || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(DbSchemeModelView.this, msg, NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                        tableEntities.add(tableEntity);
                    }
                }
                undoSupport.beginUpdate();
                try {
                    List<FieldsEntity> toDelete = new ArrayList<>();
                    for (FieldsEntity tableEntity : tableEntities) {
                        if (doDropTable(tableEntity, sqlController, e)) {
                            toDelete.add(tableEntity);
                        }
                    }
                    super.deleteEntities(toDelete);
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }

        protected boolean doDropTable(FieldsEntity tableEntity, SqlActionsController sqlController, ActionEvent e) {
            EntityView<FieldsEntity> eView = getEntityView(tableEntity);
            DropTableEdit edit = new DropTableEdit(sqlController, tableEntity.getTableName(), eView.getFields(), tableEntity);
            try {
                edit.redo();
                undoSupport.postEdit(edit);
                return true;
            } catch (Exception ex) {
                int userChoice = JOptionPane.showConfirmDialog(DbSchemeModelView.this, ex.getLocalizedMessage() + " \n" + NbBundle.getMessage(DbStructureUtils.class, "removeFromDiagram"), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                return userChoice == JOptionPane.YES_OPTION;
            }
        }

        private int getRecordsCount(FieldsEntity tableEntity) {
            if (tableEntity != null) {
                try {
                    String fullTableName = tableEntity.getTableName();
                    String schemaName = tableEntity.getTableSchemaName();
                    if (schemaName != null && !schemaName.isEmpty()) {
                        fullTableName = schemaName + "." + fullTableName;
                    }
                    SqlCompiledQuery query = new SqlCompiledQuery(model.getBasesProxy(), tableEntity.getTableDatasourceName(), "select count(*) cnt from " + fullTableName);
                    Integer count = query.executeQuery((ResultSet r) -> {
                        if (r.next()) {
                            Object cnt = r.getObject(1);
                            if (cnt instanceof Number) {
                                return ((Number) cnt).intValue();
                            } else {
                                return 0;
                            }
                        } else {
                            return 0;
                        }
                    }, null, null, null);
                    return count != null ? count : 0;
                } catch (Exception ex) {
                    Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return 0;
        }
    }

    public class PasteTablesAction extends ModelView<FieldsEntity, DbSchemeModel>.Paste {

        @Override
        public boolean isEnabled() {
            return editable && super.isEnabled();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                assert model instanceof DbSchemeModel;
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (clipboard != null) {
                    Transferable tr = clipboard.getContents(null);
                    if (tr != null) {
                        try {
                            Object oData = tr.getTransferData(DataFlavor.stringFlavor);
                            if (oData != null && oData instanceof String) {
                                String sDm = (String) oData;
                                Document doc = Source2XmlDom.transform(sDm);
                                if (doc != null) {
                                    if (model != null) {
                                        pasteTables(model, doc);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(DbSchemeModelView.this, NbBundle.getMessage(DbStructureUtils.class, "BadClipboardData"), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(DbSchemeModelView.this, NbBundle.getMessage(DbStructureUtils.class, "BadClipboardData"), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                            Logger.getLogger(PasteTablesAction.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

        private void pasteTables(DbSchemeModel innerModel, Document doc) throws Exception {
            DbSchemeModel outerModel = XmlDom2DbSchemeModel.transform(model.getBasesProxy(), doc);
            if (outerModel != null) {
                Map<Long, FieldsEntity> entities = outerModel.getEntities();
                List<FieldsEntity> alreadyExistentEntities = new ArrayList<>();
                undoSupport.beginUpdate();
                try {
                    Map<Long, FieldsEntity> innerEntities = innerModel.getEntities();
                    if (entities != null && !entities.isEmpty()
                            && innerEntities != null) {
                        Set<Entry<Long, FieldsEntity>> entSet = entities.entrySet();
                        if (entSet != null) {
                            for (Entry<Long, FieldsEntity> entEntry : entSet) {
                                Long id = entEntry.getKey();
                                FieldsEntity toPaste = entEntry.getValue();
                                if (innerEntities.containsKey(id)) {
                                    toPaste.regenerateId();
                                }
                                toPaste.setModel(innerModel);
                                String lName = toPaste.getTableName();
                                if (innerModel.getEntityByTableName(lName) == null) {
                                    if (innerModel.checkEntityAddingValid(toPaste)) {
                                        prepareEntityForPaste(toPaste);
                                        NewEntityEdit<FieldsEntity, DbSchemeModel> edit = new NewEntityEdit<>(model, toPaste);
                                        edit.redo();
                                        undoSupport.postEdit(edit);
                                        addFkRelations(false, toPaste.getTableName());
                                    }
                                } else {
                                    alreadyExistentEntities.add(toPaste);
                                }
                            }
                        }
                    }
                } finally {
                    undoSupport.endUpdate();
                }
                reportWasNotAddedEntities(alreadyExistentEntities);
                if (entities != null) {
                    for (FieldsEntity entity : entities.values()) {
                        if (entity != null && !alreadyExistentEntities.contains(entity)) {
                            EntityView<FieldsEntity> eView = getEntityView(entity);
                            DbSchemeModelView.this.makeVisible(eView, true);
                        }
                    }
                }
            }
        }

        private void reportWasNotAddedEntities(List<FieldsEntity> aEntities) {
            String msg;
            if (!aEntities.isEmpty()) {
                if (aEntities.size() == 1) {
                    msg = NbBundle.getMessage(DbStructureUtils.class, "EAS_TABLE_ALREADY_PRESENT", aEntities.get(0).getTableName() + " (" + aEntities.get(0).getTitle() + ")", null);
                } else {
                    String tablesList = "";
                    for (int i = 0; i < aEntities.size(); i++) {
                        if (i > 0) {
                            tablesList += ", ";
                        }
                        tablesList += aEntities.get(0).getTableName() + " (" + aEntities.get(0).getTitle() + ")";
                    }
                    msg = NbBundle.getMessage(DbStructureUtils.class, "EAS_TABLES_ALREADY_PRESENT", tablesList, null);
                }
                JOptionPane.showMessageDialog(DbSchemeModelView.this, msg, NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public class RelationPropertiesAction extends AbstractAction {

        public RelationPropertiesAction() {
            super();
            putValue(Action.NAME, NbBundle.getMessage(DbStructureUtils.class, RelationPropertiesAction.class.getSimpleName()));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return (isSelectedRelations() && getSelectedRelations().size() == 1)
                    || getSelectedFkFieldRelation() != null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                Window w = SwingUtilities.getWindowAncestor(DbSchemeModelView.this);
                if (w instanceof JFrame) {
                    Relation<FieldsEntity> relation;
                    if (isSelectedRelations() && getSelectedRelations().size() == 1) {
                        relation = getSelectedRelations().iterator().next();
                    } else {
                        relation = getSelectedFkFieldRelation();
                    }
                    askForeignKeyProperties((JFrame) w, relation);
                }
            }
        }
    }

    public void askForeignKeyProperties(JFrame aPrentFrame, Relation<FieldsEntity> aRelation) {
        final ForeignKeySettingsView fkView = new ForeignKeySettingsView();
        fkView.setUpdateRule(aRelation.getFkUpdateRule());
        fkView.setDeleteRule(aRelation.getFkDeleteRule());
        fkView.setDeferred(aRelation.isFkDeferrable());
        fkView.setConstraintName(aRelation.getFkName());

        SettingsDialog dlg = new SettingsDialog(aPrentFrame, fkView, true, new SettingsDialog.Checker() {
            @Override
            public boolean check() {
                String constraintName = fkView.getConstraintName();
                if (constraintName != null && !constraintName.isEmpty()) {
                    return true;
                } else {
                    fkView.focusConstraintName();
                    return false;
                }
            }
        });
        Dimension size = new Dimension(450, 200);
        dlg.setTitle(NbBundle.getMessage(DbStructureUtils.class, "ForeignKeySettingsDialogTitle"));
        dlg.setSize(size);
        dlg.setMinimumSize(size);
        dlg.setVisible(true);
        if (dlg.isOkClose()) {
            ForeignKeyRule ur = fkView.getUpdateRule();
            ForeignKeyRule dr = fkView.getDeleteRule();
            Boolean deferred = fkView.isDeferred();
            String cName = fkView.getConstraintName();
            assert ur != null;
            assert dr != null;
            assert deferred != null;
            assert cName != null;
            // edit db and diagram
            ForeignKeySpec oldFk = DbStructureUtils.constructFkSpecByRelation(aRelation);
            ForeignKeySpec newFk = (ForeignKeySpec) oldFk.copy();
            newFk.setCName(cName);
            newFk.setFkUpdateRule(ur);
            newFk.setFkDeleteRule(dr);
            newFk.setFkDeferrable(deferred);
            if (!newFk.equals(oldFk)) {
                RecreateFkEdit dbEdit = new RecreateFkEdit(sqlController, oldFk, newFk);

                CompoundEdit section = new DbStructureCompoundEdit();
                try {
                    dbEdit.redo();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                section.addEdit(dbEdit);
                try {
                    ChangeRelationTagsEdit diagramEdit = new ChangeRelationTagsEdit(aRelation);
                    diagramEdit.recordBeforeState(aRelation);
                    aRelation.setFkUpdateRule(ur);
                    aRelation.setFkDeleteRule(dr);
                    aRelation.setFkDeferrable(deferred);
                    aRelation.setFkName(cName);
                    diagramEdit.recordAfterState(aRelation);
                    section.addEdit(diagramEdit);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                section.end();
                undoSupport.postEdit(section);
            }
        }
    }

    public DbSchemeModelView(TablesSelectorCallback aSelectorCallback) {
        super(aSelectorCallback);
        undoSupport = new DbStructureUndoableEditSupport();
    }

    @Override
    protected void putEditingActions() {
        super.putEditingActions();
        ActionMap am = getActionMap();
        am.put(ModelView.AddField.class.getSimpleName(), new AddTableFieldAction());
        am.put(ModelView.AddTable.class.getSimpleName(), new AddTableAction());
        am.put(ModelView.Delete.class.getSimpleName(), new DropFkRemoveTableAction());
        am.put(ModelView.Paste.class.getSimpleName(), new PasteTablesAction());
        am.put(CreateTableAction.class.getSimpleName(), new CreateTableAction());
        am.put(DropTableAction.class.getSimpleName(), new DropTableAction());
        am.put(RelationPropertiesAction.class.getSimpleName(), new RelationPropertiesAction());
    }

    public DbSchemeModelView(DbSchemeModel aModel, TablesSelectorCallback aSelectorCallback) {
        this(aSelectorCallback);
        setModel(aModel);
    }

    public DbSchemeModelView(SqlActionsController aSqlActionsController, TablesSelectorCallback aSelectorCallback) {
        this(aSelectorCallback);
        setSqlActionsController(aSqlActionsController);
    }

    public DbSchemeModelView(DbSchemeModel aModel, SqlActionsController aSqlActionsController, TablesSelectorCallback aSelectorCallback) {
        this(aSelectorCallback);
        try {
            setModel(aModel);
            setSqlActionsController(aSqlActionsController);
        } catch (Exception ex) {
            Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected boolean editable;

    @Override
    public void setModel(DbSchemeModel aModel) {
        super.setModel(aModel);
        try {
            editable = model != null && !(model.getBasesProxy().getMetadataCache(model.getDatasourceName()).getDatasourceSqlDriver() instanceof GenericSqlDriver);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public SqlActionsController getSqlActionsController() {
        return sqlController;
    }

    public void setSqlActionsController(SqlActionsController aSqlActionsController) {
        sqlController = aSqlActionsController;
    }

    @Override
    protected void copySelectedEntities() {
        DbSchemeModel copied = new DbSchemeModel(model.getBasesProxy());
        selectedEntities.stream().forEach((FieldsEntity ent) -> {
            if (ent != null) {
                copied.getEntities().put(ent.getEntityId(), ent);
            }
        });
        Document doc = DbSchemeModel2XmlDom.transform(copied);
        String content = XmlDom2String.transform(doc);
        string2SystemClipboard(content);
    }

    public void resolveTables() throws Exception {
        Map<Long, FieldsEntity> entities = model.getEntities();
        if (entities != null && !entities.isEmpty()) {
            boolean touched = false;
            MetadataCache mdCache = model.getBasesProxy().getMetadataCache(sqlController.getDatasourceName());
            List<FieldsEntity> entities2Delete = new ArrayList<>();
            for (FieldsEntity entity : entities.values()) {
                if (entity != null) {
                    if (!mdCache.containsTableMetadata(entity.getFullTableName())) {
                        entity.setTableName(entity.getTableName().toLowerCase());
                        touched = true;
                        if (!mdCache.containsTableMetadata(entity.getFullTableName())) {
                            entity.setTableName(entity.getTableName().toUpperCase());
                            if (!mdCache.containsTableMetadata(entity.getFullTableName())) {
                                entities2Delete.add(entity);
                            }
                        }
                    }
                }
            }
            for (FieldsEntity fEntity : entities2Delete) {
                DeleteEntityEdit<FieldsEntity, DbSchemeModel> edit = new DeleteEntityEdit<>(model, fEntity);
                edit.redo();
            }
            if (touched) {
                recreateEntityViews();
            }
        }
    }

    public void resolveRelations() throws Exception {
        model.clearRelations();
        addFkRelations(true, null);
    }

}
