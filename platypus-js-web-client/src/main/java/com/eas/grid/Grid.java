package com.eas.grid;

import java.util.ArrayList;
import java.util.List;

import com.eas.core.XElement;
import com.eas.grid.columns.Column;
import com.eas.grid.columns.header.HasSortList;
import com.eas.grid.columns.header.HeaderNode;
import com.eas.menu.MenuItemCheckBox;
import com.eas.ui.Focusable;
import com.eas.ui.PublishedColor;
import com.eas.ui.Widget;
import com.eas.ui.XDataTransfer;
import com.eas.menu.Menu;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;

/**
 *
 * @author mg
 */
public abstract class Grid extends Widget implements HasSortList, Focusable {

    public static final String RULER_STYLE = "grid-ruler";
    public static final String COLUMN_PHANTOM_STYLE = "grid-column-phantom";
    public static final int MINIMUM_COLUMN_WIDTH = 15;
    //
    protected StyleElement cellsStyleElement = Document.get().createStyleElement();
    protected StyleElement oddRowsStyleElement = Document.get().createStyleElement();
    protected StyleElement evenRowsStyleElement = Document.get().createStyleElement();
    protected StyleElement headerRowsStyleElement = Document.get().createStyleElement();
    protected String dynamicCellClassName = "grid-cell-" + Document.get().createUniqueId();
    protected String dynamicOddRowsClassName = "grid-odd-row-" + Document.get().createUniqueId();
    protected String dynamicEvenRowsClassName = "grid-even-row-" + Document.get().createUniqueId();
    protected String dynamicHeaderRowClassName = "grid-heder-row-" + Document.get().createUniqueId();
    protected Element headerLeftContainer = Document.get().createDivElement();
    protected GridSection headerLeft = new GridSection(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element headerRightContainer = Document.get().createDivElement();
    protected GridSection headerRight = new GridSection(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element frozenLeftContainer = Document.get().createDivElement();
    protected GridSection frozenLeft = new GridSection(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element frozenRightContainer = Document.get().createDivElement();
    protected GridSection frozenRight = new GridSection(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element scrollableLeftContainer = Document.get().createDivElement();
    protected GridSection scrollableLeft = new GridSection(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element scrollableRightContainer = Document.get().createDivElement();
    protected GridSection scrollableRight = new GridSection(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element footerLeftContainer = Document.get().createDivElement();
    protected GridSection footerLeft = new GridSection(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element footerRightContainer = Document.get().createDivElement();
    protected GridSection footerRight = new GridSection(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element ghostLine = Document.get().createDivElement();
    protected Element ghostColumn;
    protected ColumnDrag targetDraggedColumn;

    //
    protected Element columnsChevron = Document.get().createDivElement();
    //
    protected final ColumnSortList sortList = new ColumnSortList();
    private int headerRowsHeight = 30;
    protected int rowsHeight = 30;
    protected boolean showHorizontalLines = true;
    protected boolean showVerticalLines = true;
    protected boolean showOddRowsInOtherColor = true;
    protected PublishedColor gridColor;
    protected PublishedColor oddRowsColor = PublishedColor.create(241, 241, 241, 255);

    protected ListDataProvider dataProvider;

    protected int frozenColumns;
    protected int frozenRows;

    public Grid() {
        super();
        element.setClassName("grid-shell");
        headerLeftContainer.setClassName("grid-section-header-left");
        headerRightContainer.setClassName("grid-section-header-right");
        frozenLeftContainer.setClassName("grid-section-frozen-left");
        frozenRightContainer.setClassName("grid-section-frozen-right");
        scrollableLeftContainer.setClassName("grid-section-body-left");
        scrollableRightContainer.setClassName("grid-section-body-right");
        footerLeftContainer.setClassName("grid-section-footer-left");
        footerRightContainer.setClassName("grid-section-footer-right");
        columnsChevron.addClassName("grid-columns-chevron");
        element.appendChild(cellsStyleElement);
        element.appendChild(oddRowsStyleElement);
        element.appendChild(evenRowsStyleElement);
        element.appendChild(headerLeftContainer);
        element.appendChild(headerRightContainer);
        element.appendChild(frozenLeftContainer);
        element.appendChild(frozenRightContainer);
        element.appendChild(scrollableLeftContainer);
        element.appendChild(scrollableRightContainer);
        element.appendChild(footerLeftContainer);
        element.appendChild(footerRightContainer);
        element.appendChild(columnsChevron);

        scrollableRightContainer.<XElement>cast().addEventListener(BrowserEvents.SCROLL, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                // TODO: Add ajacent sections movement logic
            }
        });
        /*
            @Override
            public void onScroll(ScrollEvent event) {
                int aimLeft = scrollableRightContainer.getElement().getScrollLeft();
                if (isHeaderVisible()) {
                    headerRightContainer.getElement().setScrollLeft(aimLeft);
                    int factLeftDelta0 = aimLeft - headerRightContainer.getElement().getScrollLeft();
                    if (factLeftDelta0 > 0) {
                        headerRightContainer.getElement().getStyle().setRight(factLeftDelta0, Style.Unit.PX);
                    } else {
                        headerRightContainer.getElement().getStyle().clearRight();
                    }
                }
                if (frozenColumns > 0 || frozenRows > 0) {
                    int aimTop = scrollableRightContainer.getElement().getScrollTop();

                    scrollableLeftContainer.getElement().setScrollTop(aimTop);
                    int factTopDelta = aimTop - scrollableLeftContainer.getElement().getScrollTop();
                    if (factTopDelta > 0) {
                        scrollableLeftContainer.getElement().getStyle().setBottom(factTopDelta, Style.Unit.PX);
                    } else {
                        scrollableLeftContainer.getElement().getStyle().setBottom(0, Style.Unit.PX);
                        //scrollableLeftContainer.getElement().getStyle().clearBottom();
                    }
                    frozenRightContainer.getElement().setScrollLeft(aimLeft);
                    int factLeftDelta1 = aimLeft - frozenRightContainer.getElement().getScrollLeft();
                    if (factLeftDelta1 > 0) {
                        frozenRightContainer.getElement().getStyle().setRight(factLeftDelta1, Style.Unit.PX);
                    } else {
                        frozenRightContainer.getElement().getStyle().clearRight();
                    }
                    footerRightContainer.getElement()
                            .setScrollLeft(scrollableRightContainer.getElement().getScrollLeft());
                    int factLeftDelta2 = aimLeft - footerRightContainer.getElement().getScrollLeft();
                    if (factLeftDelta2 > 0) {
                        footerRightContainer.getElement().getStyle().setRight(factLeftDelta2, Style.Unit.PX);
                    } else {
                        footerRightContainer.getElement().getStyle().clearRight();
                    }
                }
            }
        });
         */
        ghostLine.addClassName(RULER_STYLE);
        ghostLine.getStyle().setPosition(Style.Position.ABSOLUTE);
        ghostLine.getStyle().setTop(0, Style.Unit.PX);
        ghostColumn = Document.get().createDivElement();
        ghostColumn.addClassName(COLUMN_PHANTOM_STYLE);
        ghostColumn.getStyle().setPosition(Style.Position.ABSOLUTE);
        ghostColumn.getStyle().setTop(0, Style.Unit.PX);
        element.<XElement>cast().addEventListener(BrowserEvents.DRAGENTER, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                if (ColumnDrag.instance != null && ColumnDrag.instance.isMove()) {
                    event.preventDefault();
                    event.stopPropagation();
                    ColumnDrag target = findTargetDraggedColumn(event.getEventTarget());
                    if (target != null) {
                        showColumnMoveDecorations(target);
                        event.getDataTransfer().<XDataTransfer>cast().setDropEffect("move");
                    } else {
                        event.getDataTransfer().<XDataTransfer>cast().setDropEffect("none");
                    }
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.DRAG, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                if (ColumnDrag.instance != null && ColumnDrag.instance.isResize()) {
                    event.stopPropagation();
                }
            }

        });
        element.<XElement>cast().addEventListener(BrowserEvents.DRAGOVER, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (ColumnDrag.instance != null) {
                    event.preventDefault();
                    event.stopPropagation();
                    if (ColumnDrag.instance.isMove()) {
                        ColumnDrag target = findTargetDraggedColumn(event.getEventTarget());
                        if (target != null) {
                            event.getDataTransfer().<XDataTransfer>cast().setDropEffect("move");
                        } else {
                            hideColumnDecorations();
                            event.getDataTransfer().<XDataTransfer>cast().setDropEffect("none");
                        }
                    } else {
                        Element hostElement = Grid.this.getElement();
                        int clientX = event.getClientX();
                        int hostAbsX = hostElement.getAbsoluteLeft();
                        int hostScrollX = hostElement.getScrollLeft();
                        int docScrollX = hostElement.getOwnerDocument().getScrollLeft();
                        int relativeX = clientX - hostAbsX + hostScrollX + docScrollX;
                        ghostLine.getStyle().setLeft(relativeX, Style.Unit.PX);
                        ghostLine.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
                        if (ghostLine.getParentElement() != hostElement) {
                            hostElement.appendChild(ghostLine);
                        }
                    }
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.DRAGLEAVE, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (ColumnDrag.instance != null) {
                    event.stopPropagation();
                    if (ColumnDrag.instance.isMove()) {
                        if (event.getEventTarget() == (JavaScriptObject) Grid.this.getElement()) {
                            hideColumnDecorations();
                        }
                    }
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.DRAGEND, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (ColumnDrag.instance != null) {
                    event.stopPropagation();
                    hideColumnDecorations();
                    ColumnDrag.instance = null;
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.DROP, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                ColumnDrag source = ColumnDrag.instance;
                ColumnDrag target = targetDraggedColumn;
                hideColumnDecorations();
                ColumnDrag.instance = null;
                if (source != null) {
                    event.preventDefault();
                    event.stopPropagation();
                    if (source.isMove()) {
                        // target table may be any section in our grid
                        if (target != null) {
                            HeaderView sourceHeader = source.getHeader();
                            HeaderView targetHeader = target.getHeader();
                            moveColumnNode(sourceHeader.getHeaderNode(), targetHeader.getHeaderNode());
                        }
                    } else {
                        HeaderView header = source.getHeader();
                        if (header.isResizable()) {
                            int newWidth = Math.max(
                                    event.getClientX() - source.getCellElement().getAbsoluteLeft(),
                                    MINIMUM_COLUMN_WIDTH);
                            setColumnWidthFromHeaderDrag(header.getColumn(), newWidth, Style.Unit.PX);
                        }
                    }
                }
            }
        });

        columnsChevron.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                Menu columnsMenu = new Menu();
                fillColumns(columnsMenu, headerLeft);
                fillColumns(columnsMenu, headerRight);
                columnsMenu.showRelativeTo(columnsChevron);
            }

            private void fillColumns(Menu aTarget, final GridSection aSection) {
                for (int i = 0; i < aSection.getColumnCount(); i++) {
                    final Column column = aSection.getColumn(i);
                    MenuItemCheckBox miCheck = new MenuItemCheckBox(column.isVisible(),
                            aSection.getColumn(i).getHeader().getText(), true);
                    miCheck.addValueChangeHandler(new ValueChangeHandler() {

                        @Override
                        public void onValueChange(ValueChangeEvent event) {
                            if (Boolean.TRUE.equals(event.getNewValue())) {
                                column.setVisible(true);
                            } else {
                                column.setVisible(false);
                            }
                        }

                    });
                    aTarget.add(miCheck);
                }
            }

        });

        ColumnSortEvent.Handler sectionSortHandler = new ColumnSortEvent.Handler() {

            @Override
            public void onColumnSort(ColumnSortEvent event) {
                boolean isCtrlKey = ((GridSection<?>) event.getSource()).isCtrlKey();
                boolean contains = false;
                int containsAt = -1;
                for (int i = 0; i < sortList.size(); i++) {
                    if (sortList.get(i).getColumn() == event.getColumn()) {
                        contains = true;
                        containsAt = i;
                        break;
                    }
                }
                if (!contains) {
                    if (!isCtrlKey) {
                        sortList.clear();
                    }
                    sortList.insert(sortList.size(), new ColumnSortList.ColumnSortInfo(event.getColumn(), true));
                } else {
                    boolean wasAscending = sortList.get(containsAt).isAscending();
                    if (!isCtrlKey) {
                        sortList.clear();
                        if (wasAscending) {
                            sortList.push(new ColumnSortList.ColumnSortInfo(event.getColumn(), false));
                        }
                    } else {
                        sortList.remove(sortList.get(containsAt));
                        if (wasAscending) {
                            sortList.insert(containsAt, new ColumnSortList.ColumnSortInfo(event.getColumn(), false));
                        }
                    }
                }
                ColumnSortEvent.fire(Grid.this, sortList);
            }
        };
        headerLeft.getColumnSortList().setLimit(1);
        headerLeft.addColumnSortHandler(sectionSortHandler);
        headerRight.getColumnSortList().setLimit(1);
        headerRight.addColumnSortHandler(sectionSortHandler);
        gridColor = PublishedColor.create(211, 211, 211, 255);
        regenerateDynamicHeaderRowsStyles();
        regenerateDynamicRowsStyles();
        regenerateDynamicOddRowsStyles();
        regenerateDynamicCellsStyles();
    }

    public ColumnSortList getSortList() {
        return sortList;
    }

    /**
     * Add a handler to handle {@link ColumnSortEvent}s.
     *
     * @param handler the {@link ColumnSortEvent.Handler} to add
     * @return a {@link HandlerRegistration} to remove the handler
     */
    public HandlerRegistration addColumnSortHandler(ColumnSortEvent.Handler handler) {
        return addHandler(handler, ColumnSortEvent.getType());
    }

    public ListDataProvider getDataProvider() {
        return dataProvider;
    }

    protected ColumnDrag findTargetDraggedColumn(JavaScriptObject aEventTarget) {
        if (Element.is(aEventTarget)) {
            GridSection targetSection = null;
            Element targetCell = null;
            Element currentTarget = Element.as(aEventTarget);
            if (COLUMN_PHANTOM_STYLE.equals(currentTarget.getClassName())
                    || RULER_STYLE.equals(currentTarget.getClassName())) {
                return targetDraggedColumn;
            } else {
                while ((targetCell == null || targetSection == null) && currentTarget != null
                        && currentTarget != Grid.this.getElement()) {
                    if (targetCell == null) {
                        if ("td".equalsIgnoreCase(currentTarget.getTagName())
                                || "th".equalsIgnoreCase(currentTarget.getTagName())) {
                            targetCell = currentTarget;
                        }
                    }
                    if (targetSection == null) {
                        if (currentTarget == headerLeft.getElement()) {
                            targetSection = headerLeft;
                        } else if (currentTarget == frozenLeft.getElement()) {
                            targetSection = frozenLeft;
                        } else if (currentTarget == scrollableLeft.getElement()) {
                            targetSection = scrollableLeft;
                        } else if (currentTarget == footerLeft.getElement()) {
                            targetSection = footerLeft;
                        } else if (currentTarget == headerRight.getElement()) {
                            targetSection = headerRight;
                        } else if (currentTarget == frozenRight.getElement()) {
                            targetSection = frozenRight;
                        } else if (currentTarget == scrollableRight.getElement()) {
                            targetSection = scrollableRight;
                        } else if (currentTarget == footerRight.getElement()) {
                            targetSection = footerRight;
                        }
                    }
                    currentTarget = currentTarget.getParentElement();
                }
                if (targetSection != null && targetCell != null) {
                    HeaderView header = (HeaderView)targetCell.getPropertyObject(HeaderView.HEADER_VIEW);
                    if (header != null) {
                        return new ColumnDrag(header, targetSection, targetCell);
                    } else {
                        return null;
                    }
                }
                return null;
            }
        } else {
            return null;
        }
    }

    protected void hideColumnDecorations() {
        ghostLine.removeFromParent();
        ghostColumn.removeFromParent();
        targetDraggedColumn = null;
    }

    protected void showColumnMoveDecorations(ColumnDrag target) {
        targetDraggedColumn = target;
        Element hostElement = getElement();
        Element thtdElement = target.getCellElement();
        int thLeft = thtdElement.getAbsoluteLeft();
        thLeft = thLeft - element.getAbsoluteLeft() + hostElement.getScrollLeft();
        ghostLine.getStyle().setLeft(thLeft, Style.Unit.PX);
        ghostLine.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
        ghostColumn.getStyle().setLeft(thLeft, Style.Unit.PX);
        ghostColumn.getStyle().setWidth(thtdElement.getOffsetWidth(), Style.Unit.PX);
        ghostColumn.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
        if (ghostLine.getParentElement() != hostElement) {
            ghostLine.removeFromParent();
            hostElement.appendChild(ghostLine);
        }
        if (ghostColumn.getParentElement() != hostElement) {
            ghostColumn.removeFromParent();
            hostElement.appendChild(ghostColumn);
        }
    }

    public String getDynamicCellClassName() {
        return dynamicCellClassName;
    }

    public boolean isShowHorizontalLines() {
        return showHorizontalLines;
    }

    public void setShowHorizontalLines(boolean aValue) {
        if (showHorizontalLines != aValue) {
            showHorizontalLines = aValue;
            regenerateDynamicCellsStyles();
        }
    }

    public boolean isShowVerticalLines() {
        return showVerticalLines;
    }

    public void setShowVerticalLines(boolean aValue) {
        if (showVerticalLines != aValue) {
            showVerticalLines = aValue;
            regenerateDynamicCellsStyles();
        }
    }

    protected void regenerateDynamicCellsStyles() {
        cellsStyleElement.setInnerHTML("." + dynamicCellClassName + "{"
                + "border-left-width: " + (showHorizontalLines ? 1 : 0) + "px;"
                + "border-right-width:" + (showVerticalLines ? 1 : 0) + "px;"
                + "border-color: " + (gridColor != null ? gridColor.toStyled() : "auto") + ";");
    }

    protected void regenerateDynamicOddRowsStyles() {
        if (showOddRowsInOtherColor && oddRowsColor != null) {
            oddRowsStyleElement.setInnerHTML(
                    "." + dynamicOddRowsClassName + "{background-color: " + oddRowsColor.toStyled() + "}");
        } else {
            oddRowsStyleElement.setInnerHTML("");
        }
    }

    public PublishedColor getGridColor() {
        return gridColor;
    }

    public void setGridColor(PublishedColor aValue) {
        gridColor = aValue;
        regenerateDynamicCellsStyles();
    }

    public PublishedColor getOddRowsColor() {
        return oddRowsColor;
    }

    public void setOddRowsColor(PublishedColor aValue) {
        oddRowsColor = aValue;
        regenerateDynamicOddRowsStyles();
    }

    public int getRowsHeight() {
        return rowsHeight;
    }

    public void setHeaderRowsHeight(int aValue) {
        if (headerRowsHeight != aValue && aValue >= 10) {
            headerRowsHeight = aValue;
            regenerateDynamicRowsStyles();
        }
    }

    protected void regenerateDynamicRowsStyles() {
        headerRowsStyleElement.setInnerHTML("." + dynamicCellClassName + "{ height: " + headerRowsHeight + "px;");
    }

    public void setRowsHeight(int aValue) {
        if (rowsHeight != aValue && aValue >= 10) {
            rowsHeight = aValue;
            regenerateDynamicHeaderRowsStyles();
        }
    }

    protected void regenerateDynamicHeaderRowsStyles() {
        cellsStyleElement.setInnerHTML("." + dynamicCellClassName + "{ height: " + rowsHeight + "px;");
    }

    public boolean isHeaderVisible() {
        return !Style.Display.NONE.equals(headerLeft.getElement().getStyle().getDisplay())
                && !Style.Display.NONE.equals(headerRight.getElement().getStyle().getDisplay());
    }

    public void setHeaderVisible(boolean aValue) {
        if (aValue) {
            columnsChevron.getStyle().clearDisplay();
            headerLeftContainer.getStyle().clearDisplay();
            headerRightContainer.getStyle().clearDisplay();
        } else {
            columnsChevron.getStyle().setDisplay(Style.Display.NONE);
            headerLeftContainer.getStyle().setDisplay(Style.Display.NONE);
            headerRightContainer.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    public int getFrozenColumns() {
        return frozenColumns;
    }

    public void setFrozenColumns(int aValue) {
        if (aValue >= 0 && frozenColumns != aValue) {
            if (aValue >= 0) {
                frozenColumns = aValue;
                if (getColumnCount() > 0 && aValue <= getColumnCount()) {
                    refreshColumns();
                }
            }
        }
    }

    public int getFrozenRows() {
        return frozenRows;
    }

    public void setFrozenRows(int aValue) {
        if (aValue >= 0 && frozenRows != aValue) {
            frozenRows = aValue;
            if (dataProvider != null && aValue <= dataProvider.getList().size()) {
                setupVisibleRanges();
            }
        }
    }

    public boolean isShowOddRowsInOtherColor() {
        return showOddRowsInOtherColor;
    }

    public void setShowOddRowsInOtherColor(boolean aValue) {
        if (showOddRowsInOtherColor != aValue) {
            showOddRowsInOtherColor = aValue;
            regenerateDynamicOddRowsStyles();
        }
    }

    /**
     *
     * @param sModel
     */
    public void setSelectionModel(SelectionModel sModel) {
        headerLeft.setSelectionModel(sModel);
        headerRight.setSelectionModel(sModel);
        frozenLeft.setSelectionModel(sModel);
        frozenRight.setSelectionModel(sModel);
        scrollableLeft.setSelectionModel(sModel);
        scrollableRight.setSelectionModel(sModel);
    }

    /**
     * @param aDataProvider
     */
    public void setDataProvider(ListDataProvider aDataProvider) {
        if (dataProvider != aDataProvider) {
            unbindDataProvider();
            dataProvider = aDataProvider;
            bindDataProvider();
            setupVisibleRanges();
        }
    }

    protected void unbindDataProvider() {
        if (dataProvider != null) {
            dataProvider.removeDataDisplay(headerLeft);
            dataProvider.removeDataDisplay(headerRight);
            dataProvider.removeDataDisplay(frozenLeft);
            dataProvider.removeDataDisplay(frozenRight);
            dataProvider.removeDataDisplay(scrollableLeft);
            dataProvider.removeDataDisplay(scrollableRight);
            dataProvider.removeDataDisplay(footerLeft);
            dataProvider.removeDataDisplay(footerRight);
        }
    }

    protected void bindDataProvider() {
        if (dataProvider != null) {
            dataProvider.addDataDisplay(headerLeft);
            dataProvider.addDataDisplay(headerRight);
            dataProvider.addDataDisplay(frozenLeft);
            dataProvider.addDataDisplay(frozenRight);
            dataProvider.addDataDisplay(scrollableLeft);
            dataProvider.addDataDisplay(scrollableRight);
            dataProvider.addDataDisplay(footerLeft);
            dataProvider.addDataDisplay(footerRight);
        }
    }

    public void setupVisibleRanges() {
        List<T> list = dataProvider != null ? dataProvider.getList() : null;
        int generalLength = list != null ? list.size() : 0;
        int lfrozenRows = generalLength >= frozenRows ? frozenRows : generalLength;
        if (lfrozenRows == 0) {
            hive.getRowFormatter().setVisible(1, false);
        } else {
            hive.getRowFormatter().setVisible(1, true);
        }
        int scrollableRowCount = generalLength - lfrozenRows;
        //
        headerLeft.setVisibleRange(new Range(0, 0));
        headerRight.setVisibleRange(new Range(0, 0));
        frozenLeft.setVisibleRange(new Range(0, lfrozenRows));
        frozenRight.setVisibleRange(new Range(0, lfrozenRows));
        scrollableLeft.setVisibleRange(new Range(lfrozenRows, scrollableRowCount >= 0 ? scrollableRowCount : 0));
        scrollableRight.setVisibleRange(new Range(lfrozenRows, scrollableRowCount >= 0 ? scrollableRowCount : 0));
        footerLeft.setVisibleRange(new Range(0, 0));
        footerRight.setVisibleRange(new Range(0, 0));
        //Since footerLeft and footerRight have bpth zero range,
        //hide them entirely
        hive.getRowFormatter().setVisible(3, false);
    }

    public void addColumn(Column<T, ?> aColumn, String aWidth, HeaderView<?> aHeader, HeaderView<?> aFooter, boolean hidden) {
        addColumn(false, getColumnCount(), aColumn, aWidth, aHeader, aFooter, hidden);
    }

    public void addColumn(int aIndex, Column<T, ?> aColumn, String aWidth, HeaderView<?> aHeader, HeaderView<?> aFooter,
            boolean hidden) {
        addColumn(true, aIndex, aColumn, aWidth, aHeader, aFooter, hidden);
    }

    public void addColumn(boolean forceRefreshColumns, int aIndex, Column<T, ?> aColumn, String aWidth,
            HeaderView<?> aHeader, HeaderView<?> aFooter, boolean hidden) {
        /*
		 * if (aHeader instanceof DraggableHeader<?>) { DraggableHeader<T> h =
		 * (DraggableHeader<T>) aHeader; h.setColumn(aColumn); } WARNING! Before
		 * uncomment, answer the question: DraggableHeader can change its
		 * column?
         */
        if (aIndex < frozenColumns) {
            if (aHeader instanceof HeaderView<?>) {
                HeaderView<T> h = (HeaderView<T>) aHeader;
                h.setTable(headerLeft);
            }
            headerLeft.insertColumn(aIndex, aColumn, aHeader);
            frozenLeft.insertColumn(aIndex, aColumn);
            scrollableLeft.insertColumn(aIndex, aColumn);
            footerLeft.insertColumn(aIndex, aColumn, null, aFooter);
            headerLeft.setColumnWidth(aColumn, aWidth);// column partners will
            // take care of width
            // seetings in other
            // sections
            //
            if (forceRefreshColumns) {
                refreshColumns();
            }
        } else {
            if (aHeader instanceof HeaderView<?>) {
                HeaderView<T> h = (HeaderView<T>) aHeader;
                h.setTable(headerRight);
            }
            headerRight.insertColumn(aIndex - frozenColumns, aColumn, aHeader);
            frozenRight.insertColumn(aIndex - frozenColumns, aColumn);
            scrollableRight.insertColumn(aIndex - frozenColumns, aColumn);
            footerRight.insertColumn(aIndex - frozenColumns, aColumn, null, aFooter);
            headerRight.setColumnWidth(aColumn, aWidth);// column partners will
            // take care of width
            // seetings in other
            // sections
            //
        }
        if (hidden) {
            hideColumn(aColumn);
        }
    }

    public void moveColumnNode(HeaderNode aNode, HeaderNode aInsertBefore) {
        List<HeaderNode> nodes = aNode.getParent() != null ? aNode.getParent().getChildren() : header;
        HeaderNode parent = aInsertBefore.getParent();
        int idx = parent.getChildren().indexOf(aInsertBefore);
        parent.getChildren().add(idx, aNode);
    }

    /*
	 * public void insertColumn(int aIndex, Column<T, ?> aColumn, String
	 * aHeaderValue, Header<?> aFooter) { if (aIndex < frozenColumns) {
	 * headerLeft.insertColumn(aIndex, aColumn, new
	 * DraggableHeader<T>(aHeaderValue, headerLeft, aColumn, getElement()));
	 * frozenLeft.insertColumn(aIndex, aColumn);
	 * scrollableLeft.insertColumn(aIndex, aColumn);
	 * footerLeft.insertColumn(aIndex, aColumn, null, aFooter);
	 * refreshColumns(); } else { headerRight.insertColumn(aIndex, aColumn, new
	 * DraggableHeader<T>(aHeaderValue, headerRight, aColumn, getElement()));
	 * frozenRight.insertColumn(aIndex, aColumn);
	 * scrollableRight.insertColumn(aIndex, aColumn);
	 * footerRight.insertColumn(aIndex, aColumn, null, aFooter); } }
     */
    public void removeColumn(int aIndex) {
        if (aIndex < frozenColumns) {
            headerLeft.removeColumn(aIndex);// ColumnsRemover will care about columns sharing
            refreshColumns();
        } else {
            headerRight.removeColumn(aIndex - frozenColumns);// ColumnsRemover will care about columns sharing
        }
    }

    protected void refreshColumns() {
        List<Column> cols = new ArrayList<>();
        List<HeaderView> headers = new ArrayList<>();
        List<HeaderView> footers = new ArrayList<>();
        List<String> widths = new ArrayList<>();
        List<Boolean> hidden = new ArrayList<>();
        for (int i = headerRight.getColumnCount() - 1; i >= 0; i--) {
            Column col = headerRight.getColumn(i);
            cols.add(0, col);
            widths.add(0, headerRight.getColumnWidth(col, true));
            headers.add(0, headerRight.getHeader(i));
            footers.add(0, footerRight.getFooter(i));
            hidden.add(0, headerRight.isColumnHidden(col));
            headerRight.removeColumn(i);// ColumnsRemover will care about
            // columns sharing
        }
        for (int i = headerLeft.getColumnCount() - 1; i >= 0; i--) {
            Column col = headerLeft.getColumn(i);
            cols.add(0, col);
            widths.add(0, headerLeft.getColumnWidth(col, true));
            headers.add(0, headerLeft.getHeader(i));
            footers.add(0, footerLeft.getFooter(i));
            hidden.add(0, headerLeft.isColumnHidden(col));
            headerLeft.removeColumn(i);// ColumnsRemover will care about
            // columns sharing
        }
        for (int i = 0; i < cols.size(); i++) {
            Column col = cols.get(i);
            HeaderView h = headers.get(i);
            HeaderView f = footers.get(i);
            String w = widths.get(i);
            Boolean b = hidden.get(i);
            addColumn(col, w, h, f, b);
        }
    }

    public void setColumnWidthFromHeaderDrag(Column<T, ?> aColumn, double aWidth, Style.Unit aUnit) {
        setColumnWidth(aColumn, aWidth, aUnit);
    }

    public void setColumnWidth(Column<T, ?> aColumn, double aWidth, Style.Unit aUnit) {
        if (headerLeft.getColumnIndex(aColumn) != -1) {
            headerLeft.setColumnWidth(aColumn, aWidth, aUnit);
            frozenLeft.setColumnWidth(aColumn, aWidth, aUnit);
            scrollableLeft.setColumnWidth(aColumn, aWidth, aUnit);
            footerLeft.setColumnWidth(aColumn, aWidth, aUnit);
        } else if (headerRight.getColumnIndex(aColumn) != -1) {
            headerRight.setColumnWidth(aColumn, aWidth, aUnit);
            frozenRight.setColumnWidth(aColumn, aWidth, aUnit);
            scrollableRight.setColumnWidth(aColumn, aWidth, aUnit);
            footerRight.setColumnWidth(aColumn, aWidth, aUnit);
        } else {
            // Logger.getLogger(Grid.class.getName()).log(Level.WARNING,
            // "Unknown column is met while setting column width");
        }
    }

    public void redrawRow(int index) {
        frozenLeft.redrawRow(index);
        frozenRight.redrawRow(index);
        scrollableLeft.redrawRow(index);
        scrollableRight.redrawRow(index);
    }

    public void redraw() {
        headerRight.redraw();
        frozenLeft.redraw();
        frozenRight.redraw();
        scrollableLeft.redraw();
        scrollableRight.redraw();
        footerLeft.redraw();
        footerRight.redraw();
    }

    public void redrawHeaders() {
        headerLeft.redrawHeaders();
        headerRight.redrawHeaders();
    }

    public void redrawFooters() {
        footerLeft.redrawFooters();
        footerRight.redrawFooters();
    }

    public int getColumnCount() {
        return (headerLeft != null ? headerLeft.getColumnCount() : 0)
                + (headerRight != null ? headerRight.getColumnCount() : 0);
    }

    public Column getColumn(int aIndex) {
        if (aIndex >= 0 && aIndex < getColumnCount()) {
            return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getColumn(aIndex)
                    : headerRight.getColumn(aIndex - headerLeft.getColumnCount());
        } else {
            return null;
        }
    }

    public HeaderView getColumnHeader(int aIndex) {
        if (aIndex >= 0 && aIndex < getColumnCount()) {
            return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getHeader(aIndex)
                    : headerRight.getHeader(aIndex - headerLeft.getColumnCount());
        } else {
            return null;
        }
    }

    public HeaderView getColumnFooter(int aIndex) {
        if (aIndex >= 0 && aIndex < getColumnCount()) {
            return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getFooter(aIndex)
                    : headerRight.getFooter(aIndex - headerLeft.getColumnCount());
        } else {
            return null;
        }
    }

    public TableCellElement getViewCell(int aRow, int aCol) {
        GridSection targetSection;
        if (aRow < frozenRows) {
            if (aCol < frozenColumns) {
                targetSection = frozenLeft;
            } else {
                targetSection = frozenRight;
            }
        } else {
            if (aCol < frozenColumns) {
                targetSection = scrollableLeft;
            } else {
                targetSection = scrollableRight;
            }
        }
        return targetSection.getViewCell(aRow, aCol);
    }

    public void focusViewCell(int aRow, int aCol) {
        GridSection targetSection;
        if (aRow < frozenRows) {
            if (aCol < frozenColumns) {
                targetSection = frozenLeft;
            } else {
                targetSection = frozenRight;
            }
        } else {
            if (aCol < frozenColumns) {
                targetSection = scrollableLeft;
            } else {
                targetSection = scrollableRight;
            }
        }
        targetSection.focusCell(aRow, aCol);
    }

    public void unsort() {
        sortList.clear();
        ColumnSortEvent.fire(Grid.this, sortList);
        redrawHeaders();
    }

    public void addSort(Column aColumn, boolean isAscending) {
        if (aColumn.isSortable()) {
            boolean contains = false;
            int containsAt = -1;
            for (int i = 0; i < sortList.size(); i++) {
                if (sortList.get(i).getColumn() == aColumn) {
                    contains = true;
                    containsAt = i;
                    break;
                }
            }
            if (contains) {
                boolean wasAscending = sortList.get(containsAt).isAscending();
                if (wasAscending == isAscending) {
                    return;
                }

            }
            sortList.insert(sortList.size(), new ColumnSortList.ColumnSortInfo(aColumn, isAscending));
            ColumnSortEvent.fire(Grid.this, sortList);
            redrawHeaders();
        }
    }

    public void unsortColumn(Column aColumn) {
        if (aColumn.isSortable()) {
            boolean contains = false;
            int containsAt = -1;
            for (int i = 0; i < sortList.size(); i++) {
                if (sortList.get(i).getColumn() == aColumn) {
                    contains = true;
                    containsAt = i;
                    break;
                }
            }
            if (contains) {
                sortList.remove(sortList.get(containsAt));
                ColumnSortEvent.fire(Grid.this, sortList);
                redrawHeaders();
            }
        }
    }

    protected int tabIndex;

    protected Element calcFocusedElement() {
        Element focusedEelement = scrollableRight.getKeyboardSelectedElement();
        if (focusedEelement == null) {
            focusedEelement = scrollableLeft.getKeyboardSelectedElement();
        }
        if (focusedEelement == null) {
            focusedEelement = frozenLeft.getKeyboardSelectedElement();
        }
        if (focusedEelement == null) {
            focusedEelement = frozenRight.getKeyboardSelectedElement();
        }
        if (focusedEelement == null) {
            focusedEelement = getElement();
        }
        return focusedEelement;
    }

    @Override
    public int getTabIndex() {
        return tabIndex;
    }

    @Override
    public void setFocus(boolean focused) {
        Element focusedElement = calcFocusedElement();
        focusedElement.setTabIndex(tabIndex);
        if (focused) {
            focusedElement.focus();
        } else {
            focusedElement.blur();
        }
    }

    @Override
    public void setTabIndex(int index) {
        tabIndex = index;
        Element focusedElement = calcFocusedElement();
        focusedElement.setTabIndex(tabIndex);
    }
}
