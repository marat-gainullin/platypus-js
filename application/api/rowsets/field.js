(function() {
    var javaClass = Java.type("com.bearsoft.rowset.metadata.Field");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Field(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor Field Field
     */
    P.Field = function Field() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(Field.superclass)
            Field.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Field", {value: Field});
    Object.defineProperty(Field.prototype, "typeInfo", {
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
    if(!Field){
        /**
         * The field's type information.
         * @property typeInfo
         * @memberOf Field
         */
        P.Field.prototype.typeInfo = {};
    }
    Object.defineProperty(Field.prototype, "nullable", {
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
    if(!Field){
        /**
         * Determines if field is nullable.
         * @property nullable
         * @memberOf Field
         */
        P.Field.prototype.nullable = true;
    }
    Object.defineProperty(Field.prototype, "fk", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.fk;
            return P.boxAsJs(value);
        }
    });
    if(!Field){
        /**
         * Indicates that this field is a foreign key to another table or it is a self-reference key.
         * @property fk
         * @memberOf Field
         */
        P.Field.prototype.fk = true;
    }
    Object.defineProperty(Field.prototype, "precision", {
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
    if(!Field){
        /**
         * The precision of the field.
         * @property precision
         * @memberOf Field
         */
        P.Field.prototype.precision = 0;
    }
    Object.defineProperty(Field.prototype, "scale", {
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
    if(!Field){
        /**
         * The scale of the field.
         * @property scale
         * @memberOf Field
         */
        P.Field.prototype.scale = 0;
    }
    Object.defineProperty(Field.prototype, "description", {
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
    if(!Field){
        /**
         * The description of the field.
         * @property description
         * @memberOf Field
         */
        P.Field.prototype.description = '';
    }
    Object.defineProperty(Field.prototype, "signed", {
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
    if(!Field){
        /**
         * Determines if the field is signed.
         * @property signed
         * @memberOf Field
         */
        P.Field.prototype.signed = true;
    }
    Object.defineProperty(Field.prototype, "schemaName", {
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
    if(!Field){
        /**
         * This field schema name.
         * @property schemaName
         * @memberOf Field
         */
        P.Field.prototype.schemaName = '';
    }
    Object.defineProperty(Field.prototype, "tableName", {
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
    if(!Field){
        /**
         * This field table's name.
         * @property tableName
         * @memberOf Field
         */
        P.Field.prototype.tableName = '';
    }
    Object.defineProperty(Field.prototype, "originalName", {
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
    if(!Field){
        /**
         * The original name of the field.
         * In queries, such as select t1.f1 as f11, t2.f1 as f21 to preserve output fields' names unique,
         * but be able to generate right update sql clauses for multiple tables.
         * @property originalName
         * @memberOf Field
         */
        P.Field.prototype.originalName = '';
    }
    Object.defineProperty(Field.prototype, "size", {
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
    if(!Field){
        /**
         * The size of the field.
         * @property size
         * @memberOf Field
         */
        P.Field.prototype.size = 0;
    }
    Object.defineProperty(Field.prototype, "readonly", {
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
    if(!Field){
        /**
         * Determines if this field is readonly.
         * @property readonly
         * @memberOf Field
         */
        P.Field.prototype.readonly = true;
    }
    Object.defineProperty(Field.prototype, "name", {
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
    if(!Field){
        /**
         * The name of the field.
         * @property name
         * @memberOf Field
         */
        P.Field.prototype.name = '';
    }
    Object.defineProperty(Field.prototype, "pk", {
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
    if(!Field){
        /**
         * Determines that this field is a primary key.
         * @property pk
         * @memberOf Field
         */
        P.Field.prototype.pk = true;
    }
})();