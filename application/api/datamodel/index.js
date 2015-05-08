try{
    P.require([
          './application-db-entity.js'
        , './application-platypus-entity.js'
        , './entity-instance-delete-event.js'
        , './application-db-model.js'
        , './entity-instance-insert-event.js'
        , './cursor-position-will-change-event.js'
        , './application-platypus-model.js'
        , './cursor-position-changed-event.js'
        , './entity-instance-change-event.js'
    ]);
}catch(e){
    print(e);
}
