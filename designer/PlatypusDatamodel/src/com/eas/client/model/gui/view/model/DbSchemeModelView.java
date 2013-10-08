/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.model;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.ForeignKeySpec.ForeignKeyRule;
import com.eas.client.DbMetadataCache;
import com.eas.client.SQLUtils;
import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.IconCache;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.CreateConstraintAction;
import com.eas.client.dbstructure.SqlActionsController.DefineTableAction;
import com.eas.client.dbstructure.SqlActionsController.DropFieldAction;
import com.eas.client.dbstructure.SqlActionsController.ModifyFieldAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.dbstructure.gui.edits.*;
import com.eas.client.model.gui.SettingsDialog;
import com.eas.client.dbstructure.gui.view.ForeignKeySettingsView;
import com.eas.client.dbstructure.store.XmlDom2DbSchema;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.*;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.DmAction;
import com.eas.client.model.gui.edits.DeleteEntityEdit;
import com.eas.client.model.gui.edits.NewEntityEdit;
import com.eas.client.model.gui.edits.NewRelationEdit;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.entities.TableEntityView;
import com.eas.client.model.store.XmlDom2DbSchemeModel;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.designer.datamodel.nodes.FieldNode;
import com.eas.xml.dom.Source2XmlDom;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
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
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class DbSchemeModelView extends ModelView<FieldsEntity, FieldsEntity, DbSchemeModel> {

    protected SqlActionsController sqlController;

    /**
     * Imports structure from an xml string representing schema information
     * about database structure
     *
     * @param content String, containing xml with structure.
     */
    public void importStructure(String content) {
        Document doc = Source2XmlDom.transform(content);
        if (doc != null) {
            try {
                DbSchemeModel sourceModel = XmlDom2DbSchema.transform(model.getClient(), doc);
                resolveFieldsToDBMS(sourceModel);
                turnOffReqiredFields(sourceModel);
                importTablesCreations(sourceModel);
                importFkCreations(sourceModel);
                DbSchemeModel sourceModel1 = XmlDom2DbSchema.transform(model.getClient(), doc);
                resolveFieldsToDBMS(sourceModel1);
                mergeFields(sourceModel1);
                entireSynchronizeWithDb();
            } catch (Exception ex) {
                Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Creates tables and foreign keys, absent in target schema and present in
     * imported infomation.
     *
     * @param aSource Imported information container.
     * @see DbSchemeModel
     */
    private void importTablesCreations(DbSchemeModel aSource) {
        DbSchemeModel sModel = (DbSchemeModel) model;
        // import tables
        for (FieldsEntity entity : aSource.getEntities().values()) {
            String tableName = entity.getTableName();
            if (sModel.getEntityByTableName(tableName) == null) {
                DefineTableAction laction = sqlController.createDefineTableAction(tableName, entity.getFields());
                if (!laction.execute()) {
                    try {
                        DbActionException ex = new DbActionException(laction.getErrorString());
                        ex.setParam1(tableName);
                        throw ex;
                    } catch (DbActionException ex1) {
                        Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, ex1.getMessage());
                    }
                } else {
                    FieldsEntity newEntity = new FieldsEntity(sModel);
                    newEntity.setTableName(tableName);
                    newEntity.setX(entity.getX());
                    newEntity.setY(entity.getY());
                    newEntity.setWidth(entity.getWidth());
                    newEntity.setHeight(entity.getHeight());
                    newEntity.setTitle(entity.getTitle());
                    newEntity.setFields(entity.getFields());
                    sModel.addEntity(newEntity);
                }
            }
        }
    }

    /**
     * Creates tables and foreign keys, absent in target schema and present in
     * imported infomation.
     *
     * @param aSource Imported information container.
     * @see DbSchemeModel
     */
    private void importFkCreations(DbSchemeModel aSource) {
        // import foreign keys
        for (Relation<FieldsEntity> relation : aSource.getRelations()) {
            ForeignKeySpec fkSpec = DbStructureUtils.constructFkSpecByRelation(relation);
            CreateConstraintAction laction = sqlController.createCreateConstraintAction(fkSpec);
            if (!laction.execute()) {
                try {
                    DbActionException ex = new DbActionException(laction.getErrorString());
                    ex.setParam1(relation.getLeftEntity().getTableName());
                    ex.setParam2(relation.getLeftField().getName());
                    throw ex;
                } catch (DbActionException ex1) {
                    Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, ex1.getMessage());
                }
            }
        }
    }

    /**
     * Merges all fields in all entities. Creates fields absent in target model
     * and present in imported information. Deletes fields present in target
     * model and absent in imported information. Redefines fields present in
     * target model and in imported information if thay are defferent. Side
     * effect of this is that some fields become reqired, rather while
     * importCreations moethod execution
     *
     * @param aSource Imported information container.
     * @see DbSchemeModel
     * @see #importCreations(null)
     */
    private void mergeFields(DbSchemeModel aSource) {
        DbSchemeModel sModel = (DbSchemeModel) model;
        // import tables
        for (FieldsEntity sEntity : aSource.getEntities().values()) {
            FieldsEntity dEntity = sModel.getEntityByTableName(sEntity.getTableName());
            if (dEntity != null) {
                Fields sFields = sEntity.getFields();
                Fields dFields = dEntity.getFields();

                // new fields creation
                for (int i = 1; i <= sFields.getFieldsCount(); i++) {
                    Field sField = sFields.get(i);
                    if (!dFields.contains(sField.getName())) {
                        // let's create new field
                        SqlActionsController.AddFieldAction laction = sqlController.createAddFieldAction(dEntity.getTableName(), sField);
                        if (!laction.execute()) {
                            try {
                                DbActionException ex = new DbActionException(laction.getErrorString());
                                ex.setParam1(dEntity.getTableName());
                                ex.setParam2(sField.getName());
                                throw ex;
                            } catch (DbActionException ex1) {
                                Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, ex1.getMessage());
                            }
                        }
                    } else {
                        Field dField = dFields.get(sField.getName());
                        assert dField != null : sField.getName() + " contains but it's not returned by get(iny)'";
                        if (!dField.isEqual(sField)) {
                            // let's redefine field with same name, but different content
                            ModifyFieldAction laction = sqlController.createModifyFieldAction(dEntity.getTableName(), dField, sField);
                            if (!laction.execute()) {
                                try {
                                    DbActionException ex = new DbActionException(laction.getErrorString());
                                    ex.setParam1(dEntity.getTableName());
                                    throw ex;
                                } catch (DbActionException ex1) {
                                    Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, ex1.getMessage());
                                }
                            }
                        }
                    }
                }
                // absent in the source fields removing from target table
                for (int i = 1; i <= dFields.getFieldsCount(); i++) {
                    Field dField = dFields.get(i);
                    if (!sFields.contains(dField.getName())) {
                        // let's remove absent in source field from target
                        DropFieldAction laction = sqlController.createDropFieldAction(dEntity.getTableName(), dField);
                        if (!laction.execute()) {
                            try {
                                DbActionException ex = new DbActionException(laction.getErrorString());
                                ex.setParam1(dEntity.getTableName());
                                ex.setParam2(dField.getName());
                                throw ex;
                            } catch (DbActionException ex1) {
                                Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, ex1.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    private void turnOffReqiredFields(DbSchemeModel aModel) {
        for (FieldsEntity entity : aModel.getEntities().values()) {
            Fields fields = entity.getFields();
            for (int i = 1; i <= fields.getFieldsCount(); i++) {
                Field field = fields.get(i);
                field.setNullable(true);
            }
        }
    }

    private void resolveFieldsToDBMS(DbSchemeModel aModel) throws Exception {
        DbSchemeModel sModel = (DbSchemeModel) model;
        DbMetadataCache mdCache = sModel.getClient().getDbMetadataCache(sModel.getDbId());
        SqlDriver sqlDriver = mdCache.getConnectionDriver();
        TypesResolver tResolver = sqlDriver.getTypesResolver();
        for (FieldsEntity entity : aModel.getEntities().values()) {
            Fields fields = entity.getFields();
            for (int i = 1; i <= fields.getFieldsCount(); i++) {
                Field field = fields.get(i);
                tResolver.resolve2RDBMS(field);
            }
        }
    }

    public void resolveRelations() throws Exception {
        model.clearRelations();
        addFkRelations(true, null);
    }

    @Override
    public void doAddQuery(String aApplicationElementId, int aX, int aY) throws Exception {
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
            DbMetadataCache mdCache = model.getClient().getDbMetadataCache(model.getDbId());
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
                                        ForeignKeySpec fkSpec = (ForeignKeySpec) fkField.getFk();
                                        String refereeTableName = fkSpec.getReferee().getTable();
                                        if (refereeTableName != null
                                                && (only4Table == null || (only4Table != null && (only4Table.equalsIgnoreCase(refereeTableName) || only4Table.equalsIgnoreCase(leftTableName))))
                                                && entitiesByTableName.containsKey(refereeTableName.toLowerCase())) {
                                            FieldsEntity refereeEntity = entitiesByTableName.get(refereeTableName.toLowerCase());
                                            if (!DatamodelDesignUtils.isRelationAlreadyDefined(entity, entity.getFields().get(fkSpec.getField()), refereeEntity, refereeEntity.getFields().get(fkSpec.getReferee().getField()))) {
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

    @Override
    protected TableRef prepareTableRef4Selection() {
        TableRef oldValue = new TableRef();
        oldValue.dbId = model.getDbId();
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
        return (DbSchemeModel) XmlDom2DbSchemeModel.transform(model.getClient(), aDoc);
    }

    @Override
    protected boolean isParametersEntity(FieldsEntity aEntity) {
        return false;
    }

    @Override
    protected EntityView<FieldsEntity> createGenericEntityView(FieldsEntity aEntity) {
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
    protected DbSchemeModel newModelInstance() {
        return new DbSchemeModel(model.getClient());
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
        return isSelectedFieldsOnOneEntity() && !isSelectedFk();
    }

    private boolean isSelectedFk() {
        for (EntityFieldTuple eft : selectedFields) {
            if (eft.field.isPk()) {
                return true;
            }
        }
        return false;
    }

    public class AddTableFieldAction extends ModelView.AddField {

        @Override
        public boolean isEnabled() {
            return getSelectedEntities().size() == 1 || isSelectedFieldsOnOneEntity();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                FieldsEntity entity = (FieldsEntity) getEntity();
                DbSchemeModel model = entity.getModel();
                if (model != null) {
                    NewFieldEdit fieldEdit;
                    try {
                        Field field = NewFieldEdit.createField(entity);
                        PropertySheet ps = new PropertySheet();
                        ps.setNodes(new Node[]{new FieldNode(field, Lookups.fixed(entity), true)});
                        DialogDescriptor dd = new DialogDescriptor(ps, NbBundle.getMessage(DbSchemeModelView.class, "MSG_NewSchemeFieldDialogTitle"));
                        if (DialogDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
                            fieldEdit = new NewFieldEdit(sqlController, entity, field);
                            fieldEdit.redo();
                        } else {
                            return;
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    undoSupport.notSavable();
                    undoSupport.beginUpdate();
                    try {
                        undoSupport.postEdit(fieldEdit);
                        com.eas.client.model.gui.edits.fields.NewFieldEdit edit = new com.eas.client.model.gui.edits.fields.NewFieldEdit(entity, fieldEdit.getField());
                        edit.redo();
                        undoSupport.postEdit(edit);
                        checkActions();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                    } finally {
                        undoSupport.endUpdate();
                    }
                }
            }
        }

        @Override
        protected Entity getEntity() {
            if (!getSelectedEntities().isEmpty()) {
                return getSelectedEntities().iterator().next();
            } else if (!getSelectedFields().isEmpty()) {
                return (FieldsEntity) getSelectedFields().iterator().next().entity;
            }
            throw new IllegalStateException();
        }
    }

    public class AddTableAction extends ModelView<FieldsEntity, FieldsEntity, DbSchemeModel>.AddTable {

        @Override
        public void actionPerformed(ActionEvent e) {
            undoSupport.beginUpdate();
            try {
                needRerouteConnectors = false;
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
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            needRerouteConnectors = true;
                            refreshView();
                        }
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
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return isShowing();
        }

        @Override
        public String getDmActionText() {
            return DbStructureUtils.getString(CreateTableAction.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DbStructureUtils.getString(CreateTableAction.class.getSimpleName() + ".hint");
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
                tableName = JOptionPane.showInputDialog(DbSchemeModelView.this, DbStructureUtils.getString("inputTableName"), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.QUESTION_MESSAGE);
            }
            if (tableName != null) {
                CompoundEdit section = new DbStructureCompoundEdit();
                CreateTableEdit edit = new CreateTableEdit(sqlController, tableName);
                try {
                    edit.redo();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
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
            return isShowing() && isAnySelectedEntities();
        }

        @Override
        public String getDmActionText() {
            return DbStructureUtils.getString(DropTableAction.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DbStructureUtils.getString(DropTableAction.class.getSimpleName() + ".hint");
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
                    SqlCompiledQuery query = new SqlCompiledQuery(model.getClient(), tableEntity.getTableDbId(), "select count(*) cnt from " + fullTableName);
                    Rowset rs = query.executeQuery();
                    if (rs != null) {
                        if (rs.first()) {
                            Object cnt = rs.getObject(1);
                            if (cnt instanceof Number) {
                                return ((Number) cnt).intValue();
                            } else {
                                return 0;
                            }
                        }
                    }
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
                        msg = DbStructureUtils.getString("areYouSureInRelationsPresent", String.valueOf(inRels.size()), null);
                    } else if (rCount > 0 && inRels.isEmpty()) {
                        msg = DbStructureUtils.getString("areYouSureDataPresent", String.valueOf(rCount), null);
                    } else if (rCount > 0 && !inRels.isEmpty()) {
                        msg = DbStructureUtils.getString("areYouSureInRelationsDataPresent", String.valueOf(inRels.size()), String.valueOf(rCount));
                    }
                    if (msg == null || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(DbSchemeModelView.this, msg, DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
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
                int userChoice = JOptionPane.showConfirmDialog(DbSchemeModelView.this, ex.getLocalizedMessage() + " \n" + DbStructureUtils.getString("removeFromDiagram"), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
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

    public class DropFkRemoveTableAction extends ModelView<FieldsEntity, FieldsEntity, DbSchemeModel>.Delete {

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
                        JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
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
                            JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
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
                            dropTable(getSelectedEntities(), sqlController, e);
                            break;
                    }
                }
            }
        }

        protected int getDeleteTypeOption() {
            Object[] options = {
                DbStructureUtils.getString("dlgDeleteFromDiagram"),
                DbStructureUtils.getString("dlgDropTable"),
                DbStructureUtils.getString("dlgCancel")
            };
            return JOptionPane.showOptionDialog(DbSchemeModelView.this,
                    DbStructureUtils.getString("dlgDeleteTableMsg"),
                    DbStructureUtils.getString("dlgDeleteTableTitle"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);
        }

        private void dropFields(ActionEvent e) {
            if (isEnabled()) {
                // Remove fields from database and the diagram
                FieldsEntity entity = (FieldsEntity) getSelectedFields().iterator().next().entity;
                Set<Relation<FieldsEntity>> toConfirm = new HashSet<>();
                for (EntityFieldTuple etf : getSelectedFields()) {
                    Set<Relation<FieldsEntity>> toDel = FieldsEntity.<FieldsEntity>getInOutRelationsByEntityField(entity, etf.field);
                    toConfirm.addAll(toDel);
                }
                if (!toConfirm.isEmpty()) {
                    if (JOptionPane.showConfirmDialog(DbSchemeModelView.this,
                            DatamodelDesignUtils.getLocalizedString("ifDeleteRelationsReferences"),
                            DbStructureUtils.getString("dbSchemeEditor"),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                }
                CompoundEdit section = new DbStructureCompoundEdit();
                for (EntityFieldTuple etf : getSelectedFields()) {
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
                            JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                // act with indexes
                List<DbTableIndexSpec> indexes = entity.getIndexes();
                if (indexes != null) {
                    // collect indexes to drop
                    Set<DbTableIndexSpec> indexes2Drop = new HashSet<>();
                    for (EntityFieldTuple etf : getSelectedFields()) {
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
                for (EntityFieldTuple etf : getSelectedFields()) {
                    Field field = etf.field;
                    if (!field.isPk()) {
                        try {
                            dropField(entity, section, field, e);
                        } catch (CannotRedoException ex) {
                            section.end();
                            if (section.isSignificant()) {
                                undoSupport.postEdit(section);
                            }
                            JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
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

        private boolean dropField(FieldsEntity entity, CompoundEdit aEdit, Field field, ActionEvent e) {
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
                    msg = DbStructureUtils.getString("areYouSureFieldInRelationsPresent", String.valueOf(inRels.size()), null);
                } else if (rCount > 0 && inRels.isEmpty()) {
                    msg = DbStructureUtils.getString("areYouSureFieldDataPresent", String.valueOf(rCount), null);
                } else if (rCount > 0 && !inRels.isEmpty()) {
                    msg = DbStructureUtils.getString("areYouSureFieldInRelationsDataPresent", String.valueOf(inRels.size()), String.valueOf(rCount));
                }
                if (msg == null || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(DbSchemeModelView.this, msg, DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                    return doDropField(entity, aEdit, field, e);
                }
            }
            return true;
        }

        private boolean doDropField(FieldsEntity entity, CompoundEdit aEdit, Field field, ActionEvent e) {
            DropFieldEdit edit = new DropFieldEdit(sqlController, field, entity);
            edit.redo();
            assert edit != null;
            aEdit.addEdit(edit);
            return true;
        }

        private void dropTable(Set<FieldsEntity> tableEntities, SqlActionsController sqlController, ActionEvent e) {
            if (tableEntities != null) {
                for (FieldsEntity tableEntity : tableEntities) {
                    // act with table and it's entity.
                    // Dropping foreign keys, table and removing theirs entities and relations
                    Set<Relation<FieldsEntity>> inRels = tableEntity.getInRelations();
                    int rCount = getRecordsCount(tableEntity);
                    String msg = null;
                    if (rCount == 0 && !inRels.isEmpty()) {
                        msg = DbStructureUtils.getString("areYouSureInRelationsPresent", String.valueOf(inRels.size()), null);
                    } else if (rCount > 0 && inRels.isEmpty()) {
                        msg = DbStructureUtils.getString("areYouSureDataPresent", String.valueOf(rCount), null);
                    } else if (rCount > 0 && !inRels.isEmpty()) {
                        msg = DbStructureUtils.getString("areYouSureInRelationsDataPresent", String.valueOf(inRels.size()), String.valueOf(rCount));
                    }
                    if (msg == null || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(DbSchemeModelView.this, msg, DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                        doDropTable(tableEntity, sqlController, e);
                    }
                }
            }
        }

        public void doDropTable(FieldsEntity tableEntity, SqlActionsController sqlController, ActionEvent e) {
            EntityView<FieldsEntity> eView = getEntityView(tableEntity);
            DropTableEdit edit = new DropTableEdit(sqlController, tableEntity.getTableName(), eView.getFields(), tableEntity);
            try {
                edit.redo();
            } catch (Exception ex) {
                int userChoice = JOptionPane.showConfirmDialog(DbSchemeModelView.this, ex.getLocalizedMessage() + " \n" + DbStructureUtils.getString("removeFromDiagram"), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (userChoice == JOptionPane.YES_OPTION) {
                    super.actionPerformed(e);
                }
                return;
            }
            undoSupport.beginUpdate();
            try {
                undoSupport.postEdit(edit);
                super.actionPerformed(e);
            } finally {
                undoSupport.endUpdate();
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
                    SqlCompiledQuery query = new SqlCompiledQuery(model.getClient(), tableEntity.getTableDbId(), "select count(*) cnt from " + fullTableName);
                    Rowset rs = query.executeQuery();
                    if (rs != null) {
                        if (rs.first()) {
                            Object cnt = rs.getObject(1);
                            if (cnt instanceof Number) {
                                return ((Number) cnt).intValue();
                            } else {
                                return 0;
                            }
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(DbSchemeModelView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return 0;
        }
    }

    public class PasteTablesAction extends ModelView<FieldsEntity, FieldsEntity, DbSchemeModel>.Paste {

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
                                    JOptionPane.showMessageDialog(DbSchemeModelView.this, DbStructureUtils.getString("BadClipboardData"), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(DbSchemeModelView.this, DbStructureUtils.getString("BadClipboardData"), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                            Logger.getLogger(PasteTablesAction.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

        private void pasteTables(DbSchemeModel innerModel, Document doc) throws Exception {
            DbSchemeModel outerModel = XmlDom2DbSchemeModel.transform(model.getClient(), doc);
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
                    msg = DbStructureUtils.getString("EAS_TABLE_ALREADY_PRESENT", aEntities.get(0).getTableName() + " (" + aEntities.get(0).getTitle() + ")", null);
                } else {
                    String tablesList = "";
                    for (int i = 0; i < aEntities.size(); i++) {
                        if (i > 0) {
                            tablesList += ", ";
                        }
                        tablesList += aEntities.get(0).getTableName() + " (" + aEntities.get(0).getTitle() + ")";
                    }
                    msg = DbStructureUtils.getString("EAS_TABLES_ALREADY_PRESENT", tablesList, null);
                }
                JOptionPane.showMessageDialog(DbSchemeModelView.this, msg, DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public class RelationPropertiesAction extends AbstractAction {

        public RelationPropertiesAction() {
            super();
            putValue(Action.NAME, DbStructureUtils.getString(RelationPropertiesAction.class.getSimpleName()));
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
        dlg.setTitle(DbStructureUtils.getString("ForeignKeySettingsDialogTitle"));
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
                    JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(DbSchemeModelView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
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
        ActionMap am = getActionMap();
        am.put(AddTableFieldAction.class.getSimpleName(), new AddTableFieldAction());
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

    public SqlActionsController getSqlActionsController() {
        return sqlController;
    }

    public void setSqlActionsController(SqlActionsController aSqlActionsController) {
        sqlController = aSqlActionsController;
    }

    public void resolveTables() throws Exception {
        Map<Long, FieldsEntity> entities = model.getEntities();
        if (entities != null && !entities.isEmpty()) {
            List<FieldsEntity> entities2Delete = new ArrayList<>();
            for (FieldsEntity entity : entities.values()) {
                if (entity != null) {
                    if (!isEntityTableExists(entity)) {
                        entities2Delete.add(entity);
                        getModel().getClient().dbTableChanged(sqlController.getDbId(), sqlController.getSchema(), entity.getTableName());
                    }
                }
            }
            for (FieldsEntity fEntity : entities2Delete) {
                DeleteEntityEdit<FieldsEntity, DbSchemeModel> edit = new DeleteEntityEdit<>(model, fEntity);
                edit.redo();
            }
            if (!entities2Delete.isEmpty()) {
                createEntityViews();
            }
        }
    }

    private void resolveFields() throws Exception {
        Map<Long, FieldsEntity> entities = model.getEntities();
        if (entities != null) {
            for (FieldsEntity entity : entities.values()) {
                model.getClient().dbTableChanged(entity.getTableDbId(), entity.getTableSchemaName(), entity.getTableName());
            }
        }
    }

    private void resolveIndexes() {
        Map<Long, FieldsEntity> entities = model.getEntities();
        if (entities != null) {
            for (FieldsEntity entity : entities.values()) {
                entity.achiveIndexes();
            }
        }
    }

    public void entireSynchronizeWithDb() throws Exception {
        model.removeEditingListener(modelListener);
        try {
            resolveTables();
            resolveFields();
            resolveIndexes();
            resolveRelations();
        } finally {
            model.addEditingListener(modelListener);
        }
        refreshView();
    }

    private boolean isEntityTableExists(FieldsEntity fEntity) {
        try {
            DbMetadataCache cache = model.getClient().getDbMetadataCache(sqlController.getDbId());
            String schema = fEntity.getTableSchemaName();
            if (schema == null) {
                schema = cache.getConnectionSchema();
            }
            String qtn = schema + "." + fEntity.getTableName();
            SqlCompiledQuery query = new SqlCompiledQuery(model.getClient(), sqlController.getDbId(), SQLUtils.makeTableNameMetadataQuery(qtn));
            Rowset rs = query.executeQuery();
            return rs != null;
        } catch (Exception ex) {
            return false;
        }
    }
}
