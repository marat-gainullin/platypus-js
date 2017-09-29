define([
    'core/extend',
    './box-field'], function (
        extend,
        BoxField) {
    function ConstraintField(box, shell) {
        if (!box) {
            box = document.createElement('input');
            box.type = 'number';
        }
        if (!shell) {
            shell = box;
        }
        BoxField.call(this, box, shell);
        var self = this;

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
    }
    extend(ConstraintField, BoxField);
    return ConstraintField;
});
