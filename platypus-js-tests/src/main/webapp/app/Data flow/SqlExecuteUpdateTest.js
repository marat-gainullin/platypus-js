/**
 * 
 * @author mg
 * @public
 * @stateless
 */
function SqlExecuteUpdateTest() {
    var NEW_RECORD_ID = 4125;
    var NEW_RECORD_NAME_G = 'sql updated gname';
    var self = this
            , model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess) {
        model.ambigousChanges.requery(function () {
            if (model.ambigousChanges.length === 0)
                throw 'ambiguousChanges.length violation';
            var toPush = {gid: NEW_RECORD_ID, gname: "g-name must be overwritten", tname: "t-name must be overwritten", kname: "k-name must be overwritten"};
            model.ambigousChanges.push(toPush);
            if (toPush !== model.ambigousChanges[model.ambigousChanges.length - 1])
                throw 'toPush violation';
            if (toPush.tid === null)
                throw "toPush.tid violation";
            toPush.tid = toPush.gid;
            if (toPush.kid === null)
                throw "toPush.kid violation";
            toPush.kid = toPush.gid;

            model.save(function () {
                model.sqlUpdate.params.gid = new Number(NEW_RECORD_ID); // Tests boxing.boxAsJava() capability for executeUpdate()
                model.sqlUpdate.params.gname = NEW_RECORD_NAME_G;
                model.sqlUpdate.executeUpdate(function () {
                    model.ambigousChanges.requery(function () {
                        var found = model.ambigousChanges.find({gid: NEW_RECORD_ID});
                        var saved = found[0];
                        if (saved.gid !== NEW_RECORD_ID)
                            throw 'saved.gid violation';
                        if (saved.gname !== NEW_RECORD_NAME_G)
                            throw 'saved.gname violation';
                        model.ambigousChanges.remove(saved);
                        model.save(function () {
                            aOnSuccess();
                        }, function (e) {
                            P.Logger.severe(e);
                        });
                    }, function (e) {
                        P.Logger.severe(e);
                    });
                }, function (e) {
                    P.Logger.severe(e);
                });
            }, function (e) {
                P.Logger.severe(e);
            });
        }, function (e) {
            P.Logger.severe(e);
        });
    };
}
