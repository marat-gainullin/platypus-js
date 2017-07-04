define(function () {
    function Principal(aName) {
        Object.defineProperty(this, "name", {get: function () {
                return aName;
            }});
        Object.defineProperty(this, "hasRole", {value: function () {
                return true;
            }});
        Object.defineProperty(this, "logout", {value: function (onSuccess, onFailure) {
                //var appClient = @com.eas.client.AppClient::getInstance()();
                return /*appClient.@com.eas.client.AppClient::*/jsLogout(onSuccess, onFailure);
            }});
    }
    var module = {};
    Object.defineProperty(module, 'principal', {
        value: function (aOnSuccess, aOnFailure) {
            //var appClient = @com.eas.client.AppClient::getInstance()();
            /*appClient.@com.eas.client.AppClient::*/jsLoggedInUser(aOnSuccess ? function (aPrincipalName) {
                aOnSuccess(new Principal(aPrincipalName));
            } : null, aOnFailure);
        }
    });
    return module;
});