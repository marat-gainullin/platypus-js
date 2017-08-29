define(function () {
    function date() {
        return JSON.stringify(new Date());
    }

    function messagePrinted(aMessage) {
        if (aMessage instanceof TypeError)
            console.log(aMessage);
    }

    var module = {};
    Object.defineProperty(module, "config", {
        value: function (aMessage) {
            if (console) {
                console.log(date() + " CONFIG " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    Object.defineProperty(module, "severe", {
        value: function (aMessage) {
            if (console) {
                console.log(date() + " SEVERE " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    Object.defineProperty(module, "warning", {
        value: function (aMessage) {
            if (console) {
                console.log(date() + " WARNING " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    Object.defineProperty(module, "info", {
        value: function (aMessage) {
            if (console){
                console.log(date() + " INFO " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    Object.defineProperty(module, "fine", {
        value: function (aMessage) {
            if (console){
                console.log(date() + " FINE " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    Object.defineProperty(module, "finer", {
        value: function (aMessage) {
            if (console){
                console.log(date() + " FINER " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    Object.defineProperty(module, "finest", {
        value: function (aMessage) {
            if (console){
                console.log(date() + " FINEST " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    return module;
});