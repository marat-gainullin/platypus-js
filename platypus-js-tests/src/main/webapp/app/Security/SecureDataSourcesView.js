/**
 * 
 * @author Andrew
 */
function SecureDataSources() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    self.execute = function(){
        secureReadTest();
        secureTest();
        secureReadWriteTest();
    };

    form.button1.onActionPerformed = function (event) {
        secureReadTest();
    };
    
    function secureReadTest(){
        model.dsSecureRead.requery(function () {
            model.dsSecureRead.push({
                customer_name: "1",
                customer_address: "22"
            });
            model.save(function () {
                model.dsSecureRead.requery(function () {
                    if (!(model.dsSecureRead.length >= 1)) {
                        throw "Filed to insert";
                    }
                }, function (error) {
                    throw error;
                });
            });
        });
    };
    
    form.button2.onActionPerformed = function(event) {
        secureReadWriteTest();
    };
    
    function secureReadWriteTest(){
       model.dsSecureWrite.requery(function () {
            model.dsSecureWrite.push({
                customer_name: "5",
                customer_address: "666666"
            });
            model.save(function () {
                model.dsSecureWrite.requery(function () {
                    if (!(model.dsSecureWrite.length >= 1)) {
                        throw "Filed to insert";
                    }
                }, function (error) {
                    throw error;
                });
            });
        });
    };
    
    form.button3.onActionPerformed = function(event) {
        secureTest();
    };
    
    function secureTest(){
        model.dsSecure.requery(function () {
            model.dsSecure.push({
                customer_name: "3",
                customer_address: "4444"
            });
            model.save(function () {
                model.dsSecure.requery(function () {
                    if (!(model.dsSecure.length >= 1)) {
                        throw "Filed to insert";
                    }
                }, function (error) {
                    throw error;
                });
            });
        });
    }
}
