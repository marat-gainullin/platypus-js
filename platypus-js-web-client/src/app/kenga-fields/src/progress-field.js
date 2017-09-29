define([
    'core/logger',
    'core/extend',
    './i18n',
    './constraint-field'], function (
        Logger,
        extend,
        i18n,
        ConstraintField) {
    function ProgressField(box, shell) {
        if (!box)
            box = document.createElement('progress');
        if (!shell) {
            shell = box;
        }

        ConstraintField.call(this, box, shell);
        var self = this;
        box.classList.add('p-indeterminate');

        function checkNullClasses() {
            if (value === null) {
                box.classList.add('p-indeterminate');
                box.removeAttribute('value');
            } else {
                box.classList.remove('p-indeterminate');
            }
        }

        var value = null;
        function progressValueChanged() {
            var oldValue = value;
            value = box.value;
            if (value !== oldValue) {
                checkNullClasses();
                self.fireValueChanged(oldValue);
            }
        }

        Object.defineProperty(this, 'textChanged', {
            enumerable: false,
            get: function () {
                return progressValueChanged;
            }
        });

        Object.defineProperty(this, 'text', {
            get: function () {
                return value === null ? '' : value + '';
            },
            set: function (aValue) {
                var oldValue = value;
                if (!aValue) { // '', or even null
                    value = null;
                } else {
                    var parsed = parseFloat(aValue);
                    if (isNaN(parsed)) {
                        self.error = i18n['not.a.number'] + '(' + aValue + ')';
                    } else {
                        value = parsed;
                    }
                }
                if (value !== oldValue) {
                    checkNullClasses();
                    self.fireValueChanged(oldValue);
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
                    try {
                        box.value = aValue != null ? aValue : 0;
                        value = aValue;
                        checkNullClasses();
                        self.fireValueChanged(oldValue);
                    } catch (e) {
                        Logger.warning(e);
                    }
                }
            }
        });
    }
    extend(ProgressField, ConstraintField);
    return ProgressField;
});
