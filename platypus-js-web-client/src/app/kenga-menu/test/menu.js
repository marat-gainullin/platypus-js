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
            'ui/font',
            'ui/color',
            'ui/cursor',
            'ui/utils',
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
            'ui/utils',
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
            'ui/utils',
            'ui/font',
            'ui/color',
            'ui/cursor',
            'forms/menu/check-box-menu-item'
        ], function (
                Ui,
                Font,
                Color,
                Cursor,
                CheckBoxMenuItem) {
            expectBooleanMenuItemStructure(CheckBoxMenuItem, Ui, Font, Color, Cursor, done);
            done();
        });
    });
    it('CheckMenuItem.Markup', function (done) {
        require([
            'core/invoke',
            'core/logger',
            'forms/menu/check-box-menu-item'
        ], function (
                Invoke,
                Logger,
                CheckBoxMenuItem) {
            expectBooleanMenuItemMarkup(CheckBoxMenuItem, Invoke, Logger, done);
        });
    });
    it('RadioMenuItem.Structure', function (done) {
        require([
            'ui/utils',
            'ui/font',
            'ui/color',
            'ui/cursor',
            'forms/menu/radio-button-menu-item'
        ], function (
                Ui,
                Font,
                Color,
                Cursor,
                RadioButtonMenuItem) {
            expectBooleanMenuItemStructure(RadioButtonMenuItem, Ui, Font, Color, Cursor, done);
            done();
        });
    });
    it('RadioMenuItem.Markup', function (done) {
        require([
            'core/invoke',
            'core/logger',
            'forms/menu/radio-button-menu-item'
        ], function (
                Invoke,
                Logger,
                RadioButtonMenuItem) {
            expectBooleanMenuItemMarkup(RadioButtonMenuItem, Invoke, Logger, done);
        });
    });

    function fillMenu(
            RootMenu,
            ButtonGroup,
            MenuItem,
            CheckMenuItem,
            RadioMenuItem,
            MenuSeparator,
            Menu) {
        var menu = new RootMenu();

        var fileItem = new MenuItem('File');
        fileItem.subMenu = new Menu();
        fileItem.subMenu.add(new MenuItem('Save'));
        fileItem.subMenu.add(new MenuItem('Save As'));
        fileItem.subMenu.add(new MenuItem('Exit'));
        menu.add(fileItem);

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
            expect(item.buttonGroup).toBe(buttonGroup);
        }
        settingsItem.subMenu.add(new MenuItem('Misc'));
        settingsItem.subMenu.add(new MenuSeparator('Misc'));
        settingsItem.subMenu.add(new MenuItem('Other'));
        menu.add(settingsItem);
        return menu;
    }

    it('MenuBar.Markup', function (done) {
        require([
            'forms/containers/button-group',
            'forms/menu/menu-item',
            'forms/menu/check-box-menu-item',
            'forms/menu/radio-button-menu-item',
            'forms/menu/menu-separator',
            'forms/menu/menu',
            'forms/menu/menu-bar'
        ], function (
                ButtonGroup,
                MenuItem,
                CheckBoxMenuItem,
                RadioButtonMenuItem,
                MenuSeparator,
                Menu,
                MenuBar) {
            var menuBar = fillMenu(
                    MenuBar,
                    ButtonGroup,
                    MenuItem,
                    CheckBoxMenuItem,
                    RadioButtonMenuItem,
                    MenuSeparator,
                    Menu);
            document.body.appendChild(menuBar.element);
            document.body.removeChild(menuBar.element);
            done();
        });
    });
    it('PopupMenu.Markup', function (done) {
        require([
            'forms/buttons/button',
            'forms/containers/button-group',
            'forms/menu/menu-item',
            'forms/menu/check-box-menu-item',
            'forms/menu/radio-button-menu-item',
            'forms/menu/menu-separator',
            'forms/menu/menu',
            'forms/menu/menu-bar'
        ], function (
                Button,
                ButtonGroup,
                MenuItem,
                CheckBoxMenuItem,
                RadioButtonMenuItem,
                MenuSeparator,
                Menu) {
            var button = new Button('Right click me');
            document.body.appendChild(button.element);

            var menu = fillMenu(
                    Menu,
                    ButtonGroup,
                    MenuItem,
                    CheckBoxMenuItem,
                    RadioButtonMenuItem,
                    MenuSeparator,
                    Menu);
            button.contextMenu = menu;
            expect(button.contextMenu).toBe(menu);
            document.body.removeChild(button.element);
            done();
        });
    });
    it('DropDownMenu.Markup', function (done) {
        require([
            'core/logger',
            'forms/buttons/drop-down-button',
            'forms/containers/button-group',
            'forms/menu/menu-item',
            'forms/menu/check-box-menu-item',
            'forms/menu/radio-button-menu-item',
            'forms/menu/menu-separator',
            'forms/menu/menu',
            'forms/menu/menu-bar'
        ], function (
                Logger,
                DropDownButton,
                ButtonGroup,
                MenuItem,
                CheckBoxMenuItem,
                RadioButtonMenuItem,
                MenuSeparator,
                Menu) {
            var button = new DropDownButton('Click my chevron');
            document.body.appendChild(button.element);
            
            button.onActionPerformed = function (evt) {
                Logger.info("Action performed on '" + evt.source.constructor.name + "'");
            };

            var menu = fillMenu(
                    Menu,
                    ButtonGroup,
                    MenuItem,
                    CheckBoxMenuItem,
                    RadioButtonMenuItem,
                    MenuSeparator,
                    Menu);
            button.dropDownMenu = menu;
            expect(button.dropDownMenu).toBe(menu);
            document.body.removeChild(button.element);
            done();
        });
    });
});
