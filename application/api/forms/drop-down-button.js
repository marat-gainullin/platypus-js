/* global Java */

define(['boxing'], function(P) {
    /**
     * Drop-down button component.
     * @param text the text of the component (optional).
     * @param icon the icon of the component (optional).
     * @param iconTextGap the text gap (optional).
     * @param actionPerformed the function for the action performed handler (optional).
     * @constructor DropDownButton DropDownButton
     */
    function DropDownButton(text, icon, iconTextGap, actionPerformed) {
        var maxArgs = 4;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 4 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(iconTextGap), P.boxAsJava(actionPerformed))
            : arguments.length === 3 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(iconTextGap))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(DropDownButton.superclass)
            DropDownButton.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseDragged = aValue;
            }
        });

        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseReleased = aValue;
            }
        });

        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusLost = aValue;
            }
        });

        Object.defineProperty(this, "icon", {
            get: function() {
                var value = delegate.icon;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.icon = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return value;
            },
            set: function(aValue) {
                delegate.onMousePressed = aValue;
            }
        });

        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.error = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentMoved = aValue;
            }
        });

        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentResized = aValue;
            }
        });

        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "text", {
            get: function() {
                var value = delegate.text;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.text = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseEntered = aValue;
            }
        });

        Object.defineProperty(this, "dropDownMenu", {
            get: function() {
                var value = delegate.dropDownMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.dropDownMenu = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentShown = aValue;
            }
        });

        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseMoved = aValue;
            }
        });

        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentHidden = aValue;
            }
        });

        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "iconTextGap", {
            get: function() {
                var value = delegate.iconTextGap;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.iconTextGap = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyReleased = aValue;
            }
        });

        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return value;
            },
            set: function(aValue) {
                delegate.onActionPerformed = aValue;
            }
        });

        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyTyped = aValue;
            }
        });

        Object.defineProperty(this, "horizontalTextPosition", {
            get: function() {
                var value = delegate.horizontalTextPosition;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.horizontalTextPosition = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "verticalTextPosition", {
            get: function() {
                var value = delegate.verticalTextPosition;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.verticalTextPosition = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = aValue;
            }
        });

        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusGained = aValue;
            }
        });

        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseClicked = aValue;
            }
        });

        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseExited = aValue;
            }
        });

        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyPressed = aValue;
            }
        });

    };
    /**
     * Tries to acquire focus for this component.
     * @method focus
     * @memberOf DropDownButton
     */
    DropDownButton.prototype.focus = function() {
        var delegate = this.unwrap();
        var value = delegate.focus();
        return P.boxAsJs(value);
    };


    var className = "com.eas.client.forms.components.DropDownButton";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new DropDownButton(null, null, null, null, aDelegate);
    });
    return DropDownButton;
});