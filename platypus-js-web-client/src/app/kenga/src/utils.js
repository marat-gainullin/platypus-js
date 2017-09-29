define([
    './color',
    './cursor',
    './font',
    'core/invoke'], function (
        Color,
        Cursor,
        Font,
        Invoke) {
    var global = window;
    var Events = {
        BLUR: 'blur',
        CANPLAYTHROUGH: 'canplaythrough',
        CHANGE: 'change',
        CLICK: 'click',
        CONTEXTMENU: 'contextmenu',
        DBLCLICK: 'dblclick',
        DRAG: 'drag',
        DRAGEND: 'dragend',
        DRAGENTER: 'dragenter',
        DRAGLEAVE: 'dragleave',
        DRAGOVER: 'dragover',
        DRAGSTART: 'dragstart',
        DROP: 'drop',
        ENDED: 'ended',
        ERROR: 'error',
        FOCUS: 'focus',
        FOCUSIN: 'focusin',
        FOCUSOUT: 'focusout',
        GESTURECHANGE: 'gesturechange',
        GESTUREEND: 'gestureend',
        GESTURESTART: 'gesturestart',
        INPUT: 'input',
        KEYDOWN: 'keydown',
        KEYPRESS: 'keypress',
        KEYUP: 'keyup',
        LOAD: 'load',
        LOADEDMETADATA: 'loadedmetadata',
        LOSECAPTURE: 'losecapture',
        MOUSEDOWN: 'mousedown',
        MOUSEMOVE: 'mousemove',
        MOUSEOUT: 'mouseout',
        MOUSEOVER: 'mouseover',
        MOUSEUP: 'mouseup',
        MOUSEWHEEL: 'mousewheel',
        PROGRESS: 'progress',
        SCROLL: 'scroll',
        TOUCHCANCEL: 'touchcancel',
        TOUCHEND: 'touchend',
        TOUCHMOVE: 'touchmove',
        TOUCHSTART: 'touchstart'
    };

    var throttle = (function () {
        var watchdog = null;
        function throttle(action, timeout) {
            if (arguments.length < 1)
                throw "Missing throttle 'action' argument";
            if (arguments.length < 2)
                throw "Missing throttle 'timeout' argument";
            if (timeout < 1) // ms
                action();
            else {
                function invoked() {
                    watchdog = null;
                    action();
                }
                if (!watchdog) {
                    Invoke.delayed(timeout, invoked);
                    watchdog = invoked;
                }
            }
        }
        return throttle;
    }());

    function absoluteLeft(elem) {
        var left = 0;
        var curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent) {
            left -= curr.scrollLeft;
            curr = curr.parentNode;
        }
        while (elem) {
            left += elem.offsetLeft;
            elem = elem.offsetParent;
        }
        return left;
    }

    function absoluteTop(elem) {
        var top = 0;
        var curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent) {
            top -= curr.scrollTop;
            curr = curr.parentNode;
        }
        while (elem) {
            top += elem.offsetTop;
            elem = elem.offsetParent;
        }
        return top;
    }

    function on(element, eventName, handler, capturePhase) {
        element.addEventListener(eventName, handler, !!capturePhase);
        return {
            removeHandler: function () {
                element.removeEventListener(eventName, handler, !!capturePhase);
            }
        };
    }

    var Orientation = {HORIZONTAL: 'horizontal', VERTICAL: 'vertical'};
    var VerticalPosition = {CENTER: 'center', TOP: 'top', BOTTOM: 'bottom'};
    var HorizontalPosition = {CENTER: 'center', LEFT: 'left', RIGHT: 'right'};
    var FontStyle = Font.Style;
    var ScrollBarPolicy = {ALLWAYS: 'allways', NEVER: 'never', AUTO: 'auto'};

    function selectFile(onSelection, fileFilter) {
        var fileField = document.createElement('input');
        fileField.type = 'file';
        if (fileFilter)
            fileField.accept = fileFilter;
        on(fileField, 'change', function () {
            if (fileField.files.length === 1)
                onSelection(fileField.files[0]);
            else
                onSelection(fileField.files);
        });
        fileField.click();
    }

    function selectColor(onSelection, oldValue) {
        var colorField = document.createElement('input');
        colorField.type = 'color';
        if (oldValue)
            colorField.value = oldValue + '';
        on(colorField, 'change', function () {
            onSelection(new Color(colorField.value));
        });
        colorField.click();
    }

    function readWidgetElement(widgetRootElement, aModel) {
        var uiReader = new DefaultUiReader(widgetRootElement, aModel);
        uiReader.parse();
        var target = {};
        uiReader.widgets.forEach(function (widget) {
            target[widget.name] = widget;
        });
        return target;
    }

    function loadWidgets(aModuleName, aModel) {
        var layoutDoc = getFormDocument(aModuleName);
        if (layoutDoc) {
            var rootElement = layoutDoc.documentElement;
            var widgetRootElement = aModuleName ? findLayoutElementByBundleName(rootElement, aModuleName) : rootElement;
            return readWidgetElement(widgetRootElement, aModel);
        } else {
            throw "UI definition for module '" + aModuleName + "' is not found";
        }
    }

    function readWidgets(aContent, aModel) {
        var layoutDoc = new DOMParser().parse(aContent + "");
        var rootElement = layoutDoc.documentElement;
        return readWidgetElement(rootElement, aModel);
    }

    var module = {};
    Object.defineProperty(module, 'loadWidgets', {
        enumerable: true,
        value: loadWidgets
    });
    Object.defineProperty(module, 'readWidgets', {
        enumerable: true,
        value: readWidgets
    });
    Object.defineProperty(module, 'Colors', {
        enumerable: true,
        value: Color
    });
    Object.defineProperty(module, 'Color', {
        enumerable: true,
        value: Color
    });
    Object.defineProperty(module, 'Cursor', {
        enumerable: true,
        value: Cursor
    });
    Object.defineProperty(module, 'Font', {
        enumerable: true,
        value: Font
    });
    Object.defineProperty(module, 'selectFile', {
        enumerable: true,
        value: selectFile
    });
    Object.defineProperty(module, 'selectColor', {
        enumerable: true,
        value: selectColor
    });
    Object.defineProperty(module, 'msgBox', {
        enumerable: true,
        value: global.alert
    });
    Object.defineProperty(module, 'error', {
        enumerable: true,
        value: global.alert
    });
    Object.defineProperty(module, 'warn', {
        enumerable: true,
        value: global.alert
    });
    Object.defineProperty(module, 'HorizontalPosition', {
        enumerable: true,
        value: HorizontalPosition
    });
    Object.defineProperty(module, 'VerticalPosition', {
        enumerable: true,
        value: VerticalPosition
    });
    Object.defineProperty(module, 'Orientation', {
        enumerable: true,
        value: Orientation
    });
    Object.defineProperty(module, 'ScrollBarPolicy', {
        enumerable: true,
        value: ScrollBarPolicy
    });
    Object.defineProperty(module, 'FontStyle', {
        enumerable: true,
        value: FontStyle
    });

    Object.defineProperty(module, 'on', {
        get: function () {
            return on;
        }
    });
    Object.defineProperty(module, 'Events', {
        get: function () {
            return Events;
        }
    });
    Object.defineProperty(module, 'throttle', {
        get: function () {
            return throttle;
        }
    });
    Object.defineProperty(module, 'absoluteLeft', {
        get: function () {
            return absoluteLeft;
        }
    });
    Object.defineProperty(module, 'absoluteTop', {
        get: function () {
            return absoluteTop;
        }
    });
    function isMobile() {
        return 'orientation' in window;
    }
    Object.defineProperty(module, 'isMobile', {
        get: function () {
            return isMobile;
        }
    });

    (function () {
        var menuSession = null;
        var mouseDownReg = null;
        function startMenuSession(menu) {
            function isOutsideOfAnyMenu(anElement) {
                var currentElement = anElement;
                while (currentElement !== null && currentElement.className.indexOf('p-menu') === - 1 && currentElement !== document.body)
                    currentElement = currentElement.parentElement;
                return currentElement === document.body || currentElement === null;
            }

            if (menuSession !== menu) {
                if (menuSession) {
                    menuSession.close();
                }
                menuSession = menu;
                mouseDownReg = on(document, Events.MOUSEDOWN, function (evt) {
                    if (isOutsideOfAnyMenu(evt.target)) {
                        evt.stopPropagation();
                        closeMenuSession();
                    }
                }, true);
            }
        }

        function closeMenuSession() {
            if (menuSession) {
                mouseDownReg.removeHandler();
                menuSession.close();
                menuSession = null;
            }
        }

        function isMenuSession() {
            return !!menuSession;
        }

        Object.defineProperty(module, 'startMenuSession', {
            get: function () {
                return startMenuSession;
            }
        });
        Object.defineProperty(module, 'closeMenuSession', {
            get: function () {
                return closeMenuSession;
            }
        });
        Object.defineProperty(module, 'isMenuSession', {
            get: function () {
                return isMenuSession;
            }
        });
    }());
    return module;
});