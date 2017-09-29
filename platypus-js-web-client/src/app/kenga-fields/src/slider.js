define([
    'core/extend',
    './i18n',
    './constraint-field'], function (
        extend,
        i18n,
        ConstraintField) {
    function RangeField(shell) {
        var box = document.createElement('input');
        box.type = 'range';
        if (!shell)
            shell = box;

        ConstraintField.call(this, box, shell);
        var self = this;
        var value = null;
        var text = '';
        box.classList.add('p-indeterminate');

        function checkNullClasses() {
            if (value === null) {
                box.classList.add('p-indeterminate');
            } else {
                box.classList.remove('p-indeterminate');
            }
        }

        function textSourceChanged(aText) {
            var oldValue = value;
            if (aText !== '') {
                var parsed = parseFloat(aText);
                if (isNaN(parsed)) {
                    self.error = i18n['not.a.number'] + '(' + aText + ')';
                } else {
                    value = parsed;
                }
            } else {
                value = null;
            }
            checkNullClasses();
            if (value !== oldValue) {
                self.fireValueChanged(oldValue);
            }
        }

        function boxTextChanged() {
            textSourceChanged(box.value);
        }

        function textChanged() {
            textSourceChanged(text);
        }

        Object.defineProperty(this, 'textChanged', {
            enumerable: false,
            get: function () {
                return boxTextChanged;
            }
        });

        Object.defineProperty(this, 'text', {
            get: function () {
                return text;
            },
            set: function (aValue) {
                if (aValue && isNaN(parseFloat(aValue)))
                    return;
                if (text !== aValue) {
                    box.value = aValue;
                    text = aValue;
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
                        text = value != null ? value + '' : '';
                        box.value = text;
                        checkNullClasses();
                        self.fireValueChanged(oldValue);
                    }
                }
            }
        });
    }
    extend(RangeField, ConstraintField);
    return RangeField;
});
