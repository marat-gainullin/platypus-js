Logger = java.util.logging.Logger.getLogger('Application');
Lock = java.util.concurrent.locks.ReentrantLock;

// platypus style classes
Color = com.eas.client.scripts.ScriptColor;
Colors = java.awt.Color;

// platypus utilities
IDGenerator = com.bearsoft.rowset.utils.IDGenerator;
MD5Generator = com.eas.client.login.MD5Generator;

//Resources
Resource = {};
Object.defineProperty(Resource, "load", {
    get: function() {
        return function(aResName, aOnSuccess, aOnFailure) {
            try{
                var loaded = com.eas.client.scripts.PlatypusScriptedResource.load(aResName);
                if (aOnSuccess)
                    aOnSuccess(loaded);
                return loaded;
            }catch(e){
                if(aOnFailure)
                    aOnFailure(e.message?e.message:e);
                else
                    throw e;
            }
        };
    }
});

Object.defineProperty(Resource, "loadText", {
    get: function() {
        return function(aResName, aOnSuccessOrEncoding, aOnSuccessOrOnFailure, aOnFailure) {
            var encoding = null;
            var onSuccess = aOnSuccessOrEncoding;
            var onFailure = aOnSuccessOrOnFailure;
            if(typeof onSuccess != "function"){
                encoding = aOnSuccessOrEncoding;
                onSuccess = aOnSuccessOrOnFailure;
                onFailure = aOnFailure;
            }
            try{
                var _loaded = encoding ? com.eas.client.scripts.PlatypusScriptedResource.loadText(aResName, encoding) :
                                         com.eas.client.scripts.PlatypusScriptedResource.loadText(aResName);
                if(onSuccess)
                    onSuccess(_loaded);
                else
                    return _loaded;
            }catch(e){
                if(onFailure)
                    onFailure(e.message?e.message:e);
                else
                    throw e;
            }
        };
    }
});

Object.defineProperty(Resource, "applicationPath", {
    get: function() {
        return com.eas.client.scripts.PlatypusScriptedResource.getApplicationPath();
    }
});

function readString(aFileName, aEncoding) {
    var encoding = DEFAULT_ENCODING;
    if (aEncoding) {
        encoding = aEncoding;
    }
    return com.eas.util.FileUtils.readString(new java.io.File(aFileName), encoding);
}

function writeString(aFileName, aText, aEncoding) {
    var encoding = DEFAULT_ENCODING;
    if (aEncoding) {
        encoding = aEncoding;
    }
    com.eas.util.FileUtils.writeString(new java.io.File(aFileName), aText, encoding);
}