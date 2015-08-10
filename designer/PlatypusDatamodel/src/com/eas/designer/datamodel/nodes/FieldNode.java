/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.client.model.gui.edits.fields.ChangeFieldEdit;
import com.eas.client.model.gui.view.FieldsTypeIconsCache;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.designer.application.utils.CompoundIcon;
import com.eas.designer.datamodel.ModelUndoProvider;
import java.awt.Image;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import org.openide.actions.MoveDownAction;
import org.openide.actions.MoveUpAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
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
    public static final String TYPE_PROP_NAME = "type"; //NOI18N
    protected Field field;
    private boolean canChange;
    protected TypesResolver resolver;

    public FieldNode(Field aField, Lookup aLookup, boolean aCanChange) {
        this(aField, aLookup);
        canChange = aCanChange;
    }

    public FieldNode(Field aField, Lookup aLookup) {
        super(Children.LEAF, aLookup);
        field = aField;
        field.getChangeSupport().addPropertyChangeListener(this);
        try {
            if (getEntity().getModel() instanceof QueryModel) {
                resolver = ((QueryModel) getEntity().getModel()).getBasesProxy().getMetadataCache(((QueryModel) getEntity().getModel()).getDatasourceName()).getDatasourceSqlDriver().getTypesResolver();
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void destroy() throws IOException {
        field.getChangeSupport().removePropertyChangeListener(this);
        super.destroy();
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
        return getIcon(type, field, resolver);
    }

    public static Image getIcon(int type, Field aField, TypesResolver aResolver) {
        List<Icon> icons = new ArrayList<>();
        Icon icon = FieldsTypeIconsCache.getIcon16(aField.getType());
        if (icon == null && aResolver != null) {
            String appType = aResolver.toApplicationType(aField instanceof JdbcField ? ((JdbcField) aField).getJdbcType() : 0, aField.getType());
            icon = FieldsTypeIconsCache.getIcon16(appType);
        }
        if (icon != null) {
            icons.add(icon);
        }
        if (aField.isPk()) {
            icons.add(FieldsTypeIconsCache.getPkIcon16());
        }
        if (aField.isFk()) {
            icons.add(FieldsTypeIconsCache.getFkIcon16());
        }
        if (aField instanceof Parameter) {
            return ImageUtilities.icon2Image(new CompoundIcon(new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.CENTER, CompoundIcon.CENTER, icons.toArray(new Icon[0])), FieldsTypeIconsCache.getParameterIcon16()));
        }
        return icons.isEmpty() ? null : ImageUtilities.icon2Image(new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.CENTER, CompoundIcon.CENTER, icons.toArray(new Icon[0])));
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
        return canChange;
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

    public String getType() {
        return field.getType();
    }

    public void setType(String val) {
        UndoableEdit e = editType(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    public Boolean isNullable() {
        return field.isNullable();
    }

    public void setNullable(Boolean val) {
        UndoableEdit e = editNullable(val);
        if (e != null) {
            getUndo().undoableEditHappened(new UndoableEditEvent(this, e));
        }
    }

    protected Entity getEntity() {
        return getLookup().lookup(Entity.class);
    }

    protected UndoableEditListener getUndo() {
        ModelUndoProvider mup = getLookup().lookup(ModelUndoProvider.class);
        if (mup != null) {
            return getLookup().lookup(ModelUndoProvider.class).getModelUndo();
        } else {
            return StubUndoableEditListener.DEFAULT;
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
        pSet.put(new NullableProperty());
        sheet.put(pSet);
        return sheet;
    }
    /*
     protected void checkTypedLengthScale(Field after) {
     if (after != null) {
     if (SQLUtils.getTypeGroup(after.getTypeInfo().getSqlType()) == SQLUtils.TypesGroup.STRINGS) {
     if (after.getPrecision() <= 0 || after.getSize() <= 0) {
     after.setPrecision(1);
     after.setSize(100);
     }
     } else if (SQLUtils.getTypeGroup(after.getTypeInfo().getSqlType()) == SQLUtils.TypesGroup.NUMBERS) {
     if (after.getPrecision() <= 0 || after.getSize() <= 0) {
     after.setPrecision(0);
     after.setSize(0);
     }
     if (after.getPrecision() > 15 || after.getSize() > 15) {
     after.setPrecision(0);
     after.setSize(0);
     }
     } else if (SQLUtils.getTypeGroup(after.getTypeInfo().getSqlType()) == SQLUtils.TypesGroup.LOGICAL) {//TODO: how to handle these types?
     } else if (SQLUtils.getTypeGroup(after.getTypeInfo().getSqlType()) == SQLUtils.TypesGroup.DATES) {
     } else if (SQLUtils.getTypeGroup(after.getTypeInfo().getSqlType()) == SQLUtils.TypesGroup.LOBS) {
     }
     }
     }
     */

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
            case Field.TYPE_PROPERTY:
                firePropertyChange(TYPE_PROP_NAME, evt.getOldValue(), evt.getNewValue());
                fireIconChange();
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

    protected UndoableEdit editType(String val) {
        Field oldContent = new Field(field);
        Field newContent = new Field(field);
        newContent.setType(val);
        //checkTypedLengthScale(newContent);
        ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, newContent, field, getEntity());
        edit.redo();
        return edit;
    }
    /*
     protected Set<Relation> getIncompatibleRelations(Field newFieldContent) throws CancelException {
     Set<Relation> toProcessRels = new HashSet<>();
     Set<Relation> rels = Entity.<Entity>getInOutRelationsByEntityField(getEntity(), field);
     rels.stream().forEach((rel) -> {
     Field lfield = rel.getLeftField();
     Field rfield = rel.getRightField();
     if (lfield == field) {
     lfield = newFieldContent;
     }
     if (rfield == field) {
     rfield = newFieldContent;
     }
     if (lfield == null || rfield == null) {
     toProcessRels.add(rel);
     } else if ((lfield.isPk() || lfield.isFk())
     && (rfield.isPk() || rfield.isFk())) {
     if (!SQLUtils.isKeysCompatible(lfield, rfield)) {
     toProcessRels.add(rel);
     }
     } else if (!SQLUtils.isSimpleTypesCompatible(lfield.getType(), rfield.getType())) {
     toProcessRels.add(rel);
     }
     });
     return toProcessRels;
     }
     */

    protected UndoableEdit editNullable(Boolean val) {
        Field oldContent = new Field(field);
        Field content = new Field(field);
        content.setNullable(val);
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

    protected class TypeProperty extends Property<String> {

        public TypeProperty() {
            super(String.class);
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
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return getType();
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
            return isNullable();
        }

        @Override
        public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setNullable(val);
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

    protected static class StubUndoableEditListener implements UndoableEditListener {

        public static final UndoableEditListener DEFAULT = new StubUndoableEditListener();

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
        }
    }
}
