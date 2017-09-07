define([
    '../ui',
    './events/mouse-event',
    './events/component-event',
    './events/action-event',
    '../invoke'], function (
        Ui,
        MouseEvent,
        ComponentEvent,
        ActionEvent,
        Invoke) {
    function Widget(box, shell) {
        if (!(this instanceof Widget))
            throw 'Use new with this constructor function';
        if (!box) {
            box = document.createElement('div');
        }
        if (!shell) {
            shell = box;
        }

        if (shell !== box)
            shell.appendChild(box);

        var self = this;

        shell['p-widget'] = this;
        shell.classList.add('p-widget');

        var parent;
        var visibleDisplay = 'inline-block';
        var menu;
        var enabled = true;
        var name;
        var background;
        var foreground;
        var opaque = true;
        var cursor;
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
                return shell;
            }
        });

        Object.defineProperty(this, 'enabled', {
            get: function () {
                return enabled;
            },
            set: function (aValue) {
                if (enabled !== aValue) {
                    enabled = aValue;
                    if (enabled)
                        box.removeAttribute('disabled');
                    else
                        box.setAttribute('disabled', '');
                }
            }
        });
        Object.defineProperty(this, 'name', {
            get: function () {
                return name;
            },
            set: function (aValue) {
                name = aValue;
                self.element.name = name;
            }
        });
        Object.defineProperty(this, 'title', {
            get: function () {
                return box.title;
            },
            set: function (aValue) {
                box.title = aValue;
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
                        shell.style.fontFamily = font.family;
                        shell.style.fontSize = font.size + 'pt';
                        if (font.bold) {
                            shell.style.fontWeight = 'bold';
                        } else {
                            shell.style.fontWeight = 'normal';
                        }
                        if (font.italic) {
                            shell.style.fontStyle = 'italic';
                        } else {
                            shell.style.fontStyle = 'normal';
                        }
                    } else {
                        shell.style.fontFamily = '';
                        shell.style.fontSize = '';
                        shell.style.fontWeight = '';
                        shell.style.fontStyle = '';
                    }
                }
            }
        });

        function applyBackground() {
            if (opaque)
                box.style.backgroundColor = background && background.toStyled ? background.toStyled() : background;
            else
                box.style.background = 'none';
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
                box.style.color = foreground && foreground.toStyled ? foreground.toStyled() : foreground;
            }
        });
        Object.defineProperty(this, 'cursor', {
            get: function () {
                return cursor;
            },
            set: function (aValue) {
                cursor = aValue;
                box.style.cursor = cursor;
            }
        });

        var errorText = null;
        Object.defineProperty(this, 'error', {
            get: function () {
                if (errorText)
                    return errorText;
                else if (box.validationMessage)
                    return box.validationMessage;
                else
                    return null;
            },
            set: function (aValue) {
                if (self.error !== aValue) {
                    errorText = aValue;
                    if (box.setCustomValidity)
                        box.setCustomValidity(aValue !== null ? aValue : '');
                    if (aValue) {
                        if (self.showError)
                            self.showError();
                    } else {
                        if (self.hideError)
                            self.hideError();
                    }
                }
            }
        });
        Object.defineProperty(this, 'toolTipText', {
            get: function () {
                return toolTipText;
            },
            set: function (aValue) {
                if (toolTipText !== aValue) {
                    toolTipText = aValue;
                    box.title = toolTipText;
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
        Object.defineProperty(this, "left", {
            get: function () {
                if (isAttached()) {
                    return shell.offsetLeft;
                } else {
                    var parsed = parseFloat(shell.style.left);
                    return isNaN(parsed) ? 0 : parsed;
                }
            },
            set: function (aValue) {
                if (aValue !== null) {
                    if (parent && parent.ajustLeft)
                        parent.ajustLeft(self, aValue);
                    else
                        shell.style.left = aValue + 'px';
                } else {
                    shell.style.left = null;
                }
            }
        });
        Object.defineProperty(this, "top", {
            get: function () {
                if (isAttached()) {
                    return shell.offsetTop;
                } else {
                    var parsed = parseFloat(shell.style.top);
                    return isNaN(parsed) ? 0 : parsed;
                }
            },
            set: function (aValue) {
                if (aValue !== null) {
                    if (parent && parent.ajustTop)
                        parent.ajustTop(self, aValue);
                    else
                        shell.style.top = aValue + 'px';
                } else {
                    shell.style.top = null;
                }
            }
        });
        Object.defineProperty(this, "width", {
            get: function () {
                if (isAttached())
                    return shell.offsetWidth;
                else {
                    var parsed = parseFloat(shell.style.width);
                    return isNaN(parsed) ? 0 : parsed;
                }
            },
            set: function (aValue) {
                if (aValue !== null) {
                    if (parent && parent.ajustWidth) {
                        parent.ajustWidth(self, aValue);
                    } else {
                        shell.style.width = aValue + 'px';
                    }
                } else {
                    shell.style.width = null;
                }
            }
        });
        Object.defineProperty(this, "height", {
            get: function () {
                if (isAttached()) {
                    return shell.offsetHeight;
                } else {
                    var parsed = parseFloat(shell.style.height);
                    return isNaN(parsed) ? 0 : parsed;
                }
            },
            set: function (aValue) {
                if (aValue !== null) {
                    if (parent && parent.ajustHeight) {
                        parent.ajustHeight(self, aValue);
                    } else {
                        shell.style.height = aValue + 'px';
                    }
                } else {
                    shell.style.height = null;
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
                        menuTriggerReg = Ui.on(box, Ui.Events.CONTEXTMENU, function (event) {
                            event.preventDefault();
                            event.stopPropagation();
                            if (menu.showAt) {
                                Ui.startMenuSession(menu);
                                var pageX = event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
                                var pageY = event.clientY + document.body.scrollTop + document.documentElement.scrollTop;
                                menu.showAt(pageX, pageY);
                            }
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
            var clickReg = Ui.on(shell, Ui.Events.CLICK, function (evt) {
                handler(new MouseEvent(self, evt, 1));
            });
            var dblClickReg = Ui.on(shell, Ui.Events.DBLCLICK, function (evt) {
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
            return Ui.on(shell, Ui.Events.MOUSEDOWN, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        function addMouseUpHandler(handler) {
            return Ui.on(shell, Ui.Events.MOUSEUP, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        function addMouseMoveHandler(handler) {
            return Ui.on(shell, Ui.Events.MOUSEMOVE, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        function addMouseEnterHandler(handler) {
            return Ui.on(shell, Ui.Events.MOUSEOVER, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        function addMouseExitHandler(handler) {
            return Ui.on(shell, Ui.Events.MOUSEOUT, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        function addMouseWheelHandler(handler) {
            return Ui.on(shell, Ui.Events.MOUSEWHEEL, function (evt) {
                handler(new MouseEvent(self, evt));
            });
        }

        Object.defineProperty(this, 'addActionHandler', {
            configurable: true,
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
            var cursorElement = shell;
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
                return shell.style.display !== 'none';
            },
            set: function (aValue) {
                var oldValue = self.visible;
                if (oldValue !== aValue) {
                    if (aValue) {
                        shell.style.display = visibleDisplay;
                    } else {
                        shell.style.display = 'none';
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
            box.focus();
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
                        actionPerformedReg = self.addActionHandler(function (event) {
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
                            // TODO: Check mouse capturing capture using during dragging
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