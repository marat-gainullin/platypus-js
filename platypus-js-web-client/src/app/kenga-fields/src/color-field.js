define([
    'core/extend',
    'ui/color',
    './box-field'], function (
        extend,
        Color,
        BoxField) {
    function ColorField(shell) {
        var box = document.createElement('input');
        box.type = 'color';
        if(!shell)
            shell = box;

        BoxField.call(this, box, shell);
        var self = this;
        var value = null;

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
    extend(ColorField, BoxField);
    return ColorField;
});
