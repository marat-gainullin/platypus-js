/* global Java */

define(function () {
    var LoggerClass = Java.type("java.util.logging.Logger");
    var applicationLogger = LoggerClass.getLogger("Application");
    var Logger = {
        /**
         * Adds aMessage to 'Application' logger on INFO log level.
         * @param {String} aMessage
         * @returns {undefined}
         */
        info: function(aMessage){},
        /**
         * Adds aMessage to 'Application' logger on CONFIG log level.
         * @param {String} aMessage
         * @returns {undefined}
         */
        config: function(aMessage){},
        /**
         * Adds aMessage to 'Application' logger on FINE log level.
         * @param {String} aMessage
         * @returns {undefined}
         */
        fine: function(aMessage){},
        /**
         * Adds aMessage to 'Application' logger on FINER log level.
         * @param {String} aMessage
         * @returns {undefined}
         */
        finer: function(aMessage){},
        /**
         * Adds aMessage to 'Application' logger on FINEST log level.
         * @param {String} aMessage
         * @returns {undefined}
         */
        finest: function(aMessage){},
        /**
         * Adds aMessage to 'Application' logger on SEVERE log level.
         * This log level is typical for exceptions.
         * @param {String} aMessage
         * @returns {undefined}
         */
        severe: function(aMessage){},
        /**
         * Adds aMessage to 'Application' logger on WARNING log level.
         * @param {String} aMessage
         * @returns {undefined}
         */
        warning: function(aMessage){}
    };
    Object.defineProperty(Logger, "config", {value: function (aMessage) {
            applicationLogger.config("" + aMessage);
        }});
    Object.defineProperty(Logger, "severe", {value: function (aMessage) {
            applicationLogger.severe("" + aMessage);
        }});
    Object.defineProperty(Logger, "warning", {value: function (aMessage) {
            applicationLogger.warning("" + aMessage);
        }});
    Object.defineProperty(Logger, "info", {value: function (aMessage) {
            applicationLogger.info("" + aMessage);
        }});
    Object.defineProperty(Logger, "fine", {value: function (aMessage) {
            applicationLogger.fine("" + aMessage);
        }});
    Object.defineProperty(Logger, "finer", {value: function (aMessage) {
            applicationLogger.finer("" + aMessage);
        }});
    Object.defineProperty(Logger, "finest", {value: function (aMessage) {
            applicationLogger.finest("" + aMessage);
        }});
    return Logger;
});