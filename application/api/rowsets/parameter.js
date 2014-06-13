(function() {
    var javaClass = Java.type("com.bearsoft.rowset.metadata.Parameter");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Parameter(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Parameter Parameter
     */
    P.Parameter = function () {

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
         * The field's type information.
         * @property typeInfo
         * @memberOf Parameter
         */
        Object.defineProperty(this, "typeInfo", {
            get: function() {
                var value = delegate.typeInfo;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.typeInfo = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines if field is nullable.
         * @property nullable
         * @memberOf Parameter
         */
        Object.defineProperty(this, "nullable", {
            get: function() {
                var value = delegate.nullable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nullable = P.boxAsJava(aValue);
            }
        });

        /**
         * Indicates that this field is a foreign key to another table or it is a self-reference key.
         * @property fk
         * @memberOf Parameter
         */
        Object.defineProperty(this, "fk", {
            get: function() {
                var value = delegate.fk;
                return P.boxAsJs(value);
            }
        });

        /**
         * The default value of the parameter.
         * @property defaultValue
         * @memberOf Parameter
         */
        Object.defineProperty(this, "defaultValue", {
            get: function() {
                var value = delegate.defaultValue;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.defaultValue = P.boxAsJava(aValue);
            }
        });

        /**
         * The precision of the field.
         * @property precision
         * @memberOf Parameter
         */
        Object.defineProperty(this, "precision", {
            get: function() {
                var value = delegate.precision;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.precision = P.boxAsJava(aValue);
            }
        });

        /**
         * The scale of the field.
         * @property scale
         * @memberOf Parameter
         */
        Object.defineProperty(this, "scale", {
            get: function() {
                var value = delegate.scale;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.scale = P.boxAsJava(aValue);
            }
        });

        /**
         * The description of the field.
         * @property description
         * @memberOf Parameter
         */
        Object.defineProperty(this, "description", {
            get: function() {
                var value = delegate.description;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.description = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines if the field is signed.
         * @property signed
         * @memberOf Parameter
         */
        Object.defineProperty(this, "signed", {
            get: function() {
                var value = delegate.signed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.signed = P.boxAsJava(aValue);
            }
        });

        /**
         * This field schema name.
         * @property schemaName
         * @memberOf Parameter
         */
        Object.defineProperty(this, "schemaName", {
            get: function() {
                var value = delegate.schemaName;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.schemaName = P.boxAsJava(aValue);
            }
        });

        /**
         * This field table's name.
         * @property tableName
         * @memberOf Parameter
         */
        Object.defineProperty(this, "tableName", {
            get: function() {
                var value = delegate.tableName;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.tableName = P.boxAsJava(aValue);
            }
        });

        /**
         * Parameter's mode (in, out, in/out).
         * @property mode
         * @memberOf Parameter
         */
        Object.defineProperty(this, "mode", {
            get: function() {
                var value = delegate.mode;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.mode = P.boxAsJava(aValue);
            }
        });

        /**
         * The original name of the field.
         * In queries, such as select t1.f1 as f11, t2.f1 as f21 to preserve output fields' names unique,
         * but be able to generate right update sql clauses for multiple tables.
         * @property originalName
         * @memberOf Parameter
         */
        Object.defineProperty(this, "originalName", {
            get: function() {
                var value = delegate.originalName;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.originalName = P.boxAsJava(aValue);
            }
        });

        /**
         * The size of the field.
         * @property size
         * @memberOf Parameter
         */
        Object.defineProperty(this, "size", {
            get: function() {
                var value = delegate.size;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.size = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines if this field is readonly.
         * @property readonly
         * @memberOf Parameter
         */
        Object.defineProperty(this, "readonly", {
            get: function() {
                var value = delegate.readonly;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.readonly = P.boxAsJava(aValue);
            }
        });

        /**
         * The name of the field.
         * @property name
         * @memberOf Parameter
         */
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines if parameter was modified.
         * @property modified
         * @memberOf Parameter
         */
        Object.defineProperty(this, "modified", {
            get: function() {
                var value = delegate.modified;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.modified = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines that this field is a primary key.
         * @property pk
         * @memberOf Parameter
         */
        Object.defineProperty(this, "pk", {
            get: function() {
                var value = delegate.pk;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.pk = P.boxAsJava(aValue);
            }
        });

        /**
         * The value of the parameter.
         * @property value
         * @memberOf Parameter
         */
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.value;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.value = P.boxAsJava(aValue);
            }
        });

        /**
         * Assigning method of field/parameter information using specified source.
         * @method assignFrom
         * @memberOf Parameter
         */
        Object.defineProperty(this, "assignFrom", {
            get: function() {
                return function(arg0) {
                    var value = delegate.assignFrom(P.boxAsJava(arg0));
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();