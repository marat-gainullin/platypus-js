define(function () {
    var module = {};
    Object.defineProperty(module, 'upload', {
        enumerable: true,
        value: function (aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback) {
            return /*@com.eas.client.AppClient::*/jsUpload(aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback);
        }
    });
    Object.defineProperty(module, 'load', {
        enumerable: true,
        value: function (aResName, onSuccess, onFailure) {
            var loaded = /*@com.eas.client.AppClient::*/jsLoad(aResName, onSuccess, onFailure);
            if (loaded)
                loaded.length = loaded.byteLength;
            return loaded;
        }
    });
    Object.defineProperty(module, 'loadText', {
        enumerable: true,
        value: function (aResName, onSuccess, onFailure) {
            return /*@com.eas.client.AppClient::*/jsLoadText(aResName, onSuccess, onFailure);
        }
    });
    return module;
});