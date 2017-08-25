define([
    '../../extend',
    '../../common-utils/color',
    './text-value-field'], function (
        extend,
        Color,
        TextValueField) {
    function ColorField() {
        TextValueField.call(this);
        var self = this;
        var value = null;

        var box = this.element;
        box.type = 'color';

        function format(color) {
            return color ? color.toString() : '#000000';
        }

        function parse(source) {
            var parsed = Color.parse(source);
            return parsed ? new Color(parsed.red, parsed.green, parsed.blue, parsed.alpha) : parsed;
        }

        function textChanged() {
            var oldValue = value;
            value = parse(box.value);
            if (value !== oldValue) {
                self.fireValueChanged(oldValue);
            }
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
                    box.value = format(value);
                    self.fireValueChanged(oldValue);
                }
            }
        });
    }
    extend(ColorField, TextValueField);
    return ColorField;
});
