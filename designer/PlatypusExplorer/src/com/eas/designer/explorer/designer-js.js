/**
 * Platypus.js's designer internals initialization.
 */
(function() {
    var ScriptUtils = Java.type('com.eas.script.ScriptUtils');
    this.P = {loadModel: function() {
        }};
    ScriptUtils.setToDateFunc(
            function (aJavaDate) {
                return aJavaDate !== null ? new Date(aJavaDate.time) : null;
            });
    ScriptUtils.setMakeObjFunc(function () {
        return {};
    });
    ScriptUtils.setMakeArrayFunc(function () {
        return [];
    });
})();