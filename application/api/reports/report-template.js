(function() {
    var javaClass = Java.type("com.eas.client.reports.ReportTemplate");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ReportTemplate(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @namespace ReportTemplate
     */
    P.ReportTemplate = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
            arguments[maxArgs] : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * Generate report from template.
         * @method generateReport
         * @memberOf ReportTemplate
         */
        Object.defineProperty(this, "generateReport", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.generateReport.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();