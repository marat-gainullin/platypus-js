define(['invoke'], function (Invoke) {
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var PlatypusPrincipalClass = Java.type("com.eas.client.login.PlatypusPrincipal");

    var module = {};
    Object.defineProperty(module, 'principal', {
        value: function (aOnSuccess, aOnFailure) {
            try {
                var clientSpacePrincipal = PlatypusPrincipalClass.getClientSpacePrincipal();
                var tlsPrincipal = ScriptsClass.getContext().getPrincipal();
                var calcedPrincipal = clientSpacePrincipal !== null ? clientSpacePrincipal : tlsPrincipal;
                if (aOnSuccess) {
                    Invoke.later(function () {
                        aOnSuccess(calcedPrincipal);
                    });
                }else{
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