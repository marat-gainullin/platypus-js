(function() {
    var className = "com.eas.server.websocket.WebSocketServerSession";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.WebSocketServerSession(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor WebSocketServerSession WebSocketServerSession
     */
    P.WebSocketServerSession = function () {
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
        if(P.WebSocketServerSession.superclass)
            P.WebSocketServerSession.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "query", {
            get: function() {
                var value = delegate.query;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketServerSession){
            /**
             * Generated property jsDoc.
             * @property query
             * @memberOf WebSocketServerSession
             */
            P.WebSocketServerSession.prototype.query = '';
        }
        Object.defineProperty(this, "protocolVersion", {
            get: function() {
                var value = delegate.protocolVersion;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketServerSession){
            /**
             * Generated property jsDoc.
             * @property protocolVersion
             * @memberOf WebSocketServerSession
             */
            P.WebSocketServerSession.prototype.protocolVersion = '';
        }
        Object.defineProperty(this, "id", {
            get: function() {
                var value = delegate.id;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketServerSession){
            /**
             * Generated property jsDoc.
             * @property id
             * @memberOf WebSocketServerSession
             */
            P.WebSocketServerSession.prototype.id = '';
        }
        Object.defineProperty(this, "uri", {
            get: function() {
                var value = delegate.uri;
                return P.boxAsJs(value);
            }
        });
        if(!P.WebSocketServerSession){
            /**
             * Generated property jsDoc.
             * @property uri
             * @memberOf WebSocketServerSession
             */
            P.WebSocketServerSession.prototype.uri = '';
        }
    };
        /**
         *
         * @method close
         * @memberOf WebSocketServerSession
         */
        P.WebSocketServerSession.prototype.close = function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.close(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        };

        /**
         *
         * @method send
         * @memberOf WebSocketServerSession
         */
        P.WebSocketServerSession.prototype.send = function(data) {
            var delegate = this.unwrap();
            var value = delegate.send(P.boxAsJava(data));
            return P.boxAsJs(value);
        };

})();