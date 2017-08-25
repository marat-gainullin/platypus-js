define([
    '../../extend',
    '../i18n',
    './text-value-field'], function (
        extend,
        i18n,
        TextValueField) {
    function DateField() {
        TextValueField.call(this);
        var self = this;
        var value = null;

        var box = this.element;
        box.type = 'date';

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
                    box.value = value !== null ? format(value) : '';
                    self.fireValueChanged(oldValue);
                }
            }
        });
    }
    extend(DateField, TextValueField);
    return DateField;
});
