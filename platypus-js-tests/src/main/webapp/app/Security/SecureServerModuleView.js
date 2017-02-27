/**
 * 
 * @author Andrew
 * @rolesAllowed role1 role2
 */
function SecureServerModuleView() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    var secureModule = new P.ServerModule("SecureModule");

    form.button.onActionPerformed = function (event) {
        secureFunctionTest();
    };
    
    function secureFunctionTest(){
        if (P.principal.name === "testuser2") {
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
        throw "Failed to call secure function";
    }

    form.button2.onActionPerformed = function (event) {
        rootSecureFunctionTest();
    };
    
    function rootSecureFunctionTest(){
        if (secureModule.callLocalSecureTest() !== "localsecuretest") {
            throw "Failed to call local secure function";
        }
    }

    form.button1.onActionPerformed = function (event) {
        nonPublicModuleTest();
    };
    
    function nonPublicModuleTest(){
        try {
            var nonPublicModule = new P.ServerModule("NonPublicModule");
            nonPublicModule.test();
        } catch (e) {
            P.Logger.warning(e);
            return;
        }
        throw "Failed to call non public module function";
    }
    form.button3.onActionPerformed = function(event) {
        secureFunctionAsyncTest();
    };
    
    function secureFunctionAsyncTest(){
        secureModule.secureTest(function(aResult) {
            if (P.principal.name === "testuser2" &&  aResult === "securetest") {
                return;
            } else {
            throw "Failed to call secure function";
            }
        }, function(aError) {
            P.Logger.warning(aError);
        });
    }
    
    self.execute = function(){
        secureFunctionTest();
        secureFunctionAsyncTest();
        rootSecureFunctionTest();
        nonPublicModuleTest();
    };
}
