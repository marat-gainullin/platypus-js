define([
    '../extend',
    './anchors-pane'
], function (extend, AnchorsPane) {

    var DEFAULT_WINDOWS_SPACING_X = 25;
    var DEFAULT_WINDOWS_SPACING_Y = 20;

    function DesktopPane() {
        AnchorsPane.call(this);

        var self = this;

        var consideredPosition = {x: DEFAULT_WINDOWS_SPACING_X, y: DEFAULT_WINDOWS_SPACING_Y};


        Object.defineProperty(this, 'forms', {
            get: function () {
                var managed = [];
                self.forEach(function (w) {
                    if (w.minimize && w.maximize && w.close && w.restore) {
                        managed.push(w);
                    }
                });
                return managed;
            }
        });

        function minimizeAll() {
            self.forEach(function (w) {
                if (w.minimize) {
                    w.minimize();
                }
            });
        }
        Object.defineProperty(this, 'minimizeAll', {
            get: function () {
                return minimizeAll;
            }
        });

        function maximizeAll() {
            self.forEach(function (w) {
                if (w.maximize) {
                    w.maximize();
                }
            });
        }
        Object.defineProperty(this, 'maximizeAll', {
            get: function () {
                return maximizeAll;
            }
        });

        function restoreAll() {
            self.forEach(function (w) {
                if (w.restore) {
                    w.restore();
                }
            });
        }
        Object.defineProperty(this, 'restoreAll', {
            get: function () {
                return restoreAll;
            }
        });

        function closeAll() {
            self.forEach(function (w) {
                if (w.close) {
                    w.close();
                }
            });
        }
        Object.defineProperty(this, 'closeAll', {
            get: function () {
                return closeAll;
            }
        });

        Object.defineProperty(this, 'consideredPosition', {
            get: function () {
                return consideredPosition;
            }
        });

        var superAdd = this.add;
        function add(w, beforeIndex) {
            superAdd(w, beforeIndex);
            check(w);
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        function check(w) {
            if (w.minimize && w.maximize && w.close && w.restore) {
                refreshConsideredPosition();
                var regs = [];
                if (w.addActivateHandler) {
                    regs.push(w.addActivateHandler(function (anEvent) {
                        self.forEach(function (child) {
                            if (child != anEvent.target && child.deactivate) {
                                child.deactivate();
                            }
                        });
                    }));
                }
                if (w.addClosedHandler) {
                    regs.push(w.addClosedHandler(function (event) {
                        self.remove(event.target);
                        regs.forEach(function (reg) {
                            reg.removeHandler();
                        });
                    }));
                }
            }
        }

        function refreshConsideredPosition() {
            if (consideredPosition.x > self.element.clientWidth / 2) {
                consideredPosition = {x: 0, y: consideredPosition.y};// setX(0)
            }
            if (consideredPosition.y > self.element.clientHeight / 2) {
                consideredPosition = {x: consideredPosition.x, y: 0};// setY(0)
            }
            consideredPosition = {x: consideredPosition.x + DEFAULT_WINDOWS_SPACING_X, y: consideredPosition.y + DEFAULT_WINDOWS_SPACING_Y};
        }

    }
    extend(DesktopPane, AnchorsPane);
    return DesktopPane;
});