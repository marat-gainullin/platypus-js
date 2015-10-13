define([
      './change-value'
    , './command'
    , './delete'
    , './insert'
    , './jdbc-change-value'
    , './update'
    , './published-sourced-event'
    , './field'
    , './jdbc-field'
    , './parameter'
    , './report'
], function(
      ChangeValue
    , Command
    , Delete
    , Insert
    , JdbcChangeValue
    , Update
    , PublishedSourcedEvent
    , Field
    , JdbcField
    , Parameter
    , Report
    ){
    return {
          ChangeValue: ChangeValue
        , Command: Command
        , Delete: Delete
        , Insert: Insert
        , JdbcChangeValue: JdbcChangeValue
        , Update: Update
        , PublishedSourcedEvent: PublishedSourcedEvent
        , Field: Field
        , JdbcField: JdbcField
        , Parameter: Parameter
        , Report: Report
    };
});
