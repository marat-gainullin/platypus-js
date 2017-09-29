define(function () {
    function later(action) {
        var timeout = setTimeout(function () {
            clearTimeout(timeout);
            action();
        }, 0);
    }

    function delayed(aDelay, action) {
        if (arguments.length < 2)
            throw 'Invoke.delayed needs 2 arguments (delay, callback).';
        var timeout = setTimeout(function () {
            clearTimeout(timeout);
            action();
        }, +aDelay);
    }

    var module = {};
    Object.defineProperty(module, 'later', {
        enumerable: true,
        value: later
    });
    Object.defineProperty(module, 'delayed', {
        enumerable: true,
        value: delayed
    });
    return module;
});