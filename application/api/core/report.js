/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.report.Report";
    var javaClass = Java.type(className);
    /**
     * Creates report, generated with template.
     * @param body The report binary body (array of byte).
     * @param format Format of the report (xls, xlsx).
     * @param name Name of the report. May be used as output file name.
     * @constructor Report Report
     */
    function Report(body, format, name) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(B.boxAsJava(body), B.boxAsJava(format), B.boxAsJava(name))
            : arguments.length === 2 ? new javaClass(B.boxAsJava(body), B.boxAsJava(format))
            : arguments.length === 1 ? new javaClass(B.boxAsJava(body))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(Report.superclass)
            Report.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };
    /**
     * Runs printing.
     * @method print
     * @memberOf Report
     */
    Report.prototype.print = function() {
        var delegate = this.unwrap();
        var value = delegate.print();
        return B.boxAsJs(value);
    };

    /**
     * Saves the report at a specified location.
     * @method save
     * @memberOf Report
     * @param aFileName Name of a file, the generated report should be save in. */
    Report.prototype.save = function(aFileName) {
        var delegate = this.unwrap();
        var value = delegate.save(B.boxAsJava(aFileName));
        return B.boxAsJs(value);
    };

    /**
     * Shows report as Excel application.
     * @method show
     * @memberOf Report
     */
    Report.prototype.show = function() {
        var delegate = this.unwrap();
        var value = delegate.show();
        return B.boxAsJs(value);
    };


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Report(null, null, null, aDelegate);
    });
    return Report;
});