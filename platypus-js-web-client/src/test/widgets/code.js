/* global expect */
/* global NaN */

describe('Widgets Api', function () {

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

    function expectImageParagraph(w, Ui) {
        var h = [Ui.HorizontalPosition.LEFT, Ui.HorizontalPosition.CENTER, Ui.HorizontalPosition.RIGHT];
        var v = [Ui.VerticalPosition.TOP, Ui.VerticalPosition.CENTER, Ui.VerticalPosition.BOTTOM];
        h.forEach(function (hi) {
            w.horizontalTextPosition = hi;
            expect(w.horizontalTextPosition).toEqual(hi);
            v.forEach(function (vi) {
                w.verticalTextPosition = vi;
                expect(w.verticalTextPosition).toEqual(vi);

            });
        });
    }

    it('Label.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'ui',
            'forms/label'], function (
                Font,
                Color,
                Cursor,
                Ui,
                Label) {
            var label1 = new Label('txt', null, 45);
            expectImageParagraph(label1, Ui);
            expect(label1.text).toEqual('txt');
            expect(label1.icon).toBeNull();
            expect(label1.iconTextGap).toEqual(45);
            var label2 = new Label('txt', null);
            expectImageParagraph(label2, Ui);
            expect(label2.text).toEqual('txt');
            expect(label2.icon).toBeNull();
            expect(label2.iconTextGap).toEqual(4);
            var label3 = new Label('txt');
            expectImageParagraph(label3, Ui);
            expect(label3.text).toEqual('txt');
            expect(label3.icon).toBeNull();
            expect(label3.iconTextGap).toEqual(4);
            var label4 = new Label();
            expectImageParagraph(label4, Ui);
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
    it('Button.Structure', function (done) {
        require([
            'ui',
            'forms/buttons/button'], function (
                Ui,
                Button) {
            Ui.Icon.load('assets/binary-content.png', function (icon) {
                var btn1 = new Button();
                expect(btn1.text).toBeFalsy();
                expect(btn1.icon).toBeNull();
                expect(btn1.iconTextGap).toEqual(4);
                expect(btn1.actionPerformed).toBeFalsy();
                var btn2 = new Button('sample');
                expect(btn2.text).toEqual('sample');
                expect(btn2.icon).toBeNull();
                expect(btn2.iconTextGap).toEqual(4);
                expect(btn2.actionPerformed).toBeFalsy();
                var btn3 = new Button('sample', icon);
                expect(btn3.text).toEqual('sample');
                expect(btn3.icon).toEqual(icon);
                expect(btn3.iconTextGap).toEqual(4);
                expect(btn3.actionPerformed).toBeFalsy();
                var btn4 = new Button('sample', icon, 6);
                expect(btn4.text).toEqual('sample');
                expect(btn4.icon).toEqual(icon);
                expect(btn4.iconTextGap).toEqual(6);
                expect(btn4.actionPerformed).toBeFalsy();
                var action = function () {};
                var btn5 = new Button('sample', icon, 6, action);
                expect(btn5.text).toEqual('sample');
                expect(btn5.icon).toEqual(icon);
                expect(btn5.iconTextGap).toEqual(6);
                expect(btn5.onActionPerformed).toBe(action);
                done();
            }, function (e) {
                done.fail(e);
            });
        }, function (e) {
            done.fail(e);
        });
    });
    it('Button.Markup.1', function (done) {
        require([
            'logger',
            'ui',
            'forms/buttons/button'], function (
                Logger,
                Ui,
                Button) {
            var btn = new Button();
            document.body.appendChild(btn.element);
            btn.text = 'Sample button';
            expect(btn.iconTextGap).toEqual(4);
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                btn.icon = loaded;
                // defaults
                // right text
                expect(btn.horizontalTextPosition).toEqual(Ui.HorizontalPosition.RIGHT);
                expect(btn.verticalTextPosition).toEqual(Ui.VerticalPosition.CENTER);
                (function () {
                    var image = btn.element.firstElementChild;
                    var paragraph = btn.element.lastElementChild;
                    expect(image.offsetLeft).toEqual(6);
                    expect(paragraph.offsetLeft).toEqual(6 + 16 + 4);
                }());
                // top and bottom
                btn.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                btn.verticalTextPosition = Ui.VerticalPosition.TOP;
                // left text
                btn.horizontalTextPosition = Ui.HorizontalPosition.LEFT;
                (function () {
                    var image = btn.element.lastElementChild;
                    var paragraph = btn.element.firstElementChild;
                    expect(paragraph.offsetLeft).toEqual(6);
                    expect(image.offsetLeft).toEqual(6 + paragraph.offsetWidth + 4);
                }());
                // top and bottom
                btn.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                btn.verticalTextPosition = Ui.VerticalPosition.TOP;

                // center text
                btn.horizontalTextPosition = Ui.HorizontalPosition.CENTER;

                // top and bottom
                btn.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                (function () {
                    var image = btn.element.firstElementChild;
                    var paragraph = btn.element.lastElementChild;
                    expect(image.offsetTop).toEqual(1 + 0);
                    expect(paragraph.offsetTop).toEqual(1 + 16 + 4);
                }());
                btn.verticalTextPosition = Ui.VerticalPosition.TOP;
                (function () {
                    var image = btn.element.lastElementChild;
                    var paragraph = btn.element.firstElementChild;
                    expect(image.offsetTop).toBeGreaterThan(paragraph.offsetTop);
                }());
                // center center
                btn.verticalTextPosition = Ui.VerticalPosition.CENTER;

                btn.onActionPerformed = function () {
                    Logger.info('btn action');
                };

                document.body.removeChild(btn.element);
                done();
            }, function (e) {
                done.fail(e);
            });
        });
    });
    it('Button.Markup.2', function (done) {
        require([
            'logger',
            'ui',
            'forms/buttons/button'], function (
                Logger,
                Ui,
                Button) {
            var btn = new Button();
            document.body.appendChild(btn.element);
            expect(btn.iconTextGap).toEqual(4);
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                btn.icon = loaded;
                // defaults
                // right text
                expect(btn.horizontalTextPosition).toEqual(Ui.HorizontalPosition.RIGHT);
                expect(btn.verticalTextPosition).toEqual(Ui.VerticalPosition.CENTER);
                (function () {
                    var image = btn.element.firstElementChild;
                    var paragraph = btn.element.lastElementChild;
                    expect(image.offsetLeft).toEqual(6);
                    expect(paragraph.offsetLeft).toEqual(6 + 16 /*+ 4 gap is ignored without text or image*/);
                }());
                // top and bottom
                btn.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                btn.verticalTextPosition = Ui.VerticalPosition.TOP;
                // left text
                btn.horizontalTextPosition = Ui.HorizontalPosition.LEFT;
                (function () {
                    var image = btn.element.lastElementChild;
                    var paragraph = btn.element.firstElementChild;
                    expect(paragraph.offsetLeft).toEqual(6);
                    expect(image.offsetLeft).toEqual(6 + paragraph.offsetWidth /*+ 4 gap is ignored without text or image*/);
                }());
                // top and bottom
                btn.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                btn.verticalTextPosition = Ui.VerticalPosition.TOP;

                // center text
                btn.horizontalTextPosition = Ui.HorizontalPosition.CENTER;

                // top and bottom
                btn.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                (function () {
                    var image = btn.element.firstElementChild;
                    var paragraph = btn.element.lastElementChild;
                    expect(image.offsetTop).toEqual(1 + 0);
                    expect(paragraph.offsetTop).toEqual(1 + 16 /*+ 4 gap is ignored without text or image*/);
                }());
                btn.verticalTextPosition = Ui.VerticalPosition.TOP;
                (function () {
                    var image = btn.element.lastElementChild;
                    var paragraph = btn.element.firstElementChild;
                    expect(image.offsetTop).toEqual(paragraph.offsetTop);
                }());
                // center center
                btn.verticalTextPosition = Ui.VerticalPosition.CENTER;

                btn.onActionPerformed = function () {
                    Logger.info('btn action');
                };

                document.body.removeChild(btn.element);
                done();
            }, function (e) {
                done.fail(e);
            });
        });
    });
    it('ToggleButton.Structure', function (done) {
        require([
            'ui',
            'invoke',
            'forms/buttons/toggle-button'], function (
                Ui,
                Invoke,
                ToggleButton) {
            Ui.Icon.load('assets/binary-content.png', function (icon) {
                var btn1 = new ToggleButton();
                expect(btn1.text).toBeFalsy();
                expect(btn1.icon).toBeNull();
                expect(btn1.selected).toBeFalsy();
                expect(btn1.iconTextGap).toEqual(4);
                expect(btn1.actionPerformed).toBeFalsy();
                var btn2 = new ToggleButton('sample');
                expect(btn2.text).toEqual('sample');
                expect(btn2.icon).toBeNull();
                expect(btn2.selected).toBeFalsy();
                expect(btn2.iconTextGap).toEqual(4);
                expect(btn2.actionPerformed).toBeFalsy();
                var btn3 = new ToggleButton('sample', icon);
                expect(btn3.text).toEqual('sample');
                expect(btn3.icon).toEqual(icon);
                expect(btn3.selected).toBeFalsy();
                expect(btn3.iconTextGap).toEqual(4);
                expect(btn3.actionPerformed).toBeFalsy();
                var btn4 = new ToggleButton('sample', icon, true);
                expect(btn4.text).toEqual('sample');
                expect(btn4.icon).toEqual(icon);
                expect(btn4.selected).toBeTruthy();
                expect(btn4.iconTextGap).toEqual(4);
                expect(btn4.actionPerformed).toBeFalsy();
                var btn5 = new ToggleButton('sample', icon, true, 6);
                expect(btn5.text).toEqual('sample');
                expect(btn5.icon).toEqual(icon);
                expect(btn5.selected).toBeTruthy();
                expect(btn5.iconTextGap).toEqual(6);
                expect(btn5.actionPerformed).toBeFalsy();
                var action = function () {};
                var btn6 = new ToggleButton('sample', icon, true, 6, action);
                expect(btn6.text).toEqual('sample');
                expect(btn6.icon).toEqual(icon);
                expect(btn6.selected).toBeTruthy();
                expect(btn6.iconTextGap).toEqual(6);
                expect(btn6.onActionPerformed).toBe(action);

                btn1.onValueChange = function (event) {
                    expect(event.source).toEqual(btn1);
                    expect(event.target).toEqual(btn1);
                    expect(event.oldValue).toBeFalsy();
                    expect(event.newValue).toBeTruthy();
                };

                spyOn(btn1, 'onValueChange');

                expect(btn1.value).toBeFalsy();
                expect(btn1.selected).toBeFalsy();
                btn1.value = true;
                expect(btn1.value).toBeTruthy();
                expect(btn1.selected).toBeTruthy();

                Invoke.later(function () {
                    expect(btn1.onValueChange).toHaveBeenCalled();
                    done();
                });
            }, function (e) {
                done.fail(e);
            });
        }, function (e) {
            done.fail(e);
        });
    });
    it('ToggleButton.Markup', function (done) {
        require([
            'logger',
            'ui',
            'forms/buttons/toggle-button'], function (
                Logger,
                Ui,
                ToggleButton) {
            var toggle = new ToggleButton();
            document.body.appendChild(toggle.element);
            toggle.text = 'Sample toggle button';
            expect(toggle.iconTextGap).toEqual(4);
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                toggle.icon = loaded;
                // defaults
                // right text
                expect(toggle.horizontalTextPosition).toEqual(Ui.HorizontalPosition.RIGHT);
                expect(toggle.verticalTextPosition).toEqual(Ui.VerticalPosition.CENTER);
                (function () {
                    var image = toggle.element.firstElementChild;
                    var paragraph = toggle.element.lastElementChild;
                    expect(image.offsetLeft).toEqual(6);
                    expect(paragraph.offsetLeft).toEqual(6 + 16 + 4);
                }());
                // top and bottom
                toggle.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                toggle.verticalTextPosition = Ui.VerticalPosition.TOP;
                // left text
                toggle.horizontalTextPosition = Ui.HorizontalPosition.LEFT;
                (function () {
                    var image = toggle.element.lastElementChild;
                    var paragraph = toggle.element.firstElementChild;
                    expect(paragraph.offsetLeft).toEqual(6);
                    expect(image.offsetLeft).toEqual(6 + paragraph.offsetWidth + 4);
                }());
                // top and bottom
                toggle.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                toggle.verticalTextPosition = Ui.VerticalPosition.TOP;

                // center text
                toggle.horizontalTextPosition = Ui.HorizontalPosition.CENTER;

                // top and bottom
                toggle.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                (function () {
                    var image = toggle.element.firstElementChild;
                    var paragraph = toggle.element.lastElementChild;
                    expect(image.offsetTop).toEqual(1 + 0);
                    expect(paragraph.offsetTop).toEqual(1 + 16 + 4);
                }());
                toggle.verticalTextPosition = Ui.VerticalPosition.TOP;
                (function () {
                    var image = toggle.element.lastElementChild;
                    var paragraph = toggle.element.firstElementChild;
                    expect(image.offsetTop).toBeGreaterThan(paragraph.offsetTop);
                }());
                // center center
                toggle.verticalTextPosition = Ui.VerticalPosition.CENTER;

                toggle.onActionPerformed = function () {
                    Logger.info('toggle action');
                };

                document.body.removeChild(toggle.element);
                done();
            }, function (e) {
                done.fail(e);
            });
        });
    });
    it('DropdownButton.Structure', function (done) {
        require([
            'forms/buttons/drop-down-button'], function (
                DropdownButton) {
            var btn = new DropdownButton();
            expect(btn.dropDown).toBeFalsy();
            var menu = {};
            btn.dropDownMenu = menu;
            expect(btn.dropDownMenu).toEqual(menu);
            done();
        }, function (e) {
            done.fail(e);
        });
    });
    it('DropdownButton.Markup', function (done) {
        require([
            'logger',
            'ui',
            'forms/buttons/drop-down-button'], function (
                Logger,
                Ui,
                DropdownButton) {
            var btn = new DropdownButton();
            document.body.appendChild(btn.element);
            btn.text = 'Sample drop down button';
            expect(btn.iconTextGap).toEqual(4);
            Ui.Icon.load('assets/binary-content.png', function (loaded) {
                btn.icon = loaded;
                // defaults
                // right text
                expect(btn.horizontalTextPosition).toEqual(Ui.HorizontalPosition.RIGHT);
                expect(btn.verticalTextPosition).toEqual(Ui.VerticalPosition.CENTER);
                var image = btn.element.firstElementChild;
                var paragraph = image.nextElementSibling;
                (function () {
                    expect(image.offsetLeft).toEqual(6);
                    expect(paragraph.offsetLeft).toEqual(6 + 16 + 4);
                }());
                // top and bottom
                btn.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                btn.verticalTextPosition = Ui.VerticalPosition.TOP;
                // left text
                btn.horizontalTextPosition = Ui.HorizontalPosition.LEFT;
                (function () {
                    expect(paragraph.offsetLeft).toEqual(6);
                    expect(image.offsetLeft).toEqual(6 + paragraph.offsetWidth + 4);
                }());
                // top and bottom
                btn.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                btn.verticalTextPosition = Ui.VerticalPosition.TOP;

                // center text
                btn.horizontalTextPosition = Ui.HorizontalPosition.CENTER;

                // top and bottom
                btn.verticalTextPosition = Ui.VerticalPosition.BOTTOM;
                (function () {
                    expect(image.offsetTop).toEqual(1 + 0);
                    expect(paragraph.offsetTop).toEqual(1 + 16 + 4);
                }());
                btn.verticalTextPosition = Ui.VerticalPosition.TOP;
                (function () {
                    expect(image.offsetTop).toBeGreaterThan(paragraph.offsetTop);
                }());
                // center center
                btn.verticalTextPosition = Ui.VerticalPosition.CENTER;

                btn.onActionPerformed = function () {
                    Logger.info('drop down action');
                };

                document.body.removeChild(btn.element);
                done();
            }, function (e) {
                done.fail(e);
            });
        });
    });

    function expectCheckRadio(CheckRadio) {
        var check1 = new CheckRadio();
        expect(check1.text).toEqual('');
        expect(check1.selected).toBe(false);
        expect(check1.onActionPerformed).toBeFalsy();

        var check2 = new CheckRadio('Sample check box');
        expect(check2.text).toEqual('Sample check box');
        expect(check2.selected).toBe(false);
        expect(check2.onActionPerformed).toBeFalsy();

        check2.text = 'Sample check box 1';
        expect(check2.text).toEqual('Sample check box 1');
        check2.selected = true;
        expect(check2.selected).toBe(true);
        function action() {}

        check2.onActionPerformed = action;
        expect(check2.onActionPerformed).toBe(action);

        var check3 = new CheckRadio('Sample check box', true);
        expect(check3.text).toEqual('Sample check box');
        expect(check3.selected).toBe(true);
        expect(check3.onActionPerformed).toBeFalsy();

        var check4 = new CheckRadio('Sample check box', true, action);
        expect(check4.text).toEqual('Sample check box');
        expect(check4.selected).toBe(true);
        expect(check4.onActionPerformed).toBe(action);
    }

    it('Checkbox.Structure', function (done) {
        require([
            'forms/buttons/check-box'], function (
                CheckBox) {
            expectCheckRadio(CheckBox);
            done();
        });
    });

    function expectCheckRadioMarkup(Logger, Ui, CheckRadio) {
        var check = new CheckRadio();
        document.body.appendChild(check.element);
        check.text = 'Sample check box';
        check.onActionPerformed = function (e) {
            Logger.info('Check action');
        };
        check.onValueChange = function (e) {
            Logger.info('Check value: ' + e.newValue);
        };
        expect(check.element.style.direction).toEqual('rtl');
        check.horizontalTextPosition = Ui.HorizontalPosition.LEFT;
        expect(check.element.style.direction).toEqual('ltr');
        document.body.removeChild(check.element);
    }

    it('Checkbox.Markup', function (done) {
        require([
            'logger',
            'ui',
            'forms/buttons/check-box'], function (
                Logger,
                Ui,
                Checkbox) {
            expectCheckRadioMarkup(Logger, Ui, Checkbox);
            done();
        });
    });
    it('RadioButton.Structure', function (done) {
        require([
            'forms/buttons/radio-button'], function (
                RadioButton) {
            expectCheckRadio(RadioButton);
            done();
        });
    });
    it('RadioButton.Markup', function (done) {
        require([
            'logger',
            'ui',
            'forms/buttons/radio-button'], function (
                Logger,
                Ui,
                RadioButton) {
            expectCheckRadioMarkup(Logger, Ui, RadioButton);
            done();
        });
    });
    it('ButtonGroup.Structure', function (done) {
        require([
            'logger',
            'forms/buttons/check-box',
            'forms/buttons/radio-button',
            'forms/buttons/toggle-button',
            'forms/containers/button-group'], function (
                Logger,
                CheckBox,
                RadioButton,
                ToggleButton,
                ButtonGroup) {
            var check = new CheckBox('Check');
            var radio = new RadioButton('Radio');
            var toggle = new ToggleButton('Toggle');

            var group = new ButtonGroup();

            group.onComponentAdded = function (evt) {
                Logger.info('added ' + evt.target.constructor.name + ' | ' + evt.child.constructor.name);
            };

            group.onComponentRemoved = function (evt) {
                Logger.info('removed ' + evt.target.constructor.name + ' | ' + evt.child.constructor.name);
            };

            group.add(check);
            group.add(radio);
            group.add(toggle);
            expect(group.count).toEqual(3);
            expect(group.children()).toEqual([check, radio, toggle]);
            expect(group.indexOf(check)).toEqual(0);
            expect(group.indexOf(radio)).toEqual(1);
            expect(group.indexOf(toggle)).toEqual(2);
            expect(check.buttonGroup).toBe(group);
            expect(radio.buttonGroup).toBe(group);
            expect(toggle.buttonGroup).toBe(group);

            group.remove(check);
            expect(group.count).toEqual(2);
            expect(group.children()).toEqual([radio, toggle]);
            group.remove(radio);
            expect(group.count).toEqual(1);
            expect(group.children()).toEqual([toggle]);
            group.remove(toggle);
            expect(group.count).toEqual(0);
            expect(group.children()).toEqual([]);
            expect(check.buttonGroup).toBeNull();
            expect(radio.buttonGroup).toBeNull();
            expect(toggle.buttonGroup).toBeNull();

            check.buttonGroup = group;
            radio.buttonGroup = group;
            toggle.buttonGroup = group;
            expect(group.count).toEqual(3);
            expect(group.children()).toEqual([check, radio, toggle]);
            var met = 0;
            group.forEach(function () {
                met++;
            });
            expect(met).toEqual(3);

            group.clear();
            expect(group.count).toEqual(0);
            expect(group.children()).toEqual([]);
            expect(check.buttonGroup).toBeNull();
            expect(radio.buttonGroup).toBeNull();
            expect(toggle.buttonGroup).toBeNull();

            done();
        });
    });
    it('ButtonGroup.Markup', function (done) {
        require([
            '../logger',
            '../invoke',
            'forms/buttons/check-box',
            'forms/buttons/radio-button',
            'forms/buttons/toggle-button',
            'forms/containers/button-group'], function (
                Logger,
                Invoke,
                CheckBox,
                RadioButton,
                ToggleButton,
                ButtonGroup) {
            var check = new CheckBox('Check');
            var radio = new RadioButton('Radio');
            var toggle = new ToggleButton('Toggle');

            document.body.appendChild(check.element);
            document.body.appendChild(radio.element);
            document.body.appendChild(toggle.element);

            var group = new ButtonGroup();
            group.add(check);
            group.add(radio);
            group.add(toggle);

            group.onItemSelected = function (evt) {
                Logger.info('selected ' + evt.target.constructor.name);
            };

            spyOn(group, 'onItemSelected');

            check.selected = true;
            radio.selected = true;
            toggle.selected = true;

            Invoke.later(function () {
                Invoke.later(function () {
                    expect(group.onItemSelected.calls.count()).toEqual(3);
                    document.body.removeChild(check.element);
                    document.body.removeChild(radio.element);
                    document.body.removeChild(toggle.element);
                    done();
                });
            });
        });
    });
    it('TextField.Structure', function (done) {
        require([
            'forms/fields/text-field'], function (
                TextField) {
            var textField1 = new TextField();
            expect(textField1.text).toEqual('');
            var textField2 = new TextField('Sample text');
            expect(textField2.text).toEqual('Sample text');
            done();
        });
    });
    it('TextField.Markup', function (done) {
        require([
            'invoke',
            'logger',
            'forms/fields/text-field'], function (
                Invoke,
                Logger,
                TextField) {
            var textField = new TextField();
            document.body.appendChild(textField.element);
            textField.text = 'Sample text';
            textField.onActionPerformed = function () {
                Logger.info('TextField action');
            };
            textField.onValueChange = function (evt) {
                Logger.info('TextField value changed: newValue: ' + evt.newValue + '; oldValue: ' + evt.oldValue);
            };

            spyOn(textField, 'onValueChange');

            textField.text += ' 1';
            textField.text += ' 2';
            textField.text += ' 3';

            Invoke.later(function () {
                expect(textField.onValueChange.calls.count()).toEqual(3);
                document.body.removeChild(textField.element);
                done();
            });
        });
    });

    function expectTypedField(TypedField, Font, Color, Cursor) {
        var instance = new TypedField();
        expect(instance.element.type).not.toEqual('');
        expectWidget(instance, Font, Color, Cursor);
    }

    function expectTypedFieldMarkup(Logger, TypedField) {
        var instance = new TypedField();
        document.body.appendChild(instance.element);
        expect(instance.element.type).not.toEqual('');
        instance.onActionPerformed = function (evt) {
            Logger.info('Action performed on ' + evt.source.constructor.name);
        };
        instance.onValueChange = function (evt) {
            Logger.info('Value change on ' + evt.source.constructor.name + '. newValue: ' + evt.newValue + '; oldValue: ' + evt.oldValue);
        };
        if (instance.error)
            instance.error = null;
        document.body.removeChild(instance.element);
    }

    it('ColorField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/color-field'], function (
                Font,
                Color,
                Cursor,
                ColorField) {
            expectTypedField(ColorField, Font, Color, Cursor);
            var instance = new ColorField();
            expect(instance.text).toEqual('#000000');
            instance.text = '#fcfcfc';
            expect(instance.text).toEqual('#fcfcfc');
            expect(instance.value instanceof Color).toBe(true);
            expect(instance.value.toString()).toEqual('#fcfcfc');

            instance.text = '';
            expect(instance.text).toEqual('#000000');
            expect(instance.value.toString()).toBe('#000000');

            instance.value = Color.blue;
            expect(instance.value).toBe(Color.blue);
            expect(instance.text).toEqual('#0000ff');

            instance.value = null;
            expect(instance.value).toBe(null);
            expect(instance.text).toEqual('#000000');

            done();
        });
    });
    it('ColorField.Markup', function (done) {
        require([
            'common-utils/color',
            'logger',
            'forms/fields/color-field'], function (
                Color,
                Logger,
                ColorField) {
            expectTypedFieldMarkup(Logger, ColorField);
            done();
        });
    });
    it('DateField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/date-field'], function (
                Font,
                Color,
                Cursor,
                DateField) {
            expectTypedField(DateField, Font, Color, Cursor);
            var instance = new DateField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBe(null);

            var day = new Date('2017-04-23T00:00:00.000Z');
            instance.text = '2017-04-23';
            expect(instance.value instanceof Date).toBe(true);
            expect(instance.value.valueOf()).toEqual(day.valueOf());

            instance.text = '';
            expect(instance.value).toBeNull();

            instance.value = day;
            expect(instance.text).toEqual('2017-04-23');

            instance.value = null;
            expect(instance.text).toEqual('');

            done();
        });
    });
    it('DateField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/date-field'], function (
                Logger,
                DateField) {
            expectTypedFieldMarkup(Logger, DateField);
            done();
        });
    });
    it('DateTimeField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/date-time-field'], function (
                Font,
                Color,
                Cursor,
                DateTimeField) {
            expectTypedField(DateTimeField, Font, Color, Cursor);

            var instance = new DateTimeField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBe(null);

            var moment = new Date('2017-04-23T01:07:00.068Z');
            var localMoment = new Date(-moment.getTimezoneOffset() * 60000 + moment.valueOf()).toJSON(); // local version of moment
            localMoment = localMoment.substring(0, localMoment.length - 1);
            instance.text = localMoment;
            expect(instance.value instanceof Date).toBe(true);
            expect(instance.value.valueOf()).toEqual(moment.valueOf());

            instance.text = '';
            expect(instance.value).toBeNull();

            instance.value = moment;
            expect(instance.text).toEqual(localMoment);

            instance.value = null;
            expect(instance.text).toEqual('');

            done();
        });
    });
    it('DateTimeField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/date-time-field'], function (
                Logger,
                DateTimeField) {
            expectTypedFieldMarkup(Logger, DateTimeField);
            var instance = new DateTimeField();
            done();
        });
    });
    it('TimeField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/time-field'], function (
                Font,
                Color,
                Cursor,
                TimeField) {
            expectTypedField(TimeField, Font, Color, Cursor);
            var instance = new TimeField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBe(null);

            instance.text = '13:45';
            expect(typeof instance.value).toEqual('number');
            expect(instance.value).toEqual(13 * 3600 * 1000 + 45 * 60 * 1000);

            instance.text = '';
            expect(instance.value).toBeNull();

            instance.value = 13 * 3600 * 1000 + 45 * 60 * 1000;
            expect(instance.text).toEqual('13:45:00.000');

            instance.value = null;
            expect(instance.text).toEqual('');

            done();
        });
    });
    it('TimeField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/time-field'], function (
                Logger,
                TimeField) {
            expectTypedFieldMarkup(Logger, TimeField);
            done();
        });
    });
    it('TextField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/text-field'], function (
                Font,
                Color,
                Cursor,
                TextField) {
            expectTypedField(TextField, Font, Color, Cursor);
            var instance = new TextField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBeNull();

            instance.text = 'sample text';
            expect(instance.value).toEqual('sample text');
            instance.text = '';
            expect(instance.value).toBeNull();
            instance.value = 'another sample text';
            expect(instance.text).toEqual('another sample text');
            instance.value = null;
            expect(instance.text).toEqual('');

            done();
        });
    });
    it('TextField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/text-field'], function (
                Logger,
                TextField) {
            expectTypedFieldMarkup(Logger, TextField);
            done();
        });
    });
    it('TextArea.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/text-area'], function (
                Font,
                Color,
                Cursor,
                TextArea) {
            expectTypedField(TextArea, Font, Color, Cursor);
            var instance = new TextArea();
            expect(instance.text).toEqual('');
            expect(instance.value).toBeNull();

            instance.text = 'sample text';
            expect(instance.value).toEqual('sample text');
            instance.text = '';
            expect(instance.value).toBeNull();
            instance.value = 'another sample text';
            expect(instance.text).toEqual('another sample text');
            instance.value = null;
            expect(instance.text).toEqual('');

            done();
        });
    });
    it('TextArea.Markup', function (done) {
        require([
            'logger',
            'forms/fields/text-area'], function (
                Logger,
                TextArea) {
            expectTypedFieldMarkup(Logger, TextArea);
            done();
        });
    });
    it('EMailField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/email-field'], function (
                Font,
                Color,
                Cursor,
                EMailField) {
            expectTypedField(EMailField, Font, Color, Cursor);
            var instance = new EMailField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBeNull();

            instance.text = 'fd@mk.com';
            expect(instance.value).toEqual('fd@mk.com');
            instance.text = '';
            expect(instance.value).toBeNull();
            instance.value = 'dd@rf.nl';
            expect(instance.text).toEqual('dd@rf.nl');
            instance.value = null;
            expect(instance.text).toEqual('');

            done();
        });
    });
    it('EMailField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/email-field'], function (
                Logger,
                EMailField) {
            expectTypedFieldMarkup(Logger, EMailField);
            done();
        });
    });
    it('PasswordField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/password-field'], function (
                Font,
                Color,
                Cursor,
                PasswordField) {
            expectTypedField(PasswordField, Font, Color, Cursor);
            var instance = new PasswordField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBeNull();

            instance.text = 'fd-mk.com';
            expect(instance.value).toEqual('fd-mk.com');
            instance.text = '';
            expect(instance.value).toBeNull();
            instance.value = 'dd-rf.nl';
            expect(instance.text).toEqual('dd-rf.nl');
            instance.value = null;
            expect(instance.text).toEqual('');

            done();
        });
    });
    it('PasswordField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/password-field'], function (
                Logger,
                PasswordField) {
            expectTypedFieldMarkup(Logger, PasswordField);
            done();
        });
    });
    it('PhoneField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/phone-field'], function (
                Font,
                Color,
                Cursor,
                PhoneField) {
            expectTypedField(PhoneField, Font, Color, Cursor);
            var instance = new PhoneField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBeNull();

            instance.text = '+5 907 143 26 78';
            expect(instance.value).toEqual('+5 907 143 26 78');
            instance.text = '';
            expect(instance.value).toBeNull();
            instance.value = '+5 907 143 26 55';
            expect(instance.text).toEqual('+5 907 143 26 55');
            instance.value = null;
            expect(instance.text).toEqual('');

            done();
        });
    });
    it('PhoneField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/phone-field'], function (
                Logger,
                PhoneField) {
            expectTypedFieldMarkup(Logger, PhoneField);
            done();
        });
    });
    it('UrlField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/url-field'], function (
                Font,
                Color,
                Cursor,
                UrlField) {
            expectTypedField(UrlField, Font, Color, Cursor);
            var instance = new UrlField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBeNull();

            instance.text = 'udp://host.nl/path';
            expect(instance.value).toEqual('udp://host.nl/path');
            instance.text = '';
            expect(instance.value).toBeNull();
            instance.value = 'udp://host.nl/path1';
            expect(instance.text).toEqual('udp://host.nl/path1');
            instance.value = null;
            expect(instance.text).toEqual('');

            done();
        });
    });
    it('UrlField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/url-field'], function (
                Logger,
                UrlField) {
            expectTypedFieldMarkup(Logger, UrlField);
            done();
        });
    });
    it('NumberField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/number-field'], function (
                Font,
                Color,
                Cursor,
                NumberField) {
            expectTypedField(NumberField, Font, Color, Cursor);
            var instance = new NumberField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBeNull();

            instance.text = '09';
            expect(instance.value).toEqual(9);
            instance.text = '';
            expect(instance.value).toBeNull();
            instance.value = 67;
            expect(instance.text).toEqual('67');
            instance.step = 100;
            expect(instance.step).toEqual(100);
            instance.minimum = -100;
            expect(instance.minimum).toEqual(-100);
            instance.maximum = 100;
            expect(instance.maximum).toEqual(100);
            instance.value = -200;
            expect(instance.value).toEqual(-200); // value is assignable regardless of constraints
            instance.text = 'hh';
            expect(instance.text).toEqual('-200');
            expect(instance.value).toEqual(-200);
            instance.value = NaN;
            expect(instance.text).toEqual('-200');
            expect(instance.value).toEqual(-200);
            instance.value = null;
            expect(instance.text).toEqual('');
            if (instance.error)
                instance.error = null;

            done();
        });
    });
    it('NumberField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/number-field'], function (
                Logger,
                NumberField) {
            expectTypedFieldMarkup(Logger, NumberField);
            done();
        });
    });
    it('RangeField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/range-field'], function (
                Font,
                Color,
                Cursor,
                RangeField) {
            expectTypedField(RangeField, Font, Color, Cursor);
            var instance = new RangeField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBeNull();

            instance.text = '09';
            expect(instance.value).toEqual(9);
            instance.text = '';
            expect(instance.value).toBeNull();
            instance.value = 67;
            expect(instance.text).toEqual('67');
            instance.step = 100;
            expect(instance.step).toEqual(100);
            instance.minimum = -100;
            expect(instance.minimum).toEqual(-100);
            instance.maximum = 100;
            expect(instance.maximum).toEqual(100);
            instance.value = -200;
            expect(instance.value).toEqual(-200); // value is assignable regardless of constraints
            instance.text = 'hh';
            expect(instance.text).toEqual('-200');
            expect(instance.value).toEqual(-200);
            instance.value = NaN;
            expect(instance.text).toEqual('-200');
            expect(instance.value).toEqual(-200);
            instance.value = null;
            expect(instance.text).toEqual('');
            if (instance.error)
                instance.error = null;

            done();
        });
    });
    it('RangeField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/range-field'], function (
                Logger,
                RangeField) {
            expectTypedFieldMarkup(Logger, RangeField);
            done();
        });
    });
    it('ProgressField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/progress-field'], function (
                Font,
                Color,
                Cursor,
                ProgressField) {
            expectTypedField(ProgressField, Font, Color, Cursor);
            var instance = new ProgressField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBeNull();

            instance.text = '09';
            expect(instance.value).toEqual(9);
            instance.text = '';
            expect(instance.value).toBeNull();
            instance.text = '9.56';
            expect(instance.value).toEqual(9.56);
            instance.text = '';
            expect(instance.value).toBeNull();

            instance.value = 78;
            expect(instance.text).toEqual('78');
            instance.value = null;
            expect(instance.text).toEqual('');
            instance.value = 56;
            expect(instance.text).toEqual('56');
            instance.text = 'hh';
            expect(instance.text).toEqual('56');
            expect(instance.value).toEqual(56);
            instance.value = NaN;
            expect(instance.text).toEqual('56');
            expect(instance.value).toEqual(56);
            instance.value = null;
            expect(instance.text).toEqual('');
            if (instance.error)
                instance.error = null;

            done();
        });
    });
    it('ProgressField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/progress-field'], function (
                Logger,
                ProgressField) {
            expectTypedFieldMarkup(Logger, ProgressField);
            done();
        });
    });
    it('MeterField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/meter-field'], function (
                Font,
                Color,
                Cursor,
                MeterField) {
            expectTypedField(MeterField, Font, Color, Cursor);
            var instance = new MeterField();
            expect(instance.text).toEqual('');
            expect(instance.value).toBeNull();

            instance.text = '09';
            expect(instance.value).toEqual(9);
            instance.text = '';
            expect(instance.value).toBeNull();
            instance.text = '9.56';
            expect(instance.value).toEqual(9.56);
            instance.text = '';
            expect(instance.value).toBeNull();

            instance.value = 78;
            expect(instance.text).toEqual('78');
            instance.value = null;
            expect(instance.text).toEqual('');
            instance.value = 56;
            expect(instance.text).toEqual('56');
            instance.text = 'hh';
            expect(instance.text).toEqual('56');
            expect(instance.value).toEqual(56);
            instance.value = NaN;
            expect(instance.text).toEqual('56');
            expect(instance.value).toEqual(56);
            instance.value = null;
            expect(instance.text).toEqual('');
            if (instance.error)
                instance.error = null;

            done();
        });
    });
    it('MeterField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/meter-field'], function (
                Logger,
                MeterField) {
            expectTypedFieldMarkup(Logger, MeterField);
            done();
        });
    });
    it('DropDownField.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/drop-down-field'], function (
                Font,
                Color,
                Cursor,
                DropDownField) {
            expectWidget(new DropDownField(), Font, Color, Cursor);

            var instance = new DropDownField();
            expect(instance.value).toBeNull();
            expect(instance.text).toEqual('< . >');
            expect(instance.count).toEqual(1);

            instance.visibleItemCount = 4;
            expect(instance.visibleItemCount).toEqual(4);

            instance.addValue('1', 1);
            instance.addValue('2', 2);
            instance.addValue('3', 3);
            instance.addValue('4', 4);
            instance.addValue('5', 5);
            expect(instance.count).toEqual(6);
            expect(instance.labelAt(4)).toEqual('4');

            instance.addValue('44', 4);
            expect(instance.count).toEqual(6);
            expect(instance.labelAt(4)).toEqual('44');


            for (var i = 1; i < instance.count; i++) {
                expect(instance.valueAt(i)).toEqual(i);
                expect(instance.indexOfValue(i)).toEqual(i);
            }

            for (i = instance.count - 1; i >= 0; i--) {
                instance.removeValue(i);
            }
            expect(instance.count).toEqual(1);
            expect(instance.valueAt(0)).toBeNull();
            expect(instance.value).toBeNull();
            expect(instance.labelAt(0)).toEqual('< . >');
            expect(instance.text).toEqual('< . >');

            instance.emptyText = 'Select an item please';
            expect(instance.text).toEqual('Select an item please');
            expect(instance.labelAt(0)).toEqual('Select an item please');

            instance.addValue('1', 1);
            instance.addValue('2', 2);
            instance.addValue('3', 3);
            instance.addValue('4', 4);
            instance.addValue('5', 5);
            expect(instance.count).toEqual(6);

            instance.clear();
            expect(instance.count).toEqual(1);
            expect(instance.valueAt(0)).toBeNull();
            expect(instance.value).toBeNull();
            expect(instance.labelAt(0)).toEqual('Select an item please');
            expect(instance.text).toEqual('Select an item please');
            done();
        });
    });
    it('DropDownField.Markup', function (done) {
        require([
            'logger',
            'forms/fields/drop-down-field'], function (
                Logger,
                DropDownField) {
            var instance = new DropDownField();
            document.body.appendChild(instance.element);
            instance.addValue('1', 1);
            instance.addValue('2', 2);
            instance.addValue('3', 3);
            instance.addValue('4', 4);
            instance.addValue('5', 5);
            instance.addValue('6', 6);
            instance.addValue('7', 7);
            instance.addValue('8', 8);
            instance.addValue('9', 9);
            instance.addValue('10', 10);

            instance.emptyText = 'Select an item please';
            expect(instance.text).toEqual('Select an item please');
            instance.onActionPerformed = function () {
                Logger.info('Action performed on ' + instance.constructor.name);
            };
            instance.onItemSelected = function (evt) {
                Logger.info('Item selected on ' + instance.constructor.name + '; item: ' + evt.item);
            };
            instance.onValueChange = function (evt) {
                Logger.info(instance.constructor.name + ' value changed: newValue: ' + evt.newValue + '; oldValue: ' + evt.oldValue);
            };
            document.body.removeChild(instance.element);
            done();
        });
    });
    it('RichTextArea.Structure', function (done) {
        require([
            'common-utils/font',
            'common-utils/color',
            'common-utils/cursor',
            'forms/fields/rich-text-area'], function (
                Font,
                Color,
                Cursor,
                RichTextArea) {
            expectWidget(new RichTextArea(), Font, Color, Cursor);

            var instance = new RichTextArea();
            expect(instance.value).toBeNull();
            expect(instance.text).toEqual('');

            instance.value = '<p>content</p>';
            expect(instance.text).toEqual('content');

            instance.value = null;
            expect(instance.text).toEqual('');

            instance.text = '<p>content</p>';
            expect(instance.value).toEqual('&lt;p&gt;content&lt;/p&gt;');

            instance.text = '';
            expect(instance.value).toBeNull();

            done();
        });
    });
    it('RichTextArea.Markup', function (done) {
        require([
            'invoke',
            'logger',
            'forms/fields/rich-text-area'], function (
                Invoke,
                Logger,
                RichTextArea) {
            var instance = new RichTextArea();
            document.body.appendChild(instance.element);

            instance.onActionPerformed = function () {
                Logger.info('Action performed on ' + instance.constructor.name);
            };
            instance.onValueChange = function (evt) {
                Logger.info(instance.constructor.name + ' value changed: newValue: ' + evt.newValue + '; oldValue: ' + evt.oldValue);
            };

            spyOn(instance, 'onValueChange');

            instance.value = 'Sample content';

            Invoke.later(function () {
                expect(instance.onValueChange.calls.count()).toEqual(1);
                document.body.removeChild(instance.element);
                done();
            });
        });
    });
});
