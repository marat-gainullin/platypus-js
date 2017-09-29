define([
    'core/extend',
    './progress-field'], function (
        extend,
        ProgressField) {
    function MeterField(shell) {
        var box = document.createElement('meter');
        if(!shell)
            shell = box;
        ProgressField.call(this, box, shell);
        
        var self = this;

        Object.defineProperty(this, 'low', {
            get: function () {
                var boxLow = parseFloat(box.low);
                return isNaN(boxLow) ? null : boxLow;
            },
            set: function (aValue) {
                box.low = aValue;
            }
        });
        Object.defineProperty(this, 'high', {
            get: function () {
                var boxHigh = parseFloat(box.high);
                return isNaN(boxHigh) ? null : boxHigh;
            },
            set: function (aValue) {
                box.high = aValue;
            }
        });
        Object.defineProperty(this, 'optimum', {
            get: function () {
                var boxOptimum = parseFloat(box.optimum);
                return isNaN(boxOptimum) ? null : boxOptimum;
            },
            set: function (aValue) {
                box.optimum = aValue;
            }
        });
    }
    extend(MeterField, ProgressField);
    return MeterField;
});
