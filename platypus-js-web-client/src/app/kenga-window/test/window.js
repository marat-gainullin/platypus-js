/* global expect */
/* global NaN */

describe('Window Api', function () {

    function expectWindow(instance, Logger) {
        instance.addWindowOpenedHandler(function (evt) {
            Logger.info(evt.source.constructor.name + ' opened');
        });
        instance.addWindowActivatedHandler(function (evt) {
            Logger.info(evt.source.constructor.name + ' activated');
        });
        instance.addWindowDeactivatedHandler(function (evt) {
            Logger.info(evt.source.constructor.name + ' deactivated');
        });
        instance.addWindowClosingHandler(function (evt) {
            Logger.info(evt.source.constructor.name + ' is about to close');
        });
        instance.addWindowClosedHandler(function (evt) {
            Logger.info(evt.source.constructor.name + ' closed');
        });
        instance.addWindowMinimizedHandler(function (evt) {
            Logger.info(evt.source.constructor.name + ' minimized');
        });
        instance.addWindowMaximizedHandler(function (evt) {
            Logger.info(evt.source.constructor.name + ' maximized');
        });
        instance.addWindowRestoredHandler(function (evt) {
            Logger.info(evt.source.constructor.name + ' restored');
        });
    }

    it('Caption', function (done) {
        require([
            'ui/utils',
            'core/invoke',
            'core/logger',
            'forms/window/window-pane'
        ], function (
                Ui,
                Invoke,
                Logger,
                WindowPane) {
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            instance.title = 'Sample window';
            expect(instance.title).toEqual('Sample window');
            instance.show();
            instance.left = 400;
            instance.top = 150;
            instance.width = instance.height = 200;
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                instance.icon = loaded;
                Invoke.later(function () {
                    instance.close();
                    done();
                });
            });
        });
    });
    it('Events', function (done) {
        require([
            'ui/utils',
            'core/invoke',
            'core/logger',
            'forms/window/window-pane'
        ], function (
                Ui,
                Invoke,
                Logger,
                WindowPane) {
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            instance.title = 'Sample window';
            expect(instance.title).toEqual('Sample window');

            spyOn(instance, 'onWindowOpened');
            spyOn(instance, 'onWindowActivated');
            spyOn(instance, 'onWindowDeactivated');
            spyOn(instance, 'onWindowClosing');
            spyOn(instance, 'onWindowClosed');
            spyOn(instance, 'onWindowMinimized');
            spyOn(instance, 'onWindowMaximized');
            spyOn(instance, 'onWindowRestored');

            instance.show();
            instance.left = 400;
            instance.top = 150;
            instance.width = instance.height = 200;
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                instance.icon = loaded;
                instance.maximize();
                instance.restore();
                instance.minimize();
                Invoke.later(function () {
                    instance.close();
                    Invoke.later(function () {
                        expect(instance.onWindowOpened.calls.count()).toEqual(1);
                        expect(instance.onWindowActivated.calls.count()).toEqual(1);
                        expect(instance.onWindowClosing.calls.count()).toEqual(1);
                        expect(instance.onWindowClosed.calls.count()).toEqual(1);
                        expect(instance.onWindowMinimized.calls.count()).toEqual(1);
                        expect(instance.onWindowMaximized.calls.count()).toEqual(1);
                        expect(instance.onWindowRestored.calls.count()).toEqual(1);
                        done();
                    });
                });
            });
        });
    });
    it('Selector', function (done) {
        require([
            'core/logger',
            'forms/window/window-pane'
        ], function (
                Logger,
                WindowPane) {
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            instance.title = 'Sample window';
            instance.left = instance.top = 200;
            instance.width = instance.height = 200;
            expect(instance.title).toEqual('Sample window');
            var toBeSelected = {};
            instance.showModal(function (selected) {
                expect(selected).toBe(toBeSelected);
                done();
            });
            instance.close(toBeSelected);
        });
    });
    it('AutoClose', function (done) {
        require([
            'core/logger',
            'forms/window/window-pane'
        ], function (
                Logger,
                WindowPane) {
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            instance.title = 'Sample window';
            instance.left = instance.top = 200;
            instance.width = instance.height = 200;
            expect(instance.title).toEqual('Sample window');
            instance.show();
            instance.autoClose = true;
            instance.close();
            done();
        });
    });
    it('Undecorated', function (done) {
        require([
            'core/logger',
            'forms/window/window-pane'
        ], function (
                Logger,
                WindowPane) {
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            instance.title = 'Sample window';
            instance.left = instance.top = 200;
            instance.width = instance.height = 200;
            expect(instance.title).toEqual('Sample window');
            instance.show();
            instance.undecorated = true;
            instance.undecorated = false;
            instance.close();
            done();
        });
    });
    it('LocationByPlatform', function (done) {
        require([
            'core/logger',
            'forms/window/window-pane'
        ], function (
                Logger,
                WindowPane) {
            for (var i = 0; i < 50; i++) {
                var instance = new WindowPane();
                expectWindow(instance, Logger);
                instance.title = 'Sample window ' + i;
                instance.width = instance.height = 200;
                expect(instance.title).toEqual('Sample window ' + i);
                instance.show();
                instance.close();
            }
            done();
        });
    });
    it('Shown', function (done) {
        require([
            'core/logger',
            'forms/window/window-pane'
        ], function (
                Logger,
                WindowPane) {
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            instance.title = 'Sample window';
            instance.width = instance.height = 200;
            expect(instance.title).toEqual('Sample window');
            instance.show();
            expect(WindowPane.getShownForm(instance.formKey)).toBe(instance);
            expect(WindowPane.shown()).toEqual([instance]);
            var instance1 = new WindowPane();
            expectWindow(instance1, Logger);
            instance1.title = 'Sample window';
            instance1.width = instance1.height = 200;
            expect(instance1.title).toEqual('Sample window');
            instance1.show();
            expect(WindowPane.getShownForm(instance1.formKey)).toBe(instance1);
            expect(WindowPane.shown()).toEqual([instance, instance1]);
            instance.close();
            expect(WindowPane.getShownForm(instance1.formKey)).toBe(instance1);
            expect(WindowPane.shown()).toEqual([instance1]);
            instance1.formKey = 'new key';
            expect(WindowPane.getShownForm('new key')).toBe(instance1);
            expect(WindowPane.shown()).toEqual([instance1]);
            instance1.close();
            expect(WindowPane.shown()).toEqual([]);
            done();
        });
    });
    it('Active', function (done) {
        require([
            'core/invoke',
            'core/logger',
            'forms/window/window-pane'
        ], function (
                Invoke,
                Logger,
                WindowPane) {
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            spyOn(instance, 'onWindowDeactivated');
            instance.title = 'Sample window';
            instance.width = instance.height = 200;
            expect(instance.title).toEqual('Sample window');
            instance.show();
            var instance1 = new WindowPane();
            expectWindow(instance1, Logger);
            spyOn(instance1, 'onWindowActivated');
            instance1.title = 'Sample window';
            instance1.width = instance1.height = 200;
            expect(instance1.title).toEqual('Sample window');
            instance1.show();
            Invoke.later(function () {
                expect(instance.onWindowDeactivated.calls.count()).toEqual(1);
                expect(instance1.onWindowActivated.calls.count()).toEqual(1);
                instance.close();
                instance1.close();
                done();
            });
        });
    });
    it('Interaction', function (done) {
        require([
            'core/logger',
            'forms/window/window-pane'
        ], function (
                Logger,
                WindowPane) {
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            instance.title = 'Sample window';
            instance.width = instance.height = 200;
            expect(instance.title).toEqual('Sample window');
            instance.show();
            instance.close();
            done();
        });
    });
    it('DesktopPane.Operations', function (done) {
        require([
            'core/logger',
            'forms/window/window-pane',
            'forms/containers/desktop-pane'
        ], function (
                Logger,
                WindowPane,
                DesktopPane) {
            var desktop = new DesktopPane();
            desktop.width = desktop.height = 400;
            document.body.appendChild(desktop.element);
            var instance1 = new WindowPane();
            expectWindow(instance1, Logger);
            instance1.title = 'Sample window';
            instance1.width = instance1.height = 200;
            expect(instance1.title).toEqual('Sample window');
            instance1.showInternalFrame(desktop);
            var instance2 = new WindowPane();
            expectWindow(instance2, Logger);
            instance2.title = 'Sample window';
            instance2.width = instance2.height = 200;
            expect(instance2.title).toEqual('Sample window');
            instance2.showInternalFrame(desktop);
            desktop.maximizeAll();
            desktop.restoreAll();
            desktop.minimizeAll();
            desktop.closeAll();
            document.body.removeChild(desktop.element);
            done();
        });
    });
    it('DesktopPane.LocationByPlatform', function (done) {
        require([
            'core/logger',
            'forms/window/window-pane',
            'forms/containers/desktop-pane'
        ], function (
                Logger,
                WindowPane,
                DesktopPane) {
            var desktop = new DesktopPane();
            desktop.width = desktop.height = 400;
            document.body.appendChild(desktop.element);
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            instance.title = 'Sample window';
            instance.width = instance.height = 200;
            expect(instance.title).toEqual('Sample window');
            instance.show();
            for (var i = 0; i < 50; i++) {
                var internalFrame = new WindowPane();
                expectWindow(internalFrame, Logger);
                internalFrame.title = 'Sample window ' + i;
                internalFrame.width = internalFrame.height = 200;
                expect(internalFrame.title).toEqual('Sample window ' + i);
                internalFrame.showInternalFrame(desktop);
                instance.close();
            }
            instance.close();
            document.body.removeChild(desktop.element);
            done();
        });
    });
    it('DesktopPane.Shown', function (done) {
        require([
            'core/logger',
            'forms/window/window-pane',
            'forms/containers/desktop-pane'
        ], function (
                Logger,
                WindowPane,
                DesktopPane) {
            var desktop = new DesktopPane();
            desktop.width = desktop.height = 400;
            document.body.appendChild(desktop.element);
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            instance.title = 'Sample window';
            instance.width = instance.height = 200;
            expect(instance.title).toEqual('Sample window');
            instance.show();
            expect(WindowPane.getShownForm(instance.formKey)).toBe(instance);
            expect(WindowPane.shown()).toEqual([instance]);
            var instance1 = new WindowPane();
            expectWindow(instance1, Logger);
            instance1.title = 'Sample window';
            instance1.width = instance1.height = 200;
            expect(instance1.title).toEqual('Sample window');
            instance1.showInternalFrame(desktop);
            expect(desktop.getShownForm(instance1.formKey)).toBe(instance1);
            expect(desktop.shown()).toEqual([instance1]);
            var instance2 = new WindowPane();
            expectWindow(instance2, Logger);
            instance2.title = 'Sample window';
            instance2.width = instance2.height = 200;
            expect(instance2.title).toEqual('Sample window');
            instance2.showInternalFrame(desktop);
            expect(desktop.shown()).toEqual(desktop.forms);
            expect(desktop.getShownForm(instance2.formKey)).toBe(instance2);
            expect(desktop.shown()).toEqual([instance1, instance2]);
            instance1.close();
            expect(desktop.getShownForm(instance2.formKey)).toBe(instance2);
            expect(desktop.shown()).toEqual([instance2]);
            instance2.formKey = 'new key';
            expect(desktop.getShownForm('new key')).toBe(instance2);
            expect(desktop.shown()).toEqual([instance2]);
            instance2.close();
            expect(desktop.shown()).toEqual([]);
            instance.close();
            expect(WindowPane.shown()).toEqual([]);
            document.body.removeChild(desktop.element);
            done();
        });
    });
    it('DesktopPane.Active', function (done) {
        require([
            'core/invoke',
            'core/logger',
            'forms/window/window-pane',
            'forms/containers/desktop-pane'
        ], function (
                Invoke,
                Logger,
                WindowPane,
                DesktopPane) {
            var desktop = new DesktopPane();
            desktop.width = desktop.height = 400;
            document.body.appendChild(desktop.element);
            var instance = new WindowPane();
            expectWindow(instance, Logger);
            spyOn(instance, 'onWindowDeactivated');
            instance.title = 'Sample window';
            instance.width = instance.height = 200;
            expect(instance.title).toEqual('Sample window');
            instance.show();
            var instance1 = new WindowPane();
            expectWindow(instance1, Logger);
            spyOn(instance1, 'onWindowDeactivated');
            instance1.title = 'Sample window';
            instance1.width = instance1.height = 200;
            expect(instance1.title).toEqual('Sample window');
            instance1.showInternalFrame(desktop);
            var instance2 = new WindowPane();
            expectWindow(instance2, Logger);
            spyOn(instance2, 'onWindowActivated');
            instance2.title = 'Sample window';
            instance2.width = instance2.height = 200;
            expect(instance2.title).toEqual('Sample window');
            instance2.showInternalFrame(desktop);
            Invoke.later(function () {
                expect(instance.onWindowDeactivated.calls.count()).toEqual(0);
                expect(instance1.onWindowDeactivated.calls.count()).toEqual(1);
                expect(instance2.onWindowActivated.calls.count()).toEqual(1);
                instance.close();
                instance1.close();
                instance2.close();
                document.body.removeChild(desktop.element);
                done();
            });
        });
    });
});
