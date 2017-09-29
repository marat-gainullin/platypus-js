define(function () {

    function now(){
        return new Date().valueOf();
    }

    var COUNTER_DIGITS = 100;
    var ID = now() * COUNTER_DIGITS;

    var LONG_COUNTER_DIGITS = 1000000;
    var LONG_ID = now() * LONG_COUNTER_DIGITS;

    function generate() {
        var idMillis = Math.floor(ID / COUNTER_DIGITS);
        if (idMillis === now()) {
            var oldCounter = ID - idMillis * COUNTER_DIGITS;
            var newCounter = oldCounter + 1;
            if (newCounter === COUNTER_DIGITS) {
                // Spin with maximum duration of one millisecond ...
                var newMillis;
                do {
                    newMillis = now();
                } while (newMillis === idMillis);
                ID = newMillis * COUNTER_DIGITS;
            } else {
                ID = idMillis * COUNTER_DIGITS + newCounter;
            }
        } else {
            ID = now() * COUNTER_DIGITS;
        }
        return ID;
    }

    function generateLong() {
        var idMillis = Math.floor(LONG_ID / LONG_COUNTER_DIGITS);
        if (idMillis === now()) {
            var oldCounter = LONG_ID - idMillis * LONG_COUNTER_DIGITS;
            var newCounter = oldCounter + 1;
            if (newCounter === LONG_COUNTER_DIGITS) {
                // Spin with maximum duration of one millisecond ...
                var newMillis;
                do {
                    newMillis = now();
                } while (newMillis === idMillis);
                LONG_ID = newMillis * LONG_COUNTER_DIGITS;
            } else {
                LONG_ID = idMillis * LONG_COUNTER_DIGITS + newCounter;
            }
        } else {
            LONG_ID = now() * LONG_COUNTER_DIGITS;
        }
        return '' + LONG_ID;
    }

    var module = {};

    Object.defineProperty(module, 'generate', {
        enumerable: true,
        value: generate
    });

    Object.defineProperty(module, 'generateLong', {
        enumerable: true,
        value: generateLong
    });
    return module;
});