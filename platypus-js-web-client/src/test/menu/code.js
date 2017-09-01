/* global expect */
/* global NaN */

describe('Menu Api', function () {

    function expectHorizontalTextPosition(w, Ui) {
        var h = [Ui.HorizontalPosition.LEFT, Ui.HorizontalPosition.CENTER, Ui.HorizontalPosition.RIGHT];
        h.forEach(function (hi) {
            w.horizontalTextPosition = hi;
            expect(w.horizontalTextPosition).toEqual(hi);
        });
    }

    function expectMenuItemStructure(MenuItem, Ui, Font, Color, Cursor, done) {
        var image = document.createElement('image');
        var menuItem1 = new MenuItem('txt', image, function () {});
        expect(menuItem1.text).toEqual('txt');
        expect(menuItem1.icon).toBe(image);
        expect(menuItem1.onActionPerformed).toBeTruthy();
        expectHorizontalTextPosition(menuItem1, Ui);
        expectWidget(menuItem1, Font, Color, Cursor);

        var menuItem2 = new MenuItem('txt', image);
        expect(menuItem2.text).toEqual('txt');
        expect(menuItem2.icon).toBe(image);
        expect(menuItem2.onActionPerformed).toBeFalsy();
        expectHorizontalTextPosition(menuItem2, Ui);

        var menuItem3 = new MenuItem('txt');
        expect(menuItem3.text).toEqual('txt');
        expect(menuItem3.icon).toBeNull();
        expect(menuItem3.onActionPerformed).toBeFalsy();
        expectHorizontalTextPosition(menuItem3, Ui);

        var menuItem4 = new MenuItem();
        expect(menuItem4.text).toEqual('');
        expect(menuItem4.icon).toBeNull();
        expect(menuItem4.onActionPerformed).toBeFalsy();
        expectHorizontalTextPosition(menuItem4, Ui);

        menuItem4.text = 'Sample label';
        expect(menuItem4.iconTextGap).toEqual(4);
        Ui.Icon.load('assets/binary-content.png', function (loaded) {
            menuItem4.icon = loaded;
            done();
        }, function (e) {
            done.fail(e);
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
            expectMenuItemStructure(MenuItem, Ui, Font, Color, Cursor, done);
        });
    });
    it('MenuItem.Markup', function (done) {
        require([
            'ui',
            'forms/menu/menu-item'], function (
                Ui,
                MenuItem) {
            var menuItem = new MenuItem();
            document.body.appendChild(menuItem.element);
            menuItem.text = 'Sample menu item';
            expect(menuItem.iconTextGap).toEqual(4);
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                menuItem.icon = loaded;
                // defaults
                // right text
                expect(menuItem.horizontalTextPosition).toEqual(Ui.HorizontalPosition.RIGHT);
                (function () {
                    var image = menuItem.element.firstElementChild;
                    var paragraph = menuItem.element.lastElementChild;
                    expect(image.offsetLeft).toEqual(0);
                    expect(paragraph.offsetLeft).toEqual(16 + 4);
                }());
                // left text
                menuItem.horizontalTextPosition = Ui.HorizontalPosition.LEFT;
                (function () {
                    var image = menuItem.element.lastElementChild;
                    var paragraph = menuItem.element.firstElementChild;
                    expect(paragraph.offsetLeft).toEqual(0);
                    expect(image.offsetLeft).toEqual(paragraph.offsetWidth + 4);
                }());
                document.body.removeChild(menuItem.element);
                done();
            }, function (e) {
                done.fail(e);
            });
        });
    });
    function expectBooleanMenuItemStructure(MenuItem, Ui, Font, Color, Cursor, done) {
        var menuItem1 = new MenuItem('txt', true, function () {});
        expect(menuItem1.text).toEqual('txt');
        expect(menuItem1.selected).toBe(true);
        expect(menuItem1.onActionPerformed).toBeTruthy();
        expectHorizontalTextPosition(menuItem1, Ui);
        expectWidget(menuItem1, Font, Color, Cursor);

        var menuItem2 = new MenuItem('txt', true);
        expect(menuItem2.text).toEqual('txt');
        expect(menuItem2.selected).toBe(true);
        expect(menuItem2.onActionPerformed).toBeFalsy();
        expectHorizontalTextPosition(menuItem2, Ui);

        var menuItem3 = new MenuItem('txt');
        expect(menuItem3.text).toEqual('txt');
        expect(menuItem3.selected).toBe(false);
        expect(menuItem3.onActionPerformed).toBeFalsy();
        expectHorizontalTextPosition(menuItem3, Ui);

        var menuItem4 = new MenuItem();
        expect(menuItem4.text).toEqual('');
        expect(menuItem4.selected).toBe(false);
        expect(menuItem4.onActionPerformed).toBeFalsy();
        expectHorizontalTextPosition(menuItem4, Ui);

        done();
    }
    function expectBooleanMenuItemMarkup(MenuItem, Invoke, Logger, done) {
        var menuItem = new MenuItem();
        document.body.appendChild(menuItem.element);
        menuItem.text = 'Are you beatyful?';

        menuItem.onActionPerformed = function (evt) {
            Logger.info("Action performed on '" + evt.source.constructor.name + "'");
        };
        menuItem.onValueChange = function (evt) {
            Logger.info("Value changed on '" + evt.source.constructor.name + "' oldValue: " + evt.oldValue + '; newValue: ' + evt.newValue);
        };

        spyOn(menuItem, 'onValueChange');

        expect(menuItem.value).toBe(false);
        expect(menuItem.selected).toBe(false);
        menuItem.selected = true;
        expect(menuItem.selected).toBe(true);
        expect(menuItem.value).toBe(true);
        menuItem.value = false;
        expect(menuItem.value).toBe(false);
        expect(menuItem.selected).toBe(false);
        menuItem.selected = true;
        expect(menuItem.selected).toBe(true);
        expect(menuItem.value).toBe(true);
        menuItem.value = null;
        expect(menuItem.value).toBeNull();
        expect(menuItem.selected).toBeNull();
        Invoke.later(function () {
            expect(menuItem.onValueChange.calls.count()).toEqual(4);
            document.body.removeChild(menuItem.element);
            done();
        });
    }
    it('CheckMenuItem.Structure', function (done) {
        require([
            'ui',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/menu/check-menu-item'
        ], function (
                Ui,
                Font,
                Color,
                Cursor,
                CheckMenuItem) {
            expectBooleanMenuItemStructure(CheckMenuItem, Ui, Font, Color, Cursor, done);
            done();
        });
    });
    it('CheckMenuItem.Markup', function (done) {
        require([
            'invoke',
            'logger',
            'forms/menu/check-menu-item'
        ], function (
                Invoke,
                Logger,
                CheckMenuItem) {
            expectBooleanMenuItemMarkup(CheckMenuItem, Invoke, Logger, done);
        });
    });
    it('RadioMenuItem.Structure', function (done) {
        require([
            'ui',
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/menu/radio-menu-item'
        ], function (
                Ui,
                Font,
                Color,
                Cursor,
                RadioMenuItem) {
            expectBooleanMenuItemStructure(RadioMenuItem, Ui, Font, Color, Cursor, done);
            done();
        });
    });
    it('RadioMenuItem.Markup', function (done) {
        require([
            'invoke',
            'logger',
            'forms/menu/radio-menu-item'
        ], function (
                Invoke,
                Logger,
                RadioMenuItem) {
            expectBooleanMenuItemMarkup(RadioMenuItem, Invoke, Logger, done);
        });
    });
    fit('MenuBar.Markup', function (done) {
        require([
            'forms/containers/button-group',
            'forms/menu/menu-item',
            'forms/menu/check-menu-item',
            'forms/menu/radio-menu-item',
            'forms/menu/menu-separator',
            'forms/menu/menu',
            'forms/menu/menu-bar'
        ], function (
                ButtonGroup,
                MenuItem,
                CheckMenuItem,
                RadioMenuItem,
                MenuSeparator,
                Menu,
                MenuBar) {
            var menuBar = new MenuBar();
            document.body.appendChild(menuBar.element);
            
            var fileItem = new MenuItem('File');
            fileItem.subMenu = new Menu();
            fileItem.subMenu.add(new MenuItem('Save'));
            fileItem.subMenu.add(new MenuItem('Save As'));
            fileItem.subMenu.add(new MenuItem('Exit'));
            menuBar.add(fileItem);

            var settingsItem = new MenuItem('Settings');
            settingsItem.subMenu = new Menu();
            settingsItem.subMenu.add(new MenuItem('Main'));
            var hardwareItem = new MenuItem('Hardware');
            settingsItem.subMenu.add(hardwareItem);
            hardwareItem.subMenu = new Menu();
            hardwareItem.subMenu.add(new CheckMenuItem('Motorola'));
            hardwareItem.subMenu.add(new RadioMenuItem('LG'));
            hardwareItem.subMenu.add(new RadioMenuItem('Huawei'));
            var buttonGroup = new ButtonGroup();
            for (var m = 0; m < hardwareItem.subMenu.count; m++) {
                var item = hardwareItem.subMenu.child(m);
                item.buttonGroup = buttonGroup;
            }
            settingsItem.subMenu.add(new MenuItem('Misc'));
            settingsItem.subMenu.add(new MenuSeparator('Misc'));
            settingsItem.subMenu.add(new MenuItem('Other'));
            menuBar.add(settingsItem);

            // document.body.removeChild(menuBar.element);
            done();
        });
    });
});