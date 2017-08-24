define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function TextField(text) {
        if (arguments.length < 1)
            text = '';
        
        TextValueField.call(this);

        var self = this;

        var box = this.element;
        
        function applyText() {
            box.value = text;
        }
        applyText();

        Object.defineProperty(this, 'text', {
            get: function () {
                return text;
            },
            set: function (aValue) {
                if (text !== aValue) {
                    var oldValue = text;
                    text = aValue;
                    applyText();
                    fireValueChanged(oldValue);
                }
            }
        });

        Object.defineProperty(this, 'value', {
            get: function () {
                return self.text;
            },
            set: function (aValue) {
                self.text = aValue;
            }
        });
    }
    extend(TextField, TextValueField);
    return TextField;
});
