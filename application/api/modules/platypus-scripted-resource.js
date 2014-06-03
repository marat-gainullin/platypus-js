(function() {
    var javaClass = Java.type("com.eas.client.scripts.PlatypusScriptedResource");
    javaClass.setPublisher(function(aDelegate) {
        return new P.PlatypusScriptedResource(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @namespace PlatypusScriptedResource
     */
    P.PlatypusScriptedResource = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
            arguments[maxArgs] : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
        * Loads a resource's bytes either from disk or from datatbase.
        * @param path a relative path to the resource
        * @return the resource as a bytes array
         * @method load
         * @memberOf PlatypusScriptedResource
        */
        Object.defineProperty(this, "load", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.load.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });

        /**
        * Translates an application element name into local path name.
        * Takes into account file cache in case of in-database application storage.* Bypasses http[s] urls.* Extension is omitted to give client code a chance to load various parts of anapplication element (e.g. js source file or model definition or form/report template).* @param aResourceName an relative path to the resource in an application.
        * @return The local path name to the application element files without extension.
         * @method translateScriptPath
         * @memberOf PlatypusScriptedResource
        */
        Object.defineProperty(this, "translateScriptPath", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.translateScriptPath.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });

        /**
        * Loads a resource as text.
        * @param path an relative path to the resource
        * @param encoding an name of the specific encoding, UTF-8 by default (optional). Note: If a resource is loaded via http, http response content type header's charset have a priority.
        * @return the resource as a <code>string</code>
         * @method loadText
         * @memberOf PlatypusScriptedResource
        */
        Object.defineProperty(this, "loadText", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.loadText.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();