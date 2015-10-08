/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.metadata.Field";
    var javaClass = Java.type(className);
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
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.originalName = B.boxAsJava(aValue);
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

        Object.defineProperty(this, "nullable", {
            get: function() {
                var value = delegate.nullable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nullable = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "fk", {
            get: function() {
                var value = delegate.fk;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "description", {
            get: function() {
                var value = delegate.description;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.description = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "pk", {
            get: function() {
                var value = delegate.pk;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.pk = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.type = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "tableName", {
            get: function() {
                var value = delegate.tableName;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.tableName = B.boxAsJava(aValue);
            }
        });

    };

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Field(aDelegate);
    });
    return Field;
});