/* global Java */

define(['boxing'], function(P) {
    /**
     * Generated constructor.
     * @constructor Parameter Parameter
     */
    function Parameter() {
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
        if(Parameter.superclass)
            Parameter.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "nullable", {
            get: function() {
                var value = delegate.nullable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nullable = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "fk", {
            get: function() {
                var value = delegate.fk;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "defaultValue", {
            get: function() {
                var value = delegate.defaultValue;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.defaultValue = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "description", {
            get: function() {
                var value = delegate.description;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.description = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.type = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "tableName", {
            get: function() {
                var value = delegate.tableName;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.tableName = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "mode", {
            get: function() {
                var value = delegate.mode;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.mode = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "originalName", {
            get: function() {
                var value = delegate.originalName;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.originalName = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.jsValue;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.jsValue = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "readonly", {
            get: function() {
                var value = delegate.readonly;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.readonly = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "modified", {
            get: function() {
                var value = delegate.modified;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.modified = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "pk", {
            get: function() {
                var value = delegate.pk;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.pk = P.boxAsJava(aValue);
            }
        });

    };
    /**
     * Assigning method of field/parameter information using specified source.
     * @params sourceField
     * @method assignFrom
     * @memberOf Parameter
     */
    Parameter.prototype.assignFrom = function(sourceField) {
        var delegate = this.unwrap();
        var value = delegate.assignFrom(P.boxAsJava(sourceField));
        return P.boxAsJs(value);
    };


    var className = "com.eas.client.metadata.Parameter";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Parameter(aDelegate);
    });
    return Parameter;
});