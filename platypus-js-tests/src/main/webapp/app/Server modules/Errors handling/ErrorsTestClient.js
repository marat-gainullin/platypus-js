function ErrorsTestClient() {
    var proxy = new P.ServerModule('ErrorsProducer');
    this.execute = function (aOnSuccess, aOnFailure) {
        var completed = 0;
        function complete() {
            if (++completed === 9) {
                aOnSuccess();
            }
        }
        proxy.stringError(function () {}, function (aError) {
            if (aError === 'StringError') {
                complete();
            } else {
                aOnFailure('StringError violation');
            }
        });
        proxy.numberError(function () {}, function (aError) {
            if (aError === 789) {
                complete();
            } else {
                aOnFailure('NumberError violation');
            }
        });
        proxy.booleanError(function () {}, function (aError) {
            if (aError === true) {
                complete();
            } else {
                aOnFailure('BooleanError violation');
            }
        });
        proxy.dateError(function () {}, function (aError) {
            try{
                if (aError.getTime() === 1458910467244) {
                    complete();
                } else {
                    aOnFailure('DateError violation');
                }
            }catch(ex){
                aOnFailure(aError + '');
            }
        });
        proxy.nullError(function () {}, function (aError) {
            if (aError === null) {
                complete();
            } else {
                aOnFailure('NullError violation');
            }
        });
        proxy.undefinedError(function () {}, function (aError) {
            if (aError == undefined) {
                complete();
            } else {
                aOnFailure('UndefinedError violation');
            }
        });
        proxy.objectError(function () {}, function (aError) {
            if (aError.s === 'StringError' && aError.n === 789 && aError.b === true && aError.dt.getTime() === 1458910467244 && aError.nl === null) {
                complete();
            } else {
                aOnFailure('ObjectError violation');
            }
        });
        proxy.arrayError(function () {}, function (aError) {
            if (aError.length === 1 && aError[0].s === 'StringError' && aError[0].n === 789 && aError[0].b === true && aError[0].dt.getTime() === 1458910467244 && aError[0].nl === null) {
                complete();
            } else {
                aOnFailure('ArrayError violation');
            }
        });
        proxy.thrownError(function () {
            throw 'onFailure violation';
        }, function (ePage) {
            if (ePage.indexOf('<html>') === -1 && !ePage.startsWith('Exception from server'))
                aOnFailure('Thrown error violation');
            else
                complete();
        });
    };
}