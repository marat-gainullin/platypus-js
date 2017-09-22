define([
    '../../ui',
    '../../invoke',
    '../../extend',
    '../widget',
    '../events/value-change-event'], function (
        Ui,
        Invoke,
        extend,
        Widget,
        ValueChangeEvent) {
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
    }
    extend(BoxField, Widget);
    return BoxField;
});
       