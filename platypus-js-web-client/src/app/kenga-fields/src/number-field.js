define([
    'core/extend',
    './i18n',
    './constraint-field'], function (
        extend,
        i18n,
        ConstraintField) {
    function NumberField(box, shell) {
        if (!box) {
            box = document.createElement('input');
            box.type = 'number';
        }
        if (!shell) {
            shell = box;
        }
        ConstraintField.call(this, box, shell);
        var self = this;
        var value = null;

        function textChanged() {
            var oldValue = value;
            if (box.value !== '') {
                var parsed = parseFloat(box.value);
                if (isNaN(parsed)) {
                    self.error = i18n['not.a.number'] + '(' + box.value + ')';
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
                if (aValue && isNaN(parseFloat(aValue)))
                    return;
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
                if (!isNaN(aValue)) {
                    if (value !== aValue) {
                        var oldValue = value;
                        value = aValue;
                        box.value = value != null ? value + '' : '';
                        self.fireValueChanged(oldValue);
                    }
                }
            }
        });
    }
    extend(NumberField, ConstraintField);
    return NumberField;
});
