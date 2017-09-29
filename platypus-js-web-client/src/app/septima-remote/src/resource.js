define([
    'core/logger',
    'core/utils/caller'], function (
        Logger,
        Caller) {
    var global = window;
    if (!global.platypusjs) {
        var config = {};
        (function () {
            var sourcePath = '/';
            var apiUri = '/application';
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

        global.platypusjs = {
            config: config
        };
        Object.seal(global.platypusjs);
    }

    function lookupCallerJsFile() {
        try {
            throw new Error("Current application file test");
        } catch (ex) {
            return Caller.lookupJsFile(ex);
        }
    }

    function lookupCallerJsDir() {
        return Caller.lookupDir(lookupCallerJsFile());
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

    function remoteApi() {
        return global.platypusjs.config.remoteApi ? global.platypusjs.remoteApi : relativeUri();
    }

    function relativeUri() {
        var pageUrl = hostPageBaseURL();
        pageUrl = pageUrl.substring(0, pageUrl.length - 1);
        return pageUrl;
    }

    function resourceUri(aResourceName) {
        if (/https?:\//.test(aResourceName))
            return aResourceName;
        else {
            return relativeUri() + global.platypusjs.config.sourcePath + aResourceName;
        }
    }

    function toFilyAppModuleId(relativePath, startPoint) {
        var moduleIdNormalizer = document.createElement('div');
        moduleIdNormalizer.innerHTML = "<a href=\"" + startPoint + "/" + relativePath + "\">o</a>";
        // TODO: check if decodeURIComponent is applicable instead of decodeURI.
        var mormalizedAbsoluteModuleUrl = decodeURI(moduleIdNormalizer.firstChild.href);
        var hostContextPrefix = relativeUri() + global.platypusjs.config.sourcePath;
        var hostContextNormalizer = document.createElement('div');
        hostContextNormalizer.innerHTML = "<a href=\"" + hostContextPrefix + "\">o</a>";
        var mormalizedHostContextPrefix = decodeURI(hostContextNormalizer.firstChild.href);
        var mormalizedRelativeModuleUrl = mormalizedAbsoluteModuleUrl.substring(mormalizedHostContextPrefix.length);
        if (mormalizedRelativeModuleUrl === '')
            throw "Module reference '" + relativePath + "' couldn't be resolved, starting from '" + startPoint + "'";
        return mormalizedRelativeModuleUrl;
    }

    function load(aResourceName, aBinary, onSuccess, onFailure) {
        var url;
        if (aResourceName.startsWith('./') || aResourceName.startsWith('../')) {
            var callerDir = lookupCallerJsDir();
            url = resourceUri(toFilyAppModuleId(aResourceName, callerDir));
        } else {
            url = resourceUri(aResourceName);
        }
        if (onSuccess) {
            return startDownloadRequest(url, aBinary ? 'arraybuffer' : '', function (xhr) {
                if (200 <= xhr.status && xhr.status < 300) {
                    if (xhr.responseType === 'arraybuffer') {
                        var buffer = xhr.response;
                        buffer.length = buffer.byteLength;
                        onSuccess(buffer);
                    } else {
                        onSuccess(xhr.responseText);
                    }
                } else {
                    if (onFailure) {
                        onFailure(xhr.statusText);
                    }
                }
            }, function (aResult) {
                if (onFailure) {
                    try {
                        onFailure(aResult.status ? (aResult.status + ' : ' + aResult.statusText) : "It seems, that request has been cancelled. See browser's console for more details.");
                    } catch (ex) {
                        Logger.severe(ex);
                    }
                }
            });
        } else {
            var executed = syncRequest(url, '');
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

    function upload(aFile, aName, onComplete, onProgresss, onFailure) {
        if (aFile) {
            var completed = false;
            return startUploadRequest(aFile, aName, function (aResult) {
                completed = true;
                if (onComplete) {
                    onComplete(JSON.parse(aResult));
                }
            }, function (aResult) {
                try {
                    if (!completed) {
                        if (onProgresss) {
                            onProgresss(aResult);
                        }
                    }
                } catch (ex) {
                    Logger.severe(ex);
                }
            }, function (reason) {
                if (onFailure) {
                    try {
                        onFailure(reason);
                    } catch (ex) {
                        Logger.severe(ex);
                    }
                }
            });
        }
    }

    function startDownloadRequest(url, responseType, onSuccess, onFailure) {
        var req = new XMLHttpRequest();
        req.open("get", url);
        // Must set the onreadystatechange handler before calling send().
        req.onreadystatechange = function () {
            if (req.readyState === 4/*RequestState.DONE*/) {
                req.onreadystatechange = null;
                if (200 <= req.status && req.status < 300) {
                    if (onSuccess) {
                        onSuccess(req);
                    }
                } else {
                    if (onFailure) {
                        onFailure(req);
                    }
                }
            }
        };
        if (responseType) {
            req.responseType = responseType;
        }
        req.send();
        return {
            cancel: function () {
                req.onreadystatechange = null;
                req.abort();
            }
        };
    }

    function startUploadRequest(aFile, aName, onComplete, onProgress, onFailure) {
        var req = new XMLHttpRequest();
        req.open('post', remoteApi() + global.platypusjs.config.apiUri);
        if (req.upload) {
            req.upload.onprogress = function (aProgressEvent) {
                if (onProgress) {
                    onProgress(aProgressEvent);
                }
            };

            req.upload.onloadend = function (aProgressEvent) {
                if (onProgress) {
                    onProgress(aProgressEvent);
                }
            };

            req.upload.ontimeout = function (aProgressEvent) {
                if (onFailure) {
                    onFailure("Upload timed out");
                }
            };

            req.upload.onabort = function (aEvent) {
                if (onFailure) {
                    onFailure("Upload aborted");
                }
            };

            req.upload.onerror = function (aEvent) {
                if (onFailure) {
                    onFailure(req.responseText ? req.responseText : (req.status + ' : ' + req.statusText));
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
                    if (onComplete) {
                        onComplete(req.responseText);
                    }
                } else {
                    if (req.status === 0) {
                        onFailure("Upload canceled");
                    } else {
                        onFailure(req.responseText ? req.responseText : (req.status + ' : ' + req.statusText));
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
    function Icon() {
    }

    function loadIcon(aResourceName, onSuccess, onFailure) {
        var url;
        if (aResourceName.startsWith('./') || aResourceName.startsWith('../')) {
            var callerDir = lookupCallerJsDir();
            url = resourceUri(toFilyAppModuleId(aResourceName, callerDir));
        } else {
            url = resourceUri(aResourceName);
        }
        var image = document.createElement('img');
        image.onload = function () {
            image.onload = null;
            image.onerror = null;
            onSuccess(image);
        };
        image.onerror = function (e) {
            image.onload = null;
            image.onerror = null;
            if (onFailure)
                onFailure(e);
        };
        image.src = url;
        return url;
    }
    Object.defineProperty(Icon, 'load', {
        get: function () {
            return loadIcon;
        }
    });


    var module = {};

    Object.defineProperty(module, 'Icon', {
        enumerable: true,
        get: function () {
            return Icon;
        }
    });
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
    Object.defineProperty(module, 'toFilyAppModuleId', {
        get: function () {
            return toFilyAppModuleId;
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
    Object.defineProperty(module, 'remoteApi', {
        get: function () {
            return remoteApi;
        }
    });
    return module;
});