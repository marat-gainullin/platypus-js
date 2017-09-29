(function () {
    var global = window;

    var INJECTED_SCRIPT_CLASS_NAME = 'platypus-injected-script';
    var MODULES_INDEX = 'modules-index'; // Map module name -> script file with this module
    var TYPE_JAVASCRIPT = 'text/javascript';

    var config = {autofetch: false, cacheBust: false};
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
                    if (!sourcePath.endsWith('/')) {
                        sourcePath = sourcePath + '/';
                    }
                    if (!sourcePath.startsWith('/')) {
                        sourcePath = '/' + sourcePath;
                    }
                } else {
                    sourcePath = '/';
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
        return s.length > 0 ? s + '/' : '';
    }

    function remoteApi() {
        return config.remoteApi ? config.remoteApi : relativeUri();
    }

    function relativeUri() {
        var pageUrl = hostPageBaseURL();
        pageUrl = pageUrl.substring(0, pageUrl.length - 1);
        return pageUrl;
    }

    var startedModules = new Set();
    var startedScripts = new Set();
    var loadedScripts = new Map();
    var amdDefines = [];
    var pendingsOnModule = new Map();
    var pendingsOnScript = new Map();
    var defined = new Map();

    function later(action) {
        var timeout = setTimeout(function () {
            clearTimeout(timeout);
            action();
        }, 0);
    }

    function severe(message) {
        if (console){
            console.error('SEVERE ' + message);
        }
    }

    function info(message) {
        if (console){
            console.info('INFO ' + message);
        }
    }

    function warning(message) {
        if (console){
            console.warn('WARNING ' + message);
        }
    }

    function fireStarted(anItemName) {
        info('Loading... ' + anItemName);
    }

    function fireLoaded(anItemName) {
        info(anItemName + ' - Loaded');
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

    function AmdDefine(moduleName, dependencies, moduleDefiner) {
        Object.defineProperty(this, 'moduleName', {
            get: function () {
                return moduleName;
            }
        });

        Object.defineProperty(this, 'dependencies', {
            get: function () {
                return dependencies;
            }
        });

        Object.defineProperty(this, 'moduleDefiner', {
            get: function () {
                return moduleDefiner;
            }
        });
    }

    function addAmdDefine(aModuleName, aDependencies, aModuleDefiner) {
        amdDefines.push(new AmdDefine(aModuleName, aDependencies, aModuleDefiner));
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

    function startLoadModule(aModuleName, /*Set*/aCyclic) {
        requestModuleStructure(aModuleName, function (aStructure) {
            if (aStructure.structure.size === 0)
                throw 'Module [' + aModuleName + '] structure should contain at least one element.';
            var jsPart = null;
            var fetchedResources = new Set();
            aStructure.structure.forEach(function (part) {
                if (part.toLowerCase().endsWith('.js')) {
                    jsPart = part;
                } else {
                    fetchedResources.add(part);
                }
            });
            if (!jsPart)
                throw 'Module [' + aModuleName + '] structure should contain a *.js file.';
            var jsResource = jsPart;
            if (loadedScripts.has(jsResource)) {
                scriptOfModuleLoaded(jsResource, aModuleName);
            } else {
                pendOnScript(jsResource, function () {
                    scriptOfModuleLoaded(jsResource, aModuleName);
                }, function (aReason) {
                    notifyModuleFailed(aModuleName, [aReason]);
                });
                if (!startedScripts.has(jsResource)) {
                    startLoadScript(fetchedResources, jsResource, aStructure.clientDependencies ? aStructure.clientDependencies : [], aCyclic);
                    startedScripts.add(jsResource);
                }
            }
        }, function (reason) {
            notifyModuleFailed(aModuleName, [reason.status + ": " + reason.statusText]);
        });
    }

    function inject(jsUrl, onSuccess, onFailure) {
        var scriptElement = document.createElement('script');
        scriptElement.type = TYPE_JAVASCRIPT;
        scriptElement.src = jsUrl;
        scriptElement.className = INJECTED_SCRIPT_CLASS_NAME;
        scriptElement.onload = function () {
            scriptElement.parentElement.removeChild(scriptElement);
            onSuccess();
        };
        scriptElement.onerror = scriptElement.onabort = function (/*ErrorEvent reason*/) {
            scriptElement.parentElement.removeChild(scriptElement);
            onFailure(jsUrl + ' has failed to load.'); // Error events are hard to dereference. Detail error code is quite useless.
        };
        if (document.body)
            document.body.appendChild(scriptElement);
        else
            document.head.appendChild(scriptElement);
    }

    function startLoadScript(/*Set*/aFetchedResources, aJsResource, /*Set*/ aGlobalDependencies, /*Set*/aCyclic) {
        var scriptProcess = new Process(aFetchedResources.size === 0 ? 1 : 2, function () {
            var jsUrl = checkedCacheBust(relativeUri() + config.sourcePath + aJsResource);
            inject(jsUrl, function () {
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
                    load(amdDependencies, new Set(), function () {
                        amdModuleDefiner(amdModuleName);
                        // If module is still not defined because of buggy definer in script,
                        // we have to put it definition as null by hand.
                        if (!defined.has(amdModuleName)) {
                            defined.set(amdModuleName, null);
                        }
                        fireLoaded(amdModuleName);
                        notifyModuleLoaded(amdModuleName);
                    }, function (aReason) {
                        notifyModuleFailed(amdModuleName, [aReason]);
                    });
                });
            }, function (reason) {
                notifyScriptFailed(aJsResource, [reason]);
                severe('Script [' + aJsResource + '] is not loaded. Cause is:\n' + reason);
            });
        }, function (aReasons) {
            notifyScriptFailed(aJsResource, aReasons);
        });
        var fetchProcess = new Process(aFetchedResources.size, function () {
            scriptProcess.onSuccess();
        }, function (aReasons) {
            scriptProcess.onFailure(aReasons);
        });

        aFetchedResources.forEach(function (fetched) {
            requestDocument(fetched, function (/*Document*/aResult) {
                fetchProcess.onSuccess(aResult);
            }, function (reason) {
                fetchProcess.onFailure(reason.status + ' : ' + reason.statusText);
            });
        });
        load(aGlobalDependencies, aCyclic, function () {
            scriptProcess.onSuccess();
        }, function (aReasons) {
            scriptProcess.onFailure(aReasons);
        });
    }

    function pendOnScript(aScriptName, onSuccess, onFailure) {
        var pendingOnScript = pendingsOnScript.get(aScriptName);
        if (!pendingOnScript) {
            pendingOnScript = [];
            pendingsOnScript.set(aScriptName, pendingOnScript);
        }
        pendingOnScript.push({
            onSuccess: onSuccess,
            onFailure: onFailure
        });
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
        }
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

    function notifyModuleLoaded(aModuleName) {
        var interestedPendings = pendingsOnModule.get(aModuleName);
        pendingsOnModule.delete(aModuleName);
        if (interestedPendings) {
            interestedPendings.forEach(function (interestedPending) {
                later(function () {
                    interestedPending.onSuccess();
                });
            });
        }
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
        }
    }

    function lookupInGlobal(aModuleName) {
        return global[aModuleName];
    }

    function load(aModulesNames, aCyclic, onSuccess, onFailure) {
        var modulesNames = aModulesNames ? new Set(aModulesNames) : null;
        if (modulesNames && modulesNames.size > 0) {
            var process = new Process(modulesNames.size, function () {
                onSuccess();
            }, function (aReasons) {
                onFailure(aReasons);
            });
            modulesNames.forEach(function (moduleName) {
                if (defined.has(moduleName)) {
                    later(function () {
                        process.onSuccess();
                    });
                } else if (aCyclic.has(moduleName)) {
                    warning('Cyclic dependency detected: ' + moduleName);
                    later(function () {
                        process.onSuccess();
                    });
                } else {
                    aCyclic.add(moduleName);
                    pendOnModule(moduleName, process);
                    if (!startedModules.has(moduleName)) {
                        startLoadModule(moduleName, aCyclic);
                        startedModules.add(moduleName);
                        fireStarted(moduleName);
                    }
                }
            });
        } else {
            later(function () {
                onSuccess();
            });
        }
    }

    function isJsonResponse(aResponse) {
        var responseType = aResponse.getResponseHeader('content-type');
        if (responseType) {
            responseType = responseType.toLowerCase();
            return  responseType.indexOf('application/json') > -1 ||
                    responseType.indexOf('application/javascript') > -1 ||
                    responseType.indexOf('text/json') > -1 ||
                    responseType.indexOf('text/javascript') > -1;
        } else {
            return false;
        }
    }

    var modulesStructures = new Map();

    function getModelDocument(aModuleName) {
        return getDocumentByModule(aModuleName, '.model');
    }

    function getFormDocument(aModuleName) {
        return getDocumentByModule(aModuleName, '.layout');
    }

    function getDocumentByModule(aModuleName, aSuffix) {
        var doc = null;
        var structure = modulesStructures.get(aModuleName);
        if (structure) {
            structure.structure.forEach(function (part) {
                if (part.toLowerCase().endsWith(aSuffix)) {
                    doc = documents.get(part);
                }
            });
        }
        return doc;
    }

    function requestModuleStructure(aModuleName, onSuccess, onFailure) {
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
        } else if (!config.autofetch) {
            if (onSuccess) {
                later(function () {
                    var fakeRelativeFileName = aModuleName;
                    if (!fakeRelativeFileName.toLowerCase().endsWith('.js')) {
                        fakeRelativeFileName = fakeRelativeFileName + '.js';
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
            var query = params(param('__type'/*RequestParams.TYPE*/, '19'/*RequestTypes.rqModuleStructure*/), param('__moduleName'/*RequestParams.MODULE_NAME*/, aModuleName));
            return startApiRequest(null, query, '', 'GET', null, function (xhr) {
                if (isJsonResponse(xhr)) {
                    // Some post processing
                    var structure = JSON.parse(xhr.responseText);
                    modulesStructures.set(aModuleName, structure);
                    if (onSuccess) {
                        onSuccess(structure);
                    }
                } else {
                    if (onFailure) {
                        onFailure(xhr);
                    }
                }
            }, function (xhr) {
                if (onFailure) {
                    onFailure(xhr);
                }
            });
        }
    }

    function param(aName, aValue) {
        return aName + '=' + (aValue ? encodeURIComponent(aValue) : '');
    }

    function params() {
        var res = '';
        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i]) {
                if (res.length > 0) {
                    res += '&';
                }
                res += arguments[i];
            }
        }
        return res;
    }

    function startApiRequest(aUrlPrefix, aUrlQuery, aBody, aMethod, aContentType, onSuccess, onFailure) {
        var url = remoteApi() + config.apiUri + (aUrlPrefix ? aUrlPrefix : '') + (aUrlQuery ? '?' + aUrlQuery : '');
        var req = new XMLHttpRequest();
        req.open(aMethod, url);
        if (aContentType) {
            req.setRequestHeader('Content-Type', aContentType);
        }
        req.setRequestHeader('Pragma', 'no-cache');
        return startRequest(req, aBody, onSuccess, onFailure);
    }

    function startRequest(xhr, aBody, onSuccess, onFailure) {
        // Must set the onreadystatechange handler before calling send().
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4/*RequestState.DONE*/) {
                xhr.onreadystatechange = null;
                if (200 <= xhr.status && xhr.status < 300) {
                    if (onSuccess) {
                        onSuccess(xhr);
                    }
                } else {
                    if (onFailure) {
                        onFailure(xhr);
                    }
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
        req.open('GET', aUrl);
        if (aResponseType) {
            req.responseType = aResponseType;
        }
        req.setRequestHeader('Pragma', 'no-cache');
        return startRequest(req, null, onSuccess, onFailure);
    }

    function checkedCacheBust(aUrl) {
        return config.cacheBust ? aUrl + '?' + '__cb'/*RequestParams.CACHE_BUSTER*/ + '=' + new Date().valueOf() : aUrl;
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
                    // no op here because no request has been sent
                }
            };
        } else {
            var documentUrl = checkedCacheBust(relativeUri() + config.sourcePath + aResourceName);
            return startUrlRequest(documentUrl, 'document', function (xhr) {
                var doc = xhr.responseXML;
                documents.set(aResourceName, doc);
                if (onSuccess) {
                    onSuccess(doc);
                }
            }, function (xhr) {
                if (onFailure) {
                    onFailure(xhr);
                }
            });
        }
    }

    // Polyfill of Function#name on browsers that don't support it (IE):
    if (!(function f() {}).name) {
        Object.defineProperty(global.Function.prototype, 'name', {
            get: function () {
                var name = this.toString().match(/function\s*(\S*)\s*\(/)[1];
                // For better performance only parse once, and then replace the accessor for repeated access.
                Object.defineProperty(this, 'name', {value: name});
                return name;
            }
        });
    }

    // TODO: Check if changes are made to this function both in resource.js and here
    function toFilyAppModuleId(aRelative, aStartPoint) {
        var moduleIdNormalizer = document.createElement('div');
        moduleIdNormalizer.innerHTML = '<a href="' + aStartPoint + '/' + aRelative + '">o</a>';
        // TODO: check if decodeURIComponent is applicable instead of decodeURI.
        var mormalizedAbsoluteModuleUrl = decodeURI(moduleIdNormalizer.firstChild.href);
        var hostContextPrefix = relativeUri() + config.sourcePath;
        var hostContextNormalizer = document.createElement('div');
        hostContextNormalizer.innerHTML = '<a href="' + hostContextPrefix + '">o</a>';
        var mormalizedHostContextPrefix = decodeURI(hostContextNormalizer.firstChild.href);
        var mormalizedRelativeModuleUrl = mormalizedAbsoluteModuleUrl.substring(mormalizedHostContextPrefix.length);
        if (mormalizedRelativeModuleUrl === '')
            throw "Module reference '" + aRelative + "' couldn't be resolved, starting from '" + aStartPoint + "'";
        return mormalizedRelativeModuleUrl;
    }

    function lookupCallerJsDir() {
        var calledFromFile = lookupCallerJsFile();
        if (calledFromFile) {
            var lastSlashIndex = calledFromFile.lastIndexOf('/');
            return calledFromFile.substring(0, lastSlashIndex);
        } else {
            return null;
        }
    }

    function lookupCallerJsFile() {
        var calledFromFile = null;
        try {
            throw new Error('Current application file test');
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

    function lookupCallerJsFile() {
        try {
            throw new Error('Current file test');
        } catch (ex) {
            var stack = ex.stack.split('\n');
            return extractFileName(stack[1]);// On Chrome the first line is a error text
        }
    }

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

    function lookupResolved(deps) {
        var resolved = [];
        for (var d = 0; d < deps.length; d++) {
            var mName = deps[d];
            var m = defined.get(mName);
            resolved.push(m ? m : lookupInGlobal(mName));
        }
        return resolved;
    }

    function require(aDeps, aOnSuccess, aOnFailure) {
        if (!Array.isArray(aDeps))
            aDeps = [aDeps];

        var calledFromDir = lookupCallerJsDir();
        var deps = [];
        for (var i = 0; i < aDeps.length; i++) {
            var dep = aDeps[i];
            if (calledFromDir && dep.startsWith('./') || dep.startsWith('../')) {
                dep = toFilyAppModuleId(dep, calledFromDir);
            }
            if (dep.toLowerCase().endsWith('.js')) {
                dep = dep.substring(0, dep.length - 3);
            }
            deps.push(dep);
        }
        load(deps, new Set(), function () {
            if (aOnSuccess) {
                aOnSuccess.apply(null, lookupResolved(deps));
            } else {
                warning('platypujs.require succeded, but callback is missing. Required modules are: ' + aDeps);
            }
        }, function (reason) {
            if (aOnFailure) {
                aOnFailure(reason);
            } else {
                warning('platypujs.require failed and callback is missing. Required modules are: ' + aDeps);
            }
        });
        var resolved = lookupResolved(deps);
        return resolved.length === 1 ? resolved[0] : resolved;
    }

    function define() {
        if (arguments.length === 1 ||
                arguments.length === 2 || arguments.length === 3) {
            var moduleName = arguments.length === 3 ? arguments[0] : null;
            moduleName = moduleName ? moduleName + '' : null;
            var _deps = arguments.length === 3 ? arguments[1] : arguments.length === 2 ? arguments[0] : [];
            if (!Array.isArray(_deps))
                _deps = [_deps];
            var _moduleDefiner = arguments.length === 3 ? arguments[2] : arguments.length === 2 ? arguments[1] : arguments[0];
            function moduleDefiner() {
                return typeof _moduleDefiner === 'function' ? _moduleDefiner.apply(null, arguments) : _moduleDefiner;
            }
            ;

            var calledFromFile = lookupCallerJsFile();
            var lastSlashIndex = calledFromFile.lastIndexOf('/');
            var calledFromDir = calledFromFile.substring(0, lastSlashIndex);
            var calledFromFileShort = calledFromFile.substring(lastSlashIndex + 1, calledFromFile.length);
            var deps = [];
            for (var i = 0; i < _deps.length; i++) {
                var dep = _deps[i];
                if (calledFromDir && dep.startsWith('./') || dep.startsWith('../')) {
                    dep = toFilyAppModuleId(dep, calledFromDir);
                }
                if (dep.endsWith('.js')) {
                    dep = dep.substring(0, dep.length - 3);
                }
                deps.push(dep);
            }
            if (!moduleName) {
                moduleName = toFilyAppModuleId('./' + calledFromFileShort, calledFromDir);
                if (moduleName.endsWith('.js')) {
                    moduleName = moduleName.substring(0, moduleName.length - 3);
                }
            }
            addAmdDefine(moduleName, deps, function (aModuleName) {
                var resolved = [];
                for (var d = 0; d < deps.length; d++) {
                    var mName = deps[d];
                    var m = defined.get(mName);
                    resolved.push(m ? m : lookupInGlobal(mName));
                }
                resolved.push(aModuleName);
                var module = moduleDefiner.apply(null, resolved);
                defined.set(aModuleName, module);
            });

        } else {
            throw 'platypusjs.define() arguments mismatch';
        }
    }

    var NO_ENTRY_POINT_MSG = '"entry-point" attribute missing. Hope application is initialized in some other way.';
    var APPLICATION_INITIALIZED_MSG = 'Platypus.js application initialized';
    var APPLICATION_INITIALIZATION_ERROR_MSG = 'Error while initializing application.\n';

    function init() {
        define.amd = {}; // AMD compliance

        var platypusjs = {
            require: require,
            define: define,
            config: config,
            documents: documents,
            getModelDocument: getModelDocument,
            getFormDocument: getFormDocument
        };
        Object.seal(platypusjs);

        global.platypusjs = platypusjs;
        global.require = require;
        global.define = define;

        var thisScriptFile = lookupCallerJsFile();
        var scriptTags = document.getElementsByTagName('script');
        var modulesIndexResource = null;
        var entryPoint = null;
        for (var s = 0; s < scriptTags.length; s++) {
            var script = scriptTags[s];
            if (script.src.endsWith(thisScriptFile)) {
                if (script.hasAttribute(MODULES_INDEX)) {
                    modulesIndexResource = script.getAttribute(MODULES_INDEX);
                    if (!modulesIndexResource.toLowerCase().endsWith('.js')) {
                        modulesIndexResource += '.js';
                    }
                }
                if (script.hasAttribute('entry-point')) {
                    entryPoint = script.getAttribute('entry-point');
                    if (!entryPoint.toLowerCase().endsWith('.js')) {
                        entryPoint += '.js';
                    }
                }
                if (script.hasAttribute('remote-api')) {
                    config.remoteApi = script.getAttribute('remote-api'); // urls have case sensitive parts
                }
                if (script.hasAttribute('source-path')) {
                    config.sourcePath = script.getAttribute('source-path').toLowerCase();
                }
                if (script.hasAttribute('api-uri')) {
                    config.apiUri = script.getAttribute('api-uri').toLowerCase();
                }
                config.autofetch = script.hasAttribute('autofetch');
                break;
            }
        }
        if (modulesIndexResource) {
            inject(relativeUri() + config.sourcePath + modulesIndexResource, function () {
                var modulesIndex = define.amd['modules-index'];
                for (var resourceName in modulesIndex) {
                    var resourceIndex = modulesIndex[resourceName];
                    var resourceStructure = {
                        structure: resourceIndex.fetched ? resourceIndex.fetched.slice(0, resourceIndex.fetched.length) : [],
                        clientDependencies: resourceIndex['global-deps'],
                        serverDependencies: resourceIndex['rpc-stubs'],
                        queryDependencies: resourceIndex.entities
                    };
                    resourceStructure.structure.unshift(resourceName);
                    resourceIndex.modules.forEach(function (moduleName) {
                        modulesStructures.set(moduleName, resourceStructure);
                    });
                }
                info('Platypus.js modules index applied');
                if (entryPoint) {
                    inject(relativeUri() + config.sourcePath + entryPoint, function () {
                        info(APPLICATION_INITIALIZED_MSG);
                    }, function (reason) {
                        severe(APPLICATION_INITIALIZATION_ERROR_MSG + reason);
                    });
                } else {
                    warning(NO_ENTRY_POINT_MSG);
                }
            }, function (reason) {
                severe('Error while applying modules index.\n' + reason);
            });
        } else {
            if (entryPoint) {
                inject(relativeUri() + config.sourcePath + entryPoint, function () {
                    info(APPLICATION_INITIALIZED_MSG);
                }, function (reason) {
                    severe(APPLICATION_INITIALIZATION_ERROR_MSG + reason);
                });
            } else {
                warning(NO_ENTRY_POINT_MSG);
            }
        }
    }
    init();
}());