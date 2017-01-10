/**
 * 
 * @author mg
 */
function SqlEnqueueUpdateTest() {
    var NEW_RECORD_ID = 4125;
    var NEW_RECORD_NAME_G = 'sql updated gname';
    var self = this
            , model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess) {
        model.ambigousChanges.requery(function () {
            if (model.ambigousChanges.length === 0)
                throw 'ambiguousChanges.length violation in EnqueueUpdate test';
            var toPush = {gid: NEW_RECORD_ID, gname: "g-name must be overwritten", tname: "t-name must be overwritten", kname: "k-name must be overwritten"};
            model.ambigousChanges.push(toPush);
            if (toPush !== model.ambigousChanges[model.ambigousChanges.length - 1])
                throw 'toPush violation in EnqueueUpdate test';
            if (toPush.tid === null)
                throw "toPush.tid violation in EnqueueUpdate test";
            toPush.tid = toPush.gid;
            if (toPush.kid === null)
                throw "toPush.kid violation in EnqueueUpdate test";
            toPush.kid = toPush.gid;

            model.save(function () {
                model.sqlUpdate.enqueueUpdate({
                    gid: new Number(NEW_RECORD_ID) // Tests boxing.boxAsJava() capability for update()
                    , gname: NEW_RECORD_NAME_G
                });
                model.save(function () {
                    model.ambigousChanges.requery(function () {
                        var found = model.ambigousChanges.find({gid: NEW_RECORD_ID});
                        var saved = found[0];
                        if (saved.gid !== NEW_RECORD_ID)
                            throw 'saved.gid violation in EnqueueUpdate test';
                        if (saved.gname !== NEW_RECORD_NAME_G)
                            throw 'saved.gname violation in EnqueueUpdate test';
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
