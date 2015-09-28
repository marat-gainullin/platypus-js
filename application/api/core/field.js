/* global Java */

define(['boxing'], function(P) {
    /**
     * Generated constructor.
     * @constructor Field Field
     */
    function Field() {
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
        if(Field.superclass)
            Field.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "originalName", {
            get: function() {
                var value = delegate.originalName;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.originalName = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "pk", {
            get: function() {
                var value = delegate.pk;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.pk = P.boxAsJava(aValue);
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

    };

    var className = "com.eas.client.metadata.Field";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Field(aDelegate);
    });
    return Field;
});