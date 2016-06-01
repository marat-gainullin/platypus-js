/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.IconCache;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.gui.edits.CreateIndexEdit;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.model.Entity;
import com.eas.client.model.ModelEditingListener;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.designer.datamodel.ModelUndoProvider;
import com.eas.util.IdGenerator;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditEvent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.UndoRedo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class TableIndexesNode extends AbstractNode {

    protected static final String TREE_INDEX_ICON_NAME = "16x16/indexes.png";//NOI18N
    protected static final String INDEX_DEFAULT_PREFIX = "I";//NOI18N
    protected AddIndexAction addIndexAction = new AddIndexAction();
    protected FieldsEntity entity;
    protected SqlActionsController sqlController;
    protected ModelEditingListener modelEditingListener = new ModelEditingListenerImpl();

    public TableIndexesNode(Children aChildren, FieldsEntity anEntity, Lookup aLookup) {
        super(aChildren, aLookup);
        entity = anEntity;
        try {
            sqlController = new SqlActionsController(entity.getModel());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        entity.getModel().addEditingListener(modelEditingListener);
    }

    @Override
    public void destroy() throws IOException {
        entity.getModel().removeEditingListener(modelEditingListener);
        super.destroy();
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
        return NbBundle.getMessage(TableIndexesNode.class, "MSG_TableIndexesNodeName");//NOI18N
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    addIndexAction
                };
    }

    protected UndoRedo.Manager getUndo() {
        return getLookup().lookup(ModelUndoProvider.class).getModelUndo();
    }

    public static class TableIndexesKey {
    }

    public class AddIndexAction extends AbstractAction {

        public AddIndexAction() {
            super();
            putValue(Action.NAME, NbBundle.getMessage(DbStructureUtils.class, AddIndexAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(DbStructureUtils.class, AddIndexAction.class.getSimpleName()));
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                TableColumnsPanel columnsPanel = new TableColumnsPanel(entity);
                DialogDescriptor d = new DialogDescriptor(columnsPanel, entity.getTableName() + ": select index field(s).");
                if (DialogDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(d)) && !columnsPanel.getSelected().isEmpty()) {
                    DbTableIndexSpec newIndex = new DbTableIndexSpec();
                    int i = 0;
                    for (Field field : columnsPanel.getSelected()) {
                        DbTableIndexColumnSpec column = new DbTableIndexColumnSpec(field.getName(), true);
                        column.setOrdinalPosition(i++);
                        newIndex.addColumn(column);
                    }
                    newIndex.setName(INDEX_DEFAULT_PREFIX + IdGenerator.genStringId());
                    List<DbTableIndexSpec> idxes = entity.getIndexes();
                    CreateIndexEdit edit = new CreateIndexEdit(sqlController, entity, newIndex, idxes != null ? idxes.size() : 0);
                    try {
                        edit.redo();
                        getUndo().undoableEditHappened(new UndoableEditEvent(this, edit));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    protected class ModelEditingListenerImpl implements ModelEditingListener {

        @Override
        public void entityAdded(Entity e) {
        }

        @Override
        public void entityRemoved(Entity e) {
        }

        @Override
        public void relationAdded(Relation rltn) {
            updtateChildren(rltn);
        }

        @Override
        public void relationRemoved(Relation rltn) {
            updtateChildren(rltn);
        }

        @Override
        public void entityIndexesChanged(Entity e) {
            updtateChildren();
        }
        
        private void updtateChildren(Relation rltn) {
            if (entity.equals(rltn.getLeftEntity()) || entity.equals(rltn.getRightEntity())) {            
                updtateChildren(); 
            }
        }
                    
        private void updtateChildren() {
            entity.achiveIndexes();
            ((TableIndexesChildren) getChildren()).update();
        }
    }
}
