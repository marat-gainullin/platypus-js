/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.model.nodes;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.SQLUtils;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.edits.AccessibleCompoundEdit;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.fields.ChangeFieldEdit;
import com.eas.client.model.gui.view.FieldsTypeIconsCache;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.designer.explorer.ModelUndoProvider;
import com.eas.designer.explorer.utils.CompoundIcon;
import java.awt.Image;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;
import org.openide.actions.MoveDownAction;
import org.openide.actions.MoveUpAction;
import org.openide.actions.PropertiesAction;
import org.openide.awt.UndoRedo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class FieldNode extends AbstractNode implements PropertyChangeListener {

    public static final String NAME_PROP_NAME = PROP_NAME;
    public static final String TABLE_NAME_PROP_NAME = "tableName"; //NOI18N
    //public static final String DESCRIPTION_PROP_NAME = "description"; //NOI18N
    //public static final String TYPE_INFO_PROP_NAME = "typeInfo"; //NOI18N
    //public static final String PRECISION_PROP_NAME = "precision"; //NOI18N
    //public static final String NULLABLE_PROP_NAME = "nullable"; //NOI18N
    public static final String TYPE_PROP_NAME = "type"; //NOI18N
    public static final String TYPE_NAME_PROP_NAME = "typeName"; //NOI18N
    //public static final String SIZE_PROP_NAME = "size"; //NOI18N
    //public static final String SCALE_PROP_NAME = "scale"; //NOI18N
    //public static final String REQUIRED_PROP_NAME = "required"; //NOI18N
    protected Field field;

    public FieldNode(Field aField, Lookup aLookup) {
        super(Children.LEAF, aLookup);
        field = aField;
        field.getChangeSupport().addPropertyChangeListener(this);
    }

    @Override
    public String getDisplayName() {
        String description = field.getDescription();
        String name = field.getName();
        if (description != null && !description.isEmpty()) {
            return String.format("%s [ %s ]", name, description);
        } else {
            return String.format("%s", name);
        }
    }

    @Override
    public Image getIcon(int type) {
        return getIcon(field, type);
    }

    public static Image getIcon(Field aField, int aType) {
        List<Icon> icons = new ArrayList<>();
        icons.add(FieldsTypeIconsCache.getIcon16(aField.getTypeInfo().getSqlType()));
        if (aField.isPk()) {
            icons.add(FieldsTypeIconsCache.getPkIcon16());
        }
        if (aField.isFk()) {
            icons.add(FieldsTypeIconsCache.getFkIcon16());
        }
        if (aField instanceof Parameter) {
            return ImageUtilities.icon2Image(new CompoundIcon(new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.CENTER, CompoundIcon.CENTER, icons.toArray(new Icon[0])), FieldsTypeIconsCache.getParameterIcon16()));
        }
        return ImageUtilities.icon2Image(new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.CENTER, CompoundIcon.CENTER, icons.toArray(new Icon[0])));
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<>();
        if (isOrderingSupported()) {
            actions.add(MoveUpAction.get(MoveUpAction.class));
            actions.add(MoveDownAction.get(MoveDownAction.class));
        }
        actions.add(PropertiesAction.get(PropertiesAction.class));
        return actions.toArray(new Action[0]);
    }

    public Field getField() {
        return field;
    }

    @Override
    public boolean canRename() {
        return canChange();
    }

    public boolean canChange() {
        return false;
    }

    protected boolean isOrderingSupported() {
        return getLookup().lookup(FieldsOrderSupport.class) != null;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public void setName(String val) {
        UndoableEdit e = editName(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    public String getDescription() {
        return field.getDescription() != null ? field.getDescription() : ""; //NOI18N;
    }

    public void setDescription(String val) {
        UndoableEdit e = editDescription(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    protected String getTableName() {
        return field.getTableName();
    }

    public Integer getType() {
        return field.getTypeInfo().getSqlType();
    }

    public void setType(Integer val) {
        UndoableEdit e = editType(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    public String getTypeName() {
        return field.getTypeInfo().getSqlTypeName();
    }

    public void setTypeName(String val) {
        UndoableEdit e = editTypeName(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    public Integer getSize() {
        return field.getSize();
    }

    public void setSize(Integer val) {
        UndoableEdit e = editSize(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    public Integer getScale() {
        return field.getScale();
    }

    public void setScale(Integer val) {
        UndoableEdit e = editScale(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    public Boolean isRequired() {
        return !field.isNullable();
    }

    public void setRequired(Boolean val) {
        UndoableEdit e = editRequired(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    protected Entity getEntity() {
        return getLookup().lookup(Entity.class);
    }

    protected UndoRedo.Manager getUndo() {
        return getLookup().lookup(ModelUndoProvider.class).getModelUndo();
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
        pSet.put(new TypeNameProperty());
        pSet.put(new SizeProperty());
        pSet.put(new ScaleProperty());
        pSet.put(new NullableProperty());
        sheet.put(pSet);
        return sheet;
    }

    protected void checkTypedLengthScale(Field after) {
        if (after != null) {
            if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.VARCHAR)) {
                if (after.getPrecision() <= 0 || after.getSize() <= 0) {
                    after.setPrecision(1);
                    after.setSize(100);
                }
            } else if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.NUMERIC)) {
                if (after.getPrecision() <= 0 || after.getSize() <= 0) {
                    after.setPrecision(0);
                    after.setSize(0);
                }
                if (after.getPrecision() > 15 || after.getSize() > 15) {
                    after.setPrecision(0);
                    after.setSize(0);
                }
            } else if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.BOOLEAN)) {//TODO: how to handle these types?
            } else if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.TIME)) {
            } else if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.BLOB)) {
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case PROP_NAME:
                fireNameChange((String) evt.getOldValue(), (String) evt.getNewValue());
                fireDisplayNameChange(null, getDisplayName());
                firePropertyChange(PROP_NAME, evt.getOldValue(), evt.getNewValue());
                break;
            case Field.DESCRIPTION_PROPERTY:
                fireDisplayNameChange(null, getDisplayName());
                firePropertyChange(Field.DESCRIPTION_PROPERTY, evt.getOldValue(), evt.getNewValue());
                break;
            case Field.TYPE_INFO_PROPERTY:
                firePropertyChange(TYPE_PROP_NAME, evt.getOldValue(), evt.getNewValue());
                firePropertyChange(TYPE_NAME_PROP_NAME, evt.getOldValue(), evt.getNewValue());
                fireIconChange();
                break;
            case Field.SIZE_PROPERTY:
            case Field.PRECISION_PROPERTY:
                firePropertyChange(Field.SIZE_PROPERTY, evt.getOldValue(), evt.getNewValue());
                break;
            case Field.SCALE_PROPERTY:
                firePropertyChange(Field.SCALE_PROPERTY, evt.getOldValue(), evt.getNewValue());
                break;
            case Field.NULLABLE_PROPERTY:
                firePropertyChange(Field.NULLABLE_PROPERTY, evt.getOldValue(), evt.getNewValue());
                break;
        }
    }

    @Override
    public Transferable drag() throws IOException {
        return getTransferable();
    }

    private Transferable getTransferable() {
        if (getEntity() instanceof QueryParametersEntity) {
            return new StringSelection(String.format(":%s", field.getName()));//NOI18N
        } else if (getEntity() instanceof QueryEntity) {
            QueryEntity queryEntity = (QueryEntity) getEntity();
            if (queryEntity.getAlias() != null && !queryEntity.getAlias().isEmpty()) {
                return new StringSelection(String.format("%s.%s", queryEntity.getAlias(), field.getName()));//NOI18N
            }
        }
        return new StringSelection(field.getName());
    }

    protected UndoableEdit editName(String val) {
        String oldVal = field.getName();
        if (oldVal != null && !oldVal.equals(val) || oldVal == null && val != null) {
            Entity entity = getLookup().lookup(Entity.class);
            Parameter oldContent = new Parameter(field);
            Parameter content = new Parameter(field);
            content.setName(val);
            ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, content, field, entity);
            edit.redo();
            return edit;
        }
        return null;
    }

    protected UndoableEdit editDescription(String val) {
        Field oldContent = new Field(field);
        Field content = new Field(field);
        content.setDescription(val);
        ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, content, field, getEntity());
        edit.redo();
        return edit;
    }

    protected UndoableEdit editType(Integer val) {
        Field oldContent = new Field(field);
        Field newContent = new Field(field);
        newContent.setTypeInfo(DataTypeInfo.valueOf((Integer) val));
        checkTypedLengthScale(newContent);
        AccessibleCompoundEdit section = new AccessibleCompoundEdit();
        Set<Relation> relationToDelete;
        try {
            relationToDelete = getIncompatibleRelations(newContent);
        } catch (CancelException ex) {
            return null;
        }
        for (Relation rel : relationToDelete) {
            DeleteRelationEdit drEdit = new DeleteRelationEdit(rel);
            drEdit.redo();
            section.addEdit(drEdit);
        }
        ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, newContent, field, getEntity());
        edit.redo();
        section.addEdit(edit);
        section.end();
        return section;
    }

    protected Set<Relation> getIncompatibleRelations(Field newFieldContent) throws CancelException {
        Set<Relation> toProcessRels = new HashSet<>();
        Set<Relation> rels = FieldsEntity.getInOutRelationsByEntityField(getEntity(), field);
        for (Relation rel : rels) {
            Field lfield = rel.getLeftField();
            Field rfield = rel.getRightField();
            if (lfield == field) {
                lfield = newFieldContent;
            }
            if (rfield == field) {
                rfield = newFieldContent;
            }
            /*
             if (rel.isLeftField()) {
             lfield = rel.getLeftEntity().getFields().get(leftFieldName);
             if (rel.getLeftEntity() == getEntity()
             && oldFieldContent.getName().equals(leftFieldName)) {
             lfield = newFieldContent;
             }
             } else {
             try {
             lfield = rel.getLeftEntity().getQuery().getParameters().get(leftParameterName);
             if (rel.getLeftEntity() == getEntity() && oldFieldContent.getName().equals(leftParameterName)) {
             lfield = newFieldContent;
             }
             } catch (Exception ex) {
             Logger.getLogger(FieldNode.class.getName()).log(Level.SEVERE, null, ex);
             }
             }

             if (rel.isRightField()) {
             rfield = rel.getRightEntity().getFields().get(rightFieldName);
             if (rel.getRightEntity() == getEntity()
             && oldFieldContent.getName().equals(rightFieldName)) {
             rfield = newFieldContent;
             }
             } else {
             try {
             rfield = rel.getRightEntity().getQuery().getParameters().get(rightParameterName);
             if (rel.getRightEntity() == getEntity() && oldFieldContent.getName().equals(rightParameterName)) {
             rfield = newFieldContent;
             }
             } catch (Exception ex) {
             Logger.getLogger(FieldNode.class.getName()).log(Level.SEVERE, null, ex);
             }
             }
             */
            if ((lfield.isPk() || lfield.isFk())
                    && (rfield.isPk() || rfield.isFk())) {
                if (!SQLUtils.isKeysCompatible(lfield, rfield)) {
                    toProcessRels.add(rel);
                }
            } else if (!SQLUtils.isSimpleTypesCompatible(lfield.getTypeInfo().getSqlType(), rfield.getTypeInfo().getSqlType())) {
                toProcessRels.add(rel);
            }
        }
        /*
         if (!toProcessRels.isEmpty()) {
         NotifyDescriptor d = new NotifyDescriptor.Confirmation(NbBundle.getMessage(FieldNode.class, "MSG_BadParameterType"), //NOI18N
         NbBundle.getMessage(FieldNode.class, "LBL_RelationsCheck"), NotifyDescriptor.OK_CANCEL_OPTION); //NOI18N
         if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
         throw new CancelException();
         }
         }
         */
        return toProcessRels;
    }

    protected UndoableEdit editTypeName(String val) {
        assert DataTypeInfo.OTHER.equals(field.getTypeInfo());
        DataTypeInfo dti = DataTypeInfo.OTHER;
        dti.setSqlTypeName(val);
        Field oldContent = new Field(field);
        Field content = new Field(field);
        content.setTypeInfo(dti);
        ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, content, field, getEntity());
        edit.redo();
        return edit;
    }

    protected UndoableEdit editSize(Integer val) {
        Field oldContent = new Field(field);
        Field content = new Field(field);
        content.setSize(val);
        ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, content, field, getEntity());
        edit.redo();
        return edit;
    }

    protected UndoableEdit editScale(Integer val) {
        Field oldContent = new Field(field);
        Field content = new Field(field);
        content.setScale(val);
        ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, content, field, getEntity());
        edit.redo();
        return edit;
    }

    protected UndoableEdit editRequired(Boolean val) {
        Field oldContent = new Field(field);
        Field content = new Field(field);
        content.setNullable(!val);
        ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, content, field, getEntity());
        edit.redo();
        return edit;
    }

    protected final class CancelException extends Exception {
    }

    protected class DescriptionProperty extends Property<String> {

        public DescriptionProperty() {
            super(String.class);
        }

        @Override
        public String getName() {
            return Field.DESCRIPTION_PROPERTY;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(DescriptionProperty.class, "MSG_DescriptionPropertyShortDescription"); //NOI18N
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return getDescription();
        }

        @Override
        public boolean canWrite() {
            return canChange();
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setDescription(val);
        }
    }

    protected class TableNameProperty extends Property<String> {

        public TableNameProperty() {
            super(String.class);
        }

        @Override
        public String getName() {
            return TABLE_NAME_PROP_NAME;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(TableNameProperty.class, "MSG_TableNamePropertyShortDescription"); //NOI18N
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return getTableName();
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return false;
        }
    }

    protected class TypeProperty extends Property<Integer> {

        public TypeProperty() {
            super(Integer.class);
        }

        @Override
        public String getName() {
            return TYPE_PROP_NAME;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(TypeProperty.class, "MSG_TypePropertyShortDescription"); //NOI18N
        }

        @Override
        public Integer getValue() throws IllegalAccessException, InvocationTargetException {
            return getType();
        }

        @Override
        public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setType(val);
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return CommonTypesEditor.getNewInstanceFor(getEntity().getModel());
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

    protected class TypeNameProperty extends Property<String> {

        public TypeNameProperty() {
            super(String.class);
        }

        @Override
        public String getName() {
            return TYPE_NAME_PROP_NAME;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(TypeNameProperty.class, "MSG_TypeNamePropertyShortDescription"); //NOI18N
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return getTypeName();
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setTypeName(val);
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return canChange() && DataTypeInfo.OTHER.equals(field.getTypeInfo());
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
            return Field.SIZE_PROPERTY;
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
            return canChange() && !SQLUtils.isSameTypeGroup(field.getTypeInfo().getSqlType(), java.sql.Types.BLOB)
                    && !SQLUtils.isSameTypeGroup(field.getTypeInfo().getSqlType(), java.sql.Types.DATE)
                    && field.getTypeInfo().getSqlType() != java.sql.Types.STRUCT
                    && field.getTypeInfo().getSqlType() != java.sql.Types.OTHER
                    && field.getTypeInfo().getSqlType() != java.sql.Types.LONGVARCHAR
                    && field.getTypeInfo().getSqlType() != java.sql.Types.LONGNVARCHAR;
        }
    }

    protected class ScaleProperty extends Property<Integer> {

        public ScaleProperty() {
            super(Integer.class);
        }

        @Override
        public String getName() {
            return Field.SCALE_PROPERTY;
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
            return canChange() && SQLUtils.isSimpleTypesCompatible(field.getTypeInfo().getSqlType(), java.sql.Types.NUMERIC);
        }
    }

    protected class NullableProperty extends Property<Boolean> {

        public NullableProperty() {
            super(Boolean.class);
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(NullableProperty.class, "MSG_NullablePropertyShortDescription"); //NOI18N
        }

        @Override
        public String getName() {
            return Field.NULLABLE_PROPERTY;
        }

        @Override
        public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
            return isRequired();
        }

        @Override
        public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setRequired(val);
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
