/**
 * Contains the basic dependencies.
 */
try {
    load('classpath:core/platypus-principal.js');
    load('classpath:core/published-sourced-event.js');
    load('classpath:core/report.js');
    print('core API loaded.');
} catch (e) {
    print('core API skipped.');
}

try {
    load('classpath:datamodel/application-db-entity.js');
    load('classpath:datamodel/application-db-model.js');
    load('classpath:datamodel/application-platypus-entity.js');
    load('classpath:datamodel/application-platypus-model.js');
    load('classpath:datamodel/cursor-position-changed-event.js');
    load('classpath:datamodel/cursor-position-will-change-event.js');
    load('classpath:datamodel/entity-instance-change-event.js');
    load('classpath:datamodel/entity-instance-delete-event.js');
    load('classpath:datamodel/entity-instance-insert-event.js');
    print('datamodel API loaded.');
} catch (e) {
    print('datamodel API skipped.');
}

try {
    load('classpath:reports/report-template.js');
    print('reports API loaded.');
} catch (e) {
    print('reports API skipped.');
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
    print('rowsets API loaded.');
} catch (e) {
    print('rowsets API skipped.');
}

try {
    load('classpath:http-context.js');
    print('http-context API loaded.');
} catch (e) {
    print('http-context API skipped.');
}

try {
    load('classpath:server/session.js');
    print('server API loaded.');
} catch (e) {
    print('server API skipped.');
}

try {
    load('classpath:servlet-support/web-socket-client-session.js');
    load('classpath:servlet-support/web-socket-server-session.js');
    print('servlet-support API loaded.');
} catch (e) {
    print('servlet-support API skipped.');
}
