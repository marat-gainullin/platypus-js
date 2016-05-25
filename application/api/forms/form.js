/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.forms.Form";
    var javaClass = Java.type(className);
    /**
     * Creates a form.
     * @param aView Container instance to be used as view of created form. Optional. If it is omitted P.AnchorsPane will be created and used as view.
     * @param aFormKey Form instance key for open windows accounting. Optional.
     * @constructor Form Form
     */
    function Form(aView, aFormKey) {
        var maxArgs = 2;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 2 ? new javaClass(B.boxAsJava(aView), B.boxAsJava(aFormKey))
            : arguments.length === 1 ? new javaClass(B.boxAsJava(aView))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(Form.superclass)
            Form.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        /**
         * <code>true</code> if this form is maximizable.
         */
        this.closable = true;
        Object.defineProperty(this, "closable", {
            get: function() {
                var value = delegate.closable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.closable = B.boxAsJava(aValue);
            }
        });

        /**
         * The handler function for the form's <i>before open</i> event.
         */
        this.onWindowOpened = new Object();
        Object.defineProperty(this, "onWindowOpened", {
            get: function() {
                var value = delegate.onWindowOpened;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowOpened = aValue;
            }
        });

        /**
         * Determines whether this window should always be above other windows.
         */
        this.alwaysOnTop = true;
        Object.defineProperty(this, "alwaysOnTop", {
            get: function() {
                var value = delegate.alwaysOnTop;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.alwaysOnTop = B.boxAsJava(aValue);
            }
        });

        /**
         * The form's icon.
         */
        this.icon = new Object();
        Object.defineProperty(this, "icon", {
            get: function() {
                var value = delegate.icon;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.icon = B.boxAsJava(aValue);
            }
        });

        /**
         * The form's title text.
         */
        this.title = '';
        Object.defineProperty(this, "title", {
            get: function() {
                var value = delegate.title;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.title = B.boxAsJava(aValue);
            }
        });

        /**
         * <code>true</code> if this form is minimized.
         */
        this.minimized = true;
        Object.defineProperty(this, "minimized", {
            get: function() {
                var value = delegate.minimized;
                return B.boxAsJs(value);
            }
        });

        /**
         * The handler function for the form's <i>after minimize</i> event.
         */
        this.onWindowMinimized = new Object();
        Object.defineProperty(this, "onWindowMinimized", {
            get: function() {
                var value = delegate.onWindowMinimized;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowMinimized = aValue;
            }
        });

        /**
         * The distance for this form to the parent container's top side.
         */
        this.top = 0;
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = B.boxAsJava(aValue);
            }
        });

        /**
         * The handler function for the form's <i>after deactivate</i> event.
         */
        this.onWindowDeactivated = new Object();
        Object.defineProperty(this, "onWindowDeactivated", {
            get: function() {
                var value = delegate.onWindowDeactivated;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowDeactivated = aValue;
            }
        });

        /**
         * Determines whether this form should appear at the default location
         * for the native windowing system or at the current location.
         */
        this.locationByPlatform = true;
        Object.defineProperty(this, "locationByPlatform", {
            get: function() {
                var value = delegate.locationByPlatform;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.locationByPlatform = B.boxAsJava(aValue);
            }
        });

        /**
         * <code>true</code> if this form is minimizable.
         */
        this.minimizable = true;
        Object.defineProperty(this, "minimizable", {
            get: function() {
                var value = delegate.minimizable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.minimizable = B.boxAsJava(aValue);
            }
        });

        /**
         * The handler function for the form's <i>after activate</i> event.
         */
        this.onWindowActivated = new Object();
        Object.defineProperty(this, "onWindowActivated", {
            get: function() {
                var value = delegate.onWindowActivated;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowActivated = aValue;
            }
        });

        /**
         * The form's height.
         */
        this.height = 0;
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = B.boxAsJava(aValue);
            }
        });

        /**
         * Checks if this form is visible.
         */
        this.visible = true;
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return B.boxAsJs(value);
            }
        });

        /**
         * The handler function for the form's <i>after maximize</i> event.
         */
        this.onWindowMaximized = new Object();
        Object.defineProperty(this, "onWindowMaximized", {
            get: function() {
                var value = delegate.onWindowMaximized;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowMaximized = aValue;
            }
        });

        /**
         * <code>true</code> if this form is resizable.
         */
        this.resizable = true;
        Object.defineProperty(this, "resizable", {
            get: function() {
                var value = delegate.resizable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.resizable = B.boxAsJava(aValue);
            }
        });

        /**
         * The handler function for the form's <i>after restore</i> event.
         */
        this.onWindowRestored = new Object();
        Object.defineProperty(this, "onWindowRestored", {
            get: function() {
                var value = delegate.onWindowRestored;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowRestored = aValue;
            }
        });

        /**
         * The form key. Used to identify a form instance. Initialy set to the form's application element name.
         */
        this.formKey = '';
        Object.defineProperty(this, "formKey", {
            get: function() {
                var value = delegate.formKey;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.formKey = B.boxAsJava(aValue);
            }
        });

        /**
         * <code>true</code> if this form is maximized.
         */
        this.maximized = true;
        Object.defineProperty(this, "maximized", {
            get: function() {
                var value = delegate.maximized;
                return B.boxAsJs(value);
            }
        });

        /**
         * The handler function for the form's <i>after close</i> event.
         */
        this.onWindowClosed = new Object();
        Object.defineProperty(this, "onWindowClosed", {
            get: function() {
                var value = delegate.onWindowClosed;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowClosed = aValue;
            }
        });

        /**
         * <code>true</code> if this form is maximizable.
         */
        this.maximizable = true;
        Object.defineProperty(this, "maximizable", {
            get: function() {
                var value = delegate.maximizable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.maximizable = B.boxAsJava(aValue);
            }
        });

        /**
         * <code>true</code> if no decoration to be enabled for this form.
         */
        this.undecorated = true;
        Object.defineProperty(this, "undecorated", {
            get: function() {
                var value = delegate.undecorated;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.undecorated = B.boxAsJava(aValue);
            }
        });

        /**
         * The distance for this form to the parent container's left side.
         */
        this.left = 0;
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = B.boxAsJava(aValue);
            }
        });

        /**
         * The form's width.
         */
        this.width = 0;
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = B.boxAsJava(aValue);
            }
        });

        /**
         * The handler function for the form's <i>before close</i> event.
         */
        this.onWindowClosing = new Object();
        Object.defineProperty(this, "onWindowClosing", {
            get: function() {
                var value = delegate.onWindowClosing;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowClosing = aValue;
            }
        });

        /**
         * The opacity of the form.
         */
        this.opacity = 0;
        Object.defineProperty(this, "opacity", {
            get: function() {
                var value = delegate.opacity;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opacity = B.boxAsJava(aValue);
            }
        });

    }
    /**
     * Closes this form.
     * @param obj an object to be passed as a result of a selection into <code>showModal</code> callback handler function.
     * @method close
     * @memberOf Form
     */
    Form.prototype.close = function(obj) {
        var delegate = this.unwrap();
        var value = delegate.close(B.boxAsJava(obj));
        return B.boxAsJs(value);
    };

    /**
     * Shows the form as an ordinary window.
     * @method show
     * @memberOf Form
     */
    Form.prototype.show = function() {
        var delegate = this.unwrap();
        var value = delegate.show();
        return B.boxAsJs(value);
    };

    /**
     * Moves form to the front position.
     * @method toFront
     * @memberOf Form
     */
    Form.prototype.toFront = function() {
        var delegate = this.unwrap();
        var value = delegate.toFront();
        return B.boxAsJs(value);
    };

    /**
     * Maximizes this form.
     * @method maximize
     * @memberOf Form
     */
    Form.prototype.maximize = function() {
        var delegate = this.unwrap();
        var value = delegate.maximize();
        return B.boxAsJs(value);
    };

    /**
     * Minimizes this form.
     * @method minimize
     * @memberOf Form
     */
    Form.prototype.minimize = function() {
        var delegate = this.unwrap();
        var value = delegate.minimize();
        return B.boxAsJs(value);
    };

    /**
     * Restores this form state.
     * @method restore
     * @memberOf Form
     */
    Form.prototype.restore = function() {
        var delegate = this.unwrap();
        var value = delegate.restore();
        return B.boxAsJs(value);
    };

    /**
     * Shows the form as a dialog (modal window).
     * @param callback a callback handler function
     * @method showModal
     * @memberOf Form
     */
    Form.prototype.showModal = function(callback) {
        var delegate = this.unwrap();
        var value = delegate.showModal(B.boxAsJava(callback));
        return B.boxAsJs(value);
    };

    /**
     * Shows the form as an internal window in a desktop.
     * @param desktop the parent desktop object
     * @method showInternalFrame
     * @memberOf Form
     */
    Form.prototype.showInternalFrame = function(desktop) {
        var delegate = this.unwrap();
        var value = delegate.showInternalFrame(B.boxAsJava(desktop));
        return B.boxAsJs(value);
    };

    var FormClass = Java.type("com.eas.client.forms.Form");
    Object.defineProperty(Form, 'shown', {
        get: function () {
            var nativeArray = FormClass.getShownForms();
            var res = [];
            for (var i = 0; i < nativeArray.length; i++)
                res[res.length] = nativeArray[i].getPublished();
            return res;
        }
    });

    Object.defineProperty(Form, 'getShownForm', {
        value: function (aName) {
            var shownForm = FormClass.getShownForm(aName);
            return shownForm !== null ? shownForm.getPublished() : null;
        }
    });

    Object.defineProperty(Form, 'onChange', {
        get: function () {
            return FormClass.getOnChange();
        },
        set: function (aValue) {
            FormClass.setOnChange(aValue);
        }
    });


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Form(null, null, aDelegate);
    });
    return Form;
});