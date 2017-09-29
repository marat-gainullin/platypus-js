define([
    'ui/utils',
    'core/extend',
    'core/invoke',
    'ui/widget',
    'ui/events/value-change-event'], function (
        Ui,
        extend,
        Invoke,
        Widget,
        ValueChangeEvent) {
    function RadioButton(text, selected, onActionPerformed) {
        if (arguments.length < 2)
            selected = false;
        if (arguments.length < 1)
            text = '';

        Widget.call(this, document.createElement('label'));
        var label = this.element;
        var box = document.createElement('input');
        box.type = 'radio';
        this.opaque = false;
        this.onActionPerformed = onActionPerformed;

        var horizontalTextPosition = Ui.HorizontalPosition.RIGHT;

        var self = this;

        function applyText() {
            label.innerText = text;
            label.appendChild(box);
        }

        function aplySelected() {
            if (selected === null) {
                box.indeterminate = true;
            } else {
                box.indeterminate = false;
                box.checked = !!selected;
            }
        }

        function applyPosition() {
            label.style.direction =
                    horizontalTextPosition === Ui.HorizontalPosition.RIGHT
                    || horizontalTextPosition === Ui.HorizontalPosition.CENTER ?
                    'rtl' : 'ltr';
        }

        aplySelected();
        applyText();
        applyPosition();

        var clickReg = Ui.on(box, Ui.Events.CLICK, function (evt) {
            self.fireActionPerformed();
        });
                
        Object.defineProperty(this, 'text', {
            get: function () {
                return text;
            },
            set: function (aValue) {
                if (text !== aValue) {
                    text = aValue;
                    applyText();
                }
            }
        });

        /**
         * Horizontal position of the text relative to the icon.
         */
        Object.defineProperty(this, "horizontalTextPosition", {
            get: function () {
                return horizontalTextPosition;
            },
            set: function (aValue) {
                if (horizontalTextPosition !== aValue) {
                    horizontalTextPosition = aValue;
                    applyPosition();
                }
            }
        });

        Object.defineProperty(this, 'selected', {
            get: function () {
                return selected;
            },
            set: function (aValue) {
                if (selected !== aValue) {
                    var oldValue = selected;
                    selected = aValue;
                    aplySelected();
                    fireValueChanged(oldValue);
                }
            }
        });

        Object.defineProperty(this, 'value', {
            get: function () {
                return self.selected;
            },
            set: function (aValue) {
                self.selected = aValue;
            }
        });

        var valueChangeHandlers = new Set();
        function addValueChangeHandler(handler) {
            valueChangeHandlers.add(handler);
            return {
                removeHandler: function () {
                    valueChangeHandlers.delete(handler);
                }

            };
        }

        Object.defineProperty(this, 'addValueChangeHandler', {
            get: function () {
                return addValueChangeHandler;
            }
        });

        function fireValueChanged(oldValue) {
            var event = new ValueChangeEvent(self, oldValue, selected);
            valueChangeHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        this.addActionHandler(function () {
            if (box.indeterminate) {
                self.value = null;
            } else {
                self.value = box.checked;
            }
        });

        var buttonGroup = null;

        Object.defineProperty(this, 'buttonGroup', {
            get: function () {
                return buttonGroup;
            },
            set: function (aValue) {
                var oldGroup = buttonGroup;
                buttonGroup = aValue;
                if (oldGroup)
                    oldGroup.remove(self);
                if (buttonGroup)
                    buttonGroup.add(self);
            }
        });
    }
    extend(RadioButton, Widget);
    return RadioButton;
});
       