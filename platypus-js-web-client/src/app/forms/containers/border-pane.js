define([
    '../../id',
    '../../extend',
    '../../ui',
    '../container'
], function (
        Id,
        extend,
        Ui,
        Container) {
    function BorderPane(hgap, vgap) {
        if (arguments.length < 2)
            vgap = 0;
        if (arguments.length < 1)
            hgap = 0;

        Container.call(this);

        var self = this;
        var flexColumn = this.element;

        flexColumn.classList.add('p-borders-column');
        var flexRow = document.createElement('div');
        flexRow.classList.add('p-borders-row');
        this.element.appendChild(flexRow);

        flexColumn.id = 'p-' + Id.generate();
        flexRow.id = 'p-' + Id.generate();

        var gapsStyle = document.createElement('style');
        this.element.appendChild(gapsStyle);
        function formatChildren() {
            gapsStyle.innerHTML =
                    'div#' + flexColumn.id + ' > .p-borders-row {' +
                    'margin-top: ' + vgap + 'px;' +
                    'margin-bottom: ' + vgap + 'px;' +
                    '}' +
                    'div#' + flexRow.id + ' > .p-borders-center {' +
                    'margin-left: ' + hgap + 'px;' +
                    'margin-right: ' + hgap + 'px;' +
                    '}';
        }
        formatChildren();

        var center;
        var left;
        var right;
        var top;
        var bottom;

        Object.defineProperty(this, 'hgap', {
            get: function () {
                return hgap;
            },
            set: function (aValue) {
                if (hgap !== aValue) {
                    hgap = aValue;
                    formatChildren();
                }
            }
        });

        Object.defineProperty(this, 'vgap', {
            get: function () {
                return vgap;
            },
            set: function (aValue) {
                if (vgap !== aValue) {
                    vgap = aValue;
                    formatChildren();
                }
            }
        });

        var superAdd = this.add;
        function add(w, aPlace, aSize) {
            if (arguments.length < 2) {
                var cold = self.centerComponent;
                self.centerComponent = w;
                return cold;
            } else {
                switch (aPlace) {
                    case Ui.HorizontalPosition.LEFT:
                        var lold = self.leftComponent;
                        self.leftComponent = w;
                        if(arguments.length > 2)
                            w.width = aSize;
                        return lold;
                    case Ui.HorizontalPosition.RIGHT:
                        var rold = self.rightComponent;
                        self.rightComponent = w;
                        if(arguments.length > 2)
                            w.width = aSize;
                        return rold;
                    case Ui.VerticalPosition.TOP:
                        var told = self.topComponent;
                        self.topComponent = w;
                        if(arguments.length > 2)
                            w.height = aSize;
                        return told;
                    case Ui.VerticalPosition.BOTTOM:
                        var bold = self.bottomComponent;
                        self.bottomComponent = w;
                        if(arguments.length > 2)
                            w.height = aSize;
                        return bold;
                    default:
                        var cold = self.centerComponent;
                        self.centerComponent = w;
                        return cold;
                }
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        function leftRemoved() {
            left.element.classList.remove('p-borders-left');
            left = null;
            leftWidth = 0;
        }

        function rightRemoved() {
            right.element.classList.remove('p-borders-right');
            right = null;
            rightWidth = 0;
        }

        function topRemoved() {
            top.element.classList.remove('p-borders-top');
            top = null;
            topHeight = 0;
        }
        function bottomRemoved() {
            bottom.element.classList.remove('p-borders-bottom');
            bottom = null;
            bottomHeight = 0;
        }
        function centerRemoved() {
            center.element.classList.remove('p-borders-center');
            center = null;
        }

        function checkRemoved(w) {
            if (left === w) {
                leftRemoved();
            }
            if (right === w) {
                rightRemoved();
            }
            if (top === w) {
                topRemoved();
            }
            if (bottom === w) {
                bottomRemoved();
            }
            if (center === w) {
                centerRemoved();
            }
        }

        var superRemove = this.remove;
        function remove(widgetOrIndex) {
            var removed = superRemove(widgetOrIndex);
            checkRemoved(removed);
            return removed;
        }
        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });

        var superClear = this.clear;
        function clear() {
            superClear();
            if (left)
                leftRemoved();
            if (right)
                rightRemoved();
            if (top)
                topRemoved();
            if (bottom)
                bottomRemoved();
            if (center)
                centerRemoved();
        }
        Object.defineProperty(this, 'clear', {
            get: function () {
                return clear;
            }
        });

        Object.defineProperty(this, 'leftComponent', {
            get: function () {
                return left;
            },
            set: function (w) {
                if (w !== left) {
                    if (left) {
                        superRemove(left);
                        leftRemoved();
                    }
                    if (w) {
                        superAdd(w);
                        flexRow.appendChild(w.element);
                        w.element.classList.add('p-borders-left');
                    }
                    left = w;
                }
            }
        });
        Object.defineProperty(this, 'rightComponent', {
            get: function () {
                return right;
            },
            set: function (w) {
                if (w !== right) {
                    if (right) {
                        superRemove(right);
                        rightRemoved();
                    }
                    if (w) {
                        superAdd(w);
                        flexRow.appendChild(w.element);
                        w.element.classList.add('p-borders-right');
                    }
                    right = w;
                }
            }
        });
        Object.defineProperty(this, 'topComponent', {
            get: function () {
                return top;
            },
            set: function (w) {
                if (w !== top) {
                    if (top) {
                        superRemove(top);
                        topRemoved();
                    }
                    if (w) {
                        superAdd(w);
                        w.element.classList.add('p-borders-top');
                    }
                    top = w;
                }
            }
        });
        Object.defineProperty(this, 'bottomComponent', {
            get: function () {
                return bottom;
            },
            set: function (w) {
                if (w !== bottom) {
                    if (bottom) {
                        superRemove(bottom);
                        bottomRemoved();
                    }
                    if (w) {
                        superAdd(w);
                        w.element.classList.add('p-borders-bottom');
                    }
                    bottom = w;
                }
            }
        });
        Object.defineProperty(this, 'centerComponent', {
            get: function () {
                return center;
            },
            set: function (w) {
                if (w !== center) {
                    if (center) {
                        superRemove(center);
                        centerRemoved();
                    }
                    if (w) {
                        superAdd(w);
                        w.element.classList.add('p-borders-center');
                        flexRow.appendChild(w.element);
                    }
                    center = w;
                }
            }
        });

        function ajustLeft(w, aValue) {
        }
        Object.defineProperty(this, 'ajustLeft', {
            get: function () {
                return ajustLeft;
            }
        });

        function ajustWidth(w, aValue) {
            if (left === w) {
                left.element.style.width = aValue + 'px';
            } else if (right === w) {
                right.element.style.width = aValue + 'px';
            }
        }
        Object.defineProperty(this, 'ajustWidth', {
            get: function () {
                return ajustWidth;
            }
        });

        function ajustTop(w, aValue) {
        }
        Object.defineProperty(this, 'ajustTop', {
            get: function () {
                return ajustTop;
            }
        });

        function ajustHeight(w, aValue) {
            if (top === w) {
                top.element.style.height = aValue + 'px';
            } else if (bottom === w) {
                bottom.element.style.height = aValue + 'px';
            }
        }
        Object.defineProperty(this, 'ajustHeight', {
            get: function () {
                return ajustHeight;
            }
        });
    }
    extend(BorderPane, Container);
    return BorderPane;
});