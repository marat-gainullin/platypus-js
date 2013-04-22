/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DbGridCustomizer.java
 *
 * Created on 12.05.2009, 11:07:39
 */
package com.eas.dbcontrols.grid.customizer;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.datamodel.ApplicationEntity;
import com.eas.client.datamodel.Entity;
import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.datamodel.gui.selectors.ModelElementSelector;
import com.eas.client.datamodel.gui.selectors.ModelElementValidator;
import com.eas.client.queries.Query;
import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlChangeEdit;
import com.eas.dbcontrols.DbControlCustomizer;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignUtils;
import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.DesignIconCache;
import com.eas.dbcontrols.FieldRefRenderer;
import com.eas.dbcontrols.SampleCustomizersControlsFactory;
import com.eas.dbcontrols.ScriptEvents;
import com.eas.dbcontrols.date.DbDate;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.fontchooser.FontChooser;
import com.eas.dbcontrols.grid.DbGridCellDesignInfo;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.grid.DbGridRowsColumnsDesignInfo;
import com.eas.dbcontrols.grid.DbGridTreeDesignInfo;
import com.eas.dbcontrols.grid.customizer.actions.DbGridColumnSnapshotAction;
import com.eas.dbcontrols.grid.customizer.actions.DbGridColumnSnapshotClearAction;
import com.eas.dbcontrols.grid.customizer.actions.DbGridColumnsStructureSnapshotAction;
import com.eas.dbcontrols.grid.customizer.actions.DbGridRowsColumnsSnapshotAction;
import com.eas.dbcontrols.grid.customizer.actions.DbGridRowsColumnsSnapshotClearAction;
import com.eas.dbcontrols.grid.customizer.actions.DbGridTreeSnapshotAction;
import com.eas.dbcontrols.grid.customizer.actions.DbGridTreeSnapshotClearAction;
import com.eas.dbcontrols.grid.customizer.columnstree.DbGridColumnsTreeEditor;
import com.eas.dbcontrols.grid.customizer.columnstree.DbGridColumnsTreeModel;
import com.eas.dbcontrols.grid.customizer.columnstree.DbGridColumnsTreeRenderer;
import com.eas.dbcontrols.grid.customizer.columnstree.DbGridColumnsTreeTransferHandler;
import com.eas.dbcontrols.grid.edits.DbColumnChangeEdit;
import com.eas.dbcontrols.grid.edits.DbGridHeaderStructureEdit;
import com.eas.dbcontrols.visitors.DbControlClassFinder;
import com.eas.store.Object2Dom;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.UndoableEditSupport;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class DbGridCustomizer extends DbControlCustomizer implements java.beans.Customizer {

    protected DbGridCellsDatasourceSelectAction cellDsSelectAction = null;
    protected DbGridRowKeySelectAction cellRowKeySelectAction = null;
    protected DbGridColKeySelectAction cellColKeySelectAction = null;
    protected DbGridCellsValueFieldSelectAction cellValueSelectAction = null;
    protected DbGridCellControlChangeAction cellControlSelectAction = null;
    protected DbGridTreeUnaryLinkSelectAction unaryLinkFieldSelectAction = null;
    protected DbGridTreeParamChangeAction treeParamChangeAction = null;
    protected DbGridTreeParamSourceSelectAction paramSourceSelectAction = null;
    protected DbGridColumnsCut columnCutAction = null;
    protected DbGridColumnsCopy columnCopyAction = null;
    protected DbGridColumnsPaste columnPasteAction = null;
    protected DbGridColumnsDelete columnDeleteAction = null;
    protected DbGridColumnControlChangeAction columnControlSelectAction = null;
    protected DbGridColumnsTreeTransferHandler columnsTreeTransferHandler = null;
    protected ScriptFunctionsComboBoxModel comboLinkFunctionModel = new ScriptFunctionsComboBoxModel();
    protected ScriptFunctionsComboBoxModel comboSelectFunctionModel = new ScriptFunctionsComboBoxModel();
    protected ScriptFunctionsComboBoxModel comboCellFunctionModel = new ScriptFunctionsComboBoxModel();
    protected ScriptFunctionsComboBoxModel comboGeneralRowsFunctionModel = new ScriptFunctionsComboBoxModel();
    protected RowsColumnsDesignInfoChangeListener rowsDesignChangeListener = new RowsColumnsDesignInfoChangeListener();
    protected TreeDesignChangeListener treeDesignChangeListener = new TreeDesignChangeListener();
    protected SelectedDbGridColumnChangeListener selectedColumnDesignChangeListener = new SelectedDbGridColumnChangeListener();
    protected SelectedDbGridColumnCellChangeListener selectedColumnCellDesignChangeListener = new SelectedDbGridColumnCellChangeListener();
    protected DbGridColumn selectedColumn = null;
    protected DbGridColumnsTreeModel columnsTreeModel;

    protected final InputMap fillInputMap(InputMap im, ActionMap am) {
        Object[] keys = am.keys();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                Action action = am.get(keys[i]);
                if (action != null) {
                    Object stroke = action.getValue(Action.ACCELERATOR_KEY);
                    if (stroke != null && stroke instanceof KeyStroke) {
                        im.put((KeyStroke) stroke, keys[i]);
                    }
                    if (action == columnCutAction) {
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.SHIFT_DOWN_MASK), keys[i]);
                    }
                    if (action == columnCopyAction) {
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_DOWN_MASK), keys[i]);
                    }
                    if (action == columnPasteAction) {
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.SHIFT_DOWN_MASK), keys[i]);
                    }
                }
            }
        }
        return im;
    }

    protected class RowsColumnsDesignInfoChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            try {
                switch (evt.getPropertyName()) {
                    case DbGridRowsColumnsDesignInfo.ROWSDATASOURCE:
                        updateRowsDatasourceView();
                        break;
                    case DbGridRowsColumnsDesignInfo.FIXEDCOLUMNS:
                        updateFixedColsView();
                        break;
                    case DbGridRowsColumnsDesignInfo.FIXEDROWS:
                        updateFixedRowsView();
                        break;
                    case DbGridRowsColumnsDesignInfo.ROWSHEADERTYPE:
                        updateHeaderTypeView();
                        break;
                    case DbGridRowsColumnsDesignInfo.GENERALROWFUNCTION:
                        updateGeneralRowFunction();
                        break;
                }
                checkActionMap();
            } catch (Exception ex) {
                Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, "While rowsColumnsDesignInfo changing", ex);
            }
        }
    }

    protected class TreeDesignChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            try {
                switch (evt.getPropertyName()) {
                    case DbGridTreeDesignInfo.TREEKIND:
                        updateTreeGridKindView();
                        break;
                    case DbGridTreeDesignInfo.PARAM2GETCHILDREN:
                        updateTreeGridParam2GetChildrenView();
                        break;
                    case DbGridTreeDesignInfo.PARAMSOURCEFIELD:
                        updateTreeGridParamSourceFieldView();
                        break;
                    case DbGridTreeDesignInfo.UNARYLINKFIELD:
                        updateTreeGridUnaryLinkView();
                        break;
                    case DbGridTreeDesignInfo.PARAMETERSSETUPSCRIPT2GETCHILDREN:
                        updateTreeGridLinkFunctionView();
                        break;
                }
                checkActionMap();
            } catch (Exception ex) {
                Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, "While treeDesignInfo changing", ex);
            }
        }
    }

    protected class SelectedDbGridColumnUpdater implements TreeSelectionListener {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            try {
                if (selectedColumn != null) {
                    selectedColumn.removePropertyChangeListener(selectedColumnDesignChangeListener);
                    if (selectedColumn.getCellDesignInfo() != null) {
                        selectedColumn.getCellDesignInfo().removePropertyChangeListener(selectedColumnCellDesignChangeListener);
                    }
                }
                selectedColumn = DbGridColumnsStructureSnapshotAction.getSingleSelectedColumn(treeColumns);
                if (selectedColumn != null) {
                    selectedColumn.addPropertyChangeListener(selectedColumnDesignChangeListener);
                    checkColumnsInTreeListener();
                    if (selectedColumn.getCellDesignInfo() != null) {
                        selectedColumn.getCellDesignInfo().addPropertyChangeListener(selectedColumnCellDesignChangeListener);
                    }
                }
                updateColumnView(selectedColumn);
                enableColumnControlsBySelection();
                checkActionMap();
            } catch (Exception ex) {
                Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, "While grid columns selection changing", ex);
            }
        }

        public void checkColumnsInTreeListener() {
            PropertyChangeListener[] columnsInTreeListeners = selectedColumn.getPropertyChangeSupport().getPropertyChangeListeners();
            boolean present = false;
            for (PropertyChangeListener l : columnsInTreeListeners) {
                if (l instanceof DbGridColumnChangeListener) {
                    present = true;
                    break;
                }
            }
            if (!present) {
                selectedColumn.addPropertyChangeListener(new DbGridColumnChangeListener());
            }
        }
    }

    protected class SelectedDbGridColumnCellChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            assert evt.getSource() == selectedColumn.getCellDesignInfo();
            try {
                switch (evt.getPropertyName()) {
                    case DbGridCellDesignInfo.ROWSKEYFIELD:
                        updateColumnRowsKeyFieldView(selectedColumn);
                        break;
                    case DbGridCellDesignInfo.COLUMNSKEYFIELD:
                        updateColumnColumnsKeyFieldView(selectedColumn);
                        break;
                    case DbGridCellDesignInfo.CELLVALUEFIELD:
                        updateColumnCellValueFieldView(selectedColumn);
                        break;
                    case DbGridCellDesignInfo.CELLCONTROLINFO:
                        updateColumnCellControlView(selectedColumn);
                        break;
                }
                checkActionMap();
            } catch (Exception ex) {
                Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, "While selected grid column changing", ex);
            }
        }
    }

    protected class SelectedDbGridColumnChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            assert evt.getSource() == selectedColumn;
            try {
                switch (evt.getPropertyName()) {
                    case DbGridColumn.NAME:
                        updateColumnNameView(selectedColumn);
                        break;
                    case DbGridColumn.VISIBLE:
                        updateColumnVisibleView(selectedColumn);
                        break;
                    case DbGridColumn.READONLY:
                        updateColumnReadOnlyView(selectedColumn);
                        break;
                    case DbGridColumn.ENABLED:
                        updateColumnEnabledView(selectedColumn);
                        break;
                    case DbGridColumn.WIDTH:
                        updateColumnWidthView(selectedColumn);
                        break;
                    case DbGridColumn.STYLE:
                    case DbGridColumn.HEADERFONT:
                        updateColumnFontView(selectedColumn);
                        break;
                    case DbGridColumn.SUBSTITUTE:
                        updateColumnSubstituteView(selectedColumn);
                        break;
                    case DbGridColumn.PLAIN:
                        updateColumnFlipedView(selectedColumn);
                        break;
                    case DbGridColumn.SELECTFUNCTION:
                        updateColumnSelectFunctionView(selectedColumn);
                        break;
                    case DbGridColumn.SELECTONLY:
                        updateColumnSelectOnlyView(selectedColumn);
                        break;
                    case DbGridColumn.CELLFUNCTION:
                        updateColumnCellFunctionView(selectedColumn);
                        break;
                    case DbGridColumn.DATAMODELELEMENT:
                        updateColumnDatamodelElementView(selectedColumn);
                        break;
                    case DbGridColumn.CONTROLINFO:
                        updateColumnRowsControlView(selectedColumn);
                        break;
                    case DbGridColumn.CELLDESIGNINFO:
                        if (evt.getOldValue() != null && evt.getOldValue() instanceof DbGridCellDesignInfo) {
                            ((DbGridCellDesignInfo) evt.getOldValue()).removePropertyChangeListener(selectedColumnCellDesignChangeListener);
                        }
                        updateColumnRowsKeyFieldView(selectedColumn);
                        updateColumnColumnsKeyFieldView(selectedColumn);
                        updateColumnCellValueFieldView(selectedColumn);
                        updateColumnCellControlView(selectedColumn);
                        if (evt.getNewValue() != null && evt.getNewValue() instanceof DbGridCellDesignInfo) {
                            ((DbGridCellDesignInfo) evt.getNewValue()).addPropertyChangeListener(selectedColumnCellDesignChangeListener);
                        }
                        break;
                    case DbGridColumn.COLUMNSDATASOURCE:
                        updateColumnColumnsDatasourceView(selectedColumn);
                        break;
                    case DbGridColumn.CELLSDATASOURCE:
                        updateColumnCellsDatasourceView(selectedColumn);
                        break;
                    case DbGridColumn.COLUMNSDISPLAYFIELD:
                        updateColumnColumnsDisplayFieldView(selectedColumn);
                        break;
                }
                checkActionMap();
            } catch (Exception ex) {
                Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, "While selected grid column changing", ex);
            }
        }
    }

    protected class DbGridColumnChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            try {
                if (evt.getSource() instanceof DbGridColumn) {
                    columnsTreeModel.fireColumnChanged((DbGridColumn) evt.getSource());
                }
            } catch (Exception ex) {
                Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, "While selected grid column changing", ex);
            }
        }
    }

    @Override
    protected void designInfoPropertyChanged(String aPropertyName, Object oldValue, Object newValue) {
        try {
            switch (aPropertyName) {
                case DbGridDesignInfo.ROWSCOLUMNSDESIGNINFO:
                    assert oldValue == null || oldValue instanceof DbGridRowsColumnsDesignInfo;
                    assert newValue == null || newValue instanceof DbGridRowsColumnsDesignInfo;
                    if (oldValue != null) {
                        ((DbGridRowsColumnsDesignInfo) oldValue).removePropertyChangeListener(rowsDesignChangeListener);
                    }
                    if (newValue != null) {
                        ((DbGridRowsColumnsDesignInfo) newValue).addPropertyChangeListener(rowsDesignChangeListener);
                    }
                    updateRowsView();
                    break;
                case DbGridDesignInfo.HEADER:
                    // update columns tree
                    updateHeaderView();
                    // update column design
                    DbGridColumn col = DbGridColumnsStructureSnapshotAction.getSingleSelectedColumn(treeColumns);
                    updateColumnView(col);
                    // enabled column-related controls
                    enableColumnControlsBySelection();
                    break;
                case DbGridDesignInfo.TREEDESIGNINFO:
                    assert oldValue == null || oldValue instanceof DbGridTreeDesignInfo;
                    assert newValue == null || newValue instanceof DbGridTreeDesignInfo;
                    if (oldValue != null) {
                        ((DbGridTreeDesignInfo) oldValue).removePropertyChangeListener(treeDesignChangeListener);
                    }
                    if (newValue != null) {
                        ((DbGridTreeDesignInfo) newValue).addPropertyChangeListener(treeDesignChangeListener);
                    }
                    updateTreeGridView();
                    break;
            }
            checkActionMap();
        } catch (Exception ex) {
            Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, "While " + aPropertyName + " changing", ex);
        }
    }

    protected class DbGridRowsDatasourceSelectAction extends DbGridRowsColumnsSnapshotAction {

        public DbGridRowsDatasourceSelectAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        protected void processChangedDesignInfo(DbGridRowsColumnsDesignInfo after) {
            ModelElementRef old = after.getRowsDatasource();
            ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.DATASOURCE_SELECTION_SUBJECT, null, DbGridCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectDatasource"));
            if (newRef != null) {
                after.setRowsDatasource(newRef);
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    protected class DbGridRowsDatasourceClearAction extends DbGridRowsColumnsSnapshotClearAction {

        public DbGridRowsDatasourceClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridRowsColumnsDesignInfo info = getInfo();
            return (info != null && info.getRowsDatasource() != null && info.getRowsDatasource().getEntityId() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridRowsColumnsDesignInfo after) {
            if (after != null) {
                after.setRowsDatasource(null);
            }
        }
    }

    protected class DbGridGeneralCellScriptChangeAction extends DbGridRowsColumnsSnapshotAction {

        public DbGridGeneralCellScriptChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        protected void processChangedDesignInfo(DbGridRowsColumnsDesignInfo after) {
            String script = (String) comboGeneralCellFunction.getSelectedItem();
            if (script == null || script.isEmpty()) {
                ComboBoxEditor cbe = comboGeneralCellFunction.getEditor();
                if (cbe != null && cbe.getEditorComponent() != null) {
                    Component ec = cbe.getEditorComponent();
                    if (ec instanceof JTextComponent) {
                        JTextComponent tc = (JTextComponent) ec;
                        script = tc.getText();
                    }
                }
            }
            after.setGeneralRowFunction(script);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    protected class DbGridGeneralCellScriptClearAction extends DbGridRowsColumnsSnapshotClearAction {

        public DbGridGeneralCellScriptClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridRowsColumnsDesignInfo info = getInfo();
            return (info != null && info.getGeneralRowFunction() != null && !info.getGeneralRowFunction().isEmpty());
        }

        @Override
        protected void processChangedDesignInfo(DbGridRowsColumnsDesignInfo after) {
            if (after != null) {
                after.setGeneralRowFunction(null);
            }
        }
    }

    protected class DbGridColumnsDatasourceSelectAction extends DbGridColumnSnapshotAction {

        public DbGridColumnsDatasourceSelectAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn info = getInfo();
            return info != null && !info.isPlain();
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            ModelElementRef old = after.getColumnsDatasource();
            ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.DATASOURCE_SELECTION_SUBJECT, null, DbGridCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectDatasource"));
            if (newRef != null) {
                after.setColumnsDatasource(newRef);
            }
        }
    }

    protected class DbGridColumnsDatasourceClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridColumnsDatasourceClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn info = getInfo();
            return (info != null && !getInfo().isPlain() && info.getColumnsDatasource() != null && info.getColumnsDatasource().getEntityId() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setColumnsDatasource(null);
            }
        }
    }

    protected class DbGridColumnsDisplayFieldSelectAction extends DbGridColumnSnapshotAction {

        public DbGridColumnsDisplayFieldSelectAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn info = getInfo();
            return info != null && !info.isPlain();
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            ModelElementRef old = after.getColumnsDisplayField();
            ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, null, DbGridCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
            if (newRef != null) {
                after.setColumnsDisplayField(newRef);
                if (after.getColumnsDatasource() == null || after.getColumnsDatasource().getEntityId() == null) {
                    ModelElementRef dme = new ModelElementRef();
                    dme.setEntityId(newRef.getEntityId());
                    after.setColumnsDatasource(dme);
                }
            }
        }
    }

    protected class DbGridColumnsDisplayFieldClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridColumnsDisplayFieldClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn info = getInfo();
            return (info != null && !getInfo().isPlain() && info.getColumnsDisplayField() != null && info.getColumnsDisplayField().getEntityId() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setColumnsDisplayField(null);
            }
        }
    }

    protected class DbGridCellsDatasourceSelectAction extends DbGridColumnSnapshotAction {

        public DbGridCellsDatasourceSelectAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn info = getInfo();
            DbGridRowsColumnsDesignInfo rcInfo = getRcInfo();
            return (info != null && !getInfo().isPlain() && info.getColumnsDatasource() != null && info.getColumnsDatasource().getEntityId() != null
                    && rcInfo != null && rcInfo.getRowsDatasource() != null && rcInfo.getRowsDatasource().getEntityId() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            ModelElementRef old = after.getCellsDatasource();
            ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.DATASOURCE_SELECTION_SUBJECT, null, DbGridCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectDatasource"));
            if (newRef != null) {
                after.setCellsDatasource(newRef);
            }
        }
    }

    protected class DbGridCellsDatasourceClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridCellsDatasourceClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn info = getInfo();
            DbGridRowsColumnsDesignInfo rcInfo = getRcInfo();
            return (info != null && !getInfo().isPlain() && info.getColumnsDatasource() != null && info.getColumnsDatasource().getEntityId() != null
                    && rcInfo != null && rcInfo.getRowsDatasource() != null && rcInfo.getRowsDatasource().getEntityId() != null
                    && info.getCellsDatasource() != null && info.getCellsDatasource().getEntityId() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setCellsDatasource(null);
            }
        }
    }

    protected class DbGridFixedRowsChangeAction extends DbGridRowsColumnsSnapshotAction {

        public DbGridFixedRowsChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        protected void processChangedDesignInfo(DbGridRowsColumnsDesignInfo after) {
            if (after != null) {
                Object oValue = spinFixedRows.getValue();
                if (oValue != null && oValue instanceof Integer) {
                    int fixedRows = (Integer) oValue;
                    after.setFixedRows(fixedRows);
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return designInfo instanceof DbGridDesignInfo && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo() != null && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo().getRowsDatasource() != null;
        }
    }

    protected class DbGridFixedColumnsChangeAction extends DbGridRowsColumnsSnapshotAction {

        public DbGridFixedColumnsChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        protected void processChangedDesignInfo(DbGridRowsColumnsDesignInfo after) {
            if (after != null) {
                Object oValue = spinFixedCols.getValue();
                if (oValue != null && oValue instanceof Integer) {
                    int fixedCols = (Integer) oValue;
                    after.setFixedColumns(fixedCols);
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return designInfo instanceof DbGridDesignInfo && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo() != null && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo().getRowsDatasource() != null;
        }
    }

    protected class DbGridRowsHeaderTypeChangeAction extends DbGridRowsColumnsSnapshotAction {

        public DbGridRowsHeaderTypeChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        protected void processChangedDesignInfo(DbGridRowsColumnsDesignInfo after) {
            if (after != null) {
                Object oValue = comboRowHeaderType.getSelectedItem();
                if (oValue != null && oValue instanceof Integer) {
                    int headerType = (Integer) oValue;
                    after.setRowsHeaderType(headerType);
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    protected class DbGridRowKeySelectAction extends DbGridColumnSnapshotAction {

        public DbGridRowKeySelectAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn col = getInfo();
            return (cellDsSelectAction.isEnabled() && designInfo != null && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo() != null
                    && col != null && !getInfo().isPlain() && col.getCellsDatasource() != null && col.getCellsDatasource().getEntityId() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null && after.getCellDesignInfo() != null) {
                ModelElementRef old = after.getCellDesignInfo().getRowsKeyField();
                ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, null, DbGridCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
                if (newRef != null) {
                    after.getCellDesignInfo().setRowsKeyField(newRef);
                }
            }
        }
    }

    protected class DbGridRowKeyClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridRowKeyClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn col = getInfo();
            return (cellDsSelectAction.isEnabled() && designInfo != null && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo() != null
                    && col != null && !getInfo().isPlain() && col.getCellsDatasource() != null && col.getCellsDatasource().getEntityId() != null
                    && col.getCellDesignInfo() != null && col.getCellDesignInfo().getRowsKeyField() != null
                    && col.getCellDesignInfo().getRowsKeyField().getField() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null && after.getCellDesignInfo() != null) {
                after.getCellDesignInfo().setRowsKeyField(null);
            }
        }
    }

    protected class DbGridColKeySelectAction extends DbGridColumnSnapshotAction {

        public DbGridColKeySelectAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn col = getInfo();
            return (cellDsSelectAction.isEnabled() && designInfo != null && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo() != null
                    && col != null && !getInfo().isPlain() && col.getCellsDatasource() != null && col.getCellsDatasource().getEntityId() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null && after.getCellDesignInfo() != null) {
                ModelElementRef old = after.getCellDesignInfo().getColumnsKeyField();
                ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, null, DbGridCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
                if (newRef != null) {
                    after.getCellDesignInfo().setColumnsKeyField(newRef);
                }
            }
        }
    }

    protected class DbGridColKeyClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridColKeyClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn col = getInfo();
            return (cellDsSelectAction.isEnabled() && designInfo != null && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo() != null
                    && col != null && !getInfo().isPlain() && col.getCellsDatasource() != null && col.getCellsDatasource().getEntityId() != null
                    && col.getCellDesignInfo() != null && col.getCellDesignInfo().getColumnsKeyField() != null
                    && col.getCellDesignInfo().getColumnsKeyField().getField() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null && after.getCellDesignInfo() != null) {
                after.getCellDesignInfo().setColumnsKeyField(null);
            }
        }
    }

    protected class DbGridCellsValueFieldSelectAction extends DbGridColumnSnapshotAction {

        public DbGridCellsValueFieldSelectAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn col = getInfo();
            return (cellDsSelectAction.isEnabled() && designInfo != null && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo() != null
                    && col != null && !getInfo().isPlain() && col.getCellsDatasource() != null && col.getCellsDatasource().getEntityId() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null && after.getCellDesignInfo() != null) {
                ModelElementRef old = after.getCellDesignInfo().getCellValueField();
                ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, null, DbGridCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
                if (newRef != null) {
                    after.getCellDesignInfo().setCellValueField(newRef);
                    if (after.getCellDesignInfo().getCellControlInfo() == null || !isCompatibleWithControl(after.getCellDesignInfo(), newRef)) {
                        DbControlDesignInfo info = getDefaultDesignInfo4field(newRef);
                        if (info != null) {
                            after.getCellDesignInfo().setCellControlInfo(info);
                        }
                    }
                }
                DbControlDesignInfo cinfo = after.getCellDesignInfo().getCellControlInfo();
                if (cinfo != null) {
                    cinfo.setDatamodelElement(newRef);
                }
            }
        }

        private boolean isCompatibleWithControl(DbGridCellDesignInfo aInfo, ModelElementRef newRef) {
            if (aInfo != null && newRef != null && aInfo.getCellControlInfo() != null
                    && newRef.getField() != null) {
                DbControlClassFinder clsFinder = new DbControlClassFinder();
                aInfo.getCellControlInfo().accept(clsFinder);
                if (clsFinder.getResult() != null) {
                    return DbControlsUtils.isControlCompatible2Type(clsFinder.getResult(), newRef.getField().getTypeInfo().getSqlType());
                }
            }
            return false;
        }
    }

    protected class DbGridCellsValueFieldClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridCellsValueFieldClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn col = getInfo();
            DbGridCellDesignInfo info = col != null ? col.getCellDesignInfo() : null;
            return (cellDsSelectAction.isEnabled() && designInfo != null && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo() != null
                    && col != null && !getInfo().isPlain() && col.getCellsDatasource() != null && col.getCellsDatasource().getEntityId() != null
                    && info != null && info.getCellValueField() != null && info.getCellValueField().getField() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null && after.getCellDesignInfo() != null) {
                after.getCellDesignInfo().setCellValueField(null);
                DbControlDesignInfo cinfo = after.getCellDesignInfo().getCellControlInfo();
                if (cinfo != null) {
                    cinfo.setDatamodelElement(null);
                }
                after.getCellDesignInfo().setCellControlInfo(null);
            }
        }
    }

    protected class DbGridCellControlChangeAction extends DbGridColumnSnapshotAction {

        public DbGridCellControlChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn col = getInfo();
            DbGridCellDesignInfo info = col != null ? col.getCellDesignInfo() : null;
            return (info != null && !getInfo().isPlain() && cellDsSelectAction.isEnabled() && info.getCellValueField() != null && info.getCellValueField().getField() != null
                    && designInfo != null && ((DbGridDesignInfo) designInfo).getRowsColumnsDesignInfo() != null
                    && col.getCellsDatasource() != null && col.getCellsDatasource().getEntityId() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null && after.getCellDesignInfo() != null) {
                Object oSelected = comboCellControl.getSelectedItem();
                if (oSelected != null && oSelected instanceof Class<?>) {
                    Class<?> lControlClass = (Class<?>) oSelected;
                    Class<?> infoClass = DbControlsUtils.getDesignInfoClass(lControlClass);
                    if (infoClass != null) {
                        try {
                            DbControlDesignInfo info = (DbControlDesignInfo) infoClass.newInstance();
                            after.getCellDesignInfo().setCellControlInfo(info);
                        } catch (InstantiationException | IllegalAccessException ex) {
                            Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    protected class DbGridCellControlClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridCellControlClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridColumn info = getInfo();
            return (info != null && !getInfo().isPlain() && info.getCellDesignInfo() != null && cellDsSelectAction.isEnabled()
                    && info.getCellDesignInfo().getCellValueField() != null && info.getCellDesignInfo().getCellValueField().getField() != null
                    && info.getCellsDatasource() != null && info.getCellsDatasource().getEntityId() != null);
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null && after.getCellDesignInfo() != null) {
                ModelElementRef dmElement = after.getCellDesignInfo().getCellValueField();
                if (dmElement != null) {
                    DbControlDesignInfo info = getDefaultDesignInfo4field(dmElement);
                    if (info != null) {
                        after.getCellDesignInfo().setCellControlInfo(info);
                    }
                }
            }
        }
    }

    protected class DatasourceValidator implements ModelElementValidator {

        protected Long defEntityId = null;

        public DatasourceValidator(Long aDefEntityId) {
            super();
            defEntityId = aDefEntityId;
        }

        @Override
        public boolean validateDatamodelElementSelection(ModelElementRef aDmElement) {
            return (aDmElement != null && aDmElement.getEntityId() != null && aDmElement.isField()
                    && (defEntityId == null || defEntityId.equals(aDmElement.getEntityId())));
        }
    }

    protected class DbGridTreeUnaryLinkSelectAction extends DbGridTreeSnapshotAction {

        public DbGridTreeUnaryLinkSelectAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return isDataValid();
        }

        public boolean isDataValid() {
            if (designInfo instanceof DbGridDesignInfo) {
                DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
                if (info.getRowsColumnsDesignInfo() != null
                        && info.getRowsColumnsDesignInfo().getRowsDatasource() != null
                        && info.getRowsColumnsDesignInfo().getRowsDatasource().getEntityId() != null) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void processChangedDesignInfo(DbGridTreeDesignInfo after) {
            if (after != null && designInfo != null && designInfo instanceof DbGridDesignInfo) {
                DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
                if (info.getRowsColumnsDesignInfo() != null && info.getRowsColumnsDesignInfo().getRowsDatasource() != null
                        && info.getRowsColumnsDesignInfo().getRowsDatasource().getEntityId() != null) {
                    Long entityId = info.getRowsColumnsDesignInfo().getRowsDatasource().getEntityId();
                    ModelElementRef old = after.getUnaryLinkField();
                    ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, new DatasourceValidator(entityId), DbGridCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
                    if (newRef != null) {
                        after.setUnaryLinkField(newRef);
                    }
                }
            }
        }
    }

    protected class DbGridTreeUnaryLinkClearAction extends DbGridTreeSnapshotClearAction {

        public DbGridTreeUnaryLinkClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            if (designInfo instanceof DbGridDesignInfo) {
                DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
                if (info.getRowsColumnsDesignInfo() != null && info.getRowsColumnsDesignInfo().getRowsDatasource() != null
                        && info.getRowsColumnsDesignInfo().getRowsDatasource().getEntityId() != null
                        && info.getTreeDesignInfo() != null && info.getTreeDesignInfo().getUnaryLinkField() != null
                        && info.getTreeDesignInfo().getUnaryLinkField().getField() != null) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void processChangedDesignInfo(DbGridTreeDesignInfo after) {
            if (after != null) {
                after.setUnaryLinkField(null);
            }
        }
    }

    protected class DbGridTreeParamSourceSelectAction extends DbGridTreeSnapshotAction {

        public DbGridTreeParamSourceSelectAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && isAddQueriesTree();
        }

        @Override
        protected void processChangedDesignInfo(DbGridTreeDesignInfo after) {
            if (after != null) {
                ModelElementRef old = after.getParamSourceField();
                ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, null, DbGridCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
                if (newRef != null) {
                    after.setParamSourceField(newRef);
                }
            }
        }
    }

    protected class DbGridTreeParamSourceClearAction extends DbGridTreeSnapshotClearAction {

        public DbGridTreeParamSourceClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridTreeDesignInfo info = getInfo();
            return unaryLinkFieldSelectAction.isDataValid() && isAddQueriesTree() && info != null && info.getParamSourceField() != null && info.getParamSourceField().getField() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridTreeDesignInfo after) {
            if (after != null) {
                after.setParamSourceField(null);
            }
        }
    }

    protected class DbGridTreeParamChangeAction extends DbGridTreeSnapshotAction {

        public DbGridTreeParamChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && isAddQueriesTree();
        }

        @Override
        protected void processChangedDesignInfo(DbGridTreeDesignInfo after) {
            if (after != null) {
                if (designInfo instanceof DbGridDesignInfo) {
                    DbGridDesignInfo grdDi = (DbGridDesignInfo) designInfo;
                    DbGridRowsColumnsDesignInfo rowsInfo = grdDi.getRowsColumnsDesignInfo();
                    if (rowsInfo != null && rowsInfo.getRowsDatasource() != null) {
                        Object oSelected = comboDsParam.getSelectedItem();
                        if (oSelected != null && oSelected instanceof Parameter) {
                            ModelElementRef dmElement = new ModelElementRef();
                            dmElement.entityId = rowsInfo.getRowsDatasource().getEntityId();
                            dmElement.setField(false);
                            dmElement.setField((Parameter) oSelected);
                            after.setParam2GetChildren(dmElement);
                        }
                    }
                }
            }
        }
    }

    protected class DbGridTreeParamClearAction extends DbGridTreeSnapshotClearAction {

        public DbGridTreeParamClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridTreeDesignInfo info = getInfo();
            return unaryLinkFieldSelectAction.isDataValid() && isAddQueriesTree() && info != null && info.getParam2GetChildren() != null && info.getParam2GetChildren().getField() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridTreeDesignInfo after) {
            if (after != null) {
                after.setParam2GetChildren(null);
            }
        }
    }

    protected class DbGridTreeScriptChangeAction extends DbGridTreeSnapshotAction {

        public DbGridTreeScriptChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && isScriptTree();
        }

        @Override
        protected void processChangedDesignInfo(DbGridTreeDesignInfo after) {
            if (after != null) {
                String script = null;
                ComboBoxEditor cbe = comboLinkFunction.getEditor();
                if (cbe != null && cbe.getEditorComponent() != null) {
                    Component ec = cbe.getEditorComponent();
                    if (ec instanceof JTextComponent) {
                        JTextComponent tc = (JTextComponent) ec;
                        script = tc.getText();
                    }
                }
                after.setParametersSetupScript2GetChildren(script);
            }
        }
    }

    protected class DbGridTreeScriptClearAction extends DbGridTreeSnapshotClearAction {

        public DbGridTreeScriptClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            DbGridTreeDesignInfo info = getInfo();
            return unaryLinkFieldSelectAction.isDataValid() && isScriptTree() && info != null && info.getParametersSetupScript2GetChildren() != null && !info.getParametersSetupScript2GetChildren().isEmpty();
        }

        @Override
        protected void processChangedDesignInfo(DbGridTreeDesignInfo after) {
            if (after != null) {
                after.setParametersSetupScript2GetChildren(null);
            }
        }
    }

    protected abstract class DbGridTreeKindChangeAction extends DbGridTreeSnapshotClearAction {

        public DbGridTreeKindChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid();
        }

        @Override
        protected void processChangedDesignInfo(DbGridTreeDesignInfo after) {
            if (after != null) {
                after.setTreeKind(getTreeKind());
            }
        }

        protected abstract int getTreeKind();
    }

    protected class DbGridTreeKind1QueryAction extends DbGridTreeKindChangeAction {

        @Override
        protected int getTreeKind() {
            return DbGridTreeDesignInfo.ONE_FIELD_ONE_QUERY_TREE_KIND;
        }
    }

    protected class DbGridTreeKindAddQueriesAction extends DbGridTreeKindChangeAction {

        @Override
        protected int getTreeKind() {
            return DbGridTreeDesignInfo.FIELD_2_PARAMETER_TREE_KIND;
        }
    }

    protected class DbGridTreeKindScriptAction extends DbGridTreeKindChangeAction {

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        protected int getTreeKind() {
            return DbGridTreeDesignInfo.SCRIPT_PARAMETERS_TREE_KIND;
        }
    }

    protected class DbGridColumnsAdd extends DbGridColumnsStructureSnapshotAction {

        public DbGridColumnsAdd() {
            super(DbGridCustomizer.this);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
        }

        @Override
        public boolean isEnabled() {
            /*If the customizer in embedded, than this action is disabled*/
            return !isEmbedded() && unaryLinkFieldSelectAction.isDataValid();
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            DbGridColumn col = getSingleSelectedColumn();
            TreeModel oModel = treeColumns.getModel();
            if (oModel != null && oModel instanceof DbGridColumnsTreeModel) {
                DbGridColumnsTreeModel model = (DbGridColumnsTreeModel) oModel;
                DbGridColumn parent = null;
                int idx = -1;
                if (col == null) {
                    parent = model.getDummyRoot();
                    if (parent.getChildren() == null || parent.getChildren().isEmpty()) {
                        idx = -1;
                    } else {
                        idx = parent.getChildren().size() - 1;
                    }
                } else {
                    parent = col.getParent();
                    if (parent == null) {
                        parent = model.getDummyRoot();
                    }
                    idx = model.getIndexOfChild(parent, col);
                }
                if (model != null && parent != null) {
                    if (idx >= -1 && idx < model.getChildCount(parent)) {
                        List<DbGridColumn> children = parent.getChildren();
                        if (children != null) {
                            final DbGridColumn newcol = new DbGridColumn(parent);
                            children.add(idx + 1, newcol);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    setSelectedColumn(newcol);
                                }
                            });
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    protected class DbGridColumnsAddInNode extends DbGridColumnsStructureSnapshotAction {

        public DbGridColumnsAddInNode() {
            super(DbGridCustomizer.this);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.ALT_DOWN_MASK));
        }

        @Override
        public boolean isEnabled() {
            /*If the customizer in embedded, than this action is disabled*/
            return !isEmbedded() && unaryLinkFieldSelectAction.isDataValid() && isSingleSelectedNode();
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            DbGridColumn parent = getSingleSelectedColumn();
            TreeModel oModel = treeColumns.getModel();
            if (parent != null && oModel != null && oModel instanceof DbGridColumnsTreeModel) {
                DbGridColumnsTreeModel model = (DbGridColumnsTreeModel) oModel;
                if (model != null && parent != null) {
                    List<DbGridColumn> children = parent.getChildren();
                    if (children != null) {
                        final DbGridColumn newcol = new DbGridColumn(parent);
                        children.add(newcol);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                setSelectedColumn(newcol);
                            }
                        });
                        return true;
                    }
                }
            }
            return false;
        }
    }

    protected class DbGridColumnsDelete extends DbGridColumnsStructureSnapshotAction {

        public DbGridColumnsDelete() {
            super(DbGridCustomizer.this);
            putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/delete.png"));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        }

        @Override
        public boolean isEnabled() {
            /*If the customizer in embedded, than this action is disabled*/
            return !isEmbedded() && unaryLinkFieldSelectAction.isDataValid() && isSingleSelectedNode();
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            DbGridColumn col = getSingleSelectedColumn();
            if (col != null) {
                List<DbGridColumn> columns = getInfo();
                DbGridColumn parent = col.getParent();
                if (parent != null) {
                    columns = parent.getChildren();
                }
                if (columns != null && !columns.isEmpty()) {
                    int idx = columns.indexOf(col);
                    if (idx >= 0 && idx < columns.size()) {
                        int idx2Select = -1;
                        if (idx >= 0 && idx < columns.size() - 1) {
                            idx2Select = idx;
                        } else if (idx > 0 && idx == columns.size() - 1) {
                            idx2Select = idx - 1;
                        }
                        columns.remove(idx);
                        col.setParent(null);
                        if (idx2Select >= 0 && idx2Select < columns.size()) {
                            final DbGridColumn col2Select = columns.get(idx2Select);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (col2Select != null) {
                                        setSelectedColumn(col2Select);
                                    }
                                }
                            });
                        }
                        return true;
                    }
                }
            }
            return false;
        }
    }

    protected class DbGridColumnsFillAction extends DbGridColumnsStructureSnapshotAction {

        public DbGridColumnsFillAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            /*If the customizer in embedded, than this action is disabled*/
            return !isEmbedded() && unaryLinkFieldSelectAction.isDataValid();
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            if (designInfo instanceof DbGridDesignInfo) {
                DbGridDesignInfo di = (DbGridDesignInfo) designInfo;
                if (di != null) {
                    List<DbGridColumn> colVector = getInfo();
                    if (colVector != null && di.getRowsColumnsDesignInfo() != null && di.getRowsColumnsDesignInfo().getRowsDatasource() != null
                            && di.getRowsColumnsDesignInfo().getRowsDatasource().getEntityId() != null) {
                        Long rowsEntityId = di.getRowsColumnsDesignInfo().getRowsDatasource().getEntityId();
                        Entity rowsEntity = getDatamodel().getEntityById(rowsEntityId);
                        if (rowsEntity != null && rowsEntity.getFields() != null) {
                            Fields fields = rowsEntity.getFields();
                            int rowsetColumnsCount = fields.getFieldsCount();
                            for (int i = 1; i <= rowsetColumnsCount; i++) {
                                DbGridColumn column = new DbGridColumn();

                                Field columnMtd = fields.get(i);
                                column.setName(columnMtd.getName());
                                int lwidth = 50;
                                if (lwidth >= column.getWidth()) {
                                    column.setWidth(lwidth);
                                }
                                if (columnMtd != null) {
                                    String description = columnMtd.getDescription();
                                    if (description != null && !description.isEmpty()) {
                                        column.setTitle(description);
                                    }
                                }
                                ModelElementRef fieldRef = new ModelElementRef();
                                fieldRef.setEntityId(rowsEntity.getEntityID());
                                fieldRef.setFieldName(columnMtd.getName());
                                column.setDatamodelElement(fieldRef);
                                Class<?>[] compatibleControlsClasses = DbControlsUtils.getCompatibleControls(columnMtd.getTypeInfo().getSqlType());
                                if (compatibleControlsClasses != null && compatibleControlsClasses.length > 0) {
                                    Class<?> lControlClass = compatibleControlsClasses[0];
                                    if (lControlClass != null) {
                                        Class<?> infoClass = DbControlsUtils.getDesignInfoClass(lControlClass);
                                        if (infoClass != null) {
                                            try {
                                                DbControlDesignInfo cdi = (DbControlDesignInfo) infoClass.newInstance();
                                                cdi.setDatamodelElement(fieldRef);
                                                column.setControlInfo((DbControlDesignInfo) cdi);
                                                colVector.add(column);
                                                if (cdi instanceof DbDateDesignInfo) {
                                                    DbDateDesignInfo dateDi = (DbDateDesignInfo) cdi;
                                                    dateDi.setDateFormat(DbDate.DD_MM_YYYY_HH_MM_SS);
                                                }
                                            } catch (InstantiationException | IllegalAccessException ex) {
                                                Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                }
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    protected class DbGridColumnsCut extends DbGridColumnsStructureSnapshotAction {

        public DbGridColumnsCut() {
            super(DbGridCustomizer.this);
            putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/cut.png"));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public boolean isEnabled() {
            /*If the customizer in embedded, than this action is disabled*/
            return !isEmbedded() && columnDeleteAction.isEnabled();
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            columnCopyAction.changeStructure(aEdit);
            return columnDeleteAction.changeStructure(aEdit);
        }
    }

    protected class DbGridColumnsCopy extends DbGridColumnsStructureSnapshotAction {

        public DbGridColumnsCopy() {
            super(DbGridCustomizer.this);
            putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/copy.png"));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && isSingleSelectedNode();
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            if (isSingleSelectedNode()) {
                DbGridColumn col = getSingleSelectedColumn();
                Document doc = Object2Dom.transform(col, "column");
                if (doc != null) {
                    String serialized = XmlDom2String.transform(doc);
                    string2SystemClipboard(serialized);
                }
            }
            return false; // No any edit with copy operation!
        }

        private void string2SystemClipboard(String sEntity) throws HeadlessException {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (sEntity != null && clipboard != null) {
                StringSelection ss = new StringSelection(sEntity);
                clipboard.setContents(ss, ss);
            }
        }
    }

    protected class DbGridColumnsPaste extends DbGridColumnsStructureSnapshotAction {

        public DbGridColumnsPaste() {
            super(DbGridCustomizer.this);
            putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/paste.png"));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public boolean isEnabled() {
            /*If the customizer in embedded, than this action is disabled*/
            return !isEmbedded() && unaryLinkFieldSelectAction.isDataValid() && isStringInClipboard();
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            String ldata = stringFromClipboard();
            if (ldata != null && !ldata.isEmpty()) {
                Document doc = Source2XmlDom.transform(ldata);
                if (doc != null) {
                    final DbGridColumn newcol = new DbGridColumn();
                    Object2Dom.transform(newcol, doc);

                    DbGridColumn col = getSingleSelectedColumn();
                    TreeModel oModel = treeColumns.getModel();
                    if (oModel != null && oModel instanceof DbGridColumnsTreeModel) {
                        DbGridColumnsTreeModel model = (DbGridColumnsTreeModel) oModel;
                        DbGridColumn parent = null;
                        int idx = -1;
                        if (col == null) {
                            parent = model.getDummyRoot();
                            if (parent.getChildren() == null || parent.getChildren().isEmpty()) {
                                idx = -1;
                            } else {
                                idx = parent.getChildren().size() - 1;
                            }
                        } else {
                            parent = col.getParent();
                            idx = model.getIndexOfChild(parent, col);
                        }
                        if (model != null && parent != null) {
                            if (idx >= -1 && idx < model.getChildCount(parent)) {
                                List<DbGridColumn> children = parent.getChildren();
                                if (children != null) {
                                    newcol.setParent(parent);
                                    children.add(idx + 1, newcol);
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setSelectedColumn(newcol);
                                        }
                                    });
                                    return true;
                                }
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(splitColumns, DbControlsDesignUtils.getLocalizedString("badClipboardData"), DbControlsDesignUtils.getLocalizedString("columns"), JOptionPane.ERROR_MESSAGE);
                }
            }
            return false;
        }

        protected boolean isStringInClipboard() {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard != null) {
                try {
                    Transferable tr = clipboard.getContents(null);
                    if (tr != null) {
                        return tr.isDataFlavorSupported(DataFlavor.stringFlavor);
                    }
                } catch (IllegalStateException ex) {
                    return false;
                }
            }
            return false;
        }

        protected String stringFromClipboard() {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard != null) {
                Transferable tr = clipboard.getContents(null);
                if (tr != null) {
                    try {
                        if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                            Object oData = tr.getTransferData(DataFlavor.stringFlavor);
                            if (oData != null && oData instanceof String) {
                                return (String) oData;
                            }
                        }
                    } catch (UnsupportedFlavorException | IOException ex) {
                        Logger.getLogger(DbGridColumnsPaste.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return null;
        }
    }

    protected class DbGridColumnsMoveUp extends DbGridColumnsStructureSnapshotAction {

        public DbGridColumnsMoveUp() {
            super(DbGridCustomizer.this);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public boolean isEnabled() {
            boolean moveUpLegal = false;
            DbGridColumn col = getSingleSelectedColumn();
            if (col != null) {
                List<DbGridColumn> columns = getInfo();
                DbGridColumn parent = col.getParent();
                if (parent != null) {
                    columns = parent.getChildren();
                }
                if (columns != null && !columns.isEmpty()) {
                    int idx = columns.indexOf(col);
                    moveUpLegal = idx > 0 && idx < columns.size();
                }
            }
            /*If the customizer in embedded, than this action is disabled*/
            return !isEmbedded() && isSingleSelectedNode() && moveUpLegal;
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            final DbGridColumn col = getSingleSelectedColumn();
            if (col != null) {
                List<DbGridColumn> columns = getInfo();
                DbGridColumn parent = col.getParent();
                if (parent != null) {
                    columns = parent.getChildren();
                }
                if (columns != null && !columns.isEmpty()) {
                    int idx = columns.indexOf(col);
                    if (idx > 0 && idx < columns.size()) {
                        columns.remove(col);
                        columns.add(idx - 1, col);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                setSelectedColumn(col);
                            }
                        });
                        return true;
                    }
                }
            }
            return false;
        }
    }

    protected class DbGridColumnsMoveDown extends DbGridColumnsStructureSnapshotAction {

        public DbGridColumnsMoveDown() {
            super(DbGridCustomizer.this);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public boolean isEnabled() {
            boolean moveDownLegal = false;
            DbGridColumn col = getSingleSelectedColumn();
            if (col != null) {
                List<DbGridColumn> columns = getInfo();
                DbGridColumn parent = col.getParent();
                if (parent != null) {
                    columns = parent.getChildren();
                }
                if (columns != null && !columns.isEmpty()) {
                    int idx = columns.indexOf(col);
                    moveDownLegal = idx >= 0 && idx < columns.size() - 1;
                }
            }
            /*If the customizer in embedded, than this action is disabled*/
            return !isEmbedded() && unaryLinkFieldSelectAction.isDataValid() && isSingleSelectedNode() && moveDownLegal;
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            final DbGridColumn col = getSingleSelectedColumn();
            if (col != null) {
                List<DbGridColumn> columns = getInfo();
                DbGridColumn parent = col.getParent();
                if (parent != null) {
                    columns = parent.getChildren();
                }
                if (columns != null && !columns.isEmpty()) {
                    int idx = columns.indexOf(col);
                    if (idx >= 0 && idx < columns.size() - 1) {
                        columns.remove(col);
                        columns.add(idx + 1, col);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                setSelectedColumn(col);
                            }
                        });
                        return true;
                    }
                }
            }
            return false;
        }
    }

    protected class DbGridColumnFieldChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnFieldChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null && getInfo().isPlain();
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                ModelElementRef old = after.getDatamodelElement();
                ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, null, DbGridCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
                if (newRef != null) {
                    after.setDatamodelElement(newRef);
                    if (newRef.getField() != null) {
                        if (after.getTitle() == null || after.getTitle().isEmpty()) {
                            after.setTitle(newRef.getField().getDescription());
                        }
                        String newName = newRef.getField().getName();
                        if ((after.getName() == null || after.getName().isEmpty()) && !isNamePresent(newName) && DbControlsDesignUtils.isNameLegalColumnName(newName)) {
                            after.setName(newName);
                        }
                    }
                    if (after.getControlInfo() == null || !isCompatibleWithControl(after, newRef)) {
                        DbControlDesignInfo info = getDefaultDesignInfo4field(newRef);
                        if (info != null) {
                            after.setControlInfo(info);
                        }
                    }
                }
                DbControlDesignInfo cinfo = after.getControlInfo();
                if (cinfo != null) {
                    cinfo.setDatamodelElement(newRef);
                }
            }
        }

        private boolean isCompatibleWithControl(DbGridColumn aInfo, ModelElementRef newRef) {
            if (aInfo != null && newRef != null && aInfo.getControlInfo() != null
                    && newRef.getField() != null) {
                DbControlClassFinder clsFinder = new DbControlClassFinder();
                aInfo.getControlInfo().accept(clsFinder);
                if (clsFinder.getResult() != null) {
                    return DbControlsUtils.isControlCompatible2Type(clsFinder.getResult(), newRef.getField().getTypeInfo().getSqlType());
                }
            }
            return false;
        }
    }

    protected class DbGridColumnFieldClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridColumnFieldClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null && getInfo().isPlain()
                    && getInfo().getDatamodelElement() != null
                    && getInfo().getDatamodelElement().getField() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setDatamodelElement(null);
                DbControlDesignInfo cinfo = after.getControlInfo();
                if (cinfo != null) {
                    cinfo.setDatamodelElement(null);
                }
                after.setControlInfo(null);
            }
        }
    }

    protected class DbGridColumnNameChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnNameChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            String newName = txtName.getText();
            if (after != null) {
                if (newName == null || newName.isEmpty() || !isNamePresent(newName)) {
                    if (newName == null || newName.isEmpty() || DbControlsDesignUtils.isNameLegalColumnName(newName)) {
                        after.setName(newName);
                        updateFunctions(newName, DbControlChangeEdit.selectValueMethod, after.getSelectFunction(), comboSelectFunctionModel);
                        updateFunctions(newName, DbColumnChangeEdit.handleCellMethod, after.getCellFunction(), comboCellFunctionModel);
                    } else {
                        JOptionPane.showMessageDialog(splitColumns, DbControlsDesignUtils.getLocalizedString("badColumnName"), DbControlsDesignUtils.getLocalizedString("columns"), JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(splitColumns, DbControlsDesignUtils.getLocalizedString("nonUniqueColumnName"), DbControlsDesignUtils.getLocalizedString("columns"), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    protected class DbGridColumnHeaderFontChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnHeaderFontChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            Font oldFont = after.getHeaderFont();
            Font lfont = FontChooser.chooseFont(pnlColumn, oldFont, DbControlsDesignUtils.getLocalizedString("selectFont"));
            if (lfont != null) {
                after.setHeaderFont(lfont);
            }
        }
    }

    protected class DbGridColumnHeaderFontClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridColumnHeaderFontClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null && getInfo().getHeaderStyle().getFont() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.getHeaderStyle().setFont(null);
            }
        }
    }

    protected class DbGridColumnWidthChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnWidthChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setWidth((Integer) spinWidth.getValue());
            }
        }
    }

    protected class DbGridColumnPlainChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnPlainChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setPlain(!chkFliped.isSelected());
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            if (chkFliped.isSelected()) {
                tabsFliped.setSelectedComponent(scrollFliped);
            } else {
                tabsFliped.setSelectedComponent(pnlPlain);
            }
        }
    }

    protected class DbGridColumnSelectScriptChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnSelectScriptChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                String script = (String) comboSelectFunction.getSelectedItem();
                if (script == null || script.isEmpty()) {
                    ComboBoxEditor cbe = comboSelectFunction.getEditor();
                    if (cbe != null && cbe.getEditorComponent() != null) {
                        Component ec = cbe.getEditorComponent();
                        if (ec instanceof JTextComponent) {
                            JTextComponent tc = (JTextComponent) ec;
                            script = tc.getText();
                        }
                    }
                }
                after.setSelectFunction(script);
            }
        }
    }

    protected class DbGridColumnSelectScriptClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridColumnSelectScriptClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid()
                    && getInfo() != null && getInfo().getSelectFunction() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setSelectFunction(null);
                after.setSelectOnly(false);
            }
        }
    }

    protected class DbGridColumnSelectOnlyChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnSelectOnlyChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null
                    && getInfo().getSelectFunction() != null && !getInfo().getSelectFunction().isEmpty();
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setSelectOnly(chkColumnSelectOnly.isSelected());
            }
        }
    }

    protected class DbGridCellScriptChangeAction extends DbGridColumnSnapshotAction {

        public DbGridCellScriptChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                String script = (String) comboCellFunction.getSelectedItem();
                if (script == null || script.isEmpty()) {
                    ComboBoxEditor cbe = comboCellFunction.getEditor();
                    if (cbe != null && cbe.getEditorComponent() != null) {
                        Component ec = cbe.getEditorComponent();
                        if (ec instanceof JTextComponent) {
                            JTextComponent tc = (JTextComponent) ec;
                            script = tc.getText();
                        }
                    }
                }
                after.setCellFunction(script);
            }
        }
    }

    protected class DbGridCellScriptClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridCellScriptClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid()
                    && getInfo() != null && getInfo().getCellFunction() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setCellFunction(null);
            }
        }
    }

    protected class DbGridColumnSubstituteChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnSubstituteChangeAction() {
            super(DbGridCustomizer.this);
            putValue(Action.NAME, DbControlsDesignUtils.getLocalizedString("chkSubstitute"));
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null && !getInfo().isPlain();
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setSubstitute(chkSubstitute.isSelected());
            }
        }
    }

    protected class DbGridColumnEnabledChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnEnabledChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setEnabled(chkEnabled.isSelected());
            }
        }
    }

    protected class DbGridColumnVisibleChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnVisibleChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setVisible(chkVisible.isSelected());
            }
        }
    }

    protected class DbGridColumnReadonlyChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnReadonlyChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                after.setReadonly(chkReadOnly.isSelected());
            }
        }
    }

    protected class DbGridColumnControlChangeAction extends DbGridColumnSnapshotAction {

        public DbGridColumnControlChangeAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null && getInfo().isPlain();
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null) {
                Object oSelected = comboColumnControl.getSelectedItem();
                if (oSelected != null && oSelected instanceof Class<?>) {
                    Class<?> lControlClass = (Class<?>) oSelected;
                    Class<?> infoClass = DbControlsUtils.getDesignInfoClass(lControlClass);
                    if (infoClass != null) {
                        try {
                            DbControlDesignInfo info = (DbControlDesignInfo) infoClass.newInstance();
                            info.setDatamodelElement(after.getDatamodelElement());
                            after.setControlInfo(info);
                        } catch (InstantiationException | IllegalAccessException ex) {
                            Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    protected class DbGridColumnControlClearAction extends DbGridColumnSnapshotClearAction {

        public DbGridColumnControlClearAction() {
            super(DbGridCustomizer.this);
        }

        @Override
        public boolean isEnabled() {
            return unaryLinkFieldSelectAction.isDataValid() && getInfo() != null && getInfo().isPlain() && getInfo().getControlInfo() != null;
        }

        @Override
        protected void processChangedDesignInfo(DbGridColumn after) {
            if (after != null && after.getControlInfo() != null) {
                ModelElementRef dmElement = after.getDatamodelElement();
                if (dmElement != null) {
                    DbControlDesignInfo info = getDefaultDesignInfo4field(dmElement);
                    if (info != null) {
                        info.setDatamodelElement(dmElement);
                        after.setControlInfo(info);
                    }
                }
            }
        }
    }

    /**
     * Creates new form DbGridCustomizer
     */
    public DbGridCustomizer() {
        super();
        fillActionMap(getActionMap());
        initComponents();
        fillActionMap(treeColumns.getActionMap());
        fillInputMap(treeColumns.getInputMap(), treeColumns.getActionMap());

        columnsTreeTransferHandler = new DbGridColumnsTreeTransferHandler(this);
        comboCellControl.setModel(new EmbeddedControlComboModel());
        comboColumnControl.setModel(new EmbeddedControlComboModel());

        comboCellControl.setRenderer(new EmbeddedControlsListCellRenderer());
        comboColumnControl.setRenderer(new EmbeddedControlsListCellRenderer());

        comboDsParam.setModel(comboDsParamModel);

        String emptyfieldText = DbControlsDesignUtils.getLocalizedString("notSelected");
        txtColHeaderFont.setText(emptyfieldText);
        txtColAggregate.setText(emptyfieldText);
        txtDatamodelDs.setText(emptyfieldText);
        txtDatamodelField.setText(emptyfieldText);
        txtColumnsDs.setText(emptyfieldText);
        txtColumnsDisplayField.setText(emptyfieldText);
        txtCellsDs.setText(emptyfieldText);
        txtRowsDsKeyField.setText(emptyfieldText);
        txtColumnsDsKeyField.setText(emptyfieldText);
        txtCellsDsValueField.setText(emptyfieldText);
        txtTreeParentDmField.setText(emptyfieldText);
        txtFieldParamSource.setText(emptyfieldText);
        rowsRenderer.setBorder(new EtchedBorder());
        colsRenderer.setBorder(new EtchedBorder());
        colsDisplayFieldRenderer.setBorder(new EtchedBorder());
        cellsRenderer.setBorder(new EtchedBorder());
        rowsKeyRenderer.setBorder(new EtchedBorder());
        colKeyRenderer.setBorder(new EtchedBorder());
        unaryTreeLinkRenderer.setBorder(new EtchedBorder());
        paramSourceRenderer.setBorder(new EtchedBorder());
        columnDmRenderer.setBorder(new EtchedBorder());

        comboDsParam.setRenderer(new FieldsComboListCellRenderer(fieldsFont, comboDsParamModel));

        columnsTreeModel = new DbGridColumnsTreeModel();
        columnsTreeModel.setUndoSupport(undoSupport);
        treeColumns.setModel(columnsTreeModel);
        DbGridColumnsTreeRenderer treeColsRenderer = new DbGridColumnsTreeRenderer();
        treeColumns.setCellRenderer(treeColsRenderer);
        treeColumns.setCellEditor(new DbGridColumnsTreeEditor(treeColumns, treeColsRenderer));
        treeColumns.setDragEnabled(true);
        treeColumns.setDropMode(DropMode.ON_OR_INSERT);
        treeColumns.setTransferHandler(columnsTreeTransferHandler);
        treeColumns.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeColumns.getSelectionModel().addTreeSelectionListener(new SelectedDbGridColumnUpdater());

        comboGeneralCellFunction.setModel(comboGeneralRowsFunctionModel);
        comboLinkFunction.setModel(comboLinkFunctionModel);
        comboSelectFunction.setModel(comboSelectFunctionModel);
        comboCellFunction.setModel(comboCellFunctionModel);

        DefaultComboBoxModel rhtModel = new DefaultComboBoxModel();
        rhtModel.addElement(DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_NONE);
        rhtModel.addElement(DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_USUAL);
        rhtModel.addElement(DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_CHECKBOX);
        rhtModel.addElement(DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_RADIOBUTTON);
        comboRowHeaderType.setModel(rhtModel);
        comboRowHeaderType.setRenderer(new RowsHeaderTypeComboListCellRenderer());
        chkEnabled.setVisible(false);
    }
    private DsParametersComboModel comboDsParamModel = new DsParametersComboModel();

    @Override
    public void setScriptHost(ScriptEvents aScriptHost) {
        super.setScriptHost(aScriptHost);
        columnsTreeModel.setScriptEvents(aScriptHost);
    }

    @Override
    public void setUndoSupport(UndoableEditSupport aSupport) {
        super.setUndoSupport(aSupport);
        columnsTreeModel.setUndoSupport(undoSupport);
    }
    protected FieldRefRenderer rowsRenderer = new FieldRefRenderer();
    protected FieldRefRenderer colsRenderer = new FieldRefRenderer();
    protected FieldRefRenderer colsDisplayFieldRenderer = new FieldRefRenderer();
    protected FieldRefRenderer cellsRenderer = new FieldRefRenderer();
    protected FieldRefRenderer rowsKeyRenderer = new FieldRefRenderer();
    protected FieldRefRenderer colKeyRenderer = new FieldRefRenderer();
    protected FieldRefRenderer unaryTreeLinkRenderer = new FieldRefRenderer();
    protected FieldRefRenderer paramSourceRenderer = new FieldRefRenderer();
    protected FieldRefRenderer columnDmRenderer = new FieldRefRenderer();
    protected SampleCustomizersControlsFactory cellsCustomizers = new SampleCustomizersControlsFactory();

    protected final void fillActionMap(ActionMap am) {
        am.put(DbGridRowsDatasourceSelectAction.class.getSimpleName(), new DbGridRowsDatasourceSelectAction());
        am.put(DbGridRowsDatasourceClearAction.class.getSimpleName(), new DbGridRowsDatasourceClearAction());
        am.put(DbGridColumnsDatasourceSelectAction.class.getSimpleName(), new DbGridColumnsDatasourceSelectAction());
        am.put(DbGridColumnsDatasourceClearAction.class.getSimpleName(), new DbGridColumnsDatasourceClearAction());
        am.put(DbGridColumnsDisplayFieldSelectAction.class.getSimpleName(), new DbGridColumnsDisplayFieldSelectAction());
        am.put(DbGridColumnsDisplayFieldClearAction.class.getSimpleName(), new DbGridColumnsDisplayFieldClearAction());
        am.put(DbGridGeneralCellScriptChangeAction.class.getSimpleName(), new DbGridGeneralCellScriptChangeAction());
        am.put(DbGridGeneralCellScriptClearAction.class.getSimpleName(), new DbGridGeneralCellScriptClearAction());
        cellDsSelectAction = new DbGridCellsDatasourceSelectAction();
        am.put(DbGridCellsDatasourceSelectAction.class.getSimpleName(), cellDsSelectAction);
        am.put(DbGridCellsDatasourceClearAction.class.getSimpleName(), new DbGridCellsDatasourceClearAction());
        am.put(DbGridFixedRowsChangeAction.class.getSimpleName(), new DbGridFixedRowsChangeAction());
        am.put(DbGridFixedColumnsChangeAction.class.getSimpleName(), new DbGridFixedColumnsChangeAction());
        am.put(DbGridRowsHeaderTypeChangeAction.class.getSimpleName(), new DbGridRowsHeaderTypeChangeAction());
        cellRowKeySelectAction = new DbGridRowKeySelectAction();
        am.put(DbGridRowKeySelectAction.class.getSimpleName(), cellRowKeySelectAction);
        am.put(DbGridRowKeyClearAction.class.getSimpleName(), new DbGridRowKeyClearAction());
        cellColKeySelectAction = new DbGridColKeySelectAction();
        am.put(DbGridColKeySelectAction.class.getSimpleName(), cellColKeySelectAction);
        am.put(DbGridColKeyClearAction.class.getSimpleName(), new DbGridColKeyClearAction());
        cellValueSelectAction = new DbGridCellsValueFieldSelectAction();
        am.put(DbGridCellsValueFieldSelectAction.class.getSimpleName(), cellValueSelectAction);
        am.put(DbGridCellsValueFieldClearAction.class.getSimpleName(), new DbGridCellsValueFieldClearAction());
        cellControlSelectAction = new DbGridCellControlChangeAction();
        am.put(DbGridCellControlChangeAction.class.getSimpleName(), cellControlSelectAction);
        am.put(DbGridCellControlClearAction.class.getSimpleName(), new DbGridCellControlClearAction());
        unaryLinkFieldSelectAction = new DbGridTreeUnaryLinkSelectAction();
        am.put(DbGridTreeUnaryLinkSelectAction.class.getSimpleName(), unaryLinkFieldSelectAction);
        am.put(DbGridTreeUnaryLinkClearAction.class.getSimpleName(), new DbGridTreeUnaryLinkClearAction());
        paramSourceSelectAction = new DbGridTreeParamSourceSelectAction();
        am.put(DbGridTreeParamSourceSelectAction.class.getSimpleName(), paramSourceSelectAction);
        am.put(DbGridTreeParamSourceClearAction.class.getSimpleName(), new DbGridTreeParamSourceClearAction());

        treeParamChangeAction = new DbGridTreeParamChangeAction();
        am.put(DbGridTreeParamChangeAction.class.getSimpleName(), treeParamChangeAction);
        am.put(DbGridTreeParamClearAction.class.getSimpleName(), new DbGridTreeParamClearAction());
        am.put(DbGridTreeScriptChangeAction.class.getSimpleName(), new DbGridTreeScriptChangeAction());
        am.put(DbGridTreeScriptClearAction.class.getSimpleName(), new DbGridTreeScriptClearAction());
        am.put(DbGridTreeKind1QueryAction.class.getSimpleName(), new DbGridTreeKind1QueryAction());
        am.put(DbGridTreeKindAddQueriesAction.class.getSimpleName(), new DbGridTreeKindAddQueriesAction());
        am.put(DbGridTreeKindScriptAction.class.getSimpleName(), new DbGridTreeKindScriptAction());

        columnCopyAction = new DbGridColumnsCopy();
        am.put(DbGridColumnsCopy.class.getSimpleName(), columnCopyAction);
        columnDeleteAction = new DbGridColumnsDelete();
        am.put(DbGridColumnsDelete.class.getSimpleName(), columnDeleteAction);

        am.put(DbGridColumnsAdd.class.getSimpleName(), new DbGridColumnsAdd());
        am.put(DbGridColumnsAddInNode.class.getSimpleName(), new DbGridColumnsAddInNode());

        columnCutAction = new DbGridColumnsCut();
        am.put(DbGridColumnsCut.class.getSimpleName(), columnCutAction);
        columnPasteAction = new DbGridColumnsPaste();
        am.put(DbGridColumnsPaste.class.getSimpleName(), columnPasteAction);

        am.put(DbGridColumnsMoveUp.class.getSimpleName(), new DbGridColumnsMoveUp());
        am.put(DbGridColumnsMoveDown.class.getSimpleName(), new DbGridColumnsMoveDown());

        columnControlSelectAction = new DbGridColumnControlChangeAction();
        am.put(DbGridColumnControlChangeAction.class.getSimpleName(), columnControlSelectAction);
        am.put(DbGridColumnControlClearAction.class.getSimpleName(), new DbGridColumnControlClearAction());

        am.put(DbGridColumnEnabledChangeAction.class.getSimpleName(), new DbGridColumnEnabledChangeAction());
        am.put(DbGridColumnVisibleChangeAction.class.getSimpleName(), new DbGridColumnVisibleChangeAction());
        am.put(DbGridColumnReadonlyChangeAction.class.getSimpleName(), new DbGridColumnReadonlyChangeAction());
        am.put(DbGridColumnPlainChangeAction.class.getSimpleName(), new DbGridColumnPlainChangeAction());
        am.put(DbGridColumnSubstituteChangeAction.class.getSimpleName(), new DbGridColumnSubstituteChangeAction());

        am.put(DbGridColumnFieldChangeAction.class.getSimpleName(), new DbGridColumnFieldChangeAction());
        am.put(DbGridColumnFieldClearAction.class.getSimpleName(), new DbGridColumnFieldClearAction());

        am.put(DbGridColumnHeaderFontChangeAction.class.getSimpleName(), new DbGridColumnHeaderFontChangeAction());
        am.put(DbGridColumnHeaderFontClearAction.class.getSimpleName(), new DbGridColumnHeaderFontClearAction());

        am.put(DbGridColumnNameChangeAction.class.getSimpleName(), new DbGridColumnNameChangeAction());

        am.put(DbGridColumnWidthChangeAction.class.getSimpleName(), new DbGridColumnWidthChangeAction());

        am.put(DbGridColumnSelectScriptChangeAction.class.getSimpleName(), new DbGridColumnSelectScriptChangeAction());
        am.put(DbGridColumnSelectScriptClearAction.class.getSimpleName(), new DbGridColumnSelectScriptClearAction());
        am.put(DbGridColumnSelectOnlyChangeAction.class.getSimpleName(), new DbGridColumnSelectOnlyChangeAction());

        am.put(DbGridCellScriptChangeAction.class.getSimpleName(), new DbGridCellScriptChangeAction());
        am.put(DbGridCellScriptClearAction.class.getSimpleName(), new DbGridCellScriptClearAction());

        am.put(DbGridColumnsFillAction.class.getSimpleName(), new DbGridColumnsFillAction());
    }

    private void updateTreeGridView() throws Exception {
        updateTreeGridKindView();
        updateTreeGridParam2GetChildrenView();
        updateTreeGridParamSourceFieldView();
        updateTreeGridUnaryLinkView();
        updateTreeGridLinkFunctionView();
    }

    private void updateTreeGridLinkFunctionView() throws Exception {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        DbGridTreeDesignInfo treeDesignInfo = info.getTreeDesignInfo();
        DbControlsDesignUtils.updateScriptItem(comboLinkFunction, treeDesignInfo.getParametersSetupScript2GetChildren());
    }

    private void updateTreeGridUnaryLinkView() throws Exception {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        DbGridTreeDesignInfo treeDesignInfo = info.getTreeDesignInfo();
        if (unaryLinkFieldSelectAction.isEnabled()) {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), treeDesignInfo.getUnaryLinkField(), pnlTreeParentDmField, unaryTreeLinkRenderer, txtTreeParentDmField, fieldsFont);
        } else {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlTreeParentDmField, unaryTreeLinkRenderer, txtTreeParentDmField, fieldsFont);
        }
    }

    private void updateTreeGridParamSourceFieldView() throws Exception {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        DbGridTreeDesignInfo treeDesignInfo = info.getTreeDesignInfo();
        if (paramSourceSelectAction.isEnabled()) {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), treeDesignInfo.getParamSourceField(), pnlDmFieldParamSource, paramSourceRenderer, txtFieldParamSource, fieldsFont);
        } else {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlDmFieldParamSource, paramSourceRenderer, txtFieldParamSource, fieldsFont);
        }
    }

    private void updateTreeGridParam2GetChildrenView() throws Exception {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        DbGridTreeDesignInfo treeDesignInfo = info.getTreeDesignInfo();
        Parameters params = null;
        Long entityId = null;
        if (getDatamodel() != null) {
            ModelElementRef dmElement = info.getRowsColumnsDesignInfo().getRowsDatasource();
            if (dmElement != null) {
                entityId = dmElement.getEntityId();
                if (entityId != null) {
                    ApplicationEntity ent = getDatamodel().getEntities().get(entityId);
                    if (ent != null) {
                        Query query = ent.getQuery();
                        if (query != null) {
                            params = query.getParameters();
                        }
                    }
                }
            }
            comboDsParamModel.setParameters(getDatamodel(), entityId, params);
            if (treeParamChangeAction.isEnabled() && treeDesignInfo.getParam2GetChildren() != null && !treeDesignInfo.getParam2GetChildren().isField() && treeDesignInfo.getParam2GetChildren().getField() != null) {
                ModelElementRef param2GetChildren = treeDesignInfo.getParam2GetChildren();
                if (param2GetChildren != null && !param2GetChildren.isField()) {
                    DbControlsDesignUtils.setSelectedItem(comboDsParam, params.get(param2GetChildren.getField().getName()));
                }
            } else {
                DbControlsDesignUtils.setSelectedItem(comboDsParam, null);
            }
        }
    }

    private void updateTreeGridKindView() throws Exception {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        DbGridTreeDesignInfo treeDesignInfo = info.getTreeDesignInfo();
        radioSingleDs.setSelected(treeDesignInfo.getTreeKind() == DbGridTreeDesignInfo.ONE_FIELD_ONE_QUERY_TREE_KIND);
        radioAddQueries.setSelected(treeDesignInfo.getTreeKind() == DbGridTreeDesignInfo.FIELD_2_PARAMETER_TREE_KIND);
        radioScriptFunction.setSelected(treeDesignInfo.getTreeKind() == DbGridTreeDesignInfo.SCRIPT_PARAMETERS_TREE_KIND);
    }

    private void updateRowsView() throws Exception {
        updateRowsDatasourceView();
        updateFixedColsView();
        updateFixedRowsView();
        updateHeaderTypeView();
        updateGeneralRowFunction();
    }

    private void updateHeaderTypeView() throws Exception {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        DbGridRowsColumnsDesignInfo rowsColsInfo = info.getRowsColumnsDesignInfo();
        DbControlsDesignUtils.setSelectedItem(comboRowHeaderType, rowsColsInfo.getRowsHeaderType());
    }

    private void updateGeneralRowFunction() throws Exception {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        DbGridRowsColumnsDesignInfo rowsColsInfo = info.getRowsColumnsDesignInfo();
        updateFunctions(((Component) bean).getName(), DbColumnChangeEdit.handleCellMethod, rowsColsInfo.getGeneralRowFunction(), comboGeneralRowsFunctionModel);
        DbControlsDesignUtils.updateScriptItem(comboGeneralCellFunction, rowsColsInfo.getGeneralRowFunction());
    }

    private void updateFixedRowsView() throws Exception {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        DbGridRowsColumnsDesignInfo rowsColsInfo = info.getRowsColumnsDesignInfo();
        updatingView = true;
        try {
            spinFixedRows.setValue(rowsColsInfo.getFixedRows());
            boolean spinsFixedEnabled = rowsColsInfo.getRowsDatasource() != null && rowsColsInfo.getRowsDatasource().getEntityId() != null;
            spinFixedRows.setEnabled(spinsFixedEnabled);
        } finally {
            updatingView = false;
        }
    }

    private void updateFixedColsView() throws Exception {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        DbGridRowsColumnsDesignInfo rowsColsInfo = info.getRowsColumnsDesignInfo();
        updatingView = true;
        try {
            spinFixedCols.setValue(rowsColsInfo.getFixedColumns());
            boolean spinsFixedEnabled = rowsColsInfo.getRowsDatasource() != null && rowsColsInfo.getRowsDatasource().getEntityId() != null;
            spinFixedCols.setEnabled(spinsFixedEnabled);
        } finally {
            updatingView = false;
        }
    }

    private void updateRowsDatasourceView() throws Exception {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        DbGridRowsColumnsDesignInfo rowsColsInfo = info.getRowsColumnsDesignInfo();
        DbControlsDesignUtils.updateDmElement(getDatamodel(), rowsColsInfo.getRowsDatasource(), pnlDmDatasource, rowsRenderer, txtDatamodelDs, fieldsFont);
        updateTreeGridParam2GetChildrenView();
    }

    private void updateHeaderView() {
        DbGridDesignInfo info = (DbGridDesignInfo) designInfo;
        treeColumns.setEnabled(true);
        TreeModel tm = treeColumns.getModel();
        if (tm != null && tm instanceof DbGridColumnsTreeModel) {
            DbGridColumnsTreeModel ctm = (DbGridColumnsTreeModel) tm;
            DbGridColumn lRoot = ctm.getDummyRoot();
            if (lRoot != null) {
                ctm.setRootChildren(info.getHeader());
                ctm.fireStructureChanged();
            }
        }
    }

    @Override
    protected void updateView() {
        try {
            // rows design
            updateRowsView();
            // tree grid design
            updateTreeGridView();
            // check if header is replaced by another root columns list
            updateHeaderView();
            // update column design
            DbGridColumn col = DbGridColumnsStructureSnapshotAction.getSingleSelectedColumn(treeColumns);
            updateColumnView(col);
            enableColumnControlsBySelection();
        } catch (Exception ex) {
            Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void updateHandlers() {
        if (bean != null && scriptHost != null) {
            DbGridDesignInfo cInfo = (DbGridDesignInfo) designInfo;
            updateFunctions(((Component) bean).getName(), DbColumnChangeEdit.handleCellMethod, cInfo.getRowsColumnsDesignInfo().getGeneralRowFunction(), comboGeneralRowsFunctionModel);
        }
    }

    /*
     for (DbGridColumn col : collectColumns(((DbGridDesignInfo) designInfo).getHeader())) {
     updateFunctions(col.getName(), handleCellMethod, comboCellFunctionModel);
     updateFunctions(col.getName(), selectValueMethod, comboSelectFunctionModel);
     }
     protected static List<DbGridColumn> collectColumns(List<DbGridColumn> cols) {
     List<DbGridColumn> res = new ArrayList<>();
     res.addAll(cols);
     for (DbGridColumn col : cols) {
     List<DbGridColumn> childCols = collectColumns(col.getChildren());
     res.addAll(childCols);
     }
     return res;
     }
     */
    protected void enableColumnControlsBySelection() {
        boolean lIsColumnsEditable = treeColumns.getSelectionCount() == 1;
        lblDmField.setEnabled(lIsColumnsEditable);
        lblColName.setEnabled(lIsColumnsEditable);
        lblHeaderFont.setEnabled(lIsColumnsEditable);
        lblWidth.setEnabled(lIsColumnsEditable);
        lblColumnRowsControl.setEnabled(lIsColumnsEditable);
        lblAggregates.setEnabled(lIsColumnsEditable);
        spinWidth.setEnabled(lIsColumnsEditable);
        tabsFliped.setEnabled(lIsColumnsEditable);
        lblColumnsDs.setEnabled(lIsColumnsEditable);
        lblColumnsDisplayField.setEnabled(lIsColumnsEditable);
        lblCellsDs.setEnabled(lIsColumnsEditable);
        lblRowsKey.setEnabled(lIsColumnsEditable);
        lblColumnsKey.setEnabled(lIsColumnsEditable);
        lblCellValue.setEnabled(lIsColumnsEditable);
        lblCellsControl.setEnabled(lIsColumnsEditable);
        pnlCellsControlCustomizer.setEnabled(lIsColumnsEditable);
        chkFliped.setEnabled(lIsColumnsEditable);
    }

    private void updateColumnView(DbGridColumn aCol) throws Exception {
        // name
        updateColumnNameView(aCol);
        // flags
        updateColumnVisibleView(aCol);
        updateColumnReadOnlyView(aCol);
        updateColumnEnabledView(aCol);
        // style
        updateColumnWidthView(aCol);
        updateColumnFontView(aCol);
        updateColumnSubstituteView(aCol);
        updateColumnFlipedView(aCol);
        // script
        updateColumnSelectFunctionView(aCol);
        updateColumnSelectOnlyView(aCol);
        updateColumnCellFunctionView(aCol);
        // data
        updateColumnDatamodelElementView(aCol);
        updateColumnRowsControlView(aCol);
        // fliped
        updateColumnColumnsDatasourceView(aCol);
        updateColumnCellsDatasourceView(aCol);
        updateColumnRowsKeyFieldView(aCol);
        updateColumnColumnsKeyFieldView(aCol);
        updateColumnColumnsDisplayFieldView(aCol);
        updateColumnCellValueFieldView(aCol);
        updateColumnCellControlView(aCol);
    }

    private void updateColumnDatamodelElementView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), aCol.getDatamodelElement(), pnlDmField, columnDmRenderer, txtDatamodelField, fieldsFont);
        } else {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlDmField, columnDmRenderer, txtDatamodelField, fieldsFont);
        }
    }

    private void updateColumnFontView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            DbControlsDesignUtils.updateFont(DbControlsUtils.toNativeFont(aCol.getHeaderStyle().getFont()), pnlColHeaderFont, txtColHeaderFont, fieldsFont);
        } else {
            DbControlsDesignUtils.updateFont(null, pnlColHeaderFont, txtColHeaderFont, fieldsFont);
        }
    }

    private void updateColumnWidthView(DbGridColumn aCol) throws Exception {
        updatingView = true;
        try {
            if (aCol != null) {
                spinWidth.setValue(aCol.getWidth());
            } else {
                spinWidth.setValue(0);
            }
        } finally {
            updatingView = false;
        }
    }

    private void updateColumnEnabledView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            chkEnabled.setSelected(aCol.isEnabled());
        } else {
            chkEnabled.setSelected(false);
        }
    }

    private void updateColumnReadOnlyView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            chkReadOnly.setSelected(aCol.isReadonly());
        } else {
            chkReadOnly.setSelected(false);
        }
    }

    private void updateColumnVisibleView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            chkVisible.setSelected(aCol.isVisible());
        } else {
            chkVisible.setSelected(false);
        }
    }

    private void updateColumnNameView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            txtName.setText(aCol.getName());
        } else {
            txtName.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        }
    }

    private void updateColumnSelectOnlyView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            chkColumnSelectOnly.setSelected(aCol.isSelectOnly());
        } else {
            chkColumnSelectOnly.setSelected(false);
        }
    }

    private void updateColumnSelectFunctionView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            updateFunctions(aCol.getName(), DbControlChangeEdit.selectValueMethod, aCol.getSelectFunction(), comboSelectFunctionModel);
            DbControlsDesignUtils.updateScriptItem(comboSelectFunction, aCol.getSelectFunction());
        } else {
            DbControlsDesignUtils.updateScriptItem(comboSelectFunction, "");
        }
    }

    private void updateColumnCellFunctionView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            updateFunctions(aCol.getName(), DbColumnChangeEdit.handleCellMethod, aCol.getCellFunction(), comboCellFunctionModel);
            DbControlsDesignUtils.updateScriptItem(comboCellFunction, aCol.getCellFunction());
        } else {
            DbControlsDesignUtils.updateScriptItem(comboCellFunction, "");
        }
    }

    private void updateColumnRowsControlView(DbGridColumn aCol) throws Exception {
        pnlColRowsControlCustomizer.removeAll();
        pnlColRowsControlCustomizer.revalidate();
        pnlColRowsControlCustomizer.repaint();
        if (aCol != null) {
            Class<?> controlClass = null;
            if (aCol.getControlInfo() != null && columnControlSelectAction.isEnabled()) {
                DbControlDesignInfo linfo = aCol.getControlInfo();
                linfo.clearPropertyListeners();
                DbControlClassFinder clsFinder = new DbControlClassFinder();
                linfo.accept(clsFinder);
                controlClass = clsFinder.getResult();
                if (controlClass != null) {
                    java.beans.Customizer controlCustomizer = cellsCustomizers.createCustomizer(controlClass);
                    DbControl sampleControl = cellsCustomizers.createSampleBean(controlClass);
                    if (controlCustomizer != null && sampleControl != null) {
                        sampleControl.setDatamodel(getDatamodel());
                        sampleControl.setDesignInfo(linfo);
                        controlCustomizer.setObject(sampleControl);
                        controlCustomizer.setObject(undoSupport);
                        if (controlCustomizer instanceof JComponent) {
                            pnlColRowsControlCustomizer.add((JComponent) controlCustomizer, BorderLayout.CENTER);
                            Object oLayout = ((JComponent) controlCustomizer).getLayout();
                            if (oLayout != null && oLayout instanceof BorderLayout) {
                                BorderLayout bl = (BorderLayout) oLayout;
                                Component nComp = bl.getLayoutComponent(BorderLayout.NORTH);
                                if (nComp != null) {
                                    nComp.setVisible(false);
                                }
                            }
                        }
                        if (aCol.getControlInfo() != null) {
                            aCol.getControlInfo().setDatamodelElement(aCol.getDatamodelElement());
                        }
                    }
                }
            }
            if (aCol != null && aCol.getDatamodelElement() != null
                    && aCol.getDatamodelElement().getField() != null) {
                EmbeddedControlComboModel model = (EmbeddedControlComboModel) comboColumnControl.getModel();
                model.setFieldType(aCol.getDatamodelElement().getField().getTypeInfo().getSqlType());
            }
            DbControlsDesignUtils.setSelectedItem(comboColumnControl, controlClass);
        }
    }

    private void updateColumnColumnsDatasourceView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), aCol.getColumnsDatasource(), pnlColumnsDs, colsRenderer, txtColumnsDs, fieldsFont);
        } else {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlColumnsDs, colsRenderer, txtColumnsDs, fieldsFont);
        }
    }

    private void updateColumnColumnsDisplayFieldView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), aCol.getColumnsDisplayField(), pnlColumnsDisplayField, colsDisplayFieldRenderer, txtColumnsDisplayField, fieldsFont);
        } else {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlColumnsDisplayField, colsDisplayFieldRenderer, txtColumnsDisplayField, fieldsFont);
        }
    }

    private void updateColumnCellsDatasourceView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            if (cellDsSelectAction.isEnabled()) {
                DbControlsDesignUtils.updateDmElement(getDatamodel(), aCol.getCellsDatasource(), pnlCellsDs, cellsRenderer, txtCellsDs, fieldsFont);
            } else {
                DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlCellsDs, cellsRenderer, txtCellsDs, fieldsFont);
            }
        } else {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlCellsDs, cellsRenderer, txtCellsDs, fieldsFont);
        }
    }

    private void updateColumnRowsKeyFieldView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            DbGridCellDesignInfo cellDesignInfo = aCol.getCellDesignInfo();
            if (cellRowKeySelectAction.isEnabled()) {
                DbControlsDesignUtils.updateDmElement(getDatamodel(), cellDesignInfo.getRowsKeyField(), pnlRowsDsKeyField, rowsKeyRenderer, txtRowsDsKeyField, fieldsFont);
            } else {
                DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlRowsDsKeyField, rowsKeyRenderer, txtRowsDsKeyField, fieldsFont);
            }
        } else {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlRowsDsKeyField, rowsKeyRenderer, txtRowsDsKeyField, fieldsFont);
        }
    }

    private void updateColumnColumnsKeyFieldView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            DbGridCellDesignInfo cellDesignInfo = aCol.getCellDesignInfo();
            if (cellColKeySelectAction.isEnabled()) {
                DbControlsDesignUtils.updateDmElement(getDatamodel(), cellDesignInfo.getColumnsKeyField(), pnlColumnsDsKeyField, colKeyRenderer, txtColumnsDsKeyField, fieldsFont);
            } else {
                DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlColumnsDsKeyField, colKeyRenderer, txtColumnsDsKeyField, fieldsFont);
            }
        } else {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlColumnsDsKeyField, colKeyRenderer, txtColumnsDsKeyField, fieldsFont);
        }
    }

    private void updateColumnCellValueFieldView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            DbGridCellDesignInfo cellDesignInfo = aCol.getCellDesignInfo();
            if (cellValueSelectAction.isEnabled()) {
                DbControlsDesignUtils.updateDmElement(getDatamodel(), cellDesignInfo.getCellValueField(), pnlCellsDsValueField, fieldRenderer, txtCellsDsValueField, fieldsFont);
            } else {
                DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlCellsDsValueField, fieldRenderer, txtCellsDsValueField, fieldsFont);
            }
        } else {
            DbControlsDesignUtils.updateDmElement(getDatamodel(), null, pnlCellsDsValueField, fieldRenderer, txtCellsDsValueField, fieldsFont);
        }
    }

    private void updateColumnFlipedView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            chkFliped.setSelected(!aCol.isPlain());
        } else {
            chkFliped.setSelected(false);
        }
    }

    private void updateColumnSubstituteView(DbGridColumn aCol) throws Exception {
        if (aCol != null) {
            chkSubstitute.setSelected(aCol.isSubstitute());
        } else {
            chkSubstitute.setSelected(false);
        }
    }

    private void updateColumnCellControlView(DbGridColumn aCol) throws Exception {
        // cells control design
        pnlCellsControlCustomizer.removeAll();
        pnlCellsControlCustomizer.revalidate();
        pnlCellsControlCustomizer.repaint();
        if (aCol != null) {
            DbGridCellDesignInfo cellDesignInfo = aCol.getCellDesignInfo();
            Class<?> controlClass = null;
            if (cellDesignInfo.getCellControlInfo() != null && cellControlSelectAction.isEnabled()) {
                DbControlDesignInfo linfo = cellDesignInfo.getCellControlInfo();
                linfo.clearPropertyListeners();
                DbControlClassFinder clsFinder = new DbControlClassFinder();
                linfo.accept(clsFinder);
                controlClass = clsFinder.getResult();
                if (controlClass != null) {
                    java.beans.Customizer controlCustomizer = cellsCustomizers.createCustomizer(controlClass);
                    DbControl sampleControl = cellsCustomizers.createSampleBean(controlClass);
                    if (controlCustomizer != null && sampleControl != null) {
                        sampleControl.setDatamodel(getDatamodel());
                        sampleControl.setDesignInfo(linfo);
                        controlCustomizer.setObject(sampleControl);
                        controlCustomizer.setObject(undoSupport);
                        if (controlCustomizer instanceof JComponent) {
                            pnlCellsControlCustomizer.add((JComponent) controlCustomizer, BorderLayout.CENTER);
                            Object oLayout = ((JComponent) controlCustomizer).getLayout();
                            if (oLayout != null && oLayout instanceof BorderLayout) {
                                BorderLayout bl = (BorderLayout) oLayout;
                                Component nComp = bl.getLayoutComponent(BorderLayout.NORTH);
                                if (nComp != null) {
                                    nComp.setVisible(false);
                                }
                            }
                        }
                    }
                }
            }
            if (cellDesignInfo != null && cellDesignInfo.getCellValueField() != null
                    && cellDesignInfo.getCellValueField().getField() != null) {
                EmbeddedControlComboModel model = (EmbeddedControlComboModel) comboCellControl.getModel();
                model.setFieldType(cellDesignInfo.getCellValueField().getField().getTypeInfo().getSqlType());
            }
            DbControlsDesignUtils.setSelectedItem(comboCellControl, controlClass);
        }
    }

    protected static DbControlDesignInfo getDefaultDesignInfo4field(ModelElementRef dmElement) {
        if (dmElement != null) {
            Field fieldMd = dmElement.getField();
            if (fieldMd != null) {
                int cellType = fieldMd.getTypeInfo().getSqlType();
                Class<?>[] compatibleControlsClasses = DbControlsUtils.getCompatibleControls(cellType);
                if (compatibleControlsClasses != null && compatibleControlsClasses.length > 0) {
                    Class<?> lControlClass = compatibleControlsClasses[0];
                    if (lControlClass != null) {
                        Class<?> infoClass = DbControlsUtils.getDesignInfoClass(lControlClass);
                        if (infoClass != null) {
                            try {
                                DbControlDesignInfo linfo = (DbControlDesignInfo) infoClass.newInstance();
                                return linfo;
                            } catch (InstantiationException | IllegalAccessException ex) {
                                Logger.getLogger(DbGridCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGroupTree = new javax.swing.ButtonGroup();
        popupColumnsTree = new javax.swing.JPopupMenu();
        mnuAddColumn = new javax.swing.JMenuItem();
        mnuAddInNode = new javax.swing.JMenuItem();
        mnuDelColumn = new javax.swing.JMenuItem();
        sepClipboard = new javax.swing.JSeparator();
        mnuCut = new javax.swing.JMenuItem();
        mnuCopy = new javax.swing.JMenuItem();
        mnuPaste = new javax.swing.JMenuItem();
        sepMoves = new javax.swing.JSeparator();
        mnuUp = new javax.swing.JMenuItem();
        mnuDown = new javax.swing.JMenuItem();
        sepService = new javax.swing.JSeparator();
        mnuFill = new javax.swing.JMenuItem();
        tabsGridCustomizer = new javax.swing.JTabbedPane();
        pnlGeneric = new javax.swing.JPanel();
        lblDmDatasource = new javax.swing.JLabel();
        pnlDmDatasource = new javax.swing.JPanel();
        txtDatamodelDs = new javax.swing.JTextField();
        pnlDsButtons = new javax.swing.JPanel();
        btnDelDs = new javax.swing.JButton();
        btnSelectDs = new javax.swing.JButton();
        lblFixedCols = new javax.swing.JLabel();
        spinFixedCols = new javax.swing.JSpinner();
        lblFixedRows = new javax.swing.JLabel();
        spinFixedRows = new javax.swing.JSpinner();
        comboRowHeaderType = new javax.swing.JComboBox();
        lblRowsHeaderType = new javax.swing.JLabel();
        lblCellHandler1 = new javax.swing.JLabel();
        pnlGeneralCellFunction = new javax.swing.JPanel();
        btnDelGeneralCellFunction = new javax.swing.JButton();
        comboGeneralCellFunction = new javax.swing.JComboBox();
        splitColumns = new javax.swing.JSplitPane();
        scrollColumns = new javax.swing.JScrollPane();
        treeColumns = new javax.swing.JTree();
        pnlColumn = new javax.swing.JPanel();
        lblHeaderFont = new javax.swing.JLabel();
        pnlColHeaderFont = new javax.swing.JPanel();
        txtColHeaderFont = new javax.swing.JTextField();
        pnlColHeaderFontButtons = new javax.swing.JPanel();
        btnDelColHeaderFont = new javax.swing.JButton();
        btnColHeaderFont = new javax.swing.JButton();
        spinWidth = new javax.swing.JSpinner();
        chkEnabled = new javax.swing.JCheckBox();
        txtName = new javax.swing.JTextField();
        lblColName = new javax.swing.JLabel();
        lblWidth = new javax.swing.JLabel();
        chkVisible = new javax.swing.JCheckBox();
        chkReadOnly = new javax.swing.JCheckBox();
        tabsFliped = new javax.swing.JTabbedPane();
        pnlPlain = new javax.swing.JPanel();
        lblColumnRowsControl = new javax.swing.JLabel();
        pnlColRowsControl = new javax.swing.JPanel();
        btnDelColRowsControl = new javax.swing.JButton();
        comboColumnControl = new javax.swing.JComboBox();
        lblDmField = new javax.swing.JLabel();
        pnlDmField = new javax.swing.JPanel();
        txtDatamodelField = new javax.swing.JTextField();
        pnlFieldButtons = new javax.swing.JPanel();
        btnDelField = new javax.swing.JButton();
        btnSelectField = new javax.swing.JButton();
        pnlColRowsControlCustomizer = new javax.swing.JPanel();
        lblAggregates = new javax.swing.JLabel();
        pnlColAggregate = new javax.swing.JPanel();
        txtColAggregate = new javax.swing.JTextField();
        pnlColAggregateButtons = new javax.swing.JPanel();
        btnDelColAggregate = new javax.swing.JButton();
        btnSelectColAggregate = new javax.swing.JButton();
        scrollFliped = new javax.swing.JScrollPane();
        pnlFliped = new javax.swing.JPanel();
        pnlCellsControlCustomizer = new javax.swing.JPanel();
        pnlCellsControl = new javax.swing.JPanel();
        btnDelCellsControl = new javax.swing.JButton();
        comboCellControl = new javax.swing.JComboBox();
        lblCellsControl = new javax.swing.JLabel();
        lblColumnsKey = new javax.swing.JLabel();
        lblRowsKey = new javax.swing.JLabel();
        lblCellsDs = new javax.swing.JLabel();
        lblColumnsDs = new javax.swing.JLabel();
        lblCellValue = new javax.swing.JLabel();
        lblColumnsDisplayField = new javax.swing.JLabel();
        pnlCellsDs = new javax.swing.JPanel();
        txtCellsDs = new javax.swing.JTextField();
        pnlCellsDsButtons = new javax.swing.JPanel();
        btnDelCellsDs = new javax.swing.JButton();
        btnSelectCellsDs = new javax.swing.JButton();
        pnlRowsDsKeyField = new javax.swing.JPanel();
        txtRowsDsKeyField = new javax.swing.JTextField();
        pnlRowsDsKeyFieldButtons = new javax.swing.JPanel();
        btnDelRowsDsKeyField = new javax.swing.JButton();
        btnSelectRowsDsKeyField = new javax.swing.JButton();
        pnlColumnsDsKeyField = new javax.swing.JPanel();
        txtColumnsDsKeyField = new javax.swing.JTextField();
        pnlColumnsDsKeyFieldButtons = new javax.swing.JPanel();
        btnDelColumnsDsKeyField = new javax.swing.JButton();
        btnSelectColumnsDsKeyField = new javax.swing.JButton();
        pnlCellsDsValueField = new javax.swing.JPanel();
        txtCellsDsValueField = new javax.swing.JTextField();
        pnlCellsDsValueFieldButtons = new javax.swing.JPanel();
        btnDelCellsDsValueField = new javax.swing.JButton();
        btnSelectCellsDsValueField = new javax.swing.JButton();
        pnlColumnsDs = new javax.swing.JPanel();
        txtColumnsDs = new javax.swing.JTextField();
        pnlColumnsDsButtons = new javax.swing.JPanel();
        btnDelColumnsDs = new javax.swing.JButton();
        btnSelectColumnsDs = new javax.swing.JButton();
        pnlColumnsDisplayField = new javax.swing.JPanel();
        txtColumnsDisplayField = new javax.swing.JTextField();
        pnlColumnsDisplayFieldButtons = new javax.swing.JPanel();
        btnDelColumnsDisplayField = new javax.swing.JButton();
        btnSelectColumnsDisplayField = new javax.swing.JButton();
        chkSubstitute = new javax.swing.JCheckBox();
        chkFliped = new javax.swing.JCheckBox();
        pnlSelectFunction = new javax.swing.JPanel();
        btnDelSelectFunction = new javax.swing.JButton();
        comboSelectFunction = new javax.swing.JComboBox();
        lblValueSelectHandler = new javax.swing.JLabel();
        pnlCellFunction = new javax.swing.JPanel();
        btnDelCellFunction = new javax.swing.JButton();
        comboCellFunction = new javax.swing.JComboBox();
        lblCellHandler = new javax.swing.JLabel();
        chkColumnSelectOnly = new javax.swing.JCheckBox();
        pnlTree = new javax.swing.JPanel();
        radioSingleDs = new javax.swing.JRadioButton();
        radioAddQueries = new javax.swing.JRadioButton();
        radioScriptFunction = new javax.swing.JRadioButton();
        pnlTreeParentDmField = new javax.swing.JPanel();
        txtTreeParentDmField = new javax.swing.JTextField();
        pnlTreeParentFieldButtons = new javax.swing.JPanel();
        btnDelTreeParentField = new javax.swing.JButton();
        btnSelectTreeParentField = new javax.swing.JButton();
        pnlLinkFunction = new javax.swing.JPanel();
        btnDelLinkFunction = new javax.swing.JButton();
        comboLinkFunction = new javax.swing.JComboBox();
        pnlField2Param = new javax.swing.JPanel();
        pnlDsParam = new javax.swing.JPanel();
        btnDelDsParam = new javax.swing.JButton();
        comboDsParam = new javax.swing.JComboBox();
        pnlDmFieldParamSource = new javax.swing.JPanel();
        txtFieldParamSource = new javax.swing.JTextField();
        pnlFieldParamSourceButtons = new javax.swing.JPanel();
        btnDelFieldParamSource = new javax.swing.JButton();
        btnSelectFieldParamSource = new javax.swing.JButton();
        lblEqualsSign = new javax.swing.JLabel();
        lblUnaryLink = new javax.swing.JLabel();
        lblChildrenStyle = new javax.swing.JLabel();

        popupColumnsTree.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                popupColumnsTreePopupMenuWillBecomeVisible(evt);
            }
        });

        mnuAddColumn.setAction(getActionMap().get(DbGridColumnsAdd.class.getSimpleName()));
        popupColumnsTree.add(mnuAddColumn);

        mnuAddInNode.setAction(getActionMap().get(DbGridColumnsAddInNode.class.getSimpleName()));
        popupColumnsTree.add(mnuAddInNode);

        mnuDelColumn.setAction(getActionMap().get(DbGridColumnsDelete.class.getSimpleName()));
        popupColumnsTree.add(mnuDelColumn);
        popupColumnsTree.add(sepClipboard);

        mnuCut.setAction(getActionMap().get(DbGridColumnsCut.class.getSimpleName()));
        popupColumnsTree.add(mnuCut);

        mnuCopy.setAction(getActionMap().get(DbGridColumnsCopy.class.getSimpleName()));
        popupColumnsTree.add(mnuCopy);

        mnuPaste.setAction(getActionMap().get(DbGridColumnsPaste.class.getSimpleName()));
        popupColumnsTree.add(mnuPaste);
        popupColumnsTree.add(sepMoves);

        mnuUp.setAction(getActionMap().get(DbGridColumnsMoveUp.class.getSimpleName()));
        popupColumnsTree.add(mnuUp);

        mnuDown.setAction(getActionMap().get(DbGridColumnsMoveDown.class.getSimpleName()));
        popupColumnsTree.add(mnuDown);
        popupColumnsTree.add(sepService);

        mnuFill.setAction(getActionMap().get(DbGridColumnsFillAction.class.getSimpleName()));
        popupColumnsTree.add(mnuFill);

        lblDmDatasource.setText(DbControlsDesignUtils.getLocalizedString("dmDs")); // NOI18N

        pnlDmDatasource.setLayout(new java.awt.BorderLayout());

        txtDatamodelDs.setEditable(false);
        txtDatamodelDs.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlDmDatasource.add(txtDatamodelDs, java.awt.BorderLayout.CENTER);

        pnlDsButtons.setLayout(new java.awt.BorderLayout());

        btnDelDs.setAction(getActionMap().get(DbGridRowsDatasourceClearAction.class.getSimpleName() ));
        btnDelDs.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlDsButtons.add(btnDelDs, java.awt.BorderLayout.CENTER);

        btnSelectDs.setAction(getActionMap().get(DbGridRowsDatasourceSelectAction.class.getSimpleName()));
        btnSelectDs.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlDsButtons.add(btnSelectDs, java.awt.BorderLayout.WEST);

        pnlDmDatasource.add(pnlDsButtons, java.awt.BorderLayout.EAST);

        lblFixedCols.setText(DbControlsDesignUtils.getLocalizedString("lblFixedCols")); // NOI18N

        spinFixedCols.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        spinFixedCols.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinFixedColsStateChanged(evt);
            }
        });

        lblFixedRows.setText(DbControlsDesignUtils.getLocalizedString("lblFixedRows")); // NOI18N

        spinFixedRows.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        spinFixedRows.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinFixedRowsStateChanged(evt);
            }
        });

        comboRowHeaderType.setAction(getActionMap().get(DbGridRowsHeaderTypeChangeAction.class.getSimpleName() ));

        lblRowsHeaderType.setText(DbControlsDesignUtils.getLocalizedString("lblRowsHeaderType")); // NOI18N

        lblCellHandler1.setText(DbControlsDesignUtils.getLocalizedString("lblCellHandler")); // NOI18N

        pnlGeneralCellFunction.setLayout(new java.awt.BorderLayout());

        btnDelGeneralCellFunction.setAction(getActionMap().get(DbGridGeneralCellScriptClearAction.class.getSimpleName() ));
        btnDelGeneralCellFunction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlGeneralCellFunction.add(btnDelGeneralCellFunction, java.awt.BorderLayout.EAST);

        comboGeneralCellFunction.setEditable(true);
        comboGeneralCellFunction.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " " }));
        comboGeneralCellFunction.setAction(getActionMap().get(DbGridGeneralCellScriptChangeAction.class.getSimpleName()));
        pnlGeneralCellFunction.add(comboGeneralCellFunction, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout pnlGenericLayout = new javax.swing.GroupLayout(pnlGeneric);
        pnlGeneric.setLayout(pnlGenericLayout);
        pnlGenericLayout.setHorizontalGroup(
            pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGenericLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGenericLayout.createSequentialGroup()
                        .addComponent(pnlGeneralCellFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(pnlGenericLayout.createSequentialGroup()
                        .addGroup(pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spinFixedCols, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFixedCols))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spinFixedRows, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFixedRows))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRowsHeaderType)
                            .addComponent(comboRowHeaderType, 0, 382, Short.MAX_VALUE))
                        .addGap(11, 11, 11))
                    .addGroup(pnlGenericLayout.createSequentialGroup()
                        .addGroup(pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDmDatasource)
                            .addComponent(lblCellHandler1))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlGenericLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pnlDmDatasource, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        pnlGenericLayout.setVerticalGroup(
            pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGenericLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDmDatasource)
                .addGap(39, 39, 39)
                .addGroup(pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblFixedRows)
                        .addComponent(lblRowsHeaderType))
                    .addComponent(lblFixedCols))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinFixedCols, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinFixedRows, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboRowHeaderType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblCellHandler1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlGeneralCellFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(344, Short.MAX_VALUE))
            .addGroup(pnlGenericLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlGenericLayout.createSequentialGroup()
                    .addGap(29, 29, 29)
                    .addComponent(pnlDmDatasource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(457, Short.MAX_VALUE)))
        );

        tabsGridCustomizer.addTab(DbControlsDesignUtils.getLocalizedString("general"), pnlGeneric); // NOI18N

        splitColumns.setDividerLocation(230);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treeColumns.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeColumns.setToolTipText(DbControlsDesignUtils.getLocalizedString("treeColumnsHint")); // NOI18N
        treeColumns.setAutoscrolls(true);
        treeColumns.setComponentPopupMenu(popupColumnsTree);
        treeColumns.setEditable(true);
        treeColumns.setRootVisible(false);
        treeColumns.setShowsRootHandles(true);
        scrollColumns.setViewportView(treeColumns);

        splitColumns.setLeftComponent(scrollColumns);

        lblHeaderFont.setText(DbControlsDesignUtils.getLocalizedString("selectHeaderFont")); // NOI18N

        pnlColHeaderFont.setLayout(new java.awt.BorderLayout());

        txtColHeaderFont.setEditable(false);
        txtColHeaderFont.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlColHeaderFont.add(txtColHeaderFont, java.awt.BorderLayout.CENTER);

        pnlColHeaderFontButtons.setLayout(new java.awt.BorderLayout());

        btnDelColHeaderFont.setAction(getActionMap().get(DbGridColumnHeaderFontClearAction.class.getSimpleName()));
        btnDelColHeaderFont.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColHeaderFontButtons.add(btnDelColHeaderFont, java.awt.BorderLayout.CENTER);

        btnColHeaderFont.setAction(getActionMap().get(DbGridColumnHeaderFontChangeAction.class.getSimpleName()));
        btnColHeaderFont.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColHeaderFontButtons.add(btnColHeaderFont, java.awt.BorderLayout.WEST);

        pnlColHeaderFont.add(pnlColHeaderFontButtons, java.awt.BorderLayout.EAST);

        spinWidth.setModel(new javax.swing.SpinnerNumberModel(50, 10, 10000, 1));
        spinWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinWidthStateChanged(evt);
            }
        });

        chkEnabled.setAction(getActionMap().get(DbGridColumnEnabledChangeAction.class.getSimpleName()));
        chkEnabled.setText(DbControlsDesignUtils.getLocalizedString("colEnabled")); // NOI18N

        txtName.setAction(getActionMap().get(DbGridColumnNameChangeAction.class.getSimpleName()));

        lblColName.setText(DbControlsDesignUtils.getLocalizedString("colName")); // NOI18N

        lblWidth.setText(DbControlsDesignUtils.getLocalizedString("ColumnWidth")); // NOI18N

        chkVisible.setAction(getActionMap().get(DbGridColumnVisibleChangeAction.class.getSimpleName()));
        chkVisible.setText(DbControlsDesignUtils.getLocalizedString("chkVisible")); // NOI18N

        chkReadOnly.setAction(getActionMap().get(DbGridColumnReadonlyChangeAction.class.getSimpleName()));
        chkReadOnly.setText(DbControlsDesignUtils.getLocalizedString("colReadonly")); // NOI18N

        lblColumnRowsControl.setText(DbControlsDesignUtils.getLocalizedString("columnRowsControl")); // NOI18N

        pnlColRowsControl.setLayout(new java.awt.BorderLayout());

        btnDelColRowsControl.setAction(getActionMap().get(DbGridColumnControlClearAction.class.getSimpleName()));
        btnDelColRowsControl.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColRowsControl.add(btnDelColRowsControl, java.awt.BorderLayout.EAST);

        comboColumnControl.setAction(getActionMap().get(DbGridColumnControlChangeAction.class.getSimpleName()));
        pnlColRowsControl.add(comboColumnControl, java.awt.BorderLayout.CENTER);

        lblDmField.setText(DbControlsDesignUtils.getLocalizedString("dmField")); // NOI18N

        pnlDmField.setLayout(new java.awt.BorderLayout());

        txtDatamodelField.setEditable(false);
        txtDatamodelField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlDmField.add(txtDatamodelField, java.awt.BorderLayout.CENTER);

        pnlFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelField.setAction(getActionMap().get(DbGridColumnFieldClearAction.class.getSimpleName()));
        btnDelField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlFieldButtons.add(btnDelField, java.awt.BorderLayout.CENTER);

        btnSelectField.setAction(getActionMap().get(DbGridColumnFieldChangeAction.class.getSimpleName()));
        btnSelectField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlFieldButtons.add(btnSelectField, java.awt.BorderLayout.WEST);

        pnlDmField.add(pnlFieldButtons, java.awt.BorderLayout.EAST);

        pnlColRowsControlCustomizer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlColRowsControlCustomizer.setLayout(new java.awt.BorderLayout());

        lblAggregates.setText(DbControlsDesignUtils.getLocalizedString("lblAggregates")); // NOI18N

        pnlColAggregate.setLayout(new java.awt.BorderLayout());

        txtColAggregate.setEditable(false);
        txtColAggregate.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlColAggregate.add(txtColAggregate, java.awt.BorderLayout.CENTER);

        pnlColAggregateButtons.setLayout(new java.awt.BorderLayout());

        btnDelColAggregate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColAggregateButtons.add(btnDelColAggregate, java.awt.BorderLayout.CENTER);

        btnSelectColAggregate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColAggregateButtons.add(btnSelectColAggregate, java.awt.BorderLayout.WEST);

        pnlColAggregate.add(pnlColAggregateButtons, java.awt.BorderLayout.EAST);

        javax.swing.GroupLayout pnlPlainLayout = new javax.swing.GroupLayout(pnlPlain);
        pnlPlain.setLayout(pnlPlainLayout);
        pnlPlainLayout.setHorizontalGroup(
            pnlPlainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPlainLayout.createSequentialGroup()
                .addGroup(pnlPlainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblDmField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblColumnRowsControl, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAggregates, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(205, Short.MAX_VALUE))
            .addComponent(pnlDmField, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .addComponent(pnlColRowsControl, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .addComponent(pnlColRowsControlCustomizer, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .addComponent(pnlColAggregate, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
        );
        pnlPlainLayout.setVerticalGroup(
            pnlPlainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPlainLayout.createSequentialGroup()
                .addComponent(lblDmField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDmField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(lblColumnRowsControl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlColRowsControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlColRowsControlCustomizer, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAggregates)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlColAggregate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabsFliped.addTab(DbControlsDesignUtils.getLocalizedString("radioPlainColumn"), pnlPlain); // NOI18N

        pnlCellsControlCustomizer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlCellsControlCustomizer.setLayout(new java.awt.BorderLayout());

        pnlCellsControl.setLayout(new java.awt.BorderLayout());

        btnDelCellsControl.setAction(getActionMap().get(DbGridCellControlClearAction.class.getSimpleName()));
        btnDelCellsControl.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlCellsControl.add(btnDelCellsControl, java.awt.BorderLayout.EAST);

        comboCellControl.setAction(getActionMap().get(DbGridCellControlChangeAction.class.getSimpleName()));
        pnlCellsControl.add(comboCellControl, java.awt.BorderLayout.CENTER);

        lblCellsControl.setText(DbControlsDesignUtils.getLocalizedString("lblCellsControl")); // NOI18N

        lblColumnsKey.setText(DbControlsDesignUtils.getLocalizedString("lblColumnsKey")); // NOI18N

        lblRowsKey.setText(DbControlsDesignUtils.getLocalizedString("lblRowsKey")); // NOI18N

        lblCellsDs.setText(DbControlsDesignUtils.getLocalizedString("lblCellsDs")); // NOI18N

        lblColumnsDs.setText(DbControlsDesignUtils.getLocalizedString("lblColumnsDs")); // NOI18N

        lblCellValue.setText(DbControlsDesignUtils.getLocalizedString("lblCellValue")); // NOI18N

        lblColumnsDisplayField.setText(DbControlsDesignUtils.getLocalizedString("lblColumnsDisplayField")); // NOI18N

        pnlCellsDs.setLayout(new java.awt.BorderLayout());

        txtCellsDs.setEditable(false);
        txtCellsDs.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlCellsDs.add(txtCellsDs, java.awt.BorderLayout.CENTER);

        pnlCellsDsButtons.setLayout(new java.awt.BorderLayout());

        btnDelCellsDs.setAction(getActionMap().get(DbGridCellsDatasourceClearAction.class.getSimpleName()));
        btnDelCellsDs.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlCellsDsButtons.add(btnDelCellsDs, java.awt.BorderLayout.CENTER);

        btnSelectCellsDs.setAction(getActionMap().get(DbGridCellsDatasourceSelectAction.class.getSimpleName()));
        btnSelectCellsDs.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlCellsDsButtons.add(btnSelectCellsDs, java.awt.BorderLayout.WEST);

        pnlCellsDs.add(pnlCellsDsButtons, java.awt.BorderLayout.EAST);

        pnlRowsDsKeyField.setLayout(new java.awt.BorderLayout());

        txtRowsDsKeyField.setEditable(false);
        txtRowsDsKeyField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlRowsDsKeyField.add(txtRowsDsKeyField, java.awt.BorderLayout.CENTER);

        pnlRowsDsKeyFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelRowsDsKeyField.setAction(getActionMap().get(DbGridRowKeyClearAction.class.getSimpleName()));
        btnDelRowsDsKeyField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlRowsDsKeyFieldButtons.add(btnDelRowsDsKeyField, java.awt.BorderLayout.CENTER);

        btnSelectRowsDsKeyField.setAction(getActionMap().get(DbGridRowKeySelectAction.class.getSimpleName()));
        btnSelectRowsDsKeyField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlRowsDsKeyFieldButtons.add(btnSelectRowsDsKeyField, java.awt.BorderLayout.WEST);

        pnlRowsDsKeyField.add(pnlRowsDsKeyFieldButtons, java.awt.BorderLayout.EAST);

        pnlColumnsDsKeyField.setLayout(new java.awt.BorderLayout());

        txtColumnsDsKeyField.setEditable(false);
        txtColumnsDsKeyField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlColumnsDsKeyField.add(txtColumnsDsKeyField, java.awt.BorderLayout.CENTER);

        pnlColumnsDsKeyFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelColumnsDsKeyField.setAction(getActionMap().get(DbGridColKeyClearAction.class.getSimpleName()));
        btnDelColumnsDsKeyField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColumnsDsKeyFieldButtons.add(btnDelColumnsDsKeyField, java.awt.BorderLayout.CENTER);

        btnSelectColumnsDsKeyField.setAction(getActionMap().get(DbGridColKeySelectAction.class.getSimpleName()));
        btnSelectColumnsDsKeyField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColumnsDsKeyFieldButtons.add(btnSelectColumnsDsKeyField, java.awt.BorderLayout.WEST);

        pnlColumnsDsKeyField.add(pnlColumnsDsKeyFieldButtons, java.awt.BorderLayout.EAST);

        pnlCellsDsValueField.setLayout(new java.awt.BorderLayout());

        txtCellsDsValueField.setEditable(false);
        txtCellsDsValueField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlCellsDsValueField.add(txtCellsDsValueField, java.awt.BorderLayout.CENTER);

        pnlCellsDsValueFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelCellsDsValueField.setAction(getActionMap().get(DbGridCellsValueFieldClearAction.class.getSimpleName()));
        btnDelCellsDsValueField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlCellsDsValueFieldButtons.add(btnDelCellsDsValueField, java.awt.BorderLayout.CENTER);

        btnSelectCellsDsValueField.setAction(getActionMap().get(DbGridCellsValueFieldSelectAction.class.getSimpleName()));
        btnSelectCellsDsValueField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlCellsDsValueFieldButtons.add(btnSelectCellsDsValueField, java.awt.BorderLayout.WEST);

        pnlCellsDsValueField.add(pnlCellsDsValueFieldButtons, java.awt.BorderLayout.EAST);

        pnlColumnsDs.setLayout(new java.awt.BorderLayout());

        txtColumnsDs.setEditable(false);
        txtColumnsDs.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlColumnsDs.add(txtColumnsDs, java.awt.BorderLayout.CENTER);

        pnlColumnsDsButtons.setLayout(new java.awt.BorderLayout());

        btnDelColumnsDs.setAction(getActionMap().get(DbGridColumnsDatasourceClearAction.class.getSimpleName()));
        btnDelColumnsDs.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColumnsDsButtons.add(btnDelColumnsDs, java.awt.BorderLayout.CENTER);

        btnSelectColumnsDs.setAction(getActionMap().get(DbGridColumnsDatasourceSelectAction.class.getSimpleName()));
        btnSelectColumnsDs.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColumnsDsButtons.add(btnSelectColumnsDs, java.awt.BorderLayout.WEST);

        pnlColumnsDs.add(pnlColumnsDsButtons, java.awt.BorderLayout.EAST);

        pnlColumnsDisplayField.setLayout(new java.awt.BorderLayout());

        txtColumnsDisplayField.setEditable(false);
        txtColumnsDisplayField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlColumnsDisplayField.add(txtColumnsDisplayField, java.awt.BorderLayout.CENTER);

        pnlColumnsDisplayFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelColumnsDisplayField.setAction(getActionMap().get(DbGridColumnsDisplayFieldClearAction.class.getSimpleName()));
        btnDelColumnsDisplayField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColumnsDisplayFieldButtons.add(btnDelColumnsDisplayField, java.awt.BorderLayout.CENTER);

        btnSelectColumnsDisplayField.setAction(getActionMap().get(DbGridColumnsDisplayFieldSelectAction.class.getSimpleName()));
        btnSelectColumnsDisplayField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlColumnsDisplayFieldButtons.add(btnSelectColumnsDisplayField, java.awt.BorderLayout.WEST);

        pnlColumnsDisplayField.add(pnlColumnsDisplayFieldButtons, java.awt.BorderLayout.EAST);

        chkSubstitute.setAction(getActionMap().get(DbGridColumnSubstituteChangeAction.class.getSimpleName()));

        javax.swing.GroupLayout pnlFlipedLayout = new javax.swing.GroupLayout(pnlFliped);
        pnlFliped.setLayout(pnlFlipedLayout);
        pnlFlipedLayout.setHorizontalGroup(
            pnlFlipedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFlipedLayout.createSequentialGroup()
                .addComponent(lblColumnsDs, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkSubstitute)
                .addGap(67, 67, 67))
            .addComponent(pnlColumnsDs, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
            .addComponent(pnlColumnsDisplayField, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
            .addComponent(pnlCellsDs, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
            .addGroup(pnlFlipedLayout.createSequentialGroup()
                .addGroup(pnlFlipedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCellValue)
                    .addComponent(lblRowsKey)
                    .addComponent(lblCellsControl)
                    .addComponent(lblColumnsKey))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFlipedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlRowsDsKeyField, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                    .addComponent(pnlColumnsDsKeyField, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                    .addComponent(pnlCellsDsValueField, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                    .addComponent(pnlCellsControl, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)))
            .addComponent(pnlCellsControlCustomizer, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
            .addGroup(pnlFlipedLayout.createSequentialGroup()
                .addGroup(pnlFlipedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblColumnsDisplayField)
                    .addComponent(lblCellsDs))
                .addContainerGap())
        );
        pnlFlipedLayout.setVerticalGroup(
            pnlFlipedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFlipedLayout.createSequentialGroup()
                .addGroup(pnlFlipedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblColumnsDs)
                    .addComponent(chkSubstitute, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlColumnsDs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblColumnsDisplayField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlColumnsDisplayField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCellsDs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCellsDs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFlipedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRowsKey)
                    .addComponent(pnlRowsDsKeyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFlipedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlColumnsDsKeyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblColumnsKey))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFlipedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCellValue)
                    .addComponent(pnlCellsDsValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFlipedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCellsControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCellsControl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCellsControlCustomizer, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE))
        );

        scrollFliped.setViewportView(pnlFliped);

        tabsFliped.addTab(DbControlsDesignUtils.getLocalizedString("pnlFliped"), scrollFliped); // NOI18N

        chkFliped.setAction(getActionMap().get(DbGridColumnPlainChangeAction.class.getSimpleName()));
        chkFliped.setText(DbControlsDesignUtils.getLocalizedString("pnlFliped")); // NOI18N

        pnlSelectFunction.setLayout(new java.awt.BorderLayout());

        btnDelSelectFunction.setAction(getActionMap().get(DbGridColumnSelectScriptClearAction.class.getSimpleName()));
        btnDelSelectFunction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlSelectFunction.add(btnDelSelectFunction, java.awt.BorderLayout.EAST);

        comboSelectFunction.setEditable(true);
        comboSelectFunction.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " " }));
        comboSelectFunction.setAction(getActionMap().get(DbGridColumnSelectScriptChangeAction.class.getSimpleName()));
        pnlSelectFunction.add(comboSelectFunction, java.awt.BorderLayout.CENTER);

        lblValueSelectHandler.setText(DbControlsDesignUtils.getLocalizedString("lblSelectHandler")); // NOI18N

        pnlCellFunction.setLayout(new java.awt.BorderLayout());

        btnDelCellFunction.setAction(getActionMap().get(DbGridCellScriptClearAction.class.getSimpleName() ));
        btnDelCellFunction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlCellFunction.add(btnDelCellFunction, java.awt.BorderLayout.EAST);

        comboCellFunction.setEditable(true);
        comboCellFunction.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " " }));
        comboCellFunction.setAction(getActionMap().get(DbGridCellScriptChangeAction.class.getSimpleName()));
        pnlCellFunction.add(comboCellFunction, java.awt.BorderLayout.CENTER);

        lblCellHandler.setText(DbControlsDesignUtils.getLocalizedString("lblCellHandler")); // NOI18N

        chkColumnSelectOnly.setAction(getActionMap().get(DbGridColumnSelectOnlyChangeAction.class.getSimpleName()));
        chkColumnSelectOnly.setText(DbControlsDesignUtils.getLocalizedString("chkSelectOnly")); // NOI18N

        javax.swing.GroupLayout pnlColumnLayout = new javax.swing.GroupLayout(pnlColumn);
        pnlColumn.setLayout(pnlColumnLayout);
        pnlColumnLayout.setHorizontalGroup(
            pnlColumnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtName)
            .addComponent(pnlColHeaderFont, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlSelectFunction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlCellFunction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabsFliped, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(pnlColumnLayout.createSequentialGroup()
                .addGroup(pnlColumnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlColumnLayout.createSequentialGroup()
                        .addGroup(pnlColumnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblWidth)
                            .addComponent(spinWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlColumnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkReadOnly)
                            .addComponent(chkColumnSelectOnly))
                        .addGap(8, 8, 8)
                        .addGroup(pnlColumnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkFliped)
                            .addGroup(pnlColumnLayout.createSequentialGroup()
                                .addComponent(chkVisible, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkEnabled))))
                    .addComponent(lblColName)
                    .addComponent(lblHeaderFont)
                    .addComponent(lblValueSelectHandler)
                    .addComponent(lblCellHandler))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        pnlColumnLayout.setVerticalGroup(
            pnlColumnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlColumnLayout.createSequentialGroup()
                .addComponent(lblColName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHeaderFont)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlColHeaderFont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(pnlColumnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlColumnLayout.createSequentialGroup()
                        .addComponent(lblWidth)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlColumnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkFliped)))
                    .addGroup(pnlColumnLayout.createSequentialGroup()
                        .addGroup(pnlColumnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkVisible, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkReadOnly, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkColumnSelectOnly)))
                .addGap(8, 8, 8)
                .addComponent(lblValueSelectHandler)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSelectFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCellHandler)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCellFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsFliped, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitColumns.setRightComponent(pnlColumn);

        tabsGridCustomizer.addTab(DbControlsDesignUtils.getLocalizedString("columns"), splitColumns); // NOI18N

        radioSingleDs.setAction(getActionMap().get(DbGridTreeKind1QueryAction.class.getSimpleName()));
        btnGroupTree.add(radioSingleDs);
        radioSingleDs.setSelected(true);
        radioSingleDs.setText(DbControlsDesignUtils.getLocalizedString("singleDs")); // NOI18N

        radioAddQueries.setAction(getActionMap().get(DbGridTreeKindAddQueriesAction.class.getSimpleName()));
        btnGroupTree.add(radioAddQueries);
        radioAddQueries.setText(DbControlsDesignUtils.getLocalizedString("additionalDs")); // NOI18N

        radioScriptFunction.setAction(getActionMap().get(DbGridTreeKindScriptAction.class.getSimpleName()));
        btnGroupTree.add(radioScriptFunction);
        radioScriptFunction.setText(DbControlsDesignUtils.getLocalizedString("scriptFunction")); // NOI18N

        pnlTreeParentDmField.setLayout(new java.awt.BorderLayout());

        txtTreeParentDmField.setEditable(false);
        txtTreeParentDmField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlTreeParentDmField.add(txtTreeParentDmField, java.awt.BorderLayout.CENTER);

        pnlTreeParentFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelTreeParentField.setAction(getActionMap().get(DbGridTreeUnaryLinkClearAction.class.getSimpleName()));
        btnDelTreeParentField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlTreeParentFieldButtons.add(btnDelTreeParentField, java.awt.BorderLayout.CENTER);

        btnSelectTreeParentField.setAction(getActionMap().get(DbGridTreeUnaryLinkSelectAction.class.getSimpleName()));
        btnSelectTreeParentField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlTreeParentFieldButtons.add(btnSelectTreeParentField, java.awt.BorderLayout.WEST);

        pnlTreeParentDmField.add(pnlTreeParentFieldButtons, java.awt.BorderLayout.EAST);

        pnlLinkFunction.setLayout(new java.awt.BorderLayout());

        btnDelLinkFunction.setAction(getActionMap().get(DbGridTreeScriptClearAction.class.getSimpleName()));
        btnDelLinkFunction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlLinkFunction.add(btnDelLinkFunction, java.awt.BorderLayout.EAST);

        comboLinkFunction.setEditable(true);
        comboLinkFunction.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", " ", " ", " ", " ", " ", " ", " ", " " }));
        comboLinkFunction.setAction(getActionMap().get(DbGridTreeScriptChangeAction.class.getSimpleName()));
        pnlLinkFunction.add(comboLinkFunction, java.awt.BorderLayout.CENTER);

        pnlField2Param.setLayout(new java.awt.GridLayout(1, 2));

        pnlDsParam.setLayout(new java.awt.BorderLayout());

        btnDelDsParam.setAction(getActionMap().get(DbGridTreeParamClearAction.class.getSimpleName()));
        btnDelDsParam.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlDsParam.add(btnDelDsParam, java.awt.BorderLayout.EAST);

        comboDsParam.setAction(getActionMap().get(DbGridTreeParamChangeAction.class.getSimpleName()));
        pnlDsParam.add(comboDsParam, java.awt.BorderLayout.CENTER);

        pnlField2Param.add(pnlDsParam);

        pnlDmFieldParamSource.setLayout(new java.awt.BorderLayout());

        txtFieldParamSource.setEditable(false);
        txtFieldParamSource.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlDmFieldParamSource.add(txtFieldParamSource, java.awt.BorderLayout.CENTER);

        pnlFieldParamSourceButtons.setLayout(new java.awt.BorderLayout());

        btnDelFieldParamSource.setAction(getActionMap().get(DbGridTreeParamSourceClearAction.class.getSimpleName()));
        btnDelFieldParamSource.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlFieldParamSourceButtons.add(btnDelFieldParamSource, java.awt.BorderLayout.CENTER);

        btnSelectFieldParamSource.setAction(getActionMap().get(DbGridTreeParamSourceSelectAction.class.getSimpleName()));
        btnSelectFieldParamSource.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlFieldParamSourceButtons.add(btnSelectFieldParamSource, java.awt.BorderLayout.WEST);

        pnlDmFieldParamSource.add(pnlFieldParamSourceButtons, java.awt.BorderLayout.EAST);

        lblEqualsSign.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblEqualsSign.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEqualsSign.setText("=");
        lblEqualsSign.setMinimumSize(new java.awt.Dimension(20, 20));
        lblEqualsSign.setPreferredSize(new java.awt.Dimension(20, 20));
        pnlDmFieldParamSource.add(lblEqualsSign, java.awt.BorderLayout.WEST);

        pnlField2Param.add(pnlDmFieldParamSource);

        lblUnaryLink.setForeground(new java.awt.Color(153, 0, 0));
        lblUnaryLink.setText(DbControlsDesignUtils.getLocalizedString("lblUnaryLink")); // NOI18N

        lblChildrenStyle.setForeground(new java.awt.Color(153, 0, 0));
        lblChildrenStyle.setText(DbControlsDesignUtils.getLocalizedString("lblChildrenStyle")); // NOI18N

        javax.swing.GroupLayout pnlTreeLayout = new javax.swing.GroupLayout(pnlTree);
        pnlTree.setLayout(pnlTreeLayout);
        pnlTreeLayout.setHorizontalGroup(
            pnlTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTreeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioSingleDs, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                    .addComponent(pnlLinkFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                    .addComponent(radioScriptFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                    .addComponent(pnlField2Param, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                    .addComponent(radioAddQueries, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                    .addComponent(pnlTreeParentDmField, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                    .addComponent(lblUnaryLink)
                    .addComponent(lblChildrenStyle))
                .addContainerGap())
        );
        pnlTreeLayout.setVerticalGroup(
            pnlTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTreeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUnaryLink)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTreeParentDmField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblChildrenStyle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(radioSingleDs)
                .addGap(7, 7, 7)
                .addComponent(radioAddQueries)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlField2Param, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(radioScriptFunction)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlLinkFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(288, Short.MAX_VALUE))
        );

        tabsGridCustomizer.addTab(DbControlsDesignUtils.getLocalizedString("tree"), pnlTree); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsGridCustomizer, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsGridCustomizer)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void spinFixedColsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinFixedColsStateChanged
        Action action = getActionMap().get(DbGridFixedColumnsChangeAction.class.getSimpleName());
        if (action != null && action.isEnabled()) {
            action.actionPerformed(null);
        }
    }//GEN-LAST:event_spinFixedColsStateChanged

    private void spinFixedRowsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinFixedRowsStateChanged
        Action action = getActionMap().get(DbGridFixedRowsChangeAction.class.getSimpleName());
        if (action != null && action.isEnabled()) {
            action.actionPerformed(null);
        }
    }//GEN-LAST:event_spinFixedRowsStateChanged

    private void spinWidthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinWidthStateChanged
        Action action = getActionMap().get(DbGridColumnWidthChangeAction.class.getSimpleName());
        if (action != null && action.isEnabled()) {
            action.actionPerformed(null);
        }
    }//GEN-LAST:event_spinWidthStateChanged

    private void popupColumnsTreePopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_popupColumnsTreePopupMenuWillBecomeVisible
    }//GEN-LAST:event_popupColumnsTreePopupMenuWillBecomeVisible
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnColHeaderFont;
    private javax.swing.JButton btnDelCellFunction;
    private javax.swing.JButton btnDelCellsControl;
    private javax.swing.JButton btnDelCellsDs;
    private javax.swing.JButton btnDelCellsDsValueField;
    private javax.swing.JButton btnDelColAggregate;
    private javax.swing.JButton btnDelColHeaderFont;
    private javax.swing.JButton btnDelColRowsControl;
    private javax.swing.JButton btnDelColumnsDisplayField;
    private javax.swing.JButton btnDelColumnsDs;
    private javax.swing.JButton btnDelColumnsDsKeyField;
    private javax.swing.JButton btnDelDs;
    private javax.swing.JButton btnDelDsParam;
    private javax.swing.JButton btnDelField;
    private javax.swing.JButton btnDelFieldParamSource;
    private javax.swing.JButton btnDelGeneralCellFunction;
    private javax.swing.JButton btnDelLinkFunction;
    private javax.swing.JButton btnDelRowsDsKeyField;
    private javax.swing.JButton btnDelSelectFunction;
    private javax.swing.JButton btnDelTreeParentField;
    private javax.swing.ButtonGroup btnGroupTree;
    private javax.swing.JButton btnSelectCellsDs;
    private javax.swing.JButton btnSelectCellsDsValueField;
    private javax.swing.JButton btnSelectColAggregate;
    private javax.swing.JButton btnSelectColumnsDisplayField;
    private javax.swing.JButton btnSelectColumnsDs;
    private javax.swing.JButton btnSelectColumnsDsKeyField;
    private javax.swing.JButton btnSelectDs;
    private javax.swing.JButton btnSelectField;
    private javax.swing.JButton btnSelectFieldParamSource;
    private javax.swing.JButton btnSelectRowsDsKeyField;
    private javax.swing.JButton btnSelectTreeParentField;
    private javax.swing.JCheckBox chkColumnSelectOnly;
    private javax.swing.JCheckBox chkEnabled;
    private javax.swing.JCheckBox chkFliped;
    private javax.swing.JCheckBox chkReadOnly;
    private javax.swing.JCheckBox chkSubstitute;
    private javax.swing.JCheckBox chkVisible;
    private javax.swing.JComboBox comboCellControl;
    private javax.swing.JComboBox comboCellFunction;
    private javax.swing.JComboBox comboColumnControl;
    private javax.swing.JComboBox comboDsParam;
    private javax.swing.JComboBox comboGeneralCellFunction;
    private javax.swing.JComboBox comboLinkFunction;
    private javax.swing.JComboBox comboRowHeaderType;
    private javax.swing.JComboBox comboSelectFunction;
    private javax.swing.JLabel lblAggregates;
    private javax.swing.JLabel lblCellHandler;
    private javax.swing.JLabel lblCellHandler1;
    private javax.swing.JLabel lblCellValue;
    private javax.swing.JLabel lblCellsControl;
    private javax.swing.JLabel lblCellsDs;
    private javax.swing.JLabel lblChildrenStyle;
    private javax.swing.JLabel lblColName;
    private javax.swing.JLabel lblColumnRowsControl;
    private javax.swing.JLabel lblColumnsDisplayField;
    private javax.swing.JLabel lblColumnsDs;
    private javax.swing.JLabel lblColumnsKey;
    private javax.swing.JLabel lblDmDatasource;
    private javax.swing.JLabel lblDmField;
    private javax.swing.JLabel lblEqualsSign;
    private javax.swing.JLabel lblFixedCols;
    private javax.swing.JLabel lblFixedRows;
    private javax.swing.JLabel lblHeaderFont;
    private javax.swing.JLabel lblRowsHeaderType;
    private javax.swing.JLabel lblRowsKey;
    private javax.swing.JLabel lblUnaryLink;
    private javax.swing.JLabel lblValueSelectHandler;
    private javax.swing.JLabel lblWidth;
    private javax.swing.JMenuItem mnuAddColumn;
    private javax.swing.JMenuItem mnuAddInNode;
    private javax.swing.JMenuItem mnuCopy;
    private javax.swing.JMenuItem mnuCut;
    private javax.swing.JMenuItem mnuDelColumn;
    private javax.swing.JMenuItem mnuDown;
    private javax.swing.JMenuItem mnuFill;
    private javax.swing.JMenuItem mnuPaste;
    private javax.swing.JMenuItem mnuUp;
    private javax.swing.JPanel pnlCellFunction;
    private javax.swing.JPanel pnlCellsControl;
    private javax.swing.JPanel pnlCellsControlCustomizer;
    private javax.swing.JPanel pnlCellsDs;
    private javax.swing.JPanel pnlCellsDsButtons;
    private javax.swing.JPanel pnlCellsDsValueField;
    private javax.swing.JPanel pnlCellsDsValueFieldButtons;
    private javax.swing.JPanel pnlColAggregate;
    private javax.swing.JPanel pnlColAggregateButtons;
    private javax.swing.JPanel pnlColHeaderFont;
    private javax.swing.JPanel pnlColHeaderFontButtons;
    private javax.swing.JPanel pnlColRowsControl;
    private javax.swing.JPanel pnlColRowsControlCustomizer;
    private javax.swing.JPanel pnlColumn;
    private javax.swing.JPanel pnlColumnsDisplayField;
    private javax.swing.JPanel pnlColumnsDisplayFieldButtons;
    private javax.swing.JPanel pnlColumnsDs;
    private javax.swing.JPanel pnlColumnsDsButtons;
    private javax.swing.JPanel pnlColumnsDsKeyField;
    private javax.swing.JPanel pnlColumnsDsKeyFieldButtons;
    private javax.swing.JPanel pnlDmDatasource;
    private javax.swing.JPanel pnlDmField;
    private javax.swing.JPanel pnlDmFieldParamSource;
    private javax.swing.JPanel pnlDsButtons;
    private javax.swing.JPanel pnlDsParam;
    private javax.swing.JPanel pnlField2Param;
    private javax.swing.JPanel pnlFieldButtons;
    private javax.swing.JPanel pnlFieldParamSourceButtons;
    private javax.swing.JPanel pnlFliped;
    private javax.swing.JPanel pnlGeneralCellFunction;
    private javax.swing.JPanel pnlGeneric;
    private javax.swing.JPanel pnlLinkFunction;
    private javax.swing.JPanel pnlPlain;
    private javax.swing.JPanel pnlRowsDsKeyField;
    private javax.swing.JPanel pnlRowsDsKeyFieldButtons;
    private javax.swing.JPanel pnlSelectFunction;
    private javax.swing.JPanel pnlTree;
    private javax.swing.JPanel pnlTreeParentDmField;
    private javax.swing.JPanel pnlTreeParentFieldButtons;
    private javax.swing.JPopupMenu popupColumnsTree;
    private javax.swing.JRadioButton radioAddQueries;
    private javax.swing.JRadioButton radioScriptFunction;
    private javax.swing.JRadioButton radioSingleDs;
    private javax.swing.JScrollPane scrollColumns;
    private javax.swing.JScrollPane scrollFliped;
    private javax.swing.JSeparator sepClipboard;
    private javax.swing.JSeparator sepMoves;
    private javax.swing.JSeparator sepService;
    private javax.swing.JSpinner spinFixedCols;
    private javax.swing.JSpinner spinFixedRows;
    private javax.swing.JSpinner spinWidth;
    private javax.swing.JSplitPane splitColumns;
    private javax.swing.JTabbedPane tabsFliped;
    private javax.swing.JTabbedPane tabsGridCustomizer;
    public javax.swing.JTree treeColumns;
    private javax.swing.JTextField txtCellsDs;
    private javax.swing.JTextField txtCellsDsValueField;
    private javax.swing.JTextField txtColAggregate;
    private javax.swing.JTextField txtColHeaderFont;
    private javax.swing.JTextField txtColumnsDisplayField;
    private javax.swing.JTextField txtColumnsDs;
    private javax.swing.JTextField txtColumnsDsKeyField;
    private javax.swing.JTextField txtDatamodelDs;
    private javax.swing.JTextField txtDatamodelField;
    private javax.swing.JTextField txtFieldParamSource;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtRowsDsKeyField;
    private javax.swing.JTextField txtTreeParentDmField;
    // End of variables declaration//GEN-END:variables
}
