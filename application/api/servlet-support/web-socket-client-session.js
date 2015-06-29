(function() {
    var className = "com.eas.server.websocket.WebSocketClientSession";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.WebSocket(null, aDelegate);
    });
    
    /**
     *
     * @constructor WebSocketClientSession WebSocketClientSession
     */
    P.WebSocket = function (uri) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(uri))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(P.WebSocket.superclass)
            P.WebSocket.superclass.constructor.apply(this, arguments);
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
        if(!P.WebSocket){
            /**
             * Generated property jsDoc.
             * @property onmessage
             * @memberOf WebSocket
             */
            P.WebSocket.prototype.onmessage = {};
        }
        Object.defineProperty(this, "onerror", {
            get: function() {
                var value = delegate.onerror;
                return value;
            },
            set: function(aValue) {
                delegate.onerror = aValue;
            }
        });
        if(!P.WebSocket){
            /**
             * Generated property jsDoc.
             * @property onerror
             * @memberOf WebSocket
             */
            P.WebSocket.prototype.onerror = {};
        }
        Object.defineProperty(this, "onopen", {
            get: function() {
                var value = delegate.onopen;
                return value;
            },
            set: function(aValue) {
                delegate.onopen = aValue;
            }
        });
        if(!P.WebSocket){
            /**
             * Generated property jsDoc.
             * @property onopen
             * @memberOf WebSocket
             */
            P.WebSocket.prototype.onopen = {};
        }
        Object.defineProperty(this, "query", {
            get: function() {
                var value = delegate.query;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocket){
            /**
             * Generated property jsDoc.
             * @property query
             * @memberOf WebSocket
             */
            P.WebSocket.prototype.query = '';
        }
        Object.defineProperty(this, "onclose", {
            get: function() {
                var value = delegate.onclose;
                return value;
            },
            set: function(aValue) {
                delegate.onclose = aValue;
            }
        });
        if(!P.WebSocket){
            /**
             * Generated property jsDoc.
             * @property onclose
             * @memberOf WebSocket
             */
            P.WebSocket.prototype.onclose = {};
        }
        Object.defineProperty(this, "protocolVersion", {
            get: function() {
                var value = delegate.protocolVersion;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocket){
            /**
             * Generated property jsDoc.
             * @property protocolVersion
             * @memberOf WebSocket
             */
            P.WebSocket.prototype.protocolVersion = '';
        }
        Object.defineProperty(this, "id", {
            get: function() {
                var value = delegate.id;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocket){
            /**
             * Generated property jsDoc.
             * @property id
             * @memberOf WebSocket
             */
            P.WebSocket.prototype.id = '';
        }
        Object.defineProperty(this, "uri", {
            get: function() {
                var value = delegate.uri;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocket){
            /**
             * Generated property jsDoc.
             * @property uri
             * @memberOf WebSocket
             */
            P.WebSocket.prototype.uri = '';
        }
    };
        /**
         *
         * @method close
         * @memberOf WebSocket
         */
        P.WebSocket.prototype.close = function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.close(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        };

        /**
         *
         * @method send
         * @memberOf WebSocket
         */
        P.WebSocket.prototype.send = function(data) {
            var delegate = this.unwrap();
            var value = delegate.send(P.boxAsJava(data));
            return P.boxAsJs(value);
        };

})();