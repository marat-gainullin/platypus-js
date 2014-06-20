(function() {
    var javaClass = Java.type("com.bearsoft.rowset.metadata.Parameter");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Parameter(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Parameter Parameter
     */
    P.Parameter = function Parameter() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(Parameter.superclass)
            Parameter.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Parameter", {value: Parameter});
    Object.defineProperty(Parameter.prototype, "typeInfo", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.typeInfo;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.typeInfo = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * The field's type information.
         * @property typeInfo
         * @memberOf Parameter
         */
        P.Parameter.prototype.typeInfo = {};
    }
    Object.defineProperty(Parameter.prototype, "nullable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.nullable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.nullable = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * Determines if field is nullable.
         * @property nullable
         * @memberOf Parameter
         */
        P.Parameter.prototype.nullable = true;
    }
    Object.defineProperty(Parameter.prototype, "fk", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.fk;
            return P.boxAsJs(value);
        }
    });
    if(!Parameter){
        /**
         * Indicates that this field is a foreign key to another table or it is a self-reference key.
         * @property fk
         * @memberOf Parameter
         */
        P.Parameter.prototype.fk = true;
    }
    Object.defineProperty(Parameter.prototype, "defaultValue", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.defaultValue;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.defaultValue = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * The default value of the parameter.
         * @property defaultValue
         * @memberOf Parameter
         */
        P.Parameter.prototype.defaultValue = {};
    }
    Object.defineProperty(Parameter.prototype, "precision", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.precision;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.precision = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * The precision of the field.
         * @property precision
         * @memberOf Parameter
         */
        P.Parameter.prototype.precision = 0;
    }
    Object.defineProperty(Parameter.prototype, "scale", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.scale;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.scale = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * The scale of the field.
         * @property scale
         * @memberOf Parameter
         */
        P.Parameter.prototype.scale = 0;
    }
    Object.defineProperty(Parameter.prototype, "description", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.description;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.description = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * The description of the field.
         * @property description
         * @memberOf Parameter
         */
        P.Parameter.prototype.description = '';
    }
    Object.defineProperty(Parameter.prototype, "signed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.signed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.signed = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * Determines if the field is signed.
         * @property signed
         * @memberOf Parameter
         */
        P.Parameter.prototype.signed = true;
    }
    Object.defineProperty(Parameter.prototype, "schemaName", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.schemaName;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.schemaName = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * This field schema name.
         * @property schemaName
         * @memberOf Parameter
         */
        P.Parameter.prototype.schemaName = '';
    }
    Object.defineProperty(Parameter.prototype, "tableName", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.tableName;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.tableName = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * This field table's name.
         * @property tableName
         * @memberOf Parameter
         */
        P.Parameter.prototype.tableName = '';
    }
    Object.defineProperty(Parameter.prototype, "mode", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.mode;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.mode = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * Parameter's mode (in, out, in/out).
         * @property mode
         * @memberOf Parameter
         */
        P.Parameter.prototype.mode = 0;
    }
    Object.defineProperty(Parameter.prototype, "originalName", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.originalName;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.originalName = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * The original name of the field.
         * In queries, such as select t1.f1 as f11, t2.f1 as f21 to preserve output fields' names unique,
         * but be able to generate right update sql clauses for multiple tables.
         * @property originalName
         * @memberOf Parameter
         */
        P.Parameter.prototype.originalName = '';
    }
    Object.defineProperty(Parameter.prototype, "size", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.size;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.size = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * The size of the field.
         * @property size
         * @memberOf Parameter
         */
        P.Parameter.prototype.size = 0;
    }
    Object.defineProperty(Parameter.prototype, "readonly", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.readonly;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.readonly = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * Determines if this field is readonly.
         * @property readonly
         * @memberOf Parameter
         */
        P.Parameter.prototype.readonly = true;
    }
    Object.defineProperty(Parameter.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.name = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * The name of the field.
         * @property name
         * @memberOf Parameter
         */
        P.Parameter.prototype.name = '';
    }
    Object.defineProperty(Parameter.prototype, "modified", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.modified;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.modified = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * Determines if parameter was modified.
         * @property modified
         * @memberOf Parameter
         */
        P.Parameter.prototype.modified = true;
    }
    Object.defineProperty(Parameter.prototype, "pk", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.pk;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.pk = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * Determines that this field is a primary key.
         * @property pk
         * @memberOf Parameter
         */
        P.Parameter.prototype.pk = true;
    }
    Object.defineProperty(Parameter.prototype, "value", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.value;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.value = P.boxAsJava(aValue);
        }
    });
    if(!Parameter){
        /**
         * The value of the parameter.
         * @property value
         * @memberOf Parameter
         */
        P.Parameter.prototype.value = {};
    }
    Object.defineProperty(Parameter.prototype, "assignFrom", {
        value: function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.assignFrom(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        }
    });
    if(!Parameter){
        /**
         * Assigning method of field/parameter information using specified source.
         * @method assignFrom
         * @memberOf Parameter
         */
        P.Parameter.prototype.assignFrom = function(arg0){};
    }
})();