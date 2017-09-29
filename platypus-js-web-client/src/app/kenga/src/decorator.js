define([
    'ui/utils'], function (
        Ui) {
    function Decorator() {
        var self = this;

        this.element.classList.add("p-decorator");

        var nullable = true;
        var onSelect = null;

        var btnClear = document.createElement('div');
        btnClear.className = 'p-decoration p-clear';
        Ui.on(btnClear, Ui.Events.CLICK, function (evt) {
            evt.stopPropagation();
            self.value = null;
        });

        var btnSelect = document.createElement('div');
        btnSelect.className = 'p-decoration p-select';
        Ui.on(btnSelect, Ui.Events.CLICK, function (evt) {
            evt.stopPropagation();
            onSelect.call(self, self);
        });


        function redecorate() {
            self.element.classList.remove("p-decorator-nullable-selectable");
            self.element.classList.remove("p-decorator-nullable");
            self.element.classList.remove("p-decorator-selectable");
            if (btnClear.parentNode === self.element)
                self.element.removeChild(btnClear);
            if (btnSelect.parentNode === self.element)
                self.element.removeChild(btnSelect);
            if (nullable && onSelect) {
                self.element.classList.add("p-decorator-nullable-selectable");
                self.element.appendChild(btnClear);
                self.element.appendChild(btnSelect);
            } else if (nullable) {
                self.element.classList.add("p-decorator-nullable");
                self.element.appendChild(btnClear);
            } else if (onSelect) {
                self.element.classList.add("p-decorator-selectable");
                self.element.appendChild(btnSelect);
            }
        }
        redecorate();

        Object.defineProperty(this, 'nullable', {
            get: function () {
                return nullable;
            },
            set: function (aValue) {
                if (nullable !== aValue) {
                    nullable = aValue;
                    redecorate();
                }
            }
        });
        Object.defineProperty(this, 'onSelect', {
            get: function () {
                return onSelect;
            },
            set: function (aValue) {
                if (onSelect !== aValue) {
                    onSelect = aValue;
                    redecorate();
                }
            }
        });

    }
    return Decorator;
});