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
            'forms/grid/grid',
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
            'forms/grid/grid',
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
            'forms/grid/grid',
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
            'forms/grid/grid',
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

    var samples = [];
    (function () {
        var dataSize = 10;
        while (samples.length < dataSize) {
            samples.push({
                marker: true,
                check: samples.length % 2 === 0,
                radio: false,
                name: 'title' + samples.length,
                birth: new Date(),
                payed: true
            });
        }
    }());

    fit('Rows.Frozen', function (done) {
        require([
            'forms/grid/grid',
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
            instance.width = instance.height = 400;
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

            instance.data = samples;

            //document.body.removeChild(instance.element);
            done();
        });
    });
    it('Rows.Performance', function (done) {
        require([
            'ui',
            'invoke',
            'logger',
            'forms/grid/grid'
        ], function (
                Ui,
                Invoke,
                Logger,
                Grid) {
            var instance = new Grid();
            done();
        });
    });
    it('Rows.Sorting', function (done) {
        require([
            'ui',
            'invoke',
            'logger',
            'forms/grid/grid'
        ], function (
                Ui,
                Invoke,
                Logger,
                Grid) {
            var instance = new Grid();
            done();
        });
    });
    it('Rows.Rendering', function (done) {
        require([
            'ui',
            'invoke',
            'logger',
            'forms/grid/grid'
        ], function (
                Ui,
                Invoke,
                Logger,
                Grid) {
            var instance = new Grid();
            done();
        });
    });
    it('Rows.Tree', function (done) {
        require([
            'ui',
            'invoke',
            'logger',
            'forms/grid/grid'
        ], function (
                Ui,
                Invoke,
                Logger,
                Grid) {
            var instance = new Grid();
            done();
        });
    });
    it('Columns.Performance', function (done) {
        require([
            'ui',
            'invoke',
            'logger',
            'forms/grid/grid'
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
            'logger',
            'forms/grid/grid'
        ], function (
                Logger,
                Grid) {
            var instance = new Grid();
            done();
        });
    });
    it('Editing.Popup', function (done) {
        require([
            'logger',
            'forms/grid/grid'
        ], function (
                Logger,
                Grid) {
            var instance = new Grid();
            done();
        });
    });
    it('Events', function (done) {
        require([
            'ui',
            'invoke',
            'logger',
            'forms/grid/grid'
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
