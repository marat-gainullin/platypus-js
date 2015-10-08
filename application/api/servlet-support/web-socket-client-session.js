/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.server.websocket.WebSocketClientSession";
    var javaClass = Java.type(className);
    /**
     *
     * @constructor WebSocketClientSession WebSocketClientSession
     */
    function WebSocket(uri) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(B.boxAsJava(uri))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(WebSocket.superclass)
            WebSocket.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "onmessage", {
            get: function() {
                var value = delegate.onmessage;
                return value;
            },
            set: function(aValue) {
                delegate.onmessage = aValue;
            }
        });

        Object.defineProperty(this, "onerror", {
            get: function() {
                var value = delegate.onerror;
                return value;
            },
            set: function(aValue) {
                delegate.onerror = aValue;
            }
        });

        Object.defineProperty(this, "onopen", {
            get: function() {
                var value = delegate.onopen;
                return value;
            },
            set: function(aValue) {
                delegate.onopen = aValue;
            }
        });

        Object.defineProperty(this, "query", {
            get: function() {
                var value = delegate.query;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "onclose", {
            get: function() {
                var value = delegate.onclose;
                return value;
            },
            set: function(aValue) {
                delegate.onclose = aValue;
            }
        });

        Object.defineProperty(this, "protocolVersion", {
            get: function() {
                var value = delegate.protocolVersion;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "id", {
            get: function() {
                var value = delegate.id;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "uri", {
            get: function() {
                var value = delegate.uri;
                return B.boxAsJs(value);
            }
        });

    };
    /**
     *
     * @method close
     * @memberOf WebSocket
     */
    WebSocket.prototype.close = function(arg0, arg1) {
        var delegate = this.unwrap();
        var value = delegate.close(B.boxAsJava(arg0), B.boxAsJava(arg1));
        return B.boxAsJs(value);
    };

    /**
     *
     * @method send
     * @memberOf WebSocket
     */
    WebSocket.prototype.send = function(data) {
        var delegate = this.unwrap();
        var value = delegate.send(B.boxAsJava(data));
        return B.boxAsJs(value);
    };


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new WebSocket(null, aDelegate);
    });
    return WebSocket;
});