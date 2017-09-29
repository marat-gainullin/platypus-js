define([
    'ui/utils'
], function (
        Ui
        ) {

    var HEADER_VIEW = 'header-view';
    var columnDrag = null;

    function NodeView(text, viewColumnNode) {
        var self = this;
        var th = document.createElement('th');
        var thResizer = document.createElement('div');
        thResizer.className = 'p-grid-column-resizer';
        var thMover = document.createElement('div');
        thMover.className = 'p-grid-column-mover';
        var thTitle = document.createElement('div');
        thTitle.className = 'p-grid-column-title';
        var background = null;
        var foreground = null;
        var font = null;
        var moveable = true;
        var resizable = true;
        thMover.draggable = moveable;

        th[HEADER_VIEW] = this;
        thTitle.innerText = text;
        th.appendChild(thTitle);
        th.appendChild(thResizer);
        th.appendChild(thMover);
        var moveHintLeft = document.createElement('div');
        moveHintLeft.className = 'p-grid-column-move-hint-left';
        var moveHintRight = document.createElement('div');
        moveHintRight.className = 'p-grid-column-move-hint-right';

        Ui.on(th, Ui.Events.CLICK, function (event) {
            function checkOthers() {
                if (!event.ctrlKey && !event.metaKey) {
                    column.grid.unsort(false);
                }
            }
            if (event.button === 0) {
                var column = viewColumnNode.column;
                if (viewColumnNode.leaf && column.sortable) {
                    if (!column.comparator) {
                        checkOthers();
                        column.sort();
                    } else if (column.comparator.ascending) {
                        checkOthers();
                        column.sortDesc();
                    } else {
                        checkOthers();
                        column.unsort();
                    }
                }
            }
        });

        (function () {
            Ui.on(thResizer, Ui.Events.CLICK, function (event) {
                if (resizable && event.button === 0) {
                    event.stopPropagation();
                }
            });
            var mouseDownAtX = null;
            var mouseDownWidth = null;
            var onMouseUp = null;
            var onMouseMove = null;
            var columnToResize = null;
            Ui.on(thResizer, Ui.Events.MOUSEDOWN, function (event) {
                if (resizable && event.button === 0) {
                    event.preventDefault();
                    event.stopPropagation();
                    columnDrag = {
                        resize: true
                    };
                    columnToResize = findRightMostLeafColumn();
                    mouseDownAtX = 'pageX' in event ? event.pageX : event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
                    mouseDownWidth = viewColumnNode.width;
                    if (!onMouseUp) {
                        onMouseUp = Ui.on(document, Ui.Events.MOUSEUP, function (event) {
                            event.stopPropagation();
                            columnDrag = null;
                            columnToResize = null;
                            if (onMouseUp) {
                                onMouseUp.removeHandler();
                                onMouseUp = null;
                            }
                            if (onMouseMove) {
                                onMouseMove.removeHandler();
                                onMouseMove = null;
                            }
                        });
                    }
                    if (!onMouseMove) {
                        onMouseMove = Ui.on(document, Ui.Events.MOUSEMOVE, function (event) {
                            event.preventDefault();
                            event.stopPropagation();
                            var newPageX = 'pageX' in event ? event.pageX : event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
                            var dx = newPageX - mouseDownAtX;
                            var newWidth = mouseDownWidth + dx;
                            if (columnToResize.minWidth <= newWidth && newWidth <= columnToResize.maxWidth) {
                                columnToResize.width = newWidth;
                            }
                        });
                    }
                }
            });
        }());

        Ui.on(thMover, Ui.Events.DRAGSTART, function (event) {
            if (columnDrag && columnDrag.resize) {
                event.stopPropagation();
                event.preventDefault();
            } else {
                columnDrag = {
                    move: true,
                    column: viewColumnNode.column
                };
                event.dataTransfer.effectAllowed = 'move';
                event.dataTransfer.setData('text/plain', 'p-grid-column-move');
                var onDragEnd = Ui.on(thMover, Ui.Events.DRAGEND, function (event) {
                    onDragEnd.removeHandler();
                    onDragEnd = null;
                    if (columnDrag &&
                            columnDrag.move) {
                        if (columnDrag.clear) {
                            columnDrag.clear();
                            columnDrag.clear = null;
                        }
                        columnDrag = null;
                    }
                });
            }
        });

        function onDragOver(event) {
            if (columnDrag &&
                    columnDrag.move &&
                    columnDrag.column !== viewColumnNode.column &&
                    columnDrag.column.node.parent === viewColumnNode.column.node.parent) {
                event.preventDefault();
                event.dataTransfer.dropEffect = 'move';
                if (inThRect(event) && columnDrag.enteredTh === th) {
                    var rect = th.getBoundingClientRect();
                    if (event.clientX < rect.left + rect.width / 2) {
                        if (!moveHintLeft.parentElement) {
                            th.appendChild(moveHintLeft);
                        }
                        if (moveHintRight.parentElement) {
                            th.removeChild(moveHintRight);
                        }
                    } else {
                        if (!moveHintRight.parentElement) {
                            th.appendChild(moveHintRight);
                        }
                        if (moveHintLeft.parentElement) {
                            th.removeChild(moveHintLeft);
                        }
                    }
                }
            } else {
                event.dataTransfer.dropEffect = 'none';
            }
        }
        Ui.on(th, Ui.Events.DRAGOVER, onDragOver);
        function inThRect(event) {
            var rect = th.getBoundingClientRect();
            return event.clientX >= rect.left &&
                    event.clientY >= rect.top &&
                    event.clientX < rect.right &&
                    event.clientY < rect.bottom;
        }
        function onDragEnter(event) {
            if (inThRect(event) && columnDrag &&
                    columnDrag.move &&
                    columnDrag.column !== viewColumnNode.column &&
                    columnDrag.column.node.parent === viewColumnNode.column.node.parent) {
                columnDrag.enteredTh = th;
                event.dataTransfer.dropEffect = 'move';
                if (columnDrag.clear) {
                    columnDrag.clear();
                    columnDrag.clear = null;
                }
                if (th.className.indexOf('p-grid-column-move-target') === -1) {
                    th.classList.add('p-grid-column-move-target');

                    var rect = th.getBoundingClientRect();
                    if (event.clientX < rect.left + rect.width / 2) {
                        th.appendChild(moveHintLeft);
                    } else {
                        th.appendChild(moveHintRight);
                    }
                    columnDrag.clear = function () {
                        th.classList.remove('p-grid-column-move-target');
                        if (moveHintLeft.parentElement) {
                            th.removeChild(moveHintLeft);
                        }
                        if (moveHintRight.parentElement) {
                            th.removeChild(moveHintRight);
                        }
                    };
                }
            } else {
                event.dataTransfer.dropEffect = 'none';
            }
        }
        Ui.on(th, Ui.Events.DRAGENTER, onDragEnter);
        function onDragLeave(event) {
            if (!inThRect(event) && columnDrag &&
                    columnDrag.move &&
                    columnDrag.enteredTh === th) {
                if (columnDrag.clear) {
                    columnDrag.clear();
                    columnDrag.clear = null;
                }
            }
        }
        Ui.on(th, Ui.Events.DRAGLEAVE, onDragLeave);
        function onDrop(event) {
            if (inThRect(event) && columnDrag &&
                    columnDrag.move &&
                    columnDrag.enteredTh === th) {
                var droppedNode = columnDrag.column.node;
                var targetNode = viewColumnNode.column.node;
                var grid = viewColumnNode.column.grid;
                if (columnDrag.clear) {
                    columnDrag.clear();
                    columnDrag.clear = null;
                }
                columnDrag = null;
                var rect = th.getBoundingClientRect();
                if (event.clientX < rect.left + rect.width / 2) {
                    grid.insertBeforeColumnNode(droppedNode, targetNode);
                } else {
                    grid.insertAfterColumnNode(droppedNode, targetNode);
                }
            }
        }
        Ui.on(th, Ui.Events.DROP, onDrop);

        Object.defineProperty(this, 'element', {
            get: function () {
                return th;
            }
        });

        Object.defineProperty(this, 'text', {
            get: function () {
                return th.innerText;
            },
            set: function (aValue) {
                thTitle.innerText = aValue;
            }
        });

        Object.defineProperty(this, 'column', {
            get: function () {
                return findRightMostLeafColumn();
            }
        });

        Object.defineProperty(this, 'columnNode', {
            get: function () {
                return viewColumnNode;
            }
        });

        Object.defineProperty(this, 'background', {
            get: function () {
                return background;
            },
            set: function (aValue) {
                background = aValue;
            }
        });

        Object.defineProperty(this, 'foreground', {
            get: function () {
                return foreground;
            },
            set: function (aValue) {
                foreground = aValue;
            }
        });

        Object.defineProperty(this, 'font', {
            get: function () {
                return font;
            },
            set: function (aValue) {
                font = aValue;
            }
        });

        Object.defineProperty(this, 'resizable', {
            get: function () {
                return resizable;
            },
            set: function (aValue) {
                if (resizable !== aValue) {
                    resizable = aValue;
                    if (resizable) {
                        thResizer.style.display = '';
                        thMover.classList.remove('p-grid-column-mover-alone');
                    } else {
                        thResizer.style.display = 'none';
                        thMover.classList.add('p-grid-column-mover-alone');
                    }
                }
            }
        });

        Object.defineProperty(this, 'moveable', {
            get: function () {
                return moveable;
            },
            set: function (aValue) {
                if (moveable !== aValue) {
                    moveable = aValue;
                    thMover.draggable = moveable;
                }
            }
        });

        function findRightMostLeafColumn() {
            var node = viewColumnNode;
            while (!node.leaf) {
                node = node.children[node.children.length - 1];
            }
            return node.column;
        }
    }
    Object.defineProperty(NodeView, 'HEADER_VIEW', {
        get: function () {
            return HEADER_VIEW;
        }
    });
    return NodeView;
});