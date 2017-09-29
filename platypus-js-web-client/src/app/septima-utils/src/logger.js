define(function () {
    function date() {
        // We want JSON format on the one hand and local time on another hand
        var moment = new Date();
        var timeZone = -moment.getTimezoneOffset() * 60000;
        var local = (new Date(moment.valueOf() + timeZone)).toJSON();
        return local.substring(0, local.length - 1);
    }

    var module = {};
    Object.defineProperty(module, 'config', {
        value: function (aMessage) {
            if (console) {
                console.log(date() + ' CONFIG ' + aMessage);
            }
        }
    });
    Object.defineProperty(module, 'severe', {
        value: function (aMessage) {
            if (console) {
                if (aMessage instanceof TypeError) {
                    console.error(aMessage);
                } else {
                    console.error(date() + ' SEVERE ' + aMessage);
                }
            }
        }
    });
    Object.defineProperty(module, 'warning', {
        value: function (aMessage) {
            if (console) {
                if (aMessage instanceof TypeError) {
                    console.warn(aMessage);
                } else {
                    console.warn(date() + ' WARNING ' + aMessage);
                }
            }
        }
    });
    Object.defineProperty(module, 'info', {
        value: function (aMessage) {
            if (console) {
                console.info(date() + ' INFO ' + aMessage);
            }
        }
    });
    Object.defineProperty(module, 'fine', {
        value: function (aMessage) {
            if (console) {
                console.log(date() + ' FINE ' + aMessage);
            }
        }
    });
    Object.defineProperty(module, 'finer', {
        value: function (aMessage) {
            if (console) {
                console.log(date() + ' FINER ' + aMessage);
            }
        }
    });
    Object.defineProperty(module, 'finest', {
        value: function (aMessage) {
            if (console) {
                console.log(date() + ' FINEST ' + aMessage);
            }
        }
    });
    return module;
});