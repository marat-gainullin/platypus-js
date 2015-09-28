define([
      './web-socket-client-session'
    , './web-socket-server-session'
], function(
      WebSocketClientSession
    , WebSocketServerSession
    ){
    return {
          WebSocketClientSession: WebSocketClientSession
        , WebSocketServerSession: WebSocketServerSession
    };
});
