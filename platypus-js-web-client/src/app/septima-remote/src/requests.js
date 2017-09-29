define([
    'core/logger',
    'core/invoke',
    'core/id',
    'core/report',
    './resource'], function (
        Logger,
        Invoke,
        Id,
        Report,
        Resource) {
    var global = window;

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
        ENTITY_NAME: "__queryId",
        TYPE: "__type",
        MODULE_NAME: "__moduleName",
        METHOD_NAME: "__methodName",
        PARAMS_ARRAY: "__param[]"
    };

    var Methods = {
        GET: 'GET',
        PUT: 'PUT',
        POST: 'POST',
        HEAD: 'HEAD',
        DELETE: 'DELETE'
    };

    function param(aName, aValue) {
        return aName + "=" + (aValue ? encodeURIComponent(aValue) : aValue);
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

    function objToParams(params) {
        var res = "";
        for (var p in params) {
            if (res.length > 0) {
                res += "&";
            }
            res += param(p, params[p]);
        }
        return res;
    }

    function arrayToParams(paramsName, params) {
        var res = "";
        for (var p = 0; p < params.length; p++) {
            if (res.length > 0) {
                res += "&";
            }
            res += param(paramsName, params[p]);
        }
        return res;
    }

    function submitForm(aAction, aMethod, aFormData, onSuccess, onFailure) {
        var req = new XMLHttpRequest();
        var url = aAction ? aAction : "";
        var paramsData = objToParams(aFormData);
        if (aMethod !== Methods.POST) {
            url += "?" + paramsData;
        }
        req.open(aMethod, url);
        // Must set the onreadystatechange handler before calling send().
        req.onreadystatechange = function (event) {
            if (req.readyState === 4/*RequestState.DONE*/) {
                req.onreadystatechange = null;
                if (200 <= req.status && req.status < 300) {
                    if (onSuccess) {
                        onSuccess(req);
                    }
                } else {
                    if (onFailure) {
                        onFailure(req.responseText ? req.responseText : (req.status + ' : ' + req.statusText));
                    }
                }
            }
        };
        if (aMethod === Methods.POST) {
            req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
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

    function dateReviver(k, v) {
        if (typeof v === 'string' && /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z/.test(v)) {
            return new Date(v);
        } else {
            return v;
        }
    }

    function startApiRequest(aUrlPrefix, aUrlQuery, aBody, aMethod, aContentType, onSuccess, onFailure) {
        var url = Resource.remoteApi() + global.platypusjs.config.apiUri + (aUrlPrefix ? aUrlPrefix : "") + (aUrlQuery ? "?" + aUrlQuery : "");
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
                if (200 <= req.status && req.status < 300) {
                    if (onSuccess) {
                        onSuccess(req);
                    }
                } else {
                    if (onFailure) {
                        if (req.status === 0) {
                            // Chrome calls 'req.onreadystatechange' in the same control flow as 'req.abort()'
                            // has been called by client code. So, we have to emulate network like error control flow.
                            Invoke.later(function () {
                                onFailure(req);
                            });
                        } else {
                            onFailure(req);
                        }
                    }
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
                req.abort();
            }
        };
    }

    function syncApiRequest(aUrlPrefix, aUrlQuery, aResponseType) {
        var url = Resource.remoteApi() + global.platypusjs.config.apiUri + (aUrlPrefix ? aUrlPrefix : "") + "?" + aUrlQuery;
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

    function isJsonResponse(xhr) {
        var responseType = xhr.getResponseHeader("content-type");
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

    function requestEntity(entityName, onSuccess, onFailure) {
        var url = params(param(RequestParams.TYPE, RequestTypes.rqAppQuery), param(RequestParams.ENTITY_NAME, entityName));
        return startApiRequest(null, url, "", Methods.GET, null, function (xhr) {
            if (isJsonResponse(xhr)) {
                if (onSuccess) {
                    var entity = JSON.parse(xhr.responseText);
                    onSuccess(entity);
                }
            } else {
                if (onFailure) {
                    onFailure('Wrong response MIME type. It should be json like MIME type');
                }
            }
        }, function (xhr) {
            if (onFailure) {
                onFailure(xhr.responseText ? xhr.responseText : (xhr.status + ' : ' + xhr.statusText));
            }
        });
    }

    function requestLogout(onSuccess, onFailure) {
        var query = param(RequestParams.TYPE, RequestTypes.rqLogout);
        return startApiRequest(null, query, null, Methods.GET, null, function (xhr) {
            principal = null;
            onSuccess(xhr);
        }, function (xhr) {
            onFailure(xhr.responseText ? xhr.responseText : (xhr.status + ' : ' + xhr.statusText));
        });
    }

    function requestLoggedInUser(onSuccess, onFailure) {
        var query = param(RequestParams.TYPE, RequestTypes.rqCredential);
        return startApiRequest(null, query, "", Methods.GET, null, function (xhr) {
            if (isJsonResponse(xhr)) {
                var principal;
                if (xhr.responseText) {
                    var oResult = JSON.parse(xhr.responseText);
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
                    onFailure(xhr.responseText);
                }
            }
        }, function (xhr) {
            if (onFailure) {
                onFailure(xhr.responseText ? xhr.responseText : (xhr.status + ' : ' + xhr.statusText));
            }
        });
    }

    function requestCommit(changeLog, onSuccess, onFailure) {
        var query = param(RequestParams.TYPE, RequestTypes.rqCommit);
        return startApiRequest(null, query, JSON.stringify(changeLog), Methods.POST, "application/json; charset=utf-8", function (xhr) {
            Logger.info("Commit succeded: " + xhr.status + " : " + xhr.statusText);
            if (onSuccess) {
                if (isJsonResponse(xhr)) {
                    var touched = JSON.parse(xhr.responseText);
                    onSuccess(touched);
                } else {
                    onFailure('Wrong response MIME type. It should be json like MIME type');
                }
            }
        }, function (xhr) {
            Logger.info("Commit failed: " + xhr.status + " : " + xhr.statusText);
            if (onFailure) {
                onFailure(xhr.responseText ? xhr.responseText : (xhr.status + ' : ' + xhr.statusText));
            }
        });
    }

    function requestServerMethodExecution(aModuleName, aMethodName, aParams, onSuccess, onFailure) {
        var query = params(
                param(RequestParams.TYPE, RequestTypes.rqExecuteServerModuleMethod),
                param(RequestParams.MODULE_NAME, aModuleName),
                param(RequestParams.METHOD_NAME, aMethodName),
                arrayToParams(RequestParams.PARAMS_ARRAY, aParams));
        if (onSuccess) {
            return startApiRequest(null, null, query, Methods.POST, "application/x-www-form-urlencoded; charset=utf-8", function (xhr) {
                if (isJsonResponse(xhr)) {
                    // WARNING!!!Don't edit to JSON.parse()!
                    // It is parsed in high-level js-code.
                    onSuccess(xhr.responseText);
                } else if (isReportResponse(xhr)) {
                    onSuccess(new Report(xhr.responseText));
                } else {
                    onSuccess(xhr.responseText);
                }
            }, function (xhr) {
                if (onFailure) {
                    if (isJsonResponse(xhr)) {
                        onFailure(JSON.parse(xhr.responseText));
                    } else {
                        onFailure(xhr.responseText ? xhr.responseText : (xhr.status + ' : ' + xhr.statusText));
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

    function requestData(aServerEntityName, aParams, onSuccess, onFailure) {
        var query = params(
                param(RequestParams.TYPE, RequestTypes.rqExecuteQuery),
                param(RequestParams.ENTITY_NAME, aServerEntityName), objToParams(aParams)
                );
        return startApiRequest(null, query, "", Methods.GET, null, function (xhr) {
            if (isJsonResponse(xhr)) {
                // TODO: Check all JSON.parse() against date reviver
                var parsed = JSON.parse(xhr.responseText, dateReviver);
                if (onSuccess) {
                    onSuccess(parsed);
                }
            } else {
                if (onFailure) {
                    onFailure('Wrong response MIME type. It should be json like MIME type');
                }
            }
        }, function (xhr) {
            if (onFailure) {
                if (xhr.status === 0) {
                    onFailure('Cancel'); // In case of cancelled request, no useful information is presented in xhr.
                } else {
                    onFailure(xhr.responseText ? xhr.responseText : (xhr.status + ' : ' + xhr.statusText));
                }
            }
        });
    }

    var module = {};

    Object.defineProperty(module, 'requestEntity', {
        enumerable: true,
        get: function () {
            return requestEntity;
        }
    });

    Object.defineProperty(module, 'requestCommit', {
        enumerable: true,
        get: function () {
            return requestCommit;
        }
    });

    Object.defineProperty(module, 'requestData', {
        enumerable: true,
        get: function () {
            return requestData;
        }
    });

    Object.defineProperty(module, 'requestLoggedInUser', {
        enumerable: true,
        get: function () {
            return requestLoggedInUser;
        }
    });

    Object.defineProperty(module, 'requestLogout', {
        enumerable: true,
        get: function () {
            return requestLogout;
        }
    });

    Object.defineProperty(module, 'requestServerMethodExecution', {
        enumerable: true,
        get: function () {
            return requestServerMethodExecution;
        }
    });

    Object.defineProperty(module, 'submitForm', {
        enumerable: true,
        get: function () {
            return submitForm;
        }
    });

    Object.defineProperty(module, 'params', {
        enumerable: true,
        get: function () {
            return params;
        }
    });

    Object.defineProperty(module, 'param', {
        enumerable: true,
        get: function () {
            return param;
        }
    });

    Object.defineProperty(module, 'RequestTypes', {
        enumerable: true,
        get: function () {
            return RequestTypes;
        }
    });

    Object.defineProperty(module, 'RequestParams', {
        enumerable: true,
        get: function () {
            return RequestParams;
        }
    });

    Object.defineProperty(module, 'dateReviver', {
        get: function () {
            return dateReviver;
        }
    });
    return module;
});