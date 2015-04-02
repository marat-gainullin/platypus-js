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
            value: function() {
                return delegate;
            }
        });
        if(P.Parameter.superclass)
            P.Parameter.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "typeInfo", {
            get: function() {
                var value = delegate.typeInfo;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.typeInfo = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * The field's type information.
             * @property typeInfo
             * @memberOf Parameter
             */
            P.Parameter.prototype.typeInfo = {};
        }
        Object.defineProperty(this, "nullable", {
            get: function() {
                var value = delegate.nullable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nullable = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * Determines if field is nullable.
             * @property nullable
             * @memberOf Parameter
             */
            P.Parameter.prototype.nullable = true;
        }
        Object.defineProperty(this, "fk", {
            get: function() {
                var value = delegate.fk;
                return P.boxAsJs(value);
            }
        });
        if(!P.Parameter){
            /**
             * Indicates that this field is a foreign key to another table or it is a self-reference key.
             * @property fk
             * @memberOf Parameter
             */
            P.Parameter.prototype.fk = true;
        }
        Object.defineProperty(this, "defaultValue", {
            get: function() {
                var value = delegate.defaultValue;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.defaultValue = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * The default value of the parameter.
             * @property defaultValue
             * @memberOf Parameter
             */
            P.Parameter.prototype.defaultValue = {};
        }
        Object.defineProperty(this, "precision", {
            get: function() {
                var value = delegate.precision;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.precision = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * The precision of the field.
             * @property precision
             * @memberOf Parameter
             */
            P.Parameter.prototype.precision = 0;
        }
        Object.defineProperty(this, "scale", {
            get: function() {
                var value = delegate.scale;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.scale = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * The scale of the field.
             * @property scale
             * @memberOf Parameter
             */
            P.Parameter.prototype.scale = 0;
        }
        Object.defineProperty(this, "description", {
            get: function() {
                var value = delegate.description;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.description = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * The description of the field.
             * @property description
             * @memberOf Parameter
             */
            P.Parameter.prototype.description = '';
        }
        Object.defineProperty(this, "signed", {
            get: function() {
                var value = delegate.signed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.signed = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * Determines if the field is signed.
             * @property signed
             * @memberOf Parameter
             */
            P.Parameter.prototype.signed = true;
        }
        Object.defineProperty(this, "schemaName", {
            get: function() {
                var value = delegate.schemaName;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.schemaName = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * This field schema name.
             * @property schemaName
             * @memberOf Parameter
             */
            P.Parameter.prototype.schemaName = '';
        }
        Object.defineProperty(this, "tableName", {
            get: function() {
                var value = delegate.tableName;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.tableName = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * This field table's name.
             * @property tableName
             * @memberOf Parameter
             */
            P.Parameter.prototype.tableName = '';
        }
        Object.defineProperty(this, "mode", {
            get: function() {
                var value = delegate.mode;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.mode = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * Parameter's mode (in, out, in/out).
             * @property mode
             * @memberOf Parameter
             */
            P.Parameter.prototype.mode = 0;
        }
        Object.defineProperty(this, "originalName", {
            get: function() {
                var value = delegate.originalName;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.originalName = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * The original name of the field.
             * In queries, such as select t1.f1 as f11, t2.f1 as f21 to preserve output fields' names unique,
             * but be able to generate right update sql clauses for multiple tables.
             * @property originalName
             * @memberOf Parameter
             */
            P.Parameter.prototype.originalName = '';
        }
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.jsValue;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.jsValue = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * The value of the parameter.
             * @property jsValue
             * @memberOf Parameter
             */
            P.Parameter.prototype.value = {};
        }
        Object.defineProperty(this, "size", {
            get: function() {
                var value = delegate.size;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.size = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * The size of the field.
             * @property size
             * @memberOf Parameter
             */
            P.Parameter.prototype.size = 0;
        }
        Object.defineProperty(this, "readonly", {
            get: function() {
                var value = delegate.readonly;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.readonly = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * Determines if this field is readonly.
             * @property readonly
             * @memberOf Parameter
             */
            P.Parameter.prototype.readonly = true;
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * The name of the field.
             * @property name
             * @memberOf Parameter
             */
            P.Parameter.prototype.name = '';
        }
        Object.defineProperty(this, "modified", {
            get: function() {
                var value = delegate.modified;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.modified = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * Determines if parameter was modified.
             * @property modified
             * @memberOf Parameter
             */
            P.Parameter.prototype.modified = true;
        }
        Object.defineProperty(this, "pk", {
            get: function() {
                var value = delegate.pk;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.pk = P.boxAsJava(aValue);
            }
        });
        if(!P.Parameter){
            /**
             * Determines that this field is a primary key.
             * @property pk
             * @memberOf Parameter
             */
            P.Parameter.prototype.pk = true;
        }
    };
        /**
         * Assigning method of field/parameter information using specified source.
         * @params sourceField
         * @method assignFrom
         * @memberOf Parameter
         */
        P.Parameter.prototype.assignFrom = function(sourceField) {
            var delegate = this.unwrap();
            var value = delegate.assignFrom(P.boxAsJava(sourceField));
            return P.boxAsJs(value);
        };

})();