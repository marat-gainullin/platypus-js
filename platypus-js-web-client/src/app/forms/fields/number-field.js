define([
    '../../extend',
    '../i18n',
    './box-field'], function (
        extend,
        i18n,
        BoxField) {
    function NumberField(box, shell) {
        if (!box) {
            box = document.createElement('input');
            box.type = 'number';
        }
        if(!shell){
            shell = box;
        }
        BoxField.call(this, box, shell);
        var self = this;
        var value = null;

        Object.defineProperty(this, 'minimum', {
            get: function () {
                var boxMin = parseFloat(box.min);
                return isNaN(boxMin) ? null : boxMin;
            },
            set: function (aValue) {
                box.min = aValue;
            }
        });
        Object.defineProperty(this, 'maximum', {
            get: function () {
                var boxMax = parseFloat(box.max);
                return isNaN(boxMax) ? null : boxMax;
            },
            set: function (aValue) {
                box.max = aValue;
            }
        });
        Object.defineProperty(this, 'step', {
            get: function () {
                var boxStep = parseFloat(box.step);
                return isNaN(boxStep) ? null : boxStep;
            },
            set: function (aValue) {
                box.step = aValue;
            }
        });


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
                    box.value = value !== null ? value + '' : '';
                    self.fireValueChanged(oldValue);
                }
            }
        });
    }
    extend(NumberField, BoxField);
    return NumberField;
});
