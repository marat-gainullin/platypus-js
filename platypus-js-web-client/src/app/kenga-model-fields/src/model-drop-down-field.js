define([
    'core/extend',
    '../fields/drop-down-field',
    'ui/bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelDropDownField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);

        var self = this;

        var lengthReg = null;
        var elementsReg = null;
        var values = null;
        var displayField = null;
        
        function unbindValues(){
            if (lengthReg) {
                lengthReg.unlisten();
                lengthReg = null;
            }
            if (elementsReg) {
                elementsReg.unlisten();
                elementsReg = null;
            }
            self.clear();
        }
        
        function bindValues() {
            if (values) {
                values.forEach(function (item) {
                    self.addValue(displayField ? item[displayField] : item /* assume plain values */, item);
                });
                lengthReg = Bound.listen(values, function (evt) {
                    if (evt.propertyName === 'length')
                        rebindValues();
                });
                elementsReg = Bound.observeElements(values, function (evt) {
                    if (evt.propertyName === displayField)
                        self.updateLabel(evt.source, evt.newValue);
                });
            }
        }

        function rebindValues() {
            unbindValues();
            bindValues();
        }
        
        Object.defineProperty(this, 'values', {
            get: function () {
                return values;
            },
            set: function (aValue) {
                if (values !== aValue) {
                    unbindValues();
                    values = aValue;
                    bindValues();
                }
            }
        });


        Object.defineProperty(this, 'displayField', {
            get: function () {
                return displayField;
            },
            set: function (aValue) {
                if (displayField !== aValue) {
                    displayField = aValue;
                    rebindValues();
                }
            }
        });
    }
    extend(ModelDropDownField, Field);
    return ModelDropDownField;
});
