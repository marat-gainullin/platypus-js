/* global Java */

define(['boxing', 'common-utils/color', 'common-utils/cursor', 'common-utils/font', './cell-render-event'], function(B, Color, Cursor, Font, RenderEvent) {
    var className = "com.eas.client.forms.components.model.grid.header.RadioGridColumn";
    var javaClass = Java.type(className);
    /**
     *
     * @constructor RadioGridColumn RadioGridColumn
     */
    function RadioGridColumn() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(RadioGridColumn.superclass)
            RadioGridColumn.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "preferredWidth", {
            get: function() {
                var value = delegate.preferredWidth;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.preferredWidth = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "resizable", {
            get: function() {
                var value = delegate.resizable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.resizable = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "minWidth", {
            get: function() {
                var value = delegate.minWidth;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.minWidth = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "sortable", {
            get: function() {
                var value = delegate.sortable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.sortable = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "title", {
            get: function() {
                var value = delegate.title;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.title = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "movable", {
            get: function() {
                var value = delegate.movable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.movable = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "readonly", {
            get: function() {
                var value = delegate.readonly;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.readonly = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "maxWidth", {
            get: function() {
                var value = delegate.maxWidth;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.maxWidth = B.boxAsJava(aValue);
            }
        });

    };

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new RadioGridColumn(aDelegate);
    });
    return RadioGridColumn;
});