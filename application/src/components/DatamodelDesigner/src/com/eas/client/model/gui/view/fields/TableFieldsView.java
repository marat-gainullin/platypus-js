/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.fields;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.DbClient;
import com.eas.client.SQLUtils;
import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.gui.edits.CreateFkEdit;
import com.eas.client.dbstructure.gui.edits.DbStructureCompoundEdit;
import com.eas.client.dbstructure.gui.edits.DbStructureUndoableEditSupport;
import com.eas.client.dbstructure.gui.edits.DropFieldEdit;
import com.eas.client.dbstructure.gui.edits.DropFkEdit;
import com.eas.client.dbstructure.gui.edits.DropIndexEdit;
import com.eas.client.dbstructure.gui.edits.ModifyFieldEdit;
import com.eas.client.dbstructure.gui.edits.NewFieldEdit;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.edits.fields.ChangeFieldEdit;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.client.model.store.XmlDom2DbSchemeModel;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class TableFieldsView extends FieldsView<FieldsEntity, DbSchemeModel> {

    public class AddFieldAction extends FieldsView<FieldsEntity, DbSchemeModel>.AddField {

        @Override
        public boolean isEnabled() {
            return isShowing() && super.isEnabled();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                DbSchemeModel model = entity.getModel();
                if (model != null) {
                    NewFieldEdit fieldEdit = null;
                    try {
                        fieldEdit = new NewFieldEdit(sqlActionsController, entity, null);
                        fieldEdit.redo();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TableFieldsView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    assert (fieldEdit != null);
                    undoSupport.beginUpdate();
                    try {
                        undoSupport.postEdit(fieldEdit);
                        super.actionPerformed(e);
                    } catch (Exception ex) {
                        undoSupport.endUpdate();
                        return;
                    }
                    undoSupport.endUpdate();
                }
            }
        }
    }

    public class CopyFieldsAction extends FieldsView<FieldsEntity, DbSchemeModel>.Copy {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                Field[] fields = getSelectedFields();
                if (fields != null && fields.length > 0) {
                    try {
                        // Let's create an Application model to be able to copy anything.
                        // We'll be able to paste such data to any query or form/report model.
                        ApplicationDbModel copiedModel = new ApplicationDbModel(model.getClient());
                        Parameters parameters = copiedModel.getParameters();
                        assert parameters != null;
                        for (int i = 0; i < fields.length; i++) {
                            if (fields[i] != null) {
                                // fields[i] may be Parameter or Field.
                                // We need to copy strictly parameters.
                                parameters.add(new Parameter(fields[i]));
                            }
                        }
                        Document doc = copiedModel.toXML();
                        if (doc != null) {
                            String sEntity = XmlDom2String.transform(doc);
                            string2SystemClipboard(sEntity);
                        }
                    } catch (HeadlessException ex) {
                        Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public class PasteFieldsAction extends FieldsView<FieldsEntity, DbSchemeModel>.Paste {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                DbSchemeModel dm = entity.getModel();
                if (dm != null) {
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
                                        pasteFields(doc);
                                    } else {
                                        JOptionPane.showMessageDialog(TableFieldsView.this, DbStructureUtils.getString("BadClipboardData"), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(TableFieldsView.this, DbStructureUtils.getString("BadClipboardData"), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                                Logger.getLogger(PasteFieldsAction.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }

        private void pasteFields(Document doc) throws Exception {
            ApplicationDbModel outerModel = new ApplicationDbModel(model.getClient());
            outerModel.accept(new XmlDom2ApplicationModel<ApplicationDbEntity>(doc));
            if (outerModel != null) {
                Fields outerFields = outerModel.getParameters();
                if (outerFields != null) {
                    Fields innerFields = entity.getFields();
                    Set<String> innerNames = new HashSet<>();
                    try {
                        for (int i = 1; i <= innerFields.getFieldsCount(); i++) {
                            innerNames.add(innerFields.get(i).getName());
                        }
                        if (outerFields.getFieldsCount() > 0) {
                            CompoundEdit section = new DbStructureCompoundEdit();
                            try {
                                for (int i = 1; i <= outerFields.getFieldsCount(); i++) {
                                    // db
                                    Field newField = outerFields.get(i);
                                    newField.setFk(null);
                                    newField.setPk(false);
                                    int nameCounter = 0;
                                    String baseName = newField.getName();
                                    while (innerNames.contains(newField.getName())) {
                                        newField.setName(baseName + String.valueOf(++nameCounter));
                                    }
                                    NewFieldEdit dbEdit = new NewFieldEdit(sqlActionsController, entity, newField);
                                    dbEdit.redo();
                                    section.addEdit(dbEdit);
                                    // diagram
                                    com.eas.client.model.gui.edits.fields.NewFieldEdit<FieldsEntity> edit = new com.eas.client.model.gui.edits.fields.NewFieldEdit<>(entity);
                                    edit.redo();
                                    section.addEdit(edit);
                                    ChangeFieldEdit<FieldsEntity> cEdit = new ChangeFieldEdit<>(edit.getField(), newField, edit.getField(), entity);
                                    cEdit.redo();
                                    section.addEdit(cEdit);
                                    innerNames.add(newField.getName());
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(TableFieldsView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            section.end();
                            if (section.isSignificant()) {
                                undoSupport.postEdit(section);
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(PasteFieldsAction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public class DropFieldsAction extends FieldsView<FieldsEntity, DbSchemeModel>.Delete {

        @Override
        public boolean isEnabled() {
            boolean isPkSelected = false;
            if (isShowing() && isAnyFieldSelected()) {
                Field[] selectedFields = getSelectedFields();
                for (int i = 0; i < selectedFields.length; i++) {
                    if (selectedFields[i] != null && selectedFields[i].isPk()) {
                        isPkSelected = true;
                        break;
                    }
                }
            }
            return super.isEnabled() && !isPkSelected;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            forceRelationsDelete = false;
            if (isEnabled()) {
                // act with fields. Remove them from database and from the diagram
                Field[] selectedFields = getSelectedFields();
                FieldsEntity fEntity = entity;
                if (selectedFields != null && selectedFields.length > 0) {
                    Set<Relation<FieldsEntity>> toConfirm = new HashSet<>();
                    for (int i = 0; i < selectedFields.length; i++) {
                        Set<Relation<FieldsEntity>> toDel = FieldsEntity.<FieldsEntity>getInOutRelationsByEntityField(entity, selectedFields[i]);
                        toConfirm.addAll(toDel);
                    }
                    if (!toConfirm.isEmpty()) {
                        if (JOptionPane.showConfirmDialog(TableFieldsView.this, DatamodelDesignUtils.getLocalizedString("ifDeleteRelationsReferences"), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                    }
                    forceRelationsDelete = true;
                    CompoundEdit section = new DbStructureCompoundEdit();
                    // act with indexes
                    List<DbTableIndexSpec> indexes = fEntity.getIndexes();
                    if (indexes != null) {
                        // collect indexes to drop
                        Set<DbTableIndexSpec> indexes2Drop = new HashSet<>();
                        for (int i = 0; i < selectedFields.length; i++) {
                            Field field = selectedFields[i];
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
                            DropIndexEdit diEdit = new DropIndexEdit(sqlActionsController, fEntity, index, indexOrder);
                            try {
                                diEdit.redo();
                                section.addEdit(diEdit);
                            } catch (CannotRedoException ex) {
                                fEntity.setIndexes(null);
                                section.end();
                                if (section.isSignificant()) {
                                    undoSupport.postEdit(section);
                                }
                                JOptionPane.showMessageDialog(TableFieldsView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }
                    fEntity.setIndexes(null);
                    // act with fields
                    for (int i = 0; i < selectedFields.length; i++) {
                        Field field = selectedFields[i];
                        if (!field.isPk()) {
                            try {
                                processEntity(section, field, e);
                            } catch (CannotRedoException ex) {
                                section.end();
                                if (section.isSignificant()) {
                                    undoSupport.postEdit(section);
                                }
                                JOptionPane.showMessageDialog(TableFieldsView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }
                    // Remove fields and all of the referencing relations from the diagram
                    section.end();
                    if (section.isSignificant()) {
                        undoSupport.beginUpdate();
                        try {
                            undoSupport.postEdit(section);
                            super.actionPerformed(e);
                        } finally {
                            undoSupport.endUpdate();
                        }
                    }
                    forceRelationsDelete = false;
                }
            }
        }

        private boolean processEntity(CompoundEdit aEdit, Field field, ActionEvent e) {
            if (entity != null) {
                // act with field.
                // Dropping foreign keys, table and removing theirs entities and relations
                Set<Relation<FieldsEntity>> sourceInRels = entity.getInRelations();
                Set<Relation<FieldsEntity>> inRels = new HashSet<>();
                for (Relation<FieldsEntity> rel : sourceInRels) {
                    if (rel.getRightField().toLowerCase().equals(field.getName().toLowerCase())) {
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
                if (msg == null || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(TableFieldsView.this, msg, DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                    return editDbDiagram(aEdit, field, e);
                }
            }
            return true;
        }

        private boolean editDbDiagram(CompoundEdit aEdit, Field field, ActionEvent e) {
            DropFieldEdit edit = new DropFieldEdit(sqlActionsController, field, entity);
            edit.redo();
            assert edit != null;
            aEdit.addEdit(edit);
            return true;
        }
    }

    public class ChangeDbFieldNameAction extends ChangeFieldNameAction {

        @Override
        protected void editFieldsInDiagram() {
            if (!before.equals(after) && !before.getName().equalsIgnoreCase(after.getName())) {
                Logger.getLogger(TableFieldsView.class.getName()).fine(String.format("Changing field from %s to %s\n", before, after)); //NOI18N
                //assert !before.getName().toLowerCase().equals(after.getName().toLowerCase());
                CompoundEdit section = new DbStructureCompoundEdit();
                try {
                    // we have to recreate foreign keys in order to them to be compatible with new field names
                    // let's drop the foreign keys
                    for (Relation<FieldsEntity> rel2Del : toProcessRels) {
                        FieldsEntity lEntity = rel2Del.getLeftEntity();
                        FieldsEntity rEntity = rel2Del.getRightEntity();
                        ForeignKeySpec fkSpec = new ForeignKeySpec(lEntity.getTableSchemaName(), lEntity.getTableName(), rel2Del.getLeftField(), rel2Del.getFkName(), rel2Del.getFkUpdateRule(), rel2Del.getFkDeleteRule(), rel2Del.isFkDeferrable(), rEntity.getTableSchemaName(), rEntity.getTableName(), rel2Del.getRightField(), null);
                        DropFkEdit dEdit = new DropFkEdit(sqlActionsController, fkSpec, field);
                        dEdit.redo();
                        section.addEdit(dEdit);
                    }
                    // change the field name
                    ModifyFieldEdit dbEdit = new ModifyFieldEdit(sqlActionsController, entity.getTableName(), entity.getFields(), before, after);
                    dbEdit.redo();
                    section.addEdit(dbEdit);
                    // let's create the foreign keys
                    for (Relation<FieldsEntity> rel2Create : toProcessRels) {
                        FieldsEntity lEntity = rel2Create.getLeftEntity();
                        FieldsEntity rEntity = rel2Create.getRightEntity();
                        String leftFieldName = rel2Create.getLeftField();
                        if (getEntity() == lEntity && leftFieldName.toLowerCase().equals(before.getName().toLowerCase())) {
                            leftFieldName = after.getName();
                        }
                        String rightFieldName = rel2Create.getRightField();
                        if (getEntity() == rEntity && rightFieldName.toLowerCase().equals(before.getName().toLowerCase())) {
                            rightFieldName = after.getName();
                        }
                        ForeignKeySpec fkSpec = new ForeignKeySpec(lEntity.getTableSchemaName(), lEntity.getTableName(), leftFieldName, rel2Create.getFkName(), rel2Create.getFkUpdateRule(), rel2Create.getFkDeleteRule(), rel2Create.isFkDeferrable(), rEntity.getTableSchemaName(), rEntity.getTableName(), rightFieldName, null);
                        CreateFkEdit cEdit = new CreateFkEdit(sqlActionsController, fkSpec, field);
                        cEdit.redo();
                        section.addEdit(cEdit);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(TableFieldsView.class.getName()).log(Level.SEVERE, "Field modification error", ex);
                    JOptionPane.showMessageDialog(TableFieldsView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                    int sIndex = fieldsList.getSelectedIndex();
                    fieldsList.clearSelection();
                    fieldsList.setSelectedIndex(sIndex);
                    return;
                }
                section.end();
                undoSupport.beginUpdate();
                try {
                    undoSupport.postEdit(section);
                    // change the field name and referencing relations
                    super.editFieldsInDiagram();
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }
    }

    public class ChangeDbFieldDescriptionAction extends ChangeFieldDescriptionAction {

        @Override
        protected void editFieldsInDiagram() {
            ModifyFieldEdit dbEdit = editField(TableFieldsView.this, fieldsList, undoSupport, sqlActionsController, entity, before, after);
            if (dbEdit != null) {
                undoSupport.beginUpdate();
                try {
                    undoSupport.postEdit(dbEdit);
                    // change the field name and referencing relations
                    super.editFieldsInDiagram();
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }
    }

    public static ModifyFieldEdit editField(TableFieldsView aView, JList<Field> listParameters, UndoableEditSupport undoSupport, SqlActionsController sqlActionsController, FieldsEntity entity, Field before, Field after) {
        ModifyFieldEdit dbEdit = null;
        try {
            // change the field
            dbEdit = new ModifyFieldEdit(sqlActionsController, entity.getTableName(), entity.getFields(), before, after);
            dbEdit.redo();
        } catch (Exception ex) {
            Logger.getLogger(TableFieldsView.class.getName()).log(Level.SEVERE, "Field modification error: {0}", ex.getMessage());
            JOptionPane.showMessageDialog(aView, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
            int sIndex = listParameters.getSelectedIndex();
            listParameters.clearSelection();
            listParameters.setSelectedIndex(sIndex);
            return null;
        }
        return dbEdit;
    }

    public class ChangeDbFieldSizeAction extends ChangeFieldSizeAction {

        @Override
        public boolean isEnabled() {
            return super.isEnabled() || (field != null && field.getTypeInfo().getSqlType() == java.sql.Types.VARBINARY);
        }

        @Override
        protected void editFieldsInDiagram() {
            ModifyFieldEdit dbEdit = editField(TableFieldsView.this, fieldsList, undoSupport, sqlActionsController, entity, before, after);
            if (dbEdit != null) {
                undoSupport.beginUpdate();
                try {
                    undoSupport.postEdit(dbEdit);
                    // change the field name and referencing relations
                    super.editFieldsInDiagram();
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }
    }

    public class ChangeDbFieldScaleAction extends ChangeFieldScaleAction {

        @Override
        protected void editFieldsInDiagram() {
            ModifyFieldEdit dbEdit = editField(TableFieldsView.this, fieldsList, undoSupport, sqlActionsController, entity, before, after);
            if (dbEdit != null) {
                undoSupport.beginUpdate();
                try {
                    undoSupport.postEdit(dbEdit);
                    // change the field name and referencing relations
                    super.editFieldsInDiagram();
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }
    }

    public class ChangeDbFieldReqiredAction extends ChangeFieldReqiredAction {

        @Override
        protected void editFieldsInDiagram() {
            ModifyFieldEdit dbEdit = editField(TableFieldsView.this, fieldsList, undoSupport, sqlActionsController, entity, before, after);
            if (dbEdit != null) {
                undoSupport.beginUpdate();
                try {
                    undoSupport.postEdit(dbEdit);
                    // change the field reaireness
                    super.editFieldsInDiagram();
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }
    }

    protected static UndoableEdit editFieldTypeFk(DbClient aClient, TableFieldsView aView, JList<Field> listParameters, UndoableEditSupport undoSupport, SqlActionsController sqlActionsController, FieldsEntity entity, Field before, Field after, Field field, Set<Relation<FieldsEntity>> toProcessRels) {
        CompoundEdit section = null;
        int rCount = DbStructureUtils.getRecordsCountByField(entity, after.getName());
        String msg = null;
        String promtMsg1 = "areYouSureReTypeFieldInRelationsPresent";
        String promtMsg2 = "areYouSureReTypeFieldDataPresent";
        String promtMsg3 = "areYouSureReTypeFieldInRelationsDataPresent";
        if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.BLOB) || SQLUtils.isSameTypeGroup(before.getTypeInfo().getSqlType(), java.sql.Types.BLOB)) {
            promtMsg1 = "areYouSureBlobFieldInRelationsPresent";
            promtMsg2 = "areYouSureBlobFieldDataPresent";
            promtMsg3 = "areYouSureBlobFieldInRelationsDataPresent";
        }
        if (rCount == 0 && !toProcessRels.isEmpty()) {
            msg = DbStructureUtils.getString(promtMsg1, String.valueOf(toProcessRels.size()), null);
        } else if (rCount > 0 && toProcessRels.isEmpty()) {
            msg = DbStructureUtils.getString(promtMsg2, String.valueOf(rCount), null);
        } else if (rCount > 0 && !toProcessRels.isEmpty()) {
            msg = DbStructureUtils.getString(promtMsg3, String.valueOf(toProcessRels.size()), String.valueOf(rCount));
        }
        if (msg == null || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(aView, msg, DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
            section = new DbStructureCompoundEdit();
            try {
                // we have to remove foreign keys because of types incompatibility
                if (toProcessRels != null) {
                    for (Relation<FieldsEntity> rel2Del : toProcessRels) {
                        FieldsEntity lEntity = rel2Del.getLeftEntity();
                        FieldsEntity rEntity = rel2Del.getRightEntity();
                        ForeignKeySpec fkSpec = new ForeignKeySpec(lEntity.getTableSchemaName(), lEntity.getTableName(), rel2Del.getLeftField(), rel2Del.getFkName(), rel2Del.getFkUpdateRule(), rel2Del.getFkDeleteRule(), rel2Del.isFkDeferrable(), rEntity.getTableSchemaName(), rEntity.getTableName(), rel2Del.getRightField(), null);
                        DropFkEdit dEdit = new DropFkEdit(sqlActionsController, fkSpec, field);
                        dEdit.redo();
                        section.addEdit(dEdit);
                    }
                }
                // change the field type
                ModifyFieldEdit dbEdit = new ModifyFieldEdit(sqlActionsController, entity.getTableName(), entity.getFields(), before, after);
                dbEdit.redo();
                section.addEdit(dbEdit);
                aClient.dbTableChanged(entity.getTableDbId(), entity.getTableSchemaName(), entity.getTableName());
                int sIndex = -1;
                Fields fields = entity.getFields();
                for (int i = 1; i <= fields.getFieldsCount(); i++) {
                    Field lfield = fields.get(i);
                    if (lfield.getName().toLowerCase().equals(after.getName().toLowerCase())) {
                        sIndex = i - 1;
                    }
                }
                if (sIndex != -1) {
                    if (sIndex != listParameters.getSelectedIndex()) {
                        listParameters.setSelectedIndex(sIndex);
                    }
                } else {
                    listParameters.clearSelection();
                }
            } catch (Exception ex) {
                Logger.getLogger(TableFieldsView.class.getName()).log(Level.SEVERE, "Field modification error", ex);
                JOptionPane.showMessageDialog(aView, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                int sIndex = listParameters.getSelectedIndex();
                listParameters.clearSelection();
                listParameters.setSelectedIndex(sIndex);
                return null;
            }
            // remove relations because of types incompatibility and
            // change the field type
        }
        if (section != null) {
            section.end();
        }
        return section;
    }

    public class ChangeDbFieldTypeAction extends ChangeFieldTypeAction {

        @Override
        protected void editFieldsInDiagram() {
            UndoableEdit section = editFieldTypeFk(sqlActionsController.getClient(), TableFieldsView.this, fieldsList, undoSupport, sqlActionsController, entity, before, after, field, toProcessRels);
            if (section != null) {
                undoSupport.beginUpdate();
                try {
                    undoSupport.postEdit(section);
                    super.editFieldsInDiagram();
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }
    }

    public class ChangeDbFieldTypeNameAction extends ChangeFieldTypeNameAction {

        @Override
        protected void editFieldsInDiagram() {
            UndoableEdit section = editFieldTypeFk(sqlActionsController.getClient(), TableFieldsView.this, fieldsList, undoSupport, sqlActionsController, entity, before, after, field, toProcessRels);
            if (section != null) {
                undoSupport.beginUpdate();
                try {
                    undoSupport.postEdit(section);
                    super.editFieldsInDiagram();
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }
    }

    public class ChangeDbFieldFkAction extends ChangeFieldFkAction {

        @Override
        protected void editFieldsInDiagram() {
            UndoableEdit section = editFieldTypeFk(sqlActionsController.getClient(), TableFieldsView.this, fieldsList, undoSupport, sqlActionsController, entity, before, after, field, toProcessRels);
            if (section != null) {
                undoSupport.beginUpdate();
                try {
                    undoSupport.postEdit(section);
                    super.editFieldsInDiagram();
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }
    }

    public class ClearDbFieldFkAction extends ClearFieldFkAction {

        @Override
        protected void editFieldsInDiagram() {
            UndoableEdit section = editFieldTypeFk(sqlActionsController.getClient(), TableFieldsView.this, fieldsList, undoSupport, sqlActionsController, entity, before, after, field, toProcessRels);
            if (section != null) {
                undoSupport.beginUpdate();
                try {
                    undoSupport.postEdit(section);
                    super.editFieldsInDiagram();
                } finally {
                    undoSupport.endUpdate();
                }
            }
        }
    }
    protected SqlActionsController sqlActionsController = null;

    public TableFieldsView(SqlActionsController aSqlActionsController, TablesSelectorCallback aSelectorCallback) throws Exception {
        super(aSelectorCallback);
        sqlActionsController = aSqlActionsController;
        undoSupport = new DbStructureUndoableEditSupport();
        btnFkTable.setVisible(false);
        btnFkDel.setVisible(false);
    }

    @Override
    public DbSchemeModel newModel() {
        return new DbSchemeModel();
    }

    @Override
    protected DbSchemeModel document2Model(Document aDoc) throws Exception {
        return XmlDom2DbSchemeModel.transform(model.getClient(), aDoc);
    }

    @Override
    protected void fillActions() {
        super.fillActions();
        ActionMap am = getActionMap();

        am.put(FieldsView.AddField.class.getSimpleName(), new AddFieldAction());
        am.put(FieldsView.Paste.class.getSimpleName(), new PasteFieldsAction());
        am.put(FieldsView.Copy.class.getSimpleName(), new CopyFieldsAction());
        am.put(FieldsView.Delete.class.getSimpleName(), new DropFieldsAction());
        am.put(ChangeFieldNameAction.class.getSimpleName(), new ChangeDbFieldNameAction());
        am.put(ChangeFieldDescriptionAction.class.getSimpleName(), new ChangeDbFieldDescriptionAction());
        am.put(ChangeFieldSizeAction.class.getSimpleName(), new ChangeDbFieldSizeAction());
        am.put(ChangeFieldScaleAction.class.getSimpleName(), new ChangeDbFieldScaleAction());
        am.put(ChangeFieldReqiredAction.class.getSimpleName(), new ChangeDbFieldReqiredAction());
        am.put(ChangeFieldTypeAction.class.getSimpleName(), new ChangeDbFieldTypeAction());
        am.put(ChangeFieldTypeNameAction.class.getSimpleName(), new ChangeDbFieldTypeNameAction());
        am.put(ChangeFieldFkAction.class.getSimpleName(), new ChangeDbFieldFkAction());
        am.put(ClearFieldFkAction.class.getSimpleName(), new ClearDbFieldFkAction());
    }
}
