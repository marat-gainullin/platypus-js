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
    load('classpath:forms/action-event.js');
    load('classpath:forms/anchors-pane.js');
    load('classpath:forms/anchors.js');
    load('classpath:forms/border-pane.js');
    load('classpath:forms/box-pane.js');
    load('classpath:forms/button-group.js');
    load('classpath:forms/button.js');
    load('classpath:forms/card-pane.js');
    load('classpath:forms/cell-render-event.js');
    load('classpath:forms/check-box.js');
    load('classpath:forms/check-grid-column.js');
    load('classpath:forms/check-menu-item.js');
    load('classpath:forms/component-event.js');
    load('classpath:forms/container-event.js');
    load('classpath:forms/desktop-pane.js');
    load('classpath:forms/drop-down-button.js');
    load('classpath:forms/flow-pane.js');
    load('classpath:forms/focus-event.js');
    load('classpath:forms/form.js');
    load('classpath:forms/formatted-field.js');
    load('classpath:forms/grid-pane.js');
    load('classpath:forms/html-area.js');
    load('classpath:forms/item-event.js');
    load('classpath:forms/key-event.js');
    load('classpath:forms/label.js');
    load('classpath:forms/menu-bar.js');
    load('classpath:forms/menu-item.js');
    load('classpath:forms/menu-separator.js');
    load('classpath:forms/menu.js');
    load('classpath:forms/model-check-box.js');
    load('classpath:forms/model-combo.js');
    load('classpath:forms/model-date.js');
    load('classpath:forms/model-formatted-field.js');
    load('classpath:forms/model-grid-column.js');
    load('classpath:forms/model-grid.js');
    load('classpath:forms/model-spin.js');
    load('classpath:forms/model-text-area.js');
    load('classpath:forms/mouse-event.js');
    load('classpath:forms/password-field.js');
    load('classpath:forms/popup-menu.js');
    load('classpath:forms/progress-bar.js');
    load('classpath:forms/radio-button.js');
    load('classpath:forms/radio-grid-column.js');
    load('classpath:forms/radio-menu-item.js');
    load('classpath:forms/scroll-pane.js');
    load('classpath:forms/service-grid-column.js');
    load('classpath:forms/slider.js');
    load('classpath:forms/split-pane.js');
    load('classpath:forms/tabbed-pane.js');
    load('classpath:forms/text-area.js');
    load('classpath:forms/text-field.js');
    load('classpath:forms/toggle-button.js');
    load('classpath:forms/tool-bar.js');
    load('classpath:forms/value-change-event.js');
    load('classpath:forms/window-event.js');
    print('forms API loaded.');
} catch (e) {
    print('forms API skipped.');
}

try {
    load('classpath:grid/cell-data.js');
    print('grid API loaded.');
} catch (e) {
    print('grid API skipped.');
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
