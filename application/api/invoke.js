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
    var module = {};
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