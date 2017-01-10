/**
 * 
 * @author mg
 */
function MultiSourceWithErrorTest() {
    var self = this
            , model = P.loadModel(this.constructor.name);

    this.execute = function (aOnSuccess) {
        model.requery(function () {
            if (model.dataEAS.length !== 0) {
                throw "MultiSourceTest. dataEAS violoation 1";
            }
            if (model.dataHR.length !== 0) {
                throw "MultiSourceTest. dataHR violoation 1";
            }
            var inserted = {addr: "addr1", /*entries: null, */modified: new Date()};
            var inserted1 = {addr: "addr1", /*entries: null, */modified: new Date()};
            model.dataEAS.push(inserted);
            model.dataHR.push(inserted1);
            model.save(function () {
                throw "FAILED. Commit succeded, while it should not."
                P.Logger.info();
            }, function (e) {
                P.Logger.info("Error obtained: " + e + " Let's check data integrity...");
                model.requery(function () {
                    if (model.dataEAS.length !== 0) {
                        throw "MultiSourceTest. dataEAS violoation 2";
                    }
                    if (model.dataHR.length !== 0) {
                        throw "MultiSourceTest. dataHR violoation 2";
                    }
                    aOnSuccess();
                });                
            });
        }, function (e) {
            P.Logger.severe(e);
        });
    };
}
