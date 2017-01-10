/* global P */

/**
 * 
 * @author mg
 * @constructor
 */
function PrincipalTestClient() {
    var self = this, model = P.loadModel(this.constructor.name);

    self.execute = function (aOnComplete) {
        require('security', function (Security) {
            Security.principal(function (principal) {
                var calls = 0;
                function tryToComplete() {
                    if (++calls === 2) {
                        aOnComplete();
                    }
                }
                var s = new P.ServerModule("PrincipalTest");
                P.Logger.info('Local principal.name: ' + principal.name);
                s.testName(function (aName) {
                    P.Logger.info("user: " + aName);
                    tryToComplete();
                }, function (aError) {
                    P.Logger.severe(aError);
                });
                s.testHasRole("*", function (aStatus) {
                    P.Logger.info("has role '*': " + aStatus);
                    tryToComplete();
                }, function (aError) {
                    P.Logger.severe(aError);
                });
            });
        });
    };
}
