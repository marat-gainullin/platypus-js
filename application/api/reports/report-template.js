(function() {
    var javaClass = Java.type("com.eas.client.reports.ReportTemplate");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ReportTemplate(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ReportTemplate ReportTemplate
     */
    P.ReportTemplate = function ReportTemplate() {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ReportTemplate.superclass)
            ReportTemplate.superclass.constructor.apply(this, arguments);
        /**
         * Generate report from template.
         * @method generateReport
         * @memberOf ReportTemplate
         */
        Object.defineProperty(this, "generateReport", {
            get: function() {
                return function() {
                    var value = delegate.generateReport();
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();