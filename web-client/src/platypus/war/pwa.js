/**
 * Platypus web API allows RPC calls from browser's JavaScript code to Platypus application server modules.
 * 
 **/
if (!window.platypus) {
    window.platypus = {};
}
window.platypus.server = {}
platypus.api = new API();

function API() {
   
    this.KEEP_ALIVE_TIMEOUT = 300000; 
    this.SERVER_URL = "";
    this.API_PATH = "/api";
    this.HTTP_METHOD_GET = "GET";
    this.HTTP_METHOD_POST = "POST";
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
    
    this.rqHello = 1;
    this.rqKeepAlive = 2;
    this.rqOutHash = 3;
    this.rqLogin = 4;
    this.rqStartAppElement = 5;
    this.rqAppQuery = 6;
    this.rqExecuteQuery = 7;
    this.rqCommit = 8;
    this.rqRollback = 9;
    this.rqIsAppElementActual = 10;
    this.rqAppElement = 11;
    this.rqCreateServerModule = 12;
    this.rqDisposeServerModule = 13;
    this.rqExecuteServerModuleMethod = 14;
    this.rqAppElementChanged = 15;
    this.rqDbTableChanged = 16;
    this.rqIsUserInRole = 17;
    this.rqLogout = 18;
    this.rqExecuteReport = 19;
    this.rqGetServerModuleProperty = 20;
    this.rqSetServerModuleProperty = 21;
    
    this.setServerUrl = function(aUrl) {
        this.SERVER_URL = aUrl;
    }

    this.login = function(callback) {
        this.hello(callback);
    }

    this.logout = function(callback) {
        sendPlatypusRequest(this.HTTP_METHOD_GET, this.rqLogout, null, callback);
    }

    this.hello = function(callback) {
        sendPlatypusRequest(this.HTTP_METHOD_GET, this.rqHello, null, callback);
    }

    this.disposeServerModule = function(moduleName, callback) {
        var params = {};
        params[this.MODULE_NAME] = moduleName;
        sendPlatypusRequest(this.HTTP_METHOD_GET, this.rqDisposeServerModule, params, callback);
    }

    this.createServerModule = function(moduleName, callback) {
        var params = {};
        params[this.MODULE_NAME] = moduleName;
        sendPlatypusRequest(this.HTTP_METHOD_GET, this.rqCreateServerModule, params, callback);
    }
    
    this.executeServerModuleMethod = function(aModuleName, aMethodName, aMethodParams, callback) {
        var params = {};
        params[this.MODULE_NAME] = aModuleName;
        params[this.METHOD_NAME] = aMethodName;   
        var jsonMethodParams = [];
        for (var i = 0; i < aMethodParams.length; i++) {
            jsonMethodParams.push(JSON.stringify(aMethodParams[i]));
        }
        params[this.PARAMETER] = jsonMethodParams;
        sendPlatypusRequest(this.HTTP_METHOD_POST, this.rqExecuteServerModuleMethod, params, callback);
    }

    this.keepAlive = function(callback) {
        sendPlatypusRequest(this.HTTP_METHOD_GET, this.rqKeepAlive, null, callback);
    }
    
    this.executeServerReport = function(aModuleName) {
        var params = {};
        params[this.MODULE_NAME] = aModuleName;
        sendPlatypusDownloadRequest(this.HTTP_METHOD_POST, this.rqExecuteReport, params, null);
    }
    
    this.getServerModuleProperty = function(aModuleName, aPropertyName) {
        var params = {};
        params[this.MODULE_NAME] = aModuleName;
        params[this.PROPERTY_NAME] = aPropertyName;
        return sendPlatypusSyncRequest(this.HTTP_METHOD_POST, this.rqGetServerModuleProperty, params, null);
    }

    this.setServerModuleProperty = function(aModuleName, aPropertyName, aValue) {
        var params = {};
        params[this.MODULE_NAME] = aModuleName;
        params[this.PROPERTY_NAME] = aPropertyName;
        params[this.PROPERTY_VALUE] = JSON.stringify(aValue);
        return sendPlatypusSyncRequest(this.HTTP_METHOD_POST, this.rqSetServerModuleProperty, params);
    }
    
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
    
    function sendPlatypusRequest(httpMethod, rqId, params, successCallback, errorCallback) {
        if(self.SERVER_URL == null) {
            throw self.URL_NOT_DEFINED_ERROR;
        } else {
            var uri;    
            uri = removeEndSlash(self.SERVER_URL) + self.API_PATH  + "?" +  self.TYPE + "=" + rqId;
            $.ajax(uri, {
                type: httpMethod,
                contentType: self.FORM_URLENCODED_CONTENT_TYPE,
                dataType: "json",
                cache: false,
                data: params,
                success: function(data, textStatus, jqXHR){
                    successCallback(data, textStatus);
                }, 
                error: function(jqXHR, textStatus, errorThrown){
                    if (errorCallback) {
                        errorCallback(jqXHR.status, jqXHR.responseText);
                    } else {
                        throw new Error("Platypus ajax request error: " + jqXHR.status + " " + jqXHR.responseText )
                    }
                }
            });
        }
    }
    
    function sendPlatypusDownloadRequest(httpMethod, rqId, params) {
        if(self.SERVER_URL == null) {
            throw self.URL_NOT_DEFINED_ERROR;
        } else {
            var uri;
            var p="";      
            uri = removeEndSlash(self.SERVER_URL) + self.API_PATH;
            p += '<input type="hidden" name="' + self.TYPE  + '" value="'+rqId +'" />';
            for (var i in params) {
                if (i) {
                    p += '<input type="hidden" name="' + i + '" value="' + params[i] + '" />';
                }
            }
            $('<form action="'+ uri+'" method="' + httpMethod + '">' + p + '</form>')
		.appendTo('body').submit().remove();
        }
    }
    
     function sendPlatypusSyncRequest(httpMethod, rqId, params) {
        if(self.SERVER_URL == null) {
            throw self.URL_NOT_DEFINED_ERROR;
        } else {
            var uri;
            var contentType;
            var res;     
            uri = removeEndSlash(self.SERVER_URL) + self.API_PATH + "?" + self.TYPE + "=" + rqId;
            contentType = self.JSON_CONTENT_TYPE;
            $.ajax(uri, {
                type: httpMethod,
                async:false,
                contentType: contentType,
                dataType: "json",
                cache: false,
                data: params
            }).done(function (data, type, cfg) {
                        if (!data && cfg.responseText == "") {
                            res = undefined;
                        } else {
                            res = data
                        }
                    });
            return res;
        }
    }
    
    function getParamsQueryString(params) {
        var q = "";
        for (var p in params) {
            if (params.hasOwnProperty(p)) {
                q += "&" + p + "=" + params[p];
            }
        }
        return q;
    }
    
    function removeEndSlash(target) {
        var l = target.length - 1;
        if(target.lastIndexOf('/') === l) {
            target = target.substring(0, l);
        }
        return target;
    }
}

