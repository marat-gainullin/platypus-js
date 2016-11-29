package com.eas.client.forms.components.model.grid;

import com.eas.client.forms.IconCache;
import com.bearsoft.gui.grid.columns.ConstrainedColumnModel;
import com.bearsoft.gui.grid.constraints.LinearConstraint;
import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.gui.grid.data.CollapseExpandListener;
import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.editing.InsettedTreeEditor;
import com.bearsoft.gui.grid.header.ColumnNodesContainer;
import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.gui.grid.header.HeaderSplitter;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import com.bearsoft.gui.grid.rendering.InsettedTreeRenderer;
import com.bearsoft.gui.grid.rendering.TreeColumnLeadingComponent;
import com.bearsoft.gui.grid.rows.ConstrainedRowSorter;
import com.bearsoft.gui.grid.rows.TabularRowsSorter;
import com.bearsoft.gui.grid.rows.TreedRowsSorter;
import com.bearsoft.gui.grid.selection.ConstrainedListSelectionModel;
import com.eas.client.events.SourcedEvent;
import com.eas.client.forms.Forms;
import com.eas.client.forms.HasComponentEvents;
import com.eas.client.forms.HasJsName;
import com.eas.client.forms.Widget;
import com.eas.client.forms.components.model.ArrayModelWidget;
import com.eas.client.forms.events.CellRenderEvent;
import com.eas.client.forms.components.model.ModelComponentDecorator;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.client.forms.components.model.grid.columns.RadioServiceColumn;
import com.eas.client.forms.components.model.grid.models.ArrayModel;
import com.eas.client.forms.components.model.grid.models.ArrayTableModel;
import com.eas.client.forms.components.model.grid.models.ArrayTreedModel;
import com.eas.client.forms.events.rt.ControlEventsIProxy;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.gui.ScriptColor;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.EventMethod;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import com.eas.util.ListenerRegistration;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.JSType;
import jdk.nashorn.internal.runtime.ScriptObject;

/**
 *
 * @author mg
 */
public class ModelGrid extends JPanel implements ColumnNodesContainer, ArrayModelWidget, TablesGridContainer, HasComponentEvents, Widget, HasPublished, HasJsName {

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

    public int viewIndexByElement(JSObject anElement) {
        int modelIndex = modelIndexByElement(anElement);
        return modelIndex != -1 ? rowSorter.convertRowIndexToView(modelIndex) : -1;
    }

