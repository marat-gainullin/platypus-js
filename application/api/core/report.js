(function() {
    var javaClass = Java.type("com.eas.client.report.Report");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Report(null, null, null, aDelegate);
    });
    
    /**
     *
     * @constructor Report Report
     */
    P.Report = function (aReport, aFormat, aName) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(aReport), P.boxAsJava(aFormat), P.boxAsJava(aName))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(aReport), P.boxAsJava(aFormat))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(aReport))
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