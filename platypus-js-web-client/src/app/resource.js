define(['./logger', './internals'], function (Logger, Utils) {

    function lookupCallerApplicationJsFile() {
        try {
            throw new Error("Current application file test");
        } catch (ex) {
            return Utils.lookupCallerApplicationJsFile(ex);
        }
    }

    function lookupCallerApplicationJsDir() {
        return Utils.lookupCallerApplicationJsDir(lookupCallerApplicationJsFile());
    }

    function load(aResourceName, aBinary, onSuccess, onFailure) {
        var url;
        if (aResourceName.startsWith("./") || aResourceName.startsWith("../")) {
            var callerDir = lookupCallerApplicationJsDir();
            url = Utils.resourceUri(Utils.toFilyAppModuleId(aResourceName, callerDir));
        } else {
            url = Utils.resourceUri(aResourceName);
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
                try {
                    if (200 <= req.status && req.status < 300) {
                        if (onSuccess) {
                            onSuccess(req);
                        }
                    } else {
                        if (onFailure) {
                            onFailure(req);
                        }
                    }
                } catch (ex) {
                    severe(ex);
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
        req.open("post", Utils.remoteApi() + window.platypusjs.config.apiUri);
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
                            onComplete(req.responseText);
                        }
                    } catch (ex) {
                        Logger.severe(ex);
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