(function() {
    var javaClass = Java.type("com.eas.client.forms.Form");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Form(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Form Form
     */
    P.Form = function Form() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(Form.superclass)
            Form.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Form", {value: Form});
    Object.defineProperty(Form.prototype, "onWindowOpened", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onWindowOpened;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onWindowOpened = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The handler function for the form's <i>before open</i> event.
         * @property onWindowOpened
         * @memberOf Form
         */
        P.Form.prototype.onWindowOpened = {};
    }
    Object.defineProperty(Form.prototype, "alwaysOnTop", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.alwaysOnTop;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.alwaysOnTop = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * Determines whether this window should always be above other windows.
         * @property alwaysOnTop
         * @memberOf Form
         */
        P.Form.prototype.alwaysOnTop = true;
    }
    Object.defineProperty(Form.prototype, "icon", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.icon;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.icon = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The form's icon.
         * @property icon
         * @memberOf Form
         */
        P.Form.prototype.icon = {};
    }
    Object.defineProperty(Form.prototype, "title", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.title;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.title = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The form's title text.
         * @property title
         * @memberOf Form
         */
        P.Form.prototype.title = '';
    }
    Object.defineProperty(Form.prototype, "minimized", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.minimized;
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * <code>true</code> if this form is minimized.
         * @property minimized
         * @memberOf Form
         */
        P.Form.prototype.minimized = true;
    }
    Object.defineProperty(Form.prototype, "onWindowMinimized", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onWindowMinimized;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onWindowMinimized = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The handler function for the form's <i>after minimize</i> event.
         * @property onWindowMinimized
         * @memberOf Form
         */
        P.Form.prototype.onWindowMinimized = {};
    }
    Object.defineProperty(Form.prototype, "top", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.top;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.top = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The distance for this form to the parent container's top side.
         * @property top
         * @memberOf Form
         */
        P.Form.prototype.top = 0;
    }
    Object.defineProperty(Form.prototype, "onWindowDeactivated", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onWindowDeactivated;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onWindowDeactivated = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The handler function for the form's <i>after deactivate</i> event.
         * @property onWindowDeactivated
         * @memberOf Form
         */
        P.Form.prototype.onWindowDeactivated = {};
    }
    Object.defineProperty(Form.prototype, "locationByPlatform", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.locationByPlatform;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.locationByPlatform = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * Determines whether this form should appear at the default location
         * for the native windowing system or at the current location.
         * @property locationByPlatform
         * @memberOf Form
         */
        P.Form.prototype.locationByPlatform = true;
    }
    Object.defineProperty(Form.prototype, "minimizable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.minimizable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.minimizable = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * <code>true</code> if this form minimizable.
         * @property minimizable
         * @memberOf Form
         */
        P.Form.prototype.minimizable = true;
    }
    Object.defineProperty(Form.prototype, "onWindowActivated", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onWindowActivated;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onWindowActivated = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The handler function for the form's <i>after activate</i> event.
         * @property onWindowActivated
         * @memberOf Form
         */
        P.Form.prototype.onWindowActivated = {};
    }
    Object.defineProperty(Form.prototype, "height", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.height;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.height = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The form's height.
         * @property height
         * @memberOf Form
         */
        P.Form.prototype.height = 0;
    }
    Object.defineProperty(Form.prototype, "visible", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.visible;
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * Checks if this form is visible.
         * @property visible
         * @memberOf Form
         */
        P.Form.prototype.visible = true;
    }
    Object.defineProperty(Form.prototype, "onWindowMaximized", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onWindowMaximized;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onWindowMaximized = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The handler function for the form's <i>after maximize</i> event.
         * @property onWindowMaximized
         * @memberOf Form
         */
        P.Form.prototype.onWindowMaximized = {};
    }
    Object.defineProperty(Form.prototype, "resizable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.resizable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.resizable = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * <code>true</code> if this form resizable.
         * @property resizable
         * @memberOf Form
         */
        P.Form.prototype.resizable = true;
    }
    Object.defineProperty(Form.prototype, "onWindowRestored", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onWindowRestored;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onWindowRestored = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The handler function for the form's <i>after restore</i> event.
         * @property onWindowRestored
         * @memberOf Form
         */
        P.Form.prototype.onWindowRestored = {};
    }
    Object.defineProperty(Form.prototype, "formKey", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.formKey;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.formKey = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The form key. Used to identify a form instance. Initialy set to the form's application element name.
         * @property formKey
         * @memberOf Form
         */
        P.Form.prototype.formKey = '';
    }
    Object.defineProperty(Form.prototype, "maximized", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.maximized;
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * <code>true</code> if this form is maximized.
         * @property maximized
         * @memberOf Form
         */
        P.Form.prototype.maximized = true;
    }
    Object.defineProperty(Form.prototype, "onWindowClosed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onWindowClosed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onWindowClosed = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The handler function for the form's <i>after close</i> event.
         * @property onWindowClosed
         * @memberOf Form
         */
        P.Form.prototype.onWindowClosed = {};
    }
    Object.defineProperty(Form.prototype, "maximizable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.maximizable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.maximizable = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * <code>true</code> if this form maximizable.
         * @property maximizable
         * @memberOf Form
         */
        P.Form.prototype.maximizable = true;
    }
    Object.defineProperty(Form.prototype, "undecorated", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.undecorated;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.undecorated = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * <code>true</code> if no decoration to be enabled for this form.
         * @property undecorated
         * @memberOf Form
         */
        P.Form.prototype.undecorated = true;
    }
    Object.defineProperty(Form.prototype, "left", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.left;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.left = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The distance for this form to the parent container's left side.
         * @property left
         * @memberOf Form
         */
        P.Form.prototype.left = 0;
    }
    Object.defineProperty(Form.prototype, "width", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.width;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.width = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The form's width.
         * @property width
         * @memberOf Form
         */
        P.Form.prototype.width = 0;
    }
    Object.defineProperty(Form.prototype, "onWindowClosing", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onWindowClosing;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onWindowClosing = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The handler function for the form's <i>before close</i> event.
         * @property onWindowClosing
         * @memberOf Form
         */
        P.Form.prototype.onWindowClosing = {};
    }
    Object.defineProperty(Form.prototype, "opacity", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.opacity;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.opacity = P.boxAsJava(aValue);
        }
    });
    if(!Form){
        /**
         * The opacity of the form.
         * @property opacity
         * @memberOf Form
         */
        P.Form.prototype.opacity = 0;
    }
    Object.defineProperty(Form.prototype, "close", {
        value: function(obj) {
            var delegate = this.unwrap();
            var value = delegate.close(P.boxAsJava(obj));
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * Closes this form.
         * @param obj an object to be passed as a result of a selection into <code>showModal</code> callback handler function.
         * @method close
         * @memberOf Form
         */
        P.Form.prototype.close = function(obj){};
    }
    Object.defineProperty(Form.prototype, "show", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.show();
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * Shows the form as an ordinary window.
         * @method show
         * @memberOf Form
         */
        P.Form.prototype.show = function(){};
    }
    Object.defineProperty(Form.prototype, "toFront", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.toFront();
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * Moves form to the front position.
         * @method toFront
         * @memberOf Form
         */
        P.Form.prototype.toFront = function(){};
    }
    Object.defineProperty(Form.prototype, "showModal", {
        value: function(callback) {
            var delegate = this.unwrap();
            var value = delegate.showModal(P.boxAsJava(callback));
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * Shows the form as a dialog (modal window).
         * @param callback a callback handler function
         * @method showModal
         * @memberOf Form
         */
        P.Form.prototype.showModal = function(callback){};
    }
    Object.defineProperty(Form.prototype, "maximize", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.maximize();
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * Maximizes this form.
         * @method maximize
         * @memberOf Form
         */
        P.Form.prototype.maximize = function(){};
    }
    Object.defineProperty(Form.prototype, "minimize", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.minimize();
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * Minimizes this form.
         * @method minimize
         * @memberOf Form
         */
        P.Form.prototype.minimize = function(){};
    }
    Object.defineProperty(Form.prototype, "restore", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.restore();
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * Restores this form state.
         * @method restore
         * @memberOf Form
         */
        P.Form.prototype.restore = function(){};
    }
    Object.defineProperty(Form.prototype, "showInternalFrame", {
        value: function(desktop) {
            var delegate = this.unwrap();
            var value = delegate.showInternalFrame(P.boxAsJava(desktop));
            return P.boxAsJs(value);
        }
    });
    if(!Form){
        /**
         * Shows the form as an internal window in a desktop.
         * @param desktop the parent desktop object
         * @method showInternalFrame
         * @memberOf Form
         */
        P.Form.prototype.showInternalFrame = function(desktop){};
    }
})();