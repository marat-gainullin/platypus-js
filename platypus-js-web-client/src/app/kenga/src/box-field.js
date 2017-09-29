define([
    'ui/utils',
    'core/invoke',
    'core/extend',
    './widget',
    './events/value-change-event',
    './events/focus-event',
    './events/blur-event'], function (
        Ui,
        Invoke,
        extend,
        Widget,
        ValueChangeEvent,
        FocusEvent,
        BlurEvent) {
    var ERROR_BUBBLE_OFFSET_PART = 0.2;
    function BoxField(box, shell) {
        if (!box) {
            box = document.createElement('input');
            box.type = 'text';
        }
        if (!shell) {
            shell = box;
        }

        Widget.call(this, box, shell);
        this.focusable = true;
        var self = this;

        /**
         * The text to be shown when component's value is absent.
         */
        Object.defineProperty(this, "emptyText", {
            configurable: true,
            get: function () {
                return box.placeholder;
            },
            set: function (aValue) {
                box.placeholder = aValue;
            }
        });

        var changeReg = Ui.on(box, Ui.Events.CHANGE, function (evt) {
            self.fireActionPerformed();
            box.checkValidity();
            if(self.error)
                showError();
            else
                self.textChanged();
        });

        var changeReg = Ui.on(box, Ui.Events.INPUT, function (evt) {
            self.error = null;
        });

        function hideError() {
            if (errorPopup && errorPopup.parentNode)
                document.body.removeChild(errorPopup);
            errorPopup = null;
        }
        this.hideError = hideError;

        var errorPopup = null;
        function showError() {
            hideError();
            errorPopup = document.createElement('div');
            errorPopup.className = 'p-error-popup';
            errorPopup.innerText = self.error;
            var left = Ui.absoluteLeft(box);
            var top = Ui.absoluteTop(box);
            errorPopup.style.left = (left + box.offsetWidth / 2) + 'px';
            errorPopup.style.top = top + box.offsetHeight + 'px';
            document.body.appendChild(errorPopup);
            errorPopup.style.left = (errorPopup.offsetLeft - errorPopup.offsetWidth * ERROR_BUBBLE_OFFSET_PART) + 'px';
        }

        this.showError = showError;

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
            var event = new ValueChangeEvent(self, oldValue, self.value);
            valueChangeHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }
        Object.defineProperty(this, 'fireValueChanged', {
            get: function () {
                return fireValueChanged;
            }
        });
        var focusHandlers = new Set();
        function addFocusHandler(handler) {
            focusHandlers.add(handler);
            return {
                removeHandler: function () {
                    focusHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addFocusHandler', {
            get: function () {
                return addFocusHandler;
            }
        });

        Ui.on(box, Ui.Events.FOCUS, fireFocus);
        function fireFocus() {
            var event = new FocusEvent(self);
            focusHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var blurHandlers = new Set();
        function addBlurHandler(handler) {
            blurHandlers.add(handler);
            return {
                removeHandler: function () {
                    blurHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addBlurHandler', {
            get: function () {
                return addBlurHandler;
            }
        });

        Ui.on(box, Ui.Events.BLUR, fireBlur);
        function fireBlur() {
            var event = new BlurEvent(self);
            blurHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }
    }
    extend(BoxField, Widget);
    return BoxField;
});
       