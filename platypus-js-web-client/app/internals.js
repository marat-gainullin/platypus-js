define(function () {
    if (!window.platypusjs) {
        var config = {};
        (function () {
            var sourcePath = "/";
            var apiUri = "/application";
            Object.defineProperty(config, 'sourcePath', {
                get: function () {
                    return sourcePath;
                },
                set: function (aValue) {
                    if (aValue) {
                        sourcePath = aValue;
                        if (!sourcePath.endsWith("/")) {
                            sourcePath = sourcePath + "/";
                        }
                        if (!sourcePath.startsWith("/")) {
                            sourcePath = "/" + sourcePath;
                        }
                    } else {
                        sourcePath = "/";
                    }
                }
            });
            Object.defineProperty(config, 'apiUri', {
                get: function () {
                    return apiUri;
                },
                set: function (aValue) {
                    if (!aValue.startsWith('/'))
                        aValue = '/' + aValue;
                    if (aValue.endsWith('/'))
                        aValue = aValue.substring(0, aValue.length - 1);
                    apiUri = aValue;
                }
            });
        }());

        window.platypusjs = {config};
        Object.seal(window.platypusjs);
    }

    // TODO: Move lookupApplicationCallerFile all logic, but exception throwing here.

    function dateReviver(k, v) {
        if (typeof v === 'string' && /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z/.test(v)) {
            return new Date(v);
        } else {
            return v;
        }
    }

    function Process(expectedCalls, succeded, failed) {
        var reasons = [];
        var calls = 0;

        function complete() {
            if (++calls === expectedCalls) {
                if (reasons.length === 0) {
                    succeded();
                } else {
                    failed(reasons);
                }
            }
        }

        this.onSuccess = function () {
            complete();
        };

        this.onFailure = function (reason) {
            reasons.push(reason);
            complete();
        };
    }
    
    var module = {};
    Object.defineProperty(module, 'dateReviver', {
        get: function(){
            return dateReviver;
        }
    });
    Object.defineProperty(module, 'Process', {
        get: function(){
            return Process;
        }
    });
    return module;
});