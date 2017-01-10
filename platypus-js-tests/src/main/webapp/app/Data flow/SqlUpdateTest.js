/**
 * 
 * @author mg
 * @public
 * @stateless
 */
function SqlUpdateTest() {
    var NEW_RECORD_ID = 4125;
    var NEW_RECORD_NAME_G = 'sql updated gname';
    var self = this
            , model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess) {
        model.ambigousChanges.requery(function () {
            if (model.ambigousChanges.length === 0)
                throw 'ambiguousChanges.length violation in update test';
            var toPush = {gid: NEW_RECORD_ID, gname: "g-name must be overwritten", tname: "t-name must be overwritten", kname: "k-name must be overwritten"};
            var events = {};
            model.ambigousChanges.onInsert = function(evt){
                if(undefined == evt.source)
                    throw 'onInsert event violation 1'
                if(undefined == evt.items)
                    throw 'onInsert event violation 2'
                if(!Array.isArray(evt.items))
                    throw 'onInsert event violation 3'
                events.onInsert = true;
            };
            model.ambigousChanges.onDelete = function(evt){
                if(undefined == evt.source)
                    throw 'onDelete event violation 1'
                if(undefined == evt.items)
                    throw 'onDelete event violation 2'
                if(!Array.isArray(evt.items))
                    throw 'onDelete event violation 3'
                events.onDelete = true;
            };
            model.ambigousChanges.onChange = function(evt){
                if(undefined == evt.source)
                    throw 'onChange event violation 1'
                if(undefined === evt.oldValue)
                    throw 'onChange event violation 2'
                if(undefined === evt.newValue)
                    throw 'onChange event violation 3'
                events.onChange = true;
            };
            model.ambigousChanges.onScroll = function(evt){
                if(undefined == evt.source)
                    throw 'onScroll event violation 1'
                if(undefined === evt.oldValue)
                    throw 'onScroll event violation 2'
                if(undefined === evt.newValue)
                    throw 'onScroll event violation 3'
                events.onScroll = true;
            };
            model.ambigousChanges.onRequeried = function(evt){
                if(undefined == evt.source)
                    throw 'onRequeried event violation'
                events.onRequeried = true;
            };
            model.ambigousChanges.push(toPush);
            if (toPush !== model.ambigousChanges[model.ambigousChanges.length - 1])
                throw 'toPush violation in update test';
            if (toPush.tid === null)
                throw "toPush.tid violation in update test";
            toPush.tid = toPush.gid;
            if (toPush.kid === null)
                throw "toPush.kid violation in update test";
            toPush.kid = toPush.gid;

            model.save(function () {
                model.sqlUpdate.update({
                    gid: new Number(NEW_RECORD_ID) // Tests boxing.boxAsJava() capability for update()
                    , gname: NEW_RECORD_NAME_G
                }, function () {
                    model.ambigousChanges.requery(function () {
                        var found = model.ambigousChanges.find({gid: NEW_RECORD_ID});
                        var saved = found[0];
                        if (saved.gid !== NEW_RECORD_ID)
                            throw 'saved.gid violation in update test';
                        if (saved.gname !== NEW_RECORD_NAME_G)
                            throw 'saved.gname violation in update test';
                        model.ambigousChanges.remove(saved);
                        model.save(function () {
                            if(events.onInsert
                                    && events.onDelete
                                    && events.onChange
                                    && events.onScroll
                                    && events.onRequeried)
                                aOnSuccess();
                            else
                                P.Logger.severe('events violation');
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
