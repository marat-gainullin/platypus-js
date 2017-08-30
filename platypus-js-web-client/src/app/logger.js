define(function () {
    function date() {
        // We want JSON format on the one hand and on another hand, we want local time
        var moment = new Date();
        var timeZone = -moment.getTimezoneOffset() * 60000;
        var local = (new Date(moment.valueOf() + timeZone)).toJSON();
        return local.substring(0, local.length - 1);
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
            if (console) {
                console.log(date() + " INFO " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    Object.defineProperty(module, "fine", {
        value: function (aMessage) {
            if (console) {
                console.log(date() + " FINE " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    Object.defineProperty(module, "finer", {
        value: function (aMessage) {
            if (console) {
                console.log(date() + " FINER " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    Object.defineProperty(module, "finest", {
        value: function (aMessage) {
            if (console) {
                console.log(date() + " FINEST " + aMessage);
                messagePrinted(aMessage);
            }
        }
    });
    return module;
});