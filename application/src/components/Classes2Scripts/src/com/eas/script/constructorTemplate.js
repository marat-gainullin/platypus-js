(function() {
    
    var javaClass = Java.type("${Type}");
    javaClass.setPublisher(function(aDelegate) {
        return new P.${Name}(null, null, null, aDelegate);
    });
    
${JsDoc}
    P.${Name} = function (${Params}) {

        var maxArgs = ${MaxArgs};
        var ${Delegate} = arguments.length > maxArgs ?
            arguments[maxArgs] : new javaClass(${UnwrappedParams});

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return ${Delegate};
                };
            }
        });
    
${Props}
        delegate.setPublished(this);
    };
})();