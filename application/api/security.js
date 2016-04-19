define(['invoke'], function (Invoke) {
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var PlatypusPrincipalClass = Java.type("com.eas.client.login.PlatypusPrincipal");

    var module = {
        /**
         * Requests a server about current authenticated user and passes its object to the 'aOnSuccess' callback.
         * @param {Function} aOnSuccess Success callback. It accepts a user object of the followinf structure:<br>
         * {<br>
         * &nbsp;&nbsp;&nbsp;&nbsp;name: "User's name",<br>
         * &nbsp;&nbsp;&nbsp;&nbsp;hasRole: function(aRoleName){<br>
         * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return true | false;<br>
         * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
         * }<br>
         * @param {Function} aOnFailure Failure callback. It is called if a problem occurs while loading information about the user.
         * @returns {undefined}
         */
        principal: function (aOnSuccess, aOnFailure) {},
    };
    Object.defineProperty(module, 'principal', {
        value: function (aOnSuccess, aOnFailure) {
            try {
                var clientSpacePrincipal = PlatypusPrincipalClass.getClientSpacePrincipal();
                var tlsPrincipal = ScriptsClass.getContext() ? ScriptsClass.getContext().getPrincipal() : null;
                var calcedPrincipal = clientSpacePrincipal !== null ? clientSpacePrincipal : tlsPrincipal;
                if (aOnSuccess) {
                    Invoke.later(function () {
                        aOnSuccess(calcedPrincipal);
                    });
                } else {
                    return calcedPrincipal;
                }
            } catch (ex) {
                if (aOnFailure) {
                    Invoke.later(function () {
                        aOnFailure('' + ex);
                    });
                }
            }
        }
    });
    return module;
});