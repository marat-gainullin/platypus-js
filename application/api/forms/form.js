(function() {
    var javaClass = Java.type("com.eas.client.forms.Form");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Form(null, null, aDelegate);
    });
    
    /**
     * Creates a form.
     * @param aView Container instance to be used as view of created form. Optional. If it is omitted P.AnchorsPane will be created and used as view.
     * @param aFormKey Form instance key for open windows accounting. Optional.
     * @constructor Form Form
     */
    P.Form = function (aView, aFormKey) {
        var maxArgs = 2;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 2 ? new javaClass(P.boxAsJava(aView), P.boxAsJava(aFormKey))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(aView))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.Form.superclass)
            P.Form.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "onWindowOpened", {
            get: function() {
                var value = delegate.onWindowOpened;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowOpened = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The handler function for the form's <i>before open</i> event.
             * @property onWindowOpened
             * @memberOf Form
             */
            P.Form.prototype.onWindowOpened = {};
        }
        Object.defineProperty(this, "alwaysOnTop", {
            get: function() {
                var value = delegate.alwaysOnTop;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.alwaysOnTop = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * Determines whether this window should always be above other windows.
             * @property alwaysOnTop
             * @memberOf Form
             */
            P.Form.prototype.alwaysOnTop = true;
        }
        Object.defineProperty(this, "icon", {
            get: function() {
                var value = delegate.icon;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.icon = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The form's icon.
             * @property icon
             * @memberOf Form
             */
            P.Form.prototype.icon = {};
        }
        Object.defineProperty(this, "title", {
            get: function() {
                var value = delegate.title;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.title = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The form's title text.
             * @property title
             * @memberOf Form
             */
            P.Form.prototype.title = '';
        }
        Object.defineProperty(this, "minimized", {
            get: function() {
                var value = delegate.minimized;
                return P.boxAsJs(value);
            }
        });
        if(!P.Form){
            /**
             * <code>true</code> if this form is minimized.
             * @property minimized
             * @memberOf Form
             */
            P.Form.prototype.minimized = true;
        }
        Object.defineProperty(this, "onWindowMinimized", {
            get: function() {
                var value = delegate.onWindowMinimized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowMinimized = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The handler function for the form's <i>after minimize</i> event.
             * @property onWindowMinimized
             * @memberOf Form
             */
            P.Form.prototype.onWindowMinimized = {};
        }
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The distance for this form to the parent container's top side.
             * @property top
             * @memberOf Form
             */
            P.Form.prototype.top = 0;
        }
        Object.defineProperty(this, "onWindowDeactivated", {
            get: function() {
                var value = delegate.onWindowDeactivated;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowDeactivated = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The handler function for the form's <i>after deactivate</i> event.
             * @property onWindowDeactivated
             * @memberOf Form
             */
            P.Form.prototype.onWindowDeactivated = {};
        }
        Object.defineProperty(this, "locationByPlatform", {
            get: function() {
                var value = delegate.locationByPlatform;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.locationByPlatform = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * Determines whether this form should appear at the default location
             * for the native windowing system or at the current location.
             * @property locationByPlatform
             * @memberOf Form
             */
            P.Form.prototype.locationByPlatform = true;
        }
        Object.defineProperty(this, "minimizable", {
            get: function() {
                var value = delegate.minimizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.minimizable = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * <code>true</code> if this form minimizable.
             * @property minimizable
             * @memberOf Form
             */
            P.Form.prototype.minimizable = true;
        }
        Object.defineProperty(this, "onWindowActivated", {
            get: function() {
                var value = delegate.onWindowActivated;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowActivated = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The handler function for the form's <i>after activate</i> event.
             * @property onWindowActivated
             * @memberOf Form
             */
            P.Form.prototype.onWindowActivated = {};
        }
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The form's height.
             * @property height
             * @memberOf Form
             */
            P.Form.prototype.height = 0;
        }
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            }
        });
        if(!P.Form){
            /**
             * Checks if this form is visible.
             * @property visible
             * @memberOf Form
             */
            P.Form.prototype.visible = true;
        }
        Object.defineProperty(this, "onWindowMaximized", {
            get: function() {
                var value = delegate.onWindowMaximized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowMaximized = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The handler function for the form's <i>after maximize</i> event.
             * @property onWindowMaximized
             * @memberOf Form
             */
            P.Form.prototype.onWindowMaximized = {};
        }
        Object.defineProperty(this, "resizable", {
            get: function() {
                var value = delegate.resizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.resizable = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * <code>true</code> if this form resizable.
             * @property resizable
             * @memberOf Form
             */
            P.Form.prototype.resizable = true;
        }
        Object.defineProperty(this, "formKey", {
            get: function() {
                var value = delegate.formKey;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.formKey = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The form key. Used to identify a form instance. Initialy set to the form's application element name.
             * @property formKey
             * @memberOf Form
             */
            P.Form.prototype.formKey = '';
        }
        Object.defineProperty(this, "onWindowRestored", {
            get: function() {
                var value = delegate.onWindowRestored;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowRestored = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The handler function for the form's <i>after restore</i> event.
             * @property onWindowRestored
             * @memberOf Form
             */
            P.Form.prototype.onWindowRestored = {};
        }
        Object.defineProperty(this, "maximized", {
            get: function() {
                var value = delegate.maximized;
                return P.boxAsJs(value);
            }
        });
        if(!P.Form){
            /**
             * <code>true</code> if this form is maximized.
             * @property maximized
             * @memberOf Form
             */
            P.Form.prototype.maximized = true;
        }
        Object.defineProperty(this, "onWindowClosed", {
            get: function() {
                var value = delegate.onWindowClosed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowClosed = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The handler function for the form's <i>after close</i> event.
             * @property onWindowClosed
             * @memberOf Form
             */
            P.Form.prototype.onWindowClosed = {};
        }
        Object.defineProperty(this, "maximizable", {
            get: function() {
                var value = delegate.maximizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.maximizable = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * <code>true</code> if this form maximizable.
             * @property maximizable
             * @memberOf Form
             */
            P.Form.prototype.maximizable = true;
        }
        Object.defineProperty(this, "undecorated", {
            get: function() {
                var value = delegate.undecorated;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.undecorated = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * <code>true</code> if no decoration to be enabled for this form.
             * @property undecorated
             * @memberOf Form
             */
            P.Form.prototype.undecorated = true;
        }
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The distance for this form to the parent container's left side.
             * @property left
             * @memberOf Form
             */
            P.Form.prototype.left = 0;
        }
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The form's width.
             * @property width
             * @memberOf Form
             */
            P.Form.prototype.width = 0;
        }
        Object.defineProperty(this, "onWindowClosing", {
            get: function() {
                var value = delegate.onWindowClosing;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onWindowClosing = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The handler function for the form's <i>before close</i> event.
             * @property onWindowClosing
             * @memberOf Form
             */
            P.Form.prototype.onWindowClosing = {};
        }
        Object.defineProperty(this, "opacity", {
            get: function() {
                var value = delegate.opacity;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opacity = P.boxAsJava(aValue);
            }
        });
        if(!P.Form){
            /**
             * The opacity of the form.
             * @property opacity
             * @memberOf Form
             */
            P.Form.prototype.opacity = 0;
        }
    };
        /**
         * Closes this form.
         * @param obj an object to be passed as a result of a selection into <code>showModal</code> callback handler function.
         * @method close
         * @memberOf Form
         */
        P.Form.prototype.close = function(obj) {
            var delegate = this.unwrap();
            var value = delegate.close(P.boxAsJava(obj));
            return P.boxAsJs(value);
        };

        /**
         * Shows the form as a dialog (modal window).
         * @param callback a callback handler function
         * @method showModal
         * @memberOf Form
         */
        P.Form.prototype.showModal = function(callback) {
            var delegate = this.unwrap();
            var value = delegate.showModal(P.boxAsJava(callback));
            return P.boxAsJs(value);
        };

        /**
         * Moves form to the front position.
         * @method toFront
         * @memberOf Form
         */
        P.Form.prototype.toFront = function() {
            var delegate = this.unwrap();
            var value = delegate.toFront();
            return P.boxAsJs(value);
        };

        /**
         * Shows the form as an ordinary window.
         * @method show
         * @memberOf Form
         */
        P.Form.prototype.show = function() {
            var delegate = this.unwrap();
            var value = delegate.show();
            return P.boxAsJs(value);
        };

        /**
         * Minimizes this form.
         * @method minimize
         * @memberOf Form
         */
        P.Form.prototype.minimize = function() {
            var delegate = this.unwrap();
            var value = delegate.minimize();
            return P.boxAsJs(value);
        };

        /**
         * Restores this form state.
         * @method restore
         * @memberOf Form
         */
        P.Form.prototype.restore = function() {
            var delegate = this.unwrap();
            var value = delegate.restore();
            return P.boxAsJs(value);
        };

        /**
         * Maximizes this form.
         * @method maximize
         * @memberOf Form
         */
        P.Form.prototype.maximize = function() {
            var delegate = this.unwrap();
            var value = delegate.maximize();
            return P.boxAsJs(value);
        };

        /**
         * Shows the form as an internal window in a desktop.
         * @param desktop the parent desktop object
         * @method showInternalFrame
         * @memberOf Form
         */
        P.Form.prototype.showInternalFrame = function(desktop) {
            var delegate = this.unwrap();
            var value = delegate.showInternalFrame(P.boxAsJava(desktop));
            return P.boxAsJs(value);
        };

})();