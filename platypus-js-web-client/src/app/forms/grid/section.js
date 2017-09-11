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

    function Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName) {

        var self = this;

        var table = document.createElement('table');
        var colgroup = document.createElement('colgroup');
        var headerNodes = [];
        var keyboardSelectedElement = null;
        var draggableRows = false;
        var keyboardSelectedRow = -1;
        var keyboardSelectedColumn = -1;
        var rowsPerPage = 30; // Only for PageDown or PageUp keys handling
        var data = null; // Already sorted
        /**
         * Already sorted data array indices;
         */
        var startRow = 0; // Inclusive
        var endRow = 0; // Exclusive
        var columns = [];

        var thead = document.createElement('thead');
        table.appendChild(thead);
        thead.appendChild(colgroup);
        var tbody = document.createElement('tbody');
        table.appendChild(tbody);
        var tfoot = document.createElement('tfoot');
        table.appendChild(tfoot);

        table.style.borderCollapse = 'collapse';
        Ui.on(table, Ui.Eents.KEYDOWN, function (event) {
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

        function addColumn(index, aColumn, needRedraw) {
            if (arguments.length < 3)
                needRedraw = false;
            if (index >= 0 && index <= columns.length) { // It is all about insertBefore
                if (index < columns.length) { // It is all about insertBefore
                    columns.splice(index, 0, aColumn);
                    var col = colgroup.getChild(index);
                    colgroup.insertBefore(aColumn.element, col);
                } else {
                    columns.push(aColumn);
                    colgroup.appendChild(aColumn.element);
                }
                if (needRedraw) {
                    redraw();
                }
            }
        }

        function removeColumn(index, needRedraw) {
            if (arguments.length < 2)
                needRedraw = false;
            if (index >= 0 && index < columns.length) {
                var removed = columns.splice(index, 1)[0];
                removed.element.parentElement.removeChild(removed.element);
                removed.columnRule.parentElement.remove(removed.columnRule);
                if (needRedraw) {
                    redraw();
                }
                return removed;
            } else {
                return null;
            }
        }

        Object.defineProperty(this, 'columnsCount', {
            get: function () {
                return columns.length;
            }
        });

        function getColumn(index) {
            return index >= 0 && index < columns.length ? columns[index] : null;
        }

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

        function focusCell(aRow, aCol) {
            var cell = getViewCell(aRow, aCol);
            if (cell) {
                keyboardSelectedColumn = aCol;
                keyboardSelectedRow = aRow;
                cell.focus();
            }
        }

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

        function redrawBody() {
            tbody.parentElement.removeChild(tbody);
            tbody = document.createElement('tbody');
            table.appendChild(tbody);
            drawBody();
        }

        function drawBody() {
            if (columns.length > 0) {
                drawBodyPortion(startRow, endRow);
            }
        }

        function drawBodyPortion(start, end) {
            for (var i = start; i < end; i++) {
                var dataRow = data[i];
                var viewRow = document.createElement('tr');
                if ((i + 1) % 2 === 0) {
                    viewRow.classList.add(dynamicEvenRowsClassName);
                } else {
                    viewRow.classList.add(dynamicOddRowsClassName);
                }
                viewRow.classList.add("selected-row");
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
                    viewCell.classList.add(dynamicCellClassName);
                    viewRow.appendChild(viewCell);
                    column.render(i, dataRow, viewCell);
                }
            }
        }

        function setRange(aStartRow, aEndRow) {
            startRow = aStartRow;
            endRow = aEndRow;
            redrawBody();
        }

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

        function redrawHeaders() {
            table.removeChild(thead);
            thead = document.createElement('thead');
            table.appendChild(thead);
            drawHeaders();
        }

        function drawHeaders() {
            if (columns.length > 0) {
                var nextLayer = headerNodes;
                while (nextLayer.length > 0) {
                    nextLayer = drawHeaderRow(nextLayer);
                }
            }
        }

        function drawHeaderRow(layer) {
            var children = [];
            var tr = document.createElement('tr');
            layer.forEach(function (hn) {
                tr.appendChild(hn.header.element);
                Array.push.apply(children, hn.children);
            });
            thead.appendChild(tr);
            return children;
        }

        function redrawFooters() {
            table.removeChild(tfoot);
            tfoot = document.createTFootElement('tfoot');
            table.appendChild(tfoot);
            drawFooters();
        }

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

        function setHeaderNodes(aHeader, needReadraw) {
            if (arguments.length < 2)
                needReadraw = false;
            headerNodes = aHeader;
            if (needReadraw) {
                redrawHeaders();
            }
        }

        function clearColumnsAndHeader() {
            clearColumnsAndHeader(true);
        }

        function clearColumnsAndHeader(needRedraw) {
            if (arguments.length < 1)
                needRedraw = false;
            for (var i = columns.length - 1; i >= 0; i--) {
                var removed = columns.splice(i, 1)[0];
                removed.element.parentElement.removeChild(removed.element);
                removed.columnRule.parentElement.removeChild(removed.columnRule);
            }
            headerNodes = [];
            if (needRedraw) {
                redraw();
            }
        }
    }
    return Section;
});