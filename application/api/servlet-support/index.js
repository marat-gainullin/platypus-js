try{
    P.require([
          './web-socket-client-session.js'
        , './web-socket-server-session.js'
    ]);
}catch(e){
    P.Logger.severe(e);
}
