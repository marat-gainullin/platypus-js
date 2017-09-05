define([
    '../../ui',
    '../../invoke',
    '../../id',
    '../events/window-event'
], function (
        Ui,
        Invoke,
        Id,
        WindowEvent) {
    
    var DEFAULT_WINDOWS_SPACING_X = 25;
    var DEFAULT_WINDOWS_SPACING_Y = 20;

    var platformLocation = {x: 0, y: 0};
    var shownForms = new Map();
    function getShownForms() {
        return Array.from(shownForms.values());
    }
    function getShownForm(aFormKey) {
        return shownForms.get(aFormKey);
    }

    function WindowPane() {
        var self = this;
        var shell = document.createElement('div');
        shell.calssName = 'p-window-shell';
        var caption = document.createElement('div');
        caption.calssName = 'p-window-caption';
        var image = null;
        var text = document.createElement('p');
        text.calssName = 'p-window-text';
        caption.appendChild(text);
        Ui.on(caption, Ui.Events.DBLCLICK, function(){
            if(maximized){
                restore();
            } else if(!minimized){
                maximize();
            }
        });

        var t = document.createElement('div');
        t.className = 'p-window-t';
        var l = document.createElement('div');
        l.className = 'p-window-l';
        var b = document.createElement('div');
        b.className = 'p-window-b';
        var r = document.createElement('div');
        r.className = 'p-window-r';
        var tl = document.createElement('div');
        tl.className = 'p-window-tl';
        var tr = document.createElement('div');
        tr.className = 'p-window-tr';
        var bl = document.createElement('div');
        bl.className = 'p-window-bl';
        var br = document.createElement('div');
        br.className = 'p-window-br';

        [t, l, r, b, tl, tr, bl, b, br].forEach(function (decor) {
            shell.appendChild(decor);
        });

        var tools = document.createElement('div');
        tools.className = 'p-window-tools';
        var closeTool = document.createElement('div');
        closeTool.className = 'p-window-close-tool';
        Ui.on(closeTool, Ui.Events.CLICK, function(){
            close();
        });
        var minimizeTool = document.createElement('div');
        minimizeTool.className = 'p-window-minimize-tool';
        Ui.on(minimizeTool, Ui.Events.CLICK, function(){
            minimize();
        });
        var restoreTool = document.createElement('div');
        restoreTool.className = 'p-window-restore-tool';
        Ui.on(restoreTool, Ui.Events.CLICK, function(){
            restore();
        });
        var maximizeTool = document.createElement('div');
        maximizeTool.className = 'p-window-maximize-tool';
        Ui.on(maximizeTool, Ui.Events.CLICK, function(){
            maximize();
        });

        caption.appendChild(tools);
        [minimizeTool, restoreTool, maximizeTool, closeTool].forEach(function (tool) {
            tools.appendChild(tool);
        });

        var formKey = Id.generate();
        Object.defineProperty(this, 'formKey', {
            get: function () {
                return formKey;
            },
            set: function (aValue) {
                if (formKey !== aValue) {
                    if (shell.parentElement)
                        shownForms.delete(formKey);
                    formKey = aValue;
                    if (shell.parentElement)
                        shownForms.set(formKey, self);
                    fireShownChange();
                }
            }
        });
        var defaultCloseOperation = 2;
        Object.defineProperty(this, 'defaultCloseOperation', {
            get: function () {
                return defaultCloseOperation;
            },
            set: function (aValue) {
                if (defaultCloseOperation !== aValue) {
                    defaultCloseOperation = aValue;
                }
            }
        });

        Object.defineProperty(this, 'icon', {
            get: function () {
                return image;
            },
            set: function (aValue) {
                if (image !== aValue) {
                    if (image) {
                        image.classList.remove('p-window-image');
                        caption.removeChild(image);
                    }
                    image = aValue;
                    if (image) {
                        caption.insertBefore(text);
                        image.classList.add('p-window-image');
                    }
                }
            }
        });
        Object.defineProperty(this, "title", {
            get: function () {
                return text.innerText;
            },
            set: function (aValue) {
                if (text !== aValue) {
                    text.innerText = aValue;
                }
            }
        });
        var resizable = true;
        var minimizable = true;
        var minimized = false;
        var maximizable = true;
        var closable = true;
        var maximized = false;
        var undecorated = false;
        var opacity = 1;
        var alwaysOnTop = false;
        var locationByPlatform = true;

        function updateToolsVisibility() {
            minimizeTool.style.display = minimizable && !minimized ? '' : 'none';
            maximizeTool.style.display = maximizable && !maximized && !minimized ? '' : 'none';
            restoreTool.style.display = minimized || maximized ? '' : 'none';
            closeTool.style.display = closable ? '' : 'none';
        }

        Object.defineProperty(this, "resizable", {
            get: function () {
                return resizable;
            },
            set: function (aValue) {
                resizable = !!aValue;
                updateToolsVisibility();
            }
        });
        Object.defineProperty(this, "minimizable", {
            get: function () {
                return minimizable;
            },
            set: function (aValue) {
                minimizable = !!aValue;
                updateToolsVisibility();
            }
        });
        Object.defineProperty(this, "maximizable", {
            get: function () {
                return maximizable;
            },
            set: function (aValue) {
                maximizable = !!aValue;
                updateToolsVisibility();
            }
        });
        Object.defineProperty(this, "closable", {
            get: function () {
                return closable;
            },
            set: function (aValue) {
                closable = !!aValue;
                updateToolsVisibility();
            }
        });
        Object.defineProperty(this, "minimized", {
            get: function () {
                return minimized;
            }
        });
        Object.defineProperty(this, "maximized", {
            get: function () {
                return maximized;
            }
        });
        Object.defineProperty(this, "undecorated", {
            get: function () {
                return undecorated;
            },
            set: function (aValue) {
                undecorated = !!aValue;
                [caption, t, l, r, b, tl, tr, bl, b, br].forEach(function (decor) {
                    decor.style.display = undecorated ? 'none' : '';
                });
            }
        });
        Object.defineProperty(this, "opacity", {
            get: function () {
                return opacity;
            },
            set: function (aValue) {
                if (opacity !== aValue) {
                    opacity = (aValue * 1);
                    shell.style.opacity = opacity;
                }
            }
        });
        Object.defineProperty(this, "alwaysOnTop", {
            get: function () {
                return alwaysOnTop;
            },
            set: function (aValue) {
                alwaysOnTop = !!aValue;
            }
        });
        Object.defineProperty(this, "locationByPlatform", {
            get: function () {
                return locationByPlatform;
            },
            set: function (aValue) {
                locationByPlatform = !!aValue;
            }
        });
        Object.defineProperty(this, "left", {
            get: function () {
                return shell.offsetLeft;
            },
            set: function (aValue) {
                shell.style.left = (aValue * 1) + 'px';
            }
        });
        Object.defineProperty(this, "top", {
            get: function () {
                return shell.offsetTop;
            },
            set: function (aValue) {
                shell.style.top = (aValue * 1) + 'px';
            }
        });
        Object.defineProperty(this, "width", {
            get: function () {
                return shell.offsetWidth;
            },
            set: function (aValue) {
                content.style.width = (aValue * 1 - (shell.offsetWidth - content.offsetWidth)) + 'px';
            }
        });
        Object.defineProperty(this, "height", {
            get: function () {
                return shell.offsetHeight;
            },
            set: function (aValue) {
                content.style.height = (aValue * 1 - (shell.offsetHeight - content.offsetHeight)) + 'px';
            }
        });

        function isOutsideOfWindow(anElement) {
            var currentElement = anElement;
            while (currentElement !== null && currentElement !== shell && currentElement !== document.body)
                currentElement = currentElement.parentElement;
            return currentElement === document.body || currentElement === null;
        }
        var autoHideMouseDownReg = null;
        function applyAutoHide() {
            if (autoHideMouseDownReg) {
                autoHideMouseDownReg.removeHandler();
                autoHideMouseDownReg = null;
            }
            if (autoClose && shell.parentElement) {
                autoHideMouseDownReg = Ui.on(document, Ui.Events.MOUSEDOWN, function (evt) {
                    if (isOutsideOfWindow(evt.target)) {
                        evt.stopPropagation();
                        close();
                    }
                }, true);
            }
        }

        Object.defineProperty(this, "autoClose", {
            get: function () {
                return autoClose;
            },
            set function(aValue) {
                if (autoClose !== aValue) {
                    autoClose = !!aValue;
                    applyAutoHide();
                }
            }
        });

        var windowOpenedHandlers = new Set();
        function addWindowOpenedHandler(h) {
            windowOpenedHandlers.add(h);
            return {
                removeHandler: function () {
                    windowOpenedHandlers.delete(h);
                }
            };
        }

        Object.defineProperty(this, 'addWindowOpenedHandler', {
            get: function () {
                return addWindowOpenedHandler;
            }
        });

        function fireWindowOpened() {
            shownForms.set(formKey, self);
            var event = new WindowEvent(self);
            windowOpenedHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
            fireShownChange();
            applyAutoHide();
        }

        var onWindowOpened = null;
        var windowOpenedReg = null;
        Object.defineProperty(this, "onWindowOpened", {
            get: function () {
                return onWindowOpened;
            },
            set: function (aValue) {
                if (windowOpenedReg) {
                    windowOpenedReg.removeHandler();
                    windowOpenedReg = null;
                }
                onWindowOpened = aValue;
                if (onWindowOpened) {
                    windowOpenedReg = addWindowOpenedHandler(onWindowOpened);
                }
            }
        });

        var windowClosingHandlers = new Set();
        function addWindowClosingHandler(h) {
            windowClosingHandlers.add(h);
            return {
                removeHandler: function () {
                    windowClosingHandlers.delete(h);
                }
            };
        }

        Object.defineProperty(this, 'addWindowClosingHandler', {
            get: function () {
                return addWindowClosingHandler;
            }
        });

        function fireWindowClosing() {
            var canClose = true;
            var event = new WindowEvent(self);
            windowClosingHandlers.forEach(function (h) {
                if (!h(event)) {
                    canClose = false;
                }
            });
            return canClose;
        }

        var onWindowClosing = null;
        var windowClosingReg = null;
        Object.defineProperty(this, "onWindowClosing", {
            get: function () {
                return onWindowClosing;
            },
            set: function (aValue) {
                if (windowClosingReg) {
                    windowClosingReg.removeHandler();
                    windowClosingReg = null;
                }
                onWindowClosing = aValue;
                if (onWindowClosing) {
                    windowClosingReg = addWindowClosingHandler(onWindowClosing);
                }
            }
        });

        var windowClosedHandlers = new Set();
        function addWindowClosedHandler(h) {
            windowClosedHandlers.add(h);
            return {
                removeHandler: function () {
                    windowClosedHandlers.delete(h);
                }
            };
        }

        Object.defineProperty(this, 'addWindowClosedHandler', {
            get: function () {
                return addWindowClosedHandler;
            }
        });

        function fireWindowClosed(selectedItem) {
            shownForms.delete(formKey);
            var event = new WindowEvent(self);
            windowClosedHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
            fireShownChange();
            if (autoHideMouseDownReg) {
                autoHideMouseDownReg.removeHandler();
                autoHideMouseDownReg = null;
            }
            if (onItemSelected) {
                var _onItemSelected = onItemSelected;
                onItemSelected = null;
                Invoke.later(function () {
                    _onItemSelected(selectedItem);
                });
            }
        }

        var onWindowClosed = null;
        var windowClosedReg = null;
        Object.defineProperty(this, "onWindowClosed", {
            get: function () {
                return onWindowClosed;
            },
            set: function (aValue) {
                if (windowClosedReg) {
                    windowClosedReg.removeHandler();
                    windowClosedReg = null;
                }
                onWindowClosed = aValue;
                if (onWindowClosed) {
                    windowClosedReg = addWindowClosedHandler(onWindowClosed);
                }
            }
        });

        var windowMinimizedHandlers = new Set();
        function addWindowMinimizedHandler(h) {
            windowMinimizedHandlers.add(h);
            return {
                removeHandler: function () {
                    windowMinimizedHandlers.delete(h);
                }
            };
        }

        Object.defineProperty(this, 'addWindowMinimizedHandler', {
            get: function () {
                return addWindowMinimizedHandler;
            }
        });

        function fireWindowMinimized() {
            var event = new WindowEvent(self);
            windowMinimizedHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var onWindowMinimized = null;
        var windowMinimizedReg = null;
        Object.defineProperty(this, "onWindowMinimized", {
            get: function () {
                return onWindowMinimized;
            },
            set: function (aValue) {
                if (windowMinimizedReg) {
                    windowMinimizedReg.removeHandler();
                    windowMinimizedReg = null;
                }
                onWindowMinimized = aValue;
                if (onWindowMinimized) {
                    windowMinimizedReg = addWindowMinimizedHandler(onWindowMinimized);
                }
            }
        });

        var windowRestoredHandlers = new Set();
        function addWindowRestoredHandler(h) {
            windowRestoredHandlers.add(h);
            return {
                removeHandler: function () {
                    windowRestoredHandlers.delete(h);
                }
            };
        }

        Object.defineProperty(this, 'addWindowRestoredHandler', {
            get: function () {
                return addWindowRestoredHandler;
            }
        });

        function fireWindowRestored() {
            var event = new WindowEvent(self);
            windowRestoredHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var onWindowRestored = null;
        var windowRestoredReg = null;
        Object.defineProperty(this, "onWindowRestored", {
            get: function () {
                return onWindowRestored;
            },
            set: function (aValue) {
                if (windowRestoredReg) {
                    windowRestoredReg.removeHandler();
                    windowRestoredReg = null;
                }
                onWindowRestored = aValue;
                if (onWindowRestored) {
                    windowRestoredReg = addWindowRestoredHandler(onWindowRestored);
                }
            }
        });

        var windowMaximizedHandlers = new Set();
        function addWindowMaximizedHandler(h) {
            windowMaximizedHandlers.add(h);
            return {
                removeHandler: function () {
                    windowMaximizedHandlers.delete(h);
                }
            };
        }

        Object.defineProperty(this, 'addWindowMaximizedHandler', {
            get: function () {
                return addWindowMaximizedHandler;
            }
        });

        function fireWindowMaximized() {
            var event = new WindowEvent(self);
            windowMaximizedHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var onWindowMaximized = null;
        var windowMaximizedReg = null;
        Object.defineProperty(this, "onWindowMaximized", {
            get: function () {
                return onWindowMaximized;
            },
            set: function (aValue) {
                if (windowMaximizedReg) {
                    windowMaximizedReg.removeHandler();
                    windowMaximizedReg = null;
                }
                onWindowMaximized = aValue;
                if (onWindowMaximized) {
                    windowMaximizedReg = addWindowMaximizedHandler(onWindowMaximized);
                }
            }
        });

        var windowActivatedHandlers = new Set();
        function addWindowActivatedHandler(h) {
            windowActivatedHandlers.add(h);
            return {
                removeHandler: function () {
                    windowActivatedHandlers.delete(h);
                }
            };
        }

        Object.defineProperty(this, 'addWindowActivatedHandler', {
            get: function () {
                return addWindowActivatedHandler;
            }
        });

        function fireWindowActivated() {
            var event = new WindowEvent(self);
            windowActivatedHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var onWindowActivated = null;
        var windowActivatedReg = null;
        Object.defineProperty(this, "onWindowActivated", {
            get: function () {
                return onWindowActivated;
            },
            set: function (aValue) {
                if (windowActivatedReg) {
                    windowActivatedReg.removeHandler();
                    windowActivatedReg = null;
                }
                onWindowActivated = aValue;
                if (onWindowActivated) {
                    windowActivatedReg = addWindowHandler(onWindowActivated);
                }
            }
        });

        var windowDeactivatedHandlers = new Set();
        function addWindowDeactivatedHandler(h) {
            windowDeactivatedHandlers.add(h);
            return {
                removeHandler: function () {
                    windowDeactivatedHandlers.delete(h);
                }
            };
        }

        Object.defineProperty(this, 'addWindowDeactivatedHandler', {
            get: function () {
                return addWindowDeactivatedHandler;
            }
        });

        function fireWindowDeactivated() {
            var event = new WindowEvent(self);
            windowDeactivatedHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var onWindowDeactivated = null;
        var windowDeactivatedReg = null;
        Object.defineProperty(this, "onWindowDeactivated", {
            get: function () {
                return onWindowDeactivated;
            },
            set: function (aValue) {
                if (windowDeactivatedReg) {
                    windowDeactivatedReg.removeHandler();
                    windowDeactivatedReg = null;
                }
                onWindowDeactivated = aValue;
                if (onWindowDeactivated) {
                    windowDeactivatedReg = addWindowDeactivatedHandler(onWindowDeactivated);
                }
            }
        });

        function activate() {
            if (shell.className.indexOf('p-window-active') === -1) {
                Array.from(shownForms.values)
                        .filter(function (aWindow) {
                            return aWindow !== self;
                        })
                        .forEach(function (aWindow) {
                            aWindow.deactivate();
                        });
                shell.classList.add('p-window-active');
                fireWindowActivated();
            }
        }
        Object.defineProperty(this, "activate", {
            get: function () {
                return activate;
            }
        });

        function deactivate() {
            if (shell.className.indexOf('p-window-active') > -1) {
                shell.classList.remove('p-window-active');
                fireWindowDeactivated();
            }
        }
        Object.defineProperty(this, "deactivate", {
            get: function () {
                return deactivate;
            }
        });

        function show() {
            if (!shell.parentElement) {
                document.body.appendChild(shell);
                if (locationByPlatform) {
                    if (!shell.style.left) {
                        shell.style.left = platformLocation.x + 'px';
                    }
                    if (!shell.style.top) {
                        shell.style.top = platformLocation.y + 'px';
                    }
                    platformLocation.x += DEFAULT_WINDOWS_SPACING_X;
                    if (platformLocation.x + shell.offsetWidth > window.innerWidth)
                        platformLocation.x = 0;
                    platformLocation.y += DEFAULT_WINDOWS_SPACING_Y;
                    if (platformLocation.y + shell.offsetHeight > window.innerHeight)
                        platformLocation.y = 0;
                } else {
                    if (!shell.style.left) {
                        shell.style.left = ((window.innerWidth - shell.offsetWidth) / 2) + 'px';
                    }
                    if (!shell.style.top) {
                        shell.style.top = ((window.innerHeight - shell.offsetHeight) / 2) + 'px';
                    }
                }
                fireWindowOpened();
                activate();
            }
        }
        Object.defineProperty(this, "show", {
            get: function () {
                return show;
            }
        });

        var modelMask = document.createElement('div');
        modelMask.className = 'p-window-modal-mask';
        var onItemSelected = null;
        function showModal(aOnItemSelected) {
            if (!shell.parentElement) {
                onItemSelected = aOnItemSelected;
                document.body.appendChild(modelMask);
                show();
            }
        }
        Object.defineProperty(this, "showModal", {
            get: function () {
                return showModal;
            }
        });

        function showInternalFrame(aDesktop) {
            if (!shell.parentElement) {
                aDesktop.element.appendChild(shell);
                if (locationByPlatform) {
                    if (!shell.style.left) {
                        shell.style.left = aDesktop.platformLocationLeft + 'px';
                    }
                    if (!shell.style.top) {
                        shell.style.top = aDesktop.platformLocationTop + 'px';
                    }
                    aDesktop.platformLocationLeft += DEFAULT_WINDOWS_SPACING_X;
                    if (aDesktop.platformLocationLeft + shell.offsetWidth > aDesktop.element.clientWidth)
                        aDesktop.platformLocationLeft = 0;
                    aDesktop.platformLocationTop += DEFAULT_WINDOWS_SPACING_Y;
                    if (aDesktop.platformLocationTop + shell.offsetHeight > aDesktop.element.clientHeight)
                        aDesktop.platformLocationTop = 0;
                } else {
                    if (!shell.style.left) {
                        shell.style.left = ((aDesktop.element.offsetWidth - shell.offsetWidth) / 2) + 'px';
                    }
                    if (!shell.style.top) {
                        shell.style.top = ((aDesktop.element.offsetHeight - shell.offsetHeight) / 2) + 'px';
                    }
                }
                fireWindowOpened();
                activate();
            }
        }
        Object.defineProperty(this, "showInternalFrame", {
            get: function () {
                return showInternalFrame;
            }
        });

        function hide() {
            if (modelMask.parentElement)
                modelMask.parentElement.removeChild(modelMask);
            if (shell.parentElement)
                shell.parentElement.removeChild(shell);
        }

        function close(selectedItem) {
            if (shell.parentElement) {
                if (fireWindowClosing()) {
                    hide();
                    fireWindowClosed(selectedItem);
                }
            }
        }
        Object.defineProperty(this, "close", {
            get: function () {
                return close;
            }
        });

        var sizePositionSnapshot = {left: 0, top: 0, width: 0, height: 0};

        function minimize() {
            if (!minimized) {
                sizePositionSnapshot = {left: self.left, top: self.top, width: self.width, height: self.height, maximized: maximized};
                minimized = true;
                fireWindowMinimized();
                updateToolsVisibility();
            }
        }
        Object.defineProperty(this, "minimize", {
            get: function () {
                return minimize;
            }
        });

        function maximize() {
            if (!maximized && !minimized) {
                sizePositionSnapshot = {left: self.left, top: self.top, width: self.width, height: self.height, maximized: maximized};
                maximized = true;
                fireWindowMaximized();
                updateToolsVisibility();
            }
        }
        Object.defineProperty(this, "maximize", {
            get: function () {
                return maximize;
            }
        });

        function restore() {
            if (maximized || minimized) {
                minimized = false;
                maximized = sizePositionSnapshot.maximized;
                self.left = sizePositionSnapshot.left;
                self.top = sizePositionSnapshot.top;
                self.width = sizePositionSnapshot.width;
                self.height = sizePositionSnapshot.height;
                fireWindowRestored();
                updateToolsVisibility();
            }
        }
        Object.defineProperty(this, "restore", {
            get: function () {
                return restore;
            }
        });

        function toFront() {
            if (shell.parentElement) {
                var targetParent = shell.parentElement;
                targetParent.removeChild(shell);
                targetParent.appendChild(shell);
                activate();
            }
        }
        Object.defineProperty(this, "toFront", {
            get: function () {
                return toFront;
            }
        });
    }

    Object.defineProperty(WindowPane, 'shown', {
        get: function () {
            return getShownForms;
        }
    });

    Object.defineProperty(WindowPane, 'getShownForm', {
        get: function () {
            return getShownForm;
        }
    });

    var shownChangeHandlers = new Set();
    function addShownChangeHandler(h) {
        shownChangeHandlers.add(h);
        return {
            removeHandler: function () {
                shownChangeHandlers.delete(h);
            }
        };
    }

    Object.defineProperty(this, 'addShownChangeHandler', {
        get: function () {
            return addShownChangeHandler;
        }
    });

    function fireShownChange() {
        var event = new WindowEvent(shownForms);
        shownChangeHandlers.forEach(function (h) {
            Invoke.later(function () {
                h(event);
            });
        });
    }

    var onShownChange = null;
    var shownChangeReg = null;
    Object.defineProperty(this, "onChange", {
        get: function () {
            return onShownChange;
        },
        set: function (aValue) {
            if (shownChangeReg) {
                shownChangeReg.removeHandler();
                shownChangeReg = null;
            }
            onShownChange = aValue;
            if (onShownChange) {
                shownChangeReg = addShownChangeHandler(onShownChange);
            }
        }
    });

    return WindowPane;
});