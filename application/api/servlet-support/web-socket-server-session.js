/* global Java */

define(['boxing'], function(P) {
    /**
     * Generated constructor.
     * @constructor WebSocketServerSession WebSocketServerSession
     */
    function WebSocketServerSession() {
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
        if(WebSocketServerSession.superclass)
            WebSocketServerSession.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "query", {
            get: function() {
                var value = delegate.query;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "protocolVersion", {
            get: function() {
                var value = delegate.protocolVersion;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "id", {
            get: function() {
                var value = delegate.id;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "uri", {
            get: function() {
                var value = delegate.uri;
                return P.boxAsJs(value);
            }
        });

    };
    /**
     *
     * @method close
     * @memberOf WebSocketServerSession
     */
    WebSocketServerSession.prototype.close = function(arg0, arg1) {
        var delegate = this.unwrap();
        var value = delegate.close(P.boxAsJava(arg0), P.boxAsJava(arg1));
        return P.boxAsJs(value);
    };

    /**
     *
     * @method send
     * @memberOf WebSocketServerSession
     */
    WebSocketServerSession.prototype.send = function(data) {
        var delegate = this.unwrap();
        var value = delegate.send(P.boxAsJava(data));
        return P.boxAsJs(value);
    };


    var className = "com.eas.server.websocket.WebSocketServerSession";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new WebSocketServerSession(aDelegate);
    });
    return WebSocketServerSession;
});