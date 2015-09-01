try{
    P.require([
          './jdbc-change-value.js'
        , './field.js'
        , './report.js'
        , './command.js'
        , './update.js'
        , './parameter.js'
        , './delete.js'
        , './insert.js'
        , './published-sourced-event.js'
        , './change-value.js'
        , './jdbc-field.js'
    ]);
}catch(e){
    P.Logger.severe(e);
}
