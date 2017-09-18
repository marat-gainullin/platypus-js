/* global expect */

describe('Containers Api', function () {

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

        expect('onComponentShown' in widget).toBeTruthy();
        expectValue(widget, 'onComponentShown', function () {});
        expect('onComponentHidden' in widget).toBeTruthy();
        expectValue(widget, 'onComponentHidden', function () {});
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

    function expectContainer(container, Font, Color, Cursor) {
        expectWidget(container, Font, Color, Cursor);
        // Structure
        expect('count' in container).toBeTruthy();
        expect(container.add).toBeDefined();
        expect(typeof container.add).toEqual('function');
        expect(container.remove).toBeDefined();
        expect(typeof container.remove).toEqual('function');
        expect(container.clear).toBeDefined();
        expect(typeof container.clear).toEqual('function');
        expect(container.children).toBeDefined();
        expect(typeof container.children).toEqual('function');
        expect(container.child).toBeDefined();
        expect(typeof container.child).toEqual('function');
        expect('onComponentAdded' in container).toBeTruthy();
        expectValue(container, 'onComponentAdded', function () {});
        expect('onComponentRemoved' in container).toBeTruthy();
        expectValue(container, 'onComponentRemoved', function () {});
    }

    it('Color Api', function (done) {
        require(['common-utils/color'], function (Color) {
            var c1 = new Color("#ccc");
            expect(c1.red).toEqual(204);
            expect(c1.green).toEqual(204);
            expect(c1.blue).toEqual(204);
            expect(c1.alpha).toEqual(255);
            var c2 = new Color("rgb(12, 23, 34)");
            expect(c2.red).toEqual(12);
            expect(c2.green).toEqual(23);
            expect(c2.blue).toEqual(34);
            expect(c2.alpha).toEqual(255);
            var c22 = new Color(12, 23, 34);
            expect(c22.red).toEqual(12);
            expect(c22.green).toEqual(23);
            expect(c22.blue).toEqual(34);
            expect(c22.alpha).toEqual(255);
            var c3 = new Color("rgba(12, 23, 34, .5)");
            expect(c3.red).toEqual(12);
            expect(c3.green).toEqual(23);
            expect(c3.blue).toEqual(34);
            expect(c3.alpha).toEqual(127);
            var c33 = new Color(12, 23, 34, 45);
            expect(c33.red).toEqual(12);
            expect(c33.green).toEqual(23);
            expect(c33.blue).toEqual(34);
            expect(c33.alpha).toEqual(45);
            done();
        });
    });

    it('Flow pane.Structure', function (done) {
        require([
            'forms/flow-pane',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor'
        ], function (
                Flow,
                Font,
                Color,
                Cursor) {
            var container = new Flow();
            expectContainer(container, Font, Color, Cursor);
            expect(container.hgap).toEqual(0);
            expect(container.vgap).toEqual(0);
            expect(container.element).toBeDefined();
            var container2 = new Flow(3, 6);
            expectContainer(container2, Font, Color, Cursor);
            expect(container2.hgap).toEqual(3);
            expect(container2.vgap).toEqual(6);
            expect(container2.element).toBeDefined();

            var child0 = new Flow();
            var child1 = new Flow();
            var child2 = new Flow();
            container.add(child0);
            container.add(child1);
            container.add(child2);

            expect(container.count).toEqual(3);
            expect(container.child(0)).toEqual(child0);
            expect(container.child(1)).toEqual(child1);
            expect(container.child(2)).toEqual(child2);
            expect(container.children()).toEqual([child0, child1, child2]);
            expect(container.indexOf(child0)).toEqual(0);
            expect(container.indexOf(child1)).toEqual(1);
            expect(container.indexOf(child2)).toEqual(2);

            var removed0 = container.remove(0);
            expect(removed0).toBeDefined();
            var removed1 = container.remove(child1);
            expect(removed1).toBeDefined();

            container.clear();
            expect(container.count).toEqual(0);
            expect(container.child(0)).toEqual(null);
            expect(container.children()).toEqual([]);
            expect(container.indexOf(child0)).toEqual(-1);
            expect(container.indexOf(child1)).toEqual(-1);
            expect(container.indexOf(child2)).toEqual(-1);

            done();
        });
    });
    it('Flow pane.detached children left top width height', function (done) {
        require([
            'forms/flow-pane'
        ], function (
                Flow) {
            var container = new Flow();
            var widget = new Flow();
            container.add(widget);

            expect(widget.left).toEqual(0);
            widget.left += 10;
            expect(widget.left).toEqual(10);

            expect(widget.top).toEqual(0);
            widget.top += 20;
            expect(widget.top).toEqual(20);

            expect(widget.width).toEqual(0);
            widget.width += 30;
            expect(widget.width).toEqual(30);

            expect(widget.height).toEqual(0);
            widget.height += 40;
            expect(widget.height).toEqual(40);

            done();
        });
    });
    it('Flow pane.attached children left top width height', function (done) {
        require([
            'forms/flow-pane',
            'invoke'
        ], function (
                Flow,
                Invoke) {
            var container = new Flow();
            container.width = container.height = 40;
            document.body.appendChild(container.element);
            expect(container.attached).toBeTruthy();
            Invoke.later(function () {
                var widget = new Flow();
                container.add(widget);
                expect(widget.attached).toBeTruthy();

                expect(widget.width).toEqual(0);
                widget.width += 30;
                expect(widget.width).toEqual(30);

                expect(widget.height).toEqual(0);
                widget.height += 40;
                expect(widget.height).toEqual(40);

                expect(widget.left).toEqual(0);
                widget.left += 10;
                expect(widget.left).toEqual(10);

                expect(widget.top).toEqual(0);
                widget.top += 20;
                expect(widget.top).toEqual(20);

                document.body.removeChild(container.element);

                done();
            });
        });
    });
    it('Flow pane.attached hgap vgap', function (done) {
        require([
            'forms/flow-pane',
            'invoke'
        ], function (
                Flow,
                Invoke) {
            var container = new Flow();
            document.body.appendChild(container.element);
            expect(container.attached).toBeTruthy();
            Invoke.later(function () {
                container.width = container.height = 40;
                var widget1 = new Flow();
                container.add(widget1);
                expect(widget1.attached).toBeTruthy();
                widget1.width = 10;
                widget1.height = 10;

                var widget2 = new Flow();
                widget2.width = 10;
                widget2.height = 10;
                container.add(widget2);
                expect(widget2.attached).toBeTruthy();

                expect(widget2.left).toEqual(widget1.left + widget1.width);
                container.hgap += 5;
                expect(container.hgap).toEqual(5);
                Invoke.later(function () {
                    expect(widget2.left).toEqual(10 + 2 * container.hgap);

                    expect(widget1.top).toEqual(0);
                    expect(widget2.top).toEqual(0);
                    container.width = 20;
                    expect(widget1.top).toEqual(0);
                    expect(widget2.top).toEqual(10);
                    container.vgap += 5;
                    expect(widget1.top).toEqual(5);
                    expect(widget2.top).toEqual(20);

                    document.body.removeChild(container.element);
                    done();
                });
            });
        });
    });
    it('Cards pane.Structure', function (done) {
        require([
            'invoke',
            'forms/card-pane',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor'
        ], function (
                Invoke,
                Cards,
                Font,
                Color,
                Cursor) {
            var container = new Cards();
            var selectedEvents = 0;
            var selected = container.addSelectionHandler(function (event) {
                selectedEvents++;
                expect(event).toBeDefined();
                expect(event.source).toBeDefined();
                expect(event.target).toBeDefined();
                expect(event.item).toBeDefined();
            });
            expectContainer(container, Font, Color, Cursor);
            expect(container.hgap).toEqual(0);
            expect(container.vgap).toEqual(0);
            expect(container.element).toBeDefined();
            var container2 = new Cards(3, 6);
            expectContainer(container2, Font, Color, Cursor);
            expect(container2.hgap).toEqual(3);
            expect(container2.vgap).toEqual(6);
            expect(container2.element).toBeDefined();

            // General structure
            var child0 = new Cards();
            var child1 = new Cards();
            var child2 = new Cards();
            container.add(child0);
            container.add(child1);
            container.add(child2);

            expect(container.count).toEqual(3);
            expect(container.child(0)).toEqual(child0);
            expect(container.child(1)).toEqual(child1);
            expect(container.child(2)).toEqual(child2);
            expect(container.children()).toEqual([child0, child1, child2]);
            expect(container.indexOf(child0)).toEqual(0);
            expect(container.indexOf(child1)).toEqual(1);
            expect(container.indexOf(child2)).toEqual(2);

            var removed0 = container.remove(0);
            expect(removed0).toBeDefined();
            var removed1 = container.remove(child1);
            expect(removed1).toBeDefined();

            container.clear();
            expect(container.count).toEqual(0);
            expect(container.child(0)).toEqual(null);
            expect(container.children()).toEqual([]);
            expect(container.indexOf(child0)).toEqual(-1);
            expect(container.indexOf(child1)).toEqual(-1);
            expect(container.indexOf(child2)).toEqual(-1);

            // Cards specific
            var step1 = new Cards();
            container.add(step1, 'Step-1-view');
            var step2 = new Cards();
            container.add(step2, 'Step-2-view');
            var step03 = new Cards();
            var step3 = new Cards();
            container.add(step03, 'Step-3-view');
            container.add(step3, 'Step-3-view');
            expect(container.indexOf(step03)).toEqual(-1);

            expect(container.child('Step-1-view')).toEqual(step1);
            expect(container.child('Step-2-view')).toEqual(step2);
            expect(container.child('Step-3-view')).toEqual(step3);
            expect(container.child(0)).toEqual(step1);
            expect(container.child(1)).toEqual(step2);
            expect(container.child(2)).toEqual(step3);
            expect(container.visibleWidget).toEqual(step1);
            container.show('Step-2-view');
            expect(container.visibleWidget).toEqual(step2);
            container.show(step3);
            expect(container.visibleWidget).toEqual(step3);
            container.remove(step3);
            expect(container.visibleWidget).toEqual(step1);
            container.remove('Step-2-view');
            expect(container.count).toEqual(1);
            container.remove(0);
            expect(container.count).toEqual(0);

            selected.removeHandler();
            Invoke.later(function () {
                expect(selectedEvents).toBeGreaterThan(0);
                done();
            });
        });
    });
    it('Cards pane.detached children left top width height', function (done) {
        require([
            'forms/card-pane'
        ], function (
                Cards) {
            var container = new Cards();
            var widget = new Cards();
            container.add(widget);

            expect(widget.left).toEqual(0);
            widget.left += 10;
            expect(widget.left).toEqual(0);

            expect(widget.top).toEqual(0);
            widget.top += 20;
            expect(widget.top).toEqual(0);

            expect(widget.width).toEqual(0);
            widget.width += 30;
            expect(widget.width).toEqual(0);

            expect(widget.height).toEqual(0);
            widget.height += 40;
            expect(widget.height).toEqual(0);

            done();
        });
    });
    it('Cards pane.attached children left top width height', function (done) {
        require([
            'forms/card-pane',
            'invoke'
        ], function (
                Cards,
                Invoke) {
            var container = new Cards();
            container.width = container.height = 40;
            document.body.appendChild(container.element);
            expect(container.attached).toBeTruthy();
            Invoke.later(function () {
                var widget = new Cards();
                container.add(widget);
                expect(widget.attached).toBeTruthy();

                expect(widget.width).toEqual(40);
                widget.width += 20;
                expect(widget.width).toEqual(40);

                expect(widget.height).toEqual(40);
                widget.height += 20;
                expect(widget.height).toEqual(40);

                expect(widget.left).toEqual(0);
                widget.left += 10;
                expect(widget.left).toEqual(0);

                expect(widget.top).toEqual(0);
                widget.top += 10;
                expect(widget.top).toEqual(0);

                document.body.removeChild(container.element);

                done();
            });
        });
    });
    it('Cards pane.attached hgap vgap', function (done) {
        require([
            'common-utils/color',
            'forms/card-pane',
            'invoke'
        ], function (
                Color,
                Cards,
                Invoke) {
            var container = new Cards();
            container.background = Color.BLUE;
            document.body.appendChild(container.element);
            container.width = container.height = 400;
            expect(container.attached).toBeTruthy();
            Invoke.later(function () {
                var widget1 = new Cards();
                widget1.background = Color.black;
                container.add(widget1);
                expect(widget1.attached).toBeTruthy();

                Invoke.later(function () {
                    expect(widget1.left).toEqual(0);
                    expect(widget1.top).toEqual(0);
                    expect(widget1.width).toEqual(400);
                    expect(widget1.height).toEqual(400);
                    container.hgap += 20;
                    expect(widget1.left).toEqual(20);
                    expect(widget1.width).toEqual(360);
                    container.vgap += 20;
                    expect(widget1.top).toEqual(20);
                    expect(widget1.height).toEqual(360);

                    document.body.removeChild(container.element);
                    done();
                });
            });
        });
    });

    it('Grid pane.Structure', function (done) {
        require([
            'forms/grid-pane',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor'
        ], function (
                Cells,
                Font,
                Color,
                Cursor) {
            var container1 = new Cells();
            expectContainer(container1, Font, Color, Cursor);
            expect(container1.rows).toEqual(1);
            expect(container1.columns).toEqual(1);
            expect(container1.hgap).toEqual(0);
            expect(container1.vgap).toEqual(0);
            var container = new Cells(2, 2);
            expect(container.rows).toEqual(2);
            expect(container.columns).toEqual(2);
            expect(container.hgap).toEqual(0);
            expect(container.vgap).toEqual(0);
            var container2 = new Cells(2, 2, 5);
            expect(container2.rows).toEqual(2);
            expect(container2.columns).toEqual(2);
            expect(container2.hgap).toEqual(5);
            expect(container2.vgap).toEqual(0);
            var container3 = new Cells(2, 2, 5, 4);
            expect(container3.rows).toEqual(2);
            expect(container3.columns).toEqual(2);
            expect(container3.hgap).toEqual(5);
            expect(container3.vgap).toEqual(4);
            // General structure
            var child00 = new Cells();
            var child11 = new Cells();
            var child01 = new Cells();
            var child_10 = new Cells();
            var child10 = new Cells();
            container.add(child00, 0, 0);
            container.add(child11, 1, 1);
            container.add(child01, 0, 1);
            container.add(child_10, 1, 0);
            expect(container.indexOf(child_10)).toEqual(3);
            var evicted = container.add(child10, 1, 0);
            expect(evicted).toBeDefined();
            expect(evicted).toEqual(child_10);
            expect(container.indexOf(child_10)).toEqual(-1);

            expect(container.count).toEqual(4);
            expect(container.child(0, 0)).toEqual(child00);
            expect(container.child(1, 1)).toEqual(child11);
            expect(container.child(0, 1)).toEqual(child01);
            expect(container.child(1, 0)).toEqual(child10);
            expect(container.children()).toEqual([child00, child11, child01, child10]);
            expect(container.indexOf(child00)).toEqual(0);
            expect(container.indexOf(child11)).toEqual(1);
            expect(container.indexOf(child01)).toEqual(2);
            expect(container.indexOf(child10)).toEqual(3);

            var removed00 = container.remove(0);
            expect(removed00).toBeDefined();
            expect(removed00).toEqual(child00);
            var removed11 = container.remove(child11);
            expect(removed11).toBeDefined();
            expect(removed11).toEqual(child11);
            var removed01 = container.remove(0, 1);
            expect(removed01).toBeDefined();
            expect(removed01).toEqual(child01);

            container.clear();
            expect(container.count).toEqual(0);
            expect(container.child(0)).toEqual(null);
            expect(container.children()).toEqual([]);
            expect(container.indexOf(child00)).toEqual(-1);
            expect(container.indexOf(child11)).toEqual(-1);
            expect(container.indexOf(child01)).toEqual(-1);
            expect(container.indexOf(child10)).toEqual(-1);

            container.add(child00);
            container.add(child01);
            container.add(child10);
            container.add(child11);
            expect(container.child(0, 0)).toEqual(child00);
            expect(container.child(0, 1)).toEqual(child01);
            expect(container.child(1, 0)).toEqual(child10);
            expect(container.child(1, 1)).toEqual(child11);

            done();
        });
    });

    it('Grid pane.detached children left top width height', function (done) {
        require([
            'forms/grid-pane'
        ], function (
                Cells) {
            var container = new Cells();
            var widget = new Cells();
            container.add(widget, 0, 0);

            expect(widget.left).toEqual(0);
            widget.left += 10;
            expect(widget.left).toEqual(0);

            expect(widget.top).toEqual(0);
            widget.top += 20;
            expect(widget.top).toEqual(0);

            expect(widget.width).toEqual(0);
            widget.width += 30;
            expect(widget.width).toEqual(0);

            expect(widget.height).toEqual(0);
            widget.height += 40;
            expect(widget.height).toEqual(0);

            done();
        });
    });
    it('Grid pane.attached children left top width height', function (done) {
        require([
            'invoke',
            'forms/grid-pane'
        ], function (
                Invoke,
                Cells) {
            var container = new Cells(2, 2);
            container.width = container.height = 400;
            document.body.appendChild(container.element);
            var widget00 = new Cells();
            var widget01 = new Cells();
            var widget10 = new Cells();
            var widget11 = new Cells();
            container.add(widget00, 0, 0);
            container.add(widget01, 0, 1);
            container.add(widget10, 1, 0);
            container.add(widget11, 1, 1);

            Invoke.later(function () {
                // 00
                expect(widget00.left).toEqual(0);
                widget00.left += 10;
                expect(widget00.left).toEqual(0);

                expect(widget00.top).toEqual(0);
                widget00.top += 20;
                expect(widget00.top).toEqual(0);

                expect(widget00.width).toEqual(200);
                widget00.width += 30;
                expect(widget00.width).toEqual(200);

                expect(widget00.height).toEqual(200);
                widget00.height += 40;
                expect(widget00.height).toEqual(200);
                // 01
                expect(widget01.left).toEqual(200);
                widget01.left += 10;
                expect(widget01.left).toEqual(200);

                expect(widget01.top).toEqual(0);
                widget01.top += 20;
                expect(widget01.top).toEqual(0);

                expect(widget01.width).toEqual(200);
                widget01.width += 30;
                expect(widget01.width).toEqual(200);

                expect(widget01.height).toEqual(200);
                widget01.height += 40;
                expect(widget01.height).toEqual(200);

                // 10
                expect(widget10.left).toEqual(0);
                widget10.left += 10;
                expect(widget10.left).toEqual(0);

                expect(widget10.top).toEqual(200);
                widget10.top += 20;
                expect(widget10.top).toEqual(200);

                expect(widget10.width).toEqual(200);
                widget10.width += 30;
                expect(widget10.width).toEqual(200);

                expect(widget10.height).toEqual(200);
                widget10.height += 40;
                expect(widget10.height).toEqual(200);

                // 11
                expect(widget11.left).toEqual(200);
                widget11.left += 10;
                expect(widget11.left).toEqual(200);

                expect(widget11.top).toEqual(200);
                widget11.top += 20;
                expect(widget11.top).toEqual(200);

                expect(widget11.width).toEqual(200);
                widget11.width += 30;
                expect(widget11.width).toEqual(200);

                expect(widget11.height).toEqual(200);
                widget11.height += 40;
                expect(widget11.height).toEqual(200);

                document.body.removeChild(container.element);
                done();
            });
        });
    });
    it('Grid pane.attached hgap vgap', function (done) {
        require([
            'common-utils/color',
            'invoke',
            'forms/grid-pane'
        ], function (
                Color,
                Invoke,
                Cells) {
            var container = new Cells(2, 2);
            container.width = container.height = 400;
            container.background = Color.blue;
            document.body.appendChild(container.element);
            var widget00 = new Cells();
            widget00.background = Color.black;
            var widget01 = new Cells();
            widget01.background = Color.black;
            var widget10 = new Cells();
            widget10.background = Color.black;
            var widget11 = new Cells();
            widget11.background = Color.black;
            container.add(widget00, 0, 0);
            container.add(widget01, 0, 1);
            container.add(widget10, 1, 0);
            container.add(widget11, 1, 1);

            container.hgap = 20;
            Invoke.later(function () {
                // 00
                expect(widget00.left).toEqual(0);
                expect(widget00.top).toEqual(0);
                expect(widget00.width).toEqual(200);
                expect(widget00.height).toEqual(200);
                // 01
                expect(widget01.left).toEqual(200);
                expect(widget01.top).toEqual(0);
                expect(widget01.width).toEqual(200);
                expect(widget01.height).toEqual(200);
                // 10
                expect(widget10.left).toEqual(0);
                expect(widget10.top).toEqual(200);
                expect(widget10.width).toEqual(200);
                expect(widget10.height).toEqual(200);
                // 11
                expect(widget11.left).toEqual(200);
                expect(widget11.top).toEqual(200);
                expect(widget11.width).toEqual(200);
                expect(widget11.height).toEqual(200);

                container.vgap = 20;
                Invoke.later(function () {
                    // 00
                    expect(widget00.left).toEqual(0);
                    expect(widget00.top).toEqual(0);
                    expect(widget00.width).toEqual(200);
                    expect(widget00.height).toEqual(200);
                    // 01
                    expect(widget01.left).toEqual(200);
                    expect(widget01.top).toEqual(0);
                    expect(widget01.width).toEqual(200);
                    expect(widget01.height).toEqual(200);
                    // 10
                    expect(widget10.left).toEqual(0);
                    expect(widget10.top).toEqual(200);
                    expect(widget10.width).toEqual(200);
                    expect(widget10.height).toEqual(200);
                    // 11
                    expect(widget11.left).toEqual(200);
                    expect(widget11.top).toEqual(200);
                    expect(widget11.width).toEqual(200);
                    expect(widget11.height).toEqual(200);

                    document.body.removeChild(container.element);
                    done();
                });
            });
        });
    });
    it('Absolute pane.Structure', function (done) {
        require([
            'forms/absolute-pane',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor'
        ], function (
                Absolute,
                Font,
                Color,
                Cursor) {
            var container = new Absolute();
            expectContainer(container, Font, Color, Cursor);
            expect(container.element).toBeDefined();

            var child0 = new Absolute();
            var child1 = new Absolute();
            var child2 = new Absolute();
            container.add(child0);
            container.add(child1, {left: 10, top: 10, width: 50, height: 50});
            child2.left = 70;
            child2.top = 10;
            child2.width = 50;
            child2.height = 50;
            container.add(child2);

            expect(container.count).toEqual(3);
            expect(container.child(0)).toEqual(child0);
            expect(container.child(1)).toEqual(child1);
            expect(container.child(2)).toEqual(child2);
            expect(container.children()).toEqual([child0, child1, child2]);
            expect(container.indexOf(child0)).toEqual(0);
            expect(container.indexOf(child1)).toEqual(1);
            expect(container.indexOf(child2)).toEqual(2);

            var removed0 = container.remove(0);
            expect(removed0).toBeDefined();
            var removed1 = container.remove(child1);
            expect(removed1).toBeDefined();

            container.clear();
            expect(container.count).toEqual(0);
            expect(container.child(0)).toEqual(null);
            expect(container.children()).toEqual([]);
            expect(container.indexOf(child0)).toEqual(-1);
            expect(container.indexOf(child1)).toEqual(-1);
            expect(container.indexOf(child2)).toEqual(-1);

            done();
        });
    });
    it('Anchors pane.Structure', function (done) {
        require([
            'forms/anchors-pane',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor'
        ], function (
                Anchors,
                Font,
                Color,
                Cursor) {
            var container = new Anchors();
            expectContainer(container, Font, Color, Cursor);
            expect(container.element).toBeDefined();

            var child0 = new Anchors();
            var child1 = new Anchors();
            var child2 = new Anchors();
            container.add(child0);
            container.add(child1, {left: 10, top: 10, width: 50, height: 50});
            child2.left = 70;
            child2.top = 10;
            child2.width = 50;
            child2.height = 50;
            container.add(child2);

            expect(container.count).toEqual(3);
            expect(container.child(0)).toEqual(child0);
            expect(container.child(1)).toEqual(child1);
            expect(container.child(2)).toEqual(child2);
            expect(container.children()).toEqual([child0, child1, child2]);
            expect(container.indexOf(child0)).toEqual(0);
            expect(container.indexOf(child1)).toEqual(1);
            expect(container.indexOf(child2)).toEqual(2);

            var removed0 = container.remove(0);
            expect(removed0).toBeDefined();
            var removed1 = container.remove(child1);
            expect(removed1).toBeDefined();

            container.clear();
            expect(container.count).toEqual(0);
            expect(container.child(0)).toEqual(null);
            expect(container.children()).toEqual([]);
            expect(container.indexOf(child0)).toEqual(-1);
            expect(container.indexOf(child1)).toEqual(-1);
            expect(container.indexOf(child2)).toEqual(-1);

            done();
        });
    });
    it('Anchors pane.attached constraints', function (done) {
        require([
            'invoke',
            'forms/anchors-pane',
            'forms/anchors'
        ], function (
                Invoke,
                Anchors,
                AnchorsConstraints) {
            var constraints = new AnchorsConstraints(1, 2, 3, 4, 5, 6); // Obsolete class. Test is here only because of compliance tests
            expect(constraints.left).toEqual(1);
            expect(constraints.width).toEqual(2);
            expect(constraints.right).toEqual(3);
            expect(constraints.top).toEqual(4);
            expect(constraints.height).toEqual(5);
            expect(constraints.bottom).toEqual(6);

            var container = new Anchors();
            document.body.appendChild(container.element);

            var child0 = new Anchors();
            var child1 = new Anchors();
            var child2 = new Anchors();
            container.add(child0);
            container.add(child1, {left: 10, top: 10, width: 50, height: 50});
            child2.left = 70;
            child2.top = 10;
            child2.width = 50;
            child2.height = 50;
            container.add(child2);

            Invoke.later(function () {
                expect(child0.left).toEqual(0);
                expect(child0.top).toEqual(0);
                expect(child0.width).toEqual(0);
                expect(child0.height).toEqual(0);
                expect(child1.left).toEqual(10);
                expect(child1.top).toEqual(10);
                expect(child1.width).toEqual(50);
                expect(child1.height).toEqual(50);
                expect(child2.left).toEqual(70);
                expect(child2.top).toEqual(10);
                expect(child2.width).toEqual(50);
                expect(child2.height).toEqual(50);
                document.body.removeChild(container.element);
                done();
            });
        });
    });
    it('Anchors pane.attached constraints moving', function (done) {
        require([
            'invoke',
            'forms/anchors-pane'
        ], function (
                Invoke,
                Anchors) {

            var container = new Anchors();
            container.width = container.height = 400;
            document.body.appendChild(container.element);

            var child0 = new Anchors();
            container.add(child0, {left: '10%', top: '10%', width: '50%', height: '50%'});
            var child1 = new Anchors();
            container.add(child1, {left: '10%', top: '10%', width: '50%', height: '50%'});
            var child2 = new Anchors();
            container.add(child2, {right: '10%', bottom: '10%', width: '50%', height: '50%'});

            Invoke.later(function () {
                // child0
                expect(child0.left).toEqual(40);
                child0.left += 26;
                expect(child0.left).toEqual(40 + 26);
                expect(child0.element.style.left).toEqual('16.5%');

                expect(child0.top).toEqual(40);
                child0.top += 26;
                expect(child0.top).toEqual(40 + 26);
                expect(child0.element.style.top).toEqual('16.5%');

                // child1
                expect(child1.width).toEqual(200);
                child1.width += 26;
                expect(child1.width).toEqual(200 + 26);
                expect(child1.element.style.width).toEqual('56.5%');

                expect(child1.height).toEqual(200);
                child1.height += 26;
                expect(child1.height).toEqual(200 + 26);
                expect(child1.element.style.height).toEqual('56.5%');

                // child2
                expect(child2.width).toEqual(200);
                child2.width -= 26;
                expect(child2.width).toEqual(200 - 26);
                expect(child2.element.style.width).toEqual('43.5%');

                expect(child2.height).toEqual(200);
                child2.height -= 26;
                expect(child2.height).toEqual(200 - 26);
                expect(child2.element.style.height).toEqual('43.5%');

                document.body.removeChild(container.element);
                done();
            });
        });
    });
    it('Box pane.Structure', function (done) {
        require([
            'forms/box-pane',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor'
        ], function (
                Box,
                Font,
                Color,
                Cursor) {
            var container = new Box();
            expectContainer(container, Font, Color, Cursor);
            expect(container.element).toBeDefined();

            var child0 = new Box();
            var child1 = new Box();
            var child2 = new Box();
            container.add(child0);
            container.add(child1);
            container.add(child2);

            expect(container.count).toEqual(3);
            expect(container.child(0)).toEqual(child0);
            expect(container.child(1)).toEqual(child1);
            expect(container.child(2)).toEqual(child2);
            expect(container.children()).toEqual([child0, child1, child2]);
            expect(container.indexOf(child0)).toEqual(0);
            expect(container.indexOf(child1)).toEqual(1);
            expect(container.indexOf(child2)).toEqual(2);

            var removed0 = container.remove(0);
            expect(removed0).toBeDefined();
            var removed1 = container.remove(child1);
            expect(removed1).toBeDefined();

            container.clear();
            expect(container.count).toEqual(0);
            expect(container.child(0)).toEqual(null);
            expect(container.children()).toEqual([]);
            expect(container.indexOf(child0)).toEqual(-1);
            expect(container.indexOf(child1)).toEqual(-1);
            expect(container.indexOf(child2)).toEqual(-1);

            done();
        });
    });
    it('Box pane.detached left top width height', function (done) {
        require([
            'ui',
            'forms/box-pane'
        ], function (
                Ui,
                Box) {
            var container = new Box();
            container.width = container.height = 400;
            var child0 = new Box();
            var child1 = new Box();
            var child2 = new Box();
            container.add(child0);
            container.add(child1);
            container.add(child2);
            expect(child0.width).toEqual(0);
            expect(child1.width).toEqual(0);
            expect(child1.width).toEqual(0);
            expect(child0.height).toEqual(0);
            expect(child1.height).toEqual(0);
            expect(child1.height).toEqual(0);
            container.orientation = Ui.Orientation.VERTICAL;
            expect(child0.height).toEqual(0);
            expect(child1.height).toEqual(0);
            expect(child1.height).toEqual(0);
            expect(child0.width).toEqual(0);
            expect(child1.width).toEqual(0);
            expect(child1.width).toEqual(0);


            container.orientation = Ui.Orientation.HORIZONTAL;

            child0.left += 10;
            expect(child0.left).toEqual(0);
            child0.top += 10;
            expect(child0.top).toEqual(0);
            child0.width += 10;
            expect(child0.width).toEqual(10);
            child0.height += 10;
            expect(child0.height).toEqual(0);

            child0.width = 0;
            container.orientation = Ui.Orientation.VERTICAL;

            child0.left += 10;
            expect(child0.left).toEqual(0);
            child0.top += 10;
            expect(child0.top).toEqual(0);
            child0.width += 10;
            expect(child0.width).toEqual(0);
            child0.height += 10;
            expect(child0.height).toEqual(10);
            done();
        });
    });
    it('Box pane.attached left top width height', function (done) {
        require([
            'ui',
            'forms/box-pane'
        ], function (
                Ui,
                Box) {
            var container = new Box();
            document.body.appendChild(container.element);
            container.width = container.height = 400;
            var child0 = new Box();
            var child1 = new Box();
            var child2 = new Box();
            container.add(child0);
            container.add(child1);
            container.add(child2);
            expect(child0.width).toEqual(0);
            expect(child1.width).toEqual(0);
            expect(child1.width).toEqual(0);
            expect(child0.height).toEqual(400);
            expect(child1.height).toEqual(400);
            expect(child1.height).toEqual(400);
            container.orientation = Ui.Orientation.VERTICAL;
            expect(child0.height).toEqual(0);
            expect(child1.height).toEqual(0);
            expect(child1.height).toEqual(0);
            expect(child0.width).toEqual(400);
            expect(child1.width).toEqual(400);
            expect(child1.width).toEqual(400);

            container.orientation = Ui.Orientation.HORIZONTAL;

            child0.left += 10;
            expect(child0.left).toEqual(0);
            child0.top += 10;
            expect(child0.top).toEqual(0);
            child0.width += 10;
            expect(child0.width).toEqual(10);
            expect(child1.left).toEqual(10);

            expect(child0.height).toEqual(400);
            child0.height += 10;
            expect(child0.height).toEqual(400);

            child0.width = 0;
            expect(child1.left).toEqual(0);
            container.orientation = Ui.Orientation.VERTICAL;

            child0.left += 10;
            expect(child0.left).toEqual(0);
            child0.top += 10;
            expect(child0.top).toEqual(0);

            expect(child0.width).toEqual(0);// width of child0 is already in its tag's style
            child0.width += 10;
            expect(child0.width).toEqual(0); // width of child0 is already in its tag's style

            expect(child1.width).toEqual(400);
            child1.width += 10;
            expect(child1.width).toEqual(400);
            child0.height += 10;
            expect(child0.height).toEqual(10);
            expect(child1.top).toEqual(10);
            child0.height = 0;
            expect(child0.height).toEqual(0);
            expect(child1.top).toEqual(0);
            document.body.removeChild(container.element);
            done();
        });
    });
    it('Box pane.attached hgap vgap orientation', function (done) {
        require([
            'ui',
            'forms/box-pane'
        ], function (
                Ui,
                Box) {
            var container = new Box();
            document.body.appendChild(container.element);
            container.width = container.height = 400;
            var child0 = new Box();
            var child1 = new Box();
            container.add(child0);
            container.add(child1);

            container.hgap = 10;
            expect(child0.left).toEqual(0);
            expect(child1.left).toEqual(10);

            container.orientation = Ui.Orientation.VERTICAL;

            container.vgap = 10;
            expect(child0.top).toEqual(0);
            expect(child1.top).toEqual(10);

            document.body.removeChild(container.element);
            done();
        });
    });
    it('Box by content horizontal vertical', function (done) {
        require([
            'ui',
            'forms/box-pane'
        ], function (
                Ui,
                Box) {
            var box = new Box();
            document.body.appendChild(box.element);
            box.height = 400;
            var child0 = new Box();
            var child1 = new Box();
            box.add(child0);
            box.add(child1);
            child0.width = child1.width = 200;
            box.hgap = 10;
            expect(box.element.offsetWidth).toEqual(410);

            // cleanup
            box.height = null;
            child0.width = child1.width = null;
            box.orientation = Ui.Orientation.VERTICAL;

            box.width = 400;
            child0.height = child1.height = 200;
            box.vgap = 10;
            expect(box.element.offsetHeight).toEqual(410);

            document.body.removeChild(box.element);
            done();
        });
    });
    it('Sized Box with horizontal and vertical scrolling', function (done) {
        require([
            'ui',
            'forms/box-pane'
        ], function (
                Ui,
                Box) {
            var box = new Box();
            document.body.appendChild(box.element);
            box.width = box.height = 400;
            var child0 = new Box();
            var child1 = new Box();
            box.add(child0);
            box.add(child1);
            child0.width = child1.width = 200;
            box.hgap = 10;
            expect(box.element.offsetWidth).toEqual(400);
            expect(box.element.scrollWidth).toEqual(410);

            // cleanup
            child0.width = child1.width = null;
            box.orientation = Ui.Orientation.VERTICAL;

            child0.height = child1.height = 200;
            box.vgap = 10;
            expect(box.element.offsetHeight).toEqual(400);
            expect(box.element.scrollHeight).toEqual(410);

            document.body.removeChild(box.element);
            done();
        });
    });
    it('Scroll pane.Structure', function (done) {
        require([
            'forms/scroll-pane',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor'
        ], function (
                Scroll,
                Font,
                Color,
                Cursor) {
            var container = new Scroll();
            expectContainer(container, Font, Color, Cursor);
            expect(container.element).toBeDefined();

            var child0 = new Scroll();
            var child1 = new Scroll();

            container.add(child0);
            expect(container.count).toEqual(1);
            expect(container.child(0)).toEqual(child0);
            expect(container.view).toEqual(child0);

            container.add(child1);
            expect(container.count).toEqual(1);
            expect(container.child(0)).toEqual(child1);
            expect(container.view).toEqual(child1);

            expect(container.children()).toEqual([child1]);
            expect(container.indexOf(child0)).toEqual(-1);
            expect(container.indexOf(child1)).toEqual(0);

            var removed0 = container.remove(0);
            expect(removed0).toBeDefined();
            expect(removed0).toEqual(child1);
            expect(container.count).toEqual(0);
            expect(container.children()).toEqual([]);
            expect(container.indexOf(child1)).toEqual(-1);
            expect(container.view).toBeNull();

            container.view = child0;
            var removed1 = container.remove(child0);
            expect(removed1).toBeDefined();
            expect(removed1).toEqual(child0);

            container.view = child1;
            expect(container.count).toEqual(1);
            expect(container.child(0)).toEqual(child1);

            container.clear();
            expect(container.count).toEqual(0);
            expect(container.child(0)).toEqual(null);
            expect(container.children()).toEqual([]);

            container.view = child0;
            expect(container.count).toEqual(1);
            expect(container.child(0)).toEqual(child0);

            container.view = null;
            expect(container.count).toEqual(0);
            expect(container.child(0)).toEqual(null);
            expect(container.children()).toEqual([]);

            done();
        });
    });
    it('Scroll pane.detached left top width height', function (done) {
        require([
            'forms/scroll-pane'
        ], function (
                Scroll) {
            var child = new Scroll();
            var container = new Scroll(child);
            expect(child.left).toEqual(0);
            child.left += 10;
            expect(child.left).toEqual(0);
            expect(child.top).toEqual(0);
            child.top += 10;
            expect(child.top).toEqual(0);
            expect(child.width).toEqual(0);
            child.width += 10;
            expect(child.width).toEqual(10);
            expect(child.height).toEqual(0);
            child.height += 10;
            expect(child.height).toEqual(10);
            done();
        });
    });
    it('Scroll pane.attached left top width height', function (done) {
        require([
            'forms/scroll-pane'
        ], function (
                Scroll) {
            var child = new Scroll();
            var container = new Scroll(child);
            container.width = container.height = 400;
            document.body.appendChild(container.element);
            expect(child.width).toEqual(0);
            child.width += 10;
            expect(child.width).toEqual(10);
            expect(child.height).toEqual(0);
            child.height += 10;
            expect(child.height).toEqual(10);
            expect(child.left).toEqual(0);
            child.left += 10;
            expect(child.element.offsetLeft).toEqual(0);
            expect(child.top).toEqual(0);
            child.top += 10;
            expect(child.element.offsetTop).toEqual(0);
            document.body.removeChild(container.element);
            done();
        });
    });
    it('Box in Scroll horizontal vertical', function (done) {
        require([
            'ui',
            'forms/scroll-pane',
            'forms/box-pane'
        ], function (
                Ui,
                Scroll,
                Box) {
            var scroll = new Scroll();
            document.body.appendChild(scroll.element);
            scroll.width = scroll.height = 400;
            var box = new Box();
            scroll.view = box;
            var child0 = new Box();
            child0.width = 200;
            var child1 = new Box();
            child1.width = 200;
            box.add(child0);
            box.add(child1);
            box.hgap = 10;
            expect(box.element.offsetWidth).toEqual(410);

            // cleanup
            child0.width = null;
            child1.width = null;

            box.orientation = Ui.Orientation.VERTICAL;

            child0.height = 200;
            child1.height = 200;
            box.vgap = 10;
            expect(box.element.offsetHeight).toEqual(410);

            document.body.removeChild(scroll.element);
            done();
        });
    });

    it('Toolbar.Structure', function (done) {
        require([
            'ui',
            'invoke',
            'forms/tool-bar'
        ], function (
                Ui,
                Invoke,
                Toolbar) {
            var toolbar = new Toolbar();
            document.body.appendChild(toolbar.element);
            toolbar.height = 150;
            toolbar.width = 400;
            var tool0 = new Toolbar();
            tool0.width = 100;
            tool0.background = Ui.Color.black;
            var tool1 = new Toolbar();
            tool1.width = 100;
            tool1.background = Ui.Color.black;
            var tool2 = new Toolbar();
            tool2.width = 100;
            tool2.background = Ui.Color.black;
            var tool3 = new Toolbar();
            tool3.width = 100;
            tool3.background = Ui.Color.black;
            toolbar.add(tool0);
            toolbar.add(tool1);
            toolbar.add(tool2);
            toolbar.add(tool3);

            expect('orientation' in toolbar).toBeFalsy();
            expect('vgap' in toolbar).toBeFalsy();
            expect('hgap' in toolbar).toBeTruthy();

            expect(tool0.left).toEqual(0);
            expect(tool1.left).toEqual(100);
            expect(toolbar.element.childElementCount).toEqual(5);

            toolbar.hgap = 10;

            expect(tool0.left).toEqual(0);
            expect(tool1.left).toEqual(110);

            toolbar.remove(tool3);
            toolbar.add(tool3);
            Invoke.later(function () {
                expect(toolbar.element.childElementCount).toEqual(6);
                toolbar.element.scrollLeft = 50;
                Invoke.later(function () {
                    expect(toolbar.element.childElementCount).toEqual(6);
                    document.body.removeChild(toolbar.element);
                    done();
                });
            });
        });
    });
    it('Split pane.Structure', function (done) {
        require([
            'forms/split-pane',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor'
        ], function (
                Split,
                Font,
                Color,
                Cursor) {
            var container = new Split();
            expectContainer(container, Font, Color, Cursor);
            expect(container.element).toBeDefined();

            var child0 = new Split();
            var child1 = new Split();

            container.add(child0);
            expect(container.count).toEqual(1);
            expect(container.child(0)).toEqual(child0);
            expect(container.firstComponent).toEqual(child0);

            container.add(child1);
            expect(container.count).toEqual(2);
            expect(container.child(1)).toEqual(child1);
            expect(container.secondComponent).toEqual(child1);

            expect(container.children()).toEqual([child0, child1]);

            var removed0 = container.remove(0);
            expect(removed0).toBeDefined();
            expect(removed0).toEqual(child0);
            expect(container.count).toEqual(1);
            expect(container.children()).toEqual([child0]);

            expect(container.firstComponent).toBeNull();

            var removed1 = container.remove(0);
            expect(removed1).toBeDefined();
            expect(removed1).toEqual(child1);
            expect(container.count).toEqual(0);
            expect(container.children()).toEqual([]);

            expect(container.secondComponent).toBeNull();

            container.firstComponent = child0;
            expect(container.count).toEqual(1);
            expect(container.child(0)).toEqual(child0);
            container.secondComponent = child1;
            expect(container.count).toEqual(2);
            expect(container.child(1)).toEqual(child1);

            container.firstComponent = null;
            expect(container.count).toEqual(1);
            expect(container.child(0)).toEqual(child1);

            container.secondComponent = null;
            expect(container.count).toEqual(0);
            expect(container.children()).toEqual([]);

            container.firstComponent = child0;
            container.secondComponent = child1;
            expect(container.count).toEqual(2);
            expect(container.children()).toEqual([child0, child1]);
            container.clear();
            expect(container.count).toEqual(0);
            expect(container.children()).toEqual([]);
            expect(container.firstComponent).toBeNull();
            expect(container.secondComponent).toBeNull();

            done();
        });
    });

    it('Split pane.detached left top width height', function (done) {
        require([
            'forms/split-pane'
        ], function (
                Split) {
            var split = new Split();

            var first = new Split();
            var second = new Split();

            split.firstComponent = first;
            split.secondComponent = second;

            expect(first.left).toEqual(0);
            first.left += 10;
            expect(first.left).toEqual(0);

            expect(first.top).toEqual(0);
            first.top += 10;
            expect(first.top).toEqual(0);

            expect(first.width).toEqual(0);
            first.width += 10;
            expect(first.width).toEqual(0);

            expect(first.height).toEqual(0);
            first.height += 10;
            expect(first.height).toEqual(0);

            expect(second.left).toEqual(0);
            second.left += 10;
            expect(second.left).toEqual(0);

            expect(second.top).toEqual(0);
            second.top += 10;
            expect(second.top).toEqual(0);

            expect(second.width).toEqual(0);
            second.width += 10;
            expect(second.width).toEqual(0);

            expect(second.height).toEqual(0);
            second.height += 10;
            expect(second.height).toEqual(0);

            done();
        });
    });
    it('Split pane.attached left top width height', function (done) {
        require([
            'ui',
            'invoke',
            'forms/split-pane'
        ], function (
                Ui,
                Invoke,
                Split) {
            var split = new Split();
            split.width = split.height = 200;
            document.body.appendChild(split.element);

            var first = new Split();
            var second = new Split();

            split.dividerLocation = 100;

            split.secondComponent = second;

            Invoke.later(function () {

                expect(second.left).toEqual(110);
                second.left += 10;
                expect(second.left).toEqual(110);

                expect(second.top).toEqual(0);
                second.top += 10;
                expect(second.top).toEqual(0);

                expect(second.width).toEqual(90);
                second.width += 10;
                expect(second.width).toEqual(90);

                expect(second.height).toEqual(200);
                second.height += 10;
                expect(second.height).toEqual(200);

                split.firstComponent = first;

                Invoke.later(function () {

                    expect(first.left).toEqual(0);
                    first.left += 10;
                    expect(first.left).toEqual(0);

                    expect(first.top).toEqual(0);
                    first.top += 10;
                    expect(first.top).toEqual(0);

                    expect(first.width).toEqual(100);
                    first.width += 10;
                    expect(first.width).toEqual(100);

                    expect(first.height).toEqual(200);
                    first.height += 10;
                    expect(first.height).toEqual(200);
                    split.orientation = Ui.Orientation.VERTICAL;

                    document.body.removeChild(split.element);
                    done();
                });
            });
        });
    });
    it('Borders pane.Structure', function (done) {
        require([
            'ui',
            'forms/border-pane',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor'
        ], function (
                Ui,
                Borders,
                Font,
                Color,
                Cursor) {
            var borders = new Borders();
            expectContainer(borders, Font, Color, Cursor);
            expect(borders.element).toBeDefined();

            var topChild = new Borders();
            var leftChild = new Borders();
            var centerChild = new Borders();
            var rightChild = new Borders();
            var bottomChild = new Borders();

            borders.topComponent = topChild;
            expect(borders.count).toEqual(1);
            expect(borders.child(0)).toEqual(topChild);
            expect(borders.topComponent).toEqual(topChild);

            borders.leftComponent = leftChild;
            expect(borders.count).toEqual(2);
            expect(borders.child(1)).toEqual(leftChild);
            expect(borders.leftComponent).toEqual(leftChild);

            borders.centerComponent = centerChild;
            expect(borders.count).toEqual(3);
            expect(borders.child(2)).toEqual(centerChild);
            expect(borders.centerComponent).toEqual(centerChild);

            borders.rightComponent = rightChild;
            expect(borders.count).toEqual(4);
            expect(borders.child(3)).toEqual(rightChild);
            expect(borders.rightComponent).toEqual(rightChild);

            borders.bottomComponent = bottomChild;
            expect(borders.count).toEqual(5);
            expect(borders.child(4)).toEqual(bottomChild);
            expect(borders.bottomComponent).toEqual(bottomChild);

            expect(borders.children()).toEqual([topChild, leftChild, centerChild, rightChild, bottomChild]);

            var removed0 = borders.remove(0);
            expect(removed0).toBeDefined();
            expect(removed0).toEqual(topChild);
            expect(borders.count).toEqual(4);
            expect(borders.children()).toEqual([leftChild, centerChild, rightChild, bottomChild]);

            expect(borders.topComponent).toBeNull();

            borders.topComponent = topChild;
            expect(borders.count).toEqual(5);
            expect(borders.child(borders.count - 1)).toEqual(topChild);
            expect(borders.topComponent).toEqual(topChild);

            borders.clear();
            expect(borders.count).toEqual(0);
            expect(borders.children()).toEqual([]);
            expect(borders.leftComponent).toBeNull();
            expect(borders.centerComponent).toBeNull();
            expect(borders.rightComponent).toBeNull();
            expect(borders.bottomComponent).toBeNull();

            borders.centerComponent = centerChild;
            var centerChild1 = new Borders();
            var oldCenter = borders.add(centerChild1);
            expect(oldCenter).toEqual(centerChild);

            borders.rightComponent = rightChild;
            var rightChild1 = new Borders();
            var oldRight = borders.add(rightChild1, Ui.HorizontalPosition.RIGHT);
            expect(oldRight).toEqual(rightChild);

            done();
        });
    });
    it('Borders pane.attached left top width height', function (done) {
        require([
            'ui',
            'invoke',
            'common-utils/color',
            'forms/border-pane'
        ], function (
                Ui,
                Invoke,
                Color,
                Borders) {
            var borders = new Borders();
            borders.background = Color.blue;
            borders.width = borders.height = 400;
            document.body.appendChild(borders.element);

            var topChild = new Borders();
            topChild.height = 50;
            topChild.background = Color.black;

            var leftChild = new Borders();
            leftChild.width = 50;
            leftChild.background = Color.black;

            var centerChild = new Borders();
            centerChild.background = Color.black;

            var rightChild = new Borders();
            //rightChild.width = 50; moved to third argument 'add' call
            rightChild.background = Color.black;

            var bottomChild = new Borders();
            bottomChild.height = 50;
            bottomChild.background = Color.black;

            borders.topComponent = topChild;
            borders.leftComponent = leftChild;
            borders.centerComponent = centerChild;
            //borders.rightComponent = rightChild;
            borders.add(rightChild, Ui.HorizontalPosition.RIGHT, 50);
            borders.bottomComponent = bottomChild;

            Invoke.later(function () {
                // top
                expect(topChild.width).toEqual(400);
                topChild.width += 10;
                expect(topChild.width).toEqual(400);
                expect(topChild.height).toEqual(50);
                topChild.height += 10;
                expect(topChild.height).toEqual(60);
                expect(topChild.left).toEqual(0);
                topChild.left += 10;
                expect(topChild.left).toEqual(0);
                expect(topChild.top).toEqual(0);
                topChild.top += 10;
                expect(topChild.top).toEqual(0);
                // bottom
                expect(bottomChild.width).toEqual(400);
                bottomChild.width += 10;
                expect(bottomChild.width).toEqual(400);
                expect(bottomChild.height).toEqual(50);
                bottomChild.height += 10;
                expect(bottomChild.height).toEqual(60);
                expect(bottomChild.left).toEqual(0);
                bottomChild.left += 10;
                expect(bottomChild.left).toEqual(0);
                expect(bottomChild.top).toEqual(340);
                bottomChild.top += 10;
                expect(bottomChild.top).toEqual(340);
                // left
                expect(leftChild.width).toEqual(50);
                leftChild.width += 10;
                expect(leftChild.width).toEqual(60);
                expect(leftChild.height).toEqual(280);
                leftChild.height += 10;
                expect(leftChild.height).toEqual(280);
                expect(leftChild.left).toEqual(0);
                leftChild.left += 10;
                expect(leftChild.left).toEqual(0);
                expect(leftChild.top).toEqual(60);
                leftChild.top += 10;
                expect(leftChild.top).toEqual(60);
                // right
                expect(rightChild.width).toEqual(50);
                rightChild.width += 10;
                expect(rightChild.width).toEqual(60);
                expect(rightChild.height).toEqual(280);
                rightChild.height += 10;
                expect(rightChild.height).toEqual(280);
                expect(rightChild.left).toEqual(340);
                rightChild.left += 10;
                expect(rightChild.left).toEqual(340);
                expect(rightChild.top).toEqual(60);
                rightChild.top += 10;
                expect(rightChild.top).toEqual(60);
                // center
                expect(centerChild.width).toEqual(280);
                centerChild.width += 10;
                expect(centerChild.width).toEqual(280);
                expect(centerChild.height).toEqual(280);
                centerChild.height += 10;
                expect(centerChild.height).toEqual(280);
                expect(centerChild.left).toEqual(60);
                centerChild.left += 10;
                expect(centerChild.left).toEqual(60);
                expect(centerChild.top).toEqual(60);
                centerChild.top += 10;
                expect(centerChild.top).toEqual(60);

                document.body.removeChild(borders.element);
                done();
            });

        });
    });
    it('Borders pane.attached hgap vgap', function (done) {
        require([
            'invoke',
            'common-utils/color',
            'forms/border-pane'
        ], function (
                Invoke,
                Color,
                Borders) {
            var borders = new Borders(10);
            borders.background = Color.blue;
            borders.width = borders.height = 400;
            document.body.appendChild(borders.element);

            var topChild = new Borders();
            topChild.height = 50;
            topChild.background = Color.black;

            var leftChild = new Borders();
            leftChild.width = 50;
            leftChild.background = Color.black;

            var centerChild = new Borders();
            centerChild.background = Color.black;

            var rightChild = new Borders();
            rightChild.width = 50;
            rightChild.background = Color.black;

            var bottomChild = new Borders();
            bottomChild.height = 50;
            bottomChild.background = Color.black;

            borders.topComponent = topChild;
            borders.leftComponent = leftChild;
            borders.centerComponent = centerChild;
            borders.rightComponent = rightChild;
            borders.bottomComponent = bottomChild;

            expect(borders.vgap).toEqual(0);
            borders.vgap = 10;

            Invoke.later(function () {
                // top
                expect(topChild.width).toEqual(400);
                expect(topChild.height).toEqual(50);
                expect(topChild.left).toEqual(0);
                expect(topChild.top).toEqual(0);
                // bottom
                expect(bottomChild.width).toEqual(400);
                expect(bottomChild.height).toEqual(50);
                expect(bottomChild.left).toEqual(0);
                expect(bottomChild.top).toEqual(350);
                // left
                expect(leftChild.width).toEqual(50);
                expect(leftChild.height).toEqual(280);
                expect(leftChild.left).toEqual(0);
                expect(leftChild.top).toEqual(60);
                // right
                expect(rightChild.width).toEqual(50);
                expect(rightChild.height).toEqual(280);
                expect(rightChild.left).toEqual(350);
                expect(rightChild.top).toEqual(60);
                // center
                expect(centerChild.width).toEqual(280);
                expect(centerChild.height).toEqual(280);
                expect(centerChild.left).toEqual(60);
                expect(centerChild.top).toEqual(60);

                document.body.removeChild(borders.element);
                done();
            });

        });
    });

    it('TabedPane.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'ui',
            'forms/containers/tabbed-pane'
        ], function (
                Font,
                Color,
                Cursor,
                Ui,
                TabbedPane
                ) {
            var tabs = new TabbedPane();
            expectContainer(tabs, Font, Color, Cursor);
            var tab0 = new TabbedPane();
            var tab1 = new TabbedPane();
            var tab2 = new TabbedPane();
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                tabs.add(tab0, 'tab1', loaded, 'tooltip1');
                tabs.add(tab1, 'tab2', loaded, 'tooltip2');
                tabs.add(tab2, 'tab3', loaded, 'tooltip3');
                expect(tabs.count).toEqual(3);
                expect(tabs.child(0)).toEqual(tab0);
                expect(tabs.child(1)).toEqual(tab1);
                expect(tabs.child(2)).toEqual(tab2);
                expect(tabs.indexOf(tab0)).toEqual(0);
                expect(tabs.indexOf(tab1)).toEqual(1);
                expect(tabs.indexOf(tab2)).toEqual(2);
                expect(tabs.children()).toEqual([tab0, tab1, tab2]);
                tabs.remove(tab1);
                expect(tabs.indexOf(tab0)).toEqual(0);
                expect(tabs.indexOf(tab2)).toEqual(1);
                expect(tabs.children()).toEqual([tab0, tab2]);
                expect(tabs.indexOf(tab0)).toEqual(0);
                expect(tabs.indexOf(tab1)).toEqual(-1);
                expect(tabs.indexOf(tab2)).toEqual(1);
                tabs.clear();
                expect(tabs.count).toEqual(0);
                expect(tabs.indexOf(tab0)).toEqual(-1);
                expect(tabs.indexOf(tab1)).toEqual(-1);
                expect(tabs.indexOf(tab2)).toEqual(-1);
                expect(tabs.children()).toEqual([]);
                done();
            });
        });
    });
    it('TabedPane.Markup', function (done) {
        require([
            'ui',
            'logger',
            'forms/containers/flow-pane',
            'forms/containers/tabbed-pane'
        ], function (
                Ui,
                Logger,
                FlowPane,
                TabbedPane
                ) {
            var tabs = new TabbedPane();
            tabs.onItemSelected = function (evt) {
                Logger.info('Item selected on: ' + evt.source.constructor.name);
            };
            document.body.appendChild(tabs.element);
            var tab0 = new FlowPane();
            tab0.background = '#bcbcfc';
            tab0.width = 250;
            tab0.height = 200;
            var tab1 = new FlowPane();
            tab1.background = '#fcacac';
            tab1.width = 200;
            tab1.height = 250;
            var tab2 = new FlowPane();
            tabs.add(tab0, 'tab0', null, 'tooltip0');
            tabs.add(tab2, 'tab2', null, 'tooltip2');
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                tabs.add(tab1, 'tab1', loaded, 'tooltip1', 1);
                expect(tabs.children()).toEqual([tab0, tab1, tab2]);
                document.body.removeChild(tabs.element);
                done();
            });
        });
    });
    it('TextArea.ScrollPane.Markup', function (done) {
        require([
            'invoke',
            'forms/containers/scroll-pane',
            'forms/fields/text-area'
        ], function (
                Invoke,
                ScrollPane,
                TextArea
                ) {
            var textArea = new TextArea();
            var scroll = new ScrollPane(textArea);
            var scroll1 = new ScrollPane(scroll);
            document.body.appendChild(scroll1.element);
            textArea.text =
                    'Sample text for text area, ' +
                    'Sample text for text area, ' +
                    'Sample text for text area, ' +
                    'Sample text for text area';
            Invoke.later(function () {
                expect(textArea.element.offsetWidth).toEqual(scroll.element.offsetWidth);
                expect(textArea.element.offsetHeight).toEqual(scroll.element.offsetHeight);
                expect(scroll1.element.offsetWidth).toEqual(scroll.element.offsetWidth);
                expect(scroll1.element.offsetHeight).toEqual(scroll.element.offsetHeight);
                var oldWidth = scroll.width;
                scroll.width -= 10;
                textArea.width -= 10;
                expect(scroll.width).toEqual(oldWidth);
                expect(textArea.width).toEqual(oldWidth);
                document.body.removeChild(scroll1.element);
                done();
            });
        });
    });
    it('RichTextArea.ScrollPane.Markup', function (done) {
        require([
            'invoke',
            'forms/containers/scroll-pane',
            'forms/fields/rich-text-area'
        ], function (
                Invoke,
                ScrollPane,
                RichTextArea
                ) {
            var textArea = new RichTextArea();
            var scroll = new ScrollPane(textArea);
            var scroll1 = new ScrollPane(scroll);
            document.body.appendChild(scroll1.element);
            textArea.text =
                    'Sample text for text area, ' +
                    'Sample text for text area, ' +
                    'Sample text for text area, ' +
                    'Sample text for text area';
            Invoke.later(function () {
                expect(textArea.element.offsetWidth).toEqual(scroll.element.offsetWidth);
                expect(textArea.element.offsetHeight).toEqual(scroll.element.offsetHeight);
                expect(scroll1.element.offsetWidth).toEqual(scroll.element.offsetWidth);
                expect(scroll1.element.offsetHeight).toEqual(scroll.element.offsetHeight);
                var oldWidth = scroll.width;
                scroll.width -= 10;
                textArea.width -= 10;
                expect(scroll.width).toEqual(oldWidth);
                expect(textArea.width).toEqual(oldWidth);
                document.body.removeChild(scroll1.element);
                done();
            });
        });
    });
    it('Box.Horizontal.ScrollPane.Markup', function (done) {
        require([
            'invoke',
            'forms/containers/scroll-pane',
            'forms/containers/box-pane'
        ], function (
                Invoke,
                ScrollPane,
                BoxPane
                ) {
            var box = new BoxPane();
            var scroll = new ScrollPane(box);
            scroll.width = scroll.height = 400;
            document.body.appendChild(scroll.element);
            Invoke.later(function () {
                expect(box.element.offsetHeight).toEqual(scroll.element.offsetHeight);
                var oldHeight = box.height;
                box.height -= 10;
                expect(box.height).toEqual(oldHeight);
                document.body.removeChild(scroll.element);
                done();
            });
        });
    });
    it('Box.Vertical.ScrollPane.Markup', function (done) {
        require([
            'ui',
            'invoke',
            'forms/containers/scroll-pane',
            'forms/containers/box-pane'
        ], function (
                Ui,
                Invoke,
                ScrollPane,
                BoxPane
                ) {
            var box = new BoxPane();
            box.orientation = Ui.Orientation.VERTICAL;
            var scroll = new ScrollPane(box);
            scroll.width = scroll.height = 400;
            document.body.appendChild(scroll.element);
            Invoke.later(function () {
                expect(box.element.offsetWidth).toEqual(scroll.element.offsetWidth);
                var oldWidth = box.width;
                box.width -= 10;
                expect(box.width).toEqual(oldWidth);
                document.body.removeChild(scroll.element);
                done();
            });
        });
    });
});

