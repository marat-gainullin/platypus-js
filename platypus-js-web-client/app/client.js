define(['logger', 'invoke', 'id', 'core/report', 'internals'], function (Logger, Invoke, Id, Report, Utils) {
    var principal;
    var REPORT_LOCATION_CONTENT_TYPE = "text/platypus-report-location";

    var RequestTypes = {
        rqCredential: '5',
        rqModuleStructure: '19',
        rqResource: '20',
        rqAppQuery: '6',
        rqExecuteQuery: '7',
        rqCommit: '8',
        rqCreateServerModule: '12',
        rqDisposeServerModule: '13',
        rqExecuteServerModuleMethod: '14',
        rqLogout: '18'
    };
    
    var RequestParams = {
        CACHE_BUSTER: "__cb",
        QUERY_NAME: "__queryId",
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
        return window.platypusjs.config.remoteApi ? window.platypusjs.remoteApi : relativeUri();
    }

    function relativeUri() {
        var pageUrl = hostPageBaseURL();
        pageUrl = pageUrl.substring(0, pageUrl.length - 1);
        return pageUrl;
    }
    
    function submitForm(aAction, aMethod, aContentType, aFormData, onSuccess, onFailure) {
        var req = new XMLHttpRequest();
        var urlPath = aAction ? aAction : "";
        var parameters = [];
        for (var paramName in aFormData) {
            parameters.push(param(paramName, aFormData[paramName]));
        }
        var paramsData = params(parameters);
        if (aMethod !== Methods.POST) {
            urlPath += "?" + paramsData;
        }
        req.open(aMethod, urlPath);
        req.setRequestHeader("Content-Type", aContentType);
        // Must set the onreadystatechange handler before calling send().
        req.onreadystatechange = function (xhr) {
            if (xhr.readyState === 4/*RequestState.DONE*/) {
                req.onreadystatechange = null;
                if (200 <= xhr.status && xhr.status < 300) {
                    if (onSuccess) {
                        try {
                            onSuccess(xhr);
                        } catch (ex) {
                            Logger.severe(ex);
                        }
                    }
                } else {
                    if (onFailure) {
                        try {
                            onFailure(xhr);
                        } catch (ex) {
                            Logger.severe(ex);
                        }
                    }
                }
            }
        };
        if (aMethod === Methods.POST) {
            req.send(paramsData);
        } else {
            req.send();
        }
        return {
            cancel: function () {
                req.onreadystatechange = null;
                req.abort();
            }
        };
    }
    
    function startApiRequest(aUrlPrefix, aUrlQuery, aBody, aMethod, aContentType, onSuccess, onFailure) {
        var url = remoteApi() + window.platypusjs.config.apiUri + (aUrlPrefix ? aUrlPrefix : "") + (aUrlQuery ? "?" + aUrlQuery : "");
        var req = new XMLHttpRequest();
        req.open(aMethod, url);
        if (aContentType) {
            req.setRequestHeader("Content-Type", aContentType);
        }
        req.setRequestHeader("Pragma", "no-cache");
        return startRequest(req, aBody, onSuccess, onFailure);
    }

    function startRequest(req, aBody, onSuccess, onFailure) {
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
        if (aBody) {
            req.send(aBody);
        } else {
            req.send();
        }
        return {
            cancel: function () {
                req.onreadystatechange = null;
                req.abort();
            }
        };
    }

    function syncApiRequest(aUrlPrefix, aUrlQuery, aResponseType) {
        var url = remoteApi() + window.platypusjs.config.apiUri + (aUrlPrefix ? aUrlPrefix : "") + "?" + aUrlQuery;
        var req = syncRequest(url, aResponseType, null, Methods.GET);
        if (200 <= req.status && req.status < 300) {
            return req;
        } else {
            throw req.status + " : " + req.statusText;
        }
    }

    function syncRequest(aUrl, aResponseType, aBody, aMethod) {
        var req = new XMLHttpRequest();
        req.open(aMethod, aUrl, false);
        /*
         * Since W3C standard about sync XmlHttpRequest and response type. if
         * (aResponseType != null && aResponseType != ResponseType.Default)
         * req.setResponseType(aResponseType);
         */
        req.setRequestHeader("Pragma", "no-cache");
        if (aBody) {
            req.send(aBody);
        } else {
            req.send();
        }
        if (200 <= req.status && req.status < 300) {
            return req;
        } else {
            throw req.status + " : " + req.statusText;
        }
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
    
    function isReportResponse(aResponse) {
        var responseType = aResponse.getResponseHeader("content-type");
        if (responseType) {
            return responseType.toLowerCase().contains(REPORT_LOCATION_CONTENT_TYPE);
        } else {
            return false;
        }
    }

    function requestLogout(onSuccess, onFailure) {
        var query = param(RequestParams.TYPE, RequestTypes.rqLogout);
        return startApiRequest(null, query, null, Methods.GET, null, function (aResult) {
            principal = null;
            onSuccess(aResult);
        }, function (reason) {
            onFailure(reason.status + " : " + reason.statusText);
        });
    }

    function requestLoggedInUser(onSuccess, onFailure) {
        if (!principal) {
            var query = param(RequestParams.TYPE, RequestTypes.rqCredential);
            startApiRequest(null, query, "", Methods.GET, null, function (aResponse) {
                if (isJsonResponse(aResponse)) {
                    if (aResponse.responseText) {
                        var oResult = JSON.parse(aResponse.responseText);
                        principal = oResult.userName;
                    } else {
                        principal = null;
                    }
                    if (!principal) {
                        principal = "anonymous-" + Id.generate();
                    }
                    if (onSuccess) {
                        onSuccess(principal);
                    }
                } else {
                    if (onFailure) {
                        onFailure(aResponse.responseText);
                    }
                }
            }, function (reason) {
                principal = "anonymous-" + Id.generate();
                if (onFailure) {
                    onFailure(reason.status + " : " + reason.statusText);
                }
            });
        } else {
            if (onSuccess) {
                Invoke.later(function () {
                    onSuccess(principal);
                });
            }
        }
    }

    function requestCommit(changeLog, onSuccess, onFailure) {
        var query = param(RequestParams.TYPE, RequestTypes.rqCommit);
        return startApiRequest(null, query, JSON.stringify(changeLog), Methods.POST, "application/json; charset=utf-8", function (aResponse) {
            Logger.info("Commit succeded: " + aResponse.getStatus() + " " + aResponse.getStatusText());
            if (onSuccess) {
                onSuccess();
            }
        }, function (aResponse) {
            Logger.info("Commit failed: " + aResponse.status + " : " + aResponse.statusText);
            if (onFailure) {
                onFailure(aResponse.status + " : " + aResponse.statusText);
            }
        });
    }

    function requestServerMethodExecution(aModuleName, aMethodName, aParams, onSuccess, onFailure) {
        var convertedParams = [];
        for (var i = 0; i < aParams.length; i++) {
            convertedParams.push(param(RequestParams.PARAMS_ARRAY, aParams[i]));
        }
        var query = params(
                param(RequestParams.TYPE, RequestTypes.rqExecuteServerModuleMethod),
                param(RequestParams.MODULE_NAME, aModuleName),
                param(RequestParams.METHOD_NAME, aMethodName),
                params(convertedParams));
        if (onSuccess) {
            startApiRequest(null, null, query, Methods.POST, "application/x-www-form-urlencoded; charset=utf-8", function (aResponse) {
                if (isJsonResponse(aResponse)) {
                    // WARNING!!!Don't edit to JSON.parse()!
                    // It is parsed in high-level js-code.
                    onSuccess(aResponse.responseText);
                } else if (isReportResponse(aResponse)) {
                    onSuccess(new Report(aResponse.responseText));
                } else {
                    onSuccess(aResponse.responseText);
                }
            }, function (aResponse) {
                if (onFailure) {
                    try {
                        var responseText = aResponse.responseText;
                        if (isJsonResponse(aResponse)) {
                            onFailure(JSON.parse(responseText));
                        } else {
                            onFailure(responseText ? responseText : aResponse.statusText);
                        }
                    } catch (ex) {
                        Logger.severe(ex);
                    }
                }
            });
            return null;
        } else {
            var executed = syncApiRequest(null, query, '');
            if (executed) {
                if (200 <= executed.status && executed.status < 300) {
                    var responseType = executed.getResponseHeader("content-type");
                    if (responseType) {
                        if (isJsonResponse(executed)) {
                            // WARNING!!!Don't edit to Utils.jsonParse!
                            // It is parsed in high-level js-code.
                            return executed.responseText;
                        } else if (responseType.toLowerCase().contains(REPORT_LOCATION_CONTENT_TYPE)) {
                            return new Report(executed.responseText);
                        } else {
                            return executed.responseText;
                        }
                    } else {
                        return executed.responseText;
                    }
                } else {
                    var msg = executed.responseText ? executed.responseText : (executed.status + ' : ' + executed.statusText);
                    throw msg;
                }
            } else {
                return null;
            }
        }
    }

    function requestData(aQueryName, aParams, onSuccess, onFailure) {
        var query = params(param(RequestParams.TYPE, RequestTypes.rqExecuteQuery), param(RequestParams.QUERY_NAME, aQueryName), params(aParams));
        return startApiRequest(null, query, "", Methods.GET, null, function (aResponse) {
            if (isJsonResponse(aResponse)) {
                // TODO: Check all JSON.parse() against date reviver
                var parsed = JSON.parse(aResponse.responseText, Utils.dateReviver);
                if (onSuccess) {
                    onSuccess(parsed);
                }
            } else {
                if (onFailure) {
                    onFailure(aResponse.responseText ? aResponse.responseText : aResponse.status + ' : ' + aResponse.statusText);
                }
            }
        }, function (aResponse) {
            if (onFailure) {
                if (aResponse.status === 0) {
                    Logger.warning("Data recieving is aborted");
                }
                onFailure(aResponse.responseText ? aResponse.responseText : aResponse.status + ' : ' + aResponse.statusText);
            }
        });
    }
    
    var module = {};
    
    Object.defineProperty(module, 'requestCommit', {
        enumerable: true,
        get: function(){
            return requestCommit;
        }
    });
    
    Object.defineProperty(module, 'requestData', {
        enumerable: true,
        get: function(){
            return requestData;
        }
    });
    
    Object.defineProperty(module, 'requestLoggedInUser', {
        enumerable: true,
        get: function(){
            return requestLoggedInUser;
        }
    });
    
    Object.defineProperty(module, 'requestLogout', {
        enumerable: true,
        get: function(){
            return requestLogout;
        }
    });
    
    Object.defineProperty(module, 'requestServerMethodExecution', {
        enumerable: true,
        get: function(){
            return requestServerMethodExecution;
        }
    });
    
    Object.defineProperty(module, 'params', {
        enumerable: true,
        get: function(){
            return params;
        }
    });
    
    Object.defineProperty(module, 'param', {
        enumerable: true,
        get: function(){
            return param;
        }
    });
    
    return module;
});