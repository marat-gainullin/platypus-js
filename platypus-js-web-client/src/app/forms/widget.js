define([
    '../ui',
    './mouse-event',
    './component-event',
    '../invoke'], function (
        Ui,
        MouseEvent,
        ComponentEvent,
        ActionEvent,
        Invoke) {
    function Widget(element) {
        if (!(this instanceof Widget))
            throw 'Use new with this constructor function';

        var self = this;

        if (!element)
            element = document.createElement('div');
        element['p-widget'] = this;
        element.classList.add('p-widget');

        // TODO: Ensure all widgets have a class 'border-sized' or something as 'widget' with border sizing setted up.

        var parent;
        var visibleDisplay = 'inline-block';
        var menu;
        var enabled = true;
        var name;
        var background;
        var foreground;
        var opaque = true;
        var cursor;
        var error;
        var toolTipText;
        var nextFocusableComponent;
        var focusable = false;
        var font = null;

        Object.defineProperty(this, 'visibleDisplay', {
            get: function () {
                return visibleDisplay;
            },
            set: function (aValue) {
                visibleDisplay = aValue;
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
        Object.defineProperty(this, 'element', {
            get: function () {
                return element;
            }
        });

        Object.defineProperty(this, 'enabled', {
            get: function () {
                return enabled;
            },
            set: function (aValue) {
                var oldValue = enabled;
                enabled = aValue;
                if (!oldValue && enabled) {
                    Ui.unmask(element);
                } else if (oldValue && !enabled) {
                    Ui.disabledMask(element);
                }
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
        Object.defineProperty(this, 'title', {
            get: function () {
                return element.title;
            },
            set: function (aValue) {
                element.title = aValue;
            }
        });
        Object.defineProperty(this, 'font', {
            get: function () {
                return font;
            },
            set: function (aValue) {
                if (font !== aValue) {
                    font = aValue;
                    if (font) {
                        element.style.fontFamily = font.family;
                        element.style.fontSize = font.size + 'pt';
                        if (font.bold) {
                            element.style.fontWeight = 'bold';
                        } else {
                            element.style.fontWeight = 'normal';
                        }
                        if (font.italic) {
                            element.style.fontStyle = 'italic';
                        } else {
                            element.style.fontStyle = 'normal';
                        }
                    } else {
                        element.style.fontFamily = '';
                        element.style.fontSize = '';
                        element.style.fontWeight = '';
                        element.style.fontStyle = '';
                    }
                }
            }
        });

        function applyBackground() {
            if (opaque)
                element.style.backgroundColor = background && background.toStyled ? background.toStyled() : background;
            else
                element.style.background = 'none';
        }

        Object.defineProperty(this, 'opaque', {
            get: function () {
                return opaque;
            },
            set: function (aValue) {
                if (opaque !== aValue) {
                    opaque = aValue;
                    applyBackground();
                }
            }
        });
        Object.defineProperty(this, 'background', {
            get: function () {
                return background;
            },
            set: function (aValue) {
                if (background !== aValue) {
                    background = aValue;
                    applyBackground();
                }
            }
        });
        Object.defineProperty(this, 'foreground', {
            get: function () {
                return foreground;
            },
            set: function (aValue) {
                foreground = aValue;
                element.style.color = foreground && foreground.toStyled ? foreground.toStyled() : foreground;
            }
        });
        Object.defineProperty(this, 'cursor', {
            get: function () {
                return cursor;
            },
            set: function (aValue) {
                cursor = aValue;
                element.style.cursor = cursor;
            }
        });
        Object.defineProperty(this, 'error', {
            get: function () {
                return error;
            },
            set: function (aValue) {
                error = aValue;
            }
        });
        Object.defineProperty(this, 'toolTipText', {
            get: function () {
                return toolTipText;
            },
            set: function (aValue) {
                if (toolTipText !== aValue) {
                    toolTipText = aValue;
                    element.title = toolTipText;
                }
            }
        });
        Object.defineProperty(this, 'nextFocusableComponent', {
            get: function () {
                return nextFocusableComponent;
            },
            set: function (aValue) {
                if (nextFocusableComponent !== aValue) {
                    nextFocusableComponent = aValue;
                }
            }
        });
        Object.defineProperty(this, 'focusable', {
            get: function () {
                return focusable;
            },
            set: function (aValue) {
                if (focusable !== aValue) {
                    focusable = aValue;
                }
            }
        });
        // TODO: Check 'left', 'top', 'width', 'height' properties and
        // 'getLeft', 'getTop', 'ajustWidth' and 'ajustHeight' methods against all containers.
        Object.defineProperty(this, "left", {
            get: function () {
                if (parent && parent.getLeft) {
                    return parent.getLeft(self);
                } else {
                    if (isAttached()) {
                        return element.offsetLeft;
                    } else {
                        var parsed = parseFloat(element.style.left);
                        return isNaN(parsed) ? 0 : parsed;
                    }
                }
            },
            set: function (aValue) {
                if (aValue !== null) {
                    if (parent && parent.ajustLeft)
                        parent.ajustLeft(self, aValue);
                    else
                        element.style.left = aValue + 'px';
                } else {
                    element.style.left = null;
                }
            }
        });
        Object.defineProperty(this, "top", {
            get: function () {
                if (parent && parent.getTop) {
                    return parent.getTop(self);
                } else {
                    if (isAttached()) {
                        return element.offsetTop;
                    } else {
                        var parsed = parseFloat(element.style.top);
                        return isNaN(parsed) ? 0 : parsed;
                    }
                }
            },
            set: function (aValue) {
                if (aValue !== null) {
                    if (parent && parent.ajustTop)
                        parent.ajustTop(self, aValue);
                    else
                        element.style.top = aValue + 'px';
                } else {
                    element.style.top = null;
                }
            }
        });
        Object.defineProperty(this, "width", {
            get: function () {
                if (isAttached())
                    return element.offsetWidth;
                else {
                    var parsed = parseFloat(element.style.width);
                    return isNaN(parsed) ? 0 : parsed;
                }
            },
            set: function (aValue) {
                if (aValue !== null) {
                    if (parent && parent.ajustWidth) {
                        parent.ajustWidth(self, aValue);
                    } else {
                        element.style.width = aValue + 'px';
                    }
                } else {
                    element.style.width = null;
                }
            }
        });
        Object.defineProperty(this, "height", {
            get: function () {
                if (isAttached()) {
                    return element.offsetHeight;
                } else {
                    var parsed = parseFloat(element.style.height);
                    return isNaN(parsed) ? 0 : parsed;
                }
            },
            set: function (aValue) {
                if (aValue !== null) {
                    if (parent && parent.ajustHeight) {
                        parent.ajustHeight(self, aValue);
                    } else {
                        element.style.height = aValue + 'px';
                    }
                } else {
                    element.style.height = null;
                }
            }
        });
        var menuTriggerReg = null;
        Object.defineProperty(this, 'componentPopupMenu', {
            get: function () {
                return menu;
            },
            set: function (aValue) {
                if (menu !== aValue) {
                    if (menuTriggerReg) {
                        menuTriggerReg.removeHandler();
                        menuTriggerReg = null;
                    }
                    menu = aValue;
                    if (menu) {
                        menuTriggerReg = Ui.on(element, Ui.Events.CONTEXTMENU, function (event) {
                            event.preventDefault();
                            event.stopPropagation();
                            menu.show(Ui.absoluteLeft(element), Ui.absoluteTop(element), element.offsetWidth, element.offsetHeight);
                        });
                    }
                }
            }
        });

        var actionHandlers = new Set();

        function addActionHandler(handler) {
            actionHandlers.add(handler);
            return {
                removeHandler: function () {
                    actionHandlers.delete(handler);
                }

            };
        }

        function fireActionPerformed() {
            var event = new ActionEvent(self);
            actionHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var hideHandlers = new Set();

        function addHideHandler(handler) {
            hideHandlers.add(handler);
            return {
                removeHandler: function () {
                    hideHandlers.delete(handler);
                }
            };
        }

        var showHandlers = new Set();

        function addShowHandler(handler) {
            showHandlers.add(handler);
            return {
                removeHandler: function () {
                    showHandlers.delete(handler);
                }
            };
        }

        function addMouseClickHandler(handler) {
            var clickReg = Ui.on(element, Ui.Events.CLICK, function (evt) {
                handler(new MouseEvent(self, evt, 1));
            });
            var dblClickReg = Ui.on(element, Ui.Events.DBLCLICK, function (evt) {
                handler(new MouseEvent(self, evt, 2));
            });
            return {
                removeHandler: function () {
                    clickReg.removeHandler();
                    dblClickReg.removeHandler();
                }
            };
        }

        function addMouseDownHandler(handler) {
            return Ui.on(element, Ui.Events.MOUSEDOWN, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        function addMouseUpHandler(handler) {
            return Ui.on(element, Ui.Events.MOUSEUP, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        function addMouseMoveHandler(handler) {
            return Ui.on(element, Ui.Events.MOUSEMOVE, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        function addMouseEnterHandler(handler) {
            return Ui.on(element, Ui.Events.MOUSEOVER, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        function addMouseExitHandler(handler) {
            return Ui.on(element, Ui.Events.MOUSEOUT, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        function addMouseWheelHandler(handler) {
            return Ui.on(element, Ui.Events.MOUSEWHEEL, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        Object.defineProperty(this, 'addActionHandler', {
            get: function () {
                return addActionHandler;
            }
        });
        Object.defineProperty(this, 'addHideHandler', {
            get: function () {
                return addHideHandler;
            }
        });
        Object.defineProperty(this, 'addShowHandler', {
            get: function () {
                return addShowHandler;
            }
        });
        Object.defineProperty(this, 'addMouseClickHandler', {
            get: function () {
                return addMouseClickHandler;
            }
        });
        Object.defineProperty(this, 'addMouseDownHandler', {
            get: function () {
                return addMouseDownHandler;
            }
        });
        Object.defineProperty(this, 'addMouseUpHandler', {
            get: function () {
                return addMouseUpHandler;
            }
        });
        Object.defineProperty(this, 'addMouseMoveHandler', {
            get: function () {
                return addMouseMoveHandler;
            }
        });
        Object.defineProperty(this, 'addMouseEnterHandler', {
            get: function () {
                return addMouseEnterHandler;
            }
        });
        Object.defineProperty(this, 'addMouseExitHandler', {
            get: function () {
                return addMouseExitHandler;
            }
        });
        Object.defineProperty(this, 'addMouseWheelHandler', {
            get: function () {
                return addMouseWheelHandler;
            }
        });

        Object.defineProperty(this, 'fireActionPerformed', {
            get: function () {
                return fireActionPerformed;
            }
        });

        function isAttached() {
            var cursorElement = element;
            while (cursorElement && cursorElement !== document.body) {
                cursorElement = cursorElement.parentElement;
            }
            return !!cursorElement;
        }

        Object.defineProperty(this, 'attached', {
            get: function () {
                return isAttached();
            }
        });
        Object.defineProperty(this, 'visible', {
            get: function () {
                return element.style.display !== 'none';
            },
            set: function (aValue) {
                var oldValue = self.visible;
                if (oldValue !== aValue) {
                    if (aValue) {
                        element.style.display = visibleDisplay;
                    } else {
                        element.style.display = 'none';
                    }
                    if (aValue) {
                        fireShown();
                    } else {
                        fireHidden();
                    }
                }
            }
        });

        function focus() {
            element.focus();
        }
        Object.defineProperty(this, 'focus', {
            get: function () {
                return focus;
            }
        });
        Object.defineProperty(this, 'component', {
            get: function () {
                return null;
            }
        });

        function fireShown() {
            var event = new ComponentEvent(self);
            showHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }
        function fireHidden() {
            var event = new ComponentEvent(self);
            hideHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var MOUSESTATE = {
            NULL: {}, PRESSED: {}, MOVED: {}, DRAGGED: {}
        };

        var mouseState = MOUSESTATE.NULL;

        var onActionPerformed;
        var actionPerformedReg;
        Object.defineProperty(this, 'onActionPerformed', {
            get: function () {
                return onActionPerformed;
            }, set: function (aValue) {
                if (onActionPerformed !== aValue) {
                    if (actionPerformedReg) {
                        actionPerformedReg.removeHandler();
                        actionPerformedReg = null;
                    }
                    onActionPerformed = aValue;
                    if (onActionPerformed) {
                        actionPerformedReg = addActionHandler(function (event) {
                            if (onActionPerformed) {
                                onActionPerformed(event);
                            }
                        });
                    }
                }
            }});

        var onMouseExited;
        var mouseOutReg;
        Object.defineProperty(this, 'onMouseExited', {
            get: function () {
                return onMouseExited;
            }, set: function (aValue) {
                if (onMouseExited !== aValue) {
                    if (mouseOutReg) {
                        mouseOutReg.removeHandler();
                        mouseOutReg = null;
                    }
                    onMouseExited = aValue;
                    if (onMouseExited) {
                        mouseOutReg = addMouseExitHandler(function (evt) {
                            if (onMouseExited) {
                                evt.event.stopPropagation();
                                onMouseExited(evt);
                            }
                        });
                    }
                }
            }});
        var onMouseClicked;
        var mouseClickedReg;
        Object.defineProperty(this, 'onMouseClicked', {
            get: function () {
                return onMouseClicked;
            },
            set: function (aValue) {
                if (onMouseClicked !== aValue) {
                    if (mouseClickedReg) {
                        mouseClickedReg.removeHandler();
                        mouseClickedReg = null;
                    }
                    onMouseClicked = aValue;
                    if (onMouseClicked) {
                        mouseClickedReg = addMouseClickHandler(function (evt) {
                            if (onMouseClicked) {
                                evt.event.stopPropagation();
                                onMouseClicked(evt);
                            }
                        });
                    }
                }
            }
        });
        var onMousePressed;
        var mouseDownReg;
        Object.defineProperty(this, 'onMousePressed', {
            get: function () {
                return onMousePressed;
            },
            set: function (aValue) {
                if (onMousePressed !== aValue) {
                    if (mouseDownReg) {
                        mouseDownReg.removeHandler();
                        mouseDownReg = null;
                    }
                    onMousePressed = aValue;
                    if (onMousePressed) {
                        mouseDownReg = addMouseDownHandler(function (evt) {
                            if (onMousePressed) {
                                evt.event.stopPropagation();
                                onMousePressed(evt);
                            }
                        });
                    }
                }
            }
        });
        var onMouseReleased;
        var mouseUpReg;
        Object.defineProperty(this, 'onMouseReleased', {
            get: function () {
                return onMouseReleased;
            },
            set: function (aValue) {
                if (onMouseReleased !== aValue) {
                    if (mouseUpReg) {
                        mouseUpReg.removeHandler();
                        mouseUpReg = null;
                    }
                    onMouseReleased = aValue;
                    if (onMouseReleased) {
                        mouseUpReg = addMouseUpHandler(function (evt) {
                            if (onMouseReleased) {
                                evt.event.stopPropagation();
                                onMouseReleased(evt);
                            }
                        });
                    }
                }
            }
        });
        var onMouseEntered;
        var mouseOverReg;
        Object.defineProperty(this, 'onMouseEntered', {
            get: function () {
                return onMouseEntered;
            },
            set: function (aValue) {
                if (onMouseEntered !== aValue) {
                    if (mouseOverReg) {
                        mouseOverReg.removeHandler();
                        mouseOverReg = null;
                    }
                    onMouseEntered = aValue;
                    if (onMouseEntered) {
                        mouseOverReg = addMouseEnterHandler(function (evt) {
                            if (onMouseEntered) {
                                evt.event.stopPropagation();
                                onMouseEntered(evt);
                            }
                        });
                    }
                }
            }
        });
        var onMouseWheelMoved;
        var mouseWheelReg;
        Object.defineProperty(this, 'onMouseWheelMoved', {
            get: function () {
                return onMouseWheelMoved;
            },
            set: function (aValue) {
                if (onMouseWheelMoved !== aValue) {
                    if (mouseWheelReg) {
                        mouseWheelReg.removeHandler();
                        mouseWheelReg = null;
                    }
                    onMouseWheelMoved = aValue;
                    if (onMouseWheelMoved) {
                        mouseWheelReg = addMouseWheelHandler(function (evt) {
                            if (onMouseWheelMoved) {
                                evt.event.stopPropagation();
                                onMouseWheelMoved(evt);
                            }
                        });
                    }
                }
            }
        });
        var onMouseMoved;
        var mouseMoveReg;
        Object.defineProperty(this, 'onMouseMoved', {
            get: function () {
                return onMouseMoved;
            },
            set: function (aValue) {
                if (onMouseMoved !== aValue) {
                    if (mouseMoveReg) {
                        mouseMoveReg.removeHandler();
                        mouseMoveReg = null;
                    }
                    onMouseMoved = aValue;
                    if (onMouseMoved) {
                        mouseMoveReg = addMouseMoveHandler(function (evt) {
                            if (onMouseMoved) {
                                evt.event.stopPropagation();
                                onMouseMoved(evt);
                            }
                        });
                    }
                }

            }
        });
        var onMouseDragged;
        var mouseDownForDragReg;
        var mouseUpForDragReg;
        var mouseMoveForDragReg;
        Object.defineProperty(this, 'onMouseDragged', {
            get: function () {
                return onMouseDragged;
            },
            set: function (aValue) {
                if (onMouseDragged !== aValue) {
                    if (mouseDownForDragReg) {
                        mouseDownForDragReg.removeHandler();
                        mouseDownForDragReg = null;
                    }
                    if (mouseUpForDragReg) {
                        mouseUpForDragReg.removeHandler();
                        mouseUpForDragReg = null;
                    }
                    if (mouseMoveForDragReg) {
                        mouseMoveForDragReg.removeHandler();
                        mouseMoveForDragReg = null;
                    }
                    onMouseDragged = aValue;
                    if (onMouseDragged) {
                        mouseDownForDragReg = addMouseDownHandler(function (evt) {
                            // TODO: Check capture using during dragging
                            element.setCapture();
                            mouseState = MOUSESTATE.PRESSED;
                            onMouseDragged(evt);
                        });
                        mouseUpForDragReg = addMouseUpHandler(function (evt) {
                            document.releaseCapture();
                            evt.event.stopPropagation();
                            mouseState = MOUSESTATE.NULL;
                        });
                        mouseMoveForDragReg = addMouseMoveHandler(function (evt) {
                            if (onMouseDragged) {
                                evt.event.stopPropagation();
                                if (mouseState === MOUSESTATE.PRESSED || mouseState === MOUSESTATE.DRAGGED) {
                                    mouseState = MOUSESTATE.DRAGGED;
                                    onMouseDragged(evt);
                                }
                            }
                        });
                    }
                }
            }
        });
        var onComponentShown;
        var componentShownReg;
        Object.defineProperty(this, 'onComponentShown', {
            get: function () {
                return onComponentShown;
            },
            set: function (aValue) {
                if (onComponentShown !== aValue) {
                    if (componentShownReg) {
                        componentShownReg.removeHandler();
                        componentShownReg = null;
                    }
                    onComponentShown = aValue;
                    if (onComponentShown) {
                        componentShownReg = addShowHandler(function (event) {
                            if (onComponentShown) {
                                onComponentShown(event);
                            }
                        });
                    }
                }
            }
        });
        var onComponentHidden;
        var componentHiddenReg;
        Object.defineProperty(this, 'onComponentHidden', {
            get: function () {
                return onComponentHidden;
            },
            set: function (aValue) {
                if (onComponentHidden !== aValue) {
                    if (componentHiddenReg) {
                        componentHiddenReg.removeHandler();
                        componentHiddenReg = null;
                    }
                    onComponentHidden = aValue;
                    if (onComponentHidden) {
                        componentHiddenReg = addHideHandler(function (event) {
                            if (onComponentHidden) {
                                onComponentHidden(event);
                            }
                        });
                    }
                }
            }
        });
        var onFocusGained;
        var focusReg;
        Object.defineProperty(this, 'onFocusGained', {
            get: function () {
                return onFocusGained;
            },
            set: function (aValue) {
                if (onFocusGained !== aValue) {
                    if (focusReg) {
                        focusReg.removeHandler();
                        focusReg = null;
                    }
                    onFocusGained = aValue;
                    if (onFocusGained && self.addFocusHandler) {
                        focusReg = self.addFocusHandler(function (event) {
                            if (onFocusGained) {
                                onFocusGained(event);
                            }
                        });
                    }
                }
            }
        });
        var onFocusLost;
        var blurReg;
        Object.defineProperty(this, 'onFocusLost', {
            get: function () {
                return onFocusLost;
            },
            set: function (aValue) {
                if (onFocusLost !== aValue) {
                    if (blurReg) {
                        blurReg.removeHandler();
                        blurReg = null;
                    }
                    onFocusLost = aValue;
                    if (onFocusLost && self.addBlurHandler) {
                        blurReg = self.addBlurHandler(function (event) {
                            if (onFocusLost) {
                                onFocusLost(event);
                            }
                            mouseState = MOUSESTATE.NULL;
                        });
                    }
                }
            }
        });
        var onKeyTyped;
        var keyTypedReg;
        Object.defineProperty(this, 'onKeyTyped', {
            get: function () {
                return onKeyTyped;
            },
            set: function (aValue) {
                if (onKeyTyped !== aValue) {
                    if (keyTypedReg) {
                        keyTypedReg.removeHandler();
                        keyTypedReg = null;
                    }
                    onKeyTyped = aValue;
                    if (onKeyTyped && self.addKeyPressHandler) {
                        keyTypedReg = self.addKeyPressHandler(function (event) {
                            if (onKeyTyped) {
                                event.event.stopPropagation();
                                onKeyTyped(event);
                            }
                        });
                    }
                }
            }
        });
        var onKeyPressed;
        var keyDownReg;
        Object.defineProperty(this, 'onKeyPressed', {
            get: function () {
                return onKeyPressed;
            },
            set: function (aValue) {
                if (onKeyPressed !== aValue) {
                    if (keyDownReg) {
                        keyDownReg.removeHandler();
                        keyDownReg = null;
                    }
                    onKeyPressed = aValue;
                    if (onKeyPressed && self.addKeyDownHandler) {
                        keyDownReg = self.addKeyDownHandler(function (event) {
                            if (onKeyPressed) {
                                event.getEvent().stopPropagation();
                                onKeyPressed(event);
                            }
                        });
                    }
                }
            }
        });
        var onKeyReleased;
        var keyUpReg;
        Object.defineProperty(this, 'onKeyReleased', {
            get: function () {
                return onKeyReleased;
            },
            set: function (aValue) {
                if (onKeyReleased !== aValue) {
                    if (keyUpReg) {
                        keyUpReg.removeHandler();
                        keyUpReg = null;
                    }
                    onKeyReleased = aValue;
                    if (onKeyReleased && self.addKeyUpHandler) {
                        keyUpReg = self.addKeyUpHandler(function (event) {
                            if (onKeyReleased) {
                                event.event.stopPropagation();
                                onKeyReleased(event);
                            }
                        });
                    }
                }
            }
        });
        var onItemSelected;
        var selectedItemReg;
        Object.defineProperty(this, 'onItemSelected', {
            get: function () {
                return onItemSelected;
            },
            set: function (aValue) {
                if (onItemSelected !== aValue) {
                    if (selectedItemReg) {
                        selectedItemReg.removeHandler();
                        selectedItemReg = null;
                    }
                    onItemSelected = aValue;
                    if (onItemSelected && self.addSelectionHandler) {
                        selectedItemReg = self.addSelectionHandler(function (event) {
                            if (onItemSelected) {
                                onItemSelected(event);
                            }
                        });
                    }
                }
            }
        });
        var valueChange;
        var valueChangeReg;
        Object.defineProperty(this, 'onValueChange', {get: function () {
                return valueChange;
            },
            set: function (aValue) {
                if (valueChange !== aValue) {
                    if (valueChangeReg) {
                        valueChangeReg.removeHandler();
                        valueChangeReg = null;
                    }
                    valueChange = aValue;
                    if (valueChange && self.addValueChangeHandler) {
                        valueChangeReg = self.addValueChangeHandler(function (event) {
                            if (valueChange) {
                                valueChange(event);
                            }
                        });

                    }
                }
            }
        });
    }
    return Widget;
});