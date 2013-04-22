/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * IndexesView.java
 *
 * Created on 09.08.2009, 12:32:54
 */
package com.eas.client.dbstructure.gui.view;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.DbClient;
import com.eas.client.DbMetadataCache;
import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.IconCache;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.gui.edits.CreateIndexEdit;
import com.eas.client.dbstructure.gui.edits.DbStructureCompoundEdit;
import com.eas.client.dbstructure.gui.edits.DbStructureUndoableEditSupport;
import com.eas.client.dbstructure.gui.edits.DropIndexEdit;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.DbTableIndexes;
import com.eas.client.model.ModelEditingListener;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.view.FieldsListModel;
import com.eas.client.model.gui.view.FieldsParametersListCellRenderer;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author Марат
 */
public class IndexesView extends javax.swing.JPanel {

    protected class IndexesModel extends DefaultListModel<DbTableIndexSpec> {

        public IndexesModel() {
            super();
        }

        public void fireDataChanged() {
            super.fireContentsChanged(this, 0, getSize() - 1);
        }

        @Override
        public int getSize() {
            if (entity != null) {
                achiveIndexes();
                List<DbTableIndexSpec> idxs = entity.getIndexes();
                if (idxs != null) {
                    return idxs.size();
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        }

        public void achiveIndexes() {
            if (entity != null && entity.getIndexes() == null) {
                DbClient lclient = entity.getModel().getClient();
                if (lclient != null) {
                    try {
                        DbMetadataCache mdCache = lclient.getDbMetadataCache(entity.getTableDbId());
                        assert entity.getTableName() != null;
                        String ltblName = entity.getTableName();
                        if (entity.getTableSchemaName() != null && !entity.getTableSchemaName().isEmpty()) {
                            ltblName = entity.getTableSchemaName() + "." + ltblName;
                        }
                        DbTableIndexes indexes = mdCache.getTableIndexes(ltblName);
                        if (indexes != null && indexes.getIndexes() != null && !indexes.getIndexes().isEmpty()) {
                            List<DbTableIndexSpec> indexesVector = new ArrayList<>();
                            for (DbTableIndexSpec index : indexes.getIndexes().values()) {
                                indexesVector.add(index);
                            }
                            entity.setIndexes(indexesVector);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(IndexesView.class.getName()).log(Level.SEVERE, null, ex);
                        List<DbTableIndexSpec> indexesVector = new ArrayList<>();
                        DbTableIndexSpec errorIndex = new DbTableIndexSpec();
                        errorIndex.setName(ex.getMessage());
                        indexesVector.add(errorIndex);
                        entity.setIndexes(indexesVector);
                    }
                }
            }
        }

        @Override
        public DbTableIndexSpec getElementAt(int index) {
            if (entity != null && index >= 0 && index < getSize()) {
                List<DbTableIndexSpec> idxs = entity.getIndexes();
                return idxs.get(index);
            } else {
                return null;
            }
        }
    }

    protected class IndexesCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof DbTableIndexSpec) {
                value = ((DbTableIndexSpec) value).getName();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    protected class IndexedFieldRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (entity != null && value instanceof String) {
                String fieldName = (String) value;
                Fields fields = entity.getFields();
                if (fields != null) {
                    int fieldIndex = fields.find(fieldName);
                    if (fieldIndex >= 1) {
                        Field field = fields.get(fieldIndex);
                        if (field != null) {
                            return listAvailableIndexedFields.getCellRenderer().getListCellRendererComponent(listAvailableIndexedFields, field, fieldIndex - 1, isSelected, hasFocus);
                        }
                    }
                }
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    protected class UsedIndexedFieldsModel extends DefaultTableModel {

        public final static int INDEXED_COLUMN_NAME = 0;
        public final static int INDEXED_COLUMN_ASC = 1;
        private final Class<?>[] types = new Class<?>[]{
            java.lang.String.class, java.lang.Boolean.class
        };
        private final boolean[] canEdit = new boolean[]{
            false, true
        };

        public UsedIndexedFieldsModel() {
            super(new Object[][]{},
                    new String[]{
                        DbStructureUtils.getString("Column"), DbStructureUtils.getString("Ascending")
                    });
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return types[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
        }

        @Override
        public int getRowCount() {
            if (listIndexes != null && listIndexes.getModel().getSize() > 0) {
                Object oSelected = listIndexes.getSelectedValue();
                if (oSelected != null && oSelected instanceof DbTableIndexSpec) {
                    DbTableIndexSpec lIndex = (DbTableIndexSpec) oSelected;
                    return lIndex.getColumns().size();
                }
            }
            return super.getRowCount();
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (listIndexes != null) {
                Object oSelected = listIndexes.getSelectedValue();
                if (oSelected != null && oSelected instanceof DbTableIndexSpec) {
                    DbTableIndexSpec lIndex = (DbTableIndexSpec) oSelected;
                    if (row >= 0 && row < lIndex.getColumns().size()) {
                        DbTableIndexColumnSpec iCol = lIndex.getColumns().get(row);
                        if (column == INDEXED_COLUMN_NAME) {
                            return iCol.getColumnName();
                        } else if (column == INDEXED_COLUMN_ASC) {
                            return iCol.isAscending();
                        }
                    } else {
                        return null;
                    }
                }
            }
            return super.getValueAt(row, column);
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (column == INDEXED_COLUMN_NAME) {
                assert false : "can't set value for indexed column name";
            } else if (column == INDEXED_COLUMN_ASC) {
                controller.getColumnAscendingInvertAction().actionPerformed(new ActionEvent(tblSelectedIndexedFields, ActionEvent.ACTION_PERFORMED, ""));
            }
        }
    }

    protected class IndexSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            updateIndexControls();
            if (tblSelectedIndexedFields.getSelectionModel().isSelectionEmpty()) {
                tblSelectedIndexedFields.getSelectionModel().setSelectionInterval(0, 0);
            }
        }
    }

    protected class IndexedColumnsSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            checkActions();
        }
    }

