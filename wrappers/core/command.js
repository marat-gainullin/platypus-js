/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.changes.Command";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor Command Command
     */
    function Command() {
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
        if(Command.superclass)
            Command.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        /**
         * Indicates the change's type (Insert, Update, Delete or Command).
         */
        this.type = '';
        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return B.boxAsJs(value);
            }
        });

        /**
         * Parameters of command.
         */
        this.parameters = new Object();
        Object.defineProperty(this, "parameters", {
            get: function() {
                var value = delegate.parameters;
                return B.boxAsJs(value);
            }
        });

        /**
         * Command sql text to be applied in a database.
         */
        this.command = '';
        Object.defineProperty(this, "command", {
            get: function() {
                var value = delegate.command;
                return B.boxAsJs(value);
            }
        });

        /**
         *Indicates the change's destination entity.
         */
        this.entity = '';
        Object.defineProperty(this, "entity", {
            get: function() {
                var value = delegate.entity;
                return B.boxAsJs(value);
            }
        });

    }

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Command(aDelegate);
    });
    return Command;
});