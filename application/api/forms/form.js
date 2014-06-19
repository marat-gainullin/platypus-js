(function() {
    var javaClass = Java.type("com.eas.client.forms.Form");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Form(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Form Form
     */
    P.Form = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * The handler function for the form's <i>before open</i> event.
         * @property onWindowOpened
         * @memberOf Form
         */
        Object.defineProperty(this, "onWindowOpened", {
            get: function() {
                var value = delegate.onWindowOpened;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowOpened = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this window should always be above other windows.
         * @property alwaysOnTop
         * @memberOf Form
         */
        Object.defineProperty(this, "alwaysOnTop", {
            get: function() {
                var value = delegate.alwaysOnTop;
                return P.boxAsJs(value);
            }
        });

        /**
         * The form's icon.
         * @property icon
         * @memberOf Form
         */
        Object.defineProperty(this, "icon", {
            get: function() {
                var value = delegate.icon;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.icon = P.boxAsJava(aValue);
            }
        });

        /**
         * The form's title text.
         * @property title
         * @memberOf Form
         */
        Object.defineProperty(this, "title", {
            get: function() {
                var value = delegate.title;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.title = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>true</code> if this form is minimized.
         * @property minimized
         * @memberOf Form
         */
        Object.defineProperty(this, "minimized", {
            get: function() {
                var value = delegate.minimized;
                return P.boxAsJs(value);
            }
        });

        /**
         * The handler function for the form's <i>after minimize</i> event.
         * @property onWindowMinimized
         * @memberOf Form
         */
        Object.defineProperty(this, "onWindowMinimized", {
            get: function() {
                var value = delegate.onWindowMinimized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowMinimized = P.boxAsJava(aValue);
            }
        });

        /**
         * The distance for this form to the parent container's top side.
         * @property top
         * @memberOf Form
         */
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });

        /**
         * The handler function for the form's <i>after deactivate</i> event.
         * @property onWindowDeactivated
         * @memberOf Form
         */
        Object.defineProperty(this, "onWindowDeactivated", {
            get: function() {
                var value = delegate.onWindowDeactivated;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowDeactivated = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this form should appear at the default location
         * for the native windowing system or at the current location.
         * @property locationByPlatform
         * @memberOf Form
         */
        Object.defineProperty(this, "locationByPlatform", {
            get: function() {
                var value = delegate.locationByPlatform;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.locationByPlatform = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>true</code> if this form minimizable.
         * @property minimizable
         * @memberOf Form
         */
        Object.defineProperty(this, "minimizable", {
            get: function() {
                var value = delegate.minimizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.minimizable = P.boxAsJava(aValue);
            }
        });

        /**
         * The handler function for the form's <i>after activate</i> event.
         * @property onWindowActivated
         * @memberOf Form
         */
        Object.defineProperty(this, "onWindowActivated", {
            get: function() {
                var value = delegate.onWindowActivated;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowActivated = P.boxAsJava(aValue);
            }
        });

        /**
         * The form's height.
         * @property height
         * @memberOf Form
         */
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });

        /**
         * Checks if this form is visible.
         * @property visible
         * @memberOf Form
         */
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            }
        });

        /**
         * The handler function for the form's <i>after maximize</i> event.
         * @property onWindowMaximized
         * @memberOf Form
         */
        Object.defineProperty(this, "onWindowMaximized", {
            get: function() {
                var value = delegate.onWindowMaximized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowMaximized = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>true</code> if this form resizable.
         * @property resizable
         * @memberOf Form
         */
        Object.defineProperty(this, "resizable", {
            get: function() {
                var value = delegate.resizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.resizable = P.boxAsJava(aValue);
            }
        });

        /**
         * The form key. Used to identify a form instance. Initialy set to the form's application element name.
         * @property formKey
         * @memberOf Form
         */
        Object.defineProperty(this, "formKey", {
            get: function() {
                var value = delegate.formKey;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.formKey = P.boxAsJava(aValue);
            }
        });

        /**
         * The handler function for the form's <i>after restore</i> event.
         * @property onWindowRestored
         * @memberOf Form
         */
        Object.defineProperty(this, "onWindowRestored", {
            get: function() {
                var value = delegate.onWindowRestored;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowRestored = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>true</code> if this form is maximized.
         * @property maximized
         * @memberOf Form
         */
        Object.defineProperty(this, "maximized", {
            get: function() {
                var value = delegate.maximized;
                return P.boxAsJs(value);
            }
        });

        /**
         * The handler function for the form's <i>after close</i> event.
         * @property onWindowClosed
         * @memberOf Form
         */
        Object.defineProperty(this, "onWindowClosed", {
            get: function() {
                var value = delegate.onWindowClosed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowClosed = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>true</code> if this form maximizable.
         * @property maximizable
         * @memberOf Form
         */
        Object.defineProperty(this, "maximizable", {
            get: function() {
                var value = delegate.maximizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.maximizable = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>true</code> if no decoration to be enabled for this form.
         * @property undecorated
         * @memberOf Form
         */
        Object.defineProperty(this, "undecorated", {
            get: function() {
                var value = delegate.undecorated;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.undecorated = P.boxAsJava(aValue);
            }
        });

        /**
         * The distance for this form to the parent container's left side.
         * @property left
         * @memberOf Form
         */
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });

        /**
         * The form's width.
         * @property width
         * @memberOf Form
         */
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });

        /**
         * The handler function for the form's <i>before close</i> event.
         * @property onWindowClosing
         * @memberOf Form
         */
        Object.defineProperty(this, "onWindowClosing", {
            get: function() {
                var value = delegate.onWindowClosing;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowClosing = P.boxAsJava(aValue);
            }
        });

        /**
         * The opacity of the form.
         * @property opacity
         * @memberOf Form
         */
        Object.defineProperty(this, "opacity", {
            get: function() {
                var value = delegate.opacity;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opacity = P.boxAsJava(aValue);
            }
        });

        /**
         * Closes this form.
         * @param obj an object to be passed as a result of a selection into <code>showModal</code> callback handler function.
         * @method close
         * @memberOf Form
         */
        Object.defineProperty(this, "close", {
            get: function() {
                return function(obj) {
                    var value = delegate.close(P.boxAsJava(obj));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Shows the form as a dialog (modal window).
         * @param callback a callback handler function
         * @method showModal
         * @memberOf Form
         */
        Object.defineProperty(this, "showModal", {
            get: function() {
                return function(callback) {
                    var value = delegate.showModal(P.boxAsJava(callback));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Moves form to the front position.
         * @method toFront
         * @memberOf Form
         */
        Object.defineProperty(this, "toFront", {
            get: function() {
                return function() {
                    var value = delegate.toFront();
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Shows the form as an ordinary window.
         * @method show
         * @memberOf Form
         */
        Object.defineProperty(this, "show", {
            get: function() {
                return function() {
                    var value = delegate.show();
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Minimizes this form.
         * @method minimize
         * @memberOf Form
         */
        Object.defineProperty(this, "minimize", {
            get: function() {
                return function() {
                    var value = delegate.minimize();
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Maximizes this form.
         * @method maximize
         * @memberOf Form
         */
        Object.defineProperty(this, "maximize", {
            get: function() {
                return function() {
                    var value = delegate.maximize();
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Restores this form state.
         * @method restore
         * @memberOf Form
         */
        Object.defineProperty(this, "restore", {
            get: function() {
                return function() {
                    var value = delegate.restore();
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Shows the form as an internal window in a desktop.
         * @param desktop the parent desktop object
         * @method showInternalFrame
         * @memberOf Form
         */
        Object.defineProperty(this, "showInternalFrame", {
            get: function() {
                return function(desktop) {
                    var value = delegate.showInternalFrame(P.boxAsJava(desktop));
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();