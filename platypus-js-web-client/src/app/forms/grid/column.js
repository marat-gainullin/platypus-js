/* global Infinity */
define([
    '../../id',
    '../../ui',
    '../../logger',
    '../bound'
], function (
        Id,
        Ui,
        Logger,
        Bound
        ) {
// TODO: Check tree expandable cell decorations like left paddnig according to deepness and plus / minus icon and open / closed folder icons
    function Column() {
        var self = this;
        var cols = []; // dom 'col' elements for header,frozen,body and footer sections of the grid
        var columnRule = document.createElement('style');
        var columnStyleName = 'p-grid-column-' + Id.generate();
        var field = null;
        var sortField = null;
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
        var readonly = false;
        var visible = true;
        var sortable = false;
        var indent = 24;
        var comparator; // PathComparator
        var headers = []; // multiple instances of NodeView
        var onRender;
        var onSelect;
        var grid;

        function regenerateColStyle() {
            columnRule.innerHTML = '.' + columnStyleName + '{' +
                    (visible ? '' : 'display: none;') +
                    (width == null || width == Infinity ? '' : 'width: ' + width + 'px;') +
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
        Object.defineProperty(this, 'grid', {
            get: function () {
                return grid;
            },
            set: function (aValue) {
                grid = aValue;
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
            }
        });

        function sort(fireEvent) {
            if (arguments.length < 1)
                fireEvent = true;
            comparator = new PathComparator(sortField ? sortField : field, true);
            if (fireEvent) {
                grid.sort();
            }
        }

        function sortDesc(fireEvent) {
            if (arguments.length < 1)
                fireEvent = true;
            comparator = new PathComparator(sortField ? sortField : field, false);
            if (fireEvent) {
                grid.sort();
            }
        }

        function unsort(fireEvent) {
            if (arguments.length < 1)
                fireEvent = true;
            comparator = null;
            if (fireEvent) {
                grid.sort();
            }
        }

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
                        grid.updateRightSectionsWidth();
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

        function render(viewIndex, dataRow, viewCell) {
            var path = grid.buildPathTo(dataRow);
            var padding = indent * (path.length - 1);
            viewCell.style.paddingLeft = padding  > 0 ? padding + 'px' : '';
            var value = getValue(dataRow);
            var text;
            if (!value) {
                text = null; // No native rendering for null values
            } else {
                text = value + '';
                if (editor) {
                    editor.value = value;
                    /*
                    if (editor instanceof BooleanDecoratorField || editor instanceof CheckBox) {
                        var checkbox = document.createElement('input');
                        checkbox.type = 'checkbox';
                        checkbox.checked = !!value;
                        if (!checkbox.checked) {
                            text = "";
                        }
                        viewCell.appendChild(checkbox);
                        Ui.on(checkbox, Ui.Events.CHANGE, function (evt) {
                            setValue(dataRow, !!!value);
                        });
                    } else if (editor instanceof RadioButton) {
                        var radiobutton = document.createElement('input');
                        radiobutton.type = 'radio';
                        radiobutton.group = radioGroup;
                        checkbox.checked = !!value;
                        if (!checkbox.checked) {
                            text = "";
                        }
                        viewCell.appendChild(radiobutton);
                        Ui.on(radiobutton, Ui.Events.CHANGE, function (evt) {
                            if (!value) {
                                setValue(dataRow, true);
                            }
                        });
                    } else {
                        text = editor.text;
                        viewCell.innerText = text;
                    }
                    */
                    text = editor.text;
                    viewCell.innerText = text;
                }
            }
            // User's rendering for all values, including null
            if (onRender || grid.onRender) {
                var handler = onRender ? onRender : grid.onRender;
                handler.call(self, dataRow, viewCell, viewIndex, text);
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

        Object.defineProperty(this, 'editor', {
            get: function () {
                return editor;
            },
            set: function (aValue) {
                if (editor !== aValue) {
                    editor = aValue;
                }
            }
        });
    }
    return Column;
});