/**
 * 
 * @author mg
 * @name Load_Entity_Test
 * @public
 * @stateless
 */
function Load_Entity_Test() {
    var self = this,
            model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess) {
        var loaded = model.loadEntity("_24832514140608864");
        if (loaded === null)
            throw "entity hasn't been created";
        if (loaded.schema === null)
            throw "entity's .schema is not accessible";

        loaded.requery(function () {
            if (loaded.cursor.MDENT_NAME === null)
                throw "entity's .cursor.MDENT_NAME is not accessible";
            if (loaded.cursor.MDENT_TYPE === null)
                throw "entity's .cursor.MDENT_TYPE is not accessible";
            P.Logger.info("loaded.length: " + loaded.length);
            aOnSuccess(loaded.length);
        });
    }
// include save tests
// include leaks tests
// include right and mad use cases of model's entities in constructor
}