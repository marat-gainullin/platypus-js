/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid;

import com.bearsoft.gui.grid.columns.ConstrainedColumnModel;
import com.bearsoft.gui.grid.constraints.LinearConstraint;
import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.editing.InsettedTreeEditor;
import com.bearsoft.gui.grid.header.GridColumnsGroup;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import com.bearsoft.gui.grid.header.cell.HeaderCell;
import com.bearsoft.gui.grid.rendering.InsettedTreeRenderer;
import com.bearsoft.gui.grid.rendering.TreeColumnLeadingComponent;
import com.bearsoft.gui.grid.rows.ConstrainedRowSorter;
import com.bearsoft.gui.grid.rows.TabularRowsSorter;
import com.bearsoft.gui.grid.rows.TreedRowsSorter;
import com.bearsoft.gui.grid.selection.ConstrainedListSelectionModel;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.locators.RowWrap;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.ModelEntityParameterRef;
import com.eas.client.model.ModelEntityRef;
import com.eas.client.model.ParametersRowset;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.script.RowHostObject;
import com.eas.client.model.script.RowsetHostObject;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.dbcontrols.*;
import com.eas.dbcontrols.combo.DbCombo;
import com.eas.dbcontrols.date.DbDate;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.grid.rt.*;
import com.eas.dbcontrols.grid.rt.columns.ScriptableColumn;
import com.eas.dbcontrols.grid.rt.columns.model.FieldModelColumn;
import com.eas.dbcontrols.grid.rt.columns.model.RowModelColumn;
import com.eas.dbcontrols.grid.rt.columns.view.AnchorTableColumn;
import com.eas.dbcontrols.grid.rt.columns.view.RowHeaderTableColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsModel;
import com.eas.dbcontrols.grid.rt.models.RowsetsTableModel;
import com.eas.dbcontrols.grid.rt.models.RowsetsTreedModel;
import com.eas.dbcontrols.grid.rt.rowheader.FixedDbGridColumn;
import com.eas.dbcontrols.grid.rt.rowheader.RowHeaderCellEditor;
import com.eas.dbcontrols.grid.rt.rowheader.RowHeaderCellRenderer;
import com.eas.dbcontrols.grid.rt.veers.ColumnsRiddler;
import com.eas.dbcontrols.grid.rt.veers.ColumnsSource;
import com.eas.dbcontrols.grid.rt.veers.VeerColumnsHandler;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.gui.CascadedStyle;
import com.eas.script.NativeJavaHostObject;
import com.eas.script.ScriptUtils;
import com.eas.util.StringUtils;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public class DbGrid extends JPanel implements RowsetDbControl, TablesGridContainer {

    @Override
    public void beginUpdate() {
    }

    @Override
    public boolean isUpdating() {
        return false;
    }

    @Override
    public void endUpdate() {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            if (evt != null && evt.getSource() == model
                    && "runtime".equals(evt.getPropertyName())) {
                if (Boolean.FALSE.equals(evt.getOldValue())
                        && Boolean.TRUE.equals(evt.getNewValue())) {
                    configure();
                } else {
                    cleanup();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void applyComponentPopupMenu() {
        if (tlTable != null) {
            tlTable.setComponentPopupMenu(getComponentPopupMenu());
        }
        if (trTable != null) {
            trTable.setComponentPopupMenu(getComponentPopupMenu());
        }
        if (blTable != null) {
            blTable.setComponentPopupMenu(getComponentPopupMenu());
        }
        if (brTable != null) {
            brTable.setComponentPopupMenu(getComponentPopupMenu());
        }
    }

    protected void applyGridColor() {
        if (getGridColor() != null) {
            if (tlTable != null) {
                tlTable.setGridColor(getGridColor());
            }
            if (trTable != null) {
                trTable.setGridColor(getGridColor());
            }
            if (blTable != null) {
                blTable.setGridColor(getGridColor());
            }
            if (brTable != null) {
                brTable.setGridColor(getGridColor());
            }
        }
    }

    protected void applyRowHeight() {
        if (tlTable != null) {
            tlTable.setRowHeight(getRowsHeight());
        }
        if (trTable != null) {
            trTable.setRowHeight(getRowsHeight());
        }
        if (blTable != null) {
            blTable.setRowHeight(getRowsHeight());
        }
        if (brTable != null) {
            brTable.setRowHeight(getRowsHeight());
        }
    }

    protected void applyShowHorizontalLines() {
        if (tlTable != null) {
            tlTable.setShowHorizontalLines(isShowHorizontalLines());
        }
        if (trTable != null) {
            trTable.setShowHorizontalLines(isShowHorizontalLines());
        }
        if (blTable != null) {
            blTable.setShowHorizontalLines(isShowHorizontalLines());
        }
        if (brTable != null) {
            brTable.setShowHorizontalLines(isShowHorizontalLines());
        }
    }

    protected void applyShowVerticalLines() {
        if (tlTable != null) {
            tlTable.setShowVerticalLines(isShowVerticalLines());
        }
        if (trTable != null) {
            trTable.setShowVerticalLines(isShowVerticalLines());
        }
        if (blTable != null) {
            blTable.setShowVerticalLines(isShowVerticalLines());
        }
        if (brTable != null) {
            brTable.setShowVerticalLines(isShowVerticalLines());
        }
    }

    protected void applyToolTipText() {
        if (tlTable != null) {
            tlTable.setToolTipText(getToolTipText());
        }
        if (trTable != null) {
            trTable.setToolTipText(getToolTipText());
        }
        if (blTable != null) {
            blTable.setToolTipText(getToolTipText());
        }
        if (brTable != null) {
            brTable.setToolTipText(getToolTipText());
        }
    }

    protected Locator createPksLocator(Rowset colsRs) throws IllegalStateException {
        Locator colsLoc = colsRs.createLocator();
        colsLoc.beginConstrainting();
        try {
            List<Integer> pkIndicies = colsRs.getFields().getPrimaryKeysIndicies();
            for (Integer pkIdx : pkIndicies) {
                colsLoc.addConstraint(pkIdx);
            }
        } finally {
            colsLoc.endConstrainting();
        }
        return colsLoc;
    }

    /**
     * Returns index of a row in the model. Index is in model coordinates. Index
     * is 0-based.
     *
     * @param aRow Row to calculate index for.
     * @return Index if row.
     * @throws RowsetException
     */
    public int row2Index(Row aRow) throws RowsetException {
        int idx = -1;
        if (deepModel instanceof TableFront2TreedModel<?>) {
            TableFront2TreedModel<Row> front = (TableFront2TreedModel<Row>) deepModel;
            idx = front.getIndexOf(aRow);
        } else if (deepModel instanceof RowsetsTableModel) {
            RowsetsTableModel lmodel = (RowsetsTableModel) deepModel;
            Object[] keys = aRow.getPKValues();
            if (lmodel.getPkLocator().find(keys != null && keys.length > 1 ? new Object[]{keys[0]} : keys)) {
                RowWrap rw = lmodel.getPkLocator().getSubSet().get(0);
                idx = rw.getIndex() - 1;
            }
        }
        return idx;
    }

    /**
     * Returns row for particular Index. Index is in model's coordinates. Index
     * is 0-based.
     *
     * @param aIdx Index the row is to be calculated for.
     * @return Row's index;
     * @throws RowsetException
     */
    public Row index2Row(int aIdx) throws RowsetException {
        Row row = null;
        if (deepModel instanceof TableFront2TreedModel<?>) {
            TableFront2TreedModel<Row> front = (TableFront2TreedModel<Row>) deepModel;
            row = front.getElementAt(aIdx);
        } else if (deepModel instanceof RowsetsTableModel) {
            RowsetsTableModel lmodel = (RowsetsTableModel) deepModel;
            row = lmodel.getRowsRowset().getRow(aIdx + 1);
        }
        return row;
    }

    protected void putAction(Action aAction) {
        if (aAction != null) {
            tlTable.getActionMap().put(aAction.getClass().getName(), aAction);
            trTable.getActionMap().put(aAction.getClass().getName(), aAction);
            blTable.getActionMap().put(aAction.getClass().getName(), aAction);
            brTable.getActionMap().put(aAction.getClass().getName(), aAction);
        }
    }

    /**
     * @param aParent Columns group, with will be parentfor new groups.
     * @param aContents A list of used as a source for columns groups.
     * @param linkSource Wether to link new column groups with source
     * DbGridColumn-s. This also means, that LinkedGridColumnsGroup will be
     * created.
     * @return
     * @throws Exception
     */
    private Map<TableColumn, GridColumnsGroup> fillColumnsGroup(GridColumnsGroup aParent, List<DbGridColumn> aContents, boolean linkSource) throws Exception {
        Map<TableColumn, GridColumnsGroup> groups = new HashMap<>();
        for (DbGridColumn dCol : aContents) {
            // create grid columns group
            GridColumnsGroup group;
            if (linkSource) {
                group = new LinkedGridColumnsGroup(dCol);
            } else {
                group = new GridColumnsGroup();
            }
            dCol.initializeGridColumnsGroup(group);
            if (aParent != null) {
                group.setParent(aParent);
                aParent.addChild(group);
            }
            // Let's take care of structure
            if (dCol.hasChildren()) {
                Map<TableColumn, GridColumnsGroup> childGroups = fillColumnsGroup(group, dCol.getChildren(), linkSource);
                groups.putAll(childGroups);
            } else // Leaf group
            {
                if (dCol instanceof FixedDbGridColumn) { // Fixed
                    // View column setup
                    TableColumn tCol = new RowHeaderTableColumn(dCol.getWidth());
                    tCol.setCellRenderer(new RowHeaderCellRenderer(((FixedDbGridColumn) dCol).getHeaderType()));
                    tCol.setCellEditor(new RowHeaderCellEditor(((FixedDbGridColumn) dCol).getHeaderType()));
                    tCol.setMinWidth(dCol.getWidth());
                    tCol.setPreferredWidth(dCol.getWidth());
                    tCol.setMaxWidth(dCol.getWidth());
                    tCol.setHeaderValue("\\");
                    group.setTableColumn(tCol);
                    group.setMoveable(false);
                    group.setResizeable(false);
                    group.setSortable(false);
                    groups.put(tCol, group);
                    columnModel.addColumn(tCol);
                } else {
                    if (dCol.isPlain()) { // Plain
                        Rowset rs = DbControlsUtils.resolveRowset(model, dCol.getDatamodelElement());
                        int fidx = DbControlsUtils.resolveFieldIndex(model, dCol.getDatamodelElement());
                        if (fidx < 1) {
                            if (dCol.getDatamodelElement() != null) {
                                Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, "Bad column configuration: " + dCol.getName() + "'s model binding can't be resolved");
                            }
                        }
                        // Model column setup
                        FieldModelColumn mCol = new FieldModelColumn(rs, fidx, getHandler(dCol.getCellFunction()), getHandler(dCol.getSelectFunction()), group.isReadonly(), new HasStyle() {
                            @Override
                            public CascadedStyle getStyle() {
                                return style;
                            }
                        }, null, null);
                        rowsModel.addColumn(mCol);
                        // View column setup
                        TableColumn tCol = new TableColumn(rowsModel.getColumnCount() - 1);
                        tCol.setMinWidth(group.getMinWidth());
                        tCol.setMaxWidth(group.getMaxWidth());
                        tCol.setPreferredWidth(dCol.getWidth());
                        tCol.setWidth(dCol.getWidth());
                        if (dCol.getControlInfo() != null) {
                            TableCellRenderer cellRenderer = dCol.createCellRenderer();
                            tCol.setCellRenderer(cellRenderer);
                            if (cellRenderer instanceof ScalarDbControl) {
                                ((ScalarDbControl) cellRenderer).setModel(model);
                                mCol.setView((ScalarDbControl) cellRenderer);
                            }
                            TableCellEditor cellEditor = dCol.createCellEditor();
                            tCol.setCellEditor(cellEditor);
                            if (cellEditor instanceof ScalarDbControl) {
                                Field field = DbControlsUtils.resolveField(model, dCol.getDatamodelElement());
                                ((ScalarDbControl) cellEditor).setModel(model);
                                ((ScalarDbControl) cellEditor).extraCellControls(getHandler(dCol != null ? dCol.getSelectFunction() : null), field != null ? field.isNullable() : false);
                                mCol.setEditor((ScalarDbControl) cellEditor);
                            }
                        }
                        String title = group.getTitle();
                        if (title == null || title.isEmpty()) {
                            title = group.getName();
                        }
                        tCol.setHeaderValue(title);
                        // view - model link
                        tCol.setIdentifier(mCol);
                        // groups-view link
                        group.setTableColumn(tCol);
                        groups.put(tCol, group);
                        columnModel.addColumn(tCol);
                        scriptableColumns.add(new ScriptableColumn(dCol, mCol, tCol, columnModel.getColumnCount() - 1, columnModel, rowsModel, groups));
                    } else { // Veer
                        Rowset colsRs = DbControlsUtils.resolveRowset(model, dCol.getColumnsDatasource());
                        int colTitleColIdx = DbControlsUtils.resolveFieldIndex(model, dCol.getColumnsDisplayField());
                        Rowset cellsRs = DbControlsUtils.resolveRowset(model, dCol.getCellsDatasource());
                        int cellsRowKeyIdx = DbControlsUtils.resolveFieldIndex(model, dCol.getCellDesignInfo().getRowsKeyField());
                        int cellsColumKeyIdx = DbControlsUtils.resolveFieldIndex(model, dCol.getCellDesignInfo().getColumnsKeyField());
                        Rowset cellsValuesRs = DbControlsUtils.resolveRowset(model, dCol.getCellDesignInfo().getCellValueField());
                        int cellsValuesFieldIdx = DbControlsUtils.resolveFieldIndex(model, dCol.getCellDesignInfo().getCellValueField());
                        if (colsRs != null && cellsRs != null && cellsValuesRs != null
                                && colTitleColIdx != 0
                                && cellsRowKeyIdx != 0
                                && cellsColumKeyIdx != 0
                                && cellsValuesFieldIdx != 0) {
                            Locator colsLoc = createPksLocator(colsRs);
                            Locator cellsLoc = cellsRs.createLocator();
                            cellsLoc.beginConstrainting();
                            try {
                                cellsLoc.addConstraint(cellsRowKeyIdx);
                                cellsLoc.addConstraint(cellsColumKeyIdx);
                            } finally {
                                cellsLoc.endConstrainting();
                            }
                            AnchorTableColumn anchorCol = new AnchorTableColumn(0);
                            String title = group.getTitle();
                            if (title == null || title.isEmpty()) {
                                title = group.getName();
                            }
                            anchorCol.setHeaderValue(title);
                            anchorCol.setMinWidth(0);
                            anchorCol.setPreferredWidth(0);
                            anchorCol.setMaxWidth(0);
                            anchorCol.setWidth(0);
                            ColumnsSource cs = new ColumnsSource(group, anchorCol, colsLoc, colTitleColIdx, cellsLoc, cellsValuesRs, cellsValuesFieldIdx, new VeerColumnsHandler(model, dCol), getHandler(dCol.getCellFunction()), getHandler(dCol.getSelectFunction()));
                            anchorCol.setColumnsSource(cs);
                            //groups.put(anchorCol, group);
                            columnModel.addColumn(anchorCol);
                        }
                    }
                }
            }
        }
        return groups;
    }

    private void configureTreedView() {
        if (rowsModel instanceof RowsetsTreedModel) {
            if (columnModel.getColumnCount() > 0) {
                TableColumn tCol = null;
                // Let's find first stable column
                for (int i = 0; i < columnModel.getColumnCount(); i++) {
                    TableColumn col = columnModel.getColumn(i);
                    if (col.getIdentifier() instanceof FieldModelColumn) {
                        tCol = col;
                        break;
                    }
                }
                // If no such column, client code should worry about stability of the first model's column
                if (tCol == null) {
                    for (int i = 0; i < columnModel.getColumnCount(); i++) {
                        TableColumn col = columnModel.getColumn(i);
                        if (col.getIdentifier() instanceof RowModelColumn) {
                            tCol = col;
                            break;
                        }
                    }
                }
                assert tCol != null;
                tCol.setCellRenderer(new InsettedTreeRenderer<Row>(tCol.getCellRenderer(), new TreeColumnLeadingComponent<>(deepModel, style, false)));
                tCol.setCellEditor(new InsettedTreeEditor(tCol.getCellEditor(), new TreeColumnLeadingComponent<>(deepModel, style, true)));
            }
        }
    }

    private void applyOddRowsColor() {
        if (tlTable != null) {
            tlTable.setOddRowsColor(getOddRowsColor());
        }
        if (trTable != null) {
            trTable.setOddRowsColor(getOddRowsColor());
        }
        if (blTable != null) {
            blTable.setOddRowsColor(getOddRowsColor());
        }
        if (brTable != null) {
            brTable.setOddRowsColor(getOddRowsColor());
        }
    }

    private void applyShowOddRowsInOtherColor() {
        if (tlTable != null) {
            tlTable.setShowOddRowsInOtherColor(isShowOddRowsInOtherColor());
        }
        if (trTable != null) {
            trTable.setShowOddRowsInOtherColor(isShowOddRowsInOtherColor());
        }
        if (blTable != null) {
            blTable.setShowOddRowsInOtherColor(isShowOddRowsInOtherColor());
        }
        if (brTable != null) {
            brTable.setShowOddRowsInOtherColor(isShowOddRowsInOtherColor());
        }
    }

    private void applyEditable() {
        if (tlTable != null) {
            tlTable.setEditable(editable);
        }
        if (trTable != null) {
            trTable.setEditable(editable);
        }
        if (blTable != null) {
            blTable.setEditable(editable);
        }
        if (brTable != null) {
            brTable.setEditable(editable);
        }
    }

    private void applyEnabled() {
        if (tlTable != null) {
            tlTable.setEnabled(isEnabled());
        }
        if (trTable != null) {
            trTable.setEnabled(isEnabled());
        }
        if (blTable != null) {
            blTable.setEnabled(isEnabled());
        }
        if (brTable != null) {
            brTable.setEnabled(isEnabled());
        }
    }

    public JTable getTableByViewCell(int row, int column) {
        if (row >= 0 && row < tlTable.getRowCount()
                && column >= 0 && column < tlTable.getColumnCount()) {
            return tlTable;
        } else if (row >= 0 && row < trTable.getRowCount()
                && (column - tlTable.getColumnCount()) >= 0 && (column - tlTable.getColumnCount()) < trTable.getColumnCount()) {
            return trTable;
        } else if ((row - tlTable.getRowCount()) >= 0 && (row - tlTable.getRowCount()) < blTable.getRowCount()
                && column >= 0 && column < blTable.getColumnCount()) {
            return blTable;
        } else if ((row - tlTable.getRowCount()) >= 0 && (row - tlTable.getRowCount()) < brTable.getRowCount()
                && (column - tlTable.getColumnCount()) >= 0 && (column - tlTable.getColumnCount()) < brTable.getColumnCount()) {
            return brTable;
        } else {
            return null;
        }
    }

    private int convertFixedColumns2Leaves(List<DbGridColumn> roots, int fixedCols) {
        int leavesCount = 0;
        if (fixedCols > 0) {
            for (int i = 0; i < fixedCols; i++) {
                DbGridColumn col = roots.get(i);
                if (!col.hasChildren()) {
                    leavesCount++;
                } else {
                    leavesCount += convertFixedColumns2Leaves(col.getChildren(), col.getChildren().size());
                }
            }
        }
        return leavesCount;
    }

    public boolean isTreeConfigured() throws Exception {
        return unaryLinkField != null
                && unaryLinkField.getEntityId() != null
                && unaryLinkField.getField() != null
                && model.getEntityById(unaryLinkField.getEntityId()) != null
                && model.getEntityById(unaryLinkField.getEntityId()).getRowset() != null
                && model.getEntityById(unaryLinkField.getEntityId()).getRowset().getFields().find(unaryLinkField.getFieldName()) != -1;
    }
    public static final int CELLS_CACHE_SIZE = 65536;// 2^16
    public static final Color FIXED_COLOR = new Color(154, 204, 255);
    public static Icon processIcon = IconCache.getIcon("16x16/process-indicator.gif");
    protected TablesFocusManager tablesFocusManager = new TablesFocusManager();
    protected TablesMousePropagator tablesMousePropagator = new TablesMousePropagator();
    protected ApplicationEntity<?, ?, ?> rowsEntity;
    protected Scriptable scriptScope;
    protected Scriptable eventThis;
    // design
    protected List<DbGridColumn> header = new ArrayList<>();
    protected int rowsHeight = 20;
    protected boolean showVerticalLines = true;
    protected boolean showHorizontalLines = true;
    protected boolean showOddRowsInOtherColor = true;
    protected boolean editable = true;
    protected boolean insertable = true;
    protected boolean deletable = true;
    protected Color oddRowsColor;
    protected Color gridColor;
    protected CascadedStyle style = new CascadedStyle();
    // data
    protected RowsetsModel rowsModel;
    protected TableModel deepModel;
    protected TabularRowsSorter<? extends TableModel> rowSorter;
    protected ColumnsRiddler lriddler;
    protected ColumnsRiddler rriddler;
    protected Map<Rowset, List<ColumnsSource>> lcolumnsSources = new HashMap<>();
    protected Map<Rowset, List<ColumnsSource>> rcolumnsSources = new HashMap<>();
    protected Set<Row> processedRows = new HashSet<>();
    protected Function generalRowFunction;
    protected int rowsHeaderType = DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_USUAL;
    protected int fixedRows;
    protected int fixedColumns;
    protected ModelEntityRef rowsDatasource;
    protected ModelElementRef unaryLinkField;
    protected ModelEntityParameterRef param2GetChildren;
    protected ModelElementRef paramSourceField;
    // view
    protected TableColumnModel columnModel;
    // selection
    protected ListSelectionModel rowsSelectionModel;
    protected ListSelectionModel columnsSelectionModel;
    protected GeneralSelectionChangesReflector generalSelectionChangesReflector;
    // visual components
    protected JLabel progressIndicator;
    protected JScrollPane gridScroll;
    protected MultiLevelHeader lheader;
    protected MultiLevelHeader rheader;
    protected GridTable tlTable;
    protected GridTable trTable;
    protected GridTable blTable;
    protected GridTable brTable;
    protected RowsetListener scrollReflector = new RowsetAdapter() {
        protected void repaintRowHeader() {
            if (gridScroll.getRowHeader() != null) {
                gridScroll.getRowHeader().repaint();
            }
        }

        @Override
        public void rowsetScrolled(RowsetScrollEvent event) {
            repaintRowHeader();
        }

        @Override
        public void rowsetRolledback(RowsetRollbackEvent event) {
            repaintRowHeader();
        }

        @Override
        public void rowsetSaved(RowsetSaveEvent event) {
            repaintRowHeader();
        }
    };
    // script support
    protected List<ScriptableColumn> scriptableColumns = new ArrayList<>();
    // actions
    protected Action findSomethingAction;

    public DbGrid() {
        super();
        setupStyle();
        initializeDesign();
    }

    protected void setupStyle() {
        JTable tbl = new JTable();
        gridColor = tbl.getGridColor();
        style = new CascadedStyle(null);
        style.setAlign(SwingConstants.LEFT);
        style.setFont(DbControlsUtils.toFont(tbl.getFont()));
        Color uiColor = tbl.getBackground();
        style.setBackground(new Color(uiColor.getRed(), uiColor.getGreen(), uiColor.getBlue(), uiColor.getAlpha()));
        uiColor = tbl.getForeground();
        style.setForeground(new Color(uiColor.getRed(), uiColor.getGreen(), uiColor.getBlue(), uiColor.getAlpha()));
        style.setIconName(null);

        Object openedIcon = UIManager.get("Tree.openIcon");
        Object closedIcon = UIManager.get("Tree.closedIcon");
        Object leafIcon = UIManager.get("Tree.leafIcon");
        if (openedIcon instanceof Icon) {
            style.setOpenFolderIcon((Icon) openedIcon);
        } else {
            style.setOpenFolderIconName("folder-horizontal-open.png");
        }
        if (closedIcon instanceof Icon) {
            style.setFolderIcon((Icon) closedIcon);
        } else {
            style.setFolderIconName("folder-horizontal.png");
        }
        if (leafIcon instanceof Icon) {
            style.setLeafIcon((Icon) leafIcon);
        } else {
            style.setLeafIconName("status-offline.png");
        }
    }

    public void setShowHorizontalLines(boolean aValue) {
        showHorizontalLines = aValue;
        applyShowHorizontalLines();
    }

    public void setShowVerticalLines(boolean aValue) {
        showVerticalLines = aValue;
        applyShowVerticalLines();
    }

    @Designable(category = "appearance")
    public Color getOddRowsColor() {
        return oddRowsColor;
    }

    public void setOddRowsColor(Color aValue) {
        oddRowsColor = aValue;
        applyOddRowsColor();
    }

    @Designable(category = "appearance")
    public boolean isShowHorizontalLines() {
        return showHorizontalLines;
    }

    @Designable(category = "appearance")
    public boolean isShowVerticalLines() {
        return showVerticalLines;
    }

    @Designable(category = "appearance")
    public boolean isShowOddRowsInOtherColor() {
        return showOddRowsInOtherColor;
    }

    public void setShowOddRowsInOtherColor(boolean aValue) {
        showOddRowsInOtherColor = aValue;
        applyShowOddRowsInOtherColor();
    }

    @Designable(category = "appearance")
    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color aValue) {
        gridColor = aValue;
        applyGridColor();
    }

    @Designable(category = "appearance")
    public int getRowsHeight() {
        return rowsHeight;
    }

    public void setRowsHeight(int aValue) {
        if (aValue < 10) {
            aValue = 10;
        }
        if (aValue > 350) {
            aValue = 350;
        }
        rowsHeight = aValue;
        applyRowHeight();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        applyEnabled();
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
        applyEditable();
    }

    @Designable(category = "editing")
    public boolean isEditable() {
        return editable;
    }

    public void setInsertable(boolean aValue) {
        insertable = aValue;
    }

    @Designable(category = "editing")
    public boolean isInsertable() {
        return insertable;
    }

    public void setDeletable(boolean aValue) {
        deletable = aValue;
    }

    @Designable(category = "editing")
    public boolean isDeletable() {
        return deletable;
    }

    @Undesignable
    public Scriptable getEventThis() {
        return eventThis;
    }

    public void setEventThis(Scriptable aValue) {
        eventThis = aValue;
        if (rowsModel != null) {
            rowsModel.setScriptScope(eventThis);
        }
    }

    public GridTable getTopLeftTable() {
        return tlTable;
    }

    public GridTable getTopRightTable() {
        return trTable;
    }

    public GridTable getBottomLeftTable() {
        return blTable;
    }

    public GridTable getBottomRightTable() {
        return brTable;
    }

    public ListSelectionModel getRowsSelectionModel() {
        return rowsSelectionModel;
    }

    public ListSelectionModel getColumnsSelectionModel() {
        return columnsSelectionModel;
    }

    public Scriptable getSelected() throws Exception {
        List<Object> selectedRows = new ArrayList<>();
        if (deepModel != null) {// design time is only the case
            for (int i = 0; i < deepModel.getRowCount(); i++) {
                if (rowsSelectionModel.isSelectedIndex(i)) {
                    Row row = index2Row(rowSorter.convertRowIndexToModel(i));
                    RowHostObject rowFacade = RowHostObject.publishRow(model.getScriptScope(), row);
                    selectedRows.add(rowFacade);
                }
            }
        }
        return Context.getCurrentContext().newArray(model.getScriptScope(), selectedRows.toArray(new Object[]{}));
    }

    /*
     * public ScriptableSelection getSelection() throws Exception { List<Row>
     * selectedRows = new ArrayList<>(); if (deepModel != null) {// design time
     * is only the case for (int i = 0; i < deepModel.getRowCount(); i++) { if
     * (rowsSelectionModel.isSelectedIndex(i)) { Row row =
     * index2Row(rowSorter.convertRowIndexToModel(i)); selectedRows.add(row); }
     * } } return new ScriptableSelection(selectedRows); }
     *
     */
    @Override
    public void setComponentPopupMenu(JPopupMenu aPopup) {
        super.setComponentPopupMenu(aPopup);
        applyComponentPopupMenu();
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (style != null) {
            style.setBackground(bg);
        }
        applyBackground();
    }

    public void applyBackground() {
        if (tlTable != null) {
            tlTable.setBackground(getBackground());
        }
        if (trTable != null) {
            trTable.setBackground(getBackground());
        }
        if (blTable != null) {
            blTable.setBackground(getBackground());
        }
        if (brTable != null) {
            brTable.setBackground(getBackground());
        }
    }

    @Override
    public Color getBackground() {
        if (style != null) {
            return style.getBackground();
        } else {
            return super.getBackground();
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (style != null) {
            style.setForeground(fg);
        }
        applyForeground();
    }

    public void applyForeground() {
        if (tlTable != null) {
            tlTable.setForeground(getForeground());
        }
        if (trTable != null) {
            trTable.setForeground(getForeground());
        }
        if (blTable != null) {
            blTable.setForeground(getForeground());
        }
        if (brTable != null) {
            brTable.setForeground(getForeground());
        }
    }

    @Override
    public Color getForeground() {
        if (style != null) {
            return style.getForeground();
        } else {
            return super.getForeground();
        }
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (style != null) {
            style.setFont(DbControlsUtils.toFont(font));
        }
        applyFont();
    }

    public void applyFont() {
        if (tlTable != null) {
            tlTable.setFont(getFont());
        }
        if (trTable != null) {
            trTable.setFont(getFont());
        }
        if (blTable != null) {
            blTable.setFont(getFont());
        }
        if (brTable != null) {
            brTable.setFont(getFont());
        }
    }

    @Override
    public Font getFont() {
        if (style != null) {
            return DbControlsUtils.toNativeFont(style.getFont());
        } else {
            return super.getFont();
        }
    }

    @Override
    public void setToolTipText(String aValue) {
        if (aValue != null && aValue.isEmpty()) {
            aValue = null;
        }
        super.setToolTipText(aValue);
        applyToolTipText();
    }

    @Designable(category = "appearance")
    public int getFixedColumns() {
        return fixedColumns;
    }

    public void setFixedColumns(int aValue) {
        fixedColumns = aValue;
    }

    @Designable(category = "appearance")
    public int getFixedRows() {
        return fixedRows;
    }

    public void setFixedRows(int aValue) {
        fixedRows = aValue;
    }

    @Undesignable
    public List<DbGridColumn> getHeader() {
        return header;
    }

    public void setHeader(List<DbGridColumn> aValue) {
        header = aValue;
    }

    @Designable(displayName = "entity", category = "model")
    public ModelEntityRef getRowsDatasource() {
        return rowsDatasource;
    }

    public void setRowsDatasource(ModelEntityRef aValue) {
        rowsDatasource = aValue;
    }

    @Designable(category = "tree")
    public ModelElementRef getUnaryLinkField() {
        return unaryLinkField;
    }

    public void setUnaryLinkField(ModelElementRef aValue) {
        unaryLinkField = aValue;
    }

    @Designable(category = "tree")
    public ModelEntityParameterRef getParam2GetChildren() {
        return param2GetChildren;
    }

    public void setParam2GetChildren(ModelEntityParameterRef aValue) {
        param2GetChildren = aValue;
    }

    @Designable(category = "tree")
    public ModelElementRef getParamSourceField() {
        return paramSourceField;
    }

    public void setParamSourceField(ModelElementRef aValue) {
        paramSourceField = aValue;
    }

    @Designable(category = "appearance")
    public int getRowsHeaderType() {
        return rowsHeaderType;
    }

    public void setRowsHeaderType(int aValue) {
        rowsHeaderType = aValue;
    }

    @Undesignable
    public Function getGeneralRowFunction() {
        return generalRowFunction;
    }

    public void setGeneralRowFunction(Function aValue) {
        generalRowFunction = aValue;
    }

    public void insertRow() {
        try {
            if (insertable && !(rowsModel.getRowsRowset() instanceof ParametersRowset)) {
                rowsSelectionModel.removeListSelectionListener(generalSelectionChangesReflector);
                try {
                    if (rowsModel instanceof RowsetsTreedModel) {
                        int parentColIndex = ((RowsetsTreedModel) rowsModel).getParentFieldIndex();
                        Object parentColValue = null;
                        if (!rowsModel.getRowsRowset().isEmpty()
                                && !rowsModel.getRowsRowset().isBeforeFirst()
                                && !rowsModel.getRowsRowset().isAfterLast()) {
                            parentColValue = rowsModel.getRowsRowset().getObject(parentColIndex);
                        }
                        rowsModel.getRowsRowset().insert(parentColIndex, parentColValue);
                    } else {
                        rowsModel.getRowsRowset().insert();
                    }
                } finally {
                    rowsSelectionModel.addListSelectionListener(generalSelectionChangesReflector);
                }
                Row insertedRow = rowsModel.getRowsRowset().getCurrentRow();
                assert insertedRow.isInserted();
                makeVisible(insertedRow);
            }
        } catch (Exception ex) {
            Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteRow() {
        try {
            if (deletable && !(rowsModel.getRowsRowset() instanceof ParametersRowset)) {
                ListSelectionModel savedSelection = new DefaultListSelectionModel();
                Set<Row> rows = new HashSet<>();
                for (int viewRowIndex = rowsSelectionModel.getMinSelectionIndex(); viewRowIndex <= rowsSelectionModel.getMaxSelectionIndex(); viewRowIndex++) {
                    if (rowsSelectionModel.isSelectedIndex(viewRowIndex)) {
                        // We have to act upon model coordinates here!
                        Row rsRow = index2Row(rowSorter.convertRowIndexToModel(viewRowIndex));
                        if (rsRow != null) {
                            rows.add(rsRow);
                        }
                        // We have to act upon view coordinates here!
                        savedSelection.addSelectionInterval(viewRowIndex, viewRowIndex);
                    }
                }
                try {
                    rowsModel.getRowsRowset().delete(rows);
                } finally {
                    // We have to act upon view coordinates here!
                    rowsSelectionModel.clearSelection();
                    for (int viewRowIndex = savedSelection.getMinSelectionIndex(); viewRowIndex <= savedSelection.getMaxSelectionIndex(); viewRowIndex++) {
                        if (viewRowIndex >= 0 && viewRowIndex < rowSorter.getViewRowCount()) {
                            rowsSelectionModel.addSelectionInterval(viewRowIndex, viewRowIndex);
                        } else if (viewRowIndex == rowSorter.getViewRowCount()) {
                            rowsSelectionModel.addSelectionInterval(viewRowIndex - 1, viewRowIndex - 1);
                        }
                    }
                }
            }
        } catch (RowsetException ex) {
            Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected class GeneralSelectionChangesReflector implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            // If remove !e.getValueIsAdjusting() check,
            // then there is a bug with rowset.insert() -> rowset.field1 = ... chain 
            // because selection restoring repositions rowset before assignment can take place.
            if (!e.getValueIsAdjusting() && !rowsModel.getRowsRowset().isInserting() && rowsSelectionModel.getLeadSelectionIndex() != -1) {
                try {
                    if (!try2StopAnyEditing()) {
                        try2CancelAnyEditing();
                    }
                    Row row = index2Row(rowSorter.convertRowIndexToModel(rowsSelectionModel.getLeadSelectionIndex()));
                    if (row != null) {
                        rowsModel.positionRowsetWithRow(row);
                    }
                    repaint();
                } catch (Exception ex) {
                    Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    protected class ColumnHeaderScroller implements ChangeListener {

        protected JViewport columnHeader;
        protected JViewport content;
        protected boolean working;

        public ColumnHeaderScroller(JViewport aColumnHeader, JViewport aContent) {
            columnHeader = aColumnHeader;
            content = aContent;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!working) {
                working = true;
                try {
                    Point contentPos = content.getViewPosition();
                    contentPos.x = columnHeader.getViewPosition().x;
                    content.setViewPosition(contentPos);
                } finally {
                    working = false;
                }
            }
        }
    }

    protected class RowHeaderScroller implements ChangeListener {

        protected JViewport rowHeader;
        protected JViewport content;
        protected boolean working;

        public RowHeaderScroller(JViewport aRowHeader, JViewport aContent) {
            rowHeader = aRowHeader;
            content = aContent;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!working) {
                working = true;
                try {
                    Point contentPos = content.getViewPosition();
                    contentPos.y = rowHeader.getViewPosition().y;
                    content.setViewPosition(contentPos);
                } finally {
                    working = false;
                }
            }
        }
    }

    protected Function getHandler(String aHandlerName) {
        if (model != null && model.getScriptScope() != null && aHandlerName != null && !aHandlerName.isEmpty()) {
            Object oFunction = model.getScriptScope().get(aHandlerName, model.getScriptScope());
            if (oFunction instanceof Function) {
                return (Function) oFunction;
            }
        }
        return null;
    }

    // Some cleanup
    protected void cleanup() throws Exception {
        removeAll();
        if (rowsEntity != null && rowsEntity.getRowset() != null) {
            rowsEntity.getRowset().removeRowsetListener(scrollReflector);
        }
        rowsEntity = null;
        if (lriddler != null) {
            lriddler.die();
            lriddler = null;
        }
        if (rriddler != null) {
            rriddler.die();
            rriddler = null;
        }
        if (columnModel != null) {
            for (int i = columnModel.getColumnCount() - 1; i >= 0; i--) {
                columnModel.removeColumn(columnModel.getColumn(i));
            }
            columnModel = null;
        }
        rowsModel = null;
        deepModel = null;
        rowSorter = null;
        lcolumnsSources = new HashMap<>();
        rcolumnsSources = new HashMap<>();
        tlTable = null;
        trTable = null;
        blTable = null;
        brTable = null;
        gridScroll = null;
    }

    public void configure() throws Exception {
        if (model != null && model.isRuntime()) {
            cleanup();
            if (rowsHeaderType != DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_NONE) {
                int fixedWidth = 18;
                if (rowsHeaderType == DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_CHECKBOX
                        || rowsHeaderType == DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_RADIOBUTTON) {
                    fixedWidth += 20;
                }
                // This place is very special, because editing while configuring takes place.
                if (header.isEmpty() || header.get(0) == null || !(header.get(0) instanceof FixedDbGridColumn)) {
                    header.add(0, new FixedDbGridColumn(fixedWidth, rowsHeaderType));// Space enough for two icons
                }
                fixedColumns++;
            }
            fixedColumns = convertFixedColumns2Leaves(header, fixedColumns);

            // Rows configuration
            rowsSelectionModel = new DefaultListSelectionModel();
            rowsEntity = DbControlsUtils.resolveEntity(model, rowsDatasource);
            Rowset rowsRowset = DbControlsUtils.resolveRowset(model, rowsDatasource);
            if (rowsRowset != null) {
                if (isTreeConfigured()) {
                    final Parameter param = DbControlsUtils.resolveParameter(model, param2GetChildren);
                    final int paramSourceFieldIndex = DbControlsUtils.resolveFieldIndex(model, paramSourceField);

                    int parentColIndex = rowsRowset.getFields().find(unaryLinkField.getFieldName());
                    rowsModel = new RowsetsTreedModel(rowsRowset, parentColIndex, eventThis != null ? eventThis : scriptScope, generalRowFunction) {
                        @Override
                        public boolean isLeaf(Row anElement) {
                            if (param != null && paramSourceFieldIndex != 0)// lazy tree
                            {
                                return false;
                            } else {
                                return super.isLeaf(anElement);
                            }
                        }
                    };
                    if (param != null && paramSourceFieldIndex != 0) {// lazy tree
                        GridChildrenFetcher fetcher = new GridChildrenFetcher(this, rowsEntity, param, paramSourceFieldIndex);
                        deepModel = new TableFront2TreedModel<>((RowsetsTreedModel) rowsModel, fetcher);
                    } else {
                        deepModel = new TableFront2TreedModel<>((RowsetsTreedModel) rowsModel);
                    }
                    rowSorter = new TreedRowsSorter<>((TableFront2TreedModel<Row>) deepModel, rowsSelectionModel);
                } else {
                    rowsModel = new RowsetsTableModel(rowsRowset, eventThis != null ? eventThis : scriptScope, generalRowFunction);
                    deepModel = (TableModel) rowsModel;
                    rowSorter = new TabularRowsSorter<>((RowsetsTableModel) deepModel, rowsSelectionModel);
                }
                TableModel generalModel = new CachingTableModel(deepModel, CELLS_CACHE_SIZE);

                generalSelectionChangesReflector = new GeneralSelectionChangesReflector();
                rowsSelectionModel.addListSelectionListener(generalSelectionChangesReflector);
                // Columns configuration
                columnsSelectionModel = new DefaultListSelectionModel();
                columnModel = new DefaultTableColumnModel();
                Map<TableColumn, GridColumnsGroup> cols2groups = fillColumnsGroup(null, header, false);
                columnModel.setSelectionModel(columnsSelectionModel);
                columnModel.setColumnSelectionAllowed(true);
                rowsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                configureTreedView();

                // constraints setup
                LinearConstraint leftColsConstraint = new LinearConstraint(0, fixedColumns - 1);
                LinearConstraint rightColsConstraint = new LinearConstraint(fixedColumns, Integer.MAX_VALUE);
                LinearConstraint topRowsConstraint = new LinearConstraint(0, fixedRows - 1);
                LinearConstraint bottomRowsConstraint = new LinearConstraint(fixedRows, Integer.MAX_VALUE);

                // constrained layer models setup
                tlTable = new GridTable(null, rowsRowset, this);
                trTable = new GridTable(null, rowsRowset, this);
                blTable = new GridTable(tlTable, rowsRowset, this);
                brTable = new GridTable(trTable, rowsRowset, this);
                tlTable.setModel(generalModel);
                trTable.setModel(generalModel);
                blTable.setModel(generalModel);
                brTable.setModel(generalModel);

                columnModel.setColumnSelectionAllowed(rowsHeaderType != DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_CHECKBOX
                        && rowsHeaderType != DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_RADIOBUTTON);

                tlTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, topRowsConstraint));
                tlTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, topRowsConstraint));
                tlTable.setColumnModel(new ConstrainedColumnModel(columnModel, leftColsConstraint));
                tlTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, leftColsConstraint));

                trTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, topRowsConstraint));
                trTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, topRowsConstraint));
                trTable.setColumnModel(new ConstrainedColumnModel(columnModel, rightColsConstraint));
                trTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, rightColsConstraint));

                blTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, bottomRowsConstraint));
                blTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, bottomRowsConstraint));
                blTable.setColumnModel(new ConstrainedColumnModel(columnModel, leftColsConstraint));
                blTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, leftColsConstraint));

                brTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, bottomRowsConstraint));
                brTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, bottomRowsConstraint));
                brTable.setColumnModel(new ConstrainedColumnModel(columnModel, rightColsConstraint));
                brTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, rightColsConstraint));

                tlTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                trTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                blTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                brTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                tlTable.setAutoCreateColumnsFromModel(false);
                trTable.setAutoCreateColumnsFromModel(false);
                blTable.setAutoCreateColumnsFromModel(false);
                brTable.setAutoCreateColumnsFromModel(false);

                // keyboard focus flow setup
                tlTable.addKeyListener(tablesFocusManager);
                trTable.addKeyListener(tablesFocusManager);
                blTable.addKeyListener(tablesFocusManager);
                brTable.addKeyListener(tablesFocusManager);
                // mouse propagating setup
                tlTable.addMouseListener(tablesMousePropagator);
                trTable.addMouseListener(tablesMousePropagator);
                blTable.addMouseListener(tablesMousePropagator);
                brTable.addMouseListener(tablesMousePropagator);
                tlTable.addMouseWheelListener(tablesMousePropagator);
                trTable.addMouseWheelListener(tablesMousePropagator);
                blTable.addMouseWheelListener(tablesMousePropagator);
                brTable.addMouseWheelListener(tablesMousePropagator);
                tlTable.addMouseMotionListener(tablesMousePropagator);
                trTable.addMouseMotionListener(tablesMousePropagator);
                blTable.addMouseMotionListener(tablesMousePropagator);
                brTable.addMouseMotionListener(tablesMousePropagator);
                // grid components setup.
                // left header setup
                lheader = new MultiLevelHeader();
                lheader.setTable(tlTable);
                tlTable.getTableHeader().setResizingAllowed(true);
                lheader.setSlaveHeaders(tlTable.getTableHeader(), blTable.getTableHeader());
                lheader.setColumnModel(tlTable.getColumnModel());
                lheader.getColumnsParents().putAll(filterLeaves(cols2groups, columnModel, 0, fixedColumns - 1));
                lcolumnsSources.putAll(filterColumnsSources(columnModel, 0, fixedColumns - 1));
                lheader.setRowSorter(rowSorter);
                // right header setup
                rheader = new MultiLevelHeader();
                rheader.setTable(trTable);
                trTable.getTableHeader().setResizingAllowed(true);
                rheader.setSlaveHeaders(trTable.getTableHeader(), brTable.getTableHeader());
                rheader.setColumnModel(trTable.getColumnModel());
                rheader.getColumnsParents().putAll(filterLeaves(cols2groups, columnModel, fixedColumns, columnModel.getColumnCount() - 1));
                rcolumnsSources.putAll(filterColumnsSources(columnModel, fixedColumns, columnModel.getColumnCount() - 1));
                rheader.setRowSorter(rowSorter);
                // TODO: replace this by hashing scriptablecolumns by ColGroup and then
                // iteration through left and right colGroups heirarchy.
                for (ScriptableColumn sCol : scriptableColumns) {
                    if (rheader.getColumnsParents().containsKey(sCol.getViewColumn())) {
                        sCol.setHeader(rheader);
                    } else if (lheader.getColumnsParents().containsKey(sCol.getViewColumn())) {
                        sCol.setHeader(lheader);
                    }
                }
                // Tables are enclosed in panels to avoid table's stupid efforts
                // to configure it's parent scroll pane.
                JPanel tlPanel = new JPanel(new BorderLayout());
                tlPanel.add(lheader, BorderLayout.NORTH);
                tlPanel.add(tlTable, BorderLayout.CENTER);
                JPanel trPanel = new JPanel(new BorderLayout());
                trPanel.add(rheader, BorderLayout.NORTH);
                trPanel.add(trTable, BorderLayout.CENTER);
                JPanel blPanel = new JPanel(new BorderLayout());
                blPanel.add(blTable, BorderLayout.CENTER);
                JPanel brPanel = new GridTableScrollablePanel(brTable);
                //brPanel.add(brTable, BorderLayout.CENTER);

                boolean needOutlineCols = (fixedColumns > 0 && rowsHeaderType == DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_NONE)
                        || fixedColumns > 1;
                tlPanel.setBorder(new MatteBorder(0, 0, fixedRows > 0 ? 1 : 0, needOutlineCols ? 1 : 0, FIXED_COLOR));
                trPanel.setBorder(new MatteBorder(0, 0, fixedRows > 0 ? 1 : 0, 0, FIXED_COLOR));
                blPanel.setBorder(new MatteBorder(0, 0, 0, needOutlineCols ? 1 : 0, FIXED_COLOR));

                progressIndicator = new JLabel(processIcon);
                progressIndicator.setVisible(false);
                gridScroll = new JScrollPane();
                gridScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, tlPanel);
                gridScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, progressIndicator);
                gridScroll.setColumnHeaderView(trPanel);
                gridScroll.getColumnHeader().addChangeListener(new ColumnHeaderScroller(gridScroll.getColumnHeader(), gridScroll.getViewport()));
                gridScroll.setRowHeaderView(blPanel);
                if (rowsHeaderType != DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_NONE) {
                    gridScroll.getRowHeader().addChangeListener(new RowHeaderScroller(gridScroll.getRowHeader(), gridScroll.getViewport()));
                }
                gridScroll.setViewportView(brPanel);

                setLayout(new BorderLayout());
                add(gridScroll, BorderLayout.CENTER);
                //
                lriddler = new ColumnsRiddler(lheader.getColumnsParents(), columnModel, lheader, rowsModel, lcolumnsSources, new HasStyle() {
                    @Override
                    public CascadedStyle getStyle() {
                        return style;
                    }
                }, scriptableColumns, scriptScope != null ? scriptScope.get(getName(), scriptScope) : null);
                lriddler.setLeftConstraint(leftColsConstraint);
                lriddler.setRightConstraint(rightColsConstraint);
                lriddler.fill();

                rriddler = new ColumnsRiddler(rheader.getColumnsParents(), columnModel, rheader, rowsModel, rcolumnsSources, new HasStyle() {
                    @Override
                    public CascadedStyle getStyle() {
                        return style;
                    }
                }, scriptableColumns, scriptScope != null ? scriptScope.get(getName(), scriptScope) : null);
                rriddler.fill();

                lheader.setNeightbour(rheader);
                rheader.setNeightbour(lheader);
                lheader.setRegenerateable(true);
                rheader.setRegenerateable(true);
                lheader.regenerate();
                rheader.regenerate();
                rowsRowset.addRowsetListener(scrollReflector);
                configureScriptScope();
                configureActions();
            }
            applyEnabled();
            applyFont();
            applyEditable();
            applyBackground();
            applyForeground();
            applyComponentPopupMenu();
            applyGridColor();
            applyRowHeight();
            applyShowHorizontalLines();
            applyShowVerticalLines();
            applyToolTipText();
            applyShowOddRowsInOtherColor();
            applyOddRowsColor();
            applyComponentOrientation(getComponentOrientation());// Swing allows only argumented call. 
            repaint();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        int width = d.width;
        int height = d.height;
        if (width < DbControlsUtils.DB_CONTROLS_DEFAULT_WIDTH) {
            width = DbControlsUtils.DB_CONTROLS_DEFAULT_WIDTH;
        }
        if (height < DbControlsUtils.DB_CONTROLS_DEFAULT_HEIGHT) {
            height = DbControlsUtils.DB_CONTROLS_DEFAULT_HEIGHT;
        }
        return new Dimension(width, height);
    }

    protected class LinkedGridColumnsGroup extends GridColumnsGroup {

        protected DbGridColumn gridColumn;

        public LinkedGridColumnsGroup(DbGridColumn aGridColumn) {
            super();
            gridColumn = aGridColumn;
        }

        public DbGridColumn getGridColumn() {
            return gridColumn;
        }
    }

    public void initializeDesign() {
        if (!isRuntime()) {
            removeAll();
            setLayout(new BorderLayout());
            JLabel label = new JLabel(this.getClass().getSimpleName().replace("Db", "Model"), IconCache.getIcon("16x16/grid.png"), SwingConstants.LEADING);
            label.setOpaque(false);
            add(label, BorderLayout.CENTER);

            MatteBorder innerBorder = new MatteBorder(1, 1, 0, 0, getBackground().brighter());
            MatteBorder outerBorder = new MatteBorder(0, 0, 1, 1, getBackground().darker());
            CompoundBorder border = new CompoundBorder(innerBorder, outerBorder);
            setBorder(border);

            if (header != null && !header.isEmpty()) {
                try {
                    // Columns configuration
                    ListSelectionModel selectionModel = new DefaultListSelectionModel();
                    columnModel = new DefaultTableColumnModel() {
                        @Override
                        public int getTotalColumnWidth() {
                            // super implementation caches the result and invalidates it
                            // when width of any column is changed/
                            // We need to set width silently, because of extra event
                            // been fired by swing. We need to avoid it. 
                            // We avoid it, but it leads to break of width cache invalidation.
                            // So we need to calculate width unconditionally and allways.
                            int width = 0;
                            Enumeration<TableColumn> cols = getColumns();
                            while (cols.hasMoreElements()) {
                                width += cols.nextElement().getWidth();
                            }
                            return width;
                        }
                    };
                    Rowset rowsRowset = new ParametersRowset(new com.bearsoft.rowset.metadata.Parameters());
                    rowsModel = new RowsetsTableModel(rowsRowset, null, null) {
                        @Override
                        public int getRowCount() {
                            return 10;
                        }

                        @Override
                        public Object getValueAt(int rowIndex, int columnIndex) {
                            return null;
                        }

                        @Override
                        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                        }
                    };
                    Map<TableColumn, GridColumnsGroup> cols2groups = fillColumnsGroup(null, header, true);
                    for (int i = 0; i < columnModel.getColumnCount(); i++) {
                        TableColumn tc = columnModel.getColumn(i);
                        tc.setCellEditor(null);
                        tc.setCellRenderer(new DefaultTableCellRenderer());
                    }
                    columnModel.setSelectionModel(selectionModel);
                    columnModel.setColumnSelectionAllowed(true);

                    trTable = new GridTable(null, rowsRowset, this) {
                        @Override
                        public TableCellRenderer getCellRenderer(int row, int column) {
                            TableCellRenderer res = super.getCellRenderer(row, column);
                            if (res instanceof Component) {
                                ((Component) res).setBackground(DbGrid.this.getBackground());
                            }
                            return res;
                        }
                    };
                    trTable.setModel((TableModel) rowsModel);
                    trTable.setColumnModel(columnModel);
                    trTable.setShowHorizontalLines(showHorizontalLines);
                    trTable.setShowVerticalLines(showVerticalLines);
                    trTable.setShowOddRowsInOtherColor(showOddRowsInOtherColor);
                    trTable.setOddRowsColor(oddRowsColor);
                    trTable.setBackground(getBackground());
                    trTable.setRowHeight(rowsHeight);

                    trTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    trTable.setAutoCreateColumnsFromModel(false);

                    rheader = new MultiLevelHeader();
                    rheader.setTable(trTable);
                    rheader.setColumnModel(columnModel);
                    rheader.getColumnsParents().putAll(cols2groups);

                    JPanel rPanel = new JPanel(new BorderLayout());
                    rPanel.add(rheader, BorderLayout.NORTH);
                    JPanel trPanel = new JPanel(new BorderLayout());
                    trPanel.add(trTable, BorderLayout.CENTER);

                    gridScroll = new JScrollPane();
                    gridScroll.setColumnHeaderView(rPanel);
                    gridScroll.setViewportView(trPanel);
                    rheader.setRegenerateable(true);
                    rheader.regenerate();
                    for (int i = 0; i < rheader.getComponentCount(); i++) {
                        if (rheader.getComponent(i) instanceof HeaderCell
                                && ((HeaderCell) rheader.getComponent(i)).getColGroup() instanceof LinkedGridColumnsGroup) {
                            final HeaderCell cell = (HeaderCell) rheader.getComponent(i);
                            final LinkedGridColumnsGroup lg = (LinkedGridColumnsGroup) cell.getColGroup();
                            lg.getGridColumn().addPropertyChangeListener(new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    lg.setStyle(lg.getGridColumn().getHeaderStyle());
                                    lg.setTitle(lg.getGridColumn().getTitle());
                                    cell.applyStyle();
                                    cell.repaint();
                                }
                            });
                        }
                    }
                    add(gridScroll, BorderLayout.CENTER);
                } catch (Exception ex) {
                    Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
                }
                revalidate();
                repaint();
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (lheader != null) {
            lheader.invalidate();
        }
        if (rheader != null) {
            rheader.invalidate();
        }
        if (tlTable != null) {
            tlTable.invalidate();
        }
        if (trTable != null) {
            trTable.invalidate();
        }
        if (blTable != null) {
            blTable.invalidate();
        }
        if (brTable != null) {
            brTable.invalidate();
        }
    }

    @Override
    public boolean try2StopAnyEditing() {
        boolean res = true;
        if (tlTable.getCellEditor() != null && !tlTable.getCellEditor().stopCellEditing()) {
            res = false;
        }
        if (trTable.getCellEditor() != null && !trTable.getCellEditor().stopCellEditing()) {
            res = false;
        }
        if (blTable.getCellEditor() != null && !blTable.getCellEditor().stopCellEditing()) {
            res = false;
        }
        if (brTable.getCellEditor() != null && !brTable.getCellEditor().stopCellEditing()) {
            res = false;
        }
        return res;
    }

    @Override
    public boolean try2CancelAnyEditing() {
        if (tlTable.getCellEditor() != null) {
            tlTable.getCellEditor().cancelCellEditing();
        }
        if (trTable.getCellEditor() != null) {
            trTable.getCellEditor().cancelCellEditing();
        }
        if (blTable.getCellEditor() != null) {
            blTable.getCellEditor().cancelCellEditing();
        }
        if (brTable.getCellEditor() != null) {
            brTable.getCellEditor().cancelCellEditing();
        }
        return tlTable.getCellEditor() == null && trTable.getCellEditor() == null
                && blTable.getCellEditor() == null && brTable.getCellEditor() == null;
    }

    protected Map<TableColumn, GridColumnsGroup> filterLeaves(Map<TableColumn, GridColumnsGroup> cols2groups, TableColumnModel aTableColumnModel, int begIdx, int endIdx) {
        Set<TableColumn> allowedColumns = new HashSet<>();
        for (int i = begIdx; i <= endIdx; i++) {
            allowedColumns.add(aTableColumnModel.getColumn(i));
        }
        Map<TableColumn, GridColumnsGroup> table2Group = new HashMap<>();
        for (Entry<TableColumn, GridColumnsGroup> entry : cols2groups.entrySet()) {
            if (allowedColumns.contains(entry.getKey())) {
                table2Group.put(entry.getKey(), entry.getValue());
            }
        }
        return table2Group;
    }

    protected Map<Rowset, List<ColumnsSource>> filterColumnsSources(TableColumnModel aTableColumnModel, int begIdx, int endIdx) {
        Map<Rowset, List<ColumnsSource>> rowsetsToColumnsSource = new HashMap<>();
        for (int i = begIdx; i <= endIdx; i++) {
            TableColumn tCol = aTableColumnModel.getColumn(i);
            if (tCol instanceof AnchorTableColumn) {
                AnchorTableColumn anchorCol = (AnchorTableColumn) tCol;
                ColumnsSource colSource = anchorCol.getColumnsSource();
                Rowset colsRowset = colSource.getColumnsLocator().getRowset();
                List<ColumnsSource> colSources = rowsetsToColumnsSource.get(colsRowset);
                if (colSources == null) {
                    colSources = new ArrayList<>();
                    rowsetsToColumnsSource.put(colsRowset, colSources);
                }
                colSources.add(colSource);
            }
        }
        return rowsetsToColumnsSource;
    }

    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    public TableModel getDeepModel() {
        return deepModel;
    }

    public RowsetsModel getRowsetsModel() {
        return rowsModel;
    }

    public TabularRowsSorter<? extends TableModel> getRowSorter() {
        return rowSorter;
    }

    @Override
    public synchronized void addProcessedRow(Row aRow) {
        processedRows.add(aRow);
        progressIndicator.setVisible(true);
        gridScroll.repaint();
    }

    @Override
    public synchronized void removeProcessedRow(Row aRow) {
        processedRows.remove(aRow);
        if (processedRows.isEmpty()) {
            progressIndicator.setVisible(false);
        }
        gridScroll.repaint();
    }

    @Override
    public synchronized Row[] getProcessedRows() {
        return processedRows.toArray(new Row[]{});
    }

    @Override
    public synchronized boolean isRowProcessed(Row aRow) {
        return processedRows.contains(aRow);
    }

    public void select(RowHostObject aRow) throws Exception {
        if (aRow != null) {
            selectRow(aRow.unwrap());
        }
    }

    public void selectRow(Row aRow) throws Exception {
        if (aRow != null) {
            int idx = row2Index(aRow);
            if (idx != -1) {
                rowsSelectionModel.addSelectionInterval(idx, idx);
            }
        }
    }

    public Row selectRow(Object aId) throws Exception {
        if (rowsModel.getPkLocator().find(aId)) {
            Row lRow = rowsModel.getPkLocator().getRow(0);
            if (lRow != null) {
                selectRow(lRow);
            }
            return lRow;
        } else {
            return null;
        }
    }

    public void unselect(RowHostObject aRow) throws Exception {
        if (aRow != null) {
            unselectRow(aRow.unwrap());
        }
    }

    public void unselectRow(Row aRow) throws Exception {
        if (aRow != null) {
            int idx = row2Index(aRow);
            if (idx != -1) {
                rowsSelectionModel.removeSelectionInterval(idx, idx);
            }
        }
    }

    public Row unselectRow(Object aId) throws Exception {
        if (rowsModel.getPkLocator().find(aId)) {
            Row lRow = rowsModel.getPkLocator().getRow(0);
            unselectRow(lRow);
            return lRow;
        }
        return null;
    }

    public void clearSelection() {
        columnsSelectionModel.clearSelection();
        rowsSelectionModel.clearSelection();
    }

    public void findSomething() {
        findSomethingAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "startFinding", 0));
    }

    public boolean ensureFetched(ScriptableRowset<?> aRowset, Field aParentField, Object aTargetId) throws Exception {
        if (deepModel instanceof TableFront2TreedModel) {
            TableFront2TreedModel<Row> front = (TableFront2TreedModel<Row>) deepModel;
            if (front.unwrap() instanceof RowsetsTreedModel) {
                RowsetsTreedModel rModel = (RowsetsTreedModel) front.unwrap();
                Rowset r = aRowset.unwrap();
                if (!rowsModel.getPkLocator().getFields().isEmpty()) {
                    int pkColIndex = rowsModel.getPkLocator().getFields().get(0);
                    int parentColIndex = r.getFields().find(aParentField.getName());
                    List<Row> toFetch = new ArrayList<>();
                    toFetch.addAll(r.getCurrent());
                    int fetched = toFetch.size();
                    while (!toFetch.isEmpty() && fetched != 0) {
                        fetched = 0;
                        for (int i = toFetch.size() - 1; i >= 0; i--) {
                            Row rowToFetch = toFetch.get(i);
                            if (!rModel.getPkLocator().find(new Object[]{rowToFetch.getColumnObject(pkColIndex)})) {
                                if (rModel.getPkLocator().find(new Object[]{rowToFetch.getColumnObject(parentColIndex)})) {
                                    Row innerParentRow = rModel.getPkLocator().getRow(0);
                                    front.expand(innerParentRow, false);
                                    toFetch.remove(rowToFetch);
                                    ++fetched;
                                }
                            } else {
                                toFetch.remove(rowToFetch);
                                ++fetched;
                            }
                        }
                    }
                    return rModel.getPkLocator().find(new Object[]{aTargetId});
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean makeVisible(RowHostObject aRow) throws Exception {
        if (aRow != null) {
            return makeVisible(aRow.unwrap());
        } else {
            return false;
        }
    }

    public boolean makeVisible(RowHostObject aRow, boolean need2Select) throws Exception {
        if (aRow != null) {
            return makeVisible(aRow.unwrap(), need2Select);
        } else {
            return false;
        }
    }

    public boolean makeVisible(Row aRow) throws Exception {
        return makeVisible(aRow, true);
    }

    @Override
    public boolean makeVisible(Row aRow, boolean need2Select) throws Exception {
        if (aRow != null) {
            // Let's process possible tree paths
            if (rowsModel instanceof RowsetsTreedModel) {
                assert deepModel instanceof TableFront2TreedModel<?>;
                TableFront2TreedModel<Row> front = (TableFront2TreedModel<Row>) deepModel;
                List<Row> path = front.buildPathTo(aRow);
                for (int i = 0; i < path.size() - 1; i++) {
                    front.expand(path.get(i), true);
                }
            }
            int modelIndex = row2Index(aRow);
            if (modelIndex != -1) {
                int generalViewIndex = rowSorter.convertRowIndexToView(modelIndex);
                if (brTable.getColumnCount() > 0) {
                    int brViewIndex = generalViewIndex - trTable.getRowCount();
                    if (brViewIndex >= 0) {
                        Rectangle cellRect = brTable.getCellRect(brViewIndex, 0, false);
                        cellRect.height *= 10;
                        brTable.scrollRectToVisible(cellRect);
                    }
                } else if (blTable.getColumnCount() > 0) {
                    int blViewIndex = generalViewIndex - tlTable.getRowCount();
                    if (blViewIndex >= 0) {
                        Rectangle cellRect = blTable.getCellRect(blViewIndex, 0, false);
                        cellRect.height *= 10;
                        blTable.scrollRectToVisible(cellRect);
                    }
                }
                if (need2Select) {
                    columnsSelectionModel.setSelectionInterval(0, columnModel.getColumnCount() - 1);
                    rowsSelectionModel.setSelectionInterval(generalViewIndex, generalViewIndex);
                }
                return true;
            }
        }
        return false;
    }

    public boolean makeVisible(Object aId) throws Exception {
        return makeVisible(aId, true);
    }

    public boolean makeVisible(Object aId, boolean need2Select) throws Exception {
        aId = ScriptUtils.js2Java(aId);
        aId = rowsModel.getRowsRowset().getConverter().convert2RowsetCompatible(aId, rowsModel.getRowsRowset().getFields().get(rowsModel.getPkLocator().getFields().get(0)).getTypeInfo());
        if (rowsModel.getPkLocator().find(aId)) {
            return makeVisible(rowsModel.getPkLocator().getRow(0), need2Select);
        }
        return false;
    }

    public boolean isCurrentRow(Row aRow) {
        return getCurrentRow() == aRow;
    }

    public Row getCurrentRow() {
        try {
            if (rowsEntity != null && rowsEntity.getRowset() != null) {
                Rowset rowset = rowsEntity.getRowset();
                return rowset.getCurrentRow();
            }
        } catch (Exception ex) {
            Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JTable getFocusedTable() {
        JTable focusedTable = null;
        if (tlTable != null && tlTable.hasFocus()) {
            focusedTable = tlTable;
        }
        if (trTable != null && trTable.hasFocus()) {
            focusedTable = trTable;
        }
        if (blTable != null && blTable.hasFocus()) {
            focusedTable = blTable;
        }
        if (brTable != null && brTable.hasFocus()) {
            focusedTable = brTable;
        }
        return focusedTable;
    }

    private String transformCellValue(Object aValue, int aCol, boolean isData) {
        if (aValue != null) {
            Object value = null;
            if (isData) {
                if (aValue instanceof CellData) {
                    CellData cd = (CellData) aValue;
                    value = cd.getData();
                }
                if (value != null) {
                    return value.toString();
                }
            } else {
                TableColumn tc = getColumnModel().getColumn(aCol);
                TableCellRenderer renderer = tc.getCellRenderer();
                if (renderer instanceof DbCombo) {
                    try {
                        if (aValue instanceof CellData) {
                            value = ((DbCombo) renderer).achiveDisplayValue(((CellData) aValue).getData());
                        }
                        if (value == null) {
                            value = "";
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, "Could not get cell value", ex);
                    }
                }

                if (value == null && aValue instanceof CellData) {
                    CellData cd = (CellData) aValue;
                    value = cd.getDisplay() != null ? cd.getDisplay() : cd.getData();
                }
                if (value != null) {
                    return value.toString();
                }
            }
        }
        return "";
    }

    private String[] transformRow(int aRow, boolean selectedOnly, boolean isData) {
        TableModel view = getDeepModel();
        int minCol = 0;
        int maxCol = view.getColumnCount();
        String[] res = new String[maxCol];
        int curentColumn = 0;
        for (int col = minCol; col < getColumnModel().getColumnCount(); col++) {
            TableColumn tc = getColumnModel().getColumn(col);
            if (tc.getWidth() > 0 && !(tc instanceof AnchorTableColumn) && !(tc instanceof RowHeaderTableColumn)) {
                if (selectedOnly) {
                    if (getColumnsSelectionModel().isSelectedIndex(col)) {
                        res[curentColumn] = transformCellValue(view.getValueAt(aRow, tc.getModelIndex()), col, isData);
                        curentColumn++;
                    }
                } else {
                    res[curentColumn] = transformCellValue(view.getValueAt(aRow, tc.getModelIndex()), col, isData);
                    curentColumn++;
                }
            }
        }
        return res;
    }

    private Object[] convertView(String[][] aCells) {
        Object[] cells = new Object[aCells.length];
        for (int i = 0; i < aCells.length; i++) {
            String[] row = aCells[i];
            Object[] o = new Object[row.length];
            System.arraycopy(row, 0, o, 0, row.length);
            cells[i] = Context.getCurrentContext().newArray(model.getScriptScope(), o);
        }
        return cells;
    }

    public Scriptable getCells() {
        Object[] cells = convertView(getGridView(false, false));
        return Context.getCurrentContext().newArray(model.getScriptScope(), cells);
    }

    public Scriptable getSelectedCells() {
        Object[] selectedCells = convertView(getGridView(true, false));
        return Context.getCurrentContext().newArray(model.getScriptScope(), selectedCells);
    }

    public Scriptable getColumnsScriptView() {
        if (eventThis != null) {
            List<Object> columns = new ArrayList<>();
            for (ScriptableColumn scrCol : scriptableColumns) {
                columns.add(scrCol.getPublished());
            }
            return Context.getCurrentContext().newArray(model.getScriptScope(), columns.toArray());
        }
        return null;
    }

    private String[][] getGridView(boolean selectedOnly, boolean isData) {
        TableModel view = getDeepModel();
        if (view != null) {
            int minRow = 0;
            int maxRow = view.getRowCount();
            int columnCount = view.getColumnCount();
            String[][] res = new String[maxRow][columnCount];
            ListSelectionModel rowSelecter = getRowsSelectionModel();
            for (int row = minRow; row < maxRow; row++) {
                if (selectedOnly) {
                    if (rowSelecter.isSelectedIndex(row)) {
                        res[row] = transformRow(row, selectedOnly, isData);
                    }
                } else {
                    res[row] = transformRow(row, selectedOnly, isData);
                }
            }
            return res;
        } else {
            return new String[0][0];
        }
    }

    protected class TablesMousePropagator implements MouseListener, MouseMotionListener, MouseWheelListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseListener l : DbGrid.this.getMouseListeners()) {
                    l.mouseClicked(e);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseListener l : DbGrid.this.getMouseListeners()) {
                    l.mousePressed(e);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseListener l : DbGrid.this.getMouseListeners()) {
                    l.mouseReleased(e);
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseListener l : DbGrid.this.getMouseListeners()) {
                    l.mouseEntered(e);
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseListener l : DbGrid.this.getMouseListeners()) {
                    l.mouseExited(e);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseMotionListener l : DbGrid.this.getMouseMotionListeners()) {
                    l.mouseDragged(e);
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseMotionListener l : DbGrid.this.getMouseMotionListeners()) {
                    l.mouseMoved(e);
                }
            }
        }

        protected MouseWheelEvent sendWheelTo(MouseWheelEvent e, Component aComp) {
            MouseEvent me = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, aComp);
            e = new MouseWheelEvent(aComp, e.getID(), e.getWhen(), e.getModifiers(), me.getX(), me.getY(), me.getXOnScreen(), me.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation());
            for (MouseWheelListener l : aComp.getMouseWheelListeners()) {
                l.mouseWheelMoved(e);
            }
            return e;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getSource() instanceof Component) {
                MouseWheelEvent sended = sendWheelTo(e, DbGrid.this);
                if (!sended.isConsumed()) {
                    sendWheelTo(e, DbGrid.this.gridScroll);
                }
            }
        }
    }

    protected class TablesFocusManager implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getModifiers() == 0) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (e.getSource() == tlTable) {
                        if (blTable != null && blTable.getRowCount() > 0 && tlTable.getSelectionModel().getLeadSelectionIndex() == tlTable.getRowCount() - 1) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    int colIndex = tlTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                                    tlTable.clearSelection();
                                    blTable.getSelectionModel().setSelectionInterval(0, 0);
                                    blTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                    blTable.requestFocus();
                                }
                            });
                        }
                    } else if (e.getSource() == trTable) {
                        if (brTable != null && brTable.getRowCount() > 0 && trTable.getSelectionModel().getLeadSelectionIndex() == trTable.getRowCount() - 1) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    int colIndex = trTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                                    trTable.clearSelection();
                                    brTable.getSelectionModel().setSelectionInterval(0, 0);
                                    brTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                    brTable.requestFocus();
                                }
                            });
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (e.getSource() == blTable) {
                        if (tlTable != null && tlTable.getRowCount() > 0 && blTable.getSelectionModel().getLeadSelectionIndex() == 0) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    int colIndex = blTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                                    blTable.clearSelection();
                                    tlTable.getSelectionModel().setSelectionInterval(tlTable.getRowCount() - 1, tlTable.getRowCount() - 1);
                                    tlTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                    tlTable.requestFocus();
                                }
                            });
                        }
                    } else if (e.getSource() == brTable) {
                        if (trTable != null && trTable.getRowCount() > 0 && brTable.getSelectionModel().getLeadSelectionIndex() == 0) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    int colIndex = brTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                                    brTable.clearSelection();
                                    trTable.getSelectionModel().setSelectionInterval(trTable.getRowCount() - 1, trTable.getRowCount() - 1);
                                    trTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                    trTable.requestFocus();
                                }
                            });
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (e.getSource() == blTable) {
                        int blLeadIndex = blTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        int blColCount = blTable.getColumnModel().getColumnCount();
                        while (blLeadIndex < blColCount - 1 && GridTable.skipableColumn(blTable.getColumnModel().getColumn(blLeadIndex + 1))) {
                            blLeadIndex++;
                        }
                        if (brTable != null && brTable.getColumnCount() > 0
                                && (blLeadIndex == blColCount - 1
                                || (blLeadIndex == blColCount - 2 && blTable.getColumnModel().getColumn(blColCount - 1) instanceof AnchorTableColumn))) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    int lColIndex = 0;
                                    if (!(brTable.getColumnModel().getColumn(lColIndex) instanceof RowHeaderTableColumn)) {
                                        if (brTable.getColumnModel().getColumn(lColIndex) instanceof AnchorTableColumn) {
                                            lColIndex = 1;
                                        }
                                        while (GridTable.skipableColumn(brTable.getColumnModel().getColumn(lColIndex))) {
                                            lColIndex++;
                                        }
                                        if (lColIndex >= 0 && lColIndex < brTable.getColumnModel().getColumnCount()) {
                                            brTable.getColumnModel().getSelectionModel().setSelectionInterval(lColIndex, lColIndex);
                                            brTable.requestFocus();
                                        }
                                    }
                                }
                            });
                        }
                    } else if (e.getSource() == tlTable) {
                        int tlLeadIndex = tlTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        int tlColCount = tlTable.getColumnModel().getColumnCount();
                        while (tlLeadIndex < tlColCount - 1 && GridTable.skipableColumn(tlTable.getColumnModel().getColumn(tlLeadIndex + 1))) {
                            tlLeadIndex++;
                        }
                        if (trTable != null && trTable.getColumnCount() > 0 && (tlLeadIndex == tlColCount - 1 || (tlLeadIndex == tlColCount - 2 && tlTable.getColumnModel().getColumn(tlColCount - 1) instanceof AnchorTableColumn))) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    int lColIndex = 0;
                                    if (!(trTable.getColumnModel().getColumn(lColIndex) instanceof RowHeaderTableColumn)) {
                                        if (trTable.getColumnModel().getColumn(lColIndex) instanceof AnchorTableColumn) {
                                            lColIndex = 1;
                                        }
                                        while (GridTable.skipableColumn(trTable.getColumnModel().getColumn(lColIndex))) {
                                            lColIndex++;
                                        }
                                        if (lColIndex >= 0 && lColIndex < trTable.getColumnModel().getColumnCount()) {
                                            trTable.getColumnModel().getSelectionModel().setSelectionInterval(lColIndex, lColIndex);
                                            trTable.requestFocus();
                                        }
                                    }
                                }
                            });
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (e.getSource() == trTable) {
                        int trLeadIndex = trTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        while (trLeadIndex > 0 && GridTable.skipableColumn(trTable.getColumnModel().getColumn(trLeadIndex - 1))) {
                            trLeadIndex--;
                        }
                        if (tlTable != null && tlTable.getColumnCount() > 0
                                && (trLeadIndex == 0
                                || (trLeadIndex == 1 && (trTable.getColumnModel().getColumn(trLeadIndex - 1) instanceof AnchorTableColumn)))) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (tlTable.getColumnModel().getColumnCount() > 0) {
                                        int lColIndex = tlTable.getColumnModel().getColumnCount() - 1;
                                        if (!(tlTable.getColumnModel().getColumn(lColIndex) instanceof RowHeaderTableColumn)) {
                                            while (GridTable.skipableColumn(tlTable.getColumnModel().getColumn(lColIndex))) {
                                                lColIndex--;
                                            }
                                            if (lColIndex >= 0 && lColIndex < tlTable.getColumnModel().getColumnCount()) {
                                                tlTable.getColumnModel().getSelectionModel().setSelectionInterval(lColIndex, lColIndex);
                                                tlTable.requestFocus();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    } else if (e.getSource() == brTable) {
                        int brLeadIndex = brTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        while (brLeadIndex > 0 && GridTable.skipableColumn(brTable.getColumnModel().getColumn(brLeadIndex - 1))) {
                            brLeadIndex--;
                        }
                        if (blTable != null && blTable.getColumnCount() > 0
                                && (brLeadIndex == 0
                                || (brLeadIndex == 1 && brTable.getColumnModel().getColumn(brLeadIndex - 1) instanceof AnchorTableColumn))) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (blTable.getColumnModel().getColumnCount() > 0) {
                                        int lColIndex = blTable.getColumnModel().getColumnCount() - 1;
                                        if (!(blTable.getColumnModel().getColumn(lColIndex) instanceof RowHeaderTableColumn)) {
                                            while (GridTable.skipableColumn(blTable.getColumnModel().getColumn(lColIndex))) {
                                                lColIndex--;
                                            }
                                            if (lColIndex >= 0 && lColIndex < blTable.getColumnModel().getColumnCount()) {
                                                blTable.getColumnModel().getSelectionModel().setSelectionInterval(lColIndex, lColIndex);
                                                blTable.requestFocus();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    protected class DbGridDeleteSelectedAction extends AbstractAction {

        DbGridDeleteSelectedAction() {
            super();
            putValue(Action.NAME, DbControlsUtils.getLocalizedString(DbGridDeleteSelectedAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(DbGridDeleteSelectedAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return !rowsSelectionModel.isSelectionEmpty();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteRow();
        }
    }

    protected class DbGridInsertAction extends AbstractAction {

        DbGridInsertAction() {
            super();
            putValue(Action.NAME, DbControlsUtils.getLocalizedString(DbGridInsertAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(DbGridInsertAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            insertRow();
        }
    }

    protected class DbGridInsertChildAction extends AbstractAction {

        DbGridInsertChildAction() {
            super();
            putValue(Action.NAME, DbControlsUtils.getLocalizedString(DbGridInsertChildAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(DbGridInsertChildAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.ALT_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (insertable && !(rowsModel.getRowsRowset() instanceof ParametersRowset)) {
                    rowsSelectionModel.removeListSelectionListener(generalSelectionChangesReflector);
                    try {
                        if (rowsModel instanceof RowsetsTreedModel) {
                            int parentColIndex = ((RowsetsTreedModel) rowsModel).getParentFieldIndex();
                            Object parentColValue = null;
                            if (!rowsModel.getRowsRowset().isEmpty()
                                    && !rowsModel.getRowsRowset().isBeforeFirst()
                                    && !rowsModel.getRowsRowset().isAfterLast()) {
                                Object[] pkValues = rowsModel.getRowsRowset().getCurrentRow().getPKValues();
                                if (pkValues != null && pkValues.length == 1) {
                                    parentColValue = pkValues[0];
                                }
                            }
                            rowsModel.getRowsRowset().insert(parentColIndex, parentColValue);
                        } else {
                            rowsModel.getRowsRowset().insert();
                        }
                    } finally {
                        rowsSelectionModel.addListSelectionListener(generalSelectionChangesReflector);
                    }
                    Row insertedRow = rowsModel.getRowsRowset().getCurrentRow();
                    assert insertedRow.isInserted();
                    makeVisible(insertedRow);
                }
            } catch (Exception ex) {
                Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected class DbGridRowInfoAction extends AbstractAction {

        DbGridRowInfoAction() {
            super();
            putValue(Action.NAME, DbControlsUtils.getLocalizedString(DbGridRowInfoAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(DbGridRowInfoAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return !rowsSelectionModel.isSelectionEmpty();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!rowsModel.getRowsRowset().isEmpty()
                    && !rowsModel.getRowsRowset().isBeforeFirst()
                    && !rowsModel.getRowsRowset().isAfterLast()) {
                Row row = rowsModel.getRowsRowset().getCurrentRow();
                if (row != null) {
                    JOptionPane.showInputDialog(DbGrid.this, DbControlsUtils.getLocalizedString("rowPkValues"), (String) getValue(Action.SHORT_DESCRIPTION), JOptionPane.INFORMATION_MESSAGE, null, null, StringUtils.join(", ", StringUtils.toStringArray(row.getPKValues())));
                }
            }
        }
    }

    protected class DbGridGotoRowAction extends AbstractAction {

        DbGridGotoRowAction() {
            super();
            putValue(Action.NAME, DbControlsUtils.getLocalizedString(DbGridGotoRowAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(DbGridGotoRowAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!rowsModel.getRowsRowset().isEmpty()
                    && !rowsModel.getRowsRowset().isBeforeFirst()
                    && !rowsModel.getRowsRowset().isAfterLast()) {
                Row row = rowsModel.getRowsRowset().getCurrentRow();
                if (row != null) {
                    Object oInput = JOptionPane.showInputDialog(DbGrid.this, DbControlsUtils.getLocalizedString("rowPkValues"), (String) getValue(Action.SHORT_DESCRIPTION), JOptionPane.INFORMATION_MESSAGE, null, null, null);
                    if (oInput != null) {
                        try {
                            makeVisible(oInput);
                        } catch (Exception ex) {
                            Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    protected class DbGridCopyCellAction extends AbstractAction {

        DbGridCopyCellAction() {
            super();
            putValue(Action.NAME, DbControlsUtils.getLocalizedString(DbGridCopyCellAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(DbGridCopyCellAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder sb = new StringBuilder();
            String[][] cells = getGridView(false, false);
            for (int i = 0; i < cells.length; i++) {
                if (i != 0) {
                    sb.append("\n");
                }
                String[] row = cells[i];
                for (int j = 0; j < row.length; j++) {
                    if (j != 0) {
                        sb.append("\t");
                    }
                    String value = row[j];
                    sb.append(value);
                }

            }
            StringSelection ss = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
        }
    }

    protected class DbGridFindSomethingAction extends AbstractAction {

        protected JFrame findFrame;

        DbGridFindSomethingAction() {
            super();
            putValue(Action.NAME, DbControlsUtils.getLocalizedString(DbGridFindSomethingAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(DbGridFindSomethingAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(true);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                if (findFrame == null) {
                    findFrame = new JFrame(DbControlsUtils.getLocalizedString("Find"));
                    findFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    findFrame.getContentPane().setLayout(new BorderLayout());
                    findFrame.getContentPane().add(new GridSearchView(DbGrid.this), BorderLayout.CENTER);
                    findFrame.setIconImage(IconCache.getIcon("16x16/binocular.png").getImage());
                    findFrame.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
                    findFrame.setAlwaysOnTop(true);
                    findFrame.setLocationByPlatform(true);
                    findFrame.pack();
                }
                findFrame.setVisible(true);
            }
        }
    }

    protected void configureActions() {
        Action deleteAction = null;
        Action insertAction = null;
        Action insertChildAction = null;
        findSomethingAction = new DbGridFindSomethingAction();
        deleteAction = new DbGridDeleteSelectedAction();
        putAction(deleteAction);
        insertAction = new DbGridInsertAction();
        putAction(insertAction);
        insertChildAction = new DbGridInsertChildAction();
        putAction(insertChildAction);
        putAction(findSomethingAction);
        Action rowInfoAction = new DbGridRowInfoAction();
        putAction(rowInfoAction);
        Action goToRowAction = new DbGridGotoRowAction();
        putAction(goToRowAction);
        Action copyCellAction = new DbGridCopyCellAction();
        putAction(copyCellAction);
        fillInputMap(tlTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction, rowInfoAction, goToRowAction, copyCellAction);
        fillInputMap(trTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction, rowInfoAction, goToRowAction, copyCellAction);
        fillInputMap(blTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction, rowInfoAction, goToRowAction, copyCellAction);
        fillInputMap(brTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction, rowInfoAction, goToRowAction, copyCellAction);
    }

    protected void fillInputMap(InputMap aInputMap, Action... actions) {
        for (Action action : actions) {
            if (action != null) {
                if (action instanceof DbGridCopyCellAction) {
                    aInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_DOWN_MASK), action.getClass().getName());
                }
                KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
                if (keyStroke != null) {
                    aInputMap.put(keyStroke, action.getClass().getName());
                }
            }
        }
    }

    protected void checkDbGridActions() {
        ActionMap actionMap = getActionMap();
        if (actionMap != null) {
            for (Object lkey : actionMap.keys()) {
                if (lkey != null) {
                    Action action = actionMap.get(lkey);
                    action.setEnabled(action.isEnabled());
                }
            }
        }
    }

    protected void configureScriptScope() {
        if (scriptScope != null && scriptScope instanceof ScriptableObject) {
            for (ScriptableColumn scrCol : scriptableColumns) {
                if (eventThis instanceof NativeJavaHostObject && scrCol.getName() != null && !scrCol.getName().isEmpty()) {
                    eventThis.delete(scrCol.getName());
                    Object jsColumn = Context.javaToJS(scrCol, eventThis);
                    assert jsColumn instanceof Scriptable;
                    scrCol.setPublished((Scriptable) jsColumn);
                    ((NativeJavaHostObject) eventThis).defineProperty(scrCol.getName(), jsColumn);
                }
            }
        }
    }

    public CascadedStyle getStyle() {
        return style;
    }

    public Scriptable getScriptScope() {
        return scriptScope;
    }

    public void setScriptScope(Scriptable aScope) {
        scriptScope = aScope;
    }

    @Override
    public boolean isRuntime() {
        return model != null && model.isRuntime();
    }
    protected ApplicationModel<?, ?, ?, ?> model = null;

    @Override
    public ApplicationModel<?, ?, ?, ?> getModel() {
        return model;
    }

    @Override
    public void setModel(ApplicationModel<?, ?, ?, ?> aValue) {
        if (model != null) {
            model.getChangeSupport().removePropertyChangeListener(this);
        }
        model = aValue;
        if (model != null) {
            model.getChangeSupport().addPropertyChangeListener(this);
        }
    }

    public void fillByRowset(RowsetHostObject<?> aRowset) throws Exception {
        fillByRowset(aRowset, this, getWidth() - 20);
    }

    public static void fillByRowset(RowsetHostObject<?> aRowset, DbGrid aGrid, int aWidth) throws Exception {
        fillByEntity(((ScriptableRowset) aRowset.unwrap()).getEntity(), aGrid, aWidth);
    }

    public static void fillByEntity(ApplicationEntity<?, ?, ?> aEntity, DbGrid aGrid, int aWidth) throws Exception {
        ModelEntityRef queryEntityRef = new ModelEntityRef();
        queryEntityRef.setEntityId(aEntity.getEntityId());
        Rowset rowset = aEntity.getRowset();
        if (rowset == null) {
            throw new Exception("SQL Error (see log)");
        }
        int rowsetColumnsCount = rowset.getFields().getFieldsCount();
        List<DbGridColumn> colVector = new ArrayList<>(rowset.getFields().getFieldsCount());
        for (int i = 1; i <= rowsetColumnsCount; i++) {
            DbGridColumn column = new DbGridColumn();
            // parent of this column is missing and so we need to set style parent directly
            column.getHeaderStyle().setParent(aGrid.getStyle());

            column.setName(rowset.getFields().get(i).getName());
            int lwidth = aWidth / rowsetColumnsCount;
            if (lwidth >= column.getWidth()) {
                column.setWidth(lwidth);
            }
            if (aEntity.getQuery() != null) {
                Fields metadata = aEntity.getFields();
                Field columnMtd = metadata.get(rowset.getFields().get(i).getName());
                if (columnMtd != null) {
                    String description = columnMtd.getDescription();
                    if (description != null && !description.isEmpty()) {
                        column.setTitle(description);
                    }
                }
            }
            ModelElementRef fieldRef = queryEntityRef.copy();
            fieldRef.setFieldName(rowset.getFields().get(i).getName());
            column.setDatamodelElement(fieldRef);
            Class<?>[] compatibleControlsClasses = DbControlsUtils.getCompatibleControls(rowset.getFields().get(i).getTypeInfo().getSqlType());
            if (compatibleControlsClasses != null && compatibleControlsClasses.length > 0) {
                Class<?> lControlClass = compatibleControlsClasses[0];
                if (lControlClass != null) {
                    Class<?> infoClass = DbControlsUtils.getDesignInfoClass(lControlClass);
                    if (infoClass != null) {
                        DbControlDesignInfo cdi = (DbControlDesignInfo) infoClass.newInstance();
                        cdi.setDatamodelElement(fieldRef);
                        column.setControlInfo((DbControlDesignInfo) cdi);
                        colVector.add(column);
                        if (cdi instanceof DbDateDesignInfo) {
                            DbDateDesignInfo dateDi = (DbDateDesignInfo) cdi;
                            dateDi.setDateFormat(DbDate.DD_MM_YYYY_HH_MM_SS);
                        }
                    }
                }
            }
        }
        aGrid.setRowsDatasource(queryEntityRef);
        aGrid.setHeader(colVector);
        aGrid.configure();
    }

    @Undesignable
    public Function getOnCellRender() {
        return generalRowFunction;
    }

    public void setOnCellRender(Function aValue) {
        generalRowFunction = aValue;
        rowsModel.setGeneralCellsHandler(aValue);
    }
}
