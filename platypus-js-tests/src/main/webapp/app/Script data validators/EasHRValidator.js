/**
 * Validator module checks changes made by application on client or server side.
 * Validator module can validate changes made in context of script and relational datasources.
 * @validator easHR
 */
function EasHRValidator() {
    var self = this;

    /**
     * Method for validating of changes log to be applied within a particular datasources.
     * @param {Array} aLog Array of changes - log of changes made by clients or server side data driven code to be applied.
     * @param {String} aDatasource Datasource name mentioned in @validator annotation.
     * @returns {Boolean} False if you whant to stop validating process (e.g. break validators chain), nothing or true otherwise.
     * @throws An exception if validation fails. 
     */
    this.validate = function (aLog, aDatasource, aOnSuccess, aOnFailure) {
        P.Logger.info("EasHRValidator. aLog.length: " + aLog.length + "; aDatasource: " + aDatasource + ";");
        easHRValidatorCalls++;
        for (var le = 0; le < aLog.length; le++) {
            var aChange = aLog[le];
            if (aChange.data) {// insert and update
                for (var de = 0; de < aChange.data.length; de++) {
                    var aData = aChange.data[de];
                    if (aData.name == 'postal_code' && aData.value == '-1')
                        throw "postal_code can't be '-1'";// You may call here aOnFailure instead.
                }
            }
        }
        if (aOnSuccess) {
            aOnSuccess();
        }
    };
}

var easHRValidatorCalls = 0;