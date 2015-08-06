/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.client.metadata.Field;
import com.eas.client.model.QueryDocument;
import com.eas.client.model.gui.view.FieldsTypeIconsCache;
import com.eas.client.model.query.QueryModel;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.PlatypusQuerySupport;
import com.eas.designer.application.query.editing.StoredFieldAddEdit;
import com.eas.designer.application.query.editing.StoredFieldDeleteEdit;
import com.eas.designer.application.query.editing.StoredFieldDescriptionEdit;
import com.eas.designer.application.query.editing.StoredFieldTypeEdit;
import com.eas.designer.application.utils.CompoundIcon;
import com.eas.designer.datamodel.nodes.CommonTypesEditor;
import com.eas.designer.datamodel.nodes.FieldNode;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class OutputFieldNode extends AbstractNode implements PropertyChangeListener {

    protected final String SOURCE_PROP_NAME = "source"; //NOI18N
    protected PlatypusQueryDataObject dataObject;
    protected Field field;

    public OutputFieldNode(PlatypusQueryDataObject aDataObject, Field aField) {
        super(Children.LEAF);
        if (aField == null) {
            throw new IllegalArgumentException("Field value is null."); //NOI18N
        }
        field = aField;
        dataObject = aDataObject;
        field.getChangeSupport().addPropertyChangeListener(this);
    }

    @Override
    public void destroy() throws IOException {
        field.getChangeSupport().removePropertyChangeListener(this);
        super.destroy();
    }

    @Override
    public Image getIcon(int type) {
        List<Icon> icons = new ArrayList<>();
        icons.add(FieldsTypeIconsCache.getIcon16(getType()));
        if (field.isPk()) {
            icons.add(FieldsTypeIconsCache.getPkIcon16());
        }
        if (field.isFk()) {
            icons.add(FieldsTypeIconsCache.getFkIcon16());
        }
        Icon compoundIcon = new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.CENTER, CompoundIcon.CENTER, icons.toArray(new Icon[0]));
        return ImageUtilities.icon2Image(compoundIcon);
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public String getDisplayName() {
        if (getDescription() != null && !getDescription().isEmpty()) {
            return getName() + " [" + getDescription() + "]";
        } else {
            return getName();
        }
    }

    public String getDescription() {
        try {
            QueryDocument.StoredFieldMetadata addition = getHint();
            if (addition != null && addition.getDescription() != null) {
                return addition.getDescription();
            } else {
                return field.getDescription();
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public void setDescription(String val) {
        try {
            String oldVal = getDescription();
            UndoableEdit edit = null;
            String newDescription = val;
            if (newDescription != null && newDescription.isEmpty()) {
                newDescription = null;
            }
            QueryDocument.StoredFieldMetadata storedField = getHint();
            if (storedField != null) {// Change or delete
                if ((newDescription == null || newDescription.equals(field.getDescription()))
                        && (storedField.getType() == null || storedField.getType().equals(field.getType()))) {
                    edit = new StoredFieldDeleteEdit(dataObject, storedField);
                    edit.redo();
                } else {
                    edit = new StoredFieldDescriptionEdit(dataObject, storedField, storedField.getDescription(), newDescription);
                    edit.redo();
                }
            } else {// Add and change
                // Such approach allow us to reuse events generation
                storedField = new QueryDocument.StoredFieldMetadata(field.getName());
                edit = new CompoundEdit();

                StoredFieldAddEdit addEdit = new StoredFieldAddEdit(dataObject, storedField);
                StoredFieldDescriptionEdit changeEdit = new StoredFieldDescriptionEdit(dataObject, storedField, storedField.getDescription(), newDescription);

                addEdit.redo();
                changeEdit.redo();
                edit.addEdit(addEdit);
                edit.addEdit(changeEdit);
                ((CompoundEdit) edit).end();
            }
            getUndoRedo().undoableEditHappened(new UndoableEditEvent(this, edit));
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    public String getSource() {
        String colTable = field.getTableName();
        return colTable;
    }

    public String getType() {
        try {
            QueryDocument.StoredFieldMetadata addition = getHint();
            if (addition != null && addition.getType() != null) {
                return addition.getType();
            } else {
                return field.getType();
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    public void setType(String newType) {
        try {
            QueryDocument.StoredFieldMetadata addition = getHint();
            UndoableEdit edit;
            if (addition != null) {// Change or delete
                if ((addition.getDescription() == null || addition.getDescription().equals(field.getDescription()))
                        && (newType == null || newType.equals(field.getType()))) {
                    edit = new StoredFieldDeleteEdit(dataObject, addition);
                    edit.redo();
                } else {
                    edit = new StoredFieldTypeEdit(dataObject, addition, addition.getType(), newType);
                    edit.redo();
                }
            } else {// Add and change
                // Such approach allow us to reuse events generation
                addition = new QueryDocument.StoredFieldMetadata(field.getName());
                edit = new CompoundEdit();

                StoredFieldAddEdit addEdit = new StoredFieldAddEdit(dataObject, addition);
                StoredFieldTypeEdit changeEdit = new StoredFieldTypeEdit(dataObject, addition, addition.getType(), newType);

                addEdit.redo();
                changeEdit.redo();
                edit.addEdit(addEdit);
                edit.addEdit(changeEdit);
                ((CompoundEdit) edit).end();
            }
            getUndoRedo().undoableEditHappened(new UndoableEditEvent(this, edit));
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = new Sheet();
        Sheet.Set pSet = Sheet.createPropertiesSet();
        PropertySupport.Name nameProp = new PropertySupport.Name(this, PROP_NAME, NbBundle.getMessage(OutputFieldNode.class, "MSG_NamePropertyShortDescription")); //NOI18N
        pSet.put(nameProp);
        pSet.put(new DescriptionProperty());
        pSet.put(new SourceProperty());
        pSet.put(new TypeProperty());
        sheet.put(pSet);
        return sheet;
    }

    private QueryModel getModel() {
        try {
            return dataObject.getModel();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    private QueryDocument.StoredFieldMetadata getHint() throws Exception {
        for (QueryDocument.StoredFieldMetadata addition : dataObject.getOutputFieldsHints()) {
            if (addition.getBindedColumn().equals(field.getName())) {
                return addition;
            }
        }
        return null;
    }

    private UndoRedo.Manager getUndoRedo() {
        return dataObject.getLookup().lookup(PlatypusQuerySupport.class).getUndo();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Field.DESCRIPTION_PROPERTY:
                firePropertyChange(Field.DESCRIPTION_PROPERTY, evt.getOldValue(), evt.getNewValue());
                fireDisplayNameChange(null, getDisplayName());
                break;
            case Field.TYPE_PROPERTY:
                firePropertyChange(FieldNode.TYPE_PROP_NAME, evt.getOldValue(), evt.getNewValue());
                fireIconChange();
                break;
        }
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
            return NbBundle.getMessage(OutputFieldNode.DescriptionProperty.class, "MSG_DescriptionPropertyShortDescription"); //NOI18N
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
            return true;
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setDescription(val);
        }
    }

    protected class SourceProperty extends Property<String> {

        public SourceProperty() {
            super(String.class);
        }

        @Override
        public String getName() {
            return SOURCE_PROP_NAME;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(OutputFieldNode.DescriptionProperty.class, "MSG_SourcePropertyShortDescription"); //NOI18N
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return getSource();
        }

        @Override
        public boolean canWrite() {
            return false;
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            //NO-OP
        }
    }

    protected class TypeProperty extends Property<String> {

        public TypeProperty() {
            super(String.class);
        }

        @Override
        public String getName() {
            return FieldNode.TYPE_PROP_NAME;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(OutputFieldNode.TypeProperty.class, "MSG_TypePropertyShortDescription"); //NOI18N
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
            return CommonTypesEditor.getNewInstanceFor(getModel());
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return true;
        }
    }
}
