try{
    P.require([
          './application-platypus-entity'
        , './entity-instance-delete-event'
        , './entity-instance-insert-event'
        , './cursor-position-changed-event'
        , './application-db-model'
        , './entity-instance-change-event'
        , './application-platypus-model'
        , './application-db-entity'
        , './cursor-position-will-change-event'
    ]);
}catch(e){
    P.Logger.severe(e);
}
