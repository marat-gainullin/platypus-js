/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.gui.edits.DropIndexEdit;
import com.eas.client.dbstructure.gui.edits.ModifyIndexEdit;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.IconCache;
import com.eas.designer.datamodel.ModelUndoProvider;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.awt.UndoRedo;
import org.openide.nodes.AbstractNode;
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
public class TableIndexNode extends AbstractNode {

    protected static final String TREE_INDEX_ICON_NAME = "tree_index.png";//NOI18N
    protected SqlActionsController sqlController;
    protected DbTableIndexSpec index;
    protected PropertyChangeListener indexPropertyListener;
    protected FieldsEntity tableEntity;
    private final AddIndexColumnAction addColumnAction = new AddIndexColumnAction();

    public TableIndexNode(DbTableIndexSpec anIndexSpec, FieldsEntity aTableEntity, Lookup aLookup) throws Exception {
        super(new TableIndexChildren(anIndexSpec, aTableEntity), aLookup);
        index = anIndexSpec;
        tableEntity = aTableEntity;
        indexPropertyListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case DbTableIndexSpec.NAME_PROPERTY:
                        fireNameChange((String) evt.getOldValue(), (String) evt.getNewValue());
                        firePropertyChange(PROP_NAME, evt.getOldValue(), evt.getNewValue());
                        break;
                    case DbTableIndexSpec.UNIQUE_PROPERTY:
                        firePropertyChange(DbTableIndexSpec.UNIQUE_PROPERTY, evt.getOldValue(), evt.getNewValue());
                        break;
                    case DbTableIndexSpec.CLUSTERED_PROPERTY:
                        firePropertyChange(DbTableIndexSpec.CLUSTERED_PROPERTY, evt.getOldValue(), evt.getNewValue());
                        break;
                    case DbTableIndexSpec.HASHED_PROPERTY:
                        firePropertyChange(DbTableIndexSpec.HASHED_PROPERTY, evt.getOldValue(), evt.getNewValue());
                        break;
                    case DbTableIndexSpec.COLUMNS_PROPERTY:
                        ((TableIndexChildren) getChildren()).update();
                        break;
                }
            }
        };
        index.getChangeSupport().addPropertyChangeListener(indexPropertyListener);
        sqlController = new SqlActionsController(aTableEntity.getModel());
    }

    @Override
    public boolean canRename() {
        return true;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.icon2Image(IconCache.getIcon(TREE_INDEX_ICON_NAME));
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getName() {
        return index.getName();
    }

    @Override
    public void setName(String val) {
        validateName(val);
        if (!val.equalsIgnoreCase(index.getName())) {
            DbTableIndexSpec newIndex = index.copy();
            newIndex.setName(val);
            modifyIndex(newIndex);
        }
    }

    public Boolean isUnique() {
        return index.isUnique();
    }

    public void setUnique(Boolean val) {
        if (!val.equals(index.isUnique())) {
            DbTableIndexSpec newIndex = index.copy();
            newIndex.setUnique(val);
            modifyIndex(newIndex);
        }
    }

    public Boolean isClustered() {
        return index.isClustered();
    }

    public void setClustered(Boolean val) {
        if (!val.equals(index.isClustered())) {
            DbTableIndexSpec newIndex = index.copy();
            newIndex.setClustered(val);
            modifyIndex(newIndex);
        }
    }

    public Boolean isHashed() {
        return index.isHashed();
    }

    public void setHashed(Boolean val) {
        if (!val.equals(index.isHashed())) {
            DbTableIndexSpec newIndex = index.copy();
            newIndex.setHashed(val);
            modifyIndex(newIndex);
        }
    }

    public DbTableIndexSpec getIndex() {
        return index;
    }

    public void modifyIndex(DbTableIndexSpec newIndex) {
        ModifyIndexEdit edit = new ModifyIndexEdit(sqlController, tableEntity, index.copy(), newIndex, index, tableEntity.getIndexes().indexOf(index));
        try {
            edit.redo();
        } catch (CannotRedoException ex) {
            notifyError(ex.getLocalizedMessage());
            return;
        }
        getUndo().undoableEditHappened(new UndoableEditEvent(this, edit));
    }

    private void notifyError(String errorStr) {
        NotifyDescriptor.Message d = new NotifyDescriptor.Message(errorStr, NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(d);
    }

    protected UndoRedo.Manager getUndo() {
        return getLookup().lookup(ModelUndoProvider.class).getModelUndo();
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = new Sheet();
        Sheet.Set pSet = Sheet.createPropertiesSet();
        sheet.put(pSet);
        PropertySupport.Name nameProp = new PropertySupport.Name(this, PROP_NAME, NbBundle.getMessage(UniqueProperty.class, "MSG_NamePropertyShortDescription")); //NOI18N
        pSet.put(nameProp);
        UniqueProperty uniqueProp = new UniqueProperty();
        pSet.put(uniqueProp);
        ClusteredProperty clusteredProp = new ClusteredProperty();
        pSet.put(clusteredProp);
        HashedProperty hashedProp = new HashedProperty();
        pSet.put(hashedProp);
        return sheet;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public void destroy() throws IOException {
        DropIndexEdit edit = new DropIndexEdit(sqlController, tableEntity, index, tableEntity.getIndexes().indexOf(index));
        try {
            edit.redo();
        } catch (CannotRedoException ex) {
            notifyError(ex.getLocalizedMessage());
            return;
        }
        getUndo().undoableEditHappened(new UndoableEditEvent(this, edit));
        index.getChangeSupport().removePropertyChangeListener(indexPropertyListener);
        ((TableIndexChildren)getChildren()).removeNotify();
        super.destroy();
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    addColumnAction,
                    DeleteAction.get(DeleteAction.class),
                    PropertiesAction.get(PropertiesAction.class)
                };
    }

    //TODO: add validation rules for index
    private void validateName(String val) {
        if (val == null || val.isEmpty()) {
            throw Exceptions.attachLocalizedMessage(new IllegalStateException(), String.format(NbBundle.getMessage(TableIndexNode.class, "MSG_InvalidIndexName"), val)); //NOI18N  
        }
    }

    protected class UniqueProperty extends Property<Boolean> {

        public UniqueProperty() {
            super(Boolean.class);
        }

        @Override
        public String getName() {
            return DbTableIndexSpec.UNIQUE_PROPERTY;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(UniqueProperty.class, "MSG_UniquePropertyShortDescription"); //NOI18N
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return true;
        }

        @Override
        public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
            return isUnique();
        }

        @Override
        public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setUnique(val);
        }
    }

    protected class ClusteredProperty extends Property<Boolean> {

        public ClusteredProperty() {
            super(Boolean.class);
        }

        @Override
        public String getName() {
            return DbTableIndexSpec.CLUSTERED_PROPERTY;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(UniqueProperty.class, "MSG_ClusteredPropertyShortDescription"); //NOI18N
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return true;
        }

        @Override
        public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
            return isClustered();
        }

        @Override
        public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setClustered(val);
        }
    }

    protected class HashedProperty extends Property<Boolean> {

        public HashedProperty() {
            super(Boolean.class);
        }

        @Override
        public String getName() {
            return DbTableIndexSpec.HASHED_PROPERTY;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(UniqueProperty.class, "MSG_HashedPropertyShortDescription"); //NOI18N
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return true;
        }

        @Override
        public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
            return isHashed();
        }

        @Override
        public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setHashed(val);
        }
    }

    public class AddIndexColumnAction extends AbstractAction {

        public AddIndexColumnAction() {
            super();
            putValue(Action.NAME, NbBundle.getMessage(AddIndexColumnAction.class, "MSG_AddIndexColumnActionName")); //NOI18N
            putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(AddIndexColumnAction.class, "MSG_AddIndexColumnActionShortDescription")); //NOI18N
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        private boolean indexHasColumn(String name) {
            for (DbTableIndexColumnSpec column : getIndex().getColumns()) {
                if (column.getColumnName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
        }
        
        private int getMaxOrdinalPosition(DbTableIndexSpec idx) {
            int maxOrdinalPosition= -1;
            for (DbTableIndexColumnSpec column : idx.getColumns()) {
                if (column.getOrdinalPosition() > maxOrdinalPosition) {
                    maxOrdinalPosition = column.getOrdinalPosition();
                }
            }
            return maxOrdinalPosition;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                TableColumnsPanel columnsPanel = new TableColumnsPanel(tableEntity);
                DialogDescriptor d = new DialogDescriptor(columnsPanel, tableEntity.getTableName() + ": select index field(s).");
                if (DialogDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(d)) && !columnsPanel.getSelected().isEmpty()) {
                    DbTableIndexSpec newIndex = getIndex().copy();
                    boolean indexModified = false;
                    for (Field field : columnsPanel.getSelected()) {
                        if (!indexHasColumn(field.getName())) {
                            DbTableIndexColumnSpec column = new DbTableIndexColumnSpec(field.getName(), true);
                            column.setOrdinalPosition(getMaxOrdinalPosition(newIndex) + 1);
                            newIndex.addColumn(column);
                            indexModified = true;
                        }
                    }
                    if (indexModified) {
                        modifyIndex(newIndex);
                    }
                }
            }
        }
    }
}
