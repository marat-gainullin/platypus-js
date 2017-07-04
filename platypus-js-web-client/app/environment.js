define(function () {
    var HTML5 = "HTML5 client";
    var J2SE = "Java SE environment";

    function cacheBust(aValue) {
        //var appClient = @com.eas.client.AppClient::getInstance()();
        /*appClient.@com.eas.client.AppClient::*/setCacheBustEnabled(!!aValue);
    }

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
        value: HTML5
    });
    Object.defineProperty(module, "cacheBust", {
        enumerable: true,
        value: cacheBust
    });
    return module;
});