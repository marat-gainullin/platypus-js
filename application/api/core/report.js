(function() {
    var javaClass = Java.type("com.eas.client.report.Report");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Report(null, null, null, aDelegate);
    });
    
    /**
     * Creates report, generated with template.
     * @param body The report binary body (array of byte).
     * @param format Format of the report (xls, xlsx).
     * @param name Name of the report. May be used as output file name.
     * @constructor Report Report
     */
    P.Report = function (body, format, name) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(body), P.boxAsJava(format), P.boxAsJava(name))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(body), P.boxAsJava(format))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(body))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.Report.superclass)
            P.Report.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };
        /**
         * Runs printing.
         * @method print
         * @memberOf Report
         */
        P.Report.prototype.print = function() {
            var delegate = this.unwrap();
            var value = delegate.print();
            return P.boxAsJs(value);
        };

        /**
         * Saves the report at a specified location.
         * @method save
         * @memberOf Report
         * @param aFileName Name of a file, the generated report should be save in. */
        P.Report.prototype.save = function(aFileName) {
            var delegate = this.unwrap();
            var value = delegate.save(P.boxAsJava(aFileName));
            return P.boxAsJs(value);
        };

        /**
         * Shows report as Excel application.
         * @method show
         * @memberOf Report
         */
        P.Report.prototype.show = function() {
            var delegate = this.unwrap();
            var value = delegate.show();
            return P.boxAsJs(value);
        };

})();