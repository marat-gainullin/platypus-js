(function() {
    var javaClass = Java.type("com.bearsoft.rowset.metadata.Field");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Field(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Field Field
     */
    P.Field = function () {

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
         * @memberOf Field
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
         * @memberOf Field
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
         * @memberOf Field
         */
        Object.defineProperty(this, "fk", {
            get: function() {
                var value = delegate.fk;
                return P.boxAsJs(value);
            }
        });

        /**
         * The precision of the field.
         * @property precision
         * @memberOf Field
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
         * @memberOf Field
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
         * @memberOf Field
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
         * @memberOf Field
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
         * @memberOf Field
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
         * @memberOf Field
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
         * The original name of the field.
         * In queries, such as select t1.f1 as f11, t2.f1 as f21 to preserve output fields' names unique,
         * but be able to generate right update sql clauses for multiple tables.
         * @property originalName
         * @memberOf Field
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
         * @memberOf Field
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
         * @memberOf Field
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
         * @memberOf Field
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
         * Determines that this field is a primary key.
         * @property pk
         * @memberOf Field
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


        delegate.setPublished(this);
    };
})();