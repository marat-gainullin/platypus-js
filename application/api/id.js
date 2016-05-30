define(function(){
    var IdGeneratorClass = Java.type("com.eas.util.IdGenerator");
    
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
            return +IdGeneratorClass.genId();
        }
    });
    Object.defineProperty(module, "generate", {
        value: function () {
            return +IdGeneratorClass.genId();
        }
    });
    Object.defineProperty(module, "generateLong", {
        value: function () {
            return IdGeneratorClass.genStringId();
        }
    });
    return module;
});