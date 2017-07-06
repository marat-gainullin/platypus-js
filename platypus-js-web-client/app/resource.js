define(['logger', 'internals'], function (Logger, Utils) {
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
    
    function lookupCallerApplicationJsFile() {
        var calledFromFile = null;
        try {
            throw new Error("Current application file test");
        } catch (ex) {
            var stack = ex.stack.split('\n');
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
        }
        return calledFromFile;
    }

    function lookupCallerApplicationJsDir() {
        var calledFromFile = lookupCallerApplicationJsFile();
        if (calledFromFile) {
            var lastSlashIndex = calledFromFile.lastIndexOf('/');
            return calledFromFile.substring(0, lastSlashIndex);
        } else {
            return null;
        }
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

    function resourceUri(aResourceName){
        if(/https?:\//.test(aResourceName))
            return aResourceName;
        else {
            return relativeUri() + window.platypusjs.config.sourcePath + aResourceName;        
        }
    }

    function load(aResourceName, aBinary, onSuccess, onFailure) {
        var callerDir = lookupCallerApplicationJsDir();
        var uri = resourceUri(aResourceName.startsWith("./") || aResourceName.startsWith("../") ? toFilyAppModuleId(aResourceName, callerDir) : aResourceName);
        if (onSuccess) {
            startRequest(uri, aBinary ? 'arraybuffer' : '', function (aResult) {
                if (200 <= aResult.status && aResult.status < 300) {
                    if (aResult.responseType === 'arraybuffer') {
                        var buffer = aResult.responseArrayBuffer;
                        buffer.length = buffer.byteLength;
                        onSuccess(buffer);
                    } else {
                        onSuccess(aResult.responseText);
                    }
                } else {
                    if (onFailure) {
                        onFailure(aResult.statusText);
                    }
                }
            }, function (aResult) {
                if (onFailure) {
                    try {
                        onFailure(aResult.status ? aResult.statusText : "Request has been cancelled. See browser's console for more details.");
                    } catch (ex) {
                        Logger.severe(ex);
                    }
                }
            });
        } else {
            var executed = syncRequest(uri, '');
            if (executed) {
                if (200 <= executed.status && executed.status < 300) {
                    if (executed.responseType === 'arraybuffer') {
                        var buffer = executed.responseArrayBuffer;
                        buffer.length = buffer.byteLength;
                        return buffer;
                    } else {
                        return executed.responseText;
                    }
                } else {
                    throw executed.statusText;
                }
            }
        }
        return null;
    }

    function upload(aFile, aName, aCompleteCallback, aProgresssCallback, aErrorCallback) {
        if (aFile) {
            var completed = false;
            startUploadRequest(aFile, aName, function (aResult) {
                completed = true;
                if (aCompleteCallback) {
                    aCompleteCallback(JSON.parse(aResult.request.responseText));
                }
            }, function (aResult) {
                try {
                    if (!completed) {
                        if (aProgresssCallback) {
                            aProgresssCallback(aResult);
                        }
                    }
                } catch (ex) {
                    Logger.severe(ex);
                }
            }, function (reason) {
                if (aErrorCallback) {
                    try {
                        aErrorCallback(reason);
                    } catch (ex) {
                        Logger.severe(ex);
                    }
                }
            });
        }
    }

    function startUploadRequest(aFile, aName, onComplete, onProgress, onFailure) {
        var req = new XMLHttpRequest();
        req.open("post", remoteApi() + window.platypusjs.config.apiUri);
        if (req.upload) {
            req.upload.onprogress = function (aProgressEvent) {
                try {
                    if (onProgress) {
                        onProgress(aProgressEvent);
                    }
                } catch (ex) {
                    Logger.severe(ex);
                }
            };

            req.upload.onloadend = function (aProgressEvent) {
                try {
                    if (onProgress) {
                        onProgress(aProgressEvent);
                    }
                } catch (ex) {
                    Logger.severe(ex);
                }
            };

            req.upload.ontimeout = function (aProgressEvent) {
                if (onFailure) {
                    try {
                        onFailure("Upload timed out");
                    } catch (ex) {
                        Logger.severe(ex);
                    }
                }
            };

            req.upload.onabort = function (aEvent) {
                if (onFailure) {
                    try {
                        onFailure("Upload aborted");
                    } catch (ex) {
                        Logger.severe(ex);
                    }
                }
            };

            req.upload.onerror = function (aEvent) {
                if (onFailure) {
                    try {
                        onFailure(req.responseText ? req.responseText : (req.status + ' : ' + req.statusText));
                    } catch (ex) {
                        Logger.severe(ex);
                    }
                }
            };
        }
        var fd = new FormData();
        fd.append(aFile.name, aFile, aName);
        req.overrideMimeType("multipart/form-data");
        // Must set the onreadystatechange handler before calling send().
        req.onreadystatechange = function (xhr) {
            if (req.readyState === 4/*RequestState.DONE*/) {
                req.onreadystatechange = null;
                if (200 <= req.status && req.status < 300) {
                    try {
                        if (onComplete) {
                            onComplete();
                        }
                    } catch (ex) {
                        Logger.severe(ex);
                    }
                } else {
                    if (req.status === 0) {
                        onFailure("Upload aborted");
                    } else {
                        onFailure(req.statusText);
                    }
                }
            }
        };
        req.send(fd);
        return {
            cancel: function () {
                req.onreadystatechange = null;
                req.abort();
            }
        };
    }
    
    var module = {};
    
    Object.defineProperty(module, 'upload', {
        enumerable: true,
        get: function () {
            return upload;
        }
    });
    Object.defineProperty(module, 'load', {
        enumerable: true,
        value: function (aResName, onSuccess, onFailure) {
            return load(aResName, true, onSuccess, onFailure);
        }
    });
    Object.defineProperty(module, 'loadText', {
        enumerable: true,
        value: function (aResName, onSuccess, onFailure) {
            return load(aResName, false, onSuccess, onFailure);
        }
    });
    return module;
});