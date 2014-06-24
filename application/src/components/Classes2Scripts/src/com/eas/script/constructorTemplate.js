(function() {
    var javaClass = Java.type("${Type}");
    javaClass.setPublisher(function(aDelegate) {
        return new P.${Name}(${NullParams}aDelegate);
    });
    
${JsDoc}
    P.${Name} = function (${Params}) {
        var maxArgs = ${MaxArgs};
        var ${Delegate} = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : ${UnwrappedParams};

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return ${Delegate};
            }
        });
        if(P.${Name}.superclass)
            P.${Name}.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
${Props}${Body}    };
${Methods}
})();