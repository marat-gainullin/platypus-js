define([
    '../column',
    './node-view',
    '../../fields/text-field'
], function (
        Column,
        HeaderView,
        TextField
        ) {
    function HeaderNode(column, header) {
        var self = this;
        var name = null;
        if (!column) {
            column = new Column();
        }
        if (!header) {
            header = new HeaderView("", this);
        }
        var parent = null;
        var children = [];

        var leavesCount = 0;
        var depthRemainder = 0;

        column.editor = new TextField();

        function lightCopy() {
            var copied = new HeaderNode();
            copied.column = column;
            copied.header = header;
            return copied;
        }

        Object.defineProperty(this, 'lightCopy', {
            configurable: true,
            get: function () {
                return lightCopy;
            }
        });
        Object.defineProperty(this, 'column', {
            configurable: true,
            get: function () {
                return column;
            },
            set: function (aValue) {
                column = aValue;
            }
        });

        Object.defineProperty(this, 'parent', {
            get: function () {
                return parent;
            },
            set: function (aValue) {
                parent = aValue;
            }
        });

        Object.defineProperty(this, 'children', {
            get: function () {
                return children;
            }
        });

        Object.defineProperty(this, 'header', {
            configurable: true,
            get: function () {
                return header;
            },
            set: function (aValue) {
                header = aValue;
            }
        });

        function removeColumnNode(aNode) {
            if (children) {
                var idx = children.indexOf(aNode);
                if (idx !== -1) {
                    children.splice(idx, 1);
                    return true;
                } else {
                    return false;
                }
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
            if (!children) {
                children = [];
            }
            if (children.indexOf(aNode) === -1) {
                children.push(aNode);
                aNode.parent = self;
            }
        }
        Object.defineProperty(this, 'addColumnNode', {
            get: function () {
                return addColumnNode;
            }
        });

        function insertColumnNode(atIndex, aNode) {
            if (!children) {
                children = [];
            }
            if (children.indexOf(aNode) === -1 && atIndex >= 0 && atIndex <= children.size()) {
                children.splice(atIndex, 0, aNode);
                aNode.parent = this;
            }
        }
        Object.defineProperty(this, 'insertColumnNode', {
            get: function () {
                return insertColumnNode;
            }
        });

        Object.defineProperty(this, 'childrenNodes', {
            get: function () {
                return children.slice(0, children.length);
            }
        });

        Object.defineProperty(this, 'depthRemainder', {
            get: function () {
                return depthRemainder;
            },
            set: function (aValue) {
                depthRemainder = aValue;
                header.element.rowspan = (aValue + 1) + "";
            }
        });

        Object.defineProperty(this, 'leavesCount', {
            get: function () {
                return leavesCount;
            },
            set: function (aValue) {
                leavesCount = aValue;
                header.element.colspan = aValue + "";
            }
        });

        Object.defineProperty(this, 'leaf', {
            get: function () {
                return children.length === 0;
            }
        });

        Object.defineProperty(this, 'name', {
            get: function () {
                return name;
            },
            set: function (aValue) {
                name = aValue;
            }
        });

        Object.defineProperty(this, 'background', {
            get: function () {
                return header.background;
            },
            set: function (aValue) {
                header.background = aValue;
            }
        });

        Object.defineProperty(this, 'foreground', {
            get: function () {
                return header.foreground;
            },
            set: function (aValue) {
                header.foreground = aValue;
            }
        });

        Object.defineProperty(this, 'font', {
            get: function () {
                return header.font;
            },
            set: function (aValue) {
                header.font = aValue;
            }
        });

        Object.defineProperty(this, 'minWidth', {
            get: function () {
                return column.minWidth;
            },
            set: function (aValue) {
                column.minWidth = aValue;
            }
        });

        Object.defineProperty(this, 'maxWidth', {
            get: function () {
                return column.maxWidth;
            },
            set: function (aValue) {
                column.maxWidth = aValue;
            }
        });

        Object.defineProperty(this, 'preferredWidth', {
            get: function () {
                return column.designedWidth;
            },
            set: function (aValue) {
                column.designedWidth = aValue;
            }
        });

        Object.defineProperty(this, 'field', {
            get: function () {
                return column.field;
            },
            set: function (aValue) {
                column.field = aValue;
            }
        });

        Object.defineProperty(this, 'title', {
            get: function () {
                return header.title;
            },
            set: function (aValue) {
                header.title = aValue;
            }
        });

        Object.defineProperty(this, 'resizable', {
            get: function () {
                return header.resizable;
            },
            set: function (aValue) {
                header.resizable = aValue;
            }
        });

        Object.defineProperty(this, 'moveable', {
            get: function () {
                return header.moveable;
            },
            set: function (aValue) {
                header.moveable = aValue;
            }
        });

        Object.defineProperty(this, 'visible', {
            get: function () {
                return column.visible;
            },
            set: function (aValue) {
                column.visible = aValue;
            }
        });

        Object.defineProperty(this, 'width', {
            get: function () {
                return column.width;
            },
            set: function (aValue) {
                column.width = aValue;
            }
        });

        Object.defineProperty(this, 'readonly', {
            get: function () {
                return column.readonly;
            },
            set: function (aValue) {
                column.readonly = aValue;
            }
        });

        Object.defineProperty(this, 'sortable', {
            get: function () {
                return column.sortable;
            },
            set: function (aValue) {
                column.sortable = aValue;
            }
        });

        Object.defineProperty(this, 'sortField', {
            get: function () {
                return column.sortField;
            },
            set: function (aValue) {
                column.sortField = aValue;
            }
        });

        Object.defineProperty(this, 'onRender', {
            get: function () {
                return column.onRender;
            },
            set: function (aValue) {
                column.onRender = aValue;
            }
        });

        Object.defineProperty(this, 'onSelect', {
            get: function () {
                return column.onSelect;
            },
            set: function (aValue) {
                column.onSelect = aValue;
            }
        });

        function sort() {
            column.sort();
        }
        Object.defineProperty(this, 'sort', {
            get: function () {
                return sort;
            }
        });

        function sortDesc() {
            column.sortDesc();
        }
        Object.defineProperty(this, 'sortDesc', {
            get: function () {
                return sortDesc;
            }
        });

        function unsort() {
            column.unsort();
        }
        Object.defineProperty(this, 'unsort', {
            get: function () {
                return unsort;
            }
        });

        Object.defineProperty(this, 'editor', {
            configurable: true,
            get: function () {
                return column ? column.editor : null;
            },
            set: function (aWidget) {
                if (column) {
                    column.editor = aWidget;
                }
            }
        });
    }
});