(function() {
    var javaClass = Java.type("com.eas.client.reports.ReportTemplate");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ReportTemplate(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ReportTemplate ReportTemplate
     */
    P.ReportTemplate = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ReportTemplate.superclass)
            P.ReportTemplate.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };
        /**
         * Generate report from template.
         * @method generateReport
         * @memberOf ReportTemplate
         */
        P.ReportTemplate.prototype.generateReport = function() {
            var delegate = this.unwrap();
            var value = delegate.generateReport();
            return P.boxAsJs(value);
        };

})();