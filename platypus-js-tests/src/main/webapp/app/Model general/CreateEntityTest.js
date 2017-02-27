/**
 * 
 * @author mg
 * @name Create_Entity_Test
 * @public
 * @stateless
 */
function Create_Entity_Test() {
    var self = this,
            model = P.loadModel(this.constructor.name);

    self.execute = function (onSuccess, onFailure) {
        var created = model.createEntity("select * from MTD_ENTITIES");
        if (created === null)
            throw "entity hasn't been created";
        if (created.schema === null)
            throw "entity's .schema is not accessible";
        created.requery(function () {
            if (!created.cursor.mdent_name)
                throw "entity's .cursor.MDENT_NAME is not accessible";
            if (!created.cursor.mdent_type)
                throw "entity's .cursor.MDENT_TYPE is not accessible";
            P.Logger.info("created.length: " + created.length);
            onSuccess(created.length);
        }, function (e) {
            P.Logger.severe(e);
            if (onFailure)
                onFailure(e);
        });
    };
// include save tests
// include leaks tests
// include right and mad use cases of model's entities in constructor
}