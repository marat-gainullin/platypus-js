/**
 * 
 * @author alexey
 * @constructor
 */
function HTTPRequest() {
    var self = this;
    var URL, module, method, restMethod;
    var contentType = null;

    var request = new XMLHttpRequest();
    
    function updateUrlByPlatypus() {
        var path = window.location.pathname;
        path = path.slice(0, path.lastIndexOf('/'));
        URL = window.location.protocol +'//' + window.location.host + path +
               '/application' + (restMethod ? '/' + restMethod : '?__type=14&__moduleName=' + module + '&__methodName=' + method);
    }

    Object.defineProperty(self, 'URL', {
        get: function () {
            return URL;
        },
        set: function (aNewUrl) {
            URL = aNewUrl;
        }
    });
    
    Object.defineProperty(self, 'module', {
        get: function () {
            return module;
        },
        set: function (aNewModule) {
            module = aNewModule;
            restMethod = null;
            updateUrlByPlatypus();
        }
    });    
    
    Object.defineProperty(self, 'method', {
        get: function () {
            return method;
        },
        set: function (aNewMethod) {
            method = aNewMethod;
            restMethod = null;
            updateUrlByPlatypus();
        }
    });
    
    Object.defineProperty(self, 'restMethod', {
        get: function () {
            return restMethod;
        },
        set: function (aNewMethod) {
            restMethod = aNewMethod;
            updateUrlByPlatypus();
        }
    }); 

    Object.defineProperty(self, 'contentType', {
        get: function () {
            return contentType;
        },
        set: function (aValue) {
            contentType = aValue;
        }
    }); 

    function getParsedResponse(req) {
        try {
            return JSON.parse(req.responseText);
        } catch (e) {
            return req.responseText;
        }
    }

    function execute(aMethod, aData, onSuccess, onFailure) {
        request.open(aMethod, URL, true);
        request.setRequestHeader('Content-type', contentType ? contentType : 'application/x-www-form-urlencoded;charset=utf-8');
        request.onreadystatechange = function () {
            if (request.readyState === 4) {
                request.onreadystatechange = null;// Avoid memoy leak. Crazy browsers!
                if (request.status >= 200 && request.status < 300) {
                    onSuccess(getParsedResponse(request));
                } else {
                    onFailure(getParsedResponse(request));
                }
            }
        };
        request.send(aData);
    }

    self.get = function (onSuccess, onFailure) {
        execute('GET', null, onSuccess, onFailure);
    };

    self.post = function (aData, onSuccess, onFailure) {
        execute('POST', aData, onSuccess, onFailure);
    };

}
