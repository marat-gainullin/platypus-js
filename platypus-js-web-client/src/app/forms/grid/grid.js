define([
    '../../extend',
    '../../id',
    '../../ui',
    '../../invoke',
    '../../logger',
    '../../bound',
    '../../common-utils/color',
    '../key-codes',
    '../widget',
    '../events/item-event',
    '../events/sort-event',
    '../events/key-event',
    '../events/focus-event',
    '../events/blur-event',
    './section',
    './columns/drag',
    './columns/marker-service-column',
    './columns/check-box-service-column',
    './columns/radio-button-service-column',
    './header/node-view',
    './header/analyzer',
    './header/splitter'
], function (
        extend,
        Id,
        Ui,
        Invoke,
        Logger,
        Bound,
        Color,
        KeyCodes,
        Widget,
        ItemEvent,
        SortEvent,
        KeyEvent,
        FocusEvent,
        BlurEvent,
        Section,
        ColumnDrag,
        MarkerServiceColumn,
        CheckBoxServiceColumn,
        RadioButtonServiceColumn,
        MarkerServiceColumn,
        HeaderView,
        HeaderAnalyzer,
        HeaderSplitter
        ) {

//public class Grid extends Widget implements HasSelectionHandlers<JavaScriptObject>, HasSelectionLead, HasOnRender, HasBinding, 
//        Focusable, HasFocusHandlers, HasBlurHandlers,
//        HasKeyDownHandlers, HasKeyPressHandlers, HasKeyUpHandlers {

    var RULER_STYLE = "p-grid-ruler";
    var COLUMN_PHANTOM_STYLE = "p-grid-column-phantom";
    var MINIMUM_COLUMN_WIDTH = 15;

    function Grid() {
        var shell = document.createElement('div');
        Widget.call(shell);
        var self = this;

        var cellsStyleElement = document.createElement('style');
        var rowsStyleElement = document.createElement('style');
        var oddRowsStyleElement = document.createElement('style');
        var evenRowsStyleElement = document.createElement('style');
        var headerRowsStyleElement = document.createElement('style');
        var dynamicCellClassName = 'p-grid-cell-' + Id.generate();
        var dynamicOddRowsClassName = 'p-grid-odd-row-' + Id.generate();
        var dynamicEvenRowsClassName = 'p-grid-even-row-' + Id.generate();
        var dynamicHeaderRowClassName = 'p-grid-heder-row-' + Id.generate();
        var headerLeftContainer = document.createElement('div');
        var headerLeft = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
        var headerRightContainer = document.createElement('div');
        var headerRight = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
        var frozenLeftContainer = document.createElement('div');
        var frozenLeft = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
        var frozenRightContainer = document.createElement('div');
        var frozenRight = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
        var scrollableLeftContainer = document.createElement('div');
        var scrollableLeft = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
        var scrollableRightContainer = document.createElement('div');
        var scrollableRight = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
        var footerLeftContainer = document.createElement('div');
        var footerLeft = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
        var footerRightContainer = document.createElement('div');
        var footerRight = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
        var ghostLine = document.createElement('div');
        var ghostColumn = null;
        var targetDraggedColumn = null;

        var header = [];
        Object.defineProperty(this, 'columnNodes', {
            get: function () {
                return header.slice(0, header.length);
            }
        });

        //
        var columnsChevron = document.createElement('div');
        //
        var headerRowsHeight = 30;
        var rowsHeight = 30;
        var showHorizontalLines = true;
        var showVerticalLines = true;
        var showOddRowsInOtherColor = true;
        var gridColor = null;
        var oddRowsColor = new Color(241, 241, 241, 255);

        var selectedRows = new Set();
        var selectionLead = null;
        Object.defineProperty(this, 'selected', {
            get: function () {
                return Array.from(selectedRows);
            }
        });

        var frozenColumns = 0;
        var frozenRows = 0;
        var parentField = null;
        var childrenField = null;
        //
        var data = null; // bounded data. this is not rows source. rows source is data['field' property path]
        var sortedRows = null; // rows in view. subject of sorting. subject of collapse / expand in tree.
        var expandedRows = new Set();
        var field = null;
        var boundToData = null;
        var boundToElements = null;
        var boundToCursor = null;
        var cursorProperty = 'cursor';
        var onRender = null;
        var onAfterRender = null;
        // runtime
        var activeEditor = null;
        var editable = true;
        var deletable = true;
        var insertable = true;
        var draggableRows = false;

        shell.className = 'p-widget p-grid-shell';

        headerLeftContainer.className = 'p-grid-section-header-left';
        headerRightContainer.className = 'p-grid-section-header-right';
        frozenLeftContainer.className = 'p-grid-section-frozen-left';
        frozenRightContainer.className = 'p-grid-section-frozen-right';
        scrollableLeftContainer.className = 'p-grid-section-body-left';
        scrollableRightContainer.className = 'p-grid-section-body-right';
        footerLeftContainer.className = 'p-grid-section-footer-left';
        footerRightContainer.className = 'p-grid-section-footer-right';
        columnsChevron.className = 'p-grid-columns-chevron';
        shell.appendChild(cellsStyleElement);
        shell.appendChild(rowsStyleElement);
        shell.appendChild(oddRowsStyleElement);
        shell.appendChild(evenRowsStyleElement);
        shell.appendChild(headerLeftContainer);
        shell.appendChild(headerRightContainer);
        shell.appendChild(frozenLeftContainer);
        shell.appendChild(frozenRightContainer);
        shell.appendChild(scrollableLeftContainer);
        shell.appendChild(scrollableRightContainer);
        shell.appendChild(footerLeftContainer);
        shell.appendChild(footerRightContainer);
        shell.appendChild(columnsChevron);

        Ui.on(scrollableRightContainer, Ui.Events.SCROLL, function (evt) {
            // TODO: Add ajacent sections movement logic
        });

        /*
         @Override
         public void onScroll(ScrollEvent event) {
         int aimLeft = scrollableRightContainer.element.getScrollLeft();
         if (isHeaderVisible()) {
         headerRightContainer.element.setScrollLeft(aimLeft);
         int factLeftDelta0 = aimLeft - headerRightContainer.element.getScrollLeft();
         if (factLeftDelta0 > 0) {
         headerRightContainer.element.style.right =factLeftDelta0+ 'px');
         } else {
         headerRightContainer.element.style.clearRight();
         }
         }
         if (frozenColumns > 0 || frozenRows > 0) {
         int aimTop = scrollableRightContainer.element.getScrollTop();
         
         scrollableLeftContainer.element.setScrollTop(aimTop);
         int factTopDelta = aimTop - scrollableLeftContainer.element.getScrollTop();
         if (factTopDelta > 0) {
         scrollableLeftContainer.element.style.bottom =factTopDelta+ 'px');
         } else {
         scrollableLeftContainer.element.style.bottom =0+ 'px');
         //scrollableLeftContainer.element.style.clearBottom();
         }
         frozenRightContainer.element.setScrollLeft(aimLeft);
         int factLeftDelta1 = aimLeft - frozenRightContainer.element.getScrollLeft();
         if (factLeftDelta1 > 0) {
         frozenRightContainer.element.style.right =factLeftDelta1+ 'px');
         } else {
         frozenRightContainer.element.style.clearRight();
         }
         footerRightContainer.element
         .setScrollLeft(scrollableRightContainer.element.getScrollLeft());
         int factLeftDelta2 = aimLeft - footerRightContainer.element.getScrollLeft();
         if (factLeftDelta2 > 0) {
         footerRightContainer.element.style.right =factLeftDelta2+ 'px');
         } else {
         footerRightContainer.element.style.clearRight();
         }
         }
         }
         });
         */
        ghostLine.classList.add(RULER_STYLE);
        ghostLine.style.position = 'absolute';
        ghostLine.style.top = 0 + 'px';
        ghostColumn = document.createElement('div');
        ghostColumn.classList.add(COLUMN_PHANTOM_STYLE);
        ghostColumn.style.position = 'absolute';
        ghostColumn.style.top = 0 + 'px';

        Ui.on(shell, Ui.Events.DRAGSTART, function (event) {
            if (draggableRows) {
                var targetElement = event.target;
                if ("tr".equalsIgnoreCase(targetElement.tagName)) {
                    event.stopPropagation();
                    var dragged = targetElement[Section.JS_ROW_NAME];
                    var rows = discoverRows();
                    var dataIndex = rows.indexOf(dragged);
                    event.dataTransfer.setData('text/modelgrid-row',
                            '{"gridName":"' + name + '", "dataIndex": ' + dataIndex + '}');
                }
            }
        });
        Ui.on(shell, Ui.Events.DRAGENTER, function (event) {
            if (ColumnDrag.instance && ColumnDrag.instance.move) {
                event.preventDefault();
                event.stopPropagation();
                var target = findTargetDraggedColumn(event.target);
                if (target) {
                    showColumnMoveDecorations(target);
                    event.dataTransfer.dropEffect = 'move';
                } else {
                    event.dataTransfer.dropEffect = 'none';
                }
            }
        });
        Ui.on(shell, Ui.Events.DRAG, function (event) {
            if (ColumnDrag.instance && ColumnDrag.instance.resize) {
                event.stopPropagation();
            }
        });
        Ui.on(shell, Ui.Events.DRAGOVER, function (event) {
            if (ColumnDrag.instance) {
                event.preventDefault();
                event.stopPropagation();
                if (ColumnDrag.instance.move) {
                    var target = findTargetDraggedColumn(event.target);
                    if (target) {
                        event.dataTransfer.dropEffect = 'move';
                    } else {
                        hideColumnDecorations();
                        event.dataTransfer.dropEffect = 'none';
                    }
                } else {
                    var hostElement = self.shell;
                    var clientX = event.clientX;
                    var hostAbsX = Ui.absoluteLeft(hostElement);
                    var hostScrollX = hostElement.scrollLeft;
                    // TODO: Align with widget.js's docu,emt scroll left calc
                    var docScrollX = document.scrollLeft;
                    var relativeX = clientX - hostAbsX + hostScrollX + docScrollX;
                    ghostLine.style.left = relativeX + 'px';
                    ghostLine.style.height = hostElement.clientHeight + 'px';
                    if (ghostLine.parentElement !== hostElement) {
                        hostElement.appendChild(ghostLine);
                    }
                }
            }
        });
        Ui.on(shell, Ui.Events.DRAGLEAVE, function (event) {
            if (ColumnDrag.instance) {
                event.stopPropagation();
                if (ColumnDrag.instance.move) {
                    if (event.target === self.shell) {
                        hideColumnDecorations();
                    }
                }
            }
        });
        Ui.on(shell, Ui.Events.DRAGEND, function (event) {
            if (ColumnDrag.instance) {
                event.stopPropagation();
                hideColumnDecorations();
                ColumnDrag.instance = null;
            }
        });
        Ui.on(shell, Ui.Events.DROP, function (event) {
            var source = ColumnDrag.instance;
            var target = targetDraggedColumn;
            hideColumnDecorations();
            ColumnDrag.instance = null;
            if (source) {
                event.preventDefault();
                event.stopPropagation();
                if (source.move) {
                    // target table may be any section in our grid
                    if (target) {
                        var sourceHeader = source.header;
                        var targetHeader = target.header;
                        moveColumnNode(sourceHeader.headerNode, targetHeader.headerNode);
                    }
                } else {
                    var header = source.header;
                    if (header.resizable) {
                        var newWidth = Math.max(
                                event.clientX - Ui.absoluteLeft(source.decorationElement),
                                MINIMUM_COLUMN_WIDTH);
                        if (newWidth >= header.column.minWidth && newWidth <= header.column.maxWidth) {
                            header.column.width = newWidth;
                        }
                    }
                }
            }
        });


        (function () {
            function fillColumns(aTarget, aSection) {
                for (var i = 0; i < aSection.columnCount; i++) {
                    var column = aSection.getColumn(i);
                    var miCheck = new MenuItemCheckBox(column.visible, column.header.text, true);
                    miCheck.addValueChangeHandler(function (event) {
                        column.visible = !!event.newValue;
                    });
                    aTarget.add(miCheck);
                }
            }
            Ui.on(columnsChevron, Ui.Events.CLICK, function (event) {
                var columnsMenu = new Menu();
                fillColumns(columnsMenu, headerLeft);
                fillColumns(columnsMenu, headerRight);
                columnsMenu.showRelativeTo(columnsChevron);
            });
        }());

        gridColor = new Color(211, 211, 211, 255);
        regenerateDynamicHeaderRowsStyles();
        regenerateDynamicRowsStyles();
        regenerateDynamicOddRowsStyles();
        regenerateDynamicCellsStyles();

        Ui.on(shell, Ui.Events.KEYUP, function (event) {
            var rows = discoverRows();
            if (!activeEditor) {
                if (event.keyCode === KeyCodes.KEY_DELETE && deletable) {
                    if (sortedRows.length > 0) {
                        // calculate some view sugar
                        var lastSelectedViewIndex = -1;
                        for (var i = sortedRows.length - 1; i >= 0; i--) {
                            var element = sortedRows[i];
                            if (isSelected(element)) {
                                lastSelectedViewIndex = i;
                                break;
                            }
                        }
                        // actually delete selected elements
                        var deletedAt = -1;
                        for (var i = rows.length - 1; i >= 0; i--) {
                            var row = rows[i];
                            if (isSelected(row)) {
                                rows.splice(i, 1);
                                deletedAt = i;
                            }
                        }
                        var viewIndexToSelect = lastSelectedViewIndex;
                        if (deletedAt > -1) {
                            // TODO: Check if Invoke.Later is an option
                            var vIndex = viewIndexToSelect;
                            if (vIndex >= 0 && sortedRows.length > 0) {
                                if (vIndex >= sortedRows.length) {
                                    vIndex = sortedRows.length - 1;
                                }
                                var toSelect = sortedRows[vIndex];
                                makeVisible(toSelect, true);
                            } else {
                                self.setFocus(true);
                            }
                        }
                    }
                } else if (event.keyCode === KeyCodes.KEY_INSERT && insertable) {
                    var insertAt = -1;
                    var lead = selectionLead;
                    insertAt = rows.indexOf(lead);
                    insertAt++;
                    var elementClass = rows['elementClass'];
                    var inserted = elementClass ? new elementClass()
                            : {};
                    rows.splice(insertAt, 0, inserted);
                    // TODO: Check if Invoke.Later is an option
                    makeVisible(inserted, true);
                }
            }
        });

        function discoverRows() {
            var rows = data && field ? Bound.getPathData(data, field) : data;
            return rows ? rows : [];
        }

        function removedItems(anArray) {
            if (!Array.isArray(anArray))
                anArray = [anArray];
            rebindElements();
            redraw();
        }

        Object.defineProperty(this, 'removed', {
            get: function () {
                return removedItems;
            }
        });

        function addedItems(anArray) {
            if (!Array.isArray(anArray))
                anArray = [anArray];
            rebindElements();
            redraw();
        }

        Object.defineProperty(this, 'added', {
            get: function () {
                return addedItems;
            }
        });

        function changedItems(anArray) {
            if (!Array.isArray(anArray))
                anArray = [anArray];
            redraw();
        }

        Object.defineProperty(this, 'changed', {
            get: function () {
                return changedItems;
            }
        });

        function isSelected(item) {
            return selectedRows.has(item);
        }

        function select(item) {
            selectedRows.add(item);
            selectionLead = item;
            var rows = discoverRows();
            rows[cursorProperty] = selectionLead;
            fireSelected(item);
        }

        function unselect(item) {
            if (selectionLead === item) {
                selectionLead = null;
            }
            return selectedRows.delete(item);
        }

        function clearSelection() {
            selectedRows.clear();
            if (selectedRows.has(selectionLead)) {
                selectionLead = null;
            }
            fireSelected(null);
        }

        function findTargetDraggedColumn(aEventTarget) {
            var targetSection = null;
            var targetCell = null;
            var currentTarget = aEventTarget;
            if (COLUMN_PHANTOM_STYLE.equals(currentTarget.className)
                    || RULER_STYLE.equals(currentTarget.className)) {
                return targetDraggedColumn;
            } else {
                while ((!targetCell || !targetSection) && currentTarget
                        && currentTarget !== self.shell) {
                    if (!targetCell) {
                        if ("td" === currentTarget.tagName.toLowerCase()
                                || "th" === currentTarget.tagName.toLowerCase()) {
                            targetCell = currentTarget;
                        }
                    }
                    if (!targetSection) {
                        if (currentTarget === headerLeft.element) {
                            targetSection = headerLeft;
                        } else if (currentTarget === frozenLeft.element) {
                            targetSection = frozenLeft;
                        } else if (currentTarget === scrollableLeft.element) {
                            targetSection = scrollableLeft;
                        } else if (currentTarget === footerLeft.element) {
                            targetSection = footerLeft;
                        } else if (currentTarget === headerRight.element) {
                            targetSection = headerRight;
                        } else if (currentTarget === frozenRight.element) {
                            targetSection = frozenRight;
                        } else if (currentTarget === scrollableRight.element) {
                            targetSection = scrollableRight;
                        } else if (currentTarget === footerRight.element) {
                            targetSection = footerRight;
                        }
                    }
                    currentTarget = currentTarget.parentElement;
                }
                if (targetSection && targetCell) {
                    var header = targetCell[HeaderView.HEADER_VIEW];
                    if (header) {
                        return new ColumnDrag(header, targetCell);
                    } else {
                        return null;
                    }
                }
                return null;
            }
        }

        function hideColumnDecorations() {
            ghostLine.parentElement.removeChild(ghostLine);
            ghostColumn.parentElement.removeChild(ghostColumn);
            targetDraggedColumn = null;
        }

        function showColumnMoveDecorations(target) {
            targetDraggedColumn = target;
            var hostElement = shell;
            var thtdElement = target.decorationElement;
            var thLeft = Ui.absoluteLeft(thtdElement);
            thLeft = thLeft - Ui.absoluteLeft(shell) + hostElement.scrollLeft;
            ghostLine.style.left = thLeft + 'px';
            ghostLine.style.height = hostElement.clientHeight + 'px';
            ghostColumn.style.left = thLeft + 'px';
            ghostColumn.style.width = thtdElement.offsetWidth + 'px';
            ghostColumn.style.height = hostElement.clientHeight + 'px';
            if (ghostLine.parentElement !== hostElement) {
                ghostLine.parentElement.removeChild(ghostLine);
                hostElement.appendChild(ghostLine);
            }
            if (ghostColumn.parentElement !== hostElement) {
                ghostColumn.parentElement.removeChild(ghostColumn);
                hostElement.appendChild(ghostColumn);
            }
        }

        Object.defineProperty(this, 'dynamicCellClassName', {
            get: function () {
                return dynamicCellClassName;
            }
        });

        Object.defineProperty(this, 'showHorizontalLines', {
            get: function () {
                return showHorizontalLines;
            },
            set: function (aValue) {
                if (showHorizontalLines !== aValue) {
                    showHorizontalLines = !!aValue;
                    regenerateDynamicCellsStyles();
                }
            }
        });

        Object.defineProperty(this, 'showVerticalLines', {
            get: function () {
                return showVerticalLines;
            },
            set: function (aValue) {
                if (showVerticalLines !== aValue) {
                    showVerticalLines = !!aValue;
                    regenerateDynamicCellsStyles();
                }
            }
        });

        function regenerateDynamicCellsStyles() {
            cellsStyleElement.innerHTML =
                    '.' + dynamicCellClassName + '{' +
                    'border-left-width: ' + (showHorizontalLines ? 1 : 0) + 'px;' +
                    'border-right-width: ' + (showVerticalLines ? 1 : 0) + 'px;' +
                    'border-color: ' + (gridColor ? gridColor.toStyled() : 'auto') + ';' +
                    '}';
        }

        function regenerateDynamicOddRowsStyles() {
            if (showOddRowsInOtherColor && oddRowsColor) {
                oddRowsStyleElement.innerHTML =
                        '.' + dynamicOddRowsClassName + '{' +
                        'background-color: ' + oddRowsColor.toStyled() +
                        '}';
            } else {
                oddRowsStyleElement.innerHTML = '';
            }
        }

        Object.defineProperty(this, 'gridColor', {
            get: function () {
                return gridColor;
            },
            set: function (aValue) {
                if (gridColor !== aValue) {
                    gridColor = aValue;
                    regenerateDynamicCellsStyles();
                }
            }
        });
        Object.defineProperty(this, 'oddRowsColor', {
            get: function () {
                return oddRowsColor;
            },
            set: function (aValue) {
                if (oddRowsColor !== aValue) {
                    oddRowsColor = aValue;
                    regenerateDynamicOddRowsStyles();
                }
            }
        });

        Object.defineProperty(this, 'rowsHeight', {
            get: function () {
                return rowsHeight;
            },
            set: function (aValue) {
                if (rowsHeight !== aValue && aValue >= 10) {
                    rowsHeight = aValue;
                    regenerateDynamicRowsStyles();
                }
            }
        });

        function regenerateDynamicRowsStyles() {
            rowsStyleElement.innerHTML =
                    '.' + dynamicCellClassName + '{' +
                    ' height: ' + headerRowsHeight + 'px;' +
                    '}';
        }

        Object.defineProperty(this, 'headerRowsHeight', {
            get: function () {
                return headerRowsHeight;
            },
            set: function (aValue) {
                if (headerRowsHeight !== aValue && aValue >= 10) {
                    headerRowsHeight = aValue;
                    regenerateDynamicHeaderRowsStyles();
                }
            }
        });

        function regenerateDynamicHeaderRowsStyles() {
            headerRowsStyleElement.innerHTML =
                    '.' + dynamicCellClassName + '{' +
                    ' height: ' + rowsHeight + 'px;' +
                    '}';
        }

        Object.defineProperty(this, 'headerVisible', {
            get: function () {
                return 'none' !== headerLeft.element.style.display
                        && 'none' !== headerRight.element.style.display;
            },
            set: function (aValue) {
                if (aValue) {
                    columnsChevron.style.display = '';
                    headerLeftContainer.style.display = '';
                    headerRightContainer.style.display = '';
                } else {
                    columnsChevron.style.display = 'none';
                    headerLeftContainer.style.display = 'none';
                    headerRightContainer.style.display = 'none';
                }
            }
        });

        Object.defineProperty(this, 'frozenColumns', {
            get: function () {
                return frozenColumns;
            },
            set: function (aValue) {
                if (aValue >= 0 && frozenColumns !== aValue) {
                    frozenColumns = aValue;
                    applyColumnsNodes();
                }
            }
        });

        Object.defineProperty(this, 'frozenRows', {
            get: function () {
                return frozenRows;
            },
            set: function (aValue) {
                if (aValue >= 0 && frozenRows !== aValue) {
                    frozenRows = aValue;
                    setupRanges();
                }
            }
        });

        Object.defineProperty(this, 'showOddRowsInOtherColor', {
            get: function () {
                return showOddRowsInOtherColor;
            },
            set: function (aValue) {
                if (showOddRowsInOtherColor !== aValue) {
                    showOddRowsInOtherColor = aValue;
                    regenerateDynamicOddRowsStyles();
                }
            }
        });

        Object.defineProperty(this, 'draggableRows', {
            get: function () {
                return draggableRows;
            },
            set: function (aValue) {
                if (draggableRows !== aValue) {
                    draggableRows = aValue;
                    [frozenLeft, frozenRight, scrollableLeft, scrollableRight].forEach(function (section) {
                        section.draggableRows = aValue;
                    });
                }
            }
        });

        Object.defineProperty(this, 'activeEditor', {
            get: function () {
                return activeEditor;
            },
            set: function (aWidget) {
                activeEditor = aWidget;
            }
        });

        Object.defineProperty(this, 'onRender', {
            get: function () {
                return onRender;
            },
            set: function (aValue) {
                onRender = aValue;
            }
        });

        Object.defineProperty(this, 'onAfterRender', {
            get: function () {
                return onAfterRender;
            },
            set: function (aValue) {
                onAfterRender = aValue;
            }
        });

        Object.defineProperty(this, 'cursorProperty', {
            get: function () {
                return cursorProperty;
            },
            set: function (aValue) {
                if (aValue && cursorProperty !== aValue) {
                    unbind();
                    cursorProperty = aValue;
                    bind();
                }
            }
        });

        Object.defineProperty(this, 'editable', {
            get: function () {
                return editable;
            },
            set: function (aValue) {
                editable = aValue;
            }
        });

        Object.defineProperty(this, 'deletable', {
            get: function () {
                return deletable;
            },
            set: function (aValue) {
                deletable = aValue;
            }
        });

        Object.defineProperty(this, 'insertable', {
            get: function () {
                return insertable;
            },
            set: function (aValue) {
                insertable = aValue;
            }
        });

        var serviceColumnsRedrawQueued = null;

        function enqueueServiceColumnsRedraw() {
            function redrawServiceColumns() {
                if (serviceColumnsRedrawQueued === redrawServiceColumns) {
                    serviceColumnsRedrawQueued = null;
                    for (var i = 0; i < getColumnCount(); i++) {
                        var col = getColumn(i);
                        if (col instanceof MarkerServiceColumn) {
                            if (i < frozenColumns) {
                                frozenLeft.redrawColumn(i);
                                scrollableLeft.redrawColumn(i);
                            } else {
                                frozenRight.redrawColumn(i - frozenColumns);
                                scrollableRight.redrawColumn(i - frozenColumns);
                            }
                        }
                    }
                }
            }
            Invoke.later(redrawServiceColumns);
            serviceColumnsRedrawQueued = redrawServiceColumns;
        }

        var redrawQueued = null;

        function enqueueRedraw() {
            function callredraw() {
                if (redrawQueued === callredraw) {
                    redrawQueued = null;
                    redraw();
                }
            }
            Invoke.later(callredraw);
            redrawQueued = callredraw;
        }

        function isExpanded(anElement) {
            return isTreeConfigured() && expandedRows.has(anElement);
        }

        Object.defineProperty(this, 'expanded', {
            get: function () {
                return isExpanded;
            }
        });

        function expand(anElement) {
            if (isTreeConfigured()) {
                if (!expandedRows.has(anElement)) {
                    var children = getChildrenOf(anElement);
                    if (children && children.length > 0) {
                        expandedRows.add(anElement);
                        regenerateSortedRows(false);
                        sortSortedRows(true);
                        fireExpanded(anElement);
                    }
                }
            }
        }

        Object.defineProperty(this, 'expand', {
            get: function () {
                return expand;
            }
        });

        function collapse(anElement) {
            if (isTreeConfigured()) {
                if (expandedRows.has(anElement)) {
                    expandedRows.delete(anElement);
                    regenerateSortedRows(false);
                    sortSortedRows(true);
                    fireCollapsed(anElement);
                }
            }
        }

        Object.defineProperty(this, 'collapse', {
            get: function () {
                return collapse;
            }
        });

        function toggle(anElement) {
            if (isTreeConfigured()) {
                if (isExpanded(anElement)) {
                    collapse(anElement);
                } else {
                    expand(anElement);
                }
            }
        }

        Object.defineProperty(this, 'toggle', {
            get: function () {
                return toggle;
            }
        });

        // Tree structure
        function isLeaf(anElement) {
            return !hasRowChildren(anElement);
        }

        function hasRowChildren(parent) {
            var children = findChildren(parent);
            return children && children.length > 0;
        }

        function findChildren(aParent) {
            if (aParent) {
                return aParent[childrenField];
            } else {
                var rows = discoverRows();
                var roots = [];
                for (var i = 0; i < rows.length; i++) {
                    var item = rows[i];
                    if (item && !item[parentField]) {
                        roots.push(item);
                    }
                }
                return roots;
            }
        }

        function getParentOf(anElement) {
            return anElement[parentField];
        }

        function getChildrenOf(anElement) {
            var found = findChildren(anElement);
            return found ? found : [];
        }

        /**
         * Builds path to specified element if the element belongs to the model.
         *
         * @param anElement Element to build path to.
         * @return List<JavaScriptObject> of elements comprising the path, excluding
         * root null. So for the roots of the forest path will be a list with one
         * element.
         */
        function buildPathTo(anElement) {
            var path = [];
            if (anElement) {
                var currentParent = anElement;
                var added = new Set();
                path.push(currentParent);
                added.add(currentParent);
                while (currentParent) {
                    currentParent = getParentOf(currentParent);
                    if (added.has(currentParent)) {
                        break;
                    }
                    if (currentParent) {
                        path.add(0, currentParent);
                        added.add(currentParent);
                    }
                }
            }
            return path;
        }

        function makeVisible(anElement, aNeedToSelect) {
            // TODO: refactor indexof to something else
            // TODO: think about tree data model and path to item expanding
            //IndexOfProvider<JavaScriptObject> indexOfProvider = (IndexOfProvider<JavaScriptObject>) dataProvider;
            var index = -1;//indexOfProvider.indexOf(anElement);
            if (index > -1) {
                if (index >= 0 && index < frozenRows) {
                    var leftCell = frozenLeft.getViewCell(index, 0);
                    if (leftCell) {
                        leftCell.scrollIntoView();
                    } else {
                        var rightCell = frozenRight.getViewCell(index, 0);
                        if (rightCell) {
                            rightCell.scrollIntoView();
                        }
                    }
                } else {
                    var leftCell = scrollableLeft.getViewCell(index, 0);
                    if (leftCell) {
                        leftCell.scrollIntoView();
                    } else {
                        var rightCell = scrollableRight.getViewCell(index, 0);
                        if (rightCell) {
                            rightCell.scrollIntoView();
                        }
                    }
                }
                if (aNeedToSelect) {
                    clearSelection();
                    select(anElement);
                    if (index >= 0 && index < frozenRows) {
                        frozenLeft.keyboardSelectedRow = index;
                        frozenRight.keyboardSelectedRow = index;
                    } else {
                        scrollableLeft.keyboardSelectedRow = index - frozenRows;
                        scrollableRight.keyboardSelectedRow = index - frozenRows;
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        Object.defineProperty(this, 'makeVisible', {
            get: function () {
                return makeVisible;
            }
        });

        function rebind() {
            unbind();
            bind();
        }

        function bind() {
            if (data && field) {
                boundToData = Bound.observePath(data, field, function (anEvent) {
                    rebind();
                    redraw();
                });
                bindElements();
                bindCursor();
            }
        }

        function bindElements() {
            var rows = discoverRows();
            boundToElements = Bound.observeElements(rows, function (anEvent) {
                redraw();
            });
        }

        function unbindElements() {
            if (boundToElements) {
                boundToElements.removeHandler();
                boundToElements = null;
            }
        }

        function rebindElements() {
            unbindElements();
            bindElements();
        }

        function unbind() {
            selectedRows.clear();
            expandedRows.clear();
            if (boundToData) {
                boundToData.removeHandler();
                boundToData = null;
            }
            unbindElements();
            unbindCursor();
        }

        function bindCursor() {
            if (data) {
                var rows = discoverRows();
                boundToCursor = Bound.observePath(rows, cursorProperty, function (anEvent) {
                    enqueueServiceColumnsRedraw();
                });
            }
        }

        function unbindCursor() {
            if (boundToCursor) {
                boundToCursor.removeHandler();
                boundToCursor = null;
            }
        }

        Object.defineProperty(this, 'data', {
            get: function () {
                return data;
            },
            set: function (aValue) {
                if (data !== aValue) {
                    unbind();
                    data = aValue;
                    bind();
                }
            }
        });

        Object.defineProperty(this, 'field', {
            get: function () {
                return field;
            },
            set: function (aValue) {
                if (field !== aValue) {
                    unbind();
                    field = aValue;
                    bind();
                }
            }
        });

        Object.defineProperty(this, 'parentField', {
            get: function () {
                return parentField;
            },
            set: function (aValue) {
                if (parentField !== aValue) {
                    var wasTree = isTreeConfigured();
                    parentField = aValue;
                    var isTree = isTreeConfigured();
                    if (wasTree !== isTree) {
                        expandedRows.clear();
                        regenerateSortedRows(false);
                        sortSortedRows(true);
                    }
                }
            }
        });

        Object.defineProperty(this, 'childrenField', {
            get: function () {
                return childrenField;
            },
            set: function (aValue) {
                if (childrenField !== aValue) {
                    var wasTree = isTreeConfigured();
                    childrenField = aValue;
                    var isTree = isTreeConfigured();
                    if (wasTree !== isTree) {
                        expandedRows.clear();
                        regenerateSortedRows(false);
                        sortSortedRows(true);
                    }
                }
            }
        });

        function isTreeConfigured() {
            return parentField && childrenField;
        }

        function setupRanges() {
            frozenLeft.setRange(0, frozenRows);
            frozenRight.setRange(0, frozenRows);
            var rows = discoverRows();
            scrollableLeft.setRange(frozenRows, rows.length);
            scrollableRight.setRange(frozenRows, rows.length);
        }

        var treeIndicatorColumn;

        function checkTreeIndicatorColumn() {
            if (isTreeConfigured()) {
                if (!treeIndicatorColumn) {
                    var treeIndicatorIndex = 0;
                    while (treeIndicatorIndex < getColumnCount()) {
                        var indicatorColumn = getColumn(treeIndicatorIndex);
                        if (indicatorColumn instanceof MarkerServiceColumn || indicatorColumn instanceof RadioButtonServiceColumn
                                || indicatorColumn instanceof CheckBoxServiceColumn) {
                            treeIndicatorIndex++;
                        } else {
                            treeIndicatorColumn = indicatorColumn;
                            break;
                        }
                    }
                }
            } else {
                treeIndicatorColumn = null;
            }
        }

        function clearColumnsNodes(needRedraw) {
            if (arguments.length < 1)
                needRedraw = true;
            for (var i = getColumnCount() - 1; i >= 0; i--) {
                var toDel = getColumn(i);
                var mCol = toDel;
                if (mCol === treeIndicatorColumn) {
                    treeIndicatorColumn = null;
                }
                mCol.setGrid(null);
            }
            headerLeft.clearColumnsAndHeader(needRedraw);
            headerRight.clearColumnsAndHeader(needRedraw);
        }

        function applyColumnsNodes() {
            clearColumnsNodes(false);
            var leaves = HeaderAnalyzer.toLeaves(header);
            leaves.forEach(function (leaf) { // linear list of columner header nodes
                addColumnToSections(leaf.column);
            });
            checkTreeIndicatorColumn();
            headerLeft.setHeaderNodes(HeaderSplitter.split(header, 0, frozenColumns - 1), false);
            headerRight.setHeaderNodes(HeaderSplitter.split(header, frozenColumns, getColumnCount()), false);
            redraw();
        }


        Object.defineProperty(this, 'header', {
            get: function () {
                return header;
            },
            set: function (aHeader) {
                if (header !== aHeader) {
                    header = aHeader;
                    applyColumnsNodes();
                }
            }
        });

        function removeColumnNode(aNode) {
            var nodeIndex = header.indexOf(aNode);
            if (nodeIndex !== -1) {
                header.splice(nodeIndex, 1);
                aNode.column.grid = null;
                if (treeIndicatorColumn === aNode.column) {
                    treeIndicatorColumn = null;
                }
                applyColumnsNodes();
                return true;
            } else {
                return false;
            }
        }

        Object.defineProperty(this, 'removeColumnNode', {
            get: function () {
                return removeColumnNode;
            }
        });

        function addColumnNode(aNode) {
            header.push(aNode);
            aNode.column.grid = self;
            applyColumnsNodes();
        }

        Object.defineProperty(this, 'addColumnNode', {
            get: function () {
                return addColumnNode;
            }
        });

        function insertColumnNode(aIndex, aNode) {
            header.splice(aIndex, 0, aNode);
            aNode.column.grid = self;
            applyColumnsNodes();
        }

        Object.defineProperty(this, 'insertColumnNode', {
            get: function () {
                return insertColumnNode;
            }
        });

        function moveColumnNode(aSubject, aInsertBefore) {
            if (aSubject && aInsertBefore && aSubject.parent === aInsertBefore.parent) {
                var neighbours = aSubject.parent ? aSubject.parent.children : header;
                var neighbourIndex = neighbours.indexOf(aSubject);
                neighbours.splice(neighbourIndex, 1);
                var insertAt = neighbours.indexOf(aInsertBefore);
                neighbours.splice(insertAt, 0, aSubject);
                applyColumnsNodes();
            }
        }

        function addColumnToSections(aColumn) {
            if (headerLeft.columnCount < frozenColumns) {
                headerLeft.addColumn(aColumn, false);
                frozenLeft.addColumn(aColumn, false);
                scrollableLeft.addColumn(aColumn, false);
                footerLeft.addColumn(aColumn, false);
            } else {
                headerRight.addColumn(aColumn, false);
                frozenRight.addColumn(aColumn, false);
                scrollableRight.addColumn(aColumn, false);
                footerRight.addColumn(aColumn, false);
            }
        }

        function redrawRow(index) {
            frozenLeft.redrawRow(index);
            frozenRight.redrawRow(index);
            scrollableLeft.redrawRow(index);
            scrollableRight.redrawRow(index);
        }

        function redraw() {
            headerRight.redraw();
            frozenLeft.redraw();
            frozenRight.redraw();
            scrollableLeft.redraw();
            scrollableRight.redraw();
            footerLeft.redraw();
            footerRight.redraw();
        }

        Object.defineProperty(this, 'redraw', {
            get: function () {
                return redraw;
            }
        });

        function redrawHeaders() {
            headerLeft.redrawHeaders();
            headerRight.redrawHeaders();
        }

        function redrawFooters() {
            footerLeft.redrawFooters();
            footerRight.redrawFooters();
        }

        function getColumnCount() {
            return (headerLeft ? headerLeft.columnCount : 0)
                    + (headerRight ? headerRight.columnCount : 0);
        }

        function getColumn(aIndex) {
            if (aIndex >= 0 && aIndex < getColumnCount()) {
                return aIndex >= 0 && aIndex < headerLeft.columnCount ? headerLeft.getColumn(aIndex)
                        : headerRight.getColumn(aIndex - headerLeft.columnCount);
            } else {
                return null;
            }
        }

        function getViewCell(aRow, aCol) {
            var targetSection;
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

        function focusViewCell(aRow, aCol) {
            var targetSection;
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

        function sort() {
            regenerateSortedRows(false);
            sortSortedRows(true);
        }

        function regenerateSortedRows(needRedraw) {
            if (arguments.length < 1)
                needRedraw = true;
            var rows = discoverRows();
            if (isTreeConfigured()) {
                sortedRows = [];
                var children = getChildrenOf(null);
                Array.prototype.push.apply(sortedRows, children);
                var i = 0;
                while (i < sortedRows.length) {
                    if (expandedRows.has(sortedRows[i])) {
                        var children1 = getChildrenOf(sortedRows[i]);
                        var spliceArgs = children1;
                        spliceArgs.unshift(0);
                        spliceArgs.unshift(i + 1);
                        Array.prototype.splice.apply(sortedRows, spliceArgs); // splice(i + 1, 0, flattern -> children1);
                    }
                    ++i;
                }
            } else {
                sortedRows = rows.slice(0, rows.length);
            }
            if (needRedraw) {
                redraw();
                fireRowsSort();
            }
        }

        function sortSortedRows(needRedraw) {
            if (arguments.length < 1)
                needRedraw = true;
            var sortedColumns = 0;
            for (var c = 0; c < getColumnsCount(); c++) {
                var column = getColumn(c);
                if (column.comparator) {
                    sortedColumns++;
                }
            }
            if (sortedColumns > 0) {
                sortedRows.sort(function (o1, o2) {
                    if (isTreeConfigured() && getParentOf(o1) !== getParentOf(o2)) {
                        var path1 = buildPathTo(o1);
                        var path2 = buildPathTo(o2);
                        if (path2.contains(o1)) {
                            // o1 is parent of o2
                            return -1;
                        }
                        if (path1.contains(o2)) {
                            // o2 is parent of o1
                            return 1;
                        }
                        for (var p = 0; p < Math.min(path1.length, path2.length); p++) {
                            if (path1[p] !== path2[p]) {
                                o1 = path1[p];
                                o2 = path2[p];
                                break;
                            }
                        }
                    }
                    var res = 0;
                    var index = 0;
                    while (res === 0 && index < getColumnsCount()) {
                        var column = getColumn(index++);
                        if (column.comparator) {
                            res = column.comparator(o1, o2);
                        }
                    }
                    return res;
                });
            }
            if (needRedraw) {
                redraw();
                fireRowsSort();
            }
        }

        function unsort(needRedraw) {
            if (arguments.length < 1)
                needRedraw = true;
            for (var i = 0; i < getColumnCount(); i++) {
                var column = getColumn(i);
                column.unsort(false);
            }
            regenerateSortedRows(needRedraw);
        }

        Object.defineProperty(this, 'unsort', {
            get: function () {
                return unsort;
            }
        });

        var tabIndex = 0;

        function calcFocusedElement() {
            var focusedEelement = scrollableRight.getKeyboardSelectedElement();
            if (!focusedEelement) {
                focusedEelement = scrollableLeft.getKeyboardSelectedElement();
            }
            if (!focusedEelement) {
                focusedEelement = frozenLeft.getKeyboardSelectedElement();
            }
            if (!focusedEelement) {
                focusedEelement = frozenRight.getKeyboardSelectedElement();
            }
            if (!focusedEelement) {
                focusedEelement = getElement();
            }
            return focusedEelement;
        }

        Object.defineProperty(this, 'tabIndex', {
            get: function () {
                return tabIndex;
            },
            set: function (index) {
                tabIndex = index;
                focusedElement = calcFocusedElement();
                if (focusedElement)
                    focusedElement.tabIndex = tabIndex;
            }
        });

        function setFocus(focused) {
            var focusedElement = calcFocusedElement();
            focusedElement.tabIndex = tabIndex;
            if (focused) {
                focusedElement.focus();
            } else {
                focusedElement.blur();
            }
        }
        
        var expandListeners = new Set();
        function addExpandHandler(h) {
            expandListeners.add(h);
            return {
                removeHandler: function () {
                    expandListeners.delete(h);
                }
            };
        }
        Object.defineProperty(this, 'addExpandHandler', {
            get: function () {
                return addExpandHandler;
            }
        });

        function fireExpanded(anElement) {
            fireRowsSort();
            var event = new ItemEvent(self, anElement);
            expandListeners.forEach(function(h){
                Invoke.later(function(){
                    h(event);
                });
            });
        }

        var onExpand;
        var expandedReg;
        Object.defineProperty(this, 'onExpand', {
            get: function () {
                return onExpand;
            },
            set: function (aValue) {
                if (onExpand !== aValue) {
                    if (expandedReg) {
                        expandedReg.removeHandler();
                        expandedReg = null;
                    }
                    onExpand = aValue;
                    if (onExpand) {
                        expandedReg = addExpandHandler(function (event) {
                            if (onExpand) {
                                onExpand(event);
                            }
                        });
                    }
                }
            }
        });

        var collapseHandlers = new Set();
        function addCollapseHandler(h) {
            collapseHandlers.add(h);
            return {
                removeHandler: function () {
                    collapseHandlers.delete(h);
                }
            };
        }
        Object.defineProperty(this, 'addCollapseHandler', {
            get: function () {
                return addCollapseHandler;
            }
        });

        function fireCollapsed(anElement) {
            fireRowsSort();
            var event = new ItemEvent(self, anElement);
            collapseHandlers.forEach(function(h){
                Invoke.later(function(){
                    h(event);
                });
            });
        }

        var onCollapse;
        var collapseReg;
        Object.defineProperty(this, 'onCollapse', {
            get: function () {
                return onCollapse;
            },
            set: function (aValue) {
                if (onCollapse !== aValue) {
                    if (collapseReg) {
                        collapseReg.removeHandler();
                        collapseReg = null;
                    }
                    onCollapse = aValue;
                    if (onCollapse) {
                        collapseReg = addCollapseHandler(function (event) {
                            if (onCollapse) {
                                onCollapse(event);
                            }
                        });
                    }
                }
            }
        });
        
        var sortHandlers = new Set();
        function addSortHandler(handler) {
            sortHandlers.add(handler);
            return  {
                removeHandler: function () {
                    sortHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addSortHandler', {
            get: function(){
                return addSortHandler;
            }
        });

        function fireRowsSort() {
            var event = new SortEvent(this);
            sortHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var onSort;
        var sortedReg;
        Object.defineProperty(this, 'onSort', {
            get: function () {
                return onSort;
            },
            set: function (aValue) {
                if (onSort !== aValue) {
                    if (sortedReg) {
                        sortedReg.removeHandler();
                        sortedReg = null;
                    }
                    onSort = aValue;
                    if (onSort) {
                        sortedReg = addSortHandler(function (event) {
                            if (onSort) {
                                onSort(event);
                            }
                        });
                    }
                }
            }
        });
        
        var selectionHandlers = new Set();
        function addSelectionHandler(handler) {
            selectionHandlers.add(handler);
            return {
                removeHandler: function () {
                    selectionHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addSelectionHandler', {
            get: function(){
                return addSelectionHandler;
            }
        });

        function fireSelected(item) {
            var event = new ItemEvent(self, item);
            selectionHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var focusHandlers = new Set();
        function addFocusHandler(handler) {
            focusHandlers.add(handler);
            return {
                removeHandler: function () {
                    focusHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addFocusHandler', {
            get: function(){
                return addFocusHandler;
            }
        });

        function fireFocus() {
            var event = new FocusEvent(self);
            focusHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var blurHandlers = new Set();
        function addBlurHandler(handler) {
            blurHandlers.add(handler);
            return {
                removeHandler: function () {
                    blurHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addBlurHandler', {
            get: function(){
                return addBlurHandler;
            }
        });

        function fireBlur() {
            var event = new BlurEvent(self);
            blurHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var keyUpHandlers = new Set();
        function addKeyUpHandler(handler) {
            keyUpHandlers.add(handler);
            return {
                removeHandler: function () {
                    keyUpHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addKeyUpHandler', {
            get: function(){
                return addKeyUpHandler;
            }
        });

        function fireKeyUp(nevent) {
            var event = new KeyEvent(self, nevent);
            keyUpHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var keyDownHandlers = new Set();
        function addKeyDownHandler(handler) {
            keyDownHandlers.add(handler);
            return {
                removeHandler: function () {
                    keyDownHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addKeyDownHandler', {
            get: function(){
                return addKeyDownHandler;
            }
        });

        function fireKeyDown(nevent) {
            var event = new KeyEvent(self, nevent);
            keyDownHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var keyPressHandlers = new Set();
        function addKeyPressHandler(handler) {
            keyPressHandlers.add(handler);
            return {
                removeHandler: function () {
                    keyPressHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addKeyPressHandler', {
            get: function(){
                return addKeyPressHandler;
            }
        });

        function fireKeyPress(nevent) {
            var event = new KeyEvent(this, nevent);
            keyPressHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }
    }
    extend(Grid, Widget);
    return Grid;
});
