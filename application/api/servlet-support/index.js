try{
    P.require([
          './web-socket-client-session.js'
        , './web-socket-server-session.js'
        , './http-platypus-principal.js'
        , './web-socket-platypus-principal.js'
    ]);
}catch(e){
    print(e);
}
