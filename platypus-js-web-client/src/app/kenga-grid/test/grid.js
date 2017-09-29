/* global expect */
/* global NaN */

describe('Grid Api', function () {

    function expectColumns(instance) {
        expect(instance.headerLeft.columnsCount).toEqual(instance.frozenLeft.columnsCount);
        expect(instance.headerLeft.columnsCount).toEqual(instance.bodyLeft.columnsCount);
        expect(instance.headerLeft.columnsCount).toEqual(instance.footerLeft.columnsCount);
        expect(instance.headerRight.columnsCount).toEqual(instance.frozenRight.columnsCount);
        expect(instance.headerRight.columnsCount).toEqual(instance.bodyRight.columnsCount);
        expect(instance.headerRight.columnsCount).toEqual(instance.footerRight.columnsCount);
    }

    it('Header.Split.Plain', function (done) {
        require([
            'forms/grid/grcore/id',
            'forms/grid/columns/column-node',
            'forms/grid/columns/nodes/marker-service-node',
            'forms/grid/columns/nodes/check-box-service-node',
            'forms/grid/columns/nodes/radio-button-service-node'
        ], function (
                Grid,
                ColumnNode,
                MarkerColumnNode,
                CheckBoxColumnNode,
                RadioButtonColumnNode) {
            var instance = new Grid();
            var marker = new MarkerColumnNode();
            var check = new CheckBoxColumnNode();
            var radio = new RadioButtonColumnNode();
            var name = new ColumnNode();
            var birth = new ColumnNode();
            var payed = new ColumnNode();
            instance.addColumnNode(marker);
            instance.addColumnNode(check);
            instance.addColumnNode(radio);
            instance.addColumnNode(name);
            instance.addColumnNode(birth);
            instance.addColumnNode(payed);
            expect(instance.headerRight.columnsCount).toEqual(instance.columnsCount);
            expect(instance.headerLeft.columnsCount).toEqual(0);
            expect(instance.headerRight.columnsCount).toEqual(6);
            expectColumns(instance);
            instance.frozenColumns = 1;
            expect(instance.headerLeft.columnsCount).toEqual(1);
            expect(instance.headerRight.columnsCount).toEqual(5);
            expectColumns(instance);
            instance.frozenColumns = 2;
            expect(instance.headerLeft.columnsCount).toEqual(2);
            expect(instance.headerRight.columnsCount).toEqual(4);
            expectColumns(instance);
            instance.frozenColumns = 3;
            expect(instance.headerLeft.columnsCount).toEqual(3);
            expect(instance.headerRight.columnsCount).toEqual(3);
            expectColumns(instance);
            instance.frozenColumns = 4;
            expect(instance.headerLeft.columnsCount).toEqual(4);
            expect(instance.headerRight.columnsCount).toEqual(2);
            expectColumns(instance);
            instance.frozenColumns = 5;
            expect(instance.headerLeft.columnsCount).toEqual(5);
            expect(instance.headerRight.columnsCount).toEqual(1);
            expectColumns(instance);
            instance.frozenColumns = 6;
            expect(instance.headerLeft.columnsCount).toEqual(6);
            expect(instance.headerRight.columnsCount).toEqual(0);
            instance.frozenColumns = 7;
            expect(instance.frozenColumns).toEqual(6);
            instance.frozenColumns = -1;
            expect(instance.frozenColumns).toEqual(6);
            done();
        });
    });
    it('Header.Split.TwoLayers.1', function (done) {
        require([
            'forms/grid/grcore/id',
            'forms/grid/columns/column-node',
            'forms/grid/columns/nodes/marker-service-node',
            'forms/grid/columns/nodes/check-box-service-node',
            'forms/grid/columns/nodes/radio-button-service-node'
        ], function (
                Grid,
                ColumnNode,
                MarkerColumnNode,
                CheckBoxColumnNode,
                RadioButtonColumnNode) {
            var instance = new Grid();
            var marker = new MarkerColumnNode();
            var marker1 = new MarkerColumnNode();
            marker1.addColumnNode(marker);
            var check = new CheckBoxColumnNode();
            var check1 = new CheckBoxColumnNode();
            check1.addColumnNode(check);
            var radio = new RadioButtonColumnNode();
            var radio1 = new RadioButtonColumnNode();
            radio1.addColumnNode(radio);
            var name = new ColumnNode();
            var name1 = new ColumnNode();
            name1.addColumnNode(name);
            var birth = new ColumnNode();
            var birth1 = new ColumnNode();
            birth1.addColumnNode(birth);
            var payed = new ColumnNode();
            var payed1 = new ColumnNode();
            payed1.addColumnNode(payed);

            instance.header = [
                marker1,
                check1,
                radio1,
                name1,
                birth1,
                payed1
            ];

            expect(instance.headerRight.columnsCount).toEqual(instance.columnsCount);
            expect(instance.headerLeft.columnsCount).toEqual(0);
            expect(instance.headerRight.columnsCount).toEqual(6);
            instance.frozenColumns = 1;
            expect(instance.headerLeft.columnsCount).toEqual(1);
            expect(instance.headerRight.columnsCount).toEqual(5);
            instance.frozenColumns = 2;
            expect(instance.headerLeft.columnsCount).toEqual(2);
            expect(instance.headerRight.columnsCount).toEqual(4);
            instance.frozenColumns = 3;
            expect(instance.headerLeft.columnsCount).toEqual(3);
            expect(instance.headerRight.columnsCount).toEqual(3);
            instance.frozenColumns = 4;
            expect(instance.headerLeft.columnsCount).toEqual(4);
            expect(instance.headerRight.columnsCount).toEqual(2);
            instance.frozenColumns = 5;
            expect(instance.headerLeft.columnsCount).toEqual(5);
            expect(instance.headerRight.columnsCount).toEqual(1);
            instance.frozenColumns = 6;
            expect(instance.headerLeft.columnsCount).toEqual(6);
            expect(instance.headerRight.columnsCount).toEqual(0);
            instance.frozenColumns = 7;
            expect(instance.frozenColumns).toEqual(6);
            instance.frozenColumns = -1;
            expect(instance.frozenColumns).toEqual(6);
            done();
        });
    });
    it('Header.Split.TwoLayers.2', function (done) {
        require([
            'forms/grid/grcore/id',
            'forms/grid/columns/column-node',
            'forms/grid/columns/nodes/marker-service-node',
            'forms/grid/columns/nodes/check-box-service-node',
            'forms/grid/columns/nodes/radio-button-service-node'
        ], function (
                Grid,
                ColumnNode,
                MarkerColumnNode,
                CheckBoxColumnNode,
                RadioButtonColumnNode) {
            var instance = new Grid();
            document.body.appendChild(instance.element);

            var service = new ColumnNode();
            service.title = 'service';

            var marker = new MarkerColumnNode();
            service.addColumnNode(marker);
            var check = new CheckBoxColumnNode();
            service.addColumnNode(check);
            var radio = new RadioButtonColumnNode();
            service.addColumnNode(radio);

            var semantic = new ColumnNode();
            semantic.title = 'semantic';

            var name = new ColumnNode();
            name.title = 'name';
            semantic.addColumnNode(name);
            var birth = new ColumnNode();
            birth.title = 'birth';
            semantic.addColumnNode(birth);
            var payed = new ColumnNode();
            payed.title = 'payed';
            semantic.addColumnNode(payed);

            instance.addColumnNode(service);
            instance.addColumnNode(semantic);

            expect(instance.headerRight.columnsCount).toEqual(instance.columnsCount);
            expect(instance.headerLeft.columnsCount).toEqual(0);
            expect(instance.headerRight.columnsCount).toEqual(6);
            instance.frozenColumns = 1;
            expect(instance.headerLeft.columnsCount).toEqual(1);
            expect(instance.headerRight.columnsCount).toEqual(5);
            instance.frozenColumns = 2;
            expect(instance.headerLeft.columnsCount).toEqual(2);
            expect(instance.headerRight.columnsCount).toEqual(4);
            instance.frozenColumns = 3;
            expect(instance.headerLeft.columnsCount).toEqual(3);
            expect(instance.headerRight.columnsCount).toEqual(3);
            instance.frozenColumns = 4;
            expect(instance.headerLeft.columnsCount).toEqual(4);
            expect(instance.headerRight.columnsCount).toEqual(2);
            instance.frozenColumns = 5;
            expect(instance.headerLeft.columnsCount).toEqual(5);
            expect(instance.headerRight.columnsCount).toEqual(1);
            instance.frozenColumns = 6;
            expect(instance.headerLeft.columnsCount).toEqual(6);
            expect(instance.headerRight.columnsCount).toEqual(0);
            instance.frozenColumns = 7;
            expect(instance.frozenColumns).toEqual(6);
            instance.frozenColumns = -1;
            expect(instance.frozenColumns).toEqual(6);
            document.body.removeChild(instance.element);
            done();
        });
    });
    it('Header.Split.TwoLayers.3', function (done) {
        require([
            'forms/grid/grcore/id',
            'forms/grid/columns/column-node',
            'forms/grid/columns/nodes/marker-service-node',
            'forms/grid/columns/nodes/check-box-service-node',
            'forms/grid/columns/nodes/radio-button-service-node'
        ], function (
                Grid,
                ColumnNode,
                MarkerColumnNode,
                CheckBoxColumnNode,
                RadioButtonColumnNode) {
            var instance = new Grid();
            document.body.appendChild(instance.element);

            var marker = new MarkerColumnNode();
            instance.addColumnNode(marker);
            var check = new CheckBoxColumnNode();
            instance.addColumnNode(check);
            var radio = new RadioButtonColumnNode();
            instance.addColumnNode(radio);

            var semantic = new ColumnNode();
            semantic.title = 'semantic';

            var name = new ColumnNode();
            name.title = 'name';
            semantic.addColumnNode(name);
            var birth = new ColumnNode();
            birth.title = 'birth';
            semantic.addColumnNode(birth);
            var payed = new ColumnNode();
            payed.title = 'payed';
            semantic.addColumnNode(payed);

            instance.addColumnNode(semantic);

            expect(instance.headerRight.columnsCount).toEqual(instance.columnsCount);
            expect(instance.headerLeft.columnsCount).toEqual(0);
            expect(instance.headerRight.columnsCount).toEqual(6);
            instance.frozenColumns = 1;
            expect(instance.headerLeft.columnsCount).toEqual(1);
            expect(instance.headerRight.columnsCount).toEqual(5);
            instance.frozenColumns = 2;
            expect(instance.headerLeft.columnsCount).toEqual(2);
            expect(instance.headerRight.columnsCount).toEqual(4);
            instance.frozenColumns = 3;
            expect(instance.headerLeft.columnsCount).toEqual(3);
            expect(instance.headerRight.columnsCount).toEqual(3);
            instance.frozenColumns = 4;
            expect(instance.headerLeft.columnsCount).toEqual(4);
            expect(instance.headerRight.columnsCount).toEqual(2);
            instance.frozenColumns = 5;
            expect(instance.headerLeft.columnsCount).toEqual(5);
            expect(instance.headerRight.columnsCount).toEqual(1);
            instance.frozenColumns = 6;
            expect(instance.headerLeft.columnsCount).toEqual(6);
            expect(instance.headerRight.columnsCount).toEqual(0);
            instance.frozenColumns = 7;
            expect(instance.frozenColumns).toEqual(6);
            instance.frozenColumns = -1;
            expect(instance.frozenColumns).toEqual(6);

            document.body.removeChild(instance.element);
            done();
        });
    });

    var plainSamples = [];
    (function () {
        var moment = new Date();
        var dataSize = 10;
        while (plainSamples.length < dataSize) {
            plainSamples.push({
                marker: true,
                check: plainSamples.length % 2 === 0,
                radio: false,
                name: 'title' + plainSamples.length,
                birth: new Date(moment.valueOf() + plainSamples.length * 10),
                payed: true
            });
        }
    }());

    it('Rows.Frozen Columns.Frozen', function (done) {
        require([
            'forms/grid/grcore/id',
            'forms/grid/columns/column-node',
            'forms/grid/columns/nodes/marker-service-node',
            'forms/grid/columns/nodes/check-box-service-node',
            'forms/grid/columns/nodes/radio-button-service-node'
        ], function (
                Grid,
                ColumnNode,
                MarkerColumnNode,
                CheckBoxColumnNode,
                RadioButtonColumnNode) {
            var instance = new Grid();
            instance.width = instance.height = 250;
            instance.frozenRows = 2;
            document.body.appendChild(instance.element);

            var marker = new MarkerColumnNode();
            instance.addColumnNode(marker);
            var check = new CheckBoxColumnNode();
            instance.addColumnNode(check);
            var radio = new RadioButtonColumnNode();
            instance.addColumnNode(radio);

            var semantic = new ColumnNode();
            semantic.title = 'semantic';

            var name = new ColumnNode();
            name.field = name.title = 'name';

            semantic.addColumnNode(name);
            var birth = new ColumnNode();
            birth.editor = null;
            birth.field = birth.title = 'birth';
            birth.width = 170;
            birth.visible = false;
            semantic.addColumnNode(birth);
            var payed = new ColumnNode();
            payed.field = payed.title = 'payed';
            semantic.addColumnNode(payed);

            instance.addColumnNode(semantic);

            instance.frozenColumns = 4;

            instance.data = plainSamples;

            expect(instance.frozenLeft.rowsCount).toEqual(2);
            expect(instance.frozenRight.rowsCount).toEqual(2);
            expect(instance.bodyLeft.rowsCount).toEqual(5);// 5 instead of 8 because of virtual nature of grid
            expect(instance.bodyRight.rowsCount).toEqual(5);

            expect(instance.frozenLeft.columnsCount).toEqual(4);
            expect(instance.bodyLeft.columnsCount).toEqual(4);
            expect(instance.frozenRight.columnsCount).toEqual(2);
            expect(instance.bodyRight.columnsCount).toEqual(2);

            document.body.removeChild(instance.element);
            done();
        });
    });
    it('Rows.Sorting', function (done) {
        require([
            'forms/grid/grcore/id',
            'forms/grid/columns/column-node',
            'forms/grid/columns/nodes/marker-service-node',
            'forms/grid/columns/nodes/check-box-service-node',
            'forms/grid/columns/nodes/radio-button-service-node'
        ], function (
                Grid,
                ColumnNode,
                MarkerColumnNode,
                CheckBoxColumnNode,
                RadioButtonColumnNode) {
            var instance = new Grid();
            instance.width = instance.height = 250;
            instance.frozenRows = 2;
            document.body.appendChild(instance.element);

            var marker = new MarkerColumnNode();
            instance.addColumnNode(marker);
            var check = new CheckBoxColumnNode();
            instance.addColumnNode(check);
            var radio = new RadioButtonColumnNode();
            instance.addColumnNode(radio);

            var semantic = new ColumnNode();
            semantic.title = 'semantic';

            var name = new ColumnNode();
            name.field = name.title = 'name';

            semantic.addColumnNode(name);
            var birth = new ColumnNode();
            birth.editor = null;
            birth.field = birth.title = 'birth';
            birth.width = 170;
            birth.visible = false;
            semantic.addColumnNode(birth);
            var payed = new ColumnNode();
            payed.field = payed.title = 'payed';
            semantic.addColumnNode(payed);

            instance.addColumnNode(semantic);

            instance.frozenColumns = 4;

            instance.data = plainSamples;

            birth.sort();
            birth.sortDesc();
            birth.unsort();
            birth.sort();
            birth.sortDesc();
            instance.unsort();

            expect(instance.frozenLeft.rowsCount).toEqual(2);
            expect(instance.frozenRight.rowsCount).toEqual(2);
            expect(instance.bodyLeft.rowsCount).toEqual(5);// 5 instead of 8 because of virtual nature of grid
            expect(instance.bodyRight.rowsCount).toEqual(5);

            expect(instance.frozenLeft.columnsCount).toEqual(4);
            expect(instance.bodyLeft.columnsCount).toEqual(4);
            expect(instance.frozenRight.columnsCount).toEqual(2);
            expect(instance.bodyRight.columnsCount).toEqual(2);

            document.body.removeChild(instance.element);
            done();
        });
    });
    it('Rows.Rendering', function (done) {
        require([
            'ui/utils',
            'core/invoke',
            'core/logger',
            'forms/grid/grcore/id'
        ], function (
                Ui,
                Invoke,
                Logger,
                Grid) {
            var instance = new Grid();
            done();
        });
    });
    fit('Rows.Dragging', function (done) {
        require([
            'core/logger',
            'forms/grid/grcore/id',
            'forms/grid/columns/column-node',
            'forms/grid/columns/nodes/order-num-service-node',
            'forms/grid/columns/nodes/marker-service-node',
            'forms/grid/columns/nodes/check-box-service-node',
            'forms/grid/columns/nodes/radio-button-service-node'
        ], function (
                Logger,
                Grid,
                ColumnNode,
                OrderNumServiceColumnNode,
                MarkerColumnNode,
                CheckBoxColumnNode,
                RadioButtonColumnNode) {
            var instance = new Grid();
            instance.width = instance.height = 300;
            instance.frozenRows = 2;
            document.body.appendChild(instance.element);

            var nmb = new OrderNumServiceColumnNode();
            instance.addColumnNode(nmb);
            var marker = new MarkerColumnNode();
            instance.addColumnNode(marker);
            var check = new CheckBoxColumnNode();
            instance.addColumnNode(check);
            var radio = new RadioButtonColumnNode();
            instance.addColumnNode(radio);

            var semantic = new ColumnNode();
            semantic.title = 'semantic';

            var name = new ColumnNode();
            name.field = name.title = 'name';

            semantic.addColumnNode(name);
            var birth = new ColumnNode();
            birth.editor = birth.renderer = null;
            birth.field = birth.title = 'birth';
            birth.width = 170;
            birth.visible = false;
            semantic.addColumnNode(birth);
            var payed = new ColumnNode();
            payed.field = payed.title = 'payed';
            semantic.addColumnNode(payed);

            instance.addColumnNode(semantic);

            instance.frozenColumns = 4;

            instance.data = treeSamples;
            instance.parentField = 'parent';
            instance.childrenField = 'children';

            instance.draggableRows = true;
            instance.onDragBefore = function (row, before) {
                Logger.info('Drag of row: ' + row + '; before: ' + before);
            };
            instance.onDragInto = function (row, into) {
                Logger.info('Drag of row: ' + row + '; into: ' + into);
            };
            instance.onDragAfter = function (row, after) {
                Logger.info('Drag of row: ' + row + '; after: ' + after);
            };
            instance.onDropBefore = function (row, before) {
                Logger.info('Drop of row: ' + row + '; before: ' + before);
            };
            instance.onDropInto = function (row, into) {
                Logger.info('Drop of row: ' + row + '; into: ' + into);
            };
            instance.onDropAfter = function (row, after) {
                Logger.info('Drop of row: ' + row + '; after: ' + after);
            };

            expect(instance.frozenLeft.rowsCount).toEqual(2);
            expect(instance.frozenRight.rowsCount).toEqual(2);
            expect(instance.bodyLeft.rowsCount).toEqual(6);// 6 instead of 8 because of grid virtualization
            expect(instance.bodyRight.rowsCount).toEqual(6);

            expect(instance.frozenLeft.columnsCount).toEqual(4);
            expect(instance.bodyLeft.columnsCount).toEqual(4);
            expect(instance.frozenRight.columnsCount).toEqual(3);
            expect(instance.bodyRight.columnsCount).toEqual(3);

            document.body.removeChild(instance.element);
            done();
        });
    });
    var treeSamples = [];
    (function () {
        var maxDepth = 2;
        var childrenCount = 5;
        function generateChildren(parent, deepness) {
            if (deepness < maxDepth) {
                deepness++;
                parent.children = [];
                for (var c = 0; c < childrenCount; c++) {
                    var child = {
                        marker: true,
                        check: treeSamples.length % 2 === 0,
                        radio: false,
                        name: parent.name + ':' + c,
                        birth: new Date(moment.valueOf() + plainSamples.length * 10),
                        payed: true
                    };
                    child.parent = parent;
                    parent.children.push(child);
                    treeSamples.push(child);
                    generateChildren(child, deepness);
                }
            }
        }

        var moment = new Date();
        var rootsCount = 10;
        var r = 0;
        while (r < rootsCount) {
            var sample = {
                marker: true,
                check: treeSamples.length % 2 === 0,
                radio: false,
                name: 'title ' + r,
                birth: new Date(moment.valueOf() + treeSamples.length * 10),
                payed: true
            };
            treeSamples.push(sample);
            if (r === 4) {
                treeSamples.cursor = sample;
            }
            generateChildren(sample, 0);
            r++;
        }
    }());
    it('Rows.Tree', function (done) {
        require([
            'forms/grid/grcore/id',
            'forms/grid/columns/column-node',
            'forms/grid/columns/nodes/order-num-service-node',
            'forms/grid/columns/nodes/marker-service-node',
            'forms/grid/columns/nodes/check-box-service-node',
            'forms/grid/columns/nodes/radio-button-service-node'
        ], function (
                Grid,
                ColumnNode,
                OrderNumServiceColumnNode,
                MarkerColumnNode,
                CheckBoxColumnNode,
                RadioButtonColumnNode) {
            var instance = new Grid();
            instance.width = instance.height = 250;
            instance.frozenRows = 2;
            document.body.appendChild(instance.element);

            var nmb = new OrderNumServiceColumnNode();
            instance.addColumnNode(nmb);
            var marker = new MarkerColumnNode();
            instance.addColumnNode(marker);
            var check = new CheckBoxColumnNode();
            instance.addColumnNode(check);
            var radio = new RadioButtonColumnNode();
            instance.addColumnNode(radio);

            var semantic = new ColumnNode();
            semantic.title = 'semantic';

            var name = new ColumnNode();
            name.field = name.title = 'name';

            semantic.addColumnNode(name);
            var birth = new ColumnNode();
            birth.editor = null;
            birth.field = birth.title = 'birth';
            birth.width = 170;
            birth.visible = false;
            semantic.addColumnNode(birth);
            var payed = new ColumnNode();
            payed.field = payed.title = 'payed';
            semantic.addColumnNode(payed);

            instance.addColumnNode(semantic);

            instance.frozenColumns = 4;

            instance.data = treeSamples;
            instance.parentField = 'parent';
            instance.childrenField = 'children';

            birth.sort();
            birth.sortDesc();
            birth.unsort();
            birth.sort();
            birth.sortDesc();
            instance.unsort();

            expect(instance.frozenLeft.rowsCount).toEqual(2);
            expect(instance.frozenRight.rowsCount).toEqual(2);
            expect(instance.bodyLeft.rowsCount).toEqual(5);// 5 instead of 8 because of virtual nature of grid
            expect(instance.bodyRight.rowsCount).toEqual(5);

            expect(instance.frozenLeft.columnsCount).toEqual(4);
            expect(instance.bodyLeft.columnsCount).toEqual(4);
            expect(instance.frozenRight.columnsCount).toEqual(3);
            expect(instance.bodyRight.columnsCount).toEqual(3);

            document.body.removeChild(instance.element);
            done();
        });
    });
    it('Columns.Performance', function (done) {
        require([
            'ui/utils',
            'core/invoke',
            'core/logger',
            'forms/grid/grcore/id'
        ], function (
                Ui,
                Invoke,
                Logger,
                Grid) {
            var instance = new Grid();
            done();
        });
    });
    it('Editing.Inline', function (done) {
        require([
            'forms/grid/grcore/id',
            'forms/grid/columns/column-node',
            'forms/grid/columns/nodes/order-num-service-node',
            'forms/grid/columns/nodes/marker-service-node',
            'forms/grid/columns/nodes/check-box-service-node',
            'forms/grid/columns/nodes/radio-button-service-node'
        ], function (
                Grid,
                ColumnNode,
                OrderNumServiceColumnNode,
                MarkerColumnNode,
                CheckBoxColumnNode,
                RadioButtonColumnNode) {
            var instance = new Grid();
            instance.width = instance.height = 300;
            instance.frozenRows = 2;
            document.body.appendChild(instance.element);

            var nmb = new OrderNumServiceColumnNode();
            instance.addColumnNode(nmb);
            var marker = new MarkerColumnNode();
            instance.addColumnNode(marker);
            var check = new CheckBoxColumnNode();
            instance.addColumnNode(check);
            var radio = new RadioButtonColumnNode();
            instance.addColumnNode(radio);

            var semantic = new ColumnNode();
            semantic.title = 'semantic';

            var name = new ColumnNode();
            name.field = name.title = 'name';

            semantic.addColumnNode(name);
            var birth = new ColumnNode();
            birth.editor = birth.renderer = null;
            birth.field = birth.title = 'birth';
            birth.width = 170;
            birth.visible = false;
            semantic.addColumnNode(birth);
            var payed = new ColumnNode();
            payed.field = payed.title = 'payed';
            semantic.addColumnNode(payed);

            instance.addColumnNode(semantic);

            instance.frozenColumns = 4;

            instance.data = treeSamples;
            instance.parentField = 'parent';
            instance.childrenField = 'children';

            birth.sort();
            birth.sortDesc();
            birth.unsort();
            birth.sort();
            birth.sortDesc();
            instance.unsort();

            expect(instance.frozenLeft.rowsCount).toEqual(2);
            expect(instance.frozenRight.rowsCount).toEqual(2);
            expect(instance.bodyLeft.rowsCount).toEqual(6);// 6 instead of 8 because of grid virtualization
            expect(instance.bodyRight.rowsCount).toEqual(6);

            expect(instance.frozenLeft.columnsCount).toEqual(4);
            expect(instance.bodyLeft.columnsCount).toEqual(4);
            expect(instance.frozenRight.columnsCount).toEqual(3);
            expect(instance.bodyRight.columnsCount).toEqual(3);

            document.body.removeChild(instance.element);
            done();
        });
    });
    it('Editing.Popup', function (done) {
        require([
            'core/logger',
            'forms/grid/grcore/id'
        ], function (
                Logger,
                Grid) {
            var instance = new Grid();
            done();
        });
    });
    it('Events', function (done) {
        require([
            'ui/utils',
            'core/invoke',
            'core/logger',
            'forms/grid/grcore/id'
        ], function (
                Ui,
                Invoke,
                Logger,
                Grid) {
            var instance = new Grid();
            done();
        });
    });
});
