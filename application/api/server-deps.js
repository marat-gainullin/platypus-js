/**
 * Contains the basic dependencies.
 */
try {
    load('classpath:core/anonymous-platypus-principal.js');
    load('classpath:core/app-platypus-principal.js');
    load('classpath:core/db-platypus-principal.js');
    load('classpath:core/published-sourced-event.js');
    load('classpath:core/report.js');
    load('classpath:core/system-platypus-principal.js');
    printf('core API loaded.');
} catch (e) {
    printf('core API skipped.');
}

try {
    load('classpath:datamodel/application-db-entity.js');
    load('classpath:datamodel/application-db-model.js');
    load('classpath:datamodel/application-db-parameters-entity.js');
    load('classpath:datamodel/application-platypus-entity.js');
    load('classpath:datamodel/application-platypus-model.js');
    load('classpath:datamodel/application-platypus-parameters-entity.js');
    load('classpath:datamodel/cursor-position-changed-event.js');
    load('classpath:datamodel/cursor-position-will-change-event.js');
    load('classpath:datamodel/entity-instance-change-event.js');
    load('classpath:datamodel/entity-instance-delete-event.js');
    load('classpath:datamodel/entity-instance-insert-event.js');
    printf('datamodel API loaded.');
} catch (e) {
    printf('datamodel API skipped.');
}

try {
    load('classpath:reports/report-template.js');
    printf('reports API loaded.');
} catch (e) {
    printf('reports API skipped.');
}

try {
    load('classpath:rowsets/change-value.js');
    load('classpath:rowsets/command.js');
    load('classpath:rowsets/delete.js');
    load('classpath:rowsets/field.js');
    load('classpath:rowsets/filter.js');
    load('classpath:rowsets/insert.js');
    load('classpath:rowsets/parameter.js');
    load('classpath:rowsets/update.js');
    printf('rowsets API loaded.');
} catch (e) {
    printf('rowsets API skipped.');
}

try {
    load('classpath:http-context.js');
    printf('http-context API loaded.');
} catch (e) {
    printf('http-context API skipped.');
}