    /**
     * Returns index of a row in the model. Index is in model coordinates. Index
     * is 0-based.
     *
     * @param anElement Element to calculate index for.
     * @return Index if row.
     */
    public int modelIndexByElement(JSObject anElement) {
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

    @Override
    public JSObject elementByViewIndex(int aViewIndex) {
        return elementByModelIndex(rowSorter.convertRowIndexToModel(aViewIndex));
    }

    /**
     * Returns row for particular Index. Index is in model's coordinates. Index
     * is 0-based.
     *
     * @param aIdx Index the row is to be calculated for.
     * @return Row's index;
     */
    @ScriptFunction
    @Override
    public JSObject elementByModelIndex(int aIdx) {
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
    protected TablesKeyboardManager tablesKeyboardManager = new TablesKeyboardManager();
    protected TablesMousePropagator tablesMousePropagator = new TablesMousePropagator();
    // design
    protected Icon openFolderIcon;
    protected Icon closedFolderIcon;
    protected Icon leafIcon;
    protected List<GridColumnsNode> header = new ArrayList<>();
    protected int rowsHeight = 30;
    protected boolean showVerticalLines = true;
    protected boolean showHorizontalLines = true;
    protected boolean showOddRowsInOtherColor = true;
    protected boolean editable = true;
    protected boolean insertable = true;
    protected boolean deletable = true;
    protected Color oddRowsColor = new ScriptColor("#efefef");
    protected Color gridColor;
    // data
    protected ArrayModel rowsModel;
    protected TableModel deepModel;
    protected CachingTableModel cachingModel;
    protected TabularRowsSorter<? extends TableModel> rowSorter;
    protected Set<JSObject> processedRows = new HashSet<>();
    protected JSObject data;
    protected String field;
    protected String cursorProperty = "cursor";
    protected JSObject boundToData;
    protected JSObject boundToCursor;
    // tree
    protected String parentField;
    protected String childrenField;
    protected ListenerRegistration collapseExpandListener;
    protected JSObject onCollapse;
    protected JSObject onExpand;
    protected JSObject onAfterRender;
    // rows and columns
    protected int frozenRows;
    protected int frozenColumns;
    // view
    protected TableColumnModel columnModel;
    protected JSObject generalOnRender;
    // selection
    protected ListSelectionModel rowsSelectionModel;
    protected ListSelectionModel columnsSelectionModel;
    protected GeneralSelectionChangesReflector generalSelectionChangesReflector;
    // focus
    protected TablesFocusPropagator focusPropagator = new TablesFocusPropagator();
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

    // actions
    protected Action findSomethingAction;

    public ModelGrid() {
        super();
        // columns configuration
        columnsSelectionModel = new DefaultListSelectionModel();
        columnModel = new DefaultTableColumnModel();
        columnModel.setSelectionModel(columnsSelectionModel);
        columnModel.setColumnSelectionAllowed(true);
        // rows configuration
        rowsSelectionModel = new DefaultListSelectionModel();
        rowsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //    selection
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
        tlTable.addKeyListener(tablesKeyboardManager);
        trTable.addKeyListener(tablesKeyboardManager);
        blTable.addKeyListener(tablesKeyboardManager);
        brTable.addKeyListener(tablesKeyboardManager);
        // focus events setup
        tlTable.addFocusListener(focusPropagator);
        trTable.addFocusListener(focusPropagator);
        blTable.addFocusListener(focusPropagator);
        brTable.addFocusListener(focusPropagator);
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
        lheader.setBackground(new JButton().getBackground());
        lheader.setTable(tlTable);
        lheader.setColumnModel(tlTable.getColumnModel());
        tlTable.getTableHeader().setResizingAllowed(true);
        lheader.setSlaveHeaders(tlTable.getTableHeader(), blTable.getTableHeader());
        // right header setup
        rheader = new MultiLevelHeader();
        rheader.setBackground(new JButton().getBackground());
        rheader.setTable(trTable);
        rheader.setColumnModel(trTable.getColumnModel());
        trTable.getTableHeader().setResizingAllowed(true);
        rheader.setSlaveHeaders(trTable.getTableHeader(), brTable.getTableHeader());

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

    protected boolean headerVisible = true;

    @ScriptFunction
    public boolean isHeaderVisible() {
        return headerVisible;
    }

    @ScriptFunction
    public void setHeaderVisible(boolean aValue) {
        headerVisible = aValue;
    }

    protected boolean draggableRows;

    @ScriptFunction
    protected boolean isDraggableRows() {
        return draggableRows;
    }

    @ScriptFunction
    public void setDraggableRows(boolean aValue) {
        draggableRows = aValue;
    }

    private static final String ON_RENDER_JSDOC = ""
            + "/**\n"
            + " * General render event handler.\n"
            + " * This hanler is called on each cell's rendering in the case when no render handler is provided for the specific column.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_RENDER_JSDOC)
    @EventMethod(eventClass = CellRenderEvent.class)
    @Undesignable
    @Override
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

    private static final String ON_AFTER_RENDER_JSDOC = ""
            + "/**\n"
            + " * After render event handler.\n"
            + " * This handler is called after cells' rendering is complete.\n"
            + " * It is called in asynchonous manner."
            + " */";

    @ScriptFunction(jsDoc = ON_AFTER_RENDER_JSDOC)
    @EventMethod(eventClass = SourcedEvent.class)
    @Undesignable
    public JSObject getOnAfterRender() {
        return onAfterRender;
    }

    @ScriptFunction
    public void setOnAfterRender(JSObject aValue) {
        onAfterRender = aValue;
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
            + " * Odd rows color.\n"
            + " */";

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
            + " * Determines if grid shows horizontal lines.\n"
            + " */";

    @ScriptFunction(jsDoc = SHOW_HORIZONTAL_LINES_JSDOC)
    @Designable(category = "appearance")
    public boolean getShowHorizontalLines() {
        return showHorizontalLines;
    }

    private static final String SHOW_VERTICAL_LINES_JSDOC = ""
            + "/**\n"
            + " * Determines if grid shows vertical lines.\n"
            + " */";

    @ScriptFunction(jsDoc = SHOW_VERTICAL_LINES_JSDOC)
    @Designable(category = "appearance")
    public boolean getShowVerticalLines() {
        return showVerticalLines;
    }

    private static final String SHOW_ODD_ROWS_IN_OTHER_COLOR_JSDOC = ""
            + "/**\n"
            + " * Determines if grid shows odd rows if other color.\n"
            + " */";

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
            + " * The color of the grid.\n"
            + " */";

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
            + " * The height of grid's rows.\n"
            + " */";

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
            + " * Determines if gris cells are editable.\n"
            + " */";

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
            + " * Determines if grid allows row insertion.\n"
            + " */";

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
            + " * Determines if grid allows to delete rows.\n"
            + " */";

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
                    JSObject element = elementByViewIndex(i);
                    selectedRows.add(element);
                }
            }
        }
        return selectedRows;
    }

