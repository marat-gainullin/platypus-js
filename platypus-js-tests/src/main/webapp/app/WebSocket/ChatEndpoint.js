/**
 * @public
 * @stateless
 */
define('ChatEndpoint', ['rpc', 'logger'], function (Lpc, Logger) {
    function mc() {
        var accounter = new Lpc.Proxy('WebSocket/ChatAccounter');

        this.onopen = function (session) {
            accounter.add(session.id, function(aData){
                session.send(aData);
            });
        };
        this.onclose = function (evt) {
            // evt.id - Session id
            // evt.wasClean - True if session was closed without an error
            // evt.code - Session close code
            // evt.reason - Description of session close
            accounter.remove(evt.id);
        };
        this.onmessage = function (evt) {
            // evt.id - Session id
            // evt.data - Text data recieved from other (client) endpoint
            accounter.broadcast(evt.data);
        };
        this.onerror = function (evt) {
            // evt.id - Session id
            // evt.message - Error message from container's exception
        };
    }
    return mc;
});

