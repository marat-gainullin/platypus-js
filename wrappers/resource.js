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

    var module = {
        /**
         * Absolute path to the project's 'app' directory
         */
        applicationPath:"",
        /**
         * Loads the specified resource from project or from the specified http url.
         * Attempts to auto discover type of the resource and passes to the success callback
         * data of the appropriate type. In general, it will be 'String' or 'ArrayBuffer'.
         * If resource name starts with 'http://' prefix, than this name used as a plain http url.
         * @param {String} aResName Resource name. Resource names are considered as path to the resource from project's 'app' directory.
         * It may be relative name (./, ../)
         * @param {Function} onSuccess Success callback consuming loaded result.
         * @param {Function} onFailure Failure callback, called if problem occur while loading.
         * @returns {undefined}
         */
        load: function(aResName, onSuccess, onFailure){},
        /**
         * Loads the specified resource from project or from the specified http url.
         * Allways considers 'text' type of the resource and passes text from server response to the success callback.
         * If resource name starts with 'http://' prefix, than this name used as a plain http url.
         * If http url was used and server would not send us 'content-type' header, 'utf-8' is used as text encoding.
         * @param {String} aResName Resource name. Resource names are considered as path to the resource from project's 'app' directory.
         * It may be relative name (./, ../)
         * @param {Function} onSuccess Success callback consuming loaded result.
         * @param {Function} onFailure Failure callback, called if problem occur while loading.
         * @returns {undefined}
         */
        loadText: function(aResName, onSuccess, onFailure){}
    };
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
