(function() {
    var javaClass = Java.type("com.eas.server.websocket.WebSocketClientSession");
    javaClass.setPublisher(function(aDelegate) {
        return new P.WebSocketClientSession(null, aDelegate);
    });
    
    /**
     *
     * @constructor WebSocketClientSession WebSocketClientSession
     */
    P.WebSocketClientSession = function (uri) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(uri))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.WebSocketClientSession.superclass)
            P.WebSocketClientSession.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "onmessage", {
            get: function() {
                var value = delegate.onmessage;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onmessage = P.boxAsJava(aValue);
            }
        });
        if(!P.WebSocketClientSession){
            /**
             * Generated property jsDoc.
             * @property onmessage
             * @memberOf WebSocketClientSession
             */
            P.WebSocketClientSession.prototype.onmessage = {};
        }
        Object.defineProperty(this, "onerror", {
            get: function() {
                var value = delegate.onerror;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onerror = P.boxAsJava(aValue);
            }
        });
        if(!P.WebSocketClientSession){
            /**
             * Generated property jsDoc.
             * @property onerror
             * @memberOf WebSocketClientSession
             */
            P.WebSocketClientSession.prototype.onerror = {};
        }
        Object.defineProperty(this, "onopen", {
            get: function() {
                var value = delegate.onopen;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onopen = P.boxAsJava(aValue);
            }
        });
        if(!P.WebSocketClientSession){
            /**
             * Generated property jsDoc.
             * @property onopen
             * @memberOf WebSocketClientSession
             */
            P.WebSocketClientSession.prototype.onopen = {};
        }
        Object.defineProperty(this, "query", {
            get: function() {
                var value = delegate.query;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketClientSession){
            /**
             * Generated property jsDoc.
             * @property query
             * @memberOf WebSocketClientSession
             */
            P.WebSocketClientSession.prototype.query = '';
        }
        Object.defineProperty(this, "onclose", {
            get: function() {
                var value = delegate.onclose;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onclose = P.boxAsJava(aValue);
            }
        });
        if(!P.WebSocketClientSession){
            /**
             * Generated property jsDoc.
             * @property onclose
             * @memberOf WebSocketClientSession
             */
            P.WebSocketClientSession.prototype.onclose = {};
        }
        Object.defineProperty(this, "protocolVersion", {
            get: function() {
                var value = delegate.protocolVersion;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketClientSession){
            /**
             * Generated property jsDoc.
             * @property protocolVersion
             * @memberOf WebSocketClientSession
             */
            P.WebSocketClientSession.prototype.protocolVersion = '';
        }
        Object.defineProperty(this, "id", {
            get: function() {
                var value = delegate.id;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketClientSession){
            /**
             * Generated property jsDoc.
             * @property id
             * @memberOf WebSocketClientSession
             */
            P.WebSocketClientSession.prototype.id = '';
        }
        Object.defineProperty(this, "uri", {
            get: function() {
                var value = delegate.uri;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketClientSession){
            /**
             * Generated property jsDoc.
             * @property uri
             * @memberOf WebSocketClientSession
             */
            P.WebSocketClientSession.prototype.uri = '';
        }
    };
        /**
         *
         * @method close
         * @memberOf WebSocketClientSession
         */
        P.WebSocketClientSession.prototype.close = function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.close(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        };

        /**
         *
         * @method send
         * @memberOf WebSocketClientSession
         */
        P.WebSocketClientSession.prototype.send = function(data) {
            var delegate = this.unwrap();
            var value = delegate.send(P.boxAsJava(data));
            return P.boxAsJs(value);
        };

})();