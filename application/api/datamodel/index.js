try{
    P.require([
          './application-platypus-entity.js'
        , './entity-instance-delete-event.js'
        , './entity-instance-insert-event.js'
        , './cursor-position-changed-event.js'
        , './application-db-model.js'
        , './entity-instance-change-event.js'
        , './application-platypus-model.js'
        , './application-db-entity.js'
        , './cursor-position-will-change-event.js'
    ]);
}catch(e){
    P.Logger.severe(e);
}
