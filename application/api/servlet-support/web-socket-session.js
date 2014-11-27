(function() {
    var javaClass = Java.type("com.eas.server.websocket.WebSocketSession");
    javaClass.setPublisher(function(aDelegate) {
        return new P.WebSocketSession(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor WebSocketSession WebSocketSession
     */
    P.WebSocketSession = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.WebSocketSession.superclass)
            P.WebSocketSession.superclass.constructor.apply(this, arguments);
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
        if(!P.WebSocketSession){
            /**
             * Generated property jsDoc.
             * @property onmessage
             * @memberOf WebSocketSession
             */
            P.WebSocketSession.prototype.onmessage = {};
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
        if(!P.WebSocketSession){
            /**
             * Generated property jsDoc.
             * @property onerror
             * @memberOf WebSocketSession
             */
            P.WebSocketSession.prototype.onerror = {};
        }
        Object.defineProperty(this, "query", {
            get: function() {
                var value = delegate.query;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketSession){
            /**
             * Generated property jsDoc.
             * @property query
             * @memberOf WebSocketSession
             */
            P.WebSocketSession.prototype.query = '';
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
        if(!P.WebSocketSession){
            /**
             * Generated property jsDoc.
             * @property onclose
             * @memberOf WebSocketSession
             */
            P.WebSocketSession.prototype.onclose = {};
        }
        Object.defineProperty(this, "protocolVersion", {
            get: function() {
                var value = delegate.protocolVersion;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketSession){
            /**
             * Generated property jsDoc.
             * @property protocolVersion
             * @memberOf WebSocketSession
             */
            P.WebSocketSession.prototype.protocolVersion = '';
        }
        Object.defineProperty(this, "id", {
            get: function() {
                var value = delegate.id;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketSession){
            /**
             * Generated property jsDoc.
             * @property id
             * @memberOf WebSocketSession
             */
            P.WebSocketSession.prototype.id = '';
        }
        Object.defineProperty(this, "uri", {
            get: function() {
                var value = delegate.uri;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketSession){
            /**
             * Generated property jsDoc.
             * @property uri
             * @memberOf WebSocketSession
             */
            P.WebSocketSession.prototype.uri = '';
        }
    };
        /**
         *
         * @method close
         * @memberOf WebSocketSession
         */
        P.WebSocketSession.prototype.close = function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.close(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        };

        /**
         *
         * @method send
         * @memberOf WebSocketSession
         */
        P.WebSocketSession.prototype.send = function(data) {
            var delegate = this.unwrap();
            var value = delegate.send(P.boxAsJava(data));
            return P.boxAsJs(value);
        };

})();