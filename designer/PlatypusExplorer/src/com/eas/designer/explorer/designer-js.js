/**
 * Platypus.js's designer internals initialization.
 */
(function() {
    var ScriptUtils = Java.type('com.eas.script.ScriptUtils');
    ScriptUtils.setLookupInGlobalFunc(
            function(aPropertyName) {
                return this[aPropertyName];
            }
    );
    ScriptUtils.setPutInGlobalFunc(
            function(aPropertyName, aValue) {
                this[aPropertyName] = aValue;
            });
    this.P = {loadModel: function() {
        }};
})();