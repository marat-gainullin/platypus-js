/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.designer.datamodel.nodes.FieldNode;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.actions.MoveDownAction;
import org.openide.actions.MoveUpAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class TableIndexColumnNode extends AbstractNode {

    DbTableIndexColumnSpec columnSpec;
    FieldsEntity tableEntity;
    private final PropertyChangeListener columnspecListener;
    protected TypesResolver resolver;

    public TableIndexColumnNode(DbTableIndexColumnSpec aColumnSpec, FieldsEntity aTableEntity) {
        super(Children.LEAF);
        columnSpec = aColumnSpec;
        tableEntity = aTableEntity;
        columnspecListener = (PropertyChangeEvent evt) -> {
            if (DbTableIndexColumnSpec.ASCENDING_PROPERTY.equals(evt.getPropertyName())) {
                firePropertyChange(DbTableIndexColumnSpec.ASCENDING_PROPERTY, evt.getOldValue(), evt.getNewValue());
            }
        };
        columnSpec.getChangeSupport().addPropertyChangeListener(columnspecListener);
        try {
            resolver = tableEntity.getModel().getBasesProxy().getMetadataCache(tableEntity.getModel().getDatasourceName()).getDatasourceSqlDriver().getTypesResolver();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public String getName() {
        return columnSpec.getColumnName();
    }

    @Override
    public String getShortDescription() {
        return NbBundle.getMessage(TableIndexNode.UniqueProperty.class, "MSG_TableIndexColumnNodeShortDescription");//NOI18N
    }

    private Field getField() {
        return tableEntity.getFields().get(columnSpec.getColumnName());
    }

    @Override
    public Image getIcon(int type) {
        return FieldNode.getIcon(type, getField(), resolver);
    }

    public Boolean isAscending() {
        return columnSpec.isAscending();
    }

    @Override
    public boolean canDestroy() {
        return getIndexNode() != null && getIndexNode().getIndex().getColumns().size() > 1;
    }

    @Override
    public void destroy() throws IOException {
        columnSpec.getChangeSupport().removePropertyChangeListener(columnspecListener);
        DbTableIndexSpec newIndex = getIndexNode().getIndex().copy();
        DbTableIndexColumnSpec columnToDelete = newIndex.getColumn(columnSpec.getColumnName());
        if (columnToDelete != null) {
            newIndex.getColumns().remove(columnToDelete);
            getIndexNode().modifyIndex(newIndex);
        }
    }

    public void setAscending(Boolean val) {
        if (!val.equals(columnSpec.isAscending())) {
            TableIndexNode indexNode = getIndexNode();
            DbTableIndexSpec newIndex = indexNode.getIndex().copy();
            newIndex.getColumn(columnSpec.getColumnName()).setAscending(val);
            indexNode.modifyIndex(newIndex);
        }
    }

    private TableIndexNode getIndexNode() {
        return (TableIndexNode) getParentNode();
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = new Sheet();
        Sheet.Set pSet = Sheet.createPropertiesSet();
        sheet.put(pSet);
        PropertySupport.Name nameProp = new PropertySupport.Name(this, PROP_NAME, NbBundle.getMessage(TableIndexNode.UniqueProperty.class, "MSG_ColumnNamePropertyShortDescription")); //NOI18N
        pSet.put(nameProp);
        AscendingProperty ascendingProp = new AscendingProperty();
        pSet.put(ascendingProp);
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    DeleteAction.get(DeleteAction.class),
                    MoveUpAction.get(MoveUpAction.class),
                    MoveDownAction.get(MoveDownAction.class),
                    PropertiesAction.get(PropertiesAction.class)
                };
    }

    protected class AscendingProperty extends Property<Boolean> {

        public AscendingProperty() {
            super(Boolean.class);
        }

        @Override
        public String getName() {
            return DbTableIndexColumnSpec.ASCENDING_PROPERTY;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(TableIndexNode.UniqueProperty.class, "MSG_AscendingPropertyShortDescription"); //NOI18N
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
            return isAscending();
        }

        @Override
        public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setAscending(val);
        }
    }
    
    protected class DeleteColumnAction extends DeleteAction {

        @Override
        public boolean isEnabled() {
            return getIndexNode().getIndex().getColumns().size() > 1;
        }
    }
}
