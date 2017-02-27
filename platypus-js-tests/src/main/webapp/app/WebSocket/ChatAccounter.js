/**
 * @resident
 */
define('logger', function (Logger) {
    var sessions = {};
    function mc() {
        this.add = function (aSessionId, aOnMessage) {
            sessions[aSessionId] = aOnMessage;
            aOnMessage('registered');// send 'registered' status
            Logger.info('add ' + aSessionId + '. sessions count: ' + Object.keys(sessions).length);
        };
        this.remove = function (aSessionId) {
            delete sessions[aSessionId];
        };
        this.broadcast = function (aData) {
            Logger.info('broadcast. sessions count: ' + Object.keys(sessions).length);
            for (var s in sessions) {
                sessions[s](aData);
            }
        };
    }
    return mc;
});