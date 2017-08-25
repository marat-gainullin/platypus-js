define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function TextField(text) {
        if (arguments.length < 1)
            text = '';
        TextValueField.call(this);
        var self = this;
        var value = null;

        var box = this.element;
        box.type = 'text';
        box.value = text;

        function textChanged() {
            var oldValue = value;
            value = text === '' ? null : text;
            self.fireValueChanged(oldValue);
        }

        Object.defineProperty(this, 'textChanged', {
            enumerable: false,
            get: function () {
                return textChanged;
            }
        });

        Object.defineProperty(this, 'text', {
            get: function () {
                return box.value;
            },
            set: function (aValue) {
                if (box.value !== aValue) {
                    box.value = aValue;
                    textChanged();
                }
            }
        });

        Object.defineProperty(this, 'value', {
            get: function () {
                return value;
            },
            set: function (aValue) {
                var oldValue = value;
                value = aValue;
                self.fireValueChanged(oldValue);
            }
        });
    }
    extend(TextField, TextValueField);
    return TextField;
});
