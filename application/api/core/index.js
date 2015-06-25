try{
    P.require([
          './field.js'
        , './report.js'
        , './command.js'
        , './update.js'
        , './parameter.js'
        , './delete.js'
        , './insert.js'
        , './published-sourced-event.js'
        , './change-value.js'
    ]);
}catch(e){
    print(e);
}
