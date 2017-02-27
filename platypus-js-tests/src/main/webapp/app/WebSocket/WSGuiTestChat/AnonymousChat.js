/**
 * 
 * @author user
 */
function AnonymousChat() {
    var self = this
            , form = P.loadForm(this.constructor.name);

    var webSocket = null;
    function addEventsListener() {
        var eventsTypes = "";
        var delimiter = "";

        if (webSocket) {
            webSocket.close();
            webSocket = null;
        }
        var wsProtocol = "ws:";
        if (window.location.protocol == 'https:')
            wsProtocol = "wss:";

        webSocket = new WebSocket(wsProtocol + "//" + window.location.host + window.location.pathname.substr(0, window.location.pathname.lastIndexOf("/")) + "/ChatEndpoint");
        webSocket.onopen = function () {
            P.Logger.info("onOpen");
        };

        webSocket.onerror = function () {
            P.Logger.info("onError");
        };

        webSocket.onmessage = function (evt) {
            P.Logger.info("onMessage");
            var msgBox = new P.BoxPane();
            msgBox.element.innerHTML = evt.data;
            form.panel.add(msgBox);
        };
        
        webSocket.onclose = function () {
            P.Logger.info("onClose");
        };
    }

    self.show = function () {
        form.show();
        addEventsListener();
        form.txtMessage.focus();
    };

    form.btnSend.onActionPerformed = function (event) {
        webSocket.send(form.txtMessage.value);
        form.txtMessage.value = "";
    };

}
