try{
    P.require([
          './jdbc-change-value'
        , './field'
        , './report'
        , './command'
        , './update'
        , './parameter'
        , './delete'
        , './insert'
        , './published-sourced-event'
        , './change-value'
        , './jdbc-field'
    ]);
}catch(e){
    P.Logger.severe(e);
}
