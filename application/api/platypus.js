(function() {
    // this === global;
    var global = this;
    var oldP = global.P;
    global.P = {};
    global.P.restore = function() {
        var ns = global.P;
        global.P = oldP;
        return ns;
    };
    /*
     global.P = this; // global scope of api - for legacy applications
     global.P.restore = function() {
        throw "Legacy api can't restore the global namespace.";
     };
     */
})();

load("classpath:forms/BorderPane.js");
//...

P.require = function(deps, aOnSuccess, aOnFailure) {
    try {
        if (deps) {
            if (Array.isArray(deps)) {
                for (var i = 0; i < deps.length; i++) {
                    com.eas.client.scripts.ScriptRunner.executeResource(deps[i]);
                }
            } else {
                com.eas.client.scripts.ScriptRunner.executeResource(deps);
            }
        }
        if (aOnSuccess) {
            aOnSuccess();
        }
    } catch (e) {
        if (aOnFailure)
            aOnFailure(e);
        else
            throw e;
    }
};
