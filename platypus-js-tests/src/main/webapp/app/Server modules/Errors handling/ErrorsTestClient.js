function ErrorsTestClient() {
    var proxy = new P.ServerModule('ErrorsProducer');
    this.execute = function (aOnSuccess) {
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
                throw 'StringError violation';
            }
        });
        proxy.numberError(function () {}, function (aError) {
            if (aError === 789) {
                complete();
            } else {
                throw 'NumberError violation';
            }
        });
        proxy.booleanError(function () {}, function (aError) {
            if (aError === true) {
                complete();
            } else {
                throw 'BooleanError violation';
            }
        });
        proxy.dateError(function () {}, function (aError) {
            if (aError.getTime() === 1458910467244) {
                complete();
            } else {
                throw 'DateError violation';
            }
        });
        proxy.nullError(function () {}, function (aError) {
            if (aError === null) {
                complete();
            } else {
                throw 'NullError violation';
            }
        });
        proxy.undefinedError(function () {}, function (aError) {
            if (aError == undefined) {
                complete();
            } else {
                throw 'UndefinedError violation';
            }
        });
        proxy.objectError(function () {}, function (aError) {
            if (aError.s === 'StringError' && aError.n === 789 && aError.b === true && aError.dt.getTime() === 1458910467244 && aError.nl === null) {
                complete();
            } else {
                throw 'ObjectError violation';
            }
        });
        proxy.arrayError(function () {}, function (aError) {
            if (aError.length === 1 && aError[0].s === 'StringError' && aError[0].n === 789 && aError[0].b === true && aError[0].dt.getTime() === 1458910467244 && aError[0].nl === null) {
                complete();
            } else {
                throw 'ArrayError violation';
            }
        });
        proxy.thrownError(function () {
            throw 'onFailure violation';
        }, function (ePage) {
            if (ePage.indexOf('<html>') === -1 && !ePage.startsWith('Exception from server'))
                throw 'Thrown error violation';
            else
                complete();
        });
    };
}