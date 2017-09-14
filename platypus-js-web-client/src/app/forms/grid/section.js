define([
    '../../ui',
    '../../invoke',
    '../../logger',
    '../key-codes'
], function (
        Ui,
        Invoke,
        Logger,
        KeyCodes
        ) {

    var JS_ROW_NAME = "js-row";

    function Section(grid, dynamicCellsClassName,
            dynamicRowsClassName,
            dynamicHeaderCellsClassName,
            dynamicHeaderRowsClassName,
            dynamicOddRowsClassName,
            dynamicEvenRowsClassName) {
        var self = this;

        var table = document.createElement('table');
        table.className = 'p-grid-section';
        var colgroup = document.createElement('colgroup');
        var rowsHeight = 30;
        var headerNodes = [];
        var headerMaxDepth = 0;
        var keyboardSelectedElement = null;
        var draggableRows = false;
        var keyboardSelectedRow = -1;
        var keyboardSelectedColumn = -1;
        var rowsPerPage = 30; // Only for PageDown or PageUp keys handling
        var data = []; // Already sorted
        /**
         * Already sorted data array indices;
         */
        var startRow = 0; // Inclusive
        var endRow = 0; // Exclusive
        var columns = [];

        var thead;
        recreateHead();
        var tbody;
        recreateBody();
        var tfoot;
        recreateFoot();
        var bodyFiller = document.createElement('div');
        bodyFiller.className = 'p-grid-body-filler';

        Ui.on(table, Ui.Events.KEYDOWN, function (event) {
            var oldRow = self.keyboardSelectedRow;
            var oldColumn = self.keyboardSelectedColumn;
            var keyCode = event.keyCode;
            if (keyCode === KeyCodes.KEY_LEFT) {
            } else if (keyCode === KeyCodes.KEY_RIGHT) {
            } else if (keyCode === KeyCodes.KEY_UP) {
            } else if (keyCode === KeyCodes.KEY_DOWN) {
            } else if (keyCode === KeyCodes.KEY_PAGEUP) {
            } else if (keyCode === KeyCodes.KEY_PAGEDOWN) {
            } else if (keyCode === KeyCodes.KEY_HOME) {
            } else if (keyCode === KeyCodes.KEY_END) {
            }
        });


        Object.defineProperty(this, 'element', {
            get: function () {
                return table;
            }
        });
        Object.defineProperty(this, 'data', {
            get: function () {
                return data;
            },
            set: function (aValue) {
                if (data !== aValue) {
                    data = aValue;
                }
            }
        });
        Object.defineProperty(this, 'rowsHeight', {
            get: function () {
                return rowsHeight;
            },
            set: function (aValue) {
                if (rowsHeight !== aValue) {
                    rowsHeight = aValue;
                    redrawBody();
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
                }
            }
        });
        Object.defineProperty(this, 'keyboardSelectedColumn', {
            get: function () {
                return keyboardSelectedColumn;
            },
            set: function (aValue) {
                if (keyboardSelectedColumn !== aValue) {
                    keyboardSelectedColumn = aValue;
                }
            }
        });
        Object.defineProperty(this, 'keyboardSelectedRow', {
            get: function () {
                return keyboardSelectedRow;
            },
            set: function (aValue) {
                if (keyboardSelectedRow !== aValue) {
                    keyboardSelectedRow = aValue;
                }
            }
        });

        function insertColumn(index, aColumn, needRedraw) {
            if (arguments.length < 3)
                needRedraw = true;
            if (index >= 0 && index <= columns.length) { // It is all about insertBefore
                if (index < columns.length) { // It is all about insertBefore
                    columns.splice(index, 0, aColumn);
                    var col = colgroup.getChild(index);
                    colgroup.insertBefore(aColumn.addCol(), col);
                } else {
                    columns.push(aColumn);
                    colgroup.appendChild(aColumn.addCol());
                }
                table.parentElement.parentElement.appendChild(aColumn.columnRule);
                if (needRedraw) {
                    redraw();
                }
            }
        }
        Object.defineProperty(this, 'insertColumn', {
            get: function () {
                return insertColumn;
            }
        });
        function addColumn(aColumn, needRedraw) {
            if (arguments.length < 2)
                needRedraw = true;
            insertColumn(columns.length, aColumn, needRedraw);
        }
        Object.defineProperty(this, 'addColumn', {
            get: function () {
                return addColumn;
            }
        });

        function removeColumn(index, needRedraw) {
            if (arguments.length < 2)
                needRedraw = true;
            if (index >= 0 && index < columns.length) {
                var removed = columns.splice(index, 1)[0];
                removed.elements.forEach(function (col) {
                    col.parentElement.removeChild(col);
                });
                removed.elements.splice(0, removed.elements.length);
                if (removed.columnRule.parentElement)
                    removed.columnRule.parentElement.removeChild(removed.columnRule);
                if (needRedraw) {
                    redraw();
                }
                return removed;
            } else {
                return null;
            }
        }
        Object.defineProperty(this, 'removeColumn', {
            get: function () {
                return removeColumn;
            }
        });

        Object.defineProperty(this, 'columnsCount', {
            get: function () {
                return columns.length;
            }
        });

        function getColumn(index) {
            return index >= 0 && index < columns.length ? columns[index] : null;
        }
        Object.defineProperty(this, 'getColumn', {
            get: function () {
                return getColumn;
            }
        });

        function getColumnIndex(column) {
            return columns.indexOf(column);
        }

        Object.defineProperty(this, 'rowsCount', {
            get: function () {
                return tbody.rows.length;
            }
        });

        function getViewCell(aRow, aCol) {
            var viewRows = tbody.rows;
            if (aRow - startRow >= 0 && aRow - startRow < viewRows.length) {
                var viewRow = viewRows[aRow];
                var cells = viewRow.cells;
                if (aCol >= 0 && aCol < cells.length) {
                    return cells[aCol];
                }
            }
            return null;
        }
        Object.defineProperty(this, 'getViewCell', {
            get: function () {
                return getViewCell;
            }
        });

        function focusCell(aRow, aCol) {
            var cell = getViewCell(aRow, aCol);
            if (cell) {
                keyboardSelectedColumn = aCol;
                keyboardSelectedRow = aRow;
                cell.focus();
            }
        }
        Object.defineProperty(this, 'focusCell', {
            get: function () {
                return focusCell;
            }
        });

        Object.defineProperty(this, 'keyboardSelectedElement', {
            get: function () {
                return keyboardSelectedElement;
            }
        });

        function redraw() {
            redrawHeaders();
            redrawBody();
            redrawFooters();
        }
        Object.defineProperty(this, 'redraw', {
            get: function () {
                return redraw;
            }
        });

        function recreateHead() {
            if (thead && thead.parentElement)
                table.removeChild(thead);
            if (colgroup && colgroup.parentElement)
                table.removeChild(colgroup);
            thead = document.createElement('thead');
            table.insertBefore(thead, tbody);
            table.insertBefore(colgroup, thead);
        }

        function redrawHeaders() {
            recreateHead();
            drawHeaders();
        }
        Object.defineProperty(this, 'redrawHeaders', {
            get: function () {
                return redrawHeaders;
            }
        });

        function drawHeaders() {
            if (columns.length > 0) {
                var r = 0;
                var nextLayer = headerNodes;
                while (nextLayer.length > 0) {
                    nextLayer = drawHeaderRow(nextLayer);
                    r++;
                }
                while (r < headerMaxDepth) {
                    drawHeaderRow([]);
                    r++;
                }
            }
        }

        function drawHeaderRow(layer) {
            var children = [];
            var tr = document.createElement('tr');
            tr.className = 'p-grid-header-row ' + dynamicHeaderRowsClassName;
            layer.forEach(function (node) {
                tr.appendChild(node.view.element);
                node.view.element.className = 'p-grid-header-cell ' + dynamicHeaderCellsClassName; // reassign classes
                node.view.element.classList.add(node.column.styleName);
                Array.prototype.push.apply(children, node.children);
            });
            thead.appendChild(tr);
            return children;
        }

        function recreateBody() {
            if (tbody && tbody.parentElement)
                table.removeChild(tbody);
            tbody = document.createElement('tbody');
            table.insertBefore(tbody, tfoot);
        }

        function redrawBody() {
            recreateBody();
            drawBody();
        }

        Object.defineProperty(this, 'redrawBody', {
            get: function () {
                return redrawBody;
            }
        });

        function drawBody() {
            if (columns.length > 0) {
                drawBodyPortion(startRow, endRow);
            }
        }

        function drawBodyPortion(start, end) {
            if (end - start > 0) {
                if (!bodyFiller.parentElement)
                    table.parentElement.appendChild(bodyFiller);
                for (var i = start; i < end; i++) {
                    var dataRow = data[i];
                    var viewRow = document.createElement('tr');
                    viewRow.className = 'p-grid-row ' + dynamicRowsClassName;
                    if ((i + 1) % 2 === 0) {
                        viewRow.classList.add(dynamicEvenRowsClassName);
                    } else {
                        viewRow.classList.add(dynamicOddRowsClassName);
                    }
                    if (grid.isSelected(dataRow))
                        viewRow.classList.add('p-grid-selected-row');
                    viewRow[JS_ROW_NAME] = dataRow;
                    if (i < startRow) {
                        // insertFirst ...
                        if (tbody.firstElementChild)
                            tbody.insertBefore(viewRow, tbody.firstElementChild);
                        else
                            tbody.appendChild(viewRow);
                        // ... insertFirst
                    } else {
                        tbody.appendChild(viewRow);
                    }
                    for (var c = 0; c < columns.length; c++) {
                        var column = columns[c];
                        var viewCell = document.createElement('td');
                        // TODO: Check alignment of the cell
                        // TODO: Check image decoration of the cell and decoration styles
                        viewCell.className = 'p-grid-cell ' + dynamicCellsClassName;
                        viewRow.appendChild(viewCell);
                        column.render(i, dataRow, viewCell);
                    }
                }
                var rowSpace = endRow - startRow;
                var renderedRowPortion = end - start;
                var fillerHeight = rowsHeight * (data ? rowSpace - renderedRowPortion : 0);
                bodyFiller.style.height = fillerHeight + 'px';
                bodyFiller.style.display = fillerHeight === 0 ? 'none' : '';
            }
        }

        function setRange(start, end, needRedraw) {
            if (arguments.length < 3)
                needRedraw = true;
            startRow = start;
            endRow = end;
            if (needRedraw) {
                redrawBody();
            }
        }
        Object.defineProperty(this, 'setRange', {
            get: function () {
                return setRange;
            }
        });

        function shiftRange(aStartRow, aEndRow) {
            if (aStartRow >= endRow || aEndRow < startRow) {
                setRange(aStartRow, aEndRow);
            } else {
                if (aStartRow < startRow) {
                    drawBodyPortion(aStartRow, startRow);
                } else if (aStartRow > startRow) {
                    for (var i1 = startRow; i1 < aStartRow; i1++) {
                        tbody.removeChild(tbody.rows[0]);
                    }
                }
                startRow = aStartRow;
                if (aEndRow < endRow) {
                    for (var i2 = endRow - 1; i2 >= aEndRow; i2--) {
                        tbody.removeChild(tbody.rows[tbody.rows.length - 1]);
                    }
                } else if (aEndRow > endRow) {
                    drawBodyPortion(endRow, aEndRow);
                }
                endRow = aEndRow;
            }
        }
        Object.defineProperty(this, 'shiftRange', {
            get: function () {
                return shiftRange;
            }
        });

        function recreateFoot() {
            if (tfoot && tfoot.parentElement)
                table.removeChild(tfoot);
            tfoot = document.createElement('tfoot');
            table.appendChild(tfoot);
        }

        function redrawFooters() {
            recreateFoot();
            drawFooters();
        }
        Object.defineProperty(this, 'redrawFooters', {
            get: function () {
                return redrawFooters;
            }
        });

        function drawFooters() {
            if (columns.length > 0) {
            }
        }

        function redrawRow(index) {
            if (index - startRow >= 0 && index - startRow < tbody.rows.length) {
                var viewRow = tbody.rows[index - startRow];
                var dataRow = data[index];
                for (var c = 0; c < columns.length; c++) {
                    var column = columns[c];
                    var viewCell = viewRow.cells[c];
                    column.render(index, dataRow, viewCell);
                }
            } // if the row is not rendered then we have nothing to do
        }

        function redrawColumn(aIndex) {
            if (aIndex >= 0 && aIndex < columns.length) {
                var column = columns[aIndex];
                for (var i = startRow; i < endRow; i++) {
                    var dataRow = data[i];
                    var viewRow = tbody.rows[i - startRow];
                    var viewCell = viewRow.cells[aIndex];
                    while (viewCell.firstElementChild) {
                        viewCell.removeChild(viewCell.firstElementChild);
                    }
                    column.render(i, dataRow, viewCell);
                }
            }
        }

        function setHeaderNodes(aHeader, maxDepth, needRedraw) {
            if (arguments.length < 2)
                needRedraw = false;
            headerNodes = aHeader;
            headerMaxDepth = maxDepth;
            if (needRedraw) {
                redrawHeaders();
            }
        }
        Object.defineProperty(this, 'setHeaderNodes', {
            get: function () {
                return setHeaderNodes;
            }
        });

        function clearColumnsAndHeader(needRedraw) {
            if (arguments.length < 1)
                needRedraw = true;
            columns.forEach(function (removed) {
                removed.elements.forEach(function (col) {
                    col.parentElement.removeChild(col);
                });
                removed.elements.splice(0, removed.elements.length);
                if (removed.columnRule.parentElement)
                    removed.columnRule.parentElement.removeChild(removed.columnRule);
            });
            columns = [];
            headerNodes = [];
            if (needRedraw) {
                redraw();
            }
        }
        Object.defineProperty(this, 'clearColumnsAndHeader', {
            get: function () {
                return clearColumnsAndHeader;
            }
        });
    }
    return Section;
});