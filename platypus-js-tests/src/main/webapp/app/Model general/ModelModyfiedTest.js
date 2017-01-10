/**
 * 
 * @author user
 * @public
 * @stateless
 */
function ModelModyfiedTest() {
    var self = this, model = P.loadModel(this.constructor.name);

    // Nesessary to test:
    // Work of modified field
    // Work of modified field with revert
    // 1 - check that model is not modified
    // 2 - modify and check flag
    // 3 - revert and check flag
    // 4 - modify and save
    // 5 - modify and get
    // requery
    self.execute = function (aOnSuccess) {
        if (typeof model.modified == "undefined") {
            throw "Model.modified is undefined";
        }
        if (model.modified == true) {
            throw "Modified has been set for unmodified datamodel";
        }
        model.datamodel.push({});
        if (model.modified == false) {
            throw "Model has been modified, but flag was not set";
        }
        model.revert();
        if (model.modified == true) {
            throw "Changes have been reverted but flag not resetd";
        }

        model.datamodel.push({"name": "test"});
        if (model.datamodel.cursor.name != "test") {
            throw "Setted Wrong value";
        }
        model.save(function () {
            if (model.modified == true) {
                throw "Changes have been saved, but flag does not reseted";
            }
            model.datamodel.cursor.name = "newVal";
            if (model.modified == false) {
                throw "Model has been modified, but flag was not set";
            }
            if (model.datamodel.cursor.name == "test") {
                throw "Value changed, but it still old";
            }

            model.datamodel.splice((model.datamodel.cursorPos - 1), 1);
            if (model.modified == false) {
                throw "Model has been modified, but flag was not set";
            }
            model.save(aOnSuccess());
        });




    };
}
