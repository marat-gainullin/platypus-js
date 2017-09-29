/* global Infinity */
define([
    'core/id',
    'ui/utils',
    'ui/bound'
], function (
        Id,
        Ui,
        Bound
        ) {
    function Column(node) {
        var self = this;
        var cols = []; // dom 'col' elements for header,frozen,body and footer sections of the grid
        var columnRule = document.createElement('style');
        var columnStyleName = 'p-grid-column-' + Id.generate();
        var field = null;
        var sortField = null;
        var renderer = null;
        var editor = null;
        /**
         * Minimum column width while resizing by a user.
         */
        var minWidth = 15;
        /**
         * Maximum column width while resizing by a user.
         */
        var maxWidth = Infinity;
        var width = 75;
        var padding = 0;
        var readonly = false;
        var visible = true;
        var sortable = true;
        var comparator; // PathComparator
        var headers = []; // multiple instances of NodeView
        var onRender;
        var onSelect;
        var grid;

        function regenerateColStyle() {
            columnRule.innerHTML = '.' + columnStyleName + '{' +
                    (visible ? '' : 'display: none;') +
                    (width == null || width == Infinity ? '' : 'width: ' + (width + padding) + 'px;') +
                    (minWidth == null || minWidth == Infinity ? '' : 'min-width: ' + minWidth + 'px;') +
                    (maxWidth == null || maxWidth == Infinity ? '' : 'max-width: ' + maxWidth + 'px;') +
                    '}';
        }
        regenerateColStyle();

        Object.defineProperty(this, 'styleName', {
            get: function () {
                return columnStyleName;
            }
        });

        function addCol() {
            var col = document.createElement('col');
            cols.push(col);
            col.className = columnStyleName;
            return col;
        }

        Object.defineProperty(this, 'addCol', {
            get: function () {
                return addCol;
            }
        });
        /**
         * Multiple 'col' elements for the single column, because of grid sections.
         */
        Object.defineProperty(this, 'elements', {
            get: function () {
                return cols;
            }
        });

        /**
         * Multiple 'headers' for the single column, because of splitted column nodes.
         */
        Object.defineProperty(this, 'headers', {
            get: function () {
                return headers;
            }
        });
        /**
         * Typically, we need only leaf column's header.
         * Leaf nodes' columns can have only single header, by nature.
         */
        Object.defineProperty(this, 'header', {
            get: function () {
                return headers.length === 1 ? headers[0] : null;
            }
        });
        Object.defineProperty(this, 'grcore/id', {
            get: function () {
                return grid;
            },
            set: function (aValue) {
                grid = aValue;
            }
        });

        Object.defineProperty(this, 'node', {
            get: function () {
                return node;
            }
        });

        Object.defineProperty(this, 'columnRule', {
            get: function () {
                return columnRule;
            }
        });

        Object.defineProperty(this, 'comparator', {
            get: function () {
                return comparator;
            },
            set: function (aValue) {
                comparator = aValue;
            }
        });

        function sort(fireEvent) {
            if (arguments.length < 1)
                fireEvent = true;
            comparator = new Bound.PathComparator(sortField ? sortField : field, true);
            if (fireEvent) {
                grid.addSortedColumn(self);
            }
        }
        Object.defineProperty(this, 'sort', {
            get: function () {
                return sort;
            }
        });

        function sortDesc(fireEvent) {
            if (arguments.length < 1)
                fireEvent = true;
            comparator = new Bound.PathComparator(sortField ? sortField : field, false);
            if (fireEvent) {
                grid.addSortedColumn(self);
            }
        }
        Object.defineProperty(this, 'sortDesc', {
            get: function () {
                return sortDesc;
            }
        });

        function unsort(fireEvent) {
            if (arguments.length < 1)
                fireEvent = true;
            comparator = null;
            if (fireEvent) {
                grid.removeSortedColumn(self);
            }
        }
        Object.defineProperty(this, 'unsort', {
            get: function () {
                return unsort;
            }
        });

        Object.defineProperty(this, 'field', {
            get: function () {
                return field;
            },
            set: function (aValue) {
                if (field !== aValue) {
                    field = aValue;
                }
            }
        });

        Object.defineProperty(this, 'sortField', {
            get: function () {
                return sortField;
            },
            set: function (aValue) {
                if (sortField !== aValue) {
                    sortField = aValue;
                }
            }
        });

        Object.defineProperty(this, 'width', {
            get: function () {
                return width;
            },
            set: function (aValue) {
                if (width !== aValue) {
                    width = aValue;
                    regenerateColStyle();
                    if (grid) {
                        grid.updateSectionsWidth();
                    }
                }
            }
        });

        Object.defineProperty(this, 'padding', {
            get: function () {
                return padding;
            },
            set: function (aValue) {
                if (aValue != null && padding !== aValue) {
                    padding = aValue;
                    regenerateColStyle();
                    if (grid) {
                        grid.updateSectionsWidth();
                    }
                }
            }
        });

        Object.defineProperty(this, 'minWidth', {
            configurable: true,
            get: function () {
                return minWidth;
            },
            set: function (aValue) {
                if (minWidth !== aValue) {
                    minWidth = aValue;
                    regenerateColStyle();
                }
            }
        });

        Object.defineProperty(this, 'maxWidth', {
            configurable: true,
            get: function () {
                return maxWidth;
            },
            set: function (aValue) {
                if (maxWidth !== aValue) {
                    maxWidth = aValue;
                    regenerateColStyle();
                }
            }
        });

        function getValue(aItem) {
            if (aItem && field) {
                return Bound.getPathData(aItem, field);
            } else {
                return null;
            }
        }
        Object.defineProperty(this, 'getValue', {
            configurable: true,
            get: function () {
                return getValue;
            }
        });

        function setValue(anElement, value) {
            if (anElement && field && !readonly) {
                Bound.setPathData(anElement, field, value);
            }
        }
        Object.defineProperty(this, 'setValue', {
            configurable: true,
            get: function () {
                return setValue;
            }
        });

        function render(viewRowIndex, viewColumnIndex, dataRow, viewCell) {
            var checkbox = null;
            function handleSelection(event) {
                if (checkbox && !readonly) {
                    setValue(dataRow, !getValue(dataRow));
                }
                if (!event.ctrlKey && !event.metaKey) {
                    self.grid.unselectAll(false);
                }
                self.grid.select(dataRow, false);
                self.grid.focusCell(viewRowIndex, viewColumnIndex, true);
            }
            if (grid.treeIndicatorColumn === self) {
                var padding = grid.indent * grid.depthOf(dataRow);
                viewCell.style.paddingLeft = padding > 0 ? padding + 'px' : '';
                if (!grid.isLeaf(dataRow)) {
                    viewCell.classList.add(grid.expanded(dataRow) ? 'p-grid-cell-expanded' : 'p-grid-cell-collapsed');
                }
                function onClick(event) {
                    if (event.button === 0) {
                        var rect = viewCell.getBoundingClientRect();
                        if (event.clientX > rect.left + padding - grid.indent &&
                                event.clientX <= rect.left + padding) {
                            event.stopPropagation();
                            if (checkbox && !readonly) {
                                setValue(dataRow, !getValue(dataRow));
                            }
                            grid.focusCell(viewRowIndex, viewColumnIndex);
                            grid.toggle(dataRow);
                        } else {
                            handleSelection(event);
                        }
                    }
                }
                Ui.on(viewCell, Ui.Events.CLICK, onClick);
                if(checkbox){
                    Ui.on(checkbox, Ui.Events.CLICK, onClick);
                }
                Ui.on(viewCell, Ui.Events.DBLCLICK, function (event) {
                    if (event.button === 0) {
                        handleSelection(event);
                        self.grid.startEditing();
                    }
                });
            } else {
                Ui.on(viewCell, Ui.Events.CLICK, handleSelection);
                if(checkbox){
                    Ui.on(checkbox, Ui.Events.CLICK, handleSelection);
                }
            }
            var value = getValue(dataRow);
            if (value == null) { // null == undefined, null !== undefined
                viewCell.innerTHML = ''; // No native rendering for null values
            } else if (typeof (value) === 'boolean') {
                checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.checked = !!value;
                viewCell.appendChild(checkbox);
                viewCell.classList.add('p-grid-cell-check-box');
            } else {
                var html;
                if (renderer) {
                    renderer.value = value;
                    html = renderer.text;
                } else if (value instanceof Date) {
                    html = value.toJSON();
                } else {
                    html = value + '';
                }
                viewCell.innerHTML = html;
            }
            // User's rendering for all values, including null
            if (onRender || grid.onRender) {
                var handler = onRender ? onRender : grid.onRender;
                handler.call(self, dataRow, viewCell, viewRowIndex, html);
            }
        }

        Object.defineProperty(this, 'render', {
            configurable: true,
            get: function () {
                return render;
            }
        });
        Object.defineProperty(this, 'visible', {
            get: function () {
                return visible;
            },
            set: function (aValue) {
                if (visible !== aValue) {
                    visible = aValue;
                    regenerateColStyle();
                    if (grid) {
                        grid.applyColumnsNodes();
                    }
                }
            }
        });

        Object.defineProperty(this, 'readonly', {
            get: function () {
                return readonly;
            },
            set: function (aValue) {
                readonly = aValue;
            }
        });

        Object.defineProperty(this, 'sortable', {
            get: function () {
                return sortable;
            },
            set: function (aValue) {
                sortable = aValue;
            }
        });

        Object.defineProperty(this, 'onRender', {
            get: function () {
                return onRender;
            },
            set: function (aValue) {
                if (onRender !== aValue) {
                    onRender = aValue;
                }
            }
        });

        Object.defineProperty(this, 'onSelect', {
            get: function () {
                return editor ? editor.onSelect : null;
            },
            set: function (aValue) {
                if (onSelect !== aValue && editor) {
                    editor.onSelect = aValue;
                }
            }
        });

        Object.defineProperty(this, 'renderer', {
            get: function () {
                return renderer;
            },
            set: function (aValue) {
                if (renderer !== aValue) {
                    renderer = aValue;
                }
            }
        });
        Object.defineProperty(this, 'editor', {
            get: function () {
                return editor;
            },
            set: function (aValue) {
                if (editor !== aValue) {
                    if (editor && editor.element) {
                        editor.element.classList.remove('p-grid-cell-editor');
                    }
                    editor = aValue;
                    if (editor && editor.element) {
                        editor.element.classList.add('p-grid-cell-editor');
                    }
                }
            }
        });
    }
    return Column;
});