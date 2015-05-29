try{
    P.require([
          './http-platypus-principal.js'
        , './web-socket-platypus-principal.js'
        , './web-socket-server-session.js'
        , './web-socket-client-session.js'
    ]);
}catch(e){
    print(e);
}
