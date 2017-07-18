define(['../ui', './mouse-event', './component-event'], function (Ui, MouseEvent, ComponentEvent, ActionEvent) {

    function Widget(element) {
        if (!(this instanceof Widget))
            throw 'Use new with this constructor function';
        var self = this;
        if (!element)
            element = document.createElement('div');
        element['p-widget'] = this;
        var parent;
        var visibleDisplay = 'inline-block';
        var menu;
        var enabled = true;
        var name;

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
                h(event);
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
            var cursor = element;
            while (cursor && element !== document.body) {
                cursor = cursor.parentElement;
            }
            return !!cursor;
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
                var oldValue = this.visible;
                if (oldValue !== aValue) {
                    if (aValue) {
                        element.style.display = visibleDisplay;
                    } else {
                        element.style.display = 'none';
                    }
                    var event = new ComponentEvent(self);
                    if (aValue) {
                        showHandlers.forEach(function (h) {
                            h(event);
                        });
                    } else {
                        hideHandlers.forEach(function (h) {
                            h(event);
                        });
                    }
                }
            }
        });

        var MOUSESTATE = {
            NULL: {}, PRESSED: {}, MOVED: {}, DRAGGED: {}
        };

        var mouseState = MOUSESTATE.NULL;

        var actionPerformed;
        var actionPerformedReg;
        Object.defineProperty(this, 'actionPerformed', {
            get: function () {
                return actionPerformed;
            }, set: function (aValue) {
                if (actionPerformed !== aValue) {
                    if (actionPerformedReg) {
                        actionPerformedReg.removeHandler();
                        actionPerformedReg = null;
                    }
                    actionPerformed = aValue;
                    if (actionPerformed) {
                        actionPerformedReg = addActionHandler(function (event) {
                            if (actionPerformed) {
                                actionPerformed(event);
                            }
                        });
                    }
                }
            }});
        var mouseExited;
        var mouseOutReg;

        Object.defineProperty(this, 'mouseExited', {
            get: function () {
                return mouseExited;
            }, set: function (aValue) {
                if (mouseExited !== aValue) {
                    if (mouseOutReg) {
                        mouseOutReg.removeHandler();
                        mouseOutReg = null;
                    }
                    mouseExited = aValue;
                    if (mouseExited) {
                        mouseOutReg = addMouseExitHandler(function (evt) {
                            if (mouseExited) {
                                evt.event.stopPropagation();
                                mouseExited(evt);
                            }
                        });
                    }
                }
            }});
        var mouseClicked;
        var mouseClickedReg;
        Object.defineProperty(this, 'mouseClicked', {
            get: function () {
                return mouseClicked;
            },
            set: function (aValue) {
                if (mouseClicked !== aValue) {
                    if (mouseClickedReg) {
                        mouseClickedReg.removeHandler();
                        mouseClickedReg = null;
                    }
                    mouseClicked = aValue;
                    if (mouseClicked) {
                        mouseClickedReg = addMouseClickHandler(function (evt) {
                            if (mouseClicked) {
                                evt.event.stopPropagation();
                                mouseClicked(evt);
                            }
                        });
                    }
                }
            }
        });
        var mousePressed;
        var mouseDownReg;
        Object.defineProperty(this, 'mousePressed', {
            get: function () {
                return mousePressed;
            },
            set: function (aValue) {
                if (mousePressed !== aValue) {
                    if (mouseDownReg) {
                        mouseDownReg.removeHandler();
                        mouseDownReg = null;
                    }
                    mousePressed = aValue;
                    if (mousePressed) {
                        mouseDownReg = addMouseDownHandler(function (evt) {
                            if (mousePressed) {
                                evt.event.stopPropagation();
                                mousePressed(evt);
                            }
                        });
                    }
                }
            }
        });
        var mouseReleased;
        var mouseUpReg;
        Object.defineProperty(this, 'mouseReleased', {
            get: function () {
                return mouseReleased;
            },
            set: function (aValue) {
                if (mouseReleased !== aValue) {
                    if (mouseUpReg) {
                        mouseUpReg.removeHandler();
                        mouseUpReg = null;
                    }
                    mouseReleased = aValue;
                    if (mouseReleased) {
                        mouseUpReg = addMouseUpHandler(function (evt) {
                            if (mouseReleased) {
                                evt.event.stopPropagation();
                                mouseReleased(evt);
                            }
                        });
                    }
                }
            }
        });
        var mouseEntered;
        var mouseOverReg;
        Object.defineProperty(this, 'mouseEntered', {
            get: function () {
                return mouseEntered;
            },
            set: function (aValue) {
                if (mouseEntered !== aValue) {
                    if (mouseOverReg) {
                        mouseOverReg.removeHandler();
                        mouseOverReg = null;
                    }
                    mouseEntered = aValue;
                    if (mouseEntered) {
                        mouseOverReg = addMouseEnterHandler(function (evt) {
                            if (mouseEntered) {
                                evt.event.stopPropagation();
                                mouseEntered(evt);
                            }
                        });
                    }
                }
            }
        });
        var mouseWheelMoved;
        var mouseWheelReg;
        Object.defineProperty(this, 'mouseWheelMoved', {
            get: function () {
                return mouseWheelMoved;
            },
            set: function (aValue) {
                if (mouseWheelMoved !== aValue) {
                    if (mouseWheelReg) {
                        mouseWheelReg.removeHandler();
                        mouseWheelReg = null;
                    }
                    mouseWheelMoved = aValue;
                    if (mouseWheelMoved) {
                        mouseWheelReg = addMouseWheelHandler(function (evt) {
                            if (mouseWheelMoved) {
                                evt.event.stopPropagation();
                                mouseWheelMoved(evt);
                            }
                        });
                    }
                }
            }
        });
        var mouseMoved;
        var mouseMoveReg;
        Object.defineProperty(this, 'mouseMoved', {
            get: function () {
                return mouseMoved;
            },
            set: function (aValue) {
                if (mouseMoved !== aValue) {
                    if (mouseMoveReg) {
                        mouseMoveReg.removeHandler();
                        mouseMoveReg = null;
                    }
                    mouseMoved = aValue;
                    if (mouseMoved) {
                        mouseMoveReg = addMouseMoveHandler(function (evt) {
                            if (mouseMoved) {
                                evt.event.stopPropagation();
                                mouseMoved(evt);
                            }
                        });
                    }
                }

            }
        });
        var mouseDragged;
        var mouseDownForDragReg;
        var mouseUpForDragReg;
        var mouseMoveForDragReg;
        Object.defineProperty(this, 'mouseDragged', {
            get: function () {
                return mouseDragged;
            },
            set: function (aValue) {
                if (mouseDragged !== aValue) {
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
                    mouseDragged = aValue;
                    if (mouseDragged) {
                        mouseDownForDragReg = addMouseDownHandler(function (evt) {
                            // TODO: Check capture using during dragging
                            element.setCapture();
                            mouseState = MOUSESTATE.PRESSED;
                            mouseDragged(evt);
                        });
                        mouseUpForDragReg = addMouseUpHandler(function (evt) {
                            document.releaseCapture();
                            evt.event.stopPropagation();
                            mouseState = MOUSESTATE.NULL;
                        });
                        mouseMoveForDragReg = addMouseMoveHandler(function (evt) {
                            if (mouseDragged) {
                                evt.event.stopPropagation();
                                if (mouseState === MOUSESTATE.PRESSED || mouseState === MOUSESTATE.DRAGGED) {
                                    mouseState = MOUSESTATE.DRAGGED;
                                    mouseDragged(evt);
                                }
                            }
                        });
                    }
                }
            }
        });
        var componentShown;
        var componentShownReg;
        Object.defineProperty(this, 'componentShown', {
            get: function () {
                return componentShown;
            },
            set: function (aValue) {
                if (componentShown !== aValue) {
                    if (componentShownReg) {
                        componentShownReg.removeHandler();
                        componentShownReg = null;
                    }
                    componentShown = aValue;
                    if (componentShown) {
                        componentShownReg = addShowHandler(function (event) {
                            if (componentShown) {
                                componentShown(event);
                            }
                        });
                    }
                }
            }
        });
        var componentHidden;
        var componentHiddenReg;
        Object.defineProperty(this, 'componentHidden', {
            get: function () {
                return componentHidden;
            },
            set: function (aValue) {
                if (componentHidden !== aValue) {
                    if (componentHiddenReg) {
                        componentHiddenReg.removeHandler();
                        componentHiddenReg = null;
                    }
                    componentHidden = aValue;
                    if (componentHidden) {
                        componentHiddenReg = addHideHandler(function (event) {
                            if (componentHidden) {
                                componentHidden(event);
                            }
                        });
                    }
                }
            }
        });
        var focusGained;
        var focusReg;
        Object.defineProperty(this, 'focusGained', {
            get: function () {
                return focusGained;
            },
            set: function (aValue) {
                if (focusGained !== aValue) {
                    if (focusReg) {
                        focusReg.removeHandler();
                        focusReg = null;
                    }
                    focusGained = aValue;
                    if (focusGained && this.addFocusHandler) {
                        focusReg = this.addFocusHandler(function (event) {
                            if (focusGained) {
                                focusGained(event);
                            }
                        });
                    }
                }
            }
        });
        var focusLost;
        var blurReg;
        Object.defineProperty(this, 'focusLost', {
            get: function () {
                return focusLost;
            },
            set: function (aValue) {
                if (focusLost !== aValue) {
                    if (blurReg) {
                        blurReg.removeHandler();
                        blurReg = null;
                    }
                    focusLost = aValue;
                    if (focusLost && this.addBlurHandler) {
                        blurReg = this.addBlurHandler(function (event) {
                            if (focusLost) {
                                focusLost(event);
                            }
                            mouseState = MOUSESTATE.NULL;
                        });
                    }
                }
            }
        });
        var keyTyped;
        var keyTypedReg;
        Object.defineProperty(this, 'keyTyped', {
            get: function () {
                return keyTyped;
            },
            set: function (aValue) {
                if (keyTyped !== aValue) {
                    if (keyTypedReg) {
                        keyTypedReg.removeHandler();
                        keyTypedReg = null;
                    }
                    keyTyped = aValue;
                    if (keyTyped && this.addKeyPressHandler) {
                        keyTypedReg = this.addKeyPressHandler(function (event) {
                            if (keyTyped) {
                                event.event.stopPropagation();
                                keyTyped(event);
                            }
                        });
                    }
                }
            }
        });
        var keyPressed;
        var keyDownReg;
        Object.defineProperty(this, 'keyPressed', {
            get: function () {
                return keyPressed;
            },
            set: function (aValue) {
                if (keyPressed !== aValue) {
                    if (keyDownReg) {
                        keyDownReg.removeHandler();
                        keyDownReg = null;
                    }
                    keyPressed = aValue;
                    if (keyPressed && this.addKeyDownHandler) {
                        keyDownReg = this.addKeyDownHandler(function (event) {
                            if (keyPressed) {
                                event.getEvent().stopPropagation();
                                keyPressed(event);
                            }
                        });
                    }
                }
            }
        });
        var keyReleased;
        var keyUpReg;
        Object.defineProperty(this, 'keyReleased', {
            get: function () {
                return keyReleased;
            },
            set: function (aValue) {
                if (keyReleased !== aValue) {
                    if (keyUpReg) {
                        keyUpReg.removeHandler();
                        keyUpReg = null;
                    }
                    keyReleased = aValue;
                    if (keyReleased && this.addKeyUpHandler) {
                        keyUpReg = this.addKeyUpHandler(function (event) {
                            if (keyReleased) {
                                event.event.stopPropagation();
                                keyReleased(event);
                            }
                        });
                    }
                }
            }
        });
        var itemSelected;
        var selectedItemReg;
        Object.defineProperty(this, 'itemSelected', {
            get: function () {
                return itemSelected;
            },
            set: function (aValue) {
                if (itemSelected !== aValue) {
                    if (selectedItemReg) {
                        selectedItemReg.removeHandler();
                        selectedItemReg = null;
                    }
                    itemSelected = aValue;
                    if (itemSelected && this.addSelectionHandler) {
                        selectedItemReg = this.addSelectionHandler(function (event) {
                            if (itemSelected) {
                                itemSelected(event);
                            }
                        });
                    }
                }
            }
        });
        var valueChanged;
        var valueChangedReg;
        Object.defineProperty(this, 'valueChanged', {get: function () {
                return valueChanged;
            },
            set: function (aValue) {
                if (valueChanged !== aValue) {
                    if (valueChangedReg) {
                        valueChangedReg.removeHandler();
                        valueChangedReg = null;
                    }
                    valueChanged = aValue;
                    if (valueChanged && this.addValueChangeHandler) {
                        valueChangedReg = this.addValueChangeHandler(function (event) {
                            if (valueChanged) {
                                valueChanged(event);
                            }
                        });

                    }
                }
            }
        });
    }
});