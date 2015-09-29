define(function(){
    var HTML5 = "HTML5 client";
    var J2SE = "Java SE environment";

    var module = {};
    Object.defineProperty(module, "HTML5", {
        enumerable: true,
        value: HTML5
    });
    Object.defineProperty(module, "J2SE", {
        enumerable: true,
        value: J2SE
    });
    Object.defineProperty(module, "agent", {
        enumerable: true,
        value: J2SE
    });

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var PlatypusPrincipalClass = Java.type("com.eas.client.login.PlatypusPrincipal");
    Object.defineProperty(module, "principal", {
        enumerable: true,
        get: function () {
            var clientSpacePrincipal = PlatypusPrincipalClass.getClientSpacePrincipal();
            var tlsPrincipal = ScriptsClass.getContext().getPrincipal();
            return clientSpacePrincipal !== null ? clientSpacePrincipal : tlsPrincipal;
        }
    });
    return module;
});