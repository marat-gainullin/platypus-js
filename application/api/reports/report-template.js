/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.reports.ReportTemplate";
    var javaClass = Java.type(className);
    /**
     * Creates report template.
     * @param content The report binary body (array of byte).
     * @param name The generated report default.
     * @param format The generated report format hint (used by runtime while report generation).
     * @param data Object that propeties can be added to the report.
     * @constructor ReportTemplate ReportTemplate
     */
    function ReportTemplate(content, name, format, data) {
        var maxArgs = 4;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 4 ? new javaClass(B.boxAsJava(content), B.boxAsJava(name), B.boxAsJava(format), B.boxAsJava(data))
            : arguments.length === 3 ? new javaClass(B.boxAsJava(content), B.boxAsJava(name), B.boxAsJava(format))
            : arguments.length === 2 ? new javaClass(B.boxAsJava(content), B.boxAsJava(name))
            : arguments.length === 1 ? new javaClass(B.boxAsJava(content))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(ReportTemplate.superclass)
            ReportTemplate.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "timezoneOffset", {
            get: function() {
                var value = delegate.timezoneOffset;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.timezoneOffset = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "fixed", {
            get: function() {
                var value = delegate.fixed;
                return value;
            },
            set: function(aValue) {
                delegate.fixed = aValue;
            }
        });

    };
    /**
     * Generate report from template.
     * @method generateReport
     * @memberOf ReportTemplate
     */
    ReportTemplate.prototype.generateReport = function() {
        var delegate = this.unwrap();
        var value = delegate.generateReport();
        return B.boxAsJs(value);
    };


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new ReportTemplate(null, null, null, null, aDelegate);
    });
    return ReportTemplate;
});