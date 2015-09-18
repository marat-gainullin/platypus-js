try{
    P.require([
          './web-socket-client-session'
        , './web-socket-server-session'
    ]);
}catch(e){
    P.Logger.severe(e);
}
