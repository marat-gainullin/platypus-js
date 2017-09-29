define([
    'core/extend',
    './i18n',
    './box-field'], function (
        extend,
        i18n,
        BoxField) {
    function TimeField(shell) {
        var box = document.createElement('input');
        box.type = 'time';
        if(!shell)
            shell = box;
        
        BoxField.call(this, box, shell);
        var self = this;
        var value = null;

        function parse(source) {
            return (new Date('1970-01-01T' + source + 'Z')).valueOf();
        }

        function format(time) {
            var formatted = (new Date(time)).toJSON().substring('1970-01-01T'.length);
            var zi = formatted.indexOf('Z');
            if (zi > -1) {
                return formatted.substring(0, zi);
            } else {
                return formatted;
            }
        }

        function textChanged() {
            var oldValue = value;
            if (box.value !== '') {
                var parsed = parse(box.value);
                if (isNaN(parsed)) {
                    self.error = i18n['not.a.time'] + '(' + box.value + ')';
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
    extend(TimeField, BoxField);
    return TimeField;
});
