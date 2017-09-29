define([
    'core/extend',
    './box-field'], function (
        extend,
        BoxField) {
    function TextField(text, box, shell) {
        if (arguments.length < 1)
            text = '';
        if (!box) {
            box = document.createElement('input');
            box.type = 'text';
        }
        if (!shell) {
            shell = box;
        }
        box.value = text;
        BoxField.call(this, box, shell);

        var self = this;
        var value = null;

        function textChanged() {
            var oldValue = value;
            value = box.value === '' ? null : box.value;
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
                value = aValue !== undefined ? aValue : null;
                box.value = value;
                self.fireValueChanged(oldValue);
            }
        });
    }
    extend(TextField, BoxField);
    return TextField;
});
