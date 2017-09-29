/* global expect */
/* global NaN */

function expectValue(obj, prop, value) {
    obj[prop] = value;
    expect(obj[prop]).toEqual(value);
}

function expectWidget(widget, Font, Color, Cursor) {
    expect('name' in widget).toBeTruthy();
    expectValue(widget, 'name', 'widgetName');
    expect('element' in widget).toBeTruthy();
    expect('component' in widget).toBeTruthy();
    expect('parent' in widget).toBeTruthy();
    expectValue(widget, 'parent', new widget.constructor());
    expectValue(widget, 'parent', null);
    expect('left' in widget).toBeTruthy();
    expectValue(widget, 'left', 30);
    expect('width' in widget).toBeTruthy();
    expectValue(widget, 'width', 50);
    expect('top' in widget).toBeTruthy();
    expectValue(widget, 'top', 57);
    expect('height' in widget).toBeTruthy();
    expectValue(widget, 'height', 80);
    expect('enabled' in widget).toBeTruthy();
    expectValue(widget, 'enabled', true);
    expectValue(widget, 'enabled', false);
    expect('visible' in widget).toBeTruthy();
    expectValue(widget, 'visible', true);
    expectValue(widget, 'visible', false);
    expect('opaque' in widget).toBeTruthy();
    expectValue(widget, 'opaque', true);
    expectValue(widget, 'opaque', false);
    expect('cursor' in widget).toBeTruthy();
    expectValue(widget, 'cursor', Cursor.WAIT);
    expect('background' in widget).toBeTruthy();
    expectValue(widget, 'background', new Color('#fcfcfc'));
    expect('foreground' in widget).toBeTruthy();
    expectValue(widget, 'foreground', new Color(12, 45, 78, 35));
    expect('error' in widget).toBeTruthy();
    expectValue(widget, 'error', 'sample validation message');
    widget.error = null;
    expect('contextMenu' in widget).toBeTruthy();
    expectValue(widget, 'contextMenu', new widget.constructor());
    expect('toolTipText' in widget).toBeTruthy();
    expectValue(widget, 'toolTipText', ' sample tooltip');
    expect('nextFocusableComponent' in widget).toBeTruthy();
    expectValue(widget, 'nextFocusableComponent', new widget.constructor());
    expect('focusable' in widget).toBeTruthy();
    expectValue(widget, 'focusable', true);
    expectValue(widget, 'focusable', false);
    expect('font' in widget).toBeDefined();
    expectValue(widget, 'font', new Font('Arial', Font.Style.ITALIC, 14));
    expect(widget.focus).toBeDefined();
    expect(typeof widget.focus).toEqual('function');
    widget.focus();

    expect('onShown' in widget).toBeTruthy();
    expectValue(widget, 'onShown', function () {});
    expect('onHidden' in widget).toBeTruthy();
    expectValue(widget, 'onHidden', function () {});
    expect('onMouseDragged' in widget).toBeTruthy();
    expectValue(widget, 'onMouseDragged', function () {});
    expect('onMouseReleased' in widget).toBeTruthy();
    expectValue(widget, 'onMouseReleased', function () {});
    expect('onFocusLost' in widget).toBeTruthy();
    expectValue(widget, 'onFocusLost', function () {});
    expect('onMousePressed' in widget).toBeTruthy();
    expectValue(widget, 'onMousePressed', function () {});
    expect('onMouseEntered' in widget).toBeTruthy();
    expectValue(widget, 'onMouseEntered', function () {});
    expect('onMouseMoved' in widget).toBeTruthy();
    expectValue(widget, 'onMouseMoved', function () {});
    expect('onActionPerformed' in widget).toBeTruthy();
    expectValue(widget, 'onActionPerformed', function () {});
    expect('onKeyReleased' in widget).toBeTruthy();
    expectValue(widget, 'onKeyReleased', function () {});
    expect('onKeyTyped' in widget).toBeTruthy();
    expectValue(widget, 'onKeyTyped', function () {});
    expect('onMouseWheelMoved' in widget).toBeTruthy();
    expectValue(widget, 'onMouseWheelMoved', function () {});
    expect('onFocusGained' in widget).toBeTruthy();
    expectValue(widget, 'onFocusGained', function () {});
    expect('onMouseClicked' in widget).toBeTruthy();
    expectValue(widget, 'onMouseClicked', function () {});
    expect('onMouseExited' in widget).toBeTruthy();
    expectValue(widget, 'onMouseExited', function () {});
    expect('onKeyPressed' in widget).toBeTruthy();
    expectValue(widget, 'onKeyPressed', function () {});
}
