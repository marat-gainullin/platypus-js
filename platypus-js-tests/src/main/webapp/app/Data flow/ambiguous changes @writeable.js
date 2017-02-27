/**
 * 
 * @author mg
 * @constructor
 */
function ambigous_changes_semi_writable() {
    var NEW_RECORD_ID = 4125;
    var NEW_RECORD_NAME_G = "test gname";
    var NEW_RECORD_NAME_T = "test tname";
    var NEW_RECORD_NAME_K = "test kname";
    var self = this, model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess) {
        model.ambigousChanges.requery(function () {
            var oldLength = model.ambigousChanges.length;
            if (model.ambigousChanges.length <= 0) {
                throw 'length violation 1';
            }
            model.ambigousChanges.push({
                tid: NEW_RECORD_ID,
                gname: "-g- must be overwritten",
                tname: "-t- must be overwritten",
                kname: "-k- must be overwritten"});
            var pushed = model.ambigousChanges[model.ambigousChanges.length - 1];
            if (pushed.tid !== NEW_RECORD_ID)
                throw "pushed.tid violation";
            if (pushed.gid === null)
                throw "pushed.gid violation";
            if (pushed.kid === null)
                throw "pushed.kid violation";
            // update operations
            pushed.gid = NEW_RECORD_ID;
            pushed.kid = NEW_RECORD_ID;
            pushed.gname = NEW_RECORD_NAME_G;
            pushed.tname = NEW_RECORD_NAME_T;
            pushed.kname = NEW_RECORD_NAME_K;
            model.save(function () {
                model.requery(function () {
                    var newLength = model.ambigousChanges.length;
                    if (oldLength + 1 !== newLength) {
                        throw 'length violation 2';
                    }
                    var found = model.ambigousChanges.find({kid: NEW_RECORD_ID});
                    if (!Array.isArray(found))
                        throw 'Array.isArray violation 1';
                    if (found.length !== 1)
                        throw 'found.length violation 1';
                    var instance = found[0];
                    if (instance.gid !== null)
                        throw 'instance.gid violation';
                    if (instance.tid !== NEW_RECORD_ID)
                        throw 'instance.tid violation';
                    if (instance.kid !== NEW_RECORD_ID)
                        throw 'instance.kid violation';
                    if (instance.gname !== null)
                        throw 'instance.gname violation';
                    if (instance.tname !== NEW_RECORD_NAME_T)
                        throw 'instance.tname violation';
                    if (instance.kname !== NEW_RECORD_NAME_K)
                        throw 'instance.kname violation';
                    // Delete operation
                    model.ambigousChanges.remove(instance);
                    model.save(function () {
                        model.requery(function () {
                            var newLength1 = model.ambigousChanges.length;
                            if (oldLength !== newLength1) {
                                throw 'length violation 3';
                            }
                            var found1 = model.ambigousChanges.find({gid: NEW_RECORD_ID});
                            if (!Array.isArray(found1))
                                throw 'Array.isArray violation 2';
                            if (found1.length !== 0)
                                throw 'found.length violation 2';
                            aOnSuccess();
                        }, function (e) {
                            P.Logger.info(e);
                        });
                    }, function (e) {
                        P.Logger.info(e);
                    });
                }, function (e) {
                    P.Logger.info(e);
                });
            }, function (e) {
                P.Logger.info(e);
            });
        }, function (e) {
            P.Logger.info(e);
        });
    };
}
