/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.metadata.Parameter";
    var javaClass = Java.type(className);
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
        /**
         * Determines if field is nullable.
         */
        this.nullable = true;
        Object.defineProperty(this, "nullable", {
            get: function() {
                var value = delegate.nullable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nullable = B.boxAsJava(aValue);
            }
        });

        /**
         * Indicates that this field is a foreign key to another table or it is a self-reference key.
         */
        this.fk = true;
        Object.defineProperty(this, "fk", {
            get: function() {
                var value = delegate.fk;
                return B.boxAsJs(value);
            }
        });

        /**
         * The default value of the parameter.
         */
        this.defaultValue = new Object();
        Object.defineProperty(this, "defaultValue", {
            get: function() {
                var value = delegate.defaultValue;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.defaultValue = B.boxAsJava(aValue);
            }
        });

        /**
         * The description of the field.
         */
        this.description = '';
        Object.defineProperty(this, "description", {
            get: function() {
                var value = delegate.description;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.description = B.boxAsJava(aValue);
            }
        });

        /**
         * The field's type information.
         */
        this.type = '';
        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.type = B.boxAsJava(aValue);
            }
        });

        /**
         * This field table's name.
         */
        this.tableName = '';
        Object.defineProperty(this, "tableName", {
            get: function() {
                var value = delegate.tableName;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.tableName = B.boxAsJava(aValue);
            }
        });

        /**
         * Parameter's mode (in, out, in/out).
         */
        this.mode = 0;
        Object.defineProperty(this, "mode", {
            get: function() {
                var value = delegate.mode;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.mode = B.boxAsJava(aValue);
            }
        });

        /**
         * The original name of the field.
         * In queries, such as select t1.f1 as f11, t2.f1 as f21 to preserve output fields' names unique,
         * but be able to generate right update sql clauses for multiple tables.
         */
        this.originalName = '';
        Object.defineProperty(this, "originalName", {
            get: function() {
                var value = delegate.originalName;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.originalName = B.boxAsJava(aValue);
            }
        });

        /**
         * The value of the parameter.
         */
        this.value = new Object();
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.jsValue;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.jsValue = B.boxAsJava(aValue);
            }
        });

        /**
         * Determines if this field is readonly.
         */
        this.readonly = true;
        Object.defineProperty(this, "readonly", {
            get: function() {
                var value = delegate.readonly;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.readonly = B.boxAsJava(aValue);
            }
        });

        /**
         * The name of the field.
         */
        this.name = '';
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = B.boxAsJava(aValue);
            }
        });

        /**
         * Determines if parameter was modified.
         */
        this.modified = true;
        Object.defineProperty(this, "modified", {
            get: function() {
                var value = delegate.modified;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.modified = B.boxAsJava(aValue);
            }
        });

        /**
         * Determines that this field is a primary key.
         */
        this.pk = true;
        Object.defineProperty(this, "pk", {
            get: function() {
                var value = delegate.pk;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.pk = B.boxAsJava(aValue);
            }
        });

    }
    /**
     * Assigning method of field/parameter information using specified source.
     * @params sourceField
     * @method assignFrom
     * @memberOf Parameter
     */
    Parameter.prototype.assignFrom = function(sourceField) {
        var delegate = this.unwrap();
        var value = delegate.assignFrom(B.boxAsJava(sourceField));
        return B.boxAsJs(value);
    };


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Parameter(aDelegate);
    });
    return Parameter;
});