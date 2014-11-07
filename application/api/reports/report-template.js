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
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = P.boxAsJava(aValue);
            }
        });
        if(!P.ReportTemplate){
            /**
             * Name of the generated report's file.
             * @property name
             * @memberOf ReportTemplate
             */
            P.ReportTemplate.prototype.name = '';
        }
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