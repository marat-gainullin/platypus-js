try{
    P.require([
          './platypus-principal.js'
        , './field.js'
        , './report.js'
        , './command.js'
        , './update.js'
        , './parameter.js'
        , './anonymous-platypus-principal.js'
        , './delete.js'
        , './insert.js'
        , './published-sourced-event.js'
        , './change-value.js'
        , './system-platypus-principal.js'
    ]);
}catch(e){
    print(e);
}