    @Undesignable
    @ScriptFunction(name = "selected")
    public JSObject getJsSelected() throws Exception {
        List<JSObject> selectedRows = getSelected();
        JSObject jsArray = Scripts.getSpace().makeArray();
        JSObject jsPush = (JSObject) jsArray.getMember("push");
        selectedRows.forEach((Object aItem) -> {
            jsPush.call(jsArray, new Object[]{aItem});
        });
        return jsArray;
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

    @ScriptFunction(jsDoc = JS_NAME_DOC)
    @Override
    public String getName() {
        return super.getName();
    }

    @ScriptFunction
    @Override
    public void setName(String name) {
        super.setName(name);
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
        if (!brTable.requestFocusInWindow()) {
            if (!trTable.requestFocusInWindow()) {
                if (!blTable.requestFocusInWindow()) {
                    tlTable.requestFocusInWindow();
                }
            }
        }
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

    protected void unbindCursor() {
        if (boundToCursor != null) {
            Scripts.unlisten(boundToCursor);
            boundToCursor = null;
        }
    }

    protected void bindCursor(JSObject aModelData) {
        if (aModelData != null) {
            boundToCursor = Scripts.getSpace().listen(aModelData, cursorProperty, new AbstractJSObject() {

                @Override
                public Object call(Object thiz, Object... args) {
                    ModelGrid.this.invalidate();
                    ModelGrid.this.repaint();
                    return null;
                }

            });
        }
    }

    protected void bind() {
        if (data != null) {
            if (Scripts.isInitialized()) {
                Object modelData = field != null && !field.isEmpty() ? ModelWidget.getPathData(data, field) : data;
                if (rowsModel != null) {
                    modelData = modelData instanceof ScriptObject ? ScriptUtils.wrap((ScriptObject) modelData) : modelData;
                    if (modelData instanceof JSObject) {
                        JSObject jsModelData = (JSObject) modelData;
                        unbindCursor();
                        rowsModel.setData(jsModelData);
                        bindCursor(jsModelData);
                    } else {
                        unbindCursor();
                        rowsModel.setData(null);
                    }
                }
                if (field != null && !field.isEmpty()) {
                    boundToData = Scripts.getSpace().listen(data, field, new AbstractJSObject() {

                        @Override
                        public Object call(Object thiz, Object... args) {
                            Object newModelData = ModelWidget.getPathData(data, field);
                            if (rowsModel != null) {
                                newModelData = newModelData instanceof ScriptObject ? ScriptUtils.wrap((ScriptObject) newModelData) : newModelData;
                                if (newModelData instanceof JSObject) {
                                    JSObject jsModelData = (JSObject) newModelData;
                                    unbindCursor();
                                    rowsModel.setData(jsModelData);
                                    bindCursor(jsModelData);
                                } else {
                                    unbindCursor();
                                    rowsModel.setData(null);
                                }
                            }
                            return null;
                        }

                    });
                }
            } else {
                rowsModel.setData(data);
            }
        }
    }

    protected void unbind() {
        if (boundToData != null) {
            Scripts.unlisten(boundToData);
            boundToData = null;
        }
        rowsModel.setData(null);
    }

    @ScriptFunction
    @Designable(category = "model")
    public JSObject getData() {
        return data;
    }

    @ScriptFunction
    public void setData(JSObject aValue) {
        if (data != null ? !data.equals(aValue) : aValue != null) {
            unbind();
            data = aValue;
            bind();
        }
    }

    @ScriptFunction
    @Designable(category = "model")
    public String getField() {
        return field;
    }

    @ScriptFunction
    public void setField(String aValue) {
        if (field == null ? aValue != null : !field.equals(aValue)) {
            unbind();
            field = aValue;
            bind();
        }
    }

    public static final String CURSOR_FIELD_JSDOC = ""
            + "/**\n"
            + " * Determines wich property of ModelGrid's collection is responsible of \"current\" item. Its default value is \"cursor\".\n"
            + " */";

    @ScriptFunction(jsDoc = CURSOR_FIELD_JSDOC)
    @Designable(category = "model")
    public String getCursorProperty() {
        return cursorProperty;
    }

    @ScriptFunction
    public void setCursorProperty(String aValue) {
        if (aValue != null && !cursorProperty.equals(aValue)) {
            unbind();
            cursorProperty = aValue;
            bind();
        }
    }

    private static final String REDRAW_JSDOC = ""
            + "/**\n"
            + " * Redraw the component.\n"
            + " */";

    @ScriptFunction(jsDoc = REDRAW_JSDOC)
    @Override
    public void redraw() {
        ListSelectionModel rowsSelection = saveRowsSelection();
        ListSelectionModel columnsSelection = saveColumnsSelection();
        rowsModel.fireElementsChanged();
        invalidate();
        repaint();
        restoreRowsSelection(rowsSelection);
        restoreColumnsSelection(columnsSelection);
    }

    protected void applyRows() {
        if (collapseExpandListener != null) {
            collapseExpandListener.remove();
        }
        if (rowsModel != null) {
            rowsModel.setData(null);
        }
        Object oModelData = field != null && !field.isEmpty() ? ModelWidget.getPathData(data, field) : data;
        JSObject modelData = oModelData instanceof JSObject ? (JSObject) oModelData : null;
        if (isTreeConfigured()) {
            rowsModel = new ArrayTreedModel(columnModel, modelData, parentField, childrenField, generalOnRender);
            deepModel = new TableFront2TreedModel<>((ArrayTreedModel) rowsModel);
            collapseExpandListener = ((TableFront2TreedModel<JSObject>) deepModel).addCollapseExpandListener(new CollapseExpandListener<JSObject>() {

                @Override
                public void collapsed(JSObject aItem) {
                    if (onCollapse != null) {
                        onCollapse.call(published, new Object[]{new com.eas.client.forms.events.ItemEvent(ModelGrid.this, aItem).getPublished()});
                    }
                }

                @Override
                public void expanded(JSObject aItem) {
                    if (onExpand != null) {
                        onExpand.call(published, new Object[]{new com.eas.client.forms.events.ItemEvent(ModelGrid.this, aItem).getPublished()});
                    }
                }

            });
            rowSorter = new TreedRowsSorter<>((TableFront2TreedModel<JSObject>) deepModel, rowsSelectionModel);
        } else {
            rowsModel = new ArrayTableModel(columnModel, modelData, generalOnRender);
            deepModel = (TableModel) rowsModel;
            rowSorter = new TabularRowsSorter<>((ArrayTableModel) deepModel, rowsSelectionModel);
        }
        cachingModel = new CachingTableModel(deepModel, CELLS_CACHE_SIZE);

        lheader.setRowSorter(rowSorter);
        rheader.setRowSorter(rowSorter);
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
        tlTable.setModel(cachingModel);
        trTable.setModel(cachingModel);
        blTable.setModel(cachingModel);
        brTable.setModel(cachingModel);

        tlTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, topRowsConstraint));
        trTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, topRowsConstraint));
        blTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, bottomRowsConstraint));
        brTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, bottomRowsConstraint));
        configureTreedView();
    }

    private static final String CHANGED_JS_DOC = ""
            + "/**\n"
            + " * Notifies the grid about data have been changed.\n"
            + " * @param aChanged Array of changed objects.\n"
            + " */";

    @ScriptFunction(jsDoc = CHANGED_JS_DOC, params = "aChanged")
    public void changed(JSObject aChangedItems) {
        if (aChangedItems.hasMember("length") && JSType.toNumber(aChangedItems.getMember("length")) > 0) {
            rowsModel.fireElementsDataChanged();
        }
    }

    private static final String ADDED_JS_DOC = ""
            + "/**\n"
            + " * Notifies the grid about some elements have been added to grid's rows array.\n"
            + " * @param aAdded Array of added objects.\n"
            + " */";

    @ScriptFunction(jsDoc = ADDED_JS_DOC, params = "aAdded")
    public void added(JSObject aAddedItems) {
        rowsModel.fireElementsChanged();
    }

    private static final String REMOVED_JS_DOC = ""
            + "/**\n"
            + " * Notifies the grid about some elements have been removed from grid's rows array.\n"
            + " * @param aRemoved Array of removed objects.\n"
            + " */";

    @ScriptFunction(jsDoc = REMOVED_JS_DOC, params = "aRemoved")
    public void removed(JSObject aRemovedItems) {
        rowsModel.fireElementsChanged();
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
        reindexColumns();
    }

    @Override
    public void reindexColumns() {
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn col = columnModel.getColumn(i);
            col.setModelIndex(i);
        }
        if (cachingModel != null) {
            cachingModel.clearCache();
        }
    }

    protected void applyHeader() {
        // set header
        List<GridColumnsNode> lgroups = HeaderSplitter.split(header, 0, frozenColumns - 1);
        List<GridColumnsNode> rgroups = HeaderSplitter.split(header, frozenColumns, Integer.MAX_VALUE);
        lheader.setRoots(lgroups);
        rheader.setRoots(rgroups);
        lheader.regenerate();
        rheader.regenerate();
    }

    public void insertElementAtCursor() {
        try {
            if (insertable && rowsModel.getData() != null && rowsModel.getData().hasMember("splice")) {
                ListSelectionModel columnSelection = saveColumnsSelection();
                JSObject ldata = rowsModel.getData();
                JSObject jsSplice = (JSObject) ldata.getMember("splice");
                JSObject jsIndexOf = (JSObject) ldata.getMember("indexOf");
                Object oElementClass = ldata.getMember("elementClass");
                JSObject jsElementClass = oElementClass instanceof JSObject && ((JSObject) oElementClass).isFunction() ? (JSObject) oElementClass : null;
                JSObject jsCreated = jsElementClass != null ? (JSObject) jsElementClass.newObject(new Object[]{}) : Scripts.getSpace().makeObj();
                JSObject jsCursor = getCurrentRow();
                rowsSelectionModel.removeListSelectionListener(generalSelectionChangesReflector);
                try {
                    if (rowsModel instanceof ArrayTreedModel) {
                        Object parentValue = jsCursor != null ? ModelWidget.getPathData(jsCursor, parentField) : null;
                        int length = JSType.toInteger(ldata.getMember("length"));
                        int cursorAt = JSType.toInteger(jsIndexOf.call(ldata, new Object[]{jsCursor}));
                        int insertAt = cursorAt >= 0 && cursorAt < length ? cursorAt + 1 : length;
                        jsSplice.call(ldata, new Object[]{insertAt, 0, jsCreated});
                        ModelWidget.setPathData(jsCreated, parentField, parentValue);
                    } else {
                        int length = deepModel.getRowCount();
                        int cursorAt = rowsSelectionModel.getLeadSelectionIndex();
                        int insertAt = cursorAt >= 0 && cursorAt < length ? cursorAt + 1 : length;
                        jsSplice.call(ldata, new Object[]{insertAt, 0, jsCreated});
                    }
                } finally {
                    rowsSelectionModel.addListSelectionListener(generalSelectionChangesReflector);
                }
                EventQueue.invokeLater(() -> {
                    try {
                        makeVisible(jsCreated);
                        restoreColumnsSelection(columnSelection);
                    } catch (Exception ex) {
                        Logger.getLogger(ModelGrid.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelGrid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected ListSelectionModel saveRowsSelection() {
        ListSelectionModel savedSelection = new DefaultListSelectionModel();
        for (int viewRowIndex = rowsSelectionModel.getMinSelectionIndex(); viewRowIndex <= rowsSelectionModel.getMaxSelectionIndex(); viewRowIndex++) {
            if (rowsSelectionModel.isSelectedIndex(viewRowIndex)) {
                // We have to act upon view coordinates here!
                savedSelection.addSelectionInterval(viewRowIndex, viewRowIndex);
            }
        }
        return savedSelection;
    }

    protected ListSelectionModel saveColumnsSelection() {
        ListSelectionModel savedSelection = new DefaultListSelectionModel();
        for (int viewColumnIndex = columnsSelectionModel.getMinSelectionIndex(); viewColumnIndex <= columnsSelectionModel.getMaxSelectionIndex(); viewColumnIndex++) {
            if (columnsSelectionModel.isSelectedIndex(viewColumnIndex)) {
                // We have to act upon view coordinates here!
                savedSelection.addSelectionInterval(viewColumnIndex, viewColumnIndex);
            }
        }
        return savedSelection;
    }

    public void deleteSelectedElements() {
        if (deletable && rowsModel.getData() != null && rowsModel.getData().hasMember("splice")) {
            JSObject ldata = rowsModel.getData();
            JSObject jsSplice = (JSObject) ldata.getMember("splice");
            ListSelectionModel wasSeletedRows = saveRowsSelection();
            ListSelectionModel wasSeletedColumns = saveColumnsSelection();
            try {
                Set<Object> elements = new HashSet<>();
                for (int viewRowIndex = rowsSelectionModel.getMinSelectionIndex(); viewRowIndex <= rowsSelectionModel.getMaxSelectionIndex(); viewRowIndex++) {
                    if (rowsSelectionModel.isSelectedIndex(viewRowIndex)) {
                        // We have to act upon model coordinates here!
                        JSObject element = elementByViewIndex(viewRowIndex);
                        if (element != null) {
                            elements.add(element);
                        }
                    }
                }
                int length = JSType.toInteger(ldata.getMember("length"));
                for (int i = length - 1; i >= 0; i--) {
                    Object oElement = ldata.getSlot(i);
                    if (elements.contains(oElement)) {
                        jsSplice.call(ldata, new Object[]{i, 1});
                    }
                }
            } finally {
                EventQueue.invokeLater(() -> {
                    restoreRowsSelection(wasSeletedRows);
                    restoreColumnsSelection(wasSeletedColumns);
                });
            }
        }
    }

    protected void restoreRowsSelection(ListSelectionModel wasSeleted) {
        // We have to act upon view coordinates here!
        rowsSelectionModel.clearSelection();
        for (int viewRowIndex = wasSeleted.getMinSelectionIndex(); viewRowIndex <= wasSeleted.getMaxSelectionIndex(); viewRowIndex++) {
            if (viewRowIndex >= 0 && viewRowIndex < rowSorter.getViewRowCount()) {
                rowsSelectionModel.addSelectionInterval(viewRowIndex, viewRowIndex);
            } else if (viewRowIndex == rowSorter.getViewRowCount()) {
                rowsSelectionModel.addSelectionInterval(viewRowIndex - 1, viewRowIndex - 1);
            }
        }
    }

    protected void restoreColumnsSelection(ListSelectionModel aSeleted) {
        // We have to act upon view coordinates here!
        columnsSelectionModel.clearSelection();
        for (int viewColumnIndex = aSeleted.getMinSelectionIndex(); viewColumnIndex <= aSeleted.getMaxSelectionIndex(); viewColumnIndex++) {
            if (viewColumnIndex >= 0 && viewColumnIndex < columnModel.getColumnCount()) {
                columnsSelectionModel.addSelectionInterval(viewColumnIndex, viewColumnIndex);

            }
        }
    }

    protected class TablesFocusPropagator implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            FocusEvent fe = new FocusEvent(ModelGrid.this, e.getID(), e.isTemporary(), e.getOppositeComponent());
            for (FocusListener l : getFocusListeners()) {
                l.focusGained(fe);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            FocusEvent fe = new FocusEvent(ModelGrid.this, e.getID(), e.isTemporary(), e.getOppositeComponent());
            for (FocusListener l : getFocusListeners()) {
                l.focusLost(fe);
            }
        }

    }

    protected class GeneralSelectionChangesReflector implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            try {
                if (!try2StopAnyEditing()) {
                    try2CancelAnyEditing();
                }
                Object oModelData = field != null && !field.isEmpty() ? ModelWidget.getPathData(data, field) : data;
                JSObject modelData = oModelData instanceof JSObject ? (JSObject) oModelData : null;
                if (modelData != null) {
                    JSObject jsNewCursor = rowsSelectionModel.getLeadSelectionIndex() != -1 ? elementByViewIndex(rowsSelectionModel.getLeadSelectionIndex()) : null;
                    if (modelData.hasMember(cursorProperty)) {
                        modelData.setMember(cursorProperty, jsNewCursor);
                    }
                }
                repaint();
            } catch (Exception ex) {
                Logger.getLogger(ModelGrid.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (onItemSelected != null) {
                try {
                    JSObject jsItem = elementByViewIndex(rowsSelectionModel.getLeadSelectionIndex());
                    onItemSelected.call(getPublished(), new Object[]{new com.eas.client.forms.events.ItemEvent(ModelGrid.this, jsItem).getPublished()});
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
            super();
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

    public void refreshColumnModel() {
        List<GridColumnsNode> leaves = new ArrayList<>();
        MultiLevelHeader.achieveLeaves(header, leaves);
        for (int i = columnModel.getColumnCount() - 1; i >= 0; i--) {
            columnModel.removeColumn(columnModel.getColumn(i));
        }
        leaves.stream().sequential().forEach((GridColumnsNode aNode) -> {
            columnModel.addColumn(aNode.getTableColumn());
        });
    }

    protected boolean autoRefreshHeader = true;
    protected PropertyChangeListener columnNodesListener = (PropertyChangeEvent evt) -> {
        if (autoRefreshHeader) {
            refreshColumnModel();
            applyColumns();
            applyHeader();
        }
    };

    public boolean isAutoRefreshHeader() {
        return autoRefreshHeader;
    }

    public void setAutoRefreshHeader(boolean aValue) {
        autoRefreshHeader = aValue;
    }

    @ScriptFunction
    @Override
    public void addColumnNode(GridColumnsNode aColumn) {
        insertColumnNode(header.size(), aColumn);
    }

    @ScriptFunction
    public GridColumnsNode[] columnNodes() {
        return header.toArray(new GridColumnsNode[]{});
    }

    @ScriptFunction
    @Override
    public void removeColumnNode(GridColumnsNode aNode) {
        if (aNode != null) {
            aNode.getChangeSupport().removePropertyChangeListener("children", columnNodesListener);
            header.remove(aNode);
            if (autoRefreshHeader) {
                refreshColumnModel();
                applyColumns();
                applyHeader();
            }
        }
    }

    @ScriptFunction
    @Override
    public void insertColumnNode(int aIndex, GridColumnsNode aNode) {
        header.add(aIndex, aNode);
        aNode.getChangeSupport().removePropertyChangeListener("children", columnNodesListener);
        if (autoRefreshHeader) {
            refreshColumnModel();
            applyColumns();
            applyHeader();
        }
    }

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

    @Override
    public boolean isAutoRedraw() {
        Object oModelData = field != null && !field.isEmpty() ? ModelWidget.getPathData(data, field) : data;
        JSObject modelData = oModelData instanceof JSObject ? (JSObject) oModelData : null;
        return modelData != null && !likeEntity(modelData);
    }

    private boolean likeEntity(JSObject aCandidate) {
        return aCandidate.hasMember("onRequeried") && aCandidate.hasMember("append");
    }

    protected Runnable redrawEnqueued;

    @Override
    public void enqueueRedraw() {
        redrawEnqueued = () -> {
            if (redrawEnqueued == this) {
                redrawEnqueued = null;
                redraw();
            }
        };
        EventQueue.invokeLater(redrawEnqueued);
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
            int idx = modelIndexByElement(anElement);
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
            int idx = modelIndexByElement(anElement);
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
            + " * Shows find dialog.\n"
            + " * @deprecated Use find() instead. \n"
            + " */";

    @ScriptFunction(jsDoc = FIND_SOMETHING_JSDOC)
    public void find() {
        findSomethingAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "startFinding", 0));
    }

    public boolean makeVisible(JSObject anElement) throws Exception {
        return makeVisible(anElement, true);
    }

    private static final String MAKE_VISIBLE_JSDOC = ""
            + "/**\n"
            + " * Makes specified instance visible.\n"
            + " * @param instance Entity's instance to make visible.\n"
            + " * @param need2select true to select the instance (optional).\n"
            + " */";

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
            int modelIndex = modelIndexByElement(anElement);
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

    private static final String EXPANDED_JSDOC = ""
            + "/**\n"
            + " * Tests if node of specified .data array element is expanded.\n"
            + " * @param instance .data array element to test.\n"
            + " */";

    @ScriptFunction(jsDoc = EXPANDED_JSDOC, params = {"instance"})
    public boolean expanded(JSObject anElement) {
        if (deepModel instanceof TableFront2TreedModel<?>) {
            TableFront2TreedModel<JSObject> front = (TableFront2TreedModel<JSObject>) deepModel;
            return front.isExpanded(anElement);
        } else {
            return false;
        }
    }

    private static final String EXPAND_JSDOC = ""
            + "/**\n"
            + " * Makes node of specified .data array element expanded.\n"
            + " * @param instance .data array element to expand.\n"
            + " */";

    @ScriptFunction(jsDoc = EXPAND_JSDOC, params = {"instance"})
    public void expand(JSObject anElement) {
        if (deepModel instanceof TableFront2TreedModel<?>) {
            TableFront2TreedModel<JSObject> front = (TableFront2TreedModel<JSObject>) deepModel;
            front.expand(anElement, false);
        }
    }

    private static final String COLLAPSE_JSDOC = ""
            + "/**\n"
            + " * Makes node of specified .data array element collapsed.\n"
            + " * @param instance .data array element to collapsed.\n"
            + " */";

    @ScriptFunction(jsDoc = COLLAPSE_JSDOC, params = {"instance"})
    public void collapse(JSObject anElement) {
        if (deepModel instanceof TableFront2TreedModel<?>) {
            TableFront2TreedModel<JSObject> front = (TableFront2TreedModel<JSObject>) deepModel;
            front.collapse(anElement);
        }
    }

    private static final String TOGGLE_JSDOC = ""
            + "/**\n"
            + " * Makes node of specified .data array element expanded if it was already collapsed and collapsed otherwise.\n"
            + " * @param instance .data array element to expand or collpase.\n"
            + " */";

    @ScriptFunction(jsDoc = TOGGLE_JSDOC, params = {"instance"})
    public void toggle(JSObject anElement) {
        if (expanded(anElement)) {
            collapse(anElement);
        } else {
            expand(anElement);
        }
    }

    public boolean isCurrentRow(JSObject anElement) {
        JSObject jsCursor = getCurrentRow();
        return jsCursor != null ? jsCursor.equals(anElement) : false;
    }

    protected JSObject getCurrentRow() {
        Object oCursor = rowsModel.getData().getMember(cursorProperty);
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

    protected class TablesKeyboardManager implements KeyListener {

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

    protected class DeleteAtCursorAction extends AbstractAction {

        DeleteAtCursorAction() {
            super();
            putValue(Action.NAME, Forms.getLocalizedString(DeleteAtCursorAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, Forms.getLocalizedString(DeleteAtCursorAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return !rowsSelectionModel.isSelectionEmpty();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteSelectedElements();
        }
    }

    protected class InsertAtCursorAction extends AbstractAction {

        InsertAtCursorAction() {
            super();
            putValue(Action.NAME, Forms.getLocalizedString(InsertAtCursorAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, Forms.getLocalizedString(InsertAtCursorAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            insertElementAtCursor();
        }
    }

    /*
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
        deleteAction = new DeleteAtCursorAction();
        putAction(deleteAction);
        insertAction = new InsertAtCursorAction();
        putAction(insertAction);
        /*
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
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
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

    protected JSObject onItemSelected;

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Event that is fired when selection lead changes in this ModelGrid.\n"
            + " */")
    @EventMethod(eventClass = com.eas.client.forms.events.ItemEvent.class)
    @Undesignable
    public JSObject getOnItemSelected() {
        return onItemSelected;
    }

    @ScriptFunction
    public void setOnItemSelected(JSObject aValue) {
        if (onItemSelected != aValue) {
            onItemSelected = aValue;
        }
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Event that is fired when user collapses ModelGrid's row.\n"
            + " */")
    @EventMethod(eventClass = com.eas.client.forms.events.ItemEvent.class)
    @Undesignable
    public JSObject getOnCollapse() {
        return onCollapse;
    }

    @ScriptFunction
    public void setOnCollapse(JSObject aValue) {
        if (onCollapse != aValue) {
            onCollapse = aValue;
        }
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Event that is fired when user expands ModelGrid's row.\n"
            + " */")
    @EventMethod(eventClass = com.eas.client.forms.events.ItemEvent.class)
    @Undesignable
    public JSObject getOnExpand() {
        return onExpand;
    }

    @ScriptFunction
    public void setOnExpand(JSObject aValue) {
        if (onExpand != aValue) {
            onExpand = aValue;
        }
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
        return eventsProxy.getOnMouseWheelMoved();
    }

    @ScriptFunction
    @Override
    public void setOnMouseWheelMoved(JSObject aValue) {
        eventsProxy.setOnMouseWheelMoved(aValue);
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

    private static final String UNSORT = ""
            + "/**\n"
            + " * Clears sort on all columns, works only in HTML5"
            + " */";

    @ScriptFunction(jsDoc = UNSORT)
    public void unsort() {

    }
}
