define([
    'core/extend',
    'core/invoke',
    './button',
    'ui/events/value-change-event'], function (
        extend,
        Invoke,
        Button,
        ValueChangeEvent) {
    function ToggleButton(text, icon, selected, iconTextGap, onActionPerformed) {
        if (arguments.length < 4)
            iconTextGap = 4;
        if (arguments.length < 3)
            selected = false;
        if (arguments.length < 2)
            icon = null;
        if (arguments.length < 1)
            text = '';
        Button.call(this, text, icon, iconTextGap, onActionPerformed);
        var self = this;

        function applySelected() {
            if (selected) {
                self.element.classList.add('p-toggle-selected');
            } else {
                self.element.classList.remove('p-toggle-selected');
            }
        }

        Object.defineProperty(this, 'selected', {
            get: function () {
                return selected;
            },
            set: function (aValue) {
                if (selected !== aValue) {
                    var oldValue = selected;
                    selected = aValue;
                    applySelected();
                    fireValueChanged(oldValue);
                }
            }
        });

        Object.defineProperty(this, 'value', {
            get: function () {
                return self.selected;
            },
            set: function (aValue) {
                self.selected = aValue;
            }
        });

        var valueChangeHandlers = new Set();
        function addValueChangeHandler(handler) {
            valueChangeHandlers.add(handler);
            return {
                removeHandler: function () {
                    valueChangeHandlers.delete(handler);
                }

            };
        }

        Object.defineProperty(this, 'addValueChangeHandler', {
            get: function () {
                return addValueChangeHandler;
            }
        });

        function fireValueChanged(oldValue) {
            var event = new ValueChangeEvent(self, oldValue, selected);
            valueChangeHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }
        
        this.addActionHandler(function(){
            self.selected = !self.selected;
        });
        
        var buttonGroup = null;
        
        Object.defineProperty(this, 'buttonGroup', {
            get: function () {
                return buttonGroup;
            },
            set: function (aValue) {
                var oldGroup = buttonGroup;
                buttonGroup = aValue;
                if (oldGroup)
                    oldGroup.remove(self);
                if (buttonGroup)
                    buttonGroup.add(self);
            }
        });
    }
    extend(ToggleButton, Button);
    return ToggleButton;
});
