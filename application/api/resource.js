define(['boxing'], function (B) {
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");

    function lookupCallerFile() {
        var calledFromFile = null;
        var error = new Error('path test error');
        if (error.stack) {
            var stack = error.stack.split('\n');
            if (stack.length > 1) {
                var sourceCall = stack[3];
                var stackFrameParsed = /\((.+):\d+\)/.exec(sourceCall);
                if (stackFrameParsed && stackFrameParsed.length > 1) {
                    calledFromFile = stackFrameParsed[1];
                }
            }
        }
        return calledFromFile + '.js';
    }

    var module = {};
    Object.defineProperty(module, "load", {
        enumerable: true,
        value: function (aResName, onSuccess, onFailure) {
            var calledFromFile = lookupCallerFile();
            return B.boxAsJs(ScriptedResourceClass.load(B.boxAsJava(aResName), B.boxAsJava(calledFromFile), B.boxAsJava(onSuccess), B.boxAsJava(onFailure)));
        }
    });
    Object.defineProperty(module, "loadText", {
        enumerable: true,
        value: function (aResName, onSuccess, onFailure) {
            var calledFromFile = lookupCallerFile();
            return B.boxAsJs(ScriptedResourceClass.load(B.boxAsJava(aResName), B.boxAsJava(calledFromFile), B.boxAsJava(onSuccess), B.boxAsJava(onFailure)));
        }
    });
    Object.defineProperty(module, "upload", {
        enumerable: true,
        value: function (aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback) {
            print("upload() is not implemented for J2SE");
        }
    });
    Object.defineProperty(module, "applicationPath", {
        enumerable: true,
        get: function () {
            return ScriptedResourceClass.getApplicationPath();
        }
    });
    return module;
});
