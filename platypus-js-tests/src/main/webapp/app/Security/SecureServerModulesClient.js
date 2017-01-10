/**
 * 
 * @author Andrew
 */
function SecureServerModulesClient() {
    var self = this;

    self.execute = function (aOnSuccess) {
        require('security', function (Security) {
            Security.principal(function (principal) {
                var secureModule = new P.ServerModule("SecureModule");

                function secureFunctionTest() {
                    P.Logger.info("principal.name: " + principal.name);
                    if (principal.name === "testuser2") {
                        if (secureModule.secureTest() === "securetest") {
                            return;
                        }
                    } else {
                        try {
                            secureModule.secureTest();
                        } catch (e) {
                            P.Logger.warning(e);
                            return;
                        }
                    }
                    throw "Failed to call secure function 1";
                }

                function rootSecureFunctionTest() {
                    if (secureModule.callLocalSecureTest() !== "localsecuretest") {
                        throw "Failed to call local secure function";
                    }
                }

                function nonPublicModuleTest() {
                    try {
                        var nonPublicModule = new P.ServerModule("NonPublicModule");
                        nonPublicModule.test();
                    } catch (e) {
                        P.Logger.warning(e);
                        return;
                    }
                    throw "Failed to call non public module function";
                }

                function secureFunctionAsyncTest(aOnSuccess) {
                    secureModule.secureTest(function (aResult) {
                        if (principal.name === "testuser2" && aResult === "securetest") {
                            aOnSuccess();
                        } else {
                            throw "Failed to call secure function 2";
                        }
                    }, function (aError) {
                        P.Logger.warning(aError);
                    });
                }

                secureFunctionTest();
                rootSecureFunctionTest();
                nonPublicModuleTest();
                secureFunctionAsyncTest(aOnSuccess);
            });
        });
    };
}
