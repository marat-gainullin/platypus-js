define([
    '../id',
    '../extend',
    '../ui',
    './container'], function (
        Id,
        extend,
        Ui,
        Container) {
    function Split(orientation) {
        if (arguments.length < 1)
            orientation = Ui.Orientation.HORIZONTAL;

        Container.call(this);

        var self = this;

        var dividerSize = 10;

        var firstWidget;
        var secondWidget;

        var oneTouchExpandable;
        var dividerLocation = 0;

        this.element.classList.add('p-split');

        this.element.id = 'p-' + Id.generate();

        var style = document.createElement('style');
        self.element.appendChild(style);

        function formatChildren() {
            if (orientation === Ui.Orientation.HORIZONTAL) {
                style.innerHTML =
                        'div#' + self.element.id + ' > .p-widget:nth-child(2) {' + // firstWidget
                        'position: absolute;' +
                        'left: ' + 0 + 'px;' +
                        'top: ' + 0 + 'px;' +
                        'bottom: ' + 0 + 'px;' +
                        'width: ' + dividerLocation + 'px;' +
                        '}' +
                        'div#' + self.element.id + ' > .p-widget:last-child {' + // secondWidget
                        'position: absolute;' +
                        'right: ' + 0 + 'px;' +
                        'top: ' + 0 + 'px;' +
                        'bottom: ' + 0 + 'px;' +
                        'left: ' + (dividerLocation + dividerSize) + 'px';
                '}';
                divider.style.top = '0px';
                divider.style.bottom = '0px';
                divider.style.width = dividerSize + 'px';
                divider.style.left = dividerLocation + 'px';
                divider.style.height = '';
                divider.style.right = '';
            } else {
                style.innerHTML =
                        'div#' + self.element.id + ' > .p-widget:nth-child(2) {' + // firstWidget
                        'position: absolute;' +
                        'left: ' + 0 + 'px;' +
                        'right: ' + 0 + 'px;' +
                        'top: ' + 0 + 'px;' +
                        'height: ' + dividerLocation + 'px;' +
                        '}' +
                        'div#' + self.element.id + ' > .p-widget:last-child {' + // secondWidget
                        'position: absolute;' +
                        'left: ' + 0 + 'px;' +
                        'right: ' + 0 + 'px;' +
                        'bottom: ' + 0 + 'px;' +
                        'top: ' + (dividerLocation + dividerSize) + 'px;' +
                        '}';
                divider.style.left = '0px';
                divider.style.right = '0px';
                divider.style.height = dividerSize + 'px';
                divider.style.top = dividerLocation + 'px';
                divider.style.width = '';
                divider.style.bottom = '';
            }
        }

        var divider = document.createElement('div');
        divider.classList.add('p-split-divider');
        divider.style.position = 'absolute';
        this.element.appendChild(divider);

        (function () {
            var mouseDownAt = null;
            var mouseDownDividerAt = null;
            var onMouseUp = null;
            var onMouseMove = null;
            Ui.on(divider, Ui.Events.MOUSEDOWN, function (event) {
                if (event.button === 0) {
                    event.stopPropagation();
                    if (orientation === Ui.Orientation.HORIZONTAL) {
                        mouseDownAt = event.clientX;
                    } else {
                        mouseDownAt = event.clientY;
                    }
                    mouseDownDividerAt = dividerLocation;
                    if (!onMouseUp) {
                        onMouseUp = Ui.on(document, Ui.Events.MOUSEUP, function (event) {
                            event.stopPropagation();
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
                            event.stopPropagation();
                            var mouseDiff;
                            if (orientation === Ui.Orientation.HORIZONTAL) {
                                mouseDiff = event.clientX - mouseDownAt;
                            } else {
                                mouseDiff = event.clientY - mouseDownAt;
                            }
                            self.dividerLocation = mouseDownDividerAt + mouseDiff;
                        });
                    }
                }
            });
        }());

        formatChildren();

        function checkAdd(w) {
            if (!firstWidget) {
                firstWidget = w;
                self.element.insertBefore(firstWidget.element, divider);
            } else {
                if (secondWidget) {
                    superRemove(secondWidget);
                }
                secondWidget = w;
                self.element.appendChild(secondWidget.element);
            }
        }

        var superAdd = this.add;
        function add(w, beforeIndex) {
            if (w) {
                if (w.parent === self)
                    throw 'A widget already added to this container';
                superAdd(w, beforeIndex);
                checkAdd(w);
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        function checkRemove(w) {
            if (w === firstWidget) {
                firstWidget = null;
            }
            if (w === secondWidget) {
                secondWidget = null;
            }
        }

        var superRemove = this.remove;
        function remove(w) {
            var removed = superRemove(w);
            checkRemove(removed);
            return removed;
        }
        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });

        var superClear = this.clear;
        function clear() {
            firstWidget = null;
            secondWidget = null;
            superClear();
        }
        Object.defineProperty(this, 'clear', {
            get: function () {
                return clear;
            }
        });

        Object.defineProperty(this, "orientation", {
            get: function () {
                return orientation;
            },
            set: function (aValue) {
                if (orientation !== aValue) {
                    orientation = aValue;
                    formatChildren();
                }
            }
        });
        Object.defineProperty(this, "firstComponent", {
            get: function () {
                return firstWidget;
            },
            set: function (aFirstWidget) {
                if (firstWidget !== aFirstWidget) {
                    if (firstWidget) {
                        superRemove(firstWidget);
                    }
                    firstWidget = aFirstWidget;
                    if (firstWidget) {
                        superAdd(firstWidget);
                        self.element.insertBefore(firstWidget.element, divider);
                    }
                }
            }
        });
        Object.defineProperty(this, "secondComponent", {
            get: function () {
                return secondWidget;
            },
            set: function (aSecondWidget) {
                if (secondWidget !== aSecondWidget) {
                    if (secondWidget) {
                        superRemove(secondWidget);
                    }
                    secondWidget = aSecondWidget;
                    if (secondWidget) {
                        superAdd(secondWidget);
                        self.element.appendChild(secondWidget.element);
                    }
                }
            }
        });
        Object.defineProperty(this, "dividerLocation", {
            get: function () {
                return dividerLocation;
            },
            set: function (aValue) {
                if (dividerLocation !== aValue) {
                    var dividerLimit = (orientation === Ui.Orientation.HORIZONTAL ? self.element.offsetWidth : self.element.offsetHeight) - dividerSize;
                    if (aValue >= 0 && aValue <= dividerLimit) {
                        dividerLocation = aValue;
                        formatChildren();
                    }
                }
            }
        });
        Object.defineProperty(this, "dividerSize", {
            get: function () {
                return dividerSize;
            },
            set: function (aValue) {
                if (dividerSize !== aValue) {
                    dividerSize = aValue;
                    formatChildren();
                }
            }
        });
        Object.defineProperty(this, "oneTouchExpandable", {
            get: function () {
                return oneTouchExpandable;
            },
            set: function (aValue) {
                if (oneTouchExpandable !== aValue) {
                    oneTouchExpandable = aValue;
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
        }
        Object.defineProperty(this, 'ajustHeight', {
            get: function () {
                return ajustHeight;
            }
        });
    }
    extend(Split, Container);
    return Split;
});