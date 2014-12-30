(function() {
    var javaClass = Java.type("com.eas.client.reports.ReportTemplate");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ReportTemplate(null, null, aDelegate);
    });
    
    /**
     * Creates report template.
     * @param config The report binary body (array of byte) and some options.
     * @param data Object that propeties can be added to the report.
     * @constructor ReportTemplate ReportTemplate
     */
    P.ReportTemplate = function (config, data) {
        var maxArgs = 2;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 2 ? new javaClass(P.boxAsJava(config), P.boxAsJava(data))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(config))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ReportTemplate.superclass)
            P.ReportTemplate.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "timezoneOffset", {
            get: function() {
                var value = delegate.timezoneOffset;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.timezoneOffset = P.boxAsJava(aValue);
            }
        });
        if(!P.ReportTemplate){
            /**
             * Array of name collections, that will fixed.
             * @property timezoneOffset
             * @memberOf ReportTemplate
             */
            P.ReportTemplate.prototype.timezoneOffset = 0;
        }
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
        Object.defineProperty(this, "fixed", {
            get: function() {
                var value = delegate.fixed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.fixed = P.boxAsJava(aValue);
            }
        });
        if(!P.ReportTemplate){
            /**
             * Array of name collections, that will fixed.
             * @property fixed
             * @memberOf ReportTemplate
             */
            P.ReportTemplate.prototype.fixed = {};
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