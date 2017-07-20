define([
    '../extend',
    '../ui',
    './container'], function (
        extend,
        Ui,
        Container) {
    function Split(dividerSize) {
        Container.call(this);

        var self = this;

        if (arguments.length < 1)
            dividerSize = 10;

        var firstWidget;
        var secondWidget;

        var oneTouchExpandable;
        var orientation = Ui.Orientation.Horizontal;
        var dividerLocation = 84;

        function checkAdd(w) {
            if (!firstWidget) {
                firstWidget = w;
                formatFirst();
            } else {
                if (secondWidget) {
                    superRemove(secondWidget);
                }
                secondWidget = w;
                formatSecond();
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
            checkRemove(w);
            return superRemove(w);
        }
        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });

        function formatFirst() {
            if (firstWidget) {
                var fs = firstWidget.element.style;
                if (orientation === Ui.Orientation.Horizontal) {
                    fs.left = 0 + 'px';
                    fs.top = 0 + 'px';
                    fs.bottom = 0 + 'px';
                    fs.width = dividerLocation + 'px';
                } else {
                    fs.left = 0 + 'px';
                    fs.right = 0 + 'px';
                    fs.top = 0 + 'px';
                    fs.height = dividerLocation + 'px';
                }
            }
        }

        function formatSecond() {
            if (secondWidget) {
                var fs = secondWidget.element.style;
                if (orientation === Ui.Orientation.Horizontal) {
                    fs.right = 0 + 'px';
                    fs.top = 0 + 'px';
                    fs.bottom = 0 + 'px';
                    fs.left = dividerLocation + dividerSize + 'px';
                } else {
                    fs.left = 0 + 'px';
                    fs.right = 0 + 'px';
                    fs.bottom = 0 + 'px';
                    fs.top = dividerLocation + dividerSize + 'px';
                }
            }
        }

        function getTop(aWidget) {
            if (aWidget.parent !== this)
                throw "widget should be a child of this container";
            return aWidget.element.offsetTop;
        }
        Object.defineProperty(this, "getTop", {
            get: function () {
                return getTop;
            }
        });

        function getLeft(aWidget) {
            if (aWidget.parent !== this)
                throw "widget should be a child of this container";
            return aWidget.element.offsetLeft;
        }
        Object.defineProperty(this, "getLeft", {
            get: function () {
                return getLeft;
            }
        });

        Object.defineProperty(this, "orientation", {
            get: function () {
                return orientation;
            },
            set: function (aValue) {
                if (orientation !== aValue) {
                    orientation = aValue;
                    formatFirst();
                    formatSecond();
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
                        formatFirst();
                        superAdd(firstWidget);
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
                        formatSecond();
                        superAdd(secondWidget);
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
                    dividerLocation = aValue;
                    formatFirst();
                    formatSecond();
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
                    formatFirst();
                    formatSecond();
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
    }
    extend(Split, Container);
    return Split;
});