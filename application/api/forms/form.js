/* global Java */

define(['boxing'], function(P) {
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
            : arguments.length === 2 ? new javaClass(P.boxAsJava(aView), P.boxAsJava(aFormKey))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(aView))
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
        Object.defineProperty(this, "onWindowOpened", {
            get: function() {
                var value = delegate.onWindowOpened;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowOpened = aValue;
            }
        });

        Object.defineProperty(this, "alwaysOnTop", {
            get: function() {
                var value = delegate.alwaysOnTop;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.alwaysOnTop = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "title", {
            get: function() {
                var value = delegate.title;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.title = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "minimized", {
            get: function() {
                var value = delegate.minimized;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "onWindowMinimized", {
            get: function() {
                var value = delegate.onWindowMinimized;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowMinimized = aValue;
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

        Object.defineProperty(this, "onWindowDeactivated", {
            get: function() {
                var value = delegate.onWindowDeactivated;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowDeactivated = aValue;
            }
        });

        Object.defineProperty(this, "locationByPlatform", {
            get: function() {
                var value = delegate.locationByPlatform;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.locationByPlatform = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "minimizable", {
            get: function() {
                var value = delegate.minimizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.minimizable = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onWindowActivated", {
            get: function() {
                var value = delegate.onWindowActivated;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowActivated = aValue;
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

        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "onWindowMaximized", {
            get: function() {
                var value = delegate.onWindowMaximized;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowMaximized = aValue;
            }
        });

        Object.defineProperty(this, "resizable", {
            get: function() {
                var value = delegate.resizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.resizable = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "formKey", {
            get: function() {
                var value = delegate.formKey;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.formKey = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onWindowRestored", {
            get: function() {
                var value = delegate.onWindowRestored;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowRestored = aValue;
            }
        });

        Object.defineProperty(this, "maximized", {
            get: function() {
                var value = delegate.maximized;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "onWindowClosed", {
            get: function() {
                var value = delegate.onWindowClosed;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowClosed = aValue;
            }
        });

        Object.defineProperty(this, "maximizable", {
            get: function() {
                var value = delegate.maximizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.maximizable = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "undecorated", {
            get: function() {
                var value = delegate.undecorated;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.undecorated = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onWindowClosing", {
            get: function() {
                var value = delegate.onWindowClosing;
                return value;
            },
            set: function(aValue) {
                delegate.onWindowClosing = aValue;
            }
        });

        Object.defineProperty(this, "opacity", {
            get: function() {
                var value = delegate.opacity;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opacity = P.boxAsJava(aValue);
            }
        });

    };
    /**
     * Closes this form.
     * @param obj an object to be passed as a result of a selection into <code>showModal</code> callback handler function.
     * @method close
     * @memberOf Form
     */
    Form.prototype.close = function(obj) {
        var delegate = this.unwrap();
        var value = delegate.close(P.boxAsJava(obj));
        return P.boxAsJs(value);
    };

    /**
     * Moves form to the front position.
     * @method toFront
     * @memberOf Form
     */
    Form.prototype.toFront = function() {
        var delegate = this.unwrap();
        var value = delegate.toFront();
        return P.boxAsJs(value);
    };

    /**
     * Shows the form as a dialog (modal window).
     * @param callback a callback handler function
     * @method showModal
     * @memberOf Form
     */
    Form.prototype.showModal = function(callback) {
        var delegate = this.unwrap();
        var value = delegate.showModal(P.boxAsJava(callback));
        return P.boxAsJs(value);
    };

    /**
     * Shows the form as an ordinary window.
     * @method show
     * @memberOf Form
     */
    Form.prototype.show = function() {
        var delegate = this.unwrap();
        var value = delegate.show();
        return P.boxAsJs(value);
    };

    /**
     * Minimizes this form.
     * @method minimize
     * @memberOf Form
     */
    Form.prototype.minimize = function() {
        var delegate = this.unwrap();
        var value = delegate.minimize();
        return P.boxAsJs(value);
    };

    /**
     * Maximizes this form.
     * @method maximize
     * @memberOf Form
     */
    Form.prototype.maximize = function() {
        var delegate = this.unwrap();
        var value = delegate.maximize();
        return P.boxAsJs(value);
    };

    /**
     * Restores this form state.
     * @method restore
     * @memberOf Form
     */
    Form.prototype.restore = function() {
        var delegate = this.unwrap();
        var value = delegate.restore();
        return P.boxAsJs(value);
    };

    /**
     * Shows the form as an internal window in a desktop.
     * @param desktop the parent desktop object
     * @method showInternalFrame
     * @memberOf Form
     */
    Form.prototype.showInternalFrame = function(desktop) {
        var delegate = this.unwrap();
        var value = delegate.showInternalFrame(P.boxAsJava(desktop));
        return P.boxAsJs(value);
    };


    var className = "com.eas.client.forms.Form";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Form(null, null, aDelegate);
    });
    return Form;
});