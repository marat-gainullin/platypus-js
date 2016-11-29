define(function(){
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();

    function invokeDelayed(aTimeout, aTarget) {
        if (arguments.length < 2)
            throw "invokeDelayed needs 2 arguments - timeout, callback.";
        space.schedule(aTarget, aTimeout);
    }

    function invokeLater(aTarget) {
        space.enqueue(aTarget);
    }
    var module = {
        /**
         * Adds an action call to agent's event queue.
         * @param {Function} anAction A callback function to be called, when agent's queue will handle it.
         * @returns {undefined}
         */
        later: function(anAction){},
        /**
         * Holds an action for aTimeout and than, when aTimeout will elapse, adds it to agent's event queue.
         * @param {Number} aTimeout Amount of time to hold the action.
         * @param {Function} anAction Callback function, that will be invoked when the timeout elapses.
         * @returns {undefined}
         */
        delayed: function(aTimeout, anAction){}
    };
    Object.defineProperty(module, 'later', {
        enumerable: true,
        value: invokeLater
    });
    Object.defineProperty(module, 'delayed', {
        enumerable: true,
        value: invokeDelayed
    });
    return module;
});