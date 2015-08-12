/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.gui.edits.CreateFkEdit;
import com.eas.client.dbstructure.gui.edits.DropFkEdit;
import com.eas.client.dbstructure.gui.edits.ModifyFieldEdit;
import com.eas.client.dbstructure.gui.edits.NotSavableDbStructureCompoundEdit;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.JdbcField;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.fields.ChangeFieldEdit;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.designer.datamodel.nodes.FieldNode;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import static org.openide.nodes.Node.PROP_NAME;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class TableFieldNode extends FieldNode {

    protected SqlActionsController sqlActionsController;

    public TableFieldNode(Field aField, Lookup aLookup) throws Exception {
        super(aField, aLookup);
        sqlActionsController = new SqlActionsController((DbSchemeModel) getEntity().getModel());
        SqlDriver sqlDriver = ((DbSchemeModel) getEntity().getModel()).getBasesProxy().getMetadataCache(((DbSchemeModel) getEntity().getModel()).getDatasourceName()).getDatasourceSqlDriver();
        resolver = sqlDriver.getTypesResolver();
    }

    public Integer getSize() {
        return ((JdbcField) field).getSize();
    }

    public void setSize(Integer val) {
        UndoableEdit e = editSize(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    public Integer getScale() {
        return ((JdbcField) field).getScale();
    }

    public void setScale(Integer val) {
        UndoableEdit e = editScale(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    @Override
    public boolean canChange() {
        return true;
    }

    @Override
    protected UndoableEdit editName(String val) {
        String oldVal = field.getName();
        if (oldVal == null ? val != null : !oldVal.equalsIgnoreCase(val)) {
            CompoundEdit section = new NotSavableDbStructureCompoundEdit();
            JdbcField oldContent = new JdbcField((JdbcField) field);
            JdbcField newContent = new JdbcField((JdbcField) field);
            newContent.setName(val);
            Set<Relation<FieldsEntity>> toProcessRels = FieldsEntity.<FieldsEntity>getInOutRelationsByEntityField((FieldsEntity) getEntity(), field);
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
        if (field.getDescription() == null ? val != null : !field.getDescription().equals(val)) {
            JdbcField oldContent = new JdbcField((JdbcField) field);
            JdbcField content = new JdbcField((JdbcField) field);
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
        } else {
            return null;
        }
    }

    @Override
    protected UndoableEdit editType(String val) {
        if (!Objects.equals(field.getType(), val)) {
            try {
                JdbcField oldContent = new JdbcField((JdbcField) field);
                JdbcField newContent = new JdbcField((JdbcField) field);
                newContent.setType(val);
                if (resolver != null) {
                    resolver.resolveSize(newContent);
                }

                CompoundEdit section = new NotSavableDbStructureCompoundEdit();
                Set<Relation<FieldsEntity>> rels = FieldsEntity.<FieldsEntity>getInOutRelationsByEntityField((FieldsEntity) getEntity(), field);
                int rCount = DbStructureUtils.getRecordsCountByField((FieldsEntity) getEntity(), oldContent.getName());
                String msg = null;
                if (rCount == 0 && !rels.isEmpty()) {
                    msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureReTypeFieldInRelationsPresent", String.valueOf(rels.size()));
                } else if (rCount > 0 && rels.isEmpty()) {
                    msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureReTypeFieldDataPresent", String.valueOf(rCount));
                } else if (rCount > 0 && !rels.isEmpty()) {
                    msg = NbBundle.getMessage(DbStructureUtils.class, "areYouSureReTypeFieldInRelationsDataPresent", String.valueOf(rels.size()), String.valueOf(rCount));
                }
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
                    return section;
                } else {
                    return null;
                }
            } catch (Exception ex) {
                Logger.getLogger(TableFieldNode.class.getName()).log(Level.WARNING, "Field modification error", ex); //NOI18N
                NotifyDescriptor d = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
                return null;
            }
        } else {
            return null;
        }
    }

    private boolean confirm(String message) {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(message, NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), NotifyDescriptor.OK_CANCEL_OPTION); //NOI18N
        return NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(d));
    }

    protected UndoableEdit editSize(Integer val) {
        if (((JdbcField) field).getSize() != val) {
            JdbcField oldContent = new JdbcField((JdbcField) field);
            JdbcField content = new JdbcField((JdbcField) field);
            content.setSize(val);
            try {
                CompoundEdit section = new NotSavableDbStructureCompoundEdit();
                ModifyFieldEdit dbEdit = new ModifyFieldEdit(sqlActionsController, getEntity().getTableName(), getEntity().getFields(), oldContent, content);
                ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, content, field, getEntity());
                dbEdit.redo(); // Db edit goes first
                edit.redo();
                section.addEdit(edit);
                section.addEdit(dbEdit);
                section.end();
                return section;
            } catch (Exception ex) {
                Logger.getLogger(TableFieldNode.class.getName()).log(Level.SEVERE, "Field modification error: {0}", ex.getMessage()); //NOI18N
                NotifyDescriptor d = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
                return null;
            }
        } else {
            return null;
        }
    }

    protected UndoableEdit editScale(Integer val) {
        if (((JdbcField) field).getScale() != val) {
            JdbcField oldContent = new JdbcField((JdbcField) field);
            JdbcField content = new JdbcField((JdbcField) field);
            content.setScale(val);
            try {
                CompoundEdit section = new NotSavableDbStructureCompoundEdit();
                ModifyFieldEdit dbEdit = new ModifyFieldEdit(sqlActionsController, getEntity().getTableName(), getEntity().getFields(), oldContent, content);
                ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, content, field, getEntity());
                dbEdit.redo(); // Db edit goes first
                edit.redo();
                section.addEdit(edit);
                section.addEdit(dbEdit);
                section.end();
                return section;
            } catch (Exception ex) {
                Logger.getLogger(TableFieldNode.class.getName()).log(Level.SEVERE, "Field modification error: {0}", ex.getMessage()); //NOI18N
                NotifyDescriptor d = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    protected UndoableEdit editNullable(Boolean val) {
        if (field.isNullable() != val) {
            JdbcField oldContent = new JdbcField((JdbcField) field);
            JdbcField content = new JdbcField((JdbcField) field);
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
        } else {
            return null;
        }
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = new Sheet();
        Sheet.Set pSet = Sheet.createPropertiesSet();
        PropertySupport.Name nameProp = new PropertySupport.Name(this, PROP_NAME, NbBundle.getMessage(FieldNode.class, "MSG_ModelParameterName"));
        pSet.put(nameProp);
        pSet.put(new DescriptionProperty());
        if (field.getTableName() != null) {
            Property<String> tableNameProp = new TableNameProperty();
            pSet.put(tableNameProp);
        }
        pSet.put(new TypeProperty());
        pSet.put(new SizeProperty());
        pSet.put(new ScaleProperty());
        pSet.put(new NullableProperty());
        sheet.put(pSet);
        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case JdbcField.SIZE_PROPERTY:
            case JdbcField.PRECISION_PROPERTY:
                firePropertyChange(JdbcField.SIZE_PROPERTY, evt.getOldValue(), evt.getNewValue());
                break;
            case JdbcField.SCALE_PROPERTY:
                firePropertyChange(JdbcField.SCALE_PROPERTY, evt.getOldValue(), evt.getNewValue());
                break;
            case Field.FK_PROPERTY:
                fireIconChange();
                break;
            default:
                super.propertyChange(evt);
        }
    }

    protected class SizeProperty extends Property<Integer> {

        public SizeProperty() {
            super(Integer.class);
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(SizeProperty.class, "MSG_SizePropertyShortDescription"); //NOI18N
        }

        @Override
        public String getName() {
            return JdbcField.SIZE_PROPERTY;
        }

        @Override
        public Integer getValue() throws IllegalAccessException, InvocationTargetException {
            return getSize();
        }

        @Override
        public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setSize(val);
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return canChange();
        }
    }

    protected class ScaleProperty extends Property<Integer> {

        public ScaleProperty() {
            super(Integer.class);
        }

        @Override
        public String getName() {
            return JdbcField.SCALE_PROPERTY;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(ScaleProperty.class, "MSG_ScalePropertyShortDescription"); //NOI18N
        }

        @Override
        public Integer getValue() throws IllegalAccessException, InvocationTargetException {
            return getScale();
        }

        @Override
        public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setScale(val);
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return canChange();
        }
    }

}
