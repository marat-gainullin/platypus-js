(function() {
    var className = "com.eas.client.metadata.JdbcField";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.JdbcField(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor JdbcField JdbcField
     */
    P.JdbcField = function () {
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
        if(P.JdbcField.superclass)
            P.JdbcField.superclass.constructor.apply(this, arguments);
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
        if(!P.JdbcField){
            /**
             * The original name of the field.
             * In queries, such as select t1.f1 as f11, t2.f1 as f21 to preserve output fields' names unique,
             * but be able to generate right update sql clauses for multiple tables.
             * @property originalName
             * @memberOf JdbcField
             */
            P.JdbcField.prototype.originalName = '';
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
        if(!P.JdbcField){
            /**
             * Determines if this field is readonly.
             * @property readonly
             * @memberOf JdbcField
             */
            P.JdbcField.prototype.readonly = true;
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
        if(!P.JdbcField){
            /**
             * Determines if field is nullable.
             * @property nullable
             * @memberOf JdbcField
             */
            P.JdbcField.prototype.nullable = true;
        }
        Object.defineProperty(this, "fk", {
            get: function() {
                var value = delegate.fk;
                return P.boxAsJs(value);
            }
        });
        if(!P.JdbcField){
            /**
             * Indicates that this field is a foreign key to another table or it is a self-reference key.
             * @property fk
             * @memberOf JdbcField
             */
            P.JdbcField.prototype.fk = true;
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
        if(!P.JdbcField){
            /**
             * The name of the field.
             * @property name
             * @memberOf JdbcField
             */
            P.JdbcField.prototype.name = '';
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
        if(!P.JdbcField){
            /**
             * The description of the field.
             * @property description
             * @memberOf JdbcField
             */
            P.JdbcField.prototype.description = '';
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
        if(!P.JdbcField){
            /**
             * Determines that this field is a primary key.
             * @property pk
             * @memberOf JdbcField
             */
            P.JdbcField.prototype.pk = true;
        }
        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.type = P.boxAsJava(aValue);
            }
        });
        if(!P.JdbcField){
            /**
             * The field's type information.
             * @property type
             * @memberOf JdbcField
             */
            P.JdbcField.prototype.type = '';
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
        if(!P.JdbcField){
            /**
             * This field table's name.
             * @property tableName
             * @memberOf JdbcField
             */
            P.JdbcField.prototype.tableName = '';
        }
    };
})();