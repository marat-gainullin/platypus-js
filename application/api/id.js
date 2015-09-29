define(function(){
    var IDGeneratorClass = Java.type("com.eas.util.IDGenerator");
    
    var module = {};

    Object.defineProperty(module, "genID", {
        value: function () {
            return IDGeneratorClass.genID();
        }
    });
    Object.defineProperty(module, "generate", {
        value: function () {
            return IDGeneratorClass.genID();
        }
    });
    return module;
});