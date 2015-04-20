/**
 * Contains the basic dependencies loading.
 */
try {
    load('classpath:common-utils/color.js');
    load('classpath:common-utils/cursor.js');
    load('classpath:common-utils/font.js');
    print('common-utils API loaded.');
} catch (e) {
    print('common-utils API skipped.');
}

try {
    load('classpath:core/change-value.js');
    load('classpath:core/command.js');
    load('classpath:core/delete.js');
    load('classpath:core/field.js');
    load('classpath:core/insert.js');
    load('classpath:core/parameter.js');
    load('classpath:core/platypus-principal.js');
    load('classpath:core/published-sourced-event.js');
    load('classpath:core/report.js');
    load('classpath:core/update.js');
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