/* 
 * Platypus Server modules proxy object.
 * This API enables call server modules methods in simple manner.
 * 
 * Usage example:
 * platypus.server.module.get("ModuleName", function(module) {
 *      module.testMethod("param1", "param2", function(result) {
 *          console.log(result.prop1);
 *      });
 * });
 * 
 */
window.platypus.server.module = new ServerModule();

function ServerModule() {
    /**
     * @param moduleName Server Module name or ID
     * @param getCallback callback function called on module creation complete
     */
    this.get = function(moduleName, getCallback) {
        platypus.api.createServerModule(moduleName, function(data) {
            var sm = {};
            for (var i = 0; i < data.functions.length; i++) {           
                sm[data.functions[i]] = function (functionName) {
                    return function () {
                        var executeCallback = arguments[arguments.length - 1];
                        if (!executeCallback && !(executeCallback instanceof "function")) {
                            throw "Callback function is required.";
                        }
                        var params = [];
                        for (var j = 0; j < arguments.length - 1; j++) {
                            params[j] = arguments[j];
                        }
                        platypus.api.executeServerModuleMethod(moduleName, functionName, params, function (data) {
                            executeCallback(data);
                        });
                    }
                }(data.functions[i]);
            }
            for (var j = 0; j < data.properties.length; j++) {     
                (function (aMethodName) { 
                     Object.defineProperty(sm,  aMethodName, { 
                         set: function(aValue) {return  platypus.api.setServerModuleProperty(moduleName, aMethodName, aValue);},
                         get: function() { return  platypus.api.getServerModuleProperty(moduleName, aMethodName); }
                     });
                })(data.properties[j]);
            }
            if (data.isReport) {
                sm.show = function () {platypus.api.executeServerReport(moduleName);};
                sm.print = function () {platypus.api.executeServerReport(moduleName);};
            }
             getCallback(sm);
        });
    } 
}


