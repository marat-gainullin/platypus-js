define([
    'ui/utils',
    'core/extend',
    'labels/image-paragraph'], function (
        Ui,
        extend,
        ImageParagraph) {
    function Button(text, icon, iconTextGap, onActionPerformed) {
        if (arguments.length < 3)
            iconTextGap = 4;
        if (arguments.length < 2)
            icon = null;
        if (arguments.length < 1)
            text = '';
        ImageParagraph.call(this, document.createElement('button'), text, icon, iconTextGap);
        var self = this;
        this.opaque = true;

        var actionHandlers = 0;
        var clickReg = null;
        var superAddActionHandler = this.addActionHandler;
        function addActionHandler(handler) {
            if (actionHandlers === 0) {
                clickReg = Ui.on(this.element, Ui.Events.CLICK, function () {
                    self.fireActionPerformed();
                });
            }
            actionHandlers++;
            var reg = superAddActionHandler(handler);
            return {
                removeHandler: function () {
                    if (reg) {
                        reg.removeHandler();
                        reg = null;
                        actionHandlers--;
                        if (actionHandlers === 0) {
                            clickReg.removeHandler();
                            clickReg = null;
                        }
                    }
                }
            };
        }
        Object.defineProperty(this, 'addActionHandler', {
            get: function () {
                return addActionHandler;
            }
        });
        this.onActionPerformed = onActionPerformed;
    }
    extend(Button, ImageParagraph);
    return Button;
});