    protected class MouseColumnAdder extends MouseAdapter {

        public MouseColumnAdder() {
            super();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1) {
                controller.getAddColumnAction().actionPerformed(new ActionEvent(listAvailableIndexedFields, ActionEvent.ACTION_PERFORMED, ""));
            }
        }
    }

    protected class MouseColumnRemover extends MouseAdapter {

        public MouseColumnRemover() {
            super();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1) {
                controller.getRemoveColumnAction().actionPerformed(new ActionEvent(tblSelectedIndexedFields, ActionEvent.ACTION_PERFORMED, ""));
            }
        }
    }

    protected class IndexesChangedListener implements ModelEditingListener<FieldsEntity> {

        @Override
        public void entityAdded(FieldsEntity added) {
        }

        @Override
        public void entityRemoved(FieldsEntity removed) {
            if (removed == entity) {
                setEntity(null);
            }
        }

        public void entityChanged(FieldsEntity changed) {
        }

        @Override
        public void entityIndexesChanged(FieldsEntity changed) {
            updateIndexesList();
        }

        public void entityIndexChanged(FieldsEntity changed, DbTableIndexSpec indexChanged) {
            if (entity != null) {
                if (listIndexes.getSelectedValue() == indexChanged) {
                    updateIndexControls();
                } else {
                    listIndexes.setSelectedValue(indexChanged, true);
                }
            }
        }

        public void entityViewDataChanged(FieldsEntity changed) {
        }

        public void entityFieldChanged(FieldsEntity changed, Field aField) {
        }

        @Override
        public void relationAdded(Relation<FieldsEntity> added) {
        }

        @Override
        public void relationRemoved(Relation<FieldsEntity> removed) {
        }

        public void relationChanged(Relation<FieldsEntity> changed, boolean isLeft, boolean isParameter, String beforeName, String afterName) {
        }
    }
    protected FieldsEntity entity = null;
    protected DbSchemeModel model = null;
    protected SqlActionsController sqlActionsController = null;
    protected IndexesChangedListener indexesChangeListener = new IndexesChangedListener();
    protected FieldsParametersListCellRenderer<FieldsEntity> filedsRenderer = new FieldsParametersListCellRenderer<>();
    // models
    protected IndexesModel indexesModel = new IndexesModel();
    protected UsedIndexedFieldsModel selectedIndexedFieldsModel = new UsedIndexedFieldsModel();
    protected FieldsListModel<Field> availableFieldsModel = new FieldsListModel.FieldsModel();
    // actions
    protected IndexesController controller = new IndexesController();
    // editing
    protected UndoableEditSupport undoSupport = new DbStructureUndoableEditSupport();

    /** Creates new form IndexesView */
    public IndexesView(SqlActionsController aSqlActionsController) {
        super();
        initComponents();
        sqlActionsController = aSqlActionsController;
        listIndexes.setModel(indexesModel);
        listIndexes.setCellRenderer(new IndexesCellRenderer());
        listIndexes.addListSelectionListener(new IndexSelectionListener());
        tblSelectedIndexedFields.setModel(selectedIndexedFieldsModel);
        TableColumn nameCol = tblSelectedIndexedFields.getColumnModel().getColumn(UsedIndexedFieldsModel.INDEXED_COLUMN_NAME);
        nameCol.setCellRenderer(new IndexedFieldRenderer());
        nameCol.setPreferredWidth(nameCol.getWidth() * 5);
        tblSelectedIndexedFields.getSelectionModel().addListSelectionListener(new IndexedColumnsSelectionListener());
        listAvailableIndexedFields.setModel(availableFieldsModel);
        listAvailableIndexedFields.setCellRenderer(filedsRenderer);
        listAvailableIndexedFields.addMouseListener(new MouseColumnAdder());
        tblSelectedIndexedFields.addMouseListener(new MouseColumnRemover());
        setActionMap(controller);
    }

    public void checkActions() {
        if (controller != null) {
            controller.checkActions();
        }
    }

    public IndexesController getController() {
        return controller;
    }

    public SqlActionsController getSqlActionsController() {
        return sqlActionsController;
    }

    public void setSqlActionsController(SqlActionsController sqlActionsController) {
        this.sqlActionsController = sqlActionsController;
    }

    public DbSchemeModel getModel() {
        return model;
    }

    public void setModel(DbSchemeModel aModel) {
        if (model != null) {
            model.removeEditingListener(indexesChangeListener);
        }
        model = aModel;
        if (model != null) {
            model.addEditingListener(indexesChangeListener);
        }
    }

    public FieldsEntity getEntity() {
        return entity;
    }

    public void setEntity(FieldsEntity aEntity) {
        if (aEntity != null && aEntity.getModel() != null && model == null) {
            setModel((DbSchemeModel) aEntity.getModel());
        }
        entity = (FieldsEntity) aEntity;
        updateIndexesList();
        if (listIndexes.getSelectedValue() == null && listIndexes.getModel().getSize() > 0) {
            listIndexes.setSelectedIndex(0);
        }
        if (tblSelectedIndexedFields.getSelectionModel().isSelectionEmpty()) {
            tblSelectedIndexedFields.getSelectionModel().setSelectionInterval(0, 0);
        }
        if (entity != null) {
            lblTableName.setText(entity.getFormattedTableNameAndTitle());
        } else {
            lblTableName.setText("");// NO18IN
        }
        updateIndexControls();
        checkActions();
    }

    /**
     * Registers an <code>UndoableEditListener</code>.
     * The listener is notified whenever an edit occurs which can be undone.
     *
     * @param l  an <code>UndoableEditListener</code> object
     * @see #removeUndoableEditListener
     */
    public synchronized void addUndoableEditListener(UndoableEditListener l) {
        undoSupport.addUndoableEditListener(l);
    }

    /**
     * Removes an <code>UndoableEditListener</code>.
     *
     * @param l  the <code>UndoableEditListener</code> object to be removed
     * @see #addUndoableEditListener
     */
    public synchronized void removeUndoableEditListener(UndoableEditListener l) {
        undoSupport.removeUndoableEditListener(l);
    }

    protected void updateIndexesList() {
        if (entity == null) {
            listIndexes.clearSelection();
        }
        int selIdx = listIndexes.getSelectedIndex();

        filedsRenderer.setEntity(entity);
        indexesModel.fireDataChanged();

        availableFieldsModel.setFields(entity != null ? entity.getFields() : null);
        availableFieldsModel.fireDataChanged();

        selectedIndexedFieldsModel.fireTableDataChanged();
        if (selIdx >= 0 && selIdx < listIndexes.getModel().getSize()) {
            listIndexes.setSelectedIndex(selIdx);
        }
    }

    protected void updateIndexControls() {
        selectedIndexedFieldsModel.fireTableDataChanged();
        Object oSelected = listIndexes.getSelectedValue();
        DbTableIndexSpec lIndex = null;
        if (oSelected != null && oSelected instanceof DbTableIndexSpec) {
            lIndex = (DbTableIndexSpec) oSelected;
        }

        Action a = txtIndexName.getAction();
        txtIndexName.setAction(null);
        try {
            if (lIndex != null) {
                txtIndexName.setText(lIndex.getName());
            } else {
                txtIndexName.setText("");
            }
        } finally {
            txtIndexName.setAction(a);
        }
        a = chkUnique.getAction();
        chkUnique.setAction(null);
        try {
            if (lIndex != null) {
                chkUnique.setSelected(lIndex.isUnique());
            } else {
                chkUnique.setSelected(false);
            }
        } finally {
            chkUnique.setAction(a);
        }
        a = chkIndexClustered.getAction();
        chkIndexClustered.setAction(null);
        try {
            if (lIndex != null) {
                chkIndexClustered.setSelected(lIndex.isClustered());
            } else {
                chkIndexClustered.setSelected(false);
            }
        } finally {
            chkIndexClustered.setAction(a);
        }
        a = chkIndexHashed.getAction();
        chkIndexHashed.setAction(null);
        try {
            if (lIndex != null) {
                chkIndexHashed.setSelected(lIndex.isHashed());
            } else {
                chkIndexHashed.setSelected(false);
            }
        } finally {
            chkIndexHashed.setAction(a);
        }
        checkActions();
    }

    public class IndexesController extends ActionMap {

        protected Action addColumnAction = null;
        protected Action removeColumnAction = null;
        protected Action columnAscendingInvertAction = null;

        public IndexesController() {
            super();
            fillActions();
        }

        protected void fillActions() {
            put(AddIndexAction.class.getSimpleName(), new AddIndexAction());
            put(DropIndexAction.class.getSimpleName(), new DropIndexAction());

            put(MoveColumnDownAction.class.getSimpleName(), new MoveColumnDownAction());
            put(MoveColumnUpAction.class.getSimpleName(), new MoveColumnUpAction());

            put(RenameIndexAction.class.getSimpleName(), new RenameIndexAction());
            put(UniqueIndexChangeAction.class.getSimpleName(), new UniqueIndexChangeAction());
            put(ClusteredIndexChageAction.class.getSimpleName(), new ClusteredIndexChageAction());
            put(HashedIndexChageAction.class.getSimpleName(), new HashedIndexChageAction());

            addColumnAction = new AddColumnAction();
            put(AddColumnAction.class.getSimpleName(), addColumnAction);
            removeColumnAction = new RemoveColumnAction();
            put(RemoveColumnAction.class.getSimpleName(), removeColumnAction);

            columnAscendingInvertAction = new ColumnAscendingInvertAction();
            put(ColumnAscendingInvertAction.class.getSimpleName(), columnAscendingInvertAction);
        }

        public void checkActions() {
            for (Object key : allKeys()) {
                Action action = get(key);
                if (action != null) {
                    action.setEnabled(action.isEnabled());
                }
            }
        }

        public Action getAddColumnAction() {
            return addColumnAction;
        }

        public Action getColumnAscendingInvertAction() {
            return columnAscendingInvertAction;
        }

        public Action getRemoveColumnAction() {
            return removeColumnAction;
        }

        // add/delete indexes
        public class AddIndexAction extends AbstractAction {

            public AddIndexAction() {
                super();
                putValue(Action.NAME, DbStructureUtils.getString(AddIndexAction.class.getSimpleName()));
                putValue(Action.SHORT_DESCRIPTION, DbStructureUtils.getString(AddIndexAction.class.getSimpleName()));
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
                putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/index-add.png"));
            }

            @Override
            public boolean isEnabled() {
                return entity != null && isShowing() && !listAvailableIndexedFields.getSelectionModel().isSelectionEmpty();
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isEnabled()) {
                    DbTableIndexSpec lIndex = new DbTableIndexSpec();
                    Object[] oSelectedFields = listAvailableIndexedFields.getSelectedValues();
                    for (int i = 0; i < oSelectedFields.length; i++) {
                        Field field = (Field) oSelectedFields[i];
                        DbTableIndexColumnSpec column = new DbTableIndexColumnSpec(field.getName(), true);
                        column.setOrdinalPosition(i + 1);
                        lIndex.addColumn(column);
                    }
                    lIndex.setName("I" + String.valueOf(IDGenerator.genID()));
                    List<DbTableIndexSpec> idxes = entity.getIndexes();
                    CreateIndexEdit edit = new CreateIndexEdit(sqlActionsController, entity, lIndex, idxes != null ? idxes.size() : 0);
                    try {
                        edit.redo();
                        undoSupport.postEdit(edit);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(IndexesView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                    }
                    listIndexes.setSelectedIndex(listIndexes.getModel().getSize() - 1);
                }
            }
        }

        public class DropIndexAction extends AbstractAction {

            public DropIndexAction() {
                super();
                putValue(Action.NAME, DbStructureUtils.getString(DropIndexAction.class.getSimpleName()));
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
                putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/index-drop.png"));
            }

            @Override
            public boolean isEnabled() {
                return !listIndexes.isSelectionEmpty();
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isEnabled()) {
                    CompoundEdit section = new DbStructureCompoundEdit();
                    List<DbTableIndexSpec> selected = listIndexes.getSelectedValuesList();
                    int selectedIndex = listIndexes.getSelectedIndex();
                    for (int i = 0; i < selected.size(); i++) {
                        DbTableIndexSpec lIndex = selected.get(i);
                        DropIndexEdit edit = new DropIndexEdit(sqlActionsController, entity, lIndex, entity.getIndexes().indexOf(lIndex));
                        try {
                            edit.redo();
                            section.addEdit(edit);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(IndexesView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                    }
                    section.end();
                    if (section.isSignificant()) {
                        undoSupport.postEdit(section);
                    }
                    if (selectedIndex < listIndexes.getModel().getSize()) {
                        listIndexes.setSelectedIndex(selectedIndex);
                    } else {
                        if (listIndexes.getModel().getSize() > 0) {
                            listIndexes.setSelectedIndex(listIndexes.getModel().getSize() - 1);
                        }
                    }
                }
            }
        }

        // modify indexes
        protected class RenameIndexAction extends ModifyIndexAction {

            public RenameIndexAction() {
                super();
                putValue(Action.SHORT_DESCRIPTION, DbStructureUtils.getString(RenameIndexAction.class.getSimpleName()));
            }

            @Override
            public boolean isEnabled() {
                return listIndexes.getSelectedIndices() != null && listIndexes.getSelectedIndices().length == 1;
            }

            @Override
            public void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy) {
                indexCopy.setName(txtIndexName.getText());
            }

            @Override
            protected int[] getSelectedIndexedFields() {
                return tblSelectedIndexedFields.getSelectedRows();
            }

            @Override
            protected void setSelectedIndexedFields(int[] selectedIndexedFields) {
                if (selectedIndexedFields != null) {
                    tblSelectedIndexedFields.clearSelection();
                    for (int i = 0; i < selectedIndexedFields.length; i++) {
                        tblSelectedIndexedFields.getSelectionModel().addSelectionInterval(selectedIndexedFields[i], selectedIndexedFields[i]);
                    }
                    txtIndexName.requestFocus();
                }
            }
        }

        protected class UniqueIndexChangeAction extends ModifyIndexesAction {

            public UniqueIndexChangeAction() {
                super();
                putValue(Action.NAME, DbStructureUtils.getString(UniqueIndexChangeAction.class.getSimpleName()));
            }

            @Override
            public boolean isEnabled() {
                return !listIndexes.isSelectionEmpty();
            }

            @Override
            public void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy) {
                indexCopy.setUnique(chkUnique.isSelected());
            }
        }

        protected class ClusteredIndexChageAction extends ModifyIndexesAction {

            public ClusteredIndexChageAction() {
                super();
                putValue(Action.NAME, DbStructureUtils.getString(ClusteredIndexChageAction.class.getSimpleName()));
            }

            @Override
            public boolean isEnabled() {
                return !listIndexes.isSelectionEmpty();
            }

            @Override
            public void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy) {
                indexCopy.setClustered(chkIndexClustered.isSelected());
            }
        }

        protected class HashedIndexChageAction extends ModifyIndexesAction {

            public HashedIndexChageAction() {
                super();
                putValue(Action.NAME, DbStructureUtils.getString(HashedIndexChageAction.class.getSimpleName()));
            }

            @Override
            public boolean isEnabled() {
                return !listIndexes.isSelectionEmpty();
            }

            @Override
            public void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy) {
                indexCopy.setHashed(chkIndexHashed.isSelected());
            }
        }

        protected abstract class ModifyIndexAction extends AbstractAction {

            @Override
            public boolean isEnabled() {
                return !listIndexes.isSelectionEmpty();
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isEnabled()) {
                    int selectedIndex = listIndexes.getSelectedIndex();
                    int[] selectedAvailable = listAvailableIndexedFields.getSelectedIndices();
                    int[] selectedIndexedFields = getSelectedIndexedFields();
                    Object oIndex = listIndexes.getSelectedValue();
                    assert oIndex instanceof DbTableIndexSpec;
                    DbTableIndexSpec index = (DbTableIndexSpec) oIndex;
                    DbTableIndexSpec indexCopy = index.copy();
                    calculateNewIndex(index, indexCopy);
                    if (!index.isEqual(indexCopy)) {
                        CompoundEdit section = new DbStructureCompoundEdit();
                        int indexPosition = entity.getIndexes().indexOf(index);
                        DropIndexEdit dEdit = new DropIndexEdit(sqlActionsController, entity, index, indexPosition);
                        try {
                            dEdit.redo();
                            section.addEdit(dEdit);
                        } catch (CannotRedoException ex) {
                            JOptionPane.showMessageDialog(IndexesView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        CreateIndexEdit cEdit = new CreateIndexEdit(sqlActionsController, entity, indexCopy, indexPosition);
                        try {
                            cEdit.redo();
                            section.addEdit(cEdit);
                        } catch (CannotRedoException ex) {
                            JOptionPane.showMessageDialog(IndexesView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        section.end();
                        if (section.isSignificant()) {
                            undoSupport.postEdit(section);
                        }
                        listIndexes.setSelectedIndex(selectedIndex);
                        listAvailableIndexedFields.setSelectedIndices(selectedAvailable);
                        setSelectedIndexedFields(selectedIndexedFields);
                    }
                }
            }

            public abstract void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy);

            protected abstract int[] getSelectedIndexedFields();

            protected abstract void setSelectedIndexedFields(int[] selectedIndexedFields);
        }

        protected abstract class ModifyIndexesAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isEnabled()) {
                    int[] selectedIndices = listIndexes.getSelectedIndices();
                    int[] selectedIndexedFields = tblSelectedIndexedFields.getSelectedRows();
                    int[] selectedAvalable = listAvailableIndexedFields.getSelectedIndices();
                    try {
                        Object[] oIndexes = listIndexes.getSelectedValues();
                        CompoundEdit section = new DbStructureCompoundEdit();
                        for (int i = 0; i < oIndexes.length; i++) {
                            Object oIndex = oIndexes[i];
                            assert oIndex instanceof DbTableIndexSpec;
                            DbTableIndexSpec index = (DbTableIndexSpec) oIndex;
                            DbTableIndexSpec indexCopy = index.copy();
                            calculateNewIndex(index, indexCopy);
                            if (!index.isEqual(indexCopy)) {
                                int indexPosition = entity.getIndexes().indexOf(index);
                                DropIndexEdit dEdit = new DropIndexEdit(sqlActionsController, entity, index, indexPosition);
                                try {
                                    dEdit.redo();
                                    section.addEdit(dEdit);
                                } catch (CannotRedoException ex) {
                                    JOptionPane.showMessageDialog(IndexesView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                CreateIndexEdit cEdit = new CreateIndexEdit(sqlActionsController, entity, indexCopy, indexPosition);
                                try {
                                    cEdit.redo();
                                    section.addEdit(cEdit);
                                } catch (CannotRedoException ex) {
                                    JOptionPane.showMessageDialog(IndexesView.this, ex.getLocalizedMessage(), DbStructureUtils.getString("dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            }
                        }
                        section.end();
                        if (section.isSignificant()) {
                            undoSupport.postEdit(section);
                        }
                    } finally {
                        listIndexes.setSelectedIndices(selectedIndices);
                        tblSelectedIndexedFields.getSelectionModel().clearSelection();
                        for (int i = 0; i < selectedIndexedFields.length; i++) {
                            tblSelectedIndexedFields.getSelectionModel().addSelectionInterval(selectedIndexedFields[i], selectedIndexedFields[i]);
                        }
                        listAvailableIndexedFields.setSelectedIndices(selectedAvalable);
                        if (e.getSource() instanceof Component) {
                            ((Component) e.getSource()).requestFocus();
                        }
                    }
                }
            }

            public abstract void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy);
        }

        // modify fields list
        protected class AddColumnAction extends ModifyIndexAction {

            public AddColumnAction() {
                super();
                putValue(Action.SHORT_DESCRIPTION, DbStructureUtils.getString(AddColumnAction.class.getSimpleName()));
                putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/column-add.png"));
            }

            @Override
            public void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy) {
                Object[] oFields2Add = listAvailableIndexedFields.getSelectedValues();
                if (oFields2Add != null) {
                    for (Object oField : oFields2Add) {
                        assert oField instanceof Field;
                        Field field = (Field) oField;
                        if (!index.findColumnByName(field.getName())) {
                            DbTableIndexColumnSpec column = new DbTableIndexColumnSpec(field.getName(), true);
                            indexCopy.addColumn(column);
                        }
                    }
                }
            }

            @Override
            public boolean isEnabled() {
                return listIndexes.getSelectedIndices() != null && listIndexes.getSelectedIndices().length == 1
                        && !listAvailableIndexedFields.isSelectionEmpty();
            }

            @Override
            protected int[] getSelectedIndexedFields() {
                return tblSelectedIndexedFields.getSelectedRows();
            }

            @Override
            protected void setSelectedIndexedFields(int[] selectedIndexedFields) {
                List<Integer> toSelect = new ArrayList<>();
                Object oIndex = listIndexes.getSelectedValue();
                Object[] oFields2Add = listAvailableIndexedFields.getSelectedValues();
                if (oFields2Add != null && oIndex != null && oIndex instanceof DbTableIndexSpec) {
                    DbTableIndexSpec index = (DbTableIndexSpec) oIndex;
                    for (int i = 0; i < oFields2Add.length; i++) {
                        Object oField = oFields2Add[i];
                        assert oField instanceof Field;
                        Field field = (Field) oField;
                        if (index.findColumnByName(field.getName())) {
                            toSelect.add(index.indexOfColumnByName(field.getName()));
                        }
                    }
                }
                if (toSelect.isEmpty()) {
                    tblSelectedIndexedFields.getSelectionModel().clearSelection();
                    for (int i = 0; i < selectedIndexedFields.length; i++) {
                        tblSelectedIndexedFields.getSelectionModel().addSelectionInterval(selectedIndexedFields[i], selectedIndexedFields[i]);
                    }
                } else {
                    tblSelectedIndexedFields.getSelectionModel().clearSelection();
                    for (int i = 0; i < toSelect.size(); i++) {
                        tblSelectedIndexedFields.getSelectionModel().addSelectionInterval(toSelect.get(i), toSelect.get(i));
                    }
                }
            }
        }

        protected class RemoveColumnAction extends ModifyIndexAction {

            public RemoveColumnAction() {
                super();
                putValue(Action.SHORT_DESCRIPTION, DbStructureUtils.getString(RemoveColumnAction.class.getSimpleName()));
                putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/column-remove.png"));
            }

            @Override
            public boolean isEnabled() {
                return listIndexes.getSelectedIndices() != null && listIndexes.getSelectedIndices().length == 1
                        && tblSelectedIndexedFields.getSelectedRowCount() > 0 && tblSelectedIndexedFields.getSelectedRowCount() < tblSelectedIndexedFields.getModel().getRowCount();
            }

            @Override
            public void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy) {
                Set<Integer> selectedRows = new HashSet<>();
                int[] selectedIdxes = tblSelectedIndexedFields.getSelectedRows();
                for (int row : selectedIdxes) {
                    selectedRows.add(row);
                }
                for (int i = indexCopy.getColumns().size() - 1; i >= 0; i--) {
                    if (selectedRows.contains(i)) {
                        indexCopy.getColumns().remove(i);
                    }
                }
            }

            @Override
            protected int[] getSelectedIndexedFields() {
                return tblSelectedIndexedFields.getSelectedRows();
            }

            @Override
            protected void setSelectedIndexedFields(int[] selectedIndexedFields) {
                if (selectedIndexedFields != null) {
                    int minSelectedIndexedField = Integer.MAX_VALUE;
                    for (int i = 0; i < selectedIndexedFields.length; i++) {
                        if (minSelectedIndexedField > selectedIndexedFields[i]) {
                            minSelectedIndexedField = selectedIndexedFields[i];
                        }
                    }
                    if (minSelectedIndexedField < tblSelectedIndexedFields.getRowCount()) {
                        tblSelectedIndexedFields.getSelectionModel().setSelectionInterval(minSelectedIndexedField, minSelectedIndexedField);
                    } else {
                        tblSelectedIndexedFields.getSelectionModel().setSelectionInterval(tblSelectedIndexedFields.getRowCount() - 1, tblSelectedIndexedFields.getRowCount() - 1);
                    }
                }
            }
        }

        protected class ColumnAscendingInvertAction extends ModifyIndexAction {

            public ColumnAscendingInvertAction() {
                super();
                putValue(Action.SHORT_DESCRIPTION, DbStructureUtils.getString(ColumnAscendingInvertAction.class.getSimpleName()));
            }

            @Override
            public boolean isEnabled() {
                return listIndexes.getSelectedIndices() != null && listIndexes.getSelectedIndices().length == 1
                        && tblSelectedIndexedFields.getSelectedRowCount() == 1
                        && tblSelectedIndexedFields.getSelectedRow() < tblSelectedIndexedFields.getModel().getRowCount() - 1;
            }

            @Override
            public void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy) {
                int idx2InvertAscending = tblSelectedIndexedFields.getSelectedRow();
                DbTableIndexColumnSpec column = indexCopy.getColumns().get(idx2InvertAscending);
                column.setAscending(!column.isAscending());
            }

            @Override
            protected int[] getSelectedIndexedFields() {
                return tblSelectedIndexedFields.getSelectedRows();
            }

            @Override
            protected void setSelectedIndexedFields(int[] selectedIndexedFields) {
                if (selectedIndexedFields != null) {
                    assert selectedIndexedFields.length == 1;
                    tblSelectedIndexedFields.getSelectionModel().setSelectionInterval(selectedIndexedFields[0], selectedIndexedFields[0]);
                }
            }
        }

        protected class MoveColumnDownAction extends ModifyIndexAction {

            public MoveColumnDownAction() {
                super();
                putValue(Action.SHORT_DESCRIPTION, DbStructureUtils.getString(MoveColumnDownAction.class.getSimpleName()));
                putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/arrow-down.png"));
            }

            @Override
            public boolean isEnabled() {
                return listIndexes.getSelectedIndices() != null && listIndexes.getSelectedIndices().length == 1
                        && tblSelectedIndexedFields.getSelectedRowCount() == 1
                        && tblSelectedIndexedFields.getSelectedRow() < tblSelectedIndexedFields.getModel().getRowCount() - 1;
            }

            @Override
            public void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy) {
                int idx2MoveDown = tblSelectedIndexedFields.getSelectedRow();
                DbTableIndexColumnSpec column = indexCopy.getColumns().remove(idx2MoveDown);
                indexCopy.getColumns().add(idx2MoveDown + 1, column);
            }

            @Override
            protected int[] getSelectedIndexedFields() {
                return tblSelectedIndexedFields.getSelectedRows();
            }

            @Override
            protected void setSelectedIndexedFields(int[] selectedIndexedFields) {
                if (selectedIndexedFields != null) {
                    assert selectedIndexedFields.length == 1;
                    if (selectedIndexedFields[0] + 1 < tblSelectedIndexedFields.getRowCount()) {
                        tblSelectedIndexedFields.getSelectionModel().setSelectionInterval(selectedIndexedFields[0] + 1, selectedIndexedFields[0] + 1);
                    } else {
                        tblSelectedIndexedFields.getSelectionModel().setSelectionInterval(tblSelectedIndexedFields.getRowCount() - 1, tblSelectedIndexedFields.getRowCount() - 1);
                    }
                }
            }
        }

        protected class MoveColumnUpAction extends ModifyIndexAction {

            public MoveColumnUpAction() {
                super();
                putValue(Action.SHORT_DESCRIPTION, DbStructureUtils.getString(MoveColumnUpAction.class.getSimpleName()));
                putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/arrow-up.png"));
            }

            @Override
            public boolean isEnabled() {
                return listIndexes.getSelectedIndices() != null && listIndexes.getSelectedIndices().length == 1
                        && tblSelectedIndexedFields.getSelectedRowCount() == 1
                        && tblSelectedIndexedFields.getSelectedRow() > 0;
            }

            @Override
            public void calculateNewIndex(DbTableIndexSpec index, DbTableIndexSpec indexCopy) {
                int idx2MoveUp = tblSelectedIndexedFields.getSelectedRow();
                DbTableIndexColumnSpec column = indexCopy.getColumns().remove(idx2MoveUp);
                indexCopy.getColumns().add(idx2MoveUp - 1, column);
            }

            @Override
            protected int[] getSelectedIndexedFields() {
                return tblSelectedIndexedFields.getSelectedRows();
            }

            @Override
            protected void setSelectedIndexedFields(int[] selectedIndexedFields) {
                if (selectedIndexedFields != null) {
                    assert selectedIndexedFields.length == 1;
                    if (selectedIndexedFields[0] - 1 >= 0) {
                        tblSelectedIndexedFields.getSelectionModel().setSelectionInterval(selectedIndexedFields[0] - 1, selectedIndexedFields[0] - 1);
                    } else {
                        tblSelectedIndexedFields.getSelectionModel().setSelectionInterval(0, 0);
                    }
                }
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitIndexes = new javax.swing.JSplitPane();
        scrollIndexes = new javax.swing.JScrollPane();
        listIndexes = new javax.swing.JList<DbTableIndexSpec>();
        splitIndexedFields = new javax.swing.JSplitPane();
        scrollIndexProperties = new javax.swing.JScrollPane();
        pnlIndexProperties = new javax.swing.JPanel();
        txtIndexName = new javax.swing.JTextField();
        lblIndexName = new javax.swing.JLabel();
        chkIndexClustered = new javax.swing.JCheckBox();
        chkIndexHashed = new javax.swing.JCheckBox();
        chkUnique = new javax.swing.JCheckBox();
        splitSelectedIndexedFields = new javax.swing.JSplitPane();
        scrollAvailableIndexedFields = new javax.swing.JScrollPane();
        listAvailableIndexedFields = new javax.swing.JList<Field>();
        pnlSelectedIndexedFieldsContainer = new javax.swing.JPanel();
        scrollSelectedIndexedFields = new javax.swing.JScrollPane();
        tblSelectedIndexedFields = new javax.swing.JTable();
        pnlIndexedFieldsEditPanel = new javax.swing.JPanel();
        btnAdd2Indexed = new javax.swing.JButton();
        btnDeleteFromIndexed = new javax.swing.JButton();
        btnMoveUp = new javax.swing.JButton();
        btnMoveDown = new javax.swing.JButton();
        lblTableName = new javax.swing.JLabel();

        splitIndexes.setDividerLocation(240);
        splitIndexes.setOneTouchExpandable(true);

        scrollIndexes.setViewportView(listIndexes);

        splitIndexes.setLeftComponent(scrollIndexes);

        splitIndexedFields.setBorder(null);
        splitIndexedFields.setDividerLocation(500);
        splitIndexedFields.setOneTouchExpandable(true);

        txtIndexName.setAction(controller.get(IndexesController.RenameIndexAction.class.getSimpleName()));

        lblIndexName.setText(DbStructureUtils.getString("lblIndexName")); // NOI18N

        chkIndexClustered.setAction(controller.get(IndexesController.ClusteredIndexChageAction.class.getSimpleName()));
        chkIndexClustered.setText(DbStructureUtils.getString("lblIndexClustered")); // NOI18N

        chkIndexHashed.setAction(controller.get(IndexesController.HashedIndexChageAction.class.getSimpleName()));
        chkIndexHashed.setText(DbStructureUtils.getString("chkIndexHashed")); // NOI18N

        chkUnique.setAction(controller.get(IndexesController.UniqueIndexChangeAction.class.getSimpleName()));
        chkUnique.setText(DbStructureUtils.getString("chkUnique")); // NOI18N

        javax.swing.GroupLayout pnlIndexPropertiesLayout = new javax.swing.GroupLayout(pnlIndexProperties);
        pnlIndexProperties.setLayout(pnlIndexPropertiesLayout);
        pnlIndexPropertiesLayout.setHorizontalGroup(
            pnlIndexPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIndexPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIndexPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chkIndexClustered, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .addComponent(chkUnique, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .addComponent(txtIndexName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .addComponent(lblIndexName, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkIndexHashed, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlIndexPropertiesLayout.setVerticalGroup(
            pnlIndexPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIndexPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblIndexName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIndexName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(chkUnique)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkIndexClustered)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkIndexHashed)
                .addContainerGap(257, Short.MAX_VALUE))
        );

        scrollIndexProperties.setViewportView(pnlIndexProperties);

        splitIndexedFields.setRightComponent(scrollIndexProperties);

        splitSelectedIndexedFields.setBorder(null);
        splitSelectedIndexedFields.setDividerLocation(150);
        splitSelectedIndexedFields.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitSelectedIndexedFields.setOneTouchExpandable(true);

        listAvailableIndexedFields.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listAvailableIndexedFieldsValueChanged(evt);
            }
        });
        scrollAvailableIndexedFields.setViewportView(listAvailableIndexedFields);

        splitSelectedIndexedFields.setBottomComponent(scrollAvailableIndexedFields);

        pnlSelectedIndexedFieldsContainer.setLayout(new java.awt.BorderLayout());

        tblSelectedIndexedFields.setRowHeight(20);
        scrollSelectedIndexedFields.setViewportView(tblSelectedIndexedFields);

        pnlSelectedIndexedFieldsContainer.add(scrollSelectedIndexedFields, java.awt.BorderLayout.CENTER);

        btnAdd2Indexed.setAction(controller.get(IndexesController.AddColumnAction.class.getSimpleName()));

        btnDeleteFromIndexed.setAction(controller.get(IndexesController.RemoveColumnAction.class.getSimpleName()));

        btnMoveUp.setAction(controller.get(IndexesController.MoveColumnUpAction.class.getSimpleName()));

        btnMoveDown.setAction(controller.get(IndexesController.MoveColumnDownAction.class.getSimpleName()));

        lblTableName.setForeground(new java.awt.Color(0, 51, 255));
        lblTableName.setText("Table name");

        javax.swing.GroupLayout pnlIndexedFieldsEditPanelLayout = new javax.swing.GroupLayout(pnlIndexedFieldsEditPanel);
        pnlIndexedFieldsEditPanel.setLayout(pnlIndexedFieldsEditPanelLayout);
        pnlIndexedFieldsEditPanelLayout.setHorizontalGroup(
            pnlIndexedFieldsEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIndexedFieldsEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTableName)
                .addGap(240, 240, 240)
                .addComponent(btnAdd2Indexed, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDeleteFromIndexed, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMoveUp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMoveDown, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );
        pnlIndexedFieldsEditPanelLayout.setVerticalGroup(
            pnlIndexedFieldsEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIndexedFieldsEditPanelLayout.createSequentialGroup()
                .addGroup(pnlIndexedFieldsEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnAdd2Indexed, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteFromIndexed, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMoveUp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMoveDown, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTableName))
                .addGap(0, 0, 0))
        );

        pnlSelectedIndexedFieldsContainer.add(pnlIndexedFieldsEditPanel, java.awt.BorderLayout.SOUTH);

        splitSelectedIndexedFields.setLeftComponent(pnlSelectedIndexedFieldsContainer);

        splitIndexedFields.setLeftComponent(splitSelectedIndexedFields);

        splitIndexes.setRightComponent(splitIndexedFields);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitIndexes, javax.swing.GroupLayout.DEFAULT_SIZE, 799, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitIndexes, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void listAvailableIndexedFieldsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listAvailableIndexedFieldsValueChanged
        checkActions();
    }//GEN-LAST:event_listAvailableIndexedFieldsValueChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd2Indexed;
    private javax.swing.JButton btnDeleteFromIndexed;
    private javax.swing.JButton btnMoveDown;
    private javax.swing.JButton btnMoveUp;
    private javax.swing.JCheckBox chkIndexClustered;
    private javax.swing.JCheckBox chkIndexHashed;
    private javax.swing.JCheckBox chkUnique;
    private javax.swing.JLabel lblIndexName;
    private javax.swing.JLabel lblTableName;
    private javax.swing.JList<Field> listAvailableIndexedFields;
    public javax.swing.JList<DbTableIndexSpec> listIndexes;
    private javax.swing.JPanel pnlIndexProperties;
    private javax.swing.JPanel pnlIndexedFieldsEditPanel;
    private javax.swing.JPanel pnlSelectedIndexedFieldsContainer;
    private javax.swing.JScrollPane scrollAvailableIndexedFields;
    private javax.swing.JScrollPane scrollIndexProperties;
    private javax.swing.JScrollPane scrollIndexes;
    private javax.swing.JScrollPane scrollSelectedIndexedFields;
    private javax.swing.JSplitPane splitIndexedFields;
    private javax.swing.JSplitPane splitIndexes;
    private javax.swing.JSplitPane splitSelectedIndexedFields;
    private javax.swing.JTable tblSelectedIndexedFields;
    private javax.swing.JTextField txtIndexName;
    // End of variables declaration//GEN-END:variables
}
