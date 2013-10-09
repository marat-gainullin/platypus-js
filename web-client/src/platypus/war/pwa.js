/**
 * Platypus web API allows RPC calls from browser's JavaScript code to Platypus application server modules.
 * 
 **/
if (!window.platypus) {
    window.platypus = {};
}

platypus.server = new API();

function API() {

    this.serverUrl = "";

    this.API_PATH = "/application/api";
    this.HTTP_METHOD_GET = "GET";
    this.HTTP_METHOD_POST = "POST";
    this.CONTENT_TYPE_HEADER_NAME = "Content-Type";
    this.JSON_CONTENT_TYPE = "application/json; charset=utf-8";
    this.FORM_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";

    this.URL_NOT_DEFINED_ERROR = "Server url is not defined";

    //parameters
    this.QUERY_ID = "__queryId";
    this.ID = "__id";
    this.TYPE = "__type";
    this.MODULE_NAME = "__moduleName";
    this.METHOD_NAME = "__methodName";
    this.PROPERTY_NAME = "__propertyName";
    this.PROPERTY_VALUE = "__propertyValue";
    this.PARAMETER = "__param";

    //Platypus request ids
    this.RQ_HELLO = 1;
    this.RQ_KEEP_ALIVE = 2;
    this.RQ_OUT_HASH = 3;
    this.RQ_LOGIN = 4;
    this.RQ_START_APP_ELEMENT = 5;
    this.RQ_APP_QUERY = 6;
    this.RQ_EXECUTE_QUERY = 7;
    this.RQ_COMMIT = 8;
    this.RQ_ROLLBACK = 9;
    this.RQ_IS_APP_ELEMENT_ACTUAL = 10;
    this.RQ_APP_ELEMENT = 11;
    this.RQ_CREATE_SERVER_MODUlE = 12;
    this.RQ_DISPOSE_SERVER_MODULE = 13;
    this.RQ_EXECUTE_SERVER_MODULE_METHOD = 14;
    this.RQ_APP_ELEMENT_CHANGED = 15;
    this.RQ_DB_TABLE_CHANGED = 16;
    this.RQ_IS_USER_IN_ROLE = 17;
    this.RQ_LOGOUT = 18;
    this.RQ_EXECUTE_REPORT = 19;
    this.RQ_GET_SERVER_MODULE_PROPERTY = 20;
    this.RQ_SET_SERVER_MODULE_PROPERTY = 21;

    this.getServerUrl = function(aUrl) {
        return this.serverUrl;
    };

    this.setServerUrl = function(aUrl) {
        this.serverUrl = aUrl;
    };

    this.login = function(callback) {
        this.hello(callback);
    };

    this.logout = function(callback) {
        sendPlatypusRequest(this.HTTP_METHOD_GET, this.RQ_LOGOUT, null, callback);
    };

    this.hello = function(callback) {
        sendPlatypusRequest(this.HTTP_METHOD_GET, this.RQ_HELLO, null, callback);
    };

    this.disposeServerModule = function(moduleName, callback) {
        var params = {};
        params[this.MODULE_NAME] = moduleName;
        sendPlatypusRequest(this.HTTP_METHOD_GET, this.RQ_DISPOSE_SERVER_MODULE, params, callback);
    };

    this.createServerModule = function(moduleName, callback) {
        var params = {};
        params[this.MODULE_NAME] = moduleName;
        sendPlatypusRequest(this.HTTP_METHOD_GET, this.RQ_CREATE_SERVER_MODUlE, params, callback);
    };

    this.executeServerModuleMethod = function(aModuleName, aMethodName, aMethodParams, callback) {
        var params = {};
        params[this.MODULE_NAME] = aModuleName;
        params[this.METHOD_NAME] = aMethodName;
        var jsonMethodParams = [];
        for (var i = 0; i < aMethodParams.length; i++) {
            jsonMethodParams.push(JSON.stringify(aMethodParams[i]));
        }
        params[this.PARAMETER] = jsonMethodParams;
        sendPlatypusRequest(this.HTTP_METHOD_POST, this.RQ_EXECUTE_SERVER_MODULE_METHOD, params, callback);
    };

    this.keepAlive = function(callback) {
        sendPlatypusRequest(this.HTTP_METHOD_GET, this.RQ_KEEP_ALIVE, null, callback);
    };

    this.executeServerReport = function(aModuleName) {
        var params = {};
        params[this.MODULE_NAME] = aModuleName;
        sendPlatypusDownloadRequest(this.HTTP_METHOD_POST, this.RQ_EXECUTE_REPORT, params, null);
    };

    this.getServerModuleProperty = function(aModuleName, aPropertyName) {
        var params = {};
        params[this.MODULE_NAME] = aModuleName;
        params[this.PROPERTY_NAME] = aPropertyName;
        return sendPlatypusSyncRequest(this.HTTP_METHOD_POST, this.RQ_GET_SERVER_MODULE_PROPERTY, params, null);
    };

    this.setServerModuleProperty = function(aModuleName, aPropertyName, aValue) {
        var params = {};
        params[this.MODULE_NAME] = aModuleName;
        params[this.PROPERTY_NAME] = aPropertyName;
        params[this.PROPERTY_VALUE] = JSON.stringify(aValue);
        return sendPlatypusSyncRequest(this.HTTP_METHOD_POST, this.RQ_SET_SERVER_MODULE_PROPERTY, params);
    };

    this.IdGenPrevValue = 0;

    this.genID = function() {
        var date = new Date();
        var rndIDPart = 100;
        var val = date.getTime() * rndIDPart + Math.round(Math.random() * rndIDPart);
        while (platypus.API.IdGenPrevValue >= val) {
            val = platypus.API.IdGenPrevValue + 1;
        }
        platypus.API.IdGenPrevValue = val;
        return val;
    }

    var self = this;

    function executeHttpRequestAsync(urlStr, httpMethod, data, contentType, successCallback, errorCallback) {
        var xhr = new XMLHttpRequest();
        xhr.open(httpMethod, urlStr, true);
        if (contentType) {
            xhr.setRequestHeader(self.CONTENT_TYPE_HEADER_NAME, self.FORM_URLENCODED_CONTENT_TYPE);
        }
        xhr.onload = function() {
            successCallback(xhr.responseText);
        };
        xhr.onerror = xhr.onabort = xhr.ontimeout = errorCallback;
        xhr.send(data);
    }

    function executeHttpRequestSync(urlStr, httpMethod, data, contentType) {
        var xhr = new XMLHttpRequest();
        xhr.open(httpMethod, urlStr, false);
        if (contentType) {
            xhr.setRequestHeader(self.CONTENT_TYPE_HEADER_NAME, self.FORM_URLENCODED_CONTENT_TYPE);
        }
        xhr.send(data);
        return xhr.responseText;
    }

    function sendPlatypusRequest(httpMethod, rqId, params, successCallback, errorCallback) {
        if (!self.serverUrl) {
            throw self.URL_NOT_DEFINED_ERROR;
        } else {
            var urlStr = null;
            var sendData = null;
            if (httpMethod === self.HTTP_METHOD_POST) {
                urlStr = getFullUrl(rqId);
                sendData = serializeUrlEncoded(params);
            } else {
                urlStr = appendParams(getFullUrl(rqId), params);
            }
            executeHttpRequestAsync(urlStr,
                    httpMethod,
                    sendData,
                    self.FORM_URLENCODED_CONTENT_TYPE,
                    function(recievedData) {
                        successCallback(unserialize(recievedData));
                    },
                    errorCallback);
        }
    }

    function sendPlatypusSyncRequest(httpMethod, rqId, params) {
        if (!self.serverUrl) {
            throw self.URL_NOT_DEFINED_ERROR;
        } else {
            var urlStr = null;
            var sendData = null;
            if (httpMethod === self.HTTP_METHOD_POST) {
                urlStr = getFullUrl(rqId);
                sendData = serializeUrlEncoded(params);
            } else {
                urlStr = appendParams(getFullUrl(rqId), params);
            }
            return unserialize(executeHttpRequestSync(urlStr,
                    httpMethod,
                    sendData,
                    self.FORM_URLENCODED_CONTENT_TYPE));
        }
    }

    function sendPlatypusDownloadRequest(httpMethod, rqId, params) {
        if (!self.serverUrl) {
            throw self.URL_NOT_DEFINED_ERROR;
        } else {
            var uri = removeEndSlash(self.serverUrl) + self.API_PATH;
            var form = document.createElement("form");
            form.setAttribute("method", httpMethod);
            form.setAttribute("action", uri);

            appendHiddenField(form, self.TYPE, rqId);
            for (var key in params) {
                if (params.hasOwnProperty(key)) {
                    appendHiddenField(form, key, params[key]);
                }
            }
            document.body.appendChild(form);
            form.submit();
            document.body.removeChild(form);
        }
    }

    function appendParams(urlStr, params) {
        return params ? urlStr + "&" + serializeUrlEncoded(params) : urlStr;
    }

    function appendHiddenField(form, name, value) {
        var hiddenFieldType = document.createElement("input");
        hiddenFieldType.setAttribute("type", "hidden");
        hiddenFieldType.setAttribute("name", name);
        hiddenFieldType.setAttribute("value", value);
        form.appendChild(hiddenFieldType);
    }

    function getFullUrl(rqId) {
        return removeEndSlash(self.serverUrl) + self.API_PATH + "?" + self.TYPE + "=" + rqId;
    }

    function removeEndSlash(target) {
        var l = target.length - 1;
        if (target.lastIndexOf('/') === l) {
            target = target.substring(0, l);
        }
        return target;
    }

    function serializeUrlEncoded(obj, prefix) {
        var str = [];
        for (var p in obj) {
            var k = prefix ? prefix + "[]" : p, v = obj[p];
            str.push(typeof v === "object" ?
                    serializeUrlEncoded(v, k) :
                    encodeURIComponent(k) + "=" + encodeURIComponent(v));
        }
        return str.join("&");
    }

    function unserialize(str) {
        var obj;
        try {
            obj = JSON.parse(str);
        } catch (ex) {
            obj = str;
        }
        return obj;
    }

    /** 
     * Platypus Server's modules proxy object.
     * This API enables call server modules methods in simple manner.
     * 
     * Usage example:
     * platypus.server.getModule("ModuleName", function(module) {
     *      module.testMethod("param1", "param2", function(result) {
     *          console.log(result.prop1);
     *      });
     * });
     * 
     */
    this.getModule = function(moduleName, getCallback) {
        platypus.server.createServerModule(moduleName, function(data) {
            var sm = {};
            if (data.functions) {
                for (var i = 0; i < data.functions.length; i++) {
                    sm[data.functions[i]] = function(functionName) {
                        return function() {
                            var executeCallback = arguments[arguments.length - 1];
                            if (!executeCallback && !(executeCallback instanceof "function")) {
                                throw "Callback function is required.";
                            }
                            var params = [];
                            for (var j = 0; j < arguments.length - 1; j++) {
                                params[j] = arguments[j];
                            }
                            platypus.server.executeServerModuleMethod(moduleName, functionName, params, function(data) {
                                executeCallback(data);
                            });
                        }
                    }(data.functions[i]);
                }
            }
            if (data.properties) {
                for (var j = 0; j < data.properties.length; j++) {
                    (function(aMethodName) {
                        Object.defineProperty(sm, aMethodName, {
                            set: function(aValue) {
                                return  platypus.server.setServerModuleProperty(moduleName, aMethodName, aValue);
                            },
                            get: function() {
                                return  platypus.server.getServerModuleProperty(moduleName, aMethodName);
                            }
                        });
                    })(data.properties[j]);
                }
            }
            if (data.isReport) {
                sm.show = function() {
                    platypus.api.executeServerReport(moduleName);
                };
                sm.print = function() {
                    platypus.api.executeServerReport(moduleName);
                };
            }
            getCallback(sm);
        });
    };
}
