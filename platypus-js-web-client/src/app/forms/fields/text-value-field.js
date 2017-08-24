define([
    '../../ui',
    '../../invoke',
    '../../extend',
    '../widget',
    '../events/value-change-event'], function (
        Ui,
        Invoke,
        extend,
        Widget,
        ValueChangeEvent) {
    function TextValueField() {
        var box = document.createElement('input');
        Widget.call(this, box);

        var self = this;

        box.type = 'text';

        /**
         * The text to be shown when component's value is absent.
         */
        this.emptyText = '';
        Object.defineProperty(this, "emptyText", {
            get: function () {
                return box.placeholder;
            },
            set: function (aValue) {
                box.placeholder = aValue;
            }
        });

        var changeReg = Ui.on(box, 'change', function (evt) {
            self.fireActionPerformed();
            self.text = box.value;
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
            var event = new ValueChangeEvent(self, oldValue, self.value);
            valueChangeHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }
        Object.defineProperty(this, 'fireValueChanged', {
            get: function () {
                return fireValueChanged;
            }
        });

    }
    extend(TextValueField, Widget);
    return TextValueField;
});
       