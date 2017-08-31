define([
    '../../extend',
    '../fields/drop-down-field',
    '../bound',
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
        var list = true;

        Object.defineProperty(this, 'list', {
            get: function () {
                return list;
            },
            set: function (aValue) {
                if (list !== aValue) {
                    list = aValue;
                }
            }
        });

        var lengthReg = null;
        var elementsReg = null;
        var displayList = null;
        var displayField = null;
        function rebindList() {
            if (lengthReg) {
                lengthReg.unlisten();
                lengthReg = null;
            }
            if (elementsReg) {
                elementsReg.unlisten();
                elementsReg = null;
            }
            self.clear();
            bindList();
            if (displayList) {
                lengthReg = Bound.listen(displayList, function (evt) {
                    if (evt.propertyName === 'length')
                        rebindList();
                });
                elementsReg = Bound.observeElements(displayList, function (evt) {
                    if (evt.propertyName === displayField)
                        self.updateLabel(evt.source, evt.newValue);
                });
            }
        }
        function bindList() {
            if (displayList && displayField) {
                displayList.forEach(function (item) {
                    self.addValue(item[displayField], item);
                });
            }
        }

        Object.defineProperty(this, 'displayList', {
            get: function () {
                return displayList;
            },
            set: function (aValue) {
                if (displayList !== aValue) {
                    displayList = aValue;
                    rebindList();
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
                    rebindList();
                }
            }
        });
    }
    extend(ModelDropDownField, Field);
    return ModelDropDownField;
});
