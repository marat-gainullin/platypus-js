define([
    '../extend',
    './container'], function (
        extend,
        Container) {
    /**
     * A container with Grid Layout.
     * @param rows the number of grid rows.
     * @param columns the number of grid columns.
     * @param hgap the horizontal gap (optional).
     * @param vgap the vertical gap (optional).
     * @constructor GridPane GridPane
     */
    function GridPane(rows, columns, hgap, vgap) {
        Container.call(this);
        var self = this;

        if (arguments.length < 1)
            rows = 1;
        if (arguments.length < 2)
            columns = 1;
        if (arguments.length < 3)
            hgap = 0;
        if (arguments.length < 3)
            vgap = 0;

        var grid = [];
        for (var r = 0; r < rows; r++) {
            var row = [];
            grid.push(row);
            for (var c = 0; c < columns; c++) {
                row.push(null);
            }
        }
        Object.defineProperty(this, "rows", {
            get: function () {
                return rows;
            }
        });
        Object.defineProperty(this, "columns", {
            get: function () {
                return columns;
            }
        });

        Object.defineProperty(this, "hgap", {
            get: function () {
                return hgap;
            },
            set: function (aValue) {
                if (hgap !== aValue) {
                    hgap = aValue;
                    formatCells();
                }
            }
        });
        Object.defineProperty(this, "vgap", {
            get: function () {
                return vgap;
            },
            set: function (aValue) {
                if (vgap !== aValue) {
                    vgap = aValue;
                    formatCells();
                }
            }
        });

        function formatCells() {
            for (var i = 0; i < grid.length; i++) {
                for (var j = 0; j < grid[i].length; j++) {
                    formatCell(i, j);
                }
            }
        }

        function formatCell(row, column) {
            var w = getWidget(row, column);
            if (w) {
                var ws = w.element.style;

                ws.position = 'absolute';
                ws.left = (100 / columns) * column + '%';
                ws.top = (100 / rows) * row + '%';
                ws.width = (100 / columns) + '%';
                ws.height = (100 / rows) + '%';
                ws.margin = 0 + 'px';
                ws.paddingLeft = Math.floor(hgap / 2) + 'px';
                ws.paddingRight = Math.ceil(hgap / 2) + 'px';
                ws.paddingTop = Math.floor(vgap / 2) + 'px';
                ws.paddingBottom = Math.ceil(vgap / 2) + 'px';
            }
        }

        var superAdd = this.add;
        function add(w, row, col) {
            if (w) {
                if (w.parent === self)
                    throw 'A widget already added to this container';
                if (arguments.length < 3)
                    throw "'row' and 'col' are required parameters";
                setWidget(row, col, w);
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        function addToFreeCell(aWidget) {
            for (var row = 0; row < grid.length; row++) {
                for (var col = 0; col < grid[row].length; col++) {
                    var w = getWidget(row, col);
                    if (!w) {
                        setWidget(row, col, aWidget);
                        return true;
                    }
                }
            }
            return false;
        }
        Object.defineProperty(this, 'addToFreeCell', {
            get: function () {
                return addToFreeCell;
            }
        });

        var superRemove = this.remove;
        function setWidget(row, column, w) {
            if (row >= 0 && row < grid.length
                    && column >= 0 && column < grid[row].length) {
                var old = grid[row][column];
                if (old) {
                    superRemove(old);
                }
                grid[row][column] = w;
                formatCell(row, column);
                superAdd(w);
            }
        }

        function getWidget(row, column) {
            if (row >= 0 && row < grid.length
                    && column >= 0 && column < grid[row].length) {
                return grid[row][column];
            } else {
                return null;
            }
        }

        function remove(widgetOrIndex) {
            var removed = superRemove(widgetOrIndex);
            checkCells(removed);
            return removed;
        }
        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });

        var superChild = this.child;
        function child(row, col) {
            if (arguments < 2) {
                throw "'row' and 'col' are required parameters";
            }
            return getWidget(row, col);
        }
        Object.defineProperty(this, 'child', {
            get: function () {
                return child;
            }
        });

        function checkCells(w) {
            if (w) {
                for (var i = 0; i < grid.length; i++) {
                    for (var j = 0; j < grid[i].length; j++) {
                        if (grid[i][j] === w) {
                            grid[i][j] = null;
                        }
                    }
                }
            }
        }

        function getTop(w) {
            if (w.parent !== this)
                throw "A widget should be a child of this container";
            return w.element.offsetTop;
        }
        Object.defineProperty(this, 'getTop', {
            get: function () {
                return getTop;
            }
        });

        function getLeft(w) {
            if (w.parent !== this)
                throw "A widget should be a child of this container";
            return w.element.offsetLeft;
        }
        Object.defineProperty(this, 'getLeft', {
            get: function () {
                return getLeft;
            }
        });
    }
    extend(GridPane, Container);
    return GridPane;
});
