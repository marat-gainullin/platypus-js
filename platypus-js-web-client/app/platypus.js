(function () {
    var INJECTED_SCRIPT_CLASS_NAME = "platypus-injected-script";
    var SERVER_MODULE_TOUCHED_NAME = "Proxy-";
    var SERVER_ENTITY_TOUCHED_NAME = "Entity-";
    var MODEL_TAG_NAME = "datamodel";
    var ENTITY_TAG_NAME = "entity";
    var QUERY_ID_ATTR_NAME = "queryId";
    var TYPE_JAVASCRIPT = "text/javascript";

    var APPLICATION_URI = "/application";
    var sourcePath = "/";

    function getSourcePath() {
        return sourcePath;
    }

    function setSourcePath(aValue) {
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

    function remoteApiUri() {
        var metas = document.head.getElementsByTagName("meta");
        for (var i = 0; i < metas.getLength(); i++) {
            var meta = metas.getItem(i);
            if ("platypus-server".equalsIgnoreCase(meta.getAttribute("name"))) {
                return meta.getAttribute("content");
            }
        }
        return relativeUri();
    }

    function relativeUri() {
        var pageUrl = getHostPageBaseURL();
        pageUrl = pageUrl.substring(0, pageUrl.length() - 1);
        return pageUrl;
    }

    var startedModules = new Set();
    var startedScripts = new Set();
    var loadedScripts = new Map();
    var amdDefines = [];
    var pendingsOnModule = new Map();
    var pendingsOnScript = new Map();
    var defined = new Map();
    var simpleModules = false;

    function later(action) {
        var timeout = setTimeout(function () {
            clearTimeout(timeout);
            action();
        }, 0);
    }

    function severe(message) {
        if (console)
            console.log("SEVERE - " + message);
    }

    function info(message) {
        if (console)
            console.log("INFO - " + message);
    }

    function fireStarted(anItemName) {
        info("Loading... " + anItemName);
    }

    function fireLoaded(anItemName) {
        info(anItemName + " - Loaded");
    }

    function LoadProcess(expected, onSuccess, onFailure) {
        var calls = 0;
        var loaded = new Set();
        var failures = new Set();

        this.complete = function (aLoaded, aFailure) {
            if (aLoaded) {
                loaded.add(aLoaded);
            }
            if (aFailure) {
                failures.add(aFailure);
            }
            if (++calls === expected) {
                if (failures.size === 0) {
                    onSuccess();
                } else {
                    onFailure();
                }
            }
        };
    }

    function Process(expectedCalls, succeded, failed) {
        var reasons = [];
        var singleResult;
        var calls = 0;

        function complete() {
            if (++calls === expectedCalls) {
                if (reasons.isEmpty()) {
                    succeded(singleResult);
                } else {
                    failed(reasons);
                }
            }
        }

        this.onSuccess = function (result) {
            if (result) {
                if (!singleResult) {
                    singleResult = result;
                } else {
                    throw new IllegalStateException("CumulativeCallbackAdapter supports only one result");
                }
            }
            complete();
        };

        this.onFailure = function (reason) {
            reasons.add(reason);
            complete();
        };
    }

    function AmdDefine(moduleName, dependencies, moduleDefiner) {
        Object.defineProperty('moduleName', {
            get: function () {
                return moduleName;
            }
        });

        Object.defineProperty('dependencies', {
            get: function () {
                return dependencies;
            }
        });

        Object.defineProperty('moduleDefiner', {
            get: function () {
                return moduleDefiner;
            }
        });
    }

    function addAmdDefine(aModuleName, aDependencies, aModuleDefiner) {
        amdDefines.add(new AmdDefine(aModuleName, aDependencies, aModuleDefiner));
    }

    function consumeAmdDefines() {
        var res = amdDefines;
        amdDefines = [];
        return res;
    }

    function scriptOfModuleLoaded(aScriptName, aModuleName) {
        var amdOrderedModules = loadedScripts.get(aScriptName);
        if (!amdOrderedModules.has(aModuleName)) {
            // It seems, that module is either global module or it is a file stub
            defined.set(aModuleName, null);
            fireLoaded(aModuleName);
            notifyModuleLoaded(aModuleName);
        }
    }

    function loadModuleFromServer(aModuleName, /*Set*/aCyclic) {
        requestModuleStructure(aModuleName, function (aStructure) {
            if (aStructure.structure.size === 0)
                throw "Module [" + aModuleName + "] structure should contain at least one element.";
            var jsPart = null;
            var prefetchedResources = new Set();
            aStructure.structure.forEach(function (part) {
                if (part.toLowerCase().endsWith(".js")) {
                    jsPart = part;
                } else {
                    prefetchedResources.add(part);
                }
            });
            if (!jsPart)
                throw "Module [" + aModuleName + "] structure should contain a *.js file.";
            var jsResource = jsPart;
            if (loadedScripts.has(jsResource)) {
                scriptOfModuleLoaded(jsResource, aModuleName);
            } else {
                pendOnScript(jsResource, function () {
                    scriptOfModuleLoaded(jsResource, aModuleName);
                }, function (aReason) {
                    notifyModuleFailed(aModuleName, [aReason]);
                });
                if (!startedScripts.contains(jsResource)) {
                    loadScriptFormServer(prefetchedResources, jsResource, aStructure.clientDependencies, aStructure.queriesDependencies, aStructure.serverDependencies, aCyclic);
                    startedScripts.add(jsResource);
                }
            }
        }, function (reason) {
            notifyModuleFailed(aModuleName, [reason.status + ": " + reason.statusText]);
        });
    }
    ;


    function loadScriptFormServer(/*Set*/aPrefetchedResources, aJsResource, /*Set*/ aClientGlobalDependencies, /*Set*/ aQueriesDependencies,
            /*Set*/ aServerModulesDependencies, /*Set*/aCyclic) {
        var scriptProcess = new Process(aPrefetchedResources.size() === 0 ? 1 : 2, function () {
            var jsURL = checkedCacheBust(relativeUri() + sourcePath + aJsResource);
            var scriptElement = document.createElement('script');
            scriptElement.type = TYPE_JAVASCRIPT;
            scriptElement.src = jsURL;
            scriptElement.className = INJECTED_SCRIPT_CLASS_NAME;
            scriptElement.onloaded = function () {
                try {
                    var amdDefines = consumeAmdDefines();
                    var amdModulesOfScript = new Set();
                    amdDefines.forEach(function (amdDefine) {
                        amdModulesOfScript.add(amdDefine.moduleName);
                    });
                    loadedScripts.set(aJsResource, amdModulesOfScript);
                    notifyScriptLoaded(aJsResource);
                    // Amd in action ...
                    amdDefines.forEach(function (amdDefine) {
                        var amdModuleName = amdDefine.moduleName;
                        var amdDependencies = amdDefine.dependencies;
                        var amdModuleDefiner = amdDefine.moduleDefiner;
                        try {
                            load(amdDependencies, {
                                'onSuccess': function () {
                                    amdModuleDefiner(amdModuleName);
                                    // If module is still not defined because of buggy definer in script,
                                    // we have to put it definition as null by hand.
                                    if (!defined.has(amdModuleName)) {
                                        defined.set(amdModuleName, null);
                                    }
                                    fireLoaded(amdModuleName);
                                    notifyModuleLoaded(amdModuleName);
                                },
                                'onFailure': function (aReason) {
                                    notifyModuleFailed(amdModuleName, [aReason]);
                                }
                            }, new Set());
                        } catch (ex) {
                            severe(ex);
                        }
                    });
                } finally {
                    scriptElement.removeFromParent();
                }
            };
            scriptElement.onerror = scriptElement.onabort = function (reason) {
                try {
                    notifyScriptFailed(aJsResource, [reason]);
                    severe("Script [" + aJsResource + "] is not loaded. Cause is: " + reason);
                } finally {
                    scriptElement.removeFromParent();
                }
            };
            document.body.appendChild(scriptElement);
        }, function (aReasons) {
            notifyScriptFailed(aJsResource, aReasons);
        });
        var prefetchProcess = new Process(aPrefetchedResources.size(), function () {
            scriptProcess.onSuccess();
        }, function (aReasons) {
            scriptProcess.onFailure(aReasons);
        });

        aPrefetchedResources.forEach(function (prefetched) {
            requestDocument(prefetched, function (/*Document*/aResult) {
                prefetchProcess.onSuccess(aResult);
            }, function (reason) {
                prefetchProcess.onFailure(reason.status + " : " + reason.statusText);
            });
        });
        var dependenciesProcess = new Process(3, function () {
            scriptProcess.onSuccess();
        }, function (aReasons) {
            scriptProcess.onFailure(aReasons);
        });
        load(aClientGlobalDependencies, dependenciesProcess, aCyclic);
        loadQueries(aQueriesDependencies, dependenciesProcess);
        loadServerModules(aServerModulesDependencies, dependenciesProcess);
    }

    function notifyScriptFailed(aScriptName, aReasons) {
        var interestedPendings = pendingsOnScript.get(aScriptName);
        pendingsOnScript.delete(aScriptName);
        if (interestedPendings) {
            interestedPendings.forEach(function (interestedPending) {
                later(function () {
                    interestedPending.onFailure(aReasons);
                });
            });
            interestedPendings.clear();
        }
    }

    function notifyScriptLoaded(aScriptName) {
        var interestedPendings = pendingsOnScript.get(aScriptName);
        pendingsOnScript.delete(aScriptName);
        if (interestedPendings) {
            interestedPendings.forEach(function (interestedPending) {
                later(function () {
                    interestedPending.onSuccess();
                });
            });
            interestedPendings.clear();
        }
    }

    function pendOnScript(aScriptName, aPending) {
        var pendingOnScript = pendingsOnScript.get(aScriptName);
        if (!pendingOnScript) {
            pendingOnScript = [];
            pendingsOnScript.put(aScriptName, pendingOnScript);
        }
        pendingOnScript.push(aPending);
    }

    function notifyModuleFailed(aModuleName, /*Array*/aReasons) {
        var interestedPendings = pendingsOnModule.get(aModuleName);
        pendingsOnModule.delete(aModuleName);
        if (interestedPendings) {
            interestedPendings.forEach(function (interestedPending) {
                later(function () {
                    interestedPending.onFailure(aReasons);
                });
            });
            interestedPendings.clear();
        }
    }

    function notifyModuleLoaded(aModuleName) {
        var interestedPendings = pendingsOnModule.get(aModuleName);
        pendingsOnModule.delete(aModuleName);
        if (interestedPendings) {
            interestedPendings.forEach(function (interestedPending) {
                later(function () {
                    interestedPending.onSuccess();
                });
            });
            interestedPendings.clear();
        }
    }

    function pendOnModule(aModuleName, aPending) {
        var pendingOnModule = pendingsOnModule.get(aModuleName);
        if (!pendingOnModule) {
            pendingOnModule = [];
            pendingsOnModule.set(aModuleName, pendingOnModule);
        }
        pendingOnModule.push(aPending);
    }

    function lookupInGlobal(aModuleName) {
        return window[aModuleName];
    }

    function load(aModulesNames, parentProcess, aCyclic) {
        var modulesNames = aModulesNames ? new Set(aModulesNames) : null;
        if (modulesNames && modulesNames.size > 0) {
            var process = new Process(modulesNames.length, function () {
                parentProcess.onSuccess();
            }, function (aReasons) {
                parentProcess.onFailure();
            });
            modulesNames.forEach(function (moduleName) {
                if (defined.has(moduleName)) {
                    later(function () {
                        process.onSuccess();
                    });
                } else if (aCyclic.has(moduleName)) {
                    severe("Cyclic dependency detected: " + moduleName);
                    later(function () {
                        process.onSuccess();
                    });
                } else {
                    aCyclic.add(moduleName);
                    pendOnModule(moduleName, process);
                    if (!startedModules.has(moduleName)) {
                        loadModuleFromServer(moduleName, aCyclic);
                        startedModules.add(moduleName);
                        fireStarted(moduleName);
                    }
                }
            });
        } else {
            later(function () {
                parentProcess.onSuccess();
            });
        }
    }


    function loadServerModules(aServerModulesNames, parentProcess) {
        if (aServerModulesNames.length > 0) {
            var process = new Process(aServerModulesNames.size(), function () {
                parentProcess.onSuccess();
            }, function (aReasons) {
                parentProcess.onFailure(aReasons);
            });
            var startLoadings = [];
            aServerModulesNames.forEach(function (appElementName) {
                startLoadings.push(requestServerModule(appElementName, function (aDoc) {
                    fireLoaded(SERVER_MODULE_TOUCHED_NAME + appElementName);
                    process.onSuccess(aDoc);
                }, function (reason) {
                    severe(reason);
                    process.onFailure(reason);
                }));
                fireStarted(SERVER_MODULE_TOUCHED_NAME + appElementName);
            });
        } else {
            later(function () {
                parentProcess.onSuccess();
            });
        }
    }

    function loadQueries(aQueriesNames, parentProcess) {
        if (aQueriesNames.length > 0) {
            var process = new Process(aQueriesNames.size(), function () {
                parentProcess.onSuccess();
            }, function (aReasons) {
                parentProcess.onFailure(aReasons);
            });
            var startLoadings = [];
            aQueriesNames.forEach(function (queryName) {
                startLoadings.add(requestAppQuery(queryName, function (aQuery) {
                    fireLoaded(SERVER_ENTITY_TOUCHED_NAME + queryName);
                    process.onSuccess();
                }, function (reason) {
                    severe(reason);
                    process.onFailure(reason);
                }));
                fireStarted(SERVER_ENTITY_TOUCHED_NAME + queryName);
            });
        } else {
            later(function () {
                parentProcess.onSuccess();
            });
        }
    }

    function jsLoadQueries(aQueriesNames, onSuccess, onFailure) {
        loadQueries(aQueriesNames, {
            'onSuccess': function () {
                if (onSuccess)
                    onSuccess();
            },
            'onFailure': function (reasons) {
                if (onFailure)
                    onFailure(reasons);
            }
        });
    }

    function jsLoadServerModules(aModulesNames, onSuccess, onFailure) {
        loadServerModules(aModulesNames, {
            'onSuccess': function () {
                if (onSuccess)
                    onSuccess();
            },
            'onFailure': function (reasons) {
                if (onFailure)
                    onFailure(reasons);
            }
        });
    }

    function isJsonResponse(aResponse) {
        var responseType = aResponse.getResponseHeader("content-type");
        if (responseType) {
            responseType = responseType.toLowerCase();
            return  responseType.indexOf("application/json") > -1 ||
                    responseType.indexOf("application/javascript") > -1 ||
                    responseType.indexOf("text/json") > -1 ||
                    responseType.indexOf("text/javascript") > -1;
        } else {
            return false;
        }
    }

    // TODO: Check how localmodules index works
    var modulesIndex = new Map();
    function checkModulesLocalIndex() {
        if (modulesIndex.size > 0) {
            for (var [fileName, structure] of modulesIndex) {
                var defaultModuleName = fileName;
                if (defaultModuleName.endsWith('.js')) {
                    defaultModuleName = defaultModuleName.substring(0, defaultModuleName.length - 3);
                }
                modulesStructures.set(defaultModuleName, structure);
                if (structure.modules) {
                    for (var i = 0; i < structure.modules.length; i++) {
                        modulesStructures.set(structure.modules[i], structure);
                    }
                    delete structure.modules;
                }
            }
            modulesIndex = new Map();
        }
    }

    var modulesStructures = new Map();

    function requestModuleStructure(aModuleName, onSuccess, onFailure) {
        checkModulesLocalIndex();
        if (modulesStructures.has(aModuleName)) {
            if (onSuccess) {
                later(function () {
                    onSuccess(modulesStructures.get(aModuleName));
                });
            }
            return {
                cancel: function () {
                    // no op here because of no request
                }
            };
        } else if (simpleModules) {
            if (onSuccess) {
                later(function () {
                    var fakeRelativeFileName = aModuleName;
                    if (!fakeRelativeFileName.toLowerCase().endsWith(".js")) {
                        fakeRelativeFileName = fakeRelativeFileName + ".js";
                    }
                    onSuccess({
                        structure: [fakeRelativeFileName]
                    });
                });
            }
            return {
                cancel: function () {
                    // no op here because of no request
                }
            };
        } else {
            var query = params(param(PlatypusRequestParams.TYPE, Requests.rqModuleStructure + ''), param(PlatypusRequestParams.MODULE_NAME, aModuleName));
            return startApiRequest(null, query, "", Methods.GET, null, function (aResponse) {
                if (isJsonResponse(aResponse)) {
                    // Some post processing
                    var structure = aResponse.responseJSON;
                    modulesStructures.set(aModuleName, structure);
                    if (onSuccess) {
                        onSuccess(structure);
                    }
                } else {
                    if (onFailure) {
                        onFailure(aResponse);
                    }
                }
            }, function (reason) {
                if (onFailure) {
                    onFailure(reason);
                }
            });
        }
    }

    var Requests = {
        rqCredential: 5,
        rqModuleStructure: 19,
        rqResource: 20,
        rqAppQuery: 6,
        rqExecuteQuery: 7,
        rqCommit: 8,
        rqCreateServerModule: 12,
        rqDisposeServerModule: 13,
        rqExecuteServerModuleMethod: 14,
        rqLogout: 18
    };

    var PlatypusRequestParams = {
        CACHE_BUSTER: "__cb",
        QUERY_ID: "__queryId",
        TYPE: "__type",
        //ENTITY_ID: "__entityId",
        MODULE_NAME: "__moduleName",
        METHOD_NAME: "__methodName",
        //APP_ELEMENT_NAME: "__appElementName",
        PARAMS_ARRAY: "__param[]"
    };

    var Methods = {
        GET: 'GET',
        PUT: 'PUT',
        POST: 'POST',
        HEAD: 'HEAD',
        DELETE: 'DELETE'
    };

    var RequestState = {
        UNSENT: 0,
        OPENED: 1,
        HEADERS_RECEIVED: 2,
        LOADING: 3,
        DONE: 4
    };

    function param(aName, aValue) {
        return aName + "=" + (aValue ? encodeURIComponent(aValue) : "");
    }

    function params() {
        var res = "";
        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i]) {
                if (res.length > 0) {
                    res += "&";
                }
                res += arguments[i];
            }
        }
        return res;
    }

    var serverModules = new Map();

    function requestServerModule(aModuleName, onSuccess, onFailure) {
        if (serverModules.has(aModuleName)) {
            if (onSuccess) {
                later(function () {
                    onSuccess();
                });
            }
            return {
                cancel: function () {
                    // no op here because of no request have been sent
                }
            };
        } else {
            var query = params(param(PlatypusRequestParams.TYPE, Requests.rqCreateServerModule + ''), param(PlatypusRequestParams.MODULE_NAME, aModuleName));
            return startApiRequest(null, query, "", Methods.GET, null, function (aResponse) {
                // Some post processing
                if (isJsonResponse(aResponse)) {
                    addServerModule(aModuleName, aResponse.responseText);
                    if (onSuccess) {
                        onSuccess();
                    }
                } else {
                    if (onFailure)
                        onFailure(aResponse);
                }
            }, function (aResponse) {
                if (onFailure) {
                    var responseText = aResponse.responseText;
                    onFailure(responseText ? responseText : aResponse.statusText);
                }
            });
        }
    }

    function getServerModule(aModuleName) {
        return serverModules.get(aModuleName);
    }

    function addServerModule(aModuleName, aStructure) {
        serverModules.set(aModuleName, JSON.parse(aStructure));
    }

    function isServerModule(aModuleName) {
        return serverModules.has(aModuleName);
    }

    function startApiRequest(aUrlPrefix, aUrlQuery, aBody, aMethod, aContentType, onSuccess, onFailure) {
        var url = remoteApiUri() + APPLICATION_URI + (aUrlPrefix ? aUrlPrefix : "") + (aUrlQuery ? "?" + aUrlQuery : "");
        var req = new XMLHttpRequest();
        req.open(aMethod, url);
        if (aContentType) {
            req.setRequestHeader("Content-Type", aContentType);
        }
        req.setRequestHeader("Pragma", "no-cache");
        return startRequest(req, aBody, onSuccess, onFailure);
    }

    function startRequest(xhr, aBody, onSuccess, onFailure) {
        // Must set the onreadystatechange handler before calling send().
        xhr.onreadystatechange = function () {
            if (xhr.readyState === RequestState.DONE) {
                xhr.onreadystatechange = null;
                try {
                    if (xhr.status >= 200 || xhr.status < 300) {
                        if (onSuccess) {
                            onSuccess(xhr);
                        }
                    } else {
                        if (onFailure) {
                            onFailure(xhr);
                        }
                    }
                } catch (ex) {
                    severe(ex);
                }
            }
        };
        if (aBody) {
            xhr.send(aBody);
        } else {
            xhr.send();
        }
        return {
            cancel: function () {
                xhr.onreadystatechange = null;
                xhr.abort();
            }
        };
    }

    function startUrlRequest(aUrl, aResponseType, onSuccess, onFailure) {
        var req = new XMLHttpRequest();
        req.open(Methods.GET, aUrl);
        if (aResponseType) {
            req.setResponseType(aResponseType);
        }
        req.setRequestHeader("Pragma", "no-cache");
        return startRequest(req, null, onSuccess, onFailure);
    }

    var cacheBustEnabled = false;

    function checkedCacheBust(aUrl) {
        return cacheBustEnabled ? aUrl + "?" + PlatypusRequestParams.CACHE_BUSTER + "=" + new Date().valueOf() : aUrl;
    }

    var documents = new Map();

    function requestDocument(aResourceName, onSuccess, onFailure) {
        if (documents.has(aResourceName)) {
            var doc = documents.get(aResourceName);
            // doc may be null, because of application elements without a
            // xml-dom, plain scripts for example.
            if (onSuccess) {
                later(function () {
                    onSuccess(doc);
                });
            }
            return {
                cancel: function () {
                    // no op here because of no request have been sent
                }
            };
        } else {
            var documentUrl = checkedCacheBust(relativeUri() + getSourcePath() + aResourceName);
            return startUrlRequest(documentUrl, function (aResponse) {
                var doc = aResponse.responseXML;
                documents.set(aResourceName, doc);
                if (onSuccess) {
                    onSuccess(doc);
                }
            }, function (reason) {
                if (onFailure) {
                    onFailure(reason);
                }
            });
        }
    }

    var queries = new Map();// Map of plain query structure data, not queries by thierselfs.

    function requestAppQuery(queryName, onSuccess, onFailure) {
        var alreadyQuery = queries.get(queryName);
        if (alreadyQuery) {
            if (onSuccess) {
                later(function () {
                    onSuccess(alreadyQuery);
                });
            }
            return {
                cancel: function () {
                    // no op here because of no request have been sent
                }
            };
        } else {
            var urlQuery = params(param(PlatypusRequestParams.TYPE, Requests.rqAppQuery + ''), param(PlatypusRequestParams.QUERY_ID, queryName));
            return startApiRequest(null, urlQuery, "", Methods.GET, null, function (aResponse) {
                if (isJsonResponse(aResponse)) {
                    queries.set(queryName, aResponse.responseJSON);
                    if (onSuccess) {
                        onSuccess();
                    }
                } else {
                    if (onFailure) {
                        onFailure(aResponse.responseText);
                    }
                }
            }, function (aResponse) {
                if (onFailure) {
                    onFailure(aResponse.getStatusText());
                }
            });
        }
    }
}());