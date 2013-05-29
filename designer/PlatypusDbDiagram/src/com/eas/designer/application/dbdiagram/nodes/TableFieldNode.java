/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.eas.client.SQLUtils;
import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.gui.edits.CreateFkEdit;
import com.eas.client.dbstructure.gui.edits.DropFkEdit;
import com.eas.client.dbstructure.gui.edits.ModifyFieldEdit;
import com.eas.client.dbstructure.gui.edits.NotSavableDbStructureCompoundEdit;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.fields.ChangeFieldEdit;
import com.eas.designer.explorer.model.nodes.FieldNode;
import java.beans.PropertyChangeEvent;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class TableFieldNode extends FieldNode {

    protected SqlActionsController sqlActionsController;

    public TableFieldNode(Field aField, Lookup aLookup) throws Exception {
        super(aField, aLookup);
        sqlActionsController = new SqlActionsController((DbSchemeModel) getEntity().getModel());
    }

    @Override
    public boolean canChange() {
        return true;
    }

    @Override
    protected UndoableEdit editName(String val) {
        String oldVal = field.getName();
        if (!oldVal.equalsIgnoreCase(val)) {
            CompoundEdit section = new NotSavableDbStructureCompoundEdit();
            Field oldContent = new Field(field);
            Field newContent = new Field(field);
            newContent.setName(val);
            Set<Relation> toProcessRels = FieldsEntity.getInOutRelationsByEntityField(getEntity(), field);
            Logger.getLogger(TableFieldNode.class.getName()).fine(String.format("Changing field from %s to %s\n", oldVal, val)); //NOI18N        
            try {
                // we have to recreate foreign keys in order to them to be compatible with new field names
                // let's drop the foreign keys
                for (Relation<FieldsEntity> rel2Del : toProcessRels) {
                    ForeignKeySpec fkSpec = DbStructureUtils.constructFkSpecByRelation(rel2Del);
                    DropFkEdit dEdit = new DropFkEdit(sqlActionsController, fkSpec, field);
                    dEdit.redo();
                    section.addEdit(dEdit);
                }
                // change the field name
                ModifyFieldEdit dbEdit = new ModifyFieldEdit(sqlActionsController, getEntity().getTableName(), getEntity().getFields(), oldContent, newContent);
                dbEdit.redo();
                section.addEdit(dbEdit);
                field.setName(val);
                try {
                    // let's create the foreign keys
                    for (Relation<FieldsEntity> rel2Create : toProcessRels) {
                        ForeignKeySpec fkSpec = DbStructureUtils.constructFkSpecByRelation(rel2Create);
                        CreateFkEdit cEdit = new CreateFkEdit(sqlActionsController, fkSpec, field);
                        cEdit.redo();
                        section.addEdit(cEdit);
                    }
                } finally {
                    field.setName(oldVal);
                }
                UndoableEdit e = super.editName(val);
                assert e != null;
                section.addEdit(e);
                section.end();
                return section;
            } catch (Exception ex) {
                Logger.getLogger(TableFieldNode.class.getName()).log(Level.SEVERE, "Field modification error {0}", ex); //NOI18N
                NotifyDescriptor d = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    protected UndoableEdit editDescription(String val) {
        Field oldContent = new Field(field);
        Field content = new Field(field);
        content.setDescription(val);
        try {
            ModifyFieldEdit dbEdit = new ModifyFieldEdit(sqlActionsController, getEntity().getTableName(), getEntity().getFields(), oldContent, content);
            dbEdit.redo();
            CompoundEdit section = new NotSavableDbStructureCompoundEdit();
            section.addEdit(super.editDescription(val));
            section.addEdit(dbEdit);
            section.end();
            return section;
        } catch (Exception ex) {
            Logger.getLogger(TableFieldNode.class.getName()).log(Level.SEVERE, "Field modification error: {0}", ex.getMessage());
            NotifyDescriptor d = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return null;
        }
    }

    @Override
    protected UndoableEdit editType(Integer val) {
        Field oldContent = new Field(field);
        Field newContent = new Field(field);
        newContent.setTypeInfo(DataTypeInfo.valueOf(val));
        CompoundEdit section = new NotSavableDbStructureCompoundEdit();
        Set<Relation> rels = FieldsEntity.getInOutRelationsByEntityField(getEntity(), field);
        int rCount = DbStructureUtils.getRecordsCountByField((FieldsEntity) getEntity(), oldContent.getName());
        String msg = null;
        String promtMsg1 = "areYouSureReTypeFieldInRelationsPresent"; //NOI18N
        String promtMsg2 = "areYouSureReTypeFieldDataPresent"; //NOI18N
        String promtMsg3 = "areYouSureReTypeFieldInRelationsDataPresent"; //NOI18N
        if (SQLUtils.isSameTypeGroup(newContent.getTypeInfo().getSqlType(), java.sql.Types.BLOB) || SQLUtils.isSameTypeGroup(oldContent.getTypeInfo().getSqlType(), java.sql.Types.BLOB)) {
            promtMsg1 = "areYouSureBlobFieldInRelationsPresent"; //NOI18N
            promtMsg2 = "areYouSureBlobFieldDataPresent"; //NOI18N
            promtMsg3 = "areYouSureBlobFieldInRelationsDataPresent"; //NOI18N
        }
        if (rCount == 0 && !rels.isEmpty()) {
            msg = DbStructureUtils.getString(promtMsg1, String.valueOf(rels.size()), null);
        } else if (rCount > 0 && rels.isEmpty()) {
            msg = DbStructureUtils.getString(promtMsg2, String.valueOf(rCount), null);
        } else if (rCount > 0 && !rels.isEmpty()) {
            msg = DbStructureUtils.getString(promtMsg3, String.valueOf(rels.size()), String.valueOf(rCount));
        }
        try {
            if (msg == null || confirm(msg)) {
                // we have to remove foreign keys because of types incompatibility
                if (rels != null) {
                    for (Relation<FieldsEntity> rel2Del : rels) {
                        ForeignKeySpec fkSpec = DbStructureUtils.constructFkSpecByRelation(rel2Del);
                        DropFkEdit dEdit = new DropFkEdit(sqlActionsController, fkSpec, field);
                        dEdit.redo();
                        section.addEdit(dEdit);
                    }
                }
                // change the field type
                ModifyFieldEdit dbEdit = new ModifyFieldEdit(sqlActionsController, getEntity().getTableName(), getEntity().getFields(), oldContent, newContent);
                dbEdit.redo();
                section.addEdit(dbEdit);
                if (rels != null) {
                    for (Relation rel : rels) {
                        DeleteRelationEdit drEdit = new DeleteRelationEdit(rel);
                        drEdit.redo();
                        section.addEdit(drEdit);
                    }
                }
                newContent.setFk(null);
                ChangeFieldEdit diagramEdit = new ChangeFieldEdit(oldContent, newContent, field, getEntity());
                diagramEdit.redo();
                section.addEdit(diagramEdit);
                section.end();
                try {
                    getEntity().getModel().getClient().dbTableChanged(getEntity().getTableDbId(), getEntity().getTableSchemaName(), getEntity().getTableName());
                } catch (Exception ex) {
                    Logger.getLogger(TableFieldNode.class.getName()).log(Level.SEVERE, null, ex); //NOI18N
                }
                return section;
            } else {
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(TableFieldNode.class.getName()).log(Level.SEVERE, "Field modification error", ex); //NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return null;
        }
    }

    private boolean confirm(String message) {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(message, DbStructureUtils.getString("dbSchemeEditor"), NotifyDescriptor.OK_CANCEL_OPTION); //NOI18N
        return NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(d));
    }

    @Override
    protected UndoableEdit editSize(Integer val) {
        Field oldContent = new Field(field);
        Field content = new Field(field);
        content.setSize(val);
        try {
            ModifyFieldEdit dbEdit = new ModifyFieldEdit(sqlActionsController, getEntity().getTableName(), getEntity().getFields(), oldContent, content);
            dbEdit.redo();
            CompoundEdit section = new NotSavableDbStructureCompoundEdit();
            section.addEdit(super.editSize(val));
            section.addEdit(dbEdit);
            section.end();
            return section;
        } catch (Exception ex) {
            Logger.getLogger(TableFieldNode.class.getName()).log(Level.SEVERE, "Field modification error: {0}", ex.getMessage()); //NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return null;
        }
    }

    @Override
    protected UndoableEdit editScale(Integer val) {
        Field oldContent = new Field(field);
        Field content = new Field(field);
        content.setScale(val);
        try {
            ModifyFieldEdit dbEdit = new ModifyFieldEdit(sqlActionsController, getEntity().getTableName(), getEntity().getFields(), oldContent, content);
            dbEdit.redo();
            CompoundEdit section = new NotSavableDbStructureCompoundEdit();
            section.addEdit(super.editScale(val));
            section.addEdit(dbEdit);
            section.end();
            return section;
        } catch (Exception ex) {
            Logger.getLogger(TableFieldNode.class.getName()).log(Level.SEVERE, "Field modification error: {0}", ex.getMessage()); //NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return null;
        }
    }

    @Override
    protected UndoableEdit editNullable(Boolean val) {
        Field oldContent = new Field(field);
        Field content = new Field(field);
        content.setNullable(val);
        try {
            ModifyFieldEdit dbEdit = new ModifyFieldEdit(sqlActionsController, getEntity().getTableName(), getEntity().getFields(), oldContent, content);
            dbEdit.redo();
            CompoundEdit section = new NotSavableDbStructureCompoundEdit();
            section.addEdit(super.editNullable(val));
            section.addEdit(dbEdit);
            section.end();
            return section;
        } catch (Exception ex) {
            Logger.getLogger(TableFieldNode.class.getName()).log(Level.SEVERE, "Field modification error: {0}", ex.getMessage()); //NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return null;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Field.FK_PROPERTY.equals(evt.getPropertyName())) {
            fireIconChange();
        } else {
            super.propertyChange(evt);
        }
    }
}
