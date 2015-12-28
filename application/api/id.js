define(function(){
    var IDGeneratorClass = Java.type("com.eas.util.IDGenerator");
    
    var module = {
        /**
         * Generates a unique key to be used as primary key value.
         * The generation is based on timer.
         * @returns {String}
         */
        generate: function(){}
    };

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