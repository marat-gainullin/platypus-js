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
        expect('componentPopupMenu' in widget).toBeTruthy();
        expectValue(widget, 'componentPopupMenu', new widget.constructor());
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
        expectValue(widget, 'onComponentShown', function(){});
        expect('onComponentHidden' in widget).toBeTruthy();
        expectValue(widget, 'onComponentHidden', function(){});
        expect('onMouseDragged' in widget).toBeTruthy();
        expectValue(widget, 'onMouseDragged', function(){});
        expect('onMouseReleased' in widget).toBeTruthy();
        expectValue(widget, 'onMouseReleased', function(){});
        expect('onFocusLost' in widget).toBeTruthy();
        expectValue(widget, 'onFocusLost', function(){});
        expect('onMousePressed' in widget).toBeTruthy();
        expectValue(widget, 'onMousePressed', function(){});
        expect('onMouseEntered' in widget).toBeTruthy();
        expectValue(widget, 'onMouseEntered', function(){});
        expect('onMouseMoved' in widget).toBeTruthy();
        expectValue(widget, 'onMouseMoved', function(){});
        expect('onActionPerformed' in widget).toBeTruthy();
        expectValue(widget, 'onActionPerformed', function(){});
        expect('onKeyReleased' in widget).toBeTruthy();
        expectValue(widget, 'onKeyReleased', function(){});
        expect('onKeyTyped' in widget).toBeTruthy();
        expectValue(widget, 'onKeyTyped', function(){});
        expect('onMouseWheelMoved' in widget).toBeTruthy();
        expectValue(widget, 'onMouseWheelMoved', function(){});
        expect('onFocusGained' in widget).toBeTruthy();
        expectValue(widget, 'onFocusGained', function(){});
        expect('onMouseClicked' in widget).toBeTruthy();
        expectValue(widget, 'onMouseClicked', function(){});
        expect('onMouseExited' in widget).toBeTruthy();
        expectValue(widget, 'onMouseExited', function(){});
        expect('onKeyPressed' in widget).toBeTruthy();
        expectValue(widget, 'onKeyPressed', function(){});
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
        expectValue(container, 'onComponentAdded', function(){});
        expect('onComponentRemoved' in container).toBeTruthy();
        expectValue(container, 'onComponentRemoved', function(){});
    }

    it('Color Api', function(done){
        require(['common-utils/color'], function(Color){
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

    it('Flow pane', function (done) {
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
});