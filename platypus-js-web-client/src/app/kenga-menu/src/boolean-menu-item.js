define([
    'core/invoke',
    'ui/utils',
    'core/extend',
    './menu-element',
    'ui/events/value-change-event'], function (
        Invoke,
        Ui,
        extend,
        MenuElement,
        ValueChangeEvent) {
    function BooleanMenuItem(radio, text, selected, onActionPerformed) {
        if (arguments.length < 3)
            selected = false;
        if (arguments.length < 2)
            text = '';
        var iconTextGap = 4;

        MenuElement.call(this);
        var self = this;

        this.onActionPerformed = onActionPerformed;

        var clickReg = Ui.on(this.element, Ui.Events.CLICK, function () {
            self.fireActionPerformed();
            self.selected = !self.selected;
            // Ui.closeMenuSession();
        });

        var horizontalTextPosition = Ui.HorizontalPosition.RIGHT;

        var paragraph = document.createElement('p');
        paragraph.classList.add('p-paragraph');
        this.element.appendChild(paragraph);

        this.element.classList.add('p-menu-item');

        function aplySelected() {
            if (selected === null) {
                radio.indeterminate = true;
            } else {
                radio.indeterminate = false;
                radio.checked = !!selected;
            }
        }

        function applyPosition() {
            if (radio) {
                radio.style.marginLeft = radio.style.marginRight = '';
            }
            if (horizontalTextPosition === Ui.HorizontalPosition.LEFT) {
                if (radio) {
                    self.element.insertBefore(paragraph, radio);
                    if (iconTextGap > 0 && text)
                        radio.style.marginLeft = iconTextGap + 'px';
                }
            } else if (horizontalTextPosition === Ui.HorizontalPosition.RIGHT) {
                if (radio) {
                    self.element.insertBefore(radio, paragraph);
                    if (iconTextGap > 0 && text)
                        radio.style.marginRight = iconTextGap + 'px';
                }
            } // else // value of 'horizontalTextPosition' is unknown
        }

        function applyText() {
            paragraph.innerText = text;
        }

        aplySelected();
        applyPosition();
        applyText();

        Object.defineProperty(this, "text", {
            get: function () {
                return text;
            },
            set: function (aValue) {
                if (text !== aValue) {
                    text = aValue;
                    applyText();
                }
            }
        });
        Object.defineProperty(this, "iconTextGap", {
            get: function () {
                return iconTextGap;
            },
            set: function (aValue) {
                if (iconTextGap !== aValue) {
                    iconTextGap = aValue;
                    applyPosition();
                }
            }
        });
        /**
         * Horizontal position of the text relative to the icon.
         */
        Object.defineProperty(this, "horizontalTextPosition", {
            get: function () {
                return horizontalTextPosition;
            },
            set: function (aValue) {
                if (horizontalTextPosition !== aValue) {
                    horizontalTextPosition = aValue;
                    applyPosition();
                }
            }
        });
        Object.defineProperty(this, 'selected', {
            get: function () {
                return selected;
            },
            set: function (aValue) {
                if (selected !== aValue) {
                    var oldValue = selected;
                    selected = aValue;
                    aplySelected();
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
    extend(BooleanMenuItem, MenuElement);
    return BooleanMenuItem;
});