/* global Java */

define(['boxing'], function(B) {
${JsDoc}
    function ${Name}(${Params}) {
        var maxArgs = ${MaxArgs};
        var ${Delegate} = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : ${UnwrappedParams};

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return ${Delegate};
            }
        });
        if(${Name}.superclass)
            ${Name}.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
${Props}${Body}    };
${Methods}
    var className = "${Type}";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new ${Name}(${NullParams}aDelegate);
    });
    return ${Name};
});