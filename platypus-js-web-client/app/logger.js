/* global Java */

define(function () {
    function date() {
        return JSON.stringify(new Date());
    }
    var module = {};
    Object.defineProperty(module, "config", {
        value: function (aMessage) {
            if (console)
                console.log(date() + " CONFIG " + aMessage);
        }
    });
    Object.defineProperty(module, "severe", {
        value: function (aMessage) {
            if (console)
                console.log(date() + " SEVERE " + aMessage);
        }
    });
    Object.defineProperty(module, "warning", {
        value: function (aMessage) {
            if (console)
                console.log(date() + " WARNING " + aMessage);
        }
    });
    Object.defineProperty(module, "info", {
        value: function (aMessage) {
            if (console)
                console.log(date() + " INFO " + aMessage);
        }
    });
    Object.defineProperty(module, "fine", {
        value: function (aMessage) {
            if (console)
                console.log(date() + " FINE " + aMessage);
        }
    });
    Object.defineProperty(module, "finer", {
        value: function (aMessage) {
            if (console)
                console.log(date() + " FINER " + aMessage);
        }
    });
    Object.defineProperty(module, "finest", {
        value: function (aMessage) {
            if (console)
                console.log(date() + " FINEST " + aMessage);
        }
    });
    return module;
});