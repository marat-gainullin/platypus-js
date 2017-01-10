/**
 * 
 * @author Andrew
 */
function SecureDataSourcesTest() {
    var self = this
            , model = P.loadModel(this.constructor.name);

    self.execute = function (aOnSuccess) {
        var cleaner = new P.ServerModule('SecureDatasourcesCleaning');
        cleaner.execute(function () {
            secureTest(function () {
                cleaner.execute(function () {
                    secureReadTest(function () {
                        cleaner.execute(function () {
                            secureReadWriteTest(function () {
                                cleaner.execute(function () {
                                    aOnSuccess();
                                });
                            });
                        });
                    });
                });
            });
        });
    };

    function secureTest(aOnSuccess) {
        model.dsSecure.requery(function () {
            if (model.dsSecure.length > 0)
                throw 'Dirty data 1';
            model.dsSecure.push({
                tetsfield: "3"
            });
            model.save(function () {
                model.dsSecure.requery(function () {
                    if (model.dsSecure.length < 1) {
                        throw "Failed to insert";
                    } else
                        aOnSuccess();
                }, function (error) {
                    throw error;
                });
            });
        });
    }

    function secureReadTest(aOnSuccess) {
        model.dsSecureRead.requery(function () {
            if (model.dsSecureRead.length > 0)
                throw 'Dirty data 1';
            model.dsSecureRead.push({
                tetsfield: "22"
            });
            model.save(function () {
                model.dsSecureRead.requery(function () {
                    if (model.dsSecureRead.length < 1) {
                        throw "Failed to insert";
                    } else
                        aOnSuccess();
                }, function (error) {
                    throw error;
                });
            });
        });
    }

    function secureReadWriteTest(aOnSuccess) {
        model.dsSecureWrite.requery(function () {
            if (model.dsSecureWrite.length > 0)
                throw 'Dirty data 1';
            model.dsSecureWrite.push({
                tetsfield: "5"
            });
            model.save(function () {
                throw "Insert have to be prevented by write role, but data is inserted.";
            }, function () {
                aOnSuccess();
            });
        });
    }
}
