define(function(){
    var MD5GeneratorClass = Java.type("com.eas.client.login.MD5Generator");
    
    var module = {
        /**
         * Generates md5 hash of aSource.
         * @param {String} aSource String, the md5 hash will be generated for.
         * @returns {String} Generated md5 hash in simple hex form.
         */
        generate: function(aSource){}
    };
    Object.defineProperty(module, "generate", {
        value: function (aSource) {
            return MD5GeneratorClass.generate(aSource).toLowerCase();
        }
    });
    return module;
});