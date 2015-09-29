define(function(){
    var MD5GeneratorClass = Java.type("com.eas.client.login.MD5Generator");
    
    var module = {};
    Object.defineProperty(module, "generate", {
        value: function (aSource) {
            return MD5GeneratorClass.generate(aSource).toLowerCase();
        }
    });
    var MD5 = {};
    Object.defineProperty(MD5, "generate", {
        value: function (aSource) {
            return MD5GeneratorClass.generate(aSource).toLowerCase();
        }
    });
    return module;
});