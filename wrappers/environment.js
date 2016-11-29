define('logger', function (Logger) {
    var HTML5 = "HTML5 client";
    var J2SE = "Java SE environment";

    var module = {
        /**
         * Constant for HTML5 agent
         */
        HTML5: "",
        /**
         * Constant for Java SE agent
         */
        J2SE: "",
        /**
         * Agent environment property value (HTML5 | J2SE)
         */
        agent: "",
        /**
         * Setups the environment to supress a browser's cahce if aValue is true.
         * @param {Boolean} aValue
         * @returns {undefined}
         */
        cacheBust: function (aValue) {}
    };
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
    Object.defineProperty(module, "cacheBust", {
        enumerable: true,
        value: function (aValue) {}
    });
    return module;
});