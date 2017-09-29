define([
    'core/extend',
    './i18n',
    './box-field'], function (
        extend,
        i18n,
        BoxField) {
    function DateField(shell) {
        var box = document.createElement('input');
        box.type = 'date';
        if(!shell)
            shell = box;
        
        BoxField.call(this, box, shell);
        var self = this;
        var value = null;

        function parse(source) {
            return new Date(source + 'T00:00:00.000Z');
        }

        function format(date) {
            var json = date.toJSON();
            return json.substring(0, json.length - 'T00:00:00.000Z'.length);
        }

        function textChanged() {
            var oldValue = value;
            if (box.value !== '') {
                var parsed = parse(box.value);
                if (isNaN(parsed.valueOf())) {
                    self.error = i18n['not.a.date'] + '(' + box.value + ')';
                } else {
                    value = parsed;
                }
            } else {
                value = null;
            }
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
                    box.value = value != null ? format(value) : '';
                    self.fireValueChanged(oldValue);
                }
            }
        });
    }
    extend(DateField, BoxField);
    return DateField;
});
