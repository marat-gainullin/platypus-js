try{
    P.require([
          './cursor-position-changed-event.js'
        , './application-platypus-model.js'
        , './application-db-model.js'
        , './entity-instance-change-event.js'
        , './application-db-entity.js'
        , './cursor-position-will-change-event.js'
        , './application-platypus-entity.js'
        , './entity-instance-delete-event.js'
        , './entity-instance-insert-event.js'
    ]);
}catch(e){
    print(e);
}
