define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function FormattedField() {
        TextValueField.call(this);
        var self = this;
        var value = null;

        var box = this.element;

        function textChanged() {
            var oldValue = value;
            value = self.onParse ? self.onParse(box.value) : box.value;
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
                if (value !== aValue) {
                    var oldValue = value;
                    value = aValue;
                    box.value = self.onFormat ? self.onFormat(value) : value + '';
                    self.fireValueChanged(oldValue);
                }
            }
        });
    }
    extend(FormattedField, TextValueField);
    return FormattedField;
});
