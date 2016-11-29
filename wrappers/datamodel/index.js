define([
      './application-db-entity'
    , './application-db-model'
    , './application-platypus-entity'
    , './application-platypus-model'
    , './cursor-position-changed-event'
    , './cursor-position-will-change-event'
    , './entity-instance-change-event'
    , './entity-instance-delete-event'
    , './entity-instance-insert-event'
], function(
      ApplicationDbEntity
    , ApplicationDbModel
    , ApplicationPlatypusEntity
    , ApplicationPlatypusModel
    , CursorPositionChangedEvent
    , CursorPositionWillChangeEvent
    , EntityInstanceChangeEvent
    , EntityInstanceDeleteEvent
    , EntityInstanceInsertEvent
    ){
    return {
          ApplicationDbEntity: ApplicationDbEntity
        , ApplicationDbModel: ApplicationDbModel
        , ApplicationPlatypusEntity: ApplicationPlatypusEntity
        , ApplicationPlatypusModel: ApplicationPlatypusModel
        , CursorPositionChangedEvent: CursorPositionChangedEvent
        , CursorPositionWillChangeEvent: CursorPositionWillChangeEvent
        , EntityInstanceChangeEvent: EntityInstanceChangeEvent
        , EntityInstanceDeleteEvent: EntityInstanceDeleteEvent
        , EntityInstanceInsertEvent: EntityInstanceInsertEvent
    };
});
