define(['./client'], function (Client) {
    function Principal(aName) {
        Object.defineProperty(this, "name", {get: function () {
                return aName;
            }});
        Object.defineProperty(this, "hasRole", {value: function () {
                return true;
            }});
        Object.defineProperty(this, "logout", {value: function (onSuccess, onFailure) {
                return Client.requestLogout(onSuccess, onFailure);
            }});
    }
    var module = {};
    Object.defineProperty(module, 'principal', {
        value: function (aOnSuccess, aOnFailure) {
            Client.requestLoggedInUser(aOnSuccess ? function (aPrincipalName) {
                aOnSuccess(new Principal(aPrincipalName));
            } : null, aOnFailure);
        }
    });
    return module;
});