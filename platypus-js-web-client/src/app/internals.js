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

    // TODO: Check if changes are made to this function both in amd.js and here
    function extractFileName(aFrame) {
        if (aFrame) {
            // This is for Chrome stack traces
            var matched = aFrame.match(/(https?:\/\/.+):\d+:\d+/);
            if (matched) {
                return matched[1];
            } else {
                matched = aFrame.match(/(file:\/\/.+):\d+:\d+/);
                if (matched)
                    return matched[1];
                else
                    return null;
            }
        } else {
            return null;
        }
    }
    
    // TODO: Check if changes are made to this function both in amd.js and here
    function lookupCallerApplicationJsFile(aException) {
        var calledFromFile = null;
        var stack = aException.stack.split('\n');
        var firstFileName = extractFileName(stack[1]);// On Chrome the first line is a error text
        if (firstFileName) {
            for (var frameIdx = 1; frameIdx < stack.length; frameIdx++) {
                var fileName = extractFileName(stack[frameIdx]);
                if (fileName && fileName !== firstFileName) {
                    calledFromFile = fileName;
                    var lastQuestionIndex = calledFromFile.lastIndexOf('?');// case of cache busting
                    return lastQuestionIndex !== -1 ? calledFromFile.substring(0, lastQuestionIndex) : calledFromFile;
                }
            }
        }
        return calledFromFile;
    }

    // TODO: Check if changes are made to this function both in amd.js and here
    function lookupCallerApplicationJsDir(calledFromFile) {
        if (calledFromFile) {
            var lastSlashIndex = calledFromFile.lastIndexOf('/');
            return calledFromFile.substring(0, lastSlashIndex);
        } else {
            return null;
        }
    }

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

    function hostPageBaseURL() {
        var s = document.location.href;

        // Pull off any hash.
        var i = s.indexOf('#');
        if (i !== -1)
            s = s.substring(0, i);

        // Pull off any query string.
        i = s.indexOf('?');
        if (i !== -1)
            s = s.substring(0, i);

        // Rip off everything after the last slash.
        i = s.lastIndexOf('/');
        if (i !== -1)
            s = s.substring(0, i);

        // Ensure a final slash if non-empty.
        return s.length > 0 ? s + "/" : "";
    }

    function relativeUri() {
        var pageUrl = hostPageBaseURL();
        pageUrl = pageUrl.substring(0, pageUrl.length - 1);
        return pageUrl;
    }

    function toFilyAppModuleId(aRelative, aStartPoint) {
        var moduleIdNormalizer = document.createElement('div');
        moduleIdNormalizer.innerHTML = "<a href=\"" + aStartPoint + "/" + aRelative + "\">o</a>";
        // TODO: check if decodeURIComponent is applicable instead of decodeURI.
        var mormalizedAbsoluteModuleUrl = decodeURI(moduleIdNormalizer.firstChild.href);
        var hostContextPrefix = relativeUri() + window.platypusjs.config.sourcePath;
        var hostContextNormalizer = document.createElement('div');
        hostContextNormalizer.innerHTML = "<a href=\"" + hostContextPrefix + "\">o</a>";
        var mormalizedHostContextPrefix = decodeURI(hostContextNormalizer.firstChild.href);
        return mormalizedAbsoluteModuleUrl.substring(mormalizedHostContextPrefix.length);
    }

    function resourceUri(aResourceName) {
        if (/https?:\//.test(aResourceName))
            return aResourceName;
        else {
            return relativeUri() + window.platypusjs.config.sourcePath + aResourceName;
        }
    }

    function remoteApi() {
        return window.platypusjs.config.remoteApi ? window.platypusjs.remoteApi : relativeUri();
    }

    var module = {};
    Object.defineProperty(module, 'dateReviver', {
        get: function () {
            return dateReviver;
        }
    });
    Object.defineProperty(module, 'Process', {
        get: function () {
            return Process;
        }
    });
    Object.defineProperty(module, 'lookupCallerApplicationJsFile', {
        get: function () {
            return lookupCallerApplicationJsFile;
        }
    });
    Object.defineProperty(module, 'lookupCallerApplicationJsDir', {
        get: function () {
            return lookupCallerApplicationJsDir;
        }
    });
    Object.defineProperty(module, 'hostPageBaseURL', {
        get: function () {
            return hostPageBaseURL;
        }
    });
    Object.defineProperty(module, 'relativeUri', {
        get: function () {
            return relativeUri;
        }
    });
    Object.defineProperty(module, 'toFilyAppModuleId', {
        get: function () {
            return toFilyAppModuleId;
        }
    });
    Object.defineProperty(module, 'resourceUri', {
        get: function () {
            return resourceUri;
        }
    });
    Object.defineProperty(module, 'remoteApi', {
        get: function () {
            return remoteApi;
        }
    });
    return module;
});