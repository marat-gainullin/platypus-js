/**
 * @author ${user}
 * Validator module checks changes made by application on client or server side.
 * Validator module can validate changes made in context of script and relational datasources.
 * @validator DataSource1, DataSource2
 * @stateless
 */
define('${appElementName}', ['logger'], function (Logger, ModuleName) {
    function module_constructor() {
        var self = this;
        /**
         * Method for validating of changes log to be applied within a particular datasources.
         * @param {Array} aLog Array of changes - log of changes made by clients or server side data driven code to be applied.
         * @param {String} aDatasource Datasource name mentioned in @validator annotation (relational datasource or script datasource module name).
         * @param aOnSuccess Succes callback for asynchronous version.
         * @param aOnSuccess Failure callback for asynchronous version.
         * @returns {Boolean} False if you whant to stop validating process (e.g. break validators chain), nothing or true otherwise.
         * @throws An exception if validation fails. 
         */
        this.validate = function(aLog, aDatasource, aOnSuccess, aOnFailure) {
            Logger.info("${appElementName}. aLog.length: " + aLog.length + "; aDatasource: " + aDatasource + ";");
            if (aOnSuccess){
                // TODO: place your asynchronous validation code here
            } else{
                // TODO: place your synchronous validation code here
            }
        };
    }
    return module_constructor;
});
