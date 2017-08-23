/* global expect */

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
            'forms/button'], function (
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
            'forms/button'], function (
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
            'forms/button'], function (
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
            'forms/toggle-button'], function (
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
            'forms/toggle-button'], function (
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
            'forms/drop-down-button'], function (
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
            'forms/drop-down-button'], function (
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
            'forms/check-box'], function (
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
            'forms/check-box'], function (
                Logger,
                Ui,
                Checkbox) {
            expectCheckRadioMarkup(Logger, Ui, Checkbox);
            done();
        });
    });
    it('RadioButton.Structure', function (done) {
        require([
            'forms/radio-button'], function (
                RadioButton) {
            expectCheckRadio(RadioButton);
            done();
        });
    });
    it('RadioButton.Markup', function (done) {
        require([
            'logger',
            'ui',
            'forms/radio-button'], function (
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
            'forms/check-box',
            'forms/radio-button',
            'forms/toggle-button',
            'forms/button-group'], function (
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
            'forms/check-box',
            'forms/radio-button',
            'forms/toggle-button',
            'forms/button-group'], function (
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
});