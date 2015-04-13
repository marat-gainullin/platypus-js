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
