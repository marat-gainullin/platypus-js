package com.eas.client.forms.components.model.grid;

import com.eas.client.forms.IconCache;
import com.bearsoft.gui.grid.columns.ConstrainedColumnModel;
import com.bearsoft.gui.grid.constraints.LinearConstraint;
import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.editing.InsettedTreeEditor;
import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.gui.grid.header.HeaderSplitter;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import com.bearsoft.gui.grid.rendering.InsettedTreeRenderer;
import com.bearsoft.gui.grid.rendering.TreeColumnLeadingComponent;
import com.bearsoft.gui.grid.rows.ConstrainedRowSorter;
import com.bearsoft.gui.grid.rows.TabularRowsSorter;
import com.bearsoft.gui.grid.rows.TreedRowsSorter;
import com.bearsoft.gui.grid.selection.ConstrainedListSelectionModel;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.eas.client.forms.Forms;
import com.eas.client.forms.HasComponentEvents;
import com.eas.client.forms.Widget;
import com.eas.client.forms.components.model.ArrayModelWidget;
import com.eas.client.forms.components.model.CellRenderEvent;
import com.eas.client.forms.components.model.ModelComponentDecorator;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.client.forms.components.model.grid.columns.RadioServiceColumn;
import com.eas.client.forms.components.model.grid.models.ArrayModel;
import com.eas.client.forms.components.model.grid.models.ArrayTableModel;
import com.eas.client.forms.components.model.grid.models.ArrayTreedModel;
import com.eas.client.forms.events.rt.ControlEventsIProxy;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.EventMethod;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelGrid extends JPanel implements ArrayModelWidget, TablesGridContainer, HasComponentEvents, Widget, HasPublished {

    protected JSObject published;

    public void injectPublished(JSObject aValue) {
        published = aValue;
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
            tlTable.setShowHorizontalLines(getShowHorizontalLines());
        }
        if (trTable != null) {
            trTable.setShowHorizontalLines(getShowHorizontalLines());
        }
        if (blTable != null) {
            blTable.setShowHorizontalLines(getShowHorizontalLines());
        }
        if (brTable != null) {
            brTable.setShowHorizontalLines(getShowHorizontalLines());
        }
    }

    protected void applyShowVerticalLines() {
        if (tlTable != null) {
            tlTable.setShowVerticalLines(getShowVerticalLines());
        }
        if (trTable != null) {
            trTable.setShowVerticalLines(getShowVerticalLines());
        }
        if (blTable != null) {
            blTable.setShowVerticalLines(getShowVerticalLines());
        }
        if (brTable != null) {
            brTable.setShowVerticalLines(getShowVerticalLines());
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

    /*
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
    */

    /**
     * Returns index of a row in the model. Index is in model coordinates. Index
     * is 0-based.
     *
     * @param anElement Element to calculate index for.
     * @return Index if row.
     * @throws RowsetException
     */
    @ScriptFunction
    public int row2Index(JSObject anElement) throws RowsetException {
        int idx = -1;
        if (deepModel instanceof TableFront2TreedModel<?>) {
            TableFront2TreedModel<JSObject> front = (TableFront2TreedModel<JSObject>) deepModel;
            idx = front.getIndexOf(anElement);
        } else if (deepModel instanceof ArrayTableModel) {
            ArrayTableModel tmodel = (ArrayTableModel) deepModel;
            idx = tmodel.elementToIndex(anElement);
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
    @ScriptFunction
    public JSObject index2Row(int aIdx) throws RowsetException {
        JSObject element = null;
        if (deepModel instanceof TableFront2TreedModel<?>) {
            TableFront2TreedModel<JSObject> front = (TableFront2TreedModel<JSObject>) deepModel;
            element = front.getElementAt(aIdx);
        } else if (deepModel instanceof ArrayTableModel) {
            ArrayTableModel tmodel = (ArrayTableModel) deepModel;
            element = tmodel.indexToElement(aIdx);
        }
        return element;
    }

    protected void putAction(Action aAction) {
        if (aAction != null) {
            tlTable.getActionMap().put(aAction.getClass().getName(), aAction);
            trTable.getActionMap().put(aAction.getClass().getName(), aAction);
            blTable.getActionMap().put(aAction.getClass().getName(), aAction);
            brTable.getActionMap().put(aAction.getClass().getName(), aAction);
        }
    }

    private void configureTreedView() {
        if (rowsModel instanceof ArrayTreedModel && columnModel.getColumnCount() > 0) {
            TableColumn tCol = columnModel.getColumn(0);
            tCol.setCellRenderer(new InsettedTreeRenderer<>(tCol.getCellRenderer(), new TreeColumnLeadingComponent<>(deepModel, false, closedFolderIcon, openFolderIcon, leafIcon)));
            tCol.setCellEditor(new InsettedTreeEditor<>(tCol.getCellEditor(), new TreeColumnLeadingComponent<>(deepModel, true, closedFolderIcon, openFolderIcon, leafIcon)));
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
            tlTable.setShowOddRowsInOtherColor(getShowOddRowsInOtherColor());
        }
        if (trTable != null) {
            trTable.setShowOddRowsInOtherColor(getShowOddRowsInOtherColor());
        }
        if (blTable != null) {
            blTable.setShowOddRowsInOtherColor(getShowOddRowsInOtherColor());
        }
        if (brTable != null) {
            brTable.setShowOddRowsInOtherColor(getShowOddRowsInOtherColor());
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

    @ScriptFunction
    @Designable(category = "model")
    public String getParentField() {
        return parentField;
    }

    @ScriptFunction
    public void setParentField(String aValue) {
        if (parentField == null ? aValue != null : !parentField.equals(aValue)) {
            boolean wasTree = isTreeConfigured();
            parentField = aValue;
            boolean isTree = isTreeConfigured();
            if (wasTree != isTree) {
                applyRows();
            }
        }
    }

    @ScriptFunction
    @Designable(category = "model")
    public String getChildrenField() {
        return childrenField;
    }

    @ScriptFunction
    public void setChildrenField(String aValue) {
        if (childrenField == null ? aValue != null : !childrenField.equals(aValue)) {
            boolean wasTree = isTreeConfigured();
            childrenField = aValue;
            boolean isTree = isTreeConfigured();
            if (wasTree != isTree) {
                applyRows();
            }
        }
    }

    public final boolean isTreeConfigured() {
        return parentField != null && !parentField.isEmpty()
                && childrenField != null && !childrenField.isEmpty();
    }
    //
    public static final int CELLS_CACHE_SIZE = 65536;// 2^16
    public static final Color FIXED_COLOR = new Color(154, 204, 255);
    public static Icon processIcon = IconCache.getIcon("16x16/process-indicator.gif");
    protected TablesFocusManager tablesFocusManager = new TablesFocusManager();
    protected TablesMousePropagator tablesMousePropagator = new TablesMousePropagator();
    // design
    protected Icon openFolderIcon;
    protected Icon closedFolderIcon;
    protected Icon leafIcon;
    protected List<GridColumnsNode> header = new ArrayList<>();
    protected int rowsHeight = 20;
    protected boolean showVerticalLines = true;
    protected boolean showHorizontalLines = true;
    protected boolean showOddRowsInOtherColor = true;
    protected boolean editable = true;
    protected boolean insertable = true;
    protected boolean deletable = true;
    protected Color oddRowsColor;
    protected Color gridColor;
    // data
    protected ArrayModel rowsModel;
    protected TableModel deepModel;
    protected TabularRowsSorter<? extends TableModel> rowSorter;
    protected Set<JSObject> processedRows = new HashSet<>();
    protected JSObject data;
    // tree info
    protected String parentField;
    protected String childrenField;
    // rows column info
    protected int frozenRows;
    protected int frozenColumns;
    // view
    protected TableColumnModel columnModel;
    protected JSObject generalOnRender;
    // selection
    protected ListSelectionModel rowsSelectionModel;
    protected ListSelectionModel columnsSelectionModel;
    protected GeneralSelectionChangesReflector generalSelectionChangesReflector;
    // visual components
    protected LinearConstraint topRowsConstraint = new LinearConstraint(0, -1);
    protected LinearConstraint bottomRowsConstraint = new LinearConstraint(0, Integer.MAX_VALUE);
    protected JLabel progressIndicator;
    protected JScrollPane gridScroll;
    protected MultiLevelHeader lheader;
    protected MultiLevelHeader rheader;
    protected GridTable tlTable;
    protected GridTable trTable;
    protected GridTable blTable;
    protected GridTable brTable;
    protected JPanel tlPanel = new JPanel(new BorderLayout());
    protected JPanel trPanel = new JPanel(new BorderLayout());
    protected JPanel blPanel = new JPanel(new BorderLayout());
    protected JPanel brPanel;
    /*
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
     */
    // actions
    protected Action findSomethingAction;

    public ModelGrid() {
        super();
        // TODO: remove ROWS_HEADER_TYPE and substitute it with ServiceColumn component
        // Columns configuration
        columnsSelectionModel = new DefaultListSelectionModel();
        columnModel = new DefaultTableColumnModel();
        columnModel.setSelectionModel(columnsSelectionModel);
        columnModel.setColumnSelectionAllowed(true);
        // rows configuration
        rowsSelectionModel = new DefaultListSelectionModel();
        rowsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        generalSelectionChangesReflector = new GeneralSelectionChangesReflector();
        rowsSelectionModel.addListSelectionListener(generalSelectionChangesReflector);

        // constrained layer models setup
        tlTable = new GridTable(null, this);
        trTable = new GridTable(null, this);
        blTable = new GridTable(tlTable, this);
        brTable = new GridTable(trTable, this);
        brPanel = new GridTableScrollablePanel(brTable);

        tlTable.setOpaque(false);
        trTable.setOpaque(false);
        blTable.setOpaque(false);
        brTable.setOpaque(false);

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
        // grid columns setup.
        // left header setup
        lheader = new MultiLevelHeader();
        lheader.setTable(tlTable);
        lheader.setColumnModel(tlTable.getColumnModel());
        tlTable.getTableHeader().setResizingAllowed(true);
        lheader.setSlaveHeaders(tlTable.getTableHeader(), blTable.getTableHeader());
        lheader.setRowSorter(rowSorter);
        // right header setup
        rheader = new MultiLevelHeader();
        rheader.setTable(trTable);
        rheader.setColumnModel(trTable.getColumnModel());
        trTable.getTableHeader().setResizingAllowed(true);
        rheader.setSlaveHeaders(trTable.getTableHeader(), brTable.getTableHeader());
        rheader.setRowSorter(rowSorter);

        // Tables are enclosed in panels to avoid table's stupid efforts
        // to configure it's parent scroll pane.
        tlPanel.add(lheader, BorderLayout.NORTH);
        tlPanel.add(tlTable, BorderLayout.CENTER);
        trPanel.add(rheader, BorderLayout.NORTH);
        trPanel.add(trTable, BorderLayout.CENTER);
        blPanel.add(blTable, BorderLayout.CENTER);
        //brPanel.add(brTable, BorderLayout.CENTER);
        tlPanel.setOpaque(false);
        trPanel.setOpaque(false);
        blPanel.setOpaque(false);
        brPanel.setOpaque(false);

        progressIndicator = new JLabel(processIcon);
        progressIndicator.setOpaque(false);
        progressIndicator.setVisible(false);
        gridScroll = new JScrollPane();
        gridScroll.setOpaque(false);
        gridScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, tlPanel);
        gridScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, progressIndicator);
        gridScroll.setColumnHeaderView(trPanel);
        gridScroll.getColumnHeader().addChangeListener(new ColumnHeaderScroller(gridScroll.getColumnHeader(), gridScroll.getViewport()));
        gridScroll.setRowHeaderView(blPanel);
        gridScroll.getRowHeader().addChangeListener(new RowHeaderScroller(gridScroll.getRowHeader(), gridScroll.getViewport()));
        gridScroll.setViewportView(brPanel);
        gridScroll.getViewport().setOpaque(false);
        gridScroll.getColumnHeader().setOpaque(false);
        gridScroll.getRowHeader().setOpaque(false);

        setLayout(new BorderLayout());
        add(gridScroll, BorderLayout.CENTER);
        //
        lheader.setNeightbour(rheader);
        rheader.setNeightbour(lheader);

        applyColumns();
        applyRows();
        configureActions();
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
        setupStyle();
    }

    protected final void setupStyle() {
        JTable tbl = new JTable();
        gridColor = tbl.getGridColor();
        setFont(tbl.getFont());
        Color uiColor = tbl.getBackground();
        setBackground(new Color(uiColor.getRed(), uiColor.getGreen(), uiColor.getBlue(), uiColor.getAlpha()));
        uiColor = tbl.getForeground();
        setForeground(new Color(uiColor.getRed(), uiColor.getGreen(), uiColor.getBlue(), uiColor.getAlpha()));

        Object uiOpenedIcon = UIManager.get("Tree.openIcon");
        Object uiClosedIcon = UIManager.get("Tree.closedIcon");
        Object uiLeafIcon = UIManager.get("Tree.leafIcon");
        if (uiOpenedIcon instanceof Icon) {
            openFolderIcon = (Icon) uiOpenedIcon;
        } else {
            openFolderIcon = IconCache.getIcon("16x16/folder-horizontal-open.png");
        }
        if (uiClosedIcon instanceof Icon) {
            closedFolderIcon = (Icon) uiClosedIcon;
        } else {
            closedFolderIcon = IconCache.getIcon("16x16/folder-horizontal.png");
        }
        if (uiLeafIcon instanceof Icon) {
            leafIcon = (Icon) uiLeafIcon;
        } else {
            leafIcon = IconCache.getIcon("16x16/status-offline.png");
        }
    }

    private static final String ON_RENDER_JSDOC = ""
            + "/**\n"
            + " * General render event handler.\n"
            + " * This hanler be called on each cell's rendering in the case when no render handler is provided for the conrete column.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_RENDER_JSDOC)
    @EventMethod(eventClass = CellRenderEvent.class)
    @Undesignable
    public JSObject getOnRender() {
        return generalOnRender;
    }

    @ScriptFunction
    public void setOnRender(JSObject aValue) {
        generalOnRender = aValue;
        if (rowsModel != null) {
            rowsModel.setGeneralOnRender(aValue);
        }
    }

    @ScriptFunction
    public void setShowHorizontalLines(boolean aValue) {
        showHorizontalLines = aValue;
        applyShowHorizontalLines();
    }

    @ScriptFunction
    public void setShowVerticalLines(boolean aValue) {
        showVerticalLines = aValue;
        applyShowVerticalLines();
    }

    private static final String ODD_ROW_COLOR_JSDOC = ""
            + "/**\n"
            + "* Odd rows color.\n"
            + "*/";

    @ScriptFunction(jsDoc = ODD_ROW_COLOR_JSDOC)
    @Designable(category = "appearance")
    public Color getOddRowsColor() {
        return oddRowsColor;
    }

    @ScriptFunction
    public void setOddRowsColor(Color aValue) {
        oddRowsColor = aValue;
        applyOddRowsColor();
    }

    private static final String SHOW_HORIZONTAL_LINES_JSDOC = ""
            + "/**\n"
            + "* Determines if grid shows horizontal lines.\n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_HORIZONTAL_LINES_JSDOC)
    @Designable(category = "appearance")
    public boolean getShowHorizontalLines() {
        return showHorizontalLines;
    }

    private static final String SHOW_VERTICAL_LINES_JSDOC = ""
            + "/**\n"
            + "* Determines if grid shows vertical lines.\n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_VERTICAL_LINES_JSDOC)
    @Designable(category = "appearance")
    public boolean getShowVerticalLines() {
        return showVerticalLines;
    }

    private static final String SHOW_ODD_ROWS_IN_OTHER_COLOR_JSDOC = ""
            + "/**\n"
            + "* Determines if grid shows odd rows if other color.\n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_ODD_ROWS_IN_OTHER_COLOR_JSDOC)
    @Designable(category = "appearance")
    public boolean getShowOddRowsInOtherColor() {
        return showOddRowsInOtherColor;
    }

    @ScriptFunction
    public void setShowOddRowsInOtherColor(boolean aValue) {
        showOddRowsInOtherColor = aValue;
        applyShowOddRowsInOtherColor();
    }

    private static final String GRID_COLOR_JSDOC = ""
            + "/**\n"
            + "* The color of the grid.\n"
            + "*/";

    @ScriptFunction(jsDoc = GRID_COLOR_JSDOC)
    @Designable(category = "appearance")
    public Color getGridColor() {
        return gridColor;
    }

    @ScriptFunction
    public void setGridColor(Color aValue) {
        gridColor = aValue;
        applyGridColor();
    }

    private static final String ROWS_HEIGHT_JSDOC = ""
            + "/**\n"
            + "* The height of grid's rows.\n"
            + "*/";

    @ScriptFunction(jsDoc = ROWS_HEIGHT_JSDOC)
    @Designable(category = "appearance")
    public int getRowsHeight() {
        return rowsHeight;
    }

    @ScriptFunction
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

    @ScriptFunction
    public void setEditable(boolean aValue) {
        editable = aValue;
        applyEditable();
    }

    private static final String EDITABLE_JSDOC = ""
            + "/**\n"
            + "* Determines if gris cells are editable.\n"
            + "*/";

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    @Designable(category = "editing")
    public boolean isEditable() {
        return editable;
    }

    @ScriptFunction
    public void setInsertable(boolean aValue) {
        insertable = aValue;
    }

    private static final String INSERTABLE_JSDOC = ""
            + "/**\n"
            + "* Determines if grid allows row insertion.\n"
            + "*/";

    @ScriptFunction(jsDoc = INSERTABLE_JSDOC)
    @Designable(category = "editing")
    public boolean isInsertable() {
        return insertable;
    }

    @ScriptFunction
    public void setDeletable(boolean aValue) {
        deletable = aValue;
    }

    private static final String DELETABLE_JSDOC = ""
            + "/**\n"
            + "* Determines if grid allows to delete rows.\n"
            + "*/";

    @ScriptFunction(jsDoc = DELETABLE_JSDOC)
    @Designable(category = "editing")
    public boolean isDeletable() {
        return deletable;
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

    public List<JSObject> getSelected() throws Exception {
        List<JSObject> selectedRows = new ArrayList<>();
        if (deepModel != null) {// design time is only the case
            for (int i = 0; i < deepModel.getRowCount(); i++) {
                if (rowsSelectionModel.isSelectedIndex(i)) {
                    JSObject element = index2Row(rowSorter.convertRowIndexToModel(i));
                    selectedRows.add(element);
                }
            }
        }
        return selectedRows;
    }

    @ScriptFunction
    @Override
    public JPopupMenu getComponentPopupMenu() {
        return super.getComponentPopupMenu();
    }

    @ScriptFunction
    @Override
    public void setComponentPopupMenu(JPopupMenu aPopup) {
        super.setComponentPopupMenu(aPopup);
        applyComponentPopupMenu();
    }

    @ScriptFunction
    @Override
    public void setBackground(Color aValue) {
        super.setBackground(aValue);
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

    @ScriptFunction(jsDoc = GET_NEXT_FOCUSABLE_COMPONENT_JSDOC)
    @Override
    public JComponent getNextFocusableComponent() {
        return (JComponent) super.getNextFocusableComponent();
    }

    @ScriptFunction
    @Override
    public void setNextFocusableComponent(JComponent aValue) {
        super.setNextFocusableComponent(aValue);
    }

    protected String errorMessage;

    @ScriptFunction(jsDoc = ERROR_JSDOC)
    @Override
    public String getError() {
        return errorMessage;
    }

    @ScriptFunction
    @Override
    public void setError(String aValue) {
        errorMessage = aValue;
    }

    @ScriptFunction
    @Override
    public Color getBackground() {
        return super.getBackground();
    }

    @ScriptFunction
    @Override
    public void setForeground(Color aValue) {
        super.setForeground(aValue);
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

    @ScriptFunction
    @Override
    public Color getForeground() {
        return super.getForeground();
    }

    @ScriptFunction
    @Override
    public void setFont(Font aValue) {
        super.setFont(aValue);
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

    @ScriptFunction
    @Override
    public Font getFont() {
        return super.getFont();
    }

    @ScriptFunction(jsDoc = LEFT_JSDOC)
    @Override
    public int getLeft() {
        return super.getLocation().x;
    }

    @ScriptFunction
    @Override
    public void setLeft(int aValue) {
        if (super.getParent() != null && super.getParent().getLayout() instanceof MarginLayout) {
            MarginLayout.ajustLeft(this, aValue);
        }
        super.setLocation(aValue, getTop());
    }

    @ScriptFunction(jsDoc = TOP_JSDOC)
    @Override
    public int getTop() {
        return super.getLocation().y;
    }

    @ScriptFunction
    @Override
    public void setTop(int aValue) {
        if (super.getParent() != null && super.getParent().getLayout() instanceof MarginLayout) {
            MarginLayout.ajustTop(this, aValue);
        }
        super.setLocation(getLeft(), aValue);
    }

    @ScriptFunction(jsDoc = WIDTH_JSDOC)
    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @ScriptFunction
    @Override
    public void setWidth(int aValue) {
        Widget.setWidth(this, aValue);
    }

    @ScriptFunction(jsDoc = HEIGHT_JSDOC)
    @Override
    public int getHeight() {
        return super.getHeight();
    }

    @ScriptFunction
    @Override
    public void setHeight(int aValue) {
        Widget.setHeight(this, aValue);
    }

    @ScriptFunction(jsDoc = FOCUS_JSDOC)
    @Override
    public void focus() {
        super.requestFocus();
    }

    @ScriptFunction(jsDoc = VISIBLE_JSDOC)
    @Override
    public boolean getVisible() {
        return super.isVisible();
    }

    @ScriptFunction
    @Override
    public void setVisible(boolean aValue) {
        super.setVisible(aValue);
    }

    @ScriptFunction(jsDoc = FOCUSABLE_JSDOC)
    @Override
    public boolean getFocusable() {
        return super.isFocusable();
    }

    @ScriptFunction
    @Override
    public void setFocusable(boolean aValue) {
        super.setFocusable(aValue);
    }

    @ScriptFunction(jsDoc = ENABLED_JSDOC)
    @Override
    public boolean getEnabled() {
        return super.isEnabled();
    }

    @ScriptFunction
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        applyEnabled();
    }

    @ScriptFunction
    @Override
    public String getToolTipText() {
        return super.getToolTipText();
    }

    @ScriptFunction
    @Override
    public void setToolTipText(String aValue) {
        if (aValue != null && aValue.isEmpty()) {
            aValue = null;
        }
        super.setToolTipText(aValue);
        applyToolTipText();
    }

    @ScriptFunction(jsDoc = OPAQUE_TEXT_JSDOC)
    @Override
    public boolean getOpaque() {
        return super.isOpaque();
    }

    @ScriptFunction
    @Override
    public void setOpaque(boolean aValue) {
        super.setOpaque(aValue);
    }

    @ScriptFunction
    @Designable(category = "appearance")
    public int getFrozenColumns() {
        return frozenColumns;
    }

    @ScriptFunction
    public void setFrozenColumns(int aValue) throws Exception {
        if (frozenColumns != aValue) {
            frozenColumns = aValue;
            applyColumns();
            applyHeader();
            applyFrozenDecor();
        }
    }

    @ScriptFunction
    @Designable(category = "appearance")
    public int getFrozenRows() {
        return frozenRows;
    }

    @ScriptFunction
    public void setFrozenRows(int aValue) {
        if (frozenRows != aValue) {
            frozenRows = aValue;
            // lightweight apply
            topRowsConstraint.setMax(frozenRows - 1);
            bottomRowsConstraint.setMin(frozenRows);
            // heavy apply
            //applyRows();
            applyFrozenDecor();
        }
    }

    @ScriptFunction
    @Designable(category = "model")
    public JSObject getData() {
        return data;
    }

    @ScriptFunction
    public void setData(JSObject aValue) {
        if (data != aValue) {
            data = aValue;
            if (rowsModel != null) {
                rowsModel.setElements(data);
            }
        }
    }

    protected void applyRows() {
        if (rowsModel != null) {
            rowsModel.setElements(null);
        }
        if (isTreeConfigured()) {
            rowsModel = new ArrayTreedModel(columnModel, data, parentField, childrenField, generalOnRender);
            deepModel = new TableFront2TreedModel<>((ArrayTreedModel) rowsModel);
            rowSorter = new TreedRowsSorter<>((TableFront2TreedModel<JSObject>) deepModel, rowsSelectionModel);
        } else {
            rowsModel = new ArrayTableModel(columnModel, data, generalOnRender);
            deepModel = (TableModel) rowsModel;
            rowSorter = new TabularRowsSorter<>((ArrayTableModel) deepModel, rowsSelectionModel);
        }
        TableModel generalModel = new CachingTableModel(deepModel, CELLS_CACHE_SIZE);

        // rows constraints setup
        topRowsConstraint = new LinearConstraint(0, frozenRows - 1);
        bottomRowsConstraint = new LinearConstraint(frozenRows, Integer.MAX_VALUE);
        tlTable.setSelectionModel(new DefaultListSelectionModel());
        trTable.setSelectionModel(new DefaultListSelectionModel());
        blTable.setSelectionModel(new DefaultListSelectionModel());
        brTable.setSelectionModel(new DefaultListSelectionModel());
        tlTable.setRowSorter(null);
        trTable.setRowSorter(null);
        blTable.setRowSorter(null);
        brTable.setRowSorter(null);
        tlTable.setModel(new DefaultTableModel());
        trTable.setModel(new DefaultTableModel());
        blTable.setModel(new DefaultTableModel());
        brTable.setModel(new DefaultTableModel());

        tlTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, topRowsConstraint));
        trTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, topRowsConstraint));
        blTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, bottomRowsConstraint));
        brTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, bottomRowsConstraint));
        //
        tlTable.setModel(generalModel);
        trTable.setModel(generalModel);
        blTable.setModel(generalModel);
        brTable.setModel(generalModel);

        tlTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, topRowsConstraint));
        trTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, topRowsConstraint));
        blTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, bottomRowsConstraint));
        brTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, bottomRowsConstraint));
        configureTreedView();
    }

    @Undesignable
    public List<GridColumnsNode> getHeader() {
        return header;
    }

    public void setHeader(List<GridColumnsNode> aValue) throws Exception {
        if (header != aValue) {
            header = aValue;
            applyHeader();
        }
    }

    protected void applyFrozenDecor() {
        tlPanel.setBorder(new MatteBorder(0, 0, frozenRows > 0 ? 1 : 0, frozenColumns > 0 ? 1 : 0, FIXED_COLOR));
        trPanel.setBorder(new MatteBorder(0, 0, frozenRows > 0 ? 1 : 0, 0, FIXED_COLOR));
        blPanel.setBorder(new MatteBorder(0, 0, 0, frozenColumns > 0 ? 1 : 0, FIXED_COLOR));
    }

    protected void applyColumns() {
        // columns constraints setup
        LinearConstraint leftColsConstraint = new LinearConstraint(0, frozenColumns - 1);
        LinearConstraint rightColsConstraint = new LinearConstraint(frozenColumns, Integer.MAX_VALUE);
        // tables column models setup
        tlTable.setColumnModel(new ConstrainedColumnModel(columnModel, leftColsConstraint));
        tlTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, leftColsConstraint));
        trTable.setColumnModel(new ConstrainedColumnModel(columnModel, rightColsConstraint));
        trTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, rightColsConstraint));
        brTable.setColumnModel(new ConstrainedColumnModel(columnModel, rightColsConstraint));
        brTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, rightColsConstraint));
        blTable.setColumnModel(new ConstrainedColumnModel(columnModel, leftColsConstraint));
        blTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, leftColsConstraint));
        // headers setup
        lheader.setColumnModel(tlTable.getColumnModel());
        rheader.setColumnModel(trTable.getColumnModel());
    }

    protected void applyHeader() throws Exception {
        // set header
        List<GridColumnsNode> lgroups = HeaderSplitter.split(header, 0, frozenColumns - 1);
        List<GridColumnsNode> rgroups = HeaderSplitter.split(header, frozenColumns, Integer.MAX_VALUE);
        lheader.setRoots(lgroups);
        rheader.setRoots(rgroups);
        lheader.regenerate();
        rheader.regenerate();
    }

    /*
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
     */
    public void insertRow() {
        /*
         try {
         if (insertable && !(rowsModel.getRowsRowset() instanceof ParametersRowset)) {
         rowsSelectionModel.removeListSelectionListener(generalSelectionChangesReflector);
         try {
         if (rowsModel instanceof ArrayTreedModel) {
         int parentColIndex = ((ArrayTreedModel) rowsModel).getParentFieldIndex();
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
         if (insertedRow.isInserted()) {
         makeVisible(insertedRow);
         }
         }
         } catch (Exception ex) {
         Logger.getLogger(ModelGrid.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
    }

    public void deleteRow() {
        /*
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
         Logger.getLogger(ModelGrid.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
    }

    protected class GeneralSelectionChangesReflector implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (rowsSelectionModel.getLeadSelectionIndex() != -1) {
                try {
                    if (!try2StopAnyEditing()) {
                        try2CancelAnyEditing();
                    }
                    repaint();
                } catch (Exception ex) {
                    Logger.getLogger(ModelGrid.class.getName()).log(Level.SEVERE, null, ex);
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
            if (!working && frozenColumns > 0) {
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

    public void addColumn(ModelColumn aColumn) throws Exception {
        addColumn(columnModel.getColumnCount(), aColumn);
    }

    public void removeColumn(int aIndex) throws Exception {
        removeColumn((ModelColumn) columnModel.getColumn(aIndex));
    }

    @ScriptFunction
    public void addColumn(int aIndex, ModelColumn aColumn) throws Exception {
        columnModel.addColumn(aColumn);
        columnModel.moveColumn(columnModel.getColumnCount() - 1, aIndex);
        // edit header...
        // apply changes        
        applyColumns();
        applyHeader();
    }

    @ScriptFunction
    public void removeColumn(ModelColumn aColumn) throws Exception {
        columnModel.removeColumn(aColumn);
        // edit header...
        // apply changes        
        applyColumns();
        applyHeader();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        int width = d.width;
        int height = d.height;
        if (width < ModelComponentDecorator.WIDGETS_DEFAULT_WIDTH) {
            width = ModelComponentDecorator.WIDGETS_DEFAULT_WIDTH;
        }
        if (height < ModelComponentDecorator.WIDGETS_DEFAULT_HEIGHT) {
            height = ModelComponentDecorator.WIDGETS_DEFAULT_HEIGHT;
        }
        return new Dimension(width, height);
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

    @ScriptFunction
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

    @ScriptFunction
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

    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    public TableModel getDeepModel() {
        return deepModel;
    }

    public ArrayModel getRowsetsModel() {
        return rowsModel;
    }

    public TabularRowsSorter<? extends TableModel> getRowSorter() {
        return rowSorter;
    }

    public static ModelGrid getFirstDbGrid(Component aComp) {
        Component lParent = aComp;
        while (lParent != null && !(lParent instanceof ModelGrid)) {
            lParent = lParent.getParent();
        }
        if (lParent != null && lParent instanceof ModelGrid) {
            return (ModelGrid) lParent;
        }
        return null;
    }

    @Override
    public void addProcessedElement(JSObject aRow) {
        processedRows.add(aRow);
        progressIndicator.setVisible(true);
        gridScroll.repaint();
    }

    @Override
    public void removeProcessedElement(JSObject aRow) {
        processedRows.remove(aRow);
        if (processedRows.isEmpty()) {
            progressIndicator.setVisible(false);
        }
        gridScroll.repaint();
    }

    @Override
    public JSObject[] getProcessedElements() {
        return processedRows.toArray(new JSObject[]{});
    }

    @Override
    public boolean isElementProcessed(JSObject anElement) {
        return processedRows.contains(anElement);
    }

    private static final String SELECT_JSDOC = ""
            + "/**\n"
            + " * Selects the specified element.\n"
            + " * @param instance Entity's instance to be selected.\n"
            + " */";

    @ScriptFunction(jsDoc = SELECT_JSDOC, params = {"instance"})
    public void select(JSObject anElement) throws Exception {
        if (anElement != null) {
            int idx = row2Index(anElement);
            if (idx != -1) {
                rowsSelectionModel.addSelectionInterval(idx, idx);
            }
        }
    }

    private static final String UNSELECT_JSDOC = ""
            + "/**\n"
            + " * Unselects the specified element.\n"
            + " * @param instance Entity's instance to be unselected\n"
            + " */";

    @ScriptFunction(jsDoc = UNSELECT_JSDOC, params = {"instance"})
    public void unselect(JSObject anElement) throws Exception {
        if (anElement != null) {
            int idx = row2Index(anElement);
            if (idx != -1) {
                rowsSelectionModel.removeSelectionInterval(idx, idx);
            }
        }
    }

    private static final String CLEAR_SELECTION_JSDOC = ""
            + "/**\n"
            + " * Clears current selection.\n"
            + " */";

    @ScriptFunction(jsDoc = CLEAR_SELECTION_JSDOC)
    public void clearSelection() {
        columnsSelectionModel.clearSelection();
        rowsSelectionModel.clearSelection();
    }

    public void clearColumns() {
        Collections.list(columnModel.getColumns()).stream().forEach((TableColumn aColumn) -> {
            columnModel.removeColumn(aColumn);
        });
    }

    public void addColumns(ModelColumn[] aColumns) {
        for (ModelColumn col : aColumns) {
            columnModel.addColumn(col);
        }
        applyColumns();
    }

    public void setColumns(ModelColumn[] aColumns) {
        clearColumns();
        addColumns(aColumns);
    }

    public ModelColumn[] getColumns() {
        ModelColumn[] res = new ModelColumn[columnModel.getColumnCount()];
        for (int i = 0; i < res.length; i++) {
            res[i] = (ModelColumn) columnModel.getColumn(i);
        }
        return res;
    }

    private static final String FIND_SOMETHING_JSDOC = ""
            + "/**\n"
            + "* Shows find dialog.\n"
            + "* @deprecated Use find() instead. \n"
            + "*/";

    @ScriptFunction(jsDoc = FIND_SOMETHING_JSDOC)
    public void find() {
        findSomethingAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "startFinding", 0));
    }
    /*
     public boolean ensureFetched(ApplicationEntity<?, ?, ?> aEntity, Field aParentField, Object aTargetId) throws Exception {
     if (deepModel instanceof TableFront2TreedModel) {
     TableFront2TreedModel<Row> front = (TableFront2TreedModel<Row>) deepModel;
     if (front.unwrap() instanceof ArrayTreedModel) {
     ArrayTreedModel rModel = (ArrayTreedModel) front.unwrap();
     Rowset r = aEntity.getRowset();
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
     */

    public boolean makeVisible(JSObject anElement) throws Exception {
        return makeVisible(anElement, true);
    }

    private static final String MAKE_VISIBLE_JSDOC = ""
            + "/**\n"
            + "* Makes specified instance visible.\n"
            + "* @param instance Entity's instance to make visible.\n"
            + "* @param need2select true to select the instance (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = MAKE_VISIBLE_JSDOC, params = {"instance", "need2select"})
    @Override
    public boolean makeVisible(JSObject anElement, boolean need2Select) throws Exception {
        if (anElement != null) {
            // Let's process possible tree paths
            if (rowsModel instanceof ArrayTreedModel) {
                assert deepModel instanceof TableFront2TreedModel<?>;
                TableFront2TreedModel<JSObject> front = (TableFront2TreedModel<JSObject>) deepModel;
                List<JSObject> path = front.buildPathTo(anElement);
                for (int i = 0; i < path.size() - 1; i++) {
                    front.expand(path.get(i), true);
                }
            }
            int modelIndex = row2Index(anElement);
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
    /*
     public boolean makeVisible(Object aId) throws Exception {
     return makeVisible(aId, true);
     }

     public boolean makeVisible(Object aId, boolean need2Select) throws Exception {
     aId = ScriptUtils.toJava(aId);
     aId = rowsModel.getRowsRowset().getConverter().convert2RowsetCompatible(aId, rowsModel.getRowsRowset().getFields().get(rowsModel.getPkLocator().getFields().get(0)).getTypeInfo());
     if (rowsModel.getPkLocator().find(aId)) {
     return makeVisible(rowsModel.getPkLocator().getRow(0), need2Select);
     }
     return false;
     }
     */

    public boolean isCurrentRow(JSObject anElement) {
        JSObject jsCursor = getCurrentRow();
        return jdk.nashorn.api.scripting.ScriptUtils.unwrap(jsCursor) == jdk.nashorn.api.scripting.ScriptUtils.unwrap(anElement);
    }

    protected JSObject getCurrentRow() {
        Object oCursor = rowsModel.getElements().getMember("cursor");
        return oCursor instanceof JSObject ? (JSObject) oCursor : null;
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
    /*
     private String transformCellValue(Object aValue, int aCol, boolean isData) {
     if (aValue != null) {
     Object value = null;
     if (isData) {
     if (aValue instanceof CellData) {
     CellData cd = (CellData) aValue;
     value = cd.getData();
     } else {
     value = aValue;
     }
     } else {
     TableColumn tc = getColumnModel().getColumn(aCol);
     TableCellRenderer renderer = tc.getCellRenderer();
     if (renderer instanceof DbCombo) {
     try {
     value = ((DbCombo) renderer).achiveDisplayValue(aValue instanceof CellData ? ((CellData) aValue).getData() : aValue);
     } catch (Exception ex) {
     Logger.getLogger(ModelGrid.class.getName()).log(Level.SEVERE, "Could not get cell value", ex);
     }
     } else {
     if (aValue instanceof CellData) {
     CellData cd = (CellData) aValue;
     value = cd.getDisplay() != null ? cd.getDisplay() : cd.getData();
     } else {
     value = aValue;
     }
     }
     }
     if (value != null) {
     return value.toString();
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
     if (tc.getWidth() > 0 && !(tc instanceof RowHeaderTableColumn)) {
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
     cells[i] = o;
     }
     return cells;
     }

     public Object[] getCells() {
     Object[] cells = convertView(getGridView(false, false));
     return cells;
     }

     public Object[] getSelectedCells() {
     Object[] selectedCells = convertView(getGridView(true, false));
     return selectedCells;
     }

     private String[][] getGridView(boolean selectedOnly, boolean isData) {
     TableModel cellsModel = getDeepModel();
     if (cellsModel != null) {
     int minRow = 0;
     int maxRow = cellsModel.getRowCount();
     int columnCount = cellsModel.getColumnCount();
     String[][] res = new String[maxRow][columnCount];
     ListSelectionModel rowSelecter = getRowsSelectionModel();
     for (int viewRow = minRow; viewRow < maxRow; viewRow++) {
     if (selectedOnly) {
     if (rowSelecter.isSelectedIndex(viewRow)) {
     res[viewRow] = transformRow(rowSorter.convertRowIndexToModel(viewRow), selectedOnly, isData);
     }
     } else {
     res[viewRow] = transformRow(rowSorter.convertRowIndexToModel(viewRow), selectedOnly, isData);
     }
     }
     return res;
     } else {
     return new String[0][0];
     }
     }
     */

    protected class TablesMousePropagator implements MouseListener, MouseMotionListener, MouseWheelListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelGrid.this);
                for (MouseListener l : ModelGrid.this.getMouseListeners()) {
                    l.mouseClicked(e);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelGrid.this);
                for (MouseListener l : ModelGrid.this.getMouseListeners()) {
                    l.mousePressed(e);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelGrid.this);
                for (MouseListener l : ModelGrid.this.getMouseListeners()) {
                    l.mouseReleased(e);
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelGrid.this);
                for (MouseListener l : ModelGrid.this.getMouseListeners()) {
                    l.mouseEntered(e);
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelGrid.this);
                for (MouseListener l : ModelGrid.this.getMouseListeners()) {
                    l.mouseExited(e);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelGrid.this);
                for (MouseMotionListener l : ModelGrid.this.getMouseMotionListeners()) {
                    l.mouseDragged(e);
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelGrid.this);
                for (MouseMotionListener l : ModelGrid.this.getMouseMotionListeners()) {
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
                MouseWheelEvent sended = sendWheelTo(e, ModelGrid.this);
                if (!sended.isConsumed()) {
                    sendWheelTo(e, ModelGrid.this.gridScroll);
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
                            int colIndex = tlTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                            EventQueue.invokeLater(() -> {
                                tlTable.clearSelection();
                                blTable.getSelectionModel().setSelectionInterval(0, 0);
                                blTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                blTable.requestFocus();
                            });
                        }
                    } else if (e.getSource() == trTable) {
                        if (brTable != null && brTable.getRowCount() > 0 && trTable.getSelectionModel().getLeadSelectionIndex() == trTable.getRowCount() - 1) {
                            EventQueue.invokeLater(() -> {
                                int colIndex = trTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                                trTable.clearSelection();
                                brTable.getSelectionModel().setSelectionInterval(0, 0);
                                brTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                brTable.requestFocus();
                            });
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (e.getSource() == blTable) {
                        if (tlTable != null && tlTable.getRowCount() > 0 && blTable.getSelectionModel().getLeadSelectionIndex() == 0) {
                            int colIndex = blTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                            EventQueue.invokeLater(() -> {
                                blTable.clearSelection();
                                tlTable.getSelectionModel().setSelectionInterval(tlTable.getRowCount() - 1, tlTable.getRowCount() - 1);
                                tlTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                tlTable.requestFocus();
                            });
                        }
                    } else if (e.getSource() == brTable) {
                        if (trTable != null && trTable.getRowCount() > 0 && brTable.getSelectionModel().getLeadSelectionIndex() == 0) {
                            int colIndex = brTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                            EventQueue.invokeLater(() -> {
                                brTable.clearSelection();
                                trTable.getSelectionModel().setSelectionInterval(trTable.getRowCount() - 1, trTable.getRowCount() - 1);
                                trTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                trTable.requestFocus();
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
                                && blLeadIndex == blColCount - 1) {
                            int lColIndex = 0;
                            if (!(brTable.getColumnModel().getColumn(lColIndex) instanceof RadioServiceColumn)) {
                                while (GridTable.skipableColumn(brTable.getColumnModel().getColumn(lColIndex))) {
                                    lColIndex++;
                                }
                                if (lColIndex >= 0 && lColIndex < brTable.getColumnModel().getColumnCount()) {
                                    final int colToSelect = lColIndex;
                                    EventQueue.invokeLater(() -> {
                                        brTable.getColumnModel().getSelectionModel().setSelectionInterval(colToSelect, colToSelect);
                                        brTable.requestFocus();
                                    });
                                }
                            }
                        }
                    } else if (e.getSource() == tlTable) {
                        int tlLeadIndex = tlTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        int tlColCount = tlTable.getColumnModel().getColumnCount();
                        while (tlLeadIndex < tlColCount - 1 && GridTable.skipableColumn(tlTable.getColumnModel().getColumn(tlLeadIndex + 1))) {
                            tlLeadIndex++;
                        }
                        if (trTable != null && trTable.getColumnCount() > 0 && (tlLeadIndex == tlColCount - 1)) {
                            int lColIndex = 0;
                            if (!(trTable.getColumnModel().getColumn(lColIndex) instanceof RadioServiceColumn)) {
                                while (GridTable.skipableColumn(trTable.getColumnModel().getColumn(lColIndex))) {
                                    lColIndex++;
                                }
                                if (lColIndex >= 0 && lColIndex < trTable.getColumnModel().getColumnCount()) {
                                    final int col2Select = lColIndex;
                                    EventQueue.invokeLater(() -> {
                                        trTable.getColumnModel().getSelectionModel().setSelectionInterval(col2Select, col2Select);
                                        trTable.requestFocus();
                                    });
                                }
                            }
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (e.getSource() == trTable) {
                        int trLeadIndex = trTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        while (trLeadIndex > 0 && GridTable.skipableColumn(trTable.getColumnModel().getColumn(trLeadIndex - 1))) {
                            trLeadIndex--;
                        }
                        if (tlTable != null && tlTable.getColumnCount() > 0
                                && trLeadIndex == 0) {
                            if (tlTable.getColumnModel().getColumnCount() > 0) {
                                int lColIndex = tlTable.getColumnModel().getColumnCount() - 1;
                                if (!(tlTable.getColumnModel().getColumn(lColIndex) instanceof RadioServiceColumn)) {
                                    while (GridTable.skipableColumn(tlTable.getColumnModel().getColumn(lColIndex))) {
                                        lColIndex--;
                                    }
                                    if (lColIndex >= 0 && lColIndex < tlTable.getColumnModel().getColumnCount()) {
                                        final int col2Select = lColIndex;
                                        EventQueue.invokeLater(() -> {
                                            tlTable.getColumnModel().getSelectionModel().setSelectionInterval(col2Select, col2Select);
                                            tlTable.requestFocus();
                                        });
                                    }
                                }
                            }
                        }
                    } else if (e.getSource() == brTable) {
                        int brLeadIndex = brTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        while (brLeadIndex > 0 && GridTable.skipableColumn(brTable.getColumnModel().getColumn(brLeadIndex - 1))) {
                            brLeadIndex--;
                        }
                        if (blTable != null && blTable.getColumnCount() > 0
                                && brLeadIndex == 0) {
                            if (blTable.getColumnModel().getColumnCount() > 0) {
                                int lColIndex = blTable.getColumnModel().getColumnCount() - 1;
                                if (!(blTable.getColumnModel().getColumn(lColIndex) instanceof RadioServiceColumn)) {
                                    while (GridTable.skipableColumn(blTable.getColumnModel().getColumn(lColIndex))) {
                                        lColIndex--;
                                    }
                                    if (lColIndex >= 0 && lColIndex < blTable.getColumnModel().getColumnCount()) {
                                        final int col2Select = lColIndex;
                                        EventQueue.invokeLater(() -> {
                                            blTable.getColumnModel().getSelectionModel().setSelectionInterval(col2Select, col2Select);
                                            blTable.requestFocus();
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
    /*
     protected class DbGridDeleteSelectedAction extends AbstractAction {

     DbGridDeleteSelectedAction() {
     super();
     putValue(Action.NAME, Form.getLocalizedString(DbGridDeleteSelectedAction.class.getSimpleName()));
     putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridDeleteSelectedAction.class.getSimpleName()));
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
     putValue(Action.NAME, Form.getLocalizedString(DbGridInsertAction.class.getSimpleName()));
     putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridInsertAction.class.getSimpleName()));
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
     putValue(Action.NAME, Form.getLocalizedString(DbGridInsertChildAction.class.getSimpleName()));
     putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridInsertChildAction.class.getSimpleName()));
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
     if (rowsModel instanceof ArrayTreedModel) {
     int parentColIndex = ((ArrayTreedModel) rowsModel).getParentFieldIndex();
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
     Logger.getLogger(ModelGrid.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     }

     protected class DbGridRowInfoAction extends AbstractAction {

     DbGridRowInfoAction() {
     super();
     putValue(Action.NAME, Form.getLocalizedString(DbGridRowInfoAction.class.getSimpleName()));
     putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridRowInfoAction.class.getSimpleName()));
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
     JOptionPane.showInputDialog(ModelGrid.this, Form.getLocalizedString("rowPkValues"), (String) getValue(Action.SHORT_DESCRIPTION), JOptionPane.INFORMATION_MESSAGE, null, null, StringUtils.join(", ", StringUtils.toStringArray(row.getPKValues())));
     }
     }
     }
     }

     protected class DbGridGotoRowAction extends AbstractAction {

     DbGridGotoRowAction() {
     super();
     putValue(Action.NAME, Form.getLocalizedString(DbGridGotoRowAction.class.getSimpleName()));
     putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridGotoRowAction.class.getSimpleName()));
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
     Object oInput = JOptionPane.showInputDialog(ModelGrid.this, Form.getLocalizedString("rowPkValues"), (String) getValue(Action.SHORT_DESCRIPTION), JOptionPane.INFORMATION_MESSAGE, null, null, null);
     if (oInput != null) {
     try {
     makeVisible(oInput);
     } catch (Exception ex) {
     Logger.getLogger(ModelGrid.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     }
     }
     }
     }

     protected class DbGridCopyCellAction extends AbstractAction {

     DbGridCopyCellAction() {
     super();
     putValue(Action.NAME, Form.getLocalizedString(DbGridCopyCellAction.class.getSimpleName()));
     putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridCopyCellAction.class.getSimpleName()));
     putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
     setEnabled(false);
     }

     @Override
     public boolean isEnabled() {
     return true;
     }

     @Override
     public void actionPerformed(ActionEvent e) {
     StringBuilder sbCells = new StringBuilder();
     String[][] cells = getGridView(true, false);
     for (int i = 0; i < cells.length; i++) {
     StringBuilder sbRow = new StringBuilder();
     String[] row = cells[i];
     for (int j = 0; j < row.length; j++) {
     String value = row[j];
     if (value != null) {
     if (sbRow.length() > 0) {
     sbRow.append("\t");
     }
     sbRow.append(value);
     }
     }
     if (sbRow.length() > 0) {
     if (sbCells.length() > 0) {
     sbCells.append("\n");
     }
     sbCells.append(sbRow);
     }
     }
     StringSelection ss = new StringSelection(sbCells.toString());
     Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
     }
     }
     */

    protected class ModelGridFindSomethingAction extends AbstractAction {

        protected JFrame findFrame;

        ModelGridFindSomethingAction() {
            super();
            putValue(Action.NAME, Forms.getLocalizedString(ModelGridFindSomethingAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, Forms.getLocalizedString(ModelGridFindSomethingAction.class.getSimpleName()));
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
                    findFrame = new JFrame(Forms.getLocalizedString("Find"));
                    findFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    findFrame.getContentPane().setLayout(new BorderLayout());
                    findFrame.getContentPane().add(new GridSearchView(ModelGrid.this), BorderLayout.CENTER);
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
        findSomethingAction = new ModelGridFindSomethingAction();
        putAction(findSomethingAction);
        /*
         deleteAction = new DbGridDeleteSelectedAction();
         putAction(deleteAction);
         insertAction = new DbGridInsertAction();
         putAction(insertAction);
         insertChildAction = new DbGridInsertChildAction();
         putAction(insertChildAction);
         Action rowInfoAction = new DbGridRowInfoAction();
         putAction(rowInfoAction);
         Action goToRowAction = new DbGridGotoRowAction();
         putAction(goToRowAction);
         Action copyCellAction = new DbGridCopyCellAction();
         putAction(copyCellAction);
         */
        fillInputMap(tlTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction/*, rowInfoAction, goToRowAction, copyCellAction*/);
        fillInputMap(trTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction/*, rowInfoAction, goToRowAction, copyCellAction*/);
        fillInputMap(blTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction/*, rowInfoAction, goToRowAction, copyCellAction*/);
        fillInputMap(brTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction/*, rowInfoAction, goToRowAction, copyCellAction*/);
    }

    protected void fillInputMap(InputMap aInputMap, Action... actions) {
        for (Action action : actions) {
            if (action != null) {
                /*
                 if (action instanceof DbGridCopyCellAction) {
                 aInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_DOWN_MASK), action.getClass().getName());
                 }
                 */
                KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
                if (keyStroke != null) {
                    aInputMap.put(keyStroke, action.getClass().getName());
                }
            }
        }
    }

    protected void checkModelGridActions() {
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

    // Native API
    @ScriptFunction(jsDoc = NATIVE_COMPONENT_JSDOC)
    @Undesignable
    @Override
    public JComponent getComponent() {
        return this;
    }

    @ScriptFunction(jsDoc = NATIVE_ELEMENT_JSDOC)
    @Undesignable
    @Override
    public Object getElement() {
        return null;
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

    protected ControlEventsIProxy eventsProxy = new ControlEventsIProxy(this);

    @ScriptFunction(jsDoc = ON_MOUSE_CLICKED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseClicked() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseClicked);
    }

    @ScriptFunction
    @Override
    public void setOnMouseClicked(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseClicked, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_DRAGGED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseDragged() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseDragged);
    }

    @ScriptFunction
    @Override
    public void setOnMouseDragged(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseDragged, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_ENTERED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseEntered() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseEntered);
    }

    @ScriptFunction
    @Override
    public void setOnMouseEntered(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseEntered, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_EXITED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseExited() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseExited);
    }

    @ScriptFunction
    @Override
    public void setOnMouseExited(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseExited, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_MOVED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseMoved() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseMoved);
    }

    @ScriptFunction
    @Override
    public void setOnMouseMoved(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseMoved, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_PRESSED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMousePressed() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mousePressed);
    }

    @ScriptFunction
    @Override
    public void setOnMousePressed(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mousePressed, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_RELEASED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseReleased() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseReleased);
    }

    @ScriptFunction
    @Override
    public void setOnMouseReleased(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseReleased, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_WHEEL_MOVED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseWheelMoved() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseWheelMoved);
    }

    @ScriptFunction
    @Override
    public void setOnMouseWheelMoved(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseWheelMoved, aValue);
    }

    @ScriptFunction(jsDoc = ON_ACTION_PERFORMED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.ActionEvent.class)
    @Undesignable
    @Override
    public JSObject getOnActionPerformed() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.actionPerformed);
    }

    @ScriptFunction
    @Override
    public void setOnActionPerformed(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.actionPerformed, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_HIDDEN_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentHidden() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentHidden);
    }

    @ScriptFunction
    @Override
    public void setOnComponentHidden(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentHidden, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_MOVED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentMoved() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentMoved);
    }

    @ScriptFunction
    @Override
    public void setOnComponentMoved(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentMoved, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_RESIZED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentResized() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentResized);
    }

    @ScriptFunction
    @Override
    public void setOnComponentResized(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentResized, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_SHOWN_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentShown() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentShown);
    }

    @ScriptFunction
    @Override
    public void setOnComponentShown(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentShown, aValue);
    }

    @ScriptFunction(jsDoc = ON_FOCUS_GAINED_JSDOC)
    @EventMethod(eventClass = FocusEvent.class)
    @Undesignable
    @Override
    public JSObject getOnFocusGained() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.focusGained);
    }

    @ScriptFunction
    @Override
    public void setOnFocusGained(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.focusGained, aValue);
    }

    @ScriptFunction(jsDoc = ON_FOCUS_LOST_JSDOC)
    @EventMethod(eventClass = FocusEvent.class)
    @Undesignable
    @Override
    public JSObject getOnFocusLost() {
        return eventsProxy != null ? eventsProxy.getHandlers().get(ControlEventsIProxy.focusLost) : null;
    }

    @ScriptFunction
    @Override
    public void setOnFocusLost(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.focusLost, aValue);
    }

    @ScriptFunction(jsDoc = ON_KEY_PRESSED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    @Undesignable
    @Override
    public JSObject getOnKeyPressed() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.keyPressed);
    }

    @ScriptFunction
    @Override
    public void setOnKeyPressed(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.keyPressed, aValue);
    }

    @ScriptFunction(jsDoc = ON_KEY_RELEASED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    @Undesignable
    @Override
    public JSObject getOnKeyReleased() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.keyReleased);
    }

    @ScriptFunction
    @Override
    public void setOnKeyReleased(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.keyReleased, aValue);
    }

    @ScriptFunction(jsDoc = ON_KEY_TYPED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    @Undesignable
    @Override
    public JSObject getOnKeyTyped() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.keyTyped);
    }

    @ScriptFunction
    @Override
    public void setOnKeyTyped(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.keyTyped, aValue);
    }

    // published parent
    @ScriptFunction(name = "parent", jsDoc = PARENT_JSDOC)
    @Override
    public Widget getParentWidget() {
        return Forms.lookupPublishedParent(this);
    }
}
