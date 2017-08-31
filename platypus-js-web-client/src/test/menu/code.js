/* global expect */
/* global NaN */

describe('Menu Api', function () {
    
    function expectMenuItem(w, Ui) {
        var h = [Ui.HorizontalPosition.LEFT, Ui.HorizontalPosition.CENTER, Ui.HorizontalPosition.RIGHT];
        h.forEach(function (hi) {
            w.horizontalTextPosition = hi;
            expect(w.horizontalTextPosition).toEqual(hi);
        });
    }

    it('MenuItem.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'ui',
            'forms/menu/menu-item'], function (
                Font,
                Color,
                Cursor,
                Ui,
                MenuItem) {
            var label1 = new MenuItem('txt', null, 45);
            expectMenuItem(label1, Ui);
            expect(label1.text).toEqual('txt');
            expect(label1.icon).toBeNull();
            expect(label1.iconTextGap).toEqual(45);
            var label2 = new MenuItem('txt', null);
            expectMenuItem(label2, Ui);
            expect(label2.text).toEqual('txt');
            expect(label2.icon).toBeNull();
            expect(label2.iconTextGap).toEqual(4);
            var label3 = new MenuItem('txt');
            expectMenuItem(label3, Ui);
            expect(label3.text).toEqual('txt');
            expect(label3.icon).toBeNull();
            expect(label3.iconTextGap).toEqual(4);
            var label4 = new MenuItem();
            expectMenuItem(label4, Ui);
            expectWidget(label4, Font, Color, Cursor);
            label4.text = 'Sample label';
            expect(label4.iconTextGap).toEqual(4);
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                label4.icon = loaded;
                done();
            }, function (e) {
                done.fail(e);
            });
        });
    });
    it('Label.Markup', function (done) {
        require([
            'ui',
            'forms/label'], function (
                Ui,
                Label) {
            var label = new Label();
            document.body.appendChild(label.element);
            label.text = 'Sample label';
            expect(label.iconTextGap).toEqual(4);
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                label.icon = loaded;
                // defaults
                // right text
                expect(label.horizontalTextPosition).toEqual(Ui.HorizontalPosition.RIGHT);
                expect(label.verticalTextPosition).toEqual(Ui.VerticalPosition.CENTER);
                (function () {
                    var image = label.element.firstElementChild;
                    var paragraph = label.element.lastElementChild;
                    expect(image.offsetLeft).toEqual(0);
                    expect(paragraph.offsetLeft).toEqual(16 + 4);
                }());
                // top and bottom
                label.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                label.verticalTextPosition = Ui.VerticalPosition.TOP;
                // left text
                label.horizontalTextPosition = Ui.HorizontalPosition.LEFT;
                (function () {
                    var image = label.element.lastElementChild;
                    var paragraph = label.element.firstElementChild;
                    expect(paragraph.offsetLeft).toEqual(0);
                    expect(image.offsetLeft).toEqual(paragraph.offsetWidth + 4);
                }());
                // top and bottom
                label.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                label.verticalTextPosition = Ui.VerticalPosition.TOP;

                // center text
                label.horizontalTextPosition = Ui.HorizontalPosition.CENTER;

                // top and bottom
                label.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                (function () {
                    var image = label.element.firstElementChild;
                    var paragraph = label.element.lastElementChild;
                    expect(image.offsetTop).toEqual(0);
                    expect(paragraph.offsetTop).toEqual(16 + 4);
                }());
                label.verticalTextPosition = Ui.VerticalPosition.TOP;
                (function () {
                    var image = label.element.lastElementChild;
                    var paragraph = label.element.firstElementChild;
                    expect(image.offsetTop).toBeGreaterThan(paragraph.offsetTop);
                }());
                // center center
                label.verticalTextPosition = Ui.VerticalPosition.CENTER;

                document.body.removeChild(label.element);
                done();
            }, function (e) {
                done.fail(e);
            });
        });
    });
